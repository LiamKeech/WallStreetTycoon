package com.example.wallstreettycoon.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.model.GameEvent;
import com.example.wallstreettycoon.model.GameObserver;
import com.example.wallstreettycoon.portfolio.PortfolioStock;
import com.example.wallstreettycoon.portfolio.PortfolioStockAdapter;
import com.example.wallstreettycoon.profile.GameProfile;
import com.example.wallstreettycoon.stock.Stock;
import com.example.wallstreettycoon.stock.StockAdapter;
import com.example.wallstreettycoon.useraccount.ManageUserAccount;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class ListStocks extends AppCompatActivity implements GameObserver {
    Context context = this;
    DatabaseUtil dbUtil;

    RecyclerView stockRV;
    TextView lblEmpty, lblResult, lblHeadingDisplayed;
    ImageButton btnClear,btnDrawer;
    TextView viewBalance;
    String userBalance;
    String viewType;
    Button btnToggleP, btnToggleM, btnSearch;
    LinearLayout resultContainer;
    DrawerLayout drawerLayout;
    NavigationView navView;
    Toolbar toolbar;

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

        Game.gameInstance.addObserver(this);

        dbUtil = new DatabaseUtil(context);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        //btnDrawer = findViewById(R.id.btnDrawer);
        toolbar = findViewById(R.id.toolbar);

        stockRV = findViewById(R.id.RVstock);
        lblEmpty = findViewById(R.id.lblEmpty);
        lblResult = findViewById(R.id.lblResults);
        lblHeadingDisplayed = findViewById(R.id.lblListDisplayed);
        viewBalance = findViewById(R.id.viewBalance);
        userBalance = "$" + dbUtil.getUser(Game.currentUser.getUserUsername()).getUserBalance();
        viewBalance.setText(userBalance);

        btnToggleP = findViewById(R.id.btnToggleList);
        btnToggleM = findViewById(R.id.btnToggleList2);

        resultContainer = findViewById(R.id.resultContainer);
        btnClear = findViewById(R.id.btnClearSearch);
        btnSearch = findViewById(R.id.btnSearchDashboard);

        //get intent: (from login and or search button)
        Intent intent = getIntent();
        viewType = intent.getStringExtra("view");

        //displaying of original market and portfolio lists:
        if (viewType != null && viewType.equals("M")) {
            lblHeadingDisplayed.setText("Market");
            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
            displayAllStocks();
        } else if (viewType != null && viewType.equals("P")){
            lblHeadingDisplayed.setText("Portfolio");
            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            displayPortfolioStocks();
        }

        //changing between the market and portfolio:
        btnToggleP.setOnClickListener(v -> {    //display list of portfolio stocks, make P blue, set viewtype to P
            displayPortfolioStocks();
            lblHeadingDisplayed.setText("Portfolio");
            viewType = "P";
            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            //Also clears all searches and filters by displaying original list
            resultContainer.setVisibility(View.INVISIBLE);
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
            resultContainer.setVisibility(View.INVISIBLE);
            lblResult.setVisibility(View.GONE);
            btnClear.setVisibility(View.GONE);
        });

        btnSearch.setOnClickListener(v -> {
            FilterStocksDialogFragment searchDialog = new FilterStocksDialogFragment();
            searchDialog.setView(viewType);
            searchDialog.show(getSupportFragmentManager(), "FilterStockDialog");
        });

        btnClear.setOnClickListener(v -> {  //clear filter by displaying OG lists
            //btnClear.setTypeface(null, Typeface.ITALIC);
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

        String filter = intent.getStringExtra("filter");
        String searchCriteria = intent.getStringExtra("search");

        if (filter != null && searchCriteria == null) { //just filter chosen
            displayFilteredLists(filter, viewType);
        }
        else if (filter == null && searchCriteria != null) { //just criteria entered
            displaySearchedLists(searchCriteria, viewType);
        }
        else if (filter != null && searchCriteria != null) { //filter selected and criteria entered
            fullSearch(filter, searchCriteria, viewType);
        }

        //toggle for open/close drawer:
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //selected item in menu based on id:
                if (item.getItemId() == R.id.nav_notif) {

                }

                if (item.getItemId() == R.id.nav_profile) {
                    Intent profile = new Intent(context, GameProfile.class);
                    profile.putExtra("username", Game.currentUser.getUserUsername());
                    profile.putExtra("viewType", viewType);
                    startActivity(profile);
                }

                if (item.getItemId() == R.id.nav_settings) { //go to manage user account
                    Intent manage = new Intent(context, ManageUserAccount.class);
                    startActivity(manage);
                }

                drawerLayout.closeDrawers();

                return true;
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            // Called when the back button is pressed.
            @Override
            public void handleOnBackPressed() {
                // Check if the drawer is open
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    // Close the drawer if it's open
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Finish the activity if the drawer is closed
                    finish();
                }
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

            PortfolioStockAdapter stockAdapter = new PortfolioStockAdapter(this, portfolioStock, "P");
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            stockRV.setLayoutManager(linearLayoutManager);
            stockRV.setAdapter(stockAdapter);
        }
    }

    public void displayAllStocks(){ //gets list of all stocks, put in recyclerview
        lblEmpty.setVisibility(View.GONE);
        //List<Stock> allStockList = dbUtil.getStockList();
        List<Stock> allStockList = dbUtil.getChapterStock();

        StockAdapter stockAdapter = new StockAdapter(this, allStockList, "M");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        stockRV.setLayoutManager(linearLayoutManager);
        stockRV.setAdapter(stockAdapter);
        stockRV.setVisibility(View.VISIBLE);
    }

    /*public void displayFilteredMarket(String filter){     //take in filter and current screen (P/M)
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
                StockAdapter stockAdapter = new StockAdapter(this, filteredStock, "P");
                lblResult.setText("Showing results for: " + filter);
                resultContainer.setVisibility(View.VISIBLE);
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
                PortfolioStockAdapter stockAdapter = new PortfolioStockAdapter(this, filteredStock, "M");
                lblResult.setText("Showing results for: " + filter);
                lblResult.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                stockRV.setLayoutManager(linearLayoutManager);
                stockRV.setAdapter(stockAdapter);
            }
        }

    }*/

    //method to filter lists without criteria entered
    public void displayFilteredLists(String filter, String viewToggle) {
        if (filter != null) {
            if ("M".equals(viewToggle)) { //market screen
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
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.INVISIBLE);
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
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.VISIBLE);
                    }

                    // Create and set up the adapter BEFORE using it
                    StockAdapter stockAdapter = new StockAdapter(this, filteredMarket, "M");
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
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.INVISIBLE);
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
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.VISIBLE);
                    }

                    // Create and set up the adapter
                    PortfolioStockAdapter stockAdapter = new PortfolioStockAdapter(this, filteredPortfolio, "P");
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

    //method to search through a list fro criteria without filter selected
    public void displaySearchedLists(String search, String viewToggle) {
        if (search != null)
        {
            if ("M".equals(viewToggle)) {   //market screen
                List<Stock> searchedMarket = dbUtil.searchStocksM(search);
                if (searchedMarket.isEmpty()) {
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
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.INVISIBLE);
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
                        lblResult.setText("Showing results for: " + search);
                        lblResult.setVisibility(View.VISIBLE);
                    }
                    if (btnClear != null) {
                        btnClear.setVisibility(View.VISIBLE);
                    }
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.VISIBLE);
                    }

                    // Create and set up the adapter BEFORE using it
                    StockAdapter stockAdapter = new StockAdapter(this, searchedMarket, "M");
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
                List<PortfolioStock> searchedPortfolio = dbUtil.searchStocksP(search);
                if (searchedPortfolio.isEmpty()) {
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
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.INVISIBLE);
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
                        lblResult.setText("Showing results for: " + search);
                        lblResult.setVisibility(View.VISIBLE);
                    }
                    if (btnClear != null) {
                        btnClear.setVisibility(View.VISIBLE);
                    }
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.VISIBLE);
                    }

                    // Create and set up the adapter
                    PortfolioStockAdapter stockAdapter = new PortfolioStockAdapter(this, searchedPortfolio, "P");
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

    //method to filter a list and search through it:
    public void fullSearch(String filter, String search, String viewToggle) {
        if (filter != null && search != null) {  //filter applied and criteria entered
            if ("M".equals(viewToggle)) { //market screen
                List<Stock> combinedSearchMarket = dbUtil.combinedSearchM(filter, search);
                if (combinedSearchMarket.isEmpty()) {
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
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.INVISIBLE);
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
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.VISIBLE);
                    }

                    // Create and set up the adapter BEFORE using it
                    StockAdapter stockAdapter = new StockAdapter(this, combinedSearchMarket, "M");
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
                List<PortfolioStock> combinedSearchPortfolio = dbUtil.combinedSearchP(filter, search);
                if (combinedSearchPortfolio.isEmpty()) {
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
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.INVISIBLE);
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
                    if (resultContainer != null) {
                        resultContainer.setVisibility(View.VISIBLE);
                    }

                    // Create and set up the adapter
                    PortfolioStockAdapter stockAdapter = new PortfolioStockAdapter(this, combinedSearchPortfolio, "P");
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

    @Override
    public void onGameEvent(GameEvent event) {
       displayAllStocks();
    }
}