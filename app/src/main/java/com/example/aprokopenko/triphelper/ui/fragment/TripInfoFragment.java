package com.example.aprokopenko.triphelper.ui.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.MapUtilMethods;
import com.example.aprokopenko.triphelper.utils.util_methods.MathUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.example.aprokopenko.triphelper.R;
import com.google.android.gms.maps.MapView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Bind;

public class TripInfoFragment extends Fragment implements OnMapReadyCallback {

    @Bind(R.id.tripAvgFuelConsumption)
    TextView tripAvgFuelConsumptionView;
    @Bind(R.id.tripDistanceTravelled)
    TextView tripDistanceTravelledView;
    @Bind(R.id.tripTimeSpentInMotion)
    TextView tripTimeSpentInMotionView;
    @Bind(R.id.tripTimeSpentOnStop)
    TextView tripTimeSpentOnStopView;
    @Bind(R.id.tripFuelSpent)
    TextView tripFuelSpentView;
    @Bind(R.id.tripTimeSpent)
    TextView tripTimeSpentView;
    @Bind(R.id.tripAvgSpeed)
    TextView tripAvgSpeedView;
    @Bind(R.id.moneyOnTripFuelSpent)
    TextView moneySpentView;
    @Bind(R.id.tripDate)
    TextView tripDateView;
    @Bind(R.id.tripId)
    TextView tripIdView;
    @Bind(R.id.mapView)
    MapView  mapView;

    private static final String LOG_TAG = "TripInfoFragment";

    private static final String ARG_PARAM1  = "TripDate";
    private static final String ARG_PARAM2  = "DistTravelled";
    private static final String ARG_PARAM3  = "AvgSpeed";
    private static final String ARG_PARAM4  = "TimeSpent";
    private static final String ARG_PARAM5  = "TimeSpentOnMotion";
    private static final String ARG_PARAM6  = "TimeSpentOnStop";
    private static final String ARG_PARAM7  = "FuelConsumed";
    private static final String ARG_PARAM8  = "FuelSpent";
    private static final String ARG_PARAM9  = "TripId";
    private static final String ARG_PARAM10 = "LatitudeArray";
    private static final String ARG_PARAM11 = "LongitudeArray";
    private static final String ARG_PARAM12 = "SpeedArray";
    private static final String ARG_PARAM13 = "MoneySpent";

    private float  timeSpentInMotion;
    private float  timeSpentOnStop;
    private float  distTravelled;
    private float  fuelConsumed;
    private float  moneySpent;
    private float  fuelSpent;
    private float  timeSpent;
    private String tripDate;
    private float  avgSpeed;
    private int    tripId;

    private GoogleMap         googleMap;
    private Context           context;
    private ArrayList<Route>  routes;
    private ArrayList<String> speedValues;

    public TripInfoFragment() {
    }

