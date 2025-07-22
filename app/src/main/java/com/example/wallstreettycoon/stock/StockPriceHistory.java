package com.example.wallstreettycoon.stock;

import android.database.sqlite.SQLiteDatabase;

import com.example.wallstreettycoon.databaseHelper.DatabaseCreator;

import java.math.BigDecimal;

public class StockPriceHistory {
    Integer pk;
    Double[] amplitudes;
    Double[] frequencies;
    Integer fk;
    public StockPriceHistory(Integer pk, Double[] amplitudes, Double[] frequencies, Integer fk){
        this.pk = pk;
        this.amplitudes = amplitudes;
        this.frequencies = frequencies;
        this.fk = fk;
    }

    @Override
    public String toString(){
        return amplitudes[0].toString();
    }

    public Double getCurrentPrice(Integer timeStamp){
        Double price = 0.0;

        for(int i = 0; i < amplitudes.length; i++){
            price += amplitudes[i] * Math.sin(frequencies[i] * timeStamp.doubleValue());
        }

        return price;
    }

}
