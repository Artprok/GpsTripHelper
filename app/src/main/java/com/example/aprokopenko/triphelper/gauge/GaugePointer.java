package com.example.aprokopenko.triphelper.gauge;

import android.animation.ObjectAnimator;

public class GaugePointer {
    int color;
    private boolean enableAnimation;
    TripHelperGauge mBaseGauge;
    GaugeScale      mGaugeScale;
    PointerRender   mPointerRender;
    double          value;
    double          width;

    GaugePointer() {
        this.value = GaugeConstants.POINTER_INIT_HEIGHT_VALUE;
        this.width = GaugeConstants.POINTER_INIT_WIDTH_VALUE;
        this.color = GaugeConstants.POINTER_INIT_COLOR;
        this.enableAnimation = true;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double newValue) {
        if (this.mPointerRender != null) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(this.mPointerRender, "value", (float) this.value, (float) newValue);
            animator.setDuration(GaugeConstants.GAUGE_ANIMATION_TIME);
            animator.start();
        }
        this.value = newValue;
    }

    public void setPointerValue(float newValue) {
        this.value = (double) newValue;
        if (this.mBaseGauge != null) {
            this.mBaseGauge.refreshGauge();
        }
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
        if (this.mBaseGauge != null) {
            this.mBaseGauge.refreshGauge();
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
        if (this.mBaseGauge != null) {
            this.mBaseGauge.refreshGauge();
        }
    }
}
