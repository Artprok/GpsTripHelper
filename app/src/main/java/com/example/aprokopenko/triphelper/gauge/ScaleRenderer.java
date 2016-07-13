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

    private TripHelperGauge gauge;
    GaugeScale gaugeScale;
    private       RectF  rectF;
    private       Paint  paint;
    private final double arcAliasing;


    public ScaleRenderer(Context context, TripHelperGauge gauge, GaugeScale gaugeScale) {
        this(context, null);
        this.gauge = gauge;
        this.gaugeScale = gaugeScale;
        this.rectF = new RectF();
        this.paint = new Paint();
    }

    public ScaleRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.gauge = null;
        this.gaugeScale = null;
        this.arcAliasing = 0.7D;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.gauge != null && this.gaugeScale != null) {
            this.gaugeScale.mGauge = this.gauge;
            if (this.gaugeScale.endValue > this.gaugeScale.startValue && this.gaugeScale.startValue < this.gaugeScale.endValue) {
                double totalTicks = (this.gaugeScale.getEndValue() - this.gaugeScale.getStartValue()) / this.gaugeScale.interval;
                this.rectF = this.calculateRectF(this.gaugeScale.radiusFactor);
                this.paint.setAntiAlias(true);
                this.paint.setColor(0);
                this.paint.setStyle(Paint.Style.STROKE);
                this.paint.setStrokeWidth((float) this.gaugeScale.rimWidth);
                this.paint.setColor(this.gaugeScale.rimColor);
                if (this.gaugeScale.showRim) {
                    canvas.drawArc(this.rectF, (float) this.gaugeScale.startAngle, (float) this.gaugeScale.sweepAngle, false, this.paint);
                }

                this.paint.setStrokeWidth(1.0F);
                this.paint.setStyle(Paint.Style.FILL);
                this.paint.setTypeface(this.gaugeScale.labelTextStyle);
                this.paint = this.getLabels(this.paint, this.gaugeScale.labelColor, (float) this.gaugeScale.labelTextSize);
                this.onDrawRanges(canvas, this.gaugeScale);
                if (this.gaugeScale.showLabels) {
                    this.onDrawLabels(canvas, this.paint, totalTicks, this.gaugeScale);
                }

                this.paint = this.getTicks(this.paint, this.gaugeScale.majorTickSettings.color, this.gaugeScale);
                if (this.gaugeScale.showTicks) {
                    this.onDrawTicks(canvas, this.paint, totalTicks, this.gaugeScale);
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
        double anglularSpace = gaugeScale.sweepAngle / totalTicks * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        double angle         = gaugeScale.startAngle * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        double var10000      = this.gauge.mInnerBevelWidth;
        this.gauge.getClass();
        double tempGaugeSize = (var10000 - 10.0D) / 2.0D;
        tempGaugeSize *= 1.0D - gaugeScale.radiusFactor;
        String label           = "";
        String value           = "";
        int    fractionalDigit = gaugeScale.numberOfDecimalDigits;
        double i               = gaugeScale.startValue;

        for (int j = 0; (double) j <= totalTicks; ++j) {
            if (fractionalDigit < 0) {
                if ((double) ((int) gaugeScale.startValue) == gaugeScale.startValue && (double) ((int) gaugeScale.interval) == gaugeScale
                        .interval) {
                    label = gaugeScale.labelPrefix + String.valueOf((int) i) + gaugeScale.labelPostfix;
                }
                else {
                    label = gaugeScale.labelPrefix + String.valueOf((float) i) + gaugeScale.labelPostfix;
                }
            }
            else {
                value = String.format("%." + String.valueOf(fractionalDigit) + "f", i);
                label = gaugeScale.labelPrefix + value + gaugeScale.labelPostfix;
            }

            paint.setAntiAlias(true);
            double widthOfLabel = (double) paint.measureText(String.valueOf((int) gaugeScale.endValue));
            double outerSize = tempGaugeSize - gaugeScale.LabelOffset * this.gauge.mCentreX - widthOfLabel / 2.0D - gaugeScale.rimWidth /
                    2.0D;
            double tempLabelSize = this.gauge.mMinSize / 2.0D - tempGaugeSize + gaugeScale.LabelOffset * this.gauge.mCentreX +
                    gaugeScale.rimWidth / 2.0D;
            double x;
            double y;
            if (this.gauge.mCentreY > this.gauge.mCentreX) {
                if (this.gauge.GaugeType == GaugeType.West) {
                    x = this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + outerSize + outerSize * Math
                            .cos(angle) + tempLabelSize + widthOfLabel / 4.0D;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.SouthWest) {
                    if (this.gauge.GaugeType == GaugeType.East) {
                        x = this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + outerSize + outerSize * Math
                                .cos(angle) + tempLabelSize + widthOfLabel / 4.0D;
                    }
                    else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.SouthEast) {
                        x = outerSize + outerSize * Math.cos(angle) + tempLabelSize + widthOfLabel / 4.0D;
                    }
                    else {
                        x = this.gauge.mCentreX - this.gauge.mMinSize * 0.875D + outerSize + outerSize * 1.5D * Math
                                .cos(angle) + tempLabelSize + widthOfLabel / 4.0D;
                    }
                }
                else {
                    x = this.gauge.mCentreX - this.gauge.mMinSize * 0.125D + outerSize + outerSize * 1.5D * Math
                            .cos(angle) + tempLabelSize + widthOfLabel / 4.0D;
                }

                if (this.gauge.GaugeType == GaugeType.North) {
                    y = this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + outerSize + outerSize * Math
                            .sin(angle) + tempLabelSize + widthOfLabel / 1.8D;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.NorthEast) {
                    if (this.gauge.GaugeType == GaugeType.South) {
                        y = this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + outerSize + outerSize * Math
                                .sin(angle) + tempLabelSize + widthOfLabel / 1.8D;
                    }
                    else if (this.gauge.GaugeType != GaugeType.SouthEast && this.gauge.GaugeType != GaugeType.SouthWest) {
                        y = this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + outerSize + outerSize * Math
                                .sin(angle) + tempLabelSize + widthOfLabel / 1.8D;
                    }
                    else {
                        y = this.gauge.mCentreY - this.gauge.mMinSize * 0.875D + outerSize + outerSize * 1.5D * Math
                                .sin(angle) + tempLabelSize + widthOfLabel / 1.8D;
                    }
                }
                else {
                    y = this.gauge.mCentreY - this.gauge.mMinSize * 0.125D + outerSize + outerSize * 1.5D * Math
                            .sin(angle) + tempLabelSize + widthOfLabel / 1.8D;
                }
            }
            else {
                if (this.gauge.GaugeType == GaugeType.West) {
                    x = this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + outerSize + outerSize * Math
                            .cos(angle) + tempLabelSize + widthOfLabel / 4.0D;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.SouthWest) {
                    if (this.gauge.GaugeType == GaugeType.East) {
                        x = this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + outerSize + outerSize * Math
                                .cos(angle) + tempLabelSize + widthOfLabel / 4.0D;
                    }
                    else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.SouthEast) {
                        x = this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + outerSize + outerSize * Math
                                .cos(angle) + tempLabelSize + widthOfLabel / 4.0D;
                    }
                    else {
                        x = this.gauge.mCentreX - this.gauge.mMinSize * 0.875D + outerSize + outerSize * 1.5D * Math
                                .cos(angle) + tempLabelSize + widthOfLabel / 4.0D;
                    }
                }
                else {
                    x = this.gauge.mCentreX - this.gauge.mMinSize * 0.125D + outerSize + outerSize * 1.5D * Math
                            .cos(angle) + tempLabelSize + widthOfLabel / 4.0D;
                }

                if (this.gauge.GaugeType == GaugeType.North) {
                    y = this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + outerSize + outerSize * Math
                            .sin(angle) + tempLabelSize + widthOfLabel / 1.8D;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.NorthEast) {
                    if (this.gauge.GaugeType == GaugeType.South) {
                        y = this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + outerSize + outerSize * Math
                                .sin(angle) + tempLabelSize + widthOfLabel / 1.8D;
                    }
                    else if (this.gauge.GaugeType != GaugeType.SouthEast && this.gauge.GaugeType != GaugeType.SouthWest) {
                        y = outerSize + outerSize * Math.sin(angle) + tempLabelSize + widthOfLabel / 1.8D;
                    }
                    else {
                        y = this.gauge.mCentreY - this.gauge.mMinSize * 0.875D + outerSize + outerSize * 1.5D * Math
                                .sin(angle) + tempLabelSize + widthOfLabel / 1.8D;
                    }
                }
                else {
                    y = this.gauge.mCentreY - this.gauge.mMinSize * 0.125D + outerSize + outerSize * 1.5D * Math
                            .sin(angle) + tempLabelSize + widthOfLabel / 1.8D;
                }
            }

            canvas.drawText(label, (float) x, (float) y, paint);
            angle += anglularSpace;
            i += gaugeScale.interval;
        }

    }

    private void onDrawTicks(Canvas canvas, Paint paint, double totalTicks, GaugeScale gaugeScale) {
        double angularSpace = gaugeScale.sweepAngle / totalTicks * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        double angle        = gaugeScale.startAngle * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        double var10000     = this.gauge.mInnerBevelWidth;
        this.gauge.getClass();
        double tempGaugeSize = (var10000 - 10.0D) / 2.0D;
        tempGaugeSize *= 1.0D - gaugeScale.radiusFactor;

        double tickLength1;
        double minorTickPosition;
        double outerSize;
        double x2;
        double y2;
        double innerSize;
        for (int minorTicksCount = 0; (double) minorTicksCount <= totalTicks; ++minorTicksCount) {
            paint.setAntiAlias(true);
            paint.setStrokeWidth((float) gaugeScale.majorTickSettings.width);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(gaugeScale.majorTickSettings.color);
            double arcAliasing = 0.7D;
            tickLength1 = tempGaugeSize - gaugeScale.majorTickSettings.offset * this.gauge.mCentreX + arcAliasing * gaugeScale.rimWidth -
                    gaugeScale.rimWidth / 2.0D;
            double tickLength = this.gauge.mMinSize / 2.0D - tempGaugeSize + gaugeScale.majorTickSettings.offset * this.gauge.mCentreX -
                    arcAliasing * gaugeScale.rimWidth + gaugeScale.rimWidth / 2.0D;
            if (this.gauge.mCentreY > this.gauge.mCentreX) {
                if (this.gauge.GaugeType == GaugeType.West) {
                    minorTickPosition = this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + tickLength1 + tickLength1 * Math
                            .cos(angle) + tickLength;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.SouthWest) {
                    if (this.gauge.GaugeType == GaugeType.East) {
                        minorTickPosition = this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + tickLength1 + tickLength1 * Math
                                .cos(angle) + tickLength;
                    }
                    else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.SouthEast) {
                        minorTickPosition = tickLength1 + tickLength1 * Math.cos(angle) + tickLength;
                    }
                    else {
                        minorTickPosition = this.gauge.mCentreX - this.gauge.mMinSize * 0.875D + tickLength1 + tickLength1 * 1.5D * Math
                                .cos(angle) + tickLength;
                    }
                }
                else {
                    minorTickPosition = this.gauge.mCentreX - this.gauge.mMinSize * 0.125D + tickLength1 + tickLength1 * 1.5D * Math
                            .cos(angle) + tickLength;
                }

                if (this.gauge.GaugeType == GaugeType.North) {
                    outerSize = this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.NorthEast) {
                    if (this.gauge.GaugeType == GaugeType.South) {
                        outerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + tickLength1 + tickLength1 * Math
                                .sin(angle) + tickLength;
                    }
                    else if (this.gauge.GaugeType != GaugeType.SouthEast && this.gauge.GaugeType != GaugeType.SouthWest) {
                        outerSize = this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + tickLength1 + tickLength1 * Math
                                .sin(angle) + tickLength;
                    }
                    else {
                        outerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.875D + tickLength1 + tickLength1 * 1.5D * Math
                                .sin(angle) + tickLength;
                    }
                }
                else {
                    outerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.125D + tickLength1 + tickLength1 * 1.5D * Math
                            .sin(angle) + tickLength;
                }
            }
            else {
                if (this.gauge.GaugeType == GaugeType.West) {
                    minorTickPosition = this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + tickLength1 + tickLength1 * Math
                            .cos(angle) + tickLength;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.SouthWest) {
                    if (this.gauge.GaugeType == GaugeType.East) {
                        minorTickPosition = this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + tickLength1 + tickLength1 * Math
                                .cos(angle) + tickLength;
                    }
                    else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.SouthEast) {
                        minorTickPosition = this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + tickLength1 + tickLength1 * Math
                                .cos(angle) + tickLength;
                    }
                    else {
                        minorTickPosition = this.gauge.mCentreX - this.gauge.mMinSize * 0.875D + tickLength1 + tickLength1 * 1.5D * Math
                                .cos(angle) + tickLength;
                    }
                }
                else {
                    minorTickPosition = this.gauge.mCentreX - this.gauge.mMinSize * 0.125D + tickLength1 + tickLength1 * 1.5D * Math
                            .cos(angle) + tickLength;
                }

                if (this.gauge.GaugeType == GaugeType.North) {
                    outerSize = this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.NorthEast) {
                    if (this.gauge.GaugeType == GaugeType.South) {
                        outerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + tickLength1 + tickLength1 * Math
                                .sin(angle) + tickLength;
                    }
                    else if (this.gauge.GaugeType != GaugeType.SouthEast && this.gauge.GaugeType != GaugeType.SouthWest) {
                        outerSize = tickLength1 + tickLength1 * Math.sin(angle) + tickLength;
                    }
                    else {
                        outerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.875D + tickLength1 + tickLength1 * 1.5D * Math
                                .sin(angle) + tickLength;
                    }
                }
                else {
                    outerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.125D + tickLength1 + tickLength1 * 1.5D * Math
                            .sin(angle) + tickLength;
                }
            }

            x2 = 0.0D;
            x2 = tempGaugeSize - gaugeScale.majorTickSettings.size - gaugeScale.majorTickSettings.offset * this.gauge.mCentreX +
                    arcAliasing * gaugeScale.rimWidth - gaugeScale.rimWidth / 2.0D;
            tickLength = this.gauge.mMinSize / 2.0D - tempGaugeSize + gaugeScale.majorTickSettings.size + gaugeScale.majorTickSettings
                    .offset * this.gauge.mCentreX - arcAliasing * gaugeScale.rimWidth + gaugeScale.rimWidth / 2.0D;
            if (this.gauge.mCentreY > this.gauge.mCentreX) {
                if (this.gauge.GaugeType == GaugeType.West) {
                    y2 = this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + x2 + x2 * Math.cos(angle) + tickLength;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.SouthWest) {
                    if (this.gauge.GaugeType == GaugeType.East) {
                        y2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + x2 + x2 * Math.cos(angle) + tickLength;
                    }
                    else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.SouthEast) {
                        y2 = x2 + x2 * Math.cos(angle) + tickLength;
                    }
                    else {
                        y2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.875D + x2 + x2 * 1.5D * Math.cos(angle) + tickLength;
                    }
                }
                else {
                    y2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.125D + x2 + x2 * 1.5D * Math.cos(angle) + tickLength;
                }

                if (this.gauge.GaugeType == GaugeType.North) {
                    innerSize = this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + x2 + x2 * Math.sin(angle) + tickLength;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.NorthEast) {
                    if (this.gauge.GaugeType == GaugeType.South) {
                        innerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + x2 + x2 * Math.sin(angle) + tickLength;
                    }
                    else if (this.gauge.GaugeType != GaugeType.SouthEast && this.gauge.GaugeType != GaugeType.SouthWest) {
                        innerSize = this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + x2 + x2 * Math.sin(angle) + tickLength;
                    }
                    else {
                        innerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.875D + x2 + x2 * 1.5D * Math.sin(angle) + tickLength;
                    }
                }
                else {
                    innerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.125D + x2 + x2 * 1.5D * Math.sin(angle) + tickLength;
                }
            }
            else {
                if (this.gauge.GaugeType == GaugeType.West) {
                    y2 = this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + x2 + x2 * Math.cos(angle) + tickLength;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.SouthWest) {
                    if (this.gauge.GaugeType == GaugeType.East) {
                        y2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + x2 + x2 * Math.cos(angle) + tickLength;
                    }
                    else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.SouthEast) {
                        y2 = this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + x2 + x2 * Math.cos(angle) + tickLength;
                    }
                    else {
                        y2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.875D + x2 + x2 * 1.5D * Math.cos(angle) + tickLength;
                    }
                }
                else {
                    y2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.125D + x2 + x2 * 1.5D * Math.cos(angle) + tickLength;
                }

                if (this.gauge.GaugeType == GaugeType.North) {
                    innerSize = this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + x2 + x2 * Math.sin(angle) + tickLength;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.NorthEast) {
                    if (this.gauge.GaugeType == GaugeType.South) {
                        innerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + x2 + x2 * Math.sin(angle) + tickLength;
                    }
                    else if (this.gauge.GaugeType != GaugeType.SouthEast && this.gauge.GaugeType != GaugeType.SouthWest) {
                        innerSize = x2 + x2 * Math.sin(angle) + tickLength;
                    }
                    else {
                        innerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.875D + x2 + x2 * 1.5D * Math.sin(angle) + tickLength;
                    }
                }
                else {
                    innerSize = this.gauge.mCentreY - this.gauge.mMinSize * 0.125D + x2 + x2 * 1.5D * Math.sin(angle) + tickLength;
                }
            }

            canvas.drawLine((float) y2, (float) innerSize, (float) minorTickPosition, (float) outerSize, paint);
            angle += angularSpace;
        }

        angle = gaugeScale.startAngle * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        double var33          = totalTicks * gaugeScale.minorTicksPerInterval;
        double minorTickAngle = angularSpace / (gaugeScale.minorTicksPerInterval + 1.0D);
        paint.setStrokeWidth((float) gaugeScale.minorTickSettings.width);

        for (int i = 1; (double) i <= var33; ++i) {
            angle += minorTickAngle;
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(gaugeScale.minorTickSettings.color);
            minorTickPosition = GaugeConstants.ZERO;
            outerSize = tempGaugeSize - gaugeScale.minorTickSettings.offset * this.gauge.mCentreX + this.arcAliasing * gaugeScale
                    .rimWidth - minorTickPosition - gaugeScale.rimWidth / 2.0D;
            tickLength1 = this.gauge.mMinSize / 2.0D - tempGaugeSize + gaugeScale.minorTickSettings.offset * this.gauge.mCentreX - this
                    .arcAliasing * gaugeScale.rimWidth + minorTickPosition + gaugeScale.rimWidth / 2.0D;
            if (this.gauge.mCentreY > this.gauge.mCentreX) {
                if (this.gauge.GaugeType == GaugeType.West) {
                    x2 = this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + outerSize + outerSize * Math.cos(angle) + tickLength1;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.SouthWest) {
                    if (this.gauge.GaugeType == GaugeType.East) {
                        x2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + outerSize + outerSize * Math.cos(angle) + tickLength1;
                    }
                    else if (this.gauge.GaugeType != GaugeType.SouthEast && this.gauge.GaugeType != GaugeType.NorthEast) {
                        x2 = outerSize + outerSize * Math.cos(angle) + tickLength1;
                    }
                    else {
                        x2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.875D + outerSize + outerSize * 1.5D * Math
                                .cos(angle) + tickLength1;
                    }
                }
                else {
                    x2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.125D + outerSize + outerSize * 1.5D * Math.cos(angle) + tickLength1;
                }

                if (this.gauge.GaugeType == GaugeType.North) {
                    y2 = this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + outerSize + outerSize * Math.sin(angle) + tickLength1;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.NorthEast) {
                    if (this.gauge.GaugeType == GaugeType.South) {
                        y2 = this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + outerSize + outerSize * Math.sin(angle) + tickLength1;
                    }
                    else if (this.gauge.GaugeType != GaugeType.SouthWest && this.gauge.GaugeType != GaugeType.SouthEast) {
                        y2 = this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + outerSize + outerSize * Math.sin(angle) + tickLength1;
                    }
                    else {
                        y2 = this.gauge.mCentreY - this.gauge.mMinSize * 0.875D + outerSize + outerSize * 1.5D * Math
                                .sin(angle) + tickLength1;
                    }
                }
                else {
                    y2 = this.gauge.mCentreY - this.gauge.mMinSize * 0.125D + outerSize + outerSize * 1.5D * Math.sin(angle) + tickLength1;
                }
            }
            else {
                if (this.gauge.GaugeType == GaugeType.West) {
                    x2 = this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + outerSize + outerSize * Math.cos(angle) + tickLength1;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.SouthWest) {
                    if (this.gauge.GaugeType == GaugeType.East) {
                        x2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + outerSize + outerSize * Math.cos(angle) + tickLength1;
                    }
                    else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.SouthEast) {
                        x2 = this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + outerSize + outerSize * Math.cos(angle) + tickLength1;
                    }
                    else {
                        x2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.875D + outerSize + outerSize * 1.5D * Math
                                .cos(angle) + tickLength1;
                    }
                }
                else {
                    x2 = this.gauge.mCentreX - this.gauge.mMinSize * 0.125D + outerSize + outerSize * 1.5D * Math.cos(angle) + tickLength1;
                }

                if (this.gauge.GaugeType == GaugeType.North) {
                    y2 = this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + outerSize + outerSize * Math.sin(angle) + tickLength1;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.NorthWest) {
                    if (this.gauge.GaugeType == GaugeType.South) {
                        y2 = this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + outerSize + outerSize * Math.sin(angle) + tickLength1;
                    }
                    else if (this.gauge.GaugeType != GaugeType.SouthWest && this.gauge.GaugeType != GaugeType.SouthEast) {
                        y2 = outerSize + outerSize * Math.sin(angle) + tickLength1;
                    }
                    else {
                        y2 = this.gauge.mCentreY - this.gauge.mMinSize * 0.875D + outerSize + outerSize * 1.5D * Math
                                .sin(angle) + tickLength1;
                    }
                }
                else {
                    y2 = this.gauge.mCentreY - this.gauge.mMinSize * 0.125D + outerSize + outerSize * 1.5D * Math.sin(angle) + tickLength1;
                }
            }

            innerSize = GaugeConstants.ZERO;
            innerSize = tempGaugeSize - gaugeScale.minorTickSettings.size - gaugeScale.minorTickSettings.offset * this.gauge.mCentreX +
                    this.arcAliasing * gaugeScale.rimWidth - minorTickPosition - gaugeScale.rimWidth / 2.0D;
            tickLength1 = this.gauge.mMinSize / 2.0D - tempGaugeSize + gaugeScale.minorTickSettings.size + gaugeScale.minorTickSettings
                    .offset * this.gauge.mCentreX - this.arcAliasing * gaugeScale.rimWidth + minorTickPosition +
                    gaugeScale.rimWidth / 2.0D;
            double x1;
            double y1;
            if (this.gauge.mCentreY > this.gauge.mCentreX) {
                if (this.gauge.GaugeType == GaugeType.West) {
                    x1 = this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + innerSize + innerSize * Math.cos(angle) + tickLength1;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.SouthWest) {
                    if (this.gauge.GaugeType == GaugeType.East) {
                        x1 = this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + innerSize + innerSize * Math.cos(angle) + tickLength1;
                    }
                    else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.SouthEast) {
                        x1 = innerSize + innerSize * Math.cos(angle) + tickLength1;
                    }
                    else {
                        x1 = this.gauge.mCentreX - this.gauge.mMinSize * 0.875D + innerSize + innerSize * 1.5D * Math
                                .cos(angle) + tickLength1;
                    }
                }
                else {
                    x1 = this.gauge.mCentreX - this.gauge.mMinSize * 0.125D + innerSize + innerSize * 1.5D * Math.cos(angle) + tickLength1;
                }

                if (this.gauge.GaugeType == GaugeType.North) {
                    y1 = this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + innerSize + innerSize * Math.sin(angle) + tickLength1;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.NorthWest) {
                    if (this.gauge.GaugeType == GaugeType.South) {
                        y1 = this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + innerSize + innerSize * Math.sin(angle) + tickLength1;
                    }
                    else if (this.gauge.GaugeType != GaugeType.SouthWest && this.gauge.GaugeType != GaugeType.SouthEast) {
                        y1 = this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + innerSize + innerSize * Math.sin(angle) + tickLength1;
                    }
                    else {
                        y1 = this.gauge.mCentreY - this.gauge.mMinSize * 0.875D + innerSize + innerSize * 1.5D * Math
                                .sin(angle) + tickLength1;
                    }
                }
                else {
                    y1 = this.gauge.mCentreY - this.gauge.mMinSize * 0.125D + innerSize + innerSize * 1.5D * Math.sin(angle) + tickLength1;
                }
            }
            else {
                if (this.gauge.GaugeType == GaugeType.West) {
                    x1 = this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + innerSize + innerSize * Math.cos(angle) + tickLength1;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthWest && this.gauge.GaugeType != GaugeType.SouthWest) {
                    if (this.gauge.GaugeType == GaugeType.East) {
                        x1 = this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + innerSize + innerSize * Math.cos(angle) + tickLength1;
                    }
                    else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.SouthEast) {
                        x1 = this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + innerSize + innerSize * Math.cos(angle) + tickLength1;
                    }
                    else {
                        x1 = this.gauge.mCentreX - this.gauge.mMinSize * 0.875D + innerSize + innerSize * 1.5D * Math
                                .cos(angle) + tickLength1;
                    }
                }
                else {
                    x1 = this.gauge.mCentreX - this.gauge.mMinSize * 0.125D + innerSize + innerSize * 1.5D * Math.cos(angle) + tickLength1;
                }

                if (this.gauge.GaugeType == GaugeType.North) {
                    y1 = this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + innerSize + innerSize * Math.sin(angle) + tickLength1;
                }
                else if (this.gauge.GaugeType != GaugeType.NorthEast && this.gauge.GaugeType != GaugeType.NorthWest) {
                    if (this.gauge.GaugeType == GaugeType.South) {
                        y1 = this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + innerSize + innerSize * Math.sin(angle) + tickLength1;
                    }
                    else if (this.gauge.GaugeType != GaugeType.SouthWest && this.gauge.GaugeType != GaugeType.SouthEast) {
                        y1 = innerSize + innerSize * Math.sin(angle) + tickLength1;
                    }
                    else {
                        y1 = this.gauge.mCentreY - this.gauge.mMinSize * 0.875D + innerSize + innerSize * 1.5D * Math
                                .sin(angle) + tickLength1;
                    }
                }
                else {
                    y1 = this.gauge.mCentreY - this.gauge.mMinSize * 0.125D + innerSize + innerSize * 1.5D * Math.sin(angle) + tickLength1;
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
                    .drawArc(this.rectF, (float) startArc, (float) endtArc, false, paint)) {
                GaugeRange gaugeRange = (GaugeRange) i$.next();
                gaugeRange.mGauge = gaugeScale.mGauge;
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth((float) gaugeRange.width);
                paint.setColor(gaugeRange.color);
                startArc = gaugeScale.startAngle + this.getRangeAngle(gaugeRange.startValue, gaugeScale) - this
                        .getRangeAngle(gaugeScale.startValue, gaugeScale);
                endtArc = this.getRangeAngle(gaugeRange.endValue, gaugeScale) - this.getRangeAngle(gaugeRange.startValue, gaugeScale);
                this.gauge.calculateMargin(this.gauge.mInnerBevelWidth - this.gauge.mInnerBevelWidth * gaugeScale.radiusFactor,
                        this.gauge.mInnerBevelWidth - this.gauge.mInnerBevelWidth * gaugeScale.radiusFactor);
                double var10000;
                double rimSize;
                if (gaugeScale.radiusFactor > 0.0D) {
                    this.gauge.calculateMargin(this.gauge.mMinSize - this.gauge.mMinSize * gaugeScale.radiusFactor,
                            this.gauge.mMinSize - this.gauge.mMinSize * gaugeScale.radiusFactor);
                    var10000 = this.gauge.mRangePathWidth - this.gauge.mRimWidth;
                    this.gauge.getClass();
                    rimSize = var10000 - 4.0D * 10.0D;
                    if (this.gauge.mCentreY > this.gauge.mCentreX) {
                        this.rectF = new RectF((float) rimSize, (float) (this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + rimSize),
                                (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                                (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
                    }
                    else {
                        this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + rimSize), (float) rimSize,
                                (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                                (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
                    }

                    this.gauge.mRangeFrame = this.rectF;
                    float factor = this.gauge.mRangeFrame.left + (this.gauge.mRangeFrame
                            .width() / 2.0F - this.gauge.mRangeFrame.left) * (float) gaugeScale.radiusFactor + (float) (gaugeRange.offset
                            * (this.gauge.mCentreX - this.gauge.mRimWidth));
                    if (this.gauge.mCentreY > this.gauge.mCentreX) {
                        this.rectF = new RectF(factor, (float) (this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + (double) factor),
                                (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - (double) factor),
                                (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - (double) factor));
                    }
                    else {
                        factor = this.gauge.mRangeFrame.top + (this.gauge.mRangeFrame
                                .height() / 2.0F - this.gauge.mRangeFrame.top) * (float) gaugeScale.radiusFactor + (float) (gaugeRange
                                .offset * (this.gauge.mCentreY - this.gauge.mRimWidth));
                        this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + (double) factor), factor,
                                (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - (double) factor),
                                (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - (double) factor));
                    }
                }
                else {
                    if (this.gauge.mCentreY > this.gauge.mCentreX) {
                        rimSize = this.gauge.mMinSize - this.gauge.mRimWidth - (double) ((float) (gaugeRange.offset * (this.gauge
                                .mCentreX - this.gauge.mRimWidth))) + gaugeRange.width / 2.0D;
                        if (this.gauge.GaugeType == GaugeType.North) {
                            this.rectF = new RectF((float) rimSize, (float) (this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.75D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.South) {
                            this.rectF = new RectF((float) rimSize, (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize / 4.0D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.West) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.75D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.East) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize / 4.0D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.NorthEast) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 1.125D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.375D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.375D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize * 1.125D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.NorthWest) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.375D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.375D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize * 1.125D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize * 1.125D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.SouthEast) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 1.125D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize * 1.125D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.375D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.375D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.SouthWest) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.375D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize * 1.125D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize * 1.125D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.375D - rimSize));
                        }
                        else {
                            this.rectF = new RectF((float) rimSize, (float) (this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
                        }
                    }
                    else {
                        rimSize = this.gauge.mMinSize - this.gauge.mRimWidth - (double) ((float) (gaugeRange.offset * (this.gauge
                                .mCentreY - this.gauge.mRimWidth))) + gaugeRange.width / 2.0D;
                        if (this.gauge.GaugeType == GaugeType.West) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + rimSize), (float) rimSize,
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.75D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.East) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + rimSize), (float) rimSize,
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize / 4.0D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.North) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.75D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.South) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize / 4.0D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.NorthEast) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 1.125D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.375D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.375D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize * 1.125D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.NorthWest) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.375D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.375D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize * 1.125D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize * 1.125D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.SouthEast) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 1.125D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize * 1.125D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.375D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.375D - rimSize));
                        }
                        else if (this.gauge.GaugeType == GaugeType.SouthWest) {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.375D + rimSize),
                                    (float) (this.gauge.mCentreY - this.gauge.mMinSize * 1.125D + rimSize),
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize * 1.125D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.375D - rimSize));
                        }
                        else {
                            this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + rimSize), (float) rimSize,
                                    (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                                    (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
                        }
                    }

                    this.gauge.mRangeFrame = this.rectF;
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
        double var10000 = this.gauge.mMinSize;
        double var10001 = this.gauge.mInnerBevelWidth;
        this.gauge.getClass();
        double rimSize = var10000 - (var10001 - 10.0D) + this.gaugeScale.rimWidth / 2.0D;
        if (radiusFactor > GaugeConstants.ZERO) {
            this.gauge.calculateMargin(this.gauge.mMinSize / 2.0D - this.gauge.mCentreX * radiusFactor,
                    this.gauge.mMinSize / 2.0D - this.gauge.mCentreX * radiusFactor);
            var10000 = this.gauge.mRangePathWidth - this.gauge.mRimWidth;
            this.gauge.getClass();
            rimSize = var10000 - 4.0D * 10.0D;
            if (this.gauge.mCentreY > this.gauge.mCentreX) {
                this.rectF = new RectF((float) rimSize, (float) (this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + rimSize),
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
            }
            else {
                this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + rimSize), (float) rimSize,
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
            }

            this.gauge.mRangeFrame = this.rectF;
            float factor = this.gauge.mRangeFrame.left + (this.gauge.mRangeFrame
                    .width() / 2.0F - this.gauge.mRangeFrame.left) * (float) radiusFactor;
            if (this.gauge.mCentreY > this.gauge.mCentreX) {
                this.rectF = new RectF(factor, (float) (this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + (double) factor),
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - (double) factor),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - (double) factor));
            }
            else {
                factor = this.gauge.mRangeFrame.top + (this.gauge.mRangeFrame
                        .height() / 2.0F - this.gauge.mRangeFrame.top) * (float) radiusFactor;
                this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + (double) factor), factor,
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - (double) factor),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - (double) factor));
            }
        }
        else {
            if (this.gauge.mCentreY > this.gauge.mCentreX) {
                if (this.gauge.GaugeType == GaugeType.North) {
                    this.rectF = new RectF((float) rimSize, (float) (this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + rimSize),
                            (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                            (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.75D - rimSize));
                }
                else if (this.gauge.GaugeType == GaugeType.South) {
                    this.rectF = new RectF((float) rimSize, (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + rimSize),
                            (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                            (float) (this.gauge.mCentreY + this.gauge.mMinSize / 4.0D - rimSize));
                }
                else if (this.gauge.GaugeType == GaugeType.West) {
                    this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + rimSize),
                            (float) (this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + rimSize),
                            (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.75D - rimSize),
                            (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
                }
                else if (this.gauge.GaugeType == GaugeType.East) {
                    this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + rimSize),
                            (float) (this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + rimSize),
                            (float) (this.gauge.mCentreX + this.gauge.mMinSize / 4.0D - rimSize),
                            (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
                }
                else if (this.gauge.GaugeType == GaugeType.NorthEast) {
                    this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 1.125D + rimSize),
                            (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.375D + rimSize),
                            (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.375D - rimSize),
                            (float) (this.gauge.mCentreY + this.gauge.mMinSize * 1.125D - rimSize));
                }
                else if (this.gauge.GaugeType == GaugeType.NorthWest) {
                    this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.375D + rimSize),
                            (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.375D + rimSize),
                            (float) (this.gauge.mCentreX + this.gauge.mMinSize * 1.125D - rimSize),
                            (float) (this.gauge.mCentreY + this.gauge.mMinSize * 1.125D - rimSize));
                }
                else if (this.gauge.GaugeType == GaugeType.SouthEast) {
                    this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 1.125D + rimSize),
                            (float) (this.gauge.mCentreY - this.gauge.mMinSize * 1.125D + rimSize),
                            (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.375D - rimSize),
                            (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.375D - rimSize));
                }
                else if (this.gauge.GaugeType == GaugeType.SouthWest) {
                    this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.375D + rimSize),
                            (float) (this.gauge.mCentreY - this.gauge.mMinSize * 1.125D + rimSize),
                            (float) (this.gauge.mCentreX + this.gauge.mMinSize * 1.125D - rimSize),
                            (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.375D - rimSize));
                }
                else {
                    this.rectF = new RectF((float) rimSize, (float) (this.gauge.mCentreY - this.gauge.mMinSize / 2.0D + rimSize),
                            (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                            (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
                }
            }
            else if (this.gauge.GaugeType == GaugeType.West) {
                this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 4.0D + rimSize), (float) rimSize,
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.75D - rimSize),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
            }
            else if (this.gauge.GaugeType == GaugeType.East) {
                this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.75D + rimSize), (float) rimSize,
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize / 4.0D - rimSize),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
            }
            else if (this.gauge.GaugeType == GaugeType.North) {
                this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + rimSize),
                        (float) (this.gauge.mCentreY - this.gauge.mMinSize / 4.0D + rimSize),
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.75D - rimSize));
            }
            else if (this.gauge.GaugeType == GaugeType.South) {
                this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + rimSize),
                        (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.75D + rimSize),
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize / 4.0D - rimSize));
            }
            else if (this.gauge.GaugeType == GaugeType.NorthEast) {
                this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 1.125D + rimSize),
                        (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.375D + rimSize),
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.375D - rimSize),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize * 1.125D - rimSize));
            }
            else if (this.gauge.GaugeType == GaugeType.NorthWest) {
                this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.375D + rimSize),
                        (float) (this.gauge.mCentreY - this.gauge.mMinSize * 0.375D + rimSize),
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize * 1.125D - rimSize),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize * 1.125D - rimSize));
            }
            else if (this.gauge.GaugeType == GaugeType.SouthEast) {
                this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 1.125D + rimSize),
                        (float) (this.gauge.mCentreY - this.gauge.mMinSize * 1.125D + rimSize),
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize * 0.375D - rimSize),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.375D - rimSize));
            }
            else if (this.gauge.GaugeType == GaugeType.SouthWest) {
                this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize * 0.375D + rimSize),
                        (float) (this.gauge.mCentreY - this.gauge.mMinSize * 1.125D + rimSize),
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize * 1.125D - rimSize),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize * 0.375D - rimSize));
            }
            else {
                this.rectF = new RectF((float) (this.gauge.mCentreX - this.gauge.mMinSize / 2.0D + rimSize), (float) rimSize,
                        (float) (this.gauge.mCentreX + this.gauge.mMinSize / 2.0D - rimSize),
                        (float) (this.gauge.mCentreY + this.gauge.mMinSize / 2.0D - rimSize));
            }

            this.gauge.mRangeFrame = this.rectF;
        }

        return this.rectF;
    }
}
