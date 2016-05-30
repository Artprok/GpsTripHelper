package com.example.aprokopenko.triphelper.ui.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.text.TextWatcher;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.view.ViewGroup;
import android.text.Editable;
import android.os.AsyncTask;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.example.aprokopenko.triphelper.listener.FileEraseListener;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.R;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;


import javax.inject.Singleton;

import butterknife.Bind;
import butterknife.ButterKnife;


@Singleton public class SettingsFragment extends Fragment {

    @Bind(R.id.fuelPriceEditText)
    EditText    fuelPriceEditText;
    @Bind(R.id.fuelConsumptionEditText)
    EditText    fuelConsEditText;
    @Bind(R.id.fuelCapacityEditText)
    EditText    fuelCapacityEditText;
    @Bind(R.id.curFuelCapacity)
    TextView    curFuelCapacity;
    @Bind(R.id.curFuelCons)
    TextView    curFuelCons;
    @Bind(R.id.curFuelPrice)
    TextView    curFuelPrice;
    @Bind(R.id.eraseButton)
    ImageButton eraseButton;

    public static final String LOG_TAG          = "Settings fragment";
    private             int    fuelTankCapacity = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT;
    private             float  fuelConsumption  = ConstantValues.FUEL_CONSUMPTION_DEFAULT;
    private             float  fuelCost         = ConstantValues.FUEL_COST_DEFAULT;
    private Context           context;
    private FileEraseListener fileEraseListener;

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
        ButterKnife.bind(this, view);
        context = getActivity();
        setupEraseButton();
        setupEditTextFields();
        readDataFromFile();
    }

    public void setFileEraseListener(FileEraseListener fileEraseListener) {
        this.fileEraseListener = fileEraseListener;
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

    private void updateTextFields() {
        String fuelCons = fuelConsumption + getString(R.string.fuel_cons_prefix);
        curFuelCons.setText(fuelCons);

        String fuelCost = this.fuelCost + getString(R.string.currency_prefix);
        curFuelPrice.setText(fuelCost);

        String fuelCapacity = fuelTankCapacity + getString(R.string.fuel_prefix);
        curFuelCapacity.setText(fuelCapacity);

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
}
