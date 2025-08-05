package com.example.wallstreettycoon.minigames;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.fragment.app.DialogFragment;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.dashboard.ListStocks;

import org.w3c.dom.Text;

public class miniGame1EndDialogFragment extends DialogFragment {
    Float profit;
    TextView profitLabel;
    Button homeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mini_game1_end_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (getArguments() != null) {
            profit = getArguments().getFloat("profit");
        }

        profitLabel = view.findViewById(R.id.profit_end_textview);
        if(profit > 0.0){
            profitLabel.setText(String.format("+ $%.2f", profit));
            profitLabel.setBackgroundColor(Color.parseColor("#48C73C")); // green
        }
        else{
            profitLabel.setText(String.format("- $%.2f", profit));
            profitLabel.setBackgroundColor(Color.parseColor("#E80A00")); // red
        }

        homeButton = view.findViewById(R.id.home_button);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();

            // Get screen width
            DisplayMetrics displayMetrics = new DisplayMetrics();
            requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;

            // Set dialog width to 90% of screen width, height wrap content
            int dialogWidth = (int) (screenWidth * 0.50);
            window.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setDimAmount(0.5f); // Optional: dim background
        }
        getDialog().getWindow().setDimAmount(0.5f); // Maintain dimming

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), miniGame1Starter.class); //will change to dashboard
                startActivity(intent);
            }
        });
    }
}
