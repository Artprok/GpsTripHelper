package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.support.annotation.NonNull;

/**
 * Class contains settings about ticks on {@link GaugeScale}.
 */
public class TickSettings {
  private TripHelperGauge mGauge;
  private int color;
  private double offset;
  private double size;
  private double width;

  public TickSettings() {
    size = GaugeConstants.DEFAULT_TICK_SETTINGS_SIZE;
    width = GaugeConstants.DEFAULT_TICK_SETTINGS_WIDTH;
    color = GaugeConstants.DEFAULT_TICK_SETTINGS_COLOR;
    offset = GaugeConstants.DEFAULT_TICK_SETTINGS_OFFSET;
  }

  public double getSize() {
    return size;
  }

  public void setSize(final double size) {
    this.size = size;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public void setmGauge(@NonNull final TripHelperGauge mGauge) {
    this.mGauge = mGauge;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(final double width) {
    this.width = width;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color) {
    this.color = color;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getOffset() {
    return offset;
  }

  public void setOffset(final double offset) {
    this.offset = offset;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }
}