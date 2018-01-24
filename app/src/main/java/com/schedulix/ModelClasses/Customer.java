package com.schedulix.ModelClasses;

/**
 * Created by Nsol on 5/4/2017.
 */

public class Customer {

    String id;
    String first_name;
    String last_name;
    String email;
    String mobile_no;
    String home_no;
    String work_no;
    String address;
    String country;
    String region;
    String city;
    String zip;
    String pic;
    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }




    private static Customer mInstance = null;



    public static Customer getInstance(){
        if(mInstance == null)
        {
            mInstance = new Customer();
        }
        return mInstance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getHome_no() {
        return home_no;
    }

    public void setHome_no(String home_no) {
        this.home_no = home_no;
    }

    public String getWork_no() {
        return work_no;
    }

    public void setWork_no(String work_no) {
        this.work_no = work_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }




}
