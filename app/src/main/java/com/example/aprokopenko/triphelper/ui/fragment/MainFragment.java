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
    @Bind(R.id.maxSpeed)
    TextView       maxSpeed;
    @Bind(R.id.avgSpeed)
    TextView       avgSpeed;
    @Bind(R.id.fuelLeftView)
    TextView       fuelLeft;

    private static final String LOG_TAG = "MainFragment";
    private Subscriber<Location> locationSubscriber;
    private Subscriber<Float>    avgSpeedSubscriber;
    private Subscriber<Float>    maxSpeedSubscriber;
    private Subscriber<Float>    speedSubscriber;
    private ServiceConnection    serviceConnection;
    private LocationService      locationService;
    private TripProcessor        tripProcessor;
    private SfCircularGauge      speedometer;
    private MapFragment          mapFragment;
    private GpsHandler           gpsHandler;
    private Context              context;
    private Bundle               state;

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
        setupStartButton();
        setupStopButton();
        setupTripListButton();
        setupFillButton();
        //        if (ConstantValues.DEBUG_MODE) {
        setupEraseButton();
        //        }
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
        outState.putBoolean("ControlButtonVisibility", getButtonVisibility());
        outState.putBoolean("StatusImageState", getStatus());
        outState.putFloat("AvgSpeed", getAvgSpeed());
        outState.putFloat("MaxSpeed", getMaxSpeed());
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "onSaveInstanceState: Save called");
            Log.d(LOG_TAG, "onSaveInstanceState: " + getButtonVisibility());
            Log.d(LOG_TAG, "onSaveInstanceState: " + getStatus());
            Log.d(LOG_TAG, "onSaveInstanceState: " + getMaxSpeed());
            Log.d(LOG_TAG, "onSaveInstanceState: " + getAvgSpeed());
        }
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
        saveState();
        mapFragment.setGpsHandler(gpsHandler);
        setRouteToMapFragment();
        UtilMethods.replaceFragment(mapFragment, ConstantValues.MAP_FRAGMENT_TAG, getActivity());
    }


    private Float getMaxSpeed() {
        return Float.valueOf(maxSpeed.getText().toString());
    }

    private Float getAvgSpeed() {
        return Float.valueOf(avgSpeed.getText().toString());
    }

    private SfCircularGauge createSpeedometerGauge(Context context) {
        CircularGaugeFactory circularGaugeFactory = new CircularGaugeFactory();
        return circularGaugeFactory.getConfiguredSpeedometerGauge(context);
    }

    private float calcAvgSpd(int tripQuantity, Trip trip) {
        float avgSpeed = 0;
        if (tripQuantity == 1) {
            avgSpeed = avgSpeed + trip.getAvgSpeed();
        }
        else {
            avgSpeed = avgSpeed + trip.getAvgSpeed() / tripQuantity;
        }
        return avgSpeed;
    }

    private boolean getButtonVisibility() {
        ImageButton stB = (ImageButton) getActivity().findViewById(R.id.startButton);
        return stB.getVisibility() == View.VISIBLE;
    }

    private boolean getStatus() {
        Drawable greenSatellite = ContextCompat.getDrawable(context, R.drawable.green_satellite);
        return statusImage.getBackground() != greenSatellite;
    }

    private void fillGasTank(float fuel) {
        tripProcessor.fillGasTank(fuel);
        tripProcessor.writeTripDataToFile(context);
        fuelLeft.setText(UtilMethods.formatFloat00(tripProcessor.getFuelLeft()));
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
        state = new Bundle();
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
        restoreAvgSpeed(savedInstanceState.getFloat("AvgSpeed"));
    }

    private void restoreStatus(Boolean status) {
        if (status) {
            setStatusImage();
        }
    }

    private void restoreAvgSpeed(Float avgSpd) {
        if (avgSpd != 0.0) {
            updateAverageSpeed(avgSpd);
        }
    }

    private void restoreMaxSpeed(Float maxSpd) {
        if (maxSpd != 0.0) {
            updateMaxSpeed(maxSpd);
        }
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
        Drawable greenSatellite = ContextCompat.getDrawable(context, R.drawable.green_satellite);
        if (statusImage.getBackground() != greenSatellite) {
            statusImage.setBackground(greenSatellite);
        }
    }


    private void stopTracking() {
        tripProcessor.updateAvgSpeed(getAvgSpeed());
        tripProcessor.endTrip();
        setMetricFieldsToTripData();

        tripProcessor.writeTripDataToFile(context);
    }

    private void setMetricFieldsToTripData() {
        TripData        tripData     = tripProcessor.getTripData();
        float           fuelSpent    = 0;
        float           timeSpent    = 0;
        float           avgSpeed     = 0;
        ArrayList<Trip> allTrips     = tripData.getTrips();
        int             tripQuantity = allTrips.size();
        for (Trip t : allTrips) {
            fuelSpent = fuelSpent + t.getFuelSpent();
            timeSpent = timeSpent + t.getTimeSpent();
            // FIXME: 18.04.2016 strange behavior here? With avgSpeed.
            avgSpeed = calcAvgSpd(tripQuantity, t);
        }
        float distTravelled = MathUtils.calcDistTravelled(timeSpent, avgSpeed);

        tripData.setAvgSpeed(avgSpeed);
        tripData.setTimeSpentOnTrips(timeSpent);
        tripData.setDistanceTravelled(distTravelled);
        // FIXME: 18.04.2016 mock in fuelConsumption (add fuelConsumption calculation)
        tripData.setAvgFuelConsumption(ConstantValues.FUEL_CONSUMPTION);
        tripData.setFuelSpent(fuelSpent);
        tripData.setGasTank(tripData.getGasTank() - fuelSpent);
        // FIXME: 18.04.2016 mock in fuelFilled. Create a button to "fill fuel functionality" maybe remove this field. Useless
        tripData.setFuelFilled(65.7f);
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
        fuelLeft.setText(UtilMethods.formatFloat00(tripProcessor.getFuelLeft()));
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
        GpsHandler.setAvgSpeedSubscriber(avgSpeedSubscriber);
        GpsHandler.setMaxSpeedSubscriber(maxSpeedSubscriber);
    }

    private void addPointToRouteList(Location location) {
        LatLng routePoints = new LatLng(location.getLatitude(), location.getLongitude());
        // FIXME: 20.04.2016 REMOVE 99f!
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

    private void setupAvgSpeedSubscriber() {
        avgSpeedSubscriber = new Subscriber<Float>() {
            @Override public void onCompleted() {

            }

            @Override public void onError(Throwable e) {

            }

            @Override public void onNext(Float speed) {
                updateAverageSpeed(speed);
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
            }
        };
    }

    private void setupSubscribers() {
        setupSpeedSubscriber();
        setupLocationSubscriber();
        setupAvgSpeedSubscriber();
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
        String formattedSpeed = UtilMethods.formatFloat0(speed);
        int    initialValue   = Integer.valueOf(tmpString);
        int    finalValue     = Integer.valueOf(formattedSpeed);
        UtilMethods.animateTextView(initialValue, finalValue, speedometerTextView);
        speedometerTextView.setText(formattedSpeed);
    }

    private void updatePointerLocation(float speed) {
        CircularPointer pointer = speedometer.getCircularScales().get(0).getCircularPointers().get(0);
        pointer.setValue((double) speed);
    }

    private void updateAverageSpeed(float speed) {
        String tmpString  = UtilMethods.formatFloat0(speed);
        int    initialVal = Integer.valueOf(tmpString);
        int    finalVal   = (int) speed;
        UtilMethods.animateTextView(initialVal, finalVal, avgSpeed);
        avgSpeed.setText(String.valueOf(speed));
    }

    private void updateMaxSpeed(float speed) {
        String tmpString  = UtilMethods.formatFloat0(speed);
        int    initialVal = Integer.valueOf(tmpString);
        int    finalVal   = (int) speed;
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
                TripData         tripData         = tripProcessor.getTripData();
                TripListFragment tripListFragment = TripListFragment.newInstance();
                if (tripData != null) {
                    if (!tripData.getTrips().isEmpty()) {
                        saveState();
                        tripListFragment.setTripData(tripData);
                        UtilMethods.replaceFragment(tripListFragment, ConstantValues.TRIP_LIST_TAG, getActivity());
                    }
                }
            }
        });
    }

    private void setupStartButton() {
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Log.d(LOG_TAG, "setupStartButton: startPressed");
                avgSpeed.setText("0");
                maxSpeed.setText("0");
                tripProcessor.startNewTrip();
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
