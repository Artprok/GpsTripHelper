package com.example.aprokopenko.triphelper.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.aprokopenko.triphelper.utils.util_methods.MapUtilMethods;
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
 * Class representing a {@link View} populated with {@link com.example.aprokopenko.triphelper.datamodel.TripData}.
 */
@Singleton
public class TripInfoFragment extends android.support.v4.app.Fragment implements OnMapReadyCallback {
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

  private ArrayList<String> speedValues;
  private ArrayList<Route> routes;
  private Unbinder unbinder;
  private String currency_prefix;
  private String tripDate;
  private boolean mapOpened;
  private boolean drawMap;
  private float averageFuelConsumption;
  private float timeSpentOnStop;
  private float distTravelled;
  private float moneySpent;
  private float fuelSpent;
  private float timeSpent;
  private float avgSpeed;
  private float maxSpeed;
  private int tripId;

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

  @Override public void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      final ArrayList<String> longitudeArray = getArguments().getStringArrayList(LONGITUDE_ARR);
      final ArrayList<String> latitudeArray = getArguments().getStringArrayList(LATITUDE_ARR);
      final ArrayList<String> speedArray = getArguments().getStringArrayList(SPEED_ARR);

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

  @Override public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_trip_info, container, false);

    unbinder = ButterKnife.bind(this, view);
    setupMapView(savedInstanceState);
    return view;
  }

  @Override public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setCurrencyUnit(TripHelperApp.getSharedPreferences().getString(CURRENCY_UNIT, ""));
    setDataToInfoFragmentFields();
  }

  private void setCurrencyUnit(@NonNull final String curUnit) {
    if (!TextUtils.isEmpty(curUnit)) {
      currency_prefix = getString(R.string.grn);
    } else {
      currency_prefix = curUnit;
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  @Override public void onMapReady(@NonNull final GoogleMap googleMap) {
    if (routes != null && routes.size() != 0 && routes.get(0).getSpeed() != ConstantValues.SPEED_VALUE_WORKAROUND) {
      googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
      UtilMethods.isPermissionAllowed(getActivity());
      googleMap.getUiSettings().setMyLocationButtonEnabled(true);
      googleMap.getUiSettings().setZoomControlsEnabled(true);
      googleMap.getUiSettings().setMapToolbarEnabled(true);
      googleMap.getUiSettings().setCompassEnabled(true);
      if (UtilMethods.isPermissionAllowed(getActivity())) {
        googleMap.setMyLocationEnabled(true);
      }
      if (MapUtilMethods.drawPathFromData(routes, googleMap)) {
        drawMap = true;
        routes = null;
      }
    } else {
      drawMap = false;
      routes = null;
    }
  }

  @OnClick(R.id.btn_mapTurnActive)
  public void onOpenMapButtonClick(@NonNull final View view) {
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

  private static float getInMotionValue(final float timeSpentOnStop, @NonNull final ArrayList<String> speedValues, final float timeSpent) {
    final float percentWithoutMotion = timeSpentOnStop * 100 / speedValues.size();
    final float percentInMotion = 100 - percentWithoutMotion;

    return timeSpent / 100 * percentInMotion;
  }

  private static float getWithoutMotionValue(final float timeSpentInMotion, final float timeSpent) {
    return timeSpent - timeSpentInMotion;
  }

  private void setupMapView(@Nullable final Bundle savedInstanceState) {
    mapView.onCreate(savedInstanceState);
    MapsInitializer.initialize(this.getActivity());
    mapView.getMapAsync(this);
    mapView.onResume();
  }

  private void setDataToInfoFragmentFields() {
    final Resources res = getResources();

    for (final String value : speedValues) {
      if (Float.valueOf(value) == 0) {
        timeSpentOnStop++;
      }
    }

    if (DEBUG) {
      Log.d(LOG_TAG, "setDataToInfoFragmentFields: TIME All+" + timeSpent);
      Log.d(LOG_TAG, "setDataToInfoFragmentFields: TIME On stop+" + CalculationUtils.getTimeInNormalFormat(timeSpentOnStop, res));
    }
    final float timeInMotion = getInMotionValue(timeSpentOnStop, speedValues, timeSpent);
    tripTimeSpentOnStopView.setText(CalculationUtils.getTimeInNormalFormat(getWithoutMotionValue(timeInMotion, timeSpent), res));
    tripTimeSpentInMotionView.setText(CalculationUtils.getTimeInNormalFormat(timeInMotion, res));
    tripTimeSpentView.setText(CalculationUtils.getTimeInNormalFormat(timeSpent, res));
    tripAvgFuelConsumptionView.setText(UtilMethods.formatFloatDecimalFormat(averageFuelConsumption) + " " + getString(R.string.fuel_cons_prefix));
    tripMoneySpentView.setText(UtilMethods.formatFloatDecimalFormat(this.moneySpent) + " " + currency_prefix);
    tripDistanceTravelledView.setText(UtilMethods.formatFloatDecimalFormat(distTravelled) + " " + getString(R.string.distance_prefix));
    tripIdView.setText(String.valueOf(tripId));
    tripFuelSpentView.setText(UtilMethods.formatFloatDecimalFormat(this.fuelSpent) + " " + getString(R.string.fuel_prefix));
    tripMaxSpeedView.setText(UtilMethods.formatFloatDecimalFormat(this.maxSpeed) + " " + getString(R.string.speed_prefix));
    tripAvgSpeedView.setText(UtilMethods.formatFloatDecimalFormat(this.avgSpeed) + " " + getString(R.string.speed_prefix));
    tripDateView.setText(tripDate);
  }

  private void animateMapOpeningForLandscape(@NonNull final View view) {
    final ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, View.TRANSLATION_X, 0, view.getWidth());

    dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
    dataContainerAnimator.setInterpolator(new AnticipateInterpolator());
    dataContainerAnimator.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(@NonNull final Animator animation) {
      }

      @Override public void onAnimationEnd(@NonNull final Animator animation) {
        final ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, View.TRANSLATION_X, -view.getWidth(), 0);

        dataContainer.setVisibility(View.INVISIBLE);
        mapViewAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        mapViewAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mapViewAnimator.addListener(new Animator.AnimatorListener() {
          @Override public void onAnimationStart(@NonNull final Animator animation) {
            mapView.setVisibility(View.VISIBLE);
            mapOpened = true;
          }

          @Override public void onAnimationEnd(@NonNull final Animator animation) {
          }

          @Override public void onAnimationCancel(@NonNull final Animator animation) {
          }

          @Override public void onAnimationRepeat(@NonNull final Animator animation) {
          }
        });
        mapViewAnimator.start();
      }

      @Override public void onAnimationCancel(@NonNull final Animator animation) {
      }

      @Override public void onAnimationRepeat(@NonNull final Animator animation) {
      }
    });
    dataContainerAnimator.start();
  }

  private void animateMapClosingForLandscape(@NonNull final View view) {
    final ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, View.TRANSLATION_X, 0, -view.getWidth());

    mapViewAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
    mapViewAnimator.setInterpolator(new AnticipateInterpolator());
    mapViewAnimator.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(@NonNull final Animator animation) {
      }

      @Override public void onAnimationEnd(@NonNull final Animator animation) {
        final ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, View.TRANSLATION_X, view.getWidth(), 0);

        mapView.setVisibility(View.INVISIBLE);
        dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        dataContainerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        dataContainerAnimator.addListener(new Animator.AnimatorListener() {
          @Override public void onAnimationStart(@NonNull final Animator animation) {
            dataContainer.setVisibility(View.VISIBLE);
            mapOpened = false;
          }

          @Override public void onAnimationEnd(@NonNull final Animator animation) {
          }

          @Override public void onAnimationCancel(@NonNull final Animator animation) {
          }

          @Override public void onAnimationRepeat(@NonNull final Animator animation) {
          }
        });
        dataContainerAnimator.start();
      }

      @Override public void onAnimationCancel(@NonNull final Animator animation) {
      }

      @Override public void onAnimationRepeat(@NonNull final Animator animation) {
      }
    });
    mapViewAnimator.start();
  }

  private void animateMapOpeningForPortrait(@NonNull final View view) {
    final ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, View.TRANSLATION_Y, 0, view.getHeight());

    dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
    dataContainerAnimator.setInterpolator(new AnticipateInterpolator());
    dataContainerAnimator.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(@NonNull final Animator animation) {
      }

      @Override public void onAnimationEnd(@NonNull final Animator animation) {
        final ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, View.TRANSLATION_Y, -view.getHeight(), 0);

        dataContainer.setVisibility(View.INVISIBLE);
        mapViewAnimator.setDuration(ConstantValues.ANIMATE_MAP_OPENING_DURATION);
        mapViewAnimator.setInterpolator(new BounceInterpolator());
        mapViewAnimator.addListener(new Animator.AnimatorListener() {
          @Override public void onAnimationStart(@NonNull final Animator animation) {
            mapView.setVisibility(View.VISIBLE);
            mapOpened = true;
          }

          @Override public void onAnimationEnd(@NonNull final Animator animation) {
          }

          @Override public void onAnimationCancel(@NonNull final Animator animation) {
          }

          @Override public void onAnimationRepeat(@NonNull final Animator animation) {
          }
        });
        mapViewAnimator.start();
      }

      @Override public void onAnimationCancel(@NonNull final Animator animation) {
      }

      @Override public void onAnimationRepeat(@NonNull final Animator animation) {
      }
    });
    dataContainerAnimator.start();
  }

  private void animateMapClosingForPortrait(@NonNull final View view) {
    final ObjectAnimator mapViewAnimator = ObjectAnimator.ofFloat(mapView, View.TRANSLATION_Y, 0, -view.getHeight());

    mapViewAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
    mapViewAnimator.setInterpolator(new AnticipateInterpolator());
    mapViewAnimator.addListener(new Animator.AnimatorListener() {
      @Override public void onAnimationStart(@NonNull final Animator animation) {
      }

      @Override public void onAnimationEnd(@NonNull final Animator animation) {
        final ObjectAnimator dataContainerAnimator = ObjectAnimator.ofFloat(dataContainer, View.TRANSLATION_Y, view.getHeight(), 0);

        mapView.setVisibility(View.INVISIBLE);
        dataContainerAnimator.setDuration(ConstantValues.TEXT_ANIM_DURATION);
        dataContainerAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        dataContainerAnimator.addListener(new Animator.AnimatorListener() {
          @Override public void onAnimationStart(@NonNull final Animator animation) {
            dataContainer.setVisibility(View.VISIBLE);
            mapOpened = false;
          }

          @Override public void onAnimationEnd(@NonNull final Animator animation) {
          }

          @Override public void onAnimationCancel(@NonNull final Animator animation) {
          }

          @Override public void onAnimationRepeat(@NonNull final Animator animation) {
          }
        });
        dataContainerAnimator.start();
      }

      @Override public void onAnimationCancel(@NonNull final Animator animation) {
      }

      @Override public void onAnimationRepeat(@NonNull final Animator animation) {
      }
    });
    mapViewAnimator.start();
  }
}