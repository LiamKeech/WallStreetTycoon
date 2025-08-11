package com.example.wallstreettycoon.databaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.wallstreettycoon.portfolio.PortfolioStock;
import com.example.wallstreettycoon.stock.Stock;
import com.example.wallstreettycoon.stock.StockPriceFunction;
import com.example.wallstreettycoon.transaction.Transaction;
import com.example.wallstreettycoon.useraccount.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class DatabaseUtil {
    public static DatabaseCreator dbCreator;
    public static SQLiteDatabase db;
    public DatabaseUtil(Context context) {
        dbCreator = new DatabaseCreator(context);
        db = dbCreator.getWritableDatabase();
    }

    // Stock related methods

    //getter for stockList
    public List<Stock> getStockList(){
        List<Stock> stockList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM stocks", null);
        if (cursor.moveToFirst()) {
            do {
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double stockPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, stockPrice);
                stockList.add(stock);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return stockList;
    }

    public List<Stock> getFilteredStock(String filterCategory) {
        List<Stock> filteredList = new ArrayList<>();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM stocks WHERE category = ?",
                new String[]{filterCategory}
        );

        if (cursor.moveToFirst()) {
            do {
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double stockPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, stockPrice);
                filteredList.add(stock);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return filteredList;
    }

    public List<Stock> searchStocks(String query)
    {
        List<Stock> results = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM stocks WHERE stockName LIKE ? COLLATE NOCASE", new String[]{"%"+query+"%"});

        if(cursor.moveToFirst()) {
            do {
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                Double stockPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, stockPrice);
                results.add(stock);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return results;
    }

    public Stock getStock(Integer stockID){
        for(Stock stock : getStockList()){
            if(stock.getStockID().equals(stockID)){
                return stock;
            }
        }
        return null;
    }

    public String getStockSymbol (int stockID) {
        Cursor cursor = db.rawQuery("SELECT symbol FROM stocks WHERE stockID = ?", new String[]{String.valueOf(stockID)});
        String symbol = null;
        if (cursor.moveToFirst()) {
            do {
                symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return symbol;
    }

    //getter for stockPriceFunction
    public List<StockPriceFunction> getStockPriceFunctions(){
        List<StockPriceFunction> stockPriceHistories = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM stockPriceFunction",null);

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
        cursor.close();
        return stockPriceHistories;
    }

    public StockPriceFunction getStockPriceFunction(Integer stockID){
        for (StockPriceFunction stockPriceFunction : getStockPriceFunctions()) {
            if (stockPriceFunction.getStockID().equals(stockID)) {
                return stockPriceFunction;
            }
        }
        return null;
    }

    public Double getCurrentStockPrice(Integer stockID, Integer timeStamp){
        //Stock stock = getStockList().get(stockID);
        //StockPriceFunction stockPriceFunction = getStockPriceFunctions().get(stockID);
        StockPriceFunction stockPriceFunction = getStockPriceFunction(stockID);

        //I am rounding using BigDecimal for better precision

        Double value = stockPriceFunction.getCurrentPrice(timeStamp);
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //buy stock
    public boolean buyStock(String username, int stockID, int quantity, double price) {
        int portfolioID = getPortfolioID(username);
        BigDecimal totalCost = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(price));

        // Calculate balance
        User user = getUser(username);
        BigDecimal newBalance = BigDecimal.valueOf(user.getUserBalance()).subtract(totalCost);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            Log.d("DB_LOG", "Insufficient funds for user: " + username);
            return false; // Insufficient funds
        }

        ////
        updateBalance(newBalance.doubleValue(), username);
        Log.d("DB_LOG", "User " + username + " balance updated to: " + newBalance);

        // Check if stock already in portfolio
        Cursor cursor = db.rawQuery("SELECT quantity FROM portfolioStock WHERE portfolioID = ? AND stockID = ?", new String[]{String.valueOf(portfolioID), String.valueOf(stockID)});

        if (cursor.moveToFirst()) {
            int existingQty = cursor.getInt(0);
            int updatedQty = existingQty + quantity;

            SQLiteStatement stmt = db.compileStatement("UPDATE portfolioStock SET quantity = ?, buyPrice = ?, buyDate = date('now') WHERE portfolioID = ? AND stockID = ?");
            stmt.bindLong(1, updatedQty);
            stmt.bindDouble(2, price);
            stmt.bindLong(3, portfolioID);
            stmt.bindLong(4, stockID);
            stmt.executeUpdateDelete(); //method is used for updates
            Log.d("DB_LOG", "Updated existing stock: stockID=" + stockID + ", newQty=" + updatedQty + " for user " + username);
        } else {
            SQLiteStatement stmt = db.compileStatement("INSERT INTO portfolioStock (portfolioID, stockID, quantity, buyPrice, buyDate) VALUES (?, ?, ?, ?, date('now'))");
            stmt.bindLong(1, portfolioID);
            stmt.bindLong(2, stockID);
            stmt.bindLong(3, quantity);
            stmt.bindDouble(4, price);
            stmt.executeInsert();
            Log.d("DB_LOG", "Inserted new stock: stockID=" + stockID + ", qty=" + quantity + " for user " + username);
            }
        cursor.close();
        return true;
    }


    //sell stock
    public boolean sellStock(String username, int stockID, int quantityToSell, double price) {
        int portfolioID = getPortfolioID(username);

        // check how many shares user owns
        Cursor cursor = db.rawQuery("SELECT quantity FROM portfolioStock WHERE portfolioID = ? AND stockID = ?", new String[]{String.valueOf(portfolioID), String.valueOf(stockID)});

        if (cursor.moveToFirst()) {
            int existingQty = cursor.getInt(0);
            if (quantityToSell > existingQty) {
                cursor.close();
                Log.d("DB_LOG", "Insufficient shares for stockID=" + stockID + " for user " + username);
                return false; // insufficient shares
            }

            BigDecimal totalValue = BigDecimal.valueOf(quantityToSell).multiply(BigDecimal.valueOf(price));
            User user = getUser(username);
            if (user == null) {
                Log.e("DB_LOG", "User not found: " + username);
                return false;
            }
            updateBalance(user.getUserBalance() + totalValue.doubleValue(), username);
            Log.d("DB_LOG", "User " + username + " balance updated by: " + totalValue);

            int remainingQty = existingQty - quantityToSell;
            if (remainingQty == 0) { //remove from table if all shares sold
                SQLiteStatement stmt = db.compileStatement("DELETE FROM portfolioStock WHERE portfolioID = ? AND stockID = ?");
                stmt.bindLong(1, portfolioID);
                stmt.bindLong(2, stockID);
                stmt.executeUpdateDelete();
                Log.d("DB_LOG", "Stock fully sold: stockID=" + stockID + " removed from portfolio for user " + username);
            } else {
                SQLiteStatement stmt = db.compileStatement("UPDATE portfolioStock SET quantity = ? WHERE portfolioID = ? AND stockID = ?"); //update quantity with shares
                stmt.bindLong(1, remainingQty);
                stmt.bindLong(2, portfolioID);
                stmt.bindLong(3, stockID);
                stmt.executeUpdateDelete();
                Log.d("DB_LOG", "Stock partially sold: stockID=" + stockID + ", remainingQty=" + remainingQty + " for user " + username);
            }
        }
        cursor.close();
        return true;
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

        //create portfolio for user
        String sql2 = "INSERT INTO portfolios (username) VALUES (?)";
        SQLiteStatement stmt2 = db.compileStatement(sql2);
        stmt2.bindString(1, username);
        stmt2.executeInsert();
    }

    public User getUser(String username){
        Cursor cursor = db.rawQuery(
                "SELECT userFName, userLName, password, balance FROM users WHERE username = ?",
                new String[]{username}
        );

        User user = null;

        if (cursor.moveToFirst()) {
            String fName = cursor.getString(cursor.getColumnIndexOrThrow("userFName"));
            String lName = cursor.getString(cursor.getColumnIndexOrThrow("userLName"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));

            user = new User(username, fName, lName, password, balance);
        }
        cursor.close();


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
        String query = "SELECT 1 FROM users WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void updateUser(String username, String name, String surname, String passw)
    {
        String query = "UPDATE users SET userFName = ?, userLName = ?, password = ? WHERE username = ?";
        db.execSQL(query, new Object[]{name, surname, passw, username});
    }

    // Portfolio related methods

    public int getPortfolioID(String username) {
        Cursor cursor = db.rawQuery("SELECT portfolioID FROM portfolios WHERE username = ?", new String[]{username});
        int portfolioID = -1;
        if (cursor.moveToFirst()) {
            portfolioID = cursor.getInt(cursor.getColumnIndexOrThrow("portfolioID"));
        }
        cursor.close();
        return portfolioID;
    }

    public List<PortfolioStock> getPortfolio(String username) {
        List<PortfolioStock> list = new ArrayList<>();

        String query = "SELECT ps.portfolioID, ps.stockID, ps.quantity, ps.buyPrice, ps.buyDate " +
                "FROM portfolioStock ps " +
                "JOIN portfolios p ON ps.portfolioID = p.portfolioID " +
                "JOIN stocks s ON ps.stockID = s.stockID " +
                "WHERE p.username = ?";

        Cursor cursor = db.rawQuery(query, new String[]{username});

        while (cursor.moveToNext()) {
            int portfolioID = cursor.getInt(cursor.getColumnIndexOrThrow("portfolioID"));
            int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
            double buyPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("buyPrice"));
            String buyDate = cursor.getString(cursor.getColumnIndexOrThrow("buyDate"));


            // PortfolioStock(int portfolioID, String stockID, int quantity, double buyPrice, String buyDate)
            PortfolioStock ps = new PortfolioStock(
                    portfolioID,
                    String.valueOf(stockID),
                    quantity,
                    buyPrice, buyDate
            );

            list.add(ps);
        }

        cursor.close();
        return list;
    }

     //Transaction

//    private void insertTransaction(String username, long stockID, String transactionType, int quantity, BigDecimal price) {
//         try {
//        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
//        String sql = "INSERT INTO transaction_history (username, stockID, transactionType, quantity, price, transactionDate) VALUES (?, ?, ?, ?, ?, ?)";
//        SQLiteStatement stmt = db.compileStatement(sql);
//        stmt.bindString(1, username);
//        stmt.bindLong(2, stockID);
//        stmt.bindString(3, type);
//        stmt.bindLong(4, quantity);
//        stmt.bindDouble(5, price.doubleValue());
//        stmt.bindString(6, timestamp);
//        stmt.executeInsert();
//        Log.d("DB_LOG", "Transaction recorded: " + type + " " + quantity + " shares of " + stockID);
//    } catch (Exception e) {
//        Log.e("DB_LOG", "Failed to insert transaction: " + e.getMessage());
//    }

//    public List<Transaction> getTransactionHistory(String username) {
//        List<Transaction> transactions = new ArrayList<>();
//
//        String query = "SELECT * FROM transaction_history WHERE username = ? ORDER BY transactionDate DESC";
//
//        Cursor cursor = db.rawQuery(query, new String[]{username});
//        while (cursor.moveToNext()) {
//            long transactionID = cursor.getLong(cursor.getColumnIndexOrThrow("transactionID"));
//            long stockID = cursor.getLong(cursor.getColumnIndexOrThrow("stockID"));
//            String type = cursor.getString(cursor.getColumnIndexOrThrow("transactionType"));
//            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
//            BigDecimal price = BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
//            String transactionDate = cursor.getString(cursor.getColumnIndexOrThrow("transactionDate"));
//
//            Transaction tx = new Transaction(transactionID, username, stockID, type, quantity, price, transactionDate);
//            transactions.add(tx);
//        }
//
//        cursor.close();
//        Log.d("DB_LOG", "Retrieved " + transactions.size() + " transactions for user " + username);
//        return transactions;
//    }
}
