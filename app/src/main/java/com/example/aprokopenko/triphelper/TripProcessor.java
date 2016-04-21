package com.example.aprokopenko.triphelper;

import android.content.Context;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.MathUtils;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.datamodel.Trip;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.io.File;


public class TripProcessor {
    private static final String LOG_TAG = "TripProcessor";
    private final float            currentFuelConsumption;
    private final Calendar         calendar;
    private final ArrayList<Route> route;
    private       long             tripStartTime;
    private       int              currentTripId;
    private       float            averageSpeed;
    private       TripData         tripData;

    public TripProcessor(Context context) {
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "TripProcessor: IsConstructed");
        }

        // TODO: 13.04.2016 CurrentFuelConsumption is for changing fuelConsumption level depends on average speed or time onStop for example
        // TODO: 13.04.2016 Create this functional in future somehow!
        currentFuelConsumption = ConstantValues.FUEL_CONSUMPTION;
        calendar = Calendar.getInstance();
        route = new ArrayList<>();
        if (tripData == null) {
            readTripDataFromFile(context);
        }
    }

    public TripData getTripData() {
        return tripData;
    }

    public ArrayList<Route> getRoutes() {
        return route;
    }

    public void addRoutePoint(Route routePoint) {
        Trip trip = tripData.getTrip(currentTripId);
        trip.setRoute(route);
        route.add(routePoint);
    }

    public float getFuelLeft() {
        if (tripData != null) {
            Log.d(LOG_TAG, "getFuelLeft: " + tripData.getGasTank());

            return tripData.getGasTank();
        }
        else {
            return 0;
        }
    }

    public void startNewTrip() {
        tripStartTime = calendar.getTime().getTime();
        currentTripId = tripData.getTrips().size();
        Calendar curCalendar   = Calendar.getInstance();
        Date     date          = curCalendar.getTime();
        String   formattedDate = UtilMethods.parseDate(date);
        Trip     trip          = new Trip(currentTripId, formattedDate);
        tripData.addTrip(trip);

    }

    public void endTrip() {
        Log.d(LOG_TAG, "endTrip: end called");
        updateTrip();

        Trip  trip             = getCurrentTrip();
        long  timeSpent        = calcTimeInTrip();
        float distanceTraveled = setDistanceCovered(trip, timeSpent);
        float fuelConsumption  = getFuelConsumptionLevel(averageSpeed);
        float fuelSpent        = distanceTraveled * (fuelConsumption / 100);
        float moneySpent       = fuelSpent * ConstantValues.FUEL_COST;
        Log.d(LOG_TAG, "endTrip: " + fuelSpent);

        trip.setMoneyOnFuelSpent(moneySpent);
        trip.setFuelSpent(fuelSpent);
        trip.setDistanceTravelled(distanceTraveled);
        trip.setAvgSpeed(averageSpeed);
        trip.setTimeSpent(timeSpent);
        trip.setRoute(route);
        trip.setAvgFuelConsumption(fuelConsumption);

        updateTripState();
        setTripFieldsToStartState();
    }


    public void eraseFile(Context context) {
        context.deleteFile(ConstantValues.FILE_NAME);
    }

    public void fillGasTank(float fuel) {
        float gasTank = tripData.getGasTank();
        if (gasTank < ConstantValues.FUEL_TANK_CAPACITY) {
            if (gasTank + fuel < ConstantValues.FUEL_TANK_CAPACITY) {
                tripData.setGasTank(gasTank + fuel);
            }
        }
    }

    public void writeTripDataToFile(Context context) {
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "writeTripDataToFile: writeCalled");
            Log.d(LOG_TAG, "writeTripDataToFile: " + tripData.getAvgSpeed());
        }
        FileOutputStream fos;
        ArrayList<Trip>  trips = tripData.getTrips();
        getTripDataFieldsValues();
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "WRITE: " + tripData.getAvgFuelConsumption());
            Log.d(LOG_TAG, "WRITE: " + tripData.getFuelFilled());
            Log.d(LOG_TAG, "WRITE: " + tripData.getFuelSpent());
            Log.d(LOG_TAG, "WRITE: " + tripData.getDistanceTravelled());
            Log.d(LOG_TAG, "WRITE: " + tripData.getMoneyOnFuelSpent());
            Log.d(LOG_TAG, "WRITE: " + tripData.getAvgSpeed());
        }


        int   tripsSize          = trips.size();
        float avgFuelConsumption = tripData.getAvgFuelConsumption();
        float fuelFilled         = tripData.getFuelFilled();
        float fuelSpent          = tripData.getFuelSpent();
        float distanceTravelled  = tripData.getDistanceTravelled();
        float moneyOnFuelSpent   = tripData.getMoneyOnFuelSpent();
        float avgSpeed           = tripData.getAvgSpeed();
        float timeSpent          = tripData.getTimeSpentOnTrips();
        float gasTankCapacity    = tripData.getGasTank();

        try {
            fos = context.openFileOutput(ConstantValues.FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);

            os.writeInt(tripsSize);
            for (Trip trip : trips) {
                Log.d(LOG_TAG, "writeTripDataToFile: trips " + trip.toString());
                writeTrip(trip, os);
            }

            os.writeFloat(avgFuelConsumption);
            os.writeFloat(fuelFilled);
            os.writeFloat(fuelSpent);
            os.writeFloat(distanceTravelled);
            os.writeFloat(moneyOnFuelSpent);
            os.writeFloat(avgSpeed);
            os.writeFloat(timeSpent);
            os.writeFloat(gasTankCapacity);


            os.close();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "writeTripDataToFile: " + tripData.getTrips().toString());

    }

    public void updateAvgSpeed(float speed) {
        averageSpeed = speed;
        Trip trip = tripData.getTrip(currentTripId);
        trip.setAvgSpeed(averageSpeed);
    }


    private TripData createTripData(ArrayList<Trip> trips, float avgFuelConsumption, float fuelFilled, float fuelSpent,
                                    float distanceTravelled, float moneyOnFuelSpent, float avgSpeed, float timeSpent, float gasTank) {
        TripData tripData = new TripData();
        tripData.setTrips(trips);
        tripData.setDistanceTravelled(distanceTravelled);
        tripData.setFuelFilled(fuelFilled);
        tripData.setFuelSpent(fuelSpent);
        tripData.setMoneyOnFuelSpent(moneyOnFuelSpent);
        tripData.setAvgFuelConsumption(avgFuelConsumption);
        tripData.setAvgSpeed(avgSpeed);
        tripData.setTimeSpentOnTrips(timeSpent);
        tripData.setGasTank(gasTank);
        return tripData;
    }

    private Trip getCurrentTrip() {
        return tripData.getTrip(currentTripId);
    }

    private float setDistanceCovered(Trip trip, long timeSpent) {
        float avgSpeed = trip.getAvgSpeed();
        return MathUtils.calcDistTravelled(timeSpent, avgSpeed);
    }

    private Trip readTrip(ObjectInputStream is) {
        Trip trip = new Trip();
        trip = trip.readTrip(is);
        return trip;
    }

    private long calcTimeInTrip() {
        Calendar curCal  = Calendar.getInstance();
        long     endTime = curCal.getTime().getTime();
        return endTime - tripStartTime;
    }

    private float getFuelConsumptionLevel(float avgSpeed) {
        float initialConsumption = ConstantValues.FUEL_CONSUMPTION;
        if (avgSpeed > ConstantValues.HIGHWAY_SPEED_AVG_SPEED) {
            return initialConsumption + ConstantValues.CONSUMPTION_HIGHWAY_TRAFFIC_ADD;
        }
        else if (avgSpeed > ConstantValues.LOW_TRAFFIC_AVG_SPEED) {
            return initialConsumption + ConstantValues.CONSUMPTION_LOW_TRAFFIC_ADD;
        }
        else if (avgSpeed > ConstantValues.MEDIUM_TRAFFIC_AVG_SPEED) {
            return initialConsumption + ConstantValues.CONSUMPTION_MEDIUM_TRAFFIC_ADD;
        }
        else if (avgSpeed > ConstantValues.HIGH_TRAFFIC_AVG_SPEED) {
            return initialConsumption + ConstantValues.CONSUMPTION_HIGH_TRAFFIC_ADD;
        }
        else if (avgSpeed < ConstantValues.VETY_HIGH_TRAFFIC_AVG_SPEED) {
            return initialConsumption + ConstantValues.CONSUMPTION_VERY_HIGH_TRAFFIC_ADD;
        }
        else {
            Log.d(LOG_TAG, "getFuelConsumptionLevel: Impossible thing!");
            return initialConsumption;
        }
    }


    private void updateTrip() {
        Log.d(LOG_TAG, "updateTrip: called");
        Trip trip = getCurrentTrip();
        trip.setAvgSpeed(averageSpeed);
        if (route != null) {
            trip.setRoute(route);
        }
    }

    private void readTripDataFromFile(Context context) {
        File file = context.getFileStreamPath(ConstantValues.FILE_NAME);
        if (file.exists()) {
            Log.d(LOG_TAG, "readTripDataFromFile: ");
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
                float fuelFilled         = is.readFloat();
                float fuelSpent          = is.readFloat();
                float distanceTravelled  = is.readFloat();
                float moneyOnFuelSpent   = is.readFloat();
                float avgSpeed           = is.readFloat();
                float timeSpent          = is.readFloat();
                float gasTankCapacity    = is.readFloat();

                this.tripData = createTripData(trips, avgFuelConsumption, fuelFilled, fuelSpent, distanceTravelled, moneyOnFuelSpent,
                        avgSpeed, timeSpent, gasTankCapacity);
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "READ: " + avgFuelConsumption);
                    Log.d(LOG_TAG, "READ: " + fuelFilled);
                    Log.d(LOG_TAG, "READ: " + fuelSpent);
                    Log.d(LOG_TAG, "READ: " + distanceTravelled);
                    Log.d(LOG_TAG, "READ: " + moneyOnFuelSpent);
                    Log.d(LOG_TAG, "READ: " + avgSpeed);
                    Log.d(LOG_TAG, "READ: " + timeSpent);
                }
                is.close();
                fis.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.d(LOG_TAG, "TripProcessor: file is empty");
            tripData = new TripData();
        }
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
        float distanceTravelled = 0;
        float fuelSpent         = 0;
        float fuelConsumption   = 0;
        for (Trip trip : tripData.getTrips()) {
            distanceTravelled = distanceTravelled + trip.getDistanceTravelled();
            fuelSpent = fuelSpent + trip.getFuelSpent();
            fuelConsumption = fuelConsumption + trip.getAvgFuelConsumption();
        }
        float moneySpent = fuelSpent * ConstantValues.FUEL_COST;
        fuelConsumption = fuelConsumption / tripData.getTrips().size();

        tripData.setMoneyOnFuelSpent(moneySpent);
        tripData.setDistanceTravelled(distanceTravelled);
        tripData.setFuelSpent(fuelSpent);
        tripData.setAvgFuelConsumption(fuelConsumption);
    }

    private void updateTripState() {
        Trip trip = tripData.getTrip(currentTripId);
        tripData.updateTrip(trip, currentTripId);
    }
}



