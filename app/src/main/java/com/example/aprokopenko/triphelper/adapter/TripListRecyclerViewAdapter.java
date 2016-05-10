package com.example.aprokopenko.triphelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;

import com.example.aprokopenko.triphelper.listener.OnListFragmentInteractionListener;
import com.example.aprokopenko.triphelper.ui.fragment.TripInfoFragment;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Bind;

public class TripListRecyclerViewAdapter extends RecyclerView.Adapter<TripListRecyclerViewAdapter.ViewHolder> {
    public static final String LOG_TAG = "RECYCLER_ADAPTER";
    private final ArrayList<Trip>                   tripList;
    private final OnListFragmentInteractionListener onListFragmentInteractionListener;

    public TripListRecyclerViewAdapter(ArrayList<Trip> trips, OnListFragmentInteractionListener listener) {
        tripList = trips;
        onListFragmentInteractionListener = listener;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, int position) {
        final Trip   curTrip = tripList.get(position);
        final String date    = curTrip.getTripDate();
        final int    id      = curTrip.getTripID();


        holder.idView.setText(String.valueOf(id));
        holder.contentView.setText(date);
        holder.trip = curTrip;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (onListFragmentInteractionListener != null) {
                    onListFragmentInteractionListener.onListItemClick(holder.trip);
                    TripInfoFragment tripInfoFragment = TripInfoFragment
                            .newInstance(date, curTrip.getDistanceTravelled(), curTrip.getAvgSpeed(), curTrip.getTimeSpent(),
                                    curTrip.getTimeSpentInMotion(), curTrip.getTimeSpentOnStop(), curTrip.getAvgFuelConsumption(),
                                    curTrip.getFuelSpent(), id, curTrip.getRoute(), curTrip.getMoneyOnFuelSpent(), curTrip.getMaxSpeed());
                    onListFragmentInteractionListener.onFragmentReplacing(tripInfoFragment);
                }
            }
        });
    }

    @Override public int getItemCount() {
        return tripList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.id)
        TextView idView;
        @Bind(R.id.content)
        TextView contentView;

        public       Trip trip;
        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
