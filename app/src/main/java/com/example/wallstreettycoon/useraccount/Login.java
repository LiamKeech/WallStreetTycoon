package com.example.wallstreettycoon.useraccount;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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

import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.dashboard.ListStocks;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.portfolio.PortfolioStock;

import java.util.List;

public class Login extends AppCompatActivity {
    Context context = this;
    DatabaseUtil dbUtil;
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

        dbUtil = new DatabaseUtil(context);

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
                    if (dbUtil.userExists(username)) {  //username exists
                        User user = dbUtil.getUser(username);
                        if (user.getUserPassword().equals(password)) { //valid credentials: password matches username
                            Intent loginIntent = new Intent(Login.this, ListStocks.class);

                            Game.startGame(getApplicationContext(), user);

                            List<PortfolioStock> portfolioStockCheck = dbUtil.getPortfolio(Game.getInstance().currentUser.getUserUsername());
                            String viewType;
                            if (portfolioStockCheck.isEmpty()) {
                                viewType = "M";
                            } else {
                                viewType = "P";
                            }
                            loginIntent.putExtra("view", viewType);
                            startActivity((loginIntent));
                        }
                        else {incorrectToast();}    //password doesnt match username
                    }
                    else {
                        notValidToast();    //username doesnt exist
                    }
                }
                else { incorrectToast(); }  //username or password is blank
            }
        });
    }

    public void incorrectToast() {
        LayoutInflater inflator = getLayoutInflater();
        View layout = inflator.inflate(R.layout.custom_toast, null);
        TextView txtToast = layout.findViewById(R.id.txtToast);
        ImageView imgToast = layout.findViewById(R.id.imgToast);
        imgToast.setImageResource(android.R.drawable.ic_menu_info_details);
        txtToast.setText("Incorrect username or password.");
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public void notValidToast() {
        LayoutInflater inflator = getLayoutInflater();
        View layout = inflator.inflate(R.layout.custom_toast, null);
        TextView txtToast = layout.findViewById(R.id.txtToast);
        ImageView imgToast = layout.findViewById(R.id.imgToast);
        imgToast.setImageResource(android.R.drawable.ic_menu_info_details);
        txtToast.setText("Username does not exist.");
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}