package com.example.wallstreettycoon.useraccount;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;

public class CreateAccount extends AppCompatActivity {
    Context context = this;
    Button btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lblCreateAccount), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        btnCreateAccount = findViewById(R.id.btnCreate);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //obtain information, check and validate, create user obj:
                EditText nameInput = findViewById(R.id.edtNameCreate);
                EditText surnameInput = findViewById(R.id.edtSurnameCreate);
                EditText usernameInput = findViewById(R.id.edtUsernameLogin);
                EditText passwordInput = findViewById(R.id.edtPasswLogin);

                String name = nameInput.getText().toString();
                String surname = surnameInput.getText().toString();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (!name.isEmpty() && !surname.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                    User newUser = new User(name, surname, username, password, 1000);

                    //insert user into db:
                    DatabaseUtil dbUtil = new DatabaseUtil(context);
                    dbUtil.setUser(newUser);
                }

            }
        });
    }
}