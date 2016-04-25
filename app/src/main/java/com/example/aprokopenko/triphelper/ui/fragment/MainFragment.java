package com.example.aprokopenko.triphelper.ui.fragment;

import android.support.v4.content.ContextCompat;
import android.support.annotation.Nullable;
import android.graphics.drawable.Drawable;
import android.content.ServiceConnection;
import android.support.v7.widget.Toolbar;
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
import android.os.IBinder;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import com.example.aprokopenko.triphelper.listener.DialogFragmentInteractionListener;
import com.example.aprokopenko.triphelper.speedometerfactory.CircularGaugeFactory;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.MathUtils;
import com.example.aprokopenko.triphelper.service.LocationService;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.syncfusion.gauges.SfCircularGauge.CircularPointer;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.syncfusion.gauges.SfCircularGauge.SfCircularGauge;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.FuelFillDialog;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.TripProcessor;
import com.google.android.gms.maps.model.LatLng;
import com.example.aprokopenko.triphelper.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Bind;
import rx.Subscriber;

public class MainFragment extends Fragment implements GpsStatus.Listener {
    @Bind(R.id.speedometerContainer)
    RelativeLayout speedometerContainer;
    @Bind(R.id.speedometerTextView)
    TextView       speedometerTextView;
    @Bind(R.id.tripListButton)
    ImageButton    tripListButton;
    @Bind(R.id.statusImageView)
    ImageView      statusImage;
    @Bind(R.id.startButton)
    ImageButton    startButton;
    @Bind(R.id.eraseButton)
    ImageButton    eraseButton;
    @Bind(R.id.fillButton)
    ImageButton    fillButton;
    @Bind(R.id.stopButton)
    ImageButton    stopButton;
    @Bind(R.id.fuelLayout)
    RelativeLayout fuelLayout;
    @Bind(R.id.maxSpeed)
    TextView       maxSpeed;
    @Bind(R.id.fuelLeftView)
    TextView       fuelLeft;

    private static final String LOG_TAG = "MainFragment";
    private Subscriber<Location> locationSubscriber;
    private Subscriber<Float>    maxSpeedSubscriber;
    private ArrayList<Float>     avgSpeedArrayList;
    private Subscriber<Float>    speedSubscriber;
    private ServiceConnection    serviceConnection;
    private LocationService      locationService;
    private TripProcessor        tripProcessor;
    private SfCircularGauge      speedometer;
    private MapFragment          mapFragment;
    private GpsHandler           gpsHandler;
    private Context              context;
    private Bundle               state;
    private boolean firstStart = true;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getActivity();
        removeToolbar();

        registerGpsStatusListener();

