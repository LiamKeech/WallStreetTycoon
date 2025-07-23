package com.example.wallstreettycoon.displayBuySell;

import android.os.Bundle;
import android.widget.Button;

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

        Button btn1D = findViewById(R.id.btn1D);
        Button btn1W = findViewById(R.id.btn1W);
        Button btn1M = findViewById(R.id.btn1M);

        // Set initial colors (e.g., 1M as default selected)
        btn1M.setBackgroundTintList(getColorStateList(R.color.LightBlue));
        btn1D.setBackgroundTintList(getColorStateList(R.color.Orange));
        btn1W.setBackgroundTintList(getColorStateList(R.color.Orange));

        // Time range button listeners with color changes
        btn1D.setOnClickListener(v -> {
            updateChart("1D");
            btn1M.setBackgroundTintList(getColorStateList(R.color.Orange));
            btn1D.setBackgroundTintList(getColorStateList(R.color.LightBlue));
            btn1W.setBackgroundTintList(getColorStateList(R.color.Orange));
        });

        btn1W.setOnClickListener(v -> {
            updateChart("1W");
            btn1M.setBackgroundTintList(getColorStateList(R.color.Orange));
            btn1D.setBackgroundTintList(getColorStateList(R.color.Orange));
            btn1W.setBackgroundTintList(getColorStateList(R.color.LightBlue));
        });

        btn1M.setOnClickListener(v -> {
            updateChart("1M");
            btn1M.setBackgroundTintList(getColorStateList(R.color.LightBlue));
            btn1D.setBackgroundTintList(getColorStateList(R.color.Orange));
            btn1W.setBackgroundTintList(getColorStateList(R.color.Orange));
        });

        // Buy and Sell button listeners
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