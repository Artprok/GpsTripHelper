package com.example.aprokopenko.triphelper.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
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
import android.widget.TextView;

import com.example.aprokopenko.triphelper.BuildConfig;
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
import butterknife.OnClick;
import butterknife.Unbinder;

@Singleton
public class SettingsFragment extends android.support.v4.app.Fragment {

  @BindView(R.id.editText_fuelPrice)
  EditText fuelPriceEditText;
  @BindView(R.id.editText_fuelConsumption)
  EditText fuelConsEditText;
  @BindView(R.id.editText_fuelCapacity)
  EditText fuelCapacityEditText;
  @BindView(R.id.text_curFuelCapacity)
  TextView curFuelCapacity;
  @BindView(R.id.text_curFuelCons)
  TextView curFuelCons;
  @BindView(R.id.text_curFuelPrice)
  TextView curFuelPrice;
  @BindView(R.id.btn_erase)
  ImageButton eraseButton;
  @BindView(R.id.btn_about)
  ImageButton aboutButton;
  @BindView(R.id.measurementUnitSpinner)
  Spinner measurementUnitSpinner;
  @BindView(R.id.currencyUnitSpinner)
  Spinner currencyUnitSpinner;
  @BindView(R.id.backgroundSwitch)
  SwitchCompat backgroundSwitch;

  private static final String LOG_TAG = "Settings fragment";
  public static final boolean DEBUG = BuildConfig.DEBUG;

  private int fuelTankCapacity = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT;
  private float fuelConsumption = ConstantValues.FUEL_CONSUMPTION_DEFAULT;
  private float fuelCost = ConstantValues.FUEL_COST_DEFAULT;
  private FileEraseListener fileEraseListener;
  private SharedPreferences preferences;
  private Context context;
  private Unbinder unbinder;
  private String currency_prefix;

  public SettingsFragment() {
    // Required empty public constructor
  }

  public static SettingsFragment newInstance() {
    return new SettingsFragment();
  }

