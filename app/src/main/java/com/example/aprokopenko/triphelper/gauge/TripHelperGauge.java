package com.example.aprokopenko.triphelper.gauge;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.example.aprokopenko.triphelper.gauge.enums.GaugeType;

import java.util.ArrayList;
import java.util.Iterator;

public class TripHelperGauge extends FrameLayout {
    com.example.aprokopenko.triphelper.gauge.enums.GaugeType GaugeType;
    static float DENSITY = -1.0F;
    private final double mGap;

    private final ArrayList<ScaleRenderer> mScaleRenderers;
    private final ArrayList<PointerRender> mPointerRenderes;
    private       ArrayList<GaugeScale>    gaugeScales;
    ArrayList<Header> headers;

    private GaugeHeaderRenderer mGaugeHeader;

    RectF mVisualRect;
    RectF mRangeFrame;
    private int    frameBackgroundColor;
    double mGaugeHeight;
    double mGaugeWidth;
    double mMinSize;
    double mInnerBevelWidth;
    double mKnobDiameter;
    double mRimWidth;
    double mLabelsPathHeight;
    double mRangePathWidth;
    double mCentreX;
    double mCentreY;

    public TripHelperGauge(Context context) {
        this(context, null);
        DENSITY = this.getContext().getResources().getDisplayMetrics().density;
    }

    public TripHelperGauge(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleRenderers = new ArrayList<>();
        mPointerRenderes = new ArrayList<>();
        gaugeScales = new ArrayList<>();
        GaugeType = com.example.aprokopenko.triphelper.gauge.enums.GaugeType.Default;
        frameBackgroundColor = GaugeConstants.FRAME_BACKGROUND_COLOR;
        headers = new ArrayList<>();
        mGaugeHeight = GaugeConstants.ZERO;
        mGaugeWidth = GaugeConstants.ZERO;
        mGap = 10.0D;
        DENSITY = this.getContext().getResources().getDisplayMetrics().density;
    }

    private void init() {
        mGaugeHeader = new GaugeHeaderRenderer(this.getContext());
        mGaugeHeader.mGauge = this;
        this.addView(this.mGaugeHeader);
    }

    public ArrayList<GaugeScale> getGaugeScales() {
        return this.gaugeScales;
    }

    public void setGaugeScales(ArrayList<GaugeScale> gaugeScales) {
        this.gaugeScales = gaugeScales;
        this.refreshGauge();
        if (this.mGaugeHeader == null) {
            this.init();
        }
    }

    private GaugeType getGaugeType() {
        return this.GaugeType;
    }

    public void setGaugeType(GaugeType gaugeType) {
        this.GaugeType = gaugeType;
        this.refreshGauge();
    }

    protected int getFrameBackgroundColor() {
        return this.frameBackgroundColor;
    }

    protected void setFrameBackgroundColor(int frameBackgroundColor) {
        this.frameBackgroundColor = frameBackgroundColor;
        this.refreshGauge();
    }

    public ArrayList<Header> getHeaders() {
        return this.headers;
    }

    public void setHeaders(ArrayList<Header> headers) {
        this.headers = headers;
        this.refreshGauge();
    }

