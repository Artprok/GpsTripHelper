package com.example.aprokopenko.triphelper.utils.util_methods;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

import butterknife.ButterKnife;

/**
 * Class with some util methods.
 */
public class UtilMethods {
  private static final String LOG_TAG = "UtilMethods";
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private static final float[] testingTempVal = {1};
  private static final Random random = new Random();

  /**
   * Method for getting consumption level depends on average speed.
   *
   * @param avgSpeed {@link Float} average speed
   * @param fuelCons {@link Float} fuelConsumption
   * @return {@link Float} value representing fuel consumption for moving at this average speed.
   */
  public static float getFuelConsumptionLevel(final float avgSpeed, final float fuelCons) {
    if (avgSpeed >= ConstantValues.HIGHWAY_SPEED_AVG_SPEED) {
      if (DEBUG) {
        Log.d(LOG_TAG, "getFuelConsumptionLevel: HighwayLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
      }
      return fuelCons + ConstantValues.CONSUMPTION_VERY_LOW_ADD;
    } else if (avgSpeed <= ConstantValues.HIGHWAY_SPEED_AVG_SPEED && avgSpeed > ConstantValues.LOW_TRAFFIC_AVG_SPEED) {
      if (DEBUG) {
        Log.d(LOG_TAG, "getFuelConsumptionLevel: LowLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
      }
      return fuelCons + ConstantValues.CONSUMPTION_LOW_TRAFFIC_ADD;
    } else if (avgSpeed <= ConstantValues.LOW_TRAFFIC_AVG_SPEED && avgSpeed > ConstantValues.MEDIUM_TRAFFIC_AVG_SPEED) {
      if (DEBUG) {
        Log.d(LOG_TAG, "getFuelConsumptionLevel: NormalLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
      }
      return fuelCons + ConstantValues.CONSUMPTION_NORMAL_TRAFFIC_ADD;
    } else if (avgSpeed <= ConstantValues.MEDIUM_TRAFFIC_AVG_SPEED && avgSpeed > ConstantValues.HIGH_TRAFFIC_AVG_SPEED) {
      if (DEBUG) {
        Log.d(LOG_TAG, "getFuelConsumptionLevel: MediumLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
      }
      return fuelCons + ConstantValues.CONSUMPTION_MEDIUM_TRAFFIC_ADD;
    } else if (avgSpeed <= ConstantValues.HIGH_TRAFFIC_AVG_SPEED && avgSpeed > ConstantValues.VERY_HIGH_TRAFFIC_AVG_SPEED) {
      if (DEBUG) {
        Log.d(LOG_TAG, "getFuelConsumptionLevel: HighLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
      }
      return fuelCons + ConstantValues.CONSUMPTION_HIGH_TRAFFIC_ADD;
    } else if (avgSpeed <= ConstantValues.VERY_HIGH_TRAFFIC_AVG_SPEED) {
      if (DEBUG) {
        Log.d(LOG_TAG, "getFuelConsumptionLevel: VeryHighLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
      }
      return fuelCons + ConstantValues.CONSUMPTION_VERY_HIGH_TRAFFIC_ADD;
    } else {
      if (DEBUG) {
        Log.d(LOG_TAG, "getFuelConsumptionLevel: Impossible thing! Number out of all interval!! impossible!!! fuel cons - " +
                fuelCons + "average speed - " + avgSpeed);
      }
      return fuelCons;
    }
  }

  /**
   * Util method for format {@link Float} to format with one digit after ".".
   *
   * @param floatToFormat {@link Float} value to format
   * @return {@link Float} formatted value
   */
  public static String formatFloatDecimalFormat(@Nullable final Float floatToFormat) {
    if (floatToFormat != null) {
      return new DecimalFormat("######.#").format(floatToFormat);
    } else {
      if (DEBUG) {
        Log.e(LOG_TAG, "formatFloatDecimalFormat: PROBLEM WITH FORMAT (floatToFormat is NULL), returned 0.00");
      }
      return "0.00";
    }
  }

  /**
   * Method to format {@link Float} to {@link Integer} and cut all digits after '.'.
   *
   * @param floatToFormat {@link Float} value to format
   * @return {@link Integer} formatted value
   */
  public static String formatFloatToIntFormat(@Nullable final Float floatToFormat) {
    if (floatToFormat != null) {
      if (DEBUG) {
        Log.d(LOG_TAG, "formatFloatToIntFormat inUtilMethod: " + floatToFormat);
      }
      return new DecimalFormat("######").format(floatToFormat);
    } else {
      if (DEBUG) {
        Log.e(LOG_TAG, "formatFloatDecimalFormat: PROBLEM WITH FORMAT (floatToFormat is NULL), returned 0.00");
      }
      return "0.00";
    }
  }

  /**
   * Util method for delete file with internal app data.
   *
   * @param context {@link Context}
   * @return {@link Boolean} value that represents state of file deleting:
   * true - deleted
   * false - not deleted
   */
  public static boolean eraseFile(@NonNull final Context context) {
    context.deleteFile(ConstantValues.INTERNAL_SETTING_FILE_NAME);
    return context.deleteFile(ConstantValues.FILE_NAME);
  }

  /**
   * Method for parsing {@link Date} to {@link String}.
   *
   * @param dateString {@link Date} value to parse
   * @return {@link String} parsed value
   */
  public static String parseDate(@NonNull final Date dateString) {
    return ConstantValues.DATE_FORMAT.format(dateString);
  }

  /**
   * Method for adding a fragment.
   *
   * @param fragment         {@link Fragment} to add
   * @param fragment_tag     {@link String} tag of added fragment
   * @param fragmentActivity {@link android.support.v4.app.FragmentActivity} parent activity
   */
  public static void addFragment(@NonNull final Fragment fragment, @NonNull final String fragment_tag, @NonNull final android.support.v4.app.FragmentActivity fragmentActivity) {
    final FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
    fragmentTransaction.add(fragment, fragment_tag)
            .commit();
  }

  /**
   * Method for replacing current {@link Fragment}
   *
   * @param fragment         {@link Fragment} new {@link Fragment}
   * @param fragment_tag     {@link Fragment} tag of new {@link Fragment}
   * @param fragmentActivity {@link android.support.v4.app.FragmentActivity} parent activity
   */
  public static void replaceFragment(@NonNull final Fragment fragment, @NonNull final String fragment_tag, @NonNull final android.support.v4.app.FragmentActivity fragmentActivity) {
    final int animEnter;
    final int animExit;
    final int animPopEnter;
    final int animPopExit;

    // choice of animations: 1 - portrait, or 2 - landscape
    if (fragmentActivity.getResources()
            .getConfiguration().orientation == 1) {
      animEnter = R.anim.fragment_enter_vertical;
      animExit = R.anim.fragment_exit_vertical;
      animPopEnter = R.anim.pop_enter_vertical;
      animPopExit = R.anim.pop_exit_vertical;
    } else {
      animEnter = R.anim.fragment_enter_horizontal;
      animExit = R.anim.fragment_exit_horizontal;
      animPopEnter = R.anim.pop_enter_horizontal;
      animPopExit = R.anim.pop_exit_horizontal;
    }

    final FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
    fragmentTransaction.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit);
    fragmentTransaction
            .replace(R.id.fragmentContainer, fragment, fragment_tag)
            .addToBackStack(fragment.getTag())
            .commitAllowingStateLoss();
  }

  /**
   * Method for animating {@link TextView}.
   *
   * @param initialValue {@link Integer} initial value of {@link TextView}
   * @param finalValue   {@link Integer} value to set in {@link TextView}
   * @param textview     {@link TextView} itself
   */
  public static void animateTextView(final int initialValue, final int finalValue, @Nullable final TextView textview) {
    final ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
    valueAnimator.setDuration(ConstantValues.SPEEDOMETER_TEXT_ANIM_DURATION);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override public void onAnimationUpdate(@NonNull final ValueAnimator valueAnimator) {
        if (textview != null) {
          textview.setText(valueAnimator.getAnimatedValue().toString());
        }
      }
    });
    valueAnimator.start();
  }

  /**
   * Method for animating {@link FloatingActionButton}  animation. Extends{@link android.view.animation.Animation.AnimationListener}. (floating left when open, and floating back (right) when close).
   *
   * @param fab         {@link FloatingActionButton} to animate
   * @param transitionX {@link Integer} value to move left and right
   */
  public static void animateFabTransition(@NonNull final FloatingActionButton fab, final int transitionX) {
    fab.animate().setListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(@NonNull final Animator animator) {
      }

      @Override public void onAnimationEnd(@NonNull final Animator animator) {
        fab.setTranslationX(transitionX);
      }

      @Override public void onAnimationCancel(@NonNull final Animator animator) {
      }

      @Override public void onAnimationRepeat(@NonNull final Animator animator) {
      }
    }).setDuration(ConstantValues.TEXT_ANIM_DURATION).translationX(transitionX).start();
  }

  /**
   * Method for show {@link Toast} with set text.
   *
   * @param context      {@link Context}
   * @param stringToShow {@link String} text to show in {@link Toast}
   */
  public static void showToast(@NonNull final Context context, @NonNull final CharSequence stringToShow) {
    final Toast toast = Toast.makeText(context, stringToShow, Toast.LENGTH_SHORT);
    toast.show();
  }

  /**
   * Method that showing start dialog with full tutorial.
   *
   * @param context {@link Context}
   */
  public static void firstStartTutorialDialog(@NonNull final Context context) {
    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
    builder.setIcon(R.drawable.how_to_use);
    builder.setTitle(context.getString(R.string.tutorialTitle));
    builder.setMessage(R.string.tutorialWantToKnowInfo).setCancelable(true)
            .setPositiveButton(R.string.tutorialNext, new DialogInterface.OnClickListener() {
              public void onClick(@NonNull final DialogInterface dialog, final int id) {
                showTutorialDialog(context);
              }
            }).setNegativeButton(R.string.tutorialNo, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(@NonNull final DialogInterface dialog, final int id) {
        dialog.cancel();
      }
    });
    final android.support.v7.app.AlertDialog alert = builder.create();
    alert.show();
  }