  @Override public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_settings, container, false);
  }

  @Override public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, view);
    preferences = TripHelperApp.getSharedPreferences();
    context = getActivity();
    setupEditTextFields();
    readDataFromFile();
    setupMeasurementUnitSpinner();
    setupCurrencyUnitSpinner();


    // TODO: 07.06.2016 not working due to problems with WakeLock that called in OnLocationChanged,whatever you do..
    //        setupBackgroundSwitch();
  }

  @Override public void onDestroyView() {
    unbinder.unbind();
    super.onDestroyView();
  }

  @OnClick({R.id.btn_about, R.id.btn_erase})
  public void onClickButton(@NonNull final View view) {
    final int idView = view.getId();
    switch (idView) {
      case R.id.btn_about:
        UtilMethods.buildAndShowAboutDialog(getActivity());
        break;
      case R.id.btn_erase:
        if (UtilMethods.eraseFile(context)) {
          fileEraseListener.onFileErased();
          readDataFromFile();
          UtilMethods.showToast(context, context.getString(R.string.file_erased_toast));
        } else {
          UtilMethods.showToast(context, context.getString(R.string.file_not_erased_toast));
        }
        break;
      default:
        throw new IllegalArgumentException();
    }

  }

  public void setFileEraseListener(@NonNull final FileEraseListener fileEraseListener) {
    this.fileEraseListener = fileEraseListener;
  }

  private void setMeasurementUnit(final int position, @NonNull final String kmh, @NonNull final String mph, @NonNull final String knots) {
    final SharedPreferences.Editor editor = preferences.edit();
    switch (position) {
      case 0://Kilometer per hour case
        editor.putString("measurementUnit", kmh);
        editor.putInt("measurementUnitPosition", position);
        editor.apply();
        break;
      case 1://miles per hour case
        editor.putString("measurementUnit", mph);
        editor.putInt("measurementUnitPosition", position);
        editor.apply();
        break;
      case 2://knots per hour case
        editor.putString("measurementUnit", knots);
        editor.putInt("measurementUnitPosition", position);
        editor.apply();
        break;
    }
  }

  private void setCurrencyUnit(final int position, @NonNull final String grn, @NonNull final String rub, @NonNull final String usd, @NonNull final String eur) {
    final SharedPreferences.Editor editor = preferences.edit();
    switch (position) {
      case 0:
        editor.putString("currencyUnit", grn);
        editor.putInt("currencyUnitPosition", position);
        editor.apply();
        break;
      case 1:
        editor.putString("currencyUnit", rub);
        editor.putInt("currencyUnitPosition", position);
        editor.apply();
        break;
      case 2:
        editor.putString("currencyUnit", usd);
        editor.putInt("currencyUnitPosition", position);
        editor.apply();
        break;
      case 3:
        editor.putString("currencyUnit", eur);
        editor.putInt("currencyUnitPosition", position);
        editor.apply();
        break;
    }
  }

  private void setupEditTextFields() {
    fuelPriceEditText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(@NonNull final CharSequence s, final int start, final int count, final int after) {

      }

      @Override public void onTextChanged(@NonNull final CharSequence s, final int start, final int before, final int count) {
        if (DEBUG) {
          Log.d(LOG_TAG, "onTextChanged: + text is - " + s);
        }
        if (!TextUtils.equals(s, "")) {
          fuelCost = Float.valueOf(s.toString());
          writeDataToFile();
        }
      }

      @Override public void afterTextChanged(@NonNull final Editable s) {

      }
    });

    fuelConsEditText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(@NonNull final CharSequence s, final int start, final int count, final int after) {

      }

      @Override public void onTextChanged(@NonNull final CharSequence s, final int start, final int before, final int count) {
        if (!TextUtils.equals(s, "")) {
          fuelConsumption = (Float.valueOf(s.toString()));
          writeDataToFile();
        }
      }

      @Override public void afterTextChanged(@NonNull final Editable s) {

      }
    });

    fuelCapacityEditText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(@NonNull final CharSequence s, final int start, final int count, final int after) {

      }

      @Override public void onTextChanged(@NonNull final CharSequence s, final int start, final int before, final int count) {
        if (!TextUtils.equals(s, "")) {
          fuelTankCapacity = (Integer.valueOf(s.toString()));
          writeDataToFile();
        }
      }

      @Override public void afterTextChanged(@NonNull final Editable s) {

      }
    });
  }

  private void setupTextFields() {
    if (fuelConsumption == ConstantValues.FUEL_CONSUMPTION_DEFAULT) {
      final String fuelCons = ConstantValues.FUEL_CONSUMPTION_DEFAULT + getString(R.string.fuel_cons_prefix);
      curFuelCons.setText(fuelCons);
    } else {
      final String fuelCons = fuelConsumption + getString(R.string.fuel_cons_prefix);
      curFuelCons.setText(fuelCons);
    }

    if (fuelCost == ConstantValues.FUEL_COST_DEFAULT) {
      final String fuelCost = ConstantValues.FUEL_COST_DEFAULT + currency_prefix;
      curFuelPrice.setText(fuelCost);
    } else {
      final String fuelCost = this.fuelCost + currency_prefix;
      curFuelPrice.setText(fuelCost);
    }

    if (fuelTankCapacity == ConstantValues.FUEL_TANK_CAPACITY_DEFAULT) {
      final String fuelCap = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT + getString(R.string.fuel_prefix);
      curFuelCapacity.setText(fuelCap);
    } else {
      final String fuelCapacity = fuelTankCapacity + getString(R.string.fuel_prefix);
      curFuelCapacity.setText(fuelCapacity);
    }
  }

  private void setupMeasurementUnitSpinner() {
    final String kmh = getString(R.string.kilometreUnit);
    final String mph = getString(R.string.milesUnit);
    final String knots = getString(R.string.knots);
    final String title = getString(R.string.measurementUnitSpinnerTitle);

    final String[] data = {kmh, mph, knots};
    final ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, data);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    measurementUnitSpinner.setAdapter(adapter);
    measurementUnitSpinner.setPrompt(title);

    measurementUnitSpinner.setSelection(preferences.getInt("measurementUnitPosition", 0));
    measurementUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(@NonNull final AdapterView<?> parent, @NonNull final View view, final int position, final long id) {
        setMeasurementUnit(position, kmh, mph, knots);
      }

      @Override public void onNothingSelected(@NonNull final AdapterView<?> arg0) {
      }
    });
  }

  private void setupCurrencyUnitSpinner() {
    final String grn_label = getString(R.string.grn_label);
    final String rub_label = getString(R.string.rub_label);
    final String usd_label = getString(R.string.usd_label);
    final String eur_label = getString(R.string.eur_label);

    final String grn = getString(R.string.grn);
    final String rub = getString(R.string.rub);
    final String usd = getString(R.string.usd);
    final String eur = getString(R.string.eur);

    currency_prefix = preferences.getString("currencyUnit", "");
    if (TextUtils.equals(currency_prefix, "")) {
      currency_prefix = grn;
    }

    final String title = getString(R.string.currencyUnitText);

    final String[] data = {grn_label, rub_label, usd_label, eur_label};
    final ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, data);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    currencyUnitSpinner.setAdapter(adapter);
    currencyUnitSpinner.setPrompt(title);

    currencyUnitSpinner.setSelection(preferences.getInt("currencyUnitPosition", 0));
    currencyUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(@NonNull final AdapterView<?> parent, @NonNull final View view, final int position, final long id) {
        setCurrencyUnit(position, grn, rub, usd, eur);
        updateFuelCost();
      }

      @Override public void onNothingSelected(@NonNull final AdapterView<?> arg0) {
      }
    });
  }

  private void updateFuelCost() {
    currency_prefix = preferences.getString("currencyUnit", "");
    final String fuelCost = this.fuelCost + currency_prefix;
    curFuelPrice.setText(fuelCost);
    curFuelPrice.invalidate();
  }

  private void updateTextFields() {
    final String fuelCons = fuelConsumption + getString(R.string.fuel_cons_prefix);
    curFuelCons.setText(fuelCons);

    final String fuelCost = this.fuelCost + currency_prefix;
    curFuelPrice.setText(fuelCost);

    final String fuelCapacity = fuelTankCapacity + getString(R.string.fuel_prefix);
    curFuelCapacity.setText(fuelCapacity);
  }

  private void readDataFromFile() {
    final ReadInternalFile readFileTask = new ReadInternalFile();
    readFileTask.execute();
  }

  private void writeDataToFile() {
    final WriteInternalFile writeFileTask = new WriteInternalFile();
    writeFileTask.execute();
  }

  private class WriteInternalFile extends AsyncTask<Void, Void, Boolean> {
    @Override protected Boolean doInBackground(@NonNull final Void... params) {

      final FileOutputStream fos;

      final float consumption = fuelConsumption;
      final float price = fuelCost;
      final int capacity = fuelTankCapacity;

      if (DEBUG) {
        Log.d(LOG_TAG, "writeTripDataToFileSetting: writeCalled");
        Log.d(LOG_TAG, "writeTripDataToFileSetting: writeCalled" + consumption + price + capacity);
      }

      try {
        fos = context.openFileOutput(ConstantValues.INTERNAL_SETTING_FILE_NAME, Context.MODE_PRIVATE);
        final ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeFloat(consumption);
        os.writeFloat(price);
        os.writeInt(capacity);

        os.close();
        fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return true;
    }

    @Override protected void onPostExecute(@NonNull final Boolean result) {
      if (result) {
        updateTextFields();
        if (DEBUG) {
          Log.d(LOG_TAG, "file written successfully");
        }
      }
    }
  }

  private class ReadInternalFile extends AsyncTask<String, Void, Boolean> {
    @Override protected Boolean doInBackground(@Nullable final String... params) {
      if (DEBUG) {
        Log.d(LOG_TAG, "readFileSettings");
      }
      final File file = context.getFileStreamPath(ConstantValues.INTERNAL_SETTING_FILE_NAME);
      if (file.exists()) {
        if (DEBUG) {
          Log.d(LOG_TAG, "readTripDataFromFileSettings: ");
        }
        final FileInputStream fis;
        try {
          fis = context.openFileInput(ConstantValues.INTERNAL_SETTING_FILE_NAME);
          final ObjectInputStream is = new ObjectInputStream(fis);
          final float consumption = is.readFloat();
          final float fuelPrice = is.readFloat();
          final int capacity = is.readInt();

          fuelConsumption = consumption;
          fuelCost = fuelPrice;
          fuelTankCapacity = capacity;

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

    @Override protected void onPostExecute(@NonNull final Boolean result) {
      setupTextFields();
      super.onPostExecute(result);
    }
  }
}