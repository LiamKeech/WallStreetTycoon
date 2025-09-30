package com.example.wallstreettycoon.databaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.stock.Stock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DatabaseCreator extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "localdata.db";
    private static final int DATABASE_VERSION = 2;
    private final Context context;

    private final Random rand;

    public DatabaseCreator(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        rand = new Random();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //User table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "username TEXT PRIMARY KEY, " +
                        "userFName TEXT, userLName TEXT, password TEXT, balance REAL)"
        );

        db.execSQL("INSERT INTO users (username, userFName, userLName, password, balance) VALUES ('admin', 'testFirstName', 'testSurname', 'admin1', 1000000)");

        //Stock table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS stocks (" +
                        "stockID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "stockName TEXT, symbol TEXT, category TEXT, description TEXT, price REAL)"
        );

//        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Apple Inc.', 'AAPL', 'Technology', 'Leading tech company known for iPhones and Macs.', 198.23)");
//        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Alphabet Inc.', 'GOOGL', 'Technology', 'Parent company of Google.', 2745.30)");
//        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Amazon.com Inc.', 'AMZN', 'E-Commerce', 'Largest online retailer and cloud provider.', 3450.10)");
//        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Tesla Inc.', 'TSLA', 'Automotive', 'Electric vehicle and clean energy company.', 720.54)");
//        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Microsoft Corp.', 'MSFT', 'Technology', 'Developer of Windows OS and Office Suite.', 310.44)");
//        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Meta Platforms Inc.', 'META', 'Social Media', 'Owner of Facebook, Instagram, and WhatsApp.', 355.89)");
//        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Netflix Inc.', 'NFLX', 'Entertainment', 'Streaming service provider.', 435.77)");
//        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Coca-Cola Co.', 'KO', 'Consumer Goods', 'Beverage company known for Coke.', 63.25)");
//        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('NVIDIA Corp.', 'NVDA', 'Semiconductors', 'Graphics processing and AI chips manufacturer.', 128.88)");
//        db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('Johnson & Johnson', 'JNJ', 'Healthcare', 'Global healthcare and pharmaceutical company.', 170.55)");


        List<Stock> stockList = readCommaDelimitedStocks(R.raw.stocks);
        for(Stock s: stockList){
            db.execSQL("INSERT INTO stocks (stockName, symbol, category, description, price) VALUES ('" + s.getStockName() + "', '" + s.getSymbol() + "', '" + s.getCategory() + "', '" + s.getDescription()+ "', '" + s.getCurrentPrice() + "')");
        }
        // StockPriceFunction table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS stockPriceFunction (" +
                        "stockPriceHistoryID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "amplitudes TEXT, " +
                        "frequencies TEXT, " +
                        "marketFactor REAL, " +
                        "stockID INTEGER, " +
                        "FOREIGN KEY (stockID) REFERENCES stocks(stockID)" +
                        ")"
        );

        for(int i = 1; i < stockList.size(); i++){
            int stockID = i;
           double[] frequencyArray = generateFrequencies(10);
           double[] amplitudeArray = generateAmplitudes(10);
           String frequencyString = arrayToCommaString(frequencyArray);
           String amplitudeString = arrayToCommaString(amplitudeArray);

            db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, marketFactor, stockID) VALUES (" +
                    "'" + amplitudeString + "', " +
                    "'" + frequencyString + "', " +
                    "0.0, " +
                    stockID + ")");
        }



