package com.example.wallstreettycoon.dashboard;

import android.content.Context;
import android.os.Bundle;
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
        TextView viewBalance = findViewById(R.id.viewBalance);
        String userBalance = "$" + String.valueOf(dbUtil.getUser("admin").getUserBalance());
        viewBalance.setText(userBalance);

        Button btnToggle = findViewById(R.id.btnToggleList);
        btnToggle.setOnClickListener(v -> {
            if (btnToggle.getText().toString().equals("P")) { //Market
                List<Stock> allStockList = dbUtil.getStockList();
                StockAdapter stockAdapter = new StockAdapter(this, allStockList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                stockRV.setLayoutManager(linearLayoutManager);
                stockRV.setAdapter(stockAdapter);
                btnToggle.setText("M");
            }
            else if (btnToggle.getText().toString().equals("M")) { //Portfolio
                List<PortfolioStock> portfolioStock = dbUtil.getPortfolio(Game.currentUser.getUserUsername());
                if (portfolioStock.isEmpty()) {
                    Toast.makeText(v.getContext(), "No stocks in portfolio", Toast.LENGTH_SHORT).show();
                }
                else {
                    PortfolioStockAdapter stockAdapter = new PortfolioStockAdapter(this, portfolioStock);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    stockRV.setLayoutManager(linearLayoutManager);
                    stockRV.setAdapter(stockAdapter);
                    btnToggle.setText("P");
                }
            }
        });

        /*// Here, we have created new array list and added data to it
        List<Stock> allStockList = dbUtil.getStockList();
        List<Stock> portfolioStock = dbUtil.getPortfolio();

        // we are initializing our adapter class and passing our arraylist to it.
        StockAdapter courseAdapter = new StockAdapter(this, allStockList);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        stockRV.setLayoutManager(linearLayoutManager);
        stockRV.setAdapter(courseAdapter);*/
    }
}