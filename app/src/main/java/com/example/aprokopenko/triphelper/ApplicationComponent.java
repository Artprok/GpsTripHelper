package com.example.aprokopenko.triphelper;

import com.example.aprokopenko.triphelper.dependency_injection.AppModule;
import com.example.aprokopenko.triphelper.gps_utils.GpsHandler;

import javax.inject.Singleton;

import dagger.Component;

@Singleton @Component(modules = {AppModule.class}) public interface ApplicationComponent {
    void injectInto(GpsHandler GpsHandler);
}
