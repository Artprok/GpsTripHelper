package com.example.aprokopenko.triphelper.ui.setting_screen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.application.TripHelperApp;

import com.example.aprokopenko.triphelper.ui.main_screen.MainContract;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import butterknife.Unbinder;

public class SettingsPresenter implements SettingsContract.UserActionListener {

    private static final String LOG_TAG = "Settings fragment";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String MEASUREMENT_UNIT = "measurementUnit";
    private static final String MEASUREMENT_UNIT_POSITION = "measurementUnitPosition";
    private static final String CURRENCY_UNIT = "currencyUnit";
    private static final String CURRENCY_UNIT_POSITION = "currencyUnitPosition";

    private int fuelTankCapacity = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT;
    private float fuelConsumption = ConstantValues.FUEL_CONSUMPTION_DEFAULT;
    private float fuelCost = ConstantValues.FUEL_COST_DEFAULT;
    private MainContract.UserActionListener fileEraseListener;
    private SharedPreferences preferences;
    private Unbinder unbinder;
    private String currency_prefix;
    private SettingsFragment settingsFragment;
    private SettingsContract.View view;

    public SettingsPresenter(SettingsFragment settingsFragment, SharedPreferences preferences, MainContract.UserActionListener fileEraseListener) {
        this.settingsFragment = settingsFragment;
        this.preferences = preferences;
        this.fileEraseListener = fileEraseListener;
        settingsFragment.setPresenter(this);
    }

    public void start() {
        readDataFromFile();
    }

    private void readDataFromFile() {
        final ReadInternalFile readFileTask = new ReadInternalFile();
        readFileTask.execute();
    }

    public void setCurrencyUnit(final int position) {
        final SharedPreferences.Editor editor = preferences.edit();
        switch (position) {
            case 0:
                editor.putString(CURRENCY_UNIT, settingsFragment.getString(R.string.grn));
                editor.putInt(CURRENCY_UNIT_POSITION, position);
                editor.apply();
                break;
            case 1:
                editor.putString(CURRENCY_UNIT, settingsFragment.getString(R.string.rub));
                editor.putInt(CURRENCY_UNIT_POSITION, position);
                editor.apply();
                break;
            case 2:
                editor.putString(CURRENCY_UNIT, settingsFragment.getString(R.string.usd));
                editor.putInt(CURRENCY_UNIT_POSITION, position);
                editor.apply();
                break;
            case 3:
                editor.putString(CURRENCY_UNIT, settingsFragment.getString(R.string.eur));
                editor.putInt(CURRENCY_UNIT_POSITION, position);
                editor.apply();
                break;
            default:
                editor.putString(CURRENCY_UNIT, settingsFragment.getString(R.string.usd));
                editor.putInt(CURRENCY_UNIT_POSITION, position);
                editor.apply();
        }
    }

    @Override
    public void aboutButtonCliked() {
        UtilMethods.buildAndShowAboutDialog(settingsFragment.getContext());
    }

    @Override
    public void onFuelPriceChanged(CharSequence s) {
        if (!TextUtils.equals(s, "")) {
            fuelCost = Float.valueOf(s.toString());
            writeDataToFile();
        }

        if (DEBUG) {
            Log.d(LOG_TAG, "onTextChanged: + text is - " + s);
        }
    }

    @Override
    public void onFuelConsumptionChanged(CharSequence s) {
        if (!TextUtils.equals(s, "")) {
            fuelConsumption = (Float.valueOf(s.toString()));
            writeDataToFile();
        }
    }

    @Override
    public void onFuelCapacityChanged(CharSequence s) {
        if (!TextUtils.isEmpty(s)) {
            fuelTankCapacity = (Integer.valueOf(s.toString()));
            writeDataToFile();
        }
    }

    @Override
    public void eraseButtonCliked() {
        if (UtilMethods.eraseFile(settingsFragment.getContext())) {
            fileEraseListener.onFileErased();
            readDataFromFile();
            UtilMethods.showToast(settingsFragment.getContext(), settingsFragment.getString(R.string.file_erased_toast));
        } else {
            UtilMethods.showToast(settingsFragment.getContext(), settingsFragment.getString(R.string.file_not_erased_toast));
        }
    }

    public void setMeasurementUnit(final int position) {
        final SharedPreferences.Editor editor = preferences.edit();
        switch (position) {
            case 0://Kilometer per hour case
                editor.putString(MEASUREMENT_UNIT, settingsFragment.getString(R.string.kilometreUnit));
                editor.putInt(MEASUREMENT_UNIT_POSITION, position);
                editor.apply();
                break;
            case 1://miles per hour case
                editor.putString(MEASUREMENT_UNIT, settingsFragment.getString(R.string.milesUnit));
                editor.putInt(MEASUREMENT_UNIT_POSITION, position);
                editor.apply();
                break;
            case 2://knots per hour case
                editor.putString(MEASUREMENT_UNIT, settingsFragment.getString(R.string.knots));
                editor.putInt(MEASUREMENT_UNIT_POSITION, position);
                editor.apply();
                break;
            default:
                editor.putString(MEASUREMENT_UNIT, settingsFragment.getString(R.string.kilometreUnit));
                editor.putInt(MEASUREMENT_UNIT_POSITION, position);
                editor.apply();
        }
    }

    private void writeDataToFile() {
        final WriteInternalFile writeFileTask = new WriteInternalFile();
        writeFileTask.execute();
    }

    private class WriteInternalFile extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(@NonNull final Void... params) {
            try {
                final FileOutputStream fos = settingsFragment.getContext().openFileOutput(ConstantValues.INTERNAL_SETTING_FILE_NAME, Context.MODE_PRIVATE);
                final ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeFloat(fuelConsumption);
                os.writeFloat(fuelCost);
                os.writeInt(fuelTankCapacity);
                os.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (DEBUG) {
                Log.d(LOG_TAG, "writeTripDataToFileSetting: writeCalled");
                Log.d(LOG_TAG, "writeTripDataToFileSetting: writeCalled" + fuelConsumption + fuelCost + fuelTankCapacity);
            }
            return true;
        }

        @Override
        protected void onPostExecute(@NonNull final Boolean result) {
            if (result) {
                view.updateTextFields(fuelConsumption, fuelCost, fuelTankCapacity);
            }
        }
    }

    private class ReadInternalFile extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(@Nullable final String... params) {
            if (DEBUG) {
                Log.d(LOG_TAG, "readFileSettings");
            }

            if (settingsFragment.getContext().getFileStreamPath(ConstantValues.INTERNAL_SETTING_FILE_NAME).exists()) {
                if (DEBUG) {
                    Log.d(LOG_TAG, "readTripDataFromFileSettings: ");
                }
                try {
                    final FileInputStream fis = settingsFragment.getContext().openFileInput(ConstantValues.INTERNAL_SETTING_FILE_NAME);
                    final ObjectInputStream is = new ObjectInputStream(fis);

                    fuelConsumption = is.readFloat();
                    fuelCost = is.readFloat();
                    fuelTankCapacity = is.readInt();

                    is.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                fuelConsumption = ConstantValues.FUEL_CONSUMPTION_DEFAULT;
                fuelCost = ConstantValues.FUEL_COST_DEFAULT;
                fuelTankCapacity = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT;
                return true;
            }
            return true;
        }



        @Override
        protected void onPostExecute(@NonNull final Boolean result) {
            view.setupTextFields(fuelConsumption, fuelCost, fuelTankCapacity);
            super.onPostExecute(result);
        }
    }
}
