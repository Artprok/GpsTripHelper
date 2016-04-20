package com.example.aprokopenko.triphelper.utils.util_methods;

import android.support.annotation.Nullable;
import android.location.Location;

import com.example.aprokopenko.triphelper.utils.settings.GoogleMapsSettings;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public class MapUtilMethods {

    public static void addPolyline(GoogleMap googleMap, LatLng prevLoc, LatLng curLoc) {
        googleMap.addPolyline(
                new PolylineOptions().add(prevLoc, curLoc).width(GoogleMapsSettings.polylineWidth).color(GoogleMapsSettings.polylineColor));
    }

    public static void animateCamera(@Nullable Location location, @Nullable LatLng position, GoogleMap googleMap) {
        if (location != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(GoogleMapsSettings.googleMapCameraZoom)                   // Sets the zoom
                    .bearing(GoogleMapsSettings.googleMapCameraBearing)                // Sets the orientation of the camera to east
                    .tilt(GoogleMapsSettings.googleMapCameraTilt)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        if (position != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position)      // Sets the center of the map to location user
                    .zoom(GoogleMapsSettings.googleMapCameraZoom)                              // Sets the zoom
                    .bearing(GoogleMapsSettings.googleMapCameraBearing)                        // Sets the orientation of the camera to east
                    .tilt(GoogleMapsSettings.googleMapCameraTilt)                              // Sets the tilt of the camera to 30 degrees
                    .build();                                                                  // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public static LatLng getPositionForCamera(ArrayList<LatLng> routes) {
        LatLng lastPoint;
        int    routeSize = routes.size();
        lastPoint = routes.get(routeSize-1);
        return lastPoint;
    }

    public static ArrayList<LatLng> unwrapRoute(ArrayList<String> latitudes, ArrayList<String> longitudes) {
        ArrayList<LatLng> route = new ArrayList<>();
        for (int i = 0; i < latitudes.size(); i++) {
            LatLng routePointCoordinates = new LatLng(Float.valueOf(latitudes.get(i)), Float.valueOf(longitudes.get(i)));
            route.add(routePointCoordinates);
        }
        return route;
    }
}
