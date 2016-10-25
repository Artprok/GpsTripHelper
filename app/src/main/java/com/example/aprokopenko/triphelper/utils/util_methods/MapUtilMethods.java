package com.example.aprokopenko.triphelper.utils.util_methods;

import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.settings.GoogleMapsSettings;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapUtilMethods {

  private static final String LOG_TAG = "MapUtilMethods";
  public static final boolean DEBUG = BuildConfig.DEBUG;

  public static ArrayList<Route> unwrapRoute(@NonNull final ArrayList<String> latitudes, @NonNull final ArrayList<String> longitudes, @NonNull final ArrayList<String> speedArr) {
    final ArrayList<Route> route = new ArrayList<>();
    Float speed;
    Route routePoint;
    for (int i = 0; i < latitudes.size(); i++) {
      final LatLng routePointCoordinates = new LatLng(Float.valueOf(latitudes.get(i)), Float.valueOf(longitudes.get(i)));
      speed = Float.valueOf(speedArr.get(i));
      routePoint = new Route(routePointCoordinates, speed);
      route.add(routePoint);
    }
    return route;
  }

  public static LatLng getPreviousLocation(@NonNull final ArrayList<Route> routes, final int size, final int currentIndex) {
    final LatLng previousLocation;
    if (size > 1 && currentIndex > 1) {
      previousLocation = (routes.get(currentIndex - 1).getRoutePoints());
    } else {
      previousLocation = (routes.get(currentIndex).getRoutePoints());
    }
    return previousLocation;
  }

  public static boolean drawPathFromData(@Nullable final ArrayList<Route> routes, @NonNull final GoogleMap googleMap) {
    if (routes != null) {
      for (int i = 0; i < routes.size(); i++) {
        final LatLng currentLocation = (routes.get(i).getRoutePoints());
        final LatLng tempPreviousLocation = MapUtilMethods.getPreviousLocation(routes, routes.size(), i);
        if (DEBUG) {
          Log.d(LOG_TAG, "drawPathFromDataItem: +" + routes.get(i).getSpeed());
        }
        MapUtilMethods.addPolylineDependsOnSpeed(googleMap, tempPreviousLocation, currentLocation, routes.get(i).getSpeed());
      }
      final LatLng positionToAnimate = MapUtilMethods.getPositionForCamera(routes);
      MapUtilMethods.animateCamera(null, positionToAnimate, googleMap);
      return true;
    } else return false;
  }


  public static void animateCamera(@Nullable final Location location, @Nullable final LatLng position, @NonNull final GoogleMap googleMap) {
    if (location != null) {
      final CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(),
              location.getLongitude()))     // Sets the center of the map_icon to location user
              .zoom(GoogleMapsSettings.googleMapCameraZoom)                            // Sets the zoom
              .bearing(GoogleMapsSettings.googleMapCameraBearing)                      // Sets the orientation of the camera to east
              .tilt(GoogleMapsSettings.googleMapCameraTilt)                            // Sets the tilt of the camera to 30 degrees
              .build();                                                                // Creates a CameraPosition from the builder
      googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    if (position != null) {
      final CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(position)                                                       // Sets the center of the map_icon to
              // location user
              .zoom(GoogleMapsSettings.googleMapCameraZoom)                           // Sets the zoom
              .bearing(GoogleMapsSettings.googleMapCameraBearing)                     // Sets the orientation of the camera to east
              .tilt(GoogleMapsSettings.googleMapCameraTilt)                           // Sets the tilt of the camera to 30 degrees
              .build();                                                               // Creates a CameraPosition from the builder
      googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
  }

  public static void addPolylineDependsOnSpeed(@NonNull final GoogleMap googleMap, @NonNull final LatLng prevLoc, @NonNull final LatLng curLoc, @Nullable final Float speed) {
    int color = Color.BLACK;
    if (speed != null) {
      color = choseColorDependOnSpeed(speed);
    }
    googleMap.addPolyline(new PolylineOptions().add(prevLoc, curLoc).width(GoogleMapsSettings.polylineWidth).color(color));
  }

  private static LatLng getPositionForCamera(@NonNull final ArrayList<Route> routes) {
    final LatLng lastPoint;
    final int routeSize = routes.size();
    final int index = routeSize - 1;
    if (index > 0) {
      lastPoint = routes.get(routeSize - 1).getRoutePoints();
      return lastPoint;
    } else {
      return ConstantValues.BERMUDA_COORDINATES;
    }
  }

  private static int choseColorDependOnSpeed(final float speed) {
    int color = 0;
    if (speed >= 0 && speed < ConstantValues.CITY_SPEED_LIMIT) {
      color = GoogleMapsSettings.polylineColorCity;
    } else if (speed > ConstantValues.CITY_SPEED_LIMIT && speed < ConstantValues.OUTCITY_SPEED_LIMIT) {
      color = GoogleMapsSettings.polylineColorOutOfCity;
    } else if (speed > ConstantValues.OUTCITY_SPEED_LIMIT) {
      color = GoogleMapsSettings.polylineColorOutOfMaxSpeedAllowed;
    }
    return color;
  }
}