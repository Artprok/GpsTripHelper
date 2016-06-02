package com.example.aprokopenko.triphelper;

import android.location.Location;
import android.content.Context;
import android.os.Parcelable;
import android.os.AsyncTask;
import android.os.Parcel;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.listener.SpeedChangeListener;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.ExecutionException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Date;
import java.io.File;

import rx.Subscriber;

public class TripProcessor implements Parcelable {
    private static final String LOG_TAG = "TripProcessor";
    private final Context          context;
    private final ArrayList<Route> routes;

    private boolean          fileIsInWriteMode;
    private ArrayList<Float> avgSpeedArrayList;
    private long             tripStartTime;
    private int              currentTripId;
    private float            averageSpeed;
    private TripData         tripData;

    private float fuelConsFromSettings;
    private int   fuelCapacity;
    private float fuelPrice;

    private SpeedChangeListener speedChangeListener;

    private Subscriber<Location> locationSubscriber;
    private Subscriber<Float>    maxSpeedSubscriber;
    private Subscriber<Float>    speedSubscriber;

    //todo: tempVal is testing val REMOVE in release!
    private float[] tempVal = {1};

    private TripProcessor(Parcel in) {
        fileIsInWriteMode = in.readByte() != 0;
        tripStartTime = in.readLong();
        currentTripId = in.readInt();
        averageSpeed = in.readFloat();
        tripData = in.readParcelable(TripData.class.getClassLoader());
        routes = in.createTypedArrayList(Route.CREATOR);
        tempVal = in.createFloatArray();
        fuelConsFromSettings = in.readFloat();
        fuelPrice = in.readFloat();
        fuelCapacity = in.readInt();
        context = TripHelperApp.getAppContext();
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (fileIsInWriteMode ? 1 : 0));
        dest.writeLong(tripStartTime);
        dest.writeInt(currentTripId);
        dest.writeFloat(averageSpeed);
        dest.writeParcelable(tripData, flags);
        dest.writeTypedList(routes);
        dest.writeFloatArray(tempVal);
        dest.writeFloat(fuelConsFromSettings);
        dest.writeFloat(fuelPrice);
        dest.writeInt(fuelCapacity);
    }

    @Override public int describeContents() {
        return 0;
    }

    public static final Creator<TripProcessor> CREATOR = new Creator<TripProcessor>() {
        @Override public TripProcessor createFromParcel(Parcel in) {
            return new TripProcessor(in);
        }

        @Override public TripProcessor[] newArray(int size) {
            return new TripProcessor[size];
        }
    };

    public TripProcessor(Context context, float fuelConsFromSettings, float fuelPrice, int fuelCapacity) {
        if (ConstantValues.LOGGING_ENABLED) {
            Log.i(LOG_TAG, "TripProcessor: IsConstructed");
        }
        this.fuelConsFromSettings = fuelConsFromSettings;
        this.fuelPrice = fuelPrice;
        this.fuelCapacity = fuelCapacity;
        setupSubscribers();
        fileIsInWriteMode = false;
        routes = new ArrayList<>();
        this.context = context;
        if (tripData == null) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "TripProcessor: Reading data from file, file isn't empty");
            }
            tripData = readDataFromFile();
        }
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


    public Subscriber<Location> getLocationSubscriber() {
        return locationSubscriber;
    }

    public Subscriber<Float> getMaxSpeedSubscriber() {
        return maxSpeedSubscriber;
    }

    public Subscriber<Float> getSpeedSubscriber() {
        return speedSubscriber;
    }


    public ArrayList<Float> getAvgSpeedArrayList() {
        return avgSpeedArrayList;
    }

    public String getFuelLeftString(String distancePrefix) {
        float fuelLeftVal = getFuelLeft();
        if (ConstantValues.LOGGING_ENABLED) {
            Log.d(LOG_TAG, "getFuelLeftString: fuel written" + fuelLeftVal);
        }
        writeDataToFile();
        float distanceToDriveLeft = getDistanceToDriveLeft(fuelLeftVal);
        return (UtilMethods.formatFloatDecimalFormat(fuelLeftVal) + " (~" + UtilMethods
                .formatFloatDecimalFormat(distanceToDriveLeft) + distancePrefix + ")");
    }

    public String getFuelLevel(float fuel, String prefix) {
        fillGasTank(fuel);
        return getFuelLeftString(prefix);
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public TripData getTripData() {
        return tripData;
    }


    public void setAvgSpeedArrayList(ArrayList<Float> avgSpeedArrayList) {
        this.avgSpeedArrayList = avgSpeedArrayList;
    }

    public void setSpeedChangeListener(SpeedChangeListener speedChangeListener) {
        this.speedChangeListener = speedChangeListener;
    }


    public boolean isFileNotInWriteMode() {
        return !fileIsInWriteMode;
    }


    public void stopTracking() {
        ArrayList<Float> avgArrayList = getAvgSpeedArrayList();
        if (ConstantValues.LOGGING_ENABLED) {
            Log.d(LOG_TAG, "stopTracking: +" + avgArrayList.size());
            for (float f : avgArrayList) {
                Log.d(LOG_TAG, "stopTracking: " + f);
            }
        }

        float averageSpeed = CalculationUtils.calcAvgSpeedForOneTrip(avgArrayList);
        // TODO: 12.05.2016 uncomment maxSpeedVal, speedTick for tests
        //        float maximumSpeed = maxSpeedVal;
        float maximumSpeed = avgArrayList.size();

        updateSpeed(averageSpeed, maximumSpeed);
        endTrip();
        setMetricFieldsToTripData(fuelPrice);
        writeDataToFile();
        avgArrayList.clear();
    }

    public void startNewTrip() {
        avgSpeedArrayList = new ArrayList<>();
        Calendar currentCalendarInstance = Calendar.getInstance();
        tripStartTime = currentCalendarInstance.getTime().getTime();
        currentTripId = tripData.getTrips().size();
        Date   date          = currentCalendarInstance.getTime();
        String formattedDate = UtilMethods.parseDate(date);
        Trip   trip          = new Trip(currentTripId, formattedDate);
        tripData.addTrip(trip);
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
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "getDistanceToDriveLeft: " + fuelConsFromSettings + "avgSped" + avgSpeed);
            }
            float fuelConsLevel = UtilMethods.getFuelConsumptionLevel(avgSpeed, fuelConsFromSettings);
            return (fuelLeftVal / fuelConsLevel) * ConstantValues.PER_100_KM;
        }
        else {
            return 0f;
        }
    }

    private float getFuelLeft() {
        if (tripData != null) {
            if (ConstantValues.LOGGING_ENABLED) {
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


    private void setupLocationSubscriber() {
        locationSubscriber = new Subscriber<Location>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(Location location) {
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "onNext: Location added to route list, thread - " + Thread.currentThread());
                }
                addPointToRouteList(location);
            }
        };
    }

    private void setupMaxSpeedSubscriber() {
        maxSpeedSubscriber = new Subscriber<Float>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {

            }

            @Override public void onNext(Float speed) {
                speedChangeListener.maxSpeedChanged(speed);
            }
        };
    }

    private void setupSpeedSubscriber() {
        speedSubscriber = new Subscriber<Float>() {
            @Override public void onCompleted() {
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "complete: Complete?");
                }
            }

            @Override public void onError(Throwable e) {
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "addPointToRouteList: speed in frag - ERROR" + e.toString());
                }
            }

            @Override public void onNext(final Float speed) {
                storeSpeedTicks(speed);
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "onNext: speed in MainFragment - " + speed);
                }
                speedChangeListener.speedChanged(speed);
            }
        };
    }

    private void setupSubscribers() {
        setupSpeedSubscriber();
        setupLocationSubscriber();
        setupMaxSpeedSubscriber();
    }


    private void writeTrip(Trip trip, ObjectOutputStream os) {
        trip.writeTrip(os);
    }

    private void setMetricFieldsToTripData(float fuelPriceFromSettings) {
        final float     startVal             = ConstantValues.START_VALUE;
        TripData        tripData             = getTripData();
        float           fuelSpent            = startVal;
        float           timeSpentForAllTrips = startVal;
        float           avgSpeedSum          = startVal;
        float           avgFuelCons          = startVal;
        float           maxSpeed             = startVal;
        float           distTravelled        = startVal;
        float           timeSum              = startVal;
        ArrayList<Trip> allTrips             = tripData.getTrips();
        int             tripQuantity         = allTrips.size();


        for (Trip trip : allTrips) {
            fuelSpent = fuelSpent + trip.getFuelSpent();
            timeSpentForAllTrips = timeSpentForAllTrips + trip.getTimeSpentForTrip();
            maxSpeed = CalculationUtils.findMaxSpeed(trip.getMaxSpeed(), maxSpeed);
        }

        for (Trip trip : allTrips) {
            float multiplier  = (trip.getTimeSpentForTrip() * 100) / timeSpentForAllTrips;
            float timeForTrip = trip.getTimeSpentForTrip() / 3600000;
            avgFuelCons = (avgFuelCons + trip.getAvgFuelConsumption() * multiplier) / 100;
            avgSpeedSum = (avgSpeedSum + (trip.getDistanceTravelled() / (timeForTrip)) * timeForTrip);
            timeSum = timeSum + timeForTrip;
        }

        avgFuelCons = avgFuelCons / tripQuantity;
        avgSpeedSum = avgSpeedSum / timeSum;
        distTravelled = CalculationUtils.calcDistTravelled(timeSpentForAllTrips, avgSpeedSum);

        tripData.setMaxSpeed(maxSpeed);
        tripData.setAvgSpeed(avgSpeedSum);
        tripData.setTimeSpentOnTrips(timeSpentForAllTrips);
        tripData.setDistanceTravelled(distTravelled);
        tripData.setAvgFuelConsumption(avgFuelCons);
        tripData.setFuelSpent(fuelSpent);
        tripData.setGasTank(tripData.getGasTank() - fuelSpent);
        tripData.setMoneyOnFuelSpent(fuelSpent * fuelPriceFromSettings);
    }

    private void addRoutePoint(LatLng routePoints, float speed) {
        Route routePoint = new Route(routePoints, speed);
        addRoutePoint(routePoint);
    }

    private void updateSpeed(float avgSpeed, float maxSpd) {
        averageSpeed = avgSpeed;
        Trip trip = tripData.getTrip(currentTripId);
        trip.setAvgSpeed(averageSpeed);
        trip.setMaxSpeed(maxSpd);
    }

    private void addPointToRouteList(Location location) {
        LatLng routePoints = new LatLng(location.getLatitude(), location.getLongitude());
        float  speed;
        // TODO: 10.05.2016 remove debug code
        if (ConstantValues.DEBUG_MODE) {//debug code for testing
            Random r = new Random();
            speed = 0 + tempVal[0];
            if (speed != 0) {           //speed increment by 5 each tick
                tempVal[0] += 5;
            }
            if (speed > 70) {
                speed = r.nextInt(200);
            }
        }
        else {
            speed = CalculationUtils.getSpeedInKilometerPerHour(location.getSpeed());
        }
        addRoutePoint(routePoints, speed);
    }

    private void storeSpeedTicks(final float speed) {
        if (avgSpeedArrayList != null) {
            avgSpeedArrayList.add(speed);
        }
    }

    private void addRoutePoint(Route routePoint) {
        Trip trip = tripData.getTrip(currentTripId);
        trip.setRoute(routes);
        routes.add(routePoint);
    }

    private void setTripFieldsToStartState() {
        currentTripId = ConstantValues.START_VALUE;
        tripStartTime = ConstantValues.START_VALUE;
        averageSpeed = ConstantValues.START_VALUE;
    }

    private void getTripDataFieldsValues() {
        float distanceTravelled = ConstantValues.START_VALUE;
        float fuelSpent         = ConstantValues.START_VALUE;
        float fuelConsumption   = ConstantValues.START_VALUE;
        for (Trip trip : tripData.getTrips()) {
            distanceTravelled = distanceTravelled + trip.getDistanceTravelled();
            fuelSpent = fuelSpent + trip.getFuelSpent();
            fuelConsumption = fuelConsumption + trip.getAvgFuelConsumption();
        }
        float moneySpent = CalculationUtils.calcMoneySpent(fuelSpent, fuelPrice);
        fuelConsumption = fuelConsumption / tripData.getTrips().size();

        tripData.setDistanceTravelled(distanceTravelled);
        tripData.setAvgFuelConsumption(fuelConsumption);
        tripData.setMoneyOnFuelSpent(moneySpent);
        tripData.setFuelSpent(fuelSpent);
    }

    private void fillGasTank(float fuel) {
        if (tripData != null) {
            float gasTank = tripData.getGasTank();
            if (ConstantValues.LOGGING_ENABLED) {
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
        }
    }

    private void updateTrip(Trip trip) {
        if (ConstantValues.LOGGING_ENABLED) {
            Log.d(LOG_TAG, "updateTrip: called");
        }
        trip.setAvgSpeed(averageSpeed);
        if (routes != null) {
            trip.setRoute(routes);
        }
    }

    private void updateTripState() {
        Trip trip = tripData.getTrip(currentTripId);
        tripData.updateTrip(trip, currentTripId);
    }

    private void writeDataToFile() {
        WriteFileTask writeFileTask = new WriteFileTask();
        writeFileTask.execute(tripData);
    }

    private void endTrip() {
        if (ConstantValues.LOGGING_ENABLED) {
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


    private class WriteFileTask extends AsyncTask<TripData, Void, Boolean> {
        @Override protected Boolean doInBackground(TripData... params) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "writeTripDataToFile: writeCalled");
                Log.d(LOG_TAG, "writeTripDataToFile: avgSpeed - " + tripData.getAvgSpeed());
            }
            FileOutputStream fos;
            ArrayList<Trip>  trips = tripData.getTrips();
            getTripDataFieldsValues();
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "WRITE: distTravelledForTripData " + tripData.getDistanceTravelled());
                Log.d(LOG_TAG, "WRITE: avgConsForTripData " + tripData.getAvgFuelConsumption());
                Log.d(LOG_TAG, "WRITE: fuelSpentForTripData " + tripData.getFuelSpent());
                Log.d(LOG_TAG, "WRITE: moneyOnFuelForTripData " + tripData.getMoneyOnFuelSpent());
                Log.d(LOG_TAG, "WRITE: avgSpeedForTripData " + tripData.getAvgSpeed());
            }
            int   tripsSize          = trips.size();
            float distanceTravelled  = tripData.getDistanceTravelled();
            float avgFuelConsumption = tripData.getAvgFuelConsumption();
            float fuelSpent          = tripData.getFuelSpent();
            float moneyOnFuelSpent   = tripData.getMoneyOnFuelSpent();
            float avgSpeed           = tripData.getAvgSpeed();
            float timeSpent          = tripData.getTimeSpentOnTrips();
            float gasTankCapacity    = tripData.getGasTank();
            float maxSpeed           = tripData.getMaxSpeed();

            try {
                fos = context.openFileOutput(ConstantValues.FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeInt(tripsSize);
                for (Trip trip : trips) {
                    if (ConstantValues.LOGGING_ENABLED) {
                        Log.d(LOG_TAG, "writeTripDataToFile: trip to string - " + trip.toString());
                    }
                    writeTrip(trip, os);
                }
                os.writeFloat(avgFuelConsumption);
                os.writeFloat(fuelSpent);
                os.writeFloat(distanceTravelled);
                os.writeFloat(moneyOnFuelSpent);
                os.writeFloat(avgSpeed);
                os.writeFloat(timeSpent);
                os.writeFloat(gasTankCapacity);
                os.writeFloat(maxSpeed);
                os.close();
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "writeTripDataToFile: " + tripData.getTrips().toString());
            }
            return true;
        }

        @Override protected void onPostExecute(Boolean result) {
            if (result) {
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "file written successfully");
                }
            }
        }
    }

    private class ReadFileTask extends AsyncTask<String, Integer, TripData> {
        @Override protected TripData doInBackground(String... params) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "readFile");
            }
            fileIsInWriteMode = true;
            File file = context.getFileStreamPath(ConstantValues.FILE_NAME);
            if (file.exists()) {
                if (ConstantValues.LOGGING_ENABLED) {
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
                    if (ConstantValues.LOGGING_ENABLED) {
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
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "TripProcessor: file is empty");
                }
                tripData = new TripData();
            }
            return tripData;
        }

        @Override protected void onPostExecute(TripData tripData) {
            super.onPostExecute(tripData);
            fileIsInWriteMode = false;
        }
    }
}



