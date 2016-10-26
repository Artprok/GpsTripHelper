package com.example.aprokopenko.triphelper.utils.settings;

import android.graphics.Color;
import android.graphics.PointF;

import com.example.aprokopenko.triphelper.speedometer_gauge.enums.NeedleType;

public class GaugeFactorySettings {
  public static final int startAngle = 120;
  public static final int sweepAngle = 300;
  public static final int startAngleForLand = 110;
  public static final int sweepAngleForLand = 320;

  public static final int endValue = 200;
  public static final int interval = 10;
  public static final int minorTicksPerInterval = 5;
  public static final int startValue = 0;

  public static final int textColor = ConstantValues.TEXT_COLOR;
  public static final String speedometerHeaderText = "km/h";
  public static final int textSize = 20;

  public static final String deniedSpeedColorString = "#B71C1C";
  public static final String outCityColorString = "#FBC02D";
  public static final String cityColorString = "#4CAF50";
  public static final int rangeScaleWidth = 15;
  public static final int rangeOffset = 0;

  public static final int deniedSpeedLimitationSpeedFrom = 110;
  public static final int deniedSpeedLimitationSpeedTo = 200;
  public static final int outOfTownLimitationSpeedTo = 110;
  public static final int outOfTownLimitationSpeedFrom = 80;
  public static final int cityLimitationSpeedTo = 80;
  public static final int cityLimitationSpeedFrom = 0;
  public static final int pointerStartValue = 0;

  public static final NeedleType needleType = NeedleType.Triangle;
  public static final String knobNeedleColorString = "#000000";
  public static final int needleColorString = Color.RED;
  public static final String tickColorString = "#FFFFFF";
  public static final int labelTextColor = ConstantValues.TEXT_COLOR;
  public static final double ticksOffset = 0.07;
  public static final double labelOffset = 0.17;
  public static final double needleLengthFactor = 0.9;
  public static final int minorTickSize = 10;
  public static final int majorTickSize = 18;
  public static final int labelTextSize = 18;
  public static final int labelTextSizeForLand = 14;
  public static final int needleWidth = 20;
  public static final int knobRadius = 20;
  public static final int ticksWidth = 3;

  public static final PointF headerPosition = new PointF(0.43f, 0.7f);
  public static final PointF headerPositionLandscape = new PointF(0.43f, 0.8f);
  public static int intervalForLandscapeNotPaid = 20;
}