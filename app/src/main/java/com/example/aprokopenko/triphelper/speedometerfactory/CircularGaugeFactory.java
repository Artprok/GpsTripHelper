package com.example.aprokopenko.triphelper.speedometerfactory;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.Nullable;

import com.example.aprokopenko.triphelper.utils.settings.GaugeFactorySettings;
import com.syncfusion.gauges.SfCircularGauge.CircularPointer;
import com.syncfusion.gauges.SfCircularGauge.CircularRange;
import com.syncfusion.gauges.SfCircularGauge.CircularScale;
import com.syncfusion.gauges.SfCircularGauge.Header;
import com.syncfusion.gauges.SfCircularGauge.NeedlePointer;
import com.syncfusion.gauges.SfCircularGauge.SfCircularGauge;
import com.syncfusion.gauges.SfCircularGauge.TickSetting;

import java.util.ArrayList;

public class CircularGaugeFactory {
    public CircularGaugeFactory() {
    }

    public SfCircularGauge getConfiguredSpeedometerGauge(Context context, String title) {
        SfCircularGauge speedometer = new SfCircularGauge(context);
        configureSpeedometer(speedometer, title);
        return speedometer;
    }

    private void setScale(SfCircularGauge gauge, CircularScale scale, ArrayList<CircularRange> ranges, ArrayList<CircularPointer> pointers,
                          ArrayList<TickSetting> tickSettings) {
        ArrayList<CircularScale> circularScales = new ArrayList<>();
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

        scale.setCircularPointers(pointers);
        scale.setCircularRanges(ranges);

        circularScales.add(0, scale);
        gauge.setCircularScales(circularScales);
    }

    private ArrayList<CircularRange> setupRanges(CircularScale scale) {
        ArrayList<CircularRange> circularRangeArrayList = new ArrayList<>();
        CircularRange            circularRange1         = new CircularRange();
        circularRange1.setColor(Color.parseColor(GaugeFactorySettings.cityColorString));
        circularRange1.setStartValue(GaugeFactorySettings.cityLimitationSpeedFrom);
        circularRange1.setEndValue(GaugeFactorySettings.cityLimitationSpeedTo);
        circularRange1.setWidth(GaugeFactorySettings.rangeScaleWidth);
        circularRange1.setOffset(GaugeFactorySettings.rangeOffset);
        circularRangeArrayList.add(0, circularRange1);

        scale.setCircularRanges(circularRangeArrayList);
        CircularRange circularRange2 = new CircularRange();
        circularRange2.setColor(Color.parseColor(GaugeFactorySettings.outCityColorString));
        circularRange2.setStartValue(GaugeFactorySettings.outOfTownLimitationSpeedFrom);
        circularRange2.setEndValue(GaugeFactorySettings.outOfTownLimitationSpeedTo);
        circularRange2.setWidth(GaugeFactorySettings.rangeScaleWidth);
        circularRange2.setOffset(GaugeFactorySettings.rangeOffset);
        circularRangeArrayList.add(1, circularRange2);

        scale.setCircularRanges(circularRangeArrayList);
        CircularRange circularRange3 = new CircularRange();
        circularRange3.setColor(Color.parseColor(GaugeFactorySettings.deniedSpeedColorString));
        circularRange3.setStartValue(GaugeFactorySettings.deniedSpeedLimitationSpeedFrom);
        circularRange3.setEndValue(GaugeFactorySettings.deniedSpeedLimitationSpeedTo);
        circularRange3.setWidth(GaugeFactorySettings.rangeScaleWidth);
        circularRange3.setOffset(GaugeFactorySettings.rangeOffset);
        circularRangeArrayList.add(2, circularRange3);

        return circularRangeArrayList;
    }

    private ArrayList<CircularPointer> setupPointer() {
        ArrayList<CircularPointer> circularPointerArrayList = new ArrayList<>();
        NeedlePointer              needlePointer            = new NeedlePointer();
        needlePointer.setKnobColor(Color.parseColor(GaugeFactorySettings.knobNeedleColorString));
        needlePointer.setLengthFactor(GaugeFactorySettings.needleLengthFactor);
        needlePointer.setColor(GaugeFactorySettings.needleColorString);
        needlePointer.setValue(GaugeFactorySettings.pointerStartValue);
        needlePointer.setKnobRadius(GaugeFactorySettings.knobRadius);
        needlePointer.setWidth(GaugeFactorySettings.needleWidth);
        needlePointer.setType(GaugeFactorySettings.needleType);
        circularPointerArrayList.add(0, needlePointer);
        return circularPointerArrayList;
    }

    private ArrayList<TickSetting> setupTicks() {
        ArrayList<TickSetting> tickSettingArrayList = new ArrayList<>();
        TickSetting            majorTicksSettings   = new TickSetting();
        majorTicksSettings.setColor(Color.parseColor(GaugeFactorySettings.tickColorString));
        majorTicksSettings.setOffset(GaugeFactorySettings.ticksOffset);
        majorTicksSettings.setSize(GaugeFactorySettings.majorTickSize);
        majorTicksSettings.setWidth(GaugeFactorySettings.ticksWidth);

        TickSetting minorTicksSettings = new TickSetting();
        minorTicksSettings.setColor(Color.parseColor(GaugeFactorySettings.tickColorString));
        minorTicksSettings.setOffset(GaugeFactorySettings.ticksOffset);
        minorTicksSettings.setSize(GaugeFactorySettings.minorTickSize);
        minorTicksSettings.setWidth(GaugeFactorySettings.ticksWidth);

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

    private void configureSpeedometer(SfCircularGauge gauge, String title) {
        CircularScale circularScale = new CircularScale();
        setHeader(gauge, title);

        ArrayList<CircularRange>   circularRangeArrayList   = setupRanges(circularScale);
        ArrayList<CircularPointer> circularPointerArrayList = setupPointer();
        ArrayList<TickSetting>     tickSettingArrayList     = setupTicks();

        setScale(gauge, circularScale, circularRangeArrayList, circularPointerArrayList, tickSettingArrayList);
    }

    private void setHeader(SfCircularGauge gauge, @Nullable String title) {
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
