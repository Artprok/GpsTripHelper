package com.example.aprokopenko.triphelper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Class extends {@link  DialogFragment} responsible for showing a dialog with form that asks for amount fuel to fill.
 */
public class FuelFillDialog extends DialogFragment {
  @BindView(R.id.btn_refill)
  Button fillButton;
  @BindView(R.id.editText_fuelToFill)
  EditText fuelToFill;

  private FuelChangeAmountListener fuelChangeAmountListener;
  private Unbinder unbinder;

  static FuelFillDialog newInstance() {
    return new FuelFillDialog();
  }

  @Override public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_fuel_fill_dialog, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @OnClick(R.id.btn_refill)
  public void onRefillButtonClick() {
    final String fuelToFill = FuelFillDialog.this.fuelToFill.getText().toString();
    float fuel = 0f;
    if (!TextUtils.equals(fuelToFill, "")) {
      fuel = Float.valueOf(fuelToFill);
    }
    if (fuelChangeAmountListener != null) {
      fuelChangeAmountListener.fuelFilled(fuel);
    }
    dismiss();
  }

  @Override public void onDestroyView() {
    unbinder.unbind();
    super.onDestroyView();
  }

  @Override public void onDetach() {
    fuelChangeAmountListener = null;
    super.onDetach();
  }

  public void setFuelChangeAmountListener(@NonNull final FuelChangeAmountListener listener) {
    fuelChangeAmountListener = listener;
  }
}