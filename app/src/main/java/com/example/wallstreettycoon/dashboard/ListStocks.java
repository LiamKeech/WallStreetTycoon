package com.example.wallstreettycoon.dashboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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
    DatabaseUtil dbUtil;

    RecyclerView stockRV;
    TextView lblEmpty, lblResult, lblHeadingDisplayed;
    TextView btnClear;
    TextView viewBalance;
    String userBalance;
    String viewType;
    Button btnToggleP, btnToggleM;

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

        dbUtil = new DatabaseUtil(context);

        stockRV = findViewById(R.id.RVstock);
        lblEmpty = findViewById(R.id.lblEmpty);
        lblResult = findViewById(R.id.lblResults);
        lblHeadingDisplayed = findViewById(R.id.lblListDisplayed);
        viewBalance = findViewById(R.id.viewBalance);
        userBalance = "$" + dbUtil.getUser(Game.currentUser.getUserUsername()).getUserBalance();
        viewBalance.setText(userBalance);

        btnToggleP = findViewById(R.id.btnToggleList);
        btnToggleM = findViewById(R.id.btnToggleList2);

        //get intent: (from login and or search button)
        Intent intent = getIntent();
        viewType = intent.getStringExtra("view");

        // Displays portfolio if user owns stock, else show market stocks (for new user too)
        //List<PortfolioStock> portfolioStockCheck = dbUtil.getPortfolio(Game.currentUser.getUserUsername());
        /*if (portfolioStockCheck.isEmpty()) {
            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
            displayAllStocks();
        } else {
            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            displayPortfolioStocks();
        }*/

        if (viewType != null && viewType.equals("M")) {
            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
            displayAllStocks();
        } else if (viewType != null && viewType.equals("P")){
            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            displayPortfolioStocks();
        }

        btnToggleP.setOnClickListener(v -> {    //display list of portfolio stocks, make P blue, set viewtype to P
            displayPortfolioStocks();
            lblHeadingDisplayed.setText("Portfolio");
            viewType = "P";
            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            //Also clears all searches and filters by displaying original list
            lblResult.setVisibility(View.GONE);
            btnClear.setVisibility(View.GONE);
        });

        btnToggleM.setOnClickListener(v -> {    //display list of all stocks, make M blue, set viewtype to M
            displayAllStocks();
            lblHeadingDisplayed.setText("Market");
            viewType = "M";
            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
            //Also clears all searches and filters by displaying original list
            lblResult.setVisibility(View.GONE);
            btnClear.setVisibility(View.GONE);
        });

        Button btnSearch = findViewById(R.id.btnSearchDashboard);
        btnSearch.setOnClickListener(v -> {
            FilterStocksDialogFragment searchDialog = new FilterStocksDialogFragment();
            searchDialog.setView(viewType);
            searchDialog.show(getSupportFragmentManager(), "FilterStockDialog");
        });

        String filter = intent.getStringExtra("filter");
        if (filter != null) {
            displayFilteredLists(filter, viewType);
        }


