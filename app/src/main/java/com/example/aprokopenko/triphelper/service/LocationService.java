package com.example.aprokopenko.triphelper.service;

import android.location.LocationManager;
import android.location.Location;
import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Binder;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.listener.LocationListener;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;

public class LocationService extends Service {

    private static final String  LOG_TAG = "LocationService:";
    private final        IBinder mBinder = new LocalBinder();
    private LocationListener locationListener;
    private GpsHandler       gpsHandler;

    public GpsHandler getGpsHandler() {
        return gpsHandler;
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "onStartCommand");
        }
        super.onStartCommand(intent, flags, startId);
        gpsHandler = new GpsHandler();
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = gpsHandler;
        android.location.LocationListener locationListener = new android.location.LocationListener() {
            @Override public void onLocationChanged(Location location) {
                LocationService.this.locationListener.locationChanged(location);
            }

            @Override public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override public void onProviderEnabled(String provider) {

            }

            @Override public void onProviderDisabled(String provider) {

            }
        };

        UtilMethods.checkPermission(getApplicationContext());
        locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER, ConstantValues.MIN_UPDATE_TIME, ConstantValues.MIN_UPDATE_DISTANCE,
                        locationListener);
        return START_STICKY;
    }

    @Override public void onCreate() {
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "onCreate");
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "onDestroy");
        }
        locationListener = null;
    }
}