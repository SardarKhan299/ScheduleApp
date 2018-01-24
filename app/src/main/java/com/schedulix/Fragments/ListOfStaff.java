package com.schedulix.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.schedulix.Adapter.ListOfStaffAdapter;
import com.schedulix.AppController;
import com.schedulix.ModelClasses.Staff;
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
 * {@link ListOfStaff.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListOfStaff#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListOfStaff extends Fragment  implements OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = ListOfStaff.class.getSimpleName();
    RecyclerView rvListOfStaff;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Staff> staffArrayList;
    Context context;
    ProgressBar progressBar;
    ListOfStaffAdapter staffAdapter;
    RequestQueue queue;
    EditText search;

    public static String userId;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ListOfStaff() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListOfStaff.
     */
    // TODO: Rename and change types and number of parameters
    public static ListOfStaff newInstance(String param1, String param2) {
        ListOfStaff fragment = new ListOfStaff();
        userId = param1;
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

        staffArrayList = new ArrayList<>();
        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TAG);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_list_of_staff, container, false);

        linearLayoutManager = new GridLayoutManager(context,1);
        rvListOfStaff = (RecyclerView) view.findViewById(R.id.rvListOfStaff);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        search = (EditText) view.findViewById(R.id.edtSearchCountry);



        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvListOfStaff.setLayoutManager(linearLayoutManager);
        rvListOfStaff.getRecycledViewPool().setMaxRecycledViews(0, 0);
        rvListOfStaff.setHasFixedSize(true);
        rvListOfStaff.setItemViewCacheSize(20);
        rvListOfStaff.setDrawingCacheEnabled(true);
        staffAdapter = new ListOfStaffAdapter(context);
        // Attach the adapter to the recyclerview to populate items
        rvListOfStaff.setAdapter(staffAdapter);
        staffAdapter.setClickListener(ListOfStaff.this);

        ///// call api////
        new LoadStaffsAsync(ListOfStaff.this).execute();

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
            //    staffAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

            @Override
            public void afterTextChanged(Editable arg0) {}
        });




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
        Log.d(TAG, "onClick: " + staffArrayList.get(position).getId()+staffArrayList.get(position).getFirst_name());
        String staffId = staffArrayList.get(position).getId();

//        Fragment fragment = UpdateProfile.newInstance(userId,staffId);
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.frame, fragment);
//        fragmentTransaction.addToBackStack(TAG);
//        fragmentTransaction.commit();

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

    public class LoadStaffsAsync extends AsyncTask<Void, Void, ArrayList<Staff>> {

        ArrayList<Staff> listOfStaff;
        private ListOfStaff activity;

        public LoadStaffsAsync(ListOfStaff activity) {
            this.activity = activity;
        }

        @Override
        protected ArrayList<Staff> doInBackground(Void... arg0) {

            Log.d(TAG, "doInBackground: ");
            listOfStaff = new ArrayList();
            getStaffs();

            return listOfStaff;
        }

        @Override
        protected void onPostExecute(ArrayList<Staff> staffs) {
            progressBar.setVisibility(View.GONE);
        }


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




                            staffAdapter.setStaffs(staffArrayList);

                            RecyclerView.ItemDecoration itemDecoration = new
                                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                            rvListOfStaff.addItemDecoration(itemDecoration);


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
