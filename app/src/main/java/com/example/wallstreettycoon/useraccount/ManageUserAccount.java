package com.example.wallstreettycoon.useraccount;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.displayBuySell.BuyDialogFragment;

public class ManageUserAccount extends AppCompatActivity {
    Context context = this;
    TextView txtChangePassw;
    Button btnUpdate;

    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_user_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.Man), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtChangePassw = findViewById(R.id.lblChangePassw);
        txtChangePassw.setOnClickListener(v -> {
            txtChangePassw.setTypeface(null, Typeface.ITALIC);
            ChangePassswordDialogFragment changePassswordDialog = new ChangePassswordDialogFragment();
            changePassswordDialog.show(getSupportFragmentManager(), "ChangePasswordDialog");
        });

        btnUpdate = findViewById(R.id.btnUpdateManage);
        btnUpdate.setOnClickListener(v -> {
            EditText edtUser = findViewById(R.id.edtUsernameManage);
            EditText edtName = findViewById(R.id.edtNameManage);
            EditText edtSurname = findViewById(R.id.edtSurnameManage);
            EditText edtPassw = findViewById(R.id.editTextTextPassword);

            DatabaseUtil dbUtil = new DatabaseUtil(context);
            dbUtil.updateUser(edtUser.getText().toString(), edtName.getText().toString(),
                    edtSurname.getText().toString(), edtPassw.getText().toString());
        });


        btnCancel = findViewById(R.id.btnCancelManage);
        btnCancel.setOnClickListener(v -> {
            Intent backToLogin = new Intent(ManageUserAccount.this, Login.class);
            startActivity(backToLogin);
        });

    }
}