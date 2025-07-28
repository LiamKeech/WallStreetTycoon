package com.example.wallstreettycoon.displayBuySell;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.stock.Stock;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;

import java.util.List;

public class DisplayStockActivity extends AppCompatActivity {
    private LineChart chart;
    private DatabaseUtil dbUtil;
    private Stock currentStock;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_stock);

        dbUtil = new DatabaseUtil(this);

        Intent intentFromList = getIntent();
        int stockID = intentFromList.getIntExtra("stockID", 1);
        currentUsername = intentFromList.getStringExtra("username");

        currentStock = dbUtil.getStock(stockID);
        if (currentStock == null) {
            finish(); //close if stock not found
        }

        //get UI
        initialiseUI();

        //load data


    }

    private void initialiseUI() {
        chart = findViewById(R.id.stockChart);

        TextView stockName = findViewById(R.id.stockNameHeader);
        TextView stockSymbol = findViewById(R.id.stockSymbolValue);
        TextView currentPrice = findViewById(R.id.currentPriceValue);

        if (stockName != null) {
            stockName.setText("Viewing " + currentStock.getStockName());
        }
        if (stockSymbol != null) {
            stockSymbol.setText(currentStock.getSymbol());
        }

        int currentTime = 0; //FIXME
        double currentPriceValue = dbUtil.getCurrentStockPrice(currentStock.getStockID(), currentTime);
        if (currentPrice != null) {
            currentPrice.setText(String.format("$.2f", currentPriceValue));
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
            buyDialog.show(getSupportFragmentManager(), "BuyDialog");
        });

        findViewById(R.id.btnSell).setOnClickListener(v -> {
            SellDialogFragment sellDialog = new SellDialogFragment();
            sellDialog.show(getSupportFragmentManager(), "SellDialog");
        });
    }

    private void updateChart(String range) {
        int timeRange;
        switch (range) {
            case "1D":
                timeRange = 1;
                break;
            case "1W":
                timeRange = 7;
                break;
            case "1M":
            default:
                timeRange = 30;
                break;
        }

//        List<Entry> entries = dbUtil.generatePriceHistory(currentStock.getStockID(), timeRange);
//
//        LineDataSet dataSet = new LineDataSet(entries, "Price History");
//        dataSet.setColor(android.graphics.Color.BLUE);
//        dataSet.setDrawValues(false);
//        dataSet.setLineWidth(2f);
//        dataSet.setDrawCircles(false);
//
//        LineData lineData = new LineData(dataSet);
//        chart.setData(lineData);
//        chart.getDescription().setEnabled(false);
//        chart.getAxisRight().setEnabled(false);
//        chart.getXAxis().setEnabled(true);
//        chart.getLegend().setEnabled(false);
//        chart.getAxisLeft().setAxisMinimum(0f);
//        chart.invalidate();
    }
}
