package com.example.aprokopenko.triphelper.gauge;

class RangePointer extends GaugePointer {
    protected double offset;

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
