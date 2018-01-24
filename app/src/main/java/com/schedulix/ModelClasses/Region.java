package com.schedulix.ModelClasses;

/**
 * Created by Nsol on 5/8/2017.
 */

public class Region {
    String id;
    String name;
    String countryId;

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }



    private static Region mInstance = null;



    public static Region getInstance(){
        if(mInstance == null)
        {
            mInstance = new Region();
        }
        return mInstance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }





}
