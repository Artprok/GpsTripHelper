package com.example.aprokopenko.triphelper.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Trip implements Parcelable {
    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };
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

    public float getAvgFuelConsumption() {
        return avgFuelConsumption;
    }

    public void setAvgFuelConsumption(float avgFuelConsumption) {
        this.avgFuelConsumption = avgFuelConsumption;
    }

    public float getTimeSpentInMotion() {
        return timeSpentInMotion;
    }

    private void setTimeSpentInMotion(float timeSpentInMotion) {
        this.timeSpentInMotion = timeSpentInMotion;
    }

    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(float distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public float getMoneyOnFuelSpent() {
        return moneyOnFuelSpent;
    }

    public void setMoneyOnFuelSpent(float moneyOnFuelSpent) {
        this.moneyOnFuelSpent = moneyOnFuelSpent;
    }

    public float getTimeSpentOnStop() {
        return timeSpentOnStop;
    }

    public float getFuelSpent() {
        return fuelSpent;
    }

    public void setFuelSpent(float fuelSpent) {
        this.fuelSpent = fuelSpent;
    }

    public float getTimeSpentForTrip() {
        return timeSpent;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public ArrayList<Route> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Route> routes) {
        this.route = routes;
    }

    public String getTripDate() {
        return tripDate;
    }

    private void setTripDate(String tripDate) {
        this.tripDate = tripDate;
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

            TripInfoContainer tripInfoContainer = new TripInfoContainer(date, distanceTravelled, avgSpeed, timeSpent, timeSpentInMotion, 0,
                    fuelConsumption, fuelSpent, tripID, route, moneySpent, maxSpeed, trip);

            trip = createTripFromData(tripInfoContainer);
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return trip;
    }

    private Trip createTripFromData(TripInfoContainer tripInfoContainer) {
        Trip trip = tripInfoContainer.getTrip();
        trip.setTripDate(tripInfoContainer.getDate());
        trip.setRoute(tripInfoContainer.getRoutes());
        trip.setDistanceTravelled(tripInfoContainer.getDistanceTravelled());
        trip.setTimeSpent(tripInfoContainer.getTimeSpentForTrip());
        trip.setTimeSpentInMotion(tripInfoContainer.getTimeSpentInMotion());
        trip.setTripID(tripInfoContainer.getId());
        trip.setFuelSpent(tripInfoContainer.getFuelSpent());
        trip.setAvgSpeed(tripInfoContainer.getAvgSpeed());
        trip.setMoneyOnFuelSpent(tripInfoContainer.getMoneyOnFuelSpent());
        trip.setAvgFuelConsumption(tripInfoContainer.getAvgFuelConsumption());
        trip.setMaxSpeed(tripInfoContainer.getMaxSpeed());
        return trip;
    }

    public void setTimeSpent(float timeSpent) {
        this.timeSpent = timeSpent;
    }

    public int getTripID() {
        return tripID;
    }

    private void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public void writeTrip(ObjectOutputStream os) {
        try {
            os.writeInt(route.size());
            for (Route routePoint : route) {
                LatLng tmpRoutePoint = routePoint.getRoutePoints();
                os.writeDouble(tmpRoutePoint.latitude);
                os.writeDouble(tmpRoutePoint.longitude);
                os.writeFloat(routePoint.getSpeed());
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
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
