package com.example.aprokopenko.triphelper.speedometer_gauge;

class RangePointer extends GaugePointer {
    private double offset;

    public RangePointer() {
        this.offset = GaugeConstants.ZERO;
    }

    public double getOffset() {
        return this.offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }
}
