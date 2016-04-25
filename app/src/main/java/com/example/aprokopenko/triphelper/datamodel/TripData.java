package com.example.aprokopenko.triphelper.datamodel;


import java.util.ArrayList;

public class TripData {

    private float           avgFuelConsumption;
    private float           distanceTravelled;
    private float           timeSpentOnTrips;
    private float           moneyOnFuelSpent;
    private float           fuelFilled;
    private float           fuelSpent;
    private float           avgSpeed;
    private float           maxSpeed;
    private float           gasTank;
    private ArrayList<Trip> trips;

    public TripData() {
        trips = new ArrayList<>();
        gasTank = 0;
    }

    public ArrayList<Trip> getTrips() {
        return trips;
    }

    public Trip getTrip(int index) {
        return trips.get(index);
    }

    public float getGasTank() {
        return gasTank;
    }

    public float getTimeSpentOnTrips() {
        return timeSpentOnTrips;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
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

    public void setGasTank(float gasTank) {
        this.gasTank = gasTank;
    }

    public void setTimeSpentOnTrips(float timeSpentOnTrips) {
        this.timeSpentOnTrips = timeSpentOnTrips;
    }

    public void setAvgSpeed(float avgSpeed) {
        this.avgSpeed = avgSpeed;
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

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}

