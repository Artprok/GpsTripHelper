package com.example.aprokopenko.triphelper.gauge;

import android.animation.ObjectAnimator;
import android.graphics.Color;

public class GaugePointer {
    int             color;
    private boolean enableAnimation;
    TripHelperGauge mBaseGauge;
    GaugeScale      mGaugeScale;
    PointerRender   mPointerRender;
    double          value;
    double          width;

    GaugePointer() {
        this.value = GaugeConstants.ZERO;
        this.width = 3.0d;
        this.color = Color.parseColor("#FF777777");
        this.enableAnimation = true;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double newValue) {
        if (this.mPointerRender != null) {
            ObjectAnimator animator = ObjectAnimator
                    .ofFloat(this.mPointerRender, "value", (float) this.value, (float) newValue);
            animator.setDuration(1500);
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
