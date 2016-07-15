package com.example.aprokopenko.triphelper.speedometerfactory;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.Nullable;

import com.example.aprokopenko.triphelper.speedometer_gauge.GaugePointer;
import com.example.aprokopenko.triphelper.speedometer_gauge.GaugeRange;
import com.example.aprokopenko.triphelper.speedometer_gauge.GaugeScale;
import com.example.aprokopenko.triphelper.speedometer_gauge.Header;
import com.example.aprokopenko.triphelper.speedometer_gauge.NeedlePointer;
import com.example.aprokopenko.triphelper.speedometer_gauge.TickSettings;
import com.example.aprokopenko.triphelper.speedometer_gauge.TripHelperGauge;
import com.example.aprokopenko.triphelper.utils.settings.GaugeFactorySettings;

import java.util.ArrayList;

public class CircularGaugeFactory {
    public CircularGaugeFactory() {
    }

    public TripHelperGauge getConfiguredSpeedometerGauge(Context context, String title) {
        TripHelperGauge speedometer = new TripHelperGauge(context);
        configureSpeedometer(speedometer, title);
        return speedometer;
    }

    private void setScale(TripHelperGauge gauge, GaugeScale scale, ArrayList<GaugeRange> ranges, ArrayList<GaugePointer> pointers,
                          ArrayList<TickSettings> tickSettings) {
        ArrayList<GaugeScale> gaugeScales = new ArrayList<>();
        scale.setMinorTicksPerInterval(GaugeFactorySettings.minorTicksPerInterval);
        scale.setStartValue(GaugeFactorySettings.startValue);
        scale.setStartAngle(GaugeFactorySettings.startAngle);
        scale.setSweepAngle(GaugeFactorySettings.sweepAngle);
        scale.setEndValue(GaugeFactorySettings.endValue);
        scale.setInterval(GaugeFactorySettings.interval);

        scale.setLabelTextSize(GaugeFactorySettings.labelTextSize);
        scale.setLabelColor(GaugeFactorySettings.labelTextColor);
        scale.setLabelOffset(GaugeFactorySettings.labelOffset);

        setupTickSettings(tickSettings, scale);

        scale.setGaugePointers(pointers);
        scale.setGaugeRanges(ranges);

        gaugeScales.add(0, scale);
        gauge.setGaugeScales(gaugeScales);
    }

    private ArrayList<GaugeRange> setupRanges(GaugeScale scale) {
        ArrayList<GaugeRange> gaugeRangeArrayList = new ArrayList<>();
        GaugeRange            gaugeRange1         = new GaugeRange();
        gaugeRange1.setColor(Color.parseColor(GaugeFactorySettings.cityColorString));
        gaugeRange1.setStartValue(GaugeFactorySettings.cityLimitationSpeedFrom);
        gaugeRange1.setEndValue(GaugeFactorySettings.cityLimitationSpeedTo);
        gaugeRange1.setWidth(GaugeFactorySettings.rangeScaleWidth);
        gaugeRange1.setOffset(GaugeFactorySettings.rangeOffset);
        gaugeRangeArrayList.add(0, gaugeRange1);

        scale.setGaugeRanges(gaugeRangeArrayList);
        GaugeRange gaugeRange2 = new GaugeRange();
        gaugeRange2.setColor(Color.parseColor(GaugeFactorySettings.outCityColorString));
        gaugeRange2.setStartValue(GaugeFactorySettings.outOfTownLimitationSpeedFrom);
        gaugeRange2.setEndValue(GaugeFactorySettings.outOfTownLimitationSpeedTo);
        gaugeRange2.setWidth(GaugeFactorySettings.rangeScaleWidth);
        gaugeRange2.setOffset(GaugeFactorySettings.rangeOffset);
        gaugeRangeArrayList.add(1, gaugeRange2);

        scale.setGaugeRanges(gaugeRangeArrayList);
        GaugeRange gaugeRange3 = new GaugeRange();
        gaugeRange3.setColor(Color.parseColor(GaugeFactorySettings.deniedSpeedColorString));
        gaugeRange3.setStartValue(GaugeFactorySettings.deniedSpeedLimitationSpeedFrom);
        gaugeRange3.setEndValue(GaugeFactorySettings.deniedSpeedLimitationSpeedTo);
        gaugeRange3.setWidth(GaugeFactorySettings.rangeScaleWidth);
        gaugeRange3.setOffset(GaugeFactorySettings.rangeOffset);
        gaugeRangeArrayList.add(2, gaugeRange3);

        return gaugeRangeArrayList;
    }

