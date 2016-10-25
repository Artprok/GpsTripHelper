package com.example.aprokopenko.triphelper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.aprokopenko.triphelper.application.TripHelperApp;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.datamodel.TripData;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.example.aprokopenko.triphelper.listener.SpeedChangeListener;
import com.example.aprokopenko.triphelper.service.LocationService;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.CalculationUtils;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import rx.Subscriber;

public class TripProcessor implements Parcelable {
  private static final String LOG_TAG = "TripProcessor";
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private final Context context;

  private ArrayList<Float> avgSpeedArrayList;
  private boolean fileIsInReadMode;
  private boolean fileIsInWriteMode;
  private ServiceConnection serviceConnection;
  private LocationService locationService;
  private long tripStartTime;
  private int currentTripId;
  private Intent serviceIntent;
  private float averageSpeed;
  private boolean serviceBound;
  private float maxSpeedVal;
  private GpsHandler gpsHandler;
  private TripData tripData;
  private ArrayList<Route> routes;

  private float fuelConsFromSettings;
  private int fuelCapacity;
  private float fuelPrice;

  private Subscriber<Location> locationSubscriber;
  private Subscriber<Float> maxSpeedSubscriber;
  private Subscriber<Float> speedSubscriber;

  private SpeedChangeListener speedChangeListener;


  public TripProcessor(@NonNull final Context context, final float fuelConsFromSettings, final float fuelPriceFromSettings, final int fuelCapacityFromSettings) {
    if (DEBUG) {
      Log.i(LOG_TAG, "TripProcessor: IsConstructed");
    }
    this.context = context;
    final Bundle settings = configureSettingsBundle(fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings);
    setupStartingConditions(settings);
    setupLocationService();
    setupTripData();
  }

  private Bundle configureSettingsBundle(final float fuelConsFromSettings, final float fuelPriceFromSettings, final int fuelCapacityFromSettings) {
    final Bundle settings = new Bundle();
    settings.putFloat(context.getString(R.string.TRIP_PROCESSOR_SETTING_FUEL_CONSUMTPION_KEY), fuelConsFromSettings);
    settings.putFloat(context.getString(R.string.TRIP_PROCESSOR_SETTING_FUEL_PRICE_KEY), fuelPriceFromSettings);
    settings.putInt(context.getString(R.string.TRIP_PROCESSOR_SETTING_FUEL_CAPACITY_KEY), fuelCapacityFromSettings);
    return settings;
  }

  private TripProcessor(@NonNull final Parcel in) {
    context = setupTripProcessorFromParcel(in);
    setupSubscribers();
  }

  private Context setupTripProcessorFromParcel(@NonNull final Parcel in) {
    final Context context;
    fileIsInReadMode = in.readByte() != 0;
    tripStartTime = in.readLong();
    currentTripId = in.readInt();
    serviceIntent = in.readParcelable(Intent.class.getClassLoader());
    averageSpeed = in.readFloat();
    serviceBound = in.readByte() != 0;
    maxSpeedVal = in.readFloat();
    tripData = in.readParcelable(TripData.class.getClassLoader());
    routes = in.createTypedArrayList(Route.CREATOR);
    fuelConsFromSettings = in.readFloat();
    fuelCapacity = in.readInt();
    fuelPrice = in.readFloat();
    context = TripHelperApp.getAppContext();
    return context;
  }

  public static final Creator<TripProcessor> CREATOR = new Creator<TripProcessor>() {
    @Override public TripProcessor createFromParcel(@NonNull final Parcel in) {
      return new TripProcessor(in);
    }

    @Override public TripProcessor[] newArray(final int size) {
      return new TripProcessor[size];
    }
  };

  public ArrayList<Float> getAvgSpeedArrayList() {
    return avgSpeedArrayList;
  }

  public boolean isFileNotInWriteMode() {
    return !fileIsInReadMode;
  }

