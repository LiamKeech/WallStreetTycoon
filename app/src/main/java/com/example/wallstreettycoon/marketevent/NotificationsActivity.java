package com.example.wallstreettycoon.marketevent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.dashboard.ListStocks;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.model.MarketEvent;
import java.util.Collections;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private TextView lblEmpty;
    private ImageButton btnBack;
    private DatabaseUtil dbUtil;
    private String viewType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rvNotifications = findViewById(R.id.rvNotifications);
        lblEmpty = findViewById(R.id.lblEmptyNotifications);
        btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        viewType = intent.getStringExtra("viewType");
        if (viewType == null) {
            viewType = "M"; // Default to Market view
        }

        dbUtil = DatabaseUtil.getInstance(this);

        btnBack.setOnClickListener(v -> {
            Intent backIntent = new Intent(NotificationsActivity.this, ListStocks.class);
            backIntent.putExtra("view", viewType);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(backIntent);
            finish();
        });

        loadNotifications();
    }

    private void loadNotifications() {
        List<MarketEvent> notifications = dbUtil.getMarketEvents();

        if (notifications == null || notifications.isEmpty()) {
            lblEmpty.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            lblEmpty.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);

            Collections.reverse(notifications);

            NotificationAdapter adapter = new NotificationAdapter(this, notifications);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            rvNotifications.setLayoutManager(layoutManager);
            rvNotifications.setAdapter(adapter);
        }
    }
}