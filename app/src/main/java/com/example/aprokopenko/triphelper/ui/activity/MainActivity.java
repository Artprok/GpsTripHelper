package com.example.aprokopenko.triphelper.ui.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.content.pm.PackageManager;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.Manifest;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.ui.fragment.MainFragment;
import com.example.aprokopenko.triphelper.ui.fragment.TripInfoFragment;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;

import io.fabric.sdk.android.Fabric;
import butterknife.ButterKnife;
import butterknife.Bind;
import dagger.Module;

@Module public class MainActivity extends AppCompatActivity {
    @Bind(R.id.fab)
    FloatingActionButton fab;

    private static final String LOG_TAG               = "MainActivity";
    private static final int    LOCATION_REQUEST_CODE = 1;
    private Bundle savedInstanceState;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fab.setBackgroundColor((ContextCompat.getColor(this, R.color.colorPrimary)));

        if (UtilMethods.isPermissionAllowed(this)) {
            requestLocationPermissions();
        }
        else {
            proceedToFragmentCreating(savedInstanceState);
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                proceedToFragmentCreating(savedInstanceState);
            }
            else {
                requestPermissionWithRationale();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override public void onBackPressed() {
        final Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof MainFragment) {
            new AlertDialog.Builder(this).setIcon(R.drawable.exit_icon).setTitle(getString(R.string.exit_dialog_title))
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
            if (f instanceof TripInfoFragment) {
                super.onBackPressed();
            }
            super.onBackPressed();
        }
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,}, LOCATION_REQUEST_CODE);
    }

    private void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            final String message = getResources().getString(R.string.permissionExplanation);
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            requestLocationPermissions();
                        }
                    }).show();
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void proceedToFragmentCreating(Bundle savedInstanceState) {
        MainFragment mainFragment;
        UtilMethods.setFabInvisible(this);

        if (savedInstanceState == null) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.i(LOG_TAG, "onCreate: new fragment");
            }
            mainFragment = MainFragment.newInstance();
            assert fab != null;
            setFabToMap(mainFragment);
        }
        else {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.i(LOG_TAG, "onCreate: old fragment");
            }
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG);
            assert fab != null;
            setFabToMap(mainFragment);
        }
        UtilMethods.replaceFragment(mainFragment, ConstantValues.MAIN_FRAGMENT_TAG, this);
    }

    private void setFabToMap(final MainFragment mainFragment) {
        int res = R.drawable.map_black;
        fab.setImageResource(res);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mainFragment.openMapFragment();
                setFabToSpeedometer(mainFragment);
            }
        });
    }

    private void performExitFromApplication(MainFragment f) {
        f.performExit();
        f.onDetach();
        finish();
        System.exit(0);
    }

    private void setFabToSpeedometer(final MainFragment mainFragment) {
        int res = R.drawable.road_black;
        fab.setImageResource(res);
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
