package com.example.xavieralmolda.zahori_android;

import java.util.Date;

/**
 * Created by Xavier Almolda on 02/04/2015.
 */
public class SeriesValue {

    public Date Date;
    public Double Value;

    public SeriesValue() {

        Date = new Date();
        Value = -9999d;

    }

    public SeriesValue(Date d, Double v)
    {

        this.Date = d;
        this.Value = v;

    }

}
