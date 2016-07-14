package com.example.aprokopenko.triphelper.gauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.example.aprokopenko.triphelper.gauge.enums.GaugeType;
import com.example.aprokopenko.triphelper.gauge.enums.NeedleType;

public class PointerRender extends View {

    protected GaugePointer    mGaugePointer;
    private   GaugeScale      mGaugeScale;
    private   boolean         mEnableAnimation;
    private   TripHelperGauge mGauge;

    protected float value;

    //modif parameters
    private double modif_minSizeDivideBy4;
    private double modif_minSizeDivideBy2;
    private double modif_minSizeMultBy0dot75;
    private double modif_minSizeMultBy1dot125;
    private double modif_minSizeMultBy0dot375;
    private double modif_minSizeMultBy0dot875;
    private double modif_widthDivideBy2;

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
            value = (float) mGaugePointer.value;
            if (mGaugePointer instanceof NeedlePointer) {
                assert mGauge != null;
                mGauge.mKnobDiameter = ((NeedlePointer) mGaugePointer).knobRadius;
            }
        }
    }

    public PointerRender(Context context, AttributeSet attrs) {
        super(context, attrs);
        value = 0.0f;
        mEnableAnimation = true;
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
        if (!(mGauge == null || mGaugeScale == null || mGaugePointer == null)) {
            //modif optim
            double modif_widthMultRadFactor   = mGauge.mInnerBevelWidth * mGaugeScale.radiusFactor;
            double modif_minSizeMultRadFactor = mGauge.mMinSize * mGaugeScale.radiusFactor;
            double modfi_marginVar            = mGauge.mInnerBevelWidth - modif_widthMultRadFactor;

            mGauge.calculateMargin(modfi_marginVar, modfi_marginVar);
            double rimSize = mGauge.mMinSize - mGauge.mRimWidth;

            modif_minSizeDivideBy4 = mGauge.mMinSize / 4.0d;
            modif_minSizeDivideBy2 = mGauge.mMinSize / 2.0d;
            modif_minSizeMultBy0dot75 = mGauge.mMinSize * 0.75d;
            modif_minSizeMultBy1dot125 = mGauge.mMinSize * 1.125d;
            modif_minSizeMultBy0dot375 = mGauge.mMinSize * 0.375d;
            modif_minSizeMultBy0dot875 = mGauge.mMinSize * 0.875d;
            modif_widthDivideBy2 = mGaugePointer.width / 2.0d;

            double modif_centerYminusDivideBy4plusRimS = getCenterYminusDivideBy4plusRimS(rimSize);
            double modif_centerXplusDivideBy2minusRimS = getCenterXplusDivideBy2minusRimS(rimSize);
            double modif_centerYplus0dot75minusRimS    = getCenterYplus0dot75minusRimS(rimSize);
            double modif_centerYminus0dot75plusRimS    = getCenterYminus0dot75plusRimS(rimSize);
            double modif_centerXminusDivideBy4plusRimS = getCenterXminusDivideBy4plusRimS(rimSize);
            double modif_centerYminusDivideBy2plusRimS = getCenterYminusDivideBy2plusRimS(rimSize);
            double modif_centerXplus0dot75minusRimS    = getCenterXplus0dot75minusRimS(rimSize);
            double modif_centerYplusDivideBy4minusRimS = getCenterYplusDivideBy4minusRimS(rimSize);
            double modif_centerXminus0dot75plusRimS    = getCenterXminus0dot75plusRimS(rimSize);
            double moidf_centerXminus1dot125plusRimS   = getCenterXminus1dot125plusRimS(rimSize);
            double modif_centerXplusDivideBy4minusRimS = getCenterXplusDivideBy4minusRimS(rimSize);
            double modif_centerYminus0dot375plusRimS   = getCenterYminus0dot375plusRimS(rimSize);
            double modif_centerXplus0dot375minusRimS   = getCenterXplus0dot375minusRimS(rimSize);
            double modif_centerYplus1dot125minusRimS   = getCenterYplus1dot125minusRimS(rimSize);
            double modif_centerXminus0dot375plusRimS   = getCenterXminus0dot375plusRimS(rimSize);
            double modif_centerXplus1dot125minusRimS   = getCenterXplus1dot125minusRimS(rimSize);
            double modif_centerYminus1dot125plusRimS   = getCenterYminus1dot125plusRimS(rimSize);
            double modif_centerYplus0dot375minusRimS   = getCenterYplus0dot375minusRimS(rimSize);
            double modif_centerYplusDivideBy2minusRimS = getCenterYplusDivideBy2minusRimS(rimSize);
            double modif_centerXminusDivideBy2plusRimS = getCenterXminusDivideBy2plusRimS(rimSize);

            RectF rectF;
            if (mGaugeScale.radiusFactor > GaugeConstants.ZERO) {
                //modif optim
                double marginVar = mGauge.mMinSize - modif_minSizeMultRadFactor;

                mGauge.calculateMargin(marginVar, marginVar);
                double d = mGauge.mRangePathWidth - mGauge.mRimWidth;
                rimSize = d - (4.0d * 10.0d);

                if (mGauge.mCentreY > mGauge.mCentreX) {
                    if (mGauge.GaugeType == GaugeType.North) {
                        rectF = getRectF((float) modif_centerYminusDivideBy4plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplus0dot75minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.South) {
                        rectF = getRectF((float) modif_centerYminus0dot75plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy4minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.West) {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) modif_centerXminusDivideBy4plusRimS,
                                (float) modif_centerXplus0dot75minusRimS, (float) modif_centerYplus0dot75minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.East) {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) modif_centerXminus0dot75plusRimS,
                                (float) modif_centerXplusDivideBy4minusRimS, (float) modif_centerYplusDivideBy4minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.NorthEast) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.NorthWest) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.SouthEast) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.SouthWest) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                    }
                }
                else if (mGauge.GaugeType == GaugeType.West) {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminusDivideBy4plusRimS, (float) modif_centerXplus0dot75minusRimS,
                            (float) modif_centerYplusDivideBy2minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.East) {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminus0dot75plusRimS, (float) modif_centerXplusDivideBy4minusRimS,
                            (float) modif_centerYplusDivideBy2minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.North) {
                    rectF = getRectF((float) modif_centerYminusDivideBy4plusRimS, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplus0dot75minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.South) {
                    rectF = getRectF((float) modif_centerYminus0dot75plusRimS, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy4minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.NorthEast) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.NorthWest) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                            (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.SouthEast) {
                    rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.SouthWest) {
                    rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) modif_centerXminus0dot375plusRimS,
                            (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus0dot375minusRimS);
                }
                else {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                }
                mGauge.mRangeFrame = rectF;
                float rangePointerPoisition = 0.0f;
                if (mGaugePointer instanceof RangePointer) {
                    rangePointerPoisition = (float) ((RangePointer) mGaugePointer).offset;
                }
                float factor = (mGauge.mRangeFrame.left + (((mGauge.mRangeFrame
                        .width() / 2.0f) - mGauge.mRangeFrame.left) * ((float) mGaugeScale.radiusFactor))) + ((float) (((double)
                        rangePointerPoisition) * (mGauge.mCentreX - mGauge.mRimWidth)));
                if (mGauge.mCentreY <= mGauge.mCentreX) {
                    factor = (mGauge.mRangeFrame.top + (((mGauge.mRangeFrame
                            .height() / 2.0f) - mGauge.mRangeFrame.top) * ((float) mGaugeScale.radiusFactor))) + ((float) (((double)
                            rangePointerPoisition) * (mGauge.mCentreY - mGauge.mRimWidth)));
                    if (mGauge.GaugeType == GaugeType.West) {
                        rectF = getRectF(factor, (float) (getCenterXminusDivideBy4plusRimS(((double) factor))),
                                (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                                (float) (getCenterYplus0dot75minusRimS(((double) factor))));
                    }
                    else if (mGauge.GaugeType == GaugeType.East) {
                        rectF = getRectF(factor, (float) (getCenterXminus0dot75plusRimS(((double) factor))),
                                (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                                (float) (getCenterYplusDivideBy4minusRimS(((double) factor))));
                    }
                    else if (mGauge.GaugeType == GaugeType.North) {
                        rectF = getRectF((float) (getCenterYminusDivideBy4plusRimS(((double) factor))),
                                (float) (getCenterXminusDivideBy2plusRimS(((double) factor))),
                                (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                                (float) (getCenterYplus0dot75minusRimS(((double) factor))));
                    }
                    else if (mGauge.GaugeType == GaugeType.South) {
                        rectF = getRectF((float) (getCenterYminus0dot75plusRimS(((double) factor))),
                                (float) (getCenterXminusDivideBy2plusRimS(((double) factor))),
                                (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                                (float) (getCenterYplusDivideBy4minusRimS(((double) factor))));
                    }
                    else if (mGauge.GaugeType == GaugeType.NorthEast) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.NorthWest) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.SouthEast) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.SouthWest) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else {
                        rectF = getRectF(factor, (float) (getCenterXminusDivideBy2plusRimS(((double) factor))),
                                (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                                (float) (getCenterYplusDivideBy2minusRimS(((double) factor))));
                    }
                }
                else if (mGauge.GaugeType == GaugeType.North) {
                    rectF = getRectF((float) (getCenterYminusDivideBy4plusRimS(((double) factor))), factor,
                            (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                            (float) (getCenterYplus0dot75minusRimS(((double) factor))));
                }
                else if (mGauge.GaugeType == GaugeType.South) {
                    rectF = getRectF((float) (getCenterYminus0dot75plusRimS(((double) factor))), factor,
                            (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                            (float) (getCenterYplusDivideBy4minusRimS(((double) factor))));
                }
                else if (mGauge.GaugeType == GaugeType.West) {
                    rectF = getRectF((float) (getCenterYminusDivideBy2plusRimS(((double) factor))),
                            (float) (getCenterXminusDivideBy4plusRimS(((double) factor))),
                            (float) (getCenterXplus0dot75minusRimS(((double) factor))),
                            (float) (getCenterYplusDivideBy2minusRimS(((double) factor))));
                }
                else if (mGauge.GaugeType == GaugeType.East) {
                    rectF = getRectF((float) (getCenterYminusDivideBy2plusRimS(((double) factor))),
                            (float) (getCenterXminus0dot75plusRimS(((double) factor))),
                            (float) (getCenterXplusDivideBy4minusRimS(((double) factor))),
                            (float) (getCenterYplusDivideBy2minusRimS(((double) factor))));
                }
                else if (mGauge.GaugeType == GaugeType.NorthEast) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.NorthWest) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                            (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.SouthEast) {
                    rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.SouthWest) {
                    rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) modif_centerXminus0dot375plusRimS,
                            (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus0dot375minusRimS);
                }
                else {
                    rectF = getRectF((float) (getCenterYminusDivideBy2plusRimS(((double) factor))), factor,
                            (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                            (float) (getCenterYplusDivideBy2minusRimS(((double) factor))));
                }
            }
            else {
                if (mGaugePointer instanceof RangePointer) {
                    rimSize = (mGauge.mMinSize - mGauge.mRimWidth) - ((double) ((float) ((((double) ((float) ((RangePointer)
                            mGaugePointer).offset)) * (mGauge.mCentreX - mGauge.mRimWidth)) - (mGaugePointer.width / 2.0d))));
                }
                if (mGauge.mCentreY > mGauge.mCentreX) {
                    if (mGauge.GaugeType == GaugeType.North) {
                        rectF = getRectF((float) modif_centerYminusDivideBy4plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplus0dot75minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.South) {
                        rectF = getRectF((float) modif_centerYminus0dot75plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy4minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.West) {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) modif_centerXminusDivideBy4plusRimS,
                                (float) modif_centerXplus0dot75minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.East) {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) modif_centerXminus0dot75plusRimS,
                                (float) modif_centerXplusDivideBy4minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.NorthEast) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.NorthWest) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.SouthEast) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else if (mGauge.GaugeType == GaugeType.SouthWest) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                    }
                }
                else if (mGauge.GaugeType == GaugeType.West) {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminusDivideBy4plusRimS, (float) modif_centerXplus0dot75minusRimS,
                            (float) modif_centerYplusDivideBy2minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.East) {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminus0dot75plusRimS, (float) modif_centerXplusDivideBy4minusRimS,
                            (float) modif_centerYplusDivideBy2minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.North) {
                    rectF = getRectF((float) modif_centerYminusDivideBy4plusRimS, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplus0dot75minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.South) {
                    rectF = getRectF((float) modif_centerYminus0dot75plusRimS, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy4minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.NorthEast) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.NorthWest) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                            (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.SouthEast) {
                    rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                }
                else if (mGauge.GaugeType == GaugeType.SouthWest) {
                    rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) modif_centerXminus0dot375plusRimS,
                            (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus0dot375minusRimS);
                }
                else {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                }
                mGauge.mRangeFrame = rectF;
            }
            onDrawPointers(canvas, mGaugeScale, rectF);
        }
        super.onDraw(canvas);
    }

    @NonNull private RectF getRectF(float factor, float centerXminusDivideBy4plusRimS, float centerXplusDivideBy2minusRimS,
                                    float centerYplus0dot75minusRimS) {
        return new RectF(centerXminusDivideBy4plusRimS, factor, centerXplusDivideBy2minusRimS, centerYplus0dot75minusRimS);
    }


    private void onDrawPointers(Canvas canvas, GaugeScale gaugeScale, RectF rectF) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (mGaugePointer instanceof RangePointer) {
            paint.setColor(mGaugePointer.color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) mGaugePointer.width);
            canvas.drawArc(rectF, (float) gaugeScale.startAngle,
                    (float) ((getPointerAngle((double) value, gaugeScale) - getPointerAngle(gaugeScale.getStartValue(),
                            gaugeScale)) / GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY), false, paint);
            return;
        }
        double x0;
        double y0;
        double x2;
        double y2;
        double x1;
        double y1;
        paint.setColor(mGaugePointer.color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth((float) mGaugePointer.width);
        double pointerLength = (mGauge.mLabelsPathHeight / 2.0d) - (((NeedlePointer) mGaugePointer).lengthFactor * (mGauge
                .mLabelsPathHeight / 2.0d));
        double innerSize     = (mGauge.mLabelsPathHeight / 2.0d) - pointerLength;
        double pointerMargin = (modif_minSizeDivideBy2 - (mGauge.mLabelsPathHeight / 2.0d)) + pointerLength;
        double angle         = getPointerAngle((double) value, gaugeScale);

        //modif optim
        double modif_widthMultCos                     = modif_widthDivideBy2 * Math.cos(angle - 90.0d);
        double modif_widthMultBySin                   = modif_widthDivideBy2 * Math.sin(angle - 90.0d);
        double modif_dividedWidthMultByCos90plusAngle = modif_widthDivideBy2 * Math.cos(90.0d + angle);
        double modif_dividedWidthMultBySin90plusAngle = modif_widthDivideBy2 * Math.sin(90.0d + angle);
        double modif_innerSizeMultBy1dot5MultBySin    = (1.5d * innerSize) * Math.sin(angle);
        double modif_innerSizeMultBy1dot5MultByCos    = (1.5d * innerSize) * Math.cos(angle);
        double modif_sinMultInnerSize                 = Math.sin(angle) * innerSize;
        double modif_cosMultInnerSize                 = Math.cos(angle) * innerSize;
        double modif_centerYminusMinSizeDivideBy4     = mGauge.mCentreY - modif_minSizeDivideBy4;
        double modif_centerXplusMinSizeDivideBy4      = mGauge.mCentreX + modif_minSizeDivideBy4;
        double modif_centerYplus0dot375               = mGauge.mCentreY + modif_minSizeMultBy0dot375;
        double modif_centerXplus0dot375               = mGauge.mCentreX + modif_minSizeMultBy0dot375;
        double modif_centerXminusSizeDivideBy4        = mGauge.mCentreX - modif_minSizeDivideBy4;
        double modif_centerXminus0dot375              = mGauge.mCentreX - modif_minSizeMultBy0dot375;
        double modif_centerYminus0dot375              = mGauge.mCentreY - modif_minSizeMultBy0dot375;

        if (mGauge.GaugeType == GaugeType.West) {
            x0 = modif_centerXplusMinSizeDivideBy4 + modif_widthMultCos;
        }
        else if (mGauge.GaugeType == GaugeType.NorthWest || mGauge.GaugeType == GaugeType.SouthWest) {
            x0 = modif_centerXplus0dot375 + modif_widthMultCos;
        }
        else if (mGauge.GaugeType == GaugeType.East) {
            x0 = modif_centerXminusSizeDivideBy4 + modif_widthMultCos;
        }
        else if (mGauge.GaugeType == GaugeType.NorthEast || mGauge.GaugeType == GaugeType.SouthEast) {
            x0 = modif_centerXminus0dot375 + modif_widthMultCos;
        }
        else {
            x0 = mGauge.mCentreX + modif_widthMultCos;
        }

        if (mGauge.GaugeType == GaugeType.North) {
            y0 = modif_centerXplusMinSizeDivideBy4 + modif_widthMultBySin;
        }
        else if (mGauge.GaugeType == GaugeType.NorthEast || mGauge.GaugeType == GaugeType.NorthWest) {
            y0 = modif_centerYplus0dot375 + modif_widthMultBySin;
        }
        else if (mGauge.GaugeType == GaugeType.South) {
            y0 = modif_centerYminusMinSizeDivideBy4 + modif_widthMultBySin;
        }
        else if (mGauge.GaugeType == GaugeType.SouthEast || mGauge.GaugeType == GaugeType.SouthWest) {
            y0 = modif_centerYminus0dot375 + modif_widthMultBySin;
        }
        else {
            y0 = mGauge.mCentreY + modif_widthMultBySin;
        }

        if (mGauge.GaugeType == GaugeType.West) {
            x2 = modif_centerXplusMinSizeDivideBy4 + modif_dividedWidthMultByCos90plusAngle;
        }
        else if (mGauge.GaugeType == GaugeType.NorthWest || mGauge.GaugeType == GaugeType.SouthWest) {
            x2 = modif_centerXplus0dot375 + modif_dividedWidthMultByCos90plusAngle;
        }
        else if (mGauge.GaugeType == GaugeType.East) {
            x2 = modif_centerXminusSizeDivideBy4 + modif_dividedWidthMultByCos90plusAngle;
        }
        else if (mGauge.GaugeType == GaugeType.NorthEast || mGauge.GaugeType == GaugeType.SouthEast) {
            x2 = modif_centerXminus0dot375 + modif_dividedWidthMultByCos90plusAngle;
        }
        else {
            x2 = mGauge.mCentreX + modif_dividedWidthMultByCos90plusAngle;
        }

        if (mGauge.GaugeType == GaugeType.North) {
            y2 = modif_centerXplusMinSizeDivideBy4 + modif_dividedWidthMultBySin90plusAngle;
        }
        else if (mGauge.GaugeType == GaugeType.NorthWest || mGauge.GaugeType == GaugeType.NorthEast) {
            y2 = modif_centerYplus0dot375 + modif_dividedWidthMultBySin90plusAngle;
        }
        else if (mGauge.GaugeType == GaugeType.South) {
            y2 = modif_centerYminusMinSizeDivideBy4 + modif_dividedWidthMultBySin90plusAngle;
        }
        else if (mGauge.GaugeType == GaugeType.SouthEast || mGauge.GaugeType == GaugeType.SouthWest) {
            y2 = modif_centerYminus0dot375 + modif_dividedWidthMultBySin90plusAngle;
        }
        else {
            y2 = mGauge.mCentreY + modif_dividedWidthMultBySin90plusAngle;
        }

        if (mGauge.mCentreY > mGauge.mCentreX) {
            if (mGauge.GaugeType == GaugeType.West) {
                x1 = ((modif_centerXminusSizeDivideBy4 + innerSize) + modif_cosMultInnerSize) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.NorthWest || mGauge.GaugeType == GaugeType.SouthWest) {
                x1 = ((getCenterXminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.East) {
                x1 = ((getCenterXminus0dot75plusRimS(innerSize)) + modif_cosMultInnerSize) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.NorthEast || mGauge.GaugeType == GaugeType.SouthEast) {
                x1 = (((mGauge.mCentreX - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
            }
            else {
                x1 = (modif_cosMultInnerSize + innerSize) + pointerMargin;
            }
            if (mGauge.GaugeType == GaugeType.North) {
                y1 = ((modif_centerYminusMinSizeDivideBy4 + innerSize) + modif_sinMultInnerSize) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.NorthEast || mGauge.GaugeType == GaugeType.NorthWest) {
                y1 = ((getCenterYminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.South) {
                y1 = ((getCenterYminus0dot75plusRimS(innerSize)) + modif_sinMultInnerSize) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.SouthEast || mGauge.GaugeType == GaugeType.SouthWest) {
                y1 = (((mGauge.mCentreY - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
            }
            else {
                y1 = ((getCenterYminusDivideBy2plusRimS(innerSize)) + modif_sinMultInnerSize) + pointerMargin;
            }
        }
        else {
            if (mGauge.GaugeType == GaugeType.West) {
                x1 = ((modif_centerXminusSizeDivideBy4 + innerSize) + modif_cosMultInnerSize) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.NorthWest || mGauge.GaugeType == GaugeType.SouthWest) {
                x1 = ((getCenterXminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.East) {
                x1 = ((getCenterXminus0dot75plusRimS(innerSize)) + modif_cosMultInnerSize) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.NorthEast || mGauge.GaugeType == GaugeType.SouthEast) {
                x1 = (((mGauge.mCentreX - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
            }
            else {
                x1 = ((getCenterXminusDivideBy2plusRimS(innerSize)) + modif_cosMultInnerSize) + pointerMargin;
            }
            if (mGauge.GaugeType == GaugeType.North) {
                y1 = ((modif_centerYminusMinSizeDivideBy4 + innerSize) + modif_sinMultInnerSize) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.NorthWest || mGauge.GaugeType == GaugeType.NorthEast) {
                y1 = ((getCenterYminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.South) {
                y1 = ((getCenterYminus0dot75plusRimS(innerSize)) + modif_sinMultInnerSize) + pointerMargin;
            }
            else if (mGauge.GaugeType == GaugeType.SouthEast || mGauge.GaugeType == GaugeType.SouthWest) {
                y1 = (((mGauge.mCentreY - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
            }
            else {
                y1 = (modif_sinMultInnerSize + innerSize) + pointerMargin;
            }
        }
        if (((NeedlePointer) mGaugePointer).type != NeedleType.Bar) {
            Path path = new Path();
            path.moveTo((float) x0, (float) y0);
            path.lineTo((float) x1, (float) y1);
            path.lineTo((float) x2, (float) y2);
            paint.setStyle(Paint.Style.FILL);
            path.close();
            canvas.drawPath(path, paint);
        }
        else if (mGauge.mCentreY > mGauge.mCentreX) {
            if (mGauge.GaugeType == GaugeType.North) {
                canvas.drawLine((float) mGauge.mCentreX, (float) modif_centerXplusMinSizeDivideBy4, (float) x1, (float) y1, paint);
            }
            else if (mGauge.GaugeType == GaugeType.South) {
                canvas.drawLine((float) mGauge.mCentreX, (float) modif_centerYminusMinSizeDivideBy4, (float) x1, (float) y1, paint);
            }
            else if (mGauge.GaugeType == GaugeType.West) {
                canvas.drawLine((float) modif_centerXplusMinSizeDivideBy4, (float) mGauge.mCentreY, (float) x1, (float) y1, paint);
            }
            else if (mGauge.GaugeType == GaugeType.East) {
                canvas.drawLine((float) modif_centerXminusSizeDivideBy4, (float) mGauge.mCentreY, (float) x1, (float) y1, paint);
            }
            else if (mGauge.GaugeType == GaugeType.NorthEast) {
                canvas.drawLine((float) modif_centerXminus0dot375, (float) modif_centerYplus0dot375, (float) x1, (float) y1, paint);
            }
            else if (mGauge.GaugeType == GaugeType.NorthWest) {
                canvas.drawLine((float) modif_centerXplus0dot375, (float) modif_centerYplus0dot375, (float) x1, (float) y1, paint);
            }
            else if (mGauge.GaugeType == GaugeType.SouthEast) {
                canvas.drawLine((float) modif_centerXminus0dot375, (float) modif_centerYminus0dot375, (float) x1, (float) y1, paint);
            }
            else if (mGauge.GaugeType == GaugeType.SouthWest) {
                canvas.drawLine((float) modif_centerXplus0dot375, (float) modif_centerYminus0dot375, (float) x1, (float) y1, paint);
            }
            else {
                canvas.drawLine((float) mGauge.mCentreX, (float) mGauge.mCentreY, (float) x1, (float) y1, paint);
            }
        }
        else if (mGauge.GaugeType == GaugeType.North) {
            canvas.drawLine((float) mGauge.mCentreX, (float) modif_centerXplusMinSizeDivideBy4, (float) x1, (float) y1, paint);
        }
        else if (mGauge.GaugeType == GaugeType.South) {
            canvas.drawLine((float) mGauge.mCentreX, (float) modif_centerYminusMinSizeDivideBy4, (float) x1, (float) y1, paint);
        }
        else if (mGauge.GaugeType == GaugeType.West) {
            canvas.drawLine((float) modif_centerXplusMinSizeDivideBy4, (float) mGauge.mCentreY, (float) x1, (float) y1, paint);
        }
        else if (mGauge.GaugeType == GaugeType.East) {
            canvas.drawLine((float) modif_centerXminusSizeDivideBy4, (float) mGauge.mCentreY, (float) x1, (float) y1, paint);
        }
        else if (mGauge.GaugeType == GaugeType.NorthEast) {
            canvas.drawLine((float) modif_centerXminus0dot375, (float) modif_centerYplus0dot375, (float) x1, (float) y1, paint);
        }
        else if (mGauge.GaugeType == GaugeType.NorthWest) {
            canvas.drawLine((float) modif_centerXplus0dot375, (float) modif_centerYplus0dot375, (float) x1, (float) y1, paint);
        }
        else if (mGauge.GaugeType == GaugeType.SouthEast) {
            canvas.drawLine((float) modif_centerXminus0dot375, (float) modif_centerYminus0dot375, (float) x1, (float) y1, paint);
        }
        else if (mGauge.GaugeType == GaugeType.SouthWest) {
            canvas.drawLine((float) modif_centerXplus0dot375, (float) modif_centerYminus0dot375, (float) x1, (float) y1, paint);
        }
        else {
            canvas.drawLine((float) mGauge.mCentreX, (float) mGauge.mCentreY, (float) x1, (float) y1, paint);
        }
        paint.setColor(((NeedlePointer) mGaugePointer).knobColor);
        if (mGauge.GaugeType == GaugeType.North) {
            canvas.drawCircle((float) mGauge.mCentreX, (float) modif_centerXplusMinSizeDivideBy4,
                    (float) ((NeedlePointer) mGaugePointer).knobRadius, paint);
        }
        else if (mGauge.GaugeType == GaugeType.South) {
            canvas.drawCircle((float) mGauge.mCentreX, (float) modif_centerYminusMinSizeDivideBy4,
                    (float) ((NeedlePointer) mGaugePointer).knobRadius, paint);
        }
        else if (mGauge.GaugeType == GaugeType.West) {
            canvas.drawCircle((float) modif_centerXplusMinSizeDivideBy4, (float) mGauge.mCentreY,
                    (float) ((NeedlePointer) mGaugePointer).knobRadius, paint);
        }
        else if (mGauge.GaugeType == GaugeType.East) {
            canvas.drawCircle((float) modif_centerXminusSizeDivideBy4, (float) mGauge.mCentreY,
                    (float) ((NeedlePointer) mGaugePointer).knobRadius, paint);
        }
        else if (mGauge.GaugeType == GaugeType.NorthEast) {
            canvas.drawCircle((float) modif_centerXminus0dot375, (float) modif_centerYplus0dot375,
                    (float) ((NeedlePointer) mGaugePointer).knobRadius, paint);
        }
        else if (mGauge.GaugeType == GaugeType.NorthWest) {
            canvas.drawCircle((float) modif_centerXplus0dot375, (float) modif_centerYplus0dot375,
                    (float) ((NeedlePointer) mGaugePointer).knobRadius, paint);
        }
        else if (mGauge.GaugeType == GaugeType.SouthEast) {
            canvas.drawCircle((float) modif_centerXminus0dot375, (float) modif_centerYminus0dot375,
                    (float) ((NeedlePointer) mGaugePointer).knobRadius, paint);
        }
        else if (mGauge.GaugeType == GaugeType.SouthWest) {
            canvas.drawCircle((float) modif_centerXplus0dot375, (float) modif_centerYminus0dot375,
                    (float) ((NeedlePointer) mGaugePointer).knobRadius, paint);
        }
        else {
            canvas.drawCircle((float) mGauge.mCentreX, (float) mGauge.mCentreY, (float) ((NeedlePointer) mGaugePointer).knobRadius, paint);
        }
    }

    private double getPointerAngle(double pointerValue, GaugeScale gaugeScale) {
        if (pointerValue > gaugeScale.endValue) {
            pointerValue = gaugeScale.endValue;
        }
        double startAngle = gaugeScale.startAngle * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        if (pointerValue < gaugeScale.startValue) {
            pointerValue = gaugeScale.startValue;
        }
        return startAngle + (((pointerValue - gaugeScale.startValue) * (gaugeScale.sweepAngle / (gaugeScale.endValue - gaugeScale
                .startValue))) * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY);
    }

    private double getCenterXminusDivideBy2plusRimS(double rimSize) {
        return (mGauge.mCentreX - modif_minSizeDivideBy2) + rimSize;
    }

    private double getCenterYplusDivideBy2minusRimS(double rimSize) {
        return (mGauge.mCentreY + modif_minSizeDivideBy2) - rimSize;
    }

    private double getCenterYplus0dot375minusRimS(double rimSize) {
        return (mGauge.mCentreY + modif_minSizeMultBy0dot375) - rimSize;
    }

    private double getCenterYminus1dot125plusRimS(double rimSize) {
        return (mGauge.mCentreY - modif_minSizeMultBy1dot125) + rimSize;
    }

    private double getCenterXplus1dot125minusRimS(double rimSize) {
        return (mGauge.mCentreX + modif_minSizeMultBy1dot125) - rimSize;
    }

    private double getCenterXminus0dot375plusRimS(double rimSize) {
        return (mGauge.mCentreX - modif_minSizeMultBy0dot375) + rimSize;
    }

    private double getCenterYplus1dot125minusRimS(double rimSize) {
        return (mGauge.mCentreY + modif_minSizeMultBy1dot125) - rimSize;
    }

    private double getCenterXplus0dot375minusRimS(double rimSize) {
        return (mGauge.mCentreX + modif_minSizeMultBy0dot375) - rimSize;
    }

    private double getCenterYminus0dot375plusRimS(double rimSize) {
        return (mGauge.mCentreY - modif_minSizeMultBy0dot375) + rimSize;
    }

    private double getCenterXplusDivideBy4minusRimS(double rimSize) {
        return (mGauge.mCentreX + modif_minSizeDivideBy4) - rimSize;
    }

    private double getCenterXminus1dot125plusRimS(double rimSize) {
        return (mGauge.mCentreX - modif_minSizeMultBy1dot125) + rimSize;
    }

    private double getCenterXminus0dot75plusRimS(double rimSize) {
        return (mGauge.mCentreX - modif_minSizeMultBy0dot75) + rimSize;
    }

    private double getCenterYplusDivideBy4minusRimS(double rimSize) {
        return (mGauge.mCentreY + modif_minSizeDivideBy4) - rimSize;
    }

    private double getCenterXplus0dot75minusRimS(double rimSize) {
        return (mGauge.mCentreX + modif_minSizeMultBy0dot75) - rimSize;
    }

    private double getCenterYminusDivideBy2plusRimS(double rimSize) {
        return (mGauge.mCentreY - modif_minSizeDivideBy2) + rimSize;
    }

    private double getCenterXminusDivideBy4plusRimS(double rimSize) {
        return (mGauge.mCentreX - modif_minSizeDivideBy4) + rimSize;
    }

    private double getCenterYminus0dot75plusRimS(double rimSize) {
        return (mGauge.mCentreY - modif_minSizeMultBy0dot75) + rimSize;
    }

    private double getCenterYplus0dot75minusRimS(double rimSize) {
        return (mGauge.mCentreY + modif_minSizeMultBy0dot75) - rimSize;
    }

    private double getCenterXplusDivideBy2minusRimS(double rimSize) {
        return (mGauge.mCentreX + modif_minSizeDivideBy2) - rimSize;
    }

    private double getCenterYminusDivideBy4plusRimS(double rimSize) {
        return (mGauge.mCentreY - modif_minSizeDivideBy4) + rimSize;
    }
}
