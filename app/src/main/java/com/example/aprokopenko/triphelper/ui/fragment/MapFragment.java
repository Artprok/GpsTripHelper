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

import com.example.aprokopenko.triphelper.Route;
import com.example.aprokopenko.triphelper.utils.util_methods.MapUtilMethods;
import com.example.aprokopenko.triphelper.utils.util_methods.MathUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.example.aprokopenko.triphelper.R;

import java.util.ArrayList;

import javax.crypto.spec.DESedeKeySpec;

import butterknife.ButterKnife;
import rx.Subscriber;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String LOG_TAG = "MAP_FRAGMENT";
    private LatLng               previousLocationFromData;
    private Subscriber<Location> locationSubscriber;
    private LatLng               previousLocation;
    private ArrayList<Location>  locationList;
    private boolean fragmentVisible = false;
    private GoogleMap        googleMap;
    private Context          context;
    private ArrayList<Route> route;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public void setGpsHandler(GpsHandler GpsHandler) {
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "setGpsHandler: LocationStartTracking");
        }
        setupSubscribers();
        setSubscribersToGpsHandler(GpsHandler);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationList = new ArrayList<>();
        ButterKnife.bind(this, view);
        getGoogleMap();
        context = getActivity();
        fragmentVisible = true;
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        UtilMethods.checkPermission(context);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        this.googleMap = googleMap;
    }

    @Override public void onDetach() {
        super.onDetach();
        ButterKnife.unbind(this);
        context = null;
    }

    public void setRoute(ArrayList<Route> route) {
        this.route = route;
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

    private LatLng getPreviousLocation(int size, int currentIndex) {
        LatLng previousLocation;
        if (size > 1 && currentIndex > 1) {
            previousLocation = (route.get(currentIndex - 1).getRoutePoints());
        }
        else {
            previousLocation = (route.get(currentIndex).getRoutePoints());
        }
        return previousLocation;
    }


    private void getGoogleMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setSubscribersToGpsHandler(GpsHandler GpsHandler) {
        GpsHandler.setLocationSubscriber(locationSubscriber);
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

            @Override public void onNext(Location location) {
                locationList.add(location);
                if (fragmentVisible) {
                    UtilMethods.checkPermission(context);
                    // FIXME: 20.04.2016 DEBUG 101f
                    if (ConstantValues.DEBUG_MODE) {
                        locationTracking(googleMap, location, 101f);
                    }
                    else {
                        float speed = MathUtils.getSpeedInKilometerPerHour(location.getSpeed());
                        locationTracking(googleMap, location, speed);
                    }
                    MapUtilMethods.animateCamera(location, null, googleMap);
                }
            }
        };
    }

    private void locationTracking(GoogleMap googleMap, Location location, @Nullable Float speed) {
        drawPathFromData();
        LatLng tempPreviousLocation = getStartingPosition(location);
        LatLng tempLocation         = new LatLng(location.getLatitude(), location.getLongitude());
        previousLocation = tempLocation;
        MapUtilMethods.addPolylineDependsOnSpeed(googleMap, tempPreviousLocation, tempLocation, speed);
    }

    private void drawPathFromData() {
        if (route != null) {
            if (ConstantValues.DEBUG_MODE) {
                Log.d(LOG_TAG, "drawPathFromData: DrawFromData");
            }
            for (int i = 0; i < route.size(); i++) {
                LatLng currentLocation = (route.get(i).getRoutePoints());
                Log.d(LOG_TAG, "drawPathFromData: AAAAA" + route.get(0).getSpeed());
                // FIXME: 20.04.2016 colorHereChange
                LatLng tempPreviousLocation = getPreviousLocation(route.size(), i);
                previousLocationFromData = currentLocation;
                if (ConstantValues.DEBUG_MODE) {
                    MapUtilMethods.addPolylineDependsOnSpeed(googleMap, tempPreviousLocation, currentLocation, route.get(i).getSpeed());
                }
                else {
                    MapUtilMethods.addPolylineDependsOnSpeed(googleMap, tempPreviousLocation, currentLocation, route.get(i).getSpeed());
                }
            }
            route = null;
        }
    }
}







