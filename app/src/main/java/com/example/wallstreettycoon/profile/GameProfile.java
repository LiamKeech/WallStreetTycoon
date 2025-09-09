package com.example.wallstreettycoon.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.Game;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.portfolio.PortfolioStock;
import com.example.wallstreettycoon.transaction.Transaction;
import com.example.wallstreettycoon.transaction.TransactionsAdapter;
import com.example.wallstreettycoon.useraccount.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameProfile extends AppCompatActivity {

    private TextView tvUsername, tvTotalValue, tvProfitLoss, tvProfitLossPercent, tvEmptyHoldings, tvEmptyTransactions;
    private PieChart pieChart;
    private RecyclerView rvTransactions;
    private ImageButton backButton;

    private DatabaseUtil dbUtil;
    private String currentUsername;
    private TransactionsAdapter transactionsAdapter;
    private List<Transaction> transactionsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_profile);

        // Get username from intent
        currentUsername = getIntent().getStringExtra("username");

        // Init db and UI
        dbUtil = new DatabaseUtil(this);
        InitialiseUI();
        setupEventListeners();
        loadProfileData();
    }

    private void InitialiseUI() {
        backButton = findViewById(R.id.backButton);
        tvUsername = findViewById(R.id.usernameprofile);
        tvTotalValue = findViewById(R.id.total_portfolio_value);
        tvProfitLoss = findViewById(R.id.profit_loss);
        tvProfitLossPercent = findViewById(R.id.profit_loss_percentage);
        pieChart = findViewById(R.id.pie_chart_holdings);
        tvEmptyHoldings = findViewById(R.id.empty_holdings_text);
        rvTransactions = findViewById(R.id.recycler_transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        tvEmptyTransactions = findViewById(R.id.empty_transactions_text);

        // Empty transaction list and adapter
        transactionsList = new ArrayList<>();
        transactionsAdapter = new TransactionsAdapter(transactionsList);
        rvTransactions.setAdapter(transactionsAdapter);

        // Set username
        tvUsername.setText(currentUsername + " Profile");
    }

    private void setupEventListeners() {
        backButton.setOnClickListener(v -> {
            String viewType = getIntent().getStringExtra("viewType");

            Intent intent = new Intent(this, com.example.wallstreettycoon.dashboard.ListStocks.class);
            intent.putExtra("view", viewType);
            intent.putExtra("username", currentUsername);

            startActivity(intent);
            finish();
        });
    }

    private void loadProfileData() {
        try {
            loadPortfolioSummary();
            loadPieChart();
            loadTransactionHistory();
        } catch (Exception e) {
            Log.e("GameProfile", "Error loading profile data", e);
        }
    }

    private void loadPortfolioSummary() {
        User user = dbUtil.getUser(currentUsername);
        List<PortfolioStock> portfolioStocks = dbUtil.getPortfolio(currentUsername);

        if (user == null) {
            tvTotalValue.setText("$0.00");
            tvProfitLoss.setText("$0.00");
            tvProfitLossPercent.setText("(0.0%)");
            return;
        }

        double cashBalance = user.getUserBalance();
        double totalInvestmentValue = 0.0;
        double totalCost = 0.0;

        // Calculate current portfolio value using current stock prices
        for (PortfolioStock ps : portfolioStocks) {
            // Get current price
            double currentPrice = dbUtil.getCurrentStockPrice(ps.getStock().getStockID(), 1);
            double positionValue = currentPrice * ps.getQuantity();
            totalInvestmentValue += positionValue;

            // Calculate cost
            double positionCost = ps.getBuyPrice() * ps.getQuantity();
            totalCost += positionCost;
        }

        double totalPortfolioValue = cashBalance + totalInvestmentValue;
        double profitLoss = totalInvestmentValue - totalCost;

        double profitLossPercent;
        if (totalCost > 0) {
            profitLossPercent = (profitLoss / totalCost) * 100;
        } else {
            profitLossPercent = 0.0;
        }

        //Update UI
        tvTotalValue.setText(String.format("$%.2f", totalPortfolioValue));
        if (profitLoss >= 0) {
            tvProfitLoss.setText(String.format("+$%.2f", profitLoss));
            tvProfitLoss.setTextColor(getResources().getColor(R.color.Green));
            tvProfitLossPercent.setText(String.format("(+%.1f%%)", profitLossPercent));
            tvProfitLossPercent.setTextColor(getResources().getColor(R.color.Green));
        } else {
            tvProfitLoss.setText(String.format("-$%.2f", Math.abs(profitLoss)));
            tvProfitLoss.setTextColor(getResources().getColor(R.color.Red));
            tvProfitLossPercent.setText(String.format("(%.1f%%)", profitLossPercent));
            tvProfitLossPercent.setTextColor(getResources().getColor(R.color.Red));
        }
    }

    private void loadPieChart() { // https://github.com/PhilJay/MPAndroidChart
        List<PortfolioStock> portfolio = dbUtil.getPortfolio(currentUsername);

        if (portfolio.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            tvEmptyHoldings.setVisibility(View.VISIBLE);
            return;
        }

        pieChart.setVisibility(View.VISIBLE);
        tvEmptyHoldings.setVisibility(View.GONE);


        // Create a hash map of category/stock pairs to categorise pie chart holdings
        Map<String, Double> categoryValues = new HashMap<>();

        for (PortfolioStock ps : portfolio) {
            String category = ps.getStock().getCategory();
            double currentPrice = dbUtil.getCurrentStockPrice(ps.getStock().getStockID(), 100);
            double value = currentPrice * ps.getQuantity();

            categoryValues.put(category, categoryValues.getOrDefault(category, 0.0) + value);

            Log.d("PieChart", "Category: " + category + ", Value: $" + value);
        }

        // Create pie chart entries
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryValues.entrySet()) {
            float value = entry.getValue().floatValue();
            String category = entry.getKey();
            entries.add(new PieEntry(value, category));
            Log.d("PieChart", "Adding entry: " + category + " = $" + value);
        }

        if (entries.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            tvEmptyHoldings.setVisibility(View.VISIBLE);
            return;
        }

        // Dataset
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));
        dataSet.setSliceSpace(2f);

        PieData pieData = new PieData(dataSet);

        // General pie chart config code
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.BLUE);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.setDrawCenterText(false);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawEntryLabels(false);

        // Create legend (category, %)
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextSize(12f);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getLegend().setHorizontalAlignment(com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER);
        pieChart.getLegend().setVerticalAlignment(com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM);
        pieChart.getLegend().setOrientation(com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL);


        pieChart.animateY(1000);

        // Refresh
        pieChart.invalidate();
    }

    private void loadTransactionHistory() {
        // For now, show empty state
        transactionsList.clear();

        if (transactionsList.isEmpty()) {
            rvTransactions.setVisibility(View.GONE);
            tvEmptyTransactions.setVisibility(View.VISIBLE);
        } else {
            rvTransactions.setVisibility(View.VISIBLE);
            tvEmptyTransactions.setVisibility(View.GONE);
            transactionsAdapter.updateData(transactionsList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data
        loadProfileData();
    }

}