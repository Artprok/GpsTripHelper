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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import rx.Subscriber;

/**
 * Class in which all logic happens.
 */
public class TripProcessor implements Parcelable {
  @Inject Context context;

  private static final String LOG_TAG = "TripProcessor";
  private static final boolean DEBUG = BuildConfig.DEBUG;

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

  /**
   * Default constructor with some settings.
   *
   * @param context                  {@link Context}
   * @param fuelConsFromSettings     {@link Float} fuel consumption got from settings
   * @param fuelPriceFromSettings    {@link Float} fuel price got from settings
   * @param fuelCapacityFromSettings {@link Integer} capacity of fuel tank got from settings
   */
  public TripProcessor(@NonNull final Context context, final float fuelConsFromSettings, final float fuelPriceFromSettings, final int fuelCapacityFromSettings) {
    TripHelperApp.getApplicationComponent().injectInto(this);
    this.context = context;
    final Bundle settings = configureSettingsBundle(fuelConsFromSettings, fuelPriceFromSettings, fuelCapacityFromSettings, context);
    setupStartingConditions(settings);
    setupLocationService();
    setupTripData();

    if (DEBUG) {
      Log.i(LOG_TAG, "TripProcessor: IsConstructed");
    }
  }

  public static final Creator<TripProcessor> CREATOR = new Creator<TripProcessor>() {
    @Override public TripProcessor createFromParcel(@NonNull final Parcel in) {
      return new TripProcessor(in);
    }

    @Override public TripProcessor[] newArray(final int size) {
      return new TripProcessor[size];
    }
  };

  public ArrayList<Float> getAvgSpeedList() {
    return avgSpeedArrayList;
  }

  /**
   * Method for get information if File is in write mode now.
   *
   * @return {@link Boolean} true - file file free to use, false - file is busy (in write mode)
   */
  public boolean isFileNotInWriteMode() {
    return !fileIsInReadMode;
  }

  /**
   * Method for concat value and fuel prefix.
   *
   * @param distancePrefix {@link String} appropriate fuel prefix
   * @return {@link String} value with prefix
   */
  public String getFuelLeftString(@NonNull final String distancePrefix) {
    final float fuelLeftVal = getFuelLeft(tripData);

    if (DEBUG) {
      Log.d(LOG_TAG, "getFuelLeftString: fuel written" + fuelLeftVal);
    }
    return (UtilMethods.formatFloatDecimalFormat(fuelLeftVal) + " (~" + UtilMethods
            .formatFloatDecimalFormat(getLeftDistanceToDrive(fuelLeftVal, getTripData(), fuelConsFromSettings)) + distancePrefix + ")");
  }

  /**
   * Method for "fill" gas tank and that return its "new" fuel level.
   *
   * @param fuel   {@link Float} amount to fill
   * @param prefix {@link String} appropriate prefix (litres)
   * @return {@link} fuel level
   */
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

  public void setSpeedChangeListener(@Nullable final SpeedChangeListener speedChangeListener) {
    this.speedChangeListener = speedChangeListener;
  }

  /**
   * Method for stop {@link Trip}, store all data and write it to internal storage.
   */
  public void stopTracking() {
    final ArrayList<Float> avgArrayList = getAvgSpeedList();

    if (DEBUG) {
      Log.d(LOG_TAG, "stopTracking: +" + avgArrayList.size());
      for (final float f : avgArrayList) {
        Log.d(LOG_TAG, "stopTracking: " + f);
      }
    }

    updateAvgAndMaxSpeedInTrip(avgArrayList);
    endTrip();
    setMetricFieldsToTripData(fuelPrice, getTripData(), getCurrentTrip());
    writeDataToFile();
    currentTripId = ConstantValues.START_VALUE;
    avgArrayList.clear();
  }

  /**
   * Method to start new {@link Trip}, add it to overall trip data.
   */
  public void startNewTrip() {
    final Date date = getDateInstance();
    final Trip trip = getNewTrip(date);
    avgSpeedArrayList = getEmptyArrayList_float();
    routes = getEmptyArrayList_route();
    tripStartTime = getTimeOfStart(date);
    setRoutesToTrip(trip);
    addTripToOverallTripsData(trip);
  }

  /**
   * Method for erasing overall tripData.
   */
  public void eraseTripData() {
    tripData = new TripData();
  }

  /**
   * Method for restoring average speed list from {@link ArrayList<String>}.
   *
   * @param restoredAvgSpdList {@link ArrayList<String>} restored average speed list populated with {@link String}
   */
  public void restoreAvgList(@Nullable final ArrayList<String> restoredAvgSpdList) {
    if (restoredAvgSpdList != null && restoredAvgSpdList.size() == 0) {
      final ArrayList<Float> avgSpeedArrayList = getEmptyArrayList_float();
      for (final String restoredAvgSpdValue : restoredAvgSpdList) {
        avgSpeedArrayList.add(Float.valueOf(restoredAvgSpdValue));
      }
      setAvgSpeedArrayList(avgSpeedArrayList);
    }
  }

