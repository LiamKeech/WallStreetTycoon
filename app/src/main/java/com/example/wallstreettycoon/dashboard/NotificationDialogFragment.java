package com.example.wallstreettycoon.dashboard;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.model.MarketEvent;

public class NotificationDialogFragment extends DialogFragment {
    private MarketEvent marketEvent;

    public static NotificationDialogFragment newInstance(MarketEvent event) {
        NotificationDialogFragment fragment = new NotificationDialogFragment();
        Bundle args = new Bundle();
        // Pass event data through bundle
        args.putString("title", event.getTitle());
        args.putString("info", event.getInfo());
        args.putInt("duration", event.getDuration());
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get data from arguments
        String title = getArguments().getString("title");
        String info = getArguments().getString("info");

        TextView txtTitle = view.findViewById(R.id.txtNotificationTitle);
        TextView txtInfo = view.findViewById(R.id.txtNotificationInfo);
        TextView btnClose = view.findViewById(R.id.btnClose);
        AppCompatButton btnNotNow = view.findViewById(R.id.btnNotNow);
        AppCompatButton btnOpen = view.findViewById(R.id.btnOpen);

        txtTitle.setText(title);
        txtInfo.setText(info);

        btnClose.setOnClickListener(v -> dismiss());

        btnNotNow.setOnClickListener(v -> dismiss());

        btnOpen.setOnClickListener(v -> {
            // TODO: Handle opening related content (e.g., minigame)
            dismiss();
        });
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