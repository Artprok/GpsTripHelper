package com.example.aprokopenko.triphelper.gauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.aprokopenko.triphelper.gauge.enums.HeaderAlignment;

class GaugeHeaderRenderer extends View {
    TripHelperGauge mGauge;
    private Paint mPaint;

    public GaugeHeaderRenderer(Context context) {
        this(context, null);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
    }

    public GaugeHeaderRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mGauge != null && this.mGauge.headers != null) {
            for (Header header : this.mGauge.headers) {
                header.gauge = this.mGauge;
                this.mPaint.setColor(header.textColor);
                this.mPaint.setTextSize(((float) header.textSize) * TripHelperGauge.DENSITY);
                this.mPaint.setTypeface(header.textStyle);
                double headerTextHeight = header.textSize;
                double headerTextWidth  = (double) this.mPaint.measureText(header.text);
                if (header.HeaderAlignment != null) {
                    double left;
                    double top;
                    if (header.getHeaderAlignment() == HeaderAlignment.TopLeft) {
                        left = GaugeConstants.ZERO;
                        top = GaugeConstants.ZERO + headerTextHeight;
                    }
                    else if (header.getHeaderAlignment() == HeaderAlignment.Top) {
                        left = this.mGauge.mCentreX - (headerTextWidth / GaugeConstants.ZERO);
                        top = GaugeConstants.ZERO + headerTextHeight;
                    }
                    else if (header.getHeaderAlignment() == HeaderAlignment.TopRight) {
                        left = (this.mGauge.mCentreX * 2.0d) - headerTextWidth;
                        top = GaugeConstants.ZERO + headerTextHeight;
                    }
                    else if (header.getHeaderAlignment() == HeaderAlignment.Left) {
                        left = GaugeConstants.ZERO;
                        top = this.mGauge.mCentreY + (headerTextHeight / 2.0d);
                    }
                    else if (header.getHeaderAlignment() == HeaderAlignment.Center) {
                        left = this.mGauge.mCentreX - (headerTextWidth / 2.0d);
                        top = this.mGauge.mCentreY + (headerTextHeight / 2.0d);
                    }
                    else if (header.getHeaderAlignment() == HeaderAlignment.Right) {
                        left = (this.mGauge.mCentreX * 2.0d) - headerTextWidth;
                        top = this.mGauge.mCentreY + (headerTextHeight / 2.0d);
                    }
                    else if (header.getHeaderAlignment() == HeaderAlignment.BottomLeft) {
                        left = GaugeConstants.ZERO;
                        top = (this.mGauge.mCentreY * 2.0d) - 10.0d;
                    }
                    else if (header.getHeaderAlignment() == HeaderAlignment.Bottom) {
                        left = this.mGauge.mCentreX - (headerTextWidth / 2.0d);
                        top = (this.mGauge.mCentreY * 2.0d) - 10.0d;
                    }
                    else if (header.getHeaderAlignment() == HeaderAlignment.BottomRight) {
                        left = (this.mGauge.mCentreX * 2.0d) - headerTextWidth;
                        top = (this.mGauge.mCentreY * 2.0d) - 10.0d;
                    }
                    else if (this.mGauge.mCentreY > this.mGauge.mCentreX) {
                        left = this.mGauge.mGaugeWidth * ((double) header.position.x);
                        top = (this.mGauge.mCentreY - ((double) (this.mGauge.mVisualRect
                                .height() / 2.0f))) + ((double) (this.mGauge.mVisualRect.height() * header.position.y));
                    }
                    else {
                        left = (this.mGauge.mCentreX - ((double) (this.mGauge.mVisualRect
                                .width() / 2.0f))) + ((double) (this.mGauge.mVisualRect.width() * header.position.x));
                        top = this.mGauge.mGaugeHeight * ((double) header.position.y);
                    }
                    canvas.drawText(header.text, (float) left, (float) top, this.mPaint);
                }
            }
        }
    }
}
