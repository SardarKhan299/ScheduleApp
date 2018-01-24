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
import com.schedulix.Adapter.CountryAdapter;
import com.schedulix.Adapter.ServiceAdapter;
import com.schedulix.AppController;
import com.schedulix.ItemClickWithArray;
import com.schedulix.ModelClasses.*;
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
 * {@link ShowCountry.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ShowCountry#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowCountry extends Fragment  implements ItemClickWithArray {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Country";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context context;

    RecyclerView rvCountries;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Country> countryArrayList;
    RequestQueue queue;
    ProgressBar progressBar;
    CountryAdapter countryAdapter;
    public static String userId;
    String country_id;
    String country_name;
    EditText search;
    TextView emptyView;

    private OnFragmentInteractionListener mListener;

    public ShowCountry() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowCountry.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowCountry newInstance(String param1, String param2) {
        ShowCountry fragment = new ShowCountry();
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

        countryArrayList = new ArrayList<>();
        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TAG);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_show_country, container, false);
        rvCountries = (RecyclerView) view.findViewById(R.id.rvListOfCountries);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        search = (EditText) view.findViewById(R.id.edtSearchCountry);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvCountries.setLayoutManager(linearLayoutManager);
        rvCountries.getRecycledViewPool().setMaxRecycledViews(0, 0);
        countryAdapter = new CountryAdapter(context);
        // Attach the adapter to the recyclerview to populate items
        rvCountries.setAdapter(countryAdapter);
        countryAdapter.setClickListener(ShowCountry.this);

//        ///// call api////
        getCountries();

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

    @Override
    public void onClickWithArray(View view, int position, ArrayList<Country> arrayList) {
        Log.d(TAG, "onClick: ");

        // add list///
        ArrayList<Country> countries = new ArrayList<>();
        countries.addAll(arrayList);
        // get object//
        final Country country = countries.get(position);

        country_id  = country.getId();
        country_name  = country.getName();

        Log.d(TAG, "onClick: " + country_id+"-Name"+country_name);

        // add Service id to appointment arrayList///////

        Country.getInstance().setId(country_id);
        Country.getInstance().setName(country_name);

        // move back //////

        FragmentManager manager = getActivity().getSupportFragmentManager();
        manager.popBackStack();

    }




    private void getCountries() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_COUNTRIES,
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

                                JSONArray countiresJsonArray = success.getJSONArray("countries");
                                for (int i = 0; i < countiresJsonArray.length(); i++) {
                                    JSONObject countryJsonObject = countiresJsonArray.getJSONObject(i);
                                    Country countryObj = new Country();
                                    countryObj.setId(countryJsonObject.getString("id"));
                                    countryObj.setName(countryJsonObject.getString("name"));
                                    countryObj.setShortName(countryJsonObject.getString("sortname"));
                                    countryObj.setPhoneCode(countryJsonObject.getString("phonecode"));
                                    countryArrayList.add(countryObj);
                                }
                                Log.d(TAG, "onResponse: " + countryArrayList.size());

                                // user successfully login..
//                                Toast.makeText(context,
//                                        "" + success.getString("message"),
//                                        Toast.LENGTH_LONG).show();
                            }


                            countryAdapter.setCountries(countryArrayList);

                            RecyclerView.ItemDecoration itemDecoration = new
                                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                            rvCountries.addItemDecoration(itemDecoration);

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
