package com.example.aprokopenko.triphelper.utils.util_methods;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Class with different util math methods.
 */
public class CalculationUtils {
  private static float measurementUnitMultiplier = ConstantValues.KMH_MULTIPLIER;   //default val for KM/H

  /**
   * Method for getting speed in appropriate measurement units (kmh,mph,knots).
   *
   * @param speed {@link Float} speed in meter/seconds
   * @return {@link Float} speed in appropriate measurement units
   */
  public static float getSpeedInKilometerPerHour(final float speed) {
    return (speed * measurementUnitMultiplier);
  }

  /**
   * Method for calculating money spent depends on fuel spent and cost.
   *
   * @param fuelSpent {@link Float} fuel spent in litres
   * @param fuelCost  {@link Float} fuel cost in currency unit
   * @return {@link Float} value representing money spent
   */
  public static float calcMoneySpent(final float fuelSpent, final float fuelCost) {
    return fuelSpent * fuelCost;
  }

  /**
   * Method for transform time in milliseconds to {@link String} representing time in hours, minutes and seconds.
   *
   * @param timeInMills {@link Float} time in milliseconds
   * @param res         {@link Resources}
   * @return {@link String} representing time in hours, minutes and seconds
   */
  public static String getTimeInNormalFormat(final float timeInMills, @Nullable final Resources res) {
    final int seconds = getSecondsFromMills(timeInMills);
    final int minutes = getMinutesFromMills(timeInMills);
    final int hours = getHoursFromMills(timeInMills);
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

    String resultString;
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

  /**
   * Method for calculating value of fuel spent.
   *
   * @param distanceTraveled {@link Float} distance travelled
   * @param fuelConsumption  {@link Float} fuel consumption
   * @return {@link Float} value representing fuel spent
   */
  public static float calcFuelSpent(final float distanceTraveled, final float fuelConsumption) {
    return distanceTraveled * (fuelConsumption / ConstantValues.PER_100);
  }

  /**
   * Method for calculating average speed for one {@link Trip}
   *
   * @param avgSpeedArrayList {@link ArrayList<Float>} list of all speed stored during a {@link Trip}
   * @return {@link Float} average speed
   */
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

  /**
   * Method for calculate and set distance covered in {@link Trip}.
   *
   * @param trip      {@link Trip}
   * @param timeSpent {@link Long} time spent in {@link Trip}
   * @return {@link Float} value representing distance covered
   */
  public static float setDistanceCoveredForTrip(@NonNull final Trip trip, final long timeSpent) {
    return calcDistTravelled(timeSpent, trip.getAvgSpeed());
  }

  /**
   * Method for calculate distance travelled in {@link Trip}.
   *
   * @param timeSpent {@link Float} time spent in {@link Trip}
   * @param avgSpeed  {@link Float} average speed
   * @return {@link Float} value representing distance travelled
   */
  public static float calcDistTravelled(final float timeSpent, final float avgSpeed) {
    final Float distanceTravelled = avgSpeed * getTimeFromMills(timeSpent);
    if (distanceTravelled.isNaN()) {
      return (float) ConstantValues.START_VALUE;
    }
    return distanceTravelled;
  }

  /**
   * Method for calculating maximum speed.
   *
   * @param speed      {@link Float} speed
   * @param initialVal {@link Float} value for compare
   * @return {@link Float} value representing maximum speed
   */
  public static float findMaxSpeed(final float speed, final float initialVal) {
    if (speed > initialVal) {
      return speed;
    }
    return initialVal;
  }

  /**
   * Method for calculating time in {@link Trip}.
   *
   * @param tripStartTime {@link Long} trip start time
   * @return {@link Long} time in trip
   */
  public static long calcTimeInTrip(final long tripStartTime) {
    if (tripStartTime <= 0) {
      return 0;
    } else {
      return Calendar.getInstance().getTime().getTime() - tripStartTime;
    }
  }

  /**
   * Method for setting measurement unit multiplier (kmh,mph,knots).
   *
   * @param position {@link Integer} position for choice type of multiplier:
   *                 0 - Kilometer per hour
   *                 1 - Mile per hour
   *                 2 - Knots
   *                 <p>
   *                 default: Kilometers per hour
   */
  public static void setMeasurementMultiplier(final int position) {
    switch (position) {
      case 0:
        measurementUnitMultiplier = ConstantValues.KMH_MULTIPLIER;
        break;
      case 1:
        measurementUnitMultiplier = ConstantValues.MPH_MULTIPLIER;
        break;
      case 2:
        measurementUnitMultiplier = ConstantValues.KNOTS_MULTIPLIER;
        break;
      default:
        measurementUnitMultiplier = ConstantValues.KMH_MULTIPLIER;
    }
  }


  /**
   * Method for getting quantity of hours from milliseconds.
   *
   * @param timeInMills {@link Float} milliseconds
   * @return {@link Integer} quantity of hour
   */
  private static int getHoursFromMills(final float timeInMills) {
    return (int) ((timeInMills / (ConstantValues.MILLISECONDS_IN_SECOND * 60 * 60)) % ConstantValues.HOURS_IN_DAY);
  }

  /**
   * Method for getting quantity of minutes from milliseconds.
   *
   * @param timeInMills {@link Float} milliseconds
   * @return {@link Integer} quantity of minutes
   */
  private static int getMinutesFromMills(final float timeInMills) {
    return (int) ((timeInMills / (ConstantValues.MILLISECONDS_IN_SECOND * 60)) % 60);
  }

  /**
   * Method for getting quantity of Seconds from milliseconds.
   *
   * @param timeInMills {@link Float} milliseconds
   * @return {@link Integer} quantity of Seconds
   */
  private static int getSecondsFromMills(final float timeInMills) {
    return (int) (timeInMills / ConstantValues.MILLISECONDS_IN_SECOND) % 60;
  }

  private static float getTimeFromMills(final float timeInMills) {
    float result;
    final float seconds = getSecondsFromMills(timeInMills);
    final float minutes = getMinutesFromMills(timeInMills);
    if (getHoursFromMills(timeInMills) == 0) {
      result = (minutes / 60) + (seconds / ConstantValues.SECONDS_IN_HOUR);
      if (minutes == 0) {
        result = seconds / ConstantValues.SECONDS_IN_HOUR;
      }
    } else {
      result = getHoursFromMills(timeInMills) + (minutes / 60) + (seconds / ConstantValues.SECONDS_IN_HOUR);
    }
    return result;
  }
}