package com.example.aprokopenko.triphelper.ui.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.content.Context;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.os.Bundle;

import com.example.aprokopenko.triphelper.listener.OnListFragmentInteractionListener;
import com.example.aprokopenko.triphelper.utils.util_methods.MapUtilMethods;
import com.example.aprokopenko.triphelper.utils.util_methods.MathUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
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

    private float  timeSpentInMotion;
    private float  timeSpentOnStop;
    private float  distTravelled;
    private float  fuelConsumed;
    private float  fuelSpent;
    private float  timeSpent;
    private String tripDate;
    private float  avgSpeed;
    private int    tripId;

    private OnListFragmentInteractionListener mListener;
    private GoogleMap                         googleMap;
    private Context                           context;
    private ArrayList<LatLng>                 routes;

    public TripInfoFragment() {
    }

    public static TripInfoFragment newInstance(String tripDate, float distTravelled, float avgSpeed, float timeSpent,
                                               float timeSpentInMotion, float timeSpentOnStop, float fuelConsumed, float fuelSpent,
                                               int tripId, ArrayList<LatLng> routes) {
        TripInfoFragment  fragment       = new TripInfoFragment();
        Bundle            args           = new Bundle();
        ArrayList<String> latitudeArray  = new ArrayList<>();
        ArrayList<String> longitudeArray = new ArrayList<>();
        for (LatLng tmpLatLang : routes) {
            // FIXME: 12.04.2016 ToString?? :(
            latitudeArray.add(String.valueOf(tmpLatLang.latitude));
            longitudeArray.add(String.valueOf(tmpLatLang.longitude));
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

            routes = MapUtilMethods.unwrapRoute(latitudeArray, longitudeArray);
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

    @Override public void onDetach() {
        super.onDetach();
        ButterKnife.unbind(this);
        mListener = null;
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
            previousLocation = (routes.get(currentIndex - 1));
        }
        else {
            previousLocation = (routes.get(currentIndex));
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
        String distance    = (UtilMethods.formatFloat(distTravelled) + " " + getString(R.string.distance_prefix));
        String avgFuelCons = UtilMethods.formatFloat((fuelConsumed)) + " " + getString(R.string.fuel_prefix);
        String fuelSpent   = UtilMethods.formatFloat(this.fuelSpent) + " " + getString(R.string.fuel_prefix);
        String avgSpeed    = UtilMethods.formatFloat(this.avgSpeed) + " " + getString(R.string.speed_prefix);

        tripDistanceTravelledView.setText(distance);
        tripDateView.setText(tripDate);
        tripAvgFuelConsumptionView.setText(avgFuelCons);
        tripAvgSpeedView.setText(String.valueOf(avgSpeed));
        tripFuelSpentView.setText(fuelSpent);
        tripIdView.setText(String.valueOf(tripId));
        tripTimeSpentInMotionView.setText(String.valueOf(timeSpentInMotion));
        tripTimeSpentOnStopView.setText(String.valueOf(timeSpentOnStop));
        tripTimeSpentView.setText(MathUtils.getTimeInNormalFormat(timeSpent));
    }

    private void drawPathFromData() {
        if (routes != null) {
            for (int i = 0; i < routes.size(); i++) {
                LatLng currentLocation      = (routes.get(i));
                LatLng tempPreviousLocation = getPreviousLocation(routes.size(), i);
                MapUtilMethods.addPolyline(googleMap, tempPreviousLocation, currentLocation);
            }
            LatLng positionToAnimate = MapUtilMethods.getPositionForCamera(routes);
            MapUtilMethods.animateCamera(null, positionToAnimate, googleMap);
            routes = null;
        }
    }
}
