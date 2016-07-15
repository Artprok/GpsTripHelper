package com.example.aprokopenko.triphelper.gauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.aprokopenko.triphelper.gauge.enums.HeaderAlignment;

import java.util.ArrayList;

class GaugeHeaderRenderer extends View {
    private TripHelperGauge mGauge;
    private Paint           mPaint;
    private ArrayList<Header> headerArrayList = new ArrayList<>();

    public GaugeHeaderRenderer(Context context) {
        this(context, null);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public GaugeHeaderRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public void setmGauge(TripHelperGauge mGauge) {
        this.mGauge = mGauge;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mGauge != null && headerArrayList != null) {

            headerArrayList = mGauge.getHeaders();

            RectF  mVisualRect  = mGauge.getmVisualRect();
            double mCentreX     = mGauge.getmCentreX();
            double mCentreY     = mGauge.getmCentreY();
            double mGaugeWidth  = mGauge.getmGaugeWidth();
            double mGaugeHeight = mGauge.getmGaugeHeight();


            for (Header header : headerArrayList) {
                header.setGauge(mGauge);
                double textSize = header.getTextSize();
                mPaint.setColor(header.getTextColor());
                mPaint.setTextSize(((float) textSize) * TripHelperGauge.DENSITY);
                mPaint.setTypeface(header.getTextStyle());
                double headerTextWidth = (double) mPaint.measureText(header.getText());
                if (header.getHeaderAlignment() != null) {
                    double left;
                    double top;

                    if (header.getHeaderAlignment() == HeaderAlignment.TopLeft) {
                        left = GaugeConstants.ZERO;
                        top = GaugeConstants.ZERO + textSize;
                    }
                    else if (header.getHeaderAlignment() == HeaderAlignment.Top) {
                        left = mCentreX - (headerTextWidth / GaugeConstants.ZERO);
                        top = GaugeConstants.ZERO + textSize;
                    }
                    else {
                        double modif_centerXmultBy2minusHeaderWidth = mCentreX * 2.0d - headerTextWidth;
                        double modif_centerYmultBy2minus10          = mCentreY * 2.0d - 10.0d;
                        double modif_textHeightDivideBy2            = textSize / 2.0d;
                        double modif_textWidthDivideBy2             = headerTextWidth / 2.0d;

                        if (header.getHeaderAlignment() == HeaderAlignment.TopRight) {
                            left = modif_centerXmultBy2minusHeaderWidth;
                            top = GaugeConstants.ZERO + textSize;
                        }
                        else {
                            if (header.getHeaderAlignment() == HeaderAlignment.Left) {
                                left = GaugeConstants.ZERO;
                                top = mCentreY + modif_textHeightDivideBy2;
                            }
                            else {
                                if (header.getHeaderAlignment() == HeaderAlignment.Center) {
                                    left = mCentreX - modif_textWidthDivideBy2;
                                    top = mCentreY + modif_textHeightDivideBy2;
                                }
                                else if (header.getHeaderAlignment() == HeaderAlignment.Right) {
                                    left = modif_centerXmultBy2minusHeaderWidth;
                                    top = mCentreY + modif_textHeightDivideBy2;
                                }
                                else {

                                    if (header.getHeaderAlignment() == HeaderAlignment.BottomLeft) {
                                        left = GaugeConstants.ZERO;
                                        top = modif_centerYmultBy2minus10;
                                    }
                                    else if (header.getHeaderAlignment() == HeaderAlignment.Bottom) {
                                        left = mCentreX - modif_textWidthDivideBy2;
                                        top = modif_centerYmultBy2minus10;
                                    }
                                    else if (header.getHeaderAlignment() == HeaderAlignment.BottomRight) {
                                        left = modif_centerXmultBy2minusHeaderWidth;
                                        top = modif_centerYmultBy2minus10;
                                    }
                                    else if (mCentreY > mCentreX) {
                                        PointF pointF = header.getPosition();
                                        left = mGaugeWidth * ((double) pointF.x);
                                        top = (mCentreY - ((double) (mVisualRect.height() / 2.0f))) + ((double) (mVisualRect
                                                .height() * pointF.y));
                                    }
                                    else {
                                        PointF pointF = header.getPosition();
                                        left = (mCentreX - ((double) (mVisualRect.width() / 2.0f))) + ((double) (mVisualRect
                                                .width() * pointF.x));
                                        top = mGaugeHeight * ((double) pointF.y);
                                    }
                                }
                            }
                        }
                    }
                    canvas.drawText(header.getText(), (float) left, (float) top, this.mPaint);
                }
            }
        }
    }
}
