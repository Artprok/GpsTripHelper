package com.example.aprokopenko.triphelper.utils.settings;

import android.graphics.Color;

/**
 * Class for storing settings for {@link com.example.aprokopenko.triphelper.ui.map.MapFragment}.
 */
public class GoogleMapsSettings {
  public static final int polylineColorOutOfCity = Color.parseColor("#FBC02D");//yellow, speed still allowed
  public static final int polylineColorCity = Color.parseColor("#4CAF50");//green, speed is allowed
  public static final int polylineColorOutOfMaxSpeedAllowed = Color.parseColor("#B71C1C");//red, out of allowed speed
  public static final int googleMapCameraBearing = 90;
  public static final int googleMapCameraZoom = 17;
  public static final int googleMapCameraTilt = 40;
  public static final int polylineWidth = 5;
}