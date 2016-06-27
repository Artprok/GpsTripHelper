package com.example.aprokopenko.triphelper.datamodel;

import android.os.Parcelable;
import android.os.Parcel;

import com.google.android.gms.maps.model.LatLng;


public class Route implements Parcelable {
    private final LatLng routePoints;
    private final float  speed;

    public Route(LatLng routePoints, float speed) {
        this.routePoints = routePoints;
        this.speed = speed;
    }

    private Route(Parcel in) {
        routePoints = in.readParcelable(LatLng.class.getClassLoader());
        speed = in.readFloat();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(routePoints, flags);
        dest.writeFloat(speed);
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    public LatLng getRoutePoints() {
        return routePoints;
    }

    public float getSpeed() {
        return speed;
    }


}
