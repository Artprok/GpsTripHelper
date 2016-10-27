package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.aprokopenko.triphelper.speedometer_gauge.enums.HeaderAlignment;

import java.util.ArrayList;

/**
 * Class representing a renderer for render a {@link TripHelperGauge}
 */
public class GaugeHeaderRenderer extends View {
  private TripHelperGauge mGauge;
  private Paint mPaint;
  private ArrayList<Header> headerArrayList = new ArrayList<>();

  public GaugeHeaderRenderer(@NonNull final Context context) {
    this(context, null);
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
  }

  public GaugeHeaderRenderer(@NonNull final Context context, @Nullable final AttributeSet attrs) {
    super(context, attrs);
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
  }

  public void setmGauge(@NonNull final TripHelperGauge mGauge) {
    this.mGauge = mGauge;
  }

  protected void onDraw(@NonNull final Canvas canvas) {
    if (mGauge != null && headerArrayList != null) {

      headerArrayList = mGauge.getHeaders();
      RectF mVisualRect = mGauge.getmVisualRect();
      final double mCentreX = mGauge.getmCentreX();
      final double mCentreY = mGauge.getmCentreY();
      final double mGaugeWidth = mGauge.getmGaugeWidth();
      final double mGaugeHeight = mGauge.getmGaugeHeight();

      for (Header header : headerArrayList) {
        header.setGauge(mGauge);
        final double textSize = header.getTextSize();
        mPaint.setColor(header.getTextColor());
        mPaint.setTextSize(((float) textSize) * TripHelperGauge.DENSITY);
        mPaint.setTypeface(header.getTextStyle());
        final double headerTextWidth = mPaint.measureText(header.getText());
        if (header.getHeaderAlignment() != null) {
          final double left;
          final double top;

          if (header.getHeaderAlignment() == HeaderAlignment.TopLeft) {
            left = GaugeConstants.ZERO;
            top = GaugeConstants.ZERO + textSize;
          } else if (header.getHeaderAlignment() == HeaderAlignment.Top) {
            left = mCentreX - (headerTextWidth / GaugeConstants.ZERO);
            top = GaugeConstants.ZERO + textSize;
          } else {
            final double modif_centerXmultBy2minusHeaderWidth = mCentreX * 2.0 - headerTextWidth;
            final double modif_centerYmultBy2minus10 = mCentreY * 2.0 - 10.0;
            final double modif_textHeightDivideBy2 = textSize / 2.0;
            final double modif_textWidthDivideBy2 = headerTextWidth / 2.0;

            if (header.getHeaderAlignment() == HeaderAlignment.TopRight) {
              left = modif_centerXmultBy2minusHeaderWidth;
              top = GaugeConstants.ZERO + textSize;
            } else {
              if (header.getHeaderAlignment() == HeaderAlignment.Left) {
                left = GaugeConstants.ZERO;
                top = mCentreY + modif_textHeightDivideBy2;
              } else {
                if (header.getHeaderAlignment() == HeaderAlignment.Center) {
                  left = mCentreX - modif_textWidthDivideBy2;
                  top = mCentreY + modif_textHeightDivideBy2;
                } else if (header.getHeaderAlignment() == HeaderAlignment.Right) {
                  left = modif_centerXmultBy2minusHeaderWidth;
                  top = mCentreY + modif_textHeightDivideBy2;
                } else {

                  if (header.getHeaderAlignment() == HeaderAlignment.BottomLeft) {
                    left = GaugeConstants.ZERO;
                    top = modif_centerYmultBy2minus10;
                  } else if (header.getHeaderAlignment() == HeaderAlignment.Bottom) {
                    left = mCentreX - modif_textWidthDivideBy2;
                    top = modif_centerYmultBy2minus10;
                  } else if (header.getHeaderAlignment() == HeaderAlignment.BottomRight) {
                    left = modif_centerXmultBy2minusHeaderWidth;
                    top = modif_centerYmultBy2minus10;
                  } else if (mCentreY > mCentreX) {
                    final PointF pointF = header.getPosition();
                    left = mGaugeWidth * (pointF.x);
                    top = (mCentreY - ((mVisualRect.height() / 2.0f))) + ((mVisualRect.height() * pointF.y));
                  } else {
                    PointF pointF = header.getPosition();
                    left = (mCentreX - ((mVisualRect.width() / 2.0f))) + ((mVisualRect.width() * pointF.x));
                    top = mGaugeHeight * (pointF.y);
                  }
                }
              }
            }
          }
          canvas.drawText(header.getText(), (float) left, (float) top, mPaint);
        }
      }
    }
  }
}