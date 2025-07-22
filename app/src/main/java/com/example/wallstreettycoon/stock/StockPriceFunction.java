package com.example.wallstreettycoon.stock;

public class StockPriceFunction {
    Integer pk;
    Double[] amplitudes;
    Double[] frequencies;
    Integer fk;
    public StockPriceFunction(Integer pk, Double[] amplitudes, Double[] frequencies, Integer fk){
        this.pk = pk;
        this.amplitudes = amplitudes;
        this.frequencies = frequencies;
        this.fk = fk;
    }

    public Double getCurrentPrice(Integer timeStamp){
        Double price = 0.0;

        for(int i = 0; i < amplitudes.length; i++){
            price += amplitudes[i] * Math.sin(frequencies[i] * timeStamp.doubleValue());
        }

        return price;
    }

}