    public static TripInfoFragment newInstance(String tripDate, float distTravelled, float avgSpeed, float timeSpent,
                                               float timeSpentInMotion, float timeSpentOnStop, float fuelConsumed, float fuelSpent,
                                               int tripId, ArrayList<Route> routes, Float moneySpent) {
        TripInfoFragment  fragment       = new TripInfoFragment();
        Bundle            args           = new Bundle();
        ArrayList<String> latitudeArray  = new ArrayList<>();
        ArrayList<String> longitudeArray = new ArrayList<>();
        ArrayList<String> speedArray     = new ArrayList<>();
        for (Route tmpRoute : routes) {
            if (tmpRoute != null) {
                // FIXME: 12.04.2016 ToString?? :(
                LatLng tempLatLang = tmpRoute.getRoutePoints();
                latitudeArray.add(String.valueOf(tempLatLang.latitude));
                longitudeArray.add(String.valueOf(tempLatLang.longitude));
                speedArray.add(String.valueOf(tmpRoute.getSpeed()));
            }
        }

        args.putString(ARG_PARAM1, tripDate);
        args.putFloat(ARG_PARAM2, distTravelled);
        args.putFloat(ARG_PARAM3, avgSpeed);
        args.putFloat(ARG_PARAM4, timeSpent);
        args.putFloat(ARG_PARAM5, timeSpentInMotion);
        args.putFloat(ARG_PARAM6, timeSpentOnStop);
        args.putFloat(ARG_PARAM7, fuelConsumed);
        args.putFloat(ARG_PARAM8, fuelSpent);
        args.putInt(ARG_PARAM9, tripId);
        args.putStringArrayList(ARG_PARAM10, latitudeArray);
        args.putStringArrayList(ARG_PARAM11, longitudeArray);
        args.putStringArrayList(ARG_PARAM12, speedArray);
        args.putFloat(ARG_PARAM13, moneySpent);

        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            tripDate = getArguments().getString(ARG_PARAM1);
            distTravelled = getArguments().getFloat(ARG_PARAM2);
            avgSpeed = getArguments().getFloat(ARG_PARAM3);
            timeSpent = getArguments().getFloat(ARG_PARAM4);
            timeSpentInMotion = getArguments().getFloat(ARG_PARAM5);
            timeSpentOnStop = getArguments().getFloat(ARG_PARAM6);
            fuelConsumed = getArguments().getFloat(ARG_PARAM7);
            fuelSpent = getArguments().getFloat(ARG_PARAM8);
            tripId = getArguments().getInt(ARG_PARAM9);

            ArrayList<String> latitudeArray  = getArguments().getStringArrayList(ARG_PARAM10);
            ArrayList<String> longitudeArray = getArguments().getStringArrayList(ARG_PARAM11);
            ArrayList<String> speedArray     = getArguments().getStringArrayList(ARG_PARAM12);
            moneySpent = getArguments().getFloat(ARG_PARAM13);
            speedValues = speedArray;

            routes = MapUtilMethods.unwrapRoute(latitudeArray, longitudeArray, speedArray);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trip_info, container, false);
        ButterKnife.bind(this, v);
        setupMapView(savedInstanceState);
        return v;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getActivity();
        setDataToInfoFragmentFields();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        UtilMethods.checkPermission(context);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        this.googleMap = googleMap;
        drawPathFromData();
    }


    private LatLng getPreviousLocation(int size, int currentIndex) {
        LatLng previousLocation;
        if (size > 1 && currentIndex > 1) {
            previousLocation = (routes.get(currentIndex - 1).getRoutePoints());
        }
        else {
            previousLocation = (routes.get(currentIndex).getRoutePoints());
        }
        return previousLocation;
    }

    private void setupMapView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this.getActivity());
        mapView.getMapAsync(this);
        mapView.onResume();
    }

    private void setDataToInfoFragmentFields() {
        String distance         = UtilMethods.formatFloat(distTravelled) + " " + getString(R.string.distance_prefix);
        String avgFuelCons      = UtilMethods.formatFloat(fuelConsumed) + " " + getString(R.string.fuel_prefix);
        String fuelSpent        = UtilMethods.formatFloat(this.fuelSpent) + " " + getString(R.string.fuel_prefix);
        String avgSpeed         = UtilMethods.formatFloat(this.avgSpeed) + " " + getString(R.string.speed_prefix);
        String moneyOnFuelSpent = UtilMethods.formatFloat(this.moneySpent) + " " + getString(R.string.currency_prefix);

        for (String value : speedValues) {
            if (Float.valueOf(value) == 0) {
                timeSpentOnStop++;
            }
        }
        timeSpentInMotion = timeSpent - timeSpentOnStop;

        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "setDataToInfoFragmentFields: TIME All+" + timeSpent);
            Log.d(LOG_TAG, "setDataToInfoFragmentFields: TIME in Motion+" + timeSpentInMotion);
            Log.d(LOG_TAG, "setDataToInfoFragmentFields: TIME On stop+" + timeSpentOnStop);
        }

        tripDistanceTravelledView.setText(distance);
        tripDateView.setText(tripDate);
        tripAvgFuelConsumptionView.setText(avgFuelCons);
        tripAvgSpeedView.setText(avgSpeed);
        tripFuelSpentView.setText(fuelSpent);
        tripIdView.setText(String.valueOf(tripId));
        tripTimeSpentInMotionView.setText(MathUtils.getTimeInNormalFormat(timeSpentInMotion));
        tripTimeSpentOnStopView.setText(MathUtils.getTimeInNormalFormat(timeSpentOnStop));
        tripTimeSpentView.setText(MathUtils.getTimeInNormalFormat(timeSpent));
        moneySpentView.setText(moneyOnFuelSpent);
    }

    private void drawPathFromData() {
        if (routes != null) {
            for (int i = 0; i < routes.size(); i++) {
                LatLng currentLocation      = (routes.get(i).getRoutePoints());
                LatLng tempPreviousLocation = getPreviousLocation(routes.size(), i);
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "drawPathFromData: +" + routes.get(i).getSpeed());
                }
                MapUtilMethods.addPolylineDependsOnSpeed(googleMap, tempPreviousLocation, currentLocation, routes.get(i).getSpeed());
            }
            LatLng positionToAnimate = MapUtilMethods.getPositionForCamera(routes);
            MapUtilMethods.animateCamera(null, positionToAnimate, googleMap);
            routes = null;
        }
    }
}
