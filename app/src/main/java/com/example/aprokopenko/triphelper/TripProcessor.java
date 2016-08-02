package com.example.aprokopenko.triphelper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.example.aprokopenko.triphelper.listener.SpeedChangeListener;
import com.example.aprokopenko.triphelper.service.LocationService;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import rx.Subscriber;

public class TripProcessor implements Parcelable {
    private static final String  LOG_TAG = "TripProcessor";
    private static final boolean DEBUG   = BuildConfig.DEBUG;
    private final Context context;

    private ArrayList<Float>  avgSpeedArrayList;
    private boolean           fileIsInReadMode;
    private boolean           fileIsInWriteMode;
    private ServiceConnection serviceConnection;
    private LocationService   locationService;
    private long              tripStartTime;
    private int               currentTripId;
    private Intent            serviceIntent;
    private float             averageSpeed;
    private boolean           serviceBound;
    private float             maxSpeedVal;
    private GpsHandler        gpsHandler;
    private TripData          tripData;
    private ArrayList<Route>  routes;

    private float fuelConsFromSettings;
    private int   fuelCapacity;
    private float fuelPrice;

    private Subscriber<Location> locationSubscriber;
    private Subscriber<Float>    maxSpeedSubscriber;
    private Subscriber<Float>    speedSubscriber;

    private SpeedChangeListener speedChangeListener;


    public TripProcessor(Context context, float fuelConsFromSettings, float fuelPrice, int fuelCapacity) {
        if (DEBUG) {
            Log.i(LOG_TAG, "TripProcessor: IsConstructed");
        }
        this.context = context;
        setupStartingConditions(fuelConsFromSettings, fuelPrice, fuelCapacity);
        setupLocationService();
        setupTripData();
    }

    protected TripProcessor(Parcel in) {
        fileIsInReadMode = in.readByte() != 0;
        tripStartTime = in.readLong();
        currentTripId = in.readInt();
        serviceIntent = in.readParcelable(Intent.class.getClassLoader());
        averageSpeed = in.readFloat();
        serviceBound = in.readByte() != 0;
        maxSpeedVal = in.readFloat();
        tripData = in.readParcelable(TripData.class.getClassLoader());
        routes = in.createTypedArrayList(Route.CREATOR);
        fuelConsFromSettings = in.readFloat();
        fuelCapacity = in.readInt();
        fuelPrice = in.readFloat();
        context = TripHelperApp.getAppContext();
        setupSubscribers();
    }

    public static final Creator<TripProcessor> CREATOR = new Creator<TripProcessor>() {
        @Override public TripProcessor createFromParcel(Parcel in) {
            return new TripProcessor(in);
        }

        @Override public TripProcessor[] newArray(int size) {
            return new TripProcessor[size];
        }
    };

    public ArrayList<Float> getAvgSpeedArrayList() {
        return avgSpeedArrayList;
    }

    public boolean isFileNotInWriteMode() {
        return !fileIsInReadMode;
    }

    public String getFuelLeftString(String distancePrefix) {
        float fuelLeftVal = getFuelLeft();
        if (DEBUG) {
            Log.d(LOG_TAG, "getFuelLeftString: fuel written" + fuelLeftVal);
        }
        float distanceToDriveLeft = getDistanceToDriveLeft(fuelLeftVal);
        return (UtilMethods.formatFloatDecimalFormat(fuelLeftVal) + " (~" + UtilMethods
                .formatFloatDecimalFormat(distanceToDriveLeft) + distancePrefix + ")");
    }

    public String getFuelLevel(float fuel, String prefix) {
        fillGasTank(fuel);
        return getFuelLeftString(prefix);
    }

