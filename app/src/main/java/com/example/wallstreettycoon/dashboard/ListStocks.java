package com.example.wallstreettycoon.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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
import com.example.wallstreettycoon.chapter.Chapter;
import com.example.wallstreettycoon.chapter.ChapterManager;
import com.example.wallstreettycoon.chapter.ChapterProgressActivity;
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

public class ListStocks extends AppCompatActivity implements GameObserver, FilterStocksDialogFragment.FilterListener {
    private final Context context = this;
    private DatabaseUtil dbUtil;
    private RecyclerView stockRV;
    private TextView lblEmpty, lblResult, lblHeadingDisplayed;
    private ImageButton btnClear;
    private TextView viewBalance;
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

        // Force Market view as default
        viewType = "M";
        updateDisplay(viewType);

        // Toggle listeners
        btnToggleP.setOnClickListener(v -> {
            viewType = "P";
            updateDisplay(viewType);
            int paddingTop = btnToggleM.getPaddingTop();
            int paddingBottom = btnToggleM.getPaddingBottom();
            int paddingLeft = btnToggleM.getPaddingLeft();
            int paddingRight = btnToggleM.getPaddingRight();

            btnToggleP.setBackgroundResource(R.drawable.button_background_lightblue_small);
            btnToggleM.setBackgroundResource(R.drawable.button_background_grey_small);
            btnToggleM.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            btnToggleP.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

            clearSearchUI();
        });

        btnToggleM.setOnClickListener(v -> {
            viewType = "M";
            updateDisplay(viewType);
            int paddingTop = btnToggleM.getPaddingTop();
            int paddingBottom = btnToggleM.getPaddingBottom();
            int paddingLeft = btnToggleM.getPaddingLeft();
            int paddingRight = btnToggleM.getPaddingRight();
            btnToggleM.setBackgroundResource(R.drawable.button_background_lightblue_small);
            btnToggleP.setBackgroundResource(R.drawable.button_background_grey_small);
            btnToggleM.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
            btnToggleP.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

            clearSearchUI();
        });

        // Search listener
        btnSearch.setOnClickListener(v -> {
            FilterStocksDialogFragment searchDialog = new FilterStocksDialogFragment();
            searchDialog.show(getSupportFragmentManager(), "FilterStockDialog");
        });

