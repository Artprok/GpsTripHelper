package com.example.aprokopenko.triphelper.datamodel;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Trip {
    private float            timeSpentInMotion;
    private float            distanceTravelled;
    private float            avgFuelConsumption;
    private float            timeSpentOnStop;
    private float            fuelSpent;
    private float            timeSpent;
    private String           tripDate;
    private float            avgSpeed;
    private float            moneyOnFuelSpent;
    private int              tripID;
    private ArrayList<Route> route;

    public Trip() {

    }

    public float getMoneyOnFuelSpent() {
        return moneyOnFuelSpent;
    }

    public void setMoneyOnFuelSpent(float moneyOnFuelSpent) {
        this.moneyOnFuelSpent = moneyOnFuelSpent;
    }

    private void setTimeSpentInMotion(float timeSpentInMotion) {
        this.timeSpentInMotion = timeSpentInMotion;
    }

    public void setAvgFuelConsumption(float avgFuelConsumption) {
        this.avgFuelConsumption = avgFuelConsumption;
    }

    public void setFuelSpent(float fuelSpent) {
        this.fuelSpent = fuelSpent;
    }

    private void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    private void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Trip(int tripID, String tripDate) {
        this.tripID = tripID;
        this.tripDate = tripDate;
    }

    public void setTimeSpent(float timeSpent) {
        this.timeSpent = timeSpent;
    }

    public void setDistanceTravelled(float distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
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

    public float getTimeSpentInMotion() {
        return timeSpentInMotion;
    }

    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    public float getAvgFuelConsumption() {
        return avgFuelConsumption;
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

    public void setRoute(ArrayList<Route> routes) {
        this.route = routes;
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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

            trip = createTripFromData(date, trip, route, timeSpentInMotion, distanceTravelled, fuelSpent, timeSpent, tripID, avgSpeed,
                    moneySpent, fuelConsumption);
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return trip;
    }

    private Trip createTripFromData(String date, Trip trip, ArrayList<Route> route, float timeSpentInMotion, float distanceTravelled,
                                    float fuelSpent, float timeSpent, int tripID, float avgSpeed, float moneySpent, float fuelConsumption) {
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
        return trip;
    }

}
