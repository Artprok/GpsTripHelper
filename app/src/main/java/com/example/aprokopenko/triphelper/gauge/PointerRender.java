package com.example.aprokopenko.triphelper.gauge;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.aprokopenko.triphelper.gauge.enums.GaugeType;
import com.example.aprokopenko.triphelper.gauge.enums.NeedleType;

public class PointerRender extends View {

    GaugePointer    mGaugePointer;
    private GaugeScale      mGaugeScale;
    private boolean         mEnableAnimation;
    private TripHelperGauge mGauge;
    private Paint           paint;
    private RectF           rectF;

    float           value;

    public PointerRender(Context context, TripHelperGauge mGauge, GaugeScale mGaugeScale, GaugePointer mGaugePointer) {
        this(context, null);
        if (mGauge != null) {
            this.mGauge = mGauge;
        }
        if (mGaugeScale != null) {
            this.mGaugeScale = mGaugeScale;
        }
        if (mGaugePointer != null) {
            this.mGaugePointer = mGaugePointer;
            this.value = (float) mGaugePointer.value;
            if (mGaugePointer instanceof NeedlePointer) {
                this.mGauge.mKnobDiameter = ((NeedlePointer) mGaugePointer).knobRadius;
            }
        }
    }

    public PointerRender(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.value = 0.0f;
        this.mEnableAnimation = true;
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
        invalidate();
    }

    public boolean ismEnableAnimation() {
        return this.mEnableAnimation;
    }

    public void setmEnableAnimation(boolean mEnableAnimation) {
        this.mEnableAnimation = mEnableAnimation;
    }

