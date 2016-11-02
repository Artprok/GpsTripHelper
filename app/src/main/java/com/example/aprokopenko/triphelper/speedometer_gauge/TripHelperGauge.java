package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class extends {@link FrameLayout} and represents a circle gauge with {@link GaugeRange}, {@link NeedlePointer}, {@link GaugeScale}.
 */
public class TripHelperGauge extends FrameLayout {
  static float DENSITY = -1.0F;
  private GaugeType GaugeType;
  private final ArrayList<ScaleRenderer> mScaleRenders;
  private final ArrayList<PointerRender> mPointerRenders;
  private ArrayList<GaugeScale> gaugeScales;
  private ArrayList<Header> headers;
  private GaugeHeaderRenderer mGaugeHeader;
  private RectF mVisualRect;
  private RectF mRangeFrame;
  private int frameBackgroundColor;
  private float mGaugeHeight;
  private float mGaugeWidth;
  private double mMinSize;
  private double mInnerBevelWidth;
  private float mKnobDiameter;
  private double mRimWidth;
  private double mLabelsPathHeight;
  private double mRangePathWidth;
  private double mCentreX;
  private double mCentreY;

  public TripHelperGauge(@NonNull final Context context) {
    this(context, null);
    DENSITY = this.getContext().getResources().getDisplayMetrics().density;
  }

  public TripHelperGauge(@NonNull final Context context, @Nullable final AttributeSet attrs) {
    super(context, attrs);
    mScaleRenders = new ArrayList<>();
    mPointerRenders = new ArrayList<>();
    gaugeScales = new ArrayList<>();
    GaugeType = com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.Default;
    frameBackgroundColor = GaugeConstants.FRAME_BACKGROUND_COLOR;
    headers = new ArrayList<>();
    mGaugeHeight = (float) GaugeConstants.ZERO;
    mGaugeWidth = (float) GaugeConstants.ZERO;
    DENSITY = getContext().getResources().getDisplayMetrics().density;
  }


  private void init() {
    mGaugeHeader = new GaugeHeaderRenderer(getContext());
    mGaugeHeader.setmGauge(this);
    addView(mGaugeHeader);
  }

  public ArrayList<GaugeScale> getGaugeScales() {
    return gaugeScales;
  }

  public void setGaugeScales(@NonNull final ArrayList<GaugeScale> gaugeScales) {
    this.gaugeScales = gaugeScales;
    refreshGauge();
    if (mGaugeHeader == null) {
      init();
    }
  }

  public GaugeType getGaugeType() {
    return GaugeType;
  }

  public RectF getmVisualRect() {
    return mVisualRect;
  }

  public void setGaugeType(GaugeType gaugeType) {
    this.GaugeType = gaugeType;
    refreshGauge();
  }

