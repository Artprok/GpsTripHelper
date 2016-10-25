package com.example.aprokopenko.triphelper.datamodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.util.ArrayList;

public class TripInfoContainer {
  private float avgFuelConsumption = ConstantValues.NEGATIVE_VALUE_OF_TRIPINFO_ERROR_CODE;
  private float distanceTravelled = ConstantValues.NEGATIVE_VALUE_OF_TRIPINFO_ERROR_CODE;
  private float timeSpentInMotion = ConstantValues.NEGATIVE_VALUE_OF_TRIPINFO_ERROR_CODE;
  private float moneyOnFuelSpent = ConstantValues.NEGATIVE_VALUE_OF_TRIPINFO_ERROR_CODE;
  private float timeSpentForTrip = ConstantValues.NEGATIVE_VALUE_OF_TRIPINFO_ERROR_CODE;
  private float timeSpentOnStop = ConstantValues.NEGATIVE_VALUE_OF_TRIPINFO_ERROR_CODE;
  private float fuelSpent = ConstantValues.NEGATIVE_VALUE_OF_TRIPINFO_ERROR_CODE;
  private float avgSpeed = ConstantValues.NEGATIVE_VALUE_OF_TRIPINFO_ERROR_CODE;
  private float maxSpeed = ConstantValues.NEGATIVE_VALUE_OF_TRIPINFO_ERROR_CODE;
  private int id = ConstantValues.NEGATIVE_VALUE_OF_TRIPINFO_ERROR_CODE;

  private final ArrayList<Route> route;
  private final String date;
  private final Trip trip;

  public TripInfoContainer(@NonNull final String date, final float distanceTravelled, final float avgSpeed, final float timeSpentForTrip, final float timeSpentInMotion, final
  float timeSpentOnStop, final float avgFuelConsumption, final float fuelSpent, final int id, @NonNull final ArrayList<Route> route,
                           final float moneyOnFuelSpent, final float maxSpeed, @Nullable final Trip trip) {
    if (distanceTravelled >= 0) {
      this.distanceTravelled = distanceTravelled;
    }
    if (avgSpeed >= 0) {
      this.avgSpeed = avgSpeed;
    }
    if (timeSpentForTrip >= 0) {
      this.timeSpentForTrip = timeSpentForTrip;
    }
    if (timeSpentInMotion >= 0) {
      this.timeSpentInMotion = timeSpentInMotion;
    }
    if (timeSpentOnStop >= 0) {
      this.timeSpentOnStop = timeSpentOnStop;
    }
    if (avgFuelConsumption >= 0) {
      this.avgFuelConsumption = avgFuelConsumption;
    }
    if (fuelSpent >= 0) {
      this.fuelSpent = fuelSpent;
    }
    if (id >= 0) {
      this.id = id;
    }
    if (moneyOnFuelSpent >= 0) {
      this.moneyOnFuelSpent = moneyOnFuelSpent;
    }
    if (maxSpeed >= 0) {
      this.maxSpeed = maxSpeed;
    }
    this.route = route;
    this.trip = trip;
    this.date = date;
  }

  public Trip getTrip() {
    return trip;
  }

  public float getAvgFuelConsumption() {
    return avgFuelConsumption;
  }

  public float getDistanceTravelled() {
    return distanceTravelled;
  }

  public float getTimeSpentInMotion() {
    return timeSpentInMotion;
  }

  public float getMoneyOnFuelSpent() {
    return moneyOnFuelSpent;
  }

  public float getTimeSpentForTrip() {
    return timeSpentForTrip;
  }

  public float getTimeSpentOnStop() {
    return timeSpentOnStop;
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

  public ArrayList<Route> getRoutes() {
    return route;
  }

  public String getDate() {
    return date;
  }

  public int getId() {
    return id;
  }
}