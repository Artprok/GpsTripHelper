package com.example.aprokopenko.triphelper.ui.map;

import android.location.Location;
import android.os.Bundle;

import com.example.aprokopenko.triphelper.datamodel.LocationEmittableItem;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

public interface MapContract {
    interface View {
        void setMapPresenter(UserActionListener userActionListener);

        void onNewLocation(LocationEmittableItem locationEmittableItem, float speed);
    }

    interface UserActionListener {

        void onCreate();

        void onDestroy();

        void onDetach();

        Bundle onSaveInstaceState();

        void onRestoreInstanceState(Bundle bundle);

        void onResume();

        void onRoutesSet(ArrayList<Route> routes);

        void onGpsHandlerSet(GpsHandler gpsHandler);

        void locationTracking(GoogleMap googleMap, Location location, float speed);
    }
}