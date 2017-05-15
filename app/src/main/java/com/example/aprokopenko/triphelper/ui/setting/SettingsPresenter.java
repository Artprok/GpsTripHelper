package com.example.aprokopenko.triphelper.ui.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

    private SharedPreferences preferences;
    private SettingsContract.View view;
    private Context context;

    public SettingsPresenter(SettingsContract.View view, SharedPreferences preferences, Context context) {
        this.view = view;
        this.context = context;
        this.preferences = preferences;
        view.setPresenter(this);
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
                editor.putString(CURRENCY_UNIT, context.getString(R.string.grn));
                editor.putInt(CURRENCY_UNIT_POSITION, position);
                editor.apply();
                break;
            case 1:
                editor.putString(CURRENCY_UNIT, context.getString(R.string.rub));
                editor.putInt(CURRENCY_UNIT_POSITION, position);
                editor.apply();
                break;
            case 2:
                editor.putString(CURRENCY_UNIT, context.getString(R.string.usd));
                editor.putInt(CURRENCY_UNIT_POSITION, position);
                editor.apply();
                break;
            case 3:
                editor.putString(CURRENCY_UNIT, context.getString(R.string.eur));
                editor.putInt(CURRENCY_UNIT_POSITION, position);
                editor.apply();
                break;
            default:
                editor.putString(CURRENCY_UNIT, context.getString(R.string.usd));
                editor.putInt(CURRENCY_UNIT_POSITION, position);
                editor.apply();
        }
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
    public void fileSuccessfullyErased() {
        readDataFromFile();
    }

    public void setMeasurementUnit(final int position) {
        final SharedPreferences.Editor editor = preferences.edit();
        switch (position) {
            case 0://Kilometer per hour case
                editor.putString(MEASUREMENT_UNIT, context.getString(R.string.kilometreUnit));
                editor.putInt(MEASUREMENT_UNIT_POSITION, position);
                editor.apply();
                break;
            case 1://miles per hour case
                editor.putString(MEASUREMENT_UNIT, context.getString(R.string.milesUnit));
                editor.putInt(MEASUREMENT_UNIT_POSITION, position);
                editor.apply();
                break;
            case 2://knots per hour case
                editor.putString(MEASUREMENT_UNIT, context.getString(R.string.knots));
                editor.putInt(MEASUREMENT_UNIT_POSITION, position);
                editor.apply();
                break;
            default:
                editor.putString(MEASUREMENT_UNIT, context.getString(R.string.kilometreUnit));
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
                final FileOutputStream fos = context.openFileOutput(ConstantValues.INTERNAL_SETTING_FILE_NAME, Context.MODE_PRIVATE);
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

            if (context.getFileStreamPath(ConstantValues.INTERNAL_SETTING_FILE_NAME).exists()) {
                if (DEBUG) {
                    Log.d(LOG_TAG, "readTripDataFromFileSettings: ");
                }
                try {
                    final FileInputStream fis = context.openFileInput(ConstantValues.INTERNAL_SETTING_FILE_NAME);
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
