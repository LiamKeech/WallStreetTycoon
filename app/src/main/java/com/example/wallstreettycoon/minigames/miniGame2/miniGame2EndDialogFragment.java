/**
 * Author: Gareth Munnings
 * Created on 2025/08/11
 */
package com.example.wallstreettycoon.minigames.miniGame2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.dashboard.ListStocks;

public class miniGame2EndDialogFragment extends DialogFragment {
    Button homeButton;
    Button retryButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mini_game2_end_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

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
            int dialogWidth = (int) (screenWidth * 0.50);
            window.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setDimAmount(0.5f); // Optional: dim background
        }
        getDialog().getWindow().setDimAmount(0.5f); // Maintain dimming

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListStocks.class); //will change to dashboard
                intent.putExtra("view", "M");
                startActivity(intent);
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), miniGame2.class);
                startActivity(intent);
            }
        });
    }
}

