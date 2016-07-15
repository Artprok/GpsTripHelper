package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType;
import com.example.aprokopenko.triphelper.speedometer_gauge.enums.NeedleType;

public class PointerRender extends View {

    private GaugePointer    mGaugePointer;
    private GaugeScale      mGaugeScale;
    private boolean         mEnableAnimation;
    private TripHelperGauge mGauge;

    private float value;

    //modif parameters
    private double modif_minSizeDivideBy4;
    private double modif_minSizeDivideBy2;
    private double modif_minSizeMultBy0dot75;
    private double modif_minSizeMultBy1dot125;
    private double modif_minSizeMultBy0dot375;
    private double modif_minSizeMultBy0dot875;
    private double modif_widthDivideBy2;

    private double    mCentreX;
    private double    mCentreY;
    private GaugeType gaugeType;
    private double    mInnerBevelWidth;
    private double    mMinSize;
    private double    mRimWidth;
    private double    mLabelsPathHeight;


    public PointerRender(Context context, TripHelperGauge mGauge, GaugeScale mGaugeScale, GaugePointer mGaugePointer) {
        this(context, null);
        if (mGauge != null) {
            this.mGauge = mGauge;

            gaugeType = mGauge.getGaugeType();
            mRimWidth = mGauge.getmRimWidth();


        }
        if (mGaugeScale != null) {
            this.mGaugeScale = mGaugeScale;
        }
        if (mGaugePointer != null) {
            this.mGaugePointer = mGaugePointer;
            value = (float) mGaugePointer.getValue();
            if (mGaugePointer instanceof NeedlePointer) {
                assert mGauge != null;
            }
        }
    }

    public GaugePointer getmGaugePointer() {
        return mGaugePointer;
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
            double radFactor                  = mGaugeScale.getRadiusFactor();
            double modif_widthMultRadFactor   = mInnerBevelWidth * radFactor;
            double modif_minSizeMultRadFactor = mMinSize * radFactor;
            double modfi_marginVar            = mInnerBevelWidth - modif_widthMultRadFactor;

            RectF mRangeFrame = mGauge.getmRangeFrame();
            mCentreX = mGauge.getmCentreX();
            mCentreY = mGauge.getmCentreY();
            mMinSize = mGauge.getmMinSize();
            mInnerBevelWidth = mGauge.getmInnerBevelWidth();
            mRimWidth = mGauge.getmRimWidth();
            mLabelsPathHeight = mGauge.getmLabelsPathHeight();
            double mRangePathWidth = mGauge.getmRangePathWidth();

            mGauge.calculateMargin(modfi_marginVar);
            double rimSize = mMinSize - mRimWidth;

            modif_minSizeDivideBy4 = mMinSize / 4.0d;
            modif_minSizeDivideBy2 = mMinSize / 2.0d;
            modif_minSizeMultBy0dot75 = mMinSize * 0.75d;
            modif_minSizeMultBy1dot125 = mMinSize * 1.125d;
            modif_minSizeMultBy0dot375 = mMinSize * 0.375d;
            modif_minSizeMultBy0dot875 = mMinSize * 0.875d;
            modif_widthDivideBy2 = mGaugePointer.getWidth() / 2.0d;

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
            if (radFactor > GaugeConstants.ZERO) {
                //modif optim
                double marginVar = mMinSize - modif_minSizeMultRadFactor;

                mGauge.calculateMargin(marginVar);
                double d = mRangePathWidth - mRimWidth;
                rimSize = d - (4.0d * 10.0d);

                if (mCentreY > mCentreX) {
                    if (gaugeType == GaugeType.North) {
                        rectF = getRectF((float) modif_centerYminusDivideBy4plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplus0dot75minusRimS);
                    }
                    else if (gaugeType == GaugeType.South) {
                        rectF = getRectF((float) modif_centerYminus0dot75plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy4minusRimS);
                    }
                    else if (gaugeType == GaugeType.West) {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) modif_centerXminusDivideBy4plusRimS,
                                (float) modif_centerXplus0dot75minusRimS, (float) modif_centerYplus0dot75minusRimS);
                    }
                    else if (gaugeType == GaugeType.East) {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) modif_centerXminus0dot75plusRimS,
                                (float) modif_centerXplusDivideBy4minusRimS, (float) modif_centerYplusDivideBy4minusRimS);
                    }
                    else if (gaugeType == GaugeType.NorthEast) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (gaugeType == GaugeType.NorthWest) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (gaugeType == GaugeType.SouthEast) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else if (gaugeType == GaugeType.SouthWest) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                    }
                }
                else if (gaugeType == GaugeType.West) {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminusDivideBy4plusRimS, (float) modif_centerXplus0dot75minusRimS,
                            (float) modif_centerYplusDivideBy2minusRimS);
                }
                else if (gaugeType == GaugeType.East) {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminus0dot75plusRimS, (float) modif_centerXplusDivideBy4minusRimS,
                            (float) modif_centerYplusDivideBy2minusRimS);
                }
                else if (gaugeType == GaugeType.North) {
                    rectF = getRectF((float) modif_centerYminusDivideBy4plusRimS, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplus0dot75minusRimS);
                }
                else if (gaugeType == GaugeType.South) {
                    rectF = getRectF((float) modif_centerYminus0dot75plusRimS, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy4minusRimS);
                }
                else if (gaugeType == GaugeType.NorthEast) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (gaugeType == GaugeType.NorthWest) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                            (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (gaugeType == GaugeType.SouthEast) {
                    rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                }
                else if (gaugeType == GaugeType.SouthWest) {
                    rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) modif_centerXminus0dot375plusRimS,
                            (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus0dot375minusRimS);
                }
                else {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                }
                mGauge.setmRangeFrame(rectF);
                float rangePointerPoisition = 0.0f;
                if (mGaugePointer instanceof RangePointer) {
                    rangePointerPoisition = (float) ((RangePointer) mGaugePointer).getOffset();
                }
                float factor = (mRangeFrame.left + (((mRangeFrame
                        .width() / 2.0f) - mRangeFrame.left) * ((float) radFactor))) + ((float) (((double) rangePointerPoisition) *
                        (mCentreX - mRimWidth)));
                if (mCentreY <= mCentreX) {
                    factor = (mRangeFrame.top + (((mRangeFrame
                            .height() / 2.0f) - mRangeFrame.top) * ((float) radFactor))) + ((float) (((double) rangePointerPoisition) *
                            (mCentreY - mRimWidth)));
                    if (gaugeType == GaugeType.West) {
                        rectF = getRectF(factor, (float) (getCenterXminusDivideBy4plusRimS(((double) factor))),
                                (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                                (float) (getCenterYplus0dot75minusRimS(((double) factor))));
                    }
                    else if (gaugeType == GaugeType.East) {
                        rectF = getRectF(factor, (float) (getCenterXminus0dot75plusRimS(((double) factor))),
                                (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                                (float) (getCenterYplusDivideBy4minusRimS(((double) factor))));
                    }
                    else if (gaugeType == GaugeType.North) {
                        rectF = getRectF((float) (getCenterYminusDivideBy4plusRimS(((double) factor))),
                                (float) (getCenterXminusDivideBy2plusRimS(((double) factor))),
                                (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                                (float) (getCenterYplus0dot75minusRimS(((double) factor))));
                    }
                    else if (gaugeType == GaugeType.South) {
                        rectF = getRectF((float) (getCenterYminus0dot75plusRimS(((double) factor))),
                                (float) (getCenterXminusDivideBy2plusRimS(((double) factor))),
                                (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                                (float) (getCenterYplusDivideBy4minusRimS(((double) factor))));
                    }
                    else if (gaugeType == GaugeType.NorthEast) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (gaugeType == GaugeType.NorthWest) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (gaugeType == GaugeType.SouthEast) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else if (gaugeType == GaugeType.SouthWest) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else {
                        rectF = getRectF(factor, (float) (getCenterXminusDivideBy2plusRimS(((double) factor))),
                                (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                                (float) (getCenterYplusDivideBy2minusRimS(((double) factor))));
                    }
                }
                else if (gaugeType == GaugeType.North) {
                    rectF = getRectF((float) (getCenterYminusDivideBy4plusRimS(((double) factor))), factor,
                            (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                            (float) (getCenterYplus0dot75minusRimS(((double) factor))));
                }
                else if (gaugeType == GaugeType.South) {
                    rectF = getRectF((float) (getCenterYminus0dot75plusRimS(((double) factor))), factor,
                            (float) (getCenterXplusDivideBy2minusRimS(((double) factor))),
                            (float) (getCenterYplusDivideBy4minusRimS(((double) factor))));
                }
                else if (gaugeType == GaugeType.West) {
                    rectF = getRectF((float) (getCenterYminusDivideBy2plusRimS(((double) factor))),
                            (float) (getCenterXminusDivideBy4plusRimS(((double) factor))),
                            (float) (getCenterXplus0dot75minusRimS(((double) factor))),
                            (float) (getCenterYplusDivideBy2minusRimS(((double) factor))));
                }
                else if (gaugeType == GaugeType.East) {
                    rectF = getRectF((float) (getCenterYminusDivideBy2plusRimS(((double) factor))),
                            (float) (getCenterXminus0dot75plusRimS(((double) factor))),
                            (float) (getCenterXplusDivideBy4minusRimS(((double) factor))),
                            (float) (getCenterYplusDivideBy2minusRimS(((double) factor))));
                }
                else if (gaugeType == GaugeType.NorthEast) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (gaugeType == GaugeType.NorthWest) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                            (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (gaugeType == GaugeType.SouthEast) {
                    rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                }
                else if (gaugeType == GaugeType.SouthWest) {
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
                    rimSize = (mMinSize - mRimWidth) - ((double) ((float) ((((double) ((float) ((RangePointer) mGaugePointer)
                            .getOffset())) * (mCentreX - mRimWidth)) - (mGaugePointer.getWidth() / 2.0d))));
                }
                if (mCentreY > mCentreX) {
                    if (gaugeType == GaugeType.North) {
                        rectF = getRectF((float) modif_centerYminusDivideBy4plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplus0dot75minusRimS);
                    }
                    else if (gaugeType == GaugeType.South) {
                        rectF = getRectF((float) modif_centerYminus0dot75plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy4minusRimS);
                    }
                    else if (gaugeType == GaugeType.West) {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) modif_centerXminusDivideBy4plusRimS,
                                (float) modif_centerXplus0dot75minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                    }
                    else if (gaugeType == GaugeType.East) {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) modif_centerXminus0dot75plusRimS,
                                (float) modif_centerXplusDivideBy4minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                    }
                    else if (gaugeType == GaugeType.NorthEast) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (gaugeType == GaugeType.NorthWest) {
                        rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                    }
                    else if (gaugeType == GaugeType.SouthEast) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                                (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else if (gaugeType == GaugeType.SouthWest) {
                        rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) modif_centerXminus0dot375plusRimS,
                                (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus0dot375minusRimS);
                    }
                    else {
                        rectF = getRectF((float) modif_centerYminusDivideBy2plusRimS, (float) rimSize,
                                (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                    }
                }
                else if (gaugeType == GaugeType.West) {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminusDivideBy4plusRimS, (float) modif_centerXplus0dot75minusRimS,
                            (float) modif_centerYplusDivideBy2minusRimS);
                }
                else if (gaugeType == GaugeType.East) {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminus0dot75plusRimS, (float) modif_centerXplusDivideBy4minusRimS,
                            (float) modif_centerYplusDivideBy2minusRimS);
                }
                else if (gaugeType == GaugeType.North) {
                    rectF = getRectF((float) modif_centerYminusDivideBy4plusRimS, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplus0dot75minusRimS);
                }
                else if (gaugeType == GaugeType.South) {
                    rectF = getRectF((float) modif_centerYminus0dot75plusRimS, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy4minusRimS);
                }
                else if (gaugeType == GaugeType.NorthEast) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (gaugeType == GaugeType.NorthWest) {
                    rectF = getRectF((float) modif_centerYminus0dot375plusRimS, (float) modif_centerXminus0dot375plusRimS,
                            (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus1dot125minusRimS);
                }
                else if (gaugeType == GaugeType.SouthEast) {
                    rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) moidf_centerXminus1dot125plusRimS,
                            (float) modif_centerXplus0dot375minusRimS, (float) modif_centerYplus0dot375minusRimS);
                }
                else if (gaugeType == GaugeType.SouthWest) {
                    rectF = getRectF((float) modif_centerYminus1dot125plusRimS, (float) modif_centerXminus0dot375plusRimS,
                            (float) modif_centerXplus1dot125minusRimS, (float) modif_centerYplus0dot375minusRimS);
                }
                else {
                    rectF = getRectF((float) rimSize, (float) modif_centerXminusDivideBy2plusRimS,
                            (float) modif_centerXplusDivideBy2minusRimS, (float) modif_centerYplusDivideBy2minusRimS);
                }
                mGauge.setmRangeFrame(mRangeFrame);
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
        Paint  paint = new Paint();
        int    color = mGaugePointer.getColor();
        double width = mGaugePointer.getWidth();
        paint.setAntiAlias(true);
        if (mGaugePointer instanceof RangePointer) {
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth((float) width);
            canvas.drawArc(rectF, (float) gaugeScale.getStartAngle(),
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
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth((float) width);
        double pointerLength = (mLabelsPathHeight / 2.0d) - (((NeedlePointer) mGaugePointer)
                .getLengthFactor() * (mLabelsPathHeight / 2.0d));
        double innerSize     = (mLabelsPathHeight / 2.0d) - pointerLength;
        double pointerMargin = (modif_minSizeDivideBy2 - (mLabelsPathHeight / 2.0d)) + pointerLength;
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
        double modif_centerYminusMinSizeDivideBy4     = mCentreY - modif_minSizeDivideBy4;
        double modif_centerXplusMinSizeDivideBy4      = mCentreX + modif_minSizeDivideBy4;
        double modif_centerYplus0dot375               = mCentreY + modif_minSizeMultBy0dot375;
        double modif_centerXplus0dot375               = mCentreX + modif_minSizeMultBy0dot375;
        double modif_centerXminusSizeDivideBy4        = mCentreX - modif_minSizeDivideBy4;
        double modif_centerXminus0dot375              = mCentreX - modif_minSizeMultBy0dot375;
        double modif_centerYminus0dot375              = mCentreY - modif_minSizeMultBy0dot375;

        if (gaugeType == GaugeType.West) {
            x0 = modif_centerXplusMinSizeDivideBy4 + modif_widthMultCos;
        }
        else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.SouthWest) {
            x0 = modif_centerXplus0dot375 + modif_widthMultCos;
        }
        else if (gaugeType == GaugeType.East) {
            x0 = modif_centerXminusSizeDivideBy4 + modif_widthMultCos;
        }
        else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.SouthEast) {
            x0 = modif_centerXminus0dot375 + modif_widthMultCos;
        }
        else {
            x0 = mCentreX + modif_widthMultCos;
        }

        if (gaugeType == GaugeType.North) {
            y0 = modif_centerXplusMinSizeDivideBy4 + modif_widthMultBySin;
        }
        else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.NorthWest) {
            y0 = modif_centerYplus0dot375 + modif_widthMultBySin;
        }
        else if (gaugeType == GaugeType.South) {
            y0 = modif_centerYminusMinSizeDivideBy4 + modif_widthMultBySin;
        }
        else if (gaugeType == GaugeType.SouthEast || gaugeType == GaugeType.SouthWest) {
            y0 = modif_centerYminus0dot375 + modif_widthMultBySin;
        }
        else {
            y0 = mCentreY + modif_widthMultBySin;
        }

        if (gaugeType == GaugeType.West) {
            x2 = modif_centerXplusMinSizeDivideBy4 + modif_dividedWidthMultByCos90plusAngle;
        }
        else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.SouthWest) {
            x2 = modif_centerXplus0dot375 + modif_dividedWidthMultByCos90plusAngle;
        }
        else if (gaugeType == GaugeType.East) {
            x2 = modif_centerXminusSizeDivideBy4 + modif_dividedWidthMultByCos90plusAngle;
        }
        else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.SouthEast) {
            x2 = modif_centerXminus0dot375 + modif_dividedWidthMultByCos90plusAngle;
        }
        else {
            x2 = mCentreX + modif_dividedWidthMultByCos90plusAngle;
        }

        if (gaugeType == GaugeType.North) {
            y2 = modif_centerXplusMinSizeDivideBy4 + modif_dividedWidthMultBySin90plusAngle;
        }
        else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.NorthEast) {
            y2 = modif_centerYplus0dot375 + modif_dividedWidthMultBySin90plusAngle;
        }
        else if (gaugeType == GaugeType.South) {
            y2 = modif_centerYminusMinSizeDivideBy4 + modif_dividedWidthMultBySin90plusAngle;
        }
        else if (gaugeType == GaugeType.SouthEast || gaugeType == GaugeType.SouthWest) {
            y2 = modif_centerYminus0dot375 + modif_dividedWidthMultBySin90plusAngle;
        }
        else {
            y2 = mCentreY + modif_dividedWidthMultBySin90plusAngle;
        }

        if (mCentreY > mCentreX) {
            if (gaugeType == GaugeType.West) {
                x1 = ((modif_centerXminusSizeDivideBy4 + innerSize) + modif_cosMultInnerSize) + pointerMargin;
            }
            else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.SouthWest) {
                x1 = ((getCenterXminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
            }
            else if (gaugeType == GaugeType.East) {
                x1 = ((getCenterXminus0dot75plusRimS(innerSize)) + modif_cosMultInnerSize) + pointerMargin;
            }
            else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.SouthEast) {
                x1 = (((mCentreX - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
            }
            else {
                x1 = (modif_cosMultInnerSize + innerSize) + pointerMargin;
            }
            if (gaugeType == GaugeType.North) {
                y1 = ((modif_centerYminusMinSizeDivideBy4 + innerSize) + modif_sinMultInnerSize) + pointerMargin;
            }
            else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.NorthWest) {
                y1 = ((getCenterYminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
            }
            else if (gaugeType == GaugeType.South) {
                y1 = ((getCenterYminus0dot75plusRimS(innerSize)) + modif_sinMultInnerSize) + pointerMargin;
            }
            else if (gaugeType == GaugeType.SouthEast || gaugeType == GaugeType.SouthWest) {
                y1 = (((mCentreY - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
            }
            else {
                y1 = ((getCenterYminusDivideBy2plusRimS(innerSize)) + modif_sinMultInnerSize) + pointerMargin;
            }
        }
        else {
            if (gaugeType == GaugeType.West) {
                x1 = ((modif_centerXminusSizeDivideBy4 + innerSize) + modif_cosMultInnerSize) + pointerMargin;
            }
            else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.SouthWest) {
                x1 = ((getCenterXminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
            }
            else if (gaugeType == GaugeType.East) {
                x1 = ((getCenterXminus0dot75plusRimS(innerSize)) + modif_cosMultInnerSize) + pointerMargin;
            }
            else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.SouthEast) {
                x1 = (((mCentreX - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
            }
            else {
                x1 = ((getCenterXminusDivideBy2plusRimS(innerSize)) + modif_cosMultInnerSize) + pointerMargin;
            }
            if (gaugeType == GaugeType.North) {
                y1 = ((modif_centerYminusMinSizeDivideBy4 + innerSize) + modif_sinMultInnerSize) + pointerMargin;
            }
            else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.NorthEast) {
                y1 = ((getCenterYminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
            }
            else if (gaugeType == GaugeType.South) {
                y1 = ((getCenterYminus0dot75plusRimS(innerSize)) + modif_sinMultInnerSize) + pointerMargin;
            }
            else if (gaugeType == GaugeType.SouthEast || gaugeType == GaugeType.SouthWest) {
                y1 = (((mCentreY - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
            }
            else {
                y1 = (modif_sinMultInnerSize + innerSize) + pointerMargin;
            }
        }
        if (((NeedlePointer) mGaugePointer).getType() != NeedleType.Bar) {
            Path path = new Path();
            path.moveTo((float) x0, (float) y0);
            path.lineTo((float) x1, (float) y1);
            path.lineTo((float) x2, (float) y2);
            paint.setStyle(Paint.Style.FILL);
            path.close();
            canvas.drawPath(path, paint);
        }
        else if (mCentreY > mCentreX) {
            if (gaugeType == GaugeType.North) {
                canvas.drawLine((float) mCentreX, (float) modif_centerXplusMinSizeDivideBy4, (float) x1, (float) y1, paint);
            }
            else if (gaugeType == GaugeType.South) {
                canvas.drawLine((float) mCentreX, (float) modif_centerYminusMinSizeDivideBy4, (float) x1, (float) y1, paint);
            }
            else if (gaugeType == GaugeType.West) {
                canvas.drawLine((float) modif_centerXplusMinSizeDivideBy4, (float) mCentreY, (float) x1, (float) y1, paint);
            }
            else if (gaugeType == GaugeType.East) {
                canvas.drawLine((float) modif_centerXminusSizeDivideBy4, (float) mCentreY, (float) x1, (float) y1, paint);
            }
            else if (gaugeType == GaugeType.NorthEast) {
                canvas.drawLine((float) modif_centerXminus0dot375, (float) modif_centerYplus0dot375, (float) x1, (float) y1, paint);
            }
            else if (gaugeType == GaugeType.NorthWest) {
                canvas.drawLine((float) modif_centerXplus0dot375, (float) modif_centerYplus0dot375, (float) x1, (float) y1, paint);
            }
            else if (gaugeType == GaugeType.SouthEast) {
                canvas.drawLine((float) modif_centerXminus0dot375, (float) modif_centerYminus0dot375, (float) x1, (float) y1, paint);
            }
            else if (gaugeType == GaugeType.SouthWest) {
                canvas.drawLine((float) modif_centerXplus0dot375, (float) modif_centerYminus0dot375, (float) x1, (float) y1, paint);
            }
            else {
                canvas.drawLine((float) mCentreX, (float) mCentreY, (float) x1, (float) y1, paint);
            }
        }
        else if (gaugeType == GaugeType.North) {
            canvas.drawLine((float) mCentreX, (float) modif_centerXplusMinSizeDivideBy4, (float) x1, (float) y1, paint);
        }
        else if (gaugeType == GaugeType.South) {
            canvas.drawLine((float) mCentreX, (float) modif_centerYminusMinSizeDivideBy4, (float) x1, (float) y1, paint);
        }
        else if (gaugeType == GaugeType.West) {
            canvas.drawLine((float) modif_centerXplusMinSizeDivideBy4, (float) mCentreY, (float) x1, (float) y1, paint);
        }
        else if (gaugeType == GaugeType.East) {
            canvas.drawLine((float) modif_centerXminusSizeDivideBy4, (float) mCentreY, (float) x1, (float) y1, paint);
        }
        else if (gaugeType == GaugeType.NorthEast) {
            canvas.drawLine((float) modif_centerXminus0dot375, (float) modif_centerYplus0dot375, (float) x1, (float) y1, paint);
        }
        else if (gaugeType == GaugeType.NorthWest) {
            canvas.drawLine((float) modif_centerXplus0dot375, (float) modif_centerYplus0dot375, (float) x1, (float) y1, paint);
        }
        else if (gaugeType == GaugeType.SouthEast) {
            canvas.drawLine((float) modif_centerXminus0dot375, (float) modif_centerYminus0dot375, (float) x1, (float) y1, paint);
        }
        else if (gaugeType == GaugeType.SouthWest) {
            canvas.drawLine((float) modif_centerXplus0dot375, (float) modif_centerYminus0dot375, (float) x1, (float) y1, paint);
        }
        else {
            canvas.drawLine((float) mCentreX, (float) mCentreY, (float) x1, (float) y1, paint);
        }

        paint.setColor(((NeedlePointer) mGaugePointer).getKnobColor());
        float knobRaiuds = (float) ((NeedlePointer) mGaugePointer).getKnobRadius();
        if (gaugeType == GaugeType.North) {
            canvas.drawCircle((float) mCentreX, (float) modif_centerXplusMinSizeDivideBy4, knobRaiuds, paint);
        }
        else if (gaugeType == GaugeType.South) {
            canvas.drawCircle((float) mCentreX, (float) modif_centerYminusMinSizeDivideBy4, knobRaiuds, paint);
        }
        else if (gaugeType == GaugeType.West) {
            canvas.drawCircle((float) modif_centerXplusMinSizeDivideBy4, (float) mCentreY, knobRaiuds, paint);
        }
        else if (gaugeType == GaugeType.East) {
            canvas.drawCircle((float) modif_centerXminusSizeDivideBy4, (float) mCentreY, knobRaiuds, paint);
        }
        else if (gaugeType == GaugeType.NorthEast) {
            canvas.drawCircle((float) modif_centerXminus0dot375, (float) modif_centerYplus0dot375, knobRaiuds, paint);
        }
        else if (gaugeType == GaugeType.NorthWest) {
            canvas.drawCircle((float) modif_centerXplus0dot375, (float) modif_centerYplus0dot375, knobRaiuds, paint);
        }
        else if (gaugeType == GaugeType.SouthEast) {
            canvas.drawCircle((float) modif_centerXminus0dot375, (float) modif_centerYminus0dot375, knobRaiuds, paint);
        }
        else if (gaugeType == GaugeType.SouthWest) {
            canvas.drawCircle((float) modif_centerXplus0dot375, (float) modif_centerYminus0dot375, knobRaiuds, paint);
        }
        else {
            canvas.drawCircle((float) mCentreX, (float) mCentreY, knobRaiuds, paint);
        }
    }

    private double getPointerAngle(double pointerValue, GaugeScale gaugeScale) {
        double endVal = gaugeScale.getEndValue();
        if (pointerValue > endVal) {
            pointerValue = endVal;
        }
        double startVal   = gaugeScale.getStartValue();
        double startAngle = gaugeScale.getStartAngle() * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
        if (pointerValue < startVal) {
            pointerValue = startVal;
        }
        return startAngle + (((pointerValue - startVal) * (gaugeScale.getSweepAngle() / (gaugeScale
                .getEndValue() - startVal))) * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY);
    }

    private double getCenterXminusDivideBy2plusRimS(double rimSize) {
        return (mCentreX - modif_minSizeDivideBy2) + rimSize;
    }

    private double getCenterYplusDivideBy2minusRimS(double rimSize) {
        return (mCentreY + modif_minSizeDivideBy2) - rimSize;
    }

    private double getCenterYplus0dot375minusRimS(double rimSize) {
        return (mCentreY + modif_minSizeMultBy0dot375) - rimSize;
    }

    private double getCenterYminus1dot125plusRimS(double rimSize) {
        return (mCentreY - modif_minSizeMultBy1dot125) + rimSize;
    }

    private double getCenterXplus1dot125minusRimS(double rimSize) {
        return (mCentreX + modif_minSizeMultBy1dot125) - rimSize;
    }

    private double getCenterXminus0dot375plusRimS(double rimSize) {
        return (mCentreX - modif_minSizeMultBy0dot375) + rimSize;
    }

    private double getCenterYplus1dot125minusRimS(double rimSize) {
        return (mCentreY + modif_minSizeMultBy1dot125) - rimSize;
    }

    private double getCenterXplus0dot375minusRimS(double rimSize) {
        return (mCentreX + modif_minSizeMultBy0dot375) - rimSize;
    }

    private double getCenterYminus0dot375plusRimS(double rimSize) {
        return (mCentreY - modif_minSizeMultBy0dot375) + rimSize;
    }

    private double getCenterXplusDivideBy4minusRimS(double rimSize) {
        return (mCentreX + modif_minSizeDivideBy4) - rimSize;
    }

    private double getCenterXminus1dot125plusRimS(double rimSize) {
        return (mCentreX - modif_minSizeMultBy1dot125) + rimSize;
    }

    private double getCenterXminus0dot75plusRimS(double rimSize) {
        return (mCentreX - modif_minSizeMultBy0dot75) + rimSize;
    }

    private double getCenterYplusDivideBy4minusRimS(double rimSize) {
        return (mCentreY + modif_minSizeDivideBy4) - rimSize;
    }

    private double getCenterXplus0dot75minusRimS(double rimSize) {
        return (mCentreX + modif_minSizeMultBy0dot75) - rimSize;
    }

    private double getCenterYminusDivideBy2plusRimS(double rimSize) {
        return (mCentreY - modif_minSizeDivideBy2) + rimSize;
    }

    private double getCenterXminusDivideBy4plusRimS(double rimSize) {
        return (mCentreX - modif_minSizeDivideBy4) + rimSize;
    }

    private double getCenterYminus0dot75plusRimS(double rimSize) {
        return (mCentreY - modif_minSizeMultBy0dot75) + rimSize;
    }

    private double getCenterYplus0dot75minusRimS(double rimSize) {
        return (mCentreY + modif_minSizeMultBy0dot75) - rimSize;
    }

    private double getCenterXplusDivideBy2minusRimS(double rimSize) {
        return (mCentreX + modif_minSizeDivideBy2) - rimSize;
    }

    private double getCenterYminusDivideBy4plusRimS(double rimSize) {
        return (mCentreY - modif_minSizeDivideBy4) + rimSize;
    }
}
