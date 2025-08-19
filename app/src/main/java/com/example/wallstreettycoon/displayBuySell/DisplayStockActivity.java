package com.example.wallstreettycoon.displayBuySell;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wallstreettycoon.Game;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.dashboard.ListStocks;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.stock.Stock;
import com.example.wallstreettycoon.stock.StockPriceFunction;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import java.util.ArrayList;

import java.util.List;

public class DisplayStockActivity extends AppCompatActivity {

    private LineChart chart;
    private DatabaseUtil dbUtil;
    private Stock currentStock;
    private String currentUsername;
    Context context = this;
    Game game = new Game(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_stock);

        dbUtil = new DatabaseUtil(context);

        Intent intentFromList = getIntent();
        int stockID = intentFromList.getIntExtra("stock_id", -1); // Use -1 as default
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
            finish(); // Close if stock not found
        }

        Log.d("DisplayStock", "Stock loaded: " + currentStock.getStockName());


        //get UI
        initialiseUI();

        //load data
        updateChart("1M");
    }

    private void initialiseUI() {
        chart = findViewById(R.id.stockChart);

        TextView stockName = findViewById(R.id.stockNameHeader);
        TextView stockSymbol = findViewById(R.id.stockSymbolValue);
        TextView currentPrice = findViewById(R.id.currentPriceValue);
        EditText description = findViewById(R.id.stockDescription);
        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(DisplayStockActivity.this, ListStocks.class);
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        });

        if (stockName != null) {
            stockName.setText("Viewing " + currentStock.getStockName());
        }
        if (stockSymbol != null) {
            stockSymbol.setText(currentStock.getSymbol());
        }

        int currentTime = 1; //FIXME

        double currentPriceValue = dbUtil.getCurrentStockPrice(currentStock.getStockID(), currentTime);
        if (currentPrice != null) {
            currentPrice.setText(String.format("$%.2f", currentPriceValue));
        }

        if (description != null) {
            description.setText(currentStock.getDescription());
        }

        Button btn1D = findViewById(R.id.btn1D);
        Button btn1W = findViewById(R.id.btn1W);
        Button btn1M = findViewById(R.id.btn1M);

        btn1M.setBackgroundTintList(getColorStateList(R.color.LightBlue)); // Default selected

        btn1D.setOnClickListener(v -> {
            updateChart("1D");
            btn1D.setBackgroundTintList(getColorStateList(R.color.LightBlue));
            btn1W.setBackgroundTintList(getColorStateList(R.color.Orange));
            btn1M.setBackgroundTintList(getColorStateList(R.color.Orange));
        });

        btn1W.setOnClickListener(v -> {
            updateChart("1W");
            btn1D.setBackgroundTintList(getColorStateList(R.color.Orange));
            btn1W.setBackgroundTintList(getColorStateList(R.color.LightBlue));
            btn1M.setBackgroundTintList(getColorStateList(R.color.Orange));
        });

        btn1M.setOnClickListener(v -> {
            updateChart("1M");
            btn1D.setBackgroundTintList(getColorStateList(R.color.Orange));
            btn1W.setBackgroundTintList(getColorStateList(R.color.Orange));
            btn1M.setBackgroundTintList(getColorStateList(R.color.LightBlue));
        });

        findViewById(R.id.btnBuy).setOnClickListener(v -> {
            BuyDialogFragment buyDialog = new BuyDialogFragment();

            Bundle args = new Bundle(); //to communicate with a dialog fragment
            args.putInt("stockID", currentStock.getStockID());
            args.putString("stockSymbol", currentStock.getSymbol());
            args.putDouble("currentPrice", dbUtil.getCurrentStockPrice(currentStock.getStockID(), 1));
            args.putString("username", currentUsername);

            buyDialog.setArguments(args);
            buyDialog.show(getSupportFragmentManager(), "BuyDialog");
        });

        findViewById(R.id.btnSell).setOnClickListener(v -> {
            SellDialogFragment sellDialog = new SellDialogFragment();

            Bundle args = new Bundle();
            args.putInt("stockID", currentStock.getStockID());
            args.putString("stockSymbol", currentStock.getSymbol());
            args.putDouble("currentPrice", dbUtil.getCurrentStockPrice(currentStock.getStockID(), 1));
            args.putString("username", currentUsername);

            sellDialog.setArguments(args);
            sellDialog.show(getSupportFragmentManager(), "SellDialog");
        });
    }

    // https://github.com/PhilJay/MPAndroidChart
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

        // Chart title
        chart.getDescription().setText(chartTitle);
        chart.getDescription().setEnabled(true);
        chart.getDescription().setTextSize(16f);
        chart.getDescription().setTextColor(getColor(R.color.black));

        // Load price history
        List<Entry> entries = getPriceHistory(currentStock.getStockID(), timeRange);
        Log.d("DisplayStock", "Generated " + entries.size() + " entries for chart");

        // Add styling to line chart
        LineDataSet dataSet = new LineDataSet(entries, "Price");

        // Line styling
        dataSet.setColor(getColor(R.color.LightBlue));
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(true); // draws out data points
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

        // X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(getColor(R.color.Grey));
        xAxis.setTextColor(android.graphics.Color.DKGRAY); //getColor(R.color.black)
        xAxis.setTextSize(10f);

        // X-axis labels based on time range
        if (timeRange <= 7) { // More than 1 week
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(Math.min(timeRange + 1, 8));
        } else {
            // show fewer labels
            xAxis.setGranularity(5f);
            xAxis.setLabelCount(7);
        }

        // Y-axis
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setEnabled(true);
        yAxis.setDrawGridLines(true);
        yAxis.setGridColor(getColor(R.color.Grey));
        yAxis.setTextColor(android.graphics.Color.DKGRAY); //getColor(R.color.black)
        yAxis.setTextSize(10f);
        yAxis.setAxisMinimum(0f);

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

        // Draws out the graph each time
        chart.animateX(1000);

        // Refresh chart
        chart.invalidate();
    }

    public List<Entry> getPriceHistory(int stockID, int daysBack) {
        List<Entry> entries = new ArrayList<>();
        StockPriceFunction func = dbUtil.getStockPriceFunction(stockID);
        if (func == null) return entries;

        for (int t = 0; t <= daysBack; t++) {
            double price = func.getCurrentPrice(t);
            entries.add(new Entry(t, (float) price));
        }

        return entries;
    }
}
