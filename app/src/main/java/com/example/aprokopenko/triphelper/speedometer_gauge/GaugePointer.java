package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;

/**
 * Class responsible for configuration a pointer in {@link TripHelperGauge}.
 */
public class GaugePointer {
  private int color;
  private boolean enableAnimation;
  private TripHelperGauge mBaseGauge;
  private GaugeScale mGaugeScale;
  private PointerRender mPointerRender;
  private double value;
  private double width;

  GaugePointer() {
    value = GaugeConstants.POINTER_INIT_HEIGHT_VALUE;
    width = GaugeConstants.POINTER_INIT_WIDTH_VALUE;
    color = GaugeConstants.POINTER_INIT_COLOR;
    enableAnimation = true;
  }

  public double getValue() {
    return value;
  }

  /**
   * Method for setting value for pointer to point.
   *
   * @param newValue {@link Double} new value to point
   */
  public void setValue(final double newValue) {
    if (mPointerRender != null) {
      final ObjectAnimator animator = ObjectAnimator.ofFloat(mPointerRender, "value", (float) value, (float) newValue);
      animator.setDuration(GaugeConstants.GAUGE_ANIMATION_TIME);
      animator.start();
    }
    value = newValue;
  }

  public GaugeScale getmGaugeScale() {
    return mGaugeScale;
  }

  public void setmBaseGauge(@NonNull final TripHelperGauge mBaseGauge) {
    this.mBaseGauge = mBaseGauge;
  }

  public TripHelperGauge getBaseGauge() {
    return mBaseGauge;
  }

  public void setmGaugeScale(@NonNull final GaugeScale mGaugeScale) {
    this.mGaugeScale = mGaugeScale;
  }

  public void setmPointerRender(@NonNull final PointerRender mPointerRender) {
    this.mPointerRender = mPointerRender;
  }

  public void setPointerValue(final float newValue) {
    this.value = (double) newValue;
    if (mBaseGauge != null) {
      mBaseGauge.refreshGauge();
    }
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(final double width) {
    this.width = width;
    if (mBaseGauge != null) {
      mBaseGauge.refreshGauge();
    }
  }

  public int getColor() {
    return this.color;
  }

  public void setColor(final int color) {
    this.color = color;
    if (mBaseGauge != null) {
      mBaseGauge.refreshGauge();
    }
  }

  public boolean isEnableAnimation() {
    return this.enableAnimation;
  }

  public void setEnableAnimation(final boolean enableAnimation) {
    this.enableAnimation = enableAnimation;
    if (mBaseGauge != null) {
      mBaseGauge.refreshGauge();
    }
  }
}