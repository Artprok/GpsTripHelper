package com.example.aprokopenko.triphelper.ui.trip_list;

import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;

import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.listeners.ListFragmentInteractionListener;

public interface TripListContract {
    interface userActionListener{

        void onCreate(Parcelable parcelable);

        void onCreateView(Parcelable parcelable);

        TripData onSaveInstanceState();

        void onResume(Parcelable parcelable);

        void setTripData(TripData tripData);

        void onViewCreated();

        RecyclerView.Adapter onSetAdapter(ListFragmentInteractionListener listener);

        void onDetach();
    }

    interface view{
        void setDistanceTravelledValue(float distanceTravelled);

        void setAvgFuelConsumptionValue(float avgFuelConsumption);

        void setMoneyOnFuelValue(float moneyOnFuelSpent);

        void setFuelSpentValue(float fuelSpent);

        void setTimeSpentValue(float timeSpentOnTrips);

        void setAvgSpeedValue(float avgSpeed);

        void setMaxSpeedValue(float maxSpeed);
    }
}