package com.example.wallstreettycoon.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.Game;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.portfolio.PortfolioStock;
import com.example.wallstreettycoon.portfolio.PortfolioStockAdapter;
import com.example.wallstreettycoon.stock.Stock;
import com.example.wallstreettycoon.stock.StockAdapter;
import com.example.wallstreettycoon.useraccount.ChangePassswordDialogFragment;

import java.util.List;

public class ListStocks extends AppCompatActivity {
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_stocks);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DatabaseUtil dbUtil = new DatabaseUtil(context);

        RecyclerView stockRV = findViewById(R.id.RVstock);
        TextView lblEmpty = findViewById(R.id.lblEmpty);
        TextView viewBalance = findViewById(R.id.viewBalance);
        String userBalance = "$" + String.valueOf(dbUtil.getUser("admin").getUserBalance());
        viewBalance.setText(userBalance);

        Button btnToggle = findViewById(R.id.btnToggleList);
        btnToggle.setOnClickListener(v -> {
            if (btnToggle.getText().toString().equals("P")) { //Market
                lblEmpty.setVisibility(View.GONE);
                List<Stock> allStockList = dbUtil.getStockList();
                StockAdapter stockAdapter = new StockAdapter(this, allStockList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                stockRV.setLayoutManager(linearLayoutManager);
                stockRV.setAdapter(stockAdapter);
                stockRV.setVisibility(View.VISIBLE);
                btnToggle.setText("M");
            }
            else if (btnToggle.getText().toString().equals("M")) { //Portfolio
                List<PortfolioStock> portfolioStock = dbUtil.getPortfolio(Game.currentUser.getUserUsername());
                if (portfolioStock.isEmpty()) {
                    lblEmpty.setText("No stocks in Portfolio");
                    lblEmpty.setVisibility(View.VISIBLE);
                    stockRV.setVisibility(View.GONE);
                    //Toast.makeText(v.getContext(), "No stocks in portfolio", Toast.LENGTH_SHORT).show();
                }
                else {
                    stockRV.setVisibility(View.VISIBLE);
                    lblEmpty.setVisibility(View.GONE);
                    PortfolioStockAdapter stockAdapter = new PortfolioStockAdapter(this, portfolioStock);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    stockRV.setLayoutManager(linearLayoutManager);
                    stockRV.setAdapter(stockAdapter);

                }
                btnToggle.setText("P");
            }
        });

        Button btnSearch = findViewById(R.id.btnSearchDashboard);
        btnSearch.setOnClickListener(v -> {
            FilterStocksDialogFragment searchDialog = new FilterStocksDialogFragment();
            searchDialog.show(getSupportFragmentManager(), "SearchDialog");
            Intent intent = getIntent();
            String filter = intent.getStringExtra("filter");
            String searchCriteria = intent.getStringExtra("search");

            if (filter != null)
            {
                if (btnToggle.getText().toString() == "M") {    //market stock filter
                    List<Stock> filteredStock = dbUtil.getFilteredStock(filter);
                    if (filteredStock.isEmpty())
                    {
                        lblEmpty.setText("No results");
                        lblEmpty.setVisibility(View.VISIBLE);
                        stockRV.setVisibility(View.GONE);
                    } else {
                        //display the filtered stock in the recyclerview:
                        StockAdapter stockAdapter = new StockAdapter(this, filteredStock);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        stockRV.setLayoutManager(linearLayoutManager);
                        stockRV.setAdapter(stockAdapter);
                    }
                }
            }

            if (searchCriteria != null)
            {
                if (btnToggle.getText().toString() == "M") {    //market stock search
                    List<Stock> searchedStock = dbUtil.searchStocks(searchCriteria);
                    if (searchedStock == null)
                    {
                        lblEmpty.setText("No results");
                        lblEmpty.setVisibility(View.VISIBLE);
                        stockRV.setVisibility(View.GONE);
                    } else {
                        //display the requested stock in the recyclerview:
                        StockAdapter stockAdapter = new StockAdapter(this, searchedStock);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        stockRV.setLayoutManager(linearLayoutManager);
                        stockRV.setAdapter(stockAdapter);
                    }
                }
            }
        });
    }
}