    public void refreshGauge() {
        calculateMargin(mGaugeHeight, mGaugeWidth);
        mGaugeHeader = new GaugeHeaderRenderer(this.getContext());
        mGaugeHeader.mGauge = this;
        this.addView(mGaugeHeader);
        ArrayList scaleCircularPointers = new ArrayList();
        ArrayList removeScaleRen        = new ArrayList();
        ArrayList removedPointerRender  = new ArrayList();

        for (Object i$ : mScaleRenderers) {
            removeScaleRen.add(i$);
        }

        Iterator i$1 = gaugeScales.iterator();

        Iterator anim;
        label91:
        while (i$1.hasNext()) {
            GaugeScale    poinRen  = (GaugeScale) i$1.next();
            ScaleRenderer pointRen = null;
            anim = mScaleRenderers.iterator();

            while (true) {
                if (anim.hasNext()) {
                    ScaleRenderer scRender = (ScaleRenderer) anim.next();
                    if (scRender.gaugeScale != poinRen) {
                        continue;
                    }

                    pointRen = scRender;
                }

                if (pointRen == null) {
                    ScaleRenderer anim1 = new ScaleRenderer(this.getContext(), this, poinRen);
                    mScaleRenderers.add(anim1);
                    this.addView(anim1);
                }
                else {
                    removeScaleRen.remove(pointRen);
                    pointRen.invalidate();
                }

                anim = poinRen.GaugePointers.iterator();

                while (true) {
                    if (!anim.hasNext()) {
                        continue label91;
                    }

                    GaugePointer scRender1 = (GaugePointer) anim.next();
                    scRender1.mGaugeScale = poinRen;
                    scRender1.mBaseGauge = this;
                    scaleCircularPointers.add(scRender1);
                }
            }
        }

        i$1 = removeScaleRen.iterator();

        while (i$1.hasNext()) {
            ScaleRenderer poinRen1 = (ScaleRenderer) i$1.next();
            this.removeView(poinRen1);
        }

        i$1 = mPointerRenderes.iterator();

        PointerRender poinRen2;
        while (i$1.hasNext()) {
            poinRen2 = (PointerRender) i$1.next();
            removedPointerRender.add(poinRen2);
        }

        i$1 = scaleCircularPointers.iterator();

        while (i$1.hasNext()) {
            GaugePointer  poinRen3  = (GaugePointer) i$1.next();
            PointerRender pointRen1 = null;
            anim = mPointerRenderes.iterator();

            while (true) {
                if (anim.hasNext()) {
                    PointerRender scRender2 = (PointerRender) anim.next();
                    if (scRender2.mGaugePointer != poinRen3) {
                        continue;
                    }

                    pointRen1 = scRender2;
                }

                if (pointRen1 == null) {
                    PointerRender anim2 = new PointerRender(this.getContext(), this, poinRen3.mGaugeScale, poinRen3);
                    mPointerRenderes.add(anim2);
                    poinRen3.mPointerRender = anim2;
                    this.addView(anim2);
                }
                else {
                    removedPointerRender.remove(pointRen1);
                    ObjectAnimator anim3 = ObjectAnimator.ofFloat(pointRen1, "", pointRen1.value, (float) poinRen3.value);
                    anim3.setDuration(GaugeConstants.GAUGE_ANIMATION_TIME);
                    anim3.start();
                    pointRen1.invalidate();
                }
                break;
            }
        }

        i$1 = removedPointerRender.iterator();

        while (i$1.hasNext()) {
            poinRen2 = (PointerRender) i$1.next();
            removeView(poinRen2);
        }

        if (mGaugeHeader != null) {
            mGaugeHeader.mGauge = this;
            mGaugeHeader.requestLayout();
            mGaugeHeader.invalidate();
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mVisualRect = new RectF();
        h = h < 50 ? 50 : h;
        w = w < 50 ? 50 : w;
        double mAvailableHeight = (double) h;
        double mAvailableWidth = (double) w;
        if (mGaugeHeight > GaugeConstants.ZERO) {
            mGaugeHeight = mGaugeHeight > mAvailableHeight ? mAvailableHeight : mGaugeHeight;
        }
        else {
            mGaugeHeight = mAvailableHeight;
        }

        if (mGaugeWidth > GaugeConstants.ZERO) {
            mGaugeWidth = mGaugeWidth > mAvailableWidth ? mAvailableWidth : mGaugeWidth;
        }
        else {
            mGaugeWidth = mAvailableWidth;
        }

        GaugeType gaugeType;
        double    squarified;
        double    rectCanvas;

        //modif optimization
        double modifDiameter25 = 25.0D;

        label141:
        {
            squarified = Math.min(mGaugeHeight, mGaugeWidth);
            gaugeType = getGaugeType();
            //optimizationModif
            double modifKnobDiameterBy4 = mKnobDiameter * 4.0D;

            if (gaugeType != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.North) {
                gaugeType = getGaugeType();
                if (gaugeType != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.South) {
                    if (mKnobDiameter == GaugeConstants.ZERO) {
                        rectCanvas = Math.max(mGaugeHeight - 30.0D, mGaugeWidth);
                    }
                    else if (mKnobDiameter <= modifDiameter25) {
                        rectCanvas = Math.max(mGaugeHeight - modifKnobDiameterBy4, mGaugeWidth);
                    }
                    else {
                        rectCanvas = Math.max(mGaugeHeight - 100.0D, mGaugeWidth);
                    }
                    break label141;
                }
            }

            if (mKnobDiameter == GaugeConstants.ZERO) {
                rectCanvas = Math.max(mGaugeHeight, mGaugeWidth - 30.0D);
            }
            else if (mKnobDiameter <= modifDiameter25) {
                rectCanvas = Math.max(mGaugeHeight, mGaugeWidth - modifKnobDiameterBy4);
            }
            else {
                rectCanvas = Math.max(mGaugeHeight, mGaugeWidth - 100.0D);
            }
        }

        label145:
        {
            double initAvalHeight = mAvailableHeight;
            double initAvalWidth  = mAvailableWidth;
            if (mAvailableWidth > mAvailableHeight) {
                label134:
                {
                    gaugeType = getGaugeType();
                    if (gaugeType != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.North) {
                        gaugeType = getGaugeType();
                        if (gaugeType != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.South) {
                            break label134;
                        }
                    }

                    if (mAvailableWidth / 2.0D > mAvailableHeight) {
                        if (mKnobDiameter == GaugeConstants.ZERO) {
                            //optimizationModif
                            double modifHeightMinus20         = mAvailableHeight - 20.0D;
                            double modifHeightMinus10         = mAvailableHeight - 10.0D;
                            double modifHeightMinus10DelimBy2 = modifHeightMinus10 / 2.0D;

                            mAvailableWidth = mAvailableHeight * 2.0D - 20.0D;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.North) {
                                mVisualRect.top = (float) (modifHeightMinus10DelimBy2 - modifHeightMinus10);
                            }
                            else {
                                gaugeType = getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.South) {
                                    mVisualRect.top = (float) (modifHeightMinus10DelimBy2 - modifHeightMinus20);
                                }
                            }
                            mVisualRect.left = (float) ((initAvalWidth - 20.0D) / 2.0D - modifHeightMinus20);
                        }
                        else if (this.mKnobDiameter <= modifDiameter25) {
                            //optimizationModif
                            double modifKnobDiameterBy2         = mKnobDiameter * 2.0D;
                            double modifHeightMinusKnobBy2      = mAvailableHeight - modifKnobDiameterBy2;
                            double modifHeightMinusKnob         = mAvailableHeight - mKnobDiameter;
                            double modifHeightMinusKnobDelimBy2 = (modifHeightMinusKnob) / 2.0D;

                            mAvailableWidth = (modifHeightMinusKnob) * 2.0D;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.North) {
                                mVisualRect.top = (float) (modifHeightMinusKnobDelimBy2 - (modifHeightMinusKnob));
                            }
                            else {
                                gaugeType = getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.South) {
                                    mVisualRect.top = (float) (modifHeightMinusKnobDelimBy2 - modifHeightMinusKnobBy2);
                                }
                            }
                            mVisualRect.left = (float) ((initAvalWidth - modifKnobDiameterBy2) / 2.0D - modifHeightMinusKnobBy2);
                        }
                        else {
                            //optimizationModif
                            double modifHeightMinus50       = mAvailableHeight - 50.0D;
                            double modifHeightMinus25       = mAvailableHeight - 25.0D;
                            double modifHeightMinus25delBy2 = modifHeightMinus25 / 2.0D;

                            mAvailableWidth = mAvailableHeight * 2.0D - 50.0D;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.North) {
                                mVisualRect.top = (float) (modifHeightMinus25delBy2 - modifHeightMinus25);
                            }
                            else {
                                gaugeType = getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.South) {
                                    mVisualRect.top = (float) (modifHeightMinus25delBy2 - modifHeightMinus50);
                                }
                            }
                            mVisualRect.left = (float) ((initAvalWidth - 50.0D) / 2.0D - modifHeightMinus50);
                        }
                        mVisualRect.bottom = (float) ((double) mVisualRect.top + mAvailableWidth);
                        mVisualRect.right = (float) ((double) mVisualRect.left + mAvailableWidth);
                    }
                    else {
                        //optimizationModif
                        double modifRectCanvasDelimBy2 = rectCanvas / 2.0;

                        mVisualRect.top = (float) (mAvailableHeight / 2.0D - modifRectCanvasDelimBy2);
                        mVisualRect.left = (float) (mAvailableWidth / 2.0D - modifRectCanvasDelimBy2);
                        mVisualRect.bottom = (float) ((double) mVisualRect.top + rectCanvas);
                        mVisualRect.right = (float) ((double) mVisualRect.left + rectCanvas);
                    }
                    break label145;
                }
            }

            if (mAvailableWidth < mAvailableHeight) {
                label127:
                {
                    gaugeType = getGaugeType();
                    if (gaugeType != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.East) {
                        gaugeType = getGaugeType();
                        if (gaugeType != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.West) {
                            break label127;
                        }
                    }

                    if (mAvailableHeight / 2.0D > mAvailableWidth) {
                        //optimizationModif
                        double modifAvaliableWidthMinus20 = (mAvailableWidth - 20.0D);
                        double modifAvaliableWidthMinus10 = mAvailableWidth - 10.0D;


                        if (mKnobDiameter == GaugeConstants.ZERO) {
                            mAvailableHeight = (modifAvaliableWidthMinus10) * 2.0D;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.East) {
                                mVisualRect.left = (float) (modifAvaliableWidthMinus10 / 2.0D - modifAvaliableWidthMinus20);
                            }
                            else {
                                gaugeType = getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.West) {
                                    mVisualRect.left = (float) (modifAvaliableWidthMinus10 / 2.0D - modifAvaliableWidthMinus10);
                                }
                            }

                            mVisualRect.top = (float) ((initAvalHeight - 10.0D) / 2.0D - modifAvaliableWidthMinus20);
                        }
                        else if (mKnobDiameter <= modifDiameter25) {
                            //optimizationModif
                            double modifWidthKnobBy2        = mAvailableWidth - mKnobDiameter * 2.0D;
                            double modifWidthKnobDelimeter2 = (mAvailableWidth - mKnobDiameter) / 2.0D;

                            mAvailableHeight = (mAvailableWidth - mKnobDiameter) * 2.0D;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.East) {
                                mVisualRect.left = (float) (modifWidthKnobDelimeter2 - modifWidthKnobBy2);
                            }
                            else {
                                gaugeType = this.getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.West) {
                                    mVisualRect.left = (float) (modifWidthKnobDelimeter2 - (mAvailableWidth - mKnobDiameter));
                                }
                            }
                            mVisualRect.top = (float) ((initAvalHeight - mKnobDiameter) / 2.0D - modifWidthKnobBy2);
                        }
                        else {
                            //optimizationModif
                            double modifAvaliableWidth = ((mAvailableWidth - modifDiameter25) / 2.0D);

                            mAvailableHeight = (mAvailableWidth - modifDiameter25) * 2.0D;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.East) {
                                mVisualRect.left = (float) (modifAvaliableWidth - (mAvailableWidth - 50.0D));
                            }
                            else {
                                gaugeType = getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.West) {
                                    mVisualRect.left = (float) (modifAvaliableWidth - (mAvailableWidth - modifDiameter25));
                                }
                            }

                            mVisualRect.top = (float) ((initAvalHeight - modifDiameter25) / 2.0D - (mAvailableWidth - 50.0D));
                        }

                        mVisualRect.bottom = (float) ((double) mVisualRect.top + mAvailableHeight);
                        mVisualRect.right = (float) ((double) mVisualRect.left + mAvailableHeight);
                    }
                    else {
                        //optimizationModif
                        double modifRectCanvas = 2.0D - rectCanvas / 2.0D;

                        mVisualRect.top = (float) (mAvailableHeight / modifRectCanvas);
                        mVisualRect.left = (float) (mAvailableWidth / modifRectCanvas);
                        mVisualRect.bottom = (float) ((double) mVisualRect.top + rectCanvas);
                        mVisualRect.right = (float) ((double) mVisualRect.left + rectCanvas);
                    }
                    break label145;
                }
            }

            //optimizationModif
            double modifDelimeter = 2.0D - squarified / 2.0D;

            mVisualRect.top = (float) (mAvailableHeight / modifDelimeter);
            mVisualRect.left = (float) (mAvailableWidth / modifDelimeter);
            mVisualRect.bottom = (float) ((double) mVisualRect.top + squarified);
            mVisualRect.right = (float) ((double) mVisualRect.left + squarified);
        }

        mMinSize = (double) Math.min(mVisualRect.height(), mVisualRect.width());
        double mScaleCentreY = (double) (mVisualRect.top + mVisualRect.height() / 2.0F);
        double mScaleCentreX = (double) (mVisualRect.left + mVisualRect.width() / 2.0F);
        mCentreY = mScaleCentreY;
        mCentreX = mScaleCentreX;
        double mOuterBevelHeight = (double) mVisualRect.height();
        double mOuterBevelWidth  = (double) mVisualRect.width();
        double dimensionVar = mOuterBevelHeight;
        double mInnerBevelHeight = dimensionVar + 10.0D;
        dimensionVar = mOuterBevelWidth;
        mInnerBevelWidth = dimensionVar + 10.0D;
        calculateMargin(mInnerBevelWidth, mInnerBevelWidth);
    }

    void calculateMargin(double height, double width) {
        //optimizationModif
        double marginSubtrahend = 2.0D * 10.0D;

        double mRimHeight = height - 10.0D;
        mRimWidth = width - 10.0D;

        double dimensionVariable = mRimHeight;
        double mTicksPathHeight  = dimensionVariable - marginSubtrahend;
        dimensionVariable = mRimWidth;
        double mTicksPathWidth = dimensionVariable - marginSubtrahend;
        dimensionVariable = mTicksPathHeight;
        mLabelsPathHeight = dimensionVariable - marginSubtrahend;
        dimensionVariable = mTicksPathWidth;
        double mLabelsPathWidth = dimensionVariable - marginSubtrahend;
        dimensionVariable = mLabelsPathHeight;
        double mRangePathHeight = dimensionVariable - marginSubtrahend;
        dimensionVariable = mLabelsPathWidth;
        mRangePathWidth = dimensionVariable - marginSubtrahend;
    }
}

