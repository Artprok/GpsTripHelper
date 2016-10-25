package com.example.aprokopenko.triphelper.speedometer_gauge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.aprokopenko.triphelper.speedometer_gauge.enums.GaugeType;
import com.example.aprokopenko.triphelper.speedometer_gauge.enums.NeedleType;

public class PointerRender extends View {

  private GaugePointer mGaugePointer;
  private GaugeScale mGaugeScale;
  private boolean mEnableAnimation;
  private TripHelperGauge mGauge;

  private float value;

  //modif parameters
  private float modif_minSizeDivideBy4;
  private float modif_minSizeDivideBy2;
  private float modif_minSizeMultBy0dot75;
  private float modif_minSizeMultBy1dot125;
  private float modif_minSizeMultBy0dot375;
  private float modif_minSizeMultBy0dot875;
  private float modif_widthDivideBy2;

  private float mCentreX;
  private float mCentreY;
  private GaugeType gaugeType;
  private float mInnerBevelWidth;
  private float mMinSize;
  private float mRimWidth;
  private float mLabelsPathHeight;


  public PointerRender(@NonNull final Context context, @Nullable final TripHelperGauge mGauge, @Nullable final GaugeScale mGaugeScale, @Nullable final GaugePointer mGaugePointer) {
    this(context, null);
    if (mGauge != null) {
      this.mGauge = mGauge;

      gaugeType = mGauge.getGaugeType();
      mRimWidth = (float) mGauge.getmRimWidth();


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

  public PointerRender(@NonNull final Context context, @Nullable final AttributeSet attrs) {
    super(context, attrs);
    value = 0.0f;
    mEnableAnimation = true;
  }

  public float getValue() {
    return value;
  }

  public void setValue(final float value) {
    this.value = value;
    invalidate();
  }

  public boolean ismEnableAnimation() {
    return mEnableAnimation;
  }

  public void setmEnableAnimation(final boolean mEnableAnimation) {
    this.mEnableAnimation = mEnableAnimation;
  }

  protected void onDraw(@NonNull final Canvas canvas) {
    if (!(mGauge == null || mGaugeScale == null || mGaugePointer == null)) {
      //modif optim
      final float radFactor = (float) mGaugeScale.getRadiusFactor();
      final double modif_widthMultRadFactor = mInnerBevelWidth * radFactor;
      final double modif_minSizeMultRadFactor = mMinSize * radFactor;
      final double modfi_marginVar = mInnerBevelWidth - modif_widthMultRadFactor;

      final RectF mRangeFrame = mGauge.getmRangeFrame();
      mCentreX = (float) mGauge.getmCentreX();
      mCentreY = (float) mGauge.getmCentreY();
      mMinSize = (float) mGauge.getmMinSize();
      mInnerBevelWidth = (float) mGauge.getmInnerBevelWidth();
      mRimWidth = (float) mGauge.getmRimWidth();
      mLabelsPathHeight = (float) mGauge.getmLabelsPathHeight();
      final double mRangePathWidth = mGauge.getmRangePathWidth();

      mGauge.calculateMargin(modfi_marginVar);
      float rimSize = mMinSize - mRimWidth;

      modif_minSizeDivideBy4 = mMinSize / 4.0f;
      modif_minSizeDivideBy2 = mMinSize / 2.0f;
      modif_minSizeMultBy0dot75 = mMinSize * 0.75f;
      modif_minSizeMultBy1dot125 = mMinSize * 1.125f;
      modif_minSizeMultBy0dot375 = mMinSize * 0.375f;
      modif_minSizeMultBy0dot875 = mMinSize * 0.875f;
      modif_widthDivideBy2 = (float) (mGaugePointer.getWidth() / 2.0);

      final float modif_centerYminusDivideBy4plusRimS = getCenterYminusDivideBy4plusRimS(rimSize);
      final float modif_centerXplusDivideBy2minusRimS = getCenterXplusDivideBy2minusRimS(rimSize);
      final float modif_centerYplus0dot75minusRimS = getCenterYplus0dot75minusRimS(rimSize);
      final float modif_centerYminus0dot75plusRimS = getCenterYminus0dot75plusRimS(rimSize);
      final float modif_centerXminusDivideBy4plusRimS = getCenterXminusDivideBy4plusRimS(rimSize);
      final float modif_centerYminusDivideBy2plusRimS = getCenterYminusDivideBy2plusRimS(rimSize);
      final float modif_centerXplus0dot75minusRimS = getCenterXplus0dot75minusRimS(rimSize);
      final float modif_centerYplusDivideBy4minusRimS = getCenterYplusDivideBy4minusRimS(rimSize);
      final float modif_centerXminus0dot75plusRimS = getCenterXminus0dot75plusRimS(rimSize);
      final float moidf_centerXminus1dot125plusRimS = getCenterXminus1dot125plusRimS(rimSize);
      final float modif_centerXplusDivideBy4minusRimS = getCenterXplusDivideBy4minusRimS(rimSize);
      final float modif_centerYminus0dot375plusRimS = getCenterYminus0dot375plusRimS(rimSize);
      final float modif_centerXplus0dot375minusRimS = getCenterXplus0dot375minusRimS(rimSize);
      final float modif_centerYplus1dot125minusRimS = getCenterYplus1dot125minusRimS(rimSize);
      final float modif_centerXminus0dot375plusRimS = getCenterXminus0dot375plusRimS(rimSize);
      final float modif_centerXplus1dot125minusRimS = getCenterXplus1dot125minusRimS(rimSize);
      final float modif_centerYminus1dot125plusRimS = getCenterYminus1dot125plusRimS(rimSize);
      final float modif_centerYplus0dot375minusRimS = getCenterYplus0dot375minusRimS(rimSize);
      final float modif_centerYplusDivideBy2minusRimS = getCenterYplusDivideBy2minusRimS(rimSize);
      final float modif_centerXminusDivideBy2plusRimS = getCenterXminusDivideBy2plusRimS(rimSize);

      RectF rectF;
      if (radFactor > GaugeConstants.ZERO) {
        //modif optim
        final double marginVar = mMinSize - modif_minSizeMultRadFactor;

        mGauge.calculateMargin(marginVar);
        final double d = mRangePathWidth - mRimWidth;
        rimSize = (float) (d - (4.0 * 10.0));

        if (mCentreY > mCentreX) {
          if (gaugeType == GaugeType.North) {
            rectF = getRectF(modif_centerYminusDivideBy4plusRimS, rimSize, modif_centerXplusDivideBy2minusRimS,
                    modif_centerYplus0dot75minusRimS);
          } else if (gaugeType == GaugeType.South) {
            rectF = getRectF(modif_centerYminus0dot75plusRimS, rimSize, modif_centerXplusDivideBy2minusRimS,
                    modif_centerYplusDivideBy4minusRimS);
          } else if (gaugeType == GaugeType.West) {
            rectF = getRectF(modif_centerYminusDivideBy2plusRimS, modif_centerXminusDivideBy4plusRimS,
                    modif_centerXplus0dot75minusRimS, modif_centerYplus0dot75minusRimS);
          } else if (gaugeType == GaugeType.East) {
            rectF = getRectF(modif_centerYminusDivideBy2plusRimS, modif_centerXminus0dot75plusRimS,
                    modif_centerXplusDivideBy4minusRimS, modif_centerYplusDivideBy4minusRimS);
          } else if (gaugeType == GaugeType.NorthEast) {
            rectF = getRectF(modif_centerYminus0dot375plusRimS, moidf_centerXminus1dot125plusRimS,
                    modif_centerXplus0dot375minusRimS, modif_centerYplus1dot125minusRimS);
          } else if (gaugeType == GaugeType.NorthWest) {
            rectF = getRectF(modif_centerYminus0dot375plusRimS, modif_centerXminus0dot375plusRimS,
                    modif_centerXplus1dot125minusRimS, modif_centerYplus1dot125minusRimS);
          } else if (gaugeType == GaugeType.SouthEast) {
            rectF = getRectF(modif_centerYminus1dot125plusRimS, moidf_centerXminus1dot125plusRimS,
                    modif_centerXplus0dot375minusRimS, modif_centerYplus0dot375minusRimS);
          } else if (gaugeType == GaugeType.SouthWest) {
            rectF = getRectF(modif_centerYminus1dot125plusRimS, modif_centerXminus0dot375plusRimS,
                    modif_centerXplus1dot125minusRimS, modif_centerYplus0dot375minusRimS);
          } else {
            rectF = getRectF(modif_centerYminusDivideBy2plusRimS, rimSize, modif_centerXplusDivideBy2minusRimS,
                    modif_centerYplusDivideBy2minusRimS);
          }
        } else if (gaugeType == GaugeType.West) {
          rectF = getRectF(rimSize, modif_centerXminusDivideBy4plusRimS, modif_centerXplus0dot75minusRimS,
                  modif_centerYplusDivideBy2minusRimS);
        } else if (gaugeType == GaugeType.East) {
          rectF = getRectF(rimSize, modif_centerXminus0dot75plusRimS, modif_centerXplusDivideBy4minusRimS,
                  modif_centerYplusDivideBy2minusRimS);
        } else if (gaugeType == GaugeType.North) {
          rectF = getRectF(modif_centerYminusDivideBy4plusRimS, modif_centerXminusDivideBy2plusRimS,
                  modif_centerXplusDivideBy2minusRimS, modif_centerYplus0dot75minusRimS);
        } else if (gaugeType == GaugeType.South) {
          rectF = getRectF(modif_centerYminus0dot75plusRimS, modif_centerXminusDivideBy2plusRimS,
                  modif_centerXplusDivideBy2minusRimS, modif_centerYplusDivideBy4minusRimS);
        } else if (gaugeType == GaugeType.NorthEast) {
          rectF = getRectF(modif_centerYminus0dot375plusRimS, moidf_centerXminus1dot125plusRimS,
                  modif_centerXplus0dot375minusRimS, modif_centerYplus1dot125minusRimS);
        } else if (gaugeType == GaugeType.NorthWest) {
          rectF = getRectF(modif_centerYminus0dot375plusRimS, modif_centerXminus0dot375plusRimS,
                  modif_centerXplus1dot125minusRimS, modif_centerYplus1dot125minusRimS);
        } else if (gaugeType == GaugeType.SouthEast) {
          rectF = getRectF(modif_centerYminus1dot125plusRimS, moidf_centerXminus1dot125plusRimS,
                  modif_centerXplus0dot375minusRimS, modif_centerYplus0dot375minusRimS);
        } else if (gaugeType == GaugeType.SouthWest) {
          rectF = getRectF(modif_centerYminus1dot125plusRimS, modif_centerXminus0dot375plusRimS,
                  modif_centerXplus1dot125minusRimS, modif_centerYplus0dot375minusRimS);
        } else {
          rectF = getRectF(rimSize, modif_centerXminusDivideBy2plusRimS, modif_centerXplusDivideBy2minusRimS,
                  modif_centerYplusDivideBy2minusRimS);
        }
        mGauge.setmRangeFrame(rectF);
        float rangePointerPoisition = 0.0f;
        if (mGaugePointer instanceof RangePointer) {
          rangePointerPoisition = (float) ((RangePointer) mGaugePointer).getOffset();
        }
        float factor = ((mRangeFrame.left + (((mRangeFrame
                .width() / 2.0f) - mRangeFrame.left) * (radFactor))) + ((rangePointerPoisition) * (mCentreX - mRimWidth)));
        if (mCentreY <= mCentreX) {
          factor = ((mRangeFrame.top + (((mRangeFrame
                  .height() / 2.0f) - mRangeFrame.top) * (radFactor))) + ((rangePointerPoisition) * (mCentreY - mRimWidth)));
          if (gaugeType == GaugeType.West) {
            rectF = getRectF(factor, getCenterXminusDivideBy4plusRimS(factor), getCenterXplusDivideBy2minusRimS(factor),
                    getCenterYplus0dot75minusRimS(factor));
          } else if (gaugeType == GaugeType.East) {
            rectF = getRectF(factor, getCenterXminus0dot75plusRimS(factor), getCenterXplusDivideBy2minusRimS(factor),
                    getCenterYplusDivideBy4minusRimS(factor));
          } else if (gaugeType == GaugeType.North) {
            rectF = getRectF(getCenterYminusDivideBy4plusRimS(factor), getCenterXminusDivideBy2plusRimS(factor),
                    getCenterXplusDivideBy2minusRimS(factor), getCenterYplus0dot75minusRimS(factor));
          } else if (gaugeType == GaugeType.South) {
            rectF = getRectF(getCenterYminus0dot75plusRimS(factor), getCenterXminusDivideBy2plusRimS(factor),
                    getCenterXplusDivideBy2minusRimS(factor), getCenterYplusDivideBy4minusRimS(factor));
          } else if (gaugeType == GaugeType.NorthEast) {
            rectF = getRectF(modif_centerYminus0dot375plusRimS, moidf_centerXminus1dot125plusRimS,
                    modif_centerXplus0dot375minusRimS, modif_centerYplus1dot125minusRimS);
          } else if (gaugeType == GaugeType.NorthWest) {
            rectF = getRectF(modif_centerYminus0dot375plusRimS, modif_centerXminus0dot375plusRimS,
                    modif_centerXplus1dot125minusRimS, modif_centerYplus1dot125minusRimS);
          } else if (gaugeType == GaugeType.SouthEast) {
            rectF = getRectF(modif_centerYminus1dot125plusRimS, moidf_centerXminus1dot125plusRimS,
                    modif_centerXplus0dot375minusRimS, modif_centerYplus0dot375minusRimS);
          } else if (gaugeType == GaugeType.SouthWest) {
            rectF = getRectF(modif_centerYminus1dot125plusRimS, modif_centerXminus0dot375plusRimS,
                    modif_centerXplus1dot125minusRimS, modif_centerYplus0dot375minusRimS);
          } else {
            rectF = getRectF(factor, getCenterXminusDivideBy2plusRimS(factor), getCenterXplusDivideBy2minusRimS(factor),
                    getCenterYplusDivideBy2minusRimS(factor));
          }
        } else if (gaugeType == GaugeType.North) {
          rectF = getRectF(getCenterYminusDivideBy4plusRimS(factor), factor, getCenterXplusDivideBy2minusRimS(factor),
                  getCenterYplus0dot75minusRimS(factor));
        } else if (gaugeType == GaugeType.South) {
          rectF = getRectF(getCenterYminus0dot75plusRimS(factor), factor, getCenterXplusDivideBy2minusRimS(factor),
                  getCenterYplusDivideBy4minusRimS(factor));
        } else if (gaugeType == GaugeType.West) {
          rectF = getRectF(getCenterYminusDivideBy2plusRimS(factor), getCenterXminusDivideBy4plusRimS(factor),
                  getCenterXplus0dot75minusRimS(factor), getCenterYplusDivideBy2minusRimS(factor));
        } else if (gaugeType == GaugeType.East) {
          rectF = getRectF(getCenterYminusDivideBy2plusRimS(factor), getCenterXminus0dot75plusRimS(factor),
                  getCenterXplusDivideBy4minusRimS(factor), getCenterYplusDivideBy2minusRimS(factor));
        } else if (gaugeType == GaugeType.NorthEast) {
          rectF = getRectF(modif_centerYminus0dot375plusRimS, moidf_centerXminus1dot125plusRimS,
                  modif_centerXplus0dot375minusRimS, modif_centerYplus1dot125minusRimS);
        } else if (gaugeType == GaugeType.NorthWest) {
          rectF = getRectF(modif_centerYminus0dot375plusRimS, modif_centerXminus0dot375plusRimS,
                  modif_centerXplus1dot125minusRimS, modif_centerYplus1dot125minusRimS);
        } else if (gaugeType == GaugeType.SouthEast) {
          rectF = getRectF(modif_centerYminus1dot125plusRimS, moidf_centerXminus1dot125plusRimS,
                  modif_centerXplus0dot375minusRimS, modif_centerYplus0dot375minusRimS);
        } else if (gaugeType == GaugeType.SouthWest) {
          rectF = getRectF(modif_centerYminus1dot125plusRimS, modif_centerXminus0dot375plusRimS,
                  modif_centerXplus1dot125minusRimS, modif_centerYplus0dot375minusRimS);
        } else {
          rectF = getRectF(getCenterYminusDivideBy2plusRimS(factor), factor, getCenterXplusDivideBy2minusRimS(factor),
                  getCenterYplusDivideBy2minusRimS(factor));
        }
      } else {
        if (mGaugePointer instanceof RangePointer) {
          rimSize = (float) ((mMinSize - mRimWidth) - (((((RangePointer) mGaugePointer)
                  .getOffset())) * (mCentreX - mRimWidth)) - (mGaugePointer.getWidth() / 2.0));
        }
        if (mCentreY > mCentreX) {
          if (gaugeType == GaugeType.North) {
            rectF = getRectF(modif_centerYminusDivideBy4plusRimS, rimSize, modif_centerXplusDivideBy2minusRimS,
                    modif_centerYplus0dot75minusRimS);
          } else if (gaugeType == GaugeType.South) {
            rectF = getRectF(modif_centerYminus0dot75plusRimS, rimSize, modif_centerXplusDivideBy2minusRimS,
                    modif_centerYplusDivideBy4minusRimS);
          } else if (gaugeType == GaugeType.West) {
            rectF = getRectF(modif_centerYminusDivideBy2plusRimS, modif_centerXminusDivideBy4plusRimS,
                    modif_centerXplus0dot75minusRimS, modif_centerYplusDivideBy2minusRimS);
          } else if (gaugeType == GaugeType.East) {
            rectF = getRectF(modif_centerYminusDivideBy2plusRimS, modif_centerXminus0dot75plusRimS,
                    modif_centerXplusDivideBy4minusRimS, modif_centerYplusDivideBy2minusRimS);
          } else if (gaugeType == GaugeType.NorthEast) {
            rectF = getRectF(modif_centerYminus0dot375plusRimS, moidf_centerXminus1dot125plusRimS,
                    modif_centerXplus0dot375minusRimS, modif_centerYplus1dot125minusRimS);
          } else if (gaugeType == GaugeType.NorthWest) {
            rectF = getRectF(modif_centerYminus0dot375plusRimS, modif_centerXminus0dot375plusRimS,
                    modif_centerXplus1dot125minusRimS, modif_centerYplus1dot125minusRimS);
          } else if (gaugeType == GaugeType.SouthEast) {
            rectF = getRectF(modif_centerYminus1dot125plusRimS, moidf_centerXminus1dot125plusRimS,
                    modif_centerXplus0dot375minusRimS, modif_centerYplus0dot375minusRimS);
          } else if (gaugeType == GaugeType.SouthWest) {
            rectF = getRectF(modif_centerYminus1dot125plusRimS, modif_centerXminus0dot375plusRimS,
                    modif_centerXplus1dot125minusRimS, modif_centerYplus0dot375minusRimS);
          } else {
            rectF = getRectF(modif_centerYminusDivideBy2plusRimS, rimSize, modif_centerXplusDivideBy2minusRimS,
                    modif_centerYplusDivideBy2minusRimS);
          }
        } else if (gaugeType == GaugeType.West) {
          rectF = getRectF(rimSize, modif_centerXminusDivideBy4plusRimS, modif_centerXplus0dot75minusRimS,
                  modif_centerYplusDivideBy2minusRimS);
        } else if (gaugeType == GaugeType.East) {
          rectF = getRectF(rimSize, modif_centerXminus0dot75plusRimS, modif_centerXplusDivideBy4minusRimS,
                  modif_centerYplusDivideBy2minusRimS);
        } else if (gaugeType == GaugeType.North) {
          rectF = getRectF(modif_centerYminusDivideBy4plusRimS, modif_centerXminusDivideBy2plusRimS,
                  modif_centerXplusDivideBy2minusRimS, modif_centerYplus0dot75minusRimS);
        } else if (gaugeType == GaugeType.South) {
          rectF = getRectF(modif_centerYminus0dot75plusRimS, modif_centerXminusDivideBy2plusRimS,
                  modif_centerXplusDivideBy2minusRimS, modif_centerYplusDivideBy4minusRimS);
        } else if (gaugeType == GaugeType.NorthEast) {
          rectF = getRectF(modif_centerYminus0dot375plusRimS, moidf_centerXminus1dot125plusRimS,
                  modif_centerXplus0dot375minusRimS, modif_centerYplus1dot125minusRimS);
        } else if (gaugeType == GaugeType.NorthWest) {
          rectF = getRectF(modif_centerYminus0dot375plusRimS, modif_centerXminus0dot375plusRimS,
                  modif_centerXplus1dot125minusRimS, modif_centerYplus1dot125minusRimS);
        } else if (gaugeType == GaugeType.SouthEast) {
          rectF = getRectF(modif_centerYminus1dot125plusRimS, moidf_centerXminus1dot125plusRimS,
                  modif_centerXplus0dot375minusRimS, modif_centerYplus0dot375minusRimS);
        } else if (gaugeType == GaugeType.SouthWest) {
          rectF = getRectF(modif_centerYminus1dot125plusRimS, modif_centerXminus0dot375plusRimS,
                  modif_centerXplus1dot125minusRimS, modif_centerYplus0dot375minusRimS);
        } else {
          rectF = getRectF(rimSize, modif_centerXminusDivideBy2plusRimS, modif_centerXplusDivideBy2minusRimS,
                  modif_centerYplusDivideBy2minusRimS);
        }
        mGauge.setmRangeFrame(mRangeFrame);
      }
      onDrawPointers(canvas, mGaugeScale, rectF);
    }
    super.onDraw(canvas);
  }

  @NonNull private RectF getRectF(final float factor, final float centerXminusDivideBy4plusRimS, final float centerXplusDivideBy2minusRimS,
                                  final float centerYplus0dot75minusRimS) {
    return new RectF(centerXminusDivideBy4plusRimS, factor, centerXplusDivideBy2minusRimS, centerYplus0dot75minusRimS);
  }


  private void onDrawPointers(@NonNull final Canvas canvas, @NonNull final GaugeScale gaugeScale, @NonNull final RectF rectF) {
    final Paint paint = new Paint();
    final int color = mGaugePointer.getColor();
    final double width = mGaugePointer.getWidth();
    paint.setAntiAlias(true);
    if (mGaugePointer instanceof RangePointer) {
      paint.setColor(color);
      paint.setStyle(Paint.Style.STROKE);
      paint.setStrokeWidth((float) width);
      canvas.drawArc(rectF, (float) gaugeScale.getStartAngle(),
              (float) ((getPointerAngle(value, gaugeScale) - getPointerAngle(gaugeScale.getStartValue(),
                      gaugeScale)) / GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY), false, paint);
      return;
    }
    final double x0;
    final double y0;
    final double x2;
    final double y2;
    final float x1;
    final float y1;
    paint.setColor(color);
    paint.setStyle(Paint.Style.FILL);
    paint.setStrokeWidth((float) width);
    final double pointerLength = (mLabelsPathHeight / 2.0) - (((NeedlePointer) mGaugePointer).getLengthFactor() * (mLabelsPathHeight / 2.0));
    final float innerSize = (float) ((mLabelsPathHeight / 2.0) - pointerLength);
    final float pointerMargin = (float) ((modif_minSizeDivideBy2 - (mLabelsPathHeight / 2.0)) + pointerLength);
    final double angle = getPointerAngle(value, gaugeScale);

    //modif optim
    final float modif_widthMultCos = (float) (modif_widthDivideBy2 * Math.cos(angle - 90.0));
    final float modif_widthMultBySin = (float) (modif_widthDivideBy2 * Math.sin(angle - 90.0));
    final float modif_dividedWidthMultByCos90plusAngle = (float) (modif_widthDivideBy2 * Math.cos(90.0 + angle));
    final float modif_dividedWidthMultBySin90plusAngle = (float) (modif_widthDivideBy2 * Math.sin(90.0 + angle));
    final float modif_innerSizeMultBy1dot5MultBySin = (float) ((1.5 * innerSize) * Math.sin(angle));
    final float modif_innerSizeMultBy1dot5MultByCos = (float) ((1.5 * innerSize) * Math.cos(angle));
    final float modif_sinMultInnerSize = (float) (Math.sin(angle) * innerSize);
    final float modif_cosMultInnerSize = (float) (Math.cos(angle) * innerSize);
    final float modif_centerYminusMinSizeDivideBy4 = mCentreY - modif_minSizeDivideBy4;
    final float modif_centerXplusMinSizeDivideBy4 = mCentreX + modif_minSizeDivideBy4;
    final float modif_centerYplus0dot375 = mCentreY + modif_minSizeMultBy0dot375;
    final float modif_centerXplus0dot375 = mCentreX + modif_minSizeMultBy0dot375;
    final float modif_centerXminusSizeDivideBy4 = mCentreX - modif_minSizeDivideBy4;
    final float modif_centerXminus0dot375 = mCentreX - modif_minSizeMultBy0dot375;
    final float modif_centerYminus0dot375 = mCentreY - modif_minSizeMultBy0dot375;

    if (gaugeType == GaugeType.West) {
      x0 = modif_centerXplusMinSizeDivideBy4 + modif_widthMultCos;
    } else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.SouthWest) {
      x0 = modif_centerXplus0dot375 + modif_widthMultCos;
    } else if (gaugeType == GaugeType.East) {
      x0 = modif_centerXminusSizeDivideBy4 + modif_widthMultCos;
    } else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.SouthEast) {
      x0 = modif_centerXminus0dot375 + modif_widthMultCos;
    } else {
      x0 = mCentreX + modif_widthMultCos;
    }

    if (gaugeType == GaugeType.North) {
      y0 = modif_centerXplusMinSizeDivideBy4 + modif_widthMultBySin;
    } else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.NorthWest) {
      y0 = modif_centerYplus0dot375 + modif_widthMultBySin;
    } else if (gaugeType == GaugeType.South) {
      y0 = modif_centerYminusMinSizeDivideBy4 + modif_widthMultBySin;
    } else if (gaugeType == GaugeType.SouthEast || gaugeType == GaugeType.SouthWest) {
      y0 = modif_centerYminus0dot375 + modif_widthMultBySin;
    } else {
      y0 = mCentreY + modif_widthMultBySin;
    }

