package com.example.aprokopenko.triphelper.utils.util_methods;

import android.util.Log;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.util.ArrayList;

public class MathUtils {
    public static float figureOutAverageSpeed(float initialAvgSpeed, float currAvgSpeed, ArrayList<Float> avgList) {
        for (Float avgSpeedItem : avgList) {
            initialAvgSpeed = avgSpeedItem + initialAvgSpeed;
        }
        if (currAvgSpeed != ConstantValues.START_VALUE) {
            currAvgSpeed = (currAvgSpeed + initialAvgSpeed / avgList.size()) / 2;
        }
        else {
            currAvgSpeed = currAvgSpeed + initialAvgSpeed / avgList.size();
        }
        return currAvgSpeed;
    }

    public static float getSpeedInKilometerPerHour(float speed) {
        return speed * ConstantValues.KILOMETER_PER_HOUR_MULTIPLIER;
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

    private static float getHoursFromMills(float timeInMills) {
        return ((timeInMills / (1000 * 60 * 60)) % 24);
    }

    public static float calcDistTravelled(float timeSpent, float avgSpeed) {
        return avgSpeed * getHoursFromMills(timeSpent);
    }
}


