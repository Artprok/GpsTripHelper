package com.example.aprokopenko.triphelper.speedometer_factory;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
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

/**
 * Class responsible for creating a {@link TripHelperGauge}.
 */
public class CircularGaugeFactory {
  public CircularGaugeFactory() {
  }

  /**
   * Method for getting configured {@link TripHelperGauge} for portrait layout(by default).
   *
   * @param context {@link Context}
   * @param title   {@link String} title of {@link TripHelperGauge}
   * @return configured for portrait layout {@link TripHelperGauge}
   */
  public TripHelperGauge getConfiguredSpeedometerGauge(@NonNull final Context context, @Nullable final String title) {
    final TripHelperGauge speedometer = new TripHelperGauge(context);
    configureSpeedometer(speedometer, title);
    return speedometer;
  }

  /**
   * Method for getting configured {@link TripHelperGauge} for landscape layout.
   *
   * @param context {@link Context}
   * @param title   {@link String} title of {@link TripHelperGauge}
   * @return configured for landscape layout {@link TripHelperGauge}
   */
  public TripHelperGauge getConfiguredSpeedometerGaugeForLandscape(@NonNull final Context context, @Nullable final String title) {
    final TripHelperGauge speedometer = new TripHelperGauge(context);
    configureSpeedometerForLandscape(speedometer, title);
    return speedometer;
  }

  private void configureSpeedometerForLandscape(@NonNull final TripHelperGauge gauge, @Nullable final String title) {
    final GaugeScale gaugeScale = new GaugeScale();
    final ArrayList<GaugeRange> gaugeRangeArrayList = setupRanges(gaugeScale);
    final ArrayList<GaugePointer> gaugePointerArrayList = setupPointer();
    final ArrayList<TickSettings> tickSettingArrayList = setupTicks();

    setHeader(gauge, title, true);
    setScale(gauge, gaugeScale, gaugeRangeArrayList, gaugePointerArrayList, tickSettingArrayList,
            GaugeFactorySettings.startAngleForLand, GaugeFactorySettings.sweepAngleForLand,
            GaugeFactorySettings.interval, GaugeFactorySettings.labelTextSizeForLand);
  }

  private void configureSpeedometer(@NonNull final TripHelperGauge gauge, @Nullable final String title) {
    final GaugeScale gaugeScale = new GaugeScale();
    final ArrayList<GaugeRange> gaugeRangeArrayList = setupRanges(gaugeScale);
    final ArrayList<GaugePointer> gaugePointerArrayList = setupPointer();
    final ArrayList<TickSettings> tickSettingArrayList = setupTicks();

    setHeader(gauge, title, false);
    setScale(gauge, gaugeScale, gaugeRangeArrayList, gaugePointerArrayList, tickSettingArrayList, GaugeFactorySettings.startAngle,
            GaugeFactorySettings.sweepAngle, GaugeFactorySettings.interval, GaugeFactorySettings.labelTextSize);
  }

  private void setHeader(@NonNull final TripHelperGauge gauge, @Nullable final String title, @NonNull final Boolean isLandscape) {
    final ArrayList<Header> gaugeHeaders = new ArrayList<>();
    final Header circularGaugeHeader = new Header();
    if (title == null) {
      circularGaugeHeader.setText(GaugeFactorySettings.speedometerHeaderText);
    } else {
      circularGaugeHeader.setText(title);
    }

    circularGaugeHeader.setTextColor(GaugeFactorySettings.textColor);
    if (isLandscape) {
      circularGaugeHeader.setPosition(GaugeFactorySettings.headerPositionLandscape);
    } else {
      circularGaugeHeader.setPosition(GaugeFactorySettings.headerPosition);
    }
    circularGaugeHeader.setTextSize(GaugeFactorySettings.textSize);
    gaugeHeaders.add(0, circularGaugeHeader);
    gauge.setHeaders(gaugeHeaders);
  }

