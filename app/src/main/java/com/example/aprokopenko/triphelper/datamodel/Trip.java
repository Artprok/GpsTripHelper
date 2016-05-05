package com.example.aprokopenko.triphelper.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.google.android.gms.maps.model.LatLng;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Trip implements Parcelable {
    private float            avgFuelConsumption;
    private float            timeSpentInMotion;
    private float            distanceTravelled;
    private float            moneyOnFuelSpent;
    private float            timeSpentOnStop;
    private float            fuelSpent;
    private float            timeSpent;
    private String           tripDate;
    private float            avgSpeed;
    private float            maxSpeed;
    private int              tripID;
    private ArrayList<Route> route;

    public Trip() {
        avgFuelConsumption = ConstantValues.START_VALUE;
        timeSpentInMotion = ConstantValues.START_VALUE;
        distanceTravelled = ConstantValues.START_VALUE;
        moneyOnFuelSpent = ConstantValues.START_VALUE;
        timeSpentOnStop = ConstantValues.START_VALUE;
        fuelSpent = ConstantValues.START_VALUE;
        timeSpent = ConstantValues.START_VALUE;
        avgSpeed = ConstantValues.START_VALUE;
        maxSpeed = ConstantValues.START_VALUE;
        tripID = ConstantValues.START_VALUE;
        tripDate = "tripDateStartVal";
        route = new ArrayList<>();
    }

    public Trip(int tripID, String tripDate) {
        this.tripID = tripID;
        this.tripDate = tripDate;
    }


    private Trip(Parcel in) {
        avgFuelConsumption = in.readFloat();
        timeSpentInMotion = in.readFloat();
        distanceTravelled = in.readFloat();
        moneyOnFuelSpent = in.readFloat();
        timeSpentOnStop = in.readFloat();
        fuelSpent = in.readFloat();
        timeSpent = in.readFloat();
        tripDate = in.readString();
        avgSpeed = in.readFloat();
        maxSpeed = in.readFloat();
        tripID = in.readInt();
        route = in.createTypedArrayList(Route.CREATOR);
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public float getAvgFuelConsumption() {
        return avgFuelConsumption;
    }

    public float getTimeSpentInMotion() {
        return timeSpentInMotion;
    }

    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    public float getMoneyOnFuelSpent() {
        return moneyOnFuelSpent;
    }

    public float getTimeSpentOnStop() {
        return timeSpentOnStop;
    }

    public float getFuelSpent() {
        return fuelSpent;
    }

    public float getTimeSpent() {
        return timeSpent;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public ArrayList<Route> getRoute() {
        return route;
    }

    public String getTripDate() {
        return tripDate;
    }

    public int getTripID() {
        return tripID;
    }

    public void setAvgFuelConsumption(float avgFuelConsumption) {
        this.avgFuelConsumption = avgFuelConsumption;
    }

    public void setDistanceTravelled(float distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public void setMoneyOnFuelSpent(float moneyOnFuelSpent) {
        this.moneyOnFuelSpent = moneyOnFuelSpent;
    }

    public void setTimeSpent(float timeSpent) {
        this.timeSpent = timeSpent;
    }

    public void setFuelSpent(float fuelSpent) {
        this.fuelSpent = fuelSpent;
    }

    public void setRoute(ArrayList<Route> routes) {
        this.route = routes;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Trip readTrip(ObjectInputStream is) {
        ArrayList<Route> route = new ArrayList<>();
        int              routeSize;
        Trip             trip  = new Trip();
        try {
            routeSize = is.readInt();
            for (int i = 0; i < routeSize; i++) {
                double tmpLatitude  = is.readDouble();
                double tmpLongitude = is.readDouble();
                float  tmpSpeed     = is.readFloat();

                Route tmpRoutePoint = new Route(new LatLng(tmpLatitude, tmpLongitude), tmpSpeed);
                route.add(tmpRoutePoint);
            }
            float  timeSpentInMotion = is.readFloat();
            float  distanceTravelled = is.readFloat();
            float  fuelSpent         = is.readFloat();
            float  timeSpent         = is.readFloat();
            int    tripID            = is.readInt();
            String date              = (String) is.readObject();
            float  avgSpeed          = is.readFloat();
            float  moneySpent        = is.readFloat();
            float  fuelConsumption   = is.readFloat();
            float  maxSpeed          = is.readFloat();

            trip = createTripFromData(date, trip, route, timeSpentInMotion, distanceTravelled, fuelSpent, timeSpent, tripID, avgSpeed,
                    moneySpent, fuelConsumption, maxSpeed);
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return trip;
    }

    public void writeTrip(ObjectOutputStream os) {
        try {
            os.writeInt(route.size());
            for (Route routePoint : route) {
                LatLng tmpRoutePoint = routePoint.getRoutePoints();
                os.writeDouble(tmpRoutePoint.latitude);
                os.writeDouble(tmpRoutePoint.longitude);
                os.writeFloat(routePoint.getSpeed());
                Log.d("LOG_TAG", "writeTrip: " + routePoint.getSpeed());
            }
            os.writeFloat(timeSpentInMotion);
            os.writeFloat(distanceTravelled);
            os.writeFloat(fuelSpent);
            os.writeFloat(timeSpent);
            os.writeInt(tripID);
            os.writeObject(tripDate);
            os.writeFloat(avgSpeed);
            os.writeFloat(moneyOnFuelSpent);
            os.writeFloat(avgFuelConsumption);
            os.writeFloat(maxSpeed);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Trip createTripFromData(String date, Trip trip, ArrayList<Route> route, float timeSpentInMotion, float distanceTravelled,
                                    float fuelSpent, float timeSpent, int tripID, float avgSpeed, float moneySpent, float fuelConsumption,
                                    float maxSpeed) {
        trip.setTripDate(date);
        trip.setRoute(route);
        trip.setDistanceTravelled(distanceTravelled);
        trip.setTimeSpent(timeSpent);
        trip.setTimeSpentInMotion(timeSpentInMotion);
        trip.setTripID(tripID);
        trip.setFuelSpent(fuelSpent);
        trip.setAvgSpeed(avgSpeed);
        trip.setMoneyOnFuelSpent(moneySpent);
        trip.setAvgFuelConsumption(fuelConsumption);
        trip.setMaxSpeed(maxSpeed);
        return trip;
    }

    private void setTimeSpentInMotion(float timeSpentInMotion) {
        this.timeSpentInMotion = timeSpentInMotion;
    }

    private void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    private void setTripID(int tripID) {
        this.tripID = tripID;
    }


    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(avgFuelConsumption);
        dest.writeFloat(timeSpentInMotion);
        dest.writeFloat(distanceTravelled);
        dest.writeFloat(moneyOnFuelSpent);
        dest.writeFloat(timeSpentOnStop);
        dest.writeFloat(fuelSpent);
        dest.writeFloat(timeSpent);
        dest.writeString(tripDate);
        dest.writeFloat(avgSpeed);
        dest.writeFloat(maxSpeed);
        dest.writeInt(tripID);
        dest.writeTypedList(route);
    }
}
