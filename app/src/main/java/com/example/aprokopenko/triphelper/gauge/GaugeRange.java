package com.example.aprokopenko.triphelper.gauge;

public class GaugeRange {
    private TripHelperGauge mGauge;

    private int    color;
    private double endValue;
    private double offset;
    private double startValue;
    private double width;

    public GaugeRange() {
        color = GaugeConstants.RANGE_INIT_COLOR;
        startValue = GaugeConstants.RANGE_INIT_START_VALUE;
        endValue = GaugeConstants.RANGE_INIT_END_VALUE;
        width = GaugeConstants.RANGE_INIT_WIDTH;
        offset = GaugeConstants.RANGE_INIT_OFFSET;
    }

    public void setmGauge(TripHelperGauge mGauge) {
        this.mGauge = mGauge;
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