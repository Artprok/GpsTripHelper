package com.example.aprokopenko.triphelper.ui.fragment;

import android.support.v4.content.ContextCompat;
import android.support.annotation.Nullable;
import android.graphics.drawable.Drawable;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.content.ComponentName;
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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
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
import com.example.aprokopenko.triphelper.service.LocationService;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
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
    ImageButton    startButton;
    @Bind(R.id.stopButton)
    ImageButton    stopButton;
    @Bind(R.id.fuelLayout)
    RelativeLayout fuelLayout;
    @Bind(R.id.fuelLeftView)
    TextView       fuelLeft;
    @Bind(R.id.settingsButton)
    ImageButton    settingsButton;

    private static final String LOG_TAG = "MainFragment";

    private ServiceConnection serviceConnection;
    private LocationService   locationService;

    private TripProcessor     tripProcessor;
    private SfCircularGauge   speedometer;
    private MapFragment       mapFragment;
    private float             maxSpeedVal;
    private GpsHandler        gpsHandler;
    private Context           context;
    private Bundle            state;
    private SharedPreferences preferences;

    private static final boolean REMOVE         = false;
    private static final boolean REGISTER       = true;
    private              boolean firstStart     = true;
    private              boolean fileErasedFlag = false;

    private float fuelConsFromSettings;
    private float fuelPriceFromSettings;
    private int   fuelCapacityFromSettings;

    //    private ArrayList<Float>  avgSpeedArrayList;
    //    private Subscriber<Location> locationSubscriber;
    //    private Subscriber<Float>    maxSpeedSubscriber;
    //    private Subscriber<Float>    speedSubscriber;
    //todo: tempVal is testing val REMOVE in release!
    //    private final float[] tempVal = {1};

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getInternalSettings();
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getContextIfNull();
        tripProcessor = new TripProcessor(context, fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings);
        tripProcessor.setSpeedChangeListener(this);

        getStateFromPrefs();

        gpsStatusListener(REGISTER);
        setupLocationService();
        setupSpeedometer();

        if (savedInstanceState != null) {
            state = savedInstanceState;
        }

        // FIXME: 31.05.2016 remove debug tutorial Show
        UtilMethods.firstStartTutorialDialog(context);
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
                    Log.d(LOG_TAG, "onSaveInstanceState: " + String.valueOf(avgListItem));
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
        if (state != null && !fileErasedFlag) {
            restoreState(state);
        }
        else {
            fileErasedFlag = false;
        }
        setInternalToTripProcessor();
        super.onResume();
    }

    private void setInternalToTripProcessor() {
        getInternalSettings();
        tripProcessor.setFuelCapacity(fuelCapacityFromSettings);
        tripProcessor.setFuelConsFromSettings(fuelConsFromSettings);
        tripProcessor.setFuelPrice(fuelPriceFromSettings);
    }

    @Override public void onDetach() {
        if (ConstantValues.LOGGING_ENABLED) {
            Log.i(LOG_TAG, "onDetach: called");
        }
        cleanAllProcess();
        super.onDetach();
    }

    @Override public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
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
        mapFragment.setGpsHandler(gpsHandler);
        setRouteToMapFragment();
        UtilMethods.replaceFragment(mapFragment, ConstantValues.MAP_FRAGMENT_TAG, getActivity());
    }

    private SfCircularGauge createSpeedometerGauge(Context context) {
        CircularGaugeFactory circularGaugeFactory = new CircularGaugeFactory();
        return circularGaugeFactory.getConfiguredSpeedometerGauge(context, preferences.getString("measurementUnit", ""));
    }

    private boolean isButtonVisible(ImageButton button) {
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

    private void getStateFromPrefs() {
        preferences = TripHelperApp.getSharedPreferences();
        if (preferences.getBoolean("firstStart", true)) {
            UtilMethods.firstStartTutorialDialog(context);
            setFirstStartToFalse(preferences);
        }
        CalculationUtils.setMeasurementMultiplier(preferences.getInt("measurementUnitPosition", 0));
    }

    private void setFirstStartToFalse(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    private void fillGasTank(float fuel) {
        fuelLeft.setText(tripProcessor.getFuelLevel(fuel, getString(R.string.distance_prefix)));
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

    private void setButtonsVisibilityDuringWriteMode(int visibility) {
        tripListButton.setVisibility(visibility);
        refillButtonLayout.setVisibility(visibility);
        settingsButton.setVisibility(visibility);
    }

    private void restoreFuelLayoutVisibility(boolean firstState) {
        if (!firstState) {
            fuelLayout.setVisibility(View.VISIBLE);
        }
    }

    private void restoreButtonsVisibility(TripProcessor restoredTripProcessor, Boolean visibility) {
        if (visibility || restoredTripProcessor == null) {
            startButtonTurnActive();
        }
        else {
            stopButtonTurnActive();
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
        restoreAvgSpeedList(restoredAvgSpeedList);
        restoreTripProcessor(restoredTripProcessor);
        restoreFuelLevel();

        restoreFuelLayoutVisibility(firstStart);
        if (isButtonVisible(stopButton)) {
            UtilMethods.setFabVisible(getActivity());
        }
    }

    private void restoreTripProcessor(TripProcessor restoredTripProcess) {
        if (restoredTripProcess != null) {
            tripProcessor = restoredTripProcess;
        }
        else {
            tripProcessor = new TripProcessor(context, fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings);
        }
    }

    private void restoreAvgSpeedList(ArrayList<String> avgSpeedList) {
        ArrayList<Float> avgSpeedArrayList = new ArrayList<>();
        if (avgSpeedList != null) {
            for (String tmpStr : avgSpeedList) {
                avgSpeedArrayList.add(Float.valueOf(tmpStr));
            }
        }
        tripProcessor.setAvgSpeedArrayList(avgSpeedArrayList);
    }

    private void restoreStatus(Boolean status) {
        if (status) {
            setStatusImage();
        }
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

    private void restoreFuelLevel() {
        String fuelString = tripProcessor.getFuelLeftString(getString(R.string.distance_prefix));
        fuelLeft.setText(fuelString);
    }

    private void saveState() {
        if (state == null) {
            state = new Bundle();
        }
        onSaveInstanceState(state);
    }

    private void stopTracking() {
        tripProcessor.stopTracking();
        maxSpeedVal = 0f;
        if (firstStart) {
            Log.d(LOG_TAG, "stopTracking: WHY???? (firstStart?)");
            fuelLayout.setVisibility(View.VISIBLE);
            firstStart = false;
        }
        restoreFuelLevel();
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

    private void configureGpsHandler() {
        gpsHandler = locationService.getGpsHandler();
        UtilMethods.checkIfGpsEnabled(context);
        String fuelLeftString = tripProcessor.getFuelLeftString(getString(R.string.distance_prefix));
        fuelLeft.setText(fuelLeftString);
        if (!TextUtils.equals(fuelLeft.getText(), getString(R.string.fuel_left_initial_val))) {
            fuelLayout.setVisibility(View.VISIBLE);
        }
        configureMapFragment();
        setSubscribersToGpsHandler(gpsHandler);
        //        setupSubscribers();
    }

    private void setupServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override public void onServiceConnected(ComponentName className, IBinder service) {
                LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
                locationService = binder.getService();
                configureGpsHandler();
                setupButtons();
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.i(LOG_TAG, "onServiceConnected: bounded");
                }
            }

            @Override public void onServiceDisconnected(ComponentName arg0) {
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.i(LOG_TAG, "onServiceConnected: unbounded");
                }
            }
        };
    }

    private void setupLocationService() {
        Intent intent = new Intent(context, LocationService.class);
        if (serviceConnection == null) {
            setupServiceConnection();
            context.getApplicationContext().startService(intent);
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.i(LOG_TAG, "onServiceConnected: service already exist");
            }
            configureGpsHandler();
            setupButtons();
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

    private void setRouteToMapFragment() {
        if (tripProcessor.getRoutes() != null) {
            mapFragment.setRoutes(tripProcessor.getRoutes());
        }
    }

    private void configureMapFragment() {
        mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ConstantValues.MAP_FRAGMENT_TAG);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
        }
    }

    private void setSubscribersToGpsHandler(GpsHandler GpsHandler) {

        GpsHandler.setSpeedSubscriber(tripProcessor.getSpeedSubscriber());
        GpsHandler.setLocationSubscriber(tripProcessor.getLocationSubscriber());
        GpsHandler.setMaxSpeedSubscriber(tripProcessor.getMaxSpeedSubscriber());

        //        GpsHandler.setSpeedSubscriber(speedSubscriber);
        //        GpsHandler.setLocationSubscriber(locationSubscriber);
        //        GpsHandler.setMaxSpeedSubscriber(maxSpeedSubscriber);
    }

    private void updateMaxSpeed(float speed) {
        maxSpeedVal = speed;
    }

    private void cleanAllProcess() {
        if (isButtonVisible(stopButton)) {
            stopTracking();
        }
        if (serviceConnection != null) {
            context.unbindService(serviceConnection);
        }
        gpsStatusListener(REMOVE);
        if (locationService != null) {
            locationService.onDestroy();
            locationService = null;
        }
        mapFragment = null;
        serviceConnection = null;
        gpsHandler = null;
        tripProcessor = null;
        context = null;
        preferences = null;
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

    private void readInternalDataFromFile() {
        ReadInternalFile readFileTask = new ReadInternalFile();
        readFileTask.execute();
    }

    private void getInternalSettings() {
        readInternalDataFromFile();
    }

    @Override public void speedChanged(float speed) {
        animateSpeedUpdate(speed);
    }

    @Override public void maxSpeedChanged(float maxSpeed) {
        updateMaxSpeed(maxSpeed);
    }

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

    @Override public void onFileErased() {
        fileErasedFlag = true;
    }
}
