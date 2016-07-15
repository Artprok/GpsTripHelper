package com.example.aprokopenko.triphelper.gauge;

import android.graphics.PointF;
import android.graphics.Typeface;

import com.example.aprokopenko.triphelper.gauge.enums.HeaderAlignment;

public class Header {
    private com.example.aprokopenko.triphelper.gauge.enums.HeaderAlignment HeaderAlignment;

    private TripHelperGauge gauge;
    private PointF          position;
    private String          text;
    private int             textColor;
    private double          textSize;
    private Typeface        textStyle;

    public Header() {
        text = "headerText";
        textSize = GaugeConstants.DEFAULT_HEADER_TEXT_SIZE;
        textStyle = GaugeConstants.DEFAULT_HEADER_TEXT_STYLE;
        textColor = GaugeConstants.DEFAULT_HEADER_TEXT_COLOR;
        position = GaugeConstants.DEFAULT_HEADER_POSITION;
        HeaderAlignment = com.example.aprokopenko.triphelper.gauge.enums.HeaderAlignment.Custom;
    }

    public String getText() {
        return this.text;
    }

    public void setGauge(TripHelperGauge gauge) {
        this.gauge = gauge;
    }

    public void setText(String text) {
        this.text = text;
        if (this.gauge != null) {
            this.gauge.refreshGauge();
        }
    }

    public double getTextSize() {
        return this.textSize;
    }

    public void setTextSize(double textSize) {
        this.textSize = textSize;
        if (this.gauge != null) {
            this.gauge.refreshGauge();
        }
    }

    public Typeface getTextStyle() {
        return this.textStyle;
    }

    public void setTextStyle(Typeface textStyle) {
        this.textStyle = textStyle;
        if (this.gauge != null) {
            this.gauge.refreshGauge();
        }
    }

    public int getTextColor() {
        return this.textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        if (this.gauge != null) {
            this.gauge.refreshGauge();
        }
    }

    public PointF getPosition() {
        return this.position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public HeaderAlignment getHeaderAlignment() {
        return this.HeaderAlignment;
    }

    public void setHeaderAlignment(HeaderAlignment headerAlignment) {
        this.HeaderAlignment = headerAlignment;
    }
}
