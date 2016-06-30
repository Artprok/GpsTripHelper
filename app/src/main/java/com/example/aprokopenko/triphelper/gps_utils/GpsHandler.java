package com.example.aprokopenko.triphelper.gps_utils;

import android.location.LocationManager;
import android.location.Location;
import android.content.Context;
import android.support.annotation.UiThread;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;

import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.Subscriber;
import rx.Observable;
import rx.Observer;

public class GpsHandler implements com.google.android.gms.location.LocationListener {
    @Inject
    LocationManager locationManager;
    @Inject
    Context         context;

    private static final String LOG_TAG  = "GPSHandler";
    private              float  maxSpeed = ConstantValues.START_VALUE;
    private Observer<Location> locationSubscriber;
    private Observer<Float>    maxSpeedSubscriber;
    private Observer<Float>    speedSubscriber;
    private Thread             locationThread;


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
        Observable<Float> speedObservable = Observable.create(new Observable.OnSubscribe<Float>() {
            @Override public void call(final Subscriber<? super Float> sub) {
                sub.onNext(speed);
            }
        });
        speedObservable.observeOn(Schedulers.immediate()).subscribe(speedSubscriber);
    }


    private void getMaxSpeedAndSetupObservable(float speed) {
        maxSpeed = CalculationUtils.findMaxSpeed(speed, maxSpeed);
        setupMaxSpeedObservable(maxSpeed);
    }

    @Override public void onLocationChanged(final Location location) {
        if (locationThread == null) {
            locationThread = new Thread(new Runnable() {
                @Override public void run() {
                    float speed;
                    // FIXME: 14.04.2016 debug code remove
                    if (ConstantValues.DEBUG_MODE) {
                        speed = UtilMethods.generateRandomSpeed();
                    }
                    else {
                        speed = CalculationUtils.getSpeedInKilometerPerHour(location.getSpeed());
                    }
                    setupLocationObservable(location);
                    getMaxSpeedAndSetupObservable(speed);
                    setupSpeedObservable(speed);
                    synchronized (locationThread) {
                        try {
                            locationThread.wait();
                            locationThread.run();
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            locationThread.start();
        }
        else {
            synchronized (locationThread) {
                locationThread.notify();
            }
        }
    }

    public void performExit() {
        locationThread.interrupt();
        locationThread = null;
        context = null;
        locationManager = null;
        locationSubscriber = null;
        speedSubscriber = null;
        maxSpeedSubscriber = null;
    }
}
