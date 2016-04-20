package com.example.aprokopenko.triphelper.speedometerfactory;

import android.graphics.PointF;
import android.content.Context;
import android.graphics.Color;

import com.example.aprokopenko.triphelper.utils.settings.GaugeFactorySettings;
import com.syncfusion.gauges.SfCircularGauge.CircularPointer;
import com.syncfusion.gauges.SfCircularGauge.SfCircularGauge;
import com.syncfusion.gauges.SfCircularGauge.CircularRange;
import com.syncfusion.gauges.SfCircularGauge.CircularScale;
import com.syncfusion.gauges.SfCircularGauge.NeedlePointer;
import com.syncfusion.gauges.SfCircularGauge.TickSetting;
import com.syncfusion.gauges.SfCircularGauge.Header;

import java.util.ArrayList;

public class CircularGaugeFactory {
    public CircularGaugeFactory() {
    }

    public SfCircularGauge getConfiguredSpeedometerGauge(Context context) {
        SfCircularGauge speedometer = new SfCircularGauge(context);
        configureSpeedometer(speedometer);
        return speedometer;
    }


    private void setScale(SfCircularGauge gauge, CircularScale scale, ArrayList<CircularRange> ranges, ArrayList<CircularPointer> pointers,
                          ArrayList<TickSetting> tickSettings) {
        ArrayList<CircularScale> circularScales = new ArrayList<>();
        scale.setStartValue(GaugeFactorySettings.startValue);
        scale.setEndValue(GaugeFactorySettings.endValue);
        scale.setStartAngle(GaugeFactorySettings.startAngle);
        scale.setSweepAngle(GaugeFactorySettings.sweepAngle);
        scale.setMinorTicksPerInterval(GaugeFactorySettings.minorTicksPerInterval);
        scale.setInterval(GaugeFactorySettings.interval);

        scale.setLabelOffset(GaugeFactorySettings.labelOffset);
        scale.setLabelTextSize(GaugeFactorySettings.labelTextSize);
        scale.setLabelColor(GaugeFactorySettings.labelTextColor);

        setupTickSettings(tickSettings, scale);

        scale.setCircularRanges(ranges);
        scale.setCircularPointers(pointers);

        circularScales.add(0, scale);
        gauge.setCircularScales(circularScales);
    }

    private ArrayList<CircularRange> setupRanges(CircularScale scale) {
        ArrayList<CircularRange> circularRangeArrayList = new ArrayList<>();
        CircularRange            circularRange1         = new CircularRange();
        circularRange1.setWidth(GaugeFactorySettings.rangeScaleWidth);
        circularRange1.setColor(Color.parseColor(GaugeFactorySettings.cityColorString));
        circularRange1.setOffset(GaugeFactorySettings.rangeOffset);
        circularRange1.setStartValue(GaugeFactorySettings.cityLimitationSpeedFrom);
        circularRange1.setEndValue(GaugeFactorySettings.cityLimitationSpeedTo);
        circularRangeArrayList.add(0, circularRange1);

        scale.setCircularRanges(circularRangeArrayList);
        CircularRange circularRange2 = new CircularRange();
        circularRange2.setWidth(GaugeFactorySettings.rangeScaleWidth);
        circularRange2.setOffset(GaugeFactorySettings.rangeOffset);
        circularRange2.setColor(Color.parseColor(GaugeFactorySettings.outCityColorString));
        circularRange2.setStartValue(GaugeFactorySettings.outOfTownLimitationSpeedFrom);
        circularRange2.setEndValue(GaugeFactorySettings.outOfTownLimitationSpeedTo);
        circularRangeArrayList.add(1, circularRange2);

        scale.setCircularRanges(circularRangeArrayList);
        CircularRange circularRange3 = new CircularRange();
        circularRange3.setWidth(GaugeFactorySettings.rangeScaleWidth);
        circularRange3.setOffset(GaugeFactorySettings.rangeOffset);
        circularRange3.setColor(Color.parseColor(GaugeFactorySettings.deniedSpeedColorString));
        circularRange3.setStartValue(GaugeFactorySettings.deniedSpeedLimitationSpeedFrom);
        circularRange3.setEndValue(GaugeFactorySettings.deniedSpeedLimitationSpeedTo);
        circularRangeArrayList.add(2, circularRange3);

        return circularRangeArrayList;
    }

