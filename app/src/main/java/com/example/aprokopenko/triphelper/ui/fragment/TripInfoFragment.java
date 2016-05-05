package com.example.aprokopenko.triphelper.ui.fragment;

import android.content.res.Resources;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.support.annotation.Nullable;
import android.animation.ObjectAnimator;
import android.support.v4.app.Fragment;
import android.widget.RelativeLayout;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.animation.Animator;
import android.content.Context;
import android.widget.TextView;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

import com.example.aprokopenko.triphelper.utils.util_methods.MapUtilMethods;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.MathUtils;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;
import com.example.aprokopenko.triphelper.R;
import com.google.android.gms.maps.MapView;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Bind;

public class TripInfoFragment extends Fragment implements OnMapReadyCallback {

    @Bind(R.id.tripAvgFuelConsumption)
    TextView       tripAvgFuelConsumptionView;
    @Bind(R.id.tripDistanceTravelled)
    TextView       tripDistanceTravelledView;
    @Bind(R.id.tripTimeSpentInMotion)
    TextView       tripTimeSpentInMotionView;
    @Bind(R.id.tripTimeSpentOnStop)
    TextView       tripTimeSpentOnStopView;
    @Bind(R.id.tripMoneyOnTripFuelSpent)
    TextView       tripMoneySpentView;
    @Bind(R.id.tripFuelSpent)
    TextView       tripFuelSpentView;
    @Bind(R.id.tripTimeSpent)
    TextView       tripTimeSpentView;
    @Bind(R.id.tripAvgSpeed)
    TextView       tripAvgSpeedView;
    @Bind(R.id.tripMaxSpeed)
    TextView       tripMaxSpeedView;
    @Bind(R.id.mapTurnActiveButton)
    ImageButton    openMapButton;
    @Bind(R.id.textDataContainer)
    RelativeLayout dataContainer;
    @Bind(R.id.tripDate)
    TextView       tripDateView;
    @Bind(R.id.tripId)
    TextView       tripIdView;
    @Bind(R.id.mapView)
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

    private float  averageFuelConsumption;
    private float  timeSpentInMotion;
    private float  timeSpentOnStop;
    private float  distTravelled;
    private float  moneySpent;
    private float  fuelSpent;
    private float  timeSpent;
    private String tripDate;
    private float  avgSpeed;
    private float  maxSpeed;
    private int    tripId;

    private ArrayList<String> speedValues;
    private Context           context;
    private ArrayList<Route>  routes;

    private boolean mapOpened = false;


    public TripInfoFragment() {
    }

    public static TripInfoFragment newInstance(String tripDate, float distTravelled, float avgSpeed, float timeSpent,
                                               float timeSpentInMotion, float timeSpentOnStop, float averageFuelCons, float fuelSpent,
                                               int tripId, ArrayList<Route> routes, Float moneySpent, Float maxSpeed) {

        Log.d(LOG_TAG, "newInstance: CALLED");
        TripInfoFragment  fragment       = new TripInfoFragment();
        Bundle            args           = new Bundle();
        ArrayList<String> latitudeArray  = new ArrayList<>();
        ArrayList<String> longitudeArray = new ArrayList<>();
        ArrayList<String> speedArray     = new ArrayList<>();
        for (Route tmpRoute : routes) {
            if (tmpRoute != null) {
                // FIXME: 12.04.2016 ToString?? :(
                LatLng tempLatLang = tmpRoute.getRoutePoints();
                latitudeArray.add(String.valueOf(tempLatLang.latitude));
                longitudeArray.add(String.valueOf(tempLatLang.longitude));
                speedArray.add(String.valueOf(tmpRoute.getSpeed()));
            }
        }

        args.putFloat(TIME_SPENT_WITHOUT_MOTION, timeSpentOnStop);
        args.putFloat(TIME_SPENT_IN_MOTION, timeSpentInMotion);
        args.putStringArrayList(LONGITUDE_ARR, longitudeArray);
        args.putStringArrayList(LATITUDE_ARR, latitudeArray);
        args.putFloat(AVERAGE_FUEL_CONS, averageFuelCons);
        args.putFloat(DISTANCE_TRAVELLED, distTravelled);
        args.putStringArrayList(SPEED_ARR, speedArray);
        args.putFloat(AVERAGE_SPEED, avgSpeed);
        args.putFloat(MONEY_SPENT, moneySpent);
        args.putFloat(TIME_SPENT, timeSpent);
        args.putFloat(FUEL_SPENT, fuelSpent);
        args.putString(TRIP_DATE, tripDate);
        args.putFloat(MAX_SPEED, maxSpeed);
        args.putInt(TRIP_ID, tripId);

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
            timeSpentInMotion = getArguments().getFloat(TIME_SPENT_IN_MOTION);
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
        ButterKnife.bind(this, v);
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
                Log.d(LOG_TAG, "onClick: called");
                int orientation = getActivity().getResources().getConfiguration().orientation;
                if (orientation == 1) {
                    if (mapOpened) {
                        animateMapClosingForPortrait(view);
                    }
                    else {
                        animateMapOpeningForPortrait(view);
                    }
                }
                else {
                    if (mapOpened) {
                        animateMapClosingForLandscape(view);
                    }
                    else {
                        animateMapOpeningForLandscape(view);
                    }
                }
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        UtilMethods.checkPermission(context);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setMyLocationEnabled(true);
        boolean drawn = MapUtilMethods.drawPathFromData(routes, googleMap);
        if (drawn) {
            routes = null;
        }
    }


