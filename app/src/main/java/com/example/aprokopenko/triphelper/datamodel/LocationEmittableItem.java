package com.example.aprokopenko.triphelper.datamodel;

import android.location.Location;
import android.support.annotation.NonNull;

import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;

/**
 * Class for storing {@link Location}, speed and maxSpeed values.
 */
public class LocationEmittableItem {
  private Location location;
  private float speed;
  private float maxSpeed;

  public LocationEmittableItem(@NonNull final Location location) {
    this.location = location;
    speed = CalculationUtils.getSpeedInKilometerPerHour(location.getSpeed());
    maxSpeed = CalculationUtils.findMaxSpeed(speed, maxSpeed);
  }

  public Location getLocation() {
    return location;
  }

  public float getMaxSpeed() {
    return maxSpeed;
  }

  public float getSpeed() {
    return speed;
  }

  public void setLocation(@NonNull final Location location) {
    this.location = location;
  }

  public void setMaxSpeed(final float maxSpeed) {
    this.maxSpeed = CalculationUtils.findMaxSpeed(maxSpeed, this.maxSpeed);
  }

  /**
   * Method for setting speed in K/h.
   *
   * @param speed float valuse speed in m/sec
   */
  public void setSpeed(final float speed) {
    this.speed = CalculationUtils.getSpeedInKilometerPerHour(speed);
  }
}