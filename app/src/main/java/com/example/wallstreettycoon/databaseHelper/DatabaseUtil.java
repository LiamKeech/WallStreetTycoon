package com.example.wallstreettycoon.databaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.model.MarketEvent;
import com.example.wallstreettycoon.portfolio.PortfolioStock;
import com.example.wallstreettycoon.stock.Stock;
import com.example.wallstreettycoon.stock.StockPriceFunction;
import com.example.wallstreettycoon.transaction.Transaction;
import com.example.wallstreettycoon.useraccount.User;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * SINGLETON DatabaseUtil - Ensures only ONE database connection exists
 * All cursor operations wrapped in try-finally for proper resource management
 */
public class DatabaseUtil {
    private static DatabaseUtil instance;
    private static DatabaseCreator dbCreator;
    private static SQLiteDatabase db;

    // Private constructor prevents direct instantiation
    private DatabaseUtil(Context context) {
        if (dbCreator == null) {
            dbCreator = new DatabaseCreator(context.getApplicationContext());
        }
        if (db == null || !db.isOpen()) {
            db = dbCreator.getWritableDatabase();
        }
    }

    // Singleton getInstance method - THREAD SAFE
    public static synchronized DatabaseUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseUtil(context.getApplicationContext());
        }
        return instance;
    }

    // Stock related methods

    public List<Stock> getStockList(){
        List<Stock> stockList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM stocks", null);
            if (cursor.moveToFirst()) {
                do {
                    int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                    String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                    String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    Double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                    Stock stock = new Stock(stockID, stockName, symbol, category, description, currentPrice);
                    stockList.add(stock);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return stockList;
    }

    public List<Stock> getStockListByCategory(String category) {
        List<Stock> stockList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM stocks WHERE category = ?", new String[]{category});
            if (cursor.moveToFirst()) {
                do {
                    int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                    String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                    String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                    String stockCategory = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    Double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                    Stock stock = new Stock(stockID, stockName, symbol, category, description, currentPrice);
                    stockList.add(stock);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return stockList;
    }

    public Stock getStock(int stockID) {
        Stock stock = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM stocks WHERE stockID = ?", new String[]{String.valueOf(stockID)});
            if (cursor.moveToFirst()) {
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                stock = new Stock(stockID, stockName, symbol, category, description, currentPrice);

                populatePriceHistory(stock);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return stock;
    }

    private void populatePriceHistory(Stock stock) {
        StockPriceFunction func = getStockPriceFunction(stock.getStockID());
        if (func == null) {
            Log.w("DatabaseUtil", "No price function for stock " + stock.getSymbol());
            return;
        }

        int currentTimestamp = Game.getInstance().getCurrentTimeStamp();

        // Generate price history for the last 30 days minimum
        int historyDays = 30;
        List<Double> priceHistory = stock.getPriceHistory();
        priceHistory.clear();

        // Calculate historical prices
        for (int i = -historyDays; i <= 0; i++) {
            int timestamp = currentTimestamp + i;
            if (timestamp >= 0) {
                double priceChange = func.getCurrentPriceChange(timestamp);
                double historicalPrice = stock.getCurrentPrice() + priceChange;
                // Ensure price is never negative
                historicalPrice = Math.max(0, historicalPrice);
                priceHistory.add(historicalPrice);
            }
        }

//        Log.d("DatabaseUtil", "Populated " + priceHistory.size() + " days of history for " + stock.getSymbol());
    }

    public void updateStockPriceHistory(int stockID) {
        Stock stock = getStock(stockID);
        if (stock != null) {
            StockPriceFunction func = getStockPriceFunction(stockID);
            if (func != null) {
                int currentTimestamp = Game.getInstance().getCurrentTimeStamp();
                double priceChange = func.getCurrentPriceChange(currentTimestamp);
                double currentPrice = stock.getCurrentPrice() + priceChange;
                currentPrice = Math.max(0, currentPrice);

                List<Double> history = stock.getPriceHistory();

                // Add to history
                history.add(currentPrice);

                int maxHistorySize = 31; // 30 days + current
                if (history.size() > maxHistorySize) {
                    history.remove(0); // Remove oldest entry
                }

//                Log.d("DatabaseUtil", "Updated price history for " + stock.getSymbol() +
//                        ": $" + String.format("%.2f", currentPrice) +
//                        " (history size: " + history.size() + ")");
            }
        }
    }

    public String getSymbol(int stockID) {
        String symbol = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT symbol FROM stocks WHERE stockID = ?", new String[]{String.valueOf(stockID)});
            if (cursor.moveToFirst()) {
                symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return symbol;
    }

    public String getStockName(int stockID) {
        String name = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT stockName FROM stocks WHERE stockID = ?", new String[]{String.valueOf(stockID)});
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return name != null ? name : "unknown stock";
    }

    public List<StockPriceFunction> getStockPriceFunctions(){
        List<StockPriceFunction> stockPriceHistories = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM stockPriceFunction", null);
            if (cursor.moveToFirst()) {
                do {
                    Integer stockPriceHistoryID = cursor.getInt(cursor.getColumnIndexOrThrow("stockPriceHistoryID"));
                    String[] amplitudesString = cursor.getString(cursor.getColumnIndexOrThrow("amplitudes")).split(",");
                    Double[] amplitudes = Arrays.stream(amplitudesString)
                            .map(Double::parseDouble)
                            .toArray(Double[]::new);

                    String[] frequenciesString = cursor.getString(cursor.getColumnIndexOrThrow("frequencies")).split(",");
                    Double[] frequencies = Arrays.stream(frequenciesString)
                            .map(Double::parseDouble)
                            .toArray(Double[]::new);

                    Integer stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                    StockPriceFunction s = new StockPriceFunction(stockPriceHistoryID, amplitudes, frequencies, stockID);
                    stockPriceHistories.add(s);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return stockPriceHistories;
    }

    public StockPriceFunction getStockPriceFunction(Integer stockID){
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM stockPriceFunction WHERE stockID = ?",
                    new String[]{String.valueOf(stockID)});

            if (cursor.moveToFirst()) {
                Integer stockPriceHistoryID = cursor.getInt(cursor.getColumnIndexOrThrow("stockPriceHistoryID"));
                String[] amplitudesString = cursor.getString(cursor.getColumnIndexOrThrow("amplitudes")).split(",");
                Double[] amplitudes = Arrays.stream(amplitudesString)
                        .map(Double::parseDouble)
                        .toArray(Double[]::new);

                String[] frequenciesString = cursor.getString(cursor.getColumnIndexOrThrow("frequencies")).split(",");
                Double[] frequencies = Arrays.stream(frequenciesString)
                        .map(Double::parseDouble)
                        .toArray(Double[]::new);

                return new StockPriceFunction(stockPriceHistoryID, amplitudes, frequencies, stockID);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    public Double getCurrentStockPrice(Integer stockID){
        Integer timeStamp = Game.getInstance().getCurrentTimeStamp();
        StockPriceFunction stockPriceFunction = getStockPriceFunction(stockID);
        if (stockPriceFunction == null) return 0.0;

        Double priceChange = stockPriceFunction.getCurrentPriceChange(timeStamp);
        Stock stock = getStock(stockID);
        if (stock == null) return 0.0;

        return stock.getCurrentPrice() + priceChange;
    }

    public List<Integer> getAllStockIDs() {
        List<Integer> stockIDs = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.query("stocks", new String[]{"stockID"}, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                    stockIDs.add(stockID);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        return stockIDs;
    }

    public void setUpChapterStock() {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM chapter_stock", null);
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                return;
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        Map<Integer, List<String>> chapterStocks = new HashMap<>();
        chapterStocks.put(1, Arrays.asList("CRNB", "MHCD", "PEAR","GPLX","ORNG","BNNF","ISAM","CHRP","LMTC","OCSD","RDTN","FRBT","PNOS","NSCM","ZYND","FAUD","TESL","PLLC","ESKM","SNKS"));
        chapterStocks.put(2, Arrays.asList("CRNB", "MHCD", "PEAR","GPLX","ORNG","BNNF","ISAM","CHRP","LMTC","OCSD","RDTN","FRBT","PNOS","NSCM","ZYND","FAUD","TESL","GDBK", "MRGS","LB20","SCMP","JPMG","BRST","CTRB","HDBC","PNZI","DMHC","FAUD","BNZO","HLIX","IRCL","SKLH"));
        chapterStocks.put(3, Arrays.asList("CRNB", "MHCD", "PEAR","GPLX","ORNG","BNNF","ISAM","CHRP","LMTC","OCSD","RDTN","FRBT","PNOS","NSCM","ZYND","FAUD","TESL","DGCS", "INST", "BTCN", "HODL","SHDY","MNBK","PUMP","ELNM","ZRCN","BNNA","BGNI","HDHP","GLOW","HVHP","FAUD"));
        chapterStocks.put(4, Arrays.asList("SKHA", "EJTN","CLDN","WGIT","PRLC","JTLG","YLVC","ARFO","BGBL","NVLT","TTCK","FLIK","SLPC","VRRL","FAUD","MDMS","WNDO","GKRT","SKFI","TESL"));
        chapterStocks.put(5, Arrays.asList("TKAI","NRND","DFMD","PSSE","CGSM","PRTA","AGPT","WKBT","RBLB","PRBL","CLAI","FAUD","VRRL","TESL"));

        for (Map.Entry<Integer, List<String>> entry : chapterStocks.entrySet()) {
            int chapterID = entry.getKey();
            List<String> symbols = entry.getValue();

            for (String symbol : symbols) {
                cursor = null;
                try {
                    cursor = db.rawQuery("SELECT stockID FROM stocks WHERE symbol = ?", new String[]{symbol});
                    if (cursor.moveToFirst()) {
                        int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                        ContentValues values = new ContentValues();
                        values.put("chapterID", chapterID);
                        values.put("stockID", stockID);
                        db.insert("chapter_stock", null, values);
                    }
                } finally {
                    if (cursor != null) cursor.close();
                }
            }
        }
    }

    public List<Stock> getChapterStock() {
        setUpChapterStock();
        List<Stock> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT s.stockID, s.stockName, s.symbol, s.category, s.description, s.price " +
                    "FROM stocks s " +
                    "JOIN chapter_stock cs ON cs.stockID = s.stockID " +
                    "WHERE cs.chapterID = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(Game.currentChapter)});

            while (cursor.moveToNext()) {
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, currentPrice);
                list.add(stock);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public List<Stock> getFilteredStockM(String filterCategory) {
        List<Stock> filteredList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT s.stockID, s.stockName, s.symbol, s.category, s.description, s.price " +
                    "FROM stocks s " +
                    "JOIN chapter_stock cs ON cs.stockID = s.stockID " +
                    "WHERE cs.chapterID = ? AND s.category = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(Game.currentChapter), filterCategory});

            while (cursor.moveToNext()) {
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, currentPrice);
                filteredList.add(stock);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return filteredList;
    }

    public List<Stock> searchStocksM(String searchCriteria) {
        List<Stock> results = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT s.stockID, s.stockName, s.symbol, s.category, s.description, s.price " +
                    "FROM stocks s " +
                    "JOIN chapter_stock cs ON cs.stockID = s.stockID " +
                    "WHERE cs.chapterID = ? AND s.stockname LIKE ? COLLATE NOCASE";
            cursor = db.rawQuery(query, new String[]{String.valueOf(Game.currentChapter), "%" + searchCriteria + "%"});

            while (cursor.moveToNext()) {
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, currentPrice);
                results.add(stock);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return results;
    }

    public List<Stock> combinedSearchM(String filterCategory, String searchCriteria) {
        List<Stock> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT s.stockID, s.stockName, s.symbol, s.category, s.description, s.price " +
                    "FROM chapter_stock cs " +
                    "JOIN stocks s ON cs.stockID = s.stockID " +
                    "WHERE cs.chapterID = ? AND s.category = ? AND s.stockname LIKE ? COLLATE NOCASE";
            cursor = db.rawQuery(query, new String[]{String.valueOf(Game.currentChapter), filterCategory, "%" + searchCriteria + "%"});

            while (cursor.moveToNext()) {
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, currentPrice);
                result.add(stock);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return result;
    }

    // User related methods

    public void setUser(User user){
        String fName = user.getUserFirstName();
        String lName = user.getUserLastName();
        String username = user.getUserUsername();
        String password = user.getUserPassword();
        Double balance = user.getUserBalance();

        String sql = "INSERT INTO users (userFName, userLName, username, password, balance) VALUES (?, ?, ?, ?, ?)";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, fName);
        stmt.bindString(2, lName);
        stmt.bindString(3, username);
        stmt.bindString(4, password);
        stmt.bindDouble(5, balance);
        stmt.executeInsert();

        String sql2 = "INSERT INTO portfolios (username) VALUES (?)";
        SQLiteStatement stmt2 = db.compileStatement(sql2);
        stmt2.bindString(1, username);
        stmt2.executeInsert();
    }

    public User getUser(String username){
        User user = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT userFName, userLName, password, balance FROM users WHERE username = ?",
                    new String[]{username}
            );
            if (cursor.moveToFirst()) {
                String fName = cursor.getString(cursor.getColumnIndexOrThrow("userFName"));
                String lName = cursor.getString(cursor.getColumnIndexOrThrow("userLName"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));
                user = new User(username, fName, lName, password, balance);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return user;
    }

    public void updateBalance(double balance, String username){
        String sql = "UPDATE users SET balance = ? WHERE username = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindDouble(1, balance);
        stmt.bindString(2, username);
        stmt.executeUpdateDelete();
    }

    public boolean userExists(String username) {
        boolean exists = false;
        Cursor cursor = null;
        try {
            String query = "SELECT 1 FROM users WHERE username = ?";
            cursor = db.rawQuery(query, new String[]{username});
            exists = cursor.getCount() > 0;
        } finally {
            if (cursor != null) cursor.close();
        }
        return exists;
    }

    public void updateUser(String username, String name, String surname, String passw) {
        String query = "UPDATE users SET userFName = ?, userLName = ?, password = ? WHERE username = ?";
        db.execSQL(query, new Object[]{name, surname, passw, username});
    }

    // Portfolio related methods

    public int getPortfolioID(String username) {
        int portfolioID = -1;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT portfolioID FROM portfolios WHERE username = ?", new String[]{username});
            if (cursor.moveToFirst()) {
                portfolioID = cursor.getInt(cursor.getColumnIndexOrThrow("portfolioID"));
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return portfolioID;
    }

    public List<PortfolioStock> getPortfolio(String username) {
        List<PortfolioStock> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT ps.portfolioID, ps.quantity, ps.buyPrice, ps.buyDate, " +
                    "s.stockID, s.stockName, s.symbol, s.category, s.description, s.price " +
                    "FROM portfolioStock ps " +
                    "JOIN portfolios p ON ps.portfolioID = p.portfolioID " +
                    "JOIN stocks s ON ps.stockID = s.stockID " +
                    "WHERE p.username = ?";
            cursor = db.rawQuery(query, new String[]{username});

            while (cursor.moveToNext()) {
                int portfolioID = cursor.getInt(cursor.getColumnIndexOrThrow("portfolioID"));
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double buyPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("buyPrice"));
                String buyDate = cursor.getString(cursor.getColumnIndexOrThrow("buyDate"));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, currentPrice);
                PortfolioStock ps = new PortfolioStock(portfolioID, stock, quantity, buyPrice, buyDate);
                list.add(ps);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return list;
    }

    public List<PortfolioStock> getFilteredPortfolioP(String filterCategory, String username) {
        List<PortfolioStock> filteredList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT ps.portfolioID, ps.quantity, ps.buyPrice, ps.buyDate, " +
                    "s.stockID, s.stockName, s.symbol, s.category, s.description, s.price " +
                    "FROM portfolioStock ps " +
                    "JOIN portfolios p ON ps.portfolioID = p.portfolioID " +
                    "JOIN stocks s ON ps.stockID = s.stockID " +
                    "WHERE p.username = ? AND s.category = ?";
            cursor = db.rawQuery(query, new String[]{username, filterCategory});

            while (cursor.moveToNext()) {
                int portfolioID = cursor.getInt(cursor.getColumnIndexOrThrow("portfolioID"));
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double buyPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("buyPrice"));
                String buyDate = cursor.getString(cursor.getColumnIndexOrThrow("buyDate"));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, currentPrice);
                PortfolioStock ps = new PortfolioStock(portfolioID, stock, quantity, buyPrice, buyDate);
                filteredList.add(ps);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return filteredList;
    }

    public List<PortfolioStock> searchPortfolioStocks(String searchCriteria, String username) {
        List<PortfolioStock> results = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT ps.portfolioID, ps.quantity, ps.buyPrice, ps.buyDate, " +
                    "s.stockID, s.stockName, s.symbol, s.category, s.description, s.price " +
                    "FROM portfolioStock ps " +
                    "JOIN portfolios p ON ps.portfolioID = p.portfolioID " +
                    "JOIN stocks s ON ps.stockID = s.stockID " +
                    "WHERE p.username = ? AND s.stockname LIKE ? COLLATE NOCASE";
            cursor = db.rawQuery(query, new String[]{username, "%" + searchCriteria + "%"});

            while (cursor.moveToNext()) {
                int portfolioID = cursor.getInt(cursor.getColumnIndexOrThrow("portfolioID"));
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double buyPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("buyPrice"));
                String buyDate = cursor.getString(cursor.getColumnIndexOrThrow("buyDate"));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, currentPrice);
                PortfolioStock ps = new PortfolioStock(portfolioID, stock, quantity, buyPrice, buyDate);
                results.add(ps);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return results;
    }

    public List<PortfolioStock> combinedSearchP(String filterCategory, String searchCriteria, String username) {
        List<PortfolioStock> results = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT ps.portfolioID, ps.quantity, ps.buyPrice, ps.buyDate, " +
                    "s.stockID, s.stockName, s.symbol, s.category, s.description, s.price " +
                    "FROM portfolioStock ps " +
                    "JOIN portfolios p ON ps.portfolioID = p.portfolioID " +
                    "JOIN stocks s ON ps.stockID = s.stockID " +
                    "WHERE p.username = ? AND s.category = ? AND s.stockname LIKE ? COLLATE NOCASE";
            cursor = db.rawQuery(query, new String[]{username, filterCategory, "%" + searchCriteria + "%"});

            while (cursor.moveToNext()) {
                int portfolioID = cursor.getInt(cursor.getColumnIndexOrThrow("portfolioID"));
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double buyPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("buyPrice"));
                String buyDate = cursor.getString(cursor.getColumnIndexOrThrow("buyDate"));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, currentPrice);
                PortfolioStock ps = new PortfolioStock(portfolioID, stock, quantity, buyPrice, buyDate);
                results.add(ps);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return results;
    }

    public Double getQuantity(int portfolioID, int stockID) {
        Double qty = 0.0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT quantity FROM portfolioStock WHERE portfolioID = ? AND stockID = ?",
                    new String[]{String.valueOf(portfolioID), String.valueOf(stockID)});
            if (cursor.moveToFirst()) {
                qty = cursor.getDouble(cursor.getColumnIndexOrThrow("quantity"));
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return qty;
    }

    // Transaction methods

    public boolean buyStock(String username, int stockID, int quantity, double price) {
        db.beginTransaction();
        try {
            int portfolioID = getPortfolioID(username);
            BigDecimal totalCost = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(price));

            User user = getUser(username);
            BigDecimal newBalance = BigDecimal.valueOf(user.getUserBalance()).subtract(totalCost);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                Log.d("DB_LOG", "Insufficient funds for user: " + username);
                return false;
            }

            updateBalance(newBalance.doubleValue(), username);
            insertTransaction(username, stockID, "BUY", quantity, BigDecimal.valueOf(price));

            Cursor cursor = null;
            try {
                cursor = db.rawQuery("SELECT quantity FROM portfolioStock WHERE portfolioID = ? AND stockID = ?",
                        new String[]{String.valueOf(portfolioID), String.valueOf(stockID)});

                if (cursor.moveToFirst()) {
                    int existingQty = cursor.getInt(0);
                    int updatedQty = existingQty + quantity;

                    SQLiteStatement stmt = db.compileStatement("UPDATE portfolioStock SET quantity = ?, buyPrice = ?, buyDate = date('now') WHERE portfolioID = ? AND stockID = ?");
                    stmt.bindLong(1, updatedQty);
                    stmt.bindDouble(2, price);
                    stmt.bindLong(3, portfolioID);
                    stmt.bindLong(4, stockID);
                    stmt.executeUpdateDelete();
                } else {
                    SQLiteStatement stmt = db.compileStatement("INSERT INTO portfolioStock (portfolioID, stockID, quantity, buyPrice, buyDate) VALUES (?, ?, ?, ?, date('now'))");
                    stmt.bindLong(1, portfolioID);
                    stmt.bindLong(2, stockID);
                    stmt.bindLong(3, quantity);
                    stmt.bindDouble(4, price);
                    stmt.executeInsert();
                }
            } finally {
                if (cursor != null) cursor.close();
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DB_LOG", "Buy stock failed: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }
    public boolean sellStock(String username, int stockID, int quantityToSell, double price) {
        db.beginTransaction();
        try {
            int portfolioID = getPortfolioID(username);
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("SELECT quantity FROM portfolioStock WHERE portfolioID = ? AND stockID = ?",
                        new String[]{String.valueOf(portfolioID), String.valueOf(stockID)});

                if (!cursor.moveToFirst()) {
                    Log.d("DB_LOG", "User " + username + " does not own stockID=" + stockID);
                    return false;
                }

                int ownedQty = cursor.getInt(0);
                if (ownedQty < quantityToSell) {
                    Log.d("DB_LOG", "User " + username + " owns only " + ownedQty + " shares, cannot sell " + quantityToSell);
                    return false;
                }

                BigDecimal saleRevenue = BigDecimal.valueOf(quantityToSell).multiply(BigDecimal.valueOf(price));
                User user = getUser(username);
                BigDecimal newBalance = BigDecimal.valueOf(user.getUserBalance()).add(saleRevenue);
                updateBalance(newBalance.doubleValue(), username);
                Log.d("DB_LOG", "User " + username + " balance updated to: " + newBalance);

                insertTransaction(username, stockID, "SELL", quantityToSell, BigDecimal.valueOf(price));

                int remainingQty = ownedQty - quantityToSell;
                if (remainingQty == 0) {
                    SQLiteStatement stmt = db.compileStatement("DELETE FROM portfolioStock WHERE portfolioID = ? AND stockID = ?");
                    stmt.bindLong(1, portfolioID);
                    stmt.bindLong(2, stockID);
                    stmt.executeUpdateDelete();
                    Log.d("DB_LOG", "Removed stockID=" + stockID + " from portfolio (sold all shares)");
                } else {
                    SQLiteStatement stmt = db.compileStatement("UPDATE portfolioStock SET quantity = ? WHERE portfolioID = ? AND stockID = ?");
                    stmt.bindLong(1, remainingQty);
                    stmt.bindLong(2, portfolioID);
                    stmt.bindLong(3, stockID);
                    stmt.executeUpdateDelete();
                    Log.d("DB_LOG", "Updated stockID=" + stockID + " to " + remainingQty + " shares");
                }
            } finally {
                if (cursor != null) cursor.close();
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e("DB_LOG", "Sell stock failed: " + e.getMessage());
            return false;
        } finally {
            db.endTransaction();
        }
    }

    private void insertTransaction(String username, long stockID, String transactionType, int quantity, BigDecimal price) {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String sql = "INSERT INTO transaction_history (username, stockID, transactionType, quantity, price, transactionDate) VALUES (?, ?, ?, ?, ?, ?)";
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindString(1, username);
            stmt.bindLong(2, stockID);
            stmt.bindString(3, transactionType);
            stmt.bindLong(4, quantity);
            stmt.bindDouble(5, price.doubleValue());
            stmt.bindString(6, timestamp);
            stmt.executeInsert();
            Log.d("DB_LOG", "Transaction recorded: " + transactionType + " " + quantity + " shares of stockID=" + stockID);
        } catch (Exception e) {
            Log.e("DB_LOG", "Failed to insert transaction: " + e.getMessage());
        }
    }

    public List<Transaction> getTransactionHistory(String username) {
        List<Transaction> transactions = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT th.transactionID, th.username, th.stockID, th.transactionType, " +
                    "th.quantity, th.price, th.transactionDate, s.stockName, s.symbol " +
                    "FROM transaction_history th " +
                    "JOIN stocks s ON th.stockID = s.stockID " +
                    "WHERE th.username = ? " +
                    "ORDER BY th.transactionDate DESC";
            cursor = db.rawQuery(query, new String[]{username});

            while (cursor.moveToNext()) {
                long transactionID = cursor.getLong(cursor.getColumnIndexOrThrow("transactionID"));
                long stockID = cursor.getLong(cursor.getColumnIndexOrThrow("stockID"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("transactionType"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                BigDecimal priceVal = BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                String transactionDate = cursor.getString(cursor.getColumnIndexOrThrow("transactionDate"));

                Date date = null;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    date = sdf.parse(transactionDate);
                } catch (Exception e) {
                    Log.e("DB_LOG", "Error parsing transaction date: " + e.getMessage());
                    date = new Date();
                }

                Transaction tx = new Transaction(transactionID, username, stockID, symbol, type, quantity, priceVal, date);
                transactions.add(tx);
            }
            Log.d("DB_LOG", "Retrieved " + transactions.size() + " transactions for user " + username);
        } finally {
            if (cursor != null) cursor.close();
        }
        return transactions;
    }

    public void updateMarketFactor(int stockID, double marketFactor){
        String sql = "UPDATE stockPriceFunction SET marketFactor = ? WHERE stockID = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindDouble(1, marketFactor);
        stmt.bindLong(2, stockID);
        stmt.executeUpdateDelete();
    }

    public List<MarketEvent> getMarketEvents() {
        List<MarketEvent> marketEvents = new ArrayList<>();
        Cursor cursor = null;
        try {
            String sql = "SELECT * FROM marketEvents";
            cursor = db.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                int marketEventID = cursor.getInt(cursor.getColumnIndexOrThrow("eventID"));
                int chapterID = cursor.isNull(cursor.getColumnIndexOrThrow("chapterID"))
                        ? -1
                        : cursor.getInt(cursor.getColumnIndexOrThrow("chapterID"));
                int minigameID = cursor.isNull(cursor.getColumnIndexOrThrow("minigameID"))
                        ? -1
                        : cursor.getInt(cursor.getColumnIndexOrThrow("minigameID"));
                int eventDuration = cursor.getInt(cursor.getColumnIndexOrThrow("eventDuration"));
                String eventTitle = cursor.getString(cursor.getColumnIndexOrThrow("eventTitle"));
                String eventInfo = cursor.getString(cursor.getColumnIndexOrThrow("eventInfo"));
                int chapterPresent = cursor.getInt(cursor.getColumnIndexOrThrow("chapterPresent"));
                String marketFactorsString = cursor.getString(cursor.getColumnIndexOrThrow("marketFactors"));

                MarketEvent event = new MarketEvent(
                        marketEventID,
                        chapterID,
                        minigameID,
                        eventDuration,
                        eventTitle,
                        eventInfo
                );
                marketEvents.add(event);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return marketEvents;
    }
}