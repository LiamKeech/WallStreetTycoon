package com.example.wallstreettycoon.profile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.model.GameEvent;
import com.example.wallstreettycoon.model.GameEventType;
import com.example.wallstreettycoon.model.GameObserver;
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
import java.util.Random;

public class GameProfile extends AppCompatActivity implements GameObserver {

    private DatabaseUtil dbUtil;
    private String username;
    private TransactionsAdapter transactionsAdapter;

    private TextView tvUsername, tvTotalPortfolioValue, tvProfitLoss, tvEmptyHoldings, tvEmptyTransactions;
    private PieChart pieChart;
    private RecyclerView recyclerTransactions;
    private ImageButton backButton;

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_profile);

        Game.getInstance().addObserver(this);
        dbUtil = DatabaseUtil.getInstance(this); // SINGLETON FIX

        // Get username
        username = getIntent().getStringExtra("username");
        if (username == null) {
            username = Game.currentUser().getUserUsername();
        }

        initialiseViews();
        loadProfileData();
        setupEventListeners();
    }

    private void initialiseViews() {
        tvUsername = findViewById(R.id.usernameprofile);
        tvTotalPortfolioValue = findViewById(R.id.total_portfolio_value);
        tvProfitLoss = findViewById(R.id.profit_loss);
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

            double currentPrice = dbUtil.getCurrentStockPrice(ps.getStock().getStockID());
            BigDecimal currentStockValue = BigDecimal.valueOf(currentPrice)
                    .multiply(BigDecimal.valueOf(ps.getQuantity()));
            currentValue = currentValue.add(currentStockValue);
        }

        BigDecimal cashBalance = BigDecimal.valueOf(user.getUserBalance());
        BigDecimal totalPortfolioValue = currentValue.add(cashBalance);

        // Calculate profit/loss
        BigDecimal profitLoss = currentValue.subtract(totalInvested);
        double profitLossPercentage = 0.0;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            profitLossPercentage = profitLoss.divide(totalInvested, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        //Update UI
        tvTotalPortfolioValue.setText(dbUtil.parseDoubleToString(totalPortfolioValue.doubleValue()));

        if (profitLoss.compareTo(BigDecimal.ZERO) >= 0) {
            tvProfitLoss.setText(String.format("+$%.2f (+%.1f%%)", profitLoss.doubleValue(), profitLossPercentage));
            tvProfitLoss.setTextColor(getResources().getColor(R.color.Green));
        } else {
            tvProfitLoss.setText(String.format("$%.2f (%.1f%%)", profitLoss.doubleValue(), profitLossPercentage));
            tvProfitLoss.setTextColor(getResources().getColor(R.color.Red));
        }
    }

    private void loadPieChart(List<PortfolioStock> holdings) {
        if (holdings.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            tvEmptyHoldings.setVisibility(View.VISIBLE);
            return;
        }

        pieChart.setVisibility(View.VISIBLE);
        tvEmptyHoldings.setVisibility(View.GONE);

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        List<Integer> usedColors = new ArrayList<>(); // Track used colors

        double totalValue = 0.0;
        for (PortfolioStock ps : holdings) {
            double currentPrice = dbUtil.getCurrentStockPrice(ps.getStock().getStockID());
            totalValue += ps.getQuantity() * currentPrice;
        }

        for (PortfolioStock ps : holdings) {
            double currentPrice = dbUtil.getCurrentStockPrice(ps.getStock().getStockID());
            double stockValue = ps.getQuantity() * currentPrice;
            float percentage = (float) ((stockValue / totalValue) * 100);

            // Format label with percentage in brackets
            String label = ps.getStock().getSymbol() + " (" + String.format("%.1f%%", percentage) + ")";
            entries.add(new PieEntry(percentage, label));
            colors.add(getColourForStock(ps.getStock().getStockID(), usedColors)); // Pass usedColors
        }

        // Dataset
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(0f); // Hide percentage values on slices
        dataSet.setValueTextColor(Color.TRANSPARENT); // Ensure no text is visible
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(6f);
        dataSet.setValueTypeface(ResourcesCompat.getFont(this, R.font.jua));

        PieData pieData = new PieData(dataSet);

        // General pie chart config
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false); // Disable percentage display on chart
        pieChart.getDescription().setEnabled(false);
        pieChart.setEntryLabelColor(Color.TRANSPARENT); // Hide entry labels
        pieChart.setEntryLabelTextSize(0f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setDrawEntryLabels(false); // Ensure no labels on slices
        pieChart.setExtraOffsets(2f, 2f, 2f, 2f);
        pieChart.setEntryLabelTypeface(ResourcesCompat.getFont(this, R.font.jua));

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(6f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
        legend.setTextSize(14f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(10f);
        legend.setWordWrapEnabled(true);
        legend.setTypeface(ResourcesCompat.getFont(this, R.font.jua));

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


    private int getColourForStock(int stockId, List<Integer> usedColors) {
        int[] chartColors = new int[]{
                Color.parseColor("#4CAF50"),  // Material Green
                Color.parseColor("#2196F3"),  // Material Blue
                Color.parseColor("#FF9800"),  // Material Orange
                Color.parseColor("#9C27B0"),  // Material Purple
                Color.parseColor("#F44336"),  // Material Red
                Color.parseColor("#00BCD4"),  // Material Cyan
                Color.parseColor("#FFEB3B"),  // Material Yellow
                Color.parseColor("#795548"),  // Material Brown
                Color.parseColor("#607D8B"),  // Material Blue Grey
                Color.parseColor("#E91E63")   // Material Pink
        };


        if (stockId < chartColors.length && !usedColors.contains(chartColors[stockId])) {
            usedColors.add(chartColors[stockId]);
            return chartColors[stockId];
        }

        for (int color : chartColors) {
            if (!usedColors.contains(color)) {
                usedColors.add(color);
                return color;
            }
        }

        // Fallback
        Random random = new Random(stockId);
        int newColor = Color.rgb(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );
        usedColors.add(newColor);
        return newColor;
    }

    public void onGameEvent(GameEvent event) {
        // Update portfolio values when stock prices update
        if (event.getType() == GameEventType.UPDATE_STOCK_PRICE) {
            uiHandler.post(() -> {
                loadProfileData();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Game.getInstance().removeObserver(this);
    }
}