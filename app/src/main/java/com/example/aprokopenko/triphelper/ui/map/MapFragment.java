package com.example.aprokopenko.triphelper.ui.map;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.datamodel.LocationEmittableItem;
import com.example.aprokopenko.triphelper.datamodel.Route;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;

import java.util.ArrayList;

import javax.inject.Singleton;

/**
 * {@link Fragment} responsible for showing map with driven routes.
 */
@Singleton
public class MapFragment extends Fragment implements OnMapReadyCallback, MapContract.View {
    private static final String LOG_TAG = "MAP_FRAGMENT";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private GoogleMap googleMap;
    private MapContract.UserActionListener userActionListener;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public void setupUserActionListener(@NonNull final MapContract.View view) {
        if (userActionListener == null) {
            userActionListener = new MapPresenter(view);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserActionListener(this);
        userActionListener.onCreate();
    }

    @Override
    public android.view.View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final android.view.View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getGoogleMap(this);
    }

    @Override
    public void onMapReady(@NonNull final GoogleMap googleMap) {
        final UiSettings uiSettings = googleMap.getUiSettings();

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        UtilMethods.isPermissionAllowed(getActivity());
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        if (UtilMethods.isPermissionAllowed(getActivity())) {
            googleMap.setMyLocationEnabled(true);
        }
        this.googleMap = googleMap;
    }

    @Override
    public void onDestroyView() {
        final Fragment fragment = getChildFragmentManager().findFragmentById(R.id.mapFragment);

        userActionListener.onDestroy();
        if (fragment != null) {
            getChildFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        userActionListener.onDetach();
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(userActionListener.onSaveInstaceState());
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            userActionListener.onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        userActionListener.onResume();
        super.onResume();
    }

    public void setRoutes(@NonNull final ArrayList<Route> routes) {
        userActionListener.onRoutesSet(routes);
    }

    @Override
    public void onNewLocation(final LocationEmittableItem locationEmittableItem, final float speed) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    userActionListener.locationTracking(googleMap, locationEmittableItem.getLocation(), speed);
                }
            });
        }
    }

    public void setMapPresenter(MapContract.UserActionListener userActionListener) {
        this.userActionListener = userActionListener;
    }

    public void setGpsHandler(@NonNull final GpsHandler gpsHandler) {
        userActionListener.onGpsHandlerSet(gpsHandler);
    }

    private static void getGoogleMap(@NonNull final MapFragment fragment) {
        final SupportMapFragment mapFragment = (SupportMapFragment) fragment.getChildFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(fragment);
    }
}