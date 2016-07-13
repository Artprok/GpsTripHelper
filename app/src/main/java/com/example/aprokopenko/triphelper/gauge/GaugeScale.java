package com.example.aprokopenko.triphelper.gauge;


import android.graphics.Color;
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
        this.gaugeRanges = new ArrayList<>();
        this.GaugePointers = new ArrayList<>();
        this.startValue = GaugeConstants.ZERO;
        this.endValue = 100.0d;
        this.startAngle = 130.0d;
        this.sweepAngle = 280.0d;
        this.interval = 10.0d;
        this.labelPrefix = "";
        this.labelPostfix = "";
        this.rimColor = Color.parseColor("#FFDCDCE0");
        this.rimWidth = 10.0d;
        this.labelTextSize = 12.0d;
        this.labelTextStyle = Typeface.create("Helvetica", 0);
        this.labelColor = Color.parseColor("#FF5B5B5B");
        this.minorTicksPerInterval = 2.0d;
        this.showLabels = true;
        this.showTicks = true;
        this.showRim = true;
        this.majorTickSettings = new TickSettings();
        this.minorTickSettings = new TickSettings();
        this.radiusFactor = GaugeConstants.ZERO;
        this.numberOfDecimalDigits = -1;
        this.LabelOffset = 0.1d;
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
