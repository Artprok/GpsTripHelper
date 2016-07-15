package com.example.aprokopenko.triphelper.gauge;

import android.animation.ObjectAnimator;

public class GaugePointer {
    private int             color;
    private boolean         enableAnimation;
    private TripHelperGauge mBaseGauge;
    private GaugeScale      mGaugeScale;
    private PointerRender   mPointerRender;
    private double          value;
    private double          width;

    GaugePointer() {
        value = GaugeConstants.POINTER_INIT_HEIGHT_VALUE;
        width = GaugeConstants.POINTER_INIT_WIDTH_VALUE;
        color = GaugeConstants.POINTER_INIT_COLOR;
        enableAnimation = true;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double newValue) {
        if (mPointerRender != null) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.mPointerRender, "value", (float) this.value, (float) newValue);
            animator.setDuration(GaugeConstants.GAUGE_ANIMATION_TIME);
            animator.start();
        }
        this.value = newValue;
    }

    public GaugeScale getmGaugeScale() {
        return mGaugeScale;
    }

    public void setmBaseGauge(TripHelperGauge mBaseGauge) {
        this.mBaseGauge = mBaseGauge;
    }

    TripHelperGauge getmBaseGauge() {
        return mBaseGauge;
    }

    public void setmGaugeScale(GaugeScale mGaugeScale) {
        this.mGaugeScale = mGaugeScale;
    }

    public void setmPointerRender(PointerRender mPointerRender) {
        this.mPointerRender = mPointerRender;
    }

    public void setPointerValue(float newValue) {
        this.value = (double) newValue;
        if (this.mBaseGauge != null) {
            this.mBaseGauge.refreshGauge();
        }
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
        if (mBaseGauge != null) {
            mBaseGauge.refreshGauge();
        }
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
        if (this.mBaseGauge != null) {
            this.mBaseGauge.refreshGauge();
        }
    }

    public boolean isEnableAnimation() {
        return this.enableAnimation;
    }

    public void setEnableAnimation(boolean enableAnimation) {
        this.enableAnimation = enableAnimation;
        if (mBaseGauge != null) {
            mBaseGauge.refreshGauge();
        }
    }
}
