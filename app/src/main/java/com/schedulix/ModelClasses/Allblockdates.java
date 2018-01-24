package com.schedulix.ModelClasses;

/**
 * Created by Bir Al Sabia on 5/10/2017.
 */
public class Allblockdates {
    private  String Blockdate;
    private String fromTime;
    private String toTime;

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }



    public void setDate(String date) {
        Blockdate = date;
    }

    public String getDate() {
        return Blockdate;
    }


}