    public GpsHandler getGpsHandler() {
        return gpsHandler;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public TripData getTripData() {
        return tripData;
    }


    public void setFuelConsFromSettings(float fuelConsFromSettings) {
        this.fuelConsFromSettings = fuelConsFromSettings;
    }

    public void setFuelCapacity(int fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public void setFuelPrice(float fuelPrice) {
        this.fuelPrice = fuelPrice;
    }

    public void setSpeedChangeListener(SpeedChangeListener speedChangeListener) {
        this.speedChangeListener = speedChangeListener;
    }

    public void stopTracking() {
        ArrayList<Float> avgArrayList = getAvgSpeedArrayList();
        if (DEBUG) {
            Log.d(LOG_TAG, "stopTracking: +" + avgArrayList.size());
            for (float f : avgArrayList) {
                Log.d(LOG_TAG, "stopTracking: " + f);
            }
        }

        float averageSpeed = CalculationUtils.calcAvgSpeedForOneTrip(avgArrayList);
        float maximumSpeed = maxSpeedVal;

        updateSpeedInTrip(averageSpeed, maximumSpeed);
        endTrip();
        setMetricFieldsToTripData(fuelPrice);
        writeDataToFile();
        currentTripId = ConstantValues.START_VALUE;
        avgArrayList.clear();
    }

    public void startNewTrip() {
        Calendar currentCalendarInstance = Calendar.getInstance();
        Date     date                    = currentCalendarInstance.getTime();
        tripStartTime = date.getTime();
        avgSpeedArrayList = new ArrayList<>();
        routes = new ArrayList<>();
        currentTripId = tripData.getTrips().size();
        String formattedDate = UtilMethods.parseDate(date);
        Trip   trip          = new Trip(currentTripId, formattedDate);
        trip.setRoute(routes);
        tripData.addTrip(trip);
    }

    public void eraseTripData() {
        tripData = new TripData();
    }

    public void restoreAvgList(ArrayList<String> restoredAvgSpdList) {
        if (restoredAvgSpdList != null && restoredAvgSpdList.size() == 0) {
            ArrayList<Float> avgSpeedArrayList = new ArrayList<>();
            for (String restoredAvgSpdValue : restoredAvgSpdList) {
                avgSpeedArrayList.add(Float.valueOf(restoredAvgSpdValue));
            }
            setAvgSpeedArrayList(avgSpeedArrayList);
        }
    }

    public void performExit() {
        unsubscribeRx();
        if (serviceConnection != null) {
            unregisterService();
        }
        if (locationService != null) {
            locationService.onDestroy();
        }
        if (gpsHandler != null) {
            gpsHandler.performExit();
            gpsHandler = null;
        }
        setSpeedChangeListener(null);
    }

    private void unsubscribeRx() {
        maxSpeedSubscriber.unsubscribe();
        locationSubscriber.unsubscribe();
        speedSubscriber.unsubscribe();
    }


    private TripData createTripData(ArrayList<Trip> trips, float avgFuelConsumption, float fuelSpent, float distanceTravelled,
                                    float moneyOnFuelSpent, float avgSpeed, float timeSpent, float gasTank, float maxSpeed) {
        TripData tripData = new TripData();
        tripData.setTrips(trips);
        tripData.setDistanceTravelled(distanceTravelled);
        tripData.setFuelSpent(fuelSpent);
        tripData.setMoneyOnFuelSpent(moneyOnFuelSpent);
        tripData.setAvgFuelConsumption(avgFuelConsumption);
        tripData.setAvgSpeed(avgSpeed);
        tripData.setTimeSpentOnTrips(timeSpent);
        tripData.setGasTank(gasTank);
        tripData.setMaxSpeed(maxSpeed);
        return tripData;
    }

    private Trip getCurrentTrip() {
        return tripData.getTrip(currentTripId);
    }

    private float getDistanceToDriveLeft(float fuelLeftVal) {
        if (getTripData() != null) {
            float avgSpeed = getTripData().getAvgSpeed();
            if (avgSpeed == 0) {
                avgSpeed = ConstantValues.MEDIUM_TRAFFIC_AVG_SPEED;
            }
            if (DEBUG) {
                Log.d(LOG_TAG, "getDistanceToDriveLeft: " + fuelConsFromSettings + "avgSped" + avgSpeed);
            }
            float fuelConsLevel = UtilMethods.getFuelConsumptionLevel(avgSpeed, fuelConsFromSettings);
            return (fuelLeftVal / fuelConsLevel) * ConstantValues.PER_100;
        }
        else {
            return 0f;
        }
    }

    private float getFuelLeft() {
        if (tripData != null) {
            if (DEBUG) {
                Log.d(LOG_TAG, "getFuelLeft: " + tripData.getGasTank());
            }
            return tripData.getGasTank();
        }
        else {
            return ConstantValues.START_VALUE; // empty fuel tank
        }
    }

    private TripData readDataFromFile() {
        ReadFileTask readFileTask = new ReadFileTask();
        try {
            tripData = readFileTask.execute(ConstantValues.FILE_NAME).get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return tripData;
    }

    private Trip readTrip(ObjectInputStream is) {
        Trip trip = new Trip();
        trip = trip.readTrip(is);
        return trip;
    }


    private void setupLocationService() {
        serviceIntent = new Intent(context, LocationService.class);
        if (serviceConnection == null && !serviceBound) {
            setupServiceConnection();
            context.getApplicationContext().startService(serviceIntent);
        }
        else {
            if (DEBUG) {
                Log.i(LOG_TAG, "onServiceConnected: service already exist");
            }
            configureGpsHandler();
        }
        context.getApplicationContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setAvgSpeedArrayList(ArrayList<Float> avgSpeedArrayList) {
        this.avgSpeedArrayList = avgSpeedArrayList;
    }

    private void setupServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override public void onServiceConnected(ComponentName className, IBinder service) {
                serviceBound = true;
                LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
                locationService = binder.getService();
                configureGpsHandler();
                if (DEBUG) {
                    Log.i(LOG_TAG, "onServiceConnected: bounded");
                }
            }

            @Override public void onServiceDisconnected(ComponentName arg0) {
                serviceBound = false;
                if (DEBUG) {
                    Log.i(LOG_TAG, "onServiceConnected: unbounded");
                }
            }
        };
    }

    private void unregisterService() {
        if (serviceIntent == null) {
            serviceIntent = new Intent(context, LocationService.class);
        }
        context.getApplicationContext().stopService(serviceIntent);
        if (serviceConnection != null && serviceBound) {
            serviceBound = false;
            context.getApplicationContext().unbindService(serviceConnection);
        }
    }

    private void setupSubscribers() {
        setupLocationSubscriber();
        setupMaxSpeedSubscriber();
        setupSpeedSubscriber();
    }

    private void configureGpsHandler() {
        setupSubscribers();
        gpsHandler = locationService.getGpsHandler();
        setSubscribersToGpsHandler(gpsHandler);
    }

    private void setupTripData() {
        if (tripData == null) {
            if (DEBUG) {
                Log.i(LOG_TAG, "TripProcessor: Reading data from file...");
            }
            tripData = readDataFromFile();
        }
    }

    private void setupStartingConditions(float fuelConsFromSettings, float fuelPrice, int fuelCapacity) {
        this.fuelConsFromSettings = fuelConsFromSettings;
        this.fuelCapacity = fuelCapacity;
        this.fuelPrice = fuelPrice;
        routes = new ArrayList<>();
        fileIsInReadMode = false;
        fileIsInWriteMode = false;
    }

    private void setSubscribersToGpsHandler(GpsHandler GpsHandler) {
        GpsHandler.setSpeedSubscriber(speedSubscriber);
        GpsHandler.setLocationSubscriber(locationSubscriber);
        GpsHandler.setMaxSpeedSubscriber(maxSpeedSubscriber);
    }

    private void setupLocationSubscriber() {
        if (locationSubscriber == null) {
            locationSubscriber = new Subscriber<Location>() {
                @Override public void onCompleted() {
                }

                @Override public void onError(Throwable e) {
                }

                @Override public void onNext(Location location) {
                    addPointToRouteList(location);
                }
            };
        }
    }

    private void setupMaxSpeedSubscriber() {
        if (maxSpeedSubscriber == null) {
            maxSpeedSubscriber = new Subscriber<Float>() {
                @Override public void onCompleted() {

                }

                @Override public void onError(Throwable e) {

                }

                @Override public void onNext(Float speed) {
                    maxSpeedVal = speed;
                }
            };
        }
    }

    private void setupSpeedSubscriber() {
        if (speedSubscriber == null) {
            speedSubscriber = new Subscriber<Float>() {
                @Override public void onCompleted() {
                }

                @Override public void onError(Throwable e) {
                    if (DEBUG) {
                        Log.e(LOG_TAG, "addPointToRouteList: speed in frag - ERROR" + e.toString());
                    }
                }

                @Override public void onNext(final Float speed) {
                    addSpeedTick(speed);
                    speedChangeListener.speedChanged(speed);
                }
            };
        }
    }

    private void writeTrip(Trip trip, ObjectOutputStream os) {
        trip.writeTrip(os);
    }

    private void setMetricFieldsToTripData(float fuelPriceFromSettings) {
        final float     startVal             = ConstantValues.START_VALUE;
        TripData        tripData             = getTripData();
        Float           fuelSpent            = startVal;
        Float           timeSpentForAllTrips = startVal;
        Float           avgSpeedSum          = startVal;
        Float           avgFuelCons          = startVal;
        Float           maxSpeed             = startVal;
        Float           distTravelled;
        ArrayList<Trip> allTrips             = tripData.getTrips();

        for (Trip trip : allTrips) {
            timeSpentForAllTrips = timeSpentForAllTrips + trip.getTimeSpentForTrip();
            maxSpeed = CalculationUtils.findMaxSpeed(trip.getMaxSpeed(), maxSpeed);
            fuelSpent = fuelSpent + trip.getFuelSpent();
        }
        for (Trip trip : allTrips) {
            float majority_multiplier = (trip.getTimeSpentForTrip() / timeSpentForAllTrips) * ConstantValues.PER_100;
            avgFuelCons = (avgFuelCons + ((trip.getAvgFuelConsumption() * majority_multiplier) / ConstantValues.PER_100));
            avgSpeedSum = (avgSpeedSum + ((trip.getAvgSpeed() * majority_multiplier) / ConstantValues.PER_100));
        }
        distTravelled = CalculationUtils.calcDistTravelled(timeSpentForAllTrips, avgSpeedSum);
        //double assurance that NULL won't be passed to trip data. Method "getValueCheckedOnNAN" replaces NULL&NAN values by 0f;
        tripData.setMaxSpeed(getValueCheckedOnNAN(maxSpeed));
        tripData.setAvgSpeed(getValueCheckedOnNAN(avgSpeedSum));
        tripData.setTimeSpentOnTrips(getValueCheckedOnNAN(timeSpentForAllTrips));
        tripData.setDistanceTravelled(getValueCheckedOnNAN(distTravelled));
        tripData.setAvgFuelConsumption(getValueCheckedOnNAN(avgFuelCons));
        tripData.setFuelSpent(getValueCheckedOnNAN(fuelSpent));
        tripData.setGasTank(getValueCheckedOnNAN(tripData.getGasTank() - getCurrentTrip().getFuelSpent()));
        tripData.setMoneyOnFuelSpent(getValueCheckedOnNAN(fuelSpent * fuelPriceFromSettings));
    }

    private Float getValueCheckedOnNAN(Float value) {
        if (value == null || value.isNaN()) {
            value = 0f;
        }
        return value;
    }

    private void addRoutePoint(LatLng routePoints, float speed) {
        Route routePoint = new Route(routePoints, speed);
        addRoutePoint(routePoint);
    }

    private void updateSpeedInTrip(float avgSpeed, float maxSpd) {
        averageSpeed = avgSpeed;
        Trip trip = tripData.getTrip(currentTripId);
        trip.setAvgSpeed(avgSpeed);
        trip.setMaxSpeed(maxSpd);
    }

    private void addPointToRouteList(Location location) {
        LatLng routePoint = new LatLng(location.getLatitude(), location.getLongitude());
        float  speed;
        speed = CalculationUtils.getSpeedInKilometerPerHour(location.getSpeed());
        addRoutePoint(routePoint, speed);
    }

    private void addSpeedTick(final float speed) {
        if (avgSpeedArrayList != null) {
            avgSpeedArrayList.add(speed);
        }
    }

    private void addRoutePoint(Route routePoint) {
        routes.add(routePoint);
    }

    private void setTripFieldsToStartState() {
        tripStartTime = ConstantValues.START_VALUE;
        averageSpeed = ConstantValues.START_VALUE;
        maxSpeedVal = ConstantValues.START_VALUE;
    }

    private void getTripDataFieldsValues() {
        float distanceTravelled = ConstantValues.START_VALUE;
        float fuelSpent         = ConstantValues.START_VALUE;
        float avgFuelConsumption;
        for (Trip trip : tripData.getTrips()) {
            distanceTravelled = distanceTravelled + trip.getDistanceTravelled();
            fuelSpent = fuelSpent + trip.getFuelSpent();
        }
        float moneySpent = CalculationUtils.calcMoneySpent(fuelSpent, fuelPrice);
        avgFuelConsumption = tripData.getAvgFuelConsumption();
        tripData.setDistanceTravelled(distanceTravelled);
        tripData.setAvgFuelConsumption(avgFuelConsumption);
        tripData.setMoneyOnFuelSpent(moneySpent);
        tripData.setFuelSpent(fuelSpent);
    }

    private void fillGasTank(float fuel) {
        if (tripData != null) {
            float gasTank = tripData.getGasTank();
            if (DEBUG) {
                Log.d(LOG_TAG, "fillGasTank: gasTank" + gasTank + ",fuel" + fuel + ",fuelCap" + fuelCapacity);
            }
            if (gasTank + fuel <= fuelCapacity) {
                tripData.setGasTank(gasTank + fuel);
                CharSequence resCharSequence = context.getString(R.string.fuel_spent_toast) + fuel + context.getResources()
                        .getString(R.string.fuel_prefix);
                UtilMethods.showToast(context, resCharSequence);
            }
            else {
                UtilMethods.showToast(context, context.getString(R.string.fuel_overload_toast));
            }
            writeDataToFile();
        }
    }

    private void updateTrip(Trip trip) {
        if (DEBUG) {
            Log.d(LOG_TAG, "updateTrip: called");
        }
        trip.setAvgSpeed(averageSpeed);
        if (routes.size() == 0 || routes == null) {
            Route tmpRoutePoint = new Route(ConstantValues.BERMUDA_COORDINATES, ConstantValues.SPEED_VALUE_WORKAROUND);
            routes.add(tmpRoutePoint);
        }
        trip.setRoute(routes);
    }

    private void updateTripState() {
        Trip trip = tripData.getTrip(currentTripId);
        tripData.updateTrip(trip, currentTripId);
    }

    private void writeDataToFile() {
        if (!fileIsInWriteMode) {
            WriteFileTask writeFileTask = new WriteFileTask();
            writeFileTask.execute(tripData);
        }

    }

    private void endTrip() {
        if (DEBUG) {
            Log.d(LOG_TAG, "endTrip: end trip called");
        }
        Trip trip = getCurrentTrip();
        updateTrip(trip);
        long  timeSpent        = CalculationUtils.calcTimeInTrip(tripStartTime);
        float distanceTraveled = CalculationUtils.setDistanceCoveredForTrip(trip, timeSpent);
        float fuelConsumption  = UtilMethods.getFuelConsumptionLevel(averageSpeed, fuelConsFromSettings);
        float fuelSpent        = CalculationUtils.calcFuelSpent(distanceTraveled, fuelConsumption);
        float moneySpent       = CalculationUtils.calcMoneySpent(fuelSpent, fuelPrice);

        trip.setMoneyOnFuelSpent(moneySpent);
        trip.setFuelSpent(fuelSpent);
        trip.setDistanceTravelled(distanceTraveled);
        trip.setTimeSpent(timeSpent);
        trip.setAvgFuelConsumption(fuelConsumption);
        updateTripState();
        setTripFieldsToStartState();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (fileIsInReadMode ? 1 : 0));
        parcel.writeLong(tripStartTime);
        parcel.writeInt(currentTripId);
        parcel.writeParcelable(serviceIntent, i);
        parcel.writeFloat(averageSpeed);
        parcel.writeByte((byte) (serviceBound ? 1 : 0));
        parcel.writeFloat(maxSpeedVal);
        parcel.writeParcelable(tripData, i);
        parcel.writeTypedList(routes);
        parcel.writeFloat(fuelConsFromSettings);
        parcel.writeInt(fuelCapacity);
        parcel.writeFloat(fuelPrice);
    }

    private class WriteFileTask extends AsyncTask<TripData, Void, Boolean> {
        @Override protected void onPreExecute() {
            super.onPreExecute();
            fileIsInWriteMode = true;
        }

        @Override protected Boolean doInBackground(TripData... params) {
            if (DEBUG) {
                Log.d(LOG_TAG, "writeTripDataToFile: writeCalled");
                Log.d(LOG_TAG, "writeTripDataToFile: avgSpeed - " + tripData.getAvgSpeed());
            }
            FileOutputStream fos;
            ArrayList<Trip>  trips = tripData.getTrips();
            getTripDataFieldsValues();
            if (DEBUG) {
                Log.d(LOG_TAG, "WRITE: distTravelledForTripData " + tripData.getDistanceTravelled());
                Log.d(LOG_TAG, "WRITE: avgConsForTripData " + tripData.getAvgFuelConsumption());
                Log.d(LOG_TAG, "WRITE: fuelSpentForTripData " + tripData.getFuelSpent());
                Log.d(LOG_TAG, "WRITE: moneyOnFuelForTripData " + tripData.getMoneyOnFuelSpent());
                Log.d(LOG_TAG, "WRITE: avgSpeedForTripData " + tripData.getAvgSpeed());
            }
            int   tripsSize          = trips.size();
            Float distanceTravelled  = tripData.getDistanceTravelled();
            Float avgFuelConsumption = tripData.getAvgFuelConsumption();
            Float fuelSpent          = tripData.getFuelSpent();
            Float moneyOnFuelSpent   = tripData.getMoneyOnFuelSpent();
            Float avgSpeed           = tripData.getAvgSpeed();
            Float timeSpent          = tripData.getTimeSpentOnTrips();
            Float gasTankCapacity    = tripData.getGasTank();
            Float maxSpeed           = tripData.getMaxSpeed();

            try {
                fos = context.openFileOutput(ConstantValues.FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeInt(tripsSize);
                for (Trip trip : trips) {
                    if (DEBUG) {
                        Log.d(LOG_TAG, "writeTripDataToFile: trip to string - " + trip.toString());
                    }
                    writeTrip(trip, os);
                }
                writeToStreamWithNANcheck(avgFuelConsumption, os);
                writeToStreamWithNANcheck(fuelSpent, os);
                writeToStreamWithNANcheck(distanceTravelled, os);
                writeToStreamWithNANcheck(moneyOnFuelSpent, os);
                writeToStreamWithNANcheck(avgSpeed, os);
                writeToStreamWithNANcheck(timeSpent, os);
                writeToStreamWithNANcheck(gasTankCapacity, os);
                writeToStreamWithNANcheck(maxSpeed, os);
                os.close();
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if (DEBUG) {
                Log.d(LOG_TAG, "writeTripDataToFile: " + tripData.getTrips().toString());
            }
            return true;
        }


        private void writeToStreamWithNANcheck(Float valueToWrite, ObjectOutputStream os) throws IOException {
            if (valueToWrite == null || valueToWrite.isNaN()) {
                valueToWrite = 0f;
            }
            os.writeFloat(valueToWrite);
        }

        @Override protected void onPostExecute(Boolean result) {
            if (result) {
                if (DEBUG) {
                    Log.d(LOG_TAG, "file written successfully");
                }
                fileIsInWriteMode = false;
            }
        }
    }

    private class ReadFileTask extends AsyncTask<String, Integer, TripData> {
        @Override protected TripData doInBackground(String... params) {
            if (DEBUG) {
                Log.d(LOG_TAG, "readFile");
            }
            fileIsInReadMode = true;
            File file = context.getFileStreamPath(ConstantValues.FILE_NAME);
            if (file.exists()) {
                if (DEBUG) {
                    Log.d(LOG_TAG, "readTripDataFromFile: ");
                }
                ArrayList<Trip> trips = new ArrayList<>();
                FileInputStream fis;
                int             tripsSize;
                try {
                    fis = context.openFileInput(ConstantValues.FILE_NAME);
                    ObjectInputStream is = new ObjectInputStream(fis);
                    tripsSize = is.readInt();
                    for (int i = 0; i < tripsSize; i++) {
                        Trip trip = readTrip(is);
                        trips.add(trip);
                    }
                    float avgFuelConsumption = is.readFloat();
                    float fuelSpent          = is.readFloat();
                    float distanceTravelled  = is.readFloat();
                    float moneyOnFuelSpent   = is.readFloat();
                    float avgSpeed           = is.readFloat();
                    float timeSpent          = is.readFloat();
                    float gasTankCapacity    = is.readFloat();
                    float maxSpeed           = is.readFloat();

                    tripData = createTripData(trips, avgFuelConsumption, fuelSpent, distanceTravelled, moneyOnFuelSpent, avgSpeed,
                            timeSpent, gasTankCapacity, maxSpeed);
                    if (DEBUG) {
                        Log.d(LOG_TAG, "READ: avgFuelConsForTrip " + avgFuelConsumption);
                        Log.d(LOG_TAG, "READ: fuelSpentForTrip " + fuelSpent);
                        Log.d(LOG_TAG, "READ: distTravelledForTrip " + distanceTravelled);
                        Log.d(LOG_TAG, "READ: moneyOnFuelForTrip " + moneyOnFuelSpent);
                        Log.d(LOG_TAG, "READ: avgSpeedForTrip " + avgSpeed);
                        Log.d(LOG_TAG, "READ: timeSpentForTrip " + timeSpent);
                    }
                    is.close();
                    fis.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    tripData = new TripData();
                }
            }
            else {
                if (DEBUG) {
                    Log.d(LOG_TAG, "TripProcessor: file is empty");
                }
                tripData = new TripData();
            }
            return tripData;
        }

        @Override protected void onPostExecute(TripData tripData) {
            super.onPostExecute(tripData);
            if (tripData != null) {
                fileIsInReadMode = false;
            }
        }
    }
}



