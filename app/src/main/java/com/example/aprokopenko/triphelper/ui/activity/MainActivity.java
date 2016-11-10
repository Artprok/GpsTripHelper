package com.example.aprokopenko.triphelper.ui.activity;

import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.Module;
import io.fabric.sdk.android.Fabric;

/**
 * Class representing a main {@link AppCompatActivity}
 */
@Module
public class MainActivity extends AppCompatActivity {
  @BindView(R.id.btn_fab)
  FloatingActionButton fab;

  private static final String LOG_TAG = "MainActivity";
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private int fabTransitionValue;
  private Unbinder unbinder;

  @Override protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    unbinder = ButterKnife.bind(this);
    //        Debug.startMethodTracing("Bottlenecks");
    MobileAds.initialize(this, getString(R.string.admob_publisher_testid_forAct));
    Fabric.with(this, new Crashlytics());
    fabTransitionValue = getDataForFABanimation();

    proceedToFragmentCreating(savedInstanceState);
  }

  @Override public void onBackPressed() {
    final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
    if (fragment instanceof MainFragment) {
      new AlertDialog.Builder(this).setIcon(R.drawable.btn_exit).setTitle(getString(R.string.exit_dialog_title))
              .setMessage(getString(R.string.exit_dialog_string))
              .setPositiveButton(getString(R.string.exit_dialog_yes), new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                  performExitFromApplication((MainFragment) fragment);
                }
              }).setNegativeButton(getString(R.string.exit_dialog_no), new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialogInterface, int i) {
          UtilMethods.replaceFragment(fragment, ConstantValues.MAIN_FRAGMENT_TAG, MainActivity.this);
        }
      }).show();
    } else {
      if (fragment instanceof TripInfoFragment) {  // if we in TripInfoFragment, to prevent laggy behaviour onBackPress duplicates.
        super.onBackPressed();
      }
      if (fragment instanceof MapFragment) { // if we in MapFragment, set FAB toMap state.
        final MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG);
        setFabToMap(mainFragment);
      }
      super.onBackPressed();
    }
  }

  @Override protected void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }

  private void proceedToFragmentCreating(@Nullable final Bundle savedInstanceState) {
    UtilMethods.hideFab(this);
    if (savedInstanceState == null) {
      final MainFragment mainFragment = MainFragment.newInstance();
      assert fab != null;
      setFabToMap(mainFragment);
      UtilMethods.replaceFragment(mainFragment, ConstantValues.MAIN_FRAGMENT_TAG, this);
      if (DEBUG) {
        Log.i(LOG_TAG, "onCreate: new fragment");
      }
    } else {
      final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
      if (fragment instanceof MainFragment) {
        assert fab != null;
        setFabToMap((MainFragment) fragment);
        UtilMethods.replaceFragment(fragment, ConstantValues.MAIN_FRAGMENT_TAG, this);
        if (DEBUG) {
          Log.i(LOG_TAG, "onCreate: old fragment");
        }
      }
      if (fragment instanceof MapFragment) { // if we in MapFragment, set FAB toSpeedometer state.
        setFabToSpeedometer((MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG));
        fab.setVisibility(View.VISIBLE);
      } else {
        setFabToMap((MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG));
      }
    }
  }

  private int getDataForFABanimation() {
    final Display defaultDisplay = getWindowManager().getDefaultDisplay();
    final Point sizePoint = new Point();
    defaultDisplay.getSize(sizePoint);

    return -(sizePoint.x / getDelimiterDependsOnOrientation(defaultDisplay));
  }

  private int getDelimiterDependsOnOrientation(@NonNull final Display defaultDisplay) {
    final int rotation = defaultDisplay.getRotation();
    if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
      return ConstantValues.WIDTH_DELIMETER_FOR_LANDSCAPE;
    }
    return ConstantValues.WIDTH_DELIMETER_FOR_PORTRAIT;
  }

  private void setFabToMap(@NonNull final MainFragment mainFragment) {
    fab.setImageResource(R.drawable.map_black);
    UtilMethods.animateFabTransition(fab, 0);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(@NonNull final View view) {
        mainFragment.openMapFragment();
        setFabToSpeedometer(mainFragment);
      }
    });
  }

  private void performExitFromApplication(@NonNull final MainFragment mainFragment) {
    //        Debug.stopMethodTracing();
    mainFragment.performExit();
    mainFragment.onDetach();
    finish();
    System.exit(0);
  }

  private void setFabToSpeedometer(@NonNull final MainFragment mainFragment) {
    fab.setImageResource(R.drawable.road_black);
    UtilMethods.animateFabTransition(fab, fabTransitionValue);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(@NonNull final View view) {
        MainFragment mf = (MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG);
        if (mf == null || !mf.equals(mainFragment)) {
          mf = MainFragment.newInstance();
          assert fab != null;
          setFabToMap(mf);
        } else {
          assert fab != null;
          setFabToMap(mf);
        }
        UtilMethods.replaceFragment(mf, ConstantValues.MAIN_FRAGMENT_TAG, MainActivity.this);
      }
    });
  }
}