package com.schedulix.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.schedulix.Adapter.ServiceAdapter;
import com.schedulix.AppController;
import com.schedulix.ModelClasses.Service;
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
 * {@link ListOfServices.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListOfServices#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListOfServices extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = ListOfServices.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context context;
    public static String userId;
    RecyclerView rvServices;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Service> servicesArrayList;
    RequestQueue queue;
    ProgressBar progressBar;
    ServiceAdapter serviceAdapter;

    private OnFragmentInteractionListener mListener;

    public ListOfServices() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListOfServices.
     */
    // TODO: Rename and change types and number of parameters
    public static ListOfServices newInstance(String param1, String param2) {
        ListOfServices fragment = new ListOfServices();
        Bundle args = new Bundle();
        userId = param1;
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
        final View view = inflater.inflate(R.layout.fragment_list_of_services, container, false);

        rvServices = (RecyclerView) view.findViewById(R.id.rvListOfServices);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvServices.setLayoutManager(linearLayoutManager);
        rvServices.getRecycledViewPool().setMaxRecycledViews(0, 0);
        serviceAdapter = new ServiceAdapter(context);
        // Attach the adapter to the recyclerview to populate items
        rvServices.setAdapter(serviceAdapter);

        ///// call api////
        new LoadServicesAsync(ListOfServices.this).execute();

        // Inflate the layout for this fragment
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


    public class LoadServicesAsync extends AsyncTask<Void, Void, ArrayList<Service>> {

        ArrayList<Service> listOfStaff;
        private ListOfServices activity;

        public LoadServicesAsync(ListOfServices activity) {
            this.activity = activity;
        }

        @Override
        protected ArrayList<Service> doInBackground(Void... arg0) {

            Log.d(TAG, "doInBackground: ");
            listOfStaff = new ArrayList();
            getServices();

            return listOfStaff;
        }

        @Override
        protected void onPostExecute(ArrayList<Service> staffs) {
            progressBar.setVisibility(View.GONE);
        }


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
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }


                            serviceAdapter.setServices(servicesArrayList);

                            RecyclerView.ItemDecoration itemDecoration = new
                                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                            rvServices.addItemDecoration(itemDecoration);


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




}
