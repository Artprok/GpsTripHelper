package com.example.aprokopenko.triphelper.speedometer_gauge;

public class TickSettings {
    private TripHelperGauge mGauge;
    private int             color;
    private double          offset;
    private double          size;
    private double          width;

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
        if (mGauge != null) {
            mGauge.refreshGauge();
        }
    }

    public void setmGauge(TripHelperGauge mGauge) {
        this.mGauge = mGauge;
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
        if (mGauge != null) {
            mGauge.refreshGauge();
        }
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
        if (mGauge != null) {
            mGauge.refreshGauge();
        }
    }

    public double getOffset() {
        return this.offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
        if (mGauge != null) {
            mGauge.refreshGauge();
        }
    }
}
