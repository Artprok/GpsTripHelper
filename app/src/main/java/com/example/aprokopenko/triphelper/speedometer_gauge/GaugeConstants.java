package com.example.aprokopenko.triphelper.speedometer_gauge;


import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;

/**
 * Class representing a common settings constants for configuration {@link TripHelperGauge}
 */
class GaugeConstants {

  static final double ZERO = 0.0;
  static final int FRAME_BACKGROUND_COLOR = -16777216;
  static final long GAUGE_ANIMATION_TIME = 1500;

  static final double STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY = 0.017453292519943295D;
  public static final double ANGLE_360 = 360.0;

  static final double DEFAULT_KNOB_RADIUS = 15.0;
  static final double LENGTH_FACTOR = 1;
  static final int DEFAULT_KNOB_COLOR = Color.parseColor("#FF777777");

  static final double DEFAULT_HEADER_TEXT_SIZE = 12.0;
  static final int DEFAULT_HEADER_TEXT_COLOR = -1;
  static final PointF DEFAULT_HEADER_POSITION = new PointF(50.0f, 70.0f);
  static final Typeface DEFAULT_HEADER_TEXT_STYLE = Typeface.create("Helvetica", 0);

  static final double POINTER_INIT_WIDTH_VALUE = 3.0;
  static final double POINTER_INIT_HEIGHT_VALUE = 0.0;
  static final int POINTER_INIT_COLOR = Color.parseColor("#FF777777");

  static final int RANGE_INIT_COLOR = -16711681;
  static final double RANGE_INIT_START_VALUE = 0.0;
  static final double RANGE_INIT_END_VALUE = 0.0;
  static final double RANGE_INIT_WIDTH = 7.0;
  static final float RANGE_INIT_OFFSET = 0.4f;

  static final double SCALE_INIT_START_VALUE = 0.0;
  static final double SCALE_INIT_END_VALUE = 100.0;
  static final double SCALE_INIT_START_ANGLE = 130.0;
  static final double SCALE_INIT_SWEEP_ANGLE = 280.0;
  static final double SCALE_INIT_INTERVAL = 10.0;
  static final String SCALE_INIT_PREFIX = "";
  static final String SCALE_INIT_POSTFIX = "";
  static final int SCALE_INIT_RIM_COLOR = Color.parseColor("#FFDCDCE0");
  static final double SCALE_INIT_RIM_WIDTH = 10.0;
  static final double SCALE_INIT_LABEL_TEXT_SIZE = 12.0;
  static final Typeface SCALE_INIT_LABEL_TEXT_STYLE = Typeface.create("Helvetica", 0);
  static final int SCALE_INIT_LABEL_COLOR = Color.parseColor("#FF5B5B5B");
  static final double SCALE_INIT_MINOR_TICK_INTERVAL = 2.0;
  static final double SCALE_INIT_RADIUS_FACTOR = 0.0;
  static final int SCALE_INIT_NUMBER_OF_DECIMAL_DIGITS = -1;
  static final double SCALE_INIT_LABEL_OFFSET = 0.1;

  static final double DEFAULT_TICK_SETTINGS_SIZE = 7.0;
  static final double DEFAULT_TICK_SETTINGS_WIDTH = 3.0;
  static final int DEFAULT_TICK_SETTINGS_COLOR = -1;
  static final double DEFAULT_TICK_SETTINGS_OFFSET = 0.1;
}
