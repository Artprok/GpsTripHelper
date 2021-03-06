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

/**
 * Class representing a {@link Service} responsible for GPS processing in background.
 */
public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private static final String LOG_TAG = "LocationService:";
  private static final int FM_NOTIFICATION_ID = 1;
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private final IBinder mBinder = new LocalBinder();

  private GpsHandler gpsHandler;
  private LocationRequest locationRequest;
  private GoogleApiClient googleApiClient;
  private Thread locationServiceThread;
  private Runnable locationUpdateRunnable;

  private com.google.android.gms.location.LocationListener gmsLocationListener;

  public GpsHandler getGpsHandler() {
    return gpsHandler;
  }

  @Override public void onConnected(@Nullable final Bundle bundle) {
    final NotificationCompat.Builder builder = createNotification(this);
    createRestartAppIntent(builder, this);
    notify(builder, this);

    locationUpdateRunnable = new Runnable() {
      public void run() {
        Looper.prepare();
        if (UtilMethods.isPermissionAllowed(getApplicationContext())) {
          if (DEBUG) {
            Log.i(LOG_TAG, "onConnected: " + googleApiClient + locationRequest + gmsLocationListener);
          }
          LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, gmsLocationListener);
        } else {
          // TODO: 22.06.2016 ask for turningOn permission.
        }
        Looper.loop();
      }
    };

    locationServiceThread = new Thread(locationUpdateRunnable);
    locationServiceThread.start();
  }

  @Override public void onConnectionSuspended(final int i) {
    if (DEBUG) {
      Log.i(LOG_TAG, "onConnectionSuspended: ");
    }
  }

  @Override public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
    if (DEBUG) {
      Log.i(LOG_TAG, "onConnectionFailed: " + connectionResult);
    }
  }

  @Override public IBinder onBind(@NonNull final Intent intent) {
    return mBinder;
  }

  @Override public int onStartCommand(@NonNull final Intent intent, final int flags, final int startId) {
    if (DEBUG) {
      Log.i(LOG_TAG, "onStartCommand");
    }
    super.onStartCommand(intent, flags, startId);
    gpsHandler = new GpsHandler();
    gmsLocationListener = gpsHandler;
    locationRequest = setupLocRequest();
    googleApiClient = new GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();
    googleApiClient.connect();
    return START_STICKY;
  }

  @Override public void onCreate() {
    if (DEBUG) {
      Log.i(LOG_TAG, "service OnCreate");
    }
  }

  @Override public void onDestroy() {
    if (DEBUG) {
      Log.i(LOG_TAG, "service OnDestroy");
    }
    removeNotification(this);
    googleApiClient.disconnect();
    gmsLocationListener = null;
    locationRequest = null;
    gpsHandler = null;
    locationServiceThread = null;
    locationUpdateRunnable = null;
    super.onDestroy();
  }

  private static LocationRequest setupLocRequest() {
    return new LocationRequest()
            .setInterval(ConstantValues.MIN_UPDATE_TIME)
            .setFastestInterval(ConstantValues.MIN_UPDATE_TIME)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  private static void notify(@NonNull final NotificationCompat.Builder builder, @NonNull final Context context) {
    final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    manager.notify(FM_NOTIFICATION_ID, builder.build());
  }

  private static void createRestartAppIntent(@NonNull final NotificationCompat.Builder builder, @NonNull final Context context) {
    final Intent intent = new Intent(context, MainActivity.class);

    intent.setAction(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);
    builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
  }

  private static NotificationCompat.Builder createNotification(@NonNull final Context context) {
    return new NotificationCompat.Builder(context)
            .setSmallIcon(R.drawable.notification_icon_bw)
            .setOngoing(true)
            .setContentTitle(context.getString(R.string.notificationTitle)).setPriority(NotificationCompat.PRIORITY_MAX)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.notificationContent)))
            .setContentText(context.getString(R.string.notificationContent));
  }

  private static void removeNotification(@NonNull final Context context) {
    final NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    manager.cancel(FM_NOTIFICATION_ID);
  }

  public class LocalBinder extends Binder {
    public LocationService getService() {
      return LocationService.this;
    }
  }
}