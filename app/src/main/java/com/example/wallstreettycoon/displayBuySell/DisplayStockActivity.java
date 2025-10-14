package com.example.wallstreettycoon.displayBuySell;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.dashboard.ListStocks;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.model.GameEvent;
import com.example.wallstreettycoon.model.GameEventType;
import com.example.wallstreettycoon.model.GameObserver;
import com.example.wallstreettycoon.stock.Stock;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Description;

import java.util.ArrayList;
import java.util.List;

public class DisplayStockActivity extends AppCompatActivity implements GameObserver {

    private LineChart chart;
    private DatabaseUtil dbUtil;
    private Stock currentStock;
    private String currentUsername;
    private String viewType;
    Context context = this;

    private String currentTimeRange = "1M";
    private TextView currentPriceTextView;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private Typeface juaTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_stock);

        juaTypeface = ResourcesCompat.getFont(this, R.font.jua);

        Game.getInstance().addObserver(this);
        dbUtil = DatabaseUtil.getInstance(context);

        Intent intentFromList = getIntent();
        int stockID = intentFromList.getIntExtra("stock_id", -1);
        viewType = intentFromList.getStringExtra("view");
        currentUsername = Game.currentUser.getUserUsername();

        if (stockID == -1) {
            Log.e("DisplayStock", "No stock ID provided in intent");
            finish();
            return;
        }

        if (currentUsername == null) {
            Log.e("DisplayStock", "No username provided in intent");
            currentUsername = "admin";
        }

        Log.d("DisplayStock", "Intent received: stockID=" + stockID + ", username=" + currentUsername);

        currentStock = dbUtil.getStock(stockID);
        if (currentStock == null) {
            Log.e("DisplayStock", "Stock not found for ID: " + stockID);
            finish();
            return;
        }

        Log.d("DisplayStock", "Stock loaded: " + currentStock.getStockName());

        // Get UI
        initialiseUI();

        // Load data
        updateChart("1M");
    }

    private void initialiseUI() {
        chart = findViewById(R.id.stockChart);

        TextView stockName = findViewById(R.id.stockNameHeader);
        TextView stockSymbol = findViewById(R.id.stockSymbolValue);
        currentPriceTextView = findViewById(R.id.currentPriceValue);
        TextView description = findViewById(R.id.stockDescription);
        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(DisplayStockActivity.this, ListStocks.class);
            intent.putExtra("username", currentUsername);
            intent.putExtra("view", viewType);
            startActivity(intent);
        });

        if (stockName != null) {
            stockName.setText("Viewing " + currentStock.getStockName());
        }
        if (stockSymbol != null) {
            stockSymbol.setText(currentStock.getSymbol());
        }

        updatePriceDisplay();

        if (description != null) {
            description.setText(currentStock.getDescription());
        }

        Button btn1D = findViewById(R.id.btn1D);
        Button btn1W = findViewById(R.id.btn1W);
        Button btn1M = findViewById(R.id.btn1M);

        btn1M.setSelected(true);
        updateButtonState(btn1M, true);
        updateButtonState(btn1D, false);
        updateButtonState(btn1W, false);

        btn1D.setOnClickListener(v -> {
            currentTimeRange = "1D";
            updateChart("1D");
            updateButtonState(btn1D, true);
            updateButtonState(btn1W, false);
            updateButtonState(btn1M, false);
        });

        btn1W.setOnClickListener(v -> {
            currentTimeRange = "1W";
            updateChart("1W");
            updateButtonState(btn1D, false);
            updateButtonState(btn1W, true);
            updateButtonState(btn1M, false);
        });

        btn1M.setOnClickListener(v -> {
            currentTimeRange = "1M";
            updateChart("1M");
            updateButtonState(btn1D, false);
            updateButtonState(btn1W, false);
            updateButtonState(btn1M, true);
        });

        findViewById(R.id.btnBuy).setOnClickListener(v -> {
            BuyDialogFragment buyDialog = new BuyDialogFragment();

            // Get current price from price history
            double currentPrice = getCurrentPrice();

            Bundle args = new Bundle();
            args.putInt("stockID", currentStock.getStockID());
            args.putString("stockSymbol", currentStock.getSymbol());
            args.putDouble("currentPrice", currentPrice);
            args.putString("username", currentUsername);

            buyDialog.setArguments(args);
            buyDialog.show(getSupportFragmentManager(), "BuyDialog");
        });

        findViewById(R.id.btnSell).setOnClickListener(v -> {
            SellDialogFragment sellDialog = new SellDialogFragment();

            // Get current price from price history
            double currentPrice = getCurrentPrice();

            Bundle args = new Bundle();
            args.putInt("stockID", currentStock.getStockID());
            args.putString("stockSymbol", currentStock.getSymbol());
            args.putDouble("currentPrice", currentPrice);
            args.putString("username", currentUsername);

            sellDialog.setArguments(args);
            sellDialog.show(getSupportFragmentManager(), "SellDialog");
        });
    }

    private void updateButtonState(Button button, boolean isSelected) {
        if (isSelected) {
            button.setBackground(getDrawable(R.drawable.button_background_lightblue_small));
            int padding = (int) (8 * getResources().getDisplayMetrics().density);
            button.setPadding(padding * 2, padding, padding * 2, padding);
        } else {
            button.setBackground(getDrawable(R.drawable.button_background_orange_small));
            int padding = (int) (8 * getResources().getDisplayMetrics().density);
            button.setPadding(padding * 2, padding, padding * 2, padding);
        }
    }

    private double getCurrentPrice() {
        double[] priceHistory = currentStock.getPriceHistoryArray();

        if (priceHistory != null && priceHistory.length > 0) {
            double price = priceHistory[priceHistory.length - 1];
            return Math.max(0, price);
        } else {
            // Fallback: use currentPrice directly
            Log.w("DisplayStock", "Price history unavailable, using current price");
            Double price = currentStock.getCurrentPrice();
            return (price != null) ? Math.max(0, price) : 0.0;
        }
    }

    private void updatePriceDisplay() {
        if (currentPriceTextView != null) {
            double currentPriceValue = getCurrentPrice();
            currentPriceTextView.setText(String.format("$%.2f", currentPriceValue));
            Log.d("DisplayStock", "Updated price display: $" + String.format("%.2f", currentPriceValue));
        }
    }

    private void updateChart(String range) {
        int timeRange;
        String chartTitle;
        switch (range) {
            case "1D":
                timeRange = 1;
                chartTitle = currentStock.getSymbol() + " - 1 Day";
                break;
            case "1W":
                timeRange = 7;
                chartTitle = currentStock.getSymbol() + " - 1 Week";
                break;
            case "1M":
            default:
                timeRange = 30;
                chartTitle = currentStock.getSymbol() + " - 1 Month";
                break;
        }

        Description description = chart.getDescription();
        description.setText(chartTitle);
        description.setEnabled(true);
        description.setTextSize(14f);
        description.setTextColor(getColor(R.color.black));
        description.setPosition(0, 0);
        if (juaTypeface != null) {
            description.setTypeface(juaTypeface);
        }

        // Load price history from priceHistory array
        List<Entry> entries = getPriceHistoryFromArray(timeRange);

        Log.d("DisplayStock", "Generated " + entries.size() + " entries for chart");

        if (entries.isEmpty()) {
            Log.e("DisplayStock", "No chart data available");
            return;
        }

        // Add styling to line chart
        LineDataSet dataSet = new LineDataSet(entries, "Price");

        // Line styling
        dataSet.setColor(getColor(R.color.LightBlue));
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(getColor(R.color.LightBlue));
        dataSet.setCircleRadius(4f);
        dataSet.setCircleHoleRadius(2f);
        dataSet.setCircleHoleColor(getColor(R.color.white));

        // Fill the area under line
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(getColor(R.color.LightBlue));
        dataSet.setFillAlpha(50);

        // Highlights a value on touch
        dataSet.setHighLightColor(getColor(R.color.Orange));
        dataSet.setHighlightLineWidth(2f);
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setDrawValues(false);

        // Line chart data
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        // X-axis with jua font
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(getColor(R.color.Grey));
        xAxis.setTextColor(android.graphics.Color.DKGRAY);
        xAxis.setTextSize(10f);
        if (juaTypeface != null) {
            xAxis.setTypeface(juaTypeface);
        }

        // X-axis labels based on time range
        if (timeRange <= 7) {
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(Math.min(timeRange + 1, 8));
        } else {
            xAxis.setGranularity(5f);
            xAxis.setLabelCount(7);
        }

        // Y-axis with jua font
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(true);
        yAxis.setDrawGridLines(true);
        yAxis.setGridColor(getColor(R.color.Grey));
        yAxis.setTextColor(android.graphics.Color.DKGRAY);
        yAxis.setTextSize(10f);
        yAxis.setAxisMinimum(0f);
        if (juaTypeface != null) {
            yAxis.setTypeface(juaTypeface);
        }

        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);

        // Touch interactions
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setDoubleTapToZoomEnabled(true);

        // Highlight values on touch
        chart.setHighlightPerTapEnabled(true);
        chart.setHighlightPerDragEnabled(false);

        // Animate the graph
        chart.animateX(1000);

        // Refresh chart
        chart.invalidate();
    }

    private List<Entry> getPriceHistoryFromArray(int daysBack) {
        List<Entry> entries = new ArrayList<>();

        double[] priceHistory = currentStock.getPriceHistoryArray();

        if (priceHistory == null || priceHistory.length == 0) {
            Log.e("DisplayStock", "Price history array is null or empty");
            return entries;
        }

        int historyLength = priceHistory.length;
        Log.d("DisplayStock", "Price history length: " + historyLength + ", requesting last " + daysBack + " days");

        // Calculate starting index - get the last 'daysBack' entries
        int startIndex = Math.max(0, historyLength - daysBack - 1);
        int endIndex = historyLength - 1;

        // Build entries from the price history
        int entryIndex = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            double price = priceHistory[i];
            // Ensure no negative prices appear on chart
            price = Math.max(0, price);
            entries.add(new Entry(entryIndex, (float) price));
            entryIndex++;
        }

        if (entries.size() > 0) {
            float lastPrice = entries.get(entries.size() - 1).getY();
            Log.d("DisplayStock", "Chart entries: " + entries.size() + ", last price: $" + String.format("%.2f", lastPrice));
        }

        return entries;
    }

    @Override
    public void onGameEvent(GameEvent event) {
        // Update the chart and current price when stock prices update
        if (event.getType() == GameEventType.UPDATE_STOCK_PRICE) {
            // Run on UI thread
            uiHandler.post(() -> {
                Log.d("DisplayStock", "Updating chart due to price update event");

                // Reload the stock to get updated price history
                currentStock = dbUtil.getStock(currentStock.getStockID());

                if (currentStock != null) {
                    // Update the current price display
                    updatePriceDisplay();

                    // Refresh the chart with current time range
                    updateChart(currentTimeRange);
                } else {
                    Log.e("DisplayStock", "Failed to reload stock data");
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Game.getInstance().removeObserver(this);
    }
}