package com.example.aprokopenko.triphelper.speedometer_gauge;


import android.graphics.Typeface;
import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Class responsible for configuring a scales in {@link TripHelperGauge}.
 */
public class GaugeScale {
  private double LabelOffset;
  private ArrayList<GaugePointer> GaugePointers;
  private ArrayList<GaugeRange> gaugeRanges;
  private double endValue;
  private double interval;
  private int labelColor;
  private String labelPostfix;
  private String labelPrefix;
  private double labelTextSize;
  private Typeface labelTextStyle;
  private TripHelperGauge mGauge;
  private TickSettings majorTickSettings;
  private TickSettings minorTickSettings;
  private double minorTicksPerInterval;
  private int numberOfDecimalDigits;
  private double radiusFactor;
  private int rimColor;
  private double rimWidth;
  private boolean showLabels;
  private boolean showRim;
  private boolean showTicks;
  private double startAngle;
  private double startValue;
  private double sweepAngle;

  public GaugeScale() {
    showRim = true;
    showTicks = true;
    showLabels = true;
    gaugeRanges = new ArrayList<>();
    GaugePointers = new ArrayList<>();
    majorTickSettings = new TickSettings();
    minorTickSettings = new TickSettings();

    startValue = GaugeConstants.SCALE_INIT_START_VALUE;
    endValue = GaugeConstants.SCALE_INIT_END_VALUE;
    startAngle = GaugeConstants.SCALE_INIT_START_ANGLE;
    sweepAngle = GaugeConstants.SCALE_INIT_SWEEP_ANGLE;
    interval = GaugeConstants.SCALE_INIT_INTERVAL;
    labelPrefix = GaugeConstants.SCALE_INIT_PREFIX;
    labelPostfix = GaugeConstants.SCALE_INIT_POSTFIX;
    rimColor = GaugeConstants.SCALE_INIT_RIM_COLOR;
    rimWidth = GaugeConstants.SCALE_INIT_RIM_WIDTH;
    labelTextSize = GaugeConstants.SCALE_INIT_LABEL_TEXT_SIZE;
    labelTextStyle = GaugeConstants.SCALE_INIT_LABEL_TEXT_STYLE;
    labelColor = GaugeConstants.SCALE_INIT_LABEL_COLOR;
    minorTicksPerInterval = GaugeConstants.SCALE_INIT_MINOR_TICK_INTERVAL;
    radiusFactor = GaugeConstants.SCALE_INIT_RADIUS_FACTOR;
    numberOfDecimalDigits = GaugeConstants.SCALE_INIT_NUMBER_OF_DECIMAL_DIGITS;
    LabelOffset = GaugeConstants.SCALE_INIT_LABEL_OFFSET;
  }

  public TripHelperGauge getmGauge() {
    return mGauge;
  }

  public void setmGauge(@NonNull final TripHelperGauge mGauge) {
    this.mGauge = mGauge;
  }

  public ArrayList<GaugeRange> getGaugeRanges() {
    return gaugeRanges;
  }

