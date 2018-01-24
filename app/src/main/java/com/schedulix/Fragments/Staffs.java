package com.schedulix.Fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.schedulix.Adapter.StaffAdapter;
import com.schedulix.AppController;
import com.schedulix.ModelClasses.AddAppointment;
import com.schedulix.ModelClasses.Staff;
import com.schedulix.OnItemClickListener;
import com.schedulix.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Staffs.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Staffs#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Staffs extends Fragment implements OnItemClickListener {

    private static final String TAG = Staffs.class.getSimpleName();
    RecyclerView rvStaff;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Staff> staffArrayList;
    Context context;
    ProgressBar progressBar;
    StaffAdapter staffAdapter;
    FloatingActionButton addStaff;
    TextView emptyView;

    private static String fromAppointment = "0";

    RequestQueue queue;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Staffs() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Staffs.
     */
    // TODO: Rename and change types and number of parameters
    public static Staffs newInstance(String param1, String param2) {
        Staffs fragment = new Staffs();
        Bundle args = new Bundle();
        fromAppointment = param1;
        Log.d(TAG, "newInstance: " + fromAppointment);
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
        staffArrayList = new ArrayList<>();
        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TAG);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_staffs, container, false);

        linearLayoutManager = new GridLayoutManager(context,2);
        rvStaff = (RecyclerView) view.findViewById(R.id.rvStaffs);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        addStaff = (FloatingActionButton) view.findViewById(R.id.addStaff);
        addStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //////////// Go to Add Staff Form////////////////
                Fragment fragment = AddStaff.newInstance("","");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.addToBackStack(TAG);
                fragmentTransaction.commit();

             //   addStaffForm();
            }
        });


            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvStaff.setLayoutManager(linearLayoutManager);
        rvStaff.getRecycledViewPool().setMaxRecycledViews(0, 0);
        staffAdapter = new StaffAdapter(context);
        // Attach the adapter to the recyclerview to populate items
        rvStaff.setAdapter(staffAdapter);
        staffAdapter.setClickListener(Staffs.this);

        ///// call api////

        if(fromAppointment.equals("0"))
        {
            Log.d(TAG, "doInBackground: From Future ");
            getAllStaffs();
        }
        else if(fromAppointment.equals("2")) {
            Log.d(TAG, "doInBackground: From block Date");
            getAllStaffs();
        }
        else if(fromAppointment.equals("1"))
        {
            Log.d(TAG, "doInBackground: From Add Apointment");
            getStaffs();
        }


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

            Log.d(TAG, "onClick: " + staffArrayList.get(position).getId());

            String staff_id =staffArrayList.get(position).getId();
            String staff_name =staffArrayList.get(position).getFirst_name();
            String staff_email =staffArrayList.get(position).getEmail();
            String staff_num =staffArrayList.get(position).getPhone_num();
            String staff_image =staffArrayList.get(position).getPicture();

        if (fromAppointment.equals("2"))
        {
            Log.d(TAG, "onClick: From BlockDate ");

            /// add staff id to appointment arrayList///////
            Staff.getInstance().setId(staff_id);
            Staff.getInstance().setFirst_name(staff_name);

//            Fragment fragment = ShowServices.newInstance(userId,staff_id);
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.frame, fragment);
//            fragmentTransaction.addToBackStack(TAG);
//            fragmentTransaction.commit();


            Log.d(TAG, "onClick: " + staff_id+"-Name"+staff_name);



            ///// move back //////
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.popBackStack();


            return;
        }


        if (fromAppointment.equals("0"))
        {
            Log.d(TAG, "onClick: From Future ");

//            /// add staff id to appointment arrayList///////
//            Staff.getInstance().setId(staff_id);
//            Staff.getInstance().setFirst_name(staff_name);
//            Staff.getInstance().setPhone_num(staff_num);
//            Staff.getInstance().setEmail(staff_email);
//            Staff.getInstance().setPicture(staff_image);

            Bundle b = new Bundle();
            b.putString("staff_id",staff_id);
            b.putString("staff_name",staff_name);
            b.putString("staff_num",staff_num);
            b.putString("staff_email",staff_email);
            b.putString("staff_image",staff_image);

            Fragment fragment = FutureUnavailabilty.newInstance("","" );
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragment.setArguments(b);
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.addToBackStack(TAG);
            fragmentTransaction.commit();




            return;
        }

            /// add staff id to appointment arrayList///////
            AddAppointment.getInstance().setStaff_id(staff_id);
            AddAppointment.getInstance().setStaff_name(staff_name);

//            Fragment fragment = ShowServices.newInstance(userId,staff_id);
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.frame, fragment);
//            fragmentTransaction.addToBackStack(TAG);
//            fragmentTransaction.commit();


            Log.d(TAG, "onClick: " + staff_id+"-Name"+staff_name);



        ///// move back //////
        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.popBackStack();


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



    private void getAllStaffs() {


        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_ALL_STAFF,
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
                                    staffObj.setPhone_num(staffJsonObj.getString("mobile_no"));
                                    staffObj.setDescription(staffJsonObj.getString("description"));
                                    staffObj.setPicture(path+staffJsonObj.getString("profile_pic"));
                                    staffArrayList.add(staffObj);
                                }
                                Log.d(TAG, "onResponse: " + staffArrayList.size());

                                // user successfully login..
//                                Toast.makeText(context,
//                                        "" + success.getString("message"),
//                                        Toast.LENGTH_LONG).show();
                            }




                            staffAdapter.setStaffs(staffArrayList);

                            RecyclerView.ItemDecoration itemDecoration = new
                                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                            rvStaff.addItemDecoration(itemDecoration);

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
                params.put("company_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id());
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);

    }


    private void getStaffs() {


        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_STAFF,
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
                                    staffObj.setPhone_num(staffJsonObj.getString("mobile_no"));
                                    staffObj.setDescription(staffJsonObj.getString("description"));
                                    staffObj.setPicture(path+staffJsonObj.getString("profile_pic"));
                                    staffArrayList.add(staffObj);
                                }
                                Log.d(TAG, "onResponse: " + staffArrayList.size());

                                // user successfully login..
//                                Toast.makeText(context,
//                                        "" + success.getString("message"),
//                                        Toast.LENGTH_LONG).show();
                            }




                            staffAdapter.setStaffs(staffArrayList);

                            RecyclerView.ItemDecoration itemDecoration = new
                                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                            rvStaff.addItemDecoration(itemDecoration);

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
                params.put("company_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id());
                params.put("service_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getService_id());
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);


    }


}
