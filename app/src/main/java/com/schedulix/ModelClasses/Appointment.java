package com.schedulix.ModelClasses;

/**
 * Created by Nsol on 4/21/2017.
 */

public class Appointment {

    String id;
    String company_id; ///// User or Company (Same) //////
    String staff_id; //// staff assosiated with this appointment///
    String service_id; //// service assosiated with this appointment///
    String appointment_date;
    String appointment_duration;
    String appointment_time;
    String appointment_note;

    private static Appointment mInstance = null;



    public static Appointment getInstance(){
        if(mInstance == null)
        {
            mInstance = new Appointment();
        }
        return mInstance;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
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

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getAppointment_date() {
        return appointment_date;
    }

    public void setAppointment_date(String appointment_date) {
        this.appointment_date = appointment_date;
    }

    public String getAppointment_duration() {
        return appointment_duration;
    }

    public void setAppointment_duration(String appointment_duration) {
        this.appointment_duration = appointment_duration;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public void setAppointment_time(String appointment_time) {
        this.appointment_time = appointment_time;
    }

    public String getAppointment_note() {
        return appointment_note;
    }

    public void setAppointment_note(String appointment_note) {
        this.appointment_note = appointment_note;
    }


}