  /**
   * Method for set different ranges to one {@link GaugeScale}.
   *
   * @param gaugeRanges {@link ArrayList<GaugeRange>} list of {@link GaugeRange}
   */
  public void setGaugeRanges(@NonNull final ArrayList<GaugeRange> gaugeRanges) {
    this.gaugeRanges = gaugeRanges;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public ArrayList<GaugePointer> getGaugePointers() {
    return GaugePointers;
  }

  public void setGaugePointers(@NonNull final ArrayList<GaugePointer> GaugePointers) {
    this.GaugePointers = GaugePointers;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getStartValue() {
    return startValue;
  }

  public void setStartValue(final double startValue) {
    this.startValue = startValue;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getEndValue() {
    return endValue;
  }

  public void setEndValue(final double endValue) {
    this.endValue = endValue;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getStartAngle() {
    return startAngle;
  }

  public void setStartAngle(final double startAngle) {
    this.startAngle = startAngle;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getSweepAngle() {
    return sweepAngle;
  }

  public void setSweepAngle(final double sweepAngle) {
    this.sweepAngle = sweepAngle;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getInterval() {
    return interval;
  }

  /**
   * Method setting interval between ticks in {@link TripHelperGauge}.
   *
   * @param interval {@link Double} interval
   */
  public void setInterval(double interval) {
    this.interval = interval;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public String getLabelPrefix() {
    return labelPrefix;
  }

  public void setLabelPrefix(String labelPrefix) {
    this.labelPrefix = labelPrefix;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public String getLabelPostfix() {
    return labelPostfix;
  }

  public void setLabelPostfix(String labelPostfix) {
    this.labelPostfix = labelPostfix;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public int getRimColor() {
    return rimColor;
  }

  /**
   * Method for adding color of outer ring of {@link TripHelperGauge}.
   *
   * @param rimColor {@link Integer} color id
   */
  public void setRimColor(int rimColor) {
    this.rimColor = rimColor;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getRimWidth() {
    return rimWidth;
  }

  /**
   * Method for setting width of outer ring of {@link TripHelperGauge}.
   *
   * @param rimWidth {@link Double} width value
   */
  public void setRimWidth(double rimWidth) {
    this.rimWidth = rimWidth;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getLabelTextSize() {
    return labelTextSize;
  }

  /**
   * Method for setting label text size.
   *
   * @param labelTextSize {@link Double} value representing text size
   */
  public void setLabelTextSize(double labelTextSize) {
    this.labelTextSize = labelTextSize;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public Typeface getLabelTextStyle() {
    return labelTextStyle;
  }

  /**
   * Method for setting style for label text.
   *
   * @param labelTextStyle {@link Typeface} style
   */
  public void setLabelTextStyle(Typeface labelTextStyle) {
    this.labelTextStyle = labelTextStyle;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public int getLabelColor() {
    return labelColor;
  }

  public void setLabelColor(int labelColor) {
    this.labelColor = labelColor;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getMinorTicksPerInterval() {
    return minorTicksPerInterval;
  }

  /**
   * Method for set quantity of ticks per interval.
   *
   * @param minorTicksPerInterval {@link Double} value for minor ticks per interval
   */
  public void setMinorTicksPerInterval(double minorTicksPerInterval) {
    this.minorTicksPerInterval = minorTicksPerInterval;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public boolean isShowLabels() {
    return showLabels;
  }

  /**
   * Method for enable showing labels.
   *
   * @param showLabels {@link Boolean} if true - show, else - hide.
   */
  public void setShowLabels(final boolean showLabels) {
    this.showLabels = showLabels;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public boolean isShowTicks() {
    return showTicks;
  }

  /**
   * Method for enable showing ticks.
   *
   * @param showTicks {@link Boolean} if true - show, else - hide.
   */
  public void setShowTicks(final boolean showTicks) {
    this.showTicks = showTicks;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public boolean isShowRim() {
    return showRim;
  }

  /**
   * Method for enable showing outer ring.
   *
   * @param showRim {@link Boolean} if true - show, else - hide.
   */
  public void setShowRim(boolean showRim) {
    this.showRim = showRim;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public TickSettings getMajorTickSettings() {
    return majorTickSettings;
  }

  public void setMajorTickSettings(TickSettings majorTickSettings) {
    majorTickSettings.setmGauge(this.mGauge);
    this.majorTickSettings = majorTickSettings;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public TickSettings getMinorTickSettings() {
    return minorTickSettings;
  }

  public void setMinorTickSettings(@NonNull final TickSettings minorTickSettings) {
    minorTickSettings.setmGauge(this.mGauge);
    this.minorTickSettings = minorTickSettings;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getRadiusFactor() {
    return radiusFactor;
  }

  public void setRadiusFactor(final double radiusFactor) {
    this.radiusFactor = radiusFactor;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public int getNumberOfDecimalDigits() {
    return this.numberOfDecimalDigits;
  }

  public void setNumberOfDecimalDigits(final int numberOfDecimalDigits) {
    this.numberOfDecimalDigits = numberOfDecimalDigits;
    if (mGauge != null) {
      mGauge.refreshGauge();
    }
  }

  public double getLabelOffset() {
    return LabelOffset;
  }

  public void setLabelOffset(final double labelOffset) {
    this.LabelOffset = labelOffset;
  }
}