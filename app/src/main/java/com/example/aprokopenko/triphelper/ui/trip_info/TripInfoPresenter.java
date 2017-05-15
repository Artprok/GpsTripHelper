package com.example.aprokopenko.triphelper.ui.trip_info;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.MapUtilMethods;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class TripInfoPresenter implements TripInfoContract.UserActionListener {
    private static final String LOG_TAG = "TripInfoFragment";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TIME_SPENT_WITHOUT_MOTION = "TimeSpentOnStop";
    private static final String LONGITUDE_ARR = "LongitudeArray";
    private static final String DISTANCE_TRAVELLED = "DistTravelled";
    private static final String LATITUDE_ARR = "LatitudeArray";
    private static final String AVERAGE_FUEL_CONS = "FuelConsumed";
    private static final String MONEY_SPENT = "MoneySpent";
    private static final String SPEED_ARR = "SpeedArray";
    private static final String TIME_SPENT = "TimeSpent";
    private static final String FUEL_SPENT = "FuelSpent";
    private static final String AVERAGE_SPEED = "AvgSpeed";
    private static final String TRIP_DATE = "TripDate";
    private static final String MAX_SPEED = "MaxSpeed";
    private static final String TRIP_ID = "TripId";

    private ArrayList<String> speedValues;
    private ArrayList<Route> routes;

    private String tripDate;

    private float averageFuelConsumption;
    private float timeSpentOnStop;
    private float distTravelled;
    private float moneySpent;
    private float fuelSpent;
    private float timeSpent;
    private float avgSpeed;
    private float maxSpeed;
    private int tripId;
    private TripInfoContract.View view;

    public TripInfoPresenter(TripInfoContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }


    @Override
    public void onCreate(Bundle arguments) {
        final ArrayList<String> longitudeArray = arguments.getStringArrayList(LONGITUDE_ARR);
        final ArrayList<String> latitudeArray = arguments.getStringArrayList(LATITUDE_ARR);
        final ArrayList<String> speedArray = arguments.getStringArrayList(SPEED_ARR);

        timeSpentOnStop = arguments.getFloat(TIME_SPENT_WITHOUT_MOTION);
        averageFuelConsumption = arguments.getFloat(AVERAGE_FUEL_CONS);
        distTravelled = arguments.getFloat(DISTANCE_TRAVELLED);
        avgSpeed = arguments.getFloat(AVERAGE_SPEED);
        moneySpent = arguments.getFloat(MONEY_SPENT);
        timeSpent = arguments.getFloat(TIME_SPENT);
        fuelSpent = arguments.getFloat(FUEL_SPENT);
        tripDate = arguments.getString(TRIP_DATE);
        maxSpeed = arguments.getFloat(MAX_SPEED);
        tripId = arguments.getInt(TRIP_ID);
        speedValues = speedArray;

        routes = MapUtilMethods.unwrapRoute(latitudeArray, longitudeArray, speedArray);
    }

    @Override
    public void onViewCreated() {
        setDataToInfoFragmentFields();
    }

    @Override
    public boolean onDrawMap(GoogleMap googleMap, boolean toDraw) {
        boolean drawMap = false;
        if (toDraw) {
            if (MapUtilMethods.drawPathFromData(routes, googleMap)) {
                drawMap = true;
                routes = null;
            }
        } else {
            drawMap = true;
            routes = null;
        }
        return drawMap;
    }

    @Override
    public boolean onMapReady() {
        return routes != null && routes.size() != 0 && routes.get(0).getSpeed() != ConstantValues.SPEED_VALUE_WORKAROUND;
    }

    private void setDataToInfoFragmentFields() {
        for (final String value : speedValues) {
            if (Float.valueOf(value) == 0) {
                timeSpentOnStop++;
            }
        }

        if (DEBUG) {
            Log.d(LOG_TAG, "setDataToInfoFragmentFields: TIME All+" + timeSpent);
        }
        final float timeInMotion = getInMotionValue(timeSpentOnStop, speedValues, timeSpent);
        view.setTripTimeSpentOnStopText(getWithoutMotionValue(timeInMotion, timeSpent));
        view.setTripTimeSpentInMotionValue(timeInMotion);
        view.setTripTimeSpentValue(timeSpent);
        view.setTripAvgFuelConsumptionValue(averageFuelConsumption);
        view.setTripMoneySpentValue(moneySpent);
        view.setTripDistanceTravelledValuse(distTravelled);
        view.setTripIdValue(String.valueOf(tripId));
        view.setTripFuelSpentValue(fuelSpent);
        view.setTripMaxSpeedValue(maxSpeed);
        view.setTripAvgSpeedValue(avgSpeed);
        view.setTripDateValue(tripDate);
    }

    private static float getWithoutMotionValue(final float timeSpentInMotion, final float timeSpent) {
        return timeSpent - timeSpentInMotion;
    }

    private static float getInMotionValue(final float timeSpentOnStop, @NonNull final ArrayList<String> speedValues, final float timeSpent) {
        final float percentWithoutMotion = timeSpentOnStop * 100 / speedValues.size();
        final float percentInMotion = 100 - percentWithoutMotion;

        return timeSpent / 100 * percentInMotion;
    }
}