package com.example.aprokopenko.triphelper.ui.main_screen;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;

public interface MainContract {
    interface View {
        void setFuelLeft(final String fuelLeft);

        void resumeAdvert();

        void pauseAdvert();

        void updateSpeedometerTextField(final float speed);

        void setGpsIconPassive();

        void setGpsIconActive();

        void setupAdView(@NonNull final AdRequest request);

        void stopButtonTurnActive();

        void startButtonTurnActive();

        void setFuelVisible();

        void hideFab();

        void showFab();

        void showFuelFillDialog();

        void setSpeedometerValue(final float speed);

        void setPresenter(MainContract.UserActionListener userActionListener);
    }

    interface UserActionListener {
        void onFileErased();

        void onFuelFilled(final float fuelFilled);

        void onSpeedChanged(final float speed);

        void onSave(Bundle saveInstanceStateBundle);

        void onPause();

        void start(final Bundle savedInstanceState);

        void onResume();

        void onStartClick();

        void onStopClick();

        void onSettingsClick();

        void onTripListClick();

        void onRefillClick();

        void configureTripProcessor();

        void onOpenMapFragment();

        void cleanAllProcess();

        void requestLocationPermissions();
    }
}