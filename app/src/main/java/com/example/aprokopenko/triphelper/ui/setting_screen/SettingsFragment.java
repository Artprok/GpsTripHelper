package com.example.aprokopenko.triphelper.ui.setting_screen;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
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
import com.example.aprokopenko.triphelper.ui.main_screen.MainContract;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;

import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;

/**
 * Class responsible for store and changing settings.
 */
@Singleton
public class SettingsFragment extends android.support.v4.app.Fragment implements SettingsContract.View {
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
    @BindView(R.id.settings_fragment_measurements_unit_spinner)
    Spinner measurementUnitSpinner;
    @BindView(R.id.settings_fragment_currency_unit_spinner)
    Spinner currencyUnitSpinner;
    @BindView(R.id.backgroundSwitch)
    SwitchCompat backgroundSwitch;

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
    private MainContract.UserActionListener fileEraseListener;
    private Unbinder unbinder;
    private String currency_prefix;
    private SettingsContract.UserActionListener userActionListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        preferences = TripHelperApp.getSharedPreferences();
        userActionListener = new SettingsPresenter(this, preferences, getContext());
        userActionListener.start();
        setupMeasurementUnitSpinner();
        setupCurrencyUnitSpinner();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @OnClick({R.id.btn_about, R.id.btn_erase})
    public void onClickButton(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.btn_about:
                UtilMethods.buildAndShowAboutDialog(getActivity());
                break;
            case R.id.btn_erase:
                if (UtilMethods.eraseFile(getContext())) {
                    fileEraseListener.onFileErased();
                    userActionListener.fileSuccessfullyErased();
                    UtilMethods.showToast(getContext(), getString(R.string.file_erased_toast));
                } else {
                    UtilMethods.showToast(getContext(), getString(R.string.file_not_erased_toast));
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @OnTextChanged(R.id.editText_fuelPrice)
    public void onFuelPriceChanged(@NonNull final CharSequence s) {
        userActionListener.onFuelPriceChanged(s);
    }

    @OnTextChanged(R.id.editText_fuelConsumption)
    public void onFuelConsumptionChanged(@NonNull final CharSequence s) {
        userActionListener.onFuelConsumptionChanged(s);
    }

    @OnTextChanged(R.id.editText_fuelCapacity)
    public void onFuelCapacityChanged(@NonNull final CharSequence s) {
        userActionListener.onFuelCapacityChanged(s);
    }

    @Override
    public void setupTextFields(float consumption, float price, float fuelTank) {
        final String fuelCons;
        final String fuelCost;
        final String fuelCapacity;

        if (fuelConsumption == ConstantValues.FUEL_CONSUMPTION_DEFAULT) {
            fuelCons = ConstantValues.FUEL_CONSUMPTION_DEFAULT + getString(R.string.fuel_cons_prefix);
            curFuelCons.setText(fuelCons);
        } else {
            fuelCons = fuelConsumption + getString(R.string.fuel_cons_prefix);
            curFuelCons.setText(fuelCons);
        }

        if (this.fuelCost == ConstantValues.FUEL_COST_DEFAULT) {
            fuelCost = ConstantValues.FUEL_COST_DEFAULT + currency_prefix;
            curFuelPrice.setText(fuelCost);
        } else {
            fuelCost = this.fuelCost + currency_prefix;
            curFuelPrice.setText(fuelCost);
        }

        if (fuelTankCapacity == ConstantValues.FUEL_TANK_CAPACITY_DEFAULT) {
            fuelCapacity = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT + getString(R.string.fuel_prefix);
            curFuelCapacity.setText(fuelCapacity);
        } else {
            fuelCapacity = fuelTankCapacity + getString(R.string.fuel_prefix);
            curFuelCapacity.setText(fuelCapacity);
        }
    }

    @Override
    public void updateTextFields(float consumption, float price, float fuelTank) {
        final String fuelCons = fuelConsumption + getString(R.string.fuel_cons_prefix);
        curFuelCons.setText(fuelCons);

        final String fuelCost = this.fuelCost + currency_prefix;
        curFuelPrice.setText(fuelCost);

        final String fuelCapacity = fuelTankCapacity + getString(R.string.fuel_prefix);
        curFuelCapacity.setText(fuelCapacity);
    }

    public void setFileEraseListener(@NonNull final MainContract.UserActionListener fileEraseListener) {
        this.fileEraseListener = fileEraseListener;
    }

    public void setPresenter(@NonNull final SettingsContract.UserActionListener presenter) {
        userActionListener = presenter;
    }

    private void setupMeasurementUnitSpinner() {
        final String[] data = {getString(R.string.kilometreUnit), getString(R.string.milesUnit), getString(R.string.knots)};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        measurementUnitSpinner.setAdapter(adapter);
        measurementUnitSpinner.setPrompt(getString(R.string.measurementUnitSpinnerTitle));

        measurementUnitSpinner.setSelection(preferences.getInt(MEASUREMENT_UNIT_POSITION, 0));
        measurementUnitSpinner.setSelection(preferences.getInt(MEASUREMENT_UNIT_POSITION, 0));
        measurementUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull final AdapterView<?> parent, @NonNull final View view, final int position, final long id) {
                userActionListener.setMeasurementUnit(position);
            }

            @Override
            public void onNothingSelected(@NonNull final AdapterView<?> arg0) {
            }
        });
    }

    private void setupCurrencyUnitSpinner() {
        currency_prefix = preferences.getString(CURRENCY_UNIT, "");
        if (TextUtils.equals(currency_prefix, "")) {
            currency_prefix = getString(R.string.grn);
        }

        final String[] data = {getString(R.string.grn_label), getString(R.string.rub_label), getString(R.string.usd_label), getString(R.string.eur_label)};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        currencyUnitSpinner.setAdapter(adapter);
        currencyUnitSpinner.setPrompt(getString(R.string.currencyUnitText));
        currencyUnitSpinner.setSelection(preferences.getInt(CURRENCY_UNIT_POSITION, 0));
        currencyUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull final AdapterView<?> parent, @NonNull final View view, final int position, final long id) {
                userActionListener.setCurrencyUnit(position);
                updateFuelCost();
            }

            @Override
            public void onNothingSelected(@NonNull final AdapterView<?> arg0) {
            }
        });
    }

    private void updateFuelCost() {
        final String fuelCost = this.fuelCost + preferences.getString(CURRENCY_UNIT, "");
        curFuelPrice.setText(fuelCost);
        curFuelPrice.invalidate();
    }
}