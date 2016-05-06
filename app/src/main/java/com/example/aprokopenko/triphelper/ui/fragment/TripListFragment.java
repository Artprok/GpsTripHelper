package com.example.aprokopenko.triphelper.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.os.Bundle;

import com.example.aprokopenko.triphelper.listener.OnListFragmentInteractionListener;
import com.example.aprokopenko.triphelper.adapter.TripListRecyclerViewAdapter;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.R;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Bind;

public class TripListFragment extends Fragment implements OnListFragmentInteractionListener {
    @Bind(R.id.avgFuelConsListFrag)
    TextView     avgFuelConsumptionView;
    @Bind(R.id.distanceTravelledListFrag)
    TextView     distanceTravelledView;
    @Bind(R.id.moneyOnFuelSpentListFrag)
    TextView     moneyOnFuelView;
    @Bind(R.id.timeSpentOnAllTripsListFrag)
    TextView     timeSpentView;
    @Bind(R.id.fuelSpentListFrag)
    TextView     fuelSpentView;
    @Bind(R.id.tripList)
    RecyclerView tripListView;
    @Bind(R.id.avgSpeedListFrag)
    TextView     avgSpeedView;
    @Bind(R.id.maxSpeedListFrag)
    TextView     maxSpeedView;


    private static final String LOG_TAG = "TripListFragment";
    private TripData        tripData;
    private ArrayList<Trip> trips;

    public TripListFragment() {
    }

    @Contract(" -> !null") public static TripListFragment newInstance() {
        return new TripListFragment();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        ButterKnife.bind(this, view);
        if (savedInstanceState != null) {
            tripData = savedInstanceState.getParcelable("tripData");
            if (tripData != null) {
                trips = tripData.getTrips();
            }
        }
        tripListView.setAdapter(new TripListRecyclerViewAdapter(trips, this));
        tripListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        if (tripData != null) {
            outState.putParcelable("tripData", tripData);
        }
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Resources res = getResources();
        String    distance;
        String    avgFuelCons;
        String    moneyOnFuelSpent;
        String    fuelSpent;
        String    avgSpeed;
        String    maxSpeed;
        float     timeSpentOnTrips;

        distance = UtilMethods.formatFloat(tripData.getDistanceTravelled()) + " " + getString(R.string.distance_prefix);
        avgFuelCons = UtilMethods.formatFloat(tripData.getAvgFuelConsumption()) + " " + getString(R.string.fuel_cons_prefix);
        moneyOnFuelSpent = UtilMethods.formatFloat(tripData.getMoneyOnFuelSpent()) + " " + getString(R.string.currency_prefix);
        fuelSpent = UtilMethods.formatFloat(tripData.getFuelSpent()) + " " + getString(R.string.fuel_prefix);
        avgSpeed = UtilMethods.formatFloat(tripData.getAvgSpeed()) + " " + getString(R.string.speed_prefix);
        maxSpeed = UtilMethods.formatFloat(tripData.getMaxSpeed()) + " " + getString(R.string.speed_prefix);
        timeSpentOnTrips = tripData.getTimeSpentOnTrips();

        distanceTravelledView.setText(distance);
        avgFuelConsumptionView.setText(avgFuelCons);
        moneyOnFuelView.setText(moneyOnFuelSpent);
        fuelSpentView.setText(fuelSpent);
        timeSpentView.setText(CalculationUtils.getTimeInNormalFormat(timeSpentOnTrips, res));
        avgSpeedView.setText(avgSpeed);
        maxSpeedView.setText(maxSpeed);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override public void onDetach() {
        super.onDetach();
        tripData = null;
        trips = null;
    }

    @Override public void onListFragmentInteraction(Trip trip) {
        float            fuelConsumed      = trip.getAvgFuelConsumption();
        float            distTravelled     = trip.getDistanceTravelled();
        float            timeSpentInMotion = trip.getTimeSpentInMotion();
        float            moneyOnFuelSpent  = trip.getMoneyOnFuelSpent();
        float            timeSpentOnStop   = trip.getTimeSpentOnStop();
        float            fuelSpent         = trip.getFuelSpent();
        float            timeSpent         = trip.getTimeSpent();
        String           tripDate          = trip.getTripDate();
        float            avgSpeed          = trip.getAvgSpeed();
        float            maxSpeed          = trip.getMaxSpeed();
        int              tripId            = trip.getTripID();
        ArrayList<Route> routes            = trip.getRoute();


        TripInfoFragment tripInfoFragment = TripInfoFragment
                .newInstance(tripDate, distTravelled, avgSpeed, timeSpent, timeSpentInMotion, timeSpentOnStop, fuelConsumed, fuelSpent,
                        tripId, routes, moneyOnFuelSpent, maxSpeed);
        UtilMethods.replaceFragment(tripInfoFragment, ConstantValues.TRIP_INFO_FRAGMENT_TAG, getActivity());
    }

    @Override public void onFragmentReplacing(TripInfoFragment tripInfoFragment) {
        UtilMethods.replaceFragment(tripInfoFragment, ConstantValues.TRIP_INFO_FRAGMENT_TAG, getActivity());
    }

    public void setTripData(TripData tripData) {
        this.tripData = tripData;
        trips = tripData.getTrips();
    }
}
