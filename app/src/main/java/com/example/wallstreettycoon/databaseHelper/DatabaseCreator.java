package com.example.wallstreettycoon.databaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wallstreettycoon.stock.Stock;
import com.example.wallstreettycoon.stock.StockPriceFunction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
                        "username TEXT PRIMARY KEY, " +
                        "userFName TEXT, userLName TEXT, password TEXT, balance REAL)"
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

        // StockPriceFunction table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS stockPriceFunction (" +
                        "stockPriceHistoryID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "amplitudes TEXT, " +
                        "frequencies TEXT, " +
                        "stockID INTEGER, " +
                        "FOREIGN KEY (stockID) REFERENCES stocks(stockID)" +
                        ")"
        );

        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
                "('8.2,6.5,4.9,3.3,2.8,2.1,1.7,1.3,1.0,0.8', '0.6,1.1,1.9,2.5,3.2,4.0,5.7,6.8,8.3,9.7', 1)");

        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
                "('7.5,6.0,5.2,4.1,3.0,2.5,1.8,1.2,1.1,0.9', '0.8,1.4,2.2,3.0,3.9,5.1,6.3,7.5,8.4,9.6', 2)");

        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
                "('9.0,5.7,4.6,3.4,2.6,2.2,1.5,1.1,0.7,0.6', '0.5,1.2,2.4,3.3,4.7,6.1,7.2,8.5,9.0,10.3', 3)");

        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
                "('6.4,5.9,4.7,4.0,2.8,2.1,1.6,1.2,1.0,0.5', '1.0,1.8,2.6,3.5,4.3,5.0,6.6,7.3,8.9,9.9', 4)");

        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
                "('8.0,6.3,5.5,4.8,3.6,2.7,2.1,1.4,0.9,0.6', '0.9,1.6,2.8,3.9,4.2,5.4,6.8,7.9,8.6,10.1', 5)");

        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
                "('7.3,5.8,4.5,3.7,3.1,2.0,1.5,1.0,0.8,0.4', '0.7,1.5,2.3,3.7,4.5,5.9,6.4,7.8,8.2,9.5', 6)");

        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
                "('9.2,7.1,5.3,4.4,3.0,2.2,1.7,1.3,0.9,0.5', '0.4,1.7,2.9,3.6,4.8,6.0,7.2,8.0,9.3,10.5', 7)");

        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
                "('6.8,5.4,4.1,3.6,2.7,1.9,1.3,1.1,0.7,0.3', '1.2,2.1,3.2,4.6,5.5,6.7,7.1,8.4,9.0,10.2', 8)");

        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
                "('7.9,6.6,4.2,3.3,2.4,1.8,1.5,1.1,0.8,0.6', '0.3,1.3,2.5,3.4,4.1,5.3,6.5,7.7,9.1,10.0', 9)");

        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
                "('8.5,6.9,5.1,4.2,3.5,2.3,1.9,1.4,1.0,0.7', '0.5,1.9,2.6,3.1,4.4,5.6,6.3,7.5,8.8,10.6', 10)");



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
                        "username TEXT, " +
                        "FOREIGN KEY (username) REFERENCES users(username))"
        );

        // PortfolioStock table (many-to-many between portfolio and stock)
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS portfolioStock (" +
                        "portfolioID INTEGER, " +
                        "stockID INTEGER, " +
                        "quantity INTEGER, " +
                        "buyPrice REAL, " +
                        "buyDate TEXT, " +
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

    }


}
