package com.example.aprokopenko.triphelper.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.example.aprokopenko.triphelper.BuildConfig;
import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.ui.fragment.MapFragment;
import com.example.aprokopenko.triphelper.ui.fragment.TripInfoFragment;
import com.example.aprokopenko.triphelper.ui.main_screen.MainFragment;
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
  @BindView(R.id.spashScreen)
  ImageView splashScreen;

  private static final String LOG_TAG = "MainActivity";
  private static final boolean DEBUG = BuildConfig.DEBUG;
  private static final int SPLASH_DELAY = 2300;

  private int fabTransitionValue;
  private Unbinder unbinder;

  @Override protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    unbinder = ButterKnife.bind(this);
    MobileAds.initialize(this, getString(R.string.admob_publisher_testid_forAct));
    Fabric.with(this, new Crashlytics());
    fabTransitionValue = getDataForFABanimation(this);

    proceedToFragmentCreating(savedInstanceState);
  }

  @Override public void onBackPressed() {
    final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
    if (fragment instanceof MainFragment) {
      new AlertDialog.Builder(this).setIcon(R.drawable.btn_exit).setTitle(getString(R.string.exit_dialog_title))
              .setMessage(getString(R.string.exit_dialog_string))
              .setPositiveButton(getString(R.string.exit_dialog_yes), new DialogInterface.OnClickListener() {
                @Override public void onClick(@NonNull final DialogInterface dialog, final int which) {
                  performExitFromApplication((MainFragment) fragment, MainActivity.this);
                }
              }).setNegativeButton(getString(R.string.exit_dialog_no), new DialogInterface.OnClickListener() {
        @Override public void onClick(@NonNull final DialogInterface dialogInterface, final int i) {
          UtilMethods.replaceFragment(fragment, ConstantValues.MAIN_FRAGMENT_TAG, MainActivity.this);
        }
      }).show();
    } else {
      if (fragment instanceof TripInfoFragment) { // if we in TripInfoFragment perform super.onBack pressed, if not we willl have laggy behaviour with duplicates of backPressed.
        super.onBackPressed();
      }
      if (fragment instanceof MapFragment) { // if we in MapFragment, set FAB toMap state.
        setFabToMap((MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG));
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
      if (splashScreen != null) {
        splashScreen.setVisibility(View.VISIBLE);
        splashScreen.postDelayed(new Runnable() {
          @Override public void run() {
            splashScreen.setVisibility(View.GONE);
            UtilMethods.replaceFragment(mainFragment, ConstantValues.MAIN_FRAGMENT_TAG, MainActivity.this);
          }
        }, SPLASH_DELAY);
      }
    } else {
      final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
      if (fragment instanceof MainFragment) {
        assert fab != null;
        setFabToMap((MainFragment) fragment);
        UtilMethods.replaceFragment(fragment, ConstantValues.MAIN_FRAGMENT_TAG, this);
      } else if (fragment instanceof MapFragment) { // if we in MapFragment, set FAB toSpeedometer state.
        setFabToSpeedometer((MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG));
        fab.setVisibility(View.VISIBLE);
      } else {
        setFabToMap((MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG));
      }
    }
  }

  private static int getDataForFABanimation(@NonNull final Activity activity) {
    final Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
    final Point sizePoint = new Point();

    defaultDisplay.getSize(sizePoint);
    return -(sizePoint.x / getDelimiterDependsOnOrientation(defaultDisplay));
  }

  private static int getDelimiterDependsOnOrientation(@NonNull final Display defaultDisplay) {
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

  private static void performExitFromApplication(@NonNull final MainFragment mainFragment, @NonNull final Activity activity) {
    //        Debug.stopMethodTracing();
    mainFragment.performExit();
    mainFragment.onDetach();
    activity.finish();
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