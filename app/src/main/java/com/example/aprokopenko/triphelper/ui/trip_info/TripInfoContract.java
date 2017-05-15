package com.example.aprokopenko.triphelper.ui.trip_info;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;

interface TripInfoContract {
    interface UserActionListener {

        void onCreate(Bundle arguments);

        void onViewCreated();

        boolean onDrawMap(GoogleMap googleMap, boolean toDraw);

        boolean onMapReady();
    }

    interface View {

        void setupUserActionListener(@NonNull final TripInfoContract.View view);

        void setPresenter(TripInfoPresenter tripInfoPresenter);

        void setTripTimeSpentOnStopText(float withoutMotionValue);

        void setTripTimeSpentInMotionValue(final float timeInMotion);

        void setTripTimeSpentValue(float timeSpent);

        void setTripAvgFuelConsumptionValue(final float averageFuelConsumption);

        void setTripMoneySpentValue(float moneySpent);

        void setTripDistanceTravelledValuse(float distTravelled);

        void setTripIdValue(String id);

        void setTripFuelSpentValue(float fuelSpent);

        void setTripMaxSpeedValue(float maxSpeed);

        void setTripAvgSpeedValue(float avgSpeed);

        void setTripDateValue(String tripDate);
    }
}