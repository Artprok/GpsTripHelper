package com.example.aprokopenko.triphelper.utils.util_methods;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.view.View;
import android.util.Log;
import android.Manifest;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.R;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import butterknife.ButterKnife;

public class UtilMethods {

    public static void checkPermission(Context context) {
        if (context != null) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }

    public static void replaceFragment(Fragment fragment, String fragment_tag, android.support.v4.app.FragmentActivity fragmentActivity) {
        FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, fragment_tag).addToBackStack(fragment.getTag()).commit();

    }

    public static String parseDate(Date dateString) {
        SimpleDateFormat sdf = ConstantValues.DATE_FORMAT;
        return sdf.format(dateString);
    }

    public static String formatFloat(float speed) {
        DecimalFormat df;
        if (speed > 9) {
            df = new DecimalFormat("##.#");
        }
        else if (speed > 99) {
            df = new DecimalFormat("###.#");
        }
        else if (speed > 999) {
            df = new DecimalFormat("####.#");
        }
        else if (speed > 9999) {
            df = new DecimalFormat("#####.#");
        }
        else {
            df = new DecimalFormat("#.#");
        }
        return df.format(speed);
    }

    public static void animateTextView(int initialValue, int finalValue, final TextView textview) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (textview != null) {
                    textview.setText(valueAnimator.getAnimatedValue().toString());
                }
            }
        });
        valueAnimator.start();
    }

    public static void setFabVisible(Activity activity) {
        FloatingActionButton fab = ButterKnife.findById(activity, R.id.fab);
        fab.setVisibility(View.VISIBLE);
    }

    public static void setFabInvisible(Activity activity) {
        FloatingActionButton fab = ButterKnife.findById(activity, R.id.fab);
        fab.setVisibility(View.INVISIBLE);
    }

    private static void buildAlertMessageNoGps(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static void checkIfGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String          LOG_TAG         = "GPS_CHECK";

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ConstantValues.DEBUG_MODE) {
                Log.i(LOG_TAG, "checkIfGpsEnabled: NotEnabled");
            }
            buildAlertMessageNoGps(context);
        }
        else {
            if (ConstantValues.DEBUG_MODE) {
                Log.i(LOG_TAG, "checkIfGpsEnabled: Gps Enabled");
            }
        }
    }
}
