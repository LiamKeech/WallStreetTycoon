package com.example.wallstreettycoon.databaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.wallstreettycoon.R;
import com.example.wallstreettycoon.stock.Stock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                        "stockName TEXT, symbol TEXT, category TEXT, description TEXT, price REAL, priceHistory TEXT)"
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
            db.execSQL(
                    "INSERT INTO stocks (stockName, symbol, category, description, price, priceHistory) " +
                            "VALUES ('" + s.getStockName() + "', '" +
                            s.getSymbol() + "', '" +
                            s.getCategory() + "', '" +
                            s.getDescription() + "', '" +
                            s.getCurrentPrice() + "', '" +
                            s.getCurrentPrice() + "')" // initialize priceHistory with the initial price
            );
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



        // Chapter table
        db.execSQL("CREATE TABLE IF NOT EXISTS Chapter (" +
                "chapterID INTEGER PRIMARY KEY, " +
                "chapterName TEXT, " +
                "description TEXT)");

        db.execSQL("INSERT OR IGNORE INTO Chapter (chapterID, chapterName, description) VALUES " +
                "(0, 'Tutorial', 'Brief tutorial on controls and buying/selling stocks. Purchase Teslo shares.'), " +
                "(1, 'The Dot-Com Boom', 'Tech stock surge with reaction mini-game.'), " +
                "(2, 'The Housing Bubble', 'Investment banks to buy and sell before crash.'), " +
                "(3, 'The Crypto Surge', 'Sell old stocks, puzzle mini-game for crypto access.'), " +
                "(4, 'The Corona Crash', 'Tourism stocks fall, tech/entertainment rise.'), " +
                "(5, 'The AI Revolution', 'Invest in AI company with logic mini-game.')");

        // ChapterStock join table
        db.execSQL("CREATE TABLE IF NOT EXISTS ChapterStock (" +
                "chapterStockID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "chapterID INTEGER, " +
                "stockID INTEGER, " +
                "FOREIGN KEY(chapterID) REFERENCES Chapter(chapterID), " +
                "FOREIGN KEY(stockID) REFERENCES stocks(stockID))");

        //FIXME Populate ChapterStock
        Map<Integer, List<String>> chapterStocks = new HashMap<>();
        chapterStocks.put(1, Arrays.asList("CRNB", "MHCD", "PEAR", "GPLX", "ORNG", "BNNF", "ISAM", "CHRP", "LMTC", "OCSD", "RDTN", "FRBT", "PNOS", "NSCM", "ZYND", "FAUD", "TESL", "PLLC", "ESKM", "SNKS"));
        chapterStocks.put(2, Arrays.asList("CRNB", "MHCD", "PEAR", "GPLX", "ORNG", "BNNF", "ISAM", "CHRP", "LMTC", "OCSD", "RDTN", "FRBT", "PNOS", "NSCM", "ZYND", "FAUD", "TESL", "GDBK", "MRGS", "LB20", "SCMP", "JPMG", "BRST", "CTRB", "HDBC", "PNZI", "DMHC", "FAUD", "BNZO", "HLIX", "IRCL", "SKLH"));
        chapterStocks.put(3, Arrays.asList("CRNB", "MHCD", "PEAR", "GPLX", "ORNG", "BNNF", "ISAM", "CHRP", "LMTC", "OCSD", "RDTN", "FRBT", "PNOS", "NSCM", "ZYND", "FAUD", "TESL", "DGCS", "INST", "BTCN", "HODL", "SHDY", "MNBK", "PUMP", "ELNM", "ZRCN", "BNNA", "BGNI", "HDHP", "GLOW", "HVHP", "FAUD"));
        chapterStocks.put(4, Arrays.asList("SKHA", "EJTN", "CLDN", "WGIT", "PRLC", "JTLG", "YLVC", "ARFO", "BGBL", "NVLT", "TTCK", "FLIK", "SLPC", "VRRL", "FAUD", "MDMS", "WNDO", "GKRT", "SKFI", "TESL"));
        chapterStocks.put(5, Arrays.asList("TKAI", "NRND", "DFMD", "PSSE", "CGSM", "PRTA", "AGPT", "WKBT", "RBLB", "PRBL", "CLAI", "FAUD", "VRRL", "TESL"));

        Cursor cursor = null;
        try {
            for (Map.Entry<Integer, List<String>> entry : chapterStocks.entrySet()) {
                int chapterID = entry.getKey();
                List<String> symbols = entry.getValue();
                for (String symbol : symbols) {
                    cursor = db.rawQuery("SELECT stockID FROM stocks WHERE symbol = ?", new String[]{symbol});
                    if (cursor.moveToFirst()) {
                        int stockID = cursor.getInt(cursor.getColumnIndexOrThrow("stockID"));
                        ContentValues values = new ContentValues();
                        values.put("chapterID", chapterID);
                        values.put("stockID", stockID);
                        db.insert("ChapterStock", null, values);

                        Log.d("DATABASE CREATOR", "Added stock " + symbol + " to Chapter " + chapterID);
                    } else {
                        Log.w("DATABASE CREATOR", "Symbol " + symbol + " not found in stocks table");
                    }
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        // MarketEvent table
        //TEXT title
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS marketEvents (" +
                        "eventID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "chapterID INTEGER, " +
                        "minigameID INTEGER, " +
                        "eventDuration INTEGER, " +
                        "eventTitle TEXT, " +
                        "eventInfo TEXT, " +
                        "chapterPresent INTEGER, " +
                        "marketFactors TEXT, " +
                        "FOREIGN KEY (chapterID) REFERENCES chapters(chapterID), " +
                        "FOREIGN KEY (minigameID) REFERENCES minigames(minigameID))"
        );

        importMarketEvents(R.raw.notifications, db, context);


        // Minigame table
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS minigame (" +
                        "minigameID INTEGER PRIMARY KEY, " +
                        "minigameName TEXT, " +
                        "description TEXT, " +
                        "chapterID INTEGER, " +
                        "FOREIGN KEY(chapterID) REFERENCES Chapter(chapterID))"
        );
        db.execSQL(
                "INSERT OR IGNORE INTO minigame (minigameID, minigameName, description, chapterID) VALUES " +
                "(1, 'Reaction Game', 'Test reaction time', 1), " +
                "(2, 'Puzzle Game', 'Solve a puzzle', 3), " +
                "(3, 'Logic Game', 'Logic challenge', 5)");

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

        // Transaction history table
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

    /**
     * Generates an array of amplitudes for a waveform.
     * Each amplitude starts around 8–10 and gradually decreases.
     * The decrease is random but capped at a minimum of 0.5.
     * Values are rounded to one decimal place.
     *
     * @param size the number of amplitudes to generate
     * @return an array of size 'size' containing amplitude values
     */
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

    /**
     * Generates an array of frequencies for a waveform.
     * Each frequency starts around 0.5–1.5 and gradually increases.
     * The increase is random between 0.5 and 1.5.
     * Values are rounded to one decimal place.
     *
     * @param size the number of frequencies to generate
     * @return an array of size 'size' containing frequency values
     */
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

    public static void importMarketEvents(int rawResId, SQLiteDatabase db, Context context) {
        Resources res = context.getResources();
        try (InputStream inputStream = res.openRawResource(rawResId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                if (line.trim().isEmpty() || line.startsWith("//")) {
                    continue;
                }

                // Split into 5 parts: chapterID;minigameID;eventDuration;eventTitle;eventInfo
                String[] parts = line.split(";", 6);
                if (parts.length < 5) {
                    continue; // skip malformed lines
                }


                String chapterIDStr = parts[0].trim();
                String minigameIDStr = parts[1].trim();
                String eventDurationStr = parts[2].trim();
                String eventTitle = parts[3].trim();
                String eventInfo = parts[4].trim();


                // Convert null to actual null
                Integer chapterID = chapterIDStr.equals("null") ? null : Integer.valueOf(chapterIDStr);
                Integer minigameID = minigameIDStr.equals("null") ? null : Integer.valueOf(minigameIDStr);
                int eventDuration = Integer.parseInt(eventDurationStr);

                // Prepare values
                ContentValues values = new ContentValues();
                if (chapterID != null) values.put("chapterID", chapterID);
                if (minigameID != null) values.put("minigameID", minigameID);


                values.put("eventDuration", eventDuration);
                values.put("eventTitle", eventTitle);
                values.put("eventInfo", eventInfo);
                values.put("chapterPresent", (chapterID != null) ? 1 : 0);

                if(parts.length == 6) {
                    String marketFactorsStr = parts[5].trim();
                    values.put("marketFactors", marketFactorsStr);
                }
                else{
                    values.put("marketFactors", "");
                }

                // Insert
                db.insert("marketEvents", null, values);
                Log.d("DATABASE CREATOR", "Imported market event: " + eventTitle);
            }

        } catch (Exception e) {
            Log.d("DATABASE CREATOR", "Error importing market events, " + e.getMessage());
        }
    }
}
