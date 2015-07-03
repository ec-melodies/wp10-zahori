package com.example.xavieralmolda.zahori_android;

import java.util.Date;

/**
 * Created by Xavier Almolda on 01/07/2015.
 */
public class LevelValuesItem {


    public String Layer;
    public Double Level;
    public Date Date;

    public LevelValuesItem() {}

    public LevelValuesItem(String layer, Double level, Date date) {

        this.Layer = layer;
        this.Level = level;
        this.Date = date;

    }

}