    private float getInMotionValue() {
        float percentWithoutMotion = timeSpentOnStop * 100 / speedValues.size();
        float percentInMotion      = 100 - percentWithoutMotion;
        return timeSpent / 100 * percentInMotion;
    }

    @Contract(pure = true) private float getWithoutMotionValue(float timeSpentInMotion) {
        return timeSpent - timeSpentInMotion;
    }


    private void setupMapView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(this.getActivity());
        mapView.getMapAsync(this);
        mapView.onResume();
    }

    private void setDataToInfoFragmentFields() {
        String avgFuelCons      = UtilMethods.formatFloat(averageFuelConsumption) + " " + getString(R.string.fuel_prefix);
        String fuelSpent        = UtilMethods.formatFloat(this.fuelSpent) + " " + getString(R.string.fuel_prefix);
        String moneyOnFuelSpent = UtilMethods.formatFloat(this.moneySpent) + " " + getString(R.string.currency_prefix);
        String avgSpeed         = UtilMethods.formatFloat(this.avgSpeed) + " " + getString(R.string.speed_prefix);
        String maxSpeed         = UtilMethods.formatFloat(this.maxSpeed) + " " + getString(R.string.speed_prefix);
        String distance         = UtilMethods.formatFloat(distTravelled) + " " + getString(R.string.distance_prefix);

        for (String value : speedValues) {
            if (Float.valueOf(value) == 0) {
                timeSpentOnStop++;
            }
        }
        if (ConstantValues.DEBUG_MODE) {
            Log.d(LOG_TAG, "setDataToInfoFragmentFields: TIME All+" + timeSpent);
            Log.d(LOG_TAG, "setDataToInfoFragmentFields: TIME in Motion+" + timeSpentInMotion);
            Log.d(LOG_TAG, "setDataToInfoFragmentFields: TIME On stop+" + MathUtils.getTimeInNormalFormat(timeSpentOnStop, null));
        }

        float valInMotion      = getInMotionValue();
        float valWithoutMotion = getWithoutMotionValue(valInMotion);

        Resources res = getResources();

        tripTimeSpentOnStopView.setText(MathUtils.getTimeInNormalFormat(valWithoutMotion, res));
        tripTimeSpentInMotionView.setText(MathUtils.getTimeInNormalFormat(valInMotion, res));
        tripTimeSpentView.setText(MathUtils.getTimeInNormalFormat(timeSpent, res));
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
        dataContainerAnimator.setDuration(900);
        dataContainerAnimator.setInterpolator(new AnticipateInterpolator());
        dataContainerAnimator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
                Log.d(LOG_TAG, "onAnimationStart: MapOpen dataContAnim anim");
            }

            @Override public void onAnimationEnd(Animator animation) {
                dataContainer.setVisibility(View.INVISIBLE);
                ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, View.TRANSLATION_X, -view.getWidth(), 0);
                mapViewAnimator.setDuration(900);
                mapViewAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                mapViewAnimator.addListener(new Animator.AnimatorListener() {
                    @Override public void onAnimationStart(Animator animation) {
                        mapView.setVisibility(View.VISIBLE);
                        mapOpened = true;
                        Log.d(LOG_TAG, "onAnimationStart: MapOpen mapView anim");
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
        mapViewAnimator.setDuration(900);
        mapViewAnimator.setInterpolator(new AnticipateInterpolator());
        mapViewAnimator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
            }

            @Override public void onAnimationEnd(Animator animation) {
                mapView.setVisibility(View.INVISIBLE);
                ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, View.TRANSLATION_X, view.getWidth(), 0);
                dataContainerAnimator.setDuration(900);
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
        dataContainerAnimator.setDuration(900);
        dataContainerAnimator.setInterpolator(new AnticipateInterpolator());
        dataContainerAnimator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
                Log.d(LOG_TAG, "onAnimationStart: MapOpen dataContAnim anim");
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
                        Log.d(LOG_TAG, "onAnimationStart: MapOpen mapView anim");
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
        mapViewAnimator.setDuration(900);
        mapViewAnimator.setInterpolator(new AnticipateInterpolator());
        mapViewAnimator.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {
            }

            @Override public void onAnimationEnd(Animator animation) {
                mapView.setVisibility(View.INVISIBLE);
                ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, View.TRANSLATION_Y, view.getHeight(), 0);
                dataContainerAnimator.setDuration(900);
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