    private ArrayList<GaugePointer> setupPointer() {
        ArrayList<GaugePointer> gaugePointerArrayList = new ArrayList<>();
        NeedlePointer           needlePointer         = new NeedlePointer();
        needlePointer.setKnobColor(Color.parseColor(GaugeFactorySettings.knobNeedleColorString));
        needlePointer.setLengthFactor(GaugeFactorySettings.needleLengthFactor);
        needlePointer.setColor(GaugeFactorySettings.needleColorString);
        needlePointer.setValue(GaugeFactorySettings.pointerStartValue);
        needlePointer.setKnobRadius(GaugeFactorySettings.knobRadius);
        needlePointer.setWidth(GaugeFactorySettings.needleWidth);
        needlePointer.setType(GaugeFactorySettings.needleType);
        gaugePointerArrayList.add(0, needlePointer);
        return gaugePointerArrayList;
    }

    private ArrayList<TickSettings> setupTicks() {
        ArrayList<TickSettings> tickSettingArrayList = new ArrayList<>();
        TickSettings            majorTicksSettings   = new TickSettings();
        majorTicksSettings.setColor(Color.parseColor(GaugeFactorySettings.tickColorString));
        majorTicksSettings.setOffset(GaugeFactorySettings.ticksOffset);
        majorTicksSettings.setSize(GaugeFactorySettings.majorTickSize);
        majorTicksSettings.setWidth(GaugeFactorySettings.ticksWidth);

        TickSettings minorTicksSettings = new TickSettings();
        minorTicksSettings.setColor(Color.parseColor(GaugeFactorySettings.tickColorString));
        minorTicksSettings.setOffset(GaugeFactorySettings.ticksOffset);
        minorTicksSettings.setSize(GaugeFactorySettings.minorTickSize);
        minorTicksSettings.setWidth(GaugeFactorySettings.ticksWidth);

        tickSettingArrayList.add(0, majorTicksSettings);
        tickSettingArrayList.add(1, minorTicksSettings);
        return tickSettingArrayList;
    }


    private void setupTickSettings(ArrayList<TickSettings> ts, GaugeScale scale) {
        TickSettings majorTickSetting = ts.get(0);
        TickSettings minorTickSetting = ts.get(1);

        scale.setMajorTickSettings(majorTickSetting);
        scale.setMinorTickSettings(minorTickSetting);
        scale.setMinorTicksPerInterval(GaugeFactorySettings.minorTicksPerInterval);
    }

    private void configureSpeedometer(TripHelperGauge gauge, String title) {
        GaugeScale gaugeScale = new GaugeScale();
        setHeader(gauge, title);

        ArrayList<GaugeRange>   gaugeRangeArrayList   = setupRanges(gaugeScale);
        ArrayList<GaugePointer> gaugePointerArrayList = setupPointer();
        ArrayList<TickSettings> tickSettingArrayList  = setupTicks();

        setScale(gauge, gaugeScale, gaugeRangeArrayList, gaugePointerArrayList, tickSettingArrayList);
    }

    private void setHeader(TripHelperGauge gauge, @Nullable String title) {
        ArrayList<Header> gaugeHeaders        = new ArrayList<>();
        Header            circularGaugeHeader = new Header();
        if (title == null) {
            circularGaugeHeader.setText(GaugeFactorySettings.speedometerHeaderText);
        }
        else {
            circularGaugeHeader.setText(title);
        }

        circularGaugeHeader.setTextColor(GaugeFactorySettings.textColor);
        circularGaugeHeader.setPosition(new PointF((float) 0.437, (float) 0.70));
        circularGaugeHeader.setTextSize(GaugeFactorySettings.textSize);
        gaugeHeaders.add(0, circularGaugeHeader);
        gauge.setHeaders(gaugeHeaders);
    }
}
