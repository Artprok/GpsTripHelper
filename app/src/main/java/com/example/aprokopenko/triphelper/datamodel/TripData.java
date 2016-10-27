package com.example.aprokopenko.triphelper.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.util.ArrayList;

/**
 * Class represents overall information about all {@link Trip}
 */
public class TripData implements Parcelable {

  private float avgFuelConsumption;
  private float distanceTravelled;
  private float timeSpentOnTrips;
  private float moneyOnFuelSpent;
  private float fuelSpent;
  private float avgSpeed;
  private float maxSpeed;
  private float gasTank;
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

  @Override public void writeToParcel(@NonNull final Parcel dest,final int flags) {
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
    public TripData createFromParcel(Parcel in) {
      return new TripData(in);
    }

    @Override public TripData[] newArray(final int size) {
      return new TripData[size];
    }
  };

  public ArrayList<Trip> getTrips() {
    return trips;
  }

  public Trip getTrip(final int index) {
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

  public void setAvgFuelConsumption(final float avgFuelConsumption) {
    this.avgFuelConsumption = avgFuelConsumption;
  }

  public void setDistanceTravelled(final float distanceTravelled) {
    this.distanceTravelled = distanceTravelled;
  }

  public void setMoneyOnFuelSpent(final float moneyOnFuelSpent) {
    this.moneyOnFuelSpent = moneyOnFuelSpent;
  }

  public void setTimeSpentOnTrips(final float timeSpentOnTrips) {
    this.timeSpentOnTrips = timeSpentOnTrips;
  }

  public void setFuelSpent(final float fuelSpent) {
    this.fuelSpent = fuelSpent;
  }

  public void setAvgSpeed(final float avgSpeed) {
    this.avgSpeed = avgSpeed;
  }

  public void setMaxSpeed(final float maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  public void setTrips(@NonNull final ArrayList<Trip> trips) {
    this.trips = trips;
  }

  public void setGasTank(final float gasTank) {
    this.gasTank = gasTank;
  }

  public void updateTrip(@NonNull final Trip trip) {
    int index = trip.getTripID();
    trips.set(index, trip);
  }

  public void addTrip(@NonNull final Trip trip) {
    trips.add(trip);
  }

  private TripData(@NonNull final Parcel in) {
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