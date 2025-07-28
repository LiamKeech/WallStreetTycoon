package com.example.wallstreettycoon.databaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wallstreettycoon.stock.Stock;
import com.example.wallstreettycoon.stock.StockPriceFunction;
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
                Double stockPrice = Double.valueOf("price");

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
        db.execSQL("INSERT INTO users (userFName, userLName, username, password, balance) VALUES (fName, lName, username, password, balance)");
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

        return user;
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

    public void updatePortfolio(Stock stock){
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
        int portfolioID = getPortfolioID(username);

        if (portfolioID == -1) return list;

        String query = "SELECT ps.portfolioID, ps.stockID, ps.quantity, ps.buyPrice, ps.buyDate, s.stockID AS stock_ref_id, s.stockName, s.symbol, s.category, s.description FROM portfolioStock ps JOIN stocks s ON ps.stockID = s.stockID WHERE ps.portfolioID = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(portfolioID)});

    }
    public List<Stock> getPortfolio(){
        List<Stock> list = new ArrayList<>();

        return list;
    }
}
