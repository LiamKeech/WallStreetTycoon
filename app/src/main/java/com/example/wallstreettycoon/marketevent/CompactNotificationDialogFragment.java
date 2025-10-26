package com.example.wallstreettycoon.marketevent;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.model.MarketEvent;

public class CompactNotificationDialogFragment extends DialogFragment {
    private MarketEvent marketEvent;

    public static CompactNotificationDialogFragment newInstance(MarketEvent event) {
        CompactNotificationDialogFragment fragment = new CompactNotificationDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", event.getTitle());
        args.putString("info", event.getInfo());
        args.putInt("duration", event.getDuration());
        args.putBoolean("isMinigame", event.isMinigame());
        args.putInt("minigameID", event.getMinigameID());
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
        return inflater.inflate(R.layout.dialog_notification_compact, container, false);
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
        TextView btnClose = view.findViewById(R.id.btnCompactClose);
        AppCompatButton btnOpen = view.findViewById(R.id.btnCompactOpen);

        txtTitle.setText(title);

        // Dismiss
        btnClose.setOnClickListener(v -> dismiss());

        // Show expanded view
        btnOpen.setOnClickListener(v -> {
            dismiss();

            // Open expanded view
            ExpandedNotificationDialogFragment expandedDialog = ExpandedNotificationDialogFragment.newInstance(title, info, duration, isMinigame, minigameID);
            expandedDialog.show(getParentFragmentManager(), "ExpandedNotification");
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams params = window.getAttributes();

            // Get screen width
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int halfScreenWidth = screenWidth / 2;

            // Position at top-right corner
            params.gravity = Gravity.TOP | Gravity.END;
            params.x = 16; // Margin from right edge in pixels
            params.y = 16; // Margin from top edge in pixels

            // Set width to half the screen
            params.width = halfScreenWidth;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            window.setAttributes(params);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}