package com.example.wallstreettycoon.useraccount;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.dashboard.ListStocks;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;

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

        //to test:set current user to a user

        //set initial values to the logged in user: (from current user)
        EditText edtUser = findViewById(R.id.edtUsernameManage);
        edtUser.setText(Game.currentUser.getUserUsername());
        EditText edtName = findViewById(R.id.edtNameManage);
        edtName.setText(Game.currentUser.getUserFirstName());
        EditText edtSurname = findViewById(R.id.edtSurnameManage);
        edtSurname.setText(Game.currentUser.getUserLastName());
        EditText edtPassw = findViewById(R.id.editTextTextPassword);
        //edtPassw.setText(Game.currentUser.getUserPassword());

        txtChangePassw = findViewById(R.id.lblChangePassw);
        txtChangePassw.setOnClickListener(v -> {
            txtChangePassw.setTypeface(null, Typeface.ITALIC);
            ChangePassswordDialogFragment changePassswordDialog = new ChangePassswordDialogFragment();
            changePassswordDialog.show(getSupportFragmentManager(), "ChangePasswordDialog");
        });

        //new password value:
        Intent intent = getIntent();
        String value = intent.getStringExtra("new");
        if (value == null) { edtPassw.setText(Game.currentUser.getUserPassword());}
        else { edtPassw.setText(value);}

        btnUpdate = findViewById(R.id.btnUpdateManage);
        btnUpdate.setOnClickListener(v -> {
            DatabaseUtil dbUtil = new DatabaseUtil(context);

            String name = edtName.getText().toString();
            String surname = edtSurname.getText().toString();
            String password = edtPassw.getText().toString();

            if (!name.isEmpty() && !surname.isEmpty()) {
                //update user details
                dbUtil.updateUser(edtUser.getText().toString(), name, surname, password);
                //notify user:
                //Toast.makeText(v.getContext(), "Account updated.", Toast.LENGTH_SHORT).show();
                updateToast();
                Intent backToDash = new Intent(ManageUserAccount.this, ListStocks.class);
                startActivity(backToDash);
            }
            else { fieldsToast(); }
            /*else if (name.isEmpty()) {
                Toast.makeText(v.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                edtName.setBackgroundResource(R.drawable.red_textbox_border);
            }
            else {
                Toast.makeText(v.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                edtSurname.setBackgroundResource(R.drawable.red_textbox_border);
            }*/

            //set fields to updated information:
            edtName.setText(name);
            edtSurname.setText(surname);
            edtPassw.setText(password);

            //test
            User test = dbUtil.getUser(edtUser.getText().toString());
            Log.d(test.getUserUsername(), "Name update to " + surname);

        });

        btnCancel = findViewById(R.id.btnCancelManage);
        btnCancel.setOnClickListener(v -> { //return to dashboard
            Intent backToDash = new Intent(ManageUserAccount.this, ListStocks.class);
            startActivity(backToDash);
        });

    }

    public void updateToast()
    {
        LayoutInflater inflator = getLayoutInflater();
        View layout = inflator.inflate(R.layout.custom_toast, null);
        TextView txtToast = layout.findViewById(R.id.txtToast);
        ImageView imgToast = layout.findViewById(R.id.imgToast);
        LinearLayout toastLayout = layout.findViewById(R.id.toastLayout);

        toastLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.Green));
        imgToast.setImageResource(android.R.drawable.stat_notify_sync);
        txtToast.setText("Account updated!");
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