    private ArrayList<CircularPointer> setupPointer() {
        ArrayList<CircularPointer> circularPointerArrayList = new ArrayList<>();
        NeedlePointer              needlePointer            = new NeedlePointer();
        needlePointer.setValue(GaugeFactorySettings.pointerStartValue);
        needlePointer.setKnobColor(Color.parseColor(GaugeFactorySettings.knobNeedleColorString));
        needlePointer.setKnobRadius(GaugeFactorySettings.knobRadius);
        needlePointer.setType(GaugeFactorySettings.needleType);
        needlePointer.setLengthFactor(GaugeFactorySettings.needleLengthFactor);
        needlePointer.setWidth(GaugeFactorySettings.needleWidth);
        needlePointer.setColor(GaugeFactorySettings.needleColorString);
        circularPointerArrayList.add(0, needlePointer);
        return circularPointerArrayList;
    }

    private ArrayList<TickSetting> setupTicks() {
        ArrayList<TickSetting> tickSettingArrayList = new ArrayList<>();
        TickSetting            majorTicksSettings   = new TickSetting();
        majorTicksSettings.setColor(Color.parseColor(GaugeFactorySettings.tickColorString));
        majorTicksSettings.setSize(GaugeFactorySettings.majorTickSize);
        majorTicksSettings.setWidth(GaugeFactorySettings.ticksWidth);
        majorTicksSettings.setOffset(GaugeFactorySettings.ticksOffset);

        TickSetting minorTicksSettings = new TickSetting();
        minorTicksSettings.setColor(Color.parseColor(GaugeFactorySettings.tickColorString));
        minorTicksSettings.setSize(GaugeFactorySettings.minorTickSize);
        minorTicksSettings.setWidth(GaugeFactorySettings.ticksWidth);
        minorTicksSettings.setOffset(GaugeFactorySettings.ticksOffset);

        tickSettingArrayList.add(0, majorTicksSettings);
        tickSettingArrayList.add(1, minorTicksSettings);
        return tickSettingArrayList;
    }


    private void setupTickSettings(ArrayList<TickSetting> ts, CircularScale scale) {
        TickSetting majorTickSetting = ts.get(0);
        TickSetting minorTickSetting = ts.get(1);

        scale.setMajorTickSettings(majorTickSetting);
        scale.setMinorTickSettings(minorTickSetting);
        scale.setMinorTicksPerInterval(GaugeFactorySettings.minorTicksPerInterval);
    }

    private void configureSpeedometer(SfCircularGauge gauge) {
        CircularScale circularScale = new CircularScale();
        setHeader(gauge);

        ArrayList<CircularRange>   circularRangeArrayList   = setupRanges(circularScale);
        ArrayList<CircularPointer> circularPointerArrayList = setupPointer();
        ArrayList<TickSetting>     tickSettingArrayList     = setupTicks();

        setScale(gauge, circularScale, circularRangeArrayList, circularPointerArrayList, tickSettingArrayList);
    }

    private void setHeader(SfCircularGauge gauge) {
        ArrayList<Header> gaugeHeaders        = new ArrayList<>();
        Header            circularGaugeHeader = new Header();
        circularGaugeHeader.setText(GaugeFactorySettings.speedometerHeaderText);

        circularGaugeHeader.setTextColor(GaugeFactorySettings.textColor);
        circularGaugeHeader.setPosition(new PointF((float) 0.437, (float) 0.70));
        circularGaugeHeader.setTextSize(GaugeFactorySettings.textSize);
        gaugeHeaders.add(0, circularGaugeHeader);
        gauge.setHeaders(gaugeHeaders);
    }
}
