package com.example.aprokopenko.triphelper.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.example.aprokopenko.triphelper.ui.activity.MainActivity;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final  boolean DEBUG              = BuildConfig.DEBUG;
    private static final String  LOG_TAG            = "LocationService:";
    private static final int     FM_NOTIFICATION_ID = 1;

    private final IBinder mBinder = new LocalBinder();
    private GpsHandler      gpsHandler;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Thread          locationServiceThread;
    private Runnable        locationUpdateRunnable;

    private com.google.android.gms.location.LocationListener gmsLocationListener;

    public GpsHandler getGpsHandler() {
        return gpsHandler;
    }

    @Override public void onConnected(@Nullable Bundle bundle) {
        NotificationCompat.Builder builder = createNotification();
        createRestartAppIntent(builder);
        notify(builder);

        locationUpdateRunnable = new Runnable() {
            public void run() {
                Looper.prepare();
                if (UtilMethods.isPermissionAllowed(getApplicationContext())) {
                    if (DEBUG) {
                        Log.i(LOG_TAG, "onConnected: " + googleApiClient + locationRequest + gmsLocationListener);
                    }
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, gmsLocationListener);
                }
                else {
                    // TODO: 22.06.2016 ask for turningOn permission.
                }
                Looper.loop();
            }
        };

        locationServiceThread = new Thread(locationUpdateRunnable);
        locationServiceThread.start();
    }

    @Override public void onConnectionSuspended(int i) {
        if (DEBUG) {
            Log.i(LOG_TAG, "onConnectionSuspended: ");
        }
    }

    private NotificationCompat.Builder createNotification() {
        return new NotificationCompat.Builder(this).setSmallIcon(R.drawable.notification_icon_bw)
                .setContentTitle(getResources().getString(R.string.notificationTitle)).setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(getResources().getString(R.string.notificationContent)))
                .setContentText(getResources().getString(R.string.notificationContent));
    }

    private void createRestartAppIntent(NotificationCompat.Builder builder) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
    }

    private void notify(NotificationCompat.Builder builder) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(FM_NOTIFICATION_ID, builder.build());
    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (DEBUG) {
            Log.i(LOG_TAG, "onConnectionFailed: " + connectionResult);
        }
    }

    @Override public void onCreate() {
        if (DEBUG) {
            Log.i(LOG_TAG, "service OnCreate");
        }
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) {
            Log.i(LOG_TAG, "onStartCommand");
        }
        super.onStartCommand(intent, flags, startId);
        gpsHandler = new GpsHandler();
        gmsLocationListener = gpsHandler;
        setupLocRequest();
        googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        return START_STICKY;
    }

    @Override public void onDestroy() {
        if (DEBUG) {
            Log.i(LOG_TAG, "service OnDestroy");
        }
        removeNotification();
        googleApiClient.disconnect();
        gmsLocationListener = null;
        locationRequest = null;
        gpsHandler = null;
        locationServiceThread = null;
        locationUpdateRunnable = null;
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void removeNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(FM_NOTIFICATION_ID);
    }

    private LocationRequest setupLocRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(ConstantValues.MIN_UPDATE_TIME);
        locationRequest.setFastestInterval(ConstantValues.MIN_UPDATE_TIME);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
}