        // Clear search listener
        btnClear.setOnClickListener(v -> {
            updateDisplay(viewType);
            clearSearchUI();
        });

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
            } else if (item.getItemId() == R.id.nav_chapter_progress) {
                if (Game.getInstance() != null && Game.currentUser != null) {
                    startActivity(new Intent(ListStocks.this, ChapterProgressActivity.class));
                } else {
                    Log.w("ListStocks", "Game not initialized, cannot open ChapterProgressActivity");
                }
            } else if (item.getItemId() == R.id.nav_settings) {
                Intent manage = new Intent(context, ManageUserAccount.class);
                manage.putExtra("viewType", viewType);
                startActivity(manage);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Balance listener
        viewBalance.setOnClickListener(v -> {
            if (Game.currentUser != null) {
                Intent profile = new Intent(context, GameProfile.class);
                profile.putExtra("username", Game.currentUser.getUserUsername());
                profile.putExtra("viewType", viewType);
                startActivity(profile);
            }
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

        // Handle initial filter/search from intent (for backward compatibility)
        Intent intent = getIntent();
        String filter = intent.getStringExtra("filter");
        String searchCriteria = intent.getStringExtra("search");
        if (filter != null || searchCriteria != null) {
            onFilterApplied(filter, searchCriteria);
        }
    }

    @Override
    public void onFilterApplied(String filter, String searchCriteria) {
        if (filter != null && searchCriteria != null && !searchCriteria.trim().isEmpty()) {
            fullSearch(filter, searchCriteria, viewType);
        } else if (filter != null) {
            displayFilteredLists(filter, viewType);
        } else if (searchCriteria != null && !searchCriteria.trim().isEmpty()) {
            displaySearchedLists(searchCriteria, viewType);
        } else {
            updateDisplay(viewType);
        }
    }

    private void updateUserBalance() {
        if (Game.currentUser != null) {
            double balance = dbUtil.getUser(Game.currentUser.getUserUsername()).getUserBalance();
            String userBalance = String.format("$%.2f", balance);
            viewBalance.setText(userBalance);
        }
    }

    private void updateDisplay(String viewType) {
        this.viewType = viewType; // Ensure viewType is updated
        if ("M".equals(viewType)) {
            displayAllStocks();
        } else if ("P".equals(viewType)) {
            displayPortfolioStocks();
        }
        if (currentAdapter != null) {
            currentAdapter.notifyDataSetChanged();
        }
    }

    private void clearSearchUI() {
        lblResult.setVisibility(View.GONE);
        btnClear.setVisibility(View.GONE);
        resultContainer.setVisibility(View.GONE);
        lblEmpty.setVisibility(View.GONE); // Restore lblEmpty when clearing search
    }

    private void updateRecyclerView(List<?> dataList, RecyclerView.Adapter<?> adapter, boolean isEmpty, String heading) {
        if (currentAdapter != adapter) {
            currentAdapter = adapter;
            stockRV.setAdapter(adapter);
            stockRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
        lblHeadingDisplayed.setText(heading);

        if (isEmpty) {
            lblEmpty.setText(heading.equals("Portfolio") ? "No stocks in Portfolio" : "No stocks available");
            lblEmpty.setVisibility(View.VISIBLE);
            stockRV.setVisibility(View.GONE);
        } else {
            lblEmpty.setVisibility(View.GONE);
            stockRV.setVisibility(View.VISIBLE);
        }
        if (currentAdapter != null) {
            currentAdapter.notifyDataSetChanged();
        }
    }

    public void displayPortfolioStocks() {
        List<PortfolioStock> portfolioStock = dbUtil.getPortfolio(Game.currentUser.getUserUsername());
        updateRecyclerView(portfolioStock, new PortfolioStockAdapter(this, portfolioStock, "P"), portfolioStock.isEmpty(), "Portfolio");
    }

    public void displayAllStocks() {
        Chapter currentChapter = ChapterManager.getInstance().getCurrentChapter();
        List<Stock> chapterStocks = currentChapter != null ? currentChapter.getChapterStocks() : dbUtil.getStockList();
        updateRecyclerView(chapterStocks, new StockAdapter(this, chapterStocks, "M"), chapterStocks.isEmpty(), "Market");
    }

    public void displayFilteredLists(String filter, String viewToggle) {
        if ("M".equals(viewToggle)) {
            Chapter currentChapter = ChapterManager.getInstance().getCurrentChapter();
            List<Stock> chapterStocks = currentChapter != null ? currentChapter.getChapterStocks() : dbUtil.getStockList();
            List<Stock> filteredMarket = dbUtil.getFilteredStockM(filter, chapterStocks);
            updateRecyclerView(filteredMarket, new StockAdapter(this, filteredMarket, "M"), filteredMarket.isEmpty(), "Market");
            lblResult.setText(filteredMarket.isEmpty() ? "No results for: " + filter : "Showing results for: " + filter);
            lblResult.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
            resultContainer.setVisibility(View.VISIBLE);
            lblEmpty.setVisibility(View.GONE); // Hide lblEmpty when filtering
        } else {
            List<PortfolioStock> portfolioStocks = dbUtil.getPortfolio(Game.currentUser.getUserUsername());
            List<PortfolioStock> filteredPortfolio = dbUtil.getFilteredPortfolioP(filter, Game.currentUser.getUserUsername(), portfolioStocks);
            updateRecyclerView(filteredPortfolio, new PortfolioStockAdapter(this, filteredPortfolio, "P"), filteredPortfolio.isEmpty(), "Portfolio");
            lblResult.setText(filteredPortfolio.isEmpty() ? "No results for: " + filter : "Showing results for: " + filter);
            lblResult.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
            resultContainer.setVisibility(View.VISIBLE);
            lblEmpty.setVisibility(View.GONE); // Hide lblEmpty when filtering
        }
    }

    public void displaySearchedLists(String search, String viewToggle) {
        if ("M".equals(viewToggle)) {
            Chapter currentChapter = ChapterManager.getInstance().getCurrentChapter();
            List<Stock> chapterStocks = currentChapter != null ? currentChapter.getChapterStocks() : dbUtil.getStockList();
            List<Stock> searchedMarket = dbUtil.searchStocksM(search, chapterStocks);
            updateRecyclerView(searchedMarket, new StockAdapter(this, searchedMarket, "M"), searchedMarket.isEmpty(), "Market");
            lblResult.setText(searchedMarket.isEmpty() ? "No results for: " + search : "Showing results for: " + search);
            lblResult.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
            resultContainer.setVisibility(View.VISIBLE);
            lblEmpty.setVisibility(View.GONE); // Hide lblEmpty when searching
        } else {
            List<PortfolioStock> portfolioStocks = dbUtil.getPortfolio(Game.currentUser.getUserUsername());
            List<PortfolioStock> searchedPortfolio = dbUtil.searchPortfolioStocks(search, Game.currentUser.getUserUsername(), portfolioStocks);
            updateRecyclerView(searchedPortfolio, new PortfolioStockAdapter(this, searchedPortfolio, "P"), searchedPortfolio.isEmpty(), "Portfolio");
            lblResult.setText(searchedPortfolio.isEmpty() ? "No results for: " + search : "Showing results for: " + search);
            lblResult.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
            resultContainer.setVisibility(View.VISIBLE);
            lblEmpty.setVisibility(View.GONE); // Hide lblEmpty when searching
        }
    }

    public void fullSearch(String filter, String search, String viewToggle) {
        if ("M".equals(viewToggle)) {
            Chapter currentChapter = ChapterManager.getInstance().getCurrentChapter();
            List<Stock> chapterStocks = currentChapter != null ? currentChapter.getChapterStocks() : dbUtil.getStockList();
            List<Stock> combinedSearchMarket = dbUtil.combinedSearchM(filter, search, chapterStocks);
            updateRecyclerView(combinedSearchMarket, new StockAdapter(this, combinedSearchMarket, "M"), combinedSearchMarket.isEmpty(), "Market");
            lblResult.setText(combinedSearchMarket.isEmpty() ? "No results for: " + filter + " and " + search : "Showing results for: " + filter + " and " + search);
            lblResult.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
            resultContainer.setVisibility(View.VISIBLE);
            lblEmpty.setVisibility(View.GONE); // Hide lblEmpty when combined search
        } else {
            List<PortfolioStock> portfolioStocks = dbUtil.getPortfolio(Game.currentUser.getUserUsername());
            List<PortfolioStock> combinedSearchPortfolio = dbUtil.combinedSearchP(filter, search, Game.currentUser.getUserUsername(), portfolioStocks);
            updateRecyclerView(combinedSearchPortfolio, new PortfolioStockAdapter(this, combinedSearchPortfolio, "P"), combinedSearchPortfolio.isEmpty(), "Portfolio");
            lblResult.setText(combinedSearchPortfolio.isEmpty() ? "No results for: " + filter + " and " + search : "Showing results for: " + filter + " and " + search);
            lblResult.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.VISIBLE);
            resultContainer.setVisibility(View.VISIBLE);
            lblEmpty.setVisibility(View.GONE); // Hide lblEmpty when combined search
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
        viewType = "M"; // Force Market view on resume

        int paddingTop = btnToggleM.getPaddingTop();
        int paddingBottom = btnToggleM.getPaddingBottom();
        int paddingLeft = btnToggleM.getPaddingLeft();
        int paddingRight = btnToggleM.getPaddingRight();

        btnToggleM.setBackgroundResource(R.drawable.button_background_lightblue_small);
        btnToggleP.setBackgroundResource(R.drawable.button_background_grey_small);
        btnToggleM.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        btnToggleP.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        updateDisplay(viewType); // Refresh display on resume
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<GameEvent> pending = Game.getPendingEvents();
            for (int i = 0; i < pending.size(); i++) {
                GameEvent e = pending.get(i);
                new Handler(Looper.getMainLooper()).postDelayed(() -> Game.getInstance().onGameEvent(e), i * 5000);
            }
            pending.clear();
        }, 300);
    }
}