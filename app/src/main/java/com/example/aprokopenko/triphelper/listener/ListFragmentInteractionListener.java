package com.example.aprokopenko.triphelper.listener;

import com.example.aprokopenko.triphelper.ui.fragment.TripInfoFragment;
import com.example.aprokopenko.triphelper.datamodel.Trip;

public interface ListFragmentInteractionListener {
    void onListItemClick(Trip item);

    void onFragmentReplacing(TripInfoFragment tripInfoFragment);
}

