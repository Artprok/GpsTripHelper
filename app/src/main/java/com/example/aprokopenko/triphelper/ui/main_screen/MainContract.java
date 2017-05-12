package com.example.aprokopenko.triphelper.ui.main_screen;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.ui.map.MapFragment;
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

        void restoreFuelLevel(final String fuelAmount);

        void showTripListFragment(final TripData tripData);

        void showEndTripToast();

        void showStartTripToast();

        void showSatellitesConnectedToast();

        void onSpeedChanged(final float speed);

        void requestLocationPermissions();
    }

    interface UserActionListener {
        void onFileErased();

        void onFuelFilled(final float fuelFilled);

        void onSave(Bundle saveInstanceStateBundle, boolean buttonVisible);

        void onPause(boolean buttonVisible, boolean isMainFragment);

        void start(final Bundle savedInstanceState, boolean isMainFragment, boolean buttonVisible);

        void onResume(boolean isMainFragment, boolean isButtonVisible);

        void onStartClick();

        void onStopClick();

        void onSettingsClick(boolean buttonVisible);

        void onTripListClick(boolean buttonVisible);

        void onSpeedChanged(final float speed);

        void onRefillClick();

        void configureTripProcessor();

        void onOpenMapFragment(boolean buttonVisible);

        void cleanAllProcess(boolean buttonVisible);

        void onConfigureMapFragment(MapFragment mapFragment);
    }
}