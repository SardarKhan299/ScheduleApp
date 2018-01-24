package com.schedulix;



/**
 * Created by Nsol on 3/21/2017.
 */
import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.schedulix.ModelClasses.AddAppointment;
import com.schedulix.ModelClasses.User;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    ///////// FOR VOLLEY//////////////
    private RequestQueue mRequestQueue;
    private static AppController mInstance;

    /////////////////////////// URL's FOR API CALL/////////////////////////////////////////
   public static String BASE_URL = "http://dev.nsol.sg/projects/schedulix/";
   // public static String BASE_URL = "http://192.168.1.201/projects/schedulix/";


    public static String URL = BASE_URL+"services/user/login";
    public static String URL_RECOVER_EMAIL =BASE_URL+"services/user/forget_password";
    public static String URL_FB_LOGIN =BASE_URL+"services/user/login_with_facebook";
    public static String URL_REGISTER =BASE_URL+"services/user/register";
    public static String URL_GET_STAFF =BASE_URL+"services/user/get_my_staffs";
    public static String URL_ADD_STAFF =BASE_URL+"services/user/add_staff";
    public static String URL_UPDATE_STAFF = BASE_URL+"services/user/update_staff_basic_profile";
    public static String URL_GET_STAFF_DETAIL = BASE_URL+"services/user/view_staff_detail";
    public static String URL_GET_APPOINTMENTS =BASE_URL+"services/user/get_appointments";
    public static String URL_GET_SERVICES =BASE_URL+"services/booking/get_my_company_services";
    public static String URL_GET_STAFF_TIMINGS =BASE_URL+"services/user/get_staff_availability";
    public static String URL_ADD_APPOINTMENT =BASE_URL+"services/user/add_appointments";
    public static String URL_GET_APP_DETAIL = BASE_URL+"services/user/get_appointment_detail";
    public static String URL_UPDATE_APP = BASE_URL+"services/user/update_appointments";
    public static String URL_GET_GENERAL_NAME = BASE_URL+"services/user/get_general_name";
    public static String URL_UPDATE_GENERAL_NAME = BASE_URL+"services/user/update_general_name";
    public static String URL_ADD_SERVICE =BASE_URL+"services/booking/add_company_service";
    public static String URL_GET_CATEGRORIES =BASE_URL+"services/user/get_categories";
    public static String URL_GET_COUNTRIES =BASE_URL+"services/booking/get_country_list";
    public static String URL_GET_REGIONS =BASE_URL+"services/booking/get_state_list";
    public static String URL_GET_CITIES =BASE_URL+"services/booking/get_city_list";
    public static String URL_ADD_CUSTOMER =BASE_URL+"services/booking/add_customers";
    public static String URL_GET_CUSTOMER =BASE_URL+"services/booking/get_my_customers";
    public static String URL_GET_REVIEWS =BASE_URL+"services/booking/get_my_company_reviews";
    public static String URL_GET_ALL_STAFF =BASE_URL+"services/user/get_compnaystaff";
    public static String URL_ADD_BLOCK_DATE =BASE_URL+"services/booking/add_block_date_by_company";
    public static String URL_ADD_BLOCK_TIME =BASE_URL+"services/booking/add_block_time";
    public static String URL_GET_BLOCK_DATES =BASE_URL+"services/booking/get_company_block_dates";
    public static String URL_GET_BLOCK_TIMES =BASE_URL+"services/booking/get_company_block_time";
    public static String URL_GET_CUSTOMER_DETAIL =BASE_URL+"services/booking/view_customer_detail";
    public static String URL_UPDATE_CUSTOMER_DETAIL =BASE_URL+"services/booking/update_customer_basic_profile";




    ////////////// tags used to attach the fragments/////////////////////////////
    public static final String TAG_BOOKING = "Booking";
    public static final String TAG_REVIEW = "Review";
    public static final String TAG_CUSTOMERS = "Customers";
    public static final String TAG_CUOPON = "Cuopon";
    public static final String TAG_BUSINESS_DETAILS = "Business Details";
    public static final String TAG_PRIVACY = "Privacy Settings";
    public static final String TAG_STAFF = "Staff";
    public static final String TAG_RESOURCES = "Resources";
    public static final String TAG_SERVICES = "Services";
    public static final String TAG_ABOUT = "About";
    public static final String TAG_FEEDBACK = "Feedback";
    public static final String TAG_APPOINTMENT = "Dashboard";

    /////////////// FOR PREFERENCES ///////////
    public static String PREF_NAME = "MyPrefs";
    public static String PREF_NAME_TOKEN = "MyPrefsToken";

    public static ArrayList<User>  userArrayList = new ArrayList<>();



    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        JodaTimeAndroid.init(this);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }




}
