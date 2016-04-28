package com.example.aprokopenko.triphelper.listener;

import com.example.aprokopenko.triphelper.ui.fragment.TripInfoFragment;
import com.example.aprokopenko.triphelper.datamodel.Trip;

public interface OnListFragmentInteractionListener {
    void onListFragmentInteraction(Trip item);

    void onFragmentReplacing(TripInfoFragment tripInfoFragment);
}

