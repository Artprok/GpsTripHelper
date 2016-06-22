package com.example.aprokopenko.triphelper.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.annotation.Nullable;
import android.graphics.drawable.Drawable;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.location.GpsStatus;
import android.widget.ImageButton;
import android.graphics.Typeface;
import android.widget.ImageView;
import android.content.Context;
import android.widget.TextView;
import android.view.ViewGroup;
import android.text.TextUtils;
import android.widget.Button;
import android.os.AsyncTask;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.example.aprokopenko.triphelper.speedometerfactory.CircularGaugeFactory;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.listener.FuelChangeAmountListener;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.listener.SpeedChangeListener;
import com.example.aprokopenko.triphelper.listener.FileEraseListener;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.syncfusion.gauges.SfCircularGauge.SfCircularGauge;
import com.syncfusion.gauges.SfCircularGauge.CircularPointer;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.TripProcessor;
import com.example.aprokopenko.triphelper.R;

import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

import butterknife.ButterKnife;
import butterknife.Bind;

public class MainFragment extends Fragment implements GpsStatus.Listener, FileEraseListener, SpeedChangeListener {
    @Bind(R.id.speedometerContainer)
    RelativeLayout speedometerContainer;
    @Bind(R.id.speedometerTextView)
    TextView       speedometerTextView;
    @Bind(R.id.refillButtonLayout)
    RelativeLayout refillButtonLayout;
    @Bind(R.id.tripListButton)
    ImageButton    tripListButton;
    @Bind(R.id.statusImageView)
    ImageView      statusImage;
    @Bind(R.id.startButton)
    Button         startButton;
    @Bind(R.id.stopButton)
    Button         stopButton;
    @Bind(R.id.fuelLayout)
    RelativeLayout fuelLayout;
    @Bind(R.id.fuelLeftView)
    TextView       fuelLeft;
    @Bind(R.id.settingsButton)
    ImageButton    settingsButton;

    private static final String  LOG_TAG  = "MainFragment";
    private static final boolean REMOVE   = false;
    private static final boolean REGISTER = true;

    private boolean fileErasedFlag = false;
    private boolean firstStart     = true;

    private TripProcessor     tripProcessor;
    private SharedPreferences preferences;
    private SfCircularGauge   speedometer;
    private MapFragment       mapFragment;

    private Context context;
    private Bundle  state;

    private int   fuelCapacityFromSettings;
    private float fuelPriceFromSettings;
    private float fuelConsFromSettings;

    // TODO: 07.06.2016 notworking due to problems with WakeLock that calling in OnLocationChanged,whatever you do..
    //    private PowerManager.WakeLock wakeLock;

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
        ButterKnife.bind(this, view);
        if (savedInstanceState != null) {
            state = savedInstanceState;
        }
        getContextIfNull();
        getInternalSettings();
        getStateFromPrefs();

        configureMapFragment();


