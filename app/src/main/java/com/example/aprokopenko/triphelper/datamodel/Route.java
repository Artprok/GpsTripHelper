package com.example.aprokopenko.triphelper.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class Route implements Parcelable {
  private final LatLng routePoints;
  private final float speed;

  public Route(@NonNull final LatLng routePoints, final float speed) {
    this.routePoints = routePoints;
    this.speed = speed;
  }

  private Route(@NonNull final Parcel in) {
    routePoints = in.readParcelable(LatLng.class.getClassLoader());
    speed = in.readFloat();
  }

  @Override public void writeToParcel(@NonNull final Parcel dest, final int flags) {
    dest.writeParcelable(routePoints, flags);
    dest.writeFloat(speed);
  }

  @Override public int describeContents() {
    return 0;
  }

  public static final Creator<Route> CREATOR = new Creator<Route>() {
    @Override public Route createFromParcel(@NonNull final Parcel in) {
      return new Route(in);
    }

    @Override public Route[] newArray(final int size) {
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