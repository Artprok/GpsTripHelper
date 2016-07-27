package com.example.aprokopenko.triphelper.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.aprokopenko.triphelper.R;
import com.example.aprokopenko.triphelper.listener.FuelChangeAmountListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FuelFillDialog extends DialogFragment {
    @BindView(R.id.btn_refill)
    Button   fillButton;
    @BindView(R.id.editText_fuelToFill)
    EditText fuelToFill;

    private FuelChangeAmountListener fuelChangeAmountListener;
    private Unbinder                 unbinder;

    static FuelFillDialog newInstance() {
        return new FuelFillDialog();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fuel_fill_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);
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

    @Override public void onDetach() {
        super.onDetach();
        fuelChangeAmountListener = null;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setFuelChangeAmountListener(FuelChangeAmountListener listener) {
        fuelChangeAmountListener = listener;
    }
}

