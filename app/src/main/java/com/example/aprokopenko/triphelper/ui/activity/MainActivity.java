package com.example.aprokopenko.triphelper.ui.activity;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.ui.fragment.MainFragment;
import com.example.aprokopenko.triphelper.ui.fragment.MapFragment;
import com.example.aprokopenko.triphelper.ui.fragment.TripInfoFragment;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.Module;
import io.fabric.sdk.android.Fabric;

@Module public class MainActivity extends AppCompatActivity {
    @BindView(R.id.btn_fab)
    FloatingActionButton fab;

    private static final String  LOG_TAG = "MainActivity";
    public static final  boolean DEBUG   = BuildConfig.DEBUG;
    private int fabTransitionValue;

    private Unbinder unbinder;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        //        Debug.startMethodTracing("Bottlenecks");
        Fabric.with(this, new Crashlytics());
        fabTransitionValue = getDataForFABanimation();

        proceedToFragmentCreating(savedInstanceState);
    }

    @Override public void onBackPressed() {
        final Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof MainFragment) {
            new AlertDialog.Builder(this).setIcon(R.drawable.btn_exit).setTitle(getString(R.string.exit_dialog_title))
                    .setMessage(getString(R.string.exit_dialog_string))
                    .setPositiveButton(getString(R.string.exit_dialog_yes), new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            performExitFromApplication((MainFragment) f);
                        }
                    }).setNegativeButton(getString(R.string.exit_dialog_no), new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialogInterface, int i) {
                    UtilMethods.replaceFragment(f, ConstantValues.MAIN_FRAGMENT_TAG, MainActivity.this);
                }
            }).show();
        }
        else {
            if (f instanceof TripInfoFragment) {  // if we in TripInfoFragment, to prevent laggy behaviour onBackPress duplicates.
                super.onBackPressed();
            }
            if (f instanceof MapFragment) { // if we in MapFragment, set FAB toMap state.
                MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG);
                setFabToMap(mainFragment);
            }
            super.onBackPressed();
        }
    }

    @Override protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void proceedToFragmentCreating(Bundle savedInstanceState) {
        UtilMethods.setFabInvisible(this);
        if (savedInstanceState == null) {
            if (DEBUG) {
                Log.i(LOG_TAG, "onCreate: new fragment");
            }
            MainFragment mainFragment = MainFragment.newInstance();
            assert fab != null;
            setFabToMap(mainFragment);
            UtilMethods.replaceFragment(mainFragment, ConstantValues.MAIN_FRAGMENT_TAG, this);
        }
        else {
            Fragment     fragment     = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG);
            if (fragment instanceof MainFragment) {
                if (DEBUG) {
                    Log.i(LOG_TAG, "onCreate: old fragment");
                }
                assert fab != null;
                setFabToMap((MainFragment) fragment);
                UtilMethods.replaceFragment(fragment, ConstantValues.MAIN_FRAGMENT_TAG, this);
            }
            if (fragment instanceof MapFragment) { // if we in MapFragment, set FAB toSpeedometer state.
                setFabToSpeedometer(mainFragment);
                fab.setVisibility(View.VISIBLE);
            }
            else {
                setFabToMap(mainFragment);
            }
        }
    }

    private int getDataForFABanimation() {
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point   sizePoint      = new Point();
        defaultDisplay.getSize(sizePoint);

        int delimiter = getDelimiterDependsOnOrientation(defaultDisplay);
        int width     = sizePoint.x;

        return -(width / delimiter);
    }

    private int getDelimiterDependsOnOrientation(Display defaultDisplay) {
        int widthDelimiter = ConstantValues.WIDTH_DELIMETER_FOR_PORTRAIT;
        int rotation       = defaultDisplay.getRotation();
        if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            widthDelimiter = ConstantValues.WIDTH_DELIMETER_FOR_LANDSCAPE;
        }
        return widthDelimiter;
    }

    private void setFabToMap(final MainFragment mainFragment) {
        int res = R.drawable.map_black;
        fab.setImageResource(res);
        UtilMethods.animateFabTransition(fab, 0);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mainFragment.openMapFragment();
                setFabToSpeedometer(mainFragment);
            }
        });
    }

    private void performExitFromApplication(MainFragment f) {
        //        Debug.stopMethodTracing();
        f.performExit();
        f.onDetach();
        finish();
        System.exit(0);
    }

    private void setFabToSpeedometer(final MainFragment mainFragment) {
        int res = R.drawable.road_black;
        fab.setImageResource(res);

        UtilMethods.animateFabTransition(fab, fabTransitionValue);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                MainFragment mf = (MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG);
                if (mf == null || !mf.equals(mainFragment)) {
                    mf = MainFragment.newInstance();
                    assert fab != null;
                    setFabToMap(mf);
                }
                else {
                    assert fab != null;
                    setFabToMap(mf);
                }
                UtilMethods.replaceFragment(mf, ConstantValues.MAIN_FRAGMENT_TAG, MainActivity.this);
            }
        });
    }

}
