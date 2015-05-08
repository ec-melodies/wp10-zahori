package com.example.xavieralmolda.zahori_android;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Xavier Almolda on 30/03/2015.
 */
public class Site implements Parcelable {

    public String Name;
    public String Code;
    public double Latitude;
    public double Longitude;

    public Site() {
    }

    public Site(double latitude, double longitude, String name, String code) {

        this.Latitude = latitude;
        this.Longitude = longitude;
        this.Name = name;
        this.Code = code;
    }

    //parcel part
    public Site(Parcel in) {

        String[] data = new String[2];
        in.readStringArray(data);

        this.Name = data[0];
        this.Code = data[1];
        this.Latitude = Double.parseDouble(data[2]);
        this.Longitude = Double.parseDouble(data[3]);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringArray(new String[]{
                this.Name,
                this.Code,
                Double.toString(this.Latitude),
                Double.toString(this.Longitude),
        });
    }

    public static final Parcelable.Creator<Site> CREATOR= new Parcelable.Creator<Site>() {

        @Override
        public Site createFromParcel(Parcel source) {
            return new Site(source);  //using parcelable constructor
        }

        @Override
        public Site[] newArray(int size) {
            return new Site[size];
        }
    };
}
