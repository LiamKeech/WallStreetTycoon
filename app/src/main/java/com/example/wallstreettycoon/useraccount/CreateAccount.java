package com.example.wallstreettycoon.useraccount;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.dashboard.ListStocks;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;

public class CreateAccount extends AppCompatActivity {
    Context context = this;
    Button btnCreateAccount;
    Button btnCancelCreate;

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

        //create account:
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
                    User newUser = new User(username, name, surname,  password, 1000.0);

                    //check if username doesn't already exist:
                    DatabaseUtil dbUtil = new DatabaseUtil(context);
                    if (!dbUtil.userExists(username)) {

                        //insert user into db:
                        dbUtil.setUser(newUser);
                        //take to list stocks/dashboard:
                        Intent createdIntent = new Intent(CreateAccount.this, ListStocks.class);
                        startActivity(createdIntent);

                        //test
                        User test = dbUtil.getUser(username);
                        Log.d(test.getUserUsername(), "");
                    }
                    else {
                        Toast.makeText(v.getContext(), "Username already exists.", Toast.LENGTH_SHORT).show();
                        usernameInput.setBackgroundResource(R.drawable.red_textbox_border);
                    }
                }
                else if (name.isEmpty()) {
                    Toast.makeText(v.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    nameInput.setBackgroundResource(R.drawable.red_textbox_border);
                }
                else if (surname.isEmpty()) {
                    Toast.makeText(v.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    surnameInput.setBackgroundResource(R.drawable.red_textbox_border);
                }
                else if (username.isEmpty()) {
                    Toast.makeText(v.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    usernameInput.setBackgroundResource(R.drawable.red_textbox_border);
                }
                else if (password.isEmpty()) {
                    Toast.makeText(v.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    passwordInput.setBackgroundResource(R.drawable.red_textbox_border);
                }
            }
        });

        //cancel create account:
        btnCancelCreate = findViewById(R.id.btnCancelCreate);
        btnCancelCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cancelIntent = new Intent(CreateAccount.this, Login.class);
                startActivity(cancelIntent);
            }
        });
    }
}