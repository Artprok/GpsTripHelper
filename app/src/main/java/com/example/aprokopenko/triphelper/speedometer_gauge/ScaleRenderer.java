package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class responsible for rendering a {@link GaugeScale}.
 */
public class ScaleRenderer extends View {

  private TripHelperGauge gauge;
  private GaugeScale gaugeScale;
  private RectF rectF;
  private Paint paint;
  private Paint rangePaint;
  private final double arcAliasing;

  private float modifMinSizeDivideBy4;
  private float modifMinSizeDivideBy2;
  private float modifMinSizeMultipleBy0dot75;
  private float modifMinSizeMultipleBy0dot875;
  private float modifMinSizeMultipleBy0dot125;
  private float modifMinSizeMultipleBy0dot375;
  private float modifRimWidthDivideBy2;

  private float mCentreX;
  private float mCentreY;
  private RectF mRangeFrame;
  private GaugeType gaugeType;
  private float mInnerBevelWidth;
  private float mMinSize;
  private float mRangePathWidth;

  public ScaleRenderer(@NonNull final Context context, @NonNull final TripHelperGauge gauge, @NonNull final GaugeScale gaugeScale) {
    this(context, null);
    this.gauge = gauge;
    this.gaugeScale = gaugeScale;
    rectF = new RectF();
    paint = new Paint();
    rangePaint = new Paint();
  }

  public GaugeScale getGaugeScale() {
    return gaugeScale;
  }

  public ScaleRenderer(@NonNull final Context context, @Nullable final AttributeSet attrs) {
    super(context, attrs);
    gauge = null;
    gaugeScale = null;
    arcAliasing = 0.7;
  }

