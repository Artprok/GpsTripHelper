package com.example.aprokopenko.triphelper;

import com.google.android.gms.maps.model.LatLng;

public class Route {
    private LatLng routePoints;
    private float  speed;

    public Route(LatLng routePoints, float speed) {
        this.routePoints = routePoints;
        this.speed = speed;
    }

    public LatLng getRoutePoints() {
        return routePoints;
    }

    public float getSpeed() {
        return speed;
    }
}
