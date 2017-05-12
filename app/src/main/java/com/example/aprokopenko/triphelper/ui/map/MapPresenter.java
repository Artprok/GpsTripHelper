package com.example.aprokopenko.triphelper.ui.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.datamodel.LocationEmittableItem;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.example.aprokopenko.triphelper.utils.util_methods.MapUtilMethods;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import rx.Subscriber;

public class MapPresenter implements MapContract.UserActionListener {
    private MapContract.View view;
    private static final String LOG_TAG = "MAP_FRAGMENT";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String ROUTES = "routes";
    private static final String LOCATIONS = "locations";
    private static final String GPS_HANDLER = "gpsHandler";

    private LatLng previousLocationFromData;
    private Subscriber<LocationEmittableItem> locationSubscriber;
    private LatLng previousLocation;
    private ArrayList<Location> locationList;
    private GoogleMap googleMap;
    private Context context;
    private ArrayList<Route> routes;
    private GpsHandler gpsHandler;

    private boolean fragmentVisible;


    public MapPresenter(@NonNull final MapContract.View view) {
        this.view = view;
        view.setMapPresenter(this);
    }

    @Override
    public void onCreate() {
        locationList = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        if (locationSubscriber != null) {
            locationSubscriber.unsubscribe();
        }
    }

    @Override
    public void onDetach() {
        gpsHandler = null;
    }

    @Override
    public Bundle onSaveInstaceState() {
        final Bundle outState = new Bundle();
        outState.putParcelableArrayList(ROUTES, routes);
        outState.putParcelableArrayList(LOCATIONS, locationList);
        outState.putParcelable(GPS_HANDLER, gpsHandler);
        return outState;
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        routes = bundle.getParcelableArrayList(ROUTES);
        locationList = bundle.getParcelableArrayList(LOCATIONS);
        gpsHandler = bundle.getParcelable(GPS_HANDLER);
    }

    @Override
    public void onResume() {
        fragmentVisible = true;
        if (locationSubscriber == null && gpsHandler != null) {
            setupLocationSubscriber();
            setSubscribersToGpsHandler(gpsHandler);
        }
    }

    @Override
    public void onRoutesSet(ArrayList<Route> routes) {
        this.routes = routes;
    }

    @Override
    public void onGpsHandlerSet(GpsHandler gpsHandler) {
        if (this.gpsHandler == null) {
            this.gpsHandler = gpsHandler;
            if (DEBUG) {
                Log.d(LOG_TAG, "setGpsHandler: LocationStartTracking");
            }
            setupLocationSubscriber();
            setSubscribersToGpsHandler(gpsHandler);
        }
    }

    @Override
    public void locationTracking(GoogleMap googleMap, Location location, float speed) {
        drawPathFromData();
        previousLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MapUtilMethods.addPolylineDependsOnSpeed(googleMap, getStartingPosition(location), previousLocation, speed);
        MapUtilMethods.animateCamera(location, null, googleMap);
    }

    private void setPreviousLocationPoint(@NonNull final LatLng previousLocationPoint) {
        previousLocationFromData = previousLocationPoint;
    }

    private void drawPathFromData() {
        if (routes != null) {
            if (DEBUG) {
                Log.d(LOG_TAG, "drawPathFromData: DrawFromData");
            }

            for (int routeIndex = 0; routeIndex < routes.size(); routeIndex++) {
                final LatLng currentLocation = (routes.get(routeIndex).getRoutePoints());
                setPreviousLocationPoint(currentLocation);
                MapUtilMethods.addPolylineDependsOnSpeed(googleMap, MapUtilMethods.getPreviousLocation(routes, routes.size(), routeIndex), currentLocation, routes.get(routeIndex).getSpeed());
            }
            routes = null;
        }
    }

    private LatLng getStartingPosition(@NonNull final Location location) {
        final LatLng previousLoc;

        if (previousLocationFromData != null) {
            previousLoc = previousLocationFromData;
            previousLocationFromData = null;
        } else if (previousLocation == null) {
            previousLoc = new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            previousLoc = previousLocation;
        }
        return previousLoc;
    }

    private void setSubscribersToGpsHandler(@NonNull final GpsHandler GpsHandler) {
        GpsHandler.setLocationSubscriber(locationSubscriber);
    }

    private void setupLocationSubscriber() {
        locationSubscriber = new Subscriber<LocationEmittableItem>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(@NonNull final Throwable e) {
            }

            @Override
            public void onNext(@NonNull final LocationEmittableItem locationEmittableItem) {
                locationList.add(locationEmittableItem.getLocation());
                if (fragmentVisible) {
                    final float speed;
                    if (BuildConfig.DEBUG) {
                        speed = UtilMethods.generateRandomSpeed();
                    } else {
                        speed = locationEmittableItem.getSpeed();
                    }

                    view.onNewLocation(locationEmittableItem, speed);
                }
            }
        };
    }
}
