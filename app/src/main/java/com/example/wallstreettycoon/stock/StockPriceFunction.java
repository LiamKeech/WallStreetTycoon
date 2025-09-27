package com.example.wallstreettycoon.stock;

public class StockPriceFunction {
    Integer stockPriceHistoryID;
    Double[] amplitudes;
    Double[] frequencies;
    Integer stockID;
    Double offset; // based on amplitudes
    Double minPrice = 0.0; // minimum stock price

    public StockPriceFunction(Integer stockPriceHistoryID, Double[] amplitudes, Double[] frequencies, Integer fk){
        this.stockPriceHistoryID = stockPriceHistoryID;
        this.amplitudes = amplitudes;
        this.frequencies = frequencies;
        this.stockID = fk;

        Double sumAmplitudes = 0.0;
        for (Double amp : amplitudes) {
            sumAmplitudes += Math.abs(amp);
        }

        this.offset = sumAmplitudes;
    }

    public Integer getStockID() { return stockID; }

    public Double getCurrentPrice(Integer timeStamp){
        Double price = 0.0;

        for(int i = 0; i < amplitudes.length; i++){
            price += amplitudes[i] * Math.sin(frequencies[i] * timeStamp.doubleValue());
        }
        /// At t = 0 the price is zero for all stocks as sin(0) = 0, so I add offset price for every stock at t = 0

        // Add offset to base the price
        price = price + offset;

        // Ensure price never goes below minimum & prevents negative prices
        if (price < minPrice) {
            price = minPrice;
        }

        return price;
    }
}