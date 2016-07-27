package com.example.aprokopenko.triphelper.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
    RelativeLayout speedometerContainer;
    @BindView(R.id.text_SpeedometerView)
    TextView       speedometerTextView;
    @BindView(R.id.refillButtonLayout)
    RelativeLayout refillButtonLayout;
    @BindView(R.id.btn_TripList)
    ImageButton    tripListButton;
    @BindView(R.id.image_statusView)
    ImageView      statusImage;
    @BindView(R.id.btn_start)
    Button         startButton;
    @BindView(R.id.btn_stop)
    Button         stopButton;
    @BindView(R.id.fuelLayout)
    RelativeLayout fuelLayout;
    @BindView(R.id.text_fuelLeftView)
    TextView       fuelLeft;
    @BindView(R.id.btn_Settings)
    ImageButton    settingsButton;

    private static final int     LOCATION_REQUEST_CODE = 1;
    private static final boolean DEBUG                 = BuildConfig.DEBUG;
    private static final String  LOG_TAG               = "MainFragment";
    private static final boolean REMOVE                = false;
    private static final boolean REGISTER              = true;

    private boolean firstStart = true;
    private boolean fileErasedFlag;
    private boolean gpsIsActive;

    private TripProcessor     tripProcessor;
    private SharedPreferences preferences;
    private TripHelperGauge   speedometer;
    private MapFragment       mapFragment;
    private Context           context;
    private Bundle            state;
    private Unbinder          unbinder;

    private int   fuelCapacityFromSettings;
    private float fuelPriceFromSettings;
    private float fuelConsFromSettings;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                tripProcessor.performExit();
                tripProcessor = null;

                tripProcessor = new TripProcessor(context, fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings);
                tripProcessor.setSpeedChangeListener(this);
                gpsStatusListener(REGISTER);
            }
            else {
                requestPermissionWithRationale();
            }
        }

    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        gpsIsActive = false;

        if (savedInstanceState != null && savedStateIsCorrect(savedInstanceState)) {
            state = savedInstanceState;
        }
        getContextIfNull();
        getInternalSettings();
        getStateFromPrefs();

        setupButtons();
        setupSpeedometer();
        setupTripProcessor();
        setupFuelFields();

        visualizeSpeedometer();
    }

    @Override public void onResume() {
        UtilMethods.checkIfGpsEnabledAndShowDialogs(context);
        // TODO: 07.06.2016 not working due to problems with WakeLock that calling in OnLocationChanged,whatever you do..
        //        changeWakeLockStateAfterSettings();
        if (state != null && !fileErasedFlag) {
            restoreState(state);
        }
        else {
            fileErasedFlag = false;
        }
        setInternalSettingsToTripProcessor();
        UtilMethods.setFabVisible(getActivity());
        checkGpsStatus();
        super.onResume();
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        final Fragment f = getFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof MainFragment) {
            ArrayList<Float>  avgSpeedArrayList = tripProcessor.getAvgSpeedArrayList();
            ArrayList<String> avgStrArrList     = new ArrayList<>();
            if (avgSpeedArrayList != null) {
                for (float avgListItem : avgSpeedArrayList) {
                    avgStrArrList.add(String.valueOf(avgListItem));
                }
            }
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

            DataHolderFragment dataHolder = (DataHolderFragment) getActivity().getSupportFragmentManager()
                    .findFragmentByTag(ConstantValues.DATA_HOLDER_TAG);
            dataHolder.setTripData(tripProcessor.getTripData());

            super.onSaveInstanceState(outState);
        }
    }

    @Override public void onPause() {
        final Fragment f = getFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f != null && f instanceof MainFragment) {
            checkGpsStatus();
            saveState();
        }

        super.onPause();
    }

    @Override public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override public void onDetach() {
        if (DEBUG) {
            Log.i(LOG_TAG, "onDetach: called");
        }
        super.onDetach();
    }

    private void checkGpsStatus() {
        if (!UtilMethods.checkIfGpsEnabled(context)) {
            setGpsIconNotActive();
        }
    }

    private void saveState() {
        if (state == null) {
            state = new Bundle();
        }
        onSaveInstanceState(state);
    }

    private void setGpsIconNotActive() {
        getContextIfNull();
        ButterKnife.bind(R.id.image_statusView, getActivity());
        Drawable redSatellite = ContextCompat.getDrawable(context, R.drawable.red_satellite);
        if (statusImage != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                statusImage.setBackground(redSatellite);
            }
            else {
                statusImage.setImageDrawable(redSatellite);
            }
            gpsIsActive = false;
        }
    }

    private void getContextIfNull() {
        if (context == null) {
            context = getActivity();
        }
    }

    private boolean isButtonVisible(Button button) {
        Boolean visibility = false;
        if (button != null) {
            visibility = (button.getVisibility() == View.VISIBLE);
        }
        return visibility;
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

    private void setInternalSettingsToTripProcessor() {
        getInternalSettings();
        tripProcessor.setFuelCapacity(fuelCapacityFromSettings);
        tripProcessor.setFuelConsFromSettings(fuelConsFromSettings);
        tripProcessor.setFuelPrice(fuelPriceFromSettings);
    }

    private void restoreButtonsVisibility(Boolean visibility) {
        if (visibility) {
            startButtonTurnActive();
        }
        else {
            stopButtonTurnActive();
        }
    }

    private void restoreStatus(Boolean status) {
        if (status) {
            setGpsIconActive();
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

    private void restoreFuelLevel() {
        String fuelString = tripProcessor.getFuelLeftString(getString(R.string.distance_prefix));
        fuelLeft.setText(fuelString);
    }

    private void restoreFuelLayoutVisibility(boolean firstState) {
        if (!firstState) {
            fuelLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getInternalSettings() {
        readInternalDataFromFile();
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

    private void setGpsIconActive() {
        getContextIfNull();
        ButterKnife.bind(R.id.image_statusView, getActivity());
        Drawable greenSatellite = ContextCompat.getDrawable(context, R.drawable.green_satellite);
        if (statusImage != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                statusImage.setBackground(greenSatellite);
            }
            else {
                statusImage.setImageDrawable(greenSatellite);
            }
            gpsIsActive = true;
        }
    }

    private void readInternalDataFromFile() {
        ReadInternalFile readFileTask = new ReadInternalFile();
        readFileTask.execute();
    }

    private void setButtonsVisibilityDuringWriteMode(int visibility) {
        tripListButton.setVisibility(visibility);
        refillButtonLayout.setVisibility(visibility);
        settingsButton.setVisibility(visibility);
    }

    private void gpsStatusListener(boolean register) {
        if (register) {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            // TODO: 24.06.2016 fix permission issue here. Need to add condition when addListener is allowed
            if (UtilMethods.isPermissionAllowed(context)) {
                lm.addGpsStatusListener(this);
            }
            else {
                requestLocationPermissions();
            }
        }
        else {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            lm.removeGpsStatusListener(this);
        }
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

    private void requestLocationPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,},
                LOCATION_REQUEST_CODE);
    }

    @Override public void speedChanged(float speed) {
        animateSpeedUpdate(speed);
    }

    private void animateSpeedUpdate(final float speed) {
        getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                updatePointerLocation(speed);
                updateSpeedTextField(speed);
            }
        });
    }

    private void updatePointerLocation(float speed) {
        GaugePointer pointer = speedometer.getGaugeScales().get(0).getGaugePointers().get(0);
        if (pointer != null) {
            pointer.setValue(speed);
        }
    }

    private void updateSpeedTextField(float speed) {
        if (speedometerTextView != null) {
            String        formattedSpeed = UtilMethods.formatFloatToIntFormat(speed);
            final int     initialValue   = Integer.valueOf(speedometerTextView.getText().toString());
            final Integer finalValue     = Integer.valueOf(formattedSpeed);
            UtilMethods.animateTextView(initialValue, finalValue, speedometerTextView);
            speedometerTextView.setText(formattedSpeed);
        }
    }

    @Override public void onFileErased() {
        tripProcessor.eraseTripData();
        fileErasedFlag = true;
    }

    @Override public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                if (DEBUG) {
                    Log.d(LOG_TAG, "onGpsStatusChanged: EventFirstFix");
                }
                UtilMethods.showToast(context, context.getString(R.string.gps_first_fix_toast));
                setGpsIconActive();
                break;
        }
    }

    public void openMapFragment() {
        configureMapFragment();
        saveState();
        mapFragment.setGpsHandler(tripProcessor.getGpsHandler());
        setRouteToMapFragment();
        UtilMethods.replaceFragment(mapFragment, ConstantValues.MAP_FRAGMENT_TAG, getActivity());
    }

    private void configureMapFragment() {
        mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ConstantValues.MAP_FRAGMENT_TAG);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
        }
    }

    private void setRouteToMapFragment() {
        if (tripProcessor.getRoutes() != null) {
            mapFragment.setRoutes(tripProcessor.getRoutes());
        }
    }

    public void performExit() {
        cleanAllProcess();
    }

    private void cleanAllProcess() {
        if (isButtonVisible(stopButton)) {
            stopTracking();
        }
        tripProcessor.performExit();
        gpsStatusListener(REMOVE);
        tripProcessor = null;
        mapFragment = null;
        preferences = null;
        context = null;
    }

    private void stopTracking() {
        tripProcessor.stopTracking();
        if (firstStart) {
            fuelLayout.setVisibility(View.VISIBLE);
            firstStart = false;
        }
        restoreFuelLevel();
    }

    private TripHelperGauge createSpeedometerGauge(Context context) {
        CircularGaugeFactory circularGaugeFactory = new CircularGaugeFactory();
        return circularGaugeFactory.getConfiguredSpeedometerGauge(context, preferences.getString("measurementUnit", ""));
    }

    private boolean savedStateIsCorrect(Bundle savedState) {
        return savedState.containsKey("ControlButtonVisibility");
    }

    private void visualizeSpeedometer() {
        if (getResources().getConfiguration().orientation == 2) {//if landscape
            setSpeedometerLayoutParams();
        }
        else {
            speedometerContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setSpeedometerLayoutParams() {
        fuelLayout.post(new Runnable() {
            @Override public void run() {
                if (fuelLayout != null) {
                    RelativeLayout.LayoutParams layoutParams = getLayoutParams();
                    speedometerContainer.setLayoutParams(layoutParams);
                    speedometerContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private RelativeLayout.LayoutParams getLayoutParams() {
        DisplayMetrics dis     = getResources().getDisplayMetrics();
        int            w       = dis.heightPixels;
        int            padding = (int) (fuelLayout.getMeasuredWidth() * 1.1);//get padding for speedometer

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(w, w);
        layoutParams.setMargins(padding, 7, 7, 7);
        return layoutParams;
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
                tripProcessor.setSpeedChangeListener(this);
                gpsStatusListener(REGISTER);
            }
            else {
                requestLocationPermissions();
            }
        }
    }

    private void setupFuelFields() {
        String fuelLeftString = tripProcessor.getFuelLeftString(getString(R.string.distance_prefix));
        fuelLeft.setText(fuelLeftString);
        if (!TextUtils.equals(fuelLeft.getText(), getString(R.string.fuel_left_initial_val))) {
            fuelLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setFirstStartToFalse(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    private void getStateFromPrefs() {
        preferences = TripHelperApp.getSharedPreferences();
        if (preferences.getBoolean("firstStart", true)) {
            UtilMethods.firstStartTutorialDialog(context);
            setFirstStartToFalse(preferences);
        }
        CalculationUtils.setMeasurementMultiplier(preferences.getInt("measurementUnitPosition", 0));
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

    private void fillGasTank(float fuel) {
        fuelLeft.setText(tripProcessor.getFuelLevel(fuel, getString(R.string.distance_prefix)));
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

    private void setupSpeedometer() {
        speedometer = createSpeedometerGauge(context);
        speedometer.setScaleX(ConstantValues.SPEEDOMETER_WIDTH);
        speedometer.setScaleY(ConstantValues.SPEEDOMETER_HEIGHT);
        //todo set invisible here. Can be replaced by some splash screen with reference or something like this.
        speedometerContainer.setVisibility(View.INVISIBLE);
        speedometerContainer.addView(speedometer);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/android_7.ttf");
        speedometerTextView.setTypeface(tf);
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
