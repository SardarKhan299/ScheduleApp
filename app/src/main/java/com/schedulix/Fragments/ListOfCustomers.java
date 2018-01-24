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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.schedulix.Adapter.ListOfCustomerAdapter;
import com.schedulix.Adapter.ListOfStaffAdapter;
import com.schedulix.AppController;
import com.schedulix.ItemClickWithArray;
import com.schedulix.ModelClasses.*;
import com.schedulix.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.schedulix.AppController.PREF_NAME;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListOfCustomers.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListOfCustomers#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListOfCustomers extends Fragment implements  ItemClickWithArray.ItemClickWithArrayCu{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static String fromAppointment = "0";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "Customer";
    RecyclerView rvListOfCustomer;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Customer> customerArrayList;
    Context context;
    ProgressBar progressBar;
    ListOfCustomerAdapter customerAdapter;
    RequestQueue queue;
    EditText search;
    String customer_id;
    String customer_name;
    TextView emptyView;


    private OnFragmentInteractionListener mListener;

    public ListOfCustomers() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListOfCustomers.
     */
    // TODO: Rename and change types and number of parameters
    public static ListOfCustomers newInstance(String param1, String param2) {
        ListOfCustomers fragment = new ListOfCustomers();
        Bundle args = new Bundle();
        fromAppointment = param1;
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        queue = Volley.newRequestQueue(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        customerArrayList = new ArrayList<>();
        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TAG);

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_list_of_customers, container, false);

        linearLayoutManager = new GridLayoutManager(context,1);
        rvListOfCustomer = (RecyclerView) view.findViewById(R.id.rvListOfCustomer);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        search = (EditText) view.findViewById(R.id.edtSearchCountry);
        emptyView = (TextView) view.findViewById(R.id.empty_view);



        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvListOfCustomer.setLayoutManager(linearLayoutManager);
        rvListOfCustomer.getRecycledViewPool().setMaxRecycledViews(0, 0);
        rvListOfCustomer.setHasFixedSize(true);
        rvListOfCustomer.setItemViewCacheSize(20);
        rvListOfCustomer.setDrawingCacheEnabled(true);
        customerAdapter = new ListOfCustomerAdapter(context);
        // Attach the adapter to the recyclerview to populate items
        rvListOfCustomer.setAdapter(customerAdapter);
        customerAdapter.setClickListener(ListOfCustomers.this);

        ///// call api////
       getCustomers();

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                   customerAdapter.getFilter().filter(cs);
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
    public void onClickWithArray(View view, int position, ArrayList<Customer> arrayList) {
        Log.d(TAG, "onClick: ");



        // add list///
        ArrayList<Customer> customers = new ArrayList<>();
        customers.addAll(arrayList);
        // get object//
        final Customer customer = customers.get(position);

        customer_id = customer.getId();
        customer_name = customer.getFirst_name();

        Log.d(TAG, "onClick: " + customer_id + "-Name" + customer_name);

        if (fromAppointment.equals("0"))
        {
            Log.d(TAG, "onClickWithArray: From appointments");
            //// Edit Customer  here ////

            Fragment fragment = AddCustomer.newInstance("2",customer_id);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.addToBackStack(TAG);
            fragmentTransaction.commit();

            return;
        }

        // add Service id to appointment arrayList///////
        com.schedulix.ModelClasses.AddAppointment.getInstance().setCustomer_id(customer_id);
        Customer.getInstance().setFirst_name(customer_name);

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(fromAppointment.equals("0")) {
            inflater.inflate(R.menu.add_customer, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }else
        {
            return;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_customer) {
            Log.d(TAG, "onOptionsItemSelected : Add Customer here");

            Fragment fragment = AddCustomer.newInstance("0","");
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment);
            fragmentTransaction.addToBackStack(TAG);
            fragmentTransaction.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void getCustomers() {


        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_CUSTOMER,
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

                                    JSONObject customerJsonObj = staffs.getJSONObject(i);
                                    Customer customerObj = new Customer();
                                    customerObj.setId(customerJsonObj.getString("id"));
                                    customerObj.setFirst_name(customerJsonObj.getString("first_name"));
                                    customerObj.setLast_name(customerJsonObj.getString("last_name"));
                                    customerObj.setEmail(customerJsonObj.getString("email"));
                                    customerObj.setMobile_no(customerJsonObj.getString("mobile"));
                                    customerObj.setAddress(customerJsonObj.getString("address"));
                                    customerObj.setCountry(customerJsonObj.getString("country"));
                                    customerObj.setRegion(customerJsonObj.getString("region"));
                                    customerObj.setCity(customerJsonObj.getString("city"));
                                    customerObj.setPic(path+customerJsonObj.getString("picture_url"));
                                    customerArrayList.add(customerObj);
                                }
                                Log.d(TAG, "onResponse: " + customerArrayList.size());

                                // user successfully login..
//                                Toast.makeText(context,
//                                        "" + success.getString("message"),
//                                        Toast.LENGTH_LONG).show();
                            }




                            customerAdapter.setCustomers(customerArrayList);

                            RecyclerView.ItemDecoration itemDecoration = new
                                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                            rvListOfCustomer.addItemDecoration(itemDecoration);

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



}