        setupLocationService();
        setupSpeedometer();

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
            state = savedInstanceState;
        }

    }

    @Override public void onPause() {
        super.onPause();
        saveState();
    }

    @Override public void onResume() {
        super.onResume();
        if (state != null) {
            restoreState(state);
        }
    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "onSaveInstanceState: Save called");
            Log.d(LOG_TAG, "onSaveInstanceState: " + getButtonVisibility());
            Log.d(LOG_TAG, "onSaveInstanceState: " + getStatus());
            Log.d(LOG_TAG, "onSaveInstanceState: " + getMaxSpeed());
        }
        outState.putBoolean("ControlButtonVisibility", getButtonVisibility());
        outState.putBoolean("StatusImageState", getStatus());
        outState.putFloat("MaxSpeed", getMaxSpeed());
        outState.putBoolean("FirstStart", firstStart);
        outState.putFloat("FuelLevel", getFuelLevel());
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override public void onDetach() {
        super.onDetach();
        getActivity().unbindService(serviceConnection);
        context = null;
        locationService = null;
        mapFragment = null;
        serviceConnection = null;
        gpsHandler = null;
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
        Log.d(LOG_TAG, "openMapFragment: saveStateOpenMap");
        saveState();
        mapFragment.setGpsHandler(gpsHandler);
        setRouteToMapFragment();
        UtilMethods.replaceFragment(mapFragment, ConstantValues.MAP_FRAGMENT_TAG, getActivity());
    }


    private Float getMaxSpeed() {
        return Float.valueOf(maxSpeed.getText().toString());
    }

    private Float getFuelLevel() {
        return Float.valueOf(fuelLeft.getText().toString());
    }


    private Float calcAvgSpeed() {
        float avgSpeed = 0;
        if (avgSpeedArrayList != null) {
            for (Float tmpSpeed : avgSpeedArrayList) {
                avgSpeed = avgSpeed + tmpSpeed;
            }
            avgSpeed = avgSpeed / avgSpeedArrayList.size();
        }
        return avgSpeed;
    }

    private SfCircularGauge createSpeedometerGauge(Context context) {
        CircularGaugeFactory circularGaugeFactory = new CircularGaugeFactory();
        return circularGaugeFactory.getConfiguredSpeedometerGauge(context);
    }

    private float calcAvgSpd(Trip trip) {
        float avgSpeed = 0;
        avgSpeed = avgSpeed + trip.getAvgSpeed();
        return avgSpeed;
    }

    private boolean getButtonVisibility() {
        Boolean     visibility = false;
        ImageButton stB        = (ImageButton) getActivity().findViewById(R.id.startButton);
        if (stB != null) {
            visibility = (stB.getVisibility() == View.VISIBLE);
        }
        return visibility;
    }

    private boolean getStatus() {
        Log.d(LOG_TAG, "getStatus: DEbUG!" + context.toString());
        Drawable greenSatellite = ContextCompat.getDrawable(context, R.drawable.green_satellite);
        return statusImage.getBackground() != greenSatellite;
    }

    private void fillGasTank(float fuel) {
        tripProcessor.fillGasTank(fuel);
        tripProcessor.writeDataToFile();

        fuelLeft.setText(UtilMethods.formatFloat(tripProcessor.getFuelLeft()));
    }

    private void setupButtons() {
        setupStartButton();
        setupStopButton();
        setupTripListButton();
        setupFillButton();
        //        if (ConstantValues.DEBUG_MODE) {
        setupEraseButton();
        //        }
    }


    private void setupFillButton() {
        fillButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                FuelFillDialog dialog = new FuelFillDialog();
                dialog.setDialogFragmentInteractionListener(new DialogFragmentInteractionListener() {
                    @Override public void fuelFilled(float fuel) {
                        fillGasTank(fuel);
                    }
                });
                dialog.show(getChildFragmentManager(), "DIALOG");
            }
        });
    }

    private void saveState() {
        if (state == null) {
            state = new Bundle();
        }
        onSaveInstanceState(state);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "onViewCreated: calledRestore");
            Log.d(LOG_TAG, "restoreState: " + savedInstanceState.toString());
            Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getBoolean("ControlButtonVisibility"));
            Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getBoolean("StatusImageState"));
            Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getFloat("MaxSpeed"));
            Log.d(LOG_TAG, "restoreState: " + savedInstanceState.getFloat("AvgSpeed"));
        }
        restoreVisibility(savedInstanceState.getBoolean("ControlButtonVisibility"));
        restoreStatus(savedInstanceState.getBoolean("StatusImageState"));
        restoreMaxSpeed(savedInstanceState.getFloat("MaxSpeed"));
        restoreFuelVisibility(savedInstanceState.getBoolean("FirstStart"));
        restoreFuelLevel(savedInstanceState.getFloat("FuelLevel"));
        if (stopButton.getVisibility() == View.VISIBLE) {
            UtilMethods.setFabVisible(getActivity());
        }
    }

    private void restoreFuelVisibility(boolean firstState) {
        if (!firstState) {
            fuelLayout.setVisibility(View.VISIBLE);
        }
    }

    private void restoreFuelLevel(Float fuelLevel) {
        fuelLeft.setText(UtilMethods.formatFloat(fuelLevel));
    }

    private void restoreStatus(Boolean status) {
        if (status) {
            setStatusImage();
        }
    }

    private void restoreMaxSpeed(Float maxSpd) {
        updateMaxSpeed(maxSpd);
    }

    private void restoreVisibility(Boolean visibility) {
        if (visibility) {
            startButtonTurnActive();
        }
        else {
            stopButtonTurnActive();
        }
    }

    private void stopButtonTurnActive() {
        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
    }

    private void startButtonTurnActive() {
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
    }

    private void setStatusImage() {
        Log.d(LOG_TAG, "setStatusImage: BEFORE ERR!" + context.toString());
        Drawable greenSatellite = ContextCompat.getDrawable(context, R.drawable.green_satellite);
        if (statusImage.getBackground() != greenSatellite) {
            statusImage.setBackground(greenSatellite);
        }
    }


    private void stopTracking() {
        tripProcessor.updateSpeed(calcAvgSpeed(),getMaxSpeed());
        tripProcessor.endTrip();
        setMetricFieldsToTripData();

        tripProcessor.writeDataToFile();
        avgSpeedArrayList.clear();
        maxSpeed.setText("0");
        if (firstStart) {
            fuelLayout.setVisibility(View.VISIBLE);
            firstStart = false;
        }
    }

    private void setMetricFieldsToTripData() {
        TripData        tripData     = tripProcessor.getTripData();
        float           fuelSpent    = 0;
        float           timeSpent    = 0;
        float           avgSpeed     = 0;
        float           avgFuelCons  = 0;
        float           maxSpeed     = 0;
        ArrayList<Trip> allTrips     = tripData.getTrips();
        int             tripQuantity = allTrips.size();
        for (Trip trip : allTrips) {
            fuelSpent = fuelSpent + trip.getFuelSpent();
            timeSpent = timeSpent + trip.getTimeSpent();
            avgSpeed = avgSpeed + calcAvgSpd(trip);
            avgFuelCons = avgFuelCons + trip.getAvgFuelConsumption();
            float tmpMaxSpeed = trip.getMaxSpeed();
            if (tmpMaxSpeed>maxSpeed) {
                maxSpeed = tmpMaxSpeed;
            }

        }
        avgFuelCons = avgFuelCons / tripQuantity;
        avgSpeed = avgSpeed / tripQuantity;
        float distTravelled = MathUtils.calcDistTravelled(timeSpent, avgSpeed);
        tripData.setMaxSpeed(maxSpeed);
        tripData.setAvgSpeed(avgSpeed);
        tripData.setTimeSpentOnTrips(timeSpent);
        tripData.setDistanceTravelled(distTravelled);
        tripData.setAvgFuelConsumption(avgFuelCons);
        tripData.setFuelSpent(fuelSpent);
        tripData.setGasTank(tripData.getGasTank() - fuelSpent);
        tripData.setMoneyOnFuelSpent(fuelSpent * ConstantValues.FUEL_COST);
    }

    private void registerGpsStatusListener() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        UtilMethods.checkPermission(context);
        lm.addGpsStatusListener(this);
    }

    private void setupServiceConnection() {
        serviceConnection = new ServiceConnection() {

            @Override public void onServiceConnected(ComponentName className, IBinder service) {
                LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
                locationService = binder.getService();
                configureGpsHandler();
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "onServiceConnected: bounded");
                }
                setupButtons();
            }

            @Override public void onServiceDisconnected(ComponentName arg0) {
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "onServiceConnected: unbounded");
                }
            }
        };
    }

    private void setupLocationService() {
        Intent intent = new Intent(getActivity(), LocationService.class);
        setupServiceConnection();
        getActivity().getApplicationContext().startService(intent);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void configureGpsHandler() {
        gpsHandler = locationService.getGpsHandler();
        UtilMethods.checkIfGpsEnabled(context);
        tripProcessor = new TripProcessor(context);
        fuelLeft.setText(UtilMethods.formatFloat(tripProcessor.getFuelLeft()));
        if (!TextUtils.equals(fuelLeft.getText(), getString(R.string.fuel_left_initial_val))) {
            fuelLayout.setVisibility(View.VISIBLE);
        }
        configureMapFragment();
        setupSubscribers();
        setSubscribersToGpsHandler(gpsHandler);
    }

    private void setupSpeedometer() {
        speedometer = createSpeedometerGauge(context);
        speedometer.setScaleX(ConstantValues.SPEEDOMETER_WIDTH);
        speedometer.setScaleY(ConstantValues.SPEEDOMETER_HEIGHT);
        speedometerContainer.addView(speedometer);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/android_7.ttf");
        speedometerTextView.setTypeface(tf);
    }

    private void removeToolbar() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
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
        //        GpsHandler.setAvgSpeedSubscriber(avgSpeedSubscriber);
        GpsHandler.setMaxSpeedSubscriber(maxSpeedSubscriber);
    }

    private void addPointToRouteList(Location location) {
        LatLng routePoints = new LatLng(location.getLatitude(), location.getLongitude());
        // FIXME: 20.04.2016 REMOVE 99f! Color dependency for line on mAp
        Float speed;
        if (ConstantValues.DEBUG_MODE) {
            speed = 99f;
        }
        else {
            speed = MathUtils.getSpeedInKilometerPerHour(location.getSpeed());
        }
        Route routePoint = new Route(routePoints, speed);
        tripProcessor.addRoutePoint(routePoint);
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

            @Override public void onNext(Float speed) {
                if (ConstantValues.DEBUG_MODE) {
                    Log.d(LOG_TAG, "onNext: speed in MainFragment" + speed);
                }
                updateSpeed(speed);
                storeSpeedTicks(speed);
            }
        };
    }

    private void storeSpeedTicks(float speed) {
        avgSpeedArrayList.add(speed);
    }

    private void setupSubscribers() {
        setupSpeedSubscriber();
        setupLocationSubscriber();
        setupMaxSpeedSubscriber();
    }

    private void updateSpeed(float speed) {
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "UpdateSpeed: speed in fragment" + speed);
        }
        updatePointerLocation(speed);
        updateSpeedTextField(speed);
    }

    private void updateSpeedTextField(float speed) {
        String tmpString      = speedometerTextView.getText().toString();
        String formattedSpeed = UtilMethods.formatFloat(speed);
        int    initialValue   = Integer.valueOf(tmpString);
        int    finalValue     = Integer.valueOf(formattedSpeed);
        UtilMethods.animateTextView(initialValue, finalValue, speedometerTextView);
        speedometerTextView.setText(formattedSpeed);
    }

    private void updatePointerLocation(float speed) {
        CircularPointer pointer = speedometer.getCircularScales().get(0).getCircularPointers().get(0);
        pointer.setValue((double) speed);
    }

    private void updateMaxSpeed(float speed) {
        float tmpFloat   = Float.valueOf(maxSpeed.getText().toString());
        int   initialVal = (int) tmpFloat;
        int   finalVal   = (int) speed;
        UtilMethods.animateTextView(initialVal, finalVal, maxSpeed);
        maxSpeed.setText(String.valueOf(speed));
    }

    private void setRouteToMapFragment() {
        if (tripProcessor.getRoutes() != null) {
            mapFragment.setRoute(tripProcessor.getRoutes());
        }
    }

    private void setupTripListButton() {
        tripListButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (tripProcessor.isFileNotInWriteMode()) {
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

    private void setupStartButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Log.d(LOG_TAG, "setupStartButton: startPressed");
                tripProcessor.startNewTrip();
                avgSpeedArrayList = new ArrayList<>();
                UtilMethods.setFabVisible(getActivity());
                stopButtonTurnActive();
            }
        });
    }

    private void setupStopButton() {
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                stopTracking();
                startButtonTurnActive();

            }
        });
    }

    private void setupEraseButton() {
        eraseButton.setVisibility(View.VISIBLE);
        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                tripProcessor.eraseFile(context);
            }
        });
    }
}