    protected void onDraw(Canvas canvas) {
        if (!(this.mGauge == null || this.mGaugeScale == null || this.mGaugePointer == null)) {
            this.mGauge.calculateMargin(this.mGauge.mInnerBevelWidth - (this.mGauge.mInnerBevelWidth * this.mGaugeScale.radiusFactor),
                    this.mGauge.mInnerBevelWidth - (this.mGauge.mInnerBevelWidth * this.mGaugeScale.radiusFactor));
            double rimSize = this.mGauge.mMinSize - this.mGauge.mRimWidth;
            if (this.mGaugeScale.radiusFactor > GaugeConstants.ZERO) {
                this.mGauge.calculateMargin(this.mGauge.mMinSize - (this.mGauge.mMinSize * this.mGaugeScale.radiusFactor),
                        this.mGauge.mMinSize - (this.mGauge.mMinSize * this.mGaugeScale.radiusFactor));
                double d = this.mGauge.mRangePathWidth - this.mGauge.mRimWidth;
                this.mGauge.getClass();
                rimSize = d - (4.0d * 10.0d);
                if (this.mGauge.mCentreY > this.mGauge.mCentreX) {
                    if (this.mGauge.GaugeType == GaugeType.North) {
                        this.rectF = new RectF((float) rimSize, (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.75d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.South) {
                        this.rectF = new RectF((float) rimSize, (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.75d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.West) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 2.0d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.75d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.75d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.East) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.75d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 2.0d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 4.0d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.NorthEast) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.NorthWest) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.SouthEast) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.SouthWest) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                    }
                    else {
                        this.rectF = new RectF((float) rimSize, (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 2.0d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - rimSize));
                    }
                }
                else if (this.mGauge.GaugeType == GaugeType.West) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)) + rimSize), (float) rimSize,
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.75d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.East) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.75d)) + rimSize), (float) rimSize,
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 4.0d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.North) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 2.0d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.75d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.South) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 2.0d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.75d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.NorthEast) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.NorthWest) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.SouthEast) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.SouthWest) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                }
                else {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 2.0d)) + rimSize), (float) rimSize,
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - rimSize));
                }
                this.mGauge.mRangeFrame = this.rectF;
                float rangePointerPoisition = 0.0f;
                if (this.mGaugePointer instanceof RangePointer) {
                    rangePointerPoisition = (float) ((RangePointer) this.mGaugePointer).offset;
                }
                float factor = (this.mGauge.mRangeFrame.left + (((this.mGauge.mRangeFrame
                        .width() / 2.0f) - this.mGauge.mRangeFrame.left) * ((float) this.mGaugeScale.radiusFactor))) + ((float) ((
                        (double) rangePointerPoisition) * (this.mGauge.mCentreX - this.mGauge.mRimWidth)));
                if (this.mGauge.mCentreY <= this.mGauge.mCentreX) {
                    factor = (this.mGauge.mRangeFrame.top + (((this.mGauge.mRangeFrame
                            .height() / 2.0f) - this.mGauge.mRangeFrame.top) * ((float) this.mGaugeScale.radiusFactor))) + ((float) ((
                            (double) rangePointerPoisition) * (this.mGauge.mCentreY - this.mGauge.mRimWidth)));
                    if (this.mGauge.GaugeType == GaugeType.West) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)) + ((double) factor)), factor,
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.75d)) - ((double) factor)));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.East) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.75d)) + ((double) factor)),
                                factor, (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)) - ((double) factor)));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.North) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 2.0d)) + ((double) factor)),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)) + ((double) factor)),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.75d)) - ((double) factor)));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.South) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 2.0d)) + ((double) factor)),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.75d)) + ((double) factor)),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)) - ((double) factor)));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.NorthEast) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.NorthWest) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.SouthEast) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.SouthWest) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                    }
                    else {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 2.0d)) + ((double) factor)), factor,
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)));
                    }
                }
                else if (this.mGauge.GaugeType == GaugeType.North) {
                    this.rectF = new RectF(factor, (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)) + ((double) factor)),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.75d)) - ((double) factor)));
                }
                else if (this.mGauge.GaugeType == GaugeType.South) {
                    this.rectF = new RectF(factor, (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.75d)) + ((double) factor)),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)) - ((double) factor)));
                }
                else if (this.mGauge.GaugeType == GaugeType.West) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)) + ((double) factor)),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 2.0d)) + ((double) factor)),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.75d)) - ((double) factor)),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)));
                }
                else if (this.mGauge.GaugeType == GaugeType.East) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.75d)) + ((double) factor)),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 2.0d)) + ((double) factor)),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 4.0d)) - ((double) factor)),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)));
                }
                else if (this.mGauge.GaugeType == GaugeType.NorthEast) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.NorthWest) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.SouthEast) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.SouthWest) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                }
                else {
                    this.rectF = new RectF(factor, (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 2.0d)) + ((double) factor)),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - ((double) factor)));
                }
            }
            else {
                if (this.mGaugePointer instanceof RangePointer) {
                    rimSize = (this.mGauge.mMinSize - this.mGauge.mRimWidth) - ((double) ((float) ((((double) ((float) ((RangePointer)
                            this.mGaugePointer).offset)) * (this.mGauge.mCentreX - this.mGauge.mRimWidth)) - (this.mGaugePointer.width /
                            2.0d))));
                }
                if (this.mGauge.mCentreY > this.mGauge.mCentreX) {
                    if (this.mGauge.GaugeType == GaugeType.North) {
                        this.rectF = new RectF((float) rimSize, (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.75d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.South) {
                        this.rectF = new RectF((float) rimSize, (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.75d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.West) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 2.0d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.75d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.East) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.75d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 2.0d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 4.0d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.NorthEast) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.NorthWest) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.SouthEast) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                    }
                    else if (this.mGauge.GaugeType == GaugeType.SouthWest) {
                        this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                                (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                    }
                    else {
                        this.rectF = new RectF((float) rimSize, (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 2.0d)) + rimSize),
                                (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                                (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - rimSize));
                    }
                }
                else if (this.mGauge.GaugeType == GaugeType.West) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)) + rimSize), (float) rimSize,
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.75d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.East) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.75d)) + rimSize), (float) rimSize,
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 4.0d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.North) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 2.0d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.75d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.South) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 2.0d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.75d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.NorthEast) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.NorthWest) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 1.125d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.SouthEast) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                }
                else if (this.mGauge.GaugeType == GaugeType.SouthWest) {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + rimSize),
                            (float) ((this.mGauge.mCentreY - (this.mGauge.mMinSize * 1.125d)) + rimSize),
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize * 1.125d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) - rimSize));
                }
                else {
                    this.rectF = new RectF((float) ((this.mGauge.mCentreX - (this.mGauge.mMinSize / 2.0d)) + rimSize), (float) rimSize,
                            (float) ((this.mGauge.mCentreX + (this.mGauge.mMinSize / 2.0d)) - rimSize),
                            (float) ((this.mGauge.mCentreY + (this.mGauge.mMinSize / 2.0d)) - rimSize));
                }
                this.mGauge.mRangeFrame = this.rectF;
            }
            onDrawPointers(canvas, this.mGaugeScale, this.rectF);
        }
        super.onDraw(canvas);
    }

    private void onDrawPointers(Canvas canvas, GaugeScale gaugeScale, RectF rectF) {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        if (this.mGaugePointer instanceof RangePointer) {
            this.paint.setColor(this.mGaugePointer.color);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) this.mGaugePointer.width);
            canvas.drawArc(rectF, (float) gaugeScale.startAngle,
                    (float) ((getPointerAngle((double) this.value, gaugeScale) - getPointerAngle(gaugeScale.getStartValue(),
                            gaugeScale)) / 0.017453292519943295d), false, this.paint);
            return;
        }
        double x0;
        double y0;
        double x2;
        double y2;
        double x1;
        double y1;
        this.paint.setColor(this.mGaugePointer.color);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeWidth((float) this.mGaugePointer.width);
        double pointerLength = (this.mGauge.mLabelsPathHeight / 2.0d) - (((NeedlePointer) this.mGaugePointer).lengthFactor * (this.mGauge
                .mLabelsPathHeight / 2.0d));
        double innerSize     = (this.mGauge.mLabelsPathHeight / 2.0d) - pointerLength;
        double pointerMargin = ((this.mGauge.mMinSize / 2.0d) - (this.mGauge.mLabelsPathHeight / 2.0d)) + pointerLength;
        double angle         = getPointerAngle((double) this.value, gaugeScale);
        if (this.mGauge.GaugeType == GaugeType.West) {
            x0 = (this.mGauge.mCentreX + (this.mGauge.mMinSize / 4.0d)) + ((this.mGaugePointer.width / 2.0d) * Math.cos(angle - 90.0d));
        }
        else if (this.mGauge.GaugeType == GaugeType.NorthWest || this.mGauge.GaugeType == GaugeType.SouthWest) {
            x0 = (this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) + ((this.mGaugePointer.width / 2.0d) * Math.cos(angle - 90.0d));
        }
        else if (this.mGauge.GaugeType == GaugeType.East) {
            x0 = (this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)) + ((this.mGaugePointer.width / 2.0d) * Math.cos(angle - 90.0d));
        }
        else if (this.mGauge.GaugeType == GaugeType.NorthEast || this.mGauge.GaugeType == GaugeType.SouthEast) {
            x0 = (this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + ((this.mGaugePointer.width / 2.0d) * Math.cos(angle - 90.0d));
        }
        else {
            x0 = this.mGauge.mCentreX + ((this.mGaugePointer.width / 2.0d) * Math.cos(angle - 90.0d));
        }
        if (this.mGauge.GaugeType == GaugeType.North) {
            y0 = (this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)) + ((this.mGaugePointer.width / 2.0d) * Math.sin(angle - 90.0d));
        }
        else if (this.mGauge.GaugeType == GaugeType.NorthEast || this.mGauge.GaugeType == GaugeType.NorthWest) {
            y0 = (this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) + ((this.mGaugePointer.width / 2.0d) * Math.sin(angle - 90.0d));
        }
        else if (this.mGauge.GaugeType == GaugeType.South) {
            y0 = (this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)) + ((this.mGaugePointer.width / 2.0d) * Math.sin(angle - 90.0d));
        }
        else if (this.mGauge.GaugeType == GaugeType.SouthEast || this.mGauge.GaugeType == GaugeType.SouthWest) {
            y0 = (this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + ((this.mGaugePointer.width / 2.0d) * Math.sin(angle - 90.0d));
        }
        else {
            y0 = this.mGauge.mCentreY + ((this.mGaugePointer.width / 2.0d) * Math.sin(angle - 90.0d));
        }
        if (this.mGauge.GaugeType == GaugeType.West) {
            x2 = (this.mGauge.mCentreX + (this.mGauge.mMinSize / 4.0d)) + ((this.mGaugePointer.width / 2.0d) * Math.cos(90.0d + angle));
        }
        else if (this.mGauge.GaugeType == GaugeType.NorthWest || this.mGauge.GaugeType == GaugeType.SouthWest) {
            x2 = (this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)) + ((this.mGaugePointer.width / 2.0d) * Math.cos(90.0d + angle));
        }
        else if (this.mGauge.GaugeType == GaugeType.East) {
            x2 = (this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)) + ((this.mGaugePointer.width / 2.0d) * Math.cos(90.0d + angle));
        }
        else if (this.mGauge.GaugeType == GaugeType.NorthEast || this.mGauge.GaugeType == GaugeType.SouthEast) {
            x2 = (this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)) + ((this.mGaugePointer.width / 2.0d) * Math.cos(90.0d + angle));
        }
        else {
            x2 = this.mGauge.mCentreX + ((this.mGaugePointer.width / 2.0d) * Math.cos(90.0d + angle));
        }
        if (this.mGauge.GaugeType == GaugeType.North) {
            y2 = (this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)) + ((this.mGaugePointer.width / 2.0d) * Math.sin(90.0d + angle));
        }
        else if (this.mGauge.GaugeType == GaugeType.NorthWest || this.mGauge.GaugeType == GaugeType.NorthEast) {
            y2 = (this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)) + ((this.mGaugePointer.width / 2.0d) * Math.sin(90.0d + angle));
        }
        else if (this.mGauge.GaugeType == GaugeType.South) {
            y2 = (this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)) + ((this.mGaugePointer.width / 2.0d) * Math.sin(90.0d + angle));
        }
        else if (this.mGauge.GaugeType == GaugeType.SouthEast || this.mGauge.GaugeType == GaugeType.SouthWest) {
            y2 = (this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)) + ((this.mGaugePointer.width / 2.0d) * Math.sin(90.0d + angle));
        }
        else {
            y2 = this.mGauge.mCentreY + ((this.mGaugePointer.width / 2.0d) * Math.sin(90.0d + angle));
        }
        if (this.mGauge.mCentreY > this.mGauge.mCentreX) {
            if (this.mGauge.GaugeType == GaugeType.West) {
                x1 = (((this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)) + innerSize) + (Math.cos(angle) * innerSize)) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.NorthWest || this.mGauge.GaugeType == GaugeType.SouthWest) {
                x1 = (((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.125d)) + innerSize) + ((1.5d * innerSize) * Math
                        .cos(angle))) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.East) {
                x1 = (((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.75d)) + innerSize) + (Math
                        .cos(angle) * innerSize)) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.NorthEast || this.mGauge.GaugeType == GaugeType.SouthEast) {
                x1 = (((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.875d)) + innerSize) + ((1.5d * innerSize) * Math
                        .cos(angle))) + pointerMargin;
            }
            else {
                x1 = ((Math.cos(angle) * innerSize) + innerSize) + pointerMargin;
            }
            if (this.mGauge.GaugeType == GaugeType.North) {
                y1 = (((this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)) + innerSize) + (Math.sin(angle) * innerSize)) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.NorthEast || this.mGauge.GaugeType == GaugeType.NorthWest) {
                y1 = (((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.125d)) + innerSize) + ((1.5d * innerSize) * Math
                        .sin(angle))) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.South) {
                y1 = (((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.75d)) + innerSize) + (Math
                        .sin(angle) * innerSize)) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.SouthEast || this.mGauge.GaugeType == GaugeType.SouthWest) {
                y1 = (((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.875d)) + innerSize) + ((1.5d * innerSize) * Math
                        .sin(angle))) + pointerMargin;
            }
            else {
                y1 = (((this.mGauge.mCentreY - (this.mGauge.mMinSize / 2.0d)) + innerSize) + (Math.sin(angle) * innerSize)) + pointerMargin;
            }
        }
        else {
            if (this.mGauge.GaugeType == GaugeType.West) {
                x1 = (((this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)) + innerSize) + (Math.cos(angle) * innerSize)) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.NorthWest || this.mGauge.GaugeType == GaugeType.SouthWest) {
                x1 = (((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.125d)) + innerSize) + ((1.5d * innerSize) * Math
                        .cos(angle))) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.East) {
                x1 = (((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.75d)) + innerSize) + (Math
                        .cos(angle) * innerSize)) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.NorthEast || this.mGauge.GaugeType == GaugeType.SouthEast) {
                x1 = (((this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.875d)) + innerSize) + ((1.5d * innerSize) * Math
                        .cos(angle))) + pointerMargin;
            }
            else {
                x1 = (((this.mGauge.mCentreX - (this.mGauge.mMinSize / 2.0d)) + innerSize) + (Math.cos(angle) * innerSize)) + pointerMargin;
            }
            if (this.mGauge.GaugeType == GaugeType.North) {
                y1 = (((this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)) + innerSize) + (Math.sin(angle) * innerSize)) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.NorthWest || this.mGauge.GaugeType == GaugeType.NorthEast) {
                y1 = (((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.125d)) + innerSize) + ((1.5d * innerSize) * Math
                        .sin(angle))) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.South) {
                y1 = (((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.75d)) + innerSize) + (Math
                        .sin(angle) * innerSize)) + pointerMargin;
            }
            else if (this.mGauge.GaugeType == GaugeType.SouthEast || this.mGauge.GaugeType == GaugeType.SouthWest) {
                y1 = (((this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.875d)) + innerSize) + ((1.5d * innerSize) * Math
                        .sin(angle))) + pointerMargin;
            }
            else {
                y1 = ((Math.sin(angle) * innerSize) + innerSize) + pointerMargin;
            }
        }
        if (((NeedlePointer) this.mGaugePointer).type != NeedleType.Bar) {
            Path   path         = new Path();
            double pointerAngle = angle / 0.017453292519943295d;
            if (pointerAngle > 360.0d) {
                pointerAngle %= 360.0d;
            }
            path.moveTo((float) x0, (float) y0);
            path.lineTo((float) x1, (float) y1);
            path.lineTo((float) x2, (float) y2);
            this.paint.setStyle(Paint.Style.FILL);
            path.close();
            canvas.drawPath(path, this.paint);
        }
        else if (this.mGauge.mCentreY > this.mGauge.mCentreX) {
            if (this.mGauge.GaugeType == GaugeType.North) {
                canvas.drawLine((float) this.mGauge.mCentreX, (float) (this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)), (float) x1,
                        (float) y1, this.paint);
            }
            else if (this.mGauge.GaugeType == GaugeType.South) {
                canvas.drawLine((float) this.mGauge.mCentreX, (float) (this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)), (float) x1,
                        (float) y1, this.paint);
            }
            else if (this.mGauge.GaugeType == GaugeType.West) {
                canvas.drawLine((float) (this.mGauge.mCentreX + (this.mGauge.mMinSize / 4.0d)), (float) this.mGauge.mCentreY, (float) x1,
                        (float) y1, this.paint);
            }
            else if (this.mGauge.GaugeType == GaugeType.East) {
                canvas.drawLine((float) (this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)), (float) this.mGauge.mCentreY, (float) x1,
                        (float) y1, this.paint);
            }
            else if (this.mGauge.GaugeType == GaugeType.NorthEast) {
                canvas.drawLine((float) (this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)),
                        (float) (this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)), (float) x1, (float) y1, this.paint);
            }
            else if (this.mGauge.GaugeType == GaugeType.NorthWest) {
                canvas.drawLine((float) (this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)),
                        (float) (this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)), (float) x1, (float) y1, this.paint);
            }
            else if (this.mGauge.GaugeType == GaugeType.SouthEast) {
                canvas.drawLine((float) (this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)),
                        (float) (this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)), (float) x1, (float) y1, this.paint);
            }
            else if (this.mGauge.GaugeType == GaugeType.SouthWest) {
                canvas.drawLine((float) (this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)),
                        (float) (this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)), (float) x1, (float) y1, this.paint);
            }
            else {
                canvas.drawLine((float) this.mGauge.mCentreX, (float) this.mGauge.mCentreY, (float) x1, (float) y1, this.paint);
            }
        }
        else if (this.mGauge.GaugeType == GaugeType.North) {
            canvas.drawLine((float) this.mGauge.mCentreX, (float) (this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)), (float) x1,
                    (float) y1, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.South) {
            canvas.drawLine((float) this.mGauge.mCentreX, (float) (this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)), (float) x1,
                    (float) y1, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.West) {
            canvas.drawLine((float) (this.mGauge.mCentreX + (this.mGauge.mMinSize / 4.0d)), (float) this.mGauge.mCentreY, (float) x1,
                    (float) y1, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.East) {
            canvas.drawLine((float) (this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)), (float) this.mGauge.mCentreY, (float) x1,
                    (float) y1, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.NorthEast) {
            canvas.drawLine((float) (this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)),
                    (float) (this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)), (float) x1, (float) y1, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.NorthWest) {
            canvas.drawLine((float) (this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)),
                    (float) (this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)), (float) x1, (float) y1, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.SouthEast) {
            canvas.drawLine((float) (this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)),
                    (float) (this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)), (float) x1, (float) y1, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.SouthWest) {
            canvas.drawLine((float) (this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)),
                    (float) (this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)), (float) x1, (float) y1, this.paint);
        }
        else {
            canvas.drawLine((float) this.mGauge.mCentreX, (float) this.mGauge.mCentreY, (float) x1, (float) y1, this.paint);
        }
        this.paint.setColor(((NeedlePointer) this.mGaugePointer).knobColor);
        if (this.mGauge.GaugeType == GaugeType.North) {
            canvas.drawCircle((float) this.mGauge.mCentreX, (float) (this.mGauge.mCentreY + (this.mGauge.mMinSize / 4.0d)),
                    (float) ((NeedlePointer) this.mGaugePointer).knobRadius, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.South) {
            canvas.drawCircle((float) this.mGauge.mCentreX, (float) (this.mGauge.mCentreY - (this.mGauge.mMinSize / 4.0d)),
                    (float) ((NeedlePointer) this.mGaugePointer).knobRadius, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.West) {
            canvas.drawCircle((float) (this.mGauge.mCentreX + (this.mGauge.mMinSize / 4.0d)), (float) this.mGauge.mCentreY,
                    (float) ((NeedlePointer) this.mGaugePointer).knobRadius, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.East) {
            canvas.drawCircle((float) (this.mGauge.mCentreX - (this.mGauge.mMinSize / 4.0d)), (float) this.mGauge.mCentreY,
                    (float) ((NeedlePointer) this.mGaugePointer).knobRadius, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.NorthEast) {
            canvas.drawCircle((float) (this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)),
                    (float) (this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)),
                    (float) ((NeedlePointer) this.mGaugePointer).knobRadius, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.NorthWest) {
            canvas.drawCircle((float) (this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)),
                    (float) (this.mGauge.mCentreY + (this.mGauge.mMinSize * 0.375d)),
                    (float) ((NeedlePointer) this.mGaugePointer).knobRadius, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.SouthEast) {
            canvas.drawCircle((float) (this.mGauge.mCentreX - (this.mGauge.mMinSize * 0.375d)),
                    (float) (this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)),
                    (float) ((NeedlePointer) this.mGaugePointer).knobRadius, this.paint);
        }
        else if (this.mGauge.GaugeType == GaugeType.SouthWest) {
            canvas.drawCircle((float) (this.mGauge.mCentreX + (this.mGauge.mMinSize * 0.375d)),
                    (float) (this.mGauge.mCentreY - (this.mGauge.mMinSize * 0.375d)),
                    (float) ((NeedlePointer) this.mGaugePointer).knobRadius, this.paint);
        }
        else {
            canvas.drawCircle((float) this.mGauge.mCentreX, (float) this.mGauge.mCentreY,
                    (float) ((NeedlePointer) this.mGaugePointer).knobRadius, this.paint);
        }
    }

    private double getPointerAngle(double pointerValue, GaugeScale gaugeScale) {
        if (pointerValue > gaugeScale.endValue) {
            pointerValue = gaugeScale.endValue;
        }
        double startAngle = gaugeScale.startAngle * 0.017453292519943295d;
        if (pointerValue < gaugeScale.startValue) {
            pointerValue = gaugeScale.startValue;
        }
        return startAngle + (((pointerValue - gaugeScale.startValue) * (gaugeScale.sweepAngle / (gaugeScale.endValue - gaugeScale
                .startValue))) * 0.017453292519943295d);
    }
}
