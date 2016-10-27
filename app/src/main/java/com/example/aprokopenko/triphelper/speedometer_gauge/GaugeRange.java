package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.support.annotation.NonNull;

/**
 * Class responsible for configuration a Range for {@link TripHelperGauge}.
 */
public class GaugeRange {
  private TripHelperGauge mGauge;

  private int color;
  private double endValue;
  private float offset;
  private double startValue;
  private double width;

  public GaugeRange() {
    color = GaugeConstants.RANGE_INIT_COLOR;
    startValue = GaugeConstants.RANGE_INIT_START_VALUE;
    endValue = GaugeConstants.RANGE_INIT_END_VALUE;
    width = GaugeConstants.RANGE_INIT_WIDTH;
    offset = GaugeConstants.RANGE_INIT_OFFSET;
  }

  public void setmGauge(@NonNull final TripHelperGauge mGauge) {
    this.mGauge = mGauge;
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

  public double getStartValue() {
    return startValue;
  }

  /**
   * Method for set value from {@link GaugeRange} is starting.
   *
   * @param startValue {@link Double} startValue
   */
  public void setStartValue(final double startValue) {
    this.startValue = startValue;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getEndValue() {
    return endValue;
  }

  /**
   * Method for set value that {@link GaugeRange} ends.
   *
   * @param endValue {@link Double} end value
   */
  public void setEndValue(final double endValue) {
    this.endValue = endValue;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
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

  public float getOffset() {
    return offset;
  }

  public void setOffset(final float offset) {
    this.offset = offset;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }
}