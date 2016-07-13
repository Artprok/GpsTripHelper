package com.example.aprokopenko.triphelper.gauge;

import android.graphics.Color;

import com.example.aprokopenko.triphelper.gauge.enums.NeedleType;

public class NeedlePointer extends GaugePointer {
    int        knobColor;
    double     knobRadius;
    double     lengthFactor;
    NeedleType type;

    public NeedlePointer() {
        this.knobColor = Color.parseColor("#FF777777");
        this.knobRadius = 15.0d;
        this.type = NeedleType.Bar;
        this.lengthFactor = 1.0d;
    }

    public int getKnobColor() {
        return this.knobColor;
    }

    public void setKnobColor(int knobColor) {
        this.knobColor = knobColor;
        if (this.mBaseGauge != null) {
            this.mBaseGauge.refreshGauge();
        }
    }

    public double getKnobRadius() {
        return this.knobRadius;
    }

    public void setKnobRadius(double knobRadius) {
        this.knobRadius = knobRadius;
        if (this.mBaseGauge != null) {
            this.mBaseGauge.refreshGauge();
        }
    }

    public NeedleType getType() {
        return this.type;
    }

    public void setType(NeedleType type) {
        this.type = type;
        if (this.mBaseGauge != null) {
            this.mBaseGauge.refreshGauge();
        }
    }

    public double getLengthFactor() {
        return this.lengthFactor;
    }

    public void setLengthFactor(double lengthFactor) {
        this.lengthFactor = lengthFactor;
        if (this.mBaseGauge != null) {
            this.mBaseGauge.refreshGauge();
        }
    }
}