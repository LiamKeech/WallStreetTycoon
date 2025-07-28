package com.example.wallstreettycoon.databaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.wallstreettycoon.portfolio.PortfolioStock;
import com.example.wallstreettycoon.stock.Stock;
import com.example.wallstreettycoon.stock.StockPriceFunction;
import com.example.wallstreettycoon.transaction.Transaction;
import com.example.wallstreettycoon.useraccount.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseUtil {
    public static DatabaseCreator dbCreator;
    public static SQLiteDatabase db;
    public DatabaseUtil(Context context) {
        dbCreator = new DatabaseCreator(context);
        db = dbCreator.getWritableDatabase();
    }

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
        db.close();

        return stockList;
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

        return stockPriceHistories;
    }

    public Double getCurrentStockPrice(Integer stockID, Integer timeStamp){
        //Stock stock = getStockList().get(stockID);
        StockPriceFunction stockPriceFunction = getStockPriceFunctions().get(stockID);

        //I am rounding using BigDecimal for better precision

        Double value = stockPriceFunction.getCurrentPrice(timeStamp);
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

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
    }

    public User getUser(String username){
        Cursor cursor = db.rawQuery(
                "SELECT userFName, userLName, password, balance FROM users WHERE username = ?",
                new String[]{username}
        );

        User user = null;

        if (cursor != null && cursor.moveToFirst()) {
            String fName = cursor.getString(cursor.getColumnIndexOrThrow("userFName"));
            String lName = cursor.getString(cursor.getColumnIndexOrThrow("userLName"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            double balance = cursor.getDouble(cursor.getColumnIndexOrThrow("balance"));

            user = new User(username, fName, lName, password, balance);
        }

        if (cursor != null) {
            cursor.close();
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
        String query = "SELECT 1 FROM users WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void updateUser(String username, String name, String surname, String passw)
    {
        String query = "UPDATE users SET userFName = name, userLname = surname, password = passw WHERE username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        cursor.close();
    }

    // Portfolio related methods

    public int getPortfolioID(String username) {
        Cursor cursor = db.rawQuery("SELECT portfolioID FROM portfolio WHERE username = ?", new String[]{username});
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

    public void updatePortfolio(Stock stock){

    }

     //Transaction
    public void processTransaction(Transaction tx) {
        int currentQty = getQuantity(tx.getUsername(), tx.getStockSymbol());
        int changeInQty = tx.getType().equalsIgnoreCase("BUY")
                ? tx.getQuantity()
                : -tx.getQuantity();
        int newQty = currentQty + changeInQty;

        if (newQty <= 0) {
            // remove if zero or negative
            db.delete("portfolioStock", "username = ? AND stockSymbol = ?",
                    new String[]{ tx.getUsername(), tx.getStockSymbol() });
        } else {
            // update
            if (currentQty == 0) {
                SQLiteStatement ins = db.compileStatement("INSERT INTO portfolioStock (username, stockSymbol, quantity) VALUES (?, ?, ?);");
                ins.bindString(1, tx.getUsername());
                ins.bindString(2, tx.getStockSymbol());
                ins.bindLong(3, newQty);
                ins.executeInsert();
            } else {// insert
                SQLiteStatement upd = db.compileStatement("UPDATE portfolioStock SET quantity = ? WHERE username = ? AND stockSymbol = ?;");
                upd.bindLong(1, newQty);
                upd.bindString(2, tx.getUsername());
                upd.bindString(3, tx.getStockSymbol());
                upd.executeUpdateDelete();
            }
        }
    }

    private int getQuantity(String username, String symbol) {
        Cursor c = db.rawQuery("SELECT quantity FROM portfolioStock WHERE username = ? AND stockSymbol = ?;", new String[]{ username, symbol });
        int qty = 0;
        if (c.moveToFirst()) {
            qty = c.getInt(c.getColumnIndexOrThrow("quantity"));
        }
        c.close();
        return qty;
    }
}
