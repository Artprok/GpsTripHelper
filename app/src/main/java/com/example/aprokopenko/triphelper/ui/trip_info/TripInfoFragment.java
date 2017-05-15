package com.example.aprokopenko.triphelper.ui.trip_info;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Class representing a {@link android.view.View} populated with {@link com.example.aprokopenko.triphelper.datamodel.TripData}.
 */
@Singleton
public class TripInfoFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback, TripInfoContract.View {
    public static final String CURRENCY_UNIT = "currencyUnit";
    @BindView(R.id.text_tripAvgFuelConsumption)
    TextView tripAvgFuelConsumptionView;
    @BindView(R.id.text_tripDistanceTravelled)
    TextView tripDistanceTravelledView;
    @BindView(R.id.text_tripTimeSpentInMotion)
    TextView tripTimeSpentInMotionView;
    @BindView(R.id.text_tripTimeSpentOnStop)
    TextView tripTimeSpentOnStopView;
    @BindView(R.id.text_tripMoneyOnTripFuelSpent)
    TextView tripMoneySpentView;
    @BindView(R.id.text_tripFuelSpent)
    TextView tripFuelSpentView;
    @BindView(R.id.text_tripTimeSpent)
    TextView tripTimeSpentView;
    @BindView(R.id.text_tripAvgSpeed)
    TextView tripAvgSpeedView;
    @BindView(R.id.text_tripMaxSpeed)
    TextView tripMaxSpeedView;
    @BindView(R.id.btn_mapTurnActive)
    ImageButton openMapButton;
    @BindView(R.id.textDataContainer)
    LinearLayoutCompat dataContainer;
    @BindView(R.id.text_tripDate)
    TextView tripDateView;
    @BindView(R.id.text_tripId)
    TextView tripIdView;
    @BindView(R.id.mapView)
    MapView mapView;

    private static final String LOG_TAG = "TripInfoFragment";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TIME_SPENT_IN_MOTION = "TimeSpentOnMotion";
    private static final String TIME_SPENT_WITHOUT_MOTION = "TimeSpentOnStop";
    private static final String LONGITUDE_ARR = "LongitudeArray";
    private static final String DISTANCE_TRAVELLED = "DistTravelled";
    private static final String LATITUDE_ARR = "LatitudeArray";
    private static final String AVERAGE_FUEL_CONS = "FuelConsumed";
    private static final String MONEY_SPENT = "MoneySpent";
    private static final String SPEED_ARR = "SpeedArray";
    private static final String TIME_SPENT = "TimeSpent";
    private static final String FUEL_SPENT = "FuelSpent";
    private static final String AVERAGE_SPEED = "AvgSpeed";
    private static final String TRIP_DATE = "TripDate";
    private static final String MAX_SPEED = "MaxSpeed";
    private static final String TRIP_ID = "TripId";

    private Unbinder unbinder;
    private String currency_prefix;
    private boolean mapOpened;
    private boolean drawMap;

    private TripInfoContract.UserActionListener userActionListener;

    public TripInfoFragment() {
    }

    public static TripInfoFragment newInstance(@NonNull final Trip trip) {
        final TripInfoFragment fragment = new TripInfoFragment();

        final Bundle args = new Bundle();
        final ArrayList<String> latitudeArray = new ArrayList<>();
        final ArrayList<String> longitudeArray = new ArrayList<>();
        final ArrayList<String> speedArray = new ArrayList<>();
        final ArrayList<Route> routes = trip.getRoutes();

        if (routes != null) {
            for (final Route tmpRoute : routes) {
                latitudeArray.add(String.valueOf(tmpRoute.getLatitude()));
                longitudeArray.add(String.valueOf(tmpRoute.getLongitude()));
                speedArray.add(String.valueOf(tmpRoute.getSpeed()));
            }
        }

        args.putFloat(TIME_SPENT_WITHOUT_MOTION, trip.getTimeSpentOnStop());
        args.putFloat(TIME_SPENT_IN_MOTION, trip.getTimeSpentInMotion());
        args.putStringArrayList(LONGITUDE_ARR, longitudeArray);
        args.putStringArrayList(LATITUDE_ARR, latitudeArray);
        args.putFloat(AVERAGE_FUEL_CONS, trip.getAvgFuelConsumption());
        args.putFloat(DISTANCE_TRAVELLED, trip.getDistanceTravelled());
        args.putStringArrayList(SPEED_ARR, speedArray);
        args.putFloat(AVERAGE_SPEED, trip.getAvgSpeed());
        args.putFloat(MONEY_SPENT, trip.getMoneyOnFuelSpent());
        args.putFloat(TIME_SPENT, trip.getTimeSpentForTrip());
        args.putFloat(FUEL_SPENT, trip.getFuelSpent());
        args.putString(TRIP_DATE, trip.getTripDate());
        args.putFloat(MAX_SPEED, trip.getMaxSpeed());
        args.putInt(TRIP_ID, trip.getTripID());

        fragment.setArguments(args);
        return fragment;
    }