  /**
   * Method showing a dialog when application can't open a map in a good manner.
   *
   * @param context {@link Context}
   */
  public static void MapNotAvailableDialog(@NonNull final Context context) {
    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
    builder.setIcon(R.drawable.something_went_wrong);
    builder.setTitle(context.getString(R.string.somethingWentWrongTitle));
    builder.setMessage(R.string.somethingWentWrongMessage).setCancelable(true)
            .setPositiveButton(R.string.somethingWentWrongOk, new DialogInterface.OnClickListener() {
              public void onClick(@NonNull final DialogInterface dialog, final int id) {
                dialog.cancel();
              }
            });
    final android.support.v7.app.AlertDialog alert = builder.create();
    alert.show();
  }

  /**
   * Method for showing default about dialog.
   *
   * @param context {@link Context}
   */
  public static void buildAndShowAboutDialog(@NonNull final Context context) {
    final Resources res = context.getResources();
    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
    builder.setIcon(R.drawable.btn_about);
    builder.setTitle(context.getString(R.string.aboutTitle));
    builder.setMessage(R.string.aboutMainText).setCancelable(true)
            .setPositiveButton(R.string.aboutRateButton, new DialogInterface.OnClickListener() {
              public void onClick(@NonNull final DialogInterface dialog, final int id) {
                rateApp(context);
              }
            }).setNegativeButton(R.string.aboutNegativeButton, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(@NonNull final DialogInterface dialog, final int id) {
        dialog.cancel();
      }
    }).setNeutralButton(R.string.aboutFeedbackButton, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(@NonNull final DialogInterface dialog, final int id) {
        Intent emailFeedback = new Intent(Intent.ACTION_SEND);
        emailFeedback.setType("text/email");
        emailFeedback.putExtra(Intent.EXTRA_EMAIL, new String[]{res.getString(R.string.emailForFeedback)});
        emailFeedback.putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.subjectForFeedback));
        emailFeedback.putExtra(Intent.EXTRA_TEXT, res.getString(R.string.extraTextInFeedback) + "");
        try {
          context.startActivity(Intent.createChooser(emailFeedback, res.getString(R.string.sendWithFeedback)));
        } catch (android.content.ActivityNotFoundException exception) {
          Log.e(LOG_TAG, "onClick: Nothing to send email :(");
          UtilMethods.showToast(context, "No email clients installed on device!");
        }
      }
    });
    final android.support.v7.app.AlertDialog alert = builder.create();
    alert.show();
  }

  /**
   * Method that checks if GPS is enabled now.
   *
   * @param context {@link Context}
   * @return {@link Boolean} true - if enabled, false - disabled
   */
  public static boolean checkIfGpsEnabled(@NonNull final Context context) {
    final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  }

  /**
   * Method that checks if GPS is enabled and show dialog that offer to enable GPS.
   *
   * @param context {@link Context}
   */
  public static void checkIfGpsEnabledAndShowDialogs(@NonNull final Context context) {
    if (!checkIfGpsEnabled(context)) {
      if (DEBUG) {
        Log.i(LOG_TAG, "checkIfGpsEnabledAndShowDialogs: NotEnabled");
      }
      buildAlertMessageNoGps(context);
    } else {
      if (DEBUG) {
        Log.i(LOG_TAG, "checkIfGpsEnabledAndShowDialogs: Gps Enabled");
      }
    }
  }

  /**
   * Util method to set {@link FloatingActionButton} invisible.
   *
   * @param activity {@link Activity}
   */
  public static void hideFab(@NonNull final Activity activity) {
    final FloatingActionButton fab = ButterKnife.findById(activity, R.id.btn_fab);
    fab.setVisibility(View.INVISIBLE);
  }

  /**
   * Util method to set {@link FloatingActionButton} visible.
   *
   * @param activity {@link Activity}
   */
  public static void showFab(@NonNull final Activity activity) {
    final FloatingActionButton fab = ButterKnife.findById(activity, R.id.btn_fab);
    fab.setVisibility(View.VISIBLE);
  }

  /**
   * Method for checking if permission allowed.
   *
   * @param context {@link Context}
   * @return {@link Boolean} value represents if allowed. True - allowed, False - no
   */
  public static boolean isPermissionAllowed(@NonNull final Context context) {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
  }

  /**
   * Method that show alert that GPS not enabled and user needs to enable it.
   *
   * @param context {@link Context}
   */
  private static void buildAlertMessageNoGps(@NonNull final Context context) {
    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
    builder.setIcon(R.drawable.gps_icon);
    builder.setTitle(context.getString(R.string.gps_dialog_title));
    builder.setMessage(R.string.gps_dialog_text_label).setCancelable(false)
            .setPositiveButton(R.string.gps_dialog_agree_enable, new DialogInterface.OnClickListener() {
              public void onClick(@NonNull final DialogInterface dialog, final int id) {
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
              }
            }).setNegativeButton(R.string.gps_dialog_disagree_enable, new DialogInterface.OnClickListener() {
      public void onClick(@NonNull final DialogInterface dialog, final int id) {
        dialog.cancel();
      }
    });
    final android.support.v7.app.AlertDialog alert = builder.create();
    alert.show();
  }

  private static void showTutorialDialog(@NonNull final Context context) {
    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
    builder.setIcon(R.drawable.how_to_use);
    builder.setTitle(context.getString(R.string.tutorialTitle));
    builder.setMessage(R.string.tutorialInfo).setCancelable(true)
            .setPositiveButton(R.string.tutorialThanks, new DialogInterface.OnClickListener() {
              public void onClick(@NonNull final DialogInterface dialog, final int id) {
                dialog.cancel();
              }
            });
    final android.support.v7.app.AlertDialog alert = builder.create();
    alert.show();
  }

  private static void rateApp(@NonNull final Context context) {
    try {
      context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
    } catch (android.content.ActivityNotFoundException anfe) {
      viewInBrowser(context, "https://play.google.com/store/apps/details?id=" + context.getPackageName());
    }
  }

  private static void viewInBrowser(@NonNull final Context context, @NonNull final String url) {
    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    if (intent.resolveActivity(context.getPackageManager()) != null) {
      context.startActivity(intent);
    }
  }

  /**
   * Test method which generating a random {@link Float} values.
   *
   * @return {@link Float} random value
   */
  public static float generateRandomSpeed() {
    float speed = 0 + testingTempVal[0];
    if (speed != 0) {           //speed increment by 5 each tick
      testingTempVal[0] += 5;
    }
    if (speed > 70) {
      speed = random.nextInt(200);
    }
    speed = CalculationUtils.getSpeedInKilometerPerHour(speed);
    return speed;
  }
}