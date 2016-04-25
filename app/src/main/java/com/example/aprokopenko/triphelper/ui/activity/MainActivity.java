package com.example.aprokopenko.triphelper.ui.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

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
            mainFragment = MainFragment.newInstance();
            UtilMethods.replaceFragment(mainFragment, ConstantValues.MAIN_FRAGMENT_TAG, this);
            assert fab != null;
            setFabToMap(mainFragment);

        }
        else {
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(ConstantValues.MAIN_FRAGMENT_TAG);
            assert fab != null;
            setFabToMap(mainFragment);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setFabToMap(final MainFragment mainFragment) {
        int res = R.drawable.map;
        fab.setImageResource(res);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mainFragment.openMapFragment();
                setFabToSpeedometer();
            }
        });
    }

    private void setFabToSpeedometer() {
        int res = R.drawable.highway_alt2;
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
