package com.example.aprokopenko.triphelper.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.os.Bundle;

import com.example.aprokopenko.triphelper.listener.OnListFragmentInteractionListener;
import com.example.aprokopenko.triphelper.adapter.TripListRecyclerViewAdapter;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.MathUtils;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Bind;


public class TripListFragment extends Fragment implements OnListFragmentInteractionListener {
    @Bind(R.id.timeSpentOnAllTripsListFrag)
    TextView     timeSpentView;
    @Bind(R.id.avgFuelConsListFrag)
    TextView     avgFuelConsumptionView;
    @Bind(R.id.distanceTravelledListFrag)
    TextView     distanceTravelledView;
    @Bind(R.id.moneyOnFuelSpentListFrag)
    TextView     moneyOnFuelView;
    @Bind(R.id.fuelSpentListFrag)
    TextView     fuelSpentView;
    @Bind(R.id.tripList)
    RecyclerView tripListView;
    @Bind(R.id.avgSpeedListFrag)
    TextView     avgSpeedView;

    private static final String LOG_TAG          = "TripListFragment";
    private TripData                          tripData;
    private ArrayList<Trip>                   trips;

    public TripListFragment() {
    }

    public static TripListFragment newInstance() {
        return new TripListFragment();
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        ButterKnife.bind(this, view);
        tripListView.setAdapter(new TripListRecyclerViewAdapter(trips, this));
        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String distance         = UtilMethods.formatFloat(tripData.getDistanceTravelled()) + " " + getString(R.string.distance_prefix);
        String avgFuelCons      = UtilMethods.formatFloat(tripData.getAvgFuelConsumption()) + " " + getString(R.string.fuel_prefix);
        String moneyOnFuelSpent = UtilMethods.formatFloat(tripData.getMoneyOnFuelSpent()) + " " + getString(R.string.currency_prefix);
        String fuelSpent        = UtilMethods.formatFloat(tripData.getFuelSpent()) + " " + getString(R.string.fuel_prefix);
        String avgSpeed         = UtilMethods.formatFloat(tripData.getAvgSpeed()) + " " + getString(R.string.speed_prefix);

        distanceTravelledView.setText(distance);
        avgFuelConsumptionView.setText(avgFuelCons);
        moneyOnFuelView.setText(moneyOnFuelSpent);
        fuelSpentView.setText(fuelSpent);
        timeSpentView.setText(MathUtils.getTimeInNormalFormat(tripData.getTimeSpentOnTrips()));
        avgSpeedView.setText(avgSpeed);
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
        float            distTravelled     = trip.getDistanceTravelled();
        float            timeSpentInMotion = trip.getTimeSpentInMotion();
        float            moneyOnFuelSpent  = trip.getMoneyOnFuelSpent();
        float            fuelConsumed      = trip.getAvgFuelConsumption();
        float            timeSpentOnStop   = trip.getTimeSpentOnStop();
        float            fuelSpent         = trip.getFuelSpent();
        float            timeSpent         = trip.getTimeSpent();
        String           tripDate          = trip.getTripDate();
        float            avgSpeed          = trip.getAvgSpeed();
        int              tripId            = trip.getTripID();
        ArrayList<Route> routes            = trip.getRoute();

        TripInfoFragment tripInfoFragment = TripInfoFragment
                .newInstance(tripDate, distTravelled, avgSpeed, timeSpent, timeSpentInMotion, timeSpentOnStop, fuelConsumed, fuelSpent,
                        tripId, routes, moneyOnFuelSpent);
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
