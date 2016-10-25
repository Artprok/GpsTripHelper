package com.example.aprokopenko.triphelper.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Trip implements Parcelable {
  private float avgFuelConsumption;
  private float timeSpentInMotion;
  private float distanceTravelled;
  private float moneyOnFuelSpent;
  private float timeSpentOnStop;
  private float fuelSpent;
  private float timeSpent;
  private String tripDate;
  private float avgSpeed;
  private float maxSpeed;
  private int tripID;
  private ArrayList<Route> route;

  public Trip() {
    avgFuelConsumption = ConstantValues.START_VALUE_f;
    timeSpentInMotion = ConstantValues.START_VALUE_f;
    distanceTravelled = ConstantValues.START_VALUE_f;
    moneyOnFuelSpent = ConstantValues.START_VALUE_f;
    timeSpentOnStop = ConstantValues.START_VALUE_f;
    fuelSpent = ConstantValues.START_VALUE_f;
    timeSpent = ConstantValues.START_VALUE_f;
    avgSpeed = ConstantValues.START_VALUE_f;
    maxSpeed = ConstantValues.START_VALUE_f;
    tripID = ConstantValues.START_VALUE;
    tripDate = "tripDateStartVal";
    route = new ArrayList<>();
  }

  public Trip(int tripID, String tripDate) {
    this.tripID = tripID;
    this.tripDate = tripDate;
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(@NonNull final Parcel dest, final int flags) {
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

  public static final Creator<Trip> CREATOR = new Creator<Trip>() {
    @Override public Trip createFromParcel(@NonNull final Parcel in) {
      return new Trip(in);
    }

    @Override public Trip[] newArray(final int size) {
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

  public float getTimeSpentForTrip() {
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

  public Trip readTrip(@NonNull final ObjectInputStream is) {
    ArrayList<Route> route = new ArrayList<>();
    final int routeSize;
    Trip trip = new Trip();
    try {
      routeSize = is.readInt();
      for (int i = 0; i < routeSize; i++) {
        final double tmpLatitude = is.readDouble();
        final double tmpLongitude = is.readDouble();
        final float tmpSpeed = is.readFloat();

        final Route tmpRoutePoint = new Route(new LatLng(tmpLatitude, tmpLongitude), tmpSpeed);
        route.add(tmpRoutePoint);
      }

      final float timeSpentInMotion = is.readFloat();
      final float distanceTravelled = is.readFloat();
      final float fuelSpent = is.readFloat();
      final float timeSpent = is.readFloat();
      final int tripID = is.readInt();
      final String date = (String) is.readObject();
      final float avgSpeed = is.readFloat();
      final float moneySpent = is.readFloat();
      final float fuelConsumption = is.readFloat();
      final float maxSpeed = is.readFloat();

      final TripInfoContainer tripInfoContainer = new TripInfoContainer(date, distanceTravelled, avgSpeed, timeSpent, timeSpentInMotion, 0,
              fuelConsumption, fuelSpent, tripID, route, moneySpent, maxSpeed, trip);

      trip = createTripFromData(tripInfoContainer);
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return trip;
  }

  public int getTripID() {
    return tripID;
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

  public void setTimeSpent(final float timeSpent) {
    this.timeSpent = timeSpent;
  }

  public void setFuelSpent(final float fuelSpent) {
    this.fuelSpent = fuelSpent;
  }

  public void setRoute(@NonNull final ArrayList<Route> routes) {
    this.route = routes;
  }

  public void setAvgSpeed(final float avgSpeed) {
    this.avgSpeed = avgSpeed;
  }

  public void setMaxSpeed(final float maxSpeed) {
    this.maxSpeed = maxSpeed;
  }

  public void writeTrip(@NonNull final ObjectOutputStream os) {
    Float distanceTravelled = getDistanceTravelled();
    Float avgFuelConsumption = getAvgFuelConsumption();
    Float fuelSpent = getFuelSpent();
    Float moneyOnFuelSpent = getMoneyOnFuelSpent();
    Float avgSpeed = getAvgSpeed();
    Float timeSpent = getTimeSpentForTrip();
    Float maxSpeed = getMaxSpeed();
    Float timeSpentInMotion = getTimeSpentInMotion();

    try {
      os.writeInt(route.size());
      for (Route routePoint : route) {
        final LatLng tmpRoutePoint = routePoint.getRoutePoints();
        os.writeDouble(tmpRoutePoint.latitude);
        os.writeDouble(tmpRoutePoint.longitude);
        writeToStreamWithNANcheck(os, routePoint.getSpeed());
      }
      writeToStreamWithNANcheck(os, timeSpentInMotion);
      writeToStreamWithNANcheck(os, distanceTravelled);
      writeToStreamWithNANcheck(os, fuelSpent);
      writeToStreamWithNANcheck(os, timeSpent);
      os.writeInt(tripID);
      os.writeObject(tripDate);
      writeToStreamWithNANcheck(os, avgSpeed);
      writeToStreamWithNANcheck(os, moneyOnFuelSpent);
      writeToStreamWithNANcheck(os, avgFuelConsumption);
      writeToStreamWithNANcheck(os, maxSpeed);
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
    }
  }

  private void writeToStreamWithNANcheck(@NonNull final ObjectOutputStream os, @Nullable final Float valueToWrite) throws IOException {
    if (valueToWrite == null || valueToWrite.isNaN()) {
      os.writeFloat(0f);
    } else {
      os.writeFloat(valueToWrite);
    }
  }

  private Trip createTripFromData(@NonNull final TripInfoContainer tripInfoContainer) {
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

  private Trip(@NonNull final Parcel in) {
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

  private void setTimeSpentInMotion(final float timeSpentInMotion) {
    this.timeSpentInMotion = timeSpentInMotion;
  }

  private void setTripDate(@NonNull final String tripDate) {
    this.tripDate = tripDate;
  }

  private void setTripID(final int tripID) {
    this.tripID = tripID;
  }
}