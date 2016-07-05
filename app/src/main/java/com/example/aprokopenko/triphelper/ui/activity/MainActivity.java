package com.example.aprokopenko.triphelper.ui.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.ui.fragment.MapFragment;
import com.example.aprokopenko.triphelper.ui.fragment.MainFragment;
import com.example.aprokopenko.triphelper.ui.fragment.TripInfoFragment;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;

import io.fabric.sdk.android.Fabric;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.Unbinder;
import dagger.Module;

@Module public class MainActivity extends AppCompatActivity {
    @BindView(R.id.fab)
    FloatingActionButton fab;


    private static final String LOG_TAG = "MainActivity";
    private Unbinder unbinder;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        //        Debug.startMethodTracing("Bottlenecks");

        Fabric.with(this, new Crashlytics());

        proceedToFragmentCreating(savedInstanceState);
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

    @Override protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void proceedToFragmentCreating(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "proceedToFragmentCreating: CreatingProceed");
        UtilMethods.setFabInvisible(this);
        if (savedInstanceState == null) {
            if (ConstantValues.LOGGING_ENABLED) {
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
                if (ConstantValues.LOGGING_ENABLED) {
                    Log.i(LOG_TAG, "onCreate: old fragment");
                }
                assert fab != null;
                setFabToMap((MainFragment) fragment);
                UtilMethods.replaceFragment(fragment, ConstantValues.MAIN_FRAGMENT_TAG, this);
            }
            if (fragment instanceof MapFragment) {
                setFabToSpeedometer(mainFragment);
                fab.setVisibility(View.VISIBLE);
            }
            else {
                setFabToMap(mainFragment);
            }
        }
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
        //        Debug.stopMethodTracing();
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
