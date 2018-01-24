package com.schedulix.ModelClasses;

/**
 * Created by Nsol on 4/7/2017.
 */

public class Staff {


    String id;
    String company_id; ///// User or Company (Same) //////
    String first_name;
    String last_name;
    String user_name;
    String email;
    String phone_num;
    String home_phone;
    String profession_id;
    String country_id;
    String picture;
    String description;

    private static Staff mInstance = null;



    public static Staff getInstance(){
        if(mInstance == null)
        {
            mInstance = new Staff();
        }
        return mInstance;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }




    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfession_id() {
        return profession_id;
    }

    public void setProfession_id(String profession_id) {
        this.profession_id = profession_id;
    }

    public String getHome_phone() {
        return home_phone;
    }

    public void setHome_phone(String home_phone) {
        this.home_phone = home_phone;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }




}
