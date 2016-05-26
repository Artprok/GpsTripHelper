package com.example.aprokopenko.triphelper.service;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.app.Service;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Binder;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.ConnectionResult;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String  LOG_TAG = "LocationService:";
    private final        IBinder mBinder = new LocalBinder();
    private com.google.android.gms.location.LocationListener gmsLocationListener;
    private LocationRequest                                  locationRequest;
    private GoogleApiClient                                  googleApiClient;
    private GpsHandler                                       gpsHandler;
    //    private LocationListener                                 locationListener;


    public GpsHandler getGpsHandler() {
        return gpsHandler;
    }

    @Override public void onConnected(@Nullable Bundle bundle) {
        UtilMethods.checkPermission(getApplicationContext());
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, gmsLocationListener);
        if (ConstantValues.LOGGING_ENABLED) {
            Log.d(LOG_TAG, "onConnected: " + googleApiClient + locationRequest + gmsLocationListener);
        }
    }

    @Override public void onConnectionSuspended(int i) {
        if (ConstantValues.LOGGING_ENABLED) {
            Log.i(LOG_TAG, "onConnectionSuspended: ");
        }
    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (ConstantValues.LOGGING_ENABLED) {
            Log.i(LOG_TAG, "onConnectionFailed: " + connectionResult);
        }
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
        if (ConstantValues.LOGGING_ENABLED) {
            Log.d(LOG_TAG, "onStartCommand");
        }
        super.onStartCommand(intent, flags, startId);
        gpsHandler = new GpsHandler();
        gmsLocationListener = gpsHandler;
        setupLocRequest();
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();

        return START_STICKY;

        //        // TODO: 13.05.2016 LocationListener on LocationManager
        //                locationListener = gpsHandler;
        //                LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context
        // .LOCATION_SERVICE);
        //                android.location.LocationListener locationListener = new android.location.LocationListener() {
        //                    @Override public void onLocationChanged(Location location) {
        //                        LocationService.this.locationListener.locationChanged(location);
        //                    }
        //
        //                    @Override public void onStatusChanged(String provider, int status, Bundle extras) {
        //
        //                    }
        //
        //                    @Override public void onProviderEnabled(String provider) {
        //
        //                    }
        //
        //                    @Override public void onProviderDisabled(String provider) {
        //
        //                    }
        //                };
        //                UtilMethods.checkPermission(getApplicationContext());
        //                locationManager
        //                        .requestLocationUpdates(LocationManager.GPS_PROVIDER, ConstantValues.MIN_UPDATE_TIME, ConstantValues
        //         .MIN_UPDATE_DISTANCE,
        //                                locationListener);


        // TODO: 13.05.2016 locManager based on gps
    }

    @Override public void onCreate() {
        if (ConstantValues.LOGGING_ENABLED) {
            Log.i(LOG_TAG, "service OnCreate");
        }
    }

    @Override public void onDestroy() {
        if (ConstantValues.LOGGING_ENABLED) {
            Log.i(LOG_TAG, "service OnDestroy");
        }
        googleApiClient.disconnect();
        gmsLocationListener = null;
        locationRequest = null;
        gpsHandler = null;
        super.onDestroy();
        //        locationListener = null;
    }

    private LocationRequest setupLocRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(ConstantValues.MIN_UPDATE_TIME);
        locationRequest.setFastestInterval(ConstantValues.MIN_UPDATE_TIME);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}