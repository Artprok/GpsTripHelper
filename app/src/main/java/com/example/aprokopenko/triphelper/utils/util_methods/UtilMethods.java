package com.example.aprokopenko.triphelper.utils.util_methods;


import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;
import android.app.Activity;
import android.view.View;
import android.util.Log;
import android.Manifest;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.R;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import butterknife.ButterKnife;

public class UtilMethods {
    public static final String LOG_TAG = "UtilMethods";

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
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, fragment_tag).addToBackStack(fragment.getTag()).commit();

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

    public static float getFuelConsumptionLevel(float avgSpeed, float fuelCons) {
        if (avgSpeed >= ConstantValues.HIGHWAY_SPEED_AVG_SPEED) {
            Log.d(LOG_TAG, "getFuelConsumptionLevel: HighwayLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
            return fuelCons + ConstantValues.CONSUMPTION_HIGHWAY_TRAFFIC_ADD;
        }
        else if (avgSpeed <= ConstantValues.HIGHWAY_SPEED_AVG_SPEED && avgSpeed > ConstantValues.LOW_TRAFFIC_AVG_SPEED) {
            Log.d(LOG_TAG, "getFuelConsumptionLevel: LowLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
            return fuelCons + ConstantValues.CONSUMPTION_LOW_TRAFFIC_ADD;
        }
        else if (avgSpeed <= ConstantValues.LOW_TRAFFIC_AVG_SPEED && avgSpeed > ConstantValues.MEDIUM_TRAFFIC_AVG_SPEED) {
            Log.d(LOG_TAG, "getFuelConsumptionLevel: MediumLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
            return fuelCons + ConstantValues.CONSUMPTION_MEDIUM_TRAFFIC_ADD;
        }
        else if (avgSpeed <= ConstantValues.MEDIUM_TRAFFIC_AVG_SPEED && avgSpeed > ConstantValues.HIGH_TRAFFIC_AVG_SPEED) {
            Log.d(LOG_TAG, "getFuelConsumptionLevel: HighLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
            return fuelCons + ConstantValues.CONSUMPTION_HIGH_TRAFFIC_ADD;
        }
        else if (avgSpeed <= ConstantValues.VERY_HIGH_TRAFFIC_AVG_SPEED) {
            Log.d(LOG_TAG, "getFuelConsumptionLevel: VeryHighLevel avgSpd: " + avgSpeed + "FuelCons: " + fuelCons);
            return fuelCons + ConstantValues.CONSUMPTION_VERY_HIGH_TRAFFIC_ADD;
        }
        else {
            Log.d(LOG_TAG, "getFuelConsumptionLevel: Impossible thing! Number out of all interval!! impossible!!!" + fuelCons + avgSpeed);
            return fuelCons;
        }

    }

    public static void showToast(Context context, CharSequence stringToShow) {
        Toast toast = Toast.makeText(context, stringToShow, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static String formatFloatDecimalFormt(Float floatToFormat) {
        if (floatToFormat != null) {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);
            if (floatToFormat > 9.9) {
                df = new DecimalFormat("##.#");
            }
            else if (floatToFormat > 99.9) {
                df = new DecimalFormat("###.#");
            }
            else if (floatToFormat > 999.9) {
                df = new DecimalFormat("####.#");
            }
            else if (floatToFormat > 9999.9) {
                df = new DecimalFormat("#####.#");
            }
            return df.format(floatToFormat);
        }
        else {
            if (ConstantValues.DEBUG_MODE) {
                return "Null in format float!";
            }
            return "0.00";
        }
    }

    public static String formatFloatToIntFormat(Float floatToFormat) {
        if (floatToFormat != null) {
            Log.d(LOG_TAG, "formatFloatToIntFormat inUtilMethod: " + floatToFormat);
            DecimalFormat df = new DecimalFormat("#");
            df.setRoundingMode(RoundingMode.FLOOR);
            if (floatToFormat > 9.9) {
                df = new DecimalFormat("##");
                Log.d(LOG_TAG, "formatFloatToIntFormat: case ##/#" + df.format(floatToFormat));
            }
            else if (floatToFormat > 99.9) {
                df = new DecimalFormat("###");
            }
            else if (floatToFormat > 999.9) {
                df = new DecimalFormat("####");
            }
            else if (floatToFormat > 9999.9) {
                df = new DecimalFormat("#####");
            }
            return df.format(floatToFormat);
        }
        else {
            if (ConstantValues.DEBUG_MODE) {
                return "Null in format float!";
            }
            return "0.00";
        }
    }

    public static void checkIfGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


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

    public static void setFabInvisible(Activity activity) {
        FloatingActionButton fab = ButterKnife.findById(activity, R.id.fab);
        fab.setVisibility(View.INVISIBLE);
    }

    public static void setFabVisible(Activity activity) {
        FloatingActionButton fab = ButterKnife.findById(activity, R.id.fab);
        fab.setVisibility(View.VISIBLE);
    }

    public static void checkPermission(Context context) {
        if (context != null) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
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


    private static void buildAlertMessageNoGps(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.gps_not_enabled_label).setCancelable(false)
                .setPositiveButton(R.string.agree_gps_enable, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton(R.string.disagree_gps_enable, new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
