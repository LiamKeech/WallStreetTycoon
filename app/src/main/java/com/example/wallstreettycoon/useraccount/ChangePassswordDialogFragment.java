package com.example.wallstreettycoon.useraccount;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallstreettycoon.R;

public class ChangePassswordDialogFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_passsword_dialog, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //save button:
        TextView NewPasswInput = view.findViewById(R.id.edtNewPassw);
        TextView ConfirmPasswInput = view.findViewById(R.id.edtConfirmPassw);
        Button btnSave = view.findViewById(R.id.btnSave);
        TextView viewPassw = view.findViewById(R.id.editTextTextPassword);
        btnSave.setOnClickListener(v -> {
            String newPassw = NewPasswInput.getText().toString();
            String confPassw = ConfirmPasswInput.getText().toString();

            if (!newPassw.isEmpty() && !confPassw.isEmpty()) {
                if (newPassw.equals(confPassw)) {
                    //put password into password textbox on manage account activity:
                    viewPassw.setText(newPassw);
                }
            }
            else if (newPassw.isEmpty()) {
                Toast.makeText(v.getContext(), "Enter new password", Toast.LENGTH_SHORT).show();
                NewPasswInput.setBackgroundResource(R.drawable.red_textbox_border);
            }
            else if (confPassw.isEmpty()) {
                Toast.makeText(v.getContext(), "Confirm new password", Toast.LENGTH_SHORT).show();
                ConfirmPasswInput.setBackgroundResource(R.drawable.red_textbox_border);
            }
        });

        //cancel button: close dialog
        Button btnCancel = view.findViewById(R.id.btnCancelPasswChange);
        btnCancel.setOnClickListener(v -> dismiss());


        return view;
    }
}