  protected void onDraw(@NonNull final Canvas canvas) {
    if (gauge != null && gaugeScale != null) {
      final double startVal = gaugeScale.getStartValue();
      final double rimWidth = gaugeScale.getRimWidth();
      final double endVal = gaugeScale.getEndValue();
      gaugeScale.setmGauge(gauge);

      mCentreX = (float) gauge.getmCentreX();
      mCentreY = (float) gauge.getmCentreY();
      mInnerBevelWidth = (float) gauge.getmInnerBevelWidth();
      mMinSize = (float) gauge.getmMinSize();
      gaugeType = gauge.getGaugeType();
      mRangeFrame = gauge.getmRangeFrame();
      mRangePathWidth = (float) gauge.getmRangePathWidth();

      modifMinSizeDivideBy4 = mMinSize / 4.0f;
      modifMinSizeDivideBy2 = mMinSize / 2.0f;
      modifMinSizeMultipleBy0dot75 = mMinSize * 0.75f;
      modifMinSizeMultipleBy0dot875 = mMinSize * 0.875f;
      modifMinSizeMultipleBy0dot125 = mMinSize * 0.125f;
      modifMinSizeMultipleBy0dot375 = mMinSize * 0.375f;
      modifRimWidthDivideBy2 = (float) (rimWidth / 2.0);


      if (endVal > startVal && startVal < endVal) {
        final double totalTicks = (gaugeScale.getEndValue() - gaugeScale.getStartValue()) / gaugeScale.getInterval();
        rectF = calculateRectF((float) gaugeScale.getRadiusFactor());
        paint.setAntiAlias(true);
        paint.setColor(0);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) rimWidth);
        paint.setColor(gaugeScale.getRimColor());
        if (gaugeScale.isShowRim()) {
          canvas.drawArc(rectF, (float) gaugeScale.getStartAngle(), (float) gaugeScale.getSweepAngle(), false, paint);
        }

        paint.setStrokeWidth(1.0F);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(gaugeScale.getLabelTextStyle());
        paint = getLabels(paint, gaugeScale.getLabelColor(), (float) gaugeScale.getLabelTextSize());
        onDrawRanges(canvas, gaugeScale);
        if (gaugeScale.isShowLabels()) {
          onDrawLabels(canvas, paint, totalTicks, gaugeScale);
        }
        paint = getTicks(paint, gaugeScale.getMajorTickSettings().getColor(), gaugeScale);
        if (gaugeScale.isShowTicks()) {
          onDrawTicks(canvas, paint, totalTicks, gaugeScale);
        }
      }
    }
  }

  private static Paint getLabels(@NonNull final Paint paint, final int labelColor, final float labelSize) {
    paint.setColor(labelColor);
    paint.setTextSize(labelSize * TripHelperGauge.DENSITY);
    return paint;
  }

  private static Paint getTicks(@NonNull final Paint paint, final int majorTickColor, @NonNull final GaugeScale gaugeScale) {
    paint.setColor(majorTickColor);
    paint.setStrokeWidth((float) gaugeScale.getMajorTickSettings().getWidth());
    return paint;
  }

  private void onDrawLabels(@NonNull final Canvas canvas, @NonNull final Paint paint, final double totalTicks, @NonNull final GaugeScale gaugeScale) {
    final int fractionalDigit = gaugeScale.getNumberOfDecimalDigits();
    final String postfix = gaugeScale.getLabelPostfix();
    final String prefix = gaugeScale.getLabelPrefix();
    double angle = gaugeScale.getStartAngle() * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
    double tempGaugeSize = (mInnerBevelWidth - 10.0) / 2.0;
    tempGaugeSize *= 1.0 - gaugeScale.getRadiusFactor();
    String label;

    double startValue = gaugeScale.getStartValue();

    for (int j = 0; (double) j <= totalTicks; ++j) {
      if (fractionalDigit < 0) {
        if (((int) startValue) == startValue && ((int) gaugeScale.getInterval()) == gaugeScale.getInterval()) {
          label = prefix + String.valueOf((int) startValue) + postfix;
        } else {
          label = prefix + String.valueOf(startValue) + postfix;
        }
      } else {
        label = prefix + String.format("%." + String.valueOf(fractionalDigit) + "f", startValue) + postfix;
      }

      paint.setAntiAlias(true);
      final double widthOfLabel = paint.measureText(String.valueOf((int) gaugeScale.getEndValue()));
      //modif optimization
      final double modifWidthOfLabelDivideBy4 = widthOfLabel / 4.0;
      final double modifWidthOfLabelDivideBy1dot8 = widthOfLabel / 1.8;
      final double labelOffset = gaugeScale.getLabelOffset();
      final double outerSize = tempGaugeSize - labelOffset * mCentreX - widthOfLabel / 2.0 - modifRimWidthDivideBy2;
      final double tempLabelSize = mMinSize / 2.0 - tempGaugeSize + labelOffset * mCentreX +
              modifRimWidthDivideBy2;
      final double modifOuterSizeMultipleBy1dot5 = outerSize * 1.5;
      final double modifOuterSizeMultipleCos = outerSize * Math.cos(angle);
      final double modifOuterSizeMultipleSin = outerSize * Math.sin(angle);
      final double x;
      final double y;

      if (mCentreY > mCentreX) {
        if (gaugeType == GaugeType.West) {
          x = mCentreX - modifMinSizeDivideBy4 + outerSize + modifOuterSizeMultipleCos + tempLabelSize +
                  modifWidthOfLabelDivideBy4;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.SouthWest) {
          if (gaugeType == GaugeType.East) {
            x = mCentreX - modifMinSizeMultipleBy0dot75 + outerSize + modifOuterSizeMultipleCos + tempLabelSize +
                    modifWidthOfLabelDivideBy4;
          } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.SouthEast) {
            x = outerSize + outerSize * Math.cos(angle) + tempLabelSize + modifWidthOfLabelDivideBy4;
          } else {
            x = mCentreX - modifMinSizeMultipleBy0dot875 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                    .cos(angle) + tempLabelSize + modifWidthOfLabelDivideBy4;
          }
        } else {
          x = mCentreX - modifMinSizeMultipleBy0dot125 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                  .cos(angle) + tempLabelSize + modifWidthOfLabelDivideBy4;
        }

        if (gaugeType == GaugeType.North) {
          y = mCentreY - modifMinSizeDivideBy4 + outerSize + outerSize * Math
                  .sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.NorthEast) {
          if (gaugeType == GaugeType.South) {
            y = mCentreY - modifMinSizeMultipleBy0dot75 + outerSize + modifOuterSizeMultipleSin + tempLabelSize +
                    modifWidthOfLabelDivideBy1dot8;
          } else if (gaugeType != GaugeType.SouthEast && gaugeType != GaugeType.SouthWest) {
            y = mCentreY - modifMinSizeDivideBy2 + outerSize + modifOuterSizeMultipleSin + tempLabelSize +
                    modifWidthOfLabelDivideBy1dot8;
          } else {
            y = mCentreY - modifMinSizeMultipleBy0dot875 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                    .sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
          }
        } else {
          y = mCentreY - modifMinSizeMultipleBy0dot125 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                  .sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
        }
      } else {
        if (gaugeType == GaugeType.West) {
          x = mCentreX - modifMinSizeDivideBy4 + outerSize + modifOuterSizeMultipleCos + tempLabelSize +
                  modifWidthOfLabelDivideBy4;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.SouthWest) {
          if (gaugeType == GaugeType.East) {
            x = mCentreX - modifMinSizeMultipleBy0dot75 + outerSize + modifOuterSizeMultipleCos + tempLabelSize +
                    modifWidthOfLabelDivideBy4;
          } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.SouthEast) {
            x = mCentreX - modifMinSizeDivideBy2 + outerSize + modifOuterSizeMultipleCos + tempLabelSize +
                    modifWidthOfLabelDivideBy4;
          } else {
            x = mCentreX - modifMinSizeMultipleBy0dot875 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                    .cos(angle) + tempLabelSize + modifWidthOfLabelDivideBy4;
          }
        } else {
          x = mCentreX - modifMinSizeMultipleBy0dot125 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                  .cos(angle) + tempLabelSize + modifWidthOfLabelDivideBy4;
        }

        if (gaugeType == GaugeType.North) {
          y = mCentreY - modifMinSizeDivideBy4 + outerSize + modifOuterSizeMultipleSin + tempLabelSize +
                  modifWidthOfLabelDivideBy1dot8;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.NorthEast) {
          if (gaugeType == GaugeType.South) {
            y = mCentreY - modifMinSizeMultipleBy0dot75 + outerSize + modifOuterSizeMultipleSin + tempLabelSize +
                    modifWidthOfLabelDivideBy1dot8;
          } else if (gaugeType != GaugeType.SouthEast && gaugeType != GaugeType.SouthWest) {
            y = outerSize + outerSize * Math.sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
          } else {
            y = mCentreY - modifMinSizeMultipleBy0dot875 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                    .sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
          }
        } else {
          y = mCentreY - modifMinSizeMultipleBy0dot125 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                  .sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
        }
      }

      canvas.drawText(label, (float) x, (float) y, paint);
      angle += (gaugeScale.getSweepAngle() / totalTicks * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY);
      startValue += gaugeScale.getInterval();
    }
  }

  private void onDrawTicks(@NonNull final Canvas canvas, @NonNull final Paint paint, final double totalTicks, @NonNull final GaugeScale gaugeScale) {
    final TickSettings majorSettings = gaugeScale.getMajorTickSettings();
    final double size = majorSettings.getSize();
    final double angularSpace = gaugeScale.getSweepAngle() / totalTicks * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
    double angle = gaugeScale.getStartAngle() * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
    double tempGaugeSize = (mInnerBevelWidth - 10.0) / 2.0;
    tempGaugeSize *= 1.0 - gaugeScale.getRadiusFactor();

    double minorTickPosition;
    double outerSize;
    double x2;
    double y2;
    double innerSize;

    //modif optimization
    final double modifTickOffsetMultByCenterX = majorSettings.getOffset() * mCentreX;
    final double modifArcAliasMultByRimWidth = 0.7D * gaugeScale.getRimWidth();
    final double modif1dot5MultipByCosAngle = 1.5D * Math.cos(angle);
    final double modif1dot5MultipBySinAngle = 1.5D * Math.sin(angle);
    double tickLength1 = tempGaugeSize - modifTickOffsetMultByCenterX + modifArcAliasMultByRimWidth - modifRimWidthDivideBy2;

    paint.setAntiAlias(true);
    paint.setStrokeWidth((float) majorSettings.getWidth());
    paint.setStyle(Paint.Style.STROKE);
    paint.setColor(majorSettings.getColor());

    for (int minorTicksCount = 0; minorTicksCount <= totalTicks; ++minorTicksCount) {
      //modif optimization
      final double modifTickLengthMultByCosAngle = tickLength1 * Math.cos(angle);
      final double modifTickMultBy1dot5andMultByCosAngle = tickLength1 * modif1dot5MultipByCosAngle;
      double tickLength = modifMinSizeDivideBy2 - tempGaugeSize + modifTickOffsetMultByCenterX - modifArcAliasMultByRimWidth +
              modifRimWidthDivideBy2;

      if (mCentreY > mCentreX) {
        if (gaugeType == GaugeType.West) {
          minorTickPosition = mCentreX - modifMinSizeDivideBy4 + tickLength1 + modifTickLengthMultByCosAngle + tickLength;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.SouthWest) {
          if (gaugeType == GaugeType.East) {
            minorTickPosition = mCentreX - modifMinSizeMultipleBy0dot75 + tickLength1 + modifTickLengthMultByCosAngle +
                    tickLength;
          } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.SouthEast) {
            minorTickPosition = tickLength1 + modifTickLengthMultByCosAngle + tickLength;
          } else {
            minorTickPosition = mCentreX - modifMinSizeMultipleBy0dot875 + tickLength1 +
                    modifTickMultBy1dot5andMultByCosAngle + tickLength;
          }
        } else {
          minorTickPosition = mCentreX - modifMinSizeMultipleBy0dot125 + tickLength1 +
                  modifTickMultBy1dot5andMultByCosAngle +
                  tickLength;
        }
        if (gaugeType == GaugeType.North) {
          outerSize = mCentreY - modifMinSizeDivideBy4 + tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.NorthEast) {
          if (gaugeType == GaugeType.South) {
            outerSize = mCentreY - modifMinSizeMultipleBy0dot75 + tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
          } else if (gaugeType != GaugeType.SouthEast && gaugeType != GaugeType.SouthWest) {
            outerSize = mCentreY - modifMinSizeDivideBy2 + tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
          } else {
            outerSize = mCentreY - modifMinSizeMultipleBy0dot875 + tickLength1 + tickLength1 * modif1dot5MultipBySinAngle +
                    tickLength;
          }
        } else {
          outerSize = mCentreY - modifMinSizeMultipleBy0dot125 + tickLength1 + tickLength1 * modif1dot5MultipBySinAngle +
                  tickLength;
        }
      } else {
        if (gaugeType == GaugeType.West) {
          minorTickPosition = mCentreX - modifMinSizeDivideBy4 + tickLength1 + modifTickLengthMultByCosAngle + tickLength;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.SouthWest) {
          if (gaugeType == GaugeType.East) {
            minorTickPosition = mCentreX - modifMinSizeMultipleBy0dot75 + tickLength1 + modifTickLengthMultByCosAngle +
                    tickLength;
          } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.SouthEast) {
            minorTickPosition = mCentreX - modifMinSizeDivideBy2 + tickLength1 + modifTickLengthMultByCosAngle +
                    tickLength;
          } else {
            minorTickPosition = mCentreX - modifMinSizeMultipleBy0dot875 + tickLength1 +
                    modifTickMultBy1dot5andMultByCosAngle + tickLength;
          }
        } else {
          minorTickPosition = mCentreX - modifMinSizeMultipleBy0dot125 + tickLength1 +
                  modifTickMultBy1dot5andMultByCosAngle +
                  tickLength;
        }
        if (gaugeType == GaugeType.North) {
          outerSize = mCentreY - modifMinSizeDivideBy4 + tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.NorthEast) {
          if (gaugeType == GaugeType.South) {
            outerSize = mCentreY - modifMinSizeMultipleBy0dot75 + tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
          } else if (gaugeType != GaugeType.SouthEast && gaugeType != GaugeType.SouthWest) {
            outerSize = tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
          } else {
            outerSize = mCentreY - modifMinSizeMultipleBy0dot875 + tickLength1 + tickLength1 * modif1dot5MultipBySinAngle +
                    tickLength;
          }
        } else {
          outerSize = mCentreY - modifMinSizeMultipleBy0dot125 + tickLength1 + tickLength1 * modif1dot5MultipBySinAngle +
                  tickLength;
        }
      }
      x2 = tempGaugeSize - size - modifTickOffsetMultByCenterX + modifArcAliasMultByRimWidth - modifRimWidthDivideBy2;
      tickLength = modifMinSizeDivideBy2 - tempGaugeSize + size + modifTickOffsetMultByCenterX - modifArcAliasMultByRimWidth +
              modifRimWidthDivideBy2;

      final double modif_x2MultByCosAngle = x2 * Math.cos(angle);
      final double modif_x2MultiplBySinAngle = x2 * Math.sin(angle);
      final double modif_x2multiplByX2CosAngle = x2 * modif1dot5MultipByCosAngle;
      final double modif_x2multiplByX2SinAngle = x2 * modif1dot5MultipBySinAngle;

      if (mCentreY > mCentreX) {
        if (gaugeType == GaugeType.West) {
          y2 = mCentreX - modifMinSizeDivideBy4 + x2 + modif_x2MultByCosAngle + tickLength;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.SouthWest) {
          if (gaugeType == GaugeType.East) {
            y2 = mCentreX - modifMinSizeMultipleBy0dot75 + x2 + modif_x2MultByCosAngle + tickLength;
          } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.SouthEast) {
            y2 = x2 + modif_x2MultByCosAngle + tickLength;
          } else {
            y2 = mCentreX - modifMinSizeMultipleBy0dot875 + x2 + modif_x2multiplByX2CosAngle + tickLength;
          }
        } else {
          y2 = mCentreX - modifMinSizeMultipleBy0dot125 + x2 + modif_x2multiplByX2CosAngle + tickLength;
        }

        if (gaugeType == GaugeType.North) {
          innerSize = mCentreY - modifMinSizeDivideBy4 + x2 + modif_x2MultiplBySinAngle + tickLength;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.NorthEast) {
          if (gaugeType == GaugeType.South) {
            innerSize = mCentreY - modifMinSizeMultipleBy0dot75 + x2 + modif_x2MultiplBySinAngle + tickLength;
          } else if (gaugeType != GaugeType.SouthEast && gaugeType != GaugeType.SouthWest) {
            innerSize = mCentreY - modifMinSizeDivideBy2 + x2 + modif_x2MultiplBySinAngle + tickLength;
          } else {
            innerSize = mCentreY - modifMinSizeMultipleBy0dot875 + x2 + modif_x2multiplByX2SinAngle + tickLength;
          }
        } else {
          innerSize = mCentreY - modifMinSizeMultipleBy0dot125 + x2 + modif_x2multiplByX2SinAngle + tickLength;
        }
      } else {
        if (gaugeType == GaugeType.West) {
          y2 = mCentreX - modifMinSizeDivideBy4 + x2 + modif_x2MultByCosAngle + tickLength;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.SouthWest) {
          if (gaugeType == GaugeType.East) {
            y2 = mCentreX - modifMinSizeMultipleBy0dot75 + x2 + modif_x2MultByCosAngle + tickLength;
          } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.SouthEast) {
            y2 = mCentreX - modifMinSizeDivideBy2 + x2 + modif_x2MultByCosAngle + tickLength;
          } else {
            y2 = mCentreX - modifMinSizeMultipleBy0dot875 + x2 + modif_x2multiplByX2CosAngle + tickLength;
          }
        } else {
          y2 = mCentreX - modifMinSizeMultipleBy0dot125 + x2 + modif_x2multiplByX2CosAngle + tickLength;
        }

        if (gaugeType == GaugeType.North) {
          innerSize = mCentreY - modifMinSizeDivideBy4 + x2 + modif_x2MultiplBySinAngle + tickLength;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.NorthEast) {
          if (gaugeType == GaugeType.South) {
            innerSize = mCentreY - modifMinSizeMultipleBy0dot75 + x2 + modif_x2MultiplBySinAngle + tickLength;
          } else if (gaugeType != GaugeType.SouthEast && gaugeType != GaugeType.SouthWest) {
            innerSize = x2 + modif_x2MultiplBySinAngle + tickLength;
          } else {
            innerSize = mCentreY - modifMinSizeMultipleBy0dot875 + x2 + modif_x2multiplByX2SinAngle + tickLength;
          }
        } else {
          innerSize = mCentreY - modifMinSizeMultipleBy0dot125 + x2 + modif_x2multiplByX2SinAngle + tickLength;
        }
      }

      canvas.drawLine((float) y2, (float) innerSize, (float) minorTickPosition, (float) outerSize, paint);
      angle += angularSpace;
    }

    angle = gaugeScale.getStartAngle() * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
    final double minorTickPerInterval = gaugeScale.getMinorTicksPerInterval();
    final double minorTickAngle = angularSpace / (minorTickPerInterval + 1.0D);

    final TickSettings minorSettings = gaugeScale.getMinorTickSettings();
    final double minorSize = minorSettings.getSize();

    paint.setStrokeWidth((float) minorSettings.getWidth());
    paint.setAntiAlias(true);
    paint.setStyle(Paint.Style.STROKE);
    paint.setColor(minorSettings.getColor());

    //modif optimization
    final double modif_arcAlisaMultByWidth = arcAliasing * gaugeScale.getRimWidth();
    final double modif_minorOffsetMultByCenterX = minorSettings.getOffset() * mCentreX;
    minorTickPosition = GaugeConstants.ZERO;

    for (int i = 1; i <= totalTicks * minorTickPerInterval; ++i) {
      angle += minorTickAngle;

      outerSize = tempGaugeSize - modif_minorOffsetMultByCenterX + modif_arcAlisaMultByWidth -
              minorTickPosition - modifRimWidthDivideBy2;
      tickLength1 = modifMinSizeDivideBy2 - tempGaugeSize + modif_minorOffsetMultByCenterX - modif_arcAlisaMultByWidth +
              minorTickPosition + modifRimWidthDivideBy2;

      //modif optimization
      final double modif_outerSizeMultByCosAngle = outerSize * Math.cos(angle);
      final double modif_outerSizeMultBySinAngle = outerSize * Math.sin(angle);
      final double modif_outerSizeMultBy1dot5Cos = outerSize * modif1dot5MultipByCosAngle;
      final double modif_outerSizeMultBy1dot5Sin = outerSize * modif1dot5MultipBySinAngle;

      if (mCentreY > mCentreX) {
        if (gaugeType == GaugeType.West) {
          x2 = mCentreX - modifMinSizeDivideBy4 + outerSize + modif_outerSizeMultByCosAngle + tickLength1;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.SouthWest) {
          if (gaugeType == GaugeType.East) {
            x2 = mCentreX - modifMinSizeMultipleBy0dot75 + outerSize + modif_outerSizeMultByCosAngle + tickLength1;
          } else if (gaugeType != GaugeType.SouthEast && gaugeType != GaugeType.NorthEast) {
            x2 = outerSize + modif_outerSizeMultByCosAngle + tickLength1;
          } else {
            x2 = mCentreX - modifMinSizeMultipleBy0dot875 + outerSize + modif_outerSizeMultBy1dot5Cos + tickLength1;
          }
        } else {
          x2 = mCentreX - modifMinSizeMultipleBy0dot125 + outerSize + modif_outerSizeMultBy1dot5Cos + tickLength1;
        }

        if (gaugeType == GaugeType.North) {
          y2 = mCentreY - modifMinSizeDivideBy4 + outerSize + modif_outerSizeMultBySinAngle + tickLength1;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.NorthEast) {
          if (gaugeType == GaugeType.South) {
            y2 = mCentreY - modifMinSizeMultipleBy0dot75 + outerSize + modif_outerSizeMultBySinAngle + tickLength1;
          } else if (gaugeType != GaugeType.SouthWest && gaugeType != GaugeType.SouthEast) {
            y2 = mCentreY - modifMinSizeDivideBy2 + outerSize + modif_outerSizeMultBySinAngle + tickLength1;
          } else {
            y2 = mCentreY - modifMinSizeMultipleBy0dot875 + outerSize + modif_outerSizeMultBy1dot5Sin + tickLength1;
          }
        } else {
          y2 = mCentreY - modifMinSizeMultipleBy0dot125 + outerSize + modif_outerSizeMultBy1dot5Sin + tickLength1;
        }
      } else {
        if (gaugeType == GaugeType.West) {
          x2 = mCentreX - modifMinSizeDivideBy4 + outerSize + modif_outerSizeMultByCosAngle + tickLength1;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.SouthWest) {
          if (gaugeType == GaugeType.East) {
            x2 = mCentreX - modifMinSizeMultipleBy0dot75 + outerSize + modif_outerSizeMultByCosAngle + tickLength1;
          } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.SouthEast) {
            x2 = mCentreX - modifMinSizeDivideBy2 + outerSize + modif_outerSizeMultByCosAngle + tickLength1;
          } else {
            x2 = mCentreX - modifMinSizeMultipleBy0dot875 + outerSize + modif_outerSizeMultBy1dot5Cos + tickLength1;
          }
        } else {
          x2 = mCentreX - modifMinSizeMultipleBy0dot125 + outerSize + modif_outerSizeMultBy1dot5Cos + tickLength1;
        }

        if (gaugeType == GaugeType.North) {
          y2 = mCentreY - modifMinSizeDivideBy4 + outerSize + modif_outerSizeMultBySinAngle + tickLength1;
        } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.NorthWest) {
          if (gaugeType == GaugeType.South) {
            y2 = mCentreY - modifMinSizeMultipleBy0dot75 + outerSize + modif_outerSizeMultBySinAngle + tickLength1;
          } else if (gaugeType != GaugeType.SouthWest && gaugeType != GaugeType.SouthEast) {
            y2 = outerSize + modif_outerSizeMultBySinAngle + tickLength1;
          } else {
            y2 = mCentreY - modifMinSizeMultipleBy0dot875 + outerSize + modif_outerSizeMultBy1dot5Sin + tickLength1;
          }
        } else {
          y2 = mCentreY - modifMinSizeMultipleBy0dot125 + outerSize + modif_outerSizeMultBy1dot5Sin + tickLength1;
        }
      }

      innerSize = tempGaugeSize - minorSize - modif_minorOffsetMultByCenterX + modif_arcAlisaMultByWidth -
              minorTickPosition - modifRimWidthDivideBy2;
      tickLength1 = modifMinSizeDivideBy2 - tempGaugeSize + minorSize + modif_minorOffsetMultByCenterX - modif_arcAlisaMultByWidth
              + minorTickPosition +
              modifRimWidthDivideBy2;
      final double x1;
      final double y1;

      //modif optimization
      final double modif_innerSizeMultByCos = innerSize * Math.cos(angle);
      final double modif_innerSizeMultBySin = innerSize * Math.sin(angle);
      final double modif_innerSizeMultBy1dot5CosAngle = innerSize * modif1dot5MultipByCosAngle;
      final double modif_innerSizeMultBy1dot5SinAngle = innerSize * modif1dot5MultipBySinAngle;

      if (mCentreY > mCentreX) {
        if (gaugeType == GaugeType.West) {
          x1 = mCentreX - modifMinSizeDivideBy4 + innerSize + modif_innerSizeMultByCos + tickLength1;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.SouthWest) {
          if (gaugeType == GaugeType.East) {
            x1 = mCentreX - modifMinSizeMultipleBy0dot75 + innerSize + modif_innerSizeMultByCos + tickLength1;
          } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.SouthEast) {
            x1 = innerSize + modif_innerSizeMultByCos + tickLength1;
          } else {
            x1 = mCentreX - modifMinSizeMultipleBy0dot875 + innerSize + modif_innerSizeMultBy1dot5CosAngle + tickLength1;
          }
        } else {
          x1 = mCentreX - modifMinSizeMultipleBy0dot125 + innerSize + modif_innerSizeMultBy1dot5CosAngle + tickLength1;
        }
        if (gaugeType == GaugeType.North) {
          y1 = mCentreY - modifMinSizeDivideBy4 + innerSize + modif_innerSizeMultBySin + tickLength1;
        } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.NorthWest) {
          if (gaugeType == GaugeType.South) {
            y1 = mCentreY - modifMinSizeMultipleBy0dot75 + innerSize + modif_innerSizeMultBySin + tickLength1;
          } else if (gaugeType != GaugeType.SouthWest && gaugeType != GaugeType.SouthEast) {
            y1 = mCentreY - modifMinSizeDivideBy2 + innerSize + modif_innerSizeMultBySin + tickLength1;
          } else {
            y1 = mCentreY - modifMinSizeMultipleBy0dot875 + innerSize + modif_innerSizeMultBy1dot5SinAngle + tickLength1;
          }
        } else {
          y1 = mCentreY - modifMinSizeMultipleBy0dot125 + innerSize + modif_innerSizeMultBy1dot5SinAngle + tickLength1;
        }
      } else {
        if (gaugeType == GaugeType.West) {
          x1 = mCentreX - modifMinSizeDivideBy4 + innerSize + modif_innerSizeMultByCos + tickLength1;
        } else if (gaugeType != GaugeType.NorthWest && gaugeType != GaugeType.SouthWest) {
          if (gaugeType == GaugeType.East) {
            x1 = mCentreX - modifMinSizeMultipleBy0dot75 + innerSize + modif_innerSizeMultByCos + tickLength1;
          } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.SouthEast) {
            x1 = mCentreX - modifMinSizeDivideBy2 + innerSize + modif_innerSizeMultByCos + tickLength1;
          } else {
            x1 = mCentreX - modifMinSizeMultipleBy0dot875 + innerSize + modif_innerSizeMultBy1dot5CosAngle + tickLength1;
          }
        } else {
          x1 = mCentreX - modifMinSizeMultipleBy0dot125 + innerSize + modif_innerSizeMultBy1dot5CosAngle + tickLength1;
        }
        if (gaugeType == GaugeType.North) {
          y1 = mCentreY - modifMinSizeDivideBy4 + innerSize + modif_innerSizeMultBySin + tickLength1;
        } else if (gaugeType != GaugeType.NorthEast && gaugeType != GaugeType.NorthWest) {
          if (gaugeType == GaugeType.South) {
            y1 = mCentreY - modifMinSizeMultipleBy0dot75 + innerSize + modif_innerSizeMultBySin + tickLength1;
          } else if (gaugeType != GaugeType.SouthWest && gaugeType != GaugeType.SouthEast) {
            y1 = innerSize + modif_innerSizeMultBySin + tickLength1;
          } else {
            y1 = mCentreY - modifMinSizeMultipleBy0dot875 + innerSize + modif_innerSizeMultBy1dot5SinAngle + tickLength1;
          }
        } else {
          y1 = mCentreY - modifMinSizeMultipleBy0dot125 + innerSize + modif_innerSizeMultBy1dot5SinAngle + tickLength1;
        }
      }
      canvas.drawLine((float) x1, (float) y1, (float) x2, (float) y2, paint);
      if ((double) i % gaugeScale.getMinorTicksPerInterval() == GaugeConstants.ZERO) {
        angle += minorTickAngle;
      }
    }
  }

  private void onDrawRanges(@NonNull final Canvas canvas, @NonNull final GaugeScale gaugeScale) {
    double startArc;
    double endtArc;
    final ArrayList<GaugeRange> gaugeRanges = gaugeScale.getGaugeRanges();
    if (gaugeRanges != null) {
      rangePaint.setAntiAlias(true);
      rangePaint.setStyle(Paint.Style.STROKE);

      for (final Iterator i$ = gaugeRanges.iterator(); i$.hasNext(); canvas
              .drawArc(rectF, (float) startArc, (float) endtArc, false, rangePaint)) {
        float rimWidth = (float) gauge.getmRimWidth();
        final GaugeRange gaugeRange = (GaugeRange) i$.next();
        gaugeRange.setmGauge(gaugeScale.getmGauge());
        rangePaint.setStrokeWidth((float) gaugeRange.getWidth());
        rangePaint.setColor(gaugeRange.getColor());

        final double startVal = gaugeRange.getStartValue();

        startArc = gaugeScale.getStartAngle() + getRangeAngle(startVal, gaugeScale) - getRangeAngle(gaugeScale.getStartValue(),
                gaugeScale);
        endtArc = getRangeAngle(gaugeRange.getEndValue(), gaugeScale) - getRangeAngle(startVal, gaugeScale);

        final float radFactor = (float) gaugeScale.getRadiusFactor();
        final float rimSize;
        gauge.calculateMargin(mInnerBevelWidth - mInnerBevelWidth * radFactor);

        if (radFactor > GaugeConstants.ZERO) {
          gauge.calculateMargin(mMinSize - mMinSize * radFactor);
          rimSize = (float) ((mRangePathWidth - rimWidth) - 4.0D * 10.0D);

          final float modif_centerXplusMinSizeMinusRimSize = getCenterXplusDivideBy2minusRimSize(rimSize);
          final float modif_centerYplusMinSizeMinusRimSize = getCenterYplusDivideBy2minusRimSize(rimSize);

          if (mCentreY > mCentreX) {
            Log.d("HERE1", "onDrawRanges: ");
            rectF = getRectF(rimSize, modif_centerXplusMinSizeMinusRimSize, modif_centerYplusMinSizeMinusRimSize,
                    getCenterYminusDivideBy2plusRimSize(rimSize));
          } else {
            rectF = getRectF(getCenterXminusDivideBy2plusRimSize(rimSize), modif_centerXplusMinSizeMinusRimSize,
                    modif_centerYplusMinSizeMinusRimSize, rimSize);
          }

          mRangeFrame = rectF;
          float factor = mRangeFrame.left + (mRangeFrame.width() / 2.0F - mRangeFrame.left) * radFactor + (gaugeRange
                  .getOffset() * (mCentreX - rimWidth));
          if (mCentreY > mCentreX) {
            rectF = new RectF(factor, getCenterYminusDivideBy2plusRimSize(factor), getCenterXplusDivideBy2minusRimSize(factor),
                    getCenterYplusDivideBy2minusRimSize(factor));
          } else {
            factor = mRangeFrame.top + (mRangeFrame.height() / 2.0F - mRangeFrame.top) * radFactor + (gaugeRange
                    .getOffset() * (mCentreY - rimWidth));
            rectF = new RectF(getCenterXminusDivideBy2plusRimSize(factor), factor, getCenterXplusDivideBy2minusRimSize(factor),
                    getCenterYplusDivideBy2minusRimSize(factor));
          }
        } else {
          if (mCentreY > mCentreX) {
            final double rimW = gauge.getmRimWidth();
            rimSize = (float) (mMinSize - rimW - ((gaugeRange.getOffset() * (mCentreX - rimW))) + gaugeRange.getWidth() / 2.0);

            //modif optimization
            final float modify_centerXplus0dot125minusRimSize = getCenterXplus0dot125minusRimSize(rimSize);
            final float modify_centerYminus0dot125plusRimSize = getCenterYminus0dot125plusRimSize(rimSize);
            final float modify_centerYplus0dot375minusRimSize = getCenterYplus0dot375minusRimSize(rimSize);
            final float modify_centerXminus0dot375plusRimSize = getCenterXminus0dot375plusRimSize(rimSize);
            final float modife_centerYplus0dot125minusRimSize = getCenterYplus0dot125minusRimSize(rimSize);
            final float modif_centerYminusMinSizePlusRimSize = getCenterYminusDivideBy2plusRimSize(rimSize);
            final float modif_centerXplusMinSizeMinusRimSize = getCenterXplusDivideBy2minusRimSize(rimSize);
            final float modif_centerYplusMinSizeMinusRimSize = getCenterYplusDivideBy2minusRimSize(rimSize);
            final float modif_centerXminus0dot125plusRimSize = getCenterXminus0dot125plusRimSize(rimSize);
            final float modif_centerXminus0dot375plusRimSize = getCenterYminus0dot375plusRimSize(rimSize);
            final float modif_centerYplus0dot75minusRimSize = getCenterYplus0dot75minusRimSize(rimSize);

            if (gaugeType == GaugeType.North) {
              rectF = getRectF(rimSize, modif_centerXplusMinSizeMinusRimSize, modif_centerYplus0dot75minusRimSize,
                      getCenterYminusDivideBy4plusRimSize(rimSize));
            } else if (gaugeType == GaugeType.South) {
              rectF = getRectF(rimSize, modif_centerXplusMinSizeMinusRimSize, getCenterYplusDivideBy4minusRimSize(rimSize),
                      getCenterYminus0dot75plusRimSize(rimSize));
            } else if (gaugeType == GaugeType.West) {
              rectF = getRectF(getCenterXminusDivideBy4plusRimSize(rimSize), modif_centerYplus0dot75minusRimSize,
                      modif_centerYplusMinSizeMinusRimSize, modif_centerYminusMinSizePlusRimSize);
            } else if (gaugeType == GaugeType.East) {
              rectF = getRectF(getCenterXminus0dot75plusRimSize(rimSize), getCenterXplusDivideBy4minusRimSize(rimSize),
                      modif_centerYplusMinSizeMinusRimSize, modif_centerYminusMinSizePlusRimSize);
            } else if (gaugeType == GaugeType.NorthEast) {
              rectF = getRectF(modif_centerXminus0dot125plusRimSize, modif_centerXminus0dot125plusRimSize,
                      modife_centerYplus0dot125minusRimSize, modif_centerXminus0dot375plusRimSize);
            } else if (gaugeType == GaugeType.NorthWest) {
              rectF = getRectF(modify_centerXminus0dot375plusRimSize, modify_centerXplus0dot125minusRimSize,
                      modife_centerYplus0dot125minusRimSize, modif_centerXminus0dot375plusRimSize);
            } else if (gaugeType == GaugeType.SouthEast) {
              rectF = getRectF(modif_centerXminus0dot125plusRimSize, modif_centerXminus0dot125plusRimSize,
                      modify_centerYplus0dot375minusRimSize, modify_centerYminus0dot125plusRimSize);
            } else if (gaugeType == GaugeType.SouthWest) {
              rectF = getRectF(modify_centerXminus0dot375plusRimSize, modify_centerXplus0dot125minusRimSize,
                      modify_centerYplus0dot375minusRimSize, modify_centerYminus0dot125plusRimSize);
            } else {
              rectF = getRectF(rimSize, modif_centerXplusMinSizeMinusRimSize, modif_centerYplusMinSizeMinusRimSize,
                      modif_centerYminusMinSizePlusRimSize);
            }
          } else {
            final double rimW = gauge.getmRimWidth();
            rimSize = (float) (mMinSize - rimW - (gaugeRange.getOffset() * (mCentreY - rimW)) + gaugeRange.getWidth() / 2.0);
            //modif optimization
            final float modif_centerXplus0dot125minusRimSize = getCenterXplus0dot125minusRimSize(rimSize);
            final float modif_centerXminus0dot125plusRimSize = getCenterXminus0dot125plusRimSize(rimSize);
            final float modif_centerYplus0dot125minusRimSize = getCenterYplus0dot125minusRimSize(rimSize);
            final float modif_centerYminus0dot125plusRimSize = getCenterYminus0dot125plusRimSize(rimSize);
            final float modif_centerYplus0dot375minusRimSize = getCenterYplus0dot375minusRimSize(rimSize);
            final float modif_centerYminus0dot375plusRimSize = getCenterYminus0dot375plusRimSize(rimSize);
            final float modif_centerYplus0dot75minusRimSize = getCenterYplus0dot75minusRimSize(rimSize);
            final float modif_centerXminusDivideBy2plusRimSize = getCenterXminusDivideBy2plusRimSize(rimSize);
            final float modif_centerXplusMinSizeMinusRimSize = getCenterXplusDivideBy2minusRimSize(rimSize);
            final float modif_centerYplusMinSizeMinusRimSize = getCenterYplusDivideBy2minusRimSize(rimSize);

            if (gaugeType == GaugeType.West) {
              rectF = getRectF(getCenterXminusDivideBy4plusRimSize(rimSize), modif_centerYplus0dot75minusRimSize,
                      modif_centerYplusMinSizeMinusRimSize, rimSize);
            } else if (gaugeType == GaugeType.East) {
              rectF = getRectF(getCenterXminus0dot75plusRimSize(rimSize), getCenterXplusDivideBy4minusRimSize(rimSize),
                      modif_centerYplusMinSizeMinusRimSize, rimSize);
            } else if (gaugeType == GaugeType.North) {
              rectF = getRectF(modif_centerXminusDivideBy2plusRimSize, modif_centerXplusMinSizeMinusRimSize,
                      modif_centerYplus0dot75minusRimSize, getCenterYminusDivideBy4plusRimSize(rimSize));
            } else if (gaugeType == GaugeType.South) {
              rectF = getRectF(modif_centerXminusDivideBy2plusRimSize, modif_centerXplusMinSizeMinusRimSize,
                      getCenterYplusDivideBy4minusRimSize(rimSize), getCenterYminus0dot75plusRimSize(rimSize));
            } else if (gaugeType == GaugeType.NorthEast) {
              rectF = getRectF(modif_centerXminus0dot125plusRimSize, modif_centerXminus0dot125plusRimSize,
                      modif_centerYplus0dot125minusRimSize, modif_centerYminus0dot375plusRimSize);
            } else if (gaugeType == GaugeType.NorthWest) {
              rectF = getRectF(modif_centerYminus0dot375plusRimSize, modif_centerXplus0dot125minusRimSize,
                      modif_centerYplus0dot125minusRimSize, modif_centerYminus0dot375plusRimSize);
            } else if (gaugeType == GaugeType.SouthEast) {
              rectF = getRectF(modif_centerXminus0dot125plusRimSize, modif_centerXminus0dot125plusRimSize,
                      modif_centerYplus0dot375minusRimSize, modif_centerYminus0dot125plusRimSize);
            } else if (gaugeType == GaugeType.SouthWest) {
              rectF = getRectF(modif_centerYminus0dot375plusRimSize, modif_centerXplus0dot125minusRimSize,
                      modif_centerYplus0dot375minusRimSize, modif_centerYminus0dot125plusRimSize);
            } else {
              rectF = getRectF(modif_centerXminusDivideBy2plusRimSize, modif_centerXplusMinSizeMinusRimSize,
                      modif_centerYplusMinSizeMinusRimSize, rimSize);
            }
          }
          mRangeFrame = rectF;
        }
      }
    }
  }

  private static double getRangeAngle(double rangeValue, @NonNull final GaugeScale gaugeScale) {
    final double startVal = gaugeScale.getStartValue();
    if (rangeValue < startVal) {
      rangeValue = startVal;
    }

    final double endVal = gaugeScale.getEndValue();
    if (rangeValue > endVal) {
      rangeValue = endVal;
    }

    final double anglePartition = gaugeScale.getSweepAngle() / (gaugeScale.getEndValue() - startVal);
    return anglePartition * rangeValue;
  }

  private RectF calculateRectF(final float radiusFactor) {
    float rimSize = (float) (mMinSize - (mInnerBevelWidth - 10.0) + gaugeScale.getRimWidth() / 2.0);

    //modif optimization
    float modif_centerXplusDivideBy2minusRimSize = getCenterXplusDivideBy2minusRimSize(rimSize);
    float modif_centerYplusDivideBy2minusRimSize = getCenterYplusDivideBy2minusRimSize(rimSize);
    float modif_centerYminusDivideBy2plusRimSize = getCenterYminusDivideBy2plusRimSize(rimSize);
    final float modif_centerXminusDivideBy2plusRimSize = getCenterXminusDivideBy2plusRimSize(rimSize);

    if (radiusFactor > GaugeConstants.ZERO) {
      gauge.calculateMargin(modifMinSizeDivideBy2 - mCentreX * radiusFactor);
      mMinSize = (float) (mRangePathWidth - gauge.getmRimWidth());
      rimSize = mMinSize - 4.0f * 10.0f;

      if (mCentreY > mCentreX) {
        rectF = getRectF(rimSize, modif_centerXplusDivideBy2minusRimSize, modif_centerYplusDivideBy2minusRimSize,
                modif_centerYminusDivideBy2plusRimSize);
      } else {
        rectF = getRectF(modif_centerXminusDivideBy2plusRimSize, modif_centerXplusDivideBy2minusRimSize,
                modif_centerYplusDivideBy2minusRimSize, rimSize);
      }

      mRangeFrame = rectF;
      float factor = mRangeFrame.left + (mRangeFrame.width() / 2.0F - mRangeFrame.left) * radiusFactor;
      if (mCentreY > mCentreX) {
        rectF = new RectF(factor, getCenterYminusDivideBy2plusRimSize(factor), getCenterXplusDivideBy2minusRimSize(factor),
                getCenterYplusDivideBy2minusRimSize(factor));
      } else {
        factor = mRangeFrame.top + (mRangeFrame.height() / 2.0F - mRangeFrame.top) * radiusFactor;
        rectF = new RectF(getCenterXminusDivideBy2plusRimSize(factor), factor, getCenterXplusDivideBy2minusRimSize(factor),
                getCenterYplusDivideBy2minusRimSize(factor));
      }
    } else {
      //modif optimization
      final float modif_centerYplus0dot75minusRimSize = getCenterYplus0dot75minusRimSize(rimSize);
      final float modif_centerYminusDivideBy4plusRimSize = getCenterYminusDivideBy4plusRimSize(rimSize);
      final float modif_centerYminus0dot75plusRimSize = getCenterYminus0dot75plusRimSize(rimSize);
      final float modif_centerYplusDivideBy4minusRimSize = getCenterYplusDivideBy4minusRimSize(rimSize);
      final float modif_centerXminus0dot125plusRimSize = getCenterXminus0dot125plusRimSize(rimSize);
      final float modif_centerYminus0dot372plusRimSize = getCenterYminus0dot375plusRimSize(rimSize);
      final float modif_centerXplus0dot375minusRimSize = getCenterXplus0dot375minusRimSize(rimSize);
      final float modif_centerYplus0dot125minusRimSize = getCenterYplus0dot125minusRimSize(rimSize);
      final float modif_centerXminus0dot375plusRimSize = getCenterXminus0dot375plusRimSize(rimSize);
      final float modif_centerYminus0dot125plusRimSize = getCenterYminus0dot125plusRimSize(rimSize);
      final float modif_centerXminus0dot75plusRimSize = getCenterXminus0dot75plusRimSize(rimSize);
      final float modif_centerXplusDivideBy4minusRimSize = getCenterXplusDivideBy4minusRimSize(rimSize);
      final float modif_centerYplus0dot375minusRimSize = getCenterYplus0dot375minusRimSize(rimSize);
      final float modif_centerXplus0dot75minusRimSize = getCenterXplus0dot75minusRimSize(rimSize);
      final float modif_centerXplus0dot125minusRimSize = getCenterXplus0dot125minusRimSize(rimSize);
      final float modif_centerXminusDidiveBy4plusRimSize = getCenterXminusDivideBy4plusRimSize(rimSize);
      modif_centerXplusDivideBy2minusRimSize = getCenterXplusDivideBy2minusRimSize(rimSize);
      modif_centerYplusDivideBy2minusRimSize = getCenterYplusDivideBy2minusRimSize(rimSize);
      modif_centerYminusDivideBy2plusRimSize = getCenterYminusDivideBy2plusRimSize(rimSize);

      if (mCentreY > mCentreX) {
        if (gaugeType == GaugeType.North) {
          rectF = getRectF(rimSize, modif_centerXplusDivideBy2minusRimSize, modif_centerYplus0dot75minusRimSize,
                  modif_centerYminusDivideBy4plusRimSize);
        } else if (gaugeType == GaugeType.South) {
          rectF = getRectF(rimSize, modif_centerXplusDivideBy2minusRimSize, modif_centerYplusDivideBy4minusRimSize,
                  modif_centerYminus0dot75plusRimSize);
        } else if (gaugeType == GaugeType.West) {
          rectF = getRectF(modif_centerXminusDidiveBy4plusRimSize, modif_centerXplus0dot75minusRimSize,
                  modif_centerYplusDivideBy2minusRimSize, modif_centerYminusDivideBy2plusRimSize);
        } else if (gaugeType == GaugeType.East) {
          rectF = getRectF(modif_centerXminus0dot75plusRimSize, modif_centerXplusDivideBy4minusRimSize,
                  modif_centerYplusDivideBy2minusRimSize, modif_centerYminusDivideBy2plusRimSize);
        } else if (gaugeType == GaugeType.NorthEast) {
          rectF = getRectF(modif_centerXminus0dot125plusRimSize, modif_centerXplus0dot375minusRimSize,
                  modif_centerYplus0dot125minusRimSize, modif_centerYminus0dot372plusRimSize);
        } else if (gaugeType == GaugeType.NorthWest) {
          rectF = getRectF(modif_centerXminus0dot375plusRimSize, modif_centerXplus0dot125minusRimSize,
                  modif_centerYplus0dot125minusRimSize, modif_centerYminus0dot372plusRimSize);
        } else if (gaugeType == GaugeType.SouthEast) {
          rectF = getRectF(modif_centerXminus0dot125plusRimSize, modif_centerXplus0dot375minusRimSize,
                  modif_centerYplus0dot375minusRimSize, modif_centerYminus0dot125plusRimSize);
        } else if (gaugeType == GaugeType.SouthWest) {
          rectF = getRectF(modif_centerXminus0dot375plusRimSize, modif_centerXplus0dot125minusRimSize,
                  modif_centerYplus0dot375minusRimSize, modif_centerYminus0dot125plusRimSize);
        } else {
          rectF = getRectF(rimSize, modif_centerXplusDivideBy2minusRimSize, modif_centerYplusDivideBy2minusRimSize,
                  modif_centerYminusDivideBy2plusRimSize);
        }
      } else if (gaugeType == GaugeType.West) {
        rectF = getRectF(modif_centerXminusDidiveBy4plusRimSize, modif_centerXplus0dot75minusRimSize,
                modif_centerYplusDivideBy2minusRimSize, rimSize);
      } else if (gaugeType == GaugeType.East) {
        rectF = getRectF(modif_centerXminus0dot75plusRimSize, modif_centerXplusDivideBy4minusRimSize,
                modif_centerYplusDivideBy2minusRimSize, rimSize);
      } else if (gaugeType == GaugeType.North) {
        rectF = getRectF(modif_centerXminusDivideBy2plusRimSize, modif_centerXplusDivideBy2minusRimSize,
                modif_centerYplus0dot75minusRimSize, modif_centerYminusDivideBy4plusRimSize);
      } else if (gaugeType == GaugeType.South) {
        rectF = getRectF(modif_centerXminusDivideBy2plusRimSize, modif_centerXplusDivideBy2minusRimSize,
                modif_centerYplusDivideBy4minusRimSize, modif_centerYminus0dot75plusRimSize);
      } else if (gaugeType == GaugeType.NorthEast) {
        rectF = getRectF(modif_centerXminus0dot125plusRimSize, modif_centerXplus0dot375minusRimSize,
                modif_centerYplus0dot125minusRimSize, modif_centerYminus0dot372plusRimSize);
      } else if (gaugeType == GaugeType.NorthWest) {
        rectF = getRectF(modif_centerXminus0dot375plusRimSize, modif_centerXplus0dot125minusRimSize,
                modif_centerYplus0dot125minusRimSize, modif_centerYminus0dot372plusRimSize);
      } else if (gaugeType == GaugeType.SouthEast) {
        rectF = getRectF(modif_centerXminus0dot125plusRimSize, modif_centerXplus0dot375minusRimSize,
                modif_centerYplus0dot375minusRimSize, modif_centerYminus0dot125plusRimSize);
      } else if (gaugeType == GaugeType.SouthWest) {
        rectF = getRectF(modif_centerXminus0dot375plusRimSize, modif_centerXplus0dot125minusRimSize,
                modif_centerYplus0dot375minusRimSize, modif_centerYminus0dot125plusRimSize);
      } else {
        rectF = getRectF(modif_centerXminusDivideBy2plusRimSize, modif_centerXplusDivideBy2minusRimSize,
                modif_centerYplusDivideBy2minusRimSize, rimSize);
      }
      mRangeFrame = rectF;
    }
    return rectF;
  }

  @NonNull private static RectF getRectF(final float rimSize, final float modif_centerXplusDivideBy2minusRimSize, final float modif_centerYplus0dot75minusRimSize,
                                         final float modif_centerYminusDivideBy4plusRimSize) {
    return new RectF(rimSize, modif_centerYminusDivideBy4plusRimSize, modif_centerXplusDivideBy2minusRimSize,
            modif_centerYplus0dot75minusRimSize);
  }

  private float getCenterYplusDivideBy2minusRimSize(final float rimSize) {
    return mCentreY + modifMinSizeDivideBy2 - rimSize;
  }

  private float getCenterXplusDivideBy2minusRimSize(final float rimSize) {
    return mCentreX + modifMinSizeDivideBy2 - rimSize;
  }

  private float getCenterXplus0dot75minusRimSize(final float rimSize) {
    return mCentreX + modifMinSizeMultipleBy0dot75 - rimSize;
  }

  private float getCenterXplus0dot375minusRimSize(final float rimSize) {
    return mCentreX + modifMinSizeMultipleBy0dot375 - rimSize;
  }

  private float getCenterYplus0dot375minusRimSize(final float rimSize) {
    return mCentreY + modifMinSizeMultipleBy0dot375 - rimSize;
  }

  private float getCenterXminus0dot375plusRimSize(final float rimSize) {
    return mCentreX - modifMinSizeMultipleBy0dot375 + rimSize;
  }

  private float getCenterXminus0dot125plusRimSize(final float rimSize) {
    return mCentreX - modifMinSizeMultipleBy0dot125 + rimSize;
  }

  private float getCenterXplusDivideBy4minusRimSize(final float rimSize) {
    return mCentreX + modifMinSizeDivideBy4 - rimSize;
  }

  private float getCenterYminus0dot75plusRimSize(final float rimSize) {
    return mCentreY - modifMinSizeMultipleBy0dot75 + rimSize;
  }

  private float getCenterYminusDivideBy4plusRimSize(final float rimSize) {
    return mCentreY - modifMinSizeDivideBy4 + rimSize;
  }

  private float getCenterYminusDivideBy2plusRimSize(final float rimSize) {
    return mCentreY - modifMinSizeDivideBy2 + rimSize;
  }

  private float getCenterYplus0dot75minusRimSize(final float rimSize) {
    return mCentreY + modifMinSizeMultipleBy0dot75 - rimSize;
  }

  private float getCenterYminus0dot375plusRimSize(final float rimSize) {
    return mCentreY - modifMinSizeMultipleBy0dot375 + rimSize;
  }

  private float getCenterYminus0dot125plusRimSize(final float rimSize) {
    return mCentreY - modifMinSizeMultipleBy0dot125 + rimSize;
  }

  private float getCenterYplus0dot125minusRimSize(final float rimSize) {
    return mCentreY + modifMinSizeMultipleBy0dot125 - rimSize;
  }

  private float getCenterXminusDivideBy2plusRimSize(final float rimSize) {
    return mCentreX - modifMinSizeDivideBy2 + rimSize;
  }

  private float getCenterXminus0dot75plusRimSize(final float rimSize) {
    return mCentreX - modifMinSizeMultipleBy0dot75 + rimSize;
  }

  private float getCenterYplusDivideBy4minusRimSize(final float rimSize) {
    return mCentreY + modifMinSizeDivideBy4 - rimSize;
  }

  private float getCenterXminusDivideBy4plusRimSize(final float rimSize) {
    return mCentreX - modifMinSizeDivideBy4 + rimSize;
  }

  private float getCenterXplus0dot125minusRimSize(final float rimSize) {
    return mCentreX + modifMinSizeMultipleBy0dot125 - rimSize;
  }
}