  /**
   * Method for safe exit from application. Ending all process, etc.
   */
  public void performExit() {
    unsubscribeRx();
    unregisterServiceConnection();
    killLocationService();
    killGpsHandler();
    killSpeedChangeListener();
  }

  private static Bundle configureSettingsBundle(final float fuelConsFromSettings, final float fuelPriceFromSettings, final int fuelCapacityFromSettings, @NonNull final Context context) {
    final Bundle settings = new Bundle();

    settings.putFloat(context.getString(R.string.TRIP_PROCESSOR_SETTING_FUEL_CONSUMPTION_KEY), fuelConsFromSettings);
    settings.putFloat(context.getString(R.string.TRIP_PROCESSOR_SETTING_FUEL_PRICE_KEY), fuelPriceFromSettings);
    settings.putInt(context.getString(R.string.TRIP_PROCESSOR_SETTING_FUEL_CAPACITY_KEY), fuelCapacityFromSettings);
    return settings;
  }

  private TripProcessor(@NonNull final Parcel in) {
    context = setupTripProcessorFromParcel(in);
    setupSubscribers();
  }

  private Context setupTripProcessorFromParcel(@NonNull final Parcel in) {
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
    return context;
  }

  private void addTripToOverallTripsData(@NonNull final Trip trip) {
    tripData.addTrip(trip);
  }

  private void setRoutesToTrip(@NonNull final Trip trip) {
    trip.setRoute(routes);
  }

  @NonNull private Trip getNewTrip(@NonNull final Date date) {
    currentTripId = getTripId();
    return new Trip(currentTripId, getDateToDisplay(date));
  }

  private static String getDateToDisplay(@NonNull final Date date) {
    return UtilMethods.parseDate(date);
  }

  private int getTripId() {
    return tripData.getTrips().size();
  }

  private static Date getDateInstance() {
    return Calendar.getInstance().getTime();
  }

  private static long getTimeOfStart(@NonNull final Date date) {
    return date.getTime();
  }

  @NonNull private static ArrayList<Route> getEmptyArrayList_route() {
    return new ArrayList<>();
  }

