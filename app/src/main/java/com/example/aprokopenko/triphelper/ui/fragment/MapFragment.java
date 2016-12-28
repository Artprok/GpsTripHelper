package com.example.aprokopenko.triphelper.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.datamodel.LocationEmittableItem;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.example.aprokopenko.triphelper.utils.util_methods.MapUtilMethods;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import javax.inject.Singleton;

import rx.Subscriber;

/**
 * {@link android.support.v4.app.Fragment} responsible for showing map with driven routes.
 */
@Singleton
public class MapFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {
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

  public MapFragment() {
    // Required empty public constructor
  }

  public static MapFragment newInstance() {
    return new MapFragment();
  }

  public void setGpsHandler(@NonNull final GpsHandler gpsHandler) {
    if (this.gpsHandler == null) {
      this.gpsHandler = gpsHandler;
      if (DEBUG) {
        Log.d(LOG_TAG, "setGpsHandler: LocationStartTracking");
      }
      setupSubscribers();
      setSubscribersToGpsHandler(gpsHandler);
    }
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    locationList = new ArrayList<>();
    if (context == null) {
      context = getActivity();
    }
  }

  @Override public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_map, container, false);
  }

  @Override public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    getGoogleMap(this);
    fragmentVisible = true;
  }

  @Override public void onMapReady(@NonNull final GoogleMap googleMap) {
    final UiSettings uiSettings = googleMap.getUiSettings();

    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    UtilMethods.isPermissionAllowed(context);
    uiSettings.setMyLocationButtonEnabled(true);
    uiSettings.setZoomControlsEnabled(true);
    uiSettings.setAllGesturesEnabled(true);
    uiSettings.setMapToolbarEnabled(true);
    uiSettings.setCompassEnabled(true);
    if (UtilMethods.isPermissionAllowed(context)) {
      googleMap.setMyLocationEnabled(true);
    }
    this.googleMap = googleMap;
  }

  @Override public void onDestroyView() {
    final Fragment fragment = getChildFragmentManager().findFragmentById(R.id.mapFragment);

    if (locationSubscriber != null) {
      locationSubscriber.unsubscribe();
    }
    if (fragment != null) {
      getChildFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
    }
    super.onDestroyView();
  }

  @Override public void onDetach() {
    gpsHandler = null;
    context = null;
    super.onDetach();
  }

  @Override public void onSaveInstanceState(@NonNull final Bundle outState) {
    outState.putParcelableArrayList(ROUTES, routes);
    outState.putParcelableArrayList(LOCATIONS, locationList);
    outState.putParcelable(GPS_HANDLER, gpsHandler);
    super.onSaveInstanceState(outState);
  }

  @Override public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState != null) {
      routes = savedInstanceState.getParcelableArrayList(ROUTES);
      locationList = savedInstanceState.getParcelableArrayList(LOCATIONS);
      gpsHandler = savedInstanceState.getParcelable(GPS_HANDLER);
    }
  }

  @Override public void onResume() {
    if (locationSubscriber == null && gpsHandler != null) {
      setupSubscribers();
      setSubscribersToGpsHandler(gpsHandler);
    }
    super.onResume();
  }

  @Override public void onAttach(@NonNull final Context context) {
    super.onAttach(context);
    this.context = context;
  }

  public void setRoutes(@NonNull final ArrayList<Route> routes) {
    this.routes = routes;
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

  private void setPreviousLocationPoint(@NonNull final LatLng previousLocationPoint) {
    previousLocationFromData = previousLocationPoint;
  }

  private void setSubscribersToGpsHandler(@NonNull final GpsHandler GpsHandler) {
    GpsHandler.setLocationSubscriber(locationSubscriber);
  }

  private void locationTracking(@NonNull final GoogleMap googleMap, @NonNull final Location location, @NonNull final Float speed) {
    drawPathFromData();
    previousLocation = new LatLng(location.getLatitude(), location.getLongitude());
    MapUtilMethods.addPolylineDependsOnSpeed(googleMap, getStartingPosition(location), previousLocation, speed);
  }

  private void setupSubscribers() {
    setupLocationSubscriber();
  }

  private void setupLocationSubscriber() {
    locationSubscriber = new Subscriber<LocationEmittableItem>() {
      @Override public void onCompleted() {
      }

      @Override public void onError(@NonNull final Throwable e) {
      }

      @Override public void onNext(@NonNull final LocationEmittableItem locationEmittableItem) {
        locationList.add(locationEmittableItem.getLocation());
        if (fragmentVisible) {
          final float speed;
          if (BuildConfig.DEBUG) {
            speed = UtilMethods.generateRandomSpeed();
          } else {
            speed = locationEmittableItem.getSpeed();
          }

          final Activity activity = getActivity();
          if (activity != null) {
            activity.runOnUiThread(new Runnable() {
              @Override public void run() {
                locationTracking(googleMap, locationEmittableItem.getLocation(), speed);
                MapUtilMethods.animateCamera(locationEmittableItem.getLocation(), null, googleMap);
              }
            });
          }
        }
      }
    };
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

  private static void getGoogleMap(@NonNull final MapFragment fragment) {
    final SupportMapFragment mapFragment = (SupportMapFragment) fragment.getChildFragmentManager().findFragmentById(R.id.mapFragment);
    mapFragment.getMapAsync(fragment);
  }
}