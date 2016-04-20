package com.example.aprokopenko.triphelper.datamodel;


import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.util.ArrayList;

public class TripData {

    private float           avgFuelConsumption;
    private float           distanceTravelled;
    private float           moneyOnFuelSpent;
    private float           fuelFilled;
    private float           fuelSpent;
    private float           avgSpeed;
    private float           timeSpentOnTrips;
    private ArrayList<Trip> trips;
    private float           gasTank=0;

    public TripData() {
        // TODO: 31.03.2016 maybe testCode, maybe no. Not clear now
        trips = new ArrayList<>();
    }

    public float getGasTank() {
        return gasTank;
    }

    public void setGasTank(float gasTank) {
        this.gasTank = gasTank;
    }

    public float getTimeSpentOnTrips() {
        return timeSpentOnTrips;
    }

    public void setTimeSpentOnTrips(float timeSpentOnTrips) {
        this.timeSpentOnTrips = timeSpentOnTrips;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
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

    public float getFuelFilled() {
        return fuelFilled;
    }

    public float getFuelSpent() {
        return fuelSpent;
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

    public void setFuelFilled(float fuelFilled) {
        this.fuelFilled = fuelFilled;
    }

    public void setFuelSpent(float fuelSpent) {
        this.fuelSpent = fuelSpent;
    }

    public void setTrips(ArrayList<Trip> trips) {
        this.trips = trips;
    }

    public void addTrip(Trip trip) {
        trips.add(trip);
    }

    public void updateTrip(Trip trip, int index) {
        trips.set(index, trip);
    }

    public Trip getTrip(int index) {
        return trips.get(index);
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }


    private void updateTripData() {
        calcTripData();
    }

    private void calcTripData() {
        for (Trip trip : trips) {
            distanceTravelled = distanceTravelled + trip.getDistanceTravelled();
            fuelSpent = fuelSpent + trip.getFuelSpent();
            avgFuelConsumption = avgFuelConsumption + trip.getFuelConsumption();
        }
        avgFuelConsumption = avgFuelConsumption / trips.size();
        moneyOnFuelSpent = ConstantValues.FUEL_COST * fuelSpent;
    }
}

