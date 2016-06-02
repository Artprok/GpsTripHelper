package com.example.aprokopenko.triphelper.utils.util_methods;

import android.support.annotation.Nullable;
import android.content.res.Resources;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.R;

import java.util.ArrayList;
import java.util.Calendar;

public class CalculationUtils {
    private static float measurementUnitMultiplier = 3.6f;   //default val for KM/H

    public static float getSpeedInKilometerPerHour(float speed) {
        return (speed * measurementUnitMultiplier);
    }

    public static float calcMoneySpent(float fuelSpent, float fuelCost) {
        return fuelSpent * fuelCost;
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

    public static float calcFuelSpent(float distanceTraveled, float fuelConsumption) {
        return distanceTraveled * (fuelConsumption / ConstantValues.PER_100_KM);
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

    public static float findMaxSpeed(float speed, float initialVal) {
        float maxSpeed = initialVal;
        if (speed > maxSpeed) {
            maxSpeed = speed;
        }
        return maxSpeed;
    }

    public static long calcTimeInTrip(long tripStartTime) {
        Calendar curCal  = Calendar.getInstance();
        long     endTime = curCal.getTime().getTime();
        return endTime - tripStartTime;
    }


    public static float getTimeFromMills(float timeInMills) {
        float result;
        float seconds = getSecondsFromMills(timeInMills);
        float minutes = getMinutesFromMills(timeInMills);
        float hours   = getHoursFromMills(timeInMills);
        if (hours == 0) {
            result = minutes / 60 + seconds / 3600;
            if (minutes == 0) {
                result = seconds / 3600;
            }
        }
        else {
            result = hours + minutes / 60 + seconds / 3600;
        }
        return result;
    }

    public static void setMeasurementMultiplier(int position) {
        float result = 3.6f;
        switch (position) {
            case 0:
                result = 3.6f;
                break;
            case 1:
                result = 2.23f;
                break;
            case 2:
                result = 1.94f;
                break;
        }
        measurementUnitMultiplier = result;
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
}


