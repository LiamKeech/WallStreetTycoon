package com.example.wallstreettycoon.displayBuySell;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.model.GameEvent;
import com.example.wallstreettycoon.model.GameEventType;
import com.example.wallstreettycoon.model.GameObserver;
import com.example.wallstreettycoon.stock.Stock;
import com.example.wallstreettycoon.stock.StockPriceFunction;

public class SellDialogFragment extends DialogFragment implements GameObserver {

    private int stockID;
    private String symbolText;
    private double currentPriceValue;
    private String username;
    private double ownedShares;

    private TextView header, symbol, priceTextView, totalCost, remainingShares;
    private EditText quantityInput;
    private Button confirmButton, selectAllButton; // Added selectAllButton
    private Handler uiHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Bundle args = getArguments();
        stockID = args.getInt("stockID");
        symbolText = args.getString("stockSymbol");
        currentPriceValue = args.getDouble("currentPrice");
        username = args.getString("username");

        Game.getInstance().addObserver(this);

        // Set dialog title
        header = view.findViewById(R.id.dialogHeader);
        header.setText("Sell " + symbolText);

        // Display stock
        symbol = view.findViewById(R.id.stockID);
        symbol.setText(symbolText);

        // Display current price
        priceTextView = view.findViewById(R.id.currentPrice);
        priceTextView.setText(String.format("$%.2f", currentPriceValue));

        // Quantity and total cost logic
        quantityInput = view.findViewById(R.id.quantityInput);
        totalCost = view.findViewById(R.id.totalProceeds);
        remainingShares = view.findViewById(R.id.sharesRemaining);
        confirmButton = view.findViewById(R.id.btnConfirm);
        selectAllButton = view.findViewById(R.id.selectAllButton); // Initialize selectAllButton
        confirmButton.setText("Sell");

        // Get owned shares from DB
        DatabaseUtil dbUtil = DatabaseUtil.getInstance(requireContext());
        int portfolioID = dbUtil.getPortfolioID(username);
        ownedShares = dbUtil.getQuantity(portfolioID, stockID);
        remainingShares.setText(String.format("%.2f", ownedShares));

        // If no shares owned, disable confirm and select all
        if (ownedShares <= 0) {
            confirmButton.setEnabled(false);
            selectAllButton.setEnabled(false); // Disable selectAllButton
            totalCost.setText("No shares available");
        }

        // Select All button click listener
        selectAllButton.setOnClickListener(v -> {
            int sharesToSell = (int) Math.floor(ownedShares);
            quantityInput.setText(String.valueOf(sharesToSell));
        });

        quantityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSellCalculations(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Button actions
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

    private void updateSellCalculations(String input) {
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
    public void onGameEvent(GameEvent event) {
        if (event.getType() == GameEventType.UPDATE_STOCK_PRICE) {
            // Update price on UI thread
            uiHandler.post(() -> {
                if (!isAdded()) return;
                DatabaseUtil dbUtil = DatabaseUtil.getInstance(requireContext());
                StockPriceFunction stockPriceFunction = Game.getInstance().getStockPriceFunction(stockID);

                currentPriceValue = stockPriceFunction.getCurrentPrice(Game.getInstance().getCurrentTimeStamp());
                priceTextView.setText(String.format("$%.2f", currentPriceValue));

                // Update total proceeds with new price
                updateSellCalculations(quantityInput.getText().toString().trim());

            });
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Game.getInstance().removeObserver(this);
    }
}