package com.example.wallstreettycoon.stock;

import com.example.wallstreettycoon.model.GameEvent;
import com.example.wallstreettycoon.model.GameObserver;

public class StockPriceFunction implements GameObserver {
    Integer stockPriceHistoryID;
    Double[] amplitudes;
    Double[] frequencies;
    Integer stockID;
    Double marketFactor;

    public StockPriceFunction(Integer stockPriceHistoryID, Double[] amplitudes, Double[] frequencies, Integer fk){
        this.stockPriceHistoryID = stockPriceHistoryID;
        this.amplitudes = amplitudes;
        this.frequencies = frequencies;
        this.stockID = fk;
        this.marketFactor = 0.0;
    }

    public Integer getStockID() { return stockID; }

    public Double getCurrentPriceChange(Integer timeStamp){
        Double fourierSeries = 0.0;

        for(int i = 0; i < amplitudes.length; i++){
            fourierSeries += amplitudes[i] * Math.sin(frequencies[i] * timeStamp.doubleValue());
        }

        Double priceChange = marketFactor * timeStamp + fourierSeries;

        return priceChange;
    }

    @Override
    public void onGameEvent(GameEvent event) {
        //set current market factor
        switch (event.getType()) {
            case NOTIFICATION:
                marketFactor = (Double) event.getCargo();
                break;
            default:
                break;
        }
    }
}