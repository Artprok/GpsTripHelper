package com.example.aprokopenko.triphelper.ui.setting_screen;

import android.support.annotation.NonNull;

public class SettingsContract {
    public interface View{
        void setupTextFields(float consumption, float price, float fuelTank);

        void updateTextFields(float consumption, float price, float fuelTank);

        void setPresenter(@NonNull final SettingsContract.UserActionListener presenter);
    }

    public interface UserActionListener{

        void setMeasurementUnit(final int position);

        void setCurrencyUnit(int position);

        void onFuelPriceChanged(CharSequence s);

        void onFuelConsumptionChanged(CharSequence s);

        void onFuelCapacityChanged(CharSequence s);

        void fileSuccessfullyErased();

        void start();
    }

}