  public void setmRangeFrame(@NonNull final RectF mRangeFrame) {
    this.mRangeFrame = mRangeFrame;
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

  public double getmInnerBevelWidth() {
    return mInnerBevelWidth;
  }

  public double getmRimWidth() {
    return mRimWidth;
  }

  public double getmKnobDiameter() {
    return mKnobDiameter;
  }

  public void setmKnobDiameter(final double mKnobDiameter) {
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

  protected void setFrameBackgroundColor(final int frameBackgroundColor) {
    this.frameBackgroundColor = frameBackgroundColor;
    this.refreshGauge();
  }

  public ArrayList<Header> getHeaders() {
    return headers;
  }

  public void setHeaders(@NonNull final ArrayList<Header> headers) {
    this.headers = headers;
    this.refreshGauge();
  }

  public void refreshGauge() {
    final ArrayList<GaugePointer> scaleCircularPointers = new ArrayList<>();
    final ArrayList<ScaleRenderer> removeScaleRen = new ArrayList<>();
    final ArrayList<PointerRender> removedPointerRender = new ArrayList<>();
    calculateMargin(mGaugeWidth);
    mGaugeHeader = new GaugeHeaderRenderer(this.getContext());
    mGaugeHeader.setmGauge(this);
    this.addView(mGaugeHeader);

    for (ScaleRenderer scaleRenderer : mScaleRenders) {
      removeScaleRen.add(scaleRenderer);
    }

    Iterator i$1 = gaugeScales.iterator();
    Iterator anim;
    label91:
    while (i$1.hasNext()) {
      final GaugeScale poinRen = (GaugeScale) i$1.next();
      ScaleRenderer pointRen = null;
      anim = mScaleRenders.iterator();

      while (true) {
        if (anim.hasNext()) {
          final ScaleRenderer scRender = (ScaleRenderer) anim.next();
          if (scRender.getGaugeScale() != poinRen) {
            continue;
          }
          pointRen = scRender;
        }

        if (pointRen == null) {
          final ScaleRenderer anim1 = new ScaleRenderer(this.getContext(), this, poinRen);
          mScaleRenders.add(anim1);
          this.addView(anim1);
        } else {
          removeScaleRen.remove(pointRen);
          pointRen.invalidate();
        }

        anim = poinRen.getGaugePointers().iterator();

        while (true) {
          if (!anim.hasNext()) {
            continue label91;
          }

          final GaugePointer scRender1 = (GaugePointer) anim.next();
          scRender1.setmGaugeScale(poinRen);
          scRender1.setmBaseGauge(this);
          scaleCircularPointers.add(scRender1);
        }
      }
    }

    i$1 = removeScaleRen.iterator();

    while (i$1.hasNext()) {
      final ScaleRenderer poinRen1 = (ScaleRenderer) i$1.next();
      this.removeView(poinRen1);
    }

    i$1 = mPointerRenders.iterator();

    PointerRender poinRen2;
    while (i$1.hasNext()) {
      poinRen2 = (PointerRender) i$1.next();
      removedPointerRender.add(poinRen2);
    }

    i$1 = scaleCircularPointers.iterator();

    while (i$1.hasNext()) {
      final GaugePointer poinRen3 = (GaugePointer) i$1.next();
      PointerRender pointRen1 = null;
      anim = mPointerRenders.iterator();

      while (true) {
        if (anim.hasNext()) {
          final PointerRender scRender2 = (PointerRender) anim.next();
          if (scRender2.getmGaugePointer() != poinRen3) {
            continue;
          }

          pointRen1 = scRender2;
        }

        if (pointRen1 == null) {
          final PointerRender anim2 = new PointerRender(this.getContext(), this, poinRen3.getmGaugeScale(), poinRen3);
          mPointerRenders.add(anim2);
          poinRen3.setmPointerRender(anim2);
          this.addView(anim2);
        } else {
          removedPointerRender.remove(pointRen1);
          final ObjectAnimator anim3 = ObjectAnimator.ofFloat(pointRen1, "", pointRen1.getValue(), (float) poinRen3.getValue());
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

  protected void onSizeChanged(int w, int h, final int oldw, final int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mVisualRect = new RectF();
    h = h < 50 ? 50 : h;
    w = w < 50 ? 50 : w;
    float mAvailableHeight = h;
    float mAvailableWidth;
    if (h > w) {
      mAvailableWidth = w;
    } else {
      mAvailableWidth = mAvailableHeight;
    }
    if (mGaugeHeight > GaugeConstants.ZERO) {
      mGaugeHeight = mGaugeHeight > mAvailableHeight ? mAvailableHeight : mGaugeHeight;
    } else {
      mGaugeHeight = mAvailableHeight;
    }

    if (mGaugeWidth > GaugeConstants.ZERO) {
      mGaugeWidth = mGaugeWidth > mAvailableWidth ? mAvailableWidth : mGaugeWidth;
    } else {
      mGaugeWidth = mAvailableWidth;
    }

    GaugeType gaugeType;
    final float squarified;
    final float rectCanvas;

    //modif optimization
    final float modifDiameter25 = 25.0f;

    label141:
    {
      squarified = Math.min(mGaugeHeight, mGaugeWidth);
      gaugeType = getGaugeType();
      //optimizationModif
      final float modifKnobDiameterBy4 = mKnobDiameter * 4.0f;

      if (gaugeType != com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.North) {
        gaugeType = getGaugeType();
        if (gaugeType != com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.South) {
          if (mKnobDiameter == GaugeConstants.ZERO) {
            rectCanvas = Math.max(mGaugeHeight - 30.0f, mGaugeWidth);
          } else if (mKnobDiameter <= modifDiameter25) {
            rectCanvas = Math.max(mGaugeHeight - modifKnobDiameterBy4, mGaugeWidth);
          } else {
            rectCanvas = Math.max(mGaugeHeight - 100.0f, mGaugeWidth);
          }
          break label141;
        }
      }

      if (mKnobDiameter == GaugeConstants.ZERO) {
        rectCanvas = Math.max(mGaugeHeight, mGaugeWidth - 30.0f);
      } else if (mKnobDiameter <= modifDiameter25) {
        rectCanvas = Math.max(mGaugeHeight, mGaugeWidth - modifKnobDiameterBy4);
      } else {
        rectCanvas = Math.max(mGaugeHeight, mGaugeWidth - 100.0f);
      }
    }

    label145:
    {
      final float initAvalHeight = mAvailableHeight;
      final float initAvalWidth = mAvailableWidth;
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
              final float modifHeightMinus20 = mAvailableHeight - 20.0f;
              final float modifHeightMinus10 = mAvailableHeight - 10.0f;

              mAvailableWidth = mAvailableHeight * 2.0f - 20.0f;
              gaugeType = getGaugeType();
              if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.North) {
                mVisualRect.top = (modifHeightMinus10 / 2.0f) - modifHeightMinus10;
              } else {
                gaugeType = getGaugeType();
                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.South) {
                  mVisualRect.top = (modifHeightMinus10 / 2.0f) - modifHeightMinus20;
                }
              }
              mVisualRect.left = (initAvalWidth - 20.0f) / 2.0f - modifHeightMinus20;
            } else if (this.mKnobDiameter <= modifDiameter25) {
              final float modifKnobDiameterBy2 = (float) (mKnobDiameter * 2.0);
              final float modifHeightMinusKnobBy2 = mAvailableHeight - modifKnobDiameterBy2;
              final float modifHeightMinusKnob = mAvailableHeight - mKnobDiameter;

              mAvailableWidth = (modifHeightMinusKnob) * 2.0f;
              gaugeType = getGaugeType();
              if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.North) {
                mVisualRect.top = ((modifHeightMinusKnob) / 2.0f) - (modifHeightMinusKnob);
              } else {
                gaugeType = getGaugeType();
                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.South) {
                  mVisualRect.top = ((modifHeightMinusKnob) / 2.0f) - modifHeightMinusKnobBy2;
                }
              }
              mVisualRect.left = (initAvalWidth - modifKnobDiameterBy2) / 2.0f - modifHeightMinusKnobBy2;
            } else {
              final float modifHeightMinus50 = mAvailableHeight - 50.0f;
              final float modifHeightMinus25 = mAvailableHeight - 25.0f;

              mAvailableWidth = mAvailableHeight * 2.0f - 50.0f;
              gaugeType = getGaugeType();
              if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.North) {
                mVisualRect.top = (modifHeightMinus25 / 2.0f) - modifHeightMinus25;
              } else {
                gaugeType = getGaugeType();
                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.South) {
                  mVisualRect.top = (modifHeightMinus25 / 2.0f) - modifHeightMinus50;
                }
              }
              mVisualRect.left = (initAvalWidth - 50.0f) / 2.0f - modifHeightMinus50;
            }
            mVisualRect.bottom = mVisualRect.top + mAvailableWidth;
            mVisualRect.right = mVisualRect.left + mAvailableWidth;
          } else {
            //optimizationModif
            final float modifRectCanvasDelimBy2 = (float) (rectCanvas / 2.0);

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
            final float modifAvaliableWidthMinus20 = (mAvailableWidth - 20.0f);
            final float modifAvaliableWidthMinus10 = mAvailableWidth - 10.0f;

            if (mKnobDiameter == GaugeConstants.ZERO) {
              mAvailableHeight = (modifAvaliableWidthMinus10) * 2.0f;
              gaugeType = getGaugeType();
              if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.East) {
                mVisualRect.left = modifAvaliableWidthMinus10 / 2.0f - modifAvaliableWidthMinus20;
              } else {
                gaugeType = getGaugeType();
                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.West) {
                  mVisualRect.left = modifAvaliableWidthMinus10 / 2.0f - modifAvaliableWidthMinus10;
                }
              }

