package com.example.aprokopenko.triphelper.gauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.aprokopenko.triphelper.gauge.enums.GaugeType;

import java.util.Iterator;

class ScaleRenderer extends View {

    private       TripHelperGauge gauge;
    protected     GaugeScale      gaugeScale;
    private       RectF           rectF;
    private       Paint           paint;
    private final double          arcAliasing;

    private double modifMinSizeDivideBy4;
    private double modifMinSizeDivideBy2;
    private double modifMinSizeMultipleBy0dot75;
    private double modifMinSizeMultipleBy0dot875;
    private double modifMinSizeMultipleBy0dot125;
    private double modifMinSizeMultipleBy0dot375;
    private double modifRimWidthDivideBy2;

    public ScaleRenderer(Context context, TripHelperGauge gauge, GaugeScale gaugeScale) {
        this(context, null);
        this.gauge = gauge;
        this.gaugeScale = gaugeScale;
        rectF = new RectF();
        paint = new Paint();
    }

    public ScaleRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);
        gauge = null;
        gaugeScale = null;
        arcAliasing = 0.7D;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (gauge != null && gaugeScale != null) {
            gaugeScale.mGauge = gauge;

            //modif optimization
            modifMinSizeDivideBy4 = gauge.mMinSize / 4.0D;
            modifMinSizeDivideBy2 = gauge.mMinSize / 2.0D;
            modifMinSizeMultipleBy0dot75 = gauge.mMinSize * 0.75D;
            modifMinSizeMultipleBy0dot875 = gauge.mMinSize * 0.875D;
            modifMinSizeMultipleBy0dot125 = gauge.mMinSize * 0.125D;
            modifMinSizeMultipleBy0dot375 = gauge.mMinSize * 0.375D;
            modifRimWidthDivideBy2 = gaugeScale.rimWidth / 2.0D;

            if (gaugeScale.endValue > gaugeScale.startValue && gaugeScale.startValue < gaugeScale.endValue) {
                double totalTicks = (gaugeScale.getEndValue() - gaugeScale.getStartValue()) / gaugeScale.interval;
                rectF = calculateRectF(gaugeScale.radiusFactor);
                paint.setAntiAlias(true);
                paint.setColor(0);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth((float) gaugeScale.rimWidth);
                paint.setColor(gaugeScale.rimColor);
                if (gaugeScale.showRim) {
                    canvas.drawArc(rectF, (float) gaugeScale.startAngle, (float) gaugeScale.sweepAngle, false, paint);
                }

                paint.setStrokeWidth(1.0F);
                paint.setStyle(Paint.Style.FILL);
                paint.setTypeface(gaugeScale.labelTextStyle);
                paint = getLabels(paint, gaugeScale.labelColor, (float) gaugeScale.labelTextSize);
                onDrawRanges(canvas, gaugeScale);
                if (gaugeScale.showLabels) {
                    onDrawLabels(canvas, paint, totalTicks, gaugeScale);
                }
                paint = getTicks(paint, gaugeScale.majorTickSettings.color, gaugeScale);
                if (gaugeScale.showTicks) {
                    onDrawTicks(canvas, paint, totalTicks, gaugeScale);
                }
            }
        }
    }

    private Paint getLabels(Paint paint, int labelColor, float labelSize) {
        paint.setColor(labelColor);
        paint.setTextSize(labelSize * TripHelperGauge.DENSITY);
        return paint;
    }

    private Paint getTicks(Paint paint, int majorTickColor, GaugeScale gaugeScale) {
        paint.setColor(majorTickColor);
        paint.setStrokeWidth((float) gaugeScale.majorTickSettings.width);
        return paint;
    }

    private void onDrawLabels(Canvas canvas, Paint paint, double totalTicks, GaugeScale gaugeScale) {
        double anglularSpace    = gaugeScale.sweepAngle / totalTicks * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        double angle            = gaugeScale.startAngle * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        double mInnerBevelWidth = gauge.mInnerBevelWidth;
        double tempGaugeSize    = (mInnerBevelWidth - 10.0D) / 2.0D;
        tempGaugeSize *= 1.0D - gaugeScale.radiusFactor;
        String label;
        String value;
        int    fractionalDigit = gaugeScale.numberOfDecimalDigits;
        double startValue      = gaugeScale.startValue;

        for (int j = 0; (double) j <= totalTicks; ++j) {
            if (fractionalDigit < 0) {
                if ((double) ((int) gaugeScale.startValue) == gaugeScale.startValue && (double) ((int) gaugeScale.interval) == gaugeScale
                        .interval) {
                    label = gaugeScale.labelPrefix + String.valueOf((int) startValue) + gaugeScale.labelPostfix;
                }
                else {
                    label = gaugeScale.labelPrefix + String.valueOf((float) startValue) + gaugeScale.labelPostfix;
                }
            }
            else {
                value = String.format("%." + String.valueOf(fractionalDigit) + "f", startValue);
                label = gaugeScale.labelPrefix + value + gaugeScale.labelPostfix;
            }

            paint.setAntiAlias(true);
            double widthOfLabel = (double) paint.measureText(String.valueOf((int) gaugeScale.endValue));
            //modif optimization
            double modifWidthOfLabelDivideBy4     = widthOfLabel / 4.0D;
            double modifWidthOfLabelDivideBy1dot8 = widthOfLabel / 1.8D;


            double outerSize = tempGaugeSize - gaugeScale.LabelOffset * gauge.mCentreX - widthOfLabel / 2.0D - modifRimWidthDivideBy2;
            double tempLabelSize = gauge.mMinSize / 2.0D - tempGaugeSize + gaugeScale.LabelOffset * gauge.mCentreX +
                    modifRimWidthDivideBy2;

            double modifOuterSizeMultipleBy1dot5 = outerSize * 1.5D;
            double modifOuterSizeMultipleCos     = outerSize * Math.cos(angle);
            double modifOuterSizeMultipleSin     = outerSize * Math.sin(angle);

            double x;
            double y;

            if (gauge.mCentreY > gauge.mCentreX) {
                if (gauge.GaugeType == GaugeType.West) {
                    x = gauge.mCentreX - modifMinSizeDivideBy4 + outerSize + modifOuterSizeMultipleCos + tempLabelSize +
                            modifWidthOfLabelDivideBy4;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.SouthWest) {
                    if (gauge.GaugeType == GaugeType.East) {
                        x = gauge.mCentreX - modifMinSizeMultipleBy0dot75 + outerSize + modifOuterSizeMultipleCos + tempLabelSize +
                                modifWidthOfLabelDivideBy4;
                    }
                    else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.SouthEast) {
                        x = outerSize + outerSize * Math.cos(angle) + tempLabelSize + modifWidthOfLabelDivideBy4;
                    }
                    else {
                        x = gauge.mCentreX - modifMinSizeMultipleBy0dot875 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                                .cos(angle) + tempLabelSize + modifWidthOfLabelDivideBy4;
                    }
                }
                else {
                    x = gauge.mCentreX - modifMinSizeMultipleBy0dot125 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                            .cos(angle) + tempLabelSize + modifWidthOfLabelDivideBy4;
                }

                if (gauge.GaugeType == GaugeType.North) {
                    y = gauge.mCentreY - modifMinSizeDivideBy4 + outerSize + outerSize * Math
                            .sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.NorthEast) {
                    if (gauge.GaugeType == GaugeType.South) {
                        y = gauge.mCentreY - modifMinSizeMultipleBy0dot75 + outerSize + modifOuterSizeMultipleSin + tempLabelSize +
                                modifWidthOfLabelDivideBy1dot8;
                    }
                    else if (gauge.GaugeType != GaugeType.SouthEast && gauge.GaugeType != GaugeType.SouthWest) {
                        y = gauge.mCentreY - modifMinSizeDivideBy2 + outerSize + modifOuterSizeMultipleSin + tempLabelSize +
                                modifWidthOfLabelDivideBy1dot8;
                    }
                    else {
                        y = gauge.mCentreY - modifMinSizeMultipleBy0dot875 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                                .sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
                    }
                }
                else {
                    y = gauge.mCentreY - modifMinSizeMultipleBy0dot125 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                            .sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
                }
            }
            else {
                if (gauge.GaugeType == GaugeType.West) {
                    x = gauge.mCentreX - modifMinSizeDivideBy4 + outerSize + modifOuterSizeMultipleCos + tempLabelSize +
                            modifWidthOfLabelDivideBy4;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.SouthWest) {
                    if (gauge.GaugeType == GaugeType.East) {
                        x = gauge.mCentreX - modifMinSizeMultipleBy0dot75 + outerSize + modifOuterSizeMultipleCos + tempLabelSize +
                                modifWidthOfLabelDivideBy4;
                    }
                    else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.SouthEast) {
                        x = gauge.mCentreX - modifMinSizeDivideBy2 + outerSize + modifOuterSizeMultipleCos + tempLabelSize +
                                modifWidthOfLabelDivideBy4;
                    }
                    else {
                        x = gauge.mCentreX - modifMinSizeMultipleBy0dot875 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                                .cos(angle) + tempLabelSize + modifWidthOfLabelDivideBy4;
                    }
                }
                else {
                    x = gauge.mCentreX - modifMinSizeMultipleBy0dot125 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                            .cos(angle) + tempLabelSize + modifWidthOfLabelDivideBy4;
                }

                if (gauge.GaugeType == GaugeType.North) {
                    y = gauge.mCentreY - modifMinSizeDivideBy4 + outerSize + modifOuterSizeMultipleSin + tempLabelSize +
                            modifWidthOfLabelDivideBy1dot8;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.NorthEast) {
                    if (gauge.GaugeType == GaugeType.South) {
                        y = gauge.mCentreY - modifMinSizeMultipleBy0dot75 + outerSize + modifOuterSizeMultipleSin + tempLabelSize +
                                modifWidthOfLabelDivideBy1dot8;
                    }
                    else if (gauge.GaugeType != GaugeType.SouthEast && gauge.GaugeType != GaugeType.SouthWest) {
                        y = outerSize + outerSize * Math.sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
                    }
                    else {
                        y = gauge.mCentreY - modifMinSizeMultipleBy0dot875 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                                .sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
                    }
                }
                else {
                    y = gauge.mCentreY - modifMinSizeMultipleBy0dot125 + outerSize + modifOuterSizeMultipleBy1dot5 * Math
                            .sin(angle) + tempLabelSize + modifWidthOfLabelDivideBy1dot8;
                }
            }

            canvas.drawText(label, (float) x, (float) y, paint);
            angle += anglularSpace;
            startValue += gaugeScale.interval;
        }
    }

    private void onDrawTicks(Canvas canvas, Paint paint, double totalTicks, GaugeScale gaugeScale) {
        double angularSpace     = gaugeScale.sweepAngle / totalTicks * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        double angle            = gaugeScale.startAngle * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        double mInnerBevelWidth = gauge.mInnerBevelWidth;
        double tempGaugeSize    = (mInnerBevelWidth - 10.0D) / 2.0D;
        tempGaugeSize *= 1.0D - gaugeScale.radiusFactor;

        double tickLength1;
        double minorTickPosition;
        double outerSize;
        double x2;
        double y2;
        double innerSize;

        //modif optimization
        double modifTickOffsetMultByCenterX = gaugeScale.majorTickSettings.offset * gauge.mCentreX;
        double modifArcAliasMultByRimWidth  = 0.7D * gaugeScale.rimWidth;
        double modif1dot5MultipByCosAngle   = 1.5D * Math.cos(angle);
        double modif1dot5MultipBySinAngle   = 1.5D * Math.sin(angle);

        for (int minorTicksCount = 0; (double) minorTicksCount <= totalTicks; ++minorTicksCount) {
            paint.setAntiAlias(true);
            paint.setStrokeWidth((float) gaugeScale.majorTickSettings.width);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(gaugeScale.majorTickSettings.color);

            tickLength1 = tempGaugeSize - modifTickOffsetMultByCenterX + modifArcAliasMultByRimWidth - modifRimWidthDivideBy2;

            //modif optimization
            double modifTickLengthMultByCosAngle         = tickLength1 * Math.cos(angle);
            double modifTickMultBy1dot5andMultByCosAngle = tickLength1 * modif1dot5MultipByCosAngle;

            double tickLength = modifMinSizeDivideBy2 - tempGaugeSize + modifTickOffsetMultByCenterX - modifArcAliasMultByRimWidth +
                    modifRimWidthDivideBy2;


            if (gauge.mCentreY > gauge.mCentreX) {
                if (gauge.GaugeType == GaugeType.West) {
                    minorTickPosition = gauge.mCentreX - modifMinSizeDivideBy4 + tickLength1 + modifTickLengthMultByCosAngle + tickLength;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.SouthWest) {
                    if (gauge.GaugeType == GaugeType.East) {
                        minorTickPosition = gauge.mCentreX - modifMinSizeMultipleBy0dot75 + tickLength1 + modifTickLengthMultByCosAngle +
                                tickLength;
                    }
                    else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.SouthEast) {
                        minorTickPosition = tickLength1 + modifTickLengthMultByCosAngle + tickLength;
                    }
                    else {
                        minorTickPosition = gauge.mCentreX - modifMinSizeMultipleBy0dot875 + tickLength1 +
                                modifTickMultBy1dot5andMultByCosAngle + tickLength;
                    }
                }
                else {
                    minorTickPosition = gauge.mCentreX - modifMinSizeMultipleBy0dot125 + tickLength1 +
                            modifTickMultBy1dot5andMultByCosAngle +
                            tickLength;
                }

                if (gauge.GaugeType == GaugeType.North) {
                    outerSize = gauge.mCentreY - modifMinSizeDivideBy4 + tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.NorthEast) {
                    if (gauge.GaugeType == GaugeType.South) {
                        outerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot75 + tickLength1 + tickLength1 * Math
                                .sin(angle) + tickLength;
                    }
                    else if (gauge.GaugeType != GaugeType.SouthEast && gauge.GaugeType != GaugeType.SouthWest) {
                        outerSize = gauge.mCentreY - modifMinSizeDivideBy2 + tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
                    }
                    else {
                        outerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot875 + tickLength1 + tickLength1 *
                                modif1dot5MultipBySinAngle +
                                tickLength;
                    }
                }
                else {
                    outerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot125 + tickLength1 + tickLength1 * modif1dot5MultipBySinAngle +
                            tickLength;
                }
            }
            else {
                if (gauge.GaugeType == GaugeType.West) {
                    minorTickPosition = gauge.mCentreX - modifMinSizeDivideBy4 + tickLength1 + modifTickLengthMultByCosAngle + tickLength;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.SouthWest) {
                    if (gauge.GaugeType == GaugeType.East) {
                        minorTickPosition = gauge.mCentreX - modifMinSizeMultipleBy0dot75 + tickLength1 + modifTickLengthMultByCosAngle +
                                tickLength;
                    }
                    else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.SouthEast) {
                        minorTickPosition = gauge.mCentreX - modifMinSizeDivideBy2 + tickLength1 + modifTickLengthMultByCosAngle +
                                tickLength;
                    }
                    else {
                        minorTickPosition = gauge.mCentreX - modifMinSizeMultipleBy0dot875 + tickLength1 +
                                modifTickMultBy1dot5andMultByCosAngle + tickLength;
                    }
                }
                else {
                    minorTickPosition = gauge.mCentreX - modifMinSizeMultipleBy0dot125 + tickLength1 +
                            modifTickMultBy1dot5andMultByCosAngle +
                            tickLength;
                }

                if (gauge.GaugeType == GaugeType.North) {
                    outerSize = gauge.mCentreY - modifMinSizeDivideBy4 + tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.NorthEast) {
                    if (gauge.GaugeType == GaugeType.South) {
                        outerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot75 + tickLength1 + tickLength1 * Math
                                .sin(angle) + tickLength;
                    }
                    else if (gauge.GaugeType != GaugeType.SouthEast && gauge.GaugeType != GaugeType.SouthWest) {
                        outerSize = tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
                    }
                    else {
                        outerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot875 + tickLength1 + tickLength1 *
                                modif1dot5MultipBySinAngle +
                                tickLength;
                    }
                }
                else {
                    outerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot125 + tickLength1 + tickLength1 * modif1dot5MultipBySinAngle +
                            tickLength;
                }
            }

            x2 = tempGaugeSize - gaugeScale.majorTickSettings.size - modifTickOffsetMultByCenterX + modifArcAliasMultByRimWidth -
                    modifRimWidthDivideBy2;
            tickLength = modifMinSizeDivideBy2 - tempGaugeSize + gaugeScale.majorTickSettings.size + modifTickOffsetMultByCenterX -
                    modifArcAliasMultByRimWidth + modifRimWidthDivideBy2;

            //modif optimization
            double modif_x2MultByCosAngle      = x2 * Math.cos(angle);
            double modif_x2MultiplBySinAngle   = x2 * Math.sin(angle);
            double modif_x2multiplByX2CosAngle = x2 * modif1dot5MultipByCosAngle;
            double modif_x2multiplByX2SinAngle = x2 * modif1dot5MultipBySinAngle;

            if (gauge.mCentreY > gauge.mCentreX) {
                if (gauge.GaugeType == GaugeType.West) {
                    y2 = gauge.mCentreX - modifMinSizeDivideBy4 + x2 + modif_x2MultByCosAngle + tickLength;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.SouthWest) {
                    if (gauge.GaugeType == GaugeType.East) {
                        y2 = gauge.mCentreX - modifMinSizeMultipleBy0dot75 + x2 + modif_x2MultByCosAngle + tickLength;
                    }
                    else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.SouthEast) {
                        y2 = x2 + modif_x2MultByCosAngle + tickLength;
                    }
                    else {
                        y2 = gauge.mCentreX - modifMinSizeMultipleBy0dot875 + x2 + modif_x2multiplByX2CosAngle + tickLength;
                    }
                }
                else {
                    y2 = gauge.mCentreX - modifMinSizeMultipleBy0dot125 + x2 + modif_x2multiplByX2CosAngle + tickLength;
                }

                if (gauge.GaugeType == GaugeType.North) {
                    innerSize = gauge.mCentreY - modifMinSizeDivideBy4 + x2 + modif_x2MultiplBySinAngle + tickLength;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.NorthEast) {
                    if (gauge.GaugeType == GaugeType.South) {
                        innerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot75 + x2 + modif_x2MultiplBySinAngle + tickLength;
                    }
                    else if (gauge.GaugeType != GaugeType.SouthEast && gauge.GaugeType != GaugeType.SouthWest) {
                        innerSize = gauge.mCentreY - modifMinSizeDivideBy2 + x2 + modif_x2MultiplBySinAngle + tickLength;
                    }
                    else {
                        innerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot875 + x2 + modif_x2multiplByX2SinAngle + tickLength;
                    }
                }
                else {
                    innerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot125 + x2 + modif_x2multiplByX2SinAngle + tickLength;
                }
            }
            else {
                if (gauge.GaugeType == GaugeType.West) {
                    y2 = gauge.mCentreX - modifMinSizeDivideBy4 + x2 + modif_x2MultByCosAngle + tickLength;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.SouthWest) {
                    if (gauge.GaugeType == GaugeType.East) {
                        y2 = gauge.mCentreX - modifMinSizeMultipleBy0dot75 + x2 + modif_x2MultByCosAngle + tickLength;
                    }
                    else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.SouthEast) {
                        y2 = gauge.mCentreX - modifMinSizeDivideBy2 + x2 + modif_x2MultByCosAngle + tickLength;
                    }
                    else {
                        y2 = gauge.mCentreX - modifMinSizeMultipleBy0dot875 + x2 + modif_x2multiplByX2CosAngle + tickLength;
                    }
                }
                else {
                    y2 = gauge.mCentreX - modifMinSizeMultipleBy0dot125 + x2 + modif_x2multiplByX2CosAngle + tickLength;
                }

                if (gauge.GaugeType == GaugeType.North) {
                    innerSize = gauge.mCentreY - modifMinSizeDivideBy4 + x2 + modif_x2MultiplBySinAngle + tickLength;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.NorthEast) {
                    if (gauge.GaugeType == GaugeType.South) {
                        innerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot75 + x2 + modif_x2MultiplBySinAngle + tickLength;
                    }
                    else if (gauge.GaugeType != GaugeType.SouthEast && gauge.GaugeType != GaugeType.SouthWest) {
                        innerSize = x2 + modif_x2MultiplBySinAngle + tickLength;
                    }
                    else {
                        innerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot875 + x2 + modif_x2multiplByX2SinAngle + tickLength;
                    }
                }
                else {
                    innerSize = gauge.mCentreY - modifMinSizeMultipleBy0dot125 + x2 + modif_x2multiplByX2SinAngle + tickLength;
                }
            }

            canvas.drawLine((float) y2, (float) innerSize, (float) minorTickPosition, (float) outerSize, paint);
            angle += angularSpace;
        }

        angle = gaugeScale.startAngle * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        double minorTicksQuantity = totalTicks * gaugeScale.minorTicksPerInterval;
        double minorTickAngle     = angularSpace / (gaugeScale.minorTicksPerInterval + 1.0D);
        paint.setStrokeWidth((float) gaugeScale.minorTickSettings.width);

        //modif optimization
        double modif_arcAlisaMultByWidth      = arcAliasing * gaugeScale.rimWidth;
        double modif_minorOffsetMultByCenterX = gaugeScale.minorTickSettings.offset * gauge.mCentreX;


        for (int i = 1; (double) i <= minorTicksQuantity; ++i) {
            angle += minorTickAngle;
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(gaugeScale.minorTickSettings.color);
            minorTickPosition = GaugeConstants.ZERO;
            outerSize = tempGaugeSize - modif_minorOffsetMultByCenterX + modif_arcAlisaMultByWidth -
                    minorTickPosition - modifRimWidthDivideBy2;
            tickLength1 = modifMinSizeDivideBy2 - tempGaugeSize + modif_minorOffsetMultByCenterX - modif_arcAlisaMultByWidth +
                    minorTickPosition + modifRimWidthDivideBy2;

            //modif optimization
            double modif_outerSizeMultByCosAngle = outerSize * Math.cos(angle);
            double modif_outerSizeMultBySinAngle = outerSize * Math.sin(angle);
            double modif_outerSizeMultBy1dot5Cos = outerSize * modif1dot5MultipByCosAngle;
            double modif_outerSizeMultBy1dot5Sin = outerSize * modif1dot5MultipBySinAngle;

            if (gauge.mCentreY > gauge.mCentreX) {
                if (gauge.GaugeType == GaugeType.West) {
                    x2 = gauge.mCentreX - modifMinSizeDivideBy4 + outerSize + modif_outerSizeMultByCosAngle + tickLength1;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.SouthWest) {
                    if (gauge.GaugeType == GaugeType.East) {
                        x2 = gauge.mCentreX - modifMinSizeMultipleBy0dot75 + outerSize + modif_outerSizeMultByCosAngle + tickLength1;
                    }
                    else if (gauge.GaugeType != GaugeType.SouthEast && gauge.GaugeType != GaugeType.NorthEast) {
                        x2 = outerSize + modif_outerSizeMultByCosAngle + tickLength1;
                    }
                    else {
                        x2 = gauge.mCentreX - modifMinSizeMultipleBy0dot875 + outerSize + modif_outerSizeMultBy1dot5Cos + tickLength1;
                    }
                }
                else {
                    x2 = gauge.mCentreX - modifMinSizeMultipleBy0dot125 + outerSize + modif_outerSizeMultBy1dot5Cos + tickLength1;
                }

                if (gauge.GaugeType == GaugeType.North) {
                    y2 = gauge.mCentreY - modifMinSizeDivideBy4 + outerSize + modif_outerSizeMultBySinAngle + tickLength1;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.NorthEast) {
                    if (gauge.GaugeType == GaugeType.South) {
                        y2 = gauge.mCentreY - modifMinSizeMultipleBy0dot75 + outerSize + modif_outerSizeMultBySinAngle + tickLength1;
                    }
                    else if (gauge.GaugeType != GaugeType.SouthWest && gauge.GaugeType != GaugeType.SouthEast) {
                        y2 = gauge.mCentreY - modifMinSizeDivideBy2 + outerSize + modif_outerSizeMultBySinAngle + tickLength1;
                    }
                    else {
                        y2 = gauge.mCentreY - modifMinSizeMultipleBy0dot875 + outerSize + modif_outerSizeMultBy1dot5Sin + tickLength1;
                    }
                }
                else {
                    y2 = gauge.mCentreY - modifMinSizeMultipleBy0dot125 + outerSize + modif_outerSizeMultBy1dot5Sin + tickLength1;
                }
            }
            else {
                if (gauge.GaugeType == GaugeType.West) {
                    x2 = gauge.mCentreX - modifMinSizeDivideBy4 + outerSize + modif_outerSizeMultByCosAngle + tickLength1;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.SouthWest) {
                    if (this.gauge.GaugeType == GaugeType.East) {
                        x2 = this.gauge.mCentreX - modifMinSizeMultipleBy0dot75 + outerSize + modif_outerSizeMultByCosAngle + tickLength1;
                    }
                    else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.SouthEast) {
                        x2 = gauge.mCentreX - modifMinSizeDivideBy2 + outerSize + modif_outerSizeMultByCosAngle + tickLength1;
                    }
                    else {
                        x2 = gauge.mCentreX - modifMinSizeMultipleBy0dot875 + outerSize + modif_outerSizeMultBy1dot5Cos + tickLength1;
                    }
                }
                else {
                    x2 = gauge.mCentreX - modifMinSizeMultipleBy0dot125 + outerSize + modif_outerSizeMultBy1dot5Cos + tickLength1;
                }

                if (gauge.GaugeType == GaugeType.North) {
                    y2 = gauge.mCentreY - modifMinSizeDivideBy4 + outerSize + modif_outerSizeMultBySinAngle + tickLength1;
                }
                else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.NorthWest) {
                    if (gauge.GaugeType == GaugeType.South) {
                        y2 = gauge.mCentreY - modifMinSizeMultipleBy0dot75 + outerSize + modif_outerSizeMultBySinAngle + tickLength1;
                    }
                    else if (gauge.GaugeType != GaugeType.SouthWest && gauge.GaugeType != GaugeType.SouthEast) {
                        y2 = outerSize + modif_outerSizeMultBySinAngle + tickLength1;
                    }
                    else {
                        y2 = gauge.mCentreY - modifMinSizeMultipleBy0dot875 + outerSize + modif_outerSizeMultBy1dot5Sin + tickLength1;
                    }
                }
                else {
                    y2 = gauge.mCentreY - modifMinSizeMultipleBy0dot125 + outerSize + modif_outerSizeMultBy1dot5Sin + tickLength1;
                }
            }

            innerSize = tempGaugeSize - gaugeScale.minorTickSettings.size - modif_minorOffsetMultByCenterX + modif_arcAlisaMultByWidth -
                    minorTickPosition - modifRimWidthDivideBy2;
            tickLength1 = modifMinSizeDivideBy2 - tempGaugeSize + gaugeScale.minorTickSettings.size + modif_minorOffsetMultByCenterX -
                    modif_arcAlisaMultByWidth + minorTickPosition +
                    modifRimWidthDivideBy2;
            double x1;
            double y1;

            //modif optimization
            double modif_innerSizeMultByCos           = innerSize * Math.cos(angle);
            double modif_innerSizeMultBySin           = innerSize * Math.sin(angle);
            double modif_innerSizeMultBy1dot5CosAngle = innerSize * modif1dot5MultipByCosAngle;
            double modif_innerSizeMultBy1dot5SinAngle = innerSize * modif1dot5MultipBySinAngle;

            if (gauge.mCentreY > gauge.mCentreX) {
                if (gauge.GaugeType == GaugeType.West) {
                    x1 = gauge.mCentreX - modifMinSizeDivideBy4 + innerSize + modif_innerSizeMultByCos + tickLength1;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.SouthWest) {
                    if (gauge.GaugeType == GaugeType.East) {
                        x1 = gauge.mCentreX - modifMinSizeMultipleBy0dot75 + innerSize + modif_innerSizeMultByCos + tickLength1;
                    }
                    else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.SouthEast) {
                        x1 = innerSize + modif_innerSizeMultByCos + tickLength1;
                    }
                    else {
                        x1 = gauge.mCentreX - modifMinSizeMultipleBy0dot875 + innerSize + modif_innerSizeMultBy1dot5CosAngle + tickLength1;
                    }
                }
                else {
                    x1 = gauge.mCentreX - modifMinSizeMultipleBy0dot125 + innerSize + modif_innerSizeMultBy1dot5CosAngle + tickLength1;
                }

                if (gauge.GaugeType == GaugeType.North) {
                    y1 = gauge.mCentreY - modifMinSizeDivideBy4 + innerSize + modif_innerSizeMultBySin + tickLength1;
                }
                else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.NorthWest) {
                    if (gauge.GaugeType == GaugeType.South) {
                        y1 = gauge.mCentreY - modifMinSizeMultipleBy0dot75 + innerSize + modif_innerSizeMultBySin + tickLength1;
                    }
                    else if (gauge.GaugeType != GaugeType.SouthWest && gauge.GaugeType != GaugeType.SouthEast) {
                        y1 = gauge.mCentreY - modifMinSizeDivideBy2 + innerSize + modif_innerSizeMultBySin + tickLength1;
                    }
                    else {
                        y1 = gauge.mCentreY - modifMinSizeMultipleBy0dot875 + innerSize + modif_innerSizeMultBy1dot5SinAngle + tickLength1;
                    }
                }
                else {
                    y1 = gauge.mCentreY - modifMinSizeMultipleBy0dot125 + innerSize + modif_innerSizeMultBy1dot5SinAngle + tickLength1;
                }
            }
            else {
                if (gauge.GaugeType == GaugeType.West) {
                    x1 = gauge.mCentreX - modifMinSizeDivideBy4 + innerSize + modif_innerSizeMultByCos + tickLength1;
                }
                else if (gauge.GaugeType != GaugeType.NorthWest && gauge.GaugeType != GaugeType.SouthWest) {
                    if (gauge.GaugeType == GaugeType.East) {
                        x1 = gauge.mCentreX - modifMinSizeMultipleBy0dot75 + innerSize + modif_innerSizeMultByCos + tickLength1;
                    }
                    else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.SouthEast) {
                        x1 = gauge.mCentreX - modifMinSizeDivideBy2 + innerSize + modif_innerSizeMultByCos + tickLength1;
                    }
                    else {
                        x1 = gauge.mCentreX - modifMinSizeMultipleBy0dot875 + innerSize + modif_innerSizeMultBy1dot5CosAngle + tickLength1;
                    }
                }
                else {
                    x1 = gauge.mCentreX - modifMinSizeMultipleBy0dot125 + innerSize + modif_innerSizeMultBy1dot5CosAngle + tickLength1;
                }

                if (gauge.GaugeType == GaugeType.North) {
                    y1 = gauge.mCentreY - modifMinSizeDivideBy4 + innerSize + modif_innerSizeMultBySin + tickLength1;
                }
                else if (gauge.GaugeType != GaugeType.NorthEast && gauge.GaugeType != GaugeType.NorthWest) {
                    if (gauge.GaugeType == GaugeType.South) {
                        y1 = gauge.mCentreY - modifMinSizeMultipleBy0dot75 + innerSize + modif_innerSizeMultBySin + tickLength1;
                    }
                    else if (gauge.GaugeType != GaugeType.SouthWest && gauge.GaugeType != GaugeType.SouthEast) {
                        y1 = innerSize + modif_innerSizeMultBySin + tickLength1;
                    }
                    else {
                        y1 = gauge.mCentreY - modifMinSizeMultipleBy0dot875 + innerSize + modif_innerSizeMultBy1dot5SinAngle + tickLength1;
                    }
                }
                else {
                    y1 = gauge.mCentreY - modifMinSizeMultipleBy0dot125 + innerSize + modif_innerSizeMultBy1dot5SinAngle + tickLength1;
                }
            }
            canvas.drawLine((float) x1, (float) y1, (float) x2, (float) y2, paint);
            if ((double) i % gaugeScale.minorTicksPerInterval == GaugeConstants.ZERO) {
                angle += minorTickAngle;
            }
        }
    }

    private void onDrawRanges(Canvas canvas, GaugeScale gaugeScale) {
        Paint  paint;
        double startArc;
        double endtArc;
        if (gaugeScale.gaugeRanges != null) {
            for (Iterator i$ = gaugeScale.gaugeRanges.iterator(); i$.hasNext(); canvas
                    .drawArc(rectF, (float) startArc, (float) endtArc, false, paint)) {
                GaugeRange gaugeRange = (GaugeRange) i$.next();
                gaugeRange.mGauge = gaugeScale.mGauge;
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth((float) gaugeRange.width);
                paint.setColor(gaugeRange.color);
                startArc = gaugeScale.startAngle + getRangeAngle(gaugeRange.startValue, gaugeScale) - getRangeAngle(gaugeScale.startValue,
                        gaugeScale);
                endtArc = getRangeAngle(gaugeRange.endValue, gaugeScale) - getRangeAngle(gaugeRange.startValue, gaugeScale);

                //modif optimization
                double modif_withdMultByRadius   = gauge.mInnerBevelWidth * gaugeScale.radiusFactor;
                double modif_minSizeMultByRadius = gauge.mMinSize * gaugeScale.radiusFactor;

                gauge.calculateMargin(gauge.mInnerBevelWidth - modif_withdMultByRadius, gauge.mInnerBevelWidth - modif_withdMultByRadius);
                double var10000;
                double rimSize;

                if (gaugeScale.radiusFactor > 0.0D) {
                    gauge.calculateMargin(gauge.mMinSize - modif_minSizeMultByRadius, gauge.mMinSize - modif_minSizeMultByRadius);
                    var10000 = gauge.mRangePathWidth - gauge.mRimWidth;
                    gauge.getClass();
                    rimSize = var10000 - 4.0D * 10.0D;

                    //modif optimization
                    double modif_centerYminusMinSizePlusRimSize  = getCenterYminusDivideBy2plusRimSize(rimSize);
                    double modif_centerXplusMinSizeMinusRimSize  = getCenterXplusDivideBy2minusRimSize(rimSize);
                    double modif_centerYplusMinSizeMinusRimSize  = getCenterYplusDivideBy2minusRimSize(rimSize);
                    double modifCenterXminusDivideBy2plusRimSize = getCenterXminusDivideBy2plusRimSize(rimSize);

                    if (gauge.mCentreY > gauge.mCentreX) {
                        rectF = new RectF((float) rimSize, (float) modif_centerYminusMinSizePlusRimSize,
                                (float) modif_centerXplusMinSizeMinusRimSize, (float) modif_centerYplusMinSizeMinusRimSize);
                    }
                    else {
                        rectF = new RectF((float) modifCenterXminusDivideBy2plusRimSize, (float) rimSize,
                                (float) modif_centerXplusMinSizeMinusRimSize, (float) modif_centerYplusMinSizeMinusRimSize);
                    }

                    gauge.mRangeFrame = rectF;
                    float factor = gauge.mRangeFrame.left + (gauge.mRangeFrame
                            .width() / 2.0F - gauge.mRangeFrame.left) * (float) gaugeScale.radiusFactor + (float) (gaugeRange.offset *
                            (gauge.mCentreX - gauge.mRimWidth));
                    if (gauge.mCentreY > gauge.mCentreX) {
                        rectF = new RectF(factor, (float) (getCenterYminusDivideBy2plusRimSize((double) factor)),
                                (float) (getCenterXplusDivideBy2minusRimSize((double) factor)),
                                (float) (getCenterYplusDivideBy2minusRimSize((double) factor)));
                    }
                    else {
                        factor = gauge.mRangeFrame.top + (gauge.mRangeFrame
                                .height() / 2.0F - gauge.mRangeFrame.top) * (float) gaugeScale.radiusFactor + (float) (gaugeRange.offset
                                * (gauge.mCentreY - gauge.mRimWidth));
                        rectF = new RectF((float) (getCenterXminusDivideBy2plusRimSize((double) factor)), factor,
                                (float) (getCenterXplusDivideBy2minusRimSize((double) factor)),
                                (float) (getCenterYplusDivideBy2minusRimSize((double) factor)));
                    }
                }
                else {
                    if (gauge.mCentreY > gauge.mCentreX) {
                        rimSize = gauge.mMinSize - gauge.mRimWidth - (double) ((float) (gaugeRange.offset * (gauge.mCentreX - gauge
                                .mRimWidth))) + gaugeRange.width / 2.0D;

                        //modif optimization
                        double modify_centerXplus0dot125minusRimSize  = getCenterXplus0dot125minusRimSize(rimSize);
                        double modify_centerYminus0dot125plusRimSize  = getCenterYminus0dot125plusRimSize(rimSize);
                        double modify_centerYplus0dot375minusRimSize  = getCenterYplus0dot375minusRimSize(rimSize);
                        double modify_centerXminus0dot375plusRimSize  = getCenterXminus0dot375plusRimSize(rimSize);
                        double modife_centerYplus0dot125minusRimSize  = getCenterYplus0dot125minusRimSize(rimSize);
                        double modif_centerXminusDivideBy4plusRimSize = getCenterXminusDivideBy4plusRimSize(rimSize);
                        double modif_centerXminus0dot75plusRimSize    = getCenterXminus0dot75plusRimSize(rimSize);
                        double modif_centerYminusDivideBy4plusRimSize = getCenterYminusDivideBy4plusRimSize(rimSize);
                        double modif_centerYminus0dot75plusRimSize    = getCenterYminus0dot75plusRimSize(rimSize);
                        double modif_centerYminusMinSizePlusRimSize   = getCenterYminusDivideBy2plusRimSize(rimSize);
                        double modif_centerXplusMinSizeMinusRimSize   = getCenterXplusDivideBy2minusRimSize(rimSize);
                        double modif_centerYplusMinSizeMinusRimSize   = getCenterYplusDivideBy2minusRimSize(rimSize);
                        double modif_centerXminus0dot125plusRimSize   = getCenterXminus0dot125plusRimSize(rimSize);
                        double modif_centerXminus0dot375plusRimSize   = getCenterYminus0dot375plusRimSize(rimSize);
                        double modif_centerYplus0dot75minusRimSize    = getCenterYplus0dot75minusRimSize(rimSize);
                        double modif_centerXplusDivideBy4minusRimSize = getCenterXplusDivideBy4minusRimSize(rimSize);
                        double modif_centerYplusDivideBy4minusRimSize = getCenterYplusDivideBy4minusRimSize(rimSize);

                        if (gauge.GaugeType == GaugeType.North) {
                            rectF = new RectF((float) rimSize, (float) modif_centerYminusDivideBy4plusRimSize,
                                    (float) modif_centerXplusMinSizeMinusRimSize, (float) modif_centerYplus0dot75minusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.South) {
                            rectF = new RectF((float) rimSize, (float) modif_centerYminus0dot75plusRimSize,
                                    (float) modif_centerXplusMinSizeMinusRimSize, (float) modif_centerYplusDivideBy4minusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.West) {
                            rectF = new RectF((float) modif_centerXminusDivideBy4plusRimSize, (float) modif_centerYminusMinSizePlusRimSize,
                                    (float) modif_centerYplus0dot75minusRimSize, (float) modif_centerYplusMinSizeMinusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.East) {
                            rectF = new RectF((float) modif_centerXminus0dot75plusRimSize, (float) modif_centerYminusMinSizePlusRimSize,
                                    (float) modif_centerXplusDivideBy4minusRimSize, (float) modif_centerYplusMinSizeMinusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.NorthEast) {
                            rectF = new RectF((float) modif_centerXminus0dot125plusRimSize, (float) modif_centerXminus0dot375plusRimSize,
                                    (float) modif_centerXminus0dot125plusRimSize, (float) modife_centerYplus0dot125minusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.NorthWest) {
                            rectF = new RectF((float) modify_centerXminus0dot375plusRimSize, (float) modif_centerXminus0dot375plusRimSize,
                                    (float) modify_centerXplus0dot125minusRimSize, (float) modife_centerYplus0dot125minusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.SouthEast) {
                            rectF = new RectF((float) modif_centerXminus0dot125plusRimSize, (float) modify_centerYminus0dot125plusRimSize,
                                    (float) modif_centerXminus0dot125plusRimSize, (float) modify_centerYplus0dot375minusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.SouthWest) {
                            rectF = new RectF((float) modify_centerXminus0dot375plusRimSize, (float) modify_centerYminus0dot125plusRimSize,
                                    (float) modify_centerXplus0dot125minusRimSize, (float) modify_centerYplus0dot375minusRimSize);
                        }
                        else {
                            rectF = new RectF((float) rimSize, (float) modif_centerYminusMinSizePlusRimSize,
                                    (float) modif_centerXplusMinSizeMinusRimSize, (float) modif_centerYplusMinSizeMinusRimSize);
                        }
                    }
                    else {
                        rimSize = gauge.mMinSize - gauge.mRimWidth - (double) ((float) (gaugeRange.offset * (gauge.mCentreY - gauge
                                .mRimWidth))) + gaugeRange.width / 2.0D;
                        //modif optimization
                        double modif_centerXplus0dot125minusRimSize   = getCenterXplus0dot125minusRimSize(rimSize);
                        double modif_centerXminus0dot125plusRimSize   = getCenterXminus0dot125plusRimSize(rimSize);
                        double modif_centerYplus0dot125minusRimSize   = getCenterYplus0dot125minusRimSize(rimSize);
                        double modif_centerYminus0dot125plusRimSize   = getCenterYminus0dot125plusRimSize(rimSize);
                        double modif_centerYplus0dot375minusRimSize   = getCenterYplus0dot375minusRimSize(rimSize);
                        double modif_centerYminus0dot375plusRimSize   = getCenterYminus0dot375plusRimSize(rimSize);
                        double modif_centerXminus0dot75plusRimSize    = getCenterXminus0dot75plusRimSize(rimSize);
                        double modif_centerYminus0dot75plusRimSize    = getCenterYminus0dot75plusRimSize(rimSize);
                        double modif_centerYplus0dot75minusRimSize    = getCenterYplus0dot75minusRimSize(rimSize);
                        double modif_centerXminusDivideBy4plusRimSize = getCenterXminusDivideBy4plusRimSize(rimSize);
                        double modif_centerXplusDivideBy4minusRimSize = getCenterXplusDivideBy4minusRimSize(rimSize);
                        double modif_centerYminusDivideBy4plusRimSize = getCenterYminusDivideBy4plusRimSize(rimSize);
                        double modif_centerYplusDivideBy4minusRimSize = getCenterYplusDivideBy4minusRimSize(rimSize);
                        double modif_centerXminusDivideBy2plusRimSize = getCenterXminusDivideBy2plusRimSize(rimSize);
                        double modif_centerXplusMinSizeMinusRimSize   = getCenterXplusDivideBy2minusRimSize(rimSize);
                        double modif_centerYplusMinSizeMinusRimSize   = getCenterYplusDivideBy2minusRimSize(rimSize);


                        if (gauge.GaugeType == GaugeType.West) {
                            rectF = new RectF((float) modif_centerXminusDivideBy4plusRimSize, (float) rimSize,
                                    (float) modif_centerYplus0dot75minusRimSize, (float) modif_centerYplusMinSizeMinusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.East) {
                            rectF = new RectF((float) modif_centerXminus0dot75plusRimSize, (float) rimSize,
                                    (float) modif_centerXplusDivideBy4minusRimSize, (float) modif_centerYplusMinSizeMinusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.North) {
                            rectF = new RectF((float) modif_centerXminusDivideBy2plusRimSize,
                                    (float) modif_centerYminusDivideBy4plusRimSize, (float) modif_centerXplusMinSizeMinusRimSize,
                                    (float) modif_centerYplus0dot75minusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.South) {
                            rectF = new RectF((float) modif_centerXminusDivideBy2plusRimSize, (float) modif_centerYminus0dot75plusRimSize,
                                    (float) modif_centerXplusMinSizeMinusRimSize, (float) modif_centerYplusDivideBy4minusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.NorthEast) {
                            rectF = new RectF((float) modif_centerXminus0dot125plusRimSize, (float) modif_centerYminus0dot375plusRimSize,
                                    (float) modif_centerXminus0dot125plusRimSize, (float) modif_centerYplus0dot125minusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.NorthWest) {
                            rectF = new RectF((float) modif_centerYminus0dot375plusRimSize, (float) modif_centerYminus0dot375plusRimSize,
                                    (float) modif_centerXplus0dot125minusRimSize, (float) modif_centerYplus0dot125minusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.SouthEast) {
                            rectF = new RectF((float) modif_centerXminus0dot125plusRimSize, (float) modif_centerYminus0dot125plusRimSize,
                                    (float) modif_centerXminus0dot125plusRimSize, (float) modif_centerYplus0dot375minusRimSize);
                        }
                        else if (gauge.GaugeType == GaugeType.SouthWest) {
                            rectF = new RectF((float) modif_centerYminus0dot375plusRimSize, (float) modif_centerYminus0dot125plusRimSize,
                                    (float) modif_centerXplus0dot125minusRimSize, (float) modif_centerYplus0dot375minusRimSize);
                        }
                        else {
                            rectF = new RectF((float) modif_centerXminusDivideBy2plusRimSize, (float) rimSize,
                                    (float) modif_centerXplusMinSizeMinusRimSize, (float) modif_centerYplusMinSizeMinusRimSize);
                        }
                    }

                    gauge.mRangeFrame = rectF;
                }
            }
        }
    }

    private double getRangeAngle(double rangeValue, GaugeScale gaugeScale) {
        if (rangeValue < gaugeScale.startValue) {
            rangeValue = gaugeScale.startValue;
        }

        if (rangeValue > gaugeScale.endValue) {
            rangeValue = gaugeScale.endValue;
        }

        double anglePartition = gaugeScale.sweepAngle / (gaugeScale.endValue - gaugeScale.startValue);
        return anglePartition * rangeValue;
    }

    private RectF calculateRectF(double radiusFactor) {
        double mMinSize         = gauge.mMinSize;
        double mInnerBevelWidth = gauge.mInnerBevelWidth;
        gauge.getClass();
        double rimSize = mMinSize - (mInnerBevelWidth - 10.0D) + gaugeScale.rimWidth / 2.0D;

        //modif optimization
        double modif_centerXplusDivideBy2minusRimSize = getCenterXplusDivideBy2minusRimSize(rimSize);
        double modif_centerYplusDivideBy2minusRimSize = getCenterYplusDivideBy2minusRimSize(rimSize);
        double modif_centerYminusDivideBy2plusRimSize = getCenterYminusDivideBy2plusRimSize(rimSize);
        double modif_centerXminusDivideBy2plusRimSize = getCenterXminusDivideBy2plusRimSize(rimSize);

        if (radiusFactor > GaugeConstants.ZERO) {
            gauge.calculateMargin(modifMinSizeDivideBy2 - gauge.mCentreX * radiusFactor,
                    modifMinSizeDivideBy2 - gauge.mCentreX * radiusFactor);
            mMinSize = gauge.mRangePathWidth - gauge.mRimWidth;
            gauge.getClass();
            rimSize = mMinSize - 4.0D * 10.0D;

            if (gauge.mCentreY > gauge.mCentreX) {
                rectF = new RectF((float) rimSize, (float) modif_centerYminusDivideBy2plusRimSize,
                        (float) modif_centerXplusDivideBy2minusRimSize, (float) modif_centerYplusDivideBy2minusRimSize);
            }
            else {
                rectF = new RectF((float) modif_centerXminusDivideBy2plusRimSize, (float) rimSize,
                        (float) modif_centerXplusDivideBy2minusRimSize, (float) modif_centerYplusDivideBy2minusRimSize);
            }

            gauge.mRangeFrame = rectF;
            float factor = gauge.mRangeFrame.left + (gauge.mRangeFrame.width() / 2.0F - gauge.mRangeFrame.left) * (float) radiusFactor;
            if (gauge.mCentreY > gauge.mCentreX) {
                rectF = new RectF(factor, (float) (getCenterYminusDivideBy2plusRimSize((double) factor)),
                        (float) (getCenterXplusDivideBy2minusRimSize((double) factor)),
                        (float) (getCenterYplusDivideBy2minusRimSize((double) factor)));
            }
            else {
                factor = gauge.mRangeFrame.top + (gauge.mRangeFrame.height() / 2.0F - gauge.mRangeFrame.top) * (float) radiusFactor;
                rectF = new RectF((float) (getCenterXminusDivideBy2plusRimSize((double) factor)), factor,
                        (float) (getCenterXplusDivideBy2minusRimSize((double) factor)),
                        (float) (getCenterYplusDivideBy2minusRimSize((double) factor)));
            }
        }
        else {
            //modif optimization
            double modif_centerYplus0dot75minusRimSize    = getCenterYplus0dot75minusRimSize(rimSize);
            double modif_centerYminusDivideBy4plusRimSize = getCenterYminusDivideBy4plusRimSize(rimSize);
            double modif_centerYminus0dot75plusRimSize    = getCenterYminus0dot75plusRimSize(rimSize);
            double modif_centerYplusDivideBy4minusRimSize = getCenterYplusDivideBy4minusRimSize(rimSize);
            double modif_centerXminus0dot125plusRimSize   = getCenterXminus0dot125plusRimSize(rimSize);
            double modif_centerYminus0dot372plusRimSize   = getCenterYminus0dot375plusRimSize(rimSize);
            double modif_centerXplus0dot375minusRimSize   = getCenterXplus0dot375minusRimSize(rimSize);
            double modif_centerYplus0dot125minusRimSize   = getCenterYplus0dot125minusRimSize(rimSize);
            double modif_centerXminus0dot375plusRimSize   = getCenterXminus0dot375plusRimSize(rimSize);
            double modif_centerYminus0dot125plusRimSize   = getCenterYminus0dot125plusRimSize(rimSize);
            double modif_centerXminus0dot75plusRimSize    = getCenterXminus0dot75plusRimSize(rimSize);
            double modif_centerXplusDivideBy4minusRimSize = getCenterXplusDivideBy4minusRimSize(rimSize);
            double modif_centerYplus0dot375minusRimSize   = getCenterYplus0dot375minusRimSize(rimSize);
            double modif_centerXplus0dot75minusRimSize    = getCenterXplus0dot75minusRimSize(rimSize);
            double modif_centerXplus0dot125minusRimSize   = getCenterXplus0dot125minusRimSize(rimSize);
            double modif_centerXminusDidiveBy4plusRimSize = getCenterXminusDivideBy4plusRimSize(rimSize);
            modif_centerXplusDivideBy2minusRimSize = getCenterXplusDivideBy2minusRimSize(rimSize);
            modif_centerYplusDivideBy2minusRimSize = getCenterYplusDivideBy2minusRimSize(rimSize);
            modif_centerYminusDivideBy2plusRimSize = getCenterYminusDivideBy2plusRimSize(rimSize);

            if (gauge.mCentreY > gauge.mCentreX) {
                if (gauge.GaugeType == GaugeType.North) {
                    rectF = new RectF((float) rimSize, (float) modif_centerYminusDivideBy4plusRimSize,
                            (float) modif_centerXplusDivideBy2minusRimSize, (float) modif_centerYplus0dot75minusRimSize);
                }
                else if (gauge.GaugeType == GaugeType.South) {
                    rectF = new RectF((float) rimSize, (float) modif_centerYminus0dot75plusRimSize,
                            (float) modif_centerXplusDivideBy2minusRimSize, (float) modif_centerYplusDivideBy4minusRimSize);
                }
                else if (gauge.GaugeType == GaugeType.West) {
                    rectF = new RectF((float) (modif_centerXminusDidiveBy4plusRimSize), (float) modif_centerYminusDivideBy2plusRimSize,
                            (float) modif_centerXplus0dot75minusRimSize, (float) modif_centerYplusDivideBy2minusRimSize);
                }
                else if (gauge.GaugeType == GaugeType.East) {
                    rectF = new RectF((float) modif_centerXminus0dot75plusRimSize, (float) modif_centerYminusDivideBy2plusRimSize,
                            (float) (modif_centerXplusDivideBy4minusRimSize), (float) modif_centerYplusDivideBy2minusRimSize);
                }
                else if (gauge.GaugeType == GaugeType.NorthEast) {
                    rectF = new RectF((float) modif_centerXminus0dot125plusRimSize, (float) modif_centerYminus0dot372plusRimSize,
                            (float) modif_centerXplus0dot375minusRimSize, (float) modif_centerYplus0dot125minusRimSize);
                }
                else if (gauge.GaugeType == GaugeType.NorthWest) {
                    rectF = new RectF((float) modif_centerXminus0dot375plusRimSize, (float) modif_centerYminus0dot372plusRimSize,
                            (float) modif_centerXplus0dot125minusRimSize, (float) modif_centerYplus0dot125minusRimSize);
                }
                else if (gauge.GaugeType == GaugeType.SouthEast) {
                    rectF = new RectF((float) modif_centerXminus0dot125plusRimSize, (float) modif_centerYminus0dot125plusRimSize,
                            (float) modif_centerXplus0dot375minusRimSize, (float) (modif_centerYplus0dot375minusRimSize));
                }
                else if (gauge.GaugeType == GaugeType.SouthWest) {
                    rectF = new RectF((float) modif_centerXminus0dot375plusRimSize, (float) modif_centerYminus0dot125plusRimSize,
                            (float) modif_centerXplus0dot125minusRimSize, (float) (modif_centerYplus0dot375minusRimSize));
                }
                else {
                    rectF = new RectF((float) rimSize, (float) modif_centerYminusDivideBy2plusRimSize,
                            (float) modif_centerXplusDivideBy2minusRimSize, (float) modif_centerYplusDivideBy2minusRimSize);
                }
            }
            else if (gauge.GaugeType == GaugeType.West) {
                rectF = new RectF((float) modif_centerXminusDidiveBy4plusRimSize, (float) rimSize,
                        (float) modif_centerXplus0dot75minusRimSize, (float) modif_centerYplusDivideBy2minusRimSize);
            }
            else if (gauge.GaugeType == GaugeType.East) {
                rectF = new RectF((float) modif_centerXminus0dot75plusRimSize, (float) rimSize,
                        (float) (modif_centerXplusDivideBy4minusRimSize), (float) modif_centerYplusDivideBy2minusRimSize);
            }
            else if (gauge.GaugeType == GaugeType.North) {
                rectF = new RectF((float) modif_centerXminusDivideBy2plusRimSize, (float) modif_centerYminusDivideBy4plusRimSize,
                        (float) modif_centerXplusDivideBy2minusRimSize, (float) modif_centerYplus0dot75minusRimSize);
            }
            else if (gauge.GaugeType == GaugeType.South) {
                rectF = new RectF((float) modif_centerXminusDivideBy2plusRimSize, (float) modif_centerYminus0dot75plusRimSize,
                        (float) modif_centerXplusDivideBy2minusRimSize, (float) modif_centerYplusDivideBy4minusRimSize);
            }
            else if (gauge.GaugeType == GaugeType.NorthEast) {
                rectF = new RectF((float) modif_centerXminus0dot125plusRimSize, (float) modif_centerYminus0dot372plusRimSize,
                        (float) modif_centerXplus0dot375minusRimSize, (float) modif_centerYplus0dot125minusRimSize);
            }
            else if (gauge.GaugeType == GaugeType.NorthWest) {
                rectF = new RectF((float) modif_centerXminus0dot375plusRimSize, (float) modif_centerYminus0dot372plusRimSize,
                        (float) modif_centerXplus0dot125minusRimSize, (float) modif_centerYplus0dot125minusRimSize);
            }
            else if (gauge.GaugeType == GaugeType.SouthEast) {
                rectF = new RectF((float) modif_centerXminus0dot125plusRimSize, (float) modif_centerYminus0dot125plusRimSize,
                        (float) modif_centerXplus0dot375minusRimSize, (float) (modif_centerYplus0dot375minusRimSize));
            }
            else if (gauge.GaugeType == GaugeType.SouthWest) {
                rectF = new RectF((float) modif_centerXminus0dot375plusRimSize, (float) modif_centerYminus0dot125plusRimSize,
                        (float) modif_centerXplus0dot125minusRimSize, (float) (modif_centerYplus0dot375minusRimSize));
            }
            else {
                rectF = new RectF((float) modif_centerXminusDivideBy2plusRimSize, (float) rimSize,
                        (float) modif_centerXplusDivideBy2minusRimSize, (float) modif_centerYplusDivideBy2minusRimSize);
            }
            gauge.mRangeFrame = rectF;
        }
        return rectF;
    }

    private double getCenterYplusDivideBy2minusRimSize(double rimSize) {
        return gauge.mCentreY + modifMinSizeDivideBy2 - rimSize;
    }

    private double getCenterXplusDivideBy2minusRimSize(double rimSize) {
        return gauge.mCentreX + modifMinSizeDivideBy2 - rimSize;
    }

    private double getCenterXplus0dot75minusRimSize(double rimSize) {
        return gauge.mCentreX + modifMinSizeMultipleBy0dot75 - rimSize;
    }

    private double getCenterXplus0dot375minusRimSize(double rimSize) {
        return gauge.mCentreX + modifMinSizeMultipleBy0dot375 - rimSize;
    }

    private double getCenterYplus0dot375minusRimSize(double rimSize) {
        return gauge.mCentreY + modifMinSizeMultipleBy0dot375 - rimSize;
    }

    private double getCenterXminus0dot375plusRimSize(double rimSize) {
        return gauge.mCentreX - modifMinSizeMultipleBy0dot375 + rimSize;
    }

    private double getCenterXminus0dot125plusRimSize(double rimSize) {
        return gauge.mCentreX - modifMinSizeMultipleBy0dot125 + rimSize;
    }

    private double getCenterXplusDivideBy4minusRimSize(double rimSize) {
        return gauge.mCentreX + modifMinSizeDivideBy4 - rimSize;
    }

    private double getCenterYminus0dot75plusRimSize(double rimSize) {
        return gauge.mCentreY - modifMinSizeMultipleBy0dot75 + rimSize;
    }

    private double getCenterYminusDivideBy4plusRimSize(double rimSize) {
        return gauge.mCentreY - modifMinSizeDivideBy4 + rimSize;
    }

    private double getCenterYminusDivideBy2plusRimSize(double rimSize) {
        return gauge.mCentreY - modifMinSizeDivideBy2 + rimSize;
    }

    private double getCenterYplus0dot75minusRimSize(double rimSize) {
        return gauge.mCentreY + modifMinSizeMultipleBy0dot75 - rimSize;
    }

    private double getCenterYminus0dot375plusRimSize(double rimSize) {
        return gauge.mCentreY - modifMinSizeMultipleBy0dot375 + rimSize;
    }

    private double getCenterYminus0dot125plusRimSize(double rimSize) {
        return gauge.mCentreY - modifMinSizeMultipleBy0dot125 + rimSize;
    }

    private double getCenterYplus0dot125minusRimSize(double rimSize) {
        return gauge.mCentreY + modifMinSizeMultipleBy0dot125 - rimSize;
    }

    private double getCenterXminusDivideBy2plusRimSize(double rimSize) {
        return gauge.mCentreX - modifMinSizeDivideBy2 + rimSize;
    }

    private double getCenterXminus0dot75plusRimSize(double rimSize) {
        return gauge.mCentreX - modifMinSizeMultipleBy0dot75 + rimSize;
    }

    private double getCenterYplusDivideBy4minusRimSize(double rimSize) {
        return gauge.mCentreY + modifMinSizeDivideBy4 - rimSize;
    }

    private double getCenterXminusDivideBy4plusRimSize(double rimSize) {
        return gauge.mCentreX - modifMinSizeDivideBy4 + rimSize;
    }

    private double getCenterXplus0dot125minusRimSize(double rimSize) {
        return gauge.mCentreX + modifMinSizeMultipleBy0dot125 - rimSize;
    }
}
