package com.schedulix.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.schedulix.AppController;
import com.schedulix.ModelClasses.Appointment;
import com.schedulix.ModelClasses.Customer;
import com.schedulix.ModelClasses.Reschedule;
import com.schedulix.ModelClasses.Service;
import com.schedulix.ModelClasses.Staff;
import com.schedulix.ModelClasses.StaffTimings;
import com.schedulix.R;

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
 * {@link AddAppointment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddAppointment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAppointment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = AddAppointment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    // Animation
    Animation slideUp,slideDown;
    RequestQueue queue;
    Context context;

    EditText edtService,edtStaff,edtDate,edtTime,edtNote,edtCustomer;
    Button btn_save;
    RelativeLayout customerLayout;
    boolean layoutVisible = false;
    ImageButton newCustomer , existingCustomer,unknownCustomer;

    ArrayList<Staff> staffArrayList;
    ArrayList<Service> servicesArrayList;
    java.util.Calendar myCalendar;
    ProgressBar progressBar;
    String selectedDateForDB = null;

    private OnFragmentInteractionListener mListener;

    public AddAppointment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddAppointment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAppointment newInstance(String param1, String param2) {
        AddAppointment fragment = new AddAppointment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: ");

        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TAG);

        // load the animation
        slideUp = AnimationUtils.loadAnimation(context,
                R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(context,
                R.anim.slide_down);

        queue = Volley.newRequestQueue(context);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_appointment, container, false);

        edtService = (EditText) view.findViewById(R.id.edt_services);
        edtStaff = (EditText) view.findViewById(R.id.edt_staff);
        edtDate = (EditText) view.findViewById(R.id.edt_date);
        edtTime = (EditText) view.findViewById(R.id.edt_time);
        edtCustomer = (EditText) view.findViewById(R.id.edt_customer);
        customerLayout = (RelativeLayout) view.findViewById(R.id.customer_layout);
        edtNote = (EditText) view.findViewById(R.id.edt_note);
        btn_save = (Button) view.findViewById(R.id.btn_save);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        newCustomer = (ImageButton) view.findViewById(R.id.new_customer);
        existingCustomer = (ImageButton) view.findViewById(R.id.existing_customer);
        unknownCustomer = (ImageButton) view.findViewById(R.id.unknown_customer);


        edtService.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    // Display Dialog with Service List....
                    Fragment fragment = ShowServices.newInstance(com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id(),"");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.addToBackStack(TAG);
                    fragmentTransaction.commit();
                }

                return true; // return is important...
            }
        });

        edtStaff.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    // Display Dialog with Service List....
                 //   getStaffName();

                    if (!validateService()) {
                        return true;
                    }


                        Fragment fragment = Staffs.newInstance("1","");
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame, fragment);
                        fragmentTransaction.addToBackStack(TAG);
                        fragmentTransaction.commit();


                }

                return true; // return is important...
            }
        });
        myCalendar = java.util.Calendar.getInstance();



        DateTimeFormatter fmt = DateTimeFormat.forPattern(" EEEE , MMM d  ");
        LocalDate date = new LocalDate(com.schedulix.ModelClasses.AddAppointment.getInstance().getBooking_date());
        String selectedDate = date.toString(fmt);
        selectedDateForDB = selectedDate;
        edtDate.setText(selectedDate);

        edtDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    // Display Dialog with Service List....
                    new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            selectedDateForDB = year+"-"+(month+1)+"-"+dayOfMonth;
                            Log.d(TAG, "onDateSet: "+selectedDateForDB);

                            DateTimeFormatter fmt = DateTimeFormat.forPattern(" EEEE , MMM d  ");
                            LocalDate date = new LocalDate(selectedDateForDB);
                            String selectedDate = date.toString(fmt);
                            edtDate.setText(selectedDate);

                            ////////////////////////////set date for Appointment ////////////////////////////////
                            com.schedulix.ModelClasses.AddAppointment.getInstance().setBooking_date(selectedDateForDB);
                        }
                    }, myCalendar.get(java.util.Calendar.YEAR), myCalendar.get(java.util.Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }

                return true; // return is important...
            }
        });

        edtTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    ////// set text on edittext becuase cannot set text in oncreateView///////////////
                    if(com.schedulix.ModelClasses.AddAppointment.getInstance().getService_name()!= null) {
                        if(com.schedulix.ModelClasses.AddAppointment.getInstance().getStaff_name()!= null) {
                            getStaffTimings();
                        }
                        else
                        {
                            Toast.makeText(context, "Select Satff First", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "Select Satff First", Toast.LENGTH_SHORT).show();
                    }


                }

                return true; // return is important...
            }
        });

        edtCustomer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    if(!layoutVisible)
                    {
                        customerLayout.startAnimation(slideDown);
                        customerLayout.setVisibility(View.VISIBLE);
                        layoutVisible = true;
                    }
                    else
                    {
                        customerLayout.startAnimation(slideUp);
                        customerLayout.setVisibility(View.GONE);
                        layoutVisible = false;
                    }
                }

                return true; // return is important...
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  reSchedule();

                if (!validateService()) {
                    return ;
                }
                if (!validateStaff()) {
                    return ;
                }
                if (!validateTiming()) {
                    return ;
                }
                if (!validateCustomer()) {
                    return ;
                }


             //   String appointment_note = edtNote.getText().toString().trim();
                /// add Note  to appointment arrayList///////

                progressBar.setVisibility(View.VISIBLE);
                btn_save.setEnabled(false);

                com.schedulix.ModelClasses.AddAppointment.getInstance().setAppointment_note("Some Note");
                com.schedulix.ModelClasses.AddAppointment.getInstance().setAppointment_status_id("1");



                addAppointment();

            }
        });


        newCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = AddCustomer.newInstance("1","");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.addToBackStack(TAG);
                fragmentTransaction.commit();
            }
        });

        existingCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = ListOfCustomers.newInstance("1","");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.addToBackStack(TAG);
                fragmentTransaction.commit();
            }
        });

        unknownCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtCustomer.setText("Unknown");
                // pass 0 for unknown customer
                com.schedulix.ModelClasses.AddAppointment.getInstance().setCustomer_id("0");
                customerLayout.startAnimation(slideUp);
                customerLayout.setVisibility(View.GONE);
                layoutVisible = false;
            }
        });


        progressBar.setVisibility(View.GONE);



        ///// call api////
   //     new RescheduleAppointment.LoadAppointmentDetailAsync(RescheduleAppointment.this).execute();



        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ////// set text on edittext becuase cannot set text in oncreateView///////////////
        if(com.schedulix.ModelClasses.AddAppointment.getInstance().getService_name()!= null) {
            Log.d(TAG, "onResume: " + com.schedulix.ModelClasses.AddAppointment.getInstance().getService_name());
            edtService.setText(com.schedulix.ModelClasses.AddAppointment.getInstance().getService_name().toString());
            edtService.setError(null);
            //// clear saff field////
            edtStaff.setText("");
            if(com.schedulix.ModelClasses.AddAppointment.getInstance().getStaff_name()!= null) {
                Log.d(TAG, "onResume: " + com.schedulix.ModelClasses.AddAppointment.getInstance().getStaff_name());
                edtStaff.setText(com.schedulix.ModelClasses.AddAppointment.getInstance().getStaff_name().toString());
                edtStaff.setError(null);
            }
        }

        if(Customer.getInstance().getFirst_name() != null)
        {
            edtCustomer.setText(Customer.getInstance().getFirst_name());
        }

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

    private boolean validateService() {
        if (edtService.getText().toString().trim().isEmpty()) {
            edtService.setError(getString(R.string.err_msg_name));
            requestFocus(edtService);
            return false;
        } else {
            //  passwordLayout.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateStaff() {
        if (edtStaff.getText().toString().trim().isEmpty()) {
            edtStaff.setError(getString(R.string.err_msg_name));
            requestFocus(edtStaff);
            return false;
        } else {
            //  passwordLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateTiming() {
        if (edtTime.getText().toString().trim().isEmpty()) {
            edtTime.setError(getString(R.string.err_msg_name));
            requestFocus(edtTime);
            return false;
        } else {
            //  passwordLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateCustomer() {
        if (edtCustomer.getText().toString().trim().isEmpty()) {
            edtCustomer.setError(getString(R.string.err_msg_name));
            requestFocus(edtCustomer);
            return false;
        } else {
            //  passwordLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void getStaffTimings() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_STAFF_TIMINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, response.toString());

                        ArrayList<StaffTimings> timingsList = new ArrayList<>();

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

                                JSONArray services = success.getJSONArray("availabilitytime");
                                for (int i = 0; i < services.length(); i++) {
                                    JSONObject serviceJsonObject = services.getJSONObject(i);
                                    StaffTimings timings = new StaffTimings();
                                    timings.setService_id(serviceJsonObject.getString("service_id"));
                                    timings.setStaff_id(serviceJsonObject.getString("staff_id"));
                                    timings.setService_day(serviceJsonObject.getString("service_day"));
                                    timings.setStart_time(serviceJsonObject.getString("starttime"));
                                    timings.setEnd_time(serviceJsonObject.getString("endtime"));
                                    timingsList.add(timings);
                                }


                                // user successfully login..
//                                Toast.makeText(context,
//                                        "" + success.getString("message"),
//                                        Toast.LENGTH_LONG).show();
                            }

                            progressBar.setVisibility(View.GONE);
                            // check if we get timmings arrayList..
                            if(timingsList.size()>0)
                            showTimingsDialog(timingsList);
                            else
                            {
                                Toast.makeText(context,
                                        " No Record Found ",
                                        Toast.LENGTH_LONG).show();
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
                params.put("staff_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getStaff_id());
                params.put("service_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getService_id());
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);

    }


    private void showTimingsDialog(final ArrayList<StaffTimings> staffTimingsArrayList) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select Timings");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
        for(int i =0; i<staffTimingsArrayList.size();i++)
        {
            arrayAdapter.add("Day :"+staffTimingsArrayList.get(i).getService_day()+"Timings :"+staffTimingsArrayList.get(i).getStart_time()+"-"+staffTimingsArrayList.get(i).getEnd_time());
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                Log.d(TAG, "onClick: Timing is "+strName+"Position is "+which);
                Log.d(TAG, "onClick: Start Time is "+staffTimingsArrayList.get(which).getStart_time());
                String booking_time = staffTimingsArrayList.get(which).getStart_time()+"-"
                        +staffTimingsArrayList.get(which).getEnd_time();

                /// add Booking Timings  to appointment arrayList///////
                com.schedulix.ModelClasses.AddAppointment.getInstance().setBooking_time(booking_time);
                com.schedulix.ModelClasses.AddAppointment.getInstance().setBooking_duration("2hrs");
                edtTime.setText(booking_time);


            }
        });
        builderSingle.show();
    }


    private void addAppointment() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_ADD_APPOINTMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, response.toString());

                        ArrayList<StaffTimings> timingsList = new ArrayList<>();

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

                                // Appointment Added Successfully
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();

//                                // replace fragment donot add it to backstack..so that it remains
//                                Fragment fragment = BookingFragment.newInstance(AddAppointment.getInstance().getCompany_id(),"");
//                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                fragmentTransaction.replace(R.id.frame, fragment);
//                                fragmentTransaction.commit();


                                /// move back //////
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                manager.popBackStack();

                            }


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
                Log.d(TAG, "getParams: Customer _id is "+com.schedulix.ModelClasses.AddAppointment.getInstance().getCustomer_id());

                com.schedulix.ModelClasses.AddAppointment.getInstance().setBooking_date(selectedDateForDB);
                params.put("company_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id());
                params.put("staff_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getStaff_id());
                params.put("service_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getService_id());
                params.put("customer_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getCustomer_id());
                params.put("booking_date", com.schedulix.ModelClasses.AddAppointment.getInstance().getBooking_date());
                params.put("booking_time", com.schedulix.ModelClasses.AddAppointment.getInstance().getBooking_time());
                params.put("booking_duration", com.schedulix.ModelClasses.AddAppointment.getInstance().getBooking_duration());
                params.put("appointment_status_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getAppointment_status_id());
                params.put("appointment_note", com.schedulix.ModelClasses.AddAppointment.getInstance().getAppointment_note());

                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);
    }

}
