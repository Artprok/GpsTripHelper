package com.example.aprokopenko.triphelper.gauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.aprokopenko.triphelper.gauge.enums.HeaderAlignment;

class GaugeHeaderRenderer extends View {
    protected TripHelperGauge mGauge;
    private   Paint           mPaint;

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

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mGauge != null && mGauge.headers != null) {
            for (Header header : mGauge.headers) {
                header.gauge = mGauge;
                mPaint.setColor(header.textColor);
                mPaint.setTextSize(((float) header.textSize) * TripHelperGauge.DENSITY);
                mPaint.setTypeface(header.textStyle);
                double headerTextHeight = header.textSize;
                double headerTextWidth  = (double) mPaint.measureText(header.text);
                if (header.HeaderAlignment != null) {
                    double left;
                    double top;

                    if (header.getHeaderAlignment() == HeaderAlignment.TopLeft) {
                        left = GaugeConstants.ZERO;
                        top = GaugeConstants.ZERO + headerTextHeight;
                    }
                    else if (header.getHeaderAlignment() == HeaderAlignment.Top) {
                        left = mGauge.mCentreX - (headerTextWidth / GaugeConstants.ZERO);
                        top = GaugeConstants.ZERO + headerTextHeight;
                    }
                    else {
                        double modif_centerXmultBy2minusHeaderWidth = mGauge.mCentreX * 2.0d - headerTextWidth;
                        double modif_centerYmultBy2minus10          = mGauge.mCentreY * 2.0d - 10.0d;
                        double modif_textHeightDivideBy2            = headerTextHeight / 2.0d;
                        double modif_textWidthDivideBy2             = headerTextWidth / 2.0d;

                        if (header.getHeaderAlignment() == HeaderAlignment.TopRight) {
                            left = modif_centerXmultBy2minusHeaderWidth;
                            top = GaugeConstants.ZERO + headerTextHeight;
                        }
                        else {
                            if (header.getHeaderAlignment() == HeaderAlignment.Left) {
                                left = GaugeConstants.ZERO;
                                top = mGauge.mCentreY + modif_textHeightDivideBy2;
                            }
                            else {
                                if (header.getHeaderAlignment() == HeaderAlignment.Center) {
                                    left = mGauge.mCentreX - modif_textWidthDivideBy2;
                                    top = mGauge.mCentreY + modif_textHeightDivideBy2;
                                }
                                else if (header.getHeaderAlignment() == HeaderAlignment.Right) {
                                    left = modif_centerXmultBy2minusHeaderWidth;
                                    top = mGauge.mCentreY + modif_textHeightDivideBy2;
                                }
                                else {

                                    if (header.getHeaderAlignment() == HeaderAlignment.BottomLeft) {
                                        left = GaugeConstants.ZERO;
                                        top = modif_centerYmultBy2minus10;
                                    }
                                    else if (header.getHeaderAlignment() == HeaderAlignment.Bottom) {
                                        left = mGauge.mCentreX - modif_textWidthDivideBy2;
                                        top = modif_centerYmultBy2minus10;
                                    }
                                    else if (header.getHeaderAlignment() == HeaderAlignment.BottomRight) {
                                        left = modif_centerXmultBy2minusHeaderWidth;
                                        top = modif_centerYmultBy2minus10;
                                    }
                                    else if (mGauge.mCentreY > mGauge.mCentreX) {
                                        left = mGauge.mGaugeWidth * ((double) header.position.x);
                                        top = (mGauge.mCentreY - ((double) (mGauge.mVisualRect
                                                .height() / 2.0f))) + ((double) (mGauge.mVisualRect.height() * header.position.y));
                                    }
                                    else {
                                        left = (mGauge.mCentreX - ((double) (mGauge.mVisualRect
                                                .width() / 2.0f))) + ((double) (mGauge.mVisualRect.width() * header.position.x));
                                        top = mGauge.mGaugeHeight * ((double) header.position.y);
                                    }
                                }
                            }
                        }
                    }
                    canvas.drawText(header.text, (float) left, (float) top, this.mPaint);
                }
            }
        }
    }
}
