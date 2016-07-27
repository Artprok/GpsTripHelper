package com.example.aprokopenko.triphelper.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.datamodel.TripInfoContainer;
import com.example.aprokopenko.triphelper.listener.ListFragmentInteractionListener;
import com.example.aprokopenko.triphelper.ui.fragment.TripInfoFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripListRecyclerViewAdapter extends RecyclerView.Adapter<TripListRecyclerViewAdapter.ViewHolder> {
    public static final String LOG_TAG = "RECYCLER_ADAPTER";

    private final ListFragmentInteractionListener listFragmentInteractionListener;
    private final ArrayList<Trip>                 tripList;

    public TripListRecyclerViewAdapter(ArrayList<Trip> trips, ListFragmentInteractionListener listener) {
        listFragmentInteractionListener = listener;
        tripList = trips;
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
                if (listFragmentInteractionListener != null) {
                    listFragmentInteractionListener.onListItemClick(holder.trip);

                    TripInfoContainer tripInfoContainer = new TripInfoContainer(date, curTrip.getDistanceTravelled(), curTrip.getAvgSpeed(),
                            curTrip.getTimeSpentForTrip(), curTrip.getTimeSpentInMotion(), curTrip.getTimeSpentOnStop(),
                            curTrip.getAvgFuelConsumption(), curTrip.getFuelSpent(), id, curTrip.getRoute(), curTrip.getMoneyOnFuelSpent(),
                            curTrip.getMaxSpeed(), null);

                    TripInfoFragment tripInfoFragment = TripInfoFragment.newInstance(tripInfoContainer);
                    listFragmentInteractionListener.onFragmentReplacing(tripInfoFragment);
                }
            }
        });
    }

    @Override public int getItemCount() {
        if (tripList == null) {
            return 0;
        }
        else {
            return tripList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public       Trip trip;
        @BindView(R.id.text_id)
        TextView idView;
        @BindView(R.id.text_content)
        TextView contentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
