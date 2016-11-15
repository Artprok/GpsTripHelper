package com.example.aprokopenko.triphelper.datamodel;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class for representing user driven route on map.
 */
public class Route implements Parcelable {
  private final LatLng routePoints;
  private final float speed;

  /**
   * Constructor for {@link Route}.
   *
   * @param routePoints {@link LatLng} coordinates of route.
   * @param speed       {@link Float} value of speed to draw route with appropriate color
   */
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

  /**
   * Method for getting coordinate of route in {@link LatLng}.
   * @return {@link LatLng} coordinates
   */
  public LatLng getRoutePoints() {
    return routePoints;
  }

  /**
   * Method for getting latitude value
   * @return {@link Long} value of latitude
   */
  public double getLatitude(){
    return routePoints.latitude;
  }

  /**
   * Method for getting longitude value
   * @return {@link Long} value of longitude
   */
  public double getLongitude(){
    return routePoints.longitude;
  }

  /**
   * Method for getting speed of {@link Route}.
   * @return {@link Float} speed
   */
  public float getSpeed() {
    return speed;
  }
}