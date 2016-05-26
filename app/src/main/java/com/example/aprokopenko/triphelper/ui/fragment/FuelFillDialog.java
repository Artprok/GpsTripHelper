package com.example.aprokopenko.triphelper.ui.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;

import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.listener.FuelChangeAmountListener;

import butterknife.ButterKnife;
import butterknife.Bind;

public class FuelFillDialog extends DialogFragment {
    @Bind(R.id.refillButton)
    Button   fillButton;
    @Bind(R.id.fuelToFill)
    EditText fuelToFill;

    private FuelChangeAmountListener fuelChangeAmountListener;

    static FuelFillDialog newInstance() {
        return new FuelFillDialog();
    }

    public FuelFillDialog() {
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fuel_fill_dialog, container, false);
        ButterKnife.bind(this, view);
        fillButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String fuelToFill = FuelFillDialog.this.fuelToFill.getText().toString();
                float  fuel       = 0;
                if (!TextUtils.equals(fuelToFill, "")) {
                    fuel = Float.valueOf(fuelToFill);
                }
                if (fuelChangeAmountListener != null) {
                    fuelChangeAmountListener.fuelFilled(fuel);
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
        fuelChangeAmountListener = null;
    }

    public void setFuelChangeAmountListener(FuelChangeAmountListener listener) {
        fuelChangeAmountListener = listener;
    }
}

