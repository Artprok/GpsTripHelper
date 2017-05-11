package com.example.aprokopenko.triphelper.ui.main_screen;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.TripProcessor;
import com.example.aprokopenko.triphelper.speedometer_factory.CircularGaugeFactory;
import com.example.aprokopenko.triphelper.speedometer_gauge.GaugePointer;
import com.example.aprokopenko.triphelper.speedometer_gauge.TripHelperGauge;
import com.example.aprokopenko.triphelper.ui.fragment.FuelFillDialog;
import com.example.aprokopenko.triphelper.ui.fragment.MapFragment;
import com.example.aprokopenko.triphelper.ui.setting_screen.SettingsContract;
import com.example.aprokopenko.triphelper.ui.setting_screen.SettingsFragment;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import javax.inject.Singleton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.aprokopenko.triphelper.utils.settings.ConstantValues.SETTINGS_FRAGMENT_TAG;

/**
 * Main {@link Fragment} representing a {@link TripHelperGauge} and buttons for start, stop, setting, etc.
 */
@Singleton
public class MainFragment extends Fragment implements MainContract.View {
    @BindView(R.id.speedometerContainer)
    TripHelperGauge speedometerContainer;
    @BindView(R.id.text_SpeedometerView)
    TextView speedometerTextView;
    @BindView(R.id.refillButtonLayout)
    LinearLayoutCompat refillButtonLayout;
    @BindView(R.id.btn_tripList)
    ImageButton tripListButton;
    @BindView(R.id.image_statusView)
    ImageView statusImage;
    @BindView(R.id.btn_start)
    Button startButton;
    @BindView(R.id.btn_stop)
    Button stopButton;
    @BindView(R.id.fuel_left_layout)
    LinearLayoutCompat fuel_left_layout;
    @BindView(R.id.text_fuelLeftView)
    TextView fuelLeft;
    @BindView(R.id.btn_settings)
    ImageButton settingsButton;
    @BindView(R.id.advView)
    com.google.android.gms.ads.AdView advertView;
    @BindView(R.id.fragment_main_layout)
    LinearLayoutCompat fragment_main_layout;

    private static final String GRANT = "GRANT";
    private static final String MEASUREMENT_UNIT = "measurementUnit";
    private static final int LOCATION_REQUEST_CODE = 1;

    private Unbinder unbinder;
    private TripHelperGauge speedometer;
    private MainContract.UserActionListener userActionListener;
    private GaugePointer pointer;
    private MapFragment mapFragment;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        userActionListener = new MainPresenter(this, getContext());
        userActionListener.start(savedInstanceState);
        visualizeSpeedometer();

        if (BuildConfig.FLAVOR.contains(getString(R.string.paidVersion_code))) {
            advertView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        if (getFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof MainFragment) {
            userActionListener.onSave(outState);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onPause() {
        userActionListener.onPause();
        super.onPause();
    }

    public void pauseAdvert() {
        if (advertView != null) {
            advertView.pause();
        }
    }

    @Override
    public void onResume() {
        userActionListener.onResume();
        super.onResume();
    }

    @Override
    public void resumeAdvert() {
        if (advertView != null) {
            advertView.resume();
        }
    }

    @Override
    public void setSpeedometerValue(float speed) {
        if (pointer == null) {
            pointer = speedometer.getGaugeScales().get(0).getGaugePointers().get(0);
        }
        pointer.setValue(speed);
    }

    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    public void hideFab() {
        UtilMethods.hideFab(getActivity());
    }

    public void showFab() {
        UtilMethods.showFab(getActivity());
    }

    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_settings, R.id.btn_tripList, R.id.refillButtonLayout})
    public void onClickButton(@NonNull final View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                userActionListener.onStartClick();
                break;
            case R.id.btn_stop:
                userActionListener.onStopClick();
                break;
            case R.id.btn_settings:
                userActionListener.onSettingsClick();
                if (getChildFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT_TAG) != null) {
                    showSettingsFragment((SettingsFragment) getChildFragmentManager().findFragmentByTag(SETTINGS_FRAGMENT_TAG), view);
                } else {
                    showSettingsFragment(SettingsFragment.newInstance(), view);
                }
                break;
            case R.id.btn_tripList:
                userActionListener.onTripListClick();
                break;
            case R.id.refillButtonLayout:
                userActionListener.onRefillClick();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Method for open map, {@link MapFragment}.
     */
    public void openMapFragment() {
        userActionListener.onOpenMapFragment();
        configureMapFragment(tripProcessor);
        UtilMethods.replaceFragment(mapFragment, ConstantValues.MAP_FRAGMENT_TAG, view.getActivity());
    }

    /**
     * Method form safe exit with cleaning all services, process, etc.
     */
    public void performExit() {
        userActionListener.cleanAllProcess();
    }

    public void showFuelFillDialog() {
        showFuelFillDialog(FuelFillDialog.newInstance(), this);
    }

    private void showFuelFillDialog(@NonNull final FuelFillDialog dialog, @NonNull final MainFragment fragment) {
        dialog.setFuelChangeAmountListener(userActionListener);
        dialog.show(fragment.getChildFragmentManager(), "FILL_DIALOG");
    }

