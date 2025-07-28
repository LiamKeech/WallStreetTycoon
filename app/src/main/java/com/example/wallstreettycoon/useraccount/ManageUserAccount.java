package com.example.wallstreettycoon.useraccount;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wallstreettycoon.Game;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.dashboard.ListStocks;
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

        //set initial values to the logged in user: (from current user)
        EditText edtUser = findViewById(R.id.edtUsernameManage);
        edtUser.setText(Game.currentUser.getUserUsername());
        EditText edtName = findViewById(R.id.edtNameManage);
        edtName.setText(Game.currentUser.getUserFirstName());
        EditText edtSurname = findViewById(R.id.edtSurnameManage);
        edtSurname.setText(Game.currentUser.getUserLastName());
        EditText edtPassw = findViewById(R.id.editTextTextPassword);
        edtPassw.setText(Game.currentUser.getUserPassword());

        txtChangePassw = findViewById(R.id.lblChangePassw);
        txtChangePassw.setOnClickListener(v -> {
            txtChangePassw.setTypeface(null, Typeface.ITALIC);
            ChangePassswordDialogFragment changePassswordDialog = new ChangePassswordDialogFragment();
            changePassswordDialog.show(getSupportFragmentManager(), "ChangePasswordDialog");
            Intent intent = getIntent();
            String value = intent.getStringExtra("new");
            edtPassw.setText(value);
        });

        btnUpdate = findViewById(R.id.btnUpdateManage);
        btnUpdate.setOnClickListener(v -> {
            DatabaseUtil dbUtil = new DatabaseUtil(context);
            dbUtil.updateUser(edtUser.getText().toString(), edtName.getText().toString(),
                    edtSurname.getText().toString(), edtPassw.getText().toString());

            Toast.makeText(v.getContext(), "Account updated.", Toast.LENGTH_SHORT).show();
            Intent backToDash = new Intent(ManageUserAccount.this, ListStocks.class);
            startActivity(backToDash);

            //test
            User test = dbUtil.getUser(edtUser.getText().toString());
            Log.d(test.getUserUsername(), "");
        });

        btnCancel = findViewById(R.id.btnCancelManage);
        btnCancel.setOnClickListener(v -> { //return to dashboard
            Intent backToDash = new Intent(ManageUserAccount.this, ListStocks.class);
            startActivity(backToDash);
        });

    }
}