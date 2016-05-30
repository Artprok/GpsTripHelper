package com.example.aprokopenko.triphelper.ui.activity;

import android.content.DialogInterface;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.util.Log;

import com.example.aprokopenko.triphelper.ui.fragment.TripInfoFragment;
import com.example.aprokopenko.triphelper.utils.util_methods.UtilMethods;
import com.example.aprokopenko.triphelper.utils.settings.ConstantValues;
import com.example.aprokopenko.triphelper.ui.fragment.MainFragment;
import com.example.aprokopenko.triphelper.R;

import butterknife.ButterKnife;
import butterknife.Bind;
import dagger.Module;

@Module public class MainActivity extends AppCompatActivity {
    @Bind(R.id.fab)
    FloatingActionButton fab;

    public static final String LOG_TAG = "MainActivity";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MainFragment mainFragment;
        UtilMethods.setFabInvisible(this);

        if (savedInstanceState == null) {
            if (ConstantValues.LOGGING_ENABLED) {
                Log.i(LOG_TAG, "onCreate: new fragment");
            }
            mainFragment = MainFragment.newInstance();
            UtilMethods.replaceFragment(mainFragment, ConstantValues.MAIN_FRAGMENT_TAG, this);
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
    }

    @Override public void onBackPressed() {
        final Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (f instanceof MainFragment) {
            new AlertDialog.Builder(this).setIcon(R.drawable.exit_icon).setTitle(getString(R.string.exit_dialog_title))
                    .setMessage(getString(R.string.exit_dialog_string))
                    .setPositiveButton(getString(R.string.exit_dialog_yes), new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            f.onDetach();
                            finish();
                            System.exit(0);
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

    private void setFabToMap(final MainFragment mainFragment) {
        int res = R.drawable.map_black;
        fab.setImageResource(res);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mainFragment.openMapFragment();
                setFabToSpeedometer();
            }
        });
    }

    private void setFabToSpeedometer() {
        int res = R.drawable.road_black;
        fab.setImageResource(res);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                MainFragment mf = (MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG);
                if (mf == null) {
                    mf = MainFragment.newInstance();
                    UtilMethods.replaceFragment(mf, ConstantValues.MAIN_FRAGMENT_TAG, MainActivity.this);
                    assert fab != null;
                    setFabToMap(mf);
                }
                else {
                    mf = (MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG);
                    assert fab != null;
                    setFabToMap(mf);
                    UtilMethods.replaceFragment(mf, ConstantValues.MAIN_FRAGMENT_TAG, MainActivity.this);
                }
            }
        });
    }
}
