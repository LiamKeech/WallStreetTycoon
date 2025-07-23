package com.example.wallstreettycoon.useraccount;

import static java.security.AccessController.getContext;

import android.os.Bundle;
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

public class Login extends AppCompatActivity {
    Button btnLoginAccount;

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

        EditText usernameInput = findViewById(R.id.edtUsernameLogin);
        EditText passwordInput = findViewById(R.id.edtPasswLogin);

        btnLoginAccount = findViewById(R.id.btnLogin);
        btnLoginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check all text fields filled in:
                //username:
                String username = usernameInput.getText().toString();
                if (!username.isEmpty())
                {
                    Toast.makeText(v.getContext(), "Valid username", Toast.LENGTH_SHORT).show();
                }

                String password = passwordInput.getText().toString();
                if (!password.isEmpty())
                {
                    Toast.makeText(v.getContext(), "valid password", Toast.LENGTH_SHORT).show();
                }

                //check that details exist and match in database:


                //taken to Dashboard:

            }
        });
    }

}