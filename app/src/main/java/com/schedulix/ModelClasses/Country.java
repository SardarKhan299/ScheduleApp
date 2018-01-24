package com.schedulix.ModelClasses;

/**
 * Created by Nsol on 5/8/2017.
 */

public class Country {
    String id;
    String name;
    String shortName;
    String phoneCode;

    private static Country mInstance = null;



    public static Country getInstance(){
        if(mInstance == null)
        {
            mInstance = new Country();
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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }


}
