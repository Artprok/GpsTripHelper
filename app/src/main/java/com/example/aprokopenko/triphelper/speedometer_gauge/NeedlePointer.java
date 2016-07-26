package com.example.aprokopenko.triphelper.speedometer_gauge;

import com.example.aprokopenko.triphelper.speedometer_gauge.enums.NeedleType;

public class NeedlePointer extends GaugePointer {
    private int        knobColor;
    private double     knobRadius;
    private double     lengthFactor;
    private NeedleType type;

    public NeedlePointer() {
        knobRadius = GaugeConstants.DEFAULT_KNOB_RADIUS;
        knobColor = GaugeConstants.DEFAULT_KNOB_COLOR;
        lengthFactor = GaugeConstants.LENGHT_FACTOR;
        type = NeedleType.Bar;
    }

    public int getKnobColor() {
        return knobColor;
    }

    public void setKnobColor(int knobColor) {
        this.knobColor = knobColor;
        TripHelperGauge tripHelperGauge = getmBaseGauge();
        if (tripHelperGauge != null) {
            tripHelperGauge.refreshGauge();
        }
    }

    public double getKnobRadius() {
        return knobRadius;
    }

    public void setKnobRadius(double knobRadius) {
        this.knobRadius = knobRadius;
        TripHelperGauge tripHelperGauge = getmBaseGauge();
        if (tripHelperGauge != null) {
            tripHelperGauge.refreshGauge();
        }
    }

    public NeedleType getType() {
        return type;
    }

    public void setType(NeedleType type) {
        this.type = type;
        TripHelperGauge tripHelperGauge = getmBaseGauge();
        if (tripHelperGauge != null) {
            tripHelperGauge.refreshGauge();
        }
    }

    public double getLengthFactor() {
        return lengthFactor;
    }

    public void setLengthFactor(double lengthFactor) {
        this.lengthFactor = lengthFactor;
        TripHelperGauge tripHelperGauge = getmBaseGauge();
        if (tripHelperGauge != null) {
            tripHelperGauge.refreshGauge();
        }
    }
}