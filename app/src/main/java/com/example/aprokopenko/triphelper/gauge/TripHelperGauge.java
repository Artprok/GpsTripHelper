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
    private double mAvailableHeight;
    private double mAvailableWidth;
    double mGaugeHeight;
    double mGaugeWidth;
    private double mScaleCentreX;
    private double mScaleCentreY;
    double mMinSize;
    private double mOuterBevelHeight;
    private double mOuterBevelWidth;
    private double mInnerBevelHeight;
    double mInnerBevelWidth;
    double mKnobDiameter;
    private double mRimHeight;
    double mRimWidth;
    private double mTicksPathHeight;
    private double mTicksPathWidth;
    double mLabelsPathHeight;
    private double mLabelsPathWidth;
    private double mRangePathHeight;
    double mRangePathWidth;
    double mCentreX;
    double mCentreY;
    private double marginSubtrahend;

    public TripHelperGauge(Context context) {
        this(context, null);
        DENSITY = this.getContext().getResources().getDisplayMetrics().density;
    }

    public TripHelperGauge(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mScaleRenderers = new ArrayList<>();
        this.mPointerRenderes = new ArrayList<>();
        this.gaugeScales = new ArrayList<>();
        this.GaugeType = com.example.aprokopenko.triphelper.gauge.enums.GaugeType.Default;
        this.frameBackgroundColor = GaugeConstants.FRAME_BACKGROUND_COLOR;
        this.headers = new ArrayList<>();
        this.mGaugeHeight = GaugeConstants.ZERO;
        this.mGaugeWidth = GaugeConstants.ZERO;
        this.mGap = 10.0D;
        DENSITY = this.getContext().getResources().getDisplayMetrics().density;
    }

    private void init() {
        this.mGaugeHeader = new GaugeHeaderRenderer(this.getContext());
        this.mGaugeHeader.mGauge = this;
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
        this.calculateMargin(this.mGaugeHeight, this.mGaugeWidth);
        this.mGaugeHeader = new GaugeHeaderRenderer(this.getContext());
        this.mGaugeHeader.mGauge = this;
        this.addView(this.mGaugeHeader);
        ArrayList scaleCircularPointers = new ArrayList();
        ArrayList removeScaleRen        = new ArrayList();
        ArrayList removedPointerRender  = new ArrayList();

        for (Object i$ : this.mScaleRenderers) {
            removeScaleRen.add(i$);
        }

        boolean  index1 = false;
        Iterator i$1    = this.gaugeScales.iterator();

        Iterator anim;
        label91:
        while (i$1.hasNext()) {
            GaugeScale    poinRen  = (GaugeScale) i$1.next();
            ScaleRenderer pointRen = null;
            anim = this.mScaleRenderers.iterator();

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
                    this.mScaleRenderers.add(anim1);
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

        i$1 = this.mPointerRenderes.iterator();

        PointerRender poinRen2;
        while (i$1.hasNext()) {
            poinRen2 = (PointerRender) i$1.next();
            removedPointerRender.add(poinRen2);
        }

        index1 = false;
        i$1 = scaleCircularPointers.iterator();

        while (i$1.hasNext()) {
            GaugePointer  poinRen3  = (GaugePointer) i$1.next();
            PointerRender pointRen1 = null;
            anim = this.mPointerRenderes.iterator();

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
                    this.mPointerRenderes.add(anim2);
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
            this.removeView(poinRen2);
        }

        if (this.mGaugeHeader != null) {
            this.mGaugeHeader.mGauge = this;
            this.mGaugeHeader.requestLayout();
            this.mGaugeHeader.invalidate();
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mVisualRect = new RectF();
        h = h < 50 ? 50 : h;
        w = w < 50 ? 50 : w;
        this.mAvailableHeight = (double) h;
        this.mAvailableWidth = (double) w;
        if (this.mGaugeHeight > GaugeConstants.ZERO) {
            this.mGaugeHeight = this.mGaugeHeight > this.mAvailableHeight ? this.mAvailableHeight : this.mGaugeHeight;
        }
        else {
            this.mGaugeHeight = this.mAvailableHeight;
        }

        if (this.mGaugeWidth > GaugeConstants.ZERO) {
            this.mGaugeWidth = this.mGaugeWidth > this.mAvailableWidth ? this.mAvailableWidth : this.mGaugeWidth;
        }
        else {
            this.mGaugeWidth = this.mAvailableWidth;
        }

        GaugeType var10000;
        GaugeType var10001;
        double    squarified;
        double    rectCanvas;
        label141:
        {
            squarified = Math.min(this.mGaugeHeight, this.mGaugeWidth);
            var10000 = this.getGaugeType();
            var10001 = this.GaugeType;
            if (var10000 != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.North) {
                var10000 = this.getGaugeType();
                var10001 = this.GaugeType;
                if (var10000 != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.South) {
                    if (this.mKnobDiameter == GaugeConstants.ZERO) {
                        rectCanvas = Math.max(this.mGaugeHeight - 30.0D, this.mGaugeWidth);
                    }
                    else if (this.mKnobDiameter <= 25.0D) {
                        rectCanvas = Math.max(this.mGaugeHeight - this.mKnobDiameter * 4.0D, this.mGaugeWidth);
                    }
                    else {
                        rectCanvas = Math.max(this.mGaugeHeight - 100.0D, this.mGaugeWidth);
                    }
                    break label141;
                }
            }

            if (this.mKnobDiameter == GaugeConstants.ZERO) {
                rectCanvas = Math.max(this.mGaugeHeight, this.mGaugeWidth - 30.0D);
            }
            else if (this.mKnobDiameter <= 25.0D) {
                rectCanvas = Math.max(this.mGaugeHeight, this.mGaugeWidth - this.mKnobDiameter * 4.0D);
            }
            else {
                rectCanvas = Math.max(this.mGaugeHeight, this.mGaugeWidth - 100.0D);
            }
        }

        label145:
        {
            double _mAvailHeight = this.mAvailableHeight;
            double _mAvailWidth  = this.mAvailableWidth;
            if (this.mAvailableWidth > this.mAvailableHeight) {
                label134:
                {
                    var10000 = this.getGaugeType();
                    var10001 = this.GaugeType;
                    if (var10000 != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.North) {
                        var10000 = this.getGaugeType();
                        var10001 = this.GaugeType;
                        if (var10000 != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.South) {
                            break label134;
                        }
                    }

                    if (this.mAvailableWidth / 2.0D > this.mAvailableHeight) {
                        if (this.mKnobDiameter == GaugeConstants.ZERO) {
                            this.mAvailableWidth = this.mAvailableHeight * 2.0D - 20.0D;
                            var10000 = this.getGaugeType();
                            var10001 = this.GaugeType;
                            if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.North) {
                                this.mVisualRect.top = (float) ((this.mAvailableHeight - 10.0D) / 2.0D - (this.mAvailableHeight - 10.0D));
                            }
                            else {
                                var10000 = this.getGaugeType();
                                var10001 = this.GaugeType;
                                if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.South) {
                                    float res = (float) ((this.mAvailableHeight - 10.0D) / 2.0D - (this.mAvailableHeight - 20.0D));
                                    this.mVisualRect.top = res;
                                }
                            }

                            this.mVisualRect.left = (float) ((_mAvailWidth - 20.0D) / 2.0D - (this.mAvailableHeight - 20.0D));
                        }
                        else if (this.mKnobDiameter <= 25.0D) {
                            this.mAvailableWidth = (this.mAvailableHeight - this.mKnobDiameter) * 2.0D;
                            var10000 = this.getGaugeType();
                            var10001 = this.GaugeType;
                            if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.North) {
                                this.mVisualRect.top = (float) ((this.mAvailableHeight - this.mKnobDiameter) / 2.0D - (this
                                        .mAvailableHeight - this.mKnobDiameter));
                            }
                            else {
                                var10000 = this.getGaugeType();
                                var10001 = this.GaugeType;
                                if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.South) {
                                    this.mVisualRect.top = (float) ((this.mAvailableHeight - this.mKnobDiameter) / 2.0D - (this
                                            .mAvailableHeight - this.mKnobDiameter * 2.0D));
                                }
                            }

                            this.mVisualRect.left = (float) ((_mAvailWidth - this.mKnobDiameter * 2.0D) / 2.0D - (this.mAvailableHeight -
                                    this.mKnobDiameter * 2.0D));
                        }
                        else {
                            this.mAvailableWidth = this.mAvailableHeight * 2.0D - 50.0D;
                            var10000 = this.getGaugeType();
                            var10001 = this.GaugeType;
                            if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.North) {
                                this.mVisualRect.top = (float) ((this.mAvailableHeight - 25.0D) / 2.0D - (this.mAvailableHeight - 25.0D));
                            }
                            else {
                                var10000 = this.getGaugeType();
                                var10001 = this.GaugeType;
                                if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.South) {
                                    float res = (float) ((this.mAvailableHeight - 25.0D) / 2.0D - (this.mAvailableHeight - 50.0D));
                                    this.mVisualRect.top = res;
                                }
                            }

                            this.mVisualRect.left = (float) ((_mAvailWidth - 50.0D) / 2.0D - (this.mAvailableHeight - 50.0D));
                        }

                        this.mVisualRect.bottom = (float) ((double) this.mVisualRect.top + this.mAvailableWidth);
                        this.mVisualRect.right = (float) ((double) this.mVisualRect.left + this.mAvailableWidth);
                    }
                    else {
                        this.mVisualRect.top = (float) (this.mAvailableHeight / 2.0D - rectCanvas / 2.0D);
                        this.mVisualRect.left = (float) (this.mAvailableWidth / 2.0D - rectCanvas / 2.0D);
                        this.mVisualRect.bottom = (float) ((double) this.mVisualRect.top + rectCanvas);
                        this.mVisualRect.right = (float) ((double) this.mVisualRect.left + rectCanvas);
                    }
                    break label145;
                }
            }

            if (this.mAvailableWidth < this.mAvailableHeight) {
                label127:
                {
                    var10000 = this.getGaugeType();
                    var10001 = this.GaugeType;
                    if (var10000 != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.East) {
                        var10000 = this.getGaugeType();
                        var10001 = this.GaugeType;
                        if (var10000 != com.example.aprokopenko.triphelper.gauge.enums.GaugeType.West) {
                            break label127;
                        }
                    }

                    if (this.mAvailableHeight / 2.0D > this.mAvailableWidth) {
                        if (this.mKnobDiameter == GaugeConstants.ZERO) {
                            this.mAvailableHeight = (this.mAvailableWidth - 10.0D) * 2.0D;
                            var10000 = this.getGaugeType();
                            var10001 = this.GaugeType;
                            if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.East) {
                                this.mVisualRect.left = (float) ((this.mAvailableWidth - 10.0D) / 2.0D - (this.mAvailableWidth - 20.0D));
                            }
                            else {
                                var10000 = this.getGaugeType();
                                var10001 = this.GaugeType;
                                if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.West) {
                                    float res = (float) ((this.mAvailableWidth - 10.0D) / 2.0D - (this.mAvailableWidth - 10.0D));
                                    this.mVisualRect.left = res;
                                }
                            }

                            this.mVisualRect.top = (float) ((_mAvailHeight - 10.0D) / 2.0D - (this.mAvailableWidth - 20.0D));
                        }
                        else if (this.mKnobDiameter <= 25.0D) {
                            this.mAvailableHeight = (this.mAvailableWidth - this.mKnobDiameter) * 2.0D;
                            var10000 = this.getGaugeType();
                            var10001 = this.GaugeType;
                            if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.East) {
                                this.mVisualRect.left = (float) ((this.mAvailableWidth - this.mKnobDiameter) / 2.0D - (this
                                        .mAvailableWidth - this.mKnobDiameter * 2.0D));
                            }
                            else {
                                var10000 = this.getGaugeType();
                                var10001 = this.GaugeType;
                                if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.West) {
                                    this.mVisualRect.left = (float) ((this.mAvailableWidth - this.mKnobDiameter) / 2.0D - (this
                                            .mAvailableWidth - this.mKnobDiameter));
                                }
                            }

                            this.mVisualRect.top = (float) ((_mAvailHeight - this.mKnobDiameter) / 2.0D - (this.mAvailableWidth - this
                                    .mKnobDiameter * 2.0D));
                        }
                        else {
                            this.mAvailableHeight = (this.mAvailableWidth - 25.0D) * 2.0D;
                            var10000 = this.getGaugeType();
                            var10001 = this.GaugeType;
                            if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.East) {
                                this.mVisualRect.left = (float) ((this.mAvailableWidth - 25.0D) / 2.0D - (this.mAvailableWidth - 50.0D));
                            }
                            else {
                                var10000 = this.getGaugeType();
                                var10001 = this.GaugeType;
                                if (var10000 == com.example.aprokopenko.triphelper.gauge.enums.GaugeType.West) {
                                    float res = (float) ((this.mAvailableWidth - 25.0D) / 2.0D - (this.mAvailableWidth - 25.0D));
                                    this.mVisualRect.left = res;
                                }
                            }

                            this.mVisualRect.top = (float) ((_mAvailHeight - 25.0D) / 2.0D - (this.mAvailableWidth - 50.0D));
                        }

                        this.mVisualRect.bottom = (float) ((double) this.mVisualRect.top + this.mAvailableHeight);
                        this.mVisualRect.right = (float) ((double) this.mVisualRect.left + this.mAvailableHeight);
                    }
                    else {
                        this.mVisualRect.top = (float) (this.mAvailableHeight / 2.0D - rectCanvas / 2.0D);
                        this.mVisualRect.left = (float) (this.mAvailableWidth / 2.0D - rectCanvas / 2.0D);
                        this.mVisualRect.bottom = (float) ((double) this.mVisualRect.top + rectCanvas);
                        this.mVisualRect.right = (float) ((double) this.mVisualRect.left + rectCanvas);
                    }
                    break label145;
                }
            }

            this.mVisualRect.top = (float) (this.mAvailableHeight / 2.0D - squarified / 2.0D);
            this.mVisualRect.left = (float) (this.mAvailableWidth / 2.0D - squarified / 2.0D);
            this.mVisualRect.bottom = (float) ((double) this.mVisualRect.top + squarified);
            this.mVisualRect.right = (float) ((double) this.mVisualRect.left + squarified);
        }

        this.mMinSize = (double) Math.min(this.mVisualRect.height(), this.mVisualRect.width());
        this.mScaleCentreY = (double) (this.mVisualRect.top + this.mVisualRect.height() / 2.0F);
        this.mScaleCentreX = (double) (this.mVisualRect.left + this.mVisualRect.width() / 2.0F);
        this.mCentreY = this.mScaleCentreY;
        this.mCentreX = this.mScaleCentreX;
        this.mOuterBevelHeight = (double) this.mVisualRect.height();
        this.mOuterBevelWidth = (double) this.mVisualRect.width();
        double var13 = this.mOuterBevelHeight;
        this.getClass();
        this.mInnerBevelHeight = var13 + 10.0D;
        var13 = this.mOuterBevelWidth;
        this.getClass();
        this.mInnerBevelWidth = var13 + 10.0D;
        this.calculateMargin(this.mInnerBevelWidth, this.mInnerBevelWidth);
    }

    void calculateMargin(double height, double width) {
        marginSubtrahend = 2.0D * 10.0D;
        this.getClass();
        this.mRimHeight = height - 10.0D;
        this.getClass();
        this.mRimWidth = width - 10.0D;
        double var10001 = this.mRimHeight;
        this.getClass();
        this.mTicksPathHeight = var10001 - marginSubtrahend;
        var10001 = this.mRimWidth;
        this.getClass();
        this.mTicksPathWidth = var10001 - marginSubtrahend;
        var10001 = this.mTicksPathHeight;
        this.getClass();
        this.mLabelsPathHeight = var10001 - marginSubtrahend;
        var10001 = this.mTicksPathWidth;
        this.getClass();
        this.mLabelsPathWidth = var10001 - marginSubtrahend;
        var10001 = this.mLabelsPathHeight;
        this.getClass();
        this.mRangePathHeight = var10001 - marginSubtrahend;
        var10001 = this.mLabelsPathWidth;
        this.getClass();
        this.mRangePathWidth = var10001 - marginSubtrahend;
    }
}

