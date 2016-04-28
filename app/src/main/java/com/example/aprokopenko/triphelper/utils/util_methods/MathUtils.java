package com.example.aprokopenko.triphelper.utils.util_methods;

import android.util.Log;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.util.ArrayList;
import java.util.Calendar;

public class MathUtils {
    public static float getSpeedInKilometerPerHour(float speed) {
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

    public static float calcDistTravelled(float timeSpent, float avgSpeed) {
        Float distanceTravelled = avgSpeed * getHoursFromMills(timeSpent);
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
        Log.d("A", "findMaxSpeed: maxSpeed" + maxSpeed);
        return maxSpeed;
    }

    public static String getTimeInNormalFormat(float timeInMills) {
        String resultString;
        int    seconds = (int) (timeInMills / 1000) % 60;
        int    minutes = (int) ((timeInMills / (1000 * 60)) % 60);
        int    hours   = (int) ((timeInMills / (1000 * 60 * 60)) % 24);
        if (hours == 0) {
            resultString = minutes + " minutes," + seconds + " seconds";
        }
        else if (hours == 0 && minutes == 0) {
            resultString = seconds + " seconds";
        }
        else {
            resultString = hours + " hours," + minutes + " minutes," + seconds + " seconds";
        }
        return resultString;
    }

    public static long calcTimeInTrip(long tripStartTime) {
        Calendar curCal  = Calendar.getInstance();
        long     endTime = curCal.getTime().getTime();
        return endTime - tripStartTime;
    }


    private static float getHoursFromMills(float timeInMills) {
        return ((timeInMills / (1000 * 60 * 60)) % 24);
    }
}


