package com.schedulix.Fragments;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crash.FirebaseCrash;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.schedulix.Adapter.AppointmentAdapter;
import com.schedulix.Adapter.StaffAdapter;
import com.schedulix.AppController;
import com.schedulix.EventDecorator;
import com.schedulix.ModelClasses.AddAppointment;
import com.schedulix.ModelClasses.Appointment;
import com.schedulix.ModelClasses.Reschedule;
import com.schedulix.ModelClasses.Staff;
import com.schedulix.OnItemClickListener;
import com.schedulix.R;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingFragment extends Fragment  implements OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Bookings";

    public MaterialCalendarView mvc ;
    RequestQueue queue;
    Context context;
    boolean dateSelected = false;
    String appointmentDate ;

    ArrayList<Appointment> appointmentArrayList;

    FloatingActionButton add;

    RecyclerView rvAppointments;
    LinearLayoutManager linearLayoutManager;
    AppointmentAdapter appointmentAdapter;

    ProgressBar progressBar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static String userId;

    private OnFragmentInteractionListener mListener;

    public BookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingFragment newInstance(String param1, String param2) {
        BookingFragment fragment = new BookingFragment();
        Bundle args = new Bundle();
        userId = param1;
        Log.d(TAG, "newInstance: " + userId);
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        queue = Volley.newRequestQueue(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        appointmentArrayList = new ArrayList<>();
        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TAG);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        mvc = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        add = (FloatingActionButton) view.findViewById(R.id.addAppointment);
        rvAppointments = (RecyclerView) view.findViewById(R.id.rvAppointments);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAppointments.setLayoutManager(linearLayoutManager);
        rvAppointments.getRecycledViewPool().setMaxRecycledViews(0, 0);
        appointmentAdapter = new AppointmentAdapter(context);
        // Attach the adapter to the recyclerview to populate items
        rvAppointments.setAdapter(appointmentAdapter);
        appointmentAdapter.setClickListener(BookingFragment.this);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dateSelected)
                {

//                    //////// Add Data to Appointment ArrayList////////////////
//                    AddAppointment.getInstance().setBooking_date(appointmentDate);
//                    AddAppointment.getInstance().setCompany_id(userId);
//                    // pass 0 for unknown customer
//                    AddAppointment.getInstance().setCustomer_id("0");
//                    // pass 0 for unapproved status
//                    AddAppointment.getInstance().setAppointment_status_id("1");
//
//                    Log.d(TAG, "onClick: Selected Date is"+appointmentDate.toString());
//                    Fragment fragment = Staffs.newInstance(userId,1);
//                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.replace(R.id.frame, fragment);
//                    fragmentTransaction.addToBackStack(TAG);
//                    fragmentTransaction.commit();

                    ////// Add Data to Appointment ArrayList////////////////
                    AddAppointment.getInstance().setBooking_date(appointmentDate);
                    AddAppointment.getInstance().setCompany_id(userId);
                    // pass 0 for unapproved status
                    AddAppointment.getInstance().setAppointment_status_id("1");

                    Fragment fragment = com.schedulix.Fragments.AddAppointment.newInstance("","");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.addToBackStack(TAG);
                    fragmentTransaction.commit();

                }
                else
                {
                    Toast.makeText(context, "Kindly Select appointment date first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mvc.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                //Log.d(TAG, "Time is: "+time+"today time is "+localDate);
               dateSelected = true;
                int year = date.getYear();
                int month = (date.getMonth()+1);
                int day = date.getDay();
                Log.d(TAG, "onDateSelected: "+year+"-"+month+"-"+day);
                String selectedDate = String.valueOf(year+"-"+month+"-"+day);
               appointmentDate = selectedDate;
                Log.d(TAG, "onDateSelected: "+selectedDate);
            }
        });


        ///// call api////
        new LoadAppointmentAsync(BookingFragment.this).execute();

        return view;
    }

    @Override
    public void onClick(View view, int position) {

        Log.d(TAG, "onClick: Item"+appointmentArrayList.get(position).getId());

        ///////////////// Add Appointment data/////////////////////////////
        Appointment.getInstance().setId(appointmentArrayList.get(position).getId());
        Appointment.getInstance().setStaff_id(appointmentArrayList.get(position).getStaff_id());
        Appointment.getInstance().setService_id(appointmentArrayList.get(position).getService_id());
        Appointment.getInstance().setCompany_id(appointmentArrayList.get(position).getCompany_id());



        Log.d(TAG, "onClick: "+Appointment.getInstance().toString());


        Fragment fragment = RescheduleAppointment.newInstance("","");
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }


    public class LoadAppointmentAsync extends AsyncTask<Void, Void, ArrayList<Appointment>> {

        ArrayList<Appointment> listOfStaff;
        private BookingFragment activity;

        public LoadAppointmentAsync(BookingFragment activity) {
            this.activity = activity;
        }

        @Override
        protected ArrayList<Appointment> doInBackground(Void... arg0) {

            Log.d(TAG, "doInBackground: ");
            listOfStaff = new ArrayList();
            getAppointments();
            return listOfStaff;
        }

        @Override
        protected void onPostExecute(ArrayList<Appointment> staffs) {
            progressBar.setVisibility(View.GONE);
        }


    }

    private void getAppointments() {
        Log.d(TAG, "getAppointments: ");
        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_APPOINTMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, response.toString());
                        final HashSet<CalendarDay> dates = new HashSet<>();
                        try {
                            // Parsing json object response
                            // response will be a json object

                            JSONObject res = new JSONObject(response);

                            JSONObject success = res.getJSONObject("success");
                            int status = success.getInt("status");
                            if (status == 0) {
                                // user not login..
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            } else {

                                JSONArray appointments = success.getJSONArray("appointments");
                                for (int i = 0; i < appointments.length(); i++) {

                                    Log.d(TAG, "onResponse: Appointment Array");
                                    JSONObject appointmentJsonObj = appointments.getJSONObject(i);
                                    // get appointments that are approved or un approved
                                    // never show cancel aur failed appointments
                                    Log.d(TAG, "onResponse: Status " + appointmentJsonObj.getInt("status"));
                                    if ((appointmentJsonObj.getInt("status") == 1) || appointmentJsonObj.getInt("status") == 3) {

                                        Appointment appointmentObj = new Appointment();
                                        appointmentObj.setId(appointmentJsonObj.getString("id"));
                                        appointmentObj.setCompany_id(appointmentJsonObj.getString("company_id"));
                                        appointmentObj.setStaff_id(appointmentJsonObj.getString("staff_id"));
                                        appointmentObj.setService_id(appointmentJsonObj.getString("service_id"));
                                        appointmentObj.setAppointment_date(appointmentJsonObj.getString("booking_date"));
                                        appointmentObj.setAppointment_time(appointmentJsonObj.getString("booking_time"));
                                        appointmentObj.setAppointment_duration(appointmentJsonObj.getString("booking_duration"));
                                        appointmentObj.setAppointment_note(appointmentJsonObj.getString("appointment_note"));

                                        // convert date from string using jodatime
                                        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
                                        DateTime dt = formatter.parseDateTime(appointmentJsonObj.getString("booking_date"));
                                        Log.d(TAG, "onCreateView: " + dt.toString());
                                        // convert into calendarDay for library
                                        CalendarDay c = new CalendarDay(dt.getYear(), (dt.getMonthOfYear() - 1), dt.getDayOfMonth());
                                        Log.d(TAG, "onCreateView: " + c.toString());
                                        dates.add(c);
                                        appointmentArrayList.add(appointmentObj);
                                    }


                                }
                                Log.d(TAG, "onResponse: Appointment List Size" + appointmentArrayList.size());
                                Log.d(TAG, "Dates List Size" + dates.size());

                                // check if the fragment is available or not...
                                if (!(BookingFragment.this.isVisible()) || !(BookingFragment.this != null))
                                {
                                    return;
                                }

                                appointmentAdapter.setApp(appointmentArrayList);

                                RecyclerView.ItemDecoration itemDecoration = new
                                        DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                                rvAppointments.addItemDecoration(itemDecoration);

                                // user successfully login..
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();

                                // add events on calendar...
                                mvc.addDecorator(new EventDecorator(getResources().getColor(android.R.color.black),dates,context));



                            }




                            // txtResponse.setText(jsonResponse);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrash.report(new Exception(e.getMessage()));
                            Toast.makeText(context,
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            if (error instanceof NoConnectionError) {
                                Toast.makeText(context, "No internet Access, Check your internet connection", Toast.LENGTH_SHORT).show();
                            }

                            if (error.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message
                                Toast.makeText(context,
                                        "No internet connection.  Please connect to the internet and try again",
                                        Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("company_id", userId);
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);



    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        Log.d(TAG, "onAttach: Call");
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
