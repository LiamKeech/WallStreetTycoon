package com.example.wallstreettycoon.displayBuySell;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wallstreettycoon.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;

public class DisplayStockActivity extends AppCompatActivity {
    private LineChart chart;
    private List<Entry> initialData; //TODO: should be loaded from DB. Initial 1 month data of stock

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_stock);

        chart = findViewById(R.id.stockChart);

        // Load from DB
        initialData = new ArrayList<>();
        generateInitialData();

        // Time range
        findViewById(R.id.btn1D).setOnClickListener(v -> updateChart("1D"));
        findViewById(R.id.btn1W).setOnClickListener(v -> updateChart("1W"));
        findViewById(R.id.btn1M).setOnClickListener(v -> updateChart("1M")); // Regen/revert to initial

        // Buy and sell
        //TODO: Fragment should be in landscape orientation
        findViewById(R.id.btnBuy).setOnClickListener(v -> {
            BuyDialogFragment buyDialog = new BuyDialogFragment();
            buyDialog.show(getSupportFragmentManager(), "BuyDialog");
        });

        findViewById(R.id.btnSell).setOnClickListener(v -> {
            SellDialogFragment sellDialog = new SellDialogFragment();
            sellDialog.show(getSupportFragmentManager(), "SellDialog");
        });
    }

    private void generateInitialData() {//TODO: Should replace this with DB data, just for testing
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            entries.add(new Entry(i, 70f + (float)(Math.random() * 15)));
        }
        initialData.addAll(entries);

        LineDataSet dataSet = new LineDataSet(entries, "Price History");
        dataSet.setColor(android.graphics.Color.BLUE);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(60f);
        chart.getAxisRight().setEnabled(false);
        chart.getXAxis().setEnabled(true);
        chart.invalidate();
    }

    private void updateChart(String range) {//Updates chart scale based on time range button selection, 1M is standard and user can then pick 1D or 1W
        List<Entry> entries = new ArrayList<>();

        if ("1D".equals(range)) { //TODO: Should replace this with DB data
            entries.add(new Entry(0, 70f));
            entries.add(new Entry(1, 70.5f));
            entries.add(new Entry(2, 69.8f));
        } else if ("1W".equals(range)) {
            for (int i = 0; i < 7; i++) {
                entries.add(new Entry(i, 70f + i + (float)(Math.random() * 2)));
            }
        } else if ("1M".equals(range)) {
            entries.clear();
            entries.addAll(initialData);
        }

        //Plotting a line
        LineDataSet dataSet = new LineDataSet(entries, "Price History");
        dataSet.setColor(android.graphics.Color.BLUE);
        dataSet.setDrawValues(false);
        dataSet.setLineWidth(2f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }
}