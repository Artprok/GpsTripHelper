package com.example.aprokopenko.triphelper.gps_utils;

import android.location.LocationManager;
import android.location.Location;
import android.content.Context;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.listener.LocationListener;
import com.example.aprokopenko.triphelper.application.TripHelperApp;

import java.util.Random;

import javax.inject.Inject;

import rx.schedulers.Schedulers;
import rx.Subscriber;
import rx.Observable;
import rx.Observer;

public class GpsHandler implements LocationListener, com.google.android.gms.location.LocationListener {
    @Inject
    LocationManager locationManager;
    @Inject
    Context         context;

    public static final String LOG_TAG  = "GPSHandler";
    private             float  maxSpeed = ConstantValues.START_VALUE;
    private Observer<Location> locationSubscriber;
    private Observer<Float>    maxSpeedSubscriber;
    private Observer<Float>    speedSubscriber;
    Observable<Float> speedObservable;

    // FIXME: 14.04.2016 debug code remove
    private final float[] tempVal = {1};

    public GpsHandler() {
        TripHelperApp.getApplicationComponent().injectInto(this);
        if (ConstantValues.LOGGING_ENABLED) {
            Log.d(LOG_TAG, "GpsHandler: created,context - " + context);
            Log.d(LOG_TAG, "GpsHandler: created,locationManger - " + locationManager);
        }
    }

    public void setLocationSubscriber(Subscriber<Location> locationSubscriber) {
        this.locationSubscriber = locationSubscriber;
    }

    public void setMaxSpeedSubscriber(Subscriber<Float> maxSpeedSubscriber) {
        this.maxSpeedSubscriber = maxSpeedSubscriber;
    }

    public void setSpeedSubscriber(Subscriber<Float> speedSubscriber) {
        this.speedSubscriber = speedSubscriber;
    }


    private void setupLocationObservable(final Location location) {
        Observable<Location> locationObservable = Observable.create(new Observable.OnSubscribe<Location>() {
            @Override public void call(Subscriber<? super Location> sub) {
                sub.onNext(location);
            }
        });
        locationObservable.subscribe(locationSubscriber);
    }

    private void setupMaxSpeedObservable(final float maxSpeed) {
        Observable<Float> maxSpeedObservable = Observable.create(new Observable.OnSubscribe<Float>() {
            @Override public void call(Subscriber<? super Float> subscriber) {
                subscriber.onNext(maxSpeed);
            }
        });
        maxSpeedObservable.subscribe(maxSpeedSubscriber);
    }

    private void setupSpeedObservable(final float speed) {
        speedObservable = Observable.create(new Observable.OnSubscribe<Float>() {
            @Override public void call(Subscriber<? super Float> sub) {
                sub.onNext(speed);
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "call: On next! speedIn SetupObservable is" + speed);
                }
            }
        }).repeat();
        speedObservable.subscribeOn(Schedulers.computation()).observeOn(Schedulers.computation()).subscribe(speedSubscriber);
    }

    private void getMaxSpeedAndSetupObservable(float speed) {
        maxSpeed = CalculationUtils.findMaxSpeed(speed, maxSpeed);
        setupMaxSpeedObservable(maxSpeed);
    }

    @Override public void onLocationChanged(Location location) {
        // FIXME: 14.04.2016 debug code remove
        float speed;
        if (ConstantValues.DEBUG_MODE) {
            Random r = new Random();
            speed = 0 + tempVal[0];
            if (speed != 0) {
                tempVal[0] += 5;
            }
            if (speed > 70) {
                speed = r.nextInt(200);
            }
            speed = CalculationUtils.getSpeedInKilometerPerHour(speed);
        }
        else {
            speed = CalculationUtils.getSpeedInKilometerPerHour(location.getSpeed());
        }
        setupLocationObservable(location);
        getMaxSpeedAndSetupObservable(speed);
        setupSpeedObservable(speed);
    }

    @Override public void locationChanged(Location location) {
        // TODO: 13.05.2016 remove this method if all will work ok!
        //        Log.d(LOG_TAG, "locationChanged: GPS");
        //        // FIXME: 14.04.2016 debug code remove
        //        float speed;
        //        if (ConstantValues.DEBUG_MODE) {
        //            speed = 0 + tempVal[0];
        //            if (speed != 0) {
        //                tempVal[0] += 5;
        //            }
        //            if (speed > 50) {
        //                speed = 0;
        //            }
        //        }
        //        else {
        //            speed = CalculationUtils.getSpeedInKilometerPerHour(location.getSpeed());
        //        }
        //        setupLocationObservable(location);
        //        setupSpeedObservable(speed);
        //        getMaxSpeedAndSetupObservable(speed);
    }

}
