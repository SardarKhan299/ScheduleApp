package com.schedulix.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.schedulix.AppController;
import com.schedulix.EventDecorator;
import com.schedulix.ModelClasses.AddAppointment;
import com.schedulix.ModelClasses.Appointment;
import com.schedulix.ModelClasses.Reschedule;
import com.schedulix.ModelClasses.Service;
import com.schedulix.ModelClasses.Staff;
import com.schedulix.ModelClasses.StaffTimings;
import com.schedulix.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RescheduleAppointment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RescheduleAppointment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RescheduleAppointment extends Fragment {



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Reschedule";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RequestQueue queue;
    Context context;

    EditText edtService,edtStaff,edtDate,edtTime,edtNote;
    Button btn_reschedule;

    ArrayList<Staff> staffArrayList;
    ArrayList<Service> servicesArrayList;
    Calendar myCalendar;
    ProgressBar progressBar;


    private OnFragmentInteractionListener mListener;

    public RescheduleAppointment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Reschedule.
     */
    // TODO: Rename and change types and number of parameters
    public static RescheduleAppointment newInstance(String param1, String param2) {
        RescheduleAppointment fragment = new RescheduleAppointment();
        Bundle args = new Bundle();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TAG);


        queue = Volley.newRequestQueue(context);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_reschedule_appointment, container, false);

        edtService = (EditText) view.findViewById(R.id.edt_services);
        edtStaff = (EditText) view.findViewById(R.id.edt_staff);
        edtDate = (EditText) view.findViewById(R.id.edt_date);
        edtTime = (EditText) view.findViewById(R.id.edt_time);
        edtNote = (EditText) view.findViewById(R.id.edt_note);
        btn_reschedule = (Button) view.findViewById(R.id.btn_reschedule);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);


        edtService.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    // Display Dialog with Service List....
                    getServiceName();
                }

                return true; // return is important...
            }
        });

        edtStaff.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                   // Display Dialog with Service List....
                  getStaffName();
                }

                return true; // return is important...
            }
        });
        myCalendar = Calendar.getInstance();
        edtDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    // Display Dialog with Service List....
                    new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Log.d(TAG, "onDateSet: "+year+"-"+(month+1)+"-"+dayOfMonth);
                            edtDate.setText(year+"-"+(month+1)+"-"+dayOfMonth);
                           ////// update date because date updated by the user...//////
                            Reschedule.getInstance().setBooking_date(year+"-"+(month+1)+"-"+dayOfMonth);
                        }
                    }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }

                return true; // return is important...
            }
        });

        btn_reschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                btn_reschedule.setEnabled(false);
                reSchedule();


            }
        });




        ///// call api////
        new LoadAppointmentDetailAsync(RescheduleAppointment.this).execute();



        return  view;
    }

    private void reSchedule() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_UPDATE_APP,
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

                                progressBar.setVisibility(View.GONE);
                                // popup all fragments
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                    fm.popBackStack();
                                }




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


                params.put("company_id", Reschedule.getInstance().getCompany_id());
                params.put("staff_id", Reschedule.getInstance().getStaff_id());
                params.put("service_id", Reschedule.getInstance().getService_id());
                params.put("booking_date", Reschedule.getInstance().getBooking_date());
                params.put("booking_time", Reschedule.getInstance().getBooking_time());
                params.put("booking_duration",Reschedule.getInstance().getBooking_duration());
                params.put("appointment_status_id",Reschedule.getInstance().getAppointment_status_id() );
                params.put("appointment_note", edtNote.getText().toString());
                params.put("appointment_id", Appointment.getInstance().getId());


                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);
    }

    private void getServiceName() {
        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_SERVICES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, response.toString());
                        servicesArrayList = new ArrayList<>();

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

                                JSONArray services = success.getJSONArray("staffservices");
                                String imagePath = success.getString("imagepath");
                                String path = imagePath;
                                Log.d(TAG, "onResponse: Full path is "+path);
                                for (int i = 0; i < services.length(); i++) {

                                    JSONObject serviceJsonObject = services.getJSONObject(i);
                                    Service serviceObj = new Service();
                                    serviceObj.setId(serviceJsonObject.getString("id"));
                                    serviceObj.setTitle(serviceJsonObject.getString("title"));
                                    serviceObj.setDescrption(serviceJsonObject.getString("description"));
                                    serviceObj.setPrice(serviceJsonObject.getString("price"));
                                    serviceObj.setPicture(path+serviceJsonObject.getString("image"));
                                    serviceObj.setDuration(serviceJsonObject.getString("duration"));
                                    serviceObj.setDurationType(serviceJsonObject.getString("duration_type"));
                                    servicesArrayList.add(serviceObj);

                                }
                                Log.d(TAG, "onResponse: " + servicesArrayList.size());

                                // user successfully login..
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }


                            showServiceList();
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
                params.put("company_id", Appointment.getInstance().getCompany_id().toString());
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);

    }



    private void getStaffName() {


        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_STAFF,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, response.toString());
                        staffArrayList = new ArrayList<>();

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

                                JSONArray staffs = success.getJSONArray("staffs");
                                String imagePath = success.getString("imagepath");
                                String path = imagePath;
                                Log.d(TAG, "onResponse: Full path is "+path);
                                for (int i = 0; i < staffs.length(); i++) {
                                    JSONObject staffJsonObj = staffs.getJSONObject(i);
                                    Staff staffObj = new Staff();
                                    staffObj.setId(staffJsonObj.getString("id"));
                                    staffObj.setCompany_id(staffJsonObj.getString("company_id"));
                                    staffObj.setFirst_name(staffJsonObj.getString("first_name"));
                                    staffObj.setEmail(staffJsonObj.getString("email"));
                                    staffObj.setDescription(staffJsonObj.getString("description"));
                                    staffObj.setPicture(path+staffJsonObj.getString("profile_pic"));
                                    staffArrayList.add(staffObj);
                                }
                                Log.d(TAG, "onResponse: " + staffArrayList.size());

                                // user successfully login..
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }


                            showStaffList();


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
                params.put("company_id", Appointment.getInstance().getCompany_id().toString());
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);
    }

    private void showStaffList() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select Service:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);

        for(int i=0;i<staffArrayList.size();i++)
        {
            arrayAdapter.add(staffArrayList.get(i).getFirst_name());
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
                // get record from arrayList...
                String strName = arrayAdapter.getItem(which);
                edtStaff.setText(staffArrayList.get(which).getFirst_name());
                /////// update staffId becuase new staff is selected///////////
                Reschedule.getInstance().setStaff_id(staffArrayList.get(which).getId());
               dialog.dismiss();
            }
        });
        if(RescheduleAppointment.this.isVisible())
        builderSingle.show();


    }

    private void showServiceList() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle("Select Service:");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);

        for(int i=0;i<servicesArrayList.size();i++)
        {
            arrayAdapter.add(servicesArrayList.get(i).getTitle());
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
                // get record from arrayList...
                String strName = arrayAdapter.getItem(which);
                edtService.setText(servicesArrayList.get(which).getTitle());
                //////// update service id because new service is selected by user///////////
                Reschedule.getInstance().setService_id(servicesArrayList.get(which).getId());
                dialog.dismiss();
            }
        });

        if(RescheduleAppointment.this.isVisible())
            builderSingle.show();

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public class LoadAppointmentDetailAsync extends AsyncTask<Void, Void, ArrayList<Appointment>> {

        ArrayList<Appointment> listOfStaff;
        private RescheduleAppointment activity;

        public LoadAppointmentDetailAsync(RescheduleAppointment activity) {
            this.activity = activity;
        }

        @Override
        protected ArrayList<Appointment> doInBackground(Void... arg0) {

            Log.d(TAG, "doInBackground: ");
            listOfStaff = new ArrayList();
            getAppointmentDetail();
            return listOfStaff;
        }

        @Override
        protected void onPostExecute(ArrayList<Appointment> staffs) {

        }


    }

    private void getAppointmentDetail() {
        Log.d(TAG, "getAppointments: ");
        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_APP_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, response.toString());

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

                                JSONArray appointments = success.getJSONArray("appointmentdetail");
                                for (int i = 0; i < appointments.length(); i++) {

                                    Log.d(TAG, "onResponse: Appointment Array");
                                    JSONObject appointmentJsonObj = appointments.getJSONObject(i);
                                    // get appointments that are approved or un approved
                                    // never show cancel aur failed appointments
                                    Log.d(TAG, "onResponse: Status " + appointmentJsonObj.getInt("status"));
                                    if ((appointmentJsonObj.getInt("status") == 1) || appointmentJsonObj.getInt("status") == 3) {


                                        // check if the fragment is available or not...
                                        if (!(RescheduleAppointment.this.isVisible()) || !(RescheduleAppointment.this != null))
                                        {
                                            return;
                                        }

                                        edtService.setText(appointmentJsonObj.getString("service_title"));
                                        edtStaff.setText(appointmentJsonObj.getString("staff_name"));
                                        edtDate.setText(appointmentJsonObj.getString("booking_date"));
                                        edtTime.setText(appointmentJsonObj.getString("booking_time"));
                                        edtNote.setText(appointmentJsonObj.getString("appointment_note"));

                                        //////////// set data for further use/////////
                                        //////////// if no data updated by the user these value will be used/////////
                                        Reschedule.getInstance().setBooking_date(appointmentJsonObj.getString("booking_date"));
                                        Reschedule.getInstance().setBooking_time(appointmentJsonObj.getString("booking_time"));
                                        Reschedule.getInstance().setCompany_id(appointmentJsonObj.getString("company_id"));
                                        Reschedule.getInstance().setStaff_id(appointmentJsonObj.getString("staff_id"));
                                        Reschedule.getInstance().setService_id(appointmentJsonObj.getString("service_id"));
                                        Reschedule.getInstance().setBooking_duration(appointmentJsonObj.getString("booking_duration"));
                                        Reschedule.getInstance().setAppointment_status_id(appointmentJsonObj.getString("appointment_status_id"));


                                    }


                                }


                                // user successfully login..
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();

                                progressBar.setVisibility(View.GONE);

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
                Log.d(TAG, "getParams: "+Appointment.getInstance().getCompany_id().toString());

                params.put("company_id", Appointment.getInstance().getCompany_id().toString());
                params.put("service_id", Appointment.getInstance().getService_id().toString());
                params.put("staff_id", Appointment.getInstance().getStaff_id().toString());
                params.put("id", Appointment.getInstance().getId().toString());

                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);
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
}
