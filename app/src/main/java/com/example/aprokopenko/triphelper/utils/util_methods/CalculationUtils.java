package com.example.aprokopenko.triphelper.utils.util_methods;

import android.support.annotation.Nullable;
import android.content.res.Resources;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.R;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Calendar;

public class CalculationUtils {

    public static float calcMoneySpent(float fuelSpent, float fuelCost) {
        return fuelSpent * fuelCost;
    }

    public static float calcFuelSpent(float distanceTraveled, float fuelConsumption) {
        return distanceTraveled * (fuelConsumption / ConstantValues.PER_100_KM);
    }

    public static String getTimeInNormalFormat(float timeInMills, @Nullable Resources res) {
        String resultString;
        String hoursPrefix;
        String minutePrefix;
        String secondsPrefix;
        if (res == null) {
            hoursPrefix = " h";
            minutePrefix = " m";
            secondsPrefix = " s";
        }
        else {
            hoursPrefix = res.getString(R.string.hourPrefix);
            minutePrefix = res.getString(R.string.minutePref);
            secondsPrefix = res.getString(R.string.secondPref);
        }

        int seconds = getSecondsFromMills(timeInMills);
        int minutes = getMinutesFromMills(timeInMills);
        int hours   = getHoursFromMills(timeInMills);
        if (hours == 0) {
            resultString = minutes + minutePrefix + ", " + seconds + secondsPrefix;
            if (minutes == 0) {
                resultString = seconds + secondsPrefix;
            }
        }
        else {
            resultString = hours + hoursPrefix + ", " + minutes + minutePrefix + ", " + seconds + secondsPrefix;
        }
        return resultString;
    }

    @Contract(pure = true) public static float findMaxSpeed(float speed, float initialVal) {
        float maxSpeed = initialVal;
        if (speed > maxSpeed) {
            maxSpeed = speed;
        }
        return maxSpeed;
    }

    @Contract(pure = true) public static float getSpeedInKilometerPerHour(float speed) {
        return speed * ConstantValues.KILOMETER_PER_HOUR_MULTIPLIER;
    }

    public static float calcAvgSpeedForOneTrip(ArrayList<Float> avgSpeedArrayList) {
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

    public static float setDistanceCoveredForTrip(Trip trip, long timeSpent) {

        float avgSpeed = trip.getAvgSpeed();
        return calcDistTravelled(timeSpent, avgSpeed);
    }

    public static float calcDistTravelled(float timeSpent, float avgSpeed) {
        Float distanceTravelled = avgSpeed * getTimeFromMills(timeSpent);
        if (distanceTravelled.isNaN()) {
            distanceTravelled = (float) ConstantValues.START_VALUE;
        }
        return distanceTravelled;
    }

    public static long calcTimeInTrip(long tripStartTime) {
        Calendar curCal  = Calendar.getInstance();
        long     endTime = curCal.getTime().getTime();
        return endTime - tripStartTime;
    }


    private static int getSecondsFromMills(float timeInMills) {
        return (int) (timeInMills / 1000) % 60;
    }

    private static int getMinutesFromMills(float timeInMills) {
        return (int) ((timeInMills / (1000 * 60)) % 60);
    }

    private static int getHoursFromMills(float timeInMills) {
        return (int) ((timeInMills / (1000 * 60 * 60)) % 24);
    }

    private static float getTimeFromMills(float timeInMills) {
        float result;
        float seconds = getSecondsFromMills(timeInMills);
        float minutes = getMinutesFromMills(timeInMills);
        float hours   = getHoursFromMills(timeInMills);
        if (hours == 0) {
            result = minutes / 100 + seconds / 1000;
            if (minutes == 0) {
                result = seconds / 1000;
            }
        }
        else {
            result = hours + minutes / 100 + seconds / 1000;
        }
        return result;
    }
}