  @NonNull private static ArrayList<Float> getEmptyArrayList_float() {
    return new ArrayList<>();
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

  private static TripData createTripData(@NonNull final ArrayList<Trip> trips, final float avgFuelConsumption, final float fuelSpent, final float distanceTravelled,
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
    return getCurrentTripFromTripData(tripData, currentTripId);
  }

  private static float getLeftDistanceToDrive(final float fuelLeftVal, @Nullable final TripData tripData, final float fuelConsFromSettings) {
    return getDistanceLeftToDrive(fuelLeftVal, tripData, fuelConsFromSettings);
  }

  private static float getDistanceLeftToDrive(final float fuelLeftVal, @Nullable final TripData tripData, final float fuelConsFromSettings) {
    if (tripData != null) {
      return getDistLeftFromAvgSpeed(fuelLeftVal, fuelConsFromSettings, tripData);
    } else {
      return 0f;
    }
  }

  private static float getDistLeftFromAvgSpeed(final float fuelLeftVal, final float fuelConsFromSettings, @NonNull final TripData tripData) {
    return calculateDistanceLeftToDrive(fuelLeftVal, fuelConsFromSettings, tripData);
  }

  private static float getAverageSpeedFromOverallTrips(@NonNull final TripData tripData) {
    final float avgSpeed = tripData.getAvgSpeed();
    if (avgSpeed == 0) {
      return ConstantValues.MEDIUM_TRAFFIC_AVG_SPEED;
    }
    return avgSpeed;
  }

  private static float calculateDistanceLeftToDrive(final float fuelLeftVal, final float fuelConsFromSettings, @NonNull final TripData tripData) {
    if (DEBUG) {
      Log.d(LOG_TAG, "getLeftDistanceToDrive: " + fuelConsFromSettings + "avgSped" + getAverageSpeedFromOverallTrips(tripData));
    }
    return (fuelLeftVal / getFuelConsumptionLevelFromAvgSpeed(getAverageSpeedFromOverallTrips(tripData), fuelConsFromSettings)) * ConstantValues.PER_100;
  }

  private static float getFuelConsumptionLevelFromAvgSpeed(final float avgSpeed, final float fuelConsFromSettings) {
    return UtilMethods.getFuelConsumptionLevel(avgSpeed, fuelConsFromSettings);
  }

  private static float getFuelLeft(@Nullable final TripData tripData) {
    if (tripData != null) {
      return GasTankFromData(tripData);
    } else {
      return ConstantValues.START_VALUE; // empty fuel tank
    }
  }

  private static float GasTankFromData(@NonNull final TripData tripData) {
    if (DEBUG) {
      Log.d(LOG_TAG, "getFuelLeft: " + tripData.getGasTank());
    }
    return tripData.getGasTank();
  }

  private TripData readDataFromFile() {
    try {
      tripData = new ReadFileTask().execute(ConstantValues.FILE_NAME).get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return tripData;
  }

  private Trip readTrip(@NonNull final ObjectInputStream is) {
    return Trip.readTrip(is);
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
        final LocationService.LocalBinder binder = (LocationService.LocalBinder) service;

        serviceBound = true;
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
    this.fuelConsFromSettings = settings.getFloat(context.getString(R.string.TRIP_PROCESSOR_SETTING_FUEL_CONSUMPTION_KEY));
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

  private static void writeTrip(@NonNull final Trip trip, @NonNull final ObjectOutputStream os) {
    trip.writeTrip(os);
  }

  private static void setMetricFieldsToTripData(final float fuelPriceFromSettings, @NonNull final TripData tripData, @NonNull final Trip currentTrip) {
    final ArrayList<Trip> allTrips = tripData.getTrips();
    final float startVal = ConstantValues.START_VALUE;
    Float fuelSpent = startVal;
    Float timeSpentForAllTrips = startVal;
    Float avgSpeedSum = startVal;
    Float avgFuelCons = startVal;
    Float maxSpeed = startVal;

    for (final Trip trip : allTrips) {
      timeSpentForAllTrips = timeSpentForAllTrips + trip.getTimeSpentForTrip();
    }
    for (final Trip trip : allTrips) {
      final float majority_multiplier = (trip.getTimeSpentForTrip() / timeSpentForAllTrips) * ConstantValues.PER_100;
      avgFuelCons = (avgFuelCons + ((trip.getAvgFuelConsumption() * majority_multiplier) / ConstantValues.PER_100));
      avgSpeedSum = (avgSpeedSum + ((trip.getAvgSpeed() * majority_multiplier) / ConstantValues.PER_100));
      maxSpeed = CalculationUtils.findMaxSpeed(trip.getMaxSpeed(), maxSpeed);
      fuelSpent = fuelSpent + trip.getFuelSpent();
    }
    //double assurance that NULL won't be passed to trip data. Method "getValueCheckedOnNAN" replaces NULL&NAN values by 0f;
    tripData.setMaxSpeed(getValueCheckedOnNAN(maxSpeed));
    tripData.setAvgSpeed(getValueCheckedOnNAN(avgSpeedSum));
    tripData.setTimeSpentOnTrips(getValueCheckedOnNAN(timeSpentForAllTrips));
    tripData.setDistanceTravelled(getValueCheckedOnNAN(CalculationUtils.calcDistTravelled(timeSpentForAllTrips, avgSpeedSum)));
    tripData.setAvgFuelConsumption(getValueCheckedOnNAN(avgFuelCons));
    tripData.setFuelSpent(getValueCheckedOnNAN(fuelSpent));
    tripData.setGasTank(getValueCheckedOnNAN(tripData.getGasTank() - currentTrip.getFuelSpent()));
    tripData.setMoneyOnFuelSpent(getValueCheckedOnNAN(fuelSpent * fuelPriceFromSettings));
  }

  private static Float getValueCheckedOnNAN(@Nullable final Float value) {
    if (value == null || value.isNaN()) {
      return 0f;
    }
    return value;
  }

  private void addRoutePoint(@NonNull final LatLng routePoints, final float speed) {
    addRoutePoint(new Route(routePoints, speed));
  }

  private void updateAvgAndMaxSpeedInTrip(@NonNull final ArrayList<Float> avgArrayList) {
    final Trip trip = getCurrentTripFromTripData(tripData, currentTripId);

    trip.setAvgSpeed(CalculationUtils.calcAvgSpeedForOneTrip(avgArrayList));
    trip.setMaxSpeed(maxSpeedVal);
  }

  private static Trip getCurrentTripFromTripData(@NonNull final TripData tripData, final int currentTripId) {
    return tripData.getTrip(currentTripId);
  }

  private void addPointToRouteList(@NonNull final Location location) {
    addRoutePoint(getRoutePointFromLocation(location), getSpeedFromLocation(location));
  }

  private float getSpeedFromLocation(@NonNull final Location location) {
    return CalculationUtils.getSpeedInKilometerPerHour(location.getSpeed());
  }

  @NonNull private static LatLng getRoutePointFromLocation(@NonNull final Location location) {
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
    final float fuelSpent = tripData.getFuelSpent();

    tripData.setDistanceTravelled(tripData.getDistanceTravelled());
    tripData.setAvgFuelConsumption(tripData.getAvgFuelConsumption());
    tripData.setMoneyOnFuelSpent(CalculationUtils.calcMoneySpent(fuelSpent, fuelPrice));
    tripData.setFuelSpent(fuelSpent);
  }

  private void fillGasTank(final float fuel) {
    if (tripData != null) {
      final float gasTank = tripData.getGasTank();

      if (DEBUG) {
        Log.d(LOG_TAG, "fillGasTank: gasTank" + gasTank + ",fuel" + fuel + ",fuelCap" + fuelCapacity);
      }
      if (gasTank + fuel <= fuelCapacity) {
        tripData.setGasTank(gasTank + fuel);
        UtilMethods.showToast(context, context.getString(R.string.fuel_spent_toast) + fuel + context.getResources()
                .getString(R.string.fuel_prefix));
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
      routes.add(new Route(ConstantValues.BERMUDA_COORDINATES, ConstantValues.SPEED_VALUE_WORKAROUND));
    }
    setRoutesToTrip(trip);
  }

  private void updateTripState() {
    tripData.updateTrip(getCurrentTripFromTripData(tripData, currentTripId));
  }

  private void writeDataToFile() {
    if (!fileIsInWriteMode) {
      final WriteFileTask writeFileTask = new WriteFileTask();
      writeFileTask.execute(tripData);
    }
  }

  private void endTrip() {
    final Trip trip = getCurrentTrip();
    final long timeSpent = CalculationUtils.calcTimeInTrip(tripStartTime);
    final float distanceTraveled = CalculationUtils.setDistanceCoveredForTrip(trip, timeSpent);
    final float fuelConsumption = getFuelConsumptionLevelFromAvgSpeed(averageSpeed, fuelConsFromSettings);
    final float fuelSpent = CalculationUtils.calcFuelSpent(distanceTraveled, fuelConsumption);
    updateTrip(trip);

    trip.setMoneyOnFuelSpent(CalculationUtils.calcMoneySpent(fuelSpent, fuelPrice));
    trip.setFuelSpent(fuelSpent);
    trip.setDistanceTravelled(distanceTraveled);
    trip.setTimeSpent(timeSpent);
    trip.setAvgFuelConsumption(fuelConsumption);
    updateTripState();
    setTripFieldsToStartState();

    if (DEBUG) {
      Log.d(LOG_TAG, "endTrip: end trip called");
    }
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
      getTripDataFieldsValues();
      if (DEBUG) {
        Log.d(LOG_TAG, "WRITE: distTravelledForTripData " + tripData.getDistanceTravelled());
        Log.d(LOG_TAG, "WRITE: avgConsForTripData " + tripData.getAvgFuelConsumption());
        Log.d(LOG_TAG, "WRITE: fuelSpentForTripData " + tripData.getFuelSpent());
        Log.d(LOG_TAG, "WRITE: moneyOnFuelForTripData " + tripData.getMoneyOnFuelSpent());
        Log.d(LOG_TAG, "WRITE: avgSpeedForTripData " + tripData.getAvgSpeed());
      }

      try {
        final FileOutputStream fos = context.openFileOutput(ConstantValues.FILE_NAME, Context.MODE_PRIVATE);
        final ObjectOutputStream os = new ObjectOutputStream(fos);
        final ArrayList<Trip> trips = tripData.getTrips();

        os.writeInt(trips.size());
        for (final Trip trip : trips) {
          if (DEBUG) {
            Log.d(LOG_TAG, "writeTripDataToFile: trip to string - " + trip.toString());
          }
          writeTrip(trip, os);
        }
        writeToStreamWithNANcheck(tripData.getAvgFuelConsumption(), os);
        writeToStreamWithNANcheck(tripData.getFuelSpent(), os);
        writeToStreamWithNANcheck(tripData.getDistanceTravelled(), os);
        writeToStreamWithNANcheck(tripData.getMoneyOnFuelSpent(), os);
        writeToStreamWithNANcheck(tripData.getAvgSpeed(), os);
        writeToStreamWithNANcheck(tripData.getTimeSpentOnTrips(), os);
        writeToStreamWithNANcheck(tripData.getGasTank(), os);
        writeToStreamWithNANcheck(tripData.getMaxSpeed(), os);
        os.close();
        fos.close();
      } catch (@NonNull final IOException e) {
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
      if (context.getFileStreamPath(ConstantValues.FILE_NAME).exists()) {
        final ArrayList<Trip> trips = new ArrayList<>();
        if (DEBUG) {
          Log.d(LOG_TAG, "readTripDataFromFile: ");
        }
        try {
          final FileInputStream fis = context.openFileInput(ConstantValues.FILE_NAME);
          final ObjectInputStream is = new ObjectInputStream(fis);
          final int tripsSize = is.readInt();

          for (int i = 0; i < tripsSize; i++) {
            trips.add(readTrip(is));
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