package com.example.aprokopenko.triphelper.ui.trip_list;

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
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.listeners.ListFragmentInteractionListener;
import com.example.aprokopenko.triphelper.ui.trip_info.TripInfoFragment;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;

import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Class representing a list populated with {@link Trip}.
 */
@Singleton
public class TripListFragment extends android.support.v4.app.Fragment implements ListFragmentInteractionListener, TripListContract.view {
    @BindView(R.id.fragment_item_list_avgFuelConsListFrag)
    TextView avgFuelConsumptionView;
    @BindView(R.id.fragment_item_list_distanceTravelledListFrag)
    TextView distanceTravelledView;
    @BindView(R.id.fragment_item_list_moneyOnFuelSpentListFrag)
    TextView moneyOnFuelView;
    @BindView(R.id.fragment_item_list_timeSpentOnAllTripsListFrag)
    TextView timeSpentView;
    @BindView(R.id.fragment_item_list_fuelSpentListFrag)
    TextView fuelSpentView;
    @BindView(R.id.fragment_item_list_tripList)
    RecyclerView tripListView;
    @BindView(R.id.fragment_item_list_avgSpeedListFrag)
    TextView avgSpeedView;
    @BindView(R.id.fragment_item_list_maxSpeedListFrag)
    TextView maxSpeedView;
    @BindView(R.id.fragment_item_list_progressBar)
    ProgressBar progressBar;

    private static final String CURRENCY_UNIT = "currencyUnit";
    private static final String TRIP_DATA = "TripData";
    private static final String LOG_TAG = "TripListFragment";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private Unbinder unbinder;
    private Bundle state;
    private TripListContract.userActionListener userActionListener;
    private String curUnit;

    public TripListFragment() {
    }

    public static TripListFragment newInstance(@NonNull final TripData tripData) {
        final TripListFragment tripListFragment = new TripListFragment();
        final Bundle args = new Bundle();

        args.putParcelable(TRIP_DATA, tripData);
        tripListFragment.setArguments(args);
        tripListFragment.setUserActionListener();
        return tripListFragment;
    }

    private void setUserActionListener() {
        if (userActionListener == null) {
            userActionListener = new TripListPresenter(this);
        }
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        userActionListener.onCreate(getArguments().getParcelable(TRIP_DATA));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        unbinder = ButterKnife.bind(this, view);
        if (savedInstanceState != null) {
            userActionListener.onCreateView(savedInstanceState.getParcelable(TRIP_DATA));
        }
        tripListView.setAdapter(userActionListener.onSetAdapter(this));
        tripListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        final TripData tripData = userActionListener.onSaveInstanceState();
        if (tripData != null) {
            outState.putParcelable(TRIP_DATA, tripData);
        }
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            state = savedInstanceState;
        }

        curUnit = TripHelperApp.getSharedPreferences().getString(CURRENCY_UNIT, "");
        if (!TextUtils.isEmpty(curUnit)) {
            curUnit = getString(R.string.grn);
        }

        userActionListener.onViewCreated();
    }

    @Override
    public void setDistanceTravelledValue(float distanceTravelled) {
        distanceTravelledView.setText(UtilMethods.formatFloatDecimalFormat(distanceTravelled) + " " + getString(R.string.distance_prefix));
    }

    @Override
    public void setAvgFuelConsumptionValue(float avgFuelConsumption) {
        avgFuelConsumptionView.setText(UtilMethods.formatFloatDecimalFormat(avgFuelConsumption) + " " + getString(R.string.fuel_cons_prefix));
    }

    @Override
    public void setMoneyOnFuelValue(float moneyOnFuelSpent) {
        moneyOnFuelView.setText(UtilMethods.formatFloatDecimalFormat(moneyOnFuelSpent) + " " + curUnit);

    }

    @Override
    public void setFuelSpentValue(float fuelSpent) {
        fuelSpentView.setText(UtilMethods.formatFloatDecimalFormat(fuelSpent) + " " + getString(R.string.fuel_prefix));

    }

    @Override
    public void setTimeSpentValue(float timeSpentOnTrips) {
        timeSpentView.setText(CalculationUtils.getTimeInNormalFormat(timeSpentOnTrips, getResources()));

    }

    @Override
    public void setAvgSpeedValue(float avgSpeed) {
        avgSpeedView.setText(UtilMethods.formatFloatDecimalFormat(avgSpeed) + " " + getString(R.string.speed_prefix));

    }

    @Override
    public void setMaxSpeedValue(float maxSpeed) {
        maxSpeedView.setText(UtilMethods.formatFloatDecimalFormat(maxSpeed) + " " + getString(R.string.speed_prefix));

    }

    @Override
    public void onPause() {
        saveState();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (state != null) {
            userActionListener.onResume(state.getParcelable(TRIP_DATA));
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        userActionListener.onDetach();
        super.onDetach();
    }

    @Override
    public void onFragmentReplacing(@NonNull final TripInfoFragment tripInfoFragment) {
        UtilMethods.replaceFragment(tripInfoFragment, ConstantValues.TRIP_INFO_FRAGMENT_TAG, getActivity());
    }

    @Override
    public void onListItemClick(@NonNull final Trip trip) {
        progressBar.setVisibility(View.VISIBLE);
        UtilMethods.replaceFragment(TripInfoFragment.newInstance(trip), ConstantValues.TRIP_INFO_FRAGMENT_TAG, getActivity());
    }

    public void setTripData(@NonNull final TripData tripData) {
        userActionListener.setTripData(tripData);
    }

    private void saveState() {
        if (state == null) {
            state = new Bundle();
        }
        onSaveInstanceState(state);
    }
}