    public void setupUserActionListener(TripInfoContract.View view) {
        if (userActionListener == null) {
            userActionListener = new TripInfoPresenter(this);
        }
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserActionListener(this);
        if (getArguments() != null) {
            userActionListener.onCreate(getArguments());
        }
    }

    @Override
    public android.view.View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final android.view.View view = inflater.inflate(R.layout.fragment_trip_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupMapView(savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final android.view.View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCurrencyUnit(TripHelperApp.getSharedPreferences().getString(CURRENCY_UNIT, ""));
        userActionListener.onViewCreated();
    }

    private void setCurrencyUnit(@NonNull final String curUnit) {
        if (!TextUtils.isEmpty(curUnit)) {
            currency_prefix = getString(R.string.grn);
        } else {
            currency_prefix = curUnit;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) {
        final boolean needToDraw;
        final boolean isMapReady = userActionListener.onMapReady();
        if (isMapReady) {
            needToDraw = true;
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            UtilMethods.isPermissionAllowed(getActivity());
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            if (UtilMethods.isPermissionAllowed(getActivity())) {
                googleMap.setMyLocationEnabled(true);
            }
        } else {
            needToDraw = false;
        }
        drawMap = userActionListener.onDrawMap(googleMap, needToDraw);
    }

    @OnClick(R.id.btn_mapTurnActive)
    public void onOpenMapButtonClick(@NonNull final android.view.View view) {
        if (drawMap) {
            if (getActivity().getResources().getConfiguration().orientation == 1) {//animations for portrait orientation
                if (mapOpened) {
                    animateMapClosingForPortrait(view);
                } else {
                    animateMapOpeningForPortrait(view);
                }
            } else {
                if (mapOpened) {//animations for landscape orientation
                    animateMapClosingForLandscape(view);
                } else {
                    animateMapOpeningForLandscape(view);
                }
            }
        } else {
            UtilMethods.MapNotAvailableDialog(getActivity());
        }
    }

    @Override
    public void setTripTimeSpentOnStopText(float withoutMotionValue) {
        tripTimeSpentOnStopView.setText(CalculationUtils.getTimeInNormalFormat(withoutMotionValue, getResources()));
    }

    @Override
    public void setTripTimeSpentInMotionValue(float timeInMotion) {
        tripTimeSpentInMotionView.setText(CalculationUtils.getTimeInNormalFormat(timeInMotion, getResources()));
    }

    @Override
    public void setTripTimeSpentValue(float timeSpent) {
        tripTimeSpentView.setText(CalculationUtils.getTimeInNormalFormat(timeSpent, getResources()));
    }

    @Override
    public void setTripAvgFuelConsumptionValue(float averageFuelConsumption) {
        tripAvgFuelConsumptionView.setText(UtilMethods.formatFloatDecimalFormat(averageFuelConsumption) + " " + getString(R.string.fuel_cons_prefix));
    }

    @Override
    public void setTripMoneySpentValue(float moneySpent) {
        tripMoneySpentView.setText(UtilMethods.formatFloatDecimalFormat(moneySpent) + " " + currency_prefix);
    }

    @Override
    public void setTripDistanceTravelledValuse(float distTravelled) {
        tripDistanceTravelledView.setText(UtilMethods.formatFloatDecimalFormat(distTravelled) + " " + getString(R.string.distance_prefix));
    }

    @Override
    public void setTripIdValue(String id) {
        tripIdView.setText(id);
    }

    @Override
    public void setTripFuelSpentValue(float fuelSpent) {
        tripFuelSpentView.setText(UtilMethods.formatFloatDecimalFormat(fuelSpent) + " " + getString(R.string.fuel_prefix));
    }

    @Override
    public void setTripMaxSpeedValue(float maxSpeed) {
        tripMaxSpeedView.setText(UtilMethods.formatFloatDecimalFormat(maxSpeed) + " " + getString(R.string.speed_prefix));
    }

    @Override
    public void setTripAvgSpeedValue(float avgSpeed) {
        tripAvgSpeedView.setText(UtilMethods.formatFloatDecimalFormat(avgSpeed) + " " + getString(R.string.speed_prefix));
    }

    @Override
    public void setTripDateValue(String tripDate) {
        tripDateView.setText(tripDate);
    }

    private void setupMapView(@Nullable final Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());
        mapView.getMapAsync(this);
        mapView.onResume();
    }

    private void animateMapOpeningForLandscape(@NonNull final android.view.View view) {
        final ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, android.view.View.TRANSLATION_X, 0, view.getWidth());

        dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        dataContainerAnimator.setInterpolator(new AnticipateInterpolator());
        dataContainerAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull final Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull final Animator animation) {
                final ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, android.view.View.TRANSLATION_X, -view.getWidth(), 0);

                dataContainer.setVisibility(android.view.View.INVISIBLE);
                mapViewAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
                mapViewAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                mapViewAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull final Animator animation) {
                        mapView.setVisibility(android.view.View.VISIBLE);
                        mapOpened = true;
                    }

                    @Override
                    public void onAnimationEnd(@NonNull final Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(@NonNull final Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull final Animator animation) {
                    }
                });
                mapViewAnimator.start();
            }

            @Override
            public void onAnimationCancel(@NonNull final Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull final Animator animation) {
            }
        });
        dataContainerAnimator.start();
    }

    private void animateMapClosingForLandscape(@NonNull final android.view.View view) {
        final ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, android.view.View.TRANSLATION_X, 0, -view.getWidth());

        mapViewAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        mapViewAnimator.setInterpolator(new AnticipateInterpolator());
        mapViewAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull final Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull final Animator animation) {
                final ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, android.view.View.TRANSLATION_X, view.getWidth(), 0);

                mapView.setVisibility(android.view.View.INVISIBLE);
                dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
                dataContainerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                dataContainerAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull final Animator animation) {
                        dataContainer.setVisibility(android.view.View.VISIBLE);
                        mapOpened = false;
                    }

                    @Override
                    public void onAnimationEnd(@NonNull final Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(@NonNull final Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull final Animator animation) {
                    }
                });
                dataContainerAnimator.start();
            }

            @Override
            public void onAnimationCancel(@NonNull final Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull final Animator animation) {
            }
        });
        mapViewAnimator.start();
    }

    private void animateMapOpeningForPortrait(@NonNull final android.view.View view) {
        final ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, android.view.View.TRANSLATION_Y, 0, view.getHeight());

        dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        dataContainerAnimator.setInterpolator(new AnticipateInterpolator());
        dataContainerAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull final Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull final Animator animation) {
                final ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, android.view.View.TRANSLATION_Y, -view.getHeight(), 0);

                dataContainer.setVisibility(android.view.View.INVISIBLE);
                mapViewAnimator.setDuration(ConstantValues.ANIMATE_MAP_OPENING_DURATION);
                mapViewAnimator.setInterpolator(new BounceInterpolator());
                mapViewAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull final Animator animation) {
                        mapView.setVisibility(android.view.View.VISIBLE);
                        mapOpened = true;
                    }

                    @Override
                    public void onAnimationEnd(@NonNull final Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(@NonNull final Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull final Animator animation) {
                    }
                });
                mapViewAnimator.start();
            }

            @Override
            public void onAnimationCancel(@NonNull final Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull final Animator animation) {
            }
        });
        dataContainerAnimator.start();
    }

    private void animateMapClosingForPortrait(@NonNull final android.view.View view) {
        final ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, android.view.View.TRANSLATION_Y, 0, -view.getHeight());

        mapViewAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        mapViewAnimator.setInterpolator(new AnticipateInterpolator());
        mapViewAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull final Animator animation) {
            }

            @Override
            public void onAnimationEnd(@NonNull final Animator animation) {
                final ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, android.view.View.TRANSLATION_Y, view.getHeight(), 0);

                mapView.setVisibility(android.view.View.INVISIBLE);
                dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
                dataContainerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                dataContainerAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull final Animator animation) {
                        dataContainer.setVisibility(android.view.View.VISIBLE);
                        mapOpened = false;
                    }

                    @Override
                    public void onAnimationEnd(@NonNull final Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(@NonNull final Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull final Animator animation) {
                    }
                });
                dataContainerAnimator.start();
            }

            @Override
            public void onAnimationCancel(@NonNull final Animator animation) {
            }

            @Override
            public void onAnimationRepeat(@NonNull final Animator animation) {
            }
        });
        mapViewAnimator.start();
    }

    @Override
    public void setPresenter(@NonNull final TripInfoPresenter tripInfoPresenter) {
        userActionListener = tripInfoPresenter;
    }
}