package com.example.aprokopenko.triphelper.ui.fragment;

import android.Manifest;
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
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Main {@link Fragment} representing a {@link TripHelperGauge} and buttons for start, stop, setting, etc.
 */
@Singleton
public class MainFragment extends Fragment implements GpsStatus.Listener, FileEraseListener, SpeedChangeListener, FuelChangeAmountListener {
  @BindView(R.id.speedometerContainer)
  TripHelperGauge speedometerContainer;
  @BindView(R.id.text_SpeedometerView)
  TextView speedometerTextView;
  @BindView(R.id.refillButtonLayout)
  LinearLayoutCompat refillButtonLayout;
  @BindView(R.id.btn_tripList)
  ImageButton tripListButton;
  @BindView(R.id.image_statusView)
  ImageView statusImage;
  @BindView(R.id.btn_start)
  Button startButton;
  @BindView(R.id.btn_stop)
  Button stopButton;
  @BindView(R.id.fuel_left_layout)
  LinearLayoutCompat fuel_left_layout;
  @BindView(R.id.text_fuelLeftView)
  TextView fuelLeft;
  @BindView(R.id.btn_settings)
  ImageButton settingsButton;
  @BindView(R.id.advView)
  com.google.android.gms.ads.AdView advertView;

  private static final int LOCATION_REQUEST_CODE = 1;
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private static final String LOG_TAG = "MainFragment";
  private static final String CONTROL_BUTTON_VISIBILITY = "ControlButtonVisibility";
  private static final String STATUS_IMAGE_STATE = "StatusImageState";
  private static final String FIRST_START = "FirstStart";
  private static final String AVG_SPEED_LIST = "AvgSpeedList";
  private static final String TRIP_PROCESSOR = "TripProcessor";
  private static final String MEASUREMENT_UNIT = "measurementUnit";
  private static final String GRANT = "GRANT";
  private static final String MEASUREMENT_UNIT_POSITION = "measurementUnitPosition";

  private static final boolean REMOVE = false;
  private static final boolean REGISTER = true;

  private boolean firstStart = true;
  private boolean fileErased;
  private boolean gpsIsActive;
  private long gpsFirstFixTime;

  private TripProcessor tripProcessor;
  private LocationManager locationManager;
  private SharedPreferences preferences;
  private TripHelperGauge speedometer;
  private MapFragment mapFragment;
  private Context context;
  private Bundle state;
  private Unbinder unbinder;

  private int fuelCapacityFromSettings;
  private float fuelPriceFromSettings;
  private float fuelConsFromSettings;

  public static MainFragment newInstance() {
    return new MainFragment();
  }

  public MainFragment() {
    // Required empty public constructor
  }

  @Override public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    unbinder = ButterKnife.bind(this, view);
    gpsIsActive = false;

    if (BuildConfig.FLAVOR.contains(getString(R.string.paidVersion_code))) {
      advertView.setVisibility(View.GONE);
    }

    state = getSavedStateInstanceIfPossible(savedInstanceState);
    getContextIfNull();
    getInternalSettings();
    getStateFromPrefs();

