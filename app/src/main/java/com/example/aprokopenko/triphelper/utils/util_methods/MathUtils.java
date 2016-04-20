package com.example.aprokopenko.triphelper.utils.util_methods;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.util.ArrayList;

public class MathUtils {
    public static float figureOutAverageSpeed(float avgSpeed, float averageSpeed, ArrayList<Float> avgList) {
        for (Float f : avgList) {
            avgSpeed = f + avgSpeed;
        }
        if (averageSpeed != ConstantValues.START_VALUE) {
            averageSpeed = (averageSpeed + avgSpeed / avgList.size()) / 2;
        }
        else {
            averageSpeed = averageSpeed + avgSpeed / avgList.size();
        }
        return averageSpeed;
    }

    public static float getSpeedInKilometerPerHour(float speed) {
        return speed * ConstantValues.KILOMETER_PER_HOUR_MULTIPLIER;
    }

    public static String getTimeInNormalFormat(float timeInMills) {
        int seconds = (int) (timeInMills / 1000) % 60;
        int minutes = (int) ((timeInMills / (1000 * 60)) % 60);
        int hours   = (int) ((timeInMills / (1000 * 60 * 60)) % 24);
        return hours + " hours:" + minutes + " minutes," + seconds + " seconds";
    }

    private static float getHoursFromMills(float timeInMills) {
        return ((timeInMills / (1000 * 60 * 60)) % 24);
    }

    public static float calcDistTravelled(float timeSpent, float avgSpeed) {
        return avgSpeed * getHoursFromMills(timeSpent);
    }
}


