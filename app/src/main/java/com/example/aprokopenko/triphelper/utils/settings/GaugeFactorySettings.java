package com.example.aprokopenko.triphelper.utils.settings;

import android.graphics.Color;

import com.syncfusion.gauges.SfCircularGauge.enums.NeedleType;

public class GaugeFactorySettings {
    public static final int startAngle            = 135;
    public static final int sweepAngle            = 270;
    public static final int endValue              = 200;
    public static final int interval              = 10;
    public static final int minorTicksPerInterval = 5;
    public static final int startValue            = 0;

    public static final int    textColor             = Color.BLACK;
    public static final String speedometerHeaderText = "km/h";
    public static final int    textSize              = 20;

    public static final String deniedSpeedColorString = "#FF3D00";
    public static final String outCityColorString     = "#FFFF00";
    public static final String cityColorString        = "#00E676";
    public static final int    rangeScaleWidth        = 15;
    public static final int    rangeOffset            = 0;

    public static final int deniedSpeedLimitationSpeedFrom = 110;
    public static final int deniedSpeedLimitationSpeedTo   = 200;
    public static final int outOfTownLimitationSpeedTo     = 110;
    public static final int outOfTownLimitationSpeedFrom   = 80;
    public static final int cityLimitationSpeedTo          = 80;
    public static final int cityLimitationSpeedFrom        = 0;
    public static final int pointerStartValue              = 0;

    public static final NeedleType needleType            = NeedleType.Triangle;
    public static final String     knobNeedleColorString = "#212121";
    public static final int        needleColorString     = Color.BLACK;
    public static final String     tickColorString       = "#212121";
    public static final int        labelTextColor        = Color.BLACK;
    public static final double     ticksOffset           = 0.07;
    public static final double     labelOffset           = 0.15;
    public static final double     needleLengthFactor    = 0.8;
    public static final int        minorTickSize         = 10;
    public static final int        majorTickSize         = 20;
    public static final int        labelTextSize         = 17;
    public static final int        needleWidth           = 15;
    public static final int        knobRadius            = 20;
    public static final int        ticksWidth            = 3;
}
