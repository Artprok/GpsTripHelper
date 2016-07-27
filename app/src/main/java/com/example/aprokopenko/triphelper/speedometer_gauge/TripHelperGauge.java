package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType;

import java.util.ArrayList;
import java.util.Iterator;

public class TripHelperGauge extends FrameLayout {
    static float DENSITY = -1.0F;
    private final ArrayList<ScaleRenderer> mScaleRenderers;
    private final ArrayList<PointerRender> mPointerRenderes;
    private       GaugeType                GaugeType;
    private       ArrayList<GaugeScale>    gaugeScales;
    private       ArrayList<Header>        headers;
    private       GaugeHeaderRenderer      mGaugeHeader;
    private       RectF                    mVisualRect;
    private       RectF                    mRangeFrame;
    private       int                      frameBackgroundColor;
    private       float                    mGaugeHeight;
    private       float                    mGaugeWidth;
    private       double                   mMinSize;
    private       double                   mInnerBevelWidth;
    private       float                    mKnobDiameter;
    private       double                   mRimWidth;
    private       double                   mLabelsPathHeight;
    private       double                   mRangePathWidth;
    private       double                   mCentreX;
    private       double                   mCentreY;

    public TripHelperGauge(Context context) {
        this(context, null);
        DENSITY = this.getContext().getResources().getDisplayMetrics().density;
    }

    public TripHelperGauge(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleRenderers = new ArrayList<>();
        mPointerRenderes = new ArrayList<>();
        gaugeScales = new ArrayList<>();
        GaugeType = com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.Default;
        frameBackgroundColor = GaugeConstants.FRAME_BACKGROUND_COLOR;
        headers = new ArrayList<>();
        mGaugeHeight = (float) GaugeConstants.ZERO;
        mGaugeWidth = (float) GaugeConstants.ZERO;
        DENSITY = getContext().getResources().getDisplayMetrics().density;
    }

    public ArrayList<GaugeScale> getGaugeScales() {
        return gaugeScales;
    }

    public void setGaugeScales(ArrayList<GaugeScale> gaugeScales) {
        this.gaugeScales = gaugeScales;
        refreshGauge();
        if (mGaugeHeader == null) {
            init();
        }
    }