    public void setupAdView(@NonNull final AdRequest request) {
        advertView.loadAd(request);
        advertView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(final int i) {
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                userActionListener.configureTripProcessor();
            } else {
                requestPermissionWithRationale();
            }
        }
    }

    private void configureMapFragment(@NonNull final TripProcessor tripProcessor) {
        mapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ConstantValues.MAP_FRAGMENT_TAG);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
        }
        mapFragment.setGpsHandler(tripProcessor.getGpsHandler());
        setRoutesToMapFragment();
    }

    private void showSettingsFragment(@NonNull final SettingsFragment settingsFragment, @NonNull final Fragment fragment) {
        settingsFragment.setFileEraseListener(this);
        UtilMethods.replaceFragment(settingsFragment, SETTINGS_FRAGMENT_TAG, fragment.getActivity());
    }

    private void visualizeSpeedometer() {
        setupSpeedometerView();
        setSpeedometerLayoutParams();
    }

    private void setSpeedometerLayoutParams() {
        speedometerContainer.post(new Runnable() {
            @Override
            public void run() {
                speedometerContainer.setLayoutParams(MainFragment.getLayoutParams(!landscapeOrientation(), speedometer));
                speedometerContainer.invalidate();
                speedometerContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean landscapeOrientation() {
        return getResources().getConfiguration().orientation == 2;
    }

    private void setupSpeedometerView() {
        if (landscapeOrientation() && !BuildConfig.FLAVOR.equals(getString(R.string.paidVersion_code))) {
            speedometer = createSpeedometerGauge(getActivity(), true);
        } else {
            speedometer = createSpeedometerGauge(getActivity(), false);
        }
        speedometer.setScaleX(ConstantValues.SPEEDOMETER_WIDTH);
        speedometer.setScaleY(ConstantValues.SPEEDOMETER_HEIGHT);
        speedometerContainer.addView(speedometer);
        speedometerTextView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.speedometer_font)));
    }

    private TripHelperGauge createSpeedometerGauge(@NonNull final Context context, final boolean isLandscape) {
        return CircularGaugeFactory.getConfiguredSpeedometerGauge(context, PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(MEASUREMENT_UNIT, ""), isLandscape);
    }

    private void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.permissionExplanation), Snackbar.LENGTH_INDEFINITE)
                    .setAction(GRANT, new View.OnClickListener() {
                        @Override
                        public void onClick(@NonNull final View v) {
                            userActionListener.requestLocationPermissions();
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    private void setButtonsVisibility(final int visibility) {
        tripListButton.setVisibility(visibility);
        refillButtonLayout.setVisibility(visibility);
        settingsButton.setVisibility(visibility);
    }

    public void startButtonTurnActive() {
        startButton.setVisibility(View.VISIBLE);
        stopButton.setVisibility(View.INVISIBLE);
        setButtonsVisibility(View.VISIBLE);
    }

    public void stopButtonTurnActive() {
        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        setButtonsVisibility(View.INVISIBLE);
    }

    public void setFuelVisible() {
        fuel_left_layout.setVisibility(View.VISIBLE);
    }

    public void setFuelLeft(final String fuelLeft) {
        this.fuelLeft.setText(fuelLeft);
    }

    public void restoreFuelLevel(final String fuel) {
        fuelLeft.setText(fuel);
    }

    public void setGpsIconActive() {
        ButterKnife.bind(R.id.image_statusView, getActivity());
        if (statusImage != null) {
            setAppropriateImageStatusColor(ContextCompat.getDrawable(getActivity(), R.drawable.green_satellite));
        }
    }

    public void setGpsIconPassive() {
        ButterKnife.bind(R.id.image_statusView, getActivity());
        if (statusImage != null) {
            setAppropriateImageStatusColor(ContextCompat.getDrawable(getActivity(), R.drawable.red_satellite));
        }
    }

    private void setAppropriateImageStatusColor(@NonNull final Drawable greenSatellite) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            statusImage.setBackground(greenSatellite);
        } else {
            statusImage.setImageDrawable(greenSatellite);
        }
    }

    public static LinearLayoutCompat.LayoutParams getLayoutParams(final boolean isPortrait, @NonNull final TripHelperGauge speedometer) {
        final LinearLayoutCompat.LayoutParams layoutParams;
        final int dimension;
        if (isPortrait) {
            dimension = speedometer.getWidth();
            layoutParams = new LinearLayoutCompat.LayoutParams(dimension, dimension);
            layoutParams.setMargins(2, 2, 2, 2);
        } else {
            dimension = speedometer.getHeight();
            layoutParams = new LinearLayoutCompat.LayoutParams(dimension, dimension);
            layoutParams.setMargins(2, 2, 2, 2);
        }
        return layoutParams;
    }

    public void updateSpeedometerTextField(final float speed) {
        if (speedometerTextView != null) {
            final String formattedSpeed = UtilMethods.formatFloatToIntFormat(speed);
            UtilMethods.animateTextView(Integer.valueOf(speedometerTextView.getText().toString()), Integer.valueOf(formattedSpeed), speedometerTextView);
            speedometerTextView.setText(formattedSpeed);
        }
    }

    public void setPresenter(@NonNull final MainContract.UserActionListener presenter) {
        this.userActionListener = presenter;
    }
}