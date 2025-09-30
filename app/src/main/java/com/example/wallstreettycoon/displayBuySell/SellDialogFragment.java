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
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;

public class SellDialogFragment extends DialogFragment {
    private TextView header, symbol, price, totalCost, remainingShares;
    private EditText quantityInput;
    private Button confirmButton;
    private double ownedShares;
    private double currentPriceValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Bundle args = getArguments();
        int stockID = args.getInt("stockID");
        String symbolText = args.getString("stockSymbol");
        currentPriceValue = args.getDouble("currentPrice");
        String username = args.getString("username");

        // Set dialog title
        header = view.findViewById(R.id.dialogHeader);
        header.setText("Sell " + symbolText);

        // Display stock
        symbol = view.findViewById(R.id.stockID);
        symbol.setText(symbolText);

        // Display current price
        price = view.findViewById(R.id.currentPrice);
        price.setText(String.format("$%.2f", currentPriceValue));

        // Quantity and total cost logic
        quantityInput = view.findViewById(R.id.quantityInput);
        totalCost = view.findViewById(R.id.totalProceeds);
        remainingShares = view.findViewById(R.id.sharesRemaining);

        confirmButton = view.findViewById(R.id.btnConfirm);
        confirmButton.setText("Sell");

        // Get owned shares from DB
        DatabaseUtil dbUtil = new DatabaseUtil(requireContext());
        int portfolioID = dbUtil.getPortfolioID(username);
        ownedShares = dbUtil.getOwnedQuantity(portfolioID, stockID);
        remainingShares.setText(String.format("%.2f", ownedShares));

        // If no shares owned, disable confirm or show message
        if (ownedShares <= 0) {
            confirmButton.setEnabled(false);
            totalCost.setText("No shares available");
        }

        quantityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim(); //remove spaces
                if (!input.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(input);

                        if (quantity <= 0) {
                            totalCost.setText("Enter positive amount");
                            confirmButton.setEnabled(false);
                            remainingShares.setText(String.format("%.2f", ownedShares));
                        } else if (quantity > ownedShares) {
                            totalCost.setText("Not enough shares");
                            confirmButton.setEnabled(false);
                            remainingShares.setText(String.format("%.2f", ownedShares));
                        } else {
                            double total = quantity * currentPriceValue;
                            totalCost.setText(String.format("$%.2f", total));
                            confirmButton.setEnabled(true);

                            // Dynamically update remaining shares
                            double remaining = ownedShares - quantity;
                            remainingShares.setText(String.format("%.2f", remaining));
                        }
                    } catch (NumberFormatException e) {
                        totalCost.setText("Invalid input");
                        confirmButton.setEnabled(false);
                        remainingShares.setText(String.format("%.2f", ownedShares));
                    }
                } else {
                    totalCost.setText("$0.00");
                    confirmButton.setEnabled(false);
                    remainingShares.setText(String.format("%.2f", ownedShares));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Button actions
        Button confirmButton = view.findViewById(R.id.btnConfirm);
        confirmButton.setText("Sell");
        confirmButton.setOnClickListener(v -> {
            String quantityStr = quantityInput.getText().toString();
            if (!quantityStr.isEmpty()) {
                int quantity = Integer.parseInt(quantityStr);

                boolean success = dbUtil.sellStock(username, stockID, quantity, currentPriceValue);

                if (success) {
                    Toast.makeText(getContext(), "Sold " + quantityStr + " shares of " + symbolText, Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to sell: Not enough shares", Toast.LENGTH_SHORT).show();
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
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        getDialog().getWindow().setDimAmount(0.5f);
    }
}