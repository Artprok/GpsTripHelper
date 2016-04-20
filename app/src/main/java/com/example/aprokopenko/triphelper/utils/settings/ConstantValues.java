package com.example.aprokopenko.triphelper.utils.settings;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ConstantValues {
    public static final SimpleDateFormat DATE_FORMAT                   = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.getDefault());
    public static final String           TRIP_INFO_FRAGMENT_TAG        = "TRIP_INFO_FRAGMENT";
    public static final String           TRIP_LIST_TAG                 = "TRIP_LIST_FRAGMENT";
    public static final String           MAIN_FRAGMENT_TAG             = "MAIN_FRAGMENT";
    public static final String           MAP_FRAGMENT_TAG              = "MAP_FRAGMENT";
    public static final String           FILE_NAME                     = "TripData";
    public static final float            SPEEDOMETER_HEIGHT            = 0.99f;
    public static final float            SPEEDOMETER_WIDTH             = 0.99f;
    public static final float            TEST_SPEED_VALUE              = 35.8f;
    public static final float            FUEL_COST                     = 20.0f;
    public static final float            KILOMETER_PER_HOUR_MULTIPLIER = 3.6f;
    public static final int              TEXT_ANIM_DURATION            = 1700;
    public static final boolean          DEBUG_MODE                    = true;
    public static final int              START_VALUE                   = -1;
    public static final int              AVG_SPEED_UPDATE_FREQUENCY    = 2;
    public static final int              FUEL_TANK_CAPACITY            = 60;
    public static final int              FUEL_CONSUMPTION              = 11;
    public static final int              MIN_UPDATE_DISTANCE           = 0;
    public static final int              MIN_UPDATE_TIME               = 1;

}