    setupSpeedometerView();
    setupTripProcessor();
    visualizeSpeedometer();
  }

  @Override public void onAttach(@NonNull final Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override public void onSaveInstanceState(@NonNull final Bundle outState) {
    if (getFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof MainFragment) {
      outState.putBoolean(CONTROL_BUTTON_VISIBILITY, isButtonVisible(startButton));
      outState.putBoolean(STATUS_IMAGE_STATE, gpsIsActive);
      outState.putBoolean(FIRST_START, firstStart);
      outState.putStringArrayList(AVG_SPEED_LIST, ConfigureAvgSpdListToStore(tripProcessor));
      outState.putParcelable(TRIP_PROCESSOR, tripProcessor);
      super.onSaveInstanceState(outState);

      if (DEBUG) {
        Log.i(LOG_TAG, "onSaveInstanceState: Save called");
        Log.d(LOG_TAG, "onSaveInstanceState: ControlButtons" + isButtonVisible(startButton));
        Log.d(LOG_TAG, "onSaveInstanceState: StatusIm" + gpsIsActive);
        Log.d(LOG_TAG, "onSaveInstanceState: FirstStart" + firstStart);
      }
    }
  }

  @Override public void onPause() {
    final Fragment fragment = getFragmentManager().findFragmentById(R.id.fragmentContainer);
    if (advertView != null) {
      advertView.pause();
    }
    if (fragment != null && fragment instanceof MainFragment) {
      turnOffGpsIfAdapterDisabled();
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
    restoreStateIfPossible();
    setupFuelFields();

    UtilMethods.showFab(getActivity());
    turnOffGpsIfAdapterDisabled();
    super.onResume();
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

  @Override public void speedChanged(final float speed) {
    animateSpeedUpdate(speed, speedometer, speedometerTextView, this);
  }

  @Override public void onFileErased() {
    tripProcessor.eraseTripData();
    fileErased = true;
  }

  @Override public void onGpsStatusChanged(final int event) {
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

  @Override public void fuelFilled(float fuelFilled) {
    fillGasTank(fuelFilled);
  }

  @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_settings, R.id.btn_tripList, R.id.refillButtonLayout})
  public void onClickButton(@NonNull final View view) {
    switch (view.getId()) {
      case R.id.btn_start:
        startTrip();
        break;
      case R.id.btn_stop:
        endTrip();
        break;
      case R.id.btn_settings:
        saveState();
        UtilMethods.hideFab(getActivity());
        showSettingsFragment(SettingsFragment.newInstance(), this);
        break;
      case R.id.btn_tripList:
        if (tripProcessor.isFileNotInWriteMode()) {
          final TripData tripData = tripProcessor.getTripData();
          if (tripData != null && !isButtonVisible(stopButton)) {
            if (!tripData.getTrips().isEmpty()) {
              saveState();
              UtilMethods.hideFab(getActivity());
              showTripListFragment(tripData, TripListFragment.newInstance(tripData), this);
            }
          }
        }
        break;
      case R.id.refillButtonLayout:
        if (tripProcessor.isFileNotInWriteMode()) {
          showFuelFillDialog(FuelFillDialog.newInstance(), this);
        }
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  /**
   * Method for open map, {@link MapFragment}.
   */
  public void openMapFragment() {
    configureMapFragment(tripProcessor);
    saveState();
    UtilMethods.replaceFragment(mapFragment, ConstantValues.MAP_FRAGMENT_TAG, getActivity());
  }

  /**
   * Method form safe exit with cleaning all services, process, etc.
   */
  public void performExit() {
    cleanAllProcess();
  }

  private static void showFuelFillDialog(@NonNull final FuelFillDialog dialog, @NonNull final MainFragment fragment) {
    dialog.setFuelChangeAmountListener(fragment);
    dialog.show(fragment.getChildFragmentManager(), "FILL_DIALOG");
  }

  private static void showTripListFragment(@NonNull final TripData tripData, @NonNull final TripListFragment tripListFragment, @NonNull final Fragment fragment) {
    tripListFragment.setTripData(tripData);
    UtilMethods.replaceFragment(tripListFragment, ConstantValues.TRIP_LIST_TAG, fragment.getActivity());
  }

  private static void showSettingsFragment(@NonNull final SettingsFragment settingsFragment, @NonNull final Fragment fragment) {
    settingsFragment.setFileEraseListener((MainFragment) fragment);
    UtilMethods.replaceFragment(settingsFragment, ConstantValues.SETTINGS_FRAGMENT_TAG, fragment.getActivity());
  }

  private void setupAdvert() {
    if (!BuildConfig.FLAVOR.contains(getString(R.string.paidVersion_code))) {
      if (!advertInstalled()) {
        //                AdRequest adRequest = new AdRequest.Builder().build();
        //                advertView.loadAd(adRequest);
        setupAdView(setupAdRequest());
        setAdvertInstalled(true);
      }
    }
  }

  private void setAdvertInstalled(final boolean isAdvInstalled) {
    final SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(getString(R.string.PREFERENCE_KEY_ADVERT_INSTALLATION), isAdvInstalled).apply();
  }

  private void setupAdView(@NonNull final AdRequest request) {
    advertView.loadAd(request);
    advertView.setAdListener(new AdListener() {
      @Override public void onAdClosed() {
        super.onAdClosed();
      }

      @Override public void onAdFailedToLoad(final int i) {
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
    return new
            AdRequest.Builder()
            .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
            .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
            .setGender(AdRequest.GENDER_MALE)
            .setLocation(getLocationForAdvert())
            .build();
  }

  private boolean advertInstalled() {
    return preferences.getBoolean(getString(R.string.PREFERENCE_KEY_ADVERT_INSTALLATION), false);
  }

  private Location getLocationForAdvert() {
    locationManager = getLocationMangerIfNull();
    final Location lastKnownLocation = getLastKnownLocation(locationManager);
    if (DEBUG) {
      Log.i(LOG_TAG, "getLocationForAdvert: ");
    }
    if (lastKnownLocation != null) {
      return lastKnownLocation;
    } else {
      return getLocationIfLastKnownIsNull(locationManager);
    }
  }

  private static Location getLastKnownLocation(@NonNull final LocationManager locationManager) {
    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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

  private static Looper addLooper() {
    Looper.prepare();
    return setLooper(Looper.myLooper());
  }

  private static void requestSingleGpsUpdate(@NonNull final Looper looper, @NonNull final LocationListener locationListener, @NonNull final LocationManager locationManager) {
    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, looper);
  }

  @NonNull private static LocationListener setLocationListener(@NonNull final Location[] locations) {
    return new LocationListener() {
      @Override public void onLocationChanged(@NonNull final Location location) {
        locations[1] = location;
      }

      @Override public void onStatusChanged(@NonNull final String provider, final int status, @Nullable final Bundle extras) {
      }

      @Override public void onProviderEnabled(@NonNull final String provider) {
      }

      @Override public void onProviderDisabled(@NonNull final String provider) {
      }
    };
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

  private static Bundle getSavedStateInstanceIfPossible(@Nullable final Bundle savedInstanceState) {
    if (savedInstanceState != null && savedStateIsCorrect(savedInstanceState)) {
      return savedInstanceState;
    } else {
      return null;
    }
  }

  @Override public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == LOCATION_REQUEST_CODE && grantResults.length == 2) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
        configureTripProcessor();
      } else {
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

  @NonNull private static ArrayList<String> ConfigureAvgSpdListToStore(@NonNull final TripProcessor tripProcessor) {
    final ArrayList<Float> avgSpeedArrayList = tripProcessor.getAvgSpeedList();
    final ArrayList<String> avgStrArrList = new ArrayList<>();

    if (avgSpeedArrayList != null) {
      for (final float avgListItem : avgSpeedArrayList) {
        avgStrArrList.add(String.valueOf(avgListItem));
      }
    }

    return avgStrArrList;
  }

  private void restoreStateIfPossible() {
    if (state != null && !fileErased) {
      restoreState(state);
    } else {
      fileErased = false;
    }
  }

  private LocationManager getLocationMangerIfNull() {
    if (locationManager == null) {
      locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }
    return locationManager;
  }

  private void performGpsInitialFix() {
    if (DEBUG) {
      Log.d(LOG_TAG, "onGpsStatusChanged: EventFirstFix");
    }
    UtilMethods.showToast(context, getString(R.string.gps_first_fix_toast));
    setGpsIconActive();
  }

  private void deactivateGpsStatusIcon() {
    setGpsIconNotActive();
    gpsFirstFixTime = System.currentTimeMillis();
  }

  private void checkIfSatellitesAreStillAvailableWithInterval(final long interval) {
    final long curTime = System.currentTimeMillis();

    if ((curTime - gpsFirstFixTime) > interval) {
      if (UtilMethods.checkIfGpsEnabled(context)) {
        for (final GpsSatellite satellite : getSatellitesList(getActivity(), locationManager)) {
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

  private TripHelperGauge createSpeedometerGauge(@NonNull final Context context, final boolean isLandscape) {
    return CircularGaugeFactory.getConfiguredSpeedometerGauge(context, preferences.getString(MEASUREMENT_UNIT, ""), isLandscape);
  }

  private static boolean isButtonVisible(@Nullable final Button button) {
    return button != null && (button.getVisibility() == View.VISIBLE);
  }

  private static boolean savedStateIsCorrect(@NonNull final Bundle savedState) {
    return savedState.containsKey(CONTROL_BUTTON_VISIBILITY);
  }

  private boolean landscapeOrientation() {
    return getResources().getConfiguration().orientation == 2;
  }

  private void turnOffGpsIfAdapterDisabled() {
    if (!UtilMethods.checkIfGpsEnabled(context)) {
      setGpsIconNotActive();
    }
  }

  private void requestLocationPermissions() {
    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,},
            LOCATION_REQUEST_CODE);
  }

  private void requestPermissionWithRationale() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
      Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.permissionExplanation), Snackbar.LENGTH_INDEFINITE)
              .setAction(GRANT, new View.OnClickListener() {
                @Override public void onClick(@NonNull final View v) {
                  requestLocationPermissions();
                }
              }).show();
    } else {
      ActivityCompat.requestPermissions(getActivity(),
              new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
              LOCATION_REQUEST_CODE);
    }
  }

  private void setupTripProcessor() {
    if (tripProcessor == null) {
      if (state != null) {
        restoreState(state);
      } else {
        tripProcessor = new TripProcessor(context, fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings);
      }
      if (UtilMethods.isPermissionAllowed(context)) {
        registerListenersToTripProcessor();
      } else {
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
    fuelLeft.setText(tripProcessor.getFuelLeftString(getString(R.string.distance_prefix)));
  }

  private static void setFirstStartToFalse(@NonNull final SharedPreferences preferences) {
    final SharedPreferences.Editor editor = preferences.edit();
    editor
            .putBoolean(FIRST_START, false)
            .apply();
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
    final ReadInternalFile readFileTask = new ReadInternalFile();
    readFileTask.execute();
  }

  private void getStateFromPrefs() {
    preferences = TripHelperApp.getSharedPreferences();
    if (preferences.getBoolean(FIRST_START, true)) {
      UtilMethods.firstStartTutorialDialog(context);
      setFirstStartToFalse(preferences);
    }
    CalculationUtils.setMeasurementMultiplier(preferences.getInt(MEASUREMENT_UNIT_POSITION, 0));
  }

  private void setButtonsVisibilityDuringWriteMode(final int visibility) {
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

  private void restoreButtonsVisibility(@NonNull final Boolean visibility) {
    if (visibility) {
      startButtonTurnActive();
    } else {
      stopButtonTurnActive();
    }
  }

  private void restoreTripProcessor(@Nullable final TripProcessor restoredTripProcess) {
    if (tripProcessor == null) {
      if (restoredTripProcess != null) {
        tripProcessor = restoredTripProcess;
      } else {
        tripProcessor = new TripProcessor(context, fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings);
      }
    }
  }

  private void restoreAvgSpeedList(@NonNull final ArrayList<String> avgSpeedList) {
    tripProcessor.restoreAvgList(avgSpeedList);
  }

  private void restoreFuelLayoutVisibility(final boolean firstState) {
    if (!firstState) {
      fuel_left_layout.setVisibility(View.VISIBLE);
    }
  }

  private void restoreState(@NonNull final Bundle savedInstanceState) {
    if (getFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof MainFragment) {
      getContextIfNull();
      firstStart = savedInstanceState.getBoolean(FIRST_START);

      if (DEBUG) {
        Log.i(LOG_TAG, "onViewCreated: calledRestore");
        Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getBoolean(FIRST_START));
        Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getBoolean(CONTROL_BUTTON_VISIBILITY));
        Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getBoolean(STATUS_IMAGE_STATE));
      }

      restoreButtonsVisibility(savedInstanceState.getBoolean(CONTROL_BUTTON_VISIBILITY));
      restoreStatus(savedInstanceState.getBoolean(STATUS_IMAGE_STATE));
      restoreTripProcessor((TripProcessor) savedInstanceState.getParcelable(TRIP_PROCESSOR));
      restoreAvgSpeedList(savedInstanceState.getStringArrayList(AVG_SPEED_LIST));
      restoreFuelLevel();
      restoreFuelLayoutVisibility(firstStart);

      if (isButtonVisible(stopButton)) {
        UtilMethods.showFab(getActivity());
      }
    }
  }

  private void restoreStatus(@NonNull final Boolean status) {
    if (status) {
      setGpsIconActive();
    }
  }

  private void fillGasTank(final float fuel) {
    fuelLeft.setText(tripProcessor.getFuelLevel(fuel, getString(R.string.distance_prefix)));
  }

  private void restoreFuelLevel() {
    fuelLeft.setText(tripProcessor.getFuelLeftString(getString(R.string.distance_prefix)));
  }

  private void gpsStatusListener(final boolean register) {
    if (register) {
      // TODO: 24.06.2016 fix permission issue here. Need to add condition when addListener is allowed
      if (UtilMethods.isPermissionAllowed(context)) {
        locationManager = getLocationMangerIfNull();
        locationManager.addGpsStatusListener(this);
      } else {
        requestLocationPermissions();
      }
    } else {
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
    UtilMethods.showFab(getActivity());
    UtilMethods.showToast(context, getString(R.string.trip_started_toast));
    stopButtonTurnActive();
  }

  private void endTrip() {
    stopTracking();
    UtilMethods.showToast(context, getString(R.string.trip_ended_toast));
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
    if (statusImage != null) {
      setAppropriateImageStatusColor(ContextCompat.getDrawable(context, R.drawable.green_satellite));
      gpsIsActive = true;
      gpsFirstFixTime = System.currentTimeMillis();
    }
  }

  private void setAppropriateImageStatusColor(@NonNull final Drawable greenSatellite) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      statusImage.setBackground(greenSatellite);
    } else {
      statusImage.setImageDrawable(greenSatellite);
    }
  }

  private void visualizeSpeedometer() {
    setSpeedometerLayoutParams(!landscapeOrientation());
    speedometerContainer.setVisibility(View.VISIBLE);
  }

  private void setSpeedometerLayoutParams(final boolean isPortrait) {
    fuel_left_layout.post(new Runnable() {
      @Override public void run() {
        speedometerContainer.setLayoutParams(getLayoutParams(isPortrait, speedometer));
        speedometerContainer.setVisibility(View.VISIBLE);
        speedometerContainer.invalidate();
      }
    });
  }

  private static LinearLayoutCompat.LayoutParams getLayoutParams(final boolean isPortrait, @NonNull final TripHelperGauge speedometer) {
    final LinearLayoutCompat.LayoutParams layoutParams;
    final int dimension;
    if (isPortrait) {
      dimension = speedometer.getWidth();
      layoutParams = new LinearLayoutCompat.LayoutParams(dimension, dimension);
      layoutParams.setMargins(7, 2, 2, 2);
    } else {
      dimension = speedometer.getHeight();
      layoutParams = new LinearLayoutCompat.LayoutParams(dimension, dimension);
      layoutParams.setMargins(2, 2, 2, 2);
    }
    return layoutParams;
  }

  private void setGpsIconNotActive() {
    getContextIfNull();
    ButterKnife.bind(R.id.image_statusView, getActivity());
    if (statusImage != null) {
      setAppropriateImageStatusColor(ContextCompat.getDrawable(context, R.drawable.red_satellite));
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
      speedometer = createSpeedometerGauge(context, true);
    } else {
      speedometer = createSpeedometerGauge(context, false);
    }
    speedometer.setScaleX(ConstantValues.SPEEDOMETER_WIDTH);
    speedometer.setScaleY(ConstantValues.SPEEDOMETER_HEIGHT);
    speedometerContainer.addView(speedometer);
    speedometerTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), getString(R.string.speedometer_font)));
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

  private void configureMapFragment(@NonNull final TripProcessor tripProcessor) {
    mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ConstantValues.MAP_FRAGMENT_TAG);
    if (mapFragment == null) {
      mapFragment = MapFragment.newInstance();
    }
    mapFragment.setGpsHandler(tripProcessor.getGpsHandler());
    setRoutesToMapFragment();
  }

  private void setRoutesToMapFragment() {
    if (tripProcessor.getRoutes() != null) {
      mapFragment.setRoutes(tripProcessor.getRoutes());
    }
  }

  private static void animateSpeedUpdate(final float speed, @NonNull final TripHelperGauge speedometer, @NonNull final TextView speedometerTextView, @NonNull final Fragment fragment) {
    fragment.getActivity().runOnUiThread(new Runnable() {
      @Override public void run() {
        updateSpeedometerNeedleLocation(speed, speedometer);
        updateSpeedometerTextField(speed, speedometerTextView);
      }
    });
  }

  private static void updateSpeedometerNeedleLocation(final float speed, @NonNull final TripHelperGauge speedometer) {
    final GaugePointer pointer = speedometer.getGaugeScales().get(0).getGaugePointers().get(0);
    if (pointer != null) {
      pointer.setValue(speed);
    }
  }

  private static void updateSpeedometerTextField(final float speed, @NonNull final TextView speedometerTextView) {
    final String formattedSpeed = UtilMethods.formatFloatToIntFormat(speed);
    UtilMethods.animateTextView(Integer.valueOf(speedometerTextView.getText().toString()), Integer.valueOf(formattedSpeed), speedometerTextView);
    speedometerTextView.setText(formattedSpeed);
  }

  private class ReadInternalFile extends AsyncTask<String, Void, Boolean> {
    @Override protected Boolean doInBackground(@Nullable final String... params) {
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
        } catch (IOException e) {
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

    @Override protected void onPostExecute(@NonNull final Boolean result) {
      if (result) {
        super.onPostExecute(result);
      }
    }
  }
}