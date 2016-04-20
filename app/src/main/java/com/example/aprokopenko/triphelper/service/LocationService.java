package com.example.aprokopenko.triphelper.service;

import android.content.Intent;
import android.app.Service;
import android.os.IBinder;
import android.os.Binder;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;

public class LocationService extends Service {
    private static final String  LOG_TAG = "LocationService:";
    private final        IBinder mBinder = new LocalBinder();
    private GpsHandler GpsHandler;

    public GpsHandler getGpsHandler() {
        return GpsHandler;
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
        GpsHandler = new GpsHandler();
        return START_STICKY;
    }

    @Override public void onCreate() {
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "onCreate");
        }
    }

    @Override public void onDestroy() {
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "onDestroy");
        }
        super.onDestroy();
    }
}