package com.example.aprokopenko.triphelper.datamodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.util.ArrayList;

/**
 * Util class that contains common information about {@link Trip}
 */
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

  /**
   * Default constructor with all needed params.
   *
   * @param date               Date of trip in {@link String} format
   * @param distanceTravelled  distance travelled in {@link Float} format
   * @param avgSpeed           average speed in {@link Float} format
   * @param timeSpentForTrip   overall time spent for trip in {@link Float} format
   * @param timeSpentInMotion  part of all time spent in motion in {@link Float} format
   * @param timeSpentOnStop    part of all time spent while not moving in {@link Float} format
   * @param avgFuelConsumption average fuel consumption in {@link Float} format
   * @param fuelSpent          fuel spent during trip in {@link Float} format
   * @param id                 id of trip in {@link Integer} format
   * @param route              route (set of coordinates) represented by {@link ArrayList} of {@link Route}
   * @param moneyOnFuelSpent   amount of money spent on fuel in {@link Float} format
   * @param maxSpeed           value of maximum speed fixed in {@link Float} format
   * @param trip               {@link Trip} itself
   */
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