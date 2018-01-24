package com.schedulix.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.schedulix.Adapter.ServiceAdapter;
import com.schedulix.Adapter.StaffAdapter;
import com.schedulix.AppController;
import com.schedulix.ModelClasses.AddAppointment;
import com.schedulix.ModelClasses.Service;
import com.schedulix.ModelClasses.Staff;
import com.schedulix.ModelClasses.StaffTimings;
import com.schedulix.OnItemClickListener;
import com.schedulix.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ShowServices.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowServices#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowServices extends Fragment implements OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Services";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView rvServices;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Service> servicesArrayList;
    RequestQueue queue;
    Context context;
    ProgressBar progressBar;
    ServiceAdapter serviceAdapter;
    public static String userId;
    public static String staffId;
    String service_id;
    String service_name;
    TextView emptyView;

    private OnFragmentInteractionListener mListener;

    public ShowServices() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowServices.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowServices newInstance(String param1, String param2) {
        ShowServices fragment = new ShowServices();
        userId = param1;
        staffId = param2;
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
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        servicesArrayList = new ArrayList<>();
        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TAG);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_show_services, container, false);
        rvServices = (RecyclerView) view.findViewById(R.id.rvServices);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvServices.setLayoutManager(linearLayoutManager);
        rvServices.getRecycledViewPool().setMaxRecycledViews(0, 0);
        serviceAdapter = new ServiceAdapter(context);
        // Attach the adapter to the recyclerview to populate items
        rvServices.setAdapter(serviceAdapter);
        serviceAdapter.setClickListener(ShowServices.this);


        ///// call api////
       getServices();

        return view;
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

    @Override
    public void onClick(View view, int position) {

        service_id  = servicesArrayList.get(position).getId();
        service_name  = servicesArrayList.get(position).getTitle();

        Log.d(TAG, "onClick: " + service_id+"-Name"+service_name);

        /// add Service id to appointment arrayList///////
        AddAppointment.getInstance().setService_id(service_id);
        AddAppointment.getInstance().setService_name(service_name);

        /// move back //////
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.popBackStack();



      //  getStaffTimings();


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
                                Log.d(TAG, "onResponse: " + servicesArrayList.size());

                                // user successfully login..
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }

                            progressBar.setVisibility(View.GONE);

                            showTimingsDialog(timingsList);

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
                params.put("staff_id", staffId);
                params.put("service_id", service_id);
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
                AddAppointment.getInstance().setBooking_time(booking_time);


                Fragment fragment = Note.newInstance("","");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.addToBackStack(TAG);
                fragmentTransaction.commit();
            }
        });
        builderSingle.show();
    }






    private void getServices() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_SERVICES,
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
//                                Toast.makeText(context,
//                                        "" + success.getString("message"),
//                                        Toast.LENGTH_LONG).show();

                            }


                            serviceAdapter.setServices(servicesArrayList);

                            RecyclerView.ItemDecoration itemDecoration = new
                                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                            rvServices.addItemDecoration(itemDecoration);

                            progressBar.setVisibility(View.GONE);
                            emptyView.setVisibility(View.GONE);


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
                                progressBar.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);

                            }

                            if (error.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message
                                Toast.makeText(context,
                                        "No internet connection.  Please connect to the internet and try again",
                                        Toast.LENGTH_LONG).show();

                                progressBar.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);

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
