package com.example.aprokopenko.triphelper.gauge;

import android.graphics.PointF;
import android.graphics.Typeface;

import com.example.aprokopenko.triphelper.gauge.enums.HeaderAlignment;

public class Header {
    com.example.aprokopenko.triphelper.gauge.enums.HeaderAlignment HeaderAlignment;

    TripHelperGauge gauge;
    PointF          position;
    String          text;
    int             textColor;
    double          textSize;
    Typeface        textStyle;

    public Header() {
        this.text = "headerText";
        this.textSize = GaugeConstants.DEFAULT_HEADER_TEXT_SIZE;
        this.textStyle = GaugeConstants.DEFAULT_HEADER_TEXT_STYLE;
        this.textColor = GaugeConstants.DEFAULT_HEADER_TEXT_COLOR;
        this.position = GaugeConstants.DEFAULT_HEADER_POSITION;
        this.HeaderAlignment = com.example.aprokopenko.triphelper.gauge.enums.HeaderAlignment.Custom;
    }

    public String getText() {
        return this.text;
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
