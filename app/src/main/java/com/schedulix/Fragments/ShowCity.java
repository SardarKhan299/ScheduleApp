package com.schedulix.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
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
import com.schedulix.Adapter.CityAdapter;
import com.schedulix.Adapter.RegionAdapter;
import com.schedulix.AppController;
import com.schedulix.ItemClickWithArray;
import com.schedulix.ModelClasses.City;
import com.schedulix.ModelClasses.Country;
import com.schedulix.ModelClasses.Region;
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
 * {@link ShowCity.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowCity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowCity extends Fragment implements ItemClickWithArray.ItemClickWithArrayC {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "City" ;


    Context context;

    RecyclerView rvCity;
    LinearLayoutManager linearLayoutManager;
    ArrayList<City> cityArrayList;
    RequestQueue queue;
    ProgressBar progressBar;
    CityAdapter countryAdapter;
    public static String userId;
    String city_id;
    String city_name;
    EditText search;
    TextView emptyView;




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ShowCity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowCity.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowCity newInstance(String param1, String param2) {
        ShowCity fragment = new ShowCity();
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

        if(Region.getInstance().getName() != null)
        {
            Log.d(TAG, "onCreateView: "+Region.getInstance().getName().toString());
        }


        cityArrayList = new ArrayList<>();
        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TAG);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_show_city, container, false);
        rvCity = (RecyclerView) view.findViewById(R.id.rvListOfCity);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        search = (EditText) view.findViewById(R.id.edtSearchCountry);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvCity.setLayoutManager(linearLayoutManager);
        rvCity.getRecycledViewPool().setMaxRecycledViews(0, 0);
        countryAdapter = new CityAdapter(context);
        // Attach the adapter to the recyclerview to populate items
        rvCity.setAdapter(countryAdapter);
        countryAdapter.setClickListener(ShowCity.this);

//        ///// call api////
        getCities();

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                countryAdapter.getFilter().filter(cs);
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
    public void onClickWithArray(View view, int position, ArrayList<City> arrayList) {
        Log.d(TAG, "onClick: ");

        // add list///
        ArrayList<City> cities = new ArrayList<>();
        cities.addAll(arrayList);
        // get object//
        final City city = cities.get(position);

        city_id  = city.getId();
        city_name  = city.getName();

        Log.d(TAG, "onClick: " + city_id+"-Name"+city_name);

        // add Service id to appointment arrayList///////

        City.getInstance().setId(city_id);
        City.getInstance().setName(city_name);

        // move back //////

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






    private void getCities() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_CITIES,
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

                                JSONArray countiresJsonArray = success.getJSONArray("cities");
                                for (int i = 0; i < countiresJsonArray.length(); i++) {
                                    JSONObject countryJsonObject = countiresJsonArray.getJSONObject(i);
                                    City cityObj = new City();
                                    cityObj.setId(countryJsonObject.getString("id"));
                                    cityObj.setName(countryJsonObject.getString("name"));
                                    cityObj.setStateId(countryJsonObject.getString("state_id"));
                                    cityArrayList.add(cityObj);
                                }
                                Log.d(TAG, "onResponse: " + cityArrayList.size());



                                // user successfully login..
//                                Toast.makeText(context,
//                                        "" + success.getString("message"),
//                                        Toast.LENGTH_LONG).show();
                            }


                            countryAdapter.setRegions(cityArrayList);

                            RecyclerView.ItemDecoration itemDecoration = new
                                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                            rvCity.addItemDecoration(itemDecoration);

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
                Log.d(TAG, "getParams: "+Region.getInstance().getId()+"-Name "+Region.getInstance().getName());
                params.put("state_id", Region.getInstance().getId());
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);


    }



}
