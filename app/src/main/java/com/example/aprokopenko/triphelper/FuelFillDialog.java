package com.example.aprokopenko.triphelper;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;

import com.example.aprokopenko.triphelper.listener.DialogFragmentInteractionListener;

import butterknife.ButterKnife;
import butterknife.Bind;

public class FuelFillDialog extends DialogFragment {
    @Bind(R.id.fillButton)
    Button   fillButton;
    @Bind(R.id.fuelToFill)
    EditText fuelToFill;

    private DialogFragmentInteractionListener dialogFragmentInteractionListener;

    public FuelFillDialog() {

    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fuel_fill_dialog, container, false);
        ButterKnife.bind(this, view);
        fillButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String fuelCapacity = fuelToFill.getText().toString();
                float  fuel         = Float.valueOf(fuelCapacity);
                if (dialogFragmentInteractionListener != null) {
                    dialogFragmentInteractionListener.fuelFilled(fuel);
                }
                dismiss();
            }
        });

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override public void onDetach() {
        super.onDetach();
        dialogFragmentInteractionListener = null;
    }

    public void setDialogFragmentInteractionListener(DialogFragmentInteractionListener listener) {
        dialogFragmentInteractionListener = listener;
    }
}

