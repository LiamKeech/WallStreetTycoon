package com.example.wallstreettycoon.useraccount;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.wallstreettycoon.model.Game;

public class CreateAccount extends AppCompatActivity {
    Context context;
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

        context = this;
        //create account:
        btnCreateAccount = findViewById(R.id.btnCreate);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //obtain information, check and validate, create user obj:
                EditText nameInput = findViewById(R.id.edtNameCreate);
                EditText surnameInput = findViewById(R.id.edtSurnameCreate);
                EditText usernameInput = findViewById(R.id.edtUsernameCreate);
                EditText passwordInput = findViewById(R.id.edtPasswCreate);


                String name = nameInput.getText().toString();
                Log.d("CREATE ACCOUNT", name);
                String surname = surnameInput.getText().toString();
                Log.d("CREATE ACCOUNT", surname);
                String username = usernameInput.getText().toString();
                Log.d("CREATE ACCOUNT", username);
                String password = passwordInput.getText().toString();
                Log.d("CREATE ACCOUNT", password);

                if (!name.isEmpty() && !surname.isEmpty() && !username.isEmpty() && !password.isEmpty()) {

                    //check if username doesn't already exist:
                    DatabaseUtil dbUtil = DatabaseUtil.getInstance(context); // SINGLETON FIX
                    if (!dbUtil.userExists(username)) {

                        //insert user into db:
                        User newUser = new User(username, name, surname,  password, 1000.0);
                        dbUtil.setUser(newUser);

                        //inform user account made successfully:
                        Toast.makeText(v.getContext(), "Account created successfully", Toast.LENGTH_SHORT).show();

                        //user taken back to Login:
                        Intent createdIntent = new Intent(CreateAccount.this, Login.class);
                        startActivity(createdIntent);

                        //test
                        User test = dbUtil.getUser(username);
                        Log.d(test.getUserUsername(), "");
                    }
                    else { usernameToast(); }
                }
                else {  //either of the fields empty
                    fieldsToast();
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

    public void usernameToast()
    {
        LayoutInflater inflator = getLayoutInflater();
        View layout = inflator.inflate(R.layout.custom_toast, null);
        TextView txtToast = layout.findViewById(R.id.txtToast);
        ImageView imgToast = layout.findViewById(R.id.imgToast);

        imgToast.setImageResource(android.R.drawable.ic_menu_info_details);
        txtToast.setText("Username already exists.");
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void fieldsToast()
    {
        LayoutInflater inflator = getLayoutInflater();
        View layout = inflator.inflate(R.layout.custom_toast, null);
        TextView txtToast = layout.findViewById(R.id.txtToast);
        ImageView imgToast = layout.findViewById(R.id.imgToast);

        imgToast.setImageResource(android.R.drawable.ic_menu_info_details);
        txtToast.setText("Please fill in all fields");
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}