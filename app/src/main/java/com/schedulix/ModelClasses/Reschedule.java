package com.schedulix.ModelClasses;

/**
 * Created by Nsol on 4/27/2017.
 */

public class Reschedule {

    String company_id;
    String service_id;
    String staff_id;
    String booking_date;
    String booking_time;
    String booking_duration;
    String appointment_status_id;
    String appointment_note;

    private static Reschedule mInstance = null;



    public static Reschedule getInstance(){
        if(mInstance == null)
        {
            mInstance = new Reschedule();
        }
        return mInstance;
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

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }

    public String getBooking_duration() {
        return booking_duration;
    }

    public void setBooking_duration(String booking_duration) {
        this.booking_duration = booking_duration;
    }

    public String getAppointment_status_id() {
        return appointment_status_id;
    }

    public void setAppointment_status_id(String appointment_status_id) {
        this.appointment_status_id = appointment_status_id;
    }

    public String getAppointment_note() {
        return appointment_note;
    }

    public void setAppointment_note(String appointment_note) {
        this.appointment_note = appointment_note;
    }



}
