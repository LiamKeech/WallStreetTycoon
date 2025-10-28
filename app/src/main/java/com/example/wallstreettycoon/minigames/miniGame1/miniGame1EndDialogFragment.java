package com.example.wallstreettycoon.minigames.miniGame1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import java.io.OutputStream;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.dashboard.ListStocks;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.model.GameEvent;
import com.example.wallstreettycoon.model.GameEventType;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

public class miniGame1EndDialogFragment extends DialogFragment {
    Float profit;
    TextView profitLabel;
    Button homeButton;
    Button retryButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mini_game1_end_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (getArguments() != null) {
            profit = getArguments().getFloat("profit");

        }
        new Thread(() -> {
            DatabaseUtil.getInstance(getContext()).uploadScores(profit, "minigame1");
        }).start();

        profitLabel = view.findViewById(R.id.profit_end_textview);
        if(profit > 0.0){
            profitLabel.setText(String.format("Profit: $%.2f", profit));
            profitLabel.setTextColor(getResources().getColor(R. color. Green));
            //profitLabel.setTextAppearance(R.style.GreenButtonLarge);
            //profitLabel.setBackgroundResource(R.drawable.button_background_green_medium);
        }
        else if(profit == 0.0){
            profitLabel.setText(String.format("Break even: $0.00"));
            profitLabel.setTextColor(getResources().getColor(R.color.DarkBlue));
            //profitLabel.setTextAppearance(R.style.OrangeButtonLarge);
            //profitLabel.setBackgroundResource(R.drawable.button_background_orange_medium);
        }
        else{

            profitLabel.setText(String.format("Loss: $%.2f", profit));
            profitLabel.setTextColor(getResources().getColor(R.color.Red));
            //profitLabel.setTextAppearance(R.style.RedButtonLarge);
            //profitLabel.setBackgroundResource(R.drawable.button_background_red_medium);
        }

        homeButton = view.findViewById(R.id.home_button);
        retryButton = view.findViewById(R.id.retryButton);

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
            int dialogWidth = (int) (screenWidth * 0.5);
            window.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setDimAmount(0.5f); // Optional: dim background
        }
        getDialog().getWindow().setDimAmount(0.5f); // Maintain dimming

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game.getInstance().onGameEvent(new GameEvent(GameEventType.MINIGAME_COMPLETED, "Minigame 1 completed", 1));
                Intent intent = new Intent(getActivity(), ListStocks.class);
                intent.putExtra("view", "M");
                startActivity(intent);
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), miniGame1.class);
                startActivity(intent);
            }
        });

    }
}