        setupButtons();
        setupSpeedometer();
        setupTripProcessor();
        setupFuelFields();
        if(!UtilMethods.checkPermission(context)){
            gpsStatusListener(REGISTER);
        }
        else{
            // TODO: 22.06.2016 explain that need to turnOn permi
        }

    }

    private void setupTripProcessor() {
        if (tripProcessor == null) {
            if (state != null) {
                restoreState(state);
            }
            else {
                tripProcessor = new TripProcessor(context, fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings);
                tripProcessor.setSpeedChangeListener(this);
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

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        final Fragment f = getFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof MainFragment) {
            ArrayList<Float> avgSpeedArrayList = tripProcessor.getAvgSpeedArrayList();
            super.onSaveInstanceState(outState);
            ArrayList<String> avgStrArrList = new ArrayList<>();
            if (avgSpeedArrayList != null) {
                for (float avgListItem : avgSpeedArrayList) {
                    avgStrArrList.add(String.valueOf(avgListItem));
                }
            }
            if (ConstantValues.LOGGING_ENABLED) {
                Log.i(LOG_TAG, "onSaveInstanceState: Save called");
                Log.d(LOG_TAG, "onSaveInstanceState: ControlButtons" + isButtonVisible(startButton));
                Log.d(LOG_TAG, "onSaveInstanceState: StatusIm" + getStatusImageState());
                Log.d(LOG_TAG, "onSaveInstanceState: FirstStart" + firstStart);
            }
            outState.putBoolean("ControlButtonVisibility", isButtonVisible(startButton));
            outState.putBoolean("StatusImageState", getStatusImageState());
            outState.putBoolean("FirstStart", firstStart);
            outState.putStringArrayList("AvgSpeedList", avgStrArrList);
            outState.putParcelable("TripProcessor", tripProcessor);
        }

    }

    @Override public void onPause() {
        final Fragment f = getFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof MainFragment) {
            saveState();
        }
        super.onPause();
    }

    @Override public void onResume() {
        // TODO: 07.06.2016 notworking due to problems with WakeLock that calling in OnLocationChanged,whatever you do..
        //        changeWakeLockStateAfterSettings();
        if (state != null && !fileErasedFlag) {
            Log.d(LOG_TAG, "TEST: StateResto&");
            restoreState(state);
        }
        else {
            Log.d(LOG_TAG, "TEST: StateResto&nooooo");
            fileErasedFlag = false;
        }
        setInternalSettingsToTripProcessor();
        super.onResume();
    }

    @Override public void onDetach() {
        if (ConstantValues.LOGGING_ENABLED) {
            Log.i(LOG_TAG, "onDetach: called");
        }
        Log.d(LOG_TAG, "onDetach: TEST CALLED");

        super.onDetach();
    }

    @Override public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }


    @Override public void maxSpeedChanged(float maxSpeed) {
        //        updateMaxSpeed(maxSpeed);
    }

    @Override public void speedChanged(float speed) {
        animateSpeedUpdate(speed);
    }

    @Override public void onFileErased() {
        tripProcessor.eraseTripData();
        fileErasedFlag = true;
    }

    @Override public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "onGpsStatusChanged: EventSatStatus");
                }
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "onGpsStatusChanged: EventFirstFix");
                }
                setStatusImage();
                UtilMethods.showToast(context, context.getString(R.string.gps_first_fix_toast));
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.d(LOG_TAG, "onGpsStatusChanged: eventStarted");
                }
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                break;
        }
    }

    public void openMapFragment() {
        saveState();
        mapFragment.setGpsHandler(tripProcessor.getGpsHandler());
        setRouteToMapFragment();
        UtilMethods.replaceFragment(mapFragment, ConstantValues.MAP_FRAGMENT_TAG, getActivity());
    }

    public void performExit() {
        cleanAllProcess();
    }


    private SfCircularGauge createSpeedometerGauge(Context context) {
        CircularGaugeFactory circularGaugeFactory = new CircularGaugeFactory();
        return circularGaugeFactory.getConfiguredSpeedometerGauge(context, preferences.getString("measurementUnit", ""));
    }

    private boolean isButtonVisible(Button button) {
        Boolean visibility = false;
        if (button != null) {
            visibility = (button.getVisibility() == View.VISIBLE);
        }
        return visibility;
    }

    private boolean getStatusImageState() {
        boolean result;
        getContextIfNull();
        Drawable greenSatellite = ContextCompat.getDrawable(context, R.drawable.green_satellite);
        result = (statusImage != null && (statusImage.getBackground() != greenSatellite));
        return result;
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
                    UtilMethods.setFabInvisible(getActivity());
                    TripData         tripData         = tripProcessor.getTripData();
                    TripListFragment tripListFragment = TripListFragment.newInstance();
                    if (tripData != null && !isButtonVisible(stopButton)) {
                        if (!tripData.getTrips().isEmpty()) {
                            saveState();
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
                    dialog.show(getChildFragmentManager(), "DIALOG");
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

    private void restoreButtonsVisibility(TripProcessor restoredTripProcessor, Boolean visibility) {
        if (visibility || restoredTripProcessor == null) {
            startButtonTurnActive();
        }
        else {
            stopButtonTurnActive();
        }
    }

    private void restoreTripProcessor(TripProcessor restoredTripProcess) {
        if (tripProcessor == null) {
            if (restoredTripProcess != null) {
                Log.d(LOG_TAG, "restoreTripProcessor: TEST notnull");
                tripProcessor = restoredTripProcess;
                tripProcessor.setSpeedChangeListener(this);
            }
            else {
                tripProcessor = new TripProcessor(context, fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings);
                tripProcessor.setSpeedChangeListener(this);
            }
        }
    }

    private void restoreAvgSpeedList(ArrayList<String> avgSpeedList) {
        tripProcessor.restoreAvgList(avgSpeedList);
    }

    private void restoreFuelLayoutVisibility(boolean firstState) {
        if (!firstState) {
            fuelLayout.setVisibility(View.VISIBLE);
        }
    }

    private void restoreState(Bundle savedInstanceState) {
        getContextIfNull();
        if (ConstantValues.LOGGING_ENABLED) {
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

        restoreButtonsVisibility(restoredTripProcessor, restoredButtonVisibility);
        restoreStatus(restoredStatus);
        restoreTripProcessor(restoredTripProcessor);
        restoreAvgSpeedList(restoredAvgSpeedList);
        restoreFuelLevel();

        restoreFuelLayoutVisibility(firstStart);
        if (isButtonVisible(stopButton)) {
            UtilMethods.setFabVisible(getActivity());
        }
    }

    private void restoreStatus(Boolean status) {
        if (status) {
            setStatusImage();
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
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            UtilMethods.checkPermission(context);
            lm.addGpsStatusListener(this);
        }
        else {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            lm.removeGpsStatusListener(this);
        }
    }

    private void stopTracking() {
        tripProcessor.stopTracking();
        if (firstStart) {
            Log.d(LOG_TAG, "stopTracking: WHY???? (firstStart?)");
            fuelLayout.setVisibility(View.VISIBLE);
            firstStart = false;
        }
        restoreFuelLevel();
    }

    private void startTrip() {
        tripProcessor.startNewTrip();
        UtilMethods.setFabVisible(getActivity());
        UtilMethods.showToast(context, context.getString(R.string.trip_started_toast));
        stopButtonTurnActive();
        //                avgSpeedArrayList = new ArrayList<>();
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

    private void setStatusImage() {
        getContextIfNull();
        ButterKnife.bind(R.id.statusImageView, getActivity());
        Drawable greenSatellite = ContextCompat.getDrawable(context, R.drawable.green_satellite);
        if (statusImage.getBackground() != greenSatellite) {
            statusImage.setBackground(greenSatellite);
        }
    }

    private void getContextIfNull() {
        if (context == null) {
            context = getActivity();
        }
    }

    private void setupSpeedometer() {
        speedometer = createSpeedometerGauge(context);
        speedometer.setScaleX(ConstantValues.SPEEDOMETER_WIDTH);
        speedometer.setScaleY(ConstantValues.SPEEDOMETER_HEIGHT);
        speedometerContainer.addView(speedometer);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/android_7.ttf");
        speedometerTextView.setTypeface(tf);
    }

    private void cleanAllProcess() {
        //        TODO:07.06 .2016 notworking due to problems with WakeLock that calling in OnLocationChanged, whatever you do..
        //        if (wakeLock != null) {
        //            wakeLock.release();
        //        }
        tripProcessor.performExit();
        if (isButtonVisible(stopButton)) {
            stopTracking();
        }
        gpsStatusListener(REMOVE);
        mapFragment = null;
        tripProcessor = null;
        context = null;
        preferences = null;
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

    //    private void updateMaxSpeed(float speed) {
    //        maxSpeedVal = speed;
    //    }

    private void animateSpeedUpdate(final float speed) {
        if (ConstantValues.LOGGING_ENABLED) {
            Log.d(LOG_TAG, "UpdateSpeed: speed in fragment" + speed);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                if (ConstantValues.LOGGING_ENABLED) {
                    UtilMethods.showToast(context, "spdInFrag" + speed);
                }
                updatePointerLocation(speed);
                updateSpeedTextField(speed);
            }
        });
    }

    private void updatePointerLocation(float speed) {
        CircularPointer pointer = speedometer.getCircularScales().get(0).getCircularPointers().get(0);
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

    // TODO: 07.06.2016 notworking due to problems with WakeLock that calling in OnLocationChanged,whatever you do..
    //    private void changeWakeLockStateAfterSettings() {
    //
    //        boolean res = preferences.getBoolean("backgroundWork", false);
    //        if (res) {
    //            Log.d(LOG_TAG, "changeWakeLockStateAfterSettings: wakeLock ON");
    //            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    //            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakeLockForBackgroundWork");
    //            wakeLock.acquire();
    //        }
    //        else {
    //            if(wakeLock!=null){
    //                wakeLock.release();
    //            }
    //            Log.d(LOG_TAG, "changeWakeLockStateAfterSettings: wakeLock OFF");
    //        }
    //    }


    private class ReadInternalFile extends AsyncTask<String, Void, Boolean> {
        @Override protected Boolean doInBackground(String... params) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.i(LOG_TAG, "readFileSettings");
            }
            File file = context.getFileStreamPath(ConstantValues.INTERNAL_SETTING_FILE_NAME);
            if (file.exists()) {
                if (ConstantValues.LOGGING_ENABLED) {
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