    if (gaugeType == GaugeType.West) {
      x2 = modif_centerXplusMinSizeDivideBy4 + modif_dividedWidthMultByCos90plusAngle;
    } else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.SouthWest) {
      x2 = modif_centerXplus0dot375 + modif_dividedWidthMultByCos90plusAngle;
    } else if (gaugeType == GaugeType.East) {
      x2 = modif_centerXminusSizeDivideBy4 + modif_dividedWidthMultByCos90plusAngle;
    } else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.SouthEast) {
      x2 = modif_centerXminus0dot375 + modif_dividedWidthMultByCos90plusAngle;
    } else {
      x2 = mCentreX + modif_dividedWidthMultByCos90plusAngle;
    }

    if (gaugeType == GaugeType.North) {
      y2 = modif_centerXplusMinSizeDivideBy4 + modif_dividedWidthMultBySin90plusAngle;
    } else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.NorthEast) {
      y2 = modif_centerYplus0dot375 + modif_dividedWidthMultBySin90plusAngle;
    } else if (gaugeType == GaugeType.South) {
      y2 = modif_centerYminusMinSizeDivideBy4 + modif_dividedWidthMultBySin90plusAngle;
    } else if (gaugeType == GaugeType.SouthEast || gaugeType == GaugeType.SouthWest) {
      y2 = modif_centerYminus0dot375 + modif_dividedWidthMultBySin90plusAngle;
    } else {
      y2 = mCentreY + modif_dividedWidthMultBySin90plusAngle;
    }

    if (mCentreY > mCentreX) {
      if (gaugeType == GaugeType.West) {
        x1 = ((modif_centerXminusSizeDivideBy4 + innerSize) + modif_cosMultInnerSize) + pointerMargin;
      } else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.SouthWest) {
        x1 = ((getCenterXminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
      } else if (gaugeType == GaugeType.East) {
        x1 = ((getCenterXminus0dot75plusRimS(innerSize)) + modif_cosMultInnerSize) + pointerMargin;
      } else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.SouthEast) {
        x1 = (((mCentreX - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
      } else {
        x1 = (modif_cosMultInnerSize + innerSize) + pointerMargin;
      }
      if (gaugeType == GaugeType.North) {
        y1 = ((modif_centerYminusMinSizeDivideBy4 + innerSize) + modif_sinMultInnerSize) + pointerMargin;
      } else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.NorthWest) {
        y1 = ((getCenterYminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
      } else if (gaugeType == GaugeType.South) {
        y1 = ((getCenterYminus0dot75plusRimS(innerSize)) + modif_sinMultInnerSize) + pointerMargin;
      } else if (gaugeType == GaugeType.SouthEast || gaugeType == GaugeType.SouthWest) {
        y1 = (((mCentreY - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
      } else {
        y1 = ((getCenterYminusDivideBy2plusRimS(innerSize)) + modif_sinMultInnerSize) + pointerMargin;
      }
    } else {
      if (gaugeType == GaugeType.West) {
        x1 = ((modif_centerXminusSizeDivideBy4 + innerSize) + modif_cosMultInnerSize) + pointerMargin;
      } else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.SouthWest) {
        x1 = ((getCenterXminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
      } else if (gaugeType == GaugeType.East) {
        x1 = ((getCenterXminus0dot75plusRimS(innerSize)) + modif_cosMultInnerSize) + pointerMargin;
      } else if (gaugeType == GaugeType.NorthEast || gaugeType == GaugeType.SouthEast) {
        x1 = (((mCentreX - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultByCos) + pointerMargin;
      } else {
        x1 = ((getCenterXminusDivideBy2plusRimS(innerSize)) + modif_cosMultInnerSize) + pointerMargin;
      }
      if (gaugeType == GaugeType.North) {
        y1 = ((modif_centerYminusMinSizeDivideBy4 + innerSize) + modif_sinMultInnerSize) + pointerMargin;
      } else if (gaugeType == GaugeType.NorthWest || gaugeType == GaugeType.NorthEast) {
        y1 = ((getCenterYminus1dot125plusRimS(innerSize)) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
      } else if (gaugeType == GaugeType.South) {
        y1 = ((getCenterYminus0dot75plusRimS(innerSize)) + modif_sinMultInnerSize) + pointerMargin;
      } else if (gaugeType == GaugeType.SouthEast || gaugeType == GaugeType.SouthWest) {
        y1 = (((mCentreY - modif_minSizeMultBy0dot875) + innerSize) + modif_innerSizeMultBy1dot5MultBySin) + pointerMargin;
      } else {
        y1 = (modif_sinMultInnerSize + innerSize) + pointerMargin;
      }
    }
    if (((NeedlePointer) mGaugePointer).getType() != NeedleType.Bar) {
      final Path path = new Path();
      path.moveTo((float) x0, (float) y0);
      path.lineTo(x1, y1);
      path.lineTo((float) x2, (float) y2);
      paint.setStyle(Paint.Style.FILL);
      path.close();
      canvas.drawPath(path, paint);
    } else if (mCentreY > mCentreX) {
      if (gaugeType == GaugeType.North) {
        canvas.drawLine(mCentreX, modif_centerXplusMinSizeDivideBy4, x1, y1, paint);
      } else if (gaugeType == GaugeType.South) {
        canvas.drawLine(mCentreX, modif_centerYminusMinSizeDivideBy4, x1, y1, paint);
      } else if (gaugeType == GaugeType.West) {
        canvas.drawLine(modif_centerXplusMinSizeDivideBy4, mCentreY, x1, y1, paint);
      } else if (gaugeType == GaugeType.East) {
        canvas.drawLine(modif_centerXminusSizeDivideBy4, mCentreY, x1, y1, paint);
      } else if (gaugeType == GaugeType.NorthEast) {
        canvas.drawLine(modif_centerXminus0dot375, modif_centerYplus0dot375, x1, y1, paint);
      } else if (gaugeType == GaugeType.NorthWest) {
        canvas.drawLine(modif_centerXplus0dot375, modif_centerYplus0dot375, x1, y1, paint);
      } else if (gaugeType == GaugeType.SouthEast) {
        canvas.drawLine(modif_centerXminus0dot375, modif_centerYminus0dot375, x1, y1, paint);
      } else if (gaugeType == GaugeType.SouthWest) {
        canvas.drawLine(modif_centerXplus0dot375, modif_centerYminus0dot375, x1, y1, paint);
      } else {
        canvas.drawLine(mCentreX, mCentreY, x1, y1, paint);
      }
    } else if (gaugeType == GaugeType.North) {
      canvas.drawLine(mCentreX, modif_centerXplusMinSizeDivideBy4, x1, y1, paint);
    } else if (gaugeType == GaugeType.South) {
      canvas.drawLine(mCentreX, modif_centerYminusMinSizeDivideBy4, x1, y1, paint);
    } else if (gaugeType == GaugeType.West) {
      canvas.drawLine(modif_centerXplusMinSizeDivideBy4, mCentreY, x1, y1, paint);
    } else if (gaugeType == GaugeType.East) {
      canvas.drawLine(modif_centerXminusSizeDivideBy4, mCentreY, x1, y1, paint);
    } else if (gaugeType == GaugeType.NorthEast) {
      canvas.drawLine(modif_centerXminus0dot375, modif_centerYplus0dot375, x1, y1, paint);
    } else if (gaugeType == GaugeType.NorthWest) {
      canvas.drawLine(modif_centerXplus0dot375, modif_centerYplus0dot375, x1, y1, paint);
    } else if (gaugeType == GaugeType.SouthEast) {
      canvas.drawLine(modif_centerXminus0dot375, modif_centerYminus0dot375, x1, y1, paint);
    } else if (gaugeType == GaugeType.SouthWest) {
      canvas.drawLine(modif_centerXplus0dot375, modif_centerYminus0dot375, x1, y1, paint);
    } else {
      canvas.drawLine(mCentreX, mCentreY, x1, y1, paint);
    }

    paint.setColor(((NeedlePointer) mGaugePointer).getKnobColor());
    final float knobRaiuds = (float) ((NeedlePointer) mGaugePointer).getKnobRadius();
    if (gaugeType == GaugeType.North) {
      canvas.drawCircle(mCentreX, modif_centerXplusMinSizeDivideBy4, knobRaiuds, paint);
    } else if (gaugeType == GaugeType.South) {
      canvas.drawCircle(mCentreX, modif_centerYminusMinSizeDivideBy4, knobRaiuds, paint);
    } else if (gaugeType == GaugeType.West) {
      canvas.drawCircle(modif_centerXplusMinSizeDivideBy4, mCentreY, knobRaiuds, paint);
    } else if (gaugeType == GaugeType.East) {
      canvas.drawCircle(modif_centerXminusSizeDivideBy4, mCentreY, knobRaiuds, paint);
    } else if (gaugeType == GaugeType.NorthEast) {
      canvas.drawCircle(modif_centerXminus0dot375, modif_centerYplus0dot375, knobRaiuds, paint);
    } else if (gaugeType == GaugeType.NorthWest) {
      canvas.drawCircle(modif_centerXplus0dot375, modif_centerYplus0dot375, knobRaiuds, paint);
    } else if (gaugeType == GaugeType.SouthEast) {
      canvas.drawCircle(modif_centerXminus0dot375, modif_centerYminus0dot375, knobRaiuds, paint);
    } else if (gaugeType == GaugeType.SouthWest) {
      canvas.drawCircle(modif_centerXplus0dot375, modif_centerYminus0dot375, knobRaiuds, paint);
    } else {
      canvas.drawCircle(mCentreX, mCentreY, knobRaiuds, paint);
    }
  }

  private double getPointerAngle(double pointerValue, @NonNull final GaugeScale gaugeScale) {
    final double endVal = gaugeScale.getEndValue();
    if (pointerValue > endVal) {
      pointerValue = endVal;
    }
    final double startVal = gaugeScale.getStartValue();
    final double startAngle = gaugeScale.getStartAngle() * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY;
    if (pointerValue < startVal) {
      pointerValue = startVal;
    }
    return startAngle + (((pointerValue - startVal) * (gaugeScale.getSweepAngle() / (gaugeScale
            .getEndValue() - startVal))) * GaugeConstants.STRANGLE_MULTIPLIER_DEPENDS_ON_TICK_QUANTITY);
  }

  private float getCenterXminusDivideBy2plusRimS(final float rimSize) {
    return (mCentreX - modif_minSizeDivideBy2) + rimSize;
  }

  private float getCenterYplusDivideBy2minusRimS(final float rimSize) {
    return (mCentreY + modif_minSizeDivideBy2) - rimSize;
  }

  private float getCenterYplus0dot375minusRimS(final float rimSize) {
    return (mCentreY + modif_minSizeMultBy0dot375) - rimSize;
  }

  private float getCenterYminus1dot125plusRimS(final float rimSize) {
    return (mCentreY - modif_minSizeMultBy1dot125) + rimSize;
  }

  private float getCenterXplus1dot125minusRimS(final float rimSize) {
    return (mCentreX + modif_minSizeMultBy1dot125) - rimSize;
  }

  private float getCenterXminus0dot375plusRimS(final float rimSize) {
    return (mCentreX - modif_minSizeMultBy0dot375) + rimSize;
  }

  private float getCenterYplus1dot125minusRimS(final float rimSize) {
    return (mCentreY + modif_minSizeMultBy1dot125) - rimSize;
  }

  private float getCenterXplus0dot375minusRimS(final float rimSize) {
    return (mCentreX + modif_minSizeMultBy0dot375) - rimSize;
  }

  private float getCenterYminus0dot375plusRimS(final float rimSize) {
    return (mCentreY - modif_minSizeMultBy0dot375) + rimSize;
  }

  private float getCenterXplusDivideBy4minusRimS(final float rimSize) {
    return (mCentreX + modif_minSizeDivideBy4) - rimSize;
  }

  private float getCenterXminus1dot125plusRimS(final float rimSize) {
    return (mCentreX - modif_minSizeMultBy1dot125) + rimSize;
  }

  private float getCenterXminus0dot75plusRimS(final float rimSize) {
    return (mCentreX - modif_minSizeMultBy0dot75) + rimSize;
  }

  private float getCenterYplusDivideBy4minusRimS(final float rimSize) {
    return (mCentreY + modif_minSizeDivideBy4) - rimSize;
  }

  private float getCenterXplus0dot75minusRimS(final float rimSize) {
    return (mCentreX + modif_minSizeMultBy0dot75) - rimSize;
  }

  private float getCenterYminusDivideBy2plusRimS(final float rimSize) {
    return (mCentreY - modif_minSizeDivideBy2) + rimSize;
  }

  private float getCenterXminusDivideBy4plusRimS(final float rimSize) {
    return (mCentreX - modif_minSizeDivideBy4) + rimSize;
  }

  private float getCenterYminus0dot75plusRimS(final float rimSize) {
    return (mCentreY - modif_minSizeMultBy0dot75) + rimSize;
  }

  private float getCenterYplus0dot75minusRimS(final float rimSize) {
    return (mCentreY + modif_minSizeMultBy0dot75) - rimSize;
  }

  private float getCenterXplusDivideBy2minusRimS(final float rimSize) {
    return (mCentreX + modif_minSizeDivideBy2) - rimSize;
  }

  private float getCenterYminusDivideBy4plusRimS(final float rimSize) {
    return (mCentreY - modif_minSizeDivideBy4) + rimSize;
  }
}