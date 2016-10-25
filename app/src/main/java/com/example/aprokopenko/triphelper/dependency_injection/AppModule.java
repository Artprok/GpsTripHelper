package com.example.aprokopenko.triphelper.dependency_injection;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
  private static final String LOG_TAG = "AppModule";
  private final Context context;

  public AppModule(@NonNull final Application application) {
    context = application.getApplicationContext();
  }

  @Provides Context provideApplicationContext() {
    return context;
  }

  @Provides LocationManager provideLocationManager() {
    return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
  }
}