package com.example.aprokopenko.triphelper.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.TripProcessor;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.ui.map.MapFragment;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.ads.AdRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class MainPresenter implements MainContract.UserActionListener, GpsStatus.Listener {
    private static final String MEASUREMENT_UNIT_POSITION = "measurementUnitPosition";
    private static final String CONTROL_BUTTON_VISIBILITY = "ControlButtonVisibility";
    private static final String LOG_TAG = "MainFragment";
    private static final String STATUS_IMAGE_STATE = "StatusImageState";
    private static final String FIRST_START = "FirstStart";
    private static final String AVG_SPEED_LIST = "AvgSpeedList";
    private static final String TRIP_PROCESSOR = "TripProcessor";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final boolean REMOVE = false;
    private static final boolean REGISTER = true;

    private MainContract.View view;
    private TripProcessor tripProcessor;
    private LocationManager locationManager;
    private SharedPreferences preferences;
    private Bundle state;

    private float fuelPriceFromSettings;
    private float fuelConsFromSettings;
    private int fuelCapacityFromSettings;
    private long gpsFirstFixTime;
    private boolean firstStart = true;
    private boolean fileErased;
    private boolean gpsIsActive;
    private Context context;

    MainPresenter(@NonNull final MainContract.View view, @NonNull final Context context) {
        this.context = context;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void onFileErased() {
        fileErased = true;
        tripProcessor.onFileErased();
    }

    @Override
    public void onFuelFilled(final float fuelFilled) {
        view.setFuelLeft(tripProcessor.fillGasTankAndGetFuelLevel(fuelFilled, context.getString(R.string.distance_prefix)));
    }

    @Override
    public void onGpsStatusChanged(final int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                performGpsInitialFix();
                setupAdvert();
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                checkIfSatellitesAreStillAvailableWithInterval(ConstantValues.FIVE_MINUTES);
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                deactivateGpsStatusIcon();
                break;
        }
    }

    @Override
    public void onStartClick() {
        startTrip();
    }

    @Override
    public void onStopClick() {
        endTrip();
    }

    @Override
    public void onSettingsClick(boolean buttonVisible) {
        saveState(buttonVisible);
        view.hideFab();
    }

    @Override
    public void onOpenMapFragment(boolean buttonVisible) {
        saveState(buttonVisible);
    }

    @Override
    public void onConfigureMapFragment(@NonNull final MapFragment mapFragment) {
        mapFragment.setGpsHandler(tripProcessor.getGpsHandler());
        setRoutesToMapFragment(mapFragment);
    }

    private void setRoutesToMapFragment(@NonNull final MapFragment mapFragment) {
        if (tripProcessor.getRoutes() != null) {
            mapFragment.setRoutes(tripProcessor.getRoutes());
        }
    }

    @Override
    public void onTripListClick(final boolean buttonVisible) {
        if (tripProcessor.isFileNotInWriteMode()) {
            final TripData tripData = tripProcessor.getTripData();
            if (tripData != null && buttonVisible) {
                if (!tripData.getTrips().isEmpty()) {
                    saveState(buttonVisible);
                    view.hideFab();
                    view.showTripListFragment(tripData);
                }
            }
        }
    }

    @Override
    public void onRefillClick() {
        if (tripProcessor.isFileNotInWriteMode()) {
            view.showFuelFillDialog();
        }
    }

    @Override
    public void cleanAllProcess(boolean buttonVisible) {
        if (buttonVisible) {
            stopTracking();
        }
        tripProcessor.performExit();
        gpsStatusListener(REMOVE);
        locationManager = null;
        tripProcessor = null;
        preferences = null;
    }

    @Override
    public void onSave(final Bundle outState, boolean buttonVisible) {
        outState.putBoolean(CONTROL_BUTTON_VISIBILITY, buttonVisible);
        outState.putBoolean(STATUS_IMAGE_STATE, gpsIsActive);
        outState.putBoolean(FIRST_START, firstStart);
        outState.putStringArrayList(AVG_SPEED_LIST, ConfigureAvgSpdListToStore(tripProcessor));
        outState.putParcelable(TRIP_PROCESSOR, tripProcessor);
    }

    @Override
    public void onSpeedChanged(float speed) {
        Log.d("MPRESENTER", "onSpeedChanged: " + speed);
        view.onSpeedChanged(speed);
    }

    public void onPause(final boolean buttonVisible, final boolean isMainFragment) {
        view.pauseAdvert();
        if (isMainFragment) {
            turnOffGpsIfAdapterDisabled();
            saveState(buttonVisible);
        }
    }

    public void start(final Bundle savedInstanceState, boolean isMainFragment, boolean buttonVisible) {
        view.resumeAdvert();
        gpsIsActive = false;
        state = getSavedStateInstanceIfPossible(savedInstanceState);
        getStateFromPrefs();
        setupTripProcessor(isMainFragment, buttonVisible);
    }

    public void onResume(boolean isMainFragment, boolean isButtonVisible) {
        locationManager = getLocationMangerIfNull();
        tripProcessor.onResume();
        locationManager = getLocationMangerIfNull();
        restoreStateIfPossible(isMainFragment, isButtonVisible);
        setupFuelFields();
        turnOffGpsIfAdapterDisabled();
    }

    @Override
    public void configureTripProcessor() {
        resetTripProcessor();
        tripProcessor = new TripProcessor(fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings, this);
        gpsStatusListener(REGISTER);
    }

    private void endTrip() {
        stopTracking();
        view.showEndTripToast();
        view.startButtonTurnActive();
    }

    private void stopTracking() {
        tripProcessor.onTripEnded();
        if (firstStart) {
            view.setFuelVisible();
            firstStart = false;
        }

        view.setFuelLeft(tripProcessor.getFuelLeftString(context.getString(R.string.distance_prefix)));
    }

    private void startTrip() {
        tripProcessor.onTripStarted();
        view.showFab();
        view.showStartTripToast();
        view.stopButtonTurnActive();
    }

    private void checkIfSatellitesAreStillAvailableWithInterval(final long interval) {
        final long curTime = System.currentTimeMillis();

        if ((curTime - gpsFirstFixTime) > interval) {
            if (UtilMethods.checkIfGpsEnabled(context)) {
                for (final GpsSatellite satellite : getSatellitesList(context, locationManager)) {
                    if (satellite.usedInFix()) {
                        setGpsIconActive();
                        gpsFirstFixTime = curTime;
                    } else {
                        deactivateGpsStatusIcon();
                    }
                }
            } else {
                deactivateGpsStatusIcon();
            }
        }
    }

    private static Iterable<GpsSatellite> getSatellitesList(@NonNull final Context context, @Nullable final LocationManager locationManager) {
        return getGpsStatus(context, locationManager).getSatellites();
    }

    private static GpsStatus getGpsStatus(@NonNull final Context context, @Nullable LocationManager locationManager) {
        if (locationManager != null) {
            return locationManager.getGpsStatus(null);
        } else {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.getGpsStatus(null);
        }
    }

    private void deactivateGpsStatusIcon() {
        setGpsIconNotActive();
        gpsFirstFixTime = System.currentTimeMillis();
    }

    private void setupAdvert() {
        if (!BuildConfig.FLAVOR.contains(context.getString(R.string.paidVersion_code))) {
            if (!advertInstalled()) {
                //                AdRequest adRequest = new AdRequest.Builder().build();
                //                advertView.loadAd(adRequest);
                view.setupAdView(setupAdRequest());
                setAdvertInstalled(true);
            }
        }
    }

    private void setAdvertInstalled(final boolean isAdvInstalled) {
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getString(R.string.PREFERENCE_KEY_ADVERT_INSTALLATION), isAdvInstalled).apply();
    }

    @NonNull
    private AdRequest setupAdRequest() {
        return new
                AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .setGender(AdRequest.GENDER_MALE)
                .setLocation(getLocationForAdvert())
                .build();
    }

    private Location getLocationForAdvert() {
        locationManager = getLocationMangerIfNull();
        final Location lastKnownLocation = getLastKnownLocation(locationManager);
        if (lastKnownLocation != null) {
            return lastKnownLocation;
        } else {
            return getLocationIfLastKnownIsNull(locationManager);
        }
    }

    private static Location getLocationIfLastKnownIsNull(@NonNull final LocationManager locationManager) {
        final Location[] locationForAd = new Location[1];
        final Looper looper = addLooper();
        final LocationListener locationListener = setLocationListener(locationForAd);
        requestSingleGpsUpdate(looper, locationListener, locationManager);
        locationManager.removeUpdates(locationListener);
        stopLooper(looper);
        return locationForAd[0];
    }

    private static synchronized Looper setLooper(@NonNull final Looper looper) {
        return looper;
    }

    private static synchronized void stopLooper(@Nullable final Looper looper) {
        if (looper == null) {
            return;
        }
        looper.quit();
    }

    @NonNull
    private static LocationListener setLocationListener(@NonNull final Location[] locations) {
        return new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull final Location location) {
                locations[1] = location;
            }

            @Override
            public void onStatusChanged(@NonNull final String provider, final int status, @Nullable final Bundle extras) {
            }

            @Override
            public void onProviderEnabled(@NonNull final String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull final String provider) {
            }
        };
    }

    private static void requestSingleGpsUpdate(@NonNull final Looper looper, @NonNull final LocationListener locationListener, @NonNull final LocationManager locationManager) {
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, looper);
    }

    private static Looper addLooper() {
        Looper.prepare();
        return setLooper(Looper.myLooper());
    }

    private static Location getLastKnownLocation(@NonNull final LocationManager locationManager) {
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private boolean advertInstalled() {
        return preferences.getBoolean(context.getString(R.string.PREFERENCE_KEY_ADVERT_INSTALLATION), false);
    }

    private void performGpsInitialFix() {
        view.showSatellitesConnectedToast();
        setGpsIconActive();
    }

    private void setGpsIconActive() {
        gpsIsActive = true;
        gpsFirstFixTime = System.currentTimeMillis();
        view.setGpsIconActive();
    }

    private void resetTripProcessor() {
        tripProcessor.performExit();
        tripProcessor = null;
    }

    private void setupFuelFields() {
        setInternalSettingsToTripProcessor();
        view.setFuelLeft(tripProcessor.getFuelLeftString(context.getString(R.string.distance_prefix)));
        view.showFab();
    }

    private void setInternalSettingsToTripProcessor() {
        getInternalSettings();
        tripProcessor.setFuelCapacity(fuelCapacityFromSettings);
        tripProcessor.setFuelConsumption(fuelConsFromSettings);
        tripProcessor.setFuelPrice(fuelPriceFromSettings);
    }

    private void restoreStateIfPossible(final boolean isMainFragment, final boolean isButtonVisible) {
        if (state != null && !fileErased) {
            restoreState(state, isMainFragment, isButtonVisible);
        } else {
            fileErased = false;
        }
    }

    private void saveState(boolean buttonVisible) {
        if (state == null) {
            state = new Bundle();
        }
        onSave(state, buttonVisible);
    }

    private void turnOffGpsIfAdapterDisabled() {
        if (!UtilMethods.checkIfGpsEnabled(context)) {
            setGpsIconNotActive();
        }
    }

    private void setGpsIconNotActive() {
        gpsIsActive = false;
        view.setGpsIconPassive();
    }


    @NonNull
    private static ArrayList<String> ConfigureAvgSpdListToStore(@NonNull final TripProcessor tripProcessor) {
        final ArrayList<Float> avgSpeedArrayList = tripProcessor.getAvgSpeedList();
        final ArrayList<String> avgStrArrList = new ArrayList<>();

        if (avgSpeedArrayList != null) {
            for (final float avgListItem : avgSpeedArrayList) {
                avgStrArrList.add(String.valueOf(avgListItem));
            }
        }
        return avgStrArrList;
    }

    private void getStateFromPrefs() {
        preferences = TripHelperApp.getSharedPreferences();
        if (preferences.getBoolean(FIRST_START, true)) {
            UtilMethods.firstStartTutorialDialog(context);
            setFirstStartToFalse(preferences);
        }
        CalculationUtils.setMeasurementMultiplier(preferences.getInt(MEASUREMENT_UNIT_POSITION, 0));
        getInternalSettings();
    }

    private void getInternalSettings() {
        //fixme do something. change to sql.
        final ReadInternalFile readInternalFile = new ReadInternalFile();
        readInternalFile.execute();
    }

    private static void setFirstStartToFalse(@NonNull final SharedPreferences preferences) {
        final SharedPreferences.Editor editor = preferences.edit();
        editor
                .putBoolean(FIRST_START, false)
                .apply();
    }

    private Bundle getSavedStateInstanceIfPossible(@Nullable final Bundle savedInstanceState) {
        if (savedInstanceState != null && savedStateIsCorrect(savedInstanceState)) {
            return savedInstanceState;
        } else {
            return state;
        }
    }

    private static boolean savedStateIsCorrect(@NonNull final Bundle savedState) {
        return savedState.containsKey(CONTROL_BUTTON_VISIBILITY);
    }

    private void setupTripProcessor(boolean isMainFragment, boolean buttonVisible) {
        if (tripProcessor == null) {
            if (state != null) {
                restoreState(state, isMainFragment, buttonVisible);
            } else {
                tripProcessor = new TripProcessor(fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings, this);
            }
            if (UtilMethods.isPermissionAllowed(context)) {
                gpsStatusListener(REGISTER);
            } else {
                view.requestLocationPermissions();
            }
        }
    }

    private void gpsStatusListener(final boolean register) {
        locationManager = getLocationMangerIfNull();
        if (register) {
            if (UtilMethods.isPermissionAllowed(context)) {
                locationManager.addGpsStatusListener(this);
            } else {
                view.requestLocationPermissions();
                locationManager.addGpsStatusListener(this);
            }
        } else {
            locationManager.removeGpsStatusListener(this);
        }
    }

    private LocationManager getLocationMangerIfNull() {
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager;
    }

    private void restoreState(@NonNull final Bundle savedInstanceState, final boolean isMainFramgnet, final boolean isButtonVisible) {
        if (isMainFramgnet) {
            Log.d("maiP", "restoreState: state restore!");
            firstStart = savedInstanceState.getBoolean(FIRST_START);

            restoreButtonsVisibility(savedInstanceState);
            restoreStatusIconState(savedInstanceState);
            restoreTripProcessor(savedInstanceState);

            restoreFuelLayoutVisibility(firstStart);
            if (isButtonVisible) {
                view.showFab();
            }
        }
    }

    private void restoreFuelLayoutVisibility(final boolean firstState) {
        if (!firstState) {
            view.setFuelVisible();
        }
    }

    private void restoreTripProcessor(@Nullable final Bundle savedInstanceState) {
        if (tripProcessor == null) {
            if (savedInstanceState.getParcelable(TRIP_PROCESSOR) != null) {
                tripProcessor = savedInstanceState.getParcelable(TRIP_PROCESSOR);
            } else {
                tripProcessor = new TripProcessor(fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings, this);
            }
        }
        tripProcessor.restoreAvgList(savedInstanceState.getStringArrayList(AVG_SPEED_LIST));
        view.restoreFuelLevel(getFuelString());
    }

    private String getFuelString() {
        return tripProcessor.getFuelLeftString(context.getString(R.string.distance_prefix));
    }

    private void restoreButtonsVisibility(@NonNull final Bundle savedState) {
        if (savedState.getBoolean(CONTROL_BUTTON_VISIBILITY)) {
            view.startButtonTurnActive();
        } else {
            view.stopButtonTurnActive();
        }
    }

    private void restoreStatusIconState(@NonNull final Bundle savedState) {
        if (savedState.getBoolean(STATUS_IMAGE_STATE)) {
            setGpsIconActive();
        }
    }


    private class ReadInternalFile extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(@Nullable final String... params) {
            if (DEBUG) {
                Log.i(LOG_TAG, "readFileSettings");
            }
            if (context.getFileStreamPath(ConstantValues.INTERNAL_SETTING_FILE_NAME).exists()) {
                if (DEBUG) {
                    Log.i(LOG_TAG, "readTripDataFromFileSettings: ");
                }
                try {
                    final FileInputStream fis = context.openFileInput(ConstantValues.INTERNAL_SETTING_FILE_NAME);
                    final ObjectInputStream is = new ObjectInputStream(fis);
                    final float consumption = is.readFloat();
                    final float fuelPrice = is.readFloat();
                    final int capacity = is.readInt();

                    fuelConsFromSettings = consumption;
                    fuelPriceFromSettings = fuelPrice;
                    fuelCapacityFromSettings = capacity;
                    is.close();
                    fis.close();
                } catch (@NonNull final IOException e) {
                    e.printStackTrace();
                }
                return true;
            } else {
                fuelConsFromSettings = ConstantValues.FUEL_CONSUMPTION_DEFAULT;
                fuelPriceFromSettings = ConstantValues.FUEL_COST_DEFAULT;
                fuelCapacityFromSettings = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT;
                return false;
            }
        }

        @Override
        protected void onPostExecute(@NonNull final Boolean result) {
            super.onPostExecute(result);
        }
    }
}