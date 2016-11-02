package com.example.aprokopenko.triphelper.gps_utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
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
  public static final boolean DEBUG = BuildConfig.DEBUG;

  private float maxSpeed = ConstantValues.START_VALUE;
  private Observer<Location> locationSubscriber;
  private Observer<Float> maxSpeedSubscriber;
  private Observer<Float> speedSubscriber;
  private Thread locationThread;
  private Observable<Location> locationObservable;
  private Observable<Float> maxSpeedObservable;
  private Observable<Float> speedObservable;

  public GpsHandler() {
    TripHelperApp.getApplicationComponent().injectInto(this);
    if (DEBUG) {
      Log.d(LOG_TAG, "GpsHandler: created,context - " + context);
      Log.d(LOG_TAG, "GpsHandler: created,locationManger - " + locationManager);
    }
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
    final float speed = CalculationUtils.getSpeedInKilometerPerHour(location.getSpeed());
    setupLocationObservable(location);
    getMaxSpeedAndSetupObservable(speed);
    setupSpeedObservable(speed);
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
  public void setLocationSubscriber(@NonNull final Subscriber<Location> locationSubscriber) {
    this.locationSubscriber = locationSubscriber;
  }

  /**
   * Method for adding a {@link Subscriber} for listening for {@link Float} maxSpeed.
   *
   * @param maxSpeedSubscriber {@link Subscriber}
   */
  public void setMaxSpeedSubscriber(@NonNull final Subscriber<Float> maxSpeedSubscriber) {
    this.maxSpeedSubscriber = maxSpeedSubscriber;
  }

  /**
   * Method for adding a {@link Subscriber} for listening for {@link Float} speed.
   *
   * @param speedSubscriber {@link Subscriber}
   */
  public void setSpeedSubscriber(@NonNull final Subscriber<Float> speedSubscriber) {
    this.speedSubscriber = speedSubscriber;
  }

  private void setupLocationObservable(@NonNull final Location location) {
    if (locationObservable == null) {
      locationObservable = Observable.create(new Observable.OnSubscribe<Location>() {
        @Override public void call(Subscriber<? super Location> sub) {
          sub.onNext(location);
        }
      });
      locationObservable.subscribe(locationSubscriber);
    } else {
      locationSubscriber.onNext(location);
    }
  }

  private void setupMaxSpeedObservable(final float maxSpeed) {
    if (maxSpeedObservable == null) {
      maxSpeedObservable = Observable.create(new Observable.OnSubscribe<Float>() {
        @Override public void call(Subscriber<? super Float> subscriber) {
          subscriber.onNext(maxSpeed);
        }
      });
      maxSpeedObservable.subscribe(maxSpeedSubscriber);
    } else {
      maxSpeedSubscriber.onNext(maxSpeed);
    }
  }

  private void setupSpeedObservable(final float speed) {
    if (speedObservable == null) {
      speedObservable = Observable.create(new Observable.OnSubscribe<Float>() {
        @Override public void call(final Subscriber<? super Float> sub) {
          sub.onNext(speed);
        }
      });
      speedObservable.observeOn(Schedulers.immediate()).subscribe(speedSubscriber);
    } else {
      speedSubscriber.onNext(speed);
    }
  }

  private void getMaxSpeedAndSetupObservable(final float speed) {
    maxSpeed = CalculationUtils.findMaxSpeed(speed, maxSpeed);
    setupMaxSpeedObservable(maxSpeed);
  }

  /**
   * Method for performing exit, interrupt threads and safe ending of all process.
   */
  public void performExit() {
    if (locationThread != null) {
      locationThread.interrupt();
      locationThread = null;
    }
    context = null;
    locationManager = null;
    locationSubscriber = null;
    speedSubscriber = null;
    maxSpeedSubscriber = null;
  }
}