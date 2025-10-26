package com.example.wallstreettycoon.useraccount;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.model.Game;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class LogoutConfirmationDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_logout_confirmation, null);

        // Initialize UI elements
        TextView message = view.findViewById(R.id.logout_message);
        Button confirmButton = view.findViewById(R.id.btn_confirm_logout);
        Button cancelButton = view.findViewById(R.id.btn_cancel_logout);

        // Set message
        message.setText("Are you sure you want to log out?");

        // Confirm button listener
        confirmButton.setOnClickListener(v -> {
            // Clear current user
            Game.currentUser = null;
            // Navigate to Login
            Intent intent = new Intent(requireContext(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            dismiss();
        });

        // Cancel button listener
        cancelButton.setOnClickListener(v -> dismiss());

        builder.setView(view);
        AlertDialog dialog = builder.create();

        // Remove white background and padding
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return dialog;
    }
}