package com.example.aprokopenko.triphelper.ui.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.adapter.TripListRecyclerViewAdapter;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.datamodel.TripInfoContainer;
import com.example.aprokopenko.triphelper.listener.ListFragmentInteractionListener;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;

import java.util.ArrayList;

import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Class representing a list populated with {@link Trip}.
 */
@Singleton
public class TripListFragment extends android.support.v4.app.Fragment implements ListFragmentInteractionListener {
  @BindView(R.id.text_avgFuelConsListFrag)
  TextView avgFuelConsumptionView;
  @BindView(R.id.text_distanceTravelledListFrag)
  TextView distanceTravelledView;
  @BindView(R.id.text_moneyOnFuelSpentListFrag)
  TextView moneyOnFuelView;
  @BindView(R.id.text_timeSpentOnAllTripsListFrag)
  TextView timeSpentView;
  @BindView(R.id.text_fuelSpentListFrag)
  TextView fuelSpentView;
  @BindView(R.id.tripList)
  RecyclerView tripListView;
  @BindView(R.id.text_avgSpeedListFrag)
  TextView avgSpeedView;
  @BindView(R.id.text_maxSpeedListFrag)
  TextView maxSpeedView;
  @BindView(R.id.progressBar)
  ProgressBar progressBar;

  private static final String LOG_TAG = "TripListFragment";
  public static final boolean DEBUG = BuildConfig.DEBUG;

  private TripData tripData;
  private ArrayList<Trip> trips;
  private Unbinder unbinder;
  private Bundle state;

  public TripListFragment() {
  }

  public static TripListFragment newInstance() {
    return new TripListFragment();
  }

  @Override public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_item_list, container, false);
    unbinder = ButterKnife.bind(this, view);
    if (savedInstanceState != null) {
      tripData = savedInstanceState.getParcelable("tripData");
      if (tripData == null) {
        final DataHolderFragment dataHolderFragment = (DataHolderFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(ConstantValues.DATA_HOLDER_TAG);
        tripData = dataHolderFragment.getTripData();
      }
      if (tripData != null) {
        trips = tripData.getTrips();
      }
    }
    tripListView.setAdapter(new TripListRecyclerViewAdapter(trips, this));
    tripListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    return view;
  }

  @Override public void onSaveInstanceState(@NonNull final Bundle outState) {
    if (tripData != null) {
      outState.putParcelable("tripData", tripData);
    }
  }

  @Override public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (savedInstanceState != null) {
      state = savedInstanceState;
    }
    final Resources res = getResources();
    final String distance;
    final String avgFuelCons;
    final String moneyOnFuelSpent;
    final String fuelSpent;
    final String avgSpeed;
    final String maxSpeed;
    final float timeSpentOnTrips;

    distance = UtilMethods.formatFloatDecimalFormat(tripData.getDistanceTravelled()) + " " + getString(R.string.distance_prefix);
    avgFuelCons = UtilMethods.formatFloatDecimalFormat(tripData.getAvgFuelConsumption()) + " " + getString(R.string.fuel_cons_prefix);
    String curUnit = TripHelperApp.getSharedPreferences().getString("currencyUnit", "");
    if (TextUtils.equals(curUnit, "")) {
      curUnit = getString(R.string.grn);
    }
    moneyOnFuelSpent = UtilMethods.formatFloatDecimalFormat(tripData.getMoneyOnFuelSpent()) + " " + curUnit;
    fuelSpent = UtilMethods.formatFloatDecimalFormat(tripData.getFuelSpent()) + " " + getString(R.string.fuel_prefix);
    avgSpeed = UtilMethods.formatFloatDecimalFormat(tripData.getAvgSpeed()) + " " + getString(R.string.speed_prefix);
    maxSpeed = UtilMethods.formatFloatDecimalFormat(tripData.getMaxSpeed()) + " " + getString(R.string.speed_prefix);
    timeSpentOnTrips = tripData.getTimeSpentOnTrips();

    distanceTravelledView.setText(distance);
    avgFuelConsumptionView.setText(avgFuelCons);
    moneyOnFuelView.setText(moneyOnFuelSpent);
    fuelSpentView.setText(fuelSpent);
    timeSpentView.setText(CalculationUtils.getTimeInNormalFormat(timeSpentOnTrips, res));
    avgSpeedView.setText(avgSpeed);
    maxSpeedView.setText(maxSpeed);
  }

  @Override public void onPause() {
    saveState();
    super.onPause();
  }

  @Override public void onResume() {
    if (state != null) {
      tripData = state.getParcelable("tripData");
    }
    super.onResume();
  }


  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onDetach() {
    tripData = null;
    trips = null;
    super.onDetach();
  }

  @Override public void onFragmentReplacing(@NonNull final TripInfoFragment tripInfoFragment) {
    UtilMethods.replaceFragment(tripInfoFragment, ConstantValues.TRIP_INFO_FRAGMENT_TAG, getActivity());
  }

  @Override public void onListItemClick(@NonNull final Trip trip) {
    progressBar.setVisibility(View.VISIBLE);
    final float fuelConsumed = trip.getAvgFuelConsumption();
    final float distTravelled = trip.getDistanceTravelled();
    final float timeSpentInMotion = trip.getTimeSpentInMotion();
    final float moneyOnFuelSpent = trip.getMoneyOnFuelSpent();
    final float timeSpentOnStop = trip.getTimeSpentOnStop();
    final float fuelSpent = trip.getFuelSpent();
    final float timeSpent = trip.getTimeSpentForTrip();
    final String tripDate = trip.getTripDate();
    final float avgSpeed = trip.getAvgSpeed();
    final float maxSpeed = trip.getMaxSpeed();
    final int tripId = trip.getTripID();
    final ArrayList<Route> routes = trip.getRoute();

    final TripInfoContainer tripInfoContainer = new TripInfoContainer(tripDate, distTravelled, avgSpeed, timeSpent, timeSpentInMotion,
            timeSpentOnStop, fuelConsumed, fuelSpent, tripId, routes, moneyOnFuelSpent, maxSpeed, trip);
    final TripInfoFragment tripInfoFragment = TripInfoFragment.newInstance(tripInfoContainer);
    UtilMethods.replaceFragment(tripInfoFragment, ConstantValues.TRIP_INFO_FRAGMENT_TAG, getActivity());
  }

  public void setTripData(@NonNull final TripData tripData) {
    this.tripData = tripData;
    trips = tripData.getTrips();
  }

  private void saveState() {
    if (state == null) {
      state = new Bundle();
    }
    onSaveInstanceState(state);
  }
}