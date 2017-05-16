package com.example.aprokopenko.triphelper.ui.trip_list;

import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.adapter.TripListRecyclerViewAdapter;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.listeners.ListFragmentInteractionListener;

import java.util.ArrayList;

public class TripListPresenter implements TripListContract.UserActionListener {
    private static final String CURRENCY_UNIT = "currencyUnit";
    private static final String TRIP_DATA = "TripData";
    private static final String LOG_TAG = "TripListFragment";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private ArrayList<Trip> trips;
    private TripData tripData;
    private final TripListContract.View view;

    public TripListPresenter(TripListContract.View view) {
        this.view = view;
    }

    @Override
    public void onCreate(Parcelable tripData) {
        this.tripData = (TripData) tripData;
    }

    @Override
    public RecyclerView.Adapter onSetAdapter(ListFragmentInteractionListener listFragmentInteractionListener) {
        return new TripListRecyclerViewAdapter(trips, listFragmentInteractionListener);
    }

    @Override
    public void onDetach() {
        trips = null;
    }

    @Override
    public void onCreateView(Parcelable tripData) {
        this.tripData = (TripData) tripData;
        if (tripData != null) {
            trips = this.tripData.getTrips();
        }
    }

    @Override
    public TripData onSaveInstanceState() {
        return tripData;
    }

    @Override
    public void onResume(Parcelable parcelable) {
        tripData = (TripData) parcelable;
    }

    @Override
    public void setTripData(TripData tripData) {
        this.tripData = tripData;
        trips = tripData.getTrips();
    }

    @Override
    public void onViewCreated() {
        view.setDistanceTravelledValue(tripData.getDistanceTravelled());
        view.setAvgFuelConsumptionValue(tripData.getAvgFuelConsumption());
        view.setMoneyOnFuelValue(tripData.getMoneyOnFuelSpent());
        view.setFuelSpentValue(tripData.getFuelSpent());
        view.setTimeSpentValue(tripData.getTimeSpentOnTrips());
        view.setAvgSpeedValue(tripData.getAvgSpeed());
        view.setMaxSpeedValue(tripData.getMaxSpeed());

//        distanceTravelledView.setText(UtilMethods.formatFloatDecimalFormat(tripData.getDistanceTravelled()) + " " + getString(R.string.distance_prefix));
//        avgFuelConsumptionView.setText(UtilMethods.formatFloatDecimalFormat(tripData.getAvgFuelConsumption()) + " " + getString(R.string.fuel_cons_prefix));
//        moneyOnFuelView.setText(UtilMethods.formatFloatDecimalFormat(tripData.getMoneyOnFuelSpent()) + " " + curUnit);
//        fuelSpentView.setText(UtilMethods.formatFloatDecimalFormat(tripData.getFuelSpent()) + " " + getString(R.string.fuel_prefix));
//        timeSpentView.setText(CalculationUtils.getTimeInNormalFormat(tripData.getTimeSpentOnTrips(), getResources()));
//        avgSpeedView.setText(UtilMethods.formatFloatDecimalFormat(tripData.getAvgSpeed()) + " " + getString(R.string.speed_prefix));
//        maxSpeedView.setText(UtilMethods.formatFloatDecimalFormat(tripData.getMaxSpeed()) + " " + getString(R.string.speed_prefix));
    }
}
