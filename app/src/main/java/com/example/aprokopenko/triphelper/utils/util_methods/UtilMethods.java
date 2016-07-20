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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import butterknife.ButterKnife;

public class UtilMethods {
    private static final String  LOG_TAG        = "UtilMethods";
    private static final float[] testingTempVal = {1};
    private static final Random  random         = new Random();

    public static float getFuelConsumptionLevel(float avgSpeed, float fuelCons) {
        if (avgSpeed >= ConstantValues.HIGHWAY_SPEED_AVG_SPEED) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "getFuelConsumptionLevel: HighwayLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
            }
            return fuelCons + ConstantValues.CONSUMPTION_VERY_LOW_ADD;
        }
        else if (avgSpeed <= ConstantValues.HIGHWAY_SPEED_AVG_SPEED && avgSpeed > ConstantValues.LOW_TRAFFIC_AVG_SPEED) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "getFuelConsumptionLevel: LowLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
            }
            return fuelCons + ConstantValues.CONSUMPTION_LOW_TRAFFIC_ADD;
        }
        else if (avgSpeed <= ConstantValues.LOW_TRAFFIC_AVG_SPEED && avgSpeed > ConstantValues.MEDIUM_TRAFFIC_AVG_SPEED) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "getFuelConsumptionLevel: NormalLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
            }
            return fuelCons + ConstantValues.CONSUMPTION_NORMAL_TRAFFIC_ADD;
        }
        else if (avgSpeed <= ConstantValues.MEDIUM_TRAFFIC_AVG_SPEED && avgSpeed > ConstantValues.HIGH_TRAFFIC_AVG_SPEED) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "getFuelConsumptionLevel: MediumLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
            }
            return fuelCons + ConstantValues.CONSUMPTION_MEDIUM_TRAFFIC_ADD;
        }
        else if (avgSpeed <= ConstantValues.HIGH_TRAFFIC_AVG_SPEED && avgSpeed > ConstantValues.VERY_HIGH_TRAFFIC_AVG_SPEED) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "getFuelConsumptionLevel: HighLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
            }
            return fuelCons + ConstantValues.CONSUMPTION_HIGH_TRAFFIC_ADD;
        }
        else if (avgSpeed <= ConstantValues.VERY_HIGH_TRAFFIC_AVG_SPEED) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "getFuelConsumptionLevel: VeryHighLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
            }
            return fuelCons + ConstantValues.CONSUMPTION_VERY_HIGH_TRAFFIC_ADD;
        }
        else {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "getFuelConsumptionLevel: Impossible thing! Number out of all interval!! impossible!!! fuel cons - " +
                        fuelCons + "average speed - " + avgSpeed);
            }
            return fuelCons;
        }
    }

    public static String formatFloatDecimalFormat(Float floatToFormat) {
        if (floatToFormat != null) {
            DecimalFormat df = new DecimalFormat("######.#");
            return df.format(floatToFormat);
        }
        else {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.e(LOG_TAG, "formatFloatDecimalFormat: PROBLEM WITH FORMAT (floatToFormat is NULL), returned 0.00");
            }
            return "0.00";
        }
    }

    public static String formatFloatToIntFormat(Float floatToFormat) {
        if (floatToFormat != null) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "formatFloatToIntFormat inUtilMethod: " + floatToFormat);
            }
            DecimalFormat df = new DecimalFormat("######");
            return df.format(floatToFormat);
        }
        else {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.e(LOG_TAG, "formatFloatDecimalFormat: PROBLEM WITH FORMAT (floatToFormat is NULL), returned 0.00");
            }
            return "0.00";
        }
    }

    public static boolean eraseFile(Context context) {
        context.deleteFile(ConstantValues.INTERNAL_SETTING_FILE_NAME);
        return context.deleteFile(ConstantValues.FILE_NAME);
    }

    public static String parseDate(Date dateString) {
        SimpleDateFormat sdf = ConstantValues.DATE_FORMAT;
        return sdf.format(dateString);
    }

    public static void replaceFragment(Fragment fragment, String fragment_tag, android.support.v4.app.FragmentActivity fragmentActivity) {
        int orientation = fragmentActivity.getResources()
                .getConfiguration().orientation; // choice of animations: 1 - portrait, or 2 - landscape
        int animEnter;
        int animExit;
        int animPopEnter;
        int animPopExit;

        if (orientation == 1) {
            animEnter = R.anim.fragment_enter_vertical;
            animExit = R.anim.fragment_exit_vertical;
            animPopEnter = R.anim.pop_enter_vertical;
            animPopExit = R.anim.pop_exit_vertical;
        }
        else {
            animEnter = R.anim.fragment_enter_horizontal;
            animExit = R.anim.fragment_exit_horizontal;
            animPopEnter = R.anim.pop_enter_horizontal;
            animPopExit = R.anim.pop_exit_horizontal;
        }

        FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(animEnter, animExit, animPopEnter, animPopExit);
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, fragment_tag).addToBackStack(fragment.getTag())
                .commitAllowingStateLoss();
    }

    public static void animateTextView(int initialValue, int finalValue, final TextView textview) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(ConstantValues.SPEEDOMETER_TEXT_ANIM_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (textview != null) {
                    textview.setText(valueAnimator.getAnimatedValue().toString());
                }
            }
        });
        valueAnimator.start();
    }

    public static void animateFabTransition(final FloatingActionButton fab, final int transitionX) {
        fab.animate().setListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) {

            }

            @Override public void onAnimationEnd(Animator animator) {
                fab.setTranslationX(transitionX);
            }

            @Override public void onAnimationCancel(Animator animator) {

            }

            @Override public void onAnimationRepeat(Animator animator) {

            }
        }).setDuration(ConstantValues.TEXT_ANIM_DURATION).translationX(transitionX).start();
    }

    public static void showToast(Context context, CharSequence stringToShow) {
        Toast toast = Toast.makeText(context, stringToShow, Toast.LENGTH_SHORT);
        toast.show();

    }

    public static void firstStartTutorialDialog(final Context context) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setIcon(R.drawable.how_to_use);
        builder.setTitle(context.getString(R.string.tutorialTitle));
        builder.setMessage(R.string.tutorialWantToKnowInfo).setCancelable(true)
                .setPositiveButton(R.string.tutorialNext, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        showTutorialDialog(context);
                    }
                }).setNegativeButton(R.string.tutorialNo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();
            }
        });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public static void MapNotAvailableDialog(final Context context) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setIcon(R.drawable.something_went_wrong);
        builder.setTitle(context.getString(R.string.somethingWentWrongTitle));
        builder.setMessage(R.string.somethingWentWrongMessage).setCancelable(true)
                .setPositiveButton(R.string.somethingWentWrongOk, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public static void buildAndShowAboutDialog(final Context context) {
        final Resources                                  res     = context.getResources();
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setIcon(R.drawable.btn_about);
        builder.setTitle(context.getString(R.string.aboutTitle));
        builder.setMessage(R.string.aboutMainText).setCancelable(true)
                .setPositiveButton(R.string.aboutRateButton, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        rateApp(context);
                    }
                }).setNegativeButton(R.string.aboutNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();
            }
        }).setNeutralButton(R.string.aboutFeedbackButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                Intent emailFeedback = new Intent(Intent.ACTION_SEND);
                emailFeedback.setType("text/email");
                emailFeedback.putExtra(Intent.EXTRA_EMAIL, new String[]{res.getString(R.string.emailForFeedback)});
                emailFeedback.putExtra(Intent.EXTRA_SUBJECT, res.getString(R.string.subjectForFeedback));
                emailFeedback.putExtra(Intent.EXTRA_TEXT, res.getString(R.string.extraTextInFeedback) + "");
                try {
                    context.startActivity(Intent.createChooser(emailFeedback, res.getString(R.string.sendWithFeedback)));
                }
                catch (android.content.ActivityNotFoundException exception) {
                    Log.e(LOG_TAG, "onClick: Nothing to send email :(");
                    UtilMethods.showToast(context, "No email clients installed on device!");
                }
            }
        });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public static boolean checkIfGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void checkIfGpsEnabledAndShowDialogs(Context context) {
        boolean isGpsEnabled = checkIfGpsEnabled(context);
        if (!isGpsEnabled) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.i(LOG_TAG, "checkIfGpsEnabledAndShowDialogs: NotEnabled");
            }
            buildAlertMessageNoGps(context);
        }
        else {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.i(LOG_TAG, "checkIfGpsEnabledAndShowDialogs: Gps Enabled");
            }
        }
    }

    public static void setFabInvisible(Activity activity) {
        FloatingActionButton fab = ButterKnife.findById(activity, R.id.btn_fab);
        fab.setVisibility(View.INVISIBLE);
    }

    public static void setFabVisible(Activity activity) {
        FloatingActionButton fab = ButterKnife.findById(activity, R.id.btn_fab);
        fab.setVisibility(View.VISIBLE);
    }

    public static boolean isPermissionAllowed(Context context) {
        boolean result = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        else {
            if (context != null) {
                result = ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat
                        .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            }
        }
        return result;
    }

    private static void buildAlertMessageNoGps(final Context context) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setIcon(R.drawable.gps_icon);
        builder.setTitle(context.getString(R.string.gps_dialog_title));
        builder.setMessage(R.string.gps_dialog_text_label).setCancelable(false)
                .setPositiveButton(R.string.gps_dialog_agree_enable, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton(R.string.gps_dialog_disagree_enable, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();
            }
        });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private static void showTutorialDialog(Context context) {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setIcon(R.drawable.how_to_use);
        builder.setTitle(context.getString(R.string.tutorialTitle));
        builder.setMessage(R.string.tutorialInfo).setCancelable(true)
                .setPositiveButton(R.string.tutorialThanks, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private static void rateApp(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
        }
        catch (android.content.ActivityNotFoundException anfe) {
            viewInBrowser(context, "https://play.google.com/store/apps/details?id=" + context.getPackageName());
        }
    }

    private static void viewInBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (null != intent.resolveActivity(context.getPackageManager())) {
            context.startActivity(intent);
        }
    }

    public static float generateRandomSpeed() {
        float speed;
        speed = 0 + testingTempVal[0];
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
