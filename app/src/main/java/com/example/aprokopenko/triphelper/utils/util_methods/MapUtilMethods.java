package com.example.aprokopenko.triphelper.utils.util_methods;

import android.support.annotation.Nullable;
import android.location.Location;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.settings.GoogleMapsSettings;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class MapUtilMethods {

    public static final String LOG_TAG = "MapUtilMethods";

    public static ArrayList<Route> unwrapRoute(ArrayList<String> latitudes, ArrayList<String> longitudes, ArrayList<String> speedArr) {
        ArrayList<Route> route = new ArrayList<>();
        Float            speed;
        Route            routePoint;
        for (int i = 0; i < latitudes.size(); i++) {
            LatLng routePointCoordinates = new LatLng(Float.valueOf(latitudes.get(i)), Float.valueOf(longitudes.get(i)));
            speed = Float.valueOf(speedArr.get(i));
            Log.d("TIME", "unwrapRoute: " + speed);
            routePoint = new Route(routePointCoordinates, speed);
            route.add(routePoint);
        }
        return route;
    }

    public static void addPolylineDependsOnSpeed(GoogleMap googleMap, LatLng prevLoc, LatLng curLoc, float speed) {
        int color = choseColorDependOnSpeed(speed);
        googleMap.addPolyline(new PolylineOptions().add(prevLoc, curLoc).width(GoogleMapsSettings.polylineWidth).color(color));
    }

    public static void animateCamera(@Nullable Location location, @Nullable LatLng position, GoogleMap googleMap) {
        if (location != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))     // Sets the center of the map to location user
                    .zoom(GoogleMapsSettings.googleMapCameraZoom)                            // Sets the zoom
                    .bearing(GoogleMapsSettings.googleMapCameraBearing)                      // Sets the orientation of the camera to east
                    .tilt(GoogleMapsSettings.googleMapCameraTilt)                            // Sets the tilt of the camera to 30 degrees
                    .build();                                                                // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        if (position != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position)                                                       // Sets the center of the map to location user
                    .zoom(GoogleMapsSettings.googleMapCameraZoom)                           // Sets the zoom
                    .bearing(GoogleMapsSettings.googleMapCameraBearing)                     // Sets the orientation of the camera to east
                    .tilt(GoogleMapsSettings.googleMapCameraTilt)                           // Sets the tilt of the camera to 30 degrees
                    .build();                                                               // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public static LatLng getPreviousLocation(ArrayList<Route> routes, int size, int currentIndex) {
        LatLng previousLocation;
        if (size > 1 && currentIndex > 1) {
            previousLocation = (routes.get(currentIndex - 1).getRoutePoints());
        }
        else {
            previousLocation = (routes.get(currentIndex).getRoutePoints());
        }
        return previousLocation;
    }

    public static boolean drawPathFromData(ArrayList<Route> routes, GoogleMap googleMap) {
        boolean result = false;
        if (routes != null) {
            for (int i = 0; i < routes.size(); i++) {
                LatLng currentLocation      = (routes.get(i).getRoutePoints());
                LatLng tempPreviousLocation = MapUtilMethods.getPreviousLocation(routes, routes.size(), i);
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "drawPathFromData: +" + routes.get(i).getSpeed());
                }
                MapUtilMethods.addPolylineDependsOnSpeed(googleMap, tempPreviousLocation, currentLocation, routes.get(i).getSpeed());
            }
            LatLng positionToAnimate = MapUtilMethods.getPositionForCamera(routes);
            MapUtilMethods.animateCamera(null, positionToAnimate, googleMap);
            result = true;
        }
        return result;
    }


    private static LatLng getPositionForCamera(ArrayList<Route> routes) {
        LatLng lastPoint;
        int    routeSize = routes.size();
        int    index     = routeSize - 1;
        if (index > 0) {
            lastPoint = routes.get(routeSize - 1).getRoutePoints();
            return lastPoint;
        }
        else {
            return ConstantValues.BERMUDA_COORDINATES;
        }
    }

    private static int choseColorDependOnSpeed(float speed) {
        int color = 0;
        if (speed >= 0 && speed < 80) {
            color = GoogleMapsSettings.polylineColorCity;
        }
        else if (speed > 80 && speed < 110) {
            color = GoogleMapsSettings.polylineColorOutOfCity;
        }
        else if (speed > 110) {
            color = GoogleMapsSettings.polylineColorOutOfMaxSpeedAllowedINT;
        }
        return color;
    }
}