              mVisualRect.top = (initAvalHeight - 10.0f) / 2.0f - modifAvaliableWidthMinus20;
            } else if (mKnobDiameter <= modifDiameter25) {
              final float modifWidthKnobBy2 = mAvailableWidth - mKnobDiameter * 2.0f;

              mAvailableHeight = (mAvailableWidth - mKnobDiameter) * 2.0f;
              gaugeType = getGaugeType();
              if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.East) {
                mVisualRect.left = ((mAvailableWidth - mKnobDiameter) / 2.0f) - modifWidthKnobBy2;
              } else {
                gaugeType = this.getGaugeType();
                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.West) {
                  mVisualRect.left = ((mAvailableWidth - mKnobDiameter) / 2.0f) - (mAvailableWidth - mKnobDiameter);
                }
              }
              mVisualRect.top = (initAvalHeight - mKnobDiameter) / 2.0f - modifWidthKnobBy2;
            } else {
              mAvailableHeight = (mAvailableWidth - modifDiameter25) * 2.0f;
              gaugeType = getGaugeType();
              if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.East) {
                mVisualRect.left = ((mAvailableWidth - modifDiameter25) / 2.0f) - (mAvailableWidth - 50.0f);
              } else {
                gaugeType = getGaugeType();
                if (gaugeType == com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType.West) {
                  mVisualRect.left = ((mAvailableWidth - modifDiameter25) / 2.0f) - (mAvailableWidth - modifDiameter25);
                }
              }

              mVisualRect.top = (initAvalHeight - modifDiameter25) / 2.0f - (mAvailableWidth - 50.0f);
            }

            mVisualRect.bottom = mVisualRect.top + mAvailableHeight;
            mVisualRect.right = mVisualRect.left + mAvailableHeight;
          } else {
            final float modifRectCanvas = (float) (2.0 - rectCanvas / 2.0);

            mVisualRect.top = mAvailableHeight / modifRectCanvas;
            mVisualRect.left = mAvailableWidth / modifRectCanvas;
            mVisualRect.bottom = mVisualRect.top + rectCanvas;
            mVisualRect.right = mVisualRect.left + rectCanvas;
          }
          break label145;
        }
      }

      final float modifDelimiter = (float) (2.0 - squarified / 2.0);

      mVisualRect.top = mAvailableHeight / modifDelimiter;
      mVisualRect.left = mAvailableWidth / modifDelimiter;
      mVisualRect.bottom = mVisualRect.top + squarified;
      mVisualRect.right = mVisualRect.left + squarified;
    }

    mMinSize = Math.min(mVisualRect.height(), mVisualRect.width());
    mCentreY = mVisualRect.top + mVisualRect.height() / 2.0;
    mCentreX = mVisualRect.left + mVisualRect.width() / 2.0;
    mInnerBevelWidth = mVisualRect.width() + 10.0;
    calculateMargin(mInnerBevelWidth);
  }

  public void calculateMargin(final double width) {
    final double marginSubtrahend = 2.0 * 10.0;
    mRimWidth = width - 10.0;
    final double dimensionVariable = (mRimWidth - marginSubtrahend) - marginSubtrahend;
    mLabelsPathHeight = dimensionVariable;
    mRangePathWidth = dimensionVariable;
  }
}