  public String getFuelLeftString(@NonNull final String distancePrefix) {
    final float fuelLeftVal = getFuelLeft();
    if (DEBUG) {
      Log.d(LOG_TAG, "getFuelLeftString: fuel written" + fuelLeftVal);
    }
    final float distanceToDriveLeft = getLeftDistanceToDrive(fuelLeftVal);
    return (UtilMethods.formatFloatDecimalFormat(fuelLeftVal) + " (~" + UtilMethods
            .formatFloatDecimalFormat(distanceToDriveLeft) + distancePrefix + ")");
  }

  public String getFuelLevel(final float fuel, @NonNull final String prefix) {
    fillGasTank(fuel);
    return getFuelLeftString(prefix);
  }

  public GpsHandler getGpsHandler() {
    return gpsHandler;
  }

  public ArrayList<Route> getRoutes() {
    return routes;
  }

  public TripData getTripData() {
    return tripData;
  }


  public void setFuelConsFromSettings(final float fuelConsFromSettings) {
    this.fuelConsFromSettings = fuelConsFromSettings;
  }

  public void setFuelCapacity(final int fuelCapacity) {
    this.fuelCapacity = fuelCapacity;
  }

  public void setFuelPrice(final float fuelPrice) {
    this.fuelPrice = fuelPrice;
  }

  public void setSpeedChangeListener(@NonNull final SpeedChangeListener speedChangeListener) {
    this.speedChangeListener = speedChangeListener;
  }

  public void stopTracking() {
    final ArrayList<Float> avgArrayList = getAvgSpeedArrayList();
    if (DEBUG) {
      Log.d(LOG_TAG, "stopTracking: +" + avgArrayList.size());
      for (float f : avgArrayList) {
        Log.d(LOG_TAG, "stopTracking: " + f);
      }
    }

    updateAvgAndMaxSpeedInTrip(avgArrayList);
    endTrip();
    setMetricFieldsToTripData(fuelPrice);
    writeDataToFile();
    currentTripId = ConstantValues.START_VALUE;
    avgArrayList.clear();
  }

  public void startNewTrip() {
    avgSpeedArrayList = getEmptyArrayList_float();
    routes = getEmptyArrayList_route();

    final Date date = getDateInstance();
    final String formattedDateToDisplay = getDateToDisplay(date);
    tripStartTime = getTimeOfStart(date);

    currentTripId = getTripId();
    final Trip trip = getNewTrip(formattedDateToDisplay);
    setRoutesToTrip(trip);
    addTripToOverallTripsData(trip);
  }

  private void addTripToOverallTripsData(@NonNull final Trip trip) {
    tripData.addTrip(trip);
  }

  private void setRoutesToTrip(@NonNull final Trip trip) {
    trip.setRoute(routes);
  }

  @NonNull private Trip getNewTrip(@NonNull final String formattedDateToDisplay) {
    return new Trip(currentTripId, formattedDateToDisplay);
  }

  private String getDateToDisplay(@NonNull final Date date) {
    return UtilMethods.parseDate(date);
  }

  private int getTripId() {
    return tripData.getTrips().size();
  }

  private Date getDateInstance() {
    final Calendar currentCalendarInstance = Calendar.getInstance();
    return currentCalendarInstance.getTime();
  }

  private long getTimeOfStart(@NonNull final Date date) {
    return date.getTime();
  }

  @NonNull private ArrayList<Route> getEmptyArrayList_route() {
    return new ArrayList<>();
  }

  @NonNull private ArrayList<Float> getEmptyArrayList_float() {
    return new ArrayList<>();
  }

  public void eraseTripData() {
    tripData = new TripData();
  }

  public void restoreAvgList(@Nullable final ArrayList<String> restoredAvgSpdList) {
    if (restoredAvgSpdList != null && restoredAvgSpdList.size() == 0) {
      final ArrayList<Float> avgSpeedArrayList = getEmptyArrayList_float();
      for (String restoredAvgSpdValue : restoredAvgSpdList) {
        avgSpeedArrayList.add(Float.valueOf(restoredAvgSpdValue));
      }
      setAvgSpeedArrayList(avgSpeedArrayList);
    }
  }

