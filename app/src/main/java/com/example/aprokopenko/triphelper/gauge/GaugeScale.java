package com.example.aprokopenko.triphelper.gauge;


import android.graphics.Typeface;

import java.util.ArrayList;

public class GaugeScale {
    double                  LabelOffset;
    ArrayList<GaugePointer> GaugePointers;
    ArrayList<GaugeRange>   gaugeRanges;
    double                  endValue;
    double                  interval;
    int                     labelColor;
    String                  labelPostfix;
    String                  labelPrefix;
    double                  labelTextSize;
    Typeface                labelTextStyle;
    TripHelperGauge         mGauge;
    TickSettings            majorTickSettings;
    TickSettings            minorTickSettings;
    double                  minorTicksPerInterval;
    int                     numberOfDecimalDigits;
    double                  radiusFactor;
    int                     rimColor;
    double                  rimWidth;
    boolean                 showLabels;
    boolean                 showRim;
    boolean                 showTicks;
    double                  startAngle;
    double                  startValue;
    double                  sweepAngle;

    public GaugeScale() {
        this.showRim = true;
        this.showTicks = true;
        this.showLabels = true;
        this.gaugeRanges = new ArrayList<>();
        this.GaugePointers = new ArrayList<>();
        this.majorTickSettings = new TickSettings();
        this.minorTickSettings = new TickSettings();

        this.startValue = GaugeConstants.SCALE_INIT_START_VALUE;
        this.endValue = GaugeConstants.SCALE_INIT_END_VALUE;
        this.startAngle = GaugeConstants.SCALE_INIT_START_ANGLE;
        this.sweepAngle = GaugeConstants.SCALE_INIT_SWEEP_ANGLE;
        this.interval = GaugeConstants.SCALE_INIT_INTERVAL;
        this.labelPrefix = GaugeConstants.SCALE_INIT_PREFIX;
        this.labelPostfix = GaugeConstants.SCALE_INIT_POSTFIX;
        this.rimColor = GaugeConstants.SCALE_INIT_RIM_COLOR;
        this.rimWidth = GaugeConstants.SCALE_INIT_RIM_WIDTH;
        this.labelTextSize = GaugeConstants.SCALE_INIT_LABEL_TEXT_SIZE;
        this.labelTextStyle = GaugeConstants.SCALE_INIT_LABEL_TEXT_STYLE;
        this.labelColor = GaugeConstants.SCALE_INIT_LABEL_COLOR;
        this.minorTicksPerInterval = GaugeConstants.SCALE_INIT_MINOR_TICK_INTERVAL;
        this.radiusFactor = GaugeConstants.SCALE_INIT_RADIUS_FACTOR;
        this.numberOfDecimalDigits = GaugeConstants.SCALE_INIT_NUMBER_OF_DECIMAL_DIGITS;
        this.LabelOffset = GaugeConstants.SCALE_INIT_LABEL_OFFSET;
    }

    public ArrayList<GaugeRange> getGaugeRanges() {
        return this.gaugeRanges;
    }

    public void setGaugeRanges(ArrayList<GaugeRange> gaugeRanges) {
        this.gaugeRanges = gaugeRanges;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public ArrayList<GaugePointer> getGaugePointers() {
        return this.GaugePointers;
    }

    public void setGaugePointers(ArrayList<GaugePointer> GaugePointers) {
        this.GaugePointers = GaugePointers;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getStartValue() {
        return this.startValue;
    }

    public void setStartValue(double startValue) {
        this.startValue = startValue;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getEndValue() {
        return this.endValue;
    }

    public void setEndValue(double endValue) {
        this.endValue = endValue;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getStartAngle() {
        return this.startAngle;
    }

    public void setStartAngle(double startAngle) {
        this.startAngle = startAngle;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getSweepAngle() {
        return this.sweepAngle;
    }

    public void setSweepAngle(double sweepAngle) {
        this.sweepAngle = sweepAngle;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getInterval() {
        return this.interval;
    }

    public void setInterval(double interval) {
        this.interval = interval;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public String getLabelPrefix() {
        return this.labelPrefix;
    }

    public void setLabelPrefix(String labelPrefix) {
        this.labelPrefix = labelPrefix;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public String getLabelPostfix() {
        return this.labelPostfix;
    }

    public void setLabelPostfix(String labelPostfix) {
        this.labelPostfix = labelPostfix;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public int getRimColor() {
        return this.rimColor;
    }

    public void setRimColor(int rimColor) {
        this.rimColor = rimColor;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getRimWidth() {
        return this.rimWidth;
    }

    public void setRimWidth(double rimWidth) {
        this.rimWidth = rimWidth;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getLabelTextSize() {
        return this.labelTextSize;
    }

    public void setLabelTextSize(double labelTextSize) {
        this.labelTextSize = labelTextSize;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public Typeface getLabelTextStyle() {
        return this.labelTextStyle;
    }

    public void setLabelTextStyle(Typeface labelTextStyle) {
        this.labelTextStyle = labelTextStyle;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public int getLabelColor() {
        return this.labelColor;
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getMinorTicksPerInterval() {
        return this.minorTicksPerInterval;
    }

    public void setMinorTicksPerInterval(double minorTicksPerInterval) {
        this.minorTicksPerInterval = minorTicksPerInterval;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public boolean isShowLabels() {
        return this.showLabels;
    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public boolean isShowTicks() {
        return this.showTicks;
    }

    public void setShowTicks(boolean showTicks) {
        this.showTicks = showTicks;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public boolean isShowRim() {
        return this.showRim;
    }

    public void setShowRim(boolean showRim) {
        this.showRim = showRim;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public TickSettings getMajorTickSettings() {
        return this.majorTickSettings;
    }

    public void setMajorTickSettings(TickSettings majorTickSettings) {
        majorTickSettings.mGauge = this.mGauge;
        this.majorTickSettings = majorTickSettings;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public TickSettings getMinorTickSettings() {
        return this.minorTickSettings;
    }

    public void setMinorTickSettings(TickSettings minorTickSettings) {
        minorTickSettings.mGauge = this.mGauge;
        this.minorTickSettings = minorTickSettings;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getRadiusFactor() {
        return this.radiusFactor;
    }

    public void setRadiusFactor(double radiusFactor) {
        this.radiusFactor = radiusFactor;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public int getNumberOfDecimalDigits() {
        return this.numberOfDecimalDigits;
    }

    public void setNumberOfDecimalDigits(int numberOfDecimalDigits) {
        this.numberOfDecimalDigits = numberOfDecimalDigits;
        if (this.mGauge != null) {
            this.mGauge.refreshGauge();
        }
    }

    public double getLabelOffset() {
        return this.LabelOffset;
    }

    public void setLabelOffset(double labelOffset) {
        this.LabelOffset = labelOffset;
    }
}
