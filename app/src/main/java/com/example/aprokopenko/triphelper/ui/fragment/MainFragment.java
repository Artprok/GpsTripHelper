package com.example.aprokopenko.triphelper.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.TripProcessor;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.listener.FileEraseListener;
import com.example.aprokopenko.triphelper.listener.FuelChangeAmountListener;
import com.example.aprokopenko.triphelper.listener.SpeedChangeListener;
import com.example.aprokopenko.triphelper.speedometer_factory.CircularGaugeFactory;
import com.example.aprokopenko.triphelper.speedometer_gauge.GaugePointer;
import com.example.aprokopenko.triphelper.speedometer_gauge.TripHelperGauge;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@Singleton public class MainFragment extends Fragment implements GpsStatus.Listener, FileEraseListener, SpeedChangeListener {
    @BindView(R.id.speedometerContainer)
    RelativeLayout                    speedometerContainer;
    @BindView(R.id.text_SpeedometerView)
    TextView                          speedometerTextView;
    @BindView(R.id.refillButtonLayout)
    RelativeLayout                    refillButtonLayout;
    @BindView(R.id.btn_TripList)
    ImageButton                       tripListButton;
    @BindView(R.id.image_statusView)
    ImageView                         statusImage;
    @BindView(R.id.btn_start)
    Button                            startButton;
    @BindView(R.id.btn_stop)
    Button                            stopButton;
    @BindView(R.id.fuel_left_layout)
    RelativeLayout                    fuel_left_layout;
    @BindView(R.id.text_fuelLeftView)
    TextView                          fuelLeft;
    @BindView(R.id.btn_Settings)
    ImageButton                       settingsButton;
    @BindView(R.id.advView)
    com.google.android.gms.ads.AdView advertView;

    private static final int     LOCATION_REQUEST_CODE = 1;
    private static final boolean DEBUG                 = BuildConfig.DEBUG;

    private static final String  LOG_TAG  = "MainFragment";
    private static final boolean REMOVE   = false;
    private static final boolean REGISTER = true;

    private boolean firstStart = true;
    private boolean fileErased;
    private boolean gpsIsActive;
    private long    gpsFirstFixTime;

    private TripProcessor     tripProcessor;
    private LocationManager   locationManager;
    private SharedPreferences preferences;
    private TripHelperGauge   speedometer;
    private MapFragment       mapFragment;
    private Context           context;
    private Bundle            state;
    private Unbinder          unbinder;

    private int   fuelCapacityFromSettings;
    private float fuelPriceFromSettings;
    private float fuelConsFromSettings;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {
        // Required empty public constructor
    }


    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        gpsIsActive = false;

        if (BuildConfig.FLAVOR.contains(context.getString(R.string.paidVersion_code))) {
            advertView.setVisibility(View.GONE);
        }

        getSavedStateInstanceIfPossible(savedInstanceState);
        getContextIfNull();
        getInternalSettings();
        getStateFromPrefs();

        setupButtons();
        setupSpeedometerView();
        setupTripProcessor();
        visualizeSpeedometer();
    }

    private void setupAdvert() {
        if (!BuildConfig.FLAVOR.contains(context.getString(R.string.paidVersion_code))) {
            if (!advertInstalled()) {
                //                AdRequest adRequest = new AdRequest.Builder().build();
                //                advertView.loadAd(adRequest);
                AdRequest request = setupAdRequest();
                setupAdView(request);
                setAdverInstalled(true);
            }
        }
    }

    private void setAdverInstalled(boolean b) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.PREFERENCE_KEY_ADVERT_INSTALATION), b);
        editor.apply();
    }

    private void setupAdView(AdRequest request) {
        advertView.loadAd(request);
        advertView.setAdListener(new AdListener() {
            @Override public void onAdClosed() {
                super.onAdClosed();
            }

            @Override public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }

            @Override public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override public void onAdOpened() {
                super.onAdOpened();
            }

            @Override public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
    }

    @NonNull private AdRequest setupAdRequest() {
        Location loc = getLocationForAdvert();
        return new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .setGender(AdRequest.GENDER_MALE).setLocation(loc).build();
    }

    private boolean advertInstalled() {
        return preferences.getBoolean(getString(R.string.PREFERENCE_KEY_ADVERT_INSTALATION), false);
    }


    private Location getLocationForAdvert() {
        Log.i(LOG_TAG, "getLocationForAdvert: ");
        Location lastKnownLocation = getLastKnownLocation();
        if (lastKnownLocation != null) {
            return lastKnownLocation;
        }
        else {
            Location locationIfLastKnowNull = getLocationIfLastKnownIsNull();
            return locationIfLastKnowNull;
        }
    }

    private Location getLastKnownLocation() {
        locationManager = getLocationMangerIfNull();
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private Location getLocationIfLastKnownIsNull() {
        final Location[] locationForAd    = new Location[1];
        Looper           looper           = addLooper();
        LocationListener locationListener = setLocationListener(locationForAd);
        requestSingleGpsUpdate(looper, locationListener);
        locationManager.removeUpdates(locationListener);
        stopLooper(looper);
        return locationForAd[0];
    }

    private Looper addLooper() {
        Looper.prepare();
        return setLooper(Looper.myLooper());
    }

    private void requestSingleGpsUpdate(Looper looper, LocationListener locationListener) {
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, looper);
    }

    @NonNull private LocationListener setLocationListener(final Location[] locations) {
        return new LocationListener() {
            @Override public void onLocationChanged(Location location) {
                locations[1] = location;
            }

            @Override public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override public void onProviderEnabled(String provider) {

            }

            @Override public void onProviderDisabled(String provider) {

            }
        };
    }

    private synchronized Looper setLooper(Looper looper) {
        Looper loop = looper;
        return loop;
    }

    private synchronized void stopLooper(Looper looper) {
        if (looper == null) {
            return;
        }
        looper.quit();
    }

    private void getSavedStateInstanceIfPossible(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedStateIsCorrect(savedInstanceState)) {
            state = savedInstanceState;
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                configureTripProcessor();
            }
            else {
                requestPermissionWithRationale();
            }
        }
    }

    private void configureTripProcessor() {
        resetTripProcessor();

        tripProcessor = new TripProcessor(context, fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings);
        registerListenersToTripProcessor();
    }

    private void resetTripProcessor() {
        tripProcessor.performExit();
        tripProcessor = null;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        final Fragment f = getFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof MainFragment) {
            ArrayList<String> avgStrArrList = ConfigureAvgSpdListToStore();
            if (DEBUG) {
                Log.i(LOG_TAG, "onSaveInstanceState: Save called");
                Log.d(LOG_TAG, "onSaveInstanceState: ControlButtons" + isButtonVisible(startButton));
                Log.d(LOG_TAG, "onSaveInstanceState: StatusIm" + gpsIsActive);
                Log.d(LOG_TAG, "onSaveInstanceState: FirstStart" + firstStart);
            }
            outState.putBoolean("ControlButtonVisibility", isButtonVisible(startButton));
            outState.putBoolean("StatusImageState", gpsIsActive);
            outState.putBoolean("FirstStart", firstStart);
            outState.putStringArrayList("AvgSpeedList", avgStrArrList);
            outState.putParcelable("TripProcessor", tripProcessor);

            configureDataHolderToStore();
            super.onSaveInstanceState(outState);
        }
    }

    private void configureDataHolderToStore() {
        DataHolderFragment dataHolder = (DataHolderFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(ConstantValues.DATA_HOLDER_TAG);
        dataHolder.setTripData(tripProcessor.getTripData());
    }

    @NonNull private ArrayList<String> ConfigureAvgSpdListToStore() {
        ArrayList<Float>  avgSpeedArrayList = getAvgSpeedListFromProcessor();
        ArrayList<String> avgStrArrList     = new ArrayList<>();
        if (avgSpeedArrayList != null) {
            for (float avgListItem : avgSpeedArrayList) {
                avgStrArrList.add(String.valueOf(avgListItem));
            }
        }
        return avgStrArrList;
    }

    private ArrayList<Float> getAvgSpeedListFromProcessor() {
        return tripProcessor.getAvgSpeedArrayList();
    }

    @Override public void onPause() {
        if (advertView != null) {
            advertView.pause();
        }
        final Fragment f = getFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f != null && f instanceof MainFragment) {
            turnOffGpsIfAdapterDisabler();
            saveState();
        }

        super.onPause();
    }

    @Override public void onResume() {
        if (advertView != null) {
            advertView.pause();
        }
        UtilMethods.checkIfGpsEnabledAndShowDialogs(context);
        locationManager = getLocationMangerIfNull();
        // TODO: 07.06.2016 not working due to problems with WakeLock that calling in OnLocationChanged,whatever you do..
        //        changeWakeLockStateAfterSettings();
        restoreStateIfPossible();
        setupFuelFields();

        UtilMethods.setFabVisible(getActivity());
        turnOffGpsIfAdapterDisabler();
        super.onResume();
    }

    private void restoreStateIfPossible() {
        if (state != null && !fileErased) {
            restoreState(state);
        }
        else {
            fileErased = false;
        }
    }

    private LocationManager getLocationMangerIfNull() {
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        return locationManager;
    }

    @Override public void onDetach() {
        if (DEBUG) {
            Log.i(LOG_TAG, "onDetach: called");
        }
        super.onDetach();
    }

    @Override public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override public void speedChanged(float speed) {
        animateSpeedUpdate(speed);
    }

    @Override public void onFileErased() {
        tripProcessor.eraseTripData();
        fileErased = true;
    }

    @Override public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                performGpsInitialFix();
                setupAdvert();
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                checkIfSattelitesAreStillAvaliableWithInterval(ConstantValues.FIVE_MINUTES);
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                deactivateGpsStatusIcon();
                break;
        }
    }

    private void performGpsInitialFix() {
        if (DEBUG) {
            Log.d(LOG_TAG, "onGpsStatusChanged: EventFirstFix");
        }
        UtilMethods.showToast(context, context.getString(R.string.gps_first_fix_toast));
        setGpsIconActive();
    }

    private void deactivateGpsStatusIcon() {
        setGpsIconNotActive();
        gpsFirstFixTime = System.currentTimeMillis();
    }

    private void checkIfSattelitesAreStillAvaliableWithInterval(long interval) {
        long curTime = System.currentTimeMillis();
        if ((curTime - gpsFirstFixTime) > interval) {
            if (UtilMethods.checkIfGpsEnabled(context)) {
                Iterable<GpsSatellite> sats = getSattelitesList();
                for (GpsSatellite satellite : sats) {
                    if (satellite.usedInFix()) {
                        setGpsIconActive();
                        gpsFirstFixTime = System.currentTimeMillis();
                    }
                    else {
                        deactivateGpsStatusIcon();
                    }
                }
            }
            else {
                deactivateGpsStatusIcon();
            }
        }
    }

    private Iterable<GpsSatellite> getSattelitesList() {
        GpsStatus status = getGpsStatus();
        return status.getSatellites();
    }

    private GpsStatus getGpsStatus() {
        GpsStatus status;
        if (locationManager != null) {
            status = locationManager.getGpsStatus(null);
        }
        else {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            status = locationManager.getGpsStatus(null);
        }
        return status;
    }

    public void openMapFragment() {
        configureMapFragment();
        saveState();
        mapFragment.setGpsHandler(tripProcessor.getGpsHandler());
        setRoutesToMapFragment();
        UtilMethods.replaceFragment(mapFragment, ConstantValues.MAP_FRAGMENT_TAG, getActivity());
    }

    public void performExit() {
        cleanAllProcess();
    }


    private TripHelperGauge createSpeedometerGauge(Context context) {
        CircularGaugeFactory circularGaugeFactory = new CircularGaugeFactory();
        return circularGaugeFactory.getConfiguredSpeedometerGauge(context, preferences.getString("measurementUnit", ""));
    }

    private TripHelperGauge createSpeedometerGaugeForLandscape(Context context) {
        CircularGaugeFactory circularGaugeFactory = new CircularGaugeFactory();
        return circularGaugeFactory.getConfiguredSpeedometerGaugeForLandscape(context, preferences.getString("measurementUnit", ""));
    }

    private boolean isButtonVisible(Button button) {
        Boolean visibility = false;
        if (button != null) {
            visibility = (button.getVisibility() == View.VISIBLE);
        }
        return visibility;
    }

    private boolean savedStateIsCorrect(Bundle savedState) {
        return savedState.containsKey("ControlButtonVisibility");
    }

    private boolean landscapeOrientation() {
        return getResources().getConfiguration().orientation == 2;
    }

    private void turnOffGpsIfAdapterDisabler() {
        if (!UtilMethods.checkIfGpsEnabled(context)) {
            setGpsIconNotActive();
        }
    }

    private void requestLocationPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,},
                LOCATION_REQUEST_CODE);
    }

    private void requestPermissionWithRationale() {
        Activity activity = getActivity();
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            final String message = getResources().getString(R.string.permissionExplanation);
            Snackbar.make(activity.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            requestLocationPermissions();
                        }
                    }).show();
        }
        else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    private void setupTripProcessor() {
        if (tripProcessor == null) {
            if (state != null) {
                restoreState(state);
            }
            else {
                tripProcessor = new TripProcessor(context, fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings);
            }
            if (UtilMethods.isPermissionAllowed(context)) {
                registerListenersToTripProcessor();
            }
            else {
                requestLocationPermissions();
            }
        }
    }

    private void registerListenersToTripProcessor() {
        tripProcessor.setSpeedChangeListener(this);
        gpsStatusListener(REGISTER);
    }

    private void setupFuelFields() {
        setInternalSettingsToTripProcessor();
        String fuelLeftString = tripProcessor.getFuelLeftString(getString(R.string.distance_prefix));
        fuelLeft.setText(fuelLeftString);
    }

    private void setFirstStartToFalse(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    private void getInternalSettings() {
        readInternalDataFromFile();
    }

    private void setInternalSettingsToTripProcessor() {
        getInternalSettings();
        tripProcessor.setFuelCapacity(fuelCapacityFromSettings);
        tripProcessor.setFuelConsFromSettings(fuelConsFromSettings);
        tripProcessor.setFuelPrice(fuelPriceFromSettings);
    }

    private void readInternalDataFromFile() {
        ReadInternalFile readFileTask = new ReadInternalFile();
        readFileTask.execute();
    }

    private void getStateFromPrefs() {
        preferences = TripHelperApp.getSharedPreferences();
        if (preferences.getBoolean("firstStart", true)) {
            UtilMethods.firstStartTutorialDialog(context);
            setFirstStartToFalse(preferences);
        }
        CalculationUtils.setMeasurementMultiplier(preferences.getInt("measurementUnitPosition", 0));
    }

    private void setButtonsVisibilityDuringWriteMode(int visibility) {
        tripListButton.setVisibility(visibility);
        refillButtonLayout.setVisibility(visibility);
        settingsButton.setVisibility(visibility);
    }

    private void startButtonTurnActive() {
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        setButtonsVisibilityDuringWriteMode(View.VISIBLE);
    }

    private void stopButtonTurnActive() {
        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        setButtonsVisibilityDuringWriteMode(View.INVISIBLE);
    }

    private void setupTripListButton() {
        tripListButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (tripProcessor.isFileNotInWriteMode()) {
                    TripData tripData = tripProcessor.getTripData();
                    if (tripData != null && !isButtonVisible(stopButton)) {
                        if (!tripData.getTrips().isEmpty()) {
                            TripListFragment tripListFragment = TripListFragment.newInstance();
                            saveState();
                            UtilMethods.setFabInvisible(getActivity());
                            tripListFragment.setTripData(tripData);
                            UtilMethods.replaceFragment(tripListFragment, ConstantValues.TRIP_LIST_TAG, getActivity());
                        }
                    }
                }
            }
        });
    }

    private void setupSettingsButton() {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                saveState();
                UtilMethods.setFabInvisible(getActivity());
                SettingsFragment settingsFragment = SettingsFragment.newInstance();
                settingsFragment.setFileEraseListener(MainFragment.this);
                UtilMethods.replaceFragment(settingsFragment, ConstantValues.SETTINGS_FRAGMENT_TAG, getActivity());
            }
        });
    }

    private void setupStartButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startTrip();
            }
        });
    }

    private void setupStopButton() {
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                endTrip();
            }
        });
    }

    private void setupFillButton() {
        refillButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (tripProcessor.isFileNotInWriteMode()) {
                    FuelFillDialog dialog = FuelFillDialog.newInstance();
                    dialog.setFuelChangeAmountListener(new FuelChangeAmountListener() {
                        @Override public void fuelFilled(float fuel) {
                            fillGasTank(fuel);
                        }
                    });
                    dialog.show(getChildFragmentManager(), "FILL_DIALOG");
                }
            }
        });
    }

    private void setupButtons() {
        setupStartButton();
        setupStopButton();
        setupTripListButton();
        setupFillButton();
        setupSettingsButton();
    }

    private void restoreButtonsVisibility(Boolean visibility) {
        if (visibility) {
            startButtonTurnActive();
        }
        else {
            stopButtonTurnActive();
        }
    }

    private void restoreTripProcessor(TripProcessor restoredTripProcess) {
        if (tripProcessor == null) {
            if (restoredTripProcess != null) {
                tripProcessor = restoredTripProcess;
            }
            else {
                tripProcessor = new TripProcessor(context, fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings);
            }
        }
    }

    private void restoreAvgSpeedList(ArrayList<String> avgSpeedList) {
        tripProcessor.restoreAvgList(avgSpeedList);
    }

    private void restoreFuelLayoutVisibility(boolean firstState) {
        if (!firstState) {
            fuel_left_layout.setVisibility(View.VISIBLE);
        }
    }

    private void restoreState(Bundle savedInstanceState) {
        final Fragment f = getFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof MainFragment) {
            getContextIfNull();
            if (DEBUG) {
                Log.i(LOG_TAG, "onViewCreated: calledRestore");
                Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getBoolean("FirstStart"));
                Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getBoolean("ControlButtonVisibility"));
                Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getBoolean("StatusImageState"));
            }
            firstStart = savedInstanceState.getBoolean("FirstStart");
            boolean           restoredButtonVisibility = savedInstanceState.getBoolean("ControlButtonVisibility");
            boolean           restoredStatus           = savedInstanceState.getBoolean("StatusImageState");
            ArrayList<String> restoredAvgSpeedList     = savedInstanceState.getStringArrayList("AvgSpeedList");
            TripProcessor     restoredTripProcessor    = savedInstanceState.getParcelable("TripProcessor");

            restoreButtonsVisibility(restoredButtonVisibility);
            restoreStatus(restoredStatus);
            restoreTripProcessor(restoredTripProcessor);
            restoreAvgSpeedList(restoredAvgSpeedList);
            restoreFuelLevel();
            restoreFuelLayoutVisibility(firstStart);

            if (isButtonVisible(stopButton)) {
                UtilMethods.setFabVisible(getActivity());
            }
        }
    }

    private void restoreStatus(Boolean status) {
        if (status) {
            setGpsIconActive();
        }
    }

    private void fillGasTank(float fuel) {
        fuelLeft.setText(tripProcessor.getFuelLevel(fuel, getString(R.string.distance_prefix)));
    }

    private void restoreFuelLevel() {
        String fuelString = tripProcessor.getFuelLeftString(getString(R.string.distance_prefix));
        fuelLeft.setText(fuelString);
    }

    private void gpsStatusListener(boolean register) {
        if (register) {
            // TODO: 24.06.2016 fix permission issue here. Need to add condition when addListener is allowed
            if (UtilMethods.isPermissionAllowed(context)) {
                locationManager = getLocationMangerIfNull();
                locationManager.addGpsStatusListener(this);
            }
            else {
                requestLocationPermissions();
            }
        }
        else {
            locationManager = getLocationMangerIfNull();
            locationManager.removeGpsStatusListener(this);
        }
    }

    private void stopTracking() {
        tripProcessor.stopTracking();
        if (firstStart) {
            fuel_left_layout.setVisibility(View.VISIBLE);
            firstStart = false;
        }
        restoreFuelLevel();
    }

    private void startTrip() {
        tripProcessor.startNewTrip();
        UtilMethods.setFabVisible(getActivity());
        UtilMethods.showToast(context, context.getString(R.string.trip_started_toast));
        stopButtonTurnActive();
    }

    private void endTrip() {
        stopTracking();
        UtilMethods.showToast(context, context.getString(R.string.trip_ended_toast));
        startButtonTurnActive();
    }

    private void saveState() {
        if (state == null) {
            state = new Bundle();
        }
        onSaveInstanceState(state);
    }

    private void setGpsIconActive() {
        getContextIfNull();
        ButterKnife.bind(R.id.image_statusView, getActivity());
        Drawable greenSatellite = ContextCompat.getDrawable(context, R.drawable.green_satellite);
        if (statusImage != null) {
            setAppropriateImageStatusColor(greenSatellite);
            gpsIsActive = true;
            gpsFirstFixTime = System.currentTimeMillis();
        }
    }

    private void setAppropriateImageStatusColor(Drawable greenSatellite) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            statusImage.setBackground(greenSatellite);
        }
        else {
            statusImage.setImageDrawable(greenSatellite);
        }

    }

    private void visualizeSpeedometer() {
        if (landscapeOrientation()) {
            setSpeedometerLayoutParams();
        }
        else {
            speedometerContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setSpeedometerLayoutParams() {
        fuel_left_layout.post(new Runnable() {
            @Override public void run() {
                RelativeLayout.LayoutParams layoutParams = getLayoutParams();
                speedometerContainer.setLayoutParams(layoutParams);
                speedometerContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    private RelativeLayout.LayoutParams getLayoutParams() {
        int w       = speedometer.getHeight();
        int padding = getLeftPaddingForSpeedometer();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, w);

        layoutParams.setMargins(padding, advertView.getHeight(), 3, 3);
        return layoutParams;
    }

    private int getLeftPaddingForSpeedometer() {
        return (int) (fuel_left_layout.getMeasuredWidth() * 1.1);
    }

    private void setGpsIconNotActive() {
        getContextIfNull();
        ButterKnife.bind(R.id.image_statusView, getActivity());
        Drawable redSatellite = ContextCompat.getDrawable(context, R.drawable.red_satellite);
        if (statusImage != null) {
            setAppropriateImageStatusColor(redSatellite);
            gpsIsActive = false;
        }
    }

    private void getContextIfNull() {
        if (context == null) {
            context = getActivity();
        }
    }

    private void setupSpeedometerView() {
        if (landscapeOrientation() && !BuildConfig.FLAVOR.equals(getString(R.string.paidVersion_code))) {
            speedometer = createSpeedometerGaugeForLandscape(context);
        }
        else {
            speedometer = createSpeedometerGauge(context);
        }
        speedometer.setScaleX(ConstantValues.SPEEDOMETER_WIDTH);
        speedometer.setScaleY(ConstantValues.SPEEDOMETER_HEIGHT);
        speedometerContainer.addView(speedometer);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/android_7.ttf");
        speedometerTextView.setTypeface(tf);
    }

    private void cleanAllProcess() {
        if (isButtonVisible(stopButton)) {
            stopTracking();
        }
        tripProcessor.performExit();
        gpsStatusListener(REMOVE);
        locationManager = null;
        tripProcessor = null;
        mapFragment = null;
        preferences = null;
        context = null;
    }

    private void configureMapFragment() {
        mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ConstantValues.MAP_FRAGMENT_TAG);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
        }
    }

    private void setRoutesToMapFragment() {
        if (tripProcessor.getRoutes() != null) {
            mapFragment.setRoutes(tripProcessor.getRoutes());
        }
    }

    private void animateSpeedUpdate(final float speed) {
        getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                updateSpeedometerNeedleLocation(speed);
                updateSpeedometerTextField(speed);
            }
        });
    }

    private void updateSpeedometerNeedleLocation(float speed) {
        GaugePointer pointer = speedometer.getGaugeScales().get(0).getGaugePointers().get(0);
        if (pointer != null) {
            pointer.setValue(speed);
        }
    }

    private void updateSpeedometerTextField(float speed) {
        if (speedometerTextView != null) {
            String        formattedSpeed = UtilMethods.formatFloatToIntFormat(speed);
            final int     initialValue   = Integer.valueOf(speedometerTextView.getText().toString());
            final Integer finalValue     = Integer.valueOf(formattedSpeed);
            UtilMethods.animateTextView(initialValue, finalValue, speedometerTextView);
            speedometerTextView.setText(formattedSpeed);
        }
    }

    private class ReadInternalFile extends AsyncTask<String, Void, Boolean> {
        @Override protected Boolean doInBackground(String... params) {
            if (DEBUG) {
                Log.i(LOG_TAG, "readFileSettings");
            }
            File file = context.getFileStreamPath(ConstantValues.INTERNAL_SETTING_FILE_NAME);
            if (file.exists()) {
                if (DEBUG) {
                    Log.i(LOG_TAG, "readTripDataFromFileSettings: ");
                }
                FileInputStream fis;
                try {
                    fis = context.openFileInput(ConstantValues.INTERNAL_SETTING_FILE_NAME);
                    ObjectInputStream is          = new ObjectInputStream(fis);
                    float             consumption = is.readFloat();
                    float             fuelPrice   = is.readFloat();
                    int               capacity    = is.readInt();

                    fuelConsFromSettings = consumption;
                    fuelPriceFromSettings = fuelPrice;
                    fuelCapacityFromSettings = capacity;
                    is.close();
                    fis.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
            else {
                fuelConsFromSettings = ConstantValues.FUEL_CONSUMPTION_DEFAULT;
                fuelPriceFromSettings = ConstantValues.FUEL_COST_DEFAULT;
                fuelCapacityFromSettings = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT;
                return false;
            }
        }

        @Override protected void onPostExecute(Boolean result) {
            if (result) {
                super.onPostExecute(result);
            }
        }
    }
}
