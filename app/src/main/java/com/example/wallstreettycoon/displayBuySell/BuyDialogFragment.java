package com.example.wallstreettycoon.displayBuySell;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.DialogFragment;

import com.example.wallstreettycoon.R;

public class BuyDialogFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Set dialog title
        TextView header = view.findViewById(R.id.dialogHeader);
        header.setText("Buy AAA");

        // Set stock details
        TextView symbol = view.findViewById(R.id.stockSymbol);
        symbol.setText("AAA");
        TextView price = view.findViewById(R.id.currentPrice);
        price.setText("$50.00");

        // Quantity and total cost logic
        EditText quantityInput = view.findViewById(R.id.quantityInput);
        TextView totalCost = view.findViewById(R.id.totalCost);
        double currentPrice = 50.00;

        quantityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    int quantity = Integer.parseInt(s.toString());
                    double total = quantity * currentPrice;
                    totalCost.setText(String.format("Total Cost: $%.2f", total));
                } else {
                    totalCost.setText("Total Cost: $0.00");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Button actions
        Button confirmButton = view.findViewById(R.id.btnConfirm);
        confirmButton.setText("Buy");
        confirmButton.setOnClickListener(v -> {
            String quantity = quantityInput.getText().toString();
            if (!quantity.isEmpty()) {
                Toast.makeText(getContext(), "Bought " + quantity + " shares of AAA", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Set dialog to full width in landscape
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        getDialog().getWindow().setDimAmount(0.5f); // Maintain dimming
    }
}