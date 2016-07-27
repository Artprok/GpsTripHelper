package com.example.aprokopenko.triphelper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.aprokopenko.triphelper.datamodel.TripData;

import javax.inject.Singleton;

@Singleton public class DataHolderFragment extends Fragment {
    private static final String LOG_TAG = "DataHolder";

    private TripData tripData;

    public DataHolderFragment() {
        // Required empty public constructor
    }

    public static DataHolderFragment newInstance() {
        return new DataHolderFragment();
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public TripData getTripData() {
        return tripData;
    }

    public void setTripData(TripData tripData) {
        this.tripData = tripData;
    }
}



