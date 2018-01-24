package com.schedulix.ModelClasses;

/**
 * Created by Nsol on 5/8/2017.
 */

public class City {

    String id;
    String name;
    String stateId;


    private static City mInstance = null;



    public static City getInstance(){
        if(mInstance == null)
        {
            mInstance = new City();
        }
        return mInstance;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
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
