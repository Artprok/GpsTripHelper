package com.example.aprokopenko.triphelper.ui.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.location.Location;
import android.content.Context;
import android.view.ViewGroup;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.MapUtilMethods;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.example.aprokopenko.triphelper.R;


import java.util.ArrayList;

import rx.Subscriber;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String LOG_TAG = "MAP_FRAGMENT";
    private LatLng               previousLocationFromData;
    private Subscriber<Location> locationSubscriber;
    private LatLng               previousLocation;
    private ArrayList<Location>  locationList;
    private GoogleMap            googleMap;
    private Context              context;
    private ArrayList<Route>     routes;
    private GpsHandler           gpsHandler;

    private boolean fragmentVisible;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public void setGpsHandler(GpsHandler gpsHandler) {
        if (this.gpsHandler == null) {
            this.gpsHandler = gpsHandler;
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "setGpsHandler: LocationStartTracking");
            }
            setupSubscribers();
            setSubscribersToGpsHandler(gpsHandler);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationList = new ArrayList<>();
        getGoogleMap();
        if (context == null) {
            context = getActivity();
        }
        fragmentVisible = true;
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        UtilMethods.isPermissionAllowed(context);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        if (UtilMethods.isPermissionAllowed(context)) {
            googleMap.setMyLocationEnabled(true);
        }
        this.googleMap = googleMap;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (locationSubscriber != null) {
            locationSubscriber.unsubscribe();
        }
        Fragment f = getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (f != null) {
            getChildFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    @Override public void onDetach() {
        super.onDetach();
        gpsHandler = null;
        context = null;
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("routes", routes);
        outState.putParcelableArrayList("locations", locationList);
        outState.putParcelable("gpsHandler", gpsHandler);
        super.onSaveInstanceState(outState);
    }

    @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            routes = savedInstanceState.getParcelableArrayList("routes");
            locationList = savedInstanceState.getParcelableArrayList("locations");
            gpsHandler = savedInstanceState.getParcelable("gpsHandler");
        }
    }

    @Override public void onResume() {
        if (locationSubscriber == null && gpsHandler != null) {
            setupSubscribers();
            setSubscribersToGpsHandler(gpsHandler);
        }
        super.onResume();
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    private LatLng getStartingPosition(Location location) {
        LatLng previousLoc;
        if (previousLocationFromData != null) {
            previousLoc = previousLocationFromData;
            previousLocationFromData = null;
        }
        else if (previousLocation == null) {
            previousLoc = new LatLng(location.getLatitude(), location.getLongitude());
        }
        else {
            previousLoc = previousLocation;
        }
        return previousLoc;
    }

    private void setPreviousLocationPoint(LatLng previousLocationPoint) {
        previousLocationFromData = previousLocationPoint;
    }

    private void setSubscribersToGpsHandler(GpsHandler GpsHandler) {
        GpsHandler.setLocationSubscriber(locationSubscriber);
    }

    private void locationTracking(GoogleMap googleMap, Location location, @Nullable Float speed) {
        drawPathFromData();
        LatLng tempPreviousLocation = getStartingPosition(location);
        LatLng tempLocation         = new LatLng(location.getLatitude(), location.getLongitude());
        previousLocation = tempLocation;
        MapUtilMethods.addPolylineDependsOnSpeed(googleMap, tempPreviousLocation, tempLocation, speed);
    }

    private void setupSubscribers() {
        setupLocationSubscriber();
    }

    private void setupLocationSubscriber() {
        locationSubscriber = new Subscriber<Location>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(final Location location) {
                locationList.add(location);
                if (fragmentVisible) {
                    final float speed;
                    if (ConstantValues.DEBUG_MODE) {
                        speed = UtilMethods.generateRandomSpeed();
                    }

                    else {
                        speed = CalculationUtils.getSpeedInKilometerPerHour(location.getSpeed());
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override public void run() {
                            locationTracking(googleMap, location, speed);
                            MapUtilMethods.animateCamera(location, null, googleMap);
                        }
                    });
                }
            }
        };
    }

    private void drawPathFromData() {
        if (routes != null) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.d(LOG_TAG, "drawPathFromData: DrawFromData");
            }
            for (int i = 0; i < routes.size(); i++) {
                LatLng currentLocation      = (routes.get(i).getRoutePoints());
                LatLng tempPreviousLocation = MapUtilMethods.getPreviousLocation(routes, routes.size(), i);
                setPreviousLocationPoint(currentLocation);
                if (ConstantValues.DEBUG_MODE) {
                    MapUtilMethods.addPolylineDependsOnSpeed(googleMap, tempPreviousLocation, currentLocation, routes.get(i).getSpeed());
                }
                else {
                    MapUtilMethods.addPolylineDependsOnSpeed(googleMap, tempPreviousLocation, currentLocation, routes.get(i).getSpeed());
                }
            }
            routes = null;
        }
    }

    private void getGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }
}







