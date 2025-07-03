package com.example.wallstreettycoon.displayBuySell;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wallstreettycoon.R;

public class BuySell_DialogFragment extends DialogFragment {
    private String action; // "Buy" or "Sell"
    private String stockSymbol;
    private double currentPrice;

    // Constructor to pass data
    public static BuySell_DialogFragment newInstance(String action, String stockSymbol, double currentPrice) {
        BuySell_DialogFragment fragment = new BuySell_DialogFragment();
        Bundle args = new Bundle();
        args.putString("action", action);
        args.putString("stock_symbol", stockSymbol);
        args.putDouble("current_price", currentPrice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_sell_dialog, container, false);

        // Retrieve arguments
        action = getArguments().getString("action");
        stockSymbol = getArguments().getString("stock_symbol");
        currentPrice = getArguments().getDouble("current_price");

        // Set up UI elements
        TextView header = view.findViewById(R.id.lblStockName_Frag);
        TextView stockSymbolText = view.findViewById(R.id.txtStockSymbol_Frag);
        TextView currentPriceText = view.findViewById(R.id.txtCurrentPrice_Frag);
        EditText quantityInput = view.findViewById(R.id.txtStockQuantity_Frag);
        TextView totalCostText = view.findViewById(R.id.txtTotalCost_Frag);
        Button actionButton = view.findViewById(R.id.btnAction);
        Button cancelButton = view.findViewById(R.id.btnCancelAction);

        // Populate UI
        header.setText(action + " " + stockSymbol);
        stockSymbolText.setText(stockSymbol);
        currentPriceText.setText("$" + String.format("%.2f", currentPrice));
        actionButton.setText(action);

        // Update total cost based on quantity input
        quantityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String quantityStr = s.toString();
                if (!quantityStr.isEmpty()) {
                    try {
                        int quantity = Integer.parseInt(quantityStr);
                        double total = quantity * currentPrice;
                        totalCostText.setText("$" + String.format("%.2f", total));
                    } catch (NumberFormatException e) {
                        totalCostText.setText("$0");
                    }
                } else {
                    totalCostText.setText("$0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Handle button clicks
        actionButton.setOnClickListener(v -> {
            // Add buy/sell logic here (e.g., update portfolio or balance)
            dismiss();
        });
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setDimAmount(0.5f); // Dim background by 50%
            }
        }
    }
}
