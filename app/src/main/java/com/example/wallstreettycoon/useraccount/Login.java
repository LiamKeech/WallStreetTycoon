package com.example.wallstreettycoon.useraccount;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.dashboard.ListStocks;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;

public class Login extends AppCompatActivity {
    Context context = this;
    Button btnLoginAccount;
    TextView txtCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lblCreate), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtCreateAccount = findViewById(R.id.txtCreate);
        txtCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtCreateAccount.setTypeface(null, Typeface.ITALIC);
                Intent intent = new Intent(Login.this, CreateAccount.class);
                startActivity(intent);
            }
        });

        btnLoginAccount = findViewById(R.id.btnLogin);
        btnLoginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check all text fields filled in:
                EditText usernameInput = findViewById(R.id.edtUsernameLogin);
                EditText passwordInput = findViewById(R.id.edtPasswLogin);

                //check if username is in database, if it is check that password matches:
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (!username.isEmpty() && !password.isEmpty())
                {
                    DatabaseUtil dbUtil = new DatabaseUtil(context);
                    if (dbUtil.userExists(username)) {
                        User user = dbUtil.getUser(username);
                        if (user.getUserPassword().equals(password)) {
                            Intent loginIntent = new Intent(Login.this, ListStocks.class);
                            startActivity((loginIntent));
                        }
                    }
                    else {
                        Toast.makeText(v.getContext(), "Username does not exist.", Toast.LENGTH_SHORT).show();
                        usernameInput.setBackgroundResource(R.drawable.red_textbox_border);
                    }
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
    }

}