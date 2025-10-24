package com.example.wallstreettycoon.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.marketevent.CompactNotificationDialogFragment;
import com.example.wallstreettycoon.marketevent.NotificationsActivity;
import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.model.GameEvent;
import com.example.wallstreettycoon.model.GameObserver;
import com.example.wallstreettycoon.model.MarketEvent;
import com.example.wallstreettycoon.portfolio.PortfolioStock;
import com.example.wallstreettycoon.portfolio.PortfolioStockAdapter;
import com.example.wallstreettycoon.profile.GameProfile;
import com.example.wallstreettycoon.stock.Stock;
import com.example.wallstreettycoon.stock.StockAdapter;
import com.example.wallstreettycoon.useraccount.ManageUserAccount;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class ListStocks extends AppCompatActivity implements GameObserver {
    private Context context = this;
    private DatabaseUtil dbUtil;
    private RecyclerView stockRV;
    private TextView lblEmpty, lblResult, lblHeadingDisplayed;
    private ImageButton btnClear;
    private TextView viewBalance;
    private String userBalance;
    private String viewType;
    private Button btnToggleP, btnToggleM, btnSearch;
    private LinearLayout resultContainer;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;
    private RecyclerView.Adapter<?> currentAdapter;

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

        Game.getInstance().addObserver(this);
        dbUtil = DatabaseUtil.getInstance(context);

        // Initialize UI elements
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        stockRV = findViewById(R.id.RVstock);
        lblEmpty = findViewById(R.id.lblEmpty);
        lblResult = findViewById(R.id.lblResults);
        lblHeadingDisplayed = findViewById(R.id.lblListDisplayed);
        viewBalance = findViewById(R.id.viewBalance);
        btnToggleP = findViewById(R.id.btnToggleList);
        btnToggleM = findViewById(R.id.btnToggleList2);
        resultContainer = findViewById(R.id.resultContainer);
        btnClear = findViewById(R.id.btnClearSearch);
        btnSearch = findViewById(R.id.btnSearchDashboard);

        // Set user balance
        updateUserBalance();

        // Initial display based on intent
        Intent intent = getIntent();
        viewType = intent.getStringExtra("view");
        if (viewType == null) viewType = "M"; // Default to Market if not specified
        updateDisplay(viewType);

        // Toggle listeners
        btnToggleP.setOnClickListener(v -> {
            viewType = "P";
            updateDisplay(viewType);
            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            clearSearchUI();
        });

        btnToggleM.setOnClickListener(v -> {
            viewType = "M";
            updateDisplay(viewType);
            btnToggleP.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            btnToggleM.setBackgroundTintList(getResources().getColorStateList(R.color.LightBlue));
            clearSearchUI();
        });

        // Search listener
        btnSearch.setOnClickListener(v -> {
            FilterStocksDialogFragment searchDialog = new FilterStocksDialogFragment();
            searchDialog.setView(viewType);
            searchDialog.show(getSupportFragmentManager(), "FilterStockDialog");
        });

        // Clear search listener
        btnClear.setOnClickListener(v -> {
            updateDisplay(viewType);
            clearSearchUI();
        });

        // Handle initial filter/search from intent
        String filter = intent.getStringExtra("filter");
        String searchCriteria = intent.getStringExtra("search");
        if (filter != null && searchCriteria == null) {
            displayFilteredLists(filter, viewType);
        } else if (filter == null && searchCriteria != null) {
            displaySearchedLists(searchCriteria, viewType);
        } else if (filter != null && searchCriteria != null) {
            fullSearch(filter, searchCriteria, viewType);
        }

        // Drawer setup
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_notif) {
                Intent notifIntent = new Intent(context, NotificationsActivity.class);
                notifIntent.putExtra("viewType", viewType);
                startActivity(notifIntent);
            } else if (item.getItemId() == R.id.nav_profile) {
                Intent profile = new Intent(context, GameProfile.class);
                profile.putExtra("username", Game.currentUser.getUserUsername());
                profile.putExtra("viewType", viewType);
                startActivity(profile);
            } else if (item.getItemId() == R.id.nav_settings) {
                Intent manage = new Intent(context, ManageUserAccount.class);
                manage.putExtra("viewType", viewType);
                startActivity(manage);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
    }

    private void updateUserBalance() {
        if (Game.currentUser != null) {
            double balance = dbUtil.getUser(Game.currentUser.getUserUsername()).getUserBalance();
            userBalance = String.format("$%.2f", balance);
            viewBalance.setText(userBalance);
        }
    }

    private void updateDisplay(String viewType) {
        if ("M".equals(viewType)) {
            displayAllStocks();
        } else if ("P".equals(viewType)) {
            displayPortfolioStocks();
        }
    }

    private void clearSearchUI() {
        lblResult.setVisibility(View.GONE);
        btnClear.setVisibility(View.GONE);
        resultContainer.setVisibility(View.GONE);
    }

    private void updateRecyclerView(List<?> dataList, RecyclerView.Adapter<?> adapter, boolean isEmpty, String heading) {
        if (currentAdapter != null) {
            currentAdapter = null;
            stockRV.setAdapter(null); // Clear old adapter to prevent leaks
        }
        currentAdapter = adapter;
        stockRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        stockRV.setAdapter(adapter);
        lblHeadingDisplayed.setText(heading);

        if (isEmpty) {
            lblEmpty.setText(heading.equals("Portfolio") ? "No stocks in Portfolio" : "No stocks available");
            lblEmpty.setVisibility(View.VISIBLE);
            stockRV.setVisibility(View.GONE);
        } else {
            lblEmpty.setVisibility(View.GONE);
            stockRV.setVisibility(View.VISIBLE);
        }
    }

    public void displayPortfolioStocks() {
        List<PortfolioStock> portfolioStock = dbUtil.getPortfolio(Game.currentUser.getUserUsername());
        updateRecyclerView(portfolioStock, new PortfolioStockAdapter(this, portfolioStock, "P"), portfolioStock.isEmpty(), "Portfolio");
    }

    public void displayAllStocks() {
        List<Stock> allStockList = dbUtil.getStockList(); // Use getStockList for all stocks in Market view
        updateRecyclerView(allStockList, new StockAdapter(this, allStockList, "M"), allStockList.isEmpty(), "Market");
    }

    public void displayFilteredLists(String filter, String viewToggle) {
        if ("M".equals(viewToggle)) {
            List<Stock> filteredMarket = dbUtil.getFilteredStockM(filter);
            updateRecyclerView(filteredMarket, new StockAdapter(this, filteredMarket, "M"), filteredMarket.isEmpty(), "Market");
            if (!filteredMarket.isEmpty()) {
                lblResult.setText("Showing results for: " + filter);
                lblResult.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                resultContainer.setVisibility(View.VISIBLE);
            } else {
                clearSearchUI();
            }
        } else {
            List<PortfolioStock> filteredPortfolio = dbUtil.getFilteredPortfolioP(filter, Game.currentUser.getUserUsername());
            updateRecyclerView(filteredPortfolio, new PortfolioStockAdapter(this, filteredPortfolio, "P"), filteredPortfolio.isEmpty(), "Portfolio");
            if (!filteredPortfolio.isEmpty()) {
                lblResult.setText("Showing results for: " + filter);
                lblResult.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                resultContainer.setVisibility(View.VISIBLE);
            } else {
                clearSearchUI();
            }
        }
    }

    public void displaySearchedLists(String search, String viewToggle) {
        if ("M".equals(viewToggle)) {
            List<Stock> searchedMarket = dbUtil.searchStocksM(search);
            updateRecyclerView(searchedMarket, new StockAdapter(this, searchedMarket, "M"), searchedMarket.isEmpty(), "Market");
            if (!searchedMarket.isEmpty()) {
                lblResult.setText("Showing results for: " + search);
                lblResult.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                resultContainer.setVisibility(View.VISIBLE);
            } else {
                clearSearchUI();
            }
        } else {
            List<PortfolioStock> searchedPortfolio = dbUtil.searchPortfolioStocks(search, Game.currentUser.getUserUsername());
            updateRecyclerView(searchedPortfolio, new PortfolioStockAdapter(this, searchedPortfolio, "P"), searchedPortfolio.isEmpty(), "Portfolio");
            if (!searchedPortfolio.isEmpty()) {
                lblResult.setText("Showing results for: " + search);
                lblResult.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                resultContainer.setVisibility(View.VISIBLE);
            } else {
                clearSearchUI();
            }
        }
    }

    public void fullSearch(String filter, String search, String viewToggle) {
        if ("M".equals(viewToggle)) {
            List<Stock> combinedSearchMarket = dbUtil.combinedSearchM(filter, search);
            updateRecyclerView(combinedSearchMarket, new StockAdapter(this, combinedSearchMarket, "M"), combinedSearchMarket.isEmpty(), "Market");
            if (!combinedSearchMarket.isEmpty()) {
                lblResult.setText("Showing results for: " + filter + " and " + search);
                lblResult.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                resultContainer.setVisibility(View.VISIBLE);
            } else {
                clearSearchUI();
            }
        } else {
            List<PortfolioStock> combinedSearchPortfolio = dbUtil.combinedSearchP(filter, search, Game.currentUser.getUserUsername());
            updateRecyclerView(combinedSearchPortfolio, new PortfolioStockAdapter(this, combinedSearchPortfolio, "P"), combinedSearchPortfolio.isEmpty(), "Portfolio");
            if (!combinedSearchPortfolio.isEmpty()) {
                lblResult.setText("Showing results for: " + filter + " and " + search);
                lblResult.setVisibility(View.VISIBLE);
                btnClear.setVisibility(View.VISIBLE);
                resultContainer.setVisibility(View.VISIBLE);
            } else {
                clearSearchUI();
            }
        }
    }

    @Override
    public void onGameEvent(GameEvent event) {
        switch (event.getType()) {
            case UPDATE_STOCK_PRICE:
                if (stockRV.getAdapter() != null) {
                    stockRV.getAdapter().notifyDataSetChanged();
                    updateUserBalance(); // Update balance on price change
                }
                break;
            case MARKET_EVENT:
                MarketEvent notification = (MarketEvent) event.getCargo();
                notification.applyMarketFactors(); // Apply market factors
                CompactNotificationDialogFragment dialog = CompactNotificationDialogFragment.newInstance(notification);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(dialog, "CompactMarketEventNotification");
                ft.commitAllowingStateLoss();
                updateDisplay(viewType); // Refresh display after market event
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ListStocks", "onResume Called");
        updateUserBalance();
        updateDisplay(viewType); // Refresh display on resume
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<GameEvent> pending = Game.getPendingEvents();
            for (int i = 0; i < pending.size(); i++) {
                GameEvent e = pending.get(i);
                MarketEvent event = (MarketEvent) e.getCargo();
                event.applyMarketFactors();
                new Handler(Looper.getMainLooper()).postDelayed(() -> showMarketEvent(event), i * 5000);
            }
            pending.clear();
        }, 300);
    }

    private void showMarketEvent(MarketEvent event) {
        CompactNotificationDialogFragment dialog = CompactNotificationDialogFragment.newInstance(event);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(dialog, "CompactMarketEventNotification");
        ft.commitAllowingStateLoss();
    }
}