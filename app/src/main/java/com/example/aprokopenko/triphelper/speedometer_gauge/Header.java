package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.graphics.PointF;
import android.graphics.Typeface;

import com.example.aprokopenko.triphelper.speedometer_gauge.enums.HeaderAlignment;

public class Header {
    private com.example.aprokopenko.triphelper.speedometer_gauge.enums.HeaderAlignment HeaderAlignment;

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
        HeaderAlignment = com.example.aprokopenko.triphelper.speedometer_gauge.enums.HeaderAlignment.Custom;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
        if (gauge != null) {
            gauge.refreshGauge();
        }
    }

    public void setGauge(TripHelperGauge gauge) {
        this.gauge = gauge;
    }

    public double getTextSize() {
        return textSize;
    }

    public void setTextSize(double textSize) {
        this.textSize = textSize;
        if (gauge != null) {
            gauge.refreshGauge();
        }
    }

    public Typeface getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(Typeface textStyle) {
        this.textStyle = textStyle;
        if (gauge != null) {
            gauge.refreshGauge();
        }
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        if (gauge != null) {
            gauge.refreshGauge();
        }
    }

    public PointF getPosition() {
        return position;
    }

    public void setPosition(PointF position) {
        this.position = position;
    }

    public HeaderAlignment getHeaderAlignment() {
        return HeaderAlignment;
    }

    public void setHeaderAlignment(HeaderAlignment headerAlignment) {
        this.HeaderAlignment = headerAlignment;
    }
}
