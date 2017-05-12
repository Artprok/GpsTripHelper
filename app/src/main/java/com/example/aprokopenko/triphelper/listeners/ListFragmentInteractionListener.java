package com.example.aprokopenko.triphelper.listeners;

import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.ui.trip_info.TripInfoFragment;

public interface ListFragmentInteractionListener {
    void onListItemClick(final Trip item);

    void onFragmentReplacing(final TripInfoFragment tripInfoFragment);
}