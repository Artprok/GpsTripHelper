package com.example.aprokopenko.triphelper.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.listener.FileEraseListener;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@Singleton public class SettingsFragment extends Fragment {

    @BindView(R.id.fuelPriceEditText)
    EditText    fuelPriceEditText;
    @BindView(R.id.fuelConsumptionEditText)
    EditText    fuelConsEditText;
    @BindView(R.id.fuelCapacityEditText)
    EditText    fuelCapacityEditText;
    @BindView(R.id.curFuelCapacity)
    TextView    curFuelCapacity;
    @BindView(R.id.curFuelCons)
    TextView    curFuelCons;
    @BindView(R.id.curFuelPrice)
    TextView    curFuelPrice;
    @BindView(R.id.eraseButton)
    ImageButton eraseButton;
    @BindView(R.id.aboutButton)
    ImageButton aboutButton;
    @BindView(R.id.measurementUnitSpinner)
    Spinner     measurementUnitSpinner;
    @BindView(R.id.backgroundSwitch)
    Switch      backgroundSwitch;

    private static final String LOG_TAG = "Settings fragment";

    private int   fuelTankCapacity = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT;
    private float fuelConsumption  = ConstantValues.FUEL_CONSUMPTION_DEFAULT;
    private float fuelCost         = ConstantValues.FUEL_COST_DEFAULT;
    private FileEraseListener fileEraseListener;
    private SharedPreferences preferences;
    private Context           context;
    private Unbinder          unbinder;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        preferences = TripHelperApp.getSharedPreferences();
        context = getActivity();
        setupEraseButton();
        setupAboutButton();
        setupEditTextFields();
        readDataFromFile();
        setupSpinner();

        // TODO: 07.06.2016 not working due to problems with WakeLock that calling in OnLocationChanged,whatever you do..
        //        setupBackgroundSwitch();
    }

    @Override public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }


    public void setFileEraseListener(FileEraseListener fileEraseListener) {
        this.fileEraseListener = fileEraseListener;
    }

    private void setMeasurementUnit(int position, String kmh, String mph, String knots) {
        SharedPreferences.Editor editor = preferences.edit();
        switch (position) {
            case 0:
                editor.putString("measurementUnit", kmh);
                editor.putInt("measurementUnitPosition", position);
                editor.apply();
                break;
            case 1:
                editor.putString("measurementUnit", mph);
                editor.putInt("measurementUnitPosition", position);
                editor.apply();
                break;
            case 2:
                editor.putString("measurementUnit", knots);
                editor.putInt("measurementUnitPosition", position);
                editor.apply();
                break;
        }
    }

    private void setupEditTextFields() {
        fuelPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "onTextChanged: + text is - " + s);
                }
                if (!TextUtils.equals(s, "")) {
                    fuelCost = Float.valueOf(s.toString());
                    writeDataToFile();
                }
            }

            @Override public void afterTextChanged(Editable s) {

            }
        });

        fuelConsEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.equals(s, "")) {
                    fuelConsumption = (Float.valueOf(s.toString()));
                    writeDataToFile();
                }
            }

            @Override public void afterTextChanged(Editable s) {

            }
        });

        fuelCapacityEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.equals(s, "")) {
                    fuelTankCapacity = (Integer.valueOf(s.toString()));
                    writeDataToFile();
                }
            }

            @Override public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupAboutButton() {
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                UtilMethods.buildAndShowAboutDialog(context);
            }
        });
    }

    private void setupEraseButton() {
        eraseButton.setVisibility(View.VISIBLE);
        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (UtilMethods.eraseFile(context)) {
                    fileEraseListener.onFileErased();
                    readDataFromFile();
                    UtilMethods.showToast(context, context.getString(R.string.file_erased_toast));
                }
                else {
                    UtilMethods.showToast(context, context.getString(R.string.file_not_erased_toast));
                }
            }
        });
    }

    private void setupTextFields() {
        if (fuelConsumption == ConstantValues.FUEL_CONSUMPTION_DEFAULT) {
            String fuelCons = ConstantValues.FUEL_CONSUMPTION_DEFAULT + getString(R.string.fuel_cons_prefix);
            curFuelCons.setText(fuelCons);
        }
        else {
            String fuelCons = fuelConsumption + getString(R.string.fuel_cons_prefix);
            curFuelCons.setText(fuelCons);
        }

        if (fuelCost == ConstantValues.FUEL_COST_DEFAULT) {
            String fuelCost = ConstantValues.FUEL_COST_DEFAULT + getString(R.string.currency_prefix);
            curFuelPrice.setText(fuelCost);
        }
        else {
            String fuelCost = this.fuelCost + getString(R.string.currency_prefix);
            curFuelPrice.setText(fuelCost);
        }

        if (fuelTankCapacity == ConstantValues.FUEL_TANK_CAPACITY_DEFAULT) {
            String fuelCap = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT + getString(R.string.fuel_prefix);
            curFuelCapacity.setText(fuelCap);
        }
        else {
            String fuelCapacity = fuelTankCapacity + getString(R.string.fuel_prefix);
            curFuelCapacity.setText(fuelCapacity);
        }
    }

    private void setupSpinner() {
        final String kmh   = getString(R.string.kilometreUnit);
        final String mph   = getString(R.string.milesUnit);
        final String knots = getString(R.string.knots);
        String       title = getString(R.string.measurementUnitSpinnerTitle);

        String[]             data    = {kmh, mph, knots};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        measurementUnitSpinner.setAdapter(adapter);
        measurementUnitSpinner.setPrompt(title);

        measurementUnitSpinner.setSelection(preferences.getInt("measurementUnitPosition", 0));
        measurementUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setMeasurementUnit(position, kmh, mph, knots);
            }

            @Override public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void updateTextFields() {
        String fuelCons = fuelConsumption + getString(R.string.fuel_cons_prefix);
        curFuelCons.setText(fuelCons);

        String fuelCost = this.fuelCost + getString(R.string.currency_prefix);
        curFuelPrice.setText(fuelCost);

        String fuelCapacity = fuelTankCapacity + getString(R.string.fuel_prefix);
        curFuelCapacity.setText(fuelCapacity);

    }

    private void readDataFromFile() {
        ReadInternalFile readFileTask = new ReadInternalFile();
        readFileTask.execute();
    }

    private void writeDataToFile() {
        WriteInternalFile writeFileTask = new WriteInternalFile();
        writeFileTask.execute();
    }


    private class WriteInternalFile extends AsyncTask<Void, Void, Boolean> {
        @Override protected Boolean doInBackground(Void... params) {

            FileOutputStream fos;

            float consumption = fuelConsumption;
            float price       = fuelCost;
            int   capacity    = fuelTankCapacity;

            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "writeTripDataToFileSetting: writeCalled");
                Log.d(LOG_TAG, "writeTripDataToFileSetting: writeCalled" + consumption + price + capacity);
            }

            try {
                fos = context.openFileOutput(ConstantValues.INTERNAL_SETTING_FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeFloat(consumption);
                os.writeFloat(price);
                os.writeInt(capacity);

                os.close();
                fos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override protected void onPostExecute(Boolean result) {
            if (result) {
                updateTextFields();
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "file written successfully");
                }
            }
        }
    }

    private class ReadInternalFile extends AsyncTask<String, Void, Boolean> {
        @Override protected Boolean doInBackground(String... params) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "readFileSettings");
            }
            File file = context.getFileStreamPath(ConstantValues.INTERNAL_SETTING_FILE_NAME);
            if (file.exists()) {
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "readTripDataFromFileSettings: ");
                }
                FileInputStream fis;
                try {
                    fis = context.openFileInput(ConstantValues.INTERNAL_SETTING_FILE_NAME);
                    ObjectInputStream is          = new ObjectInputStream(fis);
                    float             consumption = is.readFloat();
                    float             fuelPrice   = is.readFloat();
                    int               capacity    = is.readInt();

                    fuelConsumption = consumption;
                    fuelCost = fuelPrice;
                    fuelTankCapacity = capacity;

                    is.close();
                    fis.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                fuelConsumption = ConstantValues.FUEL_CONSUMPTION_DEFAULT;
                fuelCost = ConstantValues.FUEL_COST_DEFAULT;
                fuelTankCapacity = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT;
                return true;
            }
            return true;
        }

        @Override protected void onPostExecute(Boolean result) {
            setupTextFields();
            super.onPostExecute(result);
        }
    }

    // TODO: 07.06.2016 not working due to problems with WakeLock that calling in OnLocationChanged,whatever you do..
    //    private void setupBackgroundSwitch() {
    //        boolean res = preferences.getBoolean("backgroundWork", false);
    //        backgroundSwitch.setChecked(res);
    //        backgroundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    //            SharedPreferences.Editor editor = preferences.edit();
    //
    //            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
    //                if (b) {
    //                    editor.putBoolean("backgroundWork", true);
    //                }
    //                else {
    //                    editor.putBoolean("backgroundWork", false);
    //                }
    //                editor.apply();
    //            }
    //        });
    //    }
}
