package com.example.aprokopenko.triphelper.gauge;

public class GaugeRange {
    int             color;
    double          endValue;
    TripHelperGauge mGauge;
    double          offset;
    double          startValue;
    double          width;

    public GaugeRange() {
        this.color = -16711681;
        this.startValue = GaugeConstants.ZERO;
        this.endValue = GaugeConstants.ZERO;
        this.width = 7.0d;
        this.offset = 0.4d;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getStartValue() {
        return this.startValue;
    }

    public void setStartValue(double startValue) {
        this.startValue = startValue;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getEndValue() {
        return this.endValue;
    }

    public void setEndValue(double endValue) {
        this.endValue = endValue;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getOffset() {
        return this.offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }
}