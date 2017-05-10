package com.example.aprokopenko.triphelper.ui.setting_screen;

public class SettingsContract {
    public interface View{
        void setupTextFields(float consumption, float price, float fuelTank);

        void updateTextFields(float consumption, float price, float fuelTank);
    }

    public interface UserActionListener{

        void setMeasurementUnit(final int position);

        void setCurrencyUnit(int position);

        void aboutButtonCliked();

        void eraseButtonCliked();

        void onFuelPriceChanged(CharSequence s);

        void onFuelConsumptionChanged(CharSequence s);

        void onFuelCapacityChanged(CharSequence s);
    }

}
