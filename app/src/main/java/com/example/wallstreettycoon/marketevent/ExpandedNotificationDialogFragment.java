package com.example.wallstreettycoon.marketevent;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.minigames.miniGame1.miniGame1;
import com.example.wallstreettycoon.minigames.miniGame1.miniGame1Notification;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2;
import com.example.wallstreettycoon.minigames.miniGame2.miniGame2Notification;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3;
import com.example.wallstreettycoon.minigames.miniGame3.miniGame3Notification;

public class ExpandedNotificationDialogFragment extends DialogFragment {

    public static ExpandedNotificationDialogFragment newInstance(String title, String info, int duration, boolean isMinigame, int minigameID) {
        ExpandedNotificationDialogFragment fragment = new ExpandedNotificationDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("info", info);
        args.putInt("duration", duration);
        args.putBoolean("isMinigame", isMinigame);
        args.putInt("minigameID", minigameID);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_notification_expanded, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get data from arguments
        String title = getArguments().getString("title");
        String info = getArguments().getString("info");
        int duration = getArguments().getInt("duration");
        boolean isMinigame = getArguments().getBoolean("isMinigame", false);
        int minigameID = getArguments().getInt("minigameID", 0);

        TextView txtTitle = view.findViewById(R.id.txtNotificationTitle);
        TextView txtInfo = view.findViewById(R.id.txtNotificationInfo);
        TextView btnClose = view.findViewById(R.id.btnExpandedClose);
        AppCompatButton btnDismiss = view.findViewById(R.id.btnDismiss);
        AppCompatButton btnPlayMinigame = view.findViewById(R.id.btnPlayMinigame);

        txtTitle.setText(title);
        txtInfo.setText(info);

        // Show/hide minigame button based on notification type
        if (isMinigame && minigameID > 0) {
            btnPlayMinigame.setVisibility(View.VISIBLE);
            btnPlayMinigame.setOnClickListener(v -> {
                launchMinigame(minigameID, duration);
                dismiss();
            });
        } else {
            btnPlayMinigame.setVisibility(View.GONE);
        }

        // Close button
        btnClose.setOnClickListener(v -> dismiss());

        // Dismiss button
        btnDismiss.setOnClickListener(v -> dismiss());
    }

    private void launchMinigame(int minigameID, int duration) {
        Intent intent;

        switch (minigameID) {
            case 1:
                intent = new Intent(getContext(), miniGame1Notification.class);
                break;
            case 2:
                intent = new Intent(getContext(), miniGame2Notification.class);
                break;
            case 3:
                intent = new Intent(getContext(), miniGame3Notification.class);
                break;
            default:
                Toast.makeText(getContext(), "Invalid minigame ID: " + minigameID, Toast.LENGTH_SHORT).show();
                return;
        }

        //intent.putExtra("duration", duration);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}