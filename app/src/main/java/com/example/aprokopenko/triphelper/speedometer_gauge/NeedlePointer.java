package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.support.annotation.NonNull;

import com.example.aprokopenko.triphelper.speedometer_gauge.enums.NeedleType;

public class NeedlePointer extends GaugePointer {
  private int knobColor;
  private double knobRadius;
  private double lengthFactor;
  private NeedleType type;

  public NeedlePointer() {
    knobRadius = GaugeConstants.DEFAULT_KNOB_RADIUS;
    knobColor = GaugeConstants.DEFAULT_KNOB_COLOR;
    lengthFactor = GaugeConstants.LENGHT_FACTOR;
    type = NeedleType.Bar;
  }

  int getKnobColor() {
    return knobColor;
  }

  public void setKnobColor(final int knobColor) {
    this.knobColor = knobColor;
    final TripHelperGauge tripHelperGauge = getmBaseGauge();
    if (tripHelperGauge != null) {
      tripHelperGauge.refreshGauge();
    }
  }

  double getKnobRadius() {
    return knobRadius;
  }

  public void setKnobRadius(final double knobRadius) {
    this.knobRadius = knobRadius;
    final TripHelperGauge tripHelperGauge = getmBaseGauge();
    if (tripHelperGauge != null) {
      tripHelperGauge.refreshGauge();
    }
  }

  NeedleType getType() {
    return type;
  }

  public void setType(@NonNull final NeedleType type) {
    this.type = type;
    final TripHelperGauge tripHelperGauge = getmBaseGauge();
    if (tripHelperGauge != null) {
      tripHelperGauge.refreshGauge();
    }
  }

  double getLengthFactor() {
    return lengthFactor;
  }

  public void setLengthFactor(final double lengthFactor) {
    this.lengthFactor = lengthFactor;
    final TripHelperGauge tripHelperGauge = getmBaseGauge();
    if (tripHelperGauge != null) {
      tripHelperGauge.refreshGauge();
    }
  }
}