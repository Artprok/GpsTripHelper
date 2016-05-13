package com.example.aprokopenko.triphelper.ui.fragment;

import android.support.v4.content.ContextCompat;
import android.support.annotation.Nullable;
import android.graphics.drawable.Drawable;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;
import android.content.ComponentName;
import android.view.LayoutInflater;
import android.location.GpsStatus;
import android.widget.ImageButton;
import android.graphics.Typeface;
import android.location.Location;
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
import com.example.aprokopenko.triphelper.service.LocationService;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.syncfusion.gauges.SfCircularGauge.SfCircularGauge;
import com.syncfusion.gauges.SfCircularGauge.CircularPointer;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.FuelFillDialog;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.TripProcessor;
import com.google.android.gms.maps.model.LatLng;
import com.example.aprokopenko.triphelper.R;

import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

import butterknife.ButterKnife;
import butterknife.Bind;
import rx.Subscriber;

public class MainFragment extends Fragment implements GpsStatus.Listener {
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

    private Subscriber<Location> locationSubscriber;
    private Subscriber<Float>    maxSpeedSubscriber;
    private Subscriber<Float>    speedSubscriber;

    private ServiceConnection serviceConnection;
    private LocationService   locationService;

    private ArrayList<Float> avgSpeedArrayList;
    private TripProcessor    tripProcessor;
    private SfCircularGauge  speedometer;
    private MapFragment      mapFragment;
    private float            maxSpeedVal;
    private GpsHandler       gpsHandler;
    private Context          context;
    private Bundle           state;
    private Runnable         storeSpeedTicksRunnable;

    private static final boolean REMOVE     = false;
    private static final boolean REGISTER   = true;
    private              boolean firstStart = true;

    private float fuelConsFromSettings;
    private float fuelPriceFromSettings;
    private int   fuelCapacityFromSettings;

    //todo: tempVal is testing val REMOVE in release!
    private final float[] tempVal = {1};

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

        gpsStatusListener(REGISTER);
        setupLocationService();
        setupSpeedometer();

        if (savedInstanceState != null) {
            state = savedInstanceState;
        }
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        Log.i(LOG_TAG, "onSaveInstanceState: Save called");
        super.onSaveInstanceState(outState);
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "onSaveInstanceState: ControlButtons" + getButtonVisibility());
            Log.d(LOG_TAG, "onSaveInstanceState: StatusIm" + getStatusImageState());
            Log.d(LOG_TAG, "onSaveInstanceState: FirstStart" + firstStart);
            Log.d(LOG_TAG, "onSaveInstanceState: FuelLevel" + getFuelLevelFieldValue());
        }
        outState.putBoolean("ControlButtonVisibility", getButtonVisibility());
        outState.putBoolean("StatusImageState", getStatusImageState());
        outState.putBoolean("FirstStart", firstStart);
    }

    @Override public void onPause() {
        saveState();
        super.onPause();
    }

    @Override public void onResume() {
        if (state != null) {
            restoreState(state);
        }
        super.onResume();
    }

    @Override public void onDetach() {
        Log.i(LOG_TAG, "onDetach: called");
        if (serviceConnection != null) {
            getActivity().unbindService(serviceConnection);
        }
        locationService = null;
        mapFragment = null;
        serviceConnection = null;
        gpsHandler = null;
        tripProcessor = null;
        storeSpeedTicksRunnable = null;
        gpsStatusListener(REMOVE);
        super.onDetach();
    }

    @Override public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "onGpsStatusChanged: EventSatStatus");
                }
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "onGpsStatusChanged: EventFirstFix");
                }
                setStatusImage();
                UtilMethods.showToast(context, context.getString(R.string.gps_first_fix_toast));
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                if (ConstantValues.DEBUG_MODE) {
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
        return circularGaugeFactory.getConfiguredSpeedometerGauge(context);
    }


    private boolean getButtonVisibility() {
        Boolean visibility = true;
        if (startButton != null) {
            visibility = (startButton.getVisibility() == View.VISIBLE);
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


    private float getDistanceToDriveLeft(float fuelLeftVal) {
        float avgSpeed = tripProcessor.getTripData().getAvgSpeed();
        if (avgSpeed == 0) {
            avgSpeed = ConstantValues.MEDIUM_TRAFFIC_AVG_SPEED;
        }
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "getDistanceToDriveLeft: " + fuelConsFromSettings + "avgSped" + avgSpeed);
        }
        float fuelConsLevel = UtilMethods.getFuelConsumptionLevel(avgSpeed, fuelConsFromSettings);
        return (fuelLeftVal / fuelConsLevel) * ConstantValues.PER_100_KM;
    }

    private Float getFuelLevelFieldValue() {
        if (tripProcessor != null) {
            return tripProcessor.getFuelLeft();
        }
        else {
            return 0f;
        }
    }

    private String getFuelLeftString() {
        float fuelLeftVal = tripProcessor.getFuelLeft();
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "getFuelLeftString: fuel written" + fuelLeftVal);
        }
        tripProcessor.writeDataToFile();
        float distanceToDriveLeft = getDistanceToDriveLeft(fuelLeftVal);
        return (UtilMethods.formatFloat(fuelLeftVal) + " (~" + UtilMethods.formatFloat(distanceToDriveLeft) + getString(
                R.string.distance_prefix) + ")");
    }

    private void fillGasTank(float fuel) {
        tripProcessor.fillGasTank(fuel);
        String fuelLeftString = getFuelLeftString();
        fuelLeft.setText(fuelLeftString);
    }


    private void setupTripListButton() {
        tripListButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (tripProcessor.isFileNotInWriteMode()) {
                    UtilMethods.setFabInvisible(getActivity());
                    TripData         tripData         = tripProcessor.getTripData();
                    TripListFragment tripListFragment = TripListFragment.newInstance();
                    if (tripData != null && stopButton.getVisibility() == View.INVISIBLE) {
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
                SettingsFragment settingsFragment = SettingsFragment.newInstance();
                UtilMethods.replaceFragment(settingsFragment, ConstantValues.SETTINGS_FRAGMENT_TAG, getActivity());
            }
        });
    }

    private void setupStartButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                tripProcessor.startNewTrip();
                avgSpeedArrayList = new ArrayList<>();
                UtilMethods.setFabVisible(getActivity());
                UtilMethods.showToast(context, context.getString(R.string.trip_started_toast));
                stopButtonTurnActive();
            }
        });
    }

    private void setupStopButton() {
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                stopTracking();
                UtilMethods.showToast(context, context.getString(R.string.trip_ended_toast));
                startButtonTurnActive();
            }
        });
    }

    private void setupFillButton() {
        refillButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (tripProcessor.isFileNotInWriteMode()) {
                    FuelFillDialog dialog = new FuelFillDialog();
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

    private void restoreButtonsVisibility(Boolean visibility) {
        if (visibility) {
            startButtonTurnActive();
        }
        else {
            stopButtonTurnActive();
        }
    }

    private void restoreState(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onViewCreated: calledRestore");
        getContextIfNull();
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getBoolean("FirstStart"));
            Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getBoolean("ControlButtonVisibility"));
            Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getBoolean("StatusImageState"));
        }

        firstStart = savedInstanceState.getBoolean("FirstStart");
        restoreButtonsVisibility(savedInstanceState.getBoolean("ControlButtonVisibility"));
        restoreStatus(savedInstanceState.getBoolean("StatusImageState"));
        restoreFuelLevel();

        restoreFuelLayoutVisibility(firstStart);
        if (stopButton.getVisibility() == View.VISIBLE) {
            UtilMethods.setFabVisible(getActivity());
        }
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
        String fuelString = getFuelLeftString();
        fuelLeft.setText(fuelString);
    }


    private void saveState() {
        if (state == null) {
            state = new Bundle();
        }
        onSaveInstanceState(state);
    }

    private void stopTracking() {
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "stopTracking: +" + avgSpeedArrayList.size());
            for (float f : avgSpeedArrayList) {
                Log.d(LOG_TAG, "stopTracking: " + f);
            }
        }

        float averageSpeed = CalculationUtils.calcAvgSpeedForOneTrip(avgSpeedArrayList);
        // TODO: 12.05.2016 uncomment maxSpeedVal, sppedTick for tests
        //        float maximumSpeed = maxSpeedVal;
        float maximumSpeed = avgSpeedArrayList.size();

        tripProcessor.updateSpeed(averageSpeed, maximumSpeed);
        tripProcessor.endTrip();
        setMetricFieldsToTripData();

        tripProcessor.writeDataToFile();
        if (avgSpeedArrayList != null) {
            avgSpeedArrayList.clear();
        }
        maxSpeedVal = 0f;
        if (firstStart) {
            Log.d(LOG_TAG, "stopTracking: WHY???? (firstStart?)");
            fuelLayout.setVisibility(View.VISIBLE);
            firstStart = false;
        }
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

    private void setMetricFieldsToTripData() {
        final float     startVal     = ConstantValues.START_VALUE;
        TripData        tripData     = tripProcessor.getTripData();
        float           fuelSpent    = startVal;
        float           timeSpent    = startVal;
        float           avgSpeed     = startVal;
        float           avgFuelCons  = startVal;
        float           maxSpeed     = startVal;
        ArrayList<Trip> allTrips     = tripData.getTrips();
        int             tripQuantity = allTrips.size();

        for (Trip trip : allTrips) {
            fuelSpent = fuelSpent + trip.getFuelSpent();
            timeSpent = timeSpent + trip.getTimeSpent();
            avgSpeed = avgSpeed + trip.getAvgSpeed();
            avgFuelCons = avgFuelCons + trip.getAvgFuelConsumption();
            maxSpeed = CalculationUtils.findMaxSpeed(trip.getMaxSpeed(), maxSpeed);
        }

        avgFuelCons = avgFuelCons / tripQuantity;
        avgSpeed = avgSpeed / tripQuantity;
        float distTravelled = CalculationUtils.calcDistTravelled(timeSpent, avgSpeed);
        tripData.setMaxSpeed(maxSpeed);
        tripData.setAvgSpeed(avgSpeed);
        tripData.setTimeSpentOnTrips(timeSpent);
        tripData.setDistanceTravelled(distTravelled);
        tripData.setAvgFuelConsumption(avgFuelCons);
        tripData.setFuelSpent(fuelSpent);
        tripData.setGasTank(tripData.getGasTank() - fuelSpent);
        tripData.setMoneyOnFuelSpent(fuelSpent * fuelPriceFromSettings);
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
        String fuelLeftString = getFuelLeftString();
        fuelLeft.setText(fuelLeftString);
        if (!TextUtils.equals(fuelLeft.getText(), getString(R.string.fuel_left_initial_val))) {
            fuelLayout.setVisibility(View.VISIBLE);
        }
        configureMapFragment();
        setupSubscribers();
        setSubscribersToGpsHandler(gpsHandler);
    }

    private void setupServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override public void onServiceConnected(ComponentName className, IBinder service) {
                LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
                locationService = binder.getService();
                configureGpsHandler();
                setupButtons();
                if (ConstantValues.DEBUG_MODE) {
                    Log.i(LOG_TAG, "onServiceConnected: bounded");
                }
            }

            @Override public void onServiceDisconnected(ComponentName arg0) {
                if (ConstantValues.DEBUG_MODE) {
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
            if (ConstantValues.DEBUG_MODE) {
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


    private void addRoutePoint(LatLng routePoints, float speed) {
        Route routePoint = new Route(routePoints, speed);
        tripProcessor.addRoutePoint(routePoint);
    }

    private void addPointToRouteList(Location location) {
        LatLng routePoints = new LatLng(location.getLatitude(), location.getLongitude());
        float  speed;
        // TODO: 10.05.2016 remove debug code
        if (ConstantValues.DEBUG_MODE) {//debug code for testing
            speed = 0 + tempVal[0];
            if (speed != 0) {           //speed increment by 5 each tick
                tempVal[0] += 5;
            }
            if (speed > 50) {           //speed reaches 50 and then turn to 0
                speed = 0;
            }
        }
        else {
            speed = CalculationUtils.getSpeedInKilometerPerHour(location.getSpeed());
        }
        addRoutePoint(routePoints, speed);
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
        GpsHandler.setSpeedSubscriber(speedSubscriber);
        GpsHandler.setLocationSubscriber(locationSubscriber);
        GpsHandler.setMaxSpeedSubscriber(maxSpeedSubscriber);
    }

    private void setupLocationSubscriber() {
        locationSubscriber = new Subscriber<Location>() {
            @Override public void onCompleted() {
            }

            @Override public void onError(Throwable e) {
            }

            @Override public void onNext(Location location) {
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "onNext: Location added to route list");
                }
                addPointToRouteList(location);
            }
        };
    }

    private void setupMaxSpeedSubscriber() {
        maxSpeedSubscriber = new Subscriber<Float>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {

            }

            @Override public void onNext(Float speed) {
                updateMaxSpeed(speed);
            }
        };
    }

    private void setupSpeedSubscriber() {
        speedSubscriber = new Subscriber<Float>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {

            }

            @Override public void onNext(final Float speed) {
                storeSpeedTicks(speed);
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "onNext: speed in MainFragment" + speed);
                }
                updateSpeed(speed);
            }
        };
    }

    private void setupSubscribers() {
        setupSpeedSubscriber();
        setupLocationSubscriber();
        setupMaxSpeedSubscriber();
    }


    private void updateMaxSpeed(float speed) {
        maxSpeedVal = speed;
    }

    private void storeSpeedTicks(final float speed) {
        // TODO: 13.05.2016 storeSpeedTicksRunnable need??
        if (storeSpeedTicksRunnable == null) {
            Log.d(LOG_TAG, "storeSpeedTicks: runable nul");
            storeSpeedTicksRunnable = new Runnable() {
                @Override public void run() {
                    avgSpeedArrayList.add(speed);
                }
            };
        }
        storeSpeedTicksRunnable.run();
    }

    private void updatePointerLocation(float speed) {
        CircularPointer pointer = speedometer.getCircularScales().get(0).getCircularPointers().get(0);
        pointer.setValue((double) speed);
    }

    private void updateSpeedTextField(float speed) {
        String formattedSpeed = UtilMethods.formatFloat(speed);
        int    initialValue   = Integer.valueOf(speedometerTextView.getText().toString());
        int    finalValue     = Integer.valueOf(formattedSpeed);
        UtilMethods.animateTextView(initialValue, finalValue, speedometerTextView);
        speedometerTextView.setText(formattedSpeed);
    }

    private void updateSpeed(float speed) {
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "UpdateSpeed: speed in fragment" + speed);
        }
        updatePointerLocation(speed);
        updateSpeedTextField(speed);
    }


    private void readInternalDataFromFile() {
        ReadInternalFile readFileTask = new ReadInternalFile();
        readFileTask.execute();
    }

    private void getInternalSettings() {
        readInternalDataFromFile();
    }

    private class ReadInternalFile extends AsyncTask<String, Void, Boolean> {
        @Override protected Boolean doInBackground(String... params) {
            Log.d(LOG_TAG, "readFileSettings");
            File file = context.getFileStreamPath(ConstantValues.INTERNAL_SETTING_FILE_NAME);
            if (file.exists()) {
                Log.d(LOG_TAG, "readTripDataFromFileSettings: ");
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
                    Log.d(LOG_TAG, "doInBackground: EXIST");
                    is.close();
                    fis.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.d(LOG_TAG, "doInBackground: NOPE");
                fuelConsFromSettings = ConstantValues.FUEL_CONSUMPTION_DEFAULT;
                fuelPriceFromSettings = ConstantValues.FUEL_COST_DEFAULT;
                fuelCapacityFromSettings = ConstantValues.FUEL_TANK_CAPACITY_DEFAULT;
                Log.d(LOG_TAG, "doInBackground: " + fuelCapacityFromSettings);
                return true;
            }
            return true;
        }

        @Override protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }
}
