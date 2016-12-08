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

/**
 * Class with {@link com.example.aprokopenko.triphelper.ui.fragment.MapFragment} utils.
 */
public class MapUtilMethods {
  private static final String LOG_TAG = "MapUtilMethods";
  private static final boolean DEBUG = BuildConfig.DEBUG;

  /**
   * Method for unwrap route from params.
   *
   * @param latitudes  {@link ArrayList<String>} list of latitudes
   * @param longitudes {@link ArrayList<String>} list of longitudes
   * @param speedArr   {@link ArrayList<String>} list of speed values
   * @return {@link ArrayList<Route>} list of {@link Route} built with provided params
   */
  public static ArrayList<Route> unwrapRoute(@NonNull final ArrayList<String> latitudes, @NonNull final ArrayList<String> longitudes, @NonNull final ArrayList<String> speedArr) {
    final ArrayList<Route> routes = new ArrayList<>();
    final int routeSize = latitudes.size();
    for (int i = 0; i < routeSize; i++) {
      routes.add(new Route(new LatLng(Float.valueOf(latitudes.get(i)), Float.valueOf(longitudes.get(i))), Float.valueOf(speedArr.get(i))));
    }
    return routes;
  }

  /**
   * Method for getting previous location. Needed to draw a route on map in appropriate way.
   *
   * @param routes       {@link ArrayList<Route>} list of {@link Route}
   * @param size         {@link Integer} size of route to draw, how many points it has
   * @param currentIndex {@link Integer} current node of route on map
   * @return {@link LatLng} previous node of route on map in coordinates
   */
  public static LatLng getPreviousLocation(@NonNull final ArrayList<Route> routes, final int size, final int currentIndex) {
    if (size > 1 && currentIndex > 1) {
      return (routes.get(currentIndex - 1).getRoutePoints());
    } else {
      return (routes.get(currentIndex).getRoutePoints());
    }
  }

  /**
   * Method for drawing route (path) on map from given data.
   *
   * @param routes    {@link ArrayList<Route>} list of {@link Route} to get positions of path nodes
   * @param googleMap {@link GoogleMap} map itself
   * @return {@link Boolean} boolean value for approve that path is drawn
   */
  public static boolean drawPathFromData(@Nullable final ArrayList<Route> routes, @NonNull final GoogleMap googleMap) {
    if (routes != null) {
      final int routeSize = routes.size();
      for (int i = 0; i < routeSize; i++) {
        if (DEBUG) {
          Log.d(LOG_TAG, "drawPathFromDataItem: +" + routes.get(i).getSpeed());
        }
        MapUtilMethods.addPolylineDependsOnSpeed(googleMap, MapUtilMethods.getPreviousLocation(routes, routes.size(), i), (routes.get(i).getRoutePoints()), routes.get(i).getSpeed());
      }
      MapUtilMethods.animateCamera(null, MapUtilMethods.getPositionForCamera(routes), googleMap);
      return true;
    } else return false;
  }


  /**
   * Method for animate camera motion to current location.
   *
   * @param location  {@link Location} location
   * @param position  {@link LatLng} position to move in coordinates
   * @param googleMap {@link GoogleMap} map itself
   */
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

  /**
   * Method that draw path in appropriate color depends on speed.
   *
   * @param googleMap {@link GoogleMap} a map itself
   * @param prevLoc   {@link LatLng} previous location in coordinates
   * @param curLoc    {@link LatLng} current location in coordinates
   * @param speed     {@link Float} a speed to choose color of drawing
   */
  public static void addPolylineDependsOnSpeed(@NonNull final GoogleMap googleMap, @NonNull final LatLng prevLoc, @NonNull final LatLng curLoc, @Nullable final Float speed) {
    int color = Color.BLACK;
    if (speed != null) {
      color = choseColorDependOnSpeed(speed);
    }
    googleMap.addPolyline(new PolylineOptions().add(prevLoc, curLoc).width(GoogleMapsSettings.polylineWidth).color(color));
  }

  private static LatLng getPositionForCamera(@NonNull final ArrayList<Route> routes) {
    final int routeSize = routes.size();
    if (routeSize - 1 > 0) {
      return routes.get(routeSize - 1).getRoutePoints();
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