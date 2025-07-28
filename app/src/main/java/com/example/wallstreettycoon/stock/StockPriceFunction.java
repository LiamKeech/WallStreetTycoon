package com.example.wallstreettycoon.stock;

public class StockPriceFunction {
    Integer stockPriceHistoryID;
    Double[] amplitudes;
    Double[] frequencies;
    Integer stockID;
    Double offset = 0.0;
    public StockPriceFunction(Integer stockPrceHistoryID, Double[] amplitudes, Double[] frequencies, Integer fk){
        this.stockPriceHistoryID = stockPrceHistoryID;
        this.amplitudes = amplitudes;
        this.frequencies = frequencies;
        this.stockID = fk;
    }

    public Integer getStockID() { return stockID; }

    public Double getCurrentPrice(Integer timeStamp){
        Double price = 0.0;

        for(int i = 0; i < amplitudes.length; i++){
            price += amplitudes[i] * Math.sin(frequencies[i] * timeStamp.doubleValue());
        }

        return price + offset;
    }

}
