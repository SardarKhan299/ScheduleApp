package com.schedulix.ModelClasses;

/**
 * Created by Nsol on 4/25/2017.
 */

public class StaffTimings {

    String service_id;
    String staff_id;
    String service_day;
    String start_time;
    String end_time;

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getService_day() {
        return service_day;
    }

    public void setService_day(String service_day) {
        this.service_day = service_day;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }


}