  public void performExit() {
    unsubscribeRx();
    unregisterServiceConnection();
    killLocationService();
    killGpsHandler();
    killSpeedChangeListener();
  }

  private void killSpeedChangeListener() {
    setSpeedChangeListener(null);
  }

  private void killGpsHandler() {
    if (gpsHandler != null) {
      gpsHandler.performExit();
      gpsHandler = null;
    }
  }

  private void killLocationService() {
    if (locationService != null) {
      locationService.onDestroy();
    }
  }

  private void unregisterServiceConnection() {
    if (serviceConnection != null) {
      unregisterService();
    }
  }

  private void unsubscribeRx() {
    maxSpeedSubscriber.unsubscribe();
    locationSubscriber.unsubscribe();
    speedSubscriber.unsubscribe();
  }


  private TripData createTripData(@NonNull final ArrayList<Trip> trips, final float avgFuelConsumption, final float fuelSpent, final float distanceTravelled,
                                  final float moneyOnFuelSpent, final float avgSpeed, final float timeSpent, final float gasTank, final float maxSpeed) {
    final TripData tripData = new TripData();
    tripData.setTrips(trips);
    tripData.setDistanceTravelled(distanceTravelled);
    tripData.setFuelSpent(fuelSpent);
    tripData.setMoneyOnFuelSpent(moneyOnFuelSpent);
    tripData.setAvgFuelConsumption(avgFuelConsumption);
    tripData.setAvgSpeed(avgSpeed);
    tripData.setTimeSpentOnTrips(timeSpent);
    tripData.setGasTank(gasTank);
    tripData.setMaxSpeed(maxSpeed);
    return tripData;
  }

  private Trip getCurrentTrip() {
    return getCurrentTripFromTripData();
  }

  private float getLeftDistanceToDrive(final float fuelLeftVal) {
    return getDistanceLeftToDrive(fuelLeftVal);
  }

  private float getDistanceLeftToDrive(final float fuelLeftVal) {
    if (getTripData() != null) {
      return getDistLeftFromAvgSpeed(fuelLeftVal);
    } else {
      return 0f;
    }
  }

  private float getDistLeftFromAvgSpeed(final float fuelLeftVal) {
    return calculateDistanceLeftToDrive(fuelLeftVal);
  }

  private float getAverageSpeedFromOverallTrips() {
    float avgSpeed = getTripData().getAvgSpeed();
    if (avgSpeed == 0) {
      avgSpeed = ConstantValues.MEDIUM_TRAFFIC_AVG_SPEED;
    }
    return avgSpeed;
  }

  private float calculateDistanceLeftToDrive(final float fuelLeftVal) {
    final float avgSpeed = getAverageSpeedFromOverallTrips();
    if (DEBUG) {
      Log.d(LOG_TAG, "getLeftDistanceToDrive: " + fuelConsFromSettings + "avgSped" + avgSpeed);
    }
    final float fuelConsLevel = getFuelConsumptionLevelFromAvgSpeed(avgSpeed);
    return (fuelLeftVal / fuelConsLevel) * ConstantValues.PER_100;
  }

  private float getFuelConsumptionLevelFromAvgSpeed(final float avgSpeed) {
    return UtilMethods.getFuelConsumptionLevel(avgSpeed, fuelConsFromSettings);
  }

  private float getFuelLeft() {
    if (tripData != null) {
      return GasTankFromData();
    } else {
      return ConstantValues.START_VALUE; // empty fuel tank
    }
  }

  private float GasTankFromData() {
    if (DEBUG) {
      Log.d(LOG_TAG, "getFuelLeft: " + tripData.getGasTank());
    }
    return tripData.getGasTank();
  }

  private TripData readDataFromFile() {
    final ReadFileTask readFileTask = new ReadFileTask();
    try {
      tripData = readFileTask.execute(ConstantValues.FILE_NAME).get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return tripData;
  }

  private Trip readTrip(@NonNull final ObjectInputStream is) {
    Trip trip = new Trip();
    trip = trip.readTrip(is);
    return trip;
  }


  private void setupLocationService() {
    serviceIntent = new Intent(context, LocationService.class);
    if (serviceConnection == null && !serviceBound) {
      setupAndStartLocationService();
    } else {
      if (DEBUG) {
        Log.i(LOG_TAG, "onServiceConnected: service already exist");
      }
      configureGpsHandler();
    }
    bindService();
  }

  private void setupAndStartLocationService() {
    setupServiceConnection();
    startService();
  }

  private void startService() {
    context.getApplicationContext().startService(serviceIntent);
  }

  private void bindService() {
    context.getApplicationContext().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
  }

  private void setAvgSpeedArrayList(@NonNull final ArrayList<Float> avgSpeedArrayList) {
    this.avgSpeedArrayList = avgSpeedArrayList;
  }

  private void setupServiceConnection() {
    serviceConnection = new ServiceConnection() {
      @Override public void onServiceConnected(@NonNull final ComponentName className, @NonNull final IBinder service) {
        serviceBound = true;
        final LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
        locationService = binder.getService();
        configureGpsHandler();
        if (DEBUG) {
          Log.i(LOG_TAG, "onServiceConnected: bounded");
        }
      }

      @Override public void onServiceDisconnected(@NonNull final ComponentName arg0) {
        serviceBound = false;
        if (DEBUG) {
          Log.i(LOG_TAG, "onServiceConnected: unbounded");
        }
      }
    };
  }

  private void unregisterService() {
    if (serviceIntent == null) {
      serviceIntent = new Intent(context, LocationService.class);
    }
    context.getApplicationContext().stopService(serviceIntent);
    if (serviceConnection != null && serviceBound) {
      serviceBound = false;
      unbindService();
    }
  }

  private void unbindService() {
    context.getApplicationContext().unbindService(serviceConnection);
  }

  private void setupSubscribers() {
    setupLocationSubscriber();
    setupMaxSpeedSubscriber();
    setupSpeedSubscriber();
  }

  private void configureGpsHandler() {
    setupSubscribers();
    setSubscribersToGpsHandler();
  }

  private void setupTripData() {
    if (tripData == null) {
      if (DEBUG) {
        Log.i(LOG_TAG, "TripProcessor: Reading data from file...");
      }
      tripData = readDataFromFile();
    }
  }

  private void setupStartingConditions(@NonNull final Bundle settings) {
    this.fuelConsFromSettings = settings.getFloat(context.getString(R.string.TRIP_PROCESSOR_SETTING_FUEL_CONSUMTPION_KEY));
    this.fuelCapacity = settings.getInt(context.getString(R.string.TRIP_PROCESSOR_SETTING_FUEL_CAPACITY_KEY));
    this.fuelPrice = settings.getFloat(context.getString(R.string.TRIP_PROCESSOR_SETTING_FUEL_PRICE_KEY));
    routes = getEmptyArrayList_route();
    fileIsInReadMode = false;
    fileIsInWriteMode = false;
  }

  private void setSubscribersToGpsHandler() {
    gpsHandler = locationService.getGpsHandler();
    gpsHandler.setSpeedSubscriber(speedSubscriber);
    gpsHandler.setLocationSubscriber(locationSubscriber);
    gpsHandler.setMaxSpeedSubscriber(maxSpeedSubscriber);
  }

  private void setupLocationSubscriber() {
    if (locationSubscriber == null) {
      locationSubscriber = new Subscriber<Location>() {
        @Override public void onCompleted() {
        }

        @Override public void onError(@NonNull final Throwable e) {
        }

        @Override public void onNext(@NonNull final Location location) {
          addPointToRouteList(location);
        }
      };
    }
  }

  private void setupMaxSpeedSubscriber() {
    if (maxSpeedSubscriber == null) {
      maxSpeedSubscriber = new Subscriber<Float>() {
        @Override public void onCompleted() {

        }

        @Override public void onError(@NonNull final Throwable e) {

        }

        @Override public void onNext(@NonNull final Float speed) {
          setMaxSpeed(speed);
        }
      };
    }
  }

  private void setMaxSpeed(@NonNull final Float speed) {
    maxSpeedVal = speed;
  }

  private void setupSpeedSubscriber() {
    if (speedSubscriber == null) {
      speedSubscriber = new Subscriber<Float>() {
        @Override public void onCompleted() {
        }

        @Override public void onError(@NonNull final Throwable e) {
          if (DEBUG) {
            Log.e(LOG_TAG, "addPointToRouteList: speed in frag - ERROR" + e.toString());
          }
        }

        @Override public void onNext(@NonNull final Float speed) {
          addSpeedTick(speed);
          speedChangeListener.speedChanged(speed);
        }
      };
    }
  }

  private void writeTrip(@NonNull final Trip trip, @NonNull final ObjectOutputStream os) {
    trip.writeTrip(os);
  }

  private void setMetricFieldsToTripData(final float fuelPriceFromSettings) {
    final float startVal = ConstantValues.START_VALUE;
    final TripData tripData = getTripData();
    Float fuelSpent = startVal;
    Float timeSpentForAllTrips = startVal;
    Float avgSpeedSum = startVal;
    Float avgFuelCons = startVal;
    Float maxSpeed = startVal;
    final Float distTravelled;
    final ArrayList<Trip> allTrips = tripData.getTrips();

    for (Trip trip : allTrips) {
      timeSpentForAllTrips = timeSpentForAllTrips + trip.getTimeSpentForTrip();
      maxSpeed = CalculationUtils.findMaxSpeed(trip.getMaxSpeed(), maxSpeed);
      fuelSpent = fuelSpent + trip.getFuelSpent();
    }
    for (Trip trip : allTrips) {
      final float majority_multiplier = (trip.getTimeSpentForTrip() / timeSpentForAllTrips) * ConstantValues.PER_100;
      avgFuelCons = (avgFuelCons + ((trip.getAvgFuelConsumption() * majority_multiplier) / ConstantValues.PER_100));
      avgSpeedSum = (avgSpeedSum + ((trip.getAvgSpeed() * majority_multiplier) / ConstantValues.PER_100));
    }
    distTravelled = CalculationUtils.calcDistTravelled(timeSpentForAllTrips, avgSpeedSum);
    //double assurance that NULL won't be passed to trip data. Method "getValueCheckedOnNAN" replaces NULL&NAN values by 0f;
    tripData.setMaxSpeed(getValueCheckedOnNAN(maxSpeed));
    tripData.setAvgSpeed(getValueCheckedOnNAN(avgSpeedSum));
    tripData.setTimeSpentOnTrips(getValueCheckedOnNAN(timeSpentForAllTrips));
    tripData.setDistanceTravelled(getValueCheckedOnNAN(distTravelled));
    tripData.setAvgFuelConsumption(getValueCheckedOnNAN(avgFuelCons));
    tripData.setFuelSpent(getValueCheckedOnNAN(fuelSpent));
    tripData.setGasTank(getValueCheckedOnNAN(tripData.getGasTank() - getCurrentTrip().getFuelSpent()));
    tripData.setMoneyOnFuelSpent(getValueCheckedOnNAN(fuelSpent * fuelPriceFromSettings));
  }

  private Float getValueCheckedOnNAN(@Nullable final Float value) {
    if (value == null || value.isNaN()) {
      return 0f;
    }
    return value;
  }

  private void addRoutePoint(@NonNull final LatLng routePoints, final float speed) {
    final Route routePoint = new Route(routePoints, speed);
    addRoutePoint(routePoint);
  }

  private void updateAvgAndMaxSpeedInTrip(@NonNull final ArrayList<Float> avgArrayList) {
    final float averageSpeed = CalculationUtils.calcAvgSpeedForOneTrip(avgArrayList);
    final float maximumSpeed = maxSpeedVal;
    final Trip trip = getCurrentTripFromTripData();
    trip.setAvgSpeed(averageSpeed);
    trip.setMaxSpeed(maximumSpeed);
  }

  private Trip getCurrentTripFromTripData() {
    return tripData.getTrip(currentTripId);
  }

  private void addPointToRouteList(@NonNull final Location location) {
    final LatLng routePoint = getRoutePointFromLocation(location);
    final float speed = getSpeedFromLocation(location);
    addRoutePoint(routePoint, speed);
  }

  private float getSpeedFromLocation(@NonNull final Location location) {
    return CalculationUtils.getSpeedInKilometerPerHour(location.getSpeed());
  }

  @NonNull private LatLng getRoutePointFromLocation(@NonNull final Location location) {
    return new LatLng(location.getLatitude(), location.getLongitude());
  }

  private void addSpeedTick(final float speed) {
    if (avgSpeedArrayList != null) {
      avgSpeedArrayList.add(speed);
    }
  }

  private void addRoutePoint(@NonNull final Route routePoint) {
    routes.add(routePoint);
  }

  private void setTripFieldsToStartState() {
    tripStartTime = ConstantValues.START_VALUE;
    averageSpeed = ConstantValues.START_VALUE;
    maxSpeedVal = ConstantValues.START_VALUE;
  }

  private void getTripDataFieldsValues() {
    float distanceTravelled = ConstantValues.START_VALUE;
    float fuelSpent = ConstantValues.START_VALUE;
    final float avgFuelConsumption;
    for (Trip trip : tripData.getTrips()) {
      distanceTravelled = distanceTravelled + trip.getDistanceTravelled();
      fuelSpent = fuelSpent + trip.getFuelSpent();
    }
    final float moneySpent = CalculationUtils.calcMoneySpent(fuelSpent, fuelPrice);
    avgFuelConsumption = tripData.getAvgFuelConsumption();
    tripData.setDistanceTravelled(distanceTravelled);
    tripData.setAvgFuelConsumption(avgFuelConsumption);
    tripData.setMoneyOnFuelSpent(moneySpent);
    tripData.setFuelSpent(fuelSpent);
  }

  private void fillGasTank(final float fuel) {
    if (tripData != null) {
      float gasTank = tripData.getGasTank();
      if (DEBUG) {
        Log.d(LOG_TAG, "fillGasTank: gasTank" + gasTank + ",fuel" + fuel + ",fuelCap" + fuelCapacity);
      }
      if (gasTank + fuel <= fuelCapacity) {
        tripData.setGasTank(gasTank + fuel);
        final CharSequence resCharSequence = context.getString(R.string.fuel_spent_toast) + fuel + context.getResources()
                .getString(R.string.fuel_prefix);
        UtilMethods.showToast(context, resCharSequence);
      } else {
        UtilMethods.showToast(context, context.getString(R.string.fuel_overload_toast));
      }
      writeDataToFile();
    }
  }

  private void updateTrip(@NonNull final Trip trip) {
    if (DEBUG) {
      Log.d(LOG_TAG, "updateTrip: called");
    }
    trip.setAvgSpeed(averageSpeed);
    if (routes.size() == 0 || routes == null) {
      final Route tmpRoutePoint = new Route(ConstantValues.BERMUDA_COORDINATES, ConstantValues.SPEED_VALUE_WORKAROUND);
      routes.add(tmpRoutePoint);
    }
    setRoutesToTrip(trip);
  }

  private void updateTripState() {
    final Trip trip = getCurrentTripFromTripData();
    tripData.updateTrip(trip);
  }

  private void writeDataToFile() {
    if (!fileIsInWriteMode) {
      final WriteFileTask writeFileTask = new WriteFileTask();
      writeFileTask.execute(tripData);
    }
  }

  private void endTrip() {
    if (DEBUG) {
      Log.d(LOG_TAG, "endTrip: end trip called");
    }
    final Trip trip = getCurrentTrip();
    updateTrip(trip);
    final long timeSpent = CalculationUtils.calcTimeInTrip(tripStartTime);
    final float distanceTraveled = CalculationUtils.setDistanceCoveredForTrip(trip, timeSpent);
    final float fuelConsumption = getFuelConsumptionLevelFromAvgSpeed(averageSpeed);
    final float fuelSpent = CalculationUtils.calcFuelSpent(distanceTraveled, fuelConsumption);
    final float moneySpent = CalculationUtils.calcMoneySpent(fuelSpent, fuelPrice);

    trip.setMoneyOnFuelSpent(moneySpent);
    trip.setFuelSpent(fuelSpent);
    trip.setDistanceTravelled(distanceTraveled);
    trip.setTimeSpent(timeSpent);
    trip.setAvgFuelConsumption(fuelConsumption);
    updateTripState();
    setTripFieldsToStartState();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(@NonNull final Parcel parcel, final int i) {
    parcel.writeByte((byte) (fileIsInReadMode ? 1 : 0));
    parcel.writeLong(tripStartTime);
    parcel.writeInt(currentTripId);
    parcel.writeParcelable(serviceIntent, i);
    parcel.writeFloat(averageSpeed);
    parcel.writeByte((byte) (serviceBound ? 1 : 0));
    parcel.writeFloat(maxSpeedVal);
    parcel.writeParcelable(tripData, i);
    parcel.writeTypedList(routes);
    parcel.writeFloat(fuelConsFromSettings);
    parcel.writeInt(fuelCapacity);
    parcel.writeFloat(fuelPrice);
  }

  private class WriteFileTask extends AsyncTask<TripData, Void, Boolean> {
    @Override protected void onPreExecute() {
      super.onPreExecute();
      fileIsInWriteMode = true;
    }

    @Override protected Boolean doInBackground(@Nullable final TripData... params) {
      if (DEBUG) {
        Log.d(LOG_TAG, "writeTripDataToFile: writeCalled");
        Log.d(LOG_TAG, "writeTripDataToFile: avgSpeed - " + tripData.getAvgSpeed());
      }
      final FileOutputStream fos;
      final ArrayList<Trip> trips = tripData.getTrips();
      getTripDataFieldsValues();
      if (DEBUG) {
        Log.d(LOG_TAG, "WRITE: distTravelledForTripData " + tripData.getDistanceTravelled());
        Log.d(LOG_TAG, "WRITE: avgConsForTripData " + tripData.getAvgFuelConsumption());
        Log.d(LOG_TAG, "WRITE: fuelSpentForTripData " + tripData.getFuelSpent());
        Log.d(LOG_TAG, "WRITE: moneyOnFuelForTripData " + tripData.getMoneyOnFuelSpent());
        Log.d(LOG_TAG, "WRITE: avgSpeedForTripData " + tripData.getAvgSpeed());
      }
      final int tripsSize = trips.size();
      final Float distanceTravelled = tripData.getDistanceTravelled();
      final Float avgFuelConsumption = tripData.getAvgFuelConsumption();
      final Float fuelSpent = tripData.getFuelSpent();
      final Float moneyOnFuelSpent = tripData.getMoneyOnFuelSpent();
      final Float avgSpeed = tripData.getAvgSpeed();
      final Float timeSpent = tripData.getTimeSpentOnTrips();
      final Float gasTankCapacity = tripData.getGasTank();
      final Float maxSpeed = tripData.getMaxSpeed();

      try {
        fos = context.openFileOutput(ConstantValues.FILE_NAME, Context.MODE_PRIVATE);
        final ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeInt(tripsSize);
        for (Trip trip : trips) {
          if (DEBUG) {
            Log.d(LOG_TAG, "writeTripDataToFile: trip to string - " + trip.toString());
          }
          writeTrip(trip, os);
        }
        writeToStreamWithNANcheck(avgFuelConsumption, os);
        writeToStreamWithNANcheck(fuelSpent, os);
        writeToStreamWithNANcheck(distanceTravelled, os);
        writeToStreamWithNANcheck(moneyOnFuelSpent, os);
        writeToStreamWithNANcheck(avgSpeed, os);
        writeToStreamWithNANcheck(timeSpent, os);
        writeToStreamWithNANcheck(gasTankCapacity, os);
        writeToStreamWithNANcheck(maxSpeed, os);
        os.close();
        fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (DEBUG) {
        Log.d(LOG_TAG, "writeTripDataToFile: " + tripData.getTrips().toString());
      }
      return true;
    }


    private void writeToStreamWithNANcheck(@Nullable Float valueToWrite, @NonNull final ObjectOutputStream os) throws IOException {
      if (valueToWrite == null || valueToWrite.isNaN()) {
        valueToWrite = 0f;
      }
      os.writeFloat(valueToWrite);
    }

    @Override protected void onPostExecute(@NonNull final Boolean result) {
      if (result) {
        if (DEBUG) {
          Log.d(LOG_TAG, "file written successfully");
        }
        fileIsInWriteMode = false;
      }
    }
  }

  private class ReadFileTask extends AsyncTask<String, Integer, TripData> {
    @Override protected TripData doInBackground(@Nullable final String... params) {
      if (DEBUG) {
        Log.d(LOG_TAG, "readFile");
      }
      fileIsInReadMode = true;
      final File file = context.getFileStreamPath(ConstantValues.FILE_NAME);
      if (file.exists()) {
        if (DEBUG) {
          Log.d(LOG_TAG, "readTripDataFromFile: ");
        }
        final ArrayList<Trip> trips = new ArrayList<>();
        final FileInputStream fis;
        final int tripsSize;
        try {
          fis = context.openFileInput(ConstantValues.FILE_NAME);
          final ObjectInputStream is = new ObjectInputStream(fis);
          tripsSize = is.readInt();
          for (int i = 0; i < tripsSize; i++) {
            Trip trip = readTrip(is);
            trips.add(trip);
          }
          final float avgFuelConsumption = is.readFloat();
          final float fuelSpent = is.readFloat();
          final float distanceTravelled = is.readFloat();
          final float moneyOnFuelSpent = is.readFloat();
          final float avgSpeed = is.readFloat();
          final float timeSpent = is.readFloat();
          final float gasTankCapacity = is.readFloat();
          final float maxSpeed = is.readFloat();

          tripData = createTripData(trips, avgFuelConsumption, fuelSpent, distanceTravelled, moneyOnFuelSpent, avgSpeed,
                  timeSpent, gasTankCapacity, maxSpeed);
          if (DEBUG) {
            Log.d(LOG_TAG, "READ: avgFuelConsForTrip " + avgFuelConsumption);
            Log.d(LOG_TAG, "READ: fuelSpentForTrip " + fuelSpent);
            Log.d(LOG_TAG, "READ: distTravelledForTrip " + distanceTravelled);
            Log.d(LOG_TAG, "READ: moneyOnFuelForTrip " + moneyOnFuelSpent);
            Log.d(LOG_TAG, "READ: avgSpeedForTrip " + avgSpeed);
            Log.d(LOG_TAG, "READ: timeSpentForTrip " + timeSpent);
          }
          is.close();
          fis.close();
        } catch (IOException e) {
          e.printStackTrace();
          tripData = new TripData();
        }
      } else {
        if (DEBUG) {
          Log.d(LOG_TAG, "TripProcessor: file is empty");
        }
        tripData = new TripData();
      }
      return tripData;
    }

    @Override protected void onPostExecute(@Nullable final TripData tripData) {
      super.onPostExecute(tripData);
      if (tripData != null) {
        fileIsInReadMode = false;
      }
    }
  }
}