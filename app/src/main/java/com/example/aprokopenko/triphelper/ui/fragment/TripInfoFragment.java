package com.example.aprokopenko.triphelper.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.datamodel.TripInfoContainer;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.MapUtilMethods;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@Singleton public class TripInfoFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.tripAvgFuelConsumption)
    TextView       tripAvgFuelConsumptionView;
    @BindView(R.id.tripDistanceTravelled)
    TextView       tripDistanceTravelledView;
    @BindView(R.id.tripTimeSpentInMotion)
    TextView       tripTimeSpentInMotionView;
    @BindView(R.id.tripTimeSpentOnStop)
    TextView       tripTimeSpentOnStopView;
    @BindView(R.id.tripMoneyOnTripFuelSpent)
    TextView       tripMoneySpentView;
    @BindView(R.id.tripFuelSpent)
    TextView       tripFuelSpentView;
    @BindView(R.id.tripTimeSpent)
    TextView       tripTimeSpentView;
    @BindView(R.id.tripAvgSpeed)
    TextView       tripAvgSpeedView;
    @BindView(R.id.tripMaxSpeed)
    TextView       tripMaxSpeedView;
    @BindView(R.id.mapTurnActiveButton)
    ImageButton    openMapButton;
    @BindView(R.id.textDataContainer)
    RelativeLayout dataContainer;
    @BindView(R.id.tripDate)
    TextView       tripDateView;
    @BindView(R.id.tripId)
    TextView       tripIdView;
    @BindView(R.id.mapView)
    MapView        mapView;

    private static final String LOG_TAG = "TripInfoFragment";

    private static final String TIME_SPENT_IN_MOTION      = "TimeSpentOnMotion";
    private static final String TIME_SPENT_WITHOUT_MOTION = "TimeSpentOnStop";
    private static final String LONGITUDE_ARR             = "LongitudeArray";
    private static final String DISTANCE_TRAVELLED        = "DistTravelled";
    private static final String LATITUDE_ARR              = "LatitudeArray";
    private static final String AVERAGE_FUEL_CONS         = "FuelConsumed";
    private static final String MONEY_SPENT               = "MoneySpent";
    private static final String SPEED_ARR                 = "SpeedArray";
    private static final String TIME_SPENT                = "TimeSpent";
    private static final String FUEL_SPENT                = "FuelSpent";
    private static final String AVERAGE_SPEED             = "AvgSpeed";
    private static final String TRIP_DATE                 = "TripDate";
    private static final String MAX_SPEED                 = "MaxSpeed";
    private static final String TRIP_ID                   = "TripId";

    private float    averageFuelConsumption;
    private float    timeSpentOnStop;
    private float    distTravelled;
    private float    moneySpent;
    private float    fuelSpent;
    private float    timeSpent;
    private float    avgSpeed;
    private float    maxSpeed;
    private String   tripDate;
    private int      tripId;
    private boolean  drawMap;
    private Unbinder unbinder;

    private ArrayList<String> speedValues;
    private Context           context;
    private ArrayList<Route>  routes;

    private boolean mapOpened;


    public TripInfoFragment() {
    }

    public static TripInfoFragment newInstance(TripInfoContainer tripInfoContainer) {
        if (ConstantValues.LOGGING_ENABLED) {
            Log.d(LOG_TAG, "newInstance: CALLED");
        }
        TripInfoFragment  fragment       = new TripInfoFragment();
        Bundle            args           = new Bundle();
        ArrayList<String> latitudeArray  = new ArrayList<>();
        ArrayList<String> longitudeArray = new ArrayList<>();
        ArrayList<String> speedArray     = new ArrayList<>();
        ArrayList<Route>  routes         = tripInfoContainer.getRoutes();
        if (routes != null) {
            for (Route tmpRoute : routes) {
                if (tmpRoute != null) {
                    LatLng tempLatLang = tmpRoute.getRoutePoints();
                    latitudeArray.add(String.valueOf(tempLatLang.latitude));
                    longitudeArray.add(String.valueOf(tempLatLang.longitude));
                    speedArray.add(String.valueOf(tmpRoute.getSpeed()));
                }
            }
        }

        args.putFloat(TIME_SPENT_WITHOUT_MOTION, tripInfoContainer.getTimeSpentOnStop());
        args.putFloat(TIME_SPENT_IN_MOTION, tripInfoContainer.getTimeSpentInMotion());
        args.putStringArrayList(LONGITUDE_ARR, longitudeArray);
        args.putStringArrayList(LATITUDE_ARR, latitudeArray);
        args.putFloat(AVERAGE_FUEL_CONS, tripInfoContainer.getAvgFuelConsumption());
        args.putFloat(DISTANCE_TRAVELLED, tripInfoContainer.getDistanceTravelled());
        args.putStringArrayList(SPEED_ARR, speedArray);
        args.putFloat(AVERAGE_SPEED, tripInfoContainer.getAvgSpeed());
        args.putFloat(MONEY_SPENT, tripInfoContainer.getMoneyOnFuelSpent());
        args.putFloat(TIME_SPENT, tripInfoContainer.getTimeSpentForTrip());
        args.putFloat(FUEL_SPENT, tripInfoContainer.getFuelSpent());
        args.putString(TRIP_DATE, tripInfoContainer.getDate());
        args.putFloat(MAX_SPEED, tripInfoContainer.getMaxSpeed());
        args.putInt(TRIP_ID, tripInfoContainer.getId());

        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ArrayList<String> longitudeArray = getArguments().getStringArrayList(LONGITUDE_ARR);
            ArrayList<String> latitudeArray  = getArguments().getStringArrayList(LATITUDE_ARR);
            ArrayList<String> speedArray     = getArguments().getStringArrayList(SPEED_ARR);
            timeSpentOnStop = getArguments().getFloat(TIME_SPENT_WITHOUT_MOTION);
            averageFuelConsumption = getArguments().getFloat(AVERAGE_FUEL_CONS);
            distTravelled = getArguments().getFloat(DISTANCE_TRAVELLED);
            avgSpeed = getArguments().getFloat(AVERAGE_SPEED);
            moneySpent = getArguments().getFloat(MONEY_SPENT);
            timeSpent = getArguments().getFloat(TIME_SPENT);
            fuelSpent = getArguments().getFloat(FUEL_SPENT);
            tripDate = getArguments().getString(TRIP_DATE);
            maxSpeed = getArguments().getFloat(MAX_SPEED);
            tripId = getArguments().getInt(TRIP_ID);
            speedValues = speedArray;

            routes = MapUtilMethods.unwrapRoute(latitudeArray, longitudeArray, speedArray);
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trip_info, container, false);
        unbinder = ButterKnife.bind(this, v);
        setupMapView(savedInstanceState);
        return v;
    }

    @Override public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        context = getActivity();
        setDataToInfoFragmentFields();

        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (drawMap) {
                    int orientation = getActivity().getResources().getConfiguration().orientation;
                    if (orientation == 1) {                    //animations for portrait orientation
                        if (mapOpened) {
                            animateMapClosingForPortrait(view);
                        }
                        else {
                            animateMapOpeningForPortrait(view);
                        }
                    }
                    else {
                        if (mapOpened) {                         //animations for landscape orientation
                            animateMapClosingForLandscape(view);
                        }
                        else {
                            animateMapOpeningForLandscape(view);
                        }
                    }
                }
                else {
                    UtilMethods.MapNotAvailableDialog(context);
                }
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        if (routes != null && routes.size() != 0 && routes.get(0).getSpeed() != ConstantValues.SPEED_VALUE_WORKAROUND) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            UtilMethods.isPermissionAllowed(context);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            if (UtilMethods.isPermissionAllowed(context)) {
                googleMap.setMyLocationEnabled(true);
            }
            boolean drawn = MapUtilMethods.drawPathFromData(routes, googleMap);
            if (drawn) {
                drawMap = true;
                routes = null;
            }
        }
        else {
            drawMap = false;
            routes = null;
        }
    }


    private float getInMotionValue() {
        float percentWithoutMotion = timeSpentOnStop * 100 / speedValues.size();
        float percentInMotion      = 100 - percentWithoutMotion;
        return timeSpent / 100 * percentInMotion;
    }

    private float getWithoutMotionValue(float timeSpentInMotion) {
        return timeSpent - timeSpentInMotion;
    }


    private void setupMapView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this.getActivity());
        mapView.getMapAsync(this);
        mapView.onResume();
    }

    private void setDataToInfoFragmentFields() {
        String avgFuelCons      = UtilMethods.formatFloatDecimalFormat(averageFuelConsumption) + " " + getString(R.string.fuel_cons_prefix);
        String fuelSpent        = UtilMethods.formatFloatDecimalFormat(this.fuelSpent) + " " + getString(R.string.fuel_prefix);
        String moneyOnFuelSpent = UtilMethods.formatFloatDecimalFormat(this.moneySpent) + " " + getString(R.string.currency_prefix);
        String avgSpeed         = UtilMethods.formatFloatDecimalFormat(this.avgSpeed) + " " + getString(R.string.speed_prefix);
        String maxSpeed         = UtilMethods.formatFloatDecimalFormat(this.maxSpeed) + " " + getString(R.string.speed_prefix);
        String distance         = UtilMethods.formatFloatDecimalFormat(distTravelled) + " " + getString(R.string.distance_prefix);

        for (String value : speedValues) {
            if (Float.valueOf(value) == 0) {
                timeSpentOnStop++;
            }
        }
        if (ConstantValues.LOGGING_ENABLED) {
            Log.d(LOG_TAG, "setDataToInfoFragmentFields: TIME All+" + timeSpent);
            Log.d(LOG_TAG, "setDataToInfoFragmentFields: TIME On stop+" + CalculationUtils.getTimeInNormalFormat(timeSpentOnStop, null));
        }

        float valInMotion      = getInMotionValue();
        float valWithoutMotion = getWithoutMotionValue(valInMotion);

        Resources res = getResources();

        tripTimeSpentOnStopView.setText(CalculationUtils.getTimeInNormalFormat(valWithoutMotion, res));
        tripTimeSpentInMotionView.setText(CalculationUtils.getTimeInNormalFormat(valInMotion, res));
        tripTimeSpentView.setText(CalculationUtils.getTimeInNormalFormat(timeSpent, res));
        tripAvgFuelConsumptionView.setText(avgFuelCons);
        tripMoneySpentView.setText(moneyOnFuelSpent);
        tripDistanceTravelledView.setText(distance);
        tripIdView.setText(String.valueOf(tripId));
        tripFuelSpentView.setText(fuelSpent);
        tripMaxSpeedView.setText(maxSpeed);
        tripAvgSpeedView.setText(avgSpeed);
        tripDateView.setText(tripDate);
    }

    private void animateMapOpeningForLandscape(final View view) {
        ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, View.TRANSLATION_X, 0, view.getWidth());
        dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        dataContainerAnimator.setInterpolator(new AnticipateInterpolator());
        dataContainerAnimator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {

            }

            @Override public void onAnimationEnd(Animator animation) {
                dataContainer.setVisibility(View.INVISIBLE);
                ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, View.TRANSLATION_X, -view.getWidth(), 0);
                mapViewAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
                mapViewAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                mapViewAnimator.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {
                        mapView.setVisibility(View.VISIBLE);
                        mapOpened = true;
                    }

                    @Override public void onAnimationEnd(Animator animation) {

                    }

                    @Override public void onAnimationCancel(Animator animation) {

                    }

                    @Override public void onAnimationRepeat(Animator animation) {

                    }
                });
                mapViewAnimator.start();
            }

            @Override public void onAnimationCancel(Animator animation) {

            }

            @Override public void onAnimationRepeat(Animator animation) {

            }
        });
        dataContainerAnimator.start();
    }

    private void animateMapClosingForLandscape(final View view) {
        ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, View.TRANSLATION_X, 0, -view.getWidth());
        mapViewAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        mapViewAnimator.setInterpolator(new AnticipateInterpolator());
        mapViewAnimator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
            }

            @Override public void onAnimationEnd(Animator animation) {
                mapView.setVisibility(View.INVISIBLE);
                ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, View.TRANSLATION_X, view.getWidth(), 0);
                dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
                dataContainerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                dataContainerAnimator.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {
                        dataContainer.setVisibility(View.VISIBLE);
                        mapOpened = false;
                    }

                    @Override public void onAnimationEnd(Animator animation) {

                    }

                    @Override public void onAnimationCancel(Animator animation) {

                    }

                    @Override public void onAnimationRepeat(Animator animation) {

                    }
                });
                dataContainerAnimator.start();
            }

            @Override public void onAnimationCancel(Animator animation) {

            }

            @Override public void onAnimationRepeat(Animator animation) {

            }
        });
        mapViewAnimator.start();
    }

    private void animateMapOpeningForPortrait(final View view) {
        ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, View.TRANSLATION_Y, 0, view.getHeight());
        dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        dataContainerAnimator.setInterpolator(new AnticipateInterpolator());
        dataContainerAnimator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {

            }

            @Override public void onAnimationEnd(Animator animation) {
                dataContainer.setVisibility(View.INVISIBLE);
                ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, View.TRANSLATION_Y, -view.getHeight(), 0);
                mapViewAnimator.setDuration(1500);
                mapViewAnimator.setInterpolator(new BounceInterpolator());
                mapViewAnimator.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {
                        mapView.setVisibility(View.VISIBLE);
                        mapOpened = true;
                    }

                    @Override public void onAnimationEnd(Animator animation) {

                    }

                    @Override public void onAnimationCancel(Animator animation) {

                    }

                    @Override public void onAnimationRepeat(Animator animation) {

                    }
                });
                mapViewAnimator.start();
            }

            @Override public void onAnimationCancel(Animator animation) {

            }

            @Override public void onAnimationRepeat(Animator animation) {

            }
        });
        dataContainerAnimator.start();
    }

    private void animateMapClosingForPortrait(final View view) {
        ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, View.TRANSLATION_Y, 0, -view.getHeight());
        mapViewAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        mapViewAnimator.setInterpolator(new AnticipateInterpolator());
        mapViewAnimator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
            }

            @Override public void onAnimationEnd(Animator animation) {
                mapView.setVisibility(View.INVISIBLE);
                ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, View.TRANSLATION_Y, view.getHeight(), 0);
                dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
                dataContainerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                dataContainerAnimator.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {
                        dataContainer.setVisibility(View.VISIBLE);
                        mapOpened = false;
                    }

                    @Override public void onAnimationEnd(Animator animation) {

                    }

                    @Override public void onAnimationCancel(Animator animation) {

                    }

                    @Override public void onAnimationRepeat(Animator animation) {

                    }
                });
                dataContainerAnimator.start();
            }

            @Override public void onAnimationCancel(Animator animation) {

            }

            @Override public void onAnimationRepeat(Animator animation) {

            }
        });
        mapViewAnimator.start();
    }
}