//        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
//                "('8.2,6.5,4.9,3.3,2.8,2.1,1.7,1.3,1.0,0.8', '0.6,1.1,1.9,2.5,3.2,4.0,5.7,6.8,8.3,9.7', 1)");
//
//        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
//                "('7.5,6.0,5.2,4.1,3.0,2.5,1.8,1.2,1.1,0.9', '0.8,1.4,2.2,3.0,3.9,5.1,6.3,7.5,8.4,9.6', 2)");
//
//        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
//                "('9.0,5.7,4.6,3.4,2.6,2.2,1.5,1.1,0.7,0.6', '0.5,1.2,2.4,3.3,4.7,6.1,7.2,8.5,9.0,10.3', 3)");
//
//        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
//                "('6.4,5.9,4.7,4.0,2.8,2.1,1.6,1.2,1.0,0.5', '1.0,1.8,2.6,3.5,4.3,5.0,6.6,7.3,8.9,9.9', 4)");
//
//        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
//                "('8.0,6.3,5.5,4.8,3.6,2.7,2.1,1.4,0.9,0.6', '0.9,1.6,2.8,3.9,4.2,5.4,6.8,7.9,8.6,10.1', 5)");
//
//        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
//                "('7.3,5.8,4.5,3.7,3.1,2.0,1.5,1.0,0.8,0.4', '0.7,1.5,2.3,3.7,4.5,5.9,6.4,7.8,8.2,9.5', 6)");
//
//        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
//                "('9.2,7.1,5.3,4.4,3.0,2.2,1.7,1.3,0.9,0.5', '0.4,1.7,2.9,3.6,4.8,6.0,7.2,8.0,9.3,10.5', 7)");
//
//        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
//                "('6.8,5.4,4.1,3.6,2.7,1.9,1.3,1.1,0.7,0.3', '1.2,2.1,3.2,4.6,5.5,6.7,7.1,8.4,9.0,10.2', 8)");
//
//        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
//                "('7.9,6.6,4.2,3.3,2.4,1.8,1.5,1.1,0.8,0.6', '0.3,1.3,2.5,3.4,4.1,5.3,6.5,7.7,9.1,10.0', 9)");
//
//        db.execSQL("INSERT INTO stockPriceFunction (amplitudes, frequencies, stockID) VALUES " +
//                "('8.5,6.9,5.1,4.2,3.5,2.3,1.9,1.4,1.0,0.7', '0.5,1.9,2.6,3.1,4.4,5.6,6.3,7.5,8.8,10.6', 10)");



        // Chapter table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS chapters (" +
                        "chapterID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "chapterName TEXT, " +
                        "description TEXT)"
        );

        db.execSQL("INSERT INTO chapters (chapterName, description) VALUES ('Chapter 1: The Dot-Com Boom', 'stocks in technology')");
        db.execSQL("INSERT INTO chapters (chapterName, description) VALUES ('Chapter 2: The Housing Bubble', 'stocks in investments')");
        db.execSQL("INSERT INTO chapters (chapterName, description) VALUES ('Chapter 3: The Crypto Surge', 'stocks in crypto')");
        db.execSQL("INSERT INTO chapters (chapterName, description) VALUES ('Chapter 4: The Corona Crash:', 'stocks in travel and entertainment')");
        db.execSQL("INSERT INTO chapters (chapterName, description) VALUES ('Chapter 5: The AI Revolution', 'stocks in ai platforms')");


        // MarketEvent table
        //TEXT title
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS marketEvents (" +
                        "eventID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "chapterID INTEGER, " +
                        "minigameID INTEGER, " +
                        "eventTitle TEXT, " +
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


        // Transaction history table

        ///database version issue by creating new tables
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS transaction_history (" +
                        "transactionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username TEXT NOT NULL, " +
                        "stockID INTEGER NOT NULL, " +
                        "transactionType TEXT NOT NULL CHECK(transactionType IN ('BUY', 'SELL')), " +
                        "quantity INTEGER NOT NULL, " +
                        "price REAL NOT NULL, " +
                        "transactionDate TEXT NOT NULL, " +
                        "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE, " +
                        "FOREIGN KEY (stockID) REFERENCES stocks(stockID) ON DELETE CASCADE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS transaction_history (" +
                            "transactionID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "username TEXT NOT NULL, " +
                            "stockID INTEGER NOT NULL, " +
                            "transactionType TEXT NOT NULL CHECK(transactionType IN ('BUY', 'SELL')), " +
                            "quantity INTEGER NOT NULL, " +
                            "price REAL NOT NULL, " +
                            "transactionDate TEXT NOT NULL, " +
                            "FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE, " +
                            "FOREIGN KEY (stockID) REFERENCES stocks(stockID) ON DELETE CASCADE)"
            );
        }
    }

    /**
     * Reads a text file line by line, splitting by commas.
     * ignores blank lines, lines with only whitespace, and lines starting with //.
     *
     * @return list of stocks
     */
    public List<Stock> readCommaDelimitedStocks(int rawResId) {
        Log.d("DBCREATOR", "Reading stocks");
        List<Stock> result = new ArrayList<>();

        try (InputStream inputStream = context.getResources().openRawResource(rawResId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;

            Integer stockID = 0;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // skip empty or comment lines
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                // split by comma and trim
                String[] tokens = line.split(",");
                if (tokens.length >= 4) {
                    for (int i = 0; i < tokens.length; i++) {
                        tokens[i] = tokens[i].trim();
                    }
                    Stock stock = new Stock(stockID, tokens[0], tokens[1], tokens[2], tokens[3], Double.parseDouble(tokens[4]));
                    result.add(stock);
                    Log.d("STOCK", stock.getStockName());
                    stockID++;
                }
            }
        }
        catch(Exception exception){
            Log.d("DatabaseCreator", "Error reading file: " + exception.getMessage());
        }
        return result;
    }

    public static double[] generateAmplitudes(int size) {
        Random rand = new Random();
        double[] amps = new double[size];

        double value = 8 + rand.nextDouble() * 2;
        for (int i = 0; i < size; i++) {
            value -= rand.nextDouble() * 1.5;
            if (value < 0.5) value = 0.5;
            amps[i] = Math.round(value * 10.0) / 10.0;
        }
        return amps;
    }

    public static double[] generateFrequencies(int size) {
        Random rand = new Random();
        double[] freqs = new double[size];

        double value = 0.5 + rand.nextDouble();
        for (int i = 0; i < size; i++) {
            value += 0.5 + rand.nextDouble();
            freqs[i] = Math.round(value * 10.0) / 10.0;
        }
        return freqs;
    }

    public static String arrayToCommaString(double[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
