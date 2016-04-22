package com.example.aprokopenko.triphelper.dependency_injection;

import android.location.LocationManager;
import android.app.Application;
import android.content.Context;

import dagger.Provides;
import dagger.Module;

@Module public class AppModule {
    private static final String LOG_TAG = "AppModule";
    private final Context context;

    public AppModule(Application application) {
        context = application.getApplicationContext();
    }

    @Provides public Context provideApplicationContext() {
        return context;
    }

    @Provides public LocationManager provideLocationManager() {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

}
