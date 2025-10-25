package com.example.wallstreettycoon.displayBuySell;

import android.annotation.SuppressLint;
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

public class BuyDialogFragment extends DialogFragment implements GameObserver {

    private int stockID;
    private String symbolText;
    private double currentPriceValue;
    private String username;

    private TextView priceTextView;
    private TextView totalCost;
    private EditText quantityInput;
    private Handler uiHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Bundle args = getArguments();
        stockID = args.getInt("stockID");
        symbolText = args.getString("stockSymbol");
        currentPriceValue = args.getDouble("currentPrice");
        username = args.getString("username");

        Game.getInstance().addObserver(this);

        // Set dialog title
        TextView header = view.findViewById(R.id.dialogHeader);
        header.setText("Buy " + symbolText);

        // Set stock details
        TextView symbol = view.findViewById(R.id.stockID);
        symbol.setText(symbolText);

        priceTextView = view.findViewById(R.id.currentPrice);
        priceTextView.setText(String.format("$%.2f", currentPriceValue));

        // Quantity and total cost logic
        quantityInput = view.findViewById(R.id.quantityInput);
        totalCost = view.findViewById(R.id.totalProceeds);

        quantityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotalCost(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Button actions
        Button confirmButton = view.findViewById(R.id.btnConfirm);
        confirmButton.setText("Buy");
        confirmButton.setOnClickListener(v -> {
            String quantityStr = quantityInput.getText().toString();

            if (!quantityStr.isEmpty()) {
                int quantity = Integer.parseInt(quantityStr);

                DatabaseUtil dbUtil = DatabaseUtil.getInstance(requireContext());

                boolean success = dbUtil.buyStock(username, stockID, quantity, currentPriceValue);

                if (success) {
                    Toast.makeText(getContext(), "Bought " + quantityStr + " shares of " + symbolText, Toast.LENGTH_SHORT).show();
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

    private void updateTotalCost(String input) {
        if (!input.isEmpty()) {
            try {
                int quantity = Integer.parseInt(input);

                if (quantity > 0) {
                    double total = quantity * currentPriceValue;
                    totalCost.setText(String.format("$%.2f", total));
                } else {
                    totalCost.setText("Enter a positive amount");
                }
            } catch (NumberFormatException e) {
                totalCost.setText("Invalid input");
            }
        } else {
            totalCost.setText("$0.00");
        }
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (event.getType() == GameEventType.UPDATE_STOCK_PRICE) {
            // Update price on UI thread
            uiHandler.post(() -> {
                if (!isAdded()) return;
                DatabaseUtil dbUtil = DatabaseUtil.getInstance(requireContext());
                Stock stock = dbUtil.getStock(stockID);

                if (stock != null) {
                    double[] priceHistory = stock.getPriceHistoryArray();
                    if (priceHistory != null && priceHistory.length > 0) {
                        currentPriceValue = Math.max(0, priceHistory[priceHistory.length - 1]);
                        priceTextView.setText(String.format("$%.2f", currentPriceValue));

                        // Update total cost with new price
                        updateTotalCost(quantityInput.getText().toString().trim());
                    }
                }
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