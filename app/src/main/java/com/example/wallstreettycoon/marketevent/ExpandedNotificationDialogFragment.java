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

public class ExpandedNotificationDialogFragment extends DialogFragment {

    public static ExpandedNotificationDialogFragment newInstance(String title, String info, int duration, boolean isMinigame) {
        ExpandedNotificationDialogFragment fragment = new ExpandedNotificationDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("info", info);
        args.putInt("duration", duration);
        args.putBoolean("isMinigame", isMinigame);
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
        //int duration = getArguments().getInt("duration");
        boolean isMinigame = getArguments().getBoolean("isMinigame", false);

        TextView txtTitle = view.findViewById(R.id.txtNotificationTitle);
        TextView txtInfo = view.findViewById(R.id.txtNotificationInfo);
        TextView btnClose = view.findViewById(R.id.btnExpandedClose);
        AppCompatButton btnDismiss = view.findViewById(R.id.btnDismiss);
        AppCompatButton btnPlayMinigame = view.findViewById(R.id.btnPlayMinigame);

        txtTitle.setText(title);
        txtInfo.setText(info);

        // Show/hide minigame button based on notification type
        if (isMinigame) {
            btnPlayMinigame.setVisibility(View.VISIBLE);
            btnPlayMinigame.setOnClickListener(v -> {
                // Launch minigame activity
                //launchMinigame(duration);
                launchMinigame();
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

    private void launchMinigame() {
        // TODO: Replace with your actual minigame activity launch
        // Example:
        // Intent intent = new Intent(getContext(), MinigameActivity.class);
        // intent.putExtra("duration", duration);
        // startActivity(intent);

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