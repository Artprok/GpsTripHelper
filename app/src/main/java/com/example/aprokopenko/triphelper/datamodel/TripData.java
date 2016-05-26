package com.example.aprokopenko.triphelper.datamodel;

import android.os.Parcelable;
import android.os.Parcel;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

public class TripData implements Parcelable {

    private float           avgFuelConsumption;
    private float           distanceTravelled;
    private float           timeSpentOnTrips;
    private float           moneyOnFuelSpent;
    private float           fuelSpent;
    private float           avgSpeed;
    private float           maxSpeed;
    private float           gasTank;
    private ArrayList<Trip> trips;

    public TripData() {
        trips = new ArrayList<>();
        avgFuelConsumption = ConstantValues.START_VALUE;
        distanceTravelled = ConstantValues.START_VALUE;
        timeSpentOnTrips = ConstantValues.START_VALUE;
        moneyOnFuelSpent = ConstantValues.START_VALUE;
        fuelSpent = ConstantValues.START_VALUE;
        avgSpeed = ConstantValues.START_VALUE;
        maxSpeed = ConstantValues.START_VALUE;
        gasTank = ConstantValues.START_VALUE;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(avgFuelConsumption);
        dest.writeFloat(distanceTravelled);
        dest.writeFloat(timeSpentOnTrips);
        dest.writeFloat(moneyOnFuelSpent);
        dest.writeFloat(fuelSpent);
        dest.writeFloat(avgSpeed);
        dest.writeFloat(maxSpeed);
        dest.writeFloat(gasTank);
        dest.writeTypedList(trips);
    }


    public static final Creator<TripData> CREATOR = new Creator<TripData>() {
        @Contract("_ -> !null") @Override public TripData createFromParcel(Parcel in) {
            return new TripData(in);
        }

        @Contract(value = "_ -> !null", pure = true) @Override public TripData[] newArray(int size) {
            return new TripData[size];
        }
    };

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    public Trip getTrip(int index) {
        return trips.get(index);
    }

    public float getAvgFuelConsumption() {
        return avgFuelConsumption;
    }

    public float getDistanceTravelled() {
        return distanceTravelled;
    }

    public float getMoneyOnFuelSpent() {
        return moneyOnFuelSpent;
    }

    public float getTimeSpentOnTrips() {
        return timeSpentOnTrips;
    }

    public float getFuelSpent() {
        return fuelSpent;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getGasTank() {
        return gasTank;
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

    public void setTimeSpentOnTrips(float timeSpentOnTrips) {
        this.timeSpentOnTrips = timeSpentOnTrips;
    }

    public void setFuelSpent(float fuelSpent) {
        this.fuelSpent = fuelSpent;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setTrips(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    public void setGasTank(float gasTank) {
        this.gasTank = gasTank;
    }

    public void updateTrip(Trip trip, int index) {
        trips.set(index, trip);
    }

    public void addTrip(Trip trip) {
        trips.add(trip);
    }

    private TripData(Parcel in) {
        avgFuelConsumption = in.readFloat();
        distanceTravelled = in.readFloat();
        timeSpentOnTrips = in.readFloat();
        moneyOnFuelSpent = in.readFloat();
        fuelSpent = in.readFloat();
        avgSpeed = in.readFloat();
        maxSpeed = in.readFloat();
        gasTank = in.readFloat();
        trips = in.createTypedArrayList(Trip.CREATOR);
    }
}

