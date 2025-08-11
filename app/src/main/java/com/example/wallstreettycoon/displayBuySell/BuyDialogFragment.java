package com.example.wallstreettycoon.displayBuySell;

import android.annotation.SuppressLint;
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
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;

public class BuyDialogFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Bundle args = getArguments();
        int stockID = args.getInt("stockID");
        String symbolText = args.getString("stockSymbol");
        double currentPriceValue = args.getDouble("currentPrice");
        String username = args.getString("username");

        // Set dialog title
        TextView header = view.findViewById(R.id.dialogHeader);
        header.setText("Buy " + symbolText);

        // Set stock details
        TextView symbol = view.findViewById(R.id.stockID);
        symbol.setText(symbolText);
        TextView price = view.findViewById(R.id.currentPrice);
        price.setText(String.format("$%.2f", currentPriceValue));

        // Quantity and total cost logic
        EditText quantityInput = view.findViewById(R.id.quantityInput);
        TextView totalCost = view.findViewById(R.id.totalCost);
        //double currentPrice = 50.00;

        quantityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim(); //remove spaces
                if (!input.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(input);

                        if (quantity > 0) {
                            double total = quantity * currentPriceValue; //calculate the total cost if input is valid and positive
                            totalCost.setText(String.format("Total Cost: $%.2f", total));
                        } else {
                            totalCost.setText("Enter a positive amount");
                        }

                    } catch (NumberFormatException e) {
                        totalCost.setText("Invalid input");
                    }
                } else {
                    totalCost.setText("Total Cost: $0.00"); //default
                }
            }

            @Override
            public void afterTextChanged(Editable s) {            }
        });


        // Button actions
        Button confirmButton = view.findViewById(R.id.btnConfirm);
        confirmButton.setText("Buy");
        confirmButton.setOnClickListener(v -> {
            String quantityStr = quantityInput.getText().toString();

            if (!quantityStr.isEmpty()) {
                int quantity = Integer.parseInt(quantityStr);

                DatabaseUtil dbUtil = new DatabaseUtil(requireContext());

                boolean success = dbUtil.buyStock(username, stockID, quantity, currentPriceValue);//FIXME

                if (success) {
                    Toast.makeText(getContext(), "Bought " + quantityStr + " shares of " + symbolText, Toast.LENGTH_SHORT).show();
                    //insertTransaction(username, stockID, "BUY", quantity, new BigDecimal(price));
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to buy: Insufficient funds", Toast.LENGTH_SHORT).show();
                }
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