package com.example.aprokopenko.triphelper.gauge;

public class TickSettings {
    TripHelperGauge mGauge;
    int             color;
    double          offset;
    double          size;
    double          width;

    public TickSettings() {
        this.size = GaugeConstants.DEFAULT_TICK_SETTINGS_SIZE;
        this.width = GaugeConstants.DEFAULT_TICK_SETTINGS_WIDTH;
        this.color = GaugeConstants.DEFAULT_TICK_SETTINGS_COLOR;
        this.offset = GaugeConstants.DEFAULT_TICK_SETTINGS_OFFSET;
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
