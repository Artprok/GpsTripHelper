package com.example.aprokopenko.triphelper.gps_utils;

import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.MathUtils;
import com.example.aprokopenko.triphelper.application.TripHelperApp;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Observable;
import rx.Observer;

public class GpsHandler {
    @Inject
    LocationManager locationManager;
    @Inject
    Context         context;

    public static final String LOG_TAG         = "GPSHandler";
    private             float  maxSpeed        = ConstantValues.START_VALUE;
    private             float  currentAvgSpeed = ConstantValues.START_VALUE;
    private Observer<Location> locationSubscriber;
    private Observer<Float>    avgSpeedSubscriber;
    private Observer<Float>    maxSpeedSubscriber;
    private Observer<Float>    speedSubscriber;
    private ArrayList<Float>   avgSpeedArrayList;

    public GpsHandler() {
        TripHelperApp.getApplicationComponent().injectInto(this);
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "GpsHandler: onCreate");
        }
        setUpLocationListener();
    }

    public void setLocationSubscriber(Subscriber<Location> locationSubscriber) {
        this.locationSubscriber = locationSubscriber;
    }

    public void setAvgSpeedSubscriber(Subscriber<Float> avgSpeedSubscriber) {
        this.avgSpeedSubscriber = avgSpeedSubscriber;
    }

    public void setMaxSpeedSubscriber(Subscriber<Float> maxSpeedSubscriber) {
        this.maxSpeedSubscriber = maxSpeedSubscriber;
    }

    public void setSpeedSubscriber(Subscriber<Float> speedSubscriber) {
        this.speedSubscriber = speedSubscriber;
    }


    private void setupAvgSpeedObservable(final float averageSpeed) {
        Observable<Float> avgSpeedObservable = Observable.create(new Observable.OnSubscribe<Float>() {
            @Override public void call(Subscriber<? super Float> subscriber) {
                subscriber.onNext(averageSpeed);
            }
        });
        avgSpeedObservable.subscribe(avgSpeedSubscriber);
    }

    private void setupLocationObservable(final Location location) {
        Observable<Location> speedObservable = Observable.create(new Observable.OnSubscribe<Location>() {
            @Override public void call(Subscriber<? super Location> sub) {
                sub.onNext(location);
            }
        });
        speedObservable.subscribe(locationSubscriber);
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
            @Override public void call(Subscriber<? super Float> sub) {
                sub.onNext(speed);
            }
        });
        speedObservable.subscribe(speedSubscriber);
    }

    private void getAverageSpeed(float speed) {
        float initialAvgSpeed = 0;
        if (avgSpeedArrayList.size() < ConstantValues.AVG_SPEED_UPDATE_FREQUENCY) {
            avgSpeedArrayList.add(speed);
        }
        else {
            currentAvgSpeed = MathUtils.figureOutAverageSpeed(initialAvgSpeed, currentAvgSpeed, avgSpeedArrayList);
            setupAvgSpeedObservable(currentAvgSpeed);
            avgSpeedArrayList.clear();
            getAverageSpeed(speed);
        }
    }

    private void getMaxSpeed(float speed) {
        if (speed > maxSpeed) {
            maxSpeed = speed;
            setupMaxSpeedObservable(maxSpeed);
        }
    }

    private void setUpLocationListener() {
        // FIXME: 14.04.2016 debug code remove
        final ArrayList<Float> test    = new ArrayList<>();
        final float[]          tempVal = {1};
        avgSpeedArrayList = new ArrayList<>();
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(final Location location) {
                // FIXME: 14.04.2016 debug code remove
                float speed;
                if (ConstantValues.DEBUG_MODE) {
                    speed = 0 + tempVal[0];
                    if (speed != 0) {
                        tempVal[0] += 5;
                    }

                    test.add(speed);
                    if (test.size() == 10) {
                        float num = 0;
                        for (Float f : test) {
                            num = f + num;
                        }
                        Log.d(LOG_TAG, "onLocationChanged: NUM" + num / test.size());
                    }
                    Log.d(LOG_TAG, "onLocationChanged: testSize" + test.size());
                }
                else {
                    speed = MathUtils.getSpeedInKilometerPerHour(location.getSpeed());
                }

                setupLocationObservable(location);
                setupSpeedObservable(speed);
                getMaxSpeed(speed);
                getAverageSpeed(speed);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "onStatusChanged");
                }
            }

            public void onProviderEnabled(String provider) {
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "onProviderEnabled");
                }
            }

            public void onProviderDisabled(String provider) {
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "onProviderDisabled: ");
                }
            }
        };

        UtilMethods.checkPermission(context);
        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, ConstantValues.MIN_UPDATE_TIME, ConstantValues.MIN_UPDATE_DISTANCE,
                        locationListener);
    }
}
