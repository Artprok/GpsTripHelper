package com.example.aprokopenko.triphelper.utils.util_methods;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.util.ArrayList;
import java.util.Calendar;

public class CalculationUtils {
  private static float measurementUnitMultiplier = ConstantValues.KMH_MULTIPLIER;   //default val for KM/H

  public static float getSpeedInKilometerPerHour(final float speed) {
    return (speed * measurementUnitMultiplier);
  }

  public static float calcMoneySpent(final float fuelSpent, final float fuelCost) {
    return fuelSpent * fuelCost;
  }

  public static String getTimeInNormalFormat(final float timeInMills, @Nullable final Resources res) {
    String resultString;
    final String hoursPrefix;
    final String minutePrefix;
    final String secondsPrefix;
    if (res == null) {
      hoursPrefix = " h";
      minutePrefix = " m";
      secondsPrefix = " s";
    } else {
      hoursPrefix = res.getString(R.string.hourPrefix);
      minutePrefix = res.getString(R.string.minutePref);
      secondsPrefix = res.getString(R.string.secondPref);
    }

    final int seconds = getSecondsFromMills(timeInMills);
    final int minutes = getMinutesFromMills(timeInMills);
    final int hours = getHoursFromMills(timeInMills);
    if (hours == 0) {
      resultString = minutes + minutePrefix + ", " + seconds + secondsPrefix;
      if (minutes == 0) {
        resultString = seconds + secondsPrefix;
      }
    } else {
      resultString = hours + hoursPrefix + ", " + minutes + minutePrefix + ", " + seconds + secondsPrefix;
    }
    return resultString;
  }

  public static float calcFuelSpent(final float distanceTraveled, final float fuelConsumption) {
    return distanceTraveled * (fuelConsumption / ConstantValues.PER_100);
  }

  public static float calcAvgSpeedForOneTrip(@Nullable final ArrayList<Float> avgSpeedArrayList) {
    Float avgSpeed = 0f;
    if (avgSpeedArrayList != null) {
      for (Float tmpSpeed : avgSpeedArrayList) {
        avgSpeed = avgSpeed + tmpSpeed;
      }
      avgSpeed = avgSpeed / avgSpeedArrayList.size();
    }
    if (avgSpeed.isNaN()) {
      avgSpeed = (float) ConstantValues.START_VALUE;
    }
    return avgSpeed;
  }

  public static float setDistanceCoveredForTrip(@NonNull final Trip trip, final long timeSpent) {
    final float avgSpeed = trip.getAvgSpeed();
    return calcDistTravelled(timeSpent, avgSpeed);
  }

  public static float calcDistTravelled(final float timeSpent, final float avgSpeed) {
    Float distanceTravelled = avgSpeed * getTimeFromMills(timeSpent);
    if (distanceTravelled.isNaN()) {
      distanceTravelled = (float) ConstantValues.START_VALUE;
    }
    return distanceTravelled;
  }

  public static float findMaxSpeed(final float speed, final float initialVal) {
    if (speed > initialVal) {
      return speed;
    }
    return initialVal;
  }

  public static long calcTimeInTrip(final long tripStartTime) {
    if (tripStartTime <= 0) {
      return 0;
    } else {
      final Calendar curCal = Calendar.getInstance();
      final long endTime = curCal.getTime().getTime();
      return endTime - tripStartTime;
    }
  }

  public static void setMeasurementMultiplier(final int position) {
    float result = ConstantValues.KMH_MULTIPLIER;
    switch (position) {
      case 0:
        result = ConstantValues.KMH_MULTIPLIER;
        break;
      case 1:
        result = ConstantValues.MPH_MULTIPLIER;
        break;
      case 2:
        result = ConstantValues.KNOTS_MULTIPLIER;
        break;
    }
    measurementUnitMultiplier = result;
  }


  private static int getHoursFromMills(final float timeInMills) {
    return (int) ((timeInMills / (ConstantValues.MILLISECONDS_IN_SECOND * 60 * 60)) % ConstantValues.HOURS_IN_DAY);
  }

  private static int getMinutesFromMills(final float timeInMills) {
    return (int) ((timeInMills / (ConstantValues.MILLISECONDS_IN_SECOND * 60)) % 60);
  }

  private static int getSecondsFromMills(final float timeInMills) {
    return (int) (timeInMills / ConstantValues.MILLISECONDS_IN_SECOND) % 60;
  }

  private static float getTimeFromMills(final float timeInMills) {
    float result;
    final int secondsInHour = ConstantValues.SECONDS_IN_HOUR;
    final float seconds = getSecondsFromMills(timeInMills);
    final float minutes = getMinutesFromMills(timeInMills);
    final float hours = getHoursFromMills(timeInMills);
    if (hours == 0) {
      result = (minutes / 60) + (seconds / secondsInHour);
      if (minutes == 0) {
        result = seconds / secondsInHour;
      }
    } else {
      result = hours + (minutes / 60) + (seconds / secondsInHour);
    }
    return result;
  }
}