package com.example.aprokopenko.triphelper.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.aprokopenko.triphelper.ApplicationComponent;
import com.example.aprokopenko.triphelper.DaggerApplicationComponent;
import com.example.aprokopenko.triphelper.dependency_injection.AppModule;

public class TripHelperApp extends Application {
  private static final String LOG_TAG = "TripHelperApp";

  private static ApplicationComponent applicationComponent;
  private static Context context;
  private static SharedPreferences sharedPreferences;

  public static ApplicationComponent getApplicationComponent() {
    return applicationComponent;
  }

  public static Context getAppContext() {
    return context;
  }

  public static SharedPreferences getSharedPreferences() {
    return sharedPreferences;
  }

  @Override public void onCreate() {
    super.onCreate();
    context = getApplicationContext();
    sharedPreferences = this.getSharedPreferences("tripPreferences", Context.MODE_PRIVATE);
    final AppModule module = new AppModule(this);
    applicationComponent = DaggerApplicationComponent.builder().appModule(module).build();
  }
}