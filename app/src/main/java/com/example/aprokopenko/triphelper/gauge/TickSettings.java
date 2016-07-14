package com.example.aprokopenko.triphelper.gauge;

public class TickSettings {
    protected TripHelperGauge mGauge;
    protected int             color;
    protected  double          offset;
    protected double          size;
    protected double          width;

    public TickSettings() {
        size = GaugeConstants.DEFAULT_TICK_SETTINGS_SIZE;
        width = GaugeConstants.DEFAULT_TICK_SETTINGS_WIDTH;
        color = GaugeConstants.DEFAULT_TICK_SETTINGS_COLOR;
        offset = GaugeConstants.DEFAULT_TICK_SETTINGS_OFFSET;
    }

    public double getSize() {
        return this.size;
    }

    public void setSize(double size) {
        this.size = size;
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

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
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
