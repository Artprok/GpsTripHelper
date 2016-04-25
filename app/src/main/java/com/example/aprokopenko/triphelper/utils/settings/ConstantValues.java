package com.example.aprokopenko.triphelper.utils.settings;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ConstantValues {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.getDefault());

    public static final String FILE_NAME = "TripData";

    public static final String  TRIP_INFO_FRAGMENT_TAG            = "TRIP_INFO_FRAGMENT";
    public static final String  TRIP_LIST_TAG                     = "TRIP_LIST_FRAGMENT";
    public static final String  MAIN_FRAGMENT_TAG                 = "MAIN_FRAGMENT";
    public static final String  MAP_FRAGMENT_TAG                  = "MAP_FRAGMENT";
    public static final float   SPEEDOMETER_HEIGHT                = 0.99f;
    public static final float   SPEEDOMETER_WIDTH                 = 0.99f;
    public static final float   FUEL_COST                         = 20.0f;
    public static final float   KILOMETER_PER_HOUR_MULTIPLIER     = 3.6f;
    public static final int     TEXT_ANIM_DURATION                = 1700;
    public static final boolean DEBUG_MODE                        = true; //todo change for release to false
    public static final int     START_VALUE                       = 0;
    public static final int     FUEL_TANK_CAPACITY                = 60;
    public static final float   FUEL_CONSUMPTION                  = 11;
    public static final int     HIGHWAY_SPEED_AVG_SPEED           = 70;
    public static final int     LOW_TRAFFIC_AVG_SPEED             = 50;
    public static final int     MEDIUM_TRAFFIC_AVG_SPEED          = 30;
    public static final int     HIGH_TRAFFIC_AVG_SPEED            = 15;
    public static final int     VERY_HIGH_TRAFFIC_AVG_SPEED       = 10;
    public static final float   CONSUMPTION_HIGHWAY_TRAFFIC_ADD   = -2;
    public static final float   CONSUMPTION_LOW_TRAFFIC_ADD       = 0;
    public static final float   CONSUMPTION_MEDIUM_TRAFFIC_ADD    = 1;
    public static final float   CONSUMPTION_HIGH_TRAFFIC_ADD      = 3;
    public static final float   CONSUMPTION_VERY_HIGH_TRAFFIC_ADD = 5;
    public static final int     MIN_UPDATE_DISTANCE               = 0;
    public static final int     MIN_UPDATE_TIME                   = 1;
}
