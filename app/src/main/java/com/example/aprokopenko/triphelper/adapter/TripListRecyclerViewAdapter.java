package com.example.aprokopenko.triphelper.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.datamodel.Trip;
import com.example.aprokopenko.triphelper.listeners.ListFragmentInteractionListener;
import com.example.aprokopenko.triphelper.ui.fragment.TripInfoFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Class represents an {@link android.widget.BaseAdapter} for populating a {@link RecyclerView} with {@link Trip}
 */
public class TripListRecyclerViewAdapter extends RecyclerView.Adapter<TripListRecyclerViewAdapter.ViewHolder> {
  private static final String LOG_TAG = "RECYCLER_ADAPTER";

  private final ListFragmentInteractionListener listFragmentInteractionListener;
  private final ArrayList<Trip> tripList;

  public TripListRecyclerViewAdapter(@NonNull final ArrayList<Trip> trips, @NonNull final ListFragmentInteractionListener listener) {
    listFragmentInteractionListener = listener;
    tripList = trips;
  }

  @Override public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false));
  }

  @Override public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
    final Trip curTrip = tripList.get(position);
    holder.idView.setText(String.valueOf(curTrip.getTripID()));
    holder.tripDate.setText(curTrip.getTripDate());
    holder.trip = curTrip;
    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(@NonNull final View v) {
        final TripInfoFragment tripInfoFragment = TripInfoFragment.newInstance(curTrip);
        listFragmentInteractionListener.onListItemClick(holder.trip);
        listFragmentInteractionListener.onFragmentReplacing(tripInfoFragment);
      }
    });
  }

  @Override public int getItemCount() {
    return tripList.size();
  }

  class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.text_id)
    TextView idView;
    @BindView(R.id.trip_date)
    TextView tripDate;

    private final View mView;
    private Trip trip;

    ViewHolder(@NonNull final View view) {
      super(view);
      mView = view;
      ButterKnife.bind(this, view);
    }
  }
}