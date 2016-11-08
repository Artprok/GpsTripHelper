package com.example.aprokopenko.triphelper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.aprokopenko.triphelper.datamodel.TripData;

import javax.inject.Singleton;

/**
 * Class responsible for transfer a {@link TripData} between fragments and state.
 */
@Singleton
public class DataHolderFragment extends android.support.v4.app.Fragment {
  private static final String LOG_TAG = "DataHolder";
  private TripData tripData;

  public static DataHolderFragment newInstance() {
    return new DataHolderFragment();
  }

  public DataHolderFragment() {
    // Required empty public constructor
  }

  @Override public void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
  }

  public TripData getTripData() {
    return tripData;
  }

  public void setTripData(@NonNull final TripData tripData) {
    this.tripData = tripData;
  }
}