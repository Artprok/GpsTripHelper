package com.example.aprokopenko.triphelper.service;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.Location;
import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.listener.LocationListener;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String  LOG_TAG = "LocationService:";
    private final        IBinder mBinder = new LocalBinder();
    private LocationListener                                 locationListener;
    private com.google.android.gms.location.LocationListener gmsLocListener;
    private GpsHandler                                       gpsHandler;
    private com.google.android.gms.location.LocationListener gpsList;
    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;

    public GpsHandler getGpsHandler() {
        return gpsHandler;
    }

    @Override public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "onConnected: CONNECTED");
        UtilMethods.checkPermission(getApplicationContext());
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, gpsList);
    }

    @Override public void onConnectionSuspended(int i) {

    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        // TODO: 13.05.2016 LocationListener on LocationManager
        //        locationListener = gpsHandler;
        //        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //        android.location.LocationListener locationListener = new android.location.LocationListener() {
        //            @Override public void onLocationChanged(Location location) {
        //                LocationService.this.locationListener.locationChanged(location);
        //            }
        //
        //            @Override public void onStatusChanged(String provider, int status, Bundle extras) {
        //
        //            }
        //
        //            @Override public void onProviderEnabled(String provider) {
        //
        //            }
        //
        //            @Override public void onProviderDisabled(String provider) {
        //
        //            }
        //        };
        //        UtilMethods.checkPermission(getApplicationContext());
        //        locationManager
        //                .requestLocationUpdates(LocationManager.GPS_PROVIDER, ConstantValues.MIN_UPDATE_TIME, ConstantValues
        // .MIN_UPDATE_DISTANCE,
        //                        locationListener);


        // TODO: 13.05.2016 locManager based on gps
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        setupLocRequest();
        gmsLocListener = gpsHandler;
        gpsList = new com.google.android.gms.location.LocationListener() {
            @Override public void onLocationChanged(Location location) {
                LocationService.this.gmsLocListener.onLocationChanged(location);
            }
        };

        return START_STICKY;
    }

    @Override public void onCreate() {
        if (ConstantValues.DEBUG_MODE) {
            Log.i(LOG_TAG, "service OnCreate");
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        if (ConstantValues.DEBUG_MODE) {
            Log.i(LOG_TAG, "service OnDestroy");
        }
        locationListener = null;
        gmsLocListener = null;
    }

    private LocationRequest setupLocRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return LocationRequest.create();
    }
}