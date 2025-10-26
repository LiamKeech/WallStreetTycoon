package com.example.wallstreettycoon.stock;

import android.util.Log;

import com.example.wallstreettycoon.databaseHelper.DatabaseUtil;
import com.example.wallstreettycoon.model.Game;
import com.example.wallstreettycoon.model.GameEvent;
import com.example.wallstreettycoon.model.GameObserver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StockPriceFunction implements GameObserver, java.io.Serializable {
    private final Integer stockPriceFunctionID;
    private final Integer stockID;
    private final Double[] amplitudes;
    private final Double[] frequencies;
    private final List<Segment> segments;
    private Double currentMarketFactor;
    private final Double initialPrice;

    public StockPriceFunction(Integer stockPriceFunctionID, Double[] amplitudes, Double[] frequencies, Double marketFactor, Integer fk){
        this.stockPriceFunctionID = stockPriceFunctionID;
        this.amplitudes = amplitudes;
        this.frequencies = frequencies;
        this.currentMarketFactor = marketFactor;
        this.segments = new ArrayList<>();
        this.stockID = fk;
        this.initialPrice = DatabaseUtil.getInstance(Game.getInstance().getContext()).getStock(stockID).getInitialPrice();
    }

    public Integer getStockID() { return stockID; }

    public Double getCurrentPrice(Integer timeStamp){

        Double sumOfSegments = 0.0;
        if(!segments.isEmpty()) {
            for (Segment s : segments) {
                sumOfSegments += s.getTotalInfluence();
            }
            sumOfSegments += currentMarketFactor * (timeStamp - segments.getLast().getEndTimeStamp());
        }
        else{
            sumOfSegments += currentMarketFactor * (timeStamp);
        }

        Double fourierSeries = 0.0;

        for(int i = 0; i < amplitudes.length; i++){
            fourierSeries += amplitudes[i] * Math.sin(frequencies[i] * timeStamp.doubleValue());
        }

        if(sumOfSegments == 0)
            return initialPrice + 0.05 * fourierSeries;
        else
            return initialPrice + sumOfSegments * (1 + 0.2 * fourierSeries);

    }

    public List<Double> getPriceHistory(Integer numberOfDays){
        List<Double> priceHistory = new ArrayList<>();
        for(int i = 0; i < numberOfDays; i++){
            if(Game.getInstance().getCurrentTimeStamp() - i * Game.numberOfSecondsInADay > 0)
                priceHistory.add(getCurrentPrice(Game.getInstance().getCurrentTimeStamp() - i * Game.numberOfSecondsInADay));
        }
        return priceHistory;
    }

    @Override
    public void onGameEvent(GameEvent event) {
        //set current market factor
        switch (event.getType()) {
            case MARKET_EVENT:
                //add last segment to the list of segments
                if(!segments.isEmpty())
                    segments.add(new Segment(segments.getLast().getEndTimeStamp(), Game.getInstance().getCurrentTimeStamp(), currentMarketFactor));

                //update new marketFactor
                currentMarketFactor = (Double) event.getCargo();
                break;
            default:
                break;
        }
    }

    public Double getCurrentMarketFactor(){return currentMarketFactor;}

    private static class Segment{
        private final Integer startTimeStamp;
        private final Integer endTimeStamp;
        private final Double marketFactor;
        public Segment(Integer startTimeStamp, Integer endTimeStamp, Double marketFactor){
            this.startTimeStamp = startTimeStamp;
            this.endTimeStamp = endTimeStamp;
            this.marketFactor = marketFactor;
        }

        public Integer getStartTimeStamp(){return startTimeStamp;}
        public Integer getEndTimeStamp(){return endTimeStamp;}
        public Double getMarketFactor(){return marketFactor;}
        public Double getTotalInfluence(){
            return marketFactor * (endTimeStamp - startTimeStamp);
        }
    }
}