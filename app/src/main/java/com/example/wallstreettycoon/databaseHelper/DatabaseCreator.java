package com.example.wallstreettycoon.databaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wallstreettycoon.stock.Stock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCreator extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "localdata.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseCreator(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //User table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "userID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "userFName TEXT, userLName TEXT, username TEXT, password TEXT, balance REAL)"
        );

        //Stock table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS stocks (" +
                        "stockID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "stockName TEXT, symbol TEXT, category TEXT, description TEXT, price REAL)"
        );

        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Apple Inc.', 'AAPL', 'Technology', 'Leading tech company known for iPhones and Macs.', 198.23)");
        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Alphabet Inc.', 'GOOGL', 'Technology', 'Parent company of Google.', 2745.30)");
        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Amazon.com Inc.', 'AMZN', 'E-Commerce', 'Largest online retailer and cloud provider.', 3450.10)");
        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Tesla Inc.', 'TSLA', 'Automotive', 'Electric vehicle and clean energy company.', 720.54)");
        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Microsoft Corp.', 'MSFT', 'Technology', 'Developer of Windows OS and Office Suite.', 310.44)");
        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Meta Platforms Inc.', 'META', 'Social Media', 'Owner of Facebook, Instagram, and WhatsApp.', 355.89)");
        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Netflix Inc.', 'NFLX', 'Entertainment', 'Streaming service provider.', 435.77)");
        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Coca-Cola Co.', 'KO', 'Consumer Goods', 'Beverage company known for Coke.', 63.25)");
        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('NVIDIA Corp.', 'NVDA', 'Semiconductors', 'Graphics processing and AI chips manufacturer.', 128.88)");
        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Johnson & Johnson', 'JNJ', 'Healthcare', 'Global healthcare and pharmaceutical company.', 170.55)");

        // StockPriceHistory table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS stockPriceHistory (" +
                        "stockPriceHistoryID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "amplitudes TEXT, " +
                        "frequencies TEXT, " +
                        "FOREIGN KEY (stockID) REFERENCES stocks(stockID))"
        );

        //TODO populate stockprice history table


        // Chapter table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS chapters (" +
                        "chapterID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "chapterName TEXT, " +
                        "description TEXT)"
        );

        //TODO populate chapter table

        // MarketEvent table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS marketEvents (" +
                        "eventID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "chapterID INTEGER, " +
                        "minigameID INTEGER, " +
                        "eventInfo TEXT, " +
                        "chapterPresent INTEGER, " +
                        "FOREIGN KEY (chapterID) REFERENCES chapters(chapterID), " +
                        "FOREIGN KEY (minigameID) REFERENCES minigames(minigameID))"
        );

        //TODO populate market event table

        // Minigame table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS minigames (" +
                        "minigameID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "minigameName TEXT)"
        );

        //TODO populate minigames table

        // Portfolio table, will be empty on creation
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS portfolios (" +
                        "portfolioID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "userID INTEGER, " +
                        "FOREIGN KEY (userID) REFERENCES users(userID))"
        );

        // PortfolioStock table (many-to-many between portfolio and stock)
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS portfolioStock (" +
                        "portfolioID INTEGER, " +
                        "stockID INTEGER, " +
                        "quantity INTEGER, " +
                        "PRIMARY KEY (portfolioID, stockID), " +
                        "FOREIGN KEY (portfolioID) REFERENCES portfolios(portfolioID), " +
                        "FOREIGN KEY (stockID) REFERENCES stocks(stockID))"
        );

        // Chapter_Stock join table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS chapter_stock (" +
                        "ChapterStockID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "chapterID INTEGER, " +
                        "stockID INTEGER, " +
                        "FOREIGN KEY (chapterID) REFERENCES chapters(chapterID), " +
                        "FOREIGN KEY (stockID) REFERENCES stocks(stockID))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle schema migrations here
    }

    //getter for stockList
    public List<Stock> getStockList(){
        List<Stock> stockList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM stocks", null);
        if (cursor.moveToFirst()) {
            do {
                int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow("symbol"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                BigDecimal stockPrice = new BigDecimal(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));

                Stock stock = new Stock(stockID, stockName, symbol, category, description, stockPrice);
                stockList.add(stock);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return stockList;
    }
}
