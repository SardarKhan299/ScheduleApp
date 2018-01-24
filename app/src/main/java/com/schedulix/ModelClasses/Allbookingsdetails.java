package com.schedulix.ModelClasses;

import java.io.Serializable;

/**
 * Created by Bir Al Sabia on 12/21/2016.
 */

public class Allbookingsdetails implements Serializable{


    String id;
    private String service_name;
    private String Dateto;
    private String Datefrom;
    private String Staffname;
    private String Price;
    private String Date;
    private String Time;
    String company_id; ///// User or Company (Same) //////
    String staff_id; //// staff assosiated with this appointment///
    String service_id; //// service assosiated with this appointment///
    String booking_duration;
    String booking_note;
    String customerId;
    String customerName;
    String customerEmail;
    String customerPhoneNumber;

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }




    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }



    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String cutomerId) {
        this.customerId = cutomerId;
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

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getBooking_duration() {
        return booking_duration;
    }

    public void setBooking_duration(String booking_duration) {
        this.booking_duration = booking_duration;
    }

    public String getBooking_note() {
        return booking_note;
    }

    public void setBooking_note(String booking_note) {
        this.booking_note = booking_note;
    }




    public void setServiceName(String name) {
        service_name = name;
    }

    public String getServiceName() {
        return service_name;
    }

    public void setDateto(String dateto) {
        Dateto = dateto;
    }

    public String getDateto(){
        return Dateto;
    }

    public void setDatefrom(String datefrom) {
        Datefrom = datefrom;
    }

    public String getDatefrom() {
        return Datefrom;
    }

    public void setStaffname(String staffname) {
        Staffname = staffname;
    }

    public String getStaffname() {
        return Staffname;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getPrice() {
        return Price;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDate() {
        return Date;
    }

    public void settime(String time) {
        Time = time;
    }

    public String gettime() {
        return Time;
    }


  /*  public static List<Categoriesimages> createContactsList(int numContacts, int offset) {
        List<Categoriesimages> contacts = new ArrayList<Categoriesimages>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new Contact("Person " + ++lastContactId + " offset: " + offset, i <= numContacts / 2));
        }

        return contacts;
    }
}*/

}