    public void refreshGauge() {
        calculateMargin(mGaugeWidth);
        mGaugeHeader = new GaugeHeaderRenderer(this.getContext());
        mGaugeHeader.setmGauge(this);
        this.addView(mGaugeHeader);
        ArrayList<GaugePointer>  scaleCircularPointers = new ArrayList<>();
        ArrayList<ScaleRenderer> removeScaleRen        = new ArrayList<>();
        ArrayList<PointerRender> removedPointerRender  = new ArrayList<>();

        for (ScaleRenderer scaleRenderer : mScaleRenderers) {
            removeScaleRen.add(scaleRenderer);
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
                    if (scRender.getGaugeScale() != poinRen) {
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

                anim = poinRen.getGaugePointers().iterator();

                while (true) {
                    if (!anim.hasNext()) {
                        continue label91;
                    }

                    GaugePointer scRender1 = (GaugePointer) anim.next();
                    scRender1.setmGaugeScale(poinRen);
                    scRender1.setmBaseGauge(this);
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
                    if (scRender2.getmGaugePointer() != poinRen3) {
                        continue;
                    }

                    pointRen1 = scRender2;
                }

                if (pointRen1 == null) {
                    PointerRender anim2 = new PointerRender(this.getContext(), this, poinRen3.getmGaugeScale(), poinRen3);
                    mPointerRenderes.add(anim2);
                    poinRen3.setmPointerRender(anim2);
                    this.addView(anim2);
                }
                else {
                    removedPointerRender.remove(pointRen1);
                    ObjectAnimator anim3 = ObjectAnimator.ofFloat(pointRen1, "", pointRen1.getValue(), (float) poinRen3.getValue());
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
            mGaugeHeader.setmGauge(this);
            mGaugeHeader.requestLayout();
            mGaugeHeader.invalidate();
        }
    }

    private void init() {
        mGaugeHeader = new GaugeHeaderRenderer(getContext());
        mGaugeHeader.setmGauge(this);
        addView(mGaugeHeader);
    }

    public void calculateMargin(double width) {
        //optimizationModif
        double marginSubtrahend = 2.0 * 10.0;

        mRimWidth = width - 10.0;
        double dimensionVariable = (mRimWidth - marginSubtrahend) - marginSubtrahend;

        mLabelsPathHeight = dimensionVariable;
        mRangePathWidth = dimensionVariable;
    }

    public RectF getmVisualRect() {
        return mVisualRect;
    }

    public double getmCentreX() {
        return mCentreX;
    }

    public double getmCentreY() {
        return mCentreY;
    }

    public double getmGaugeHeight() {
        return mGaugeHeight;
    }

    public double getmGaugeWidth() {
        return mGaugeWidth;
    }

    public double getmMinSize() {
        return mMinSize;
    }

    public RectF getmRangeFrame() {
        return mRangeFrame;
    }

    public void setmRangeFrame(RectF mRangeFrame) {
        this.mRangeFrame = mRangeFrame;
    }

    public double getmInnerBevelWidth() {
        return mInnerBevelWidth;
    }

    public double getmRimWidth() {
        return mRimWidth;
    }

    public double getmKnobDiameter() {
        return mKnobDiameter;
    }

    public void setmKnobDiameter(double mKnobDiameter) {
        this.mKnobDiameter = (float) mKnobDiameter;
    }

    public double getmLabelsPathHeight() {
        return mLabelsPathHeight;
    }


    public double getmRangePathWidth() {
        return mRangePathWidth;
    }

    protected int getFrameBackgroundColor() {
        return this.frameBackgroundColor;
    }

    protected void setFrameBackgroundColor(int frameBackgroundColor) {
        this.frameBackgroundColor = frameBackgroundColor;
        this.refreshGauge();
    }

    public ArrayList<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(ArrayList<Header> headers) {
        this.headers = headers;
        this.refreshGauge();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mVisualRect = new RectF();
        h = h < 50 ? 50 : h;
        w = w < 50 ? 50 : w;
        float mAvailableHeight = h;
        float mAvailableWidth;
        if (h > w) {
            mAvailableWidth = w;
        }
        else {
            mAvailableWidth = mAvailableHeight;
        }
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
        float     squarified;
        float     rectCanvas;

        //modif optimization
        float modifDiameter25 = 25.0f;

        label141:
        {
            squarified = Math.min(mGaugeHeight, mGaugeWidth);
            gaugeType = getGaugeType();
            //optimizationModif
            float modifKnobDiameterBy4 = mKnobDiameter * 4.0f;

            if (gaugeType != com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.North) {
                gaugeType = getGaugeType();
                if (gaugeType != com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.South) {
                    if (mKnobDiameter == GaugeConstants.ZERO) {
                        rectCanvas = Math.max(mGaugeHeight - 30.0f, mGaugeWidth);
                    }
                    else if (mKnobDiameter <= modifDiameter25) {
                        rectCanvas = Math.max(mGaugeHeight - modifKnobDiameterBy4, mGaugeWidth);
                    }
                    else {
                        rectCanvas = Math.max(mGaugeHeight - 100.0f, mGaugeWidth);
                    }
                    break label141;
                }
            }

            if (mKnobDiameter == GaugeConstants.ZERO) {
                rectCanvas = Math.max(mGaugeHeight, mGaugeWidth - 30.0f);
            }
            else if (mKnobDiameter <= modifDiameter25) {
                rectCanvas = Math.max(mGaugeHeight, mGaugeWidth - modifKnobDiameterBy4);
            }
            else {
                rectCanvas = Math.max(mGaugeHeight, mGaugeWidth - 100.0f);
            }
        }

        label145:
        {
            float initAvalHeight = mAvailableHeight;
            float initAvalWidth  = mAvailableWidth;
            if (mAvailableWidth > mAvailableHeight) {
                label134:
                {
                    gaugeType = getGaugeType();
                    if (gaugeType != com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.North) {
                        gaugeType = getGaugeType();
                        if (gaugeType != com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.South) {
                            break label134;
                        }
                    }

                    if (mAvailableWidth / 2.0D > mAvailableHeight) {
                        if (mKnobDiameter == GaugeConstants.ZERO) {
                            //optimizationModif
                            float modifHeightMinus20         = mAvailableHeight - 20.0f;
                            float modifHeightMinus10         = mAvailableHeight - 10.0f;
                            float modifHeightMinus10DelimBy2 = modifHeightMinus10 / 2.0f;

                            mAvailableWidth = mAvailableHeight * 2.0f - 20.0f;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.North) {
                                mVisualRect.top = modifHeightMinus10DelimBy2 - modifHeightMinus10;
                            }
                            else {
                                gaugeType = getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.South) {
                                    mVisualRect.top = modifHeightMinus10DelimBy2 - modifHeightMinus20;
                                }
                            }
                            mVisualRect.left = (initAvalWidth - 20.0f) / 2.0f - modifHeightMinus20;
                        }
                        else if (this.mKnobDiameter <= modifDiameter25) {
                            //optimizationModif
                            float modifKnobDiameterBy2         = (float) (mKnobDiameter * 2.0);
                            float modifHeightMinusKnobBy2      = mAvailableHeight - modifKnobDiameterBy2;
                            float modifHeightMinusKnob         = mAvailableHeight - mKnobDiameter;
                            float modifHeightMinusKnobDelimBy2 = (modifHeightMinusKnob) / 2.0f;

                            mAvailableWidth = (modifHeightMinusKnob) * 2.0f;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.North) {
                                mVisualRect.top = modifHeightMinusKnobDelimBy2 - (modifHeightMinusKnob);
                            }
                            else {
                                gaugeType = getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.South) {
                                    mVisualRect.top = modifHeightMinusKnobDelimBy2 - modifHeightMinusKnobBy2;
                                }
                            }
                            mVisualRect.left = (initAvalWidth - modifKnobDiameterBy2) / 2.0f - modifHeightMinusKnobBy2;
                        }
                        else {
                            //optimizationModif
                            float modifHeightMinus50       = mAvailableHeight - 50.0f;
                            float modifHeightMinus25       = mAvailableHeight - 25.0f;
                            float modifHeightMinus25delBy2 = modifHeightMinus25 / 2.0f;

                            mAvailableWidth = mAvailableHeight * 2.0f - 50.0f;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.North) {
                                mVisualRect.top = modifHeightMinus25delBy2 - modifHeightMinus25;
                            }
                            else {
                                gaugeType = getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.South) {
                                    mVisualRect.top = modifHeightMinus25delBy2 - modifHeightMinus50;
                                }
                            }
                            mVisualRect.left = (initAvalWidth - 50.0f) / 2.0f - modifHeightMinus50;
                        }
                        mVisualRect.bottom = mVisualRect.top + mAvailableWidth;
                        mVisualRect.right = mVisualRect.left + mAvailableWidth;
                    }
                    else {
                        //optimizationModif
                        float modifRectCanvasDelimBy2 = (float) (rectCanvas / 2.0);

                        mVisualRect.top = mAvailableHeight / 2.0f - modifRectCanvasDelimBy2;
                        mVisualRect.left = mAvailableWidth / 2.0f - modifRectCanvasDelimBy2;
                        mVisualRect.bottom = mVisualRect.top + rectCanvas;
                        mVisualRect.right = mVisualRect.left + rectCanvas;
                    }
                    break label145;
                }
            }

            if (mAvailableWidth < mAvailableHeight) {
                label127:
                {
                    gaugeType = getGaugeType();
                    if (gaugeType != com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.East) {
                        gaugeType = getGaugeType();
                        if (gaugeType != com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.West) {
                            break label127;
                        }
                    }

                    if (mAvailableHeight / 2.0D > mAvailableWidth) {
                        //optimizationModif
                        float modifAvaliableWidthMinus20 = (mAvailableWidth - 20.0f);
                        float modifAvaliableWidthMinus10 = mAvailableWidth - 10.0f;


                        if (mKnobDiameter == GaugeConstants.ZERO) {
                            mAvailableHeight = (modifAvaliableWidthMinus10) * 2.0f;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.East) {
                                mVisualRect.left = modifAvaliableWidthMinus10 / 2.0f - modifAvaliableWidthMinus20;
                            }
                            else {
                                gaugeType = getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.West) {
                                    mVisualRect.left = modifAvaliableWidthMinus10 / 2.0f - modifAvaliableWidthMinus10;
                                }
                            }

                            mVisualRect.top = (initAvalHeight - 10.0f) / 2.0f - modifAvaliableWidthMinus20;
                        }
                        else if (mKnobDiameter <= modifDiameter25) {
                            //optimizationModif
                            float modifWidthKnobBy2        = mAvailableWidth - mKnobDiameter * 2.0f;
                            float modifWidthKnobDelimiter2 = (mAvailableWidth - mKnobDiameter) / 2.0f;

                            mAvailableHeight = (mAvailableWidth - mKnobDiameter) * 2.0f;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.East) {
                                mVisualRect.left = modifWidthKnobDelimiter2 - modifWidthKnobBy2;
                            }
                            else {
                                gaugeType = this.getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.West) {
                                    mVisualRect.left = modifWidthKnobDelimiter2 - (mAvailableWidth - mKnobDiameter);
                                }
                            }
                            mVisualRect.top = (initAvalHeight - mKnobDiameter) / 2.0f - modifWidthKnobBy2;
                        }
                        else {
                            //optimizationModif
                            float modifAvaliableWidth = ((mAvailableWidth - modifDiameter25) / 2.0f);

                            mAvailableHeight = (mAvailableWidth - modifDiameter25) * 2.0f;
                            gaugeType = getGaugeType();
                            if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.East) {
                                mVisualRect.left = modifAvaliableWidth - (mAvailableWidth - 50.0f);
                            }
                            else {
                                gaugeType = getGaugeType();
                                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.West) {
                                    mVisualRect.left = modifAvaliableWidth - (mAvailableWidth - modifDiameter25);
                                }
                            }

                            mVisualRect.top = (initAvalHeight - modifDiameter25) / 2.0f - (mAvailableWidth - 50.0f);
                        }

                        mVisualRect.bottom = mVisualRect.top + mAvailableHeight;
                        mVisualRect.right = mVisualRect.left + mAvailableHeight;
                    }
                    else {
                        //optimizationModif
                        float modifRectCanvas = (float) (2.0 - rectCanvas / 2.0);

                        mVisualRect.top = mAvailableHeight / modifRectCanvas;
                        mVisualRect.left = mAvailableWidth / modifRectCanvas;
                        mVisualRect.bottom = mVisualRect.top + rectCanvas;
                        mVisualRect.right = mVisualRect.left + rectCanvas;
                    }
                    break label145;
                }
            }

            //optimizationModif
            float modifDelimiter = (float) (2.0 - squarified / 2.0);

            mVisualRect.top = mAvailableHeight / modifDelimiter;
            mVisualRect.left = mAvailableWidth / modifDelimiter;
            mVisualRect.bottom = mVisualRect.top + squarified;
            mVisualRect.right = mVisualRect.left + squarified;
        }

        mMinSize = Math.min(mVisualRect.height(), mVisualRect.width());
        double mScaleCentreY = mVisualRect.top + mVisualRect.height() / 2.0;
        double mScaleCentreX = mVisualRect.left + mVisualRect.width() / 2.0;
        mCentreY = mScaleCentreY;
        mCentreX = mScaleCentreX;
        double dimensionVar = mVisualRect.width();
        mInnerBevelWidth = dimensionVar + 10.0;
        calculateMargin(mInnerBevelWidth);
    }

    public GaugeType getGaugeType() {
        return GaugeType;
    }

    public void setGaugeType(GaugeType gaugeType) {
        this.GaugeType = gaugeType;
        refreshGauge();
    }
}