//        if (viewType == "M") {
//            //display filtered market
//            displayFilteredMarket(filter);
//            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
//            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
//
//        } else {
//            //display filtered portfolio
//            displayFilteredPortfolio(filter);
//            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
//            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
//        }



        Log.d("filter: ", "filter");//FIXME filter message
        String searchCriteria = intent.getStringExtra("search");

        btnClear = findViewById(R.id.btnClearSearch);
        btnClear.setOnClickListener(v -> {  //clear filter by displaying OG lists
            btnClear.setTypeface(null, Typeface.ITALIC);
            lblResult.setVisibility(View.GONE);
            btnClear.setVisibility(View.GONE);
            if (viewType != null && viewType.equals("M")) {
                displayAllStocks();
                lblHeadingDisplayed.setText("Market");
            } else if (viewType != null && viewType.equals("P")) { //"P"
                displayPortfolioStocks();
                lblHeadingDisplayed.setText("Portfolio");
            }
        });

    }
    public void displayPortfolioStocks(){   //gets portfolio list, checks if empty, put it in recyclerview
        List<PortfolioStock> portfolioStock = dbUtil.getPortfolio(Game.currentUser.getUserUsername());
        /*for(PortfolioStock stock : portfolioStock){
            Log.d("", stock.getStockID().toString());
        }*/
        if (portfolioStock.isEmpty()) {
            lblEmpty.setText("No stocks in Portfolio");
            lblEmpty.setVisibility(View.VISIBLE);
            stockRV.setVisibility(View.GONE);
        }
        else {
            stockRV.setVisibility(View.VISIBLE);
            lblEmpty.setVisibility(View.GONE);
            PortfolioStockAdapter stockAdapter = new PortfolioStockAdapter(this, portfolioStock);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            stockRV.setLayoutManager(linearLayoutManager);
            stockRV.setAdapter(stockAdapter);
        }
    }

    public void displayAllStocks(){ //gets list of all stocks, put in recyclerview
        lblEmpty.setVisibility(View.GONE);
        List<Stock> allStockList = dbUtil.getStockList();
        StockAdapter stockAdapter = new StockAdapter(this, allStockList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        stockRV.setLayoutManager(linearLayoutManager);
        stockRV.setAdapter(stockAdapter);
        stockRV.setVisibility(View.VISIBLE);
    }

    public void displayFilteredMarket(String filter){     //take in filter and current screen (P/M)
        if (filter != null)
        {
            List<Stock> filteredStock = dbUtil.getFilteredStockM(filter);
            if (filteredStock.isEmpty())
            {
                lblEmpty.setText("No results");
                lblEmpty.setVisibility(View.VISIBLE);
                stockRV.setVisibility(View.GONE);
            } else {
                //display the filtered stock in the recyclerview:
                StockAdapter stockAdapter = new StockAdapter(this, filteredStock);
                lblResult.setText("Showing results for: " + filter);
                lblResult.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                stockRV.setLayoutManager(linearLayoutManager);
                stockRV.setAdapter(stockAdapter);
            }
        }
    }

    public void displayFilteredPortfolio(String filter) {
        if (filter != null)
        {
            List<PortfolioStock> filteredStock = dbUtil.getFilteredStockP(filter);
            if (filteredStock.isEmpty())
            {
                lblEmpty.setText("No results");
                lblEmpty.setVisibility(View.VISIBLE);
                stockRV.setVisibility(View.GONE);
            } else {
                //display the filtered stock in the recyclerview:
                PortfolioStockAdapter stockAdapter = new PortfolioStockAdapter(this, filteredStock);
                lblResult.setText("Showing results for: " + filter);
                lblResult.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                stockRV.setLayoutManager(linearLayoutManager);
                stockRV.setAdapter(stockAdapter);
            }
        }

    }

//    public void displayFilteredLists(String filter, String viewToggle) {
//        if (filter != null) {
//            List<Stock> filteredMarket = dbUtil.getFilteredStockM(filter);
//            List<PortfolioStock> filteredPortfolio = dbUtil.getFilteredStockP(filter);
//            if (filteredMarket.isEmpty() || filteredPortfolio.isEmpty())
//            {
//                lblEmpty.setText("No results");
//                lblEmpty.setVisibility(View.VISIBLE);
//                stockRV.setVisibility(View.GONE);
//            } else {
//                if (viewType != null && viewType.equals("M")) {
//                    StockAdapter stockAdapter = new StockAdapter(this, filteredMarket);
//                    lblResult.setText("Showing results for: " + filter);
//                    lblResult.setVisibility(View.VISIBLE);
//                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//                    stockRV.setLayoutManager(linearLayoutManager);
//                    stockRV.setAdapter(stockAdapter);
//                    btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
//                    btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
//                } else if (viewType != null && viewType.equals("P")) { //"P"
//                    PortfolioStockAdapter stockAdapter = new PortfolioStockAdapter(this, filteredPortfolio);
//                    lblResult.setText("Showing results for: " + filter);
//                    lblResult.setVisibility(View.VISIBLE);
//                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//                    stockRV.setLayoutManager(linearLayoutManager);
//                    stockRV.setAdapter(stockAdapter);
//                    btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
//                    btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
//                }
//            }
//        }

    public void displayFilteredLists(String filter, String viewToggle) {
        if (filter != null) {
            if ("M".equals(viewToggle)) {
                List<Stock> filteredMarket = dbUtil.getFilteredStockM(filter);
                if (filteredMarket.isEmpty()) {
                    // Show no results message
                    if (lblEmpty != null) {
                        lblEmpty.setText("No results");
                        lblEmpty.setVisibility(View.VISIBLE);
                    }
                    if (stockRV != null) {
                        stockRV.setVisibility(View.GONE);
                    }
                    // Hide result label and clear button when no results
                    if (lblResult != null) {
                        lblResult.setVisibility(View.GONE);
                    }
                    if (btnClear != null) {
                        btnClear.setVisibility(View.GONE);
                    }
                } else {
                    // Hide empty message and show results
                    if (lblEmpty != null) {
                        lblEmpty.setVisibility(View.GONE);
                    }
                    if (stockRV != null) {
                        stockRV.setVisibility(View.VISIBLE);
                    }
                    if (lblResult != null) {
                        lblResult.setText("Showing results for: " + filter);
                        lblResult.setVisibility(View.VISIBLE);
                    }
                    if (btnClear != null) {
                        btnClear.setVisibility(View.VISIBLE);
                    }

                    // Create and set up the adapter BEFORE using it
                    StockAdapter stockAdapter = new StockAdapter(this, filteredMarket);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    stockRV.setLayoutManager(linearLayoutManager);
                    stockRV.setAdapter(stockAdapter);

                    // Ensure correct button states and heading
                    if (btnToggleP != null) {
                        btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
                    }
                    if (btnToggleM != null) {
                        btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
                    }
                    if (lblHeadingDisplayed != null) {
                        lblHeadingDisplayed.setText("Market");
                    }
                }
            } else { // "P" - Portfolio view
                List<PortfolioStock> filteredPortfolio = dbUtil.getFilteredStockP(filter);
                if (filteredPortfolio.isEmpty()) {
                    // Show no results message
                    if (lblEmpty != null) {
                        lblEmpty.setText("No results");
                        lblEmpty.setVisibility(View.VISIBLE);
                    }
                    if (stockRV != null) {
                        stockRV.setVisibility(View.GONE);
                    }
                    // Hide result label and clear button when no results
                    if (lblResult != null) {
                        lblResult.setVisibility(View.GONE);
                    }
                    if (btnClear != null) {
                        btnClear.setVisibility(View.GONE);
                    }
                } else {
                    // Hide empty message and show results
                    if (lblEmpty != null) {
                        lblEmpty.setVisibility(View.GONE);
                    }
                    if (stockRV != null) {
                        stockRV.setVisibility(View.VISIBLE);
                    }
                    if (lblResult != null) {
                        lblResult.setText("Showing results for: " + filter);
                        lblResult.setVisibility(View.VISIBLE);
                    }
                    if (btnClear != null) {
                        btnClear.setVisibility(View.VISIBLE);
                    }

                    // Create and set up the adapter
                    PortfolioStockAdapter stockAdapter = new PortfolioStockAdapter(this, filteredPortfolio);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    stockRV.setLayoutManager(linearLayoutManager);
                    stockRV.setAdapter(stockAdapter);

                    // Ensure correct button states and heading
                    if (btnToggleP != null) {
                        btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
                    }
                    if (btnToggleM != null) {
                        btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
                    }
                    if (lblHeadingDisplayed != null) {
                        lblHeadingDisplayed.setText("Portfolio");
                    }
                }
            }
        }
    }


    /*public void displaySearchedStock() {
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
        });
    }
    public void displayPortfolioStocks(){
        List<PortfolioStock> portfolioStock = dbUtil.getPortfolio(Game.currentUser.getUserUsername());
        for(PortfolioStock stock : portfolioStock){
            Log.d("", stock.getStock().getStockID().toString());
        }
    }*/
}