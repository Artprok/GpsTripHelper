package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.support.annotation.NonNull;

import com.example.aprokopenko.triphelper.speedometer_gauge.enums.NeedleType;

/**
 * Class responsible for configuring NeedleType pointer for {@link TripHelperGauge}.
 */
public class NeedlePointer extends GaugePointer {
  private int knobColor;
  private double knobRadius;
  private double lengthFactor;
  private NeedleType type;

  public NeedlePointer() {
    knobRadius = GaugeConstants.DEFAULT_KNOB_RADIUS;
    knobColor = GaugeConstants.DEFAULT_KNOB_COLOR;
    lengthFactor = GaugeConstants.LENGTH_FACTOR;
    type = NeedleType.Bar;
  }

  int getKnobColor() {
    return knobColor;
  }

  public void setKnobColor(final int knobColor) {
    final TripHelperGauge tripHelperGauge = getBaseGauge();
    this.knobColor = knobColor;
    if (tripHelperGauge != null) {
      tripHelperGauge.refreshGauge();
    }
  }

  double getKnobRadius() {
    return knobRadius;
  }

  public void setKnobRadius(final double knobRadius) {
    final TripHelperGauge tripHelperGauge = getBaseGauge();
    this.knobRadius = knobRadius;
    if (tripHelperGauge != null) {
      tripHelperGauge.refreshGauge();
    }
  }

  NeedleType getType() {
    return type;
  }

  public void setType(@NonNull final NeedleType type) {
    final TripHelperGauge tripHelperGauge = getBaseGauge();
    this.type = type;
    if (tripHelperGauge != null) {
      tripHelperGauge.refreshGauge();
    }
  }

  double getLengthFactor() {
    return lengthFactor;
  }

  public void setLengthFactor(final double lengthFactor) {
    final TripHelperGauge tripHelperGauge = getBaseGauge();
    this.lengthFactor = lengthFactor;
    if (tripHelperGauge != null) {
      tripHelperGauge.refreshGauge();
    }
  }
}