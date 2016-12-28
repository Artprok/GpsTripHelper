package com.example.aprokopenko.triphelper.gps_utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.datamodel.LocationEmittableItem;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.google.android.gms.location.LocationListener;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Class responsible for work with GPS utils.
 */
public class GpsHandler implements LocationListener, Parcelable {
  @Inject
  LocationManager locationManager;
  @Inject
  Context context;

  private static final String LOG_TAG = "GPSHandler";
  private static final boolean DEBUG = BuildConfig.DEBUG;

  private Observable<LocationEmittableItem> locationObservable;
  private Observer<LocationEmittableItem> locationSubscriber;
  private LocationEmittableItem locationEmittableItem;
  private float maxSpeed = ConstantValues.START_VALUE;

  public GpsHandler() {
    TripHelperApp.getApplicationComponent().injectInto(this);
  }

  private GpsHandler(@NonNull final Parcel in) {
    maxSpeed = in.readFloat();
  }

  public static final Creator<GpsHandler> CREATOR = new Creator<GpsHandler>() {
    @Override public GpsHandler createFromParcel(@NonNull final Parcel in) {
      return new GpsHandler(in);
    }

    @Override public GpsHandler[] newArray(final int size) {
      return new GpsHandler[size];
    }
  };

  @Override public void onLocationChanged(@NonNull final Location location) {
    if (locationEmittableItem == null) {
      locationEmittableItem = new LocationEmittableItem(location);
    } else {
      locationEmittableItem.setLocation(location);
      locationEmittableItem.setSpeed(location.getSpeed());
      locationEmittableItem.setMaxSpeed(maxSpeed);
    }
    setupLocationObservable(locationEmittableItem);
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(@NonNull final Parcel parcel, final int i) {
    parcel.writeFloat(maxSpeed);
  }

  /**
   * Method for adding a {@link Subscriber} for listening for {@link Location}.
   *
   * @param locationSubscriber {@link Subscriber}
   */
  public void setLocationSubscriber(@NonNull final Subscriber<LocationEmittableItem> locationSubscriber) {
    this.locationSubscriber = locationSubscriber;
  }

  private void setupLocationObservable(@NonNull final LocationEmittableItem locationEmittableItem) {
    if (locationObservable == null) {
      locationObservable = Observable.create(new Observable.OnSubscribe<LocationEmittableItem>() {
        @Override public void call(@NonNull final Subscriber<? super LocationEmittableItem> sub) {
          sub.onNext(locationEmittableItem);
        }
      })
              .observeOn(Schedulers.immediate());
    } else {
      locationSubscriber.onNext(locationEmittableItem);
    }
  }

  /**
   * Method for performing exit, interrupt threads and safe ending of all process.
   */
  public void performExit() {
    context = null;
    locationManager = null;
    locationSubscriber = null;
  }
}
