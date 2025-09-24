package com.example.wallstreettycoon.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.portfolio.PortfolioStock;
import com.example.wallstreettycoon.transaction.Transaction;
import com.example.wallstreettycoon.transaction.TransactionsAdapter;
import com.example.wallstreettycoon.useraccount.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class GameProfile extends AppCompatActivity {

    private DatabaseUtil dbUtil;
    private String username;
    private TransactionsAdapter transactionsAdapter;

    private TextView tvUsername, tvTotalPortfolioValue, tvProfitLoss, tvProfitLossPercentage, tvEmptyHoldings, tvEmptyTransactions;
    private PieChart pieChart;
    private RecyclerView recyclerTransactions;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_profile);

        dbUtil = new DatabaseUtil(this);

        // Get username
        username = getIntent().getStringExtra("username");
        if (username == null) {
            username = Game.currentUser.getUserUsername();
        }

        initialiseViews();
        loadProfileData();
        setupEventListeners();
    }

    private void initialiseViews() {
        tvUsername = findViewById(R.id.usernameprofile);
        tvTotalPortfolioValue = findViewById(R.id.total_portfolio_value);
        tvProfitLoss = findViewById(R.id.profit_loss);
        tvProfitLossPercentage = findViewById(R.id.profit_loss_percentage);
        pieChart = findViewById(R.id.pie_chart_holdings);
        recyclerTransactions = findViewById(R.id.recycler_transactions);
        tvEmptyHoldings = findViewById(R.id.empty_holdings_text);
        tvEmptyTransactions = findViewById(R.id.empty_transactions_text);
        backButton = findViewById(R.id.backButton);

        recyclerTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionsAdapter = new TransactionsAdapter(new ArrayList<>(), this);
        recyclerTransactions.setAdapter(transactionsAdapter);
        recyclerTransactions.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void setupEventListeners() {
        backButton.setOnClickListener(v -> {
            String viewType = getIntent().getStringExtra("viewType");

            Intent intent = new Intent(this, com.example.wallstreettycoon.dashboard.ListStocks.class);
            intent.putExtra("view", viewType);
            intent.putExtra("username", username);

            startActivity(intent);
            finish();
        });
    }

    private void loadProfileData() {
        tvUsername.setText(username + "'s Portfolio");

        // Load user data
        User user = dbUtil.getUser(username);

        // Load portfolio holdings
        List<PortfolioStock> holdings = dbUtil.getPortfolio(username);

        // Load transaction history
        List<Transaction> transactions = dbUtil.getTransactionHistory(username);

        // Update UI with data
        loadPortfolioSummary(user, holdings);
        loadPieChart(holdings);
        loadTransactionHistory(transactions);
    }

    private void loadPortfolioSummary(User user, List<PortfolioStock> holdings) {
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;

        // Calculate current portfolio value using current stock prices
        for (PortfolioStock ps : holdings) {
            BigDecimal invested = BigDecimal.valueOf(ps.getBuyPrice())
                    .multiply(BigDecimal.valueOf(ps.getQuantity()));
            totalInvested = totalInvested.add(invested);

            //FIXME Calculate cost
            double currentPrice = ps.getBuyPrice(); //dbUtil.getCurrentStockPrice(ps.getStock().getStockID(), Game.currentTimeStamp);
            BigDecimal currentStockValue = BigDecimal.valueOf(currentPrice)
                    .multiply(BigDecimal.valueOf(ps.getQuantity()));
            currentValue = currentValue.add(currentStockValue);
        }

        BigDecimal cashBalance = BigDecimal.valueOf(user.getUserBalance());
        BigDecimal totalPortfolioValue = currentValue.add(cashBalance);

        // Calculate profit/loss
        BigDecimal profitLoss = currentValue.subtract(totalInvested);
        BigDecimal profitLossPercentage = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            profitLossPercentage = profitLoss.divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        //Update UI
        tvTotalPortfolioValue.setText(String.format("$%,.2f", totalPortfolioValue.doubleValue()));

        if (profitLoss.compareTo(BigDecimal.ZERO) >= 0) {
            tvProfitLoss.setText(String.format("+$%,.2f", profitLoss.doubleValue()));
            tvProfitLoss.setTextColor(getColor(R.color.Green));
            tvProfitLossPercentage.setText(String.format("(+%.2f%%)", profitLossPercentage.doubleValue()));
            tvProfitLossPercentage.setTextColor(getColor(R.color.Green));
        } else {
            tvProfitLoss.setText(String.format("-$%,.2f", Math.abs(profitLoss.doubleValue())));
            tvProfitLoss.setTextColor(getColor(R.color.Red));
            tvProfitLossPercentage.setText(String.format("(%.2f%%)", profitLossPercentage.doubleValue()));
            tvProfitLossPercentage.setTextColor(getColor(R.color.Red));
        }
    }

    private void loadPieChart(List<PortfolioStock> holdings) { // https://github.com/PhilJay/MPAndroidChart
        if (holdings.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            tvEmptyHoldings.setVisibility(View.VISIBLE);
            tvEmptyHoldings.setText("No holdings yet. Start investing!");
            tvEmptyHoldings.setTextColor(getColor(R.color.LightGrey));
            return;
        }

        pieChart.setVisibility(View.VISIBLE);
        tvEmptyHoldings.setVisibility(View.GONE);

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        // Predefined colors for the chart
        int[] chartColors = new int[]{
                Color.parseColor("#4CAF50"), // Green
                Color.parseColor("#2196F3"), // Blue
                Color.parseColor("#FFC107"), // Yellow
                Color.parseColor("#FF5722"), // Orange
                Color.parseColor("#9C27B0"), // Purple
                Color.parseColor("#E91E63"), // Pink
                Color.parseColor("#607D8B"), // Gray-blue
                Color.parseColor("#CDDC39")  // Lime
        };

        int colorIndex = 0;
        for (PortfolioStock ps : holdings) {
            float value = ps.getQuantity() * (float) ps.getBuyPrice();
            entries.add(new PieEntry(value, ps.getStock().getSymbol()));
            colors.add(chartColors[colorIndex % chartColors.length]);
            colorIndex++;
        }

        // Dataset
        PieDataSet dataSet = new PieDataSet(entries, "Holdings");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueFormatter(new PercentFormatter(pieChart));

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextColor(Color.BLACK);

        // General pie chart config code
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.parseColor("#20000000"));
        pieChart.setTransparentCircleRadius(62f);
        pieChart.setHoleRadius(60f);
        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateY(1000);

        // Create legend (category, %)
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
        legend.setTextSize(10f);
        legend.setForm(Legend.LegendForm.CIRCLE);

        pieChart.invalidate();
    }

    private void loadTransactionHistory(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            recyclerTransactions.setVisibility(View.GONE);
            tvEmptyTransactions.setVisibility(View.VISIBLE);
        } else {
            recyclerTransactions.setVisibility(View.VISIBLE);
            tvEmptyTransactions.setVisibility(View.GONE);
            transactionsAdapter.updateData(transactions);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data
        loadProfileData();
    }

}