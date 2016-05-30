package com.example.aprokopenko.triphelper;

import android.content.Context;
import android.os.Parcelable;
import android.os.AsyncTask;
import android.os.Parcel;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.datamodel.Trip;

import java.util.concurrent.ExecutionException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.io.File;

public class TripProcessor implements Parcelable {
    private static final String LOG_TAG = "TripProcessor";
    private       boolean          fileIsInWriteMode;
    private       long             tripStartTime;
    private       int              currentTripId;
    private       float            averageSpeed;
    private       TripData         tripData;
    private final Context          context;
    private final ArrayList<Route> routes;

    private final float fuelConsFromSettings;
    private final float fuelPrice;
    private final int   fuelCapacity;

    public TripProcessor(Context context, float fuelConsFromSettings, float fuelPrice, int fuelCapacity) {
        if (ConstantValues.LOGGING_ENABLED) {
            Log.i(LOG_TAG, "TripProcessor: IsConstructed");
        }
        this.fuelConsFromSettings = fuelConsFromSettings;
        this.fuelPrice = fuelPrice;
        this.fuelCapacity = fuelCapacity;


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

    protected TripProcessor(Parcel in) {
        fileIsInWriteMode = in.readByte() != 0;
        tripStartTime = in.readLong();
        currentTripId = in.readInt();
        averageSpeed = in.readFloat();
        tripData = in.readParcelable(TripData.class.getClassLoader());
        routes = in.createTypedArrayList(Route.CREATOR);
        fuelConsFromSettings = in.readFloat();
        fuelPrice = in.readFloat();
        fuelCapacity = in.readInt();
        context = TripHelperApp.getAppContext();
    }

    public static final Creator<TripProcessor> CREATOR = new Creator<TripProcessor>() {
        @Override public TripProcessor createFromParcel(Parcel in) {
            return new TripProcessor(in);
        }

        @Override public TripProcessor[] newArray(int size) {
            return new TripProcessor[size];
        }
    };

    public boolean isFileNotInWriteMode() {
        return !fileIsInWriteMode;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public TripData getTripData() {
        return tripData;
    }

    public float getFuelLeft() {
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

    public void startNewTrip() {
        Calendar currentCalendarInstance = Calendar.getInstance();
        tripStartTime = currentCalendarInstance.getTime().getTime();
        currentTripId = tripData.getTrips().size();
        Date   date          = currentCalendarInstance.getTime();
        String formattedDate = UtilMethods.parseDate(date);
        Trip   trip          = new Trip(currentTripId, formattedDate);
        tripData.addTrip(trip);
    }

    public void endTrip() {
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

    public void updateSpeed(float avgSpeed, float maxSpd) {
        averageSpeed = avgSpeed;
        Trip trip = tripData.getTrip(currentTripId);
        trip.setAvgSpeed(averageSpeed);
        trip.setMaxSpeed(maxSpd);
    }

    public void addRoutePoint(Route routePoint) {
        Trip trip = tripData.getTrip(currentTripId);
        trip.setRoute(routes);
        routes.add(routePoint);
    }

    public void fillGasTank(float fuel) {
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

    public void writeDataToFile() {
        WriteFileTask writeFileTask = new WriteFileTask();
        writeFileTask.execute(tripData);
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

    private Trip readTrip(ObjectInputStream is) {
        Trip trip = new Trip();
        trip = trip.readTrip(is);
        return trip;
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


    private void writeTrip(Trip trip, ObjectOutputStream os) {
        trip.writeTrip(os);
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

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (fileIsInWriteMode ? 1 : 0));
        parcel.writeLong(tripStartTime);
        parcel.writeInt(currentTripId);
        parcel.writeFloat(averageSpeed);
        parcel.writeParcelable(tripData, i);
        parcel.writeTypedList(routes);
        parcel.writeFloat(fuelConsFromSettings);
        parcel.writeFloat(fuelPrice);
        parcel.writeInt(fuelCapacity);
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



