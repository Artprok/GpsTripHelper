package com.example.aprokopenko.triphelper.datamodel;

import com.google.android.gms.maps.model.LatLng;

public class Route {
    private final LatLng routePoints;
    private final float  speed;

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
