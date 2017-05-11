package com.example.aprokopenko.triphelper.ui.main_screen;

import android.Manifest;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.TripProcessor;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.speedometer_gauge.TripHelperGauge;
import com.example.aprokopenko.triphelper.ui.fragment.MapFragment;
import com.example.aprokopenko.triphelper.ui.fragment.TripListFragment;
import com.example.aprokopenko.triphelper.ui.setting_screen.SettingsFragment;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.ads.AdRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import static com.example.aprokopenko.triphelper.utils.settings.ConstantValues.SETTINGS_FRAGMENT_TAG;
import static com.example.aprokopenko.triphelper.utils.settings.ConstantValues.TRIP_LIST_TAG;

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
    private static final int LOCATION_REQUEST_CODE = 1;

    private MainContract.View view;
    private TripProcessor tripProcessor;
    private LocationManager locationManager;
    private SharedPreferences preferences;
    private MapFragment mapFragment;
    private Bundle state;

    private float fuelPriceFromSettings;
    private float fuelConsFromSettings;
    private int fuelCapacityFromSettings;
    private long gpsFirstFixTime;
    private boolean firstStart = true;
    private boolean fileErased;
    private boolean gpsIsActive;
    private Context context;

    public MainPresenter(MainContract.View view, Context context) {
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
    public void onSpeedChanged(final float speed) {
        animateSpeedUpdate(speed);
    }

    @Override
    public void onFuelFilled(final float fuelFilled) {
        view.setFuelLeft(tripProcessor.fillGasTankAndGetFuelLevel(fuelFilled, view.getString(R.string.distance_prefix)));
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
    public void onSettingsClick() {
        saveState();
        view.hideFab();
    }

    @Override
    public void onOpenMapFragment() {
        saveState();
    }

    @Override
    public void onTripListClick() {
        if (tripProcessor.isFileNotInWriteMode()) {
            final TripData tripData = tripProcessor.getTripData();
            if (tripData != null && !isButtonVisible(view.stopButton)) {
                if (!tripData.getTrips().isEmpty()) {
                    saveState();
                    view.hideFab();
                    if (view.getChildFragmentManager().findFragmentByTag(TRIP_LIST_TAG) != null) {
                        showTripListFragment(tripData, (TripListFragment) view.getChildFragmentManager().findFragmentByTag(TRIP_LIST_TAG), view);
                    } else {
                        showTripListFragment(tripData, TripListFragment.newInstance(tripData), view);
                    }
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
    public void cleanAllProcess() {
        if (isButtonVisible(view.stopButton)) {
            stopTracking();
        }
        tripProcessor.performExit();
        gpsStatusListener(REMOVE);
        locationManager = null;
        tripProcessor = null;
        mapFragment = null;
        preferences = null;
    }

    @Override
    public void onSave(final Bundle outState) {
        outState.putBoolean(CONTROL_BUTTON_VISIBILITY, isButtonVisible(view.startButton));
        outState.putBoolean(STATUS_IMAGE_STATE, gpsIsActive);
        outState.putBoolean(FIRST_START, firstStart);
        outState.putStringArrayList(AVG_SPEED_LIST, ConfigureAvgSpdListToStore(tripProcessor));
        outState.putParcelable(TRIP_PROCESSOR, tripProcessor);
    }

    public void onPause() {
        final Fragment fragment = view.getFragmentManager().findFragmentById(R.id.fragmentContainer);
        view.pauseAdvert();
        if (fragment != null && fragment instanceof MainFragment) {
            turnOffGpsIfAdapterDisabled();
            saveState();
        }
    }

    public void start(final Bundle savedInstanceState) {
        view.resumeAdvert();
        gpsIsActive = false;
        state = getSavedStateInstanceIfPossible(savedInstanceState);
        getStateFromPrefs();
        setupTripProcessor();
    }

    public void onResume() {
        locationManager = getLocationMangerIfNull();
        tripProcessor.onResume();
        UtilMethods.checkIfGpsEnabledAndShowDialogs(context);
        locationManager = getLocationMangerIfNull();
        restoreStateIfPossible();
        setupFuelFields();
        turnOffGpsIfAdapterDisabled();
    }

    @Override
    public void configureTripProcessor() {
        resetTripProcessor();
        tripProcessor = new TripProcessor(fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings, this);
        gpsStatusListener(REGISTER);
    }

    public void requestLocationPermissions() {
        view.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,},
                LOCATION_REQUEST_CODE);
    }

    private void setRoutesToMapFragment() {
        if (tripProcessor.getRoutes() != null) {
            mapFragment.setRoutes(tripProcessor.getRoutes());
        }
    }

    private static void showTripListFragment(@NonNull final TripData tripData, @NonNull final TripListFragment tripListFragment, @NonNull final Fragment fragment) {
        tripListFragment.setTripData(tripData);
        UtilMethods.replaceFragment(tripListFragment, ConstantValues.TRIP_LIST_TAG, fragment.getActivity());
    }

    private void endTrip() {
        stopTracking();
        UtilMethods.showToast(context, view.getString(R.string.trip_ended_toast));
        view.startButtonTurnActive();
    }

    private void stopTracking() {
        tripProcessor.onTripEnded();
        if (firstStart) {
            view.setFuelVisible();
            firstStart = false;
        }

        view.setFuelLeft(tripProcessor.getFuelLeftString(view.getString(R.string.distance_prefix)));
    }

    private void startTrip() {
        tripProcessor.onTripStarted();
        view.showFab();
        UtilMethods.showToast(context, view.getString(R.string.trip_started_toast));
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
        if (!BuildConfig.FLAVOR.contains(view.getString(R.string.paidVersion_code))) {
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
        editor.putBoolean(view.getString(R.string.PREFERENCE_KEY_ADVERT_INSTALLATION), isAdvInstalled).apply();
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
        return preferences.getBoolean(view.getString(R.string.PREFERENCE_KEY_ADVERT_INSTALLATION), false);
    }

    private void performGpsInitialFix() {
        UtilMethods.showToast(context, view.getString(R.string.gps_first_fix_toast));
        setGpsIconActive();
    }

    public void setGpsIconActive() {
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
        view.setFuelLeft(tripProcessor.getFuelLeftString(view.getString(R.string.distance_prefix)));
        view.showFab();
    }

    private void setInternalSettingsToTripProcessor() {
        getInternalSettings();
        tripProcessor.setFuelCapacity(fuelCapacityFromSettings);
        tripProcessor.setFuelConsumption(fuelConsFromSettings);
        tripProcessor.setFuelPrice(fuelPriceFromSettings);
    }

    private void restoreStateIfPossible() {
        if (state != null && !fileErased) {
            restoreState(state);
        } else {
            fileErased = false;
        }
    }

    private void saveState() {
        if (state == null) {
            state = new Bundle();
        }
        onSave(state);
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

    private void animateSpeedUpdate(final float speed) {
        view.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setSpeedometerValue(speed);
                view.updateSpeedometerTextField(speed);
            }
        });
    }

    private void setupTripProcessor() {
        if (tripProcessor == null) {
            if (state != null) {
                restoreState(state);
            } else {
                tripProcessor = new TripProcessor(fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings, this);
            }
            if (UtilMethods.isPermissionAllowed(context)) {
                gpsStatusListener(REGISTER);
            } else {
                requestLocationPermissions();
            }
        }
    }

    private void gpsStatusListener(final boolean register) {
        locationManager = getLocationMangerIfNull();
        if (register) {
            if (UtilMethods.isPermissionAllowed(context)) {
                locationManager.addGpsStatusListener(this);
            } else {
                requestLocationPermissions();
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

    private void restoreState(@NonNull final Bundle savedInstanceState) {
        if (view.getFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof MainFragment) {
            firstStart = savedInstanceState.getBoolean(FIRST_START);

            restoreButtonsVisibility(savedInstanceState);
            restoreStatusIconState(savedInstanceState);
            restoreTripProcessor(savedInstanceState);

            restoreFuelLayoutVisibility(firstStart);
            if (isButtonVisible(view.stopButton)) {
                view.showFab();
            }
        }
    }

    private static boolean isButtonVisible(@Nullable final Button button) {
        return button != null && (button.getVisibility() == View.VISIBLE);
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
        return tripProcessor.getFuelLeftString(view.getString(R.string.distance_prefix));
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