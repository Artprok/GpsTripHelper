package com.example.aprokopenko.triphelper.speedometer_gauge;

class RangePointer extends GaugePointer {
  private double offset;

  public RangePointer() {
    offset = GaugeConstants.ZERO;
  }

  public double getOffset() {
    return offset;
  }

  public void setOffset(final double offset) {
    this.offset = offset;
  }
}