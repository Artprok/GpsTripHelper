package com.example.aprokopenko.triphelper.utils.settings;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ConstantValues {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.getDefault());

    public static final String INTERNAL_SETTING_FILE_NAME = "INTERNAL_DATA";
    public static final String FILE_NAME                  = "TripData";

    public static final LatLng BERMUDA_COORDINATES    = new LatLng(32.30, -64.78);
    public static final String TRIP_INFO_FRAGMENT_TAG = "TRIP_INFO_FRAGMENT";
    public static final String TRIP_LIST_TAG          = "TRIP_LIST_FRAGMENT";
    public static final String SETTINGS_FRAGMENT_TAG  = "SETTINGS_FRAGMENT";
    public static final String MAIN_FRAGMENT_TAG      = "MAIN_FRAGMENT";
    public static final String MAP_FRAGMENT_TAG       = "MAP_FRAGMENT";

    public static final float SPEEDOMETER_HEIGHT = 1f;
    public static final float SPEEDOMETER_WIDTH  = 1f;
    public static final int   PER_100            = 100;

    public static final int   START_VALUE                = 0;
    public static final Float START_VALUE_f              = 0f;
    public static final float FUEL_COST_DEFAULT          = 21.8f;
    public static final int   FUEL_TANK_CAPACITY_DEFAULT = 60;
    public static final float FUEL_CONSUMPTION_DEFAULT   = 11;

    public static final int HIGHWAY_SPEED_AVG_SPEED     = 70;
    public static final int LOW_TRAFFIC_AVG_SPEED       = 50;
    public static final int MEDIUM_TRAFFIC_AVG_SPEED    = 30;
    public static final int HIGH_TRAFFIC_AVG_SPEED      = 15;
    public static final int VERY_HIGH_TRAFFIC_AVG_SPEED = 10;

    public static final float CONSUMPTION_VERY_LOW_ADD          = -3;
    public static final float CONSUMPTION_LOW_TRAFFIC_ADD       = -1;
    public static final float CONSUMPTION_NORMAL_TRAFFIC_ADD    = 0;
    public static final float CONSUMPTION_MEDIUM_TRAFFIC_ADD    = 4;
    public static final float CONSUMPTION_HIGH_TRAFFIC_ADD      = 5;
    public static final float CONSUMPTION_VERY_HIGH_TRAFFIC_ADD = 10;

    public static final int   TEXT_COLOR             = Color.parseColor("#EEEEEE");
    public static final long  MIN_UPDATE_TIME        = 1700;
    public static final float SPEED_VALUE_WORKAROUND = 666;

    public static final int TEXT_ANIM_DURATION             = (int) MIN_UPDATE_TIME;
    public static final int SPEEDOMETER_TEXT_ANIM_DURATION = (int) MIN_UPDATE_TIME;

    public static final float KMH_MULTIPLIER   = 3.6f;
    public static final float MPH_MULTIPLIER   = 2.23f;
    public static final float KNOTS_MULTIPLIER = 1.94f;

    public static final float MILLISECONDS_IN_HOUR   = 3600000;
    public static final int   MILLISECONDS_IN_SECOND = 1000;
    public static final int   HOURS_IN_DAY           = 24;
    public static final int   SECONDS_IN_HOUR        = 3600;

    public static final int CITY_SPEED_LIMIT    = 80;
    public static final int OUTCITY_SPEED_LIMIT = 110;

    public static final int NEGATIVE_VALUE_OF_TRIPINFO_ERROR_CODE = 111;

    public static final int    WIDTH_DELIMETER_FOR_PORTRAIT  = 6;
    public static final int    WIDTH_DELIMETER_FOR_LANDSCAPE = 12;
    public static final String DATA_HOLDER_TAG               = "DATA_HOLDER";
}