  private ArrayList<GaugeRange> setupRanges(@NonNull final GaugeScale scale) {
    final ArrayList<GaugeRange> gaugeRangeArrayList = new ArrayList<>();
    final GaugeRange gaugeRange1 = new GaugeRange();
    gaugeRange1.setColor(Color.parseColor(GaugeFactorySettings.cityColorString));
    gaugeRange1.setStartValue(GaugeFactorySettings.cityLimitationSpeedFrom);
    gaugeRange1.setEndValue(GaugeFactorySettings.cityLimitationSpeedTo);
    gaugeRange1.setWidth(GaugeFactorySettings.rangeScaleWidth);
    gaugeRange1.setOffset(GaugeFactorySettings.rangeOffset);
    gaugeRangeArrayList.add(0, gaugeRange1);

    scale.setGaugeRanges(gaugeRangeArrayList);
    final GaugeRange gaugeRange2 = new GaugeRange();
    gaugeRange2.setColor(Color.parseColor(GaugeFactorySettings.outCityColorString));
    gaugeRange2.setStartValue(GaugeFactorySettings.outOfTownLimitationSpeedFrom);
    gaugeRange2.setEndValue(GaugeFactorySettings.outOfTownLimitationSpeedTo);
    gaugeRange2.setWidth(GaugeFactorySettings.rangeScaleWidth);
    gaugeRange2.setOffset(GaugeFactorySettings.rangeOffset);
    gaugeRangeArrayList.add(1, gaugeRange2);

    scale.setGaugeRanges(gaugeRangeArrayList);
    final GaugeRange gaugeRange3 = new GaugeRange();
    gaugeRange3.setColor(Color.parseColor(GaugeFactorySettings.deniedSpeedColorString));
    gaugeRange3.setStartValue(GaugeFactorySettings.deniedSpeedLimitationSpeedFrom);
    gaugeRange3.setEndValue(GaugeFactorySettings.deniedSpeedLimitationSpeedTo);
    gaugeRange3.setWidth(GaugeFactorySettings.rangeScaleWidth);
    gaugeRange3.setOffset(GaugeFactorySettings.rangeOffset);
    gaugeRangeArrayList.add(2, gaugeRange3);

    return gaugeRangeArrayList;
  }

  private ArrayList<GaugePointer> setupPointer() {
    final ArrayList<GaugePointer> gaugePointerArrayList = new ArrayList<>();
    final NeedlePointer needlePointer = new NeedlePointer();
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
    final ArrayList<TickSettings> tickSettingArrayList = new ArrayList<>();
    final TickSettings majorTicksSettings = new TickSettings();
    majorTicksSettings.setColor(Color.parseColor(GaugeFactorySettings.tickColorString));
    majorTicksSettings.setOffset(GaugeFactorySettings.ticksOffset);
    majorTicksSettings.setSize(GaugeFactorySettings.majorTickSize);
    majorTicksSettings.setWidth(GaugeFactorySettings.ticksWidth);

    final TickSettings minorTicksSettings = new TickSettings();
    minorTicksSettings.setColor(Color.parseColor(GaugeFactorySettings.tickColorString));
    minorTicksSettings.setOffset(GaugeFactorySettings.ticksOffset);
    minorTicksSettings.setSize(GaugeFactorySettings.minorTickSize);
    minorTicksSettings.setWidth(GaugeFactorySettings.ticksWidth);

    tickSettingArrayList.add(0, majorTicksSettings);
    tickSettingArrayList.add(1, minorTicksSettings);
    return tickSettingArrayList;
  }

  private void setScale(@NonNull final TripHelperGauge gauge, @NonNull final GaugeScale scale, @NonNull final ArrayList<GaugeRange> ranges, @NonNull final ArrayList<GaugePointer> pointers,
                        @NonNull final ArrayList<TickSettings> tickSettings, final int startAngle, final int SweepAngle, final int interval, final int labelTextSize) {
    final ArrayList<GaugeScale> gaugeScales = new ArrayList<>();
    scale.setMinorTicksPerInterval(GaugeFactorySettings.minorTicksPerInterval);
    scale.setStartValue(GaugeFactorySettings.startValue);
    scale.setStartAngle(startAngle);
    scale.setSweepAngle(SweepAngle);
    scale.setEndValue(GaugeFactorySettings.endValue);
    scale.setInterval(interval);

    scale.setLabelTextSize(labelTextSize);
    scale.setLabelColor(GaugeFactorySettings.labelTextColor);
    scale.setLabelOffset(GaugeFactorySettings.labelOffset);

    setupTickSettings(tickSettings, scale);

    scale.setGaugePointers(pointers);
    scale.setGaugeRanges(ranges);

    gaugeScales.add(0, scale);
    gauge.setGaugeScales(gaugeScales);
  }

  private void setupTickSettings(@NonNull final ArrayList<TickSettings> ts, @NonNull final GaugeScale scale) {
    final TickSettings majorTickSetting = ts.get(0);
    final TickSettings minorTickSetting = ts.get(1);

    scale.setMajorTickSettings(majorTickSetting);
    scale.setMinorTickSettings(minorTickSetting);
    scale.setMinorTicksPerInterval(GaugeFactorySettings.minorTicksPerInterval);
  }
}