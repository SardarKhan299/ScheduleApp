package com.schedulix.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.schedulix.Adapter.Adapter_all_bookings;
import com.schedulix.AppController;
import com.schedulix.ModelClasses.*;
import com.schedulix.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bir Al Sabia on 5/9/2017.
 */
public class Bookings extends Fragment {
    private static final String TAG = Bookings.class.getSimpleName() ;
    FloatingActionButton add;
    RequestQueue queue;
    Allbookingsdetails obj;
    ArrayList<Allbookingsdetails> arraybookingsdetail = new ArrayList<>();
    Adapter_all_bookings allbookingsadpatersdetails;
    LinearLayoutManager Linearlayoutmanager;
    RecyclerView recyclerView;
    Context context;
    TextView emptyView;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        queue = Volley.newRequestQueue(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.rvshowallblockdates);
        Linearlayoutmanager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(Linearlayoutmanager);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        emptyView = (TextView) view.findViewById(R.id.empty_view);

        getallappointments();



        return view;

    }

    public void getallappointments() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_APPOINTMENTS, new Response.Listener<String>() {
            // StringRequest stringRequest = new StringRequest
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectdata=jsonObject.getJSONObject("success");
                    JSONArray jsonobj = jsonObjectdata.getJSONArray("appointments");
                    for (int i = 0; i < jsonobj.length(); i++) {
                        JSONObject jobj = jsonobj.getJSONObject(i);
                        obj = new Allbookingsdetails();
                        obj.setServiceName(jobj.getString("service_title"));
                        obj.setDate(jobj.getString("booking_date"));
                        obj.setStaffname(jobj.getString("staff_name"));
                        obj.setDatefrom(jobj.getString("booking_time"));
                        obj.setPrice(jobj.getString("price"));
                        obj.settime(jobj.getString("starttime"));
                        obj.setId(jobj.getString("id"));
                        obj.setCompany_id(jobj.getString("company_id"));
                        obj.setService_id(jobj.getString("service_id"));
                        obj.setStaff_id(jobj.getString("staff_id"));
                        obj.setCustomerId(jobj.getString("customer_id"));
                        obj.setBooking_note(jobj.getString("appointment_note"));
                        obj.setBooking_duration(jobj.getString("booking_duration"));
                        arraybookingsdetail.add(obj);


                    }
                    allbookingsadpatersdetails = new Adapter_all_bookings(getActivity(), arraybookingsdetail);
                    int currentPosition = Linearlayoutmanager.findLastVisibleItemPosition();
                    allbookingsadpatersdetails.notifyDataSetChanged();
                    recyclerView.setAdapter(allbookingsadpatersdetails);
                    Linearlayoutmanager.scrollToPositionWithOffset(currentPosition - 3, 0);

                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);

                    allbookingsadpatersdetails.SetOnItemClickListener(new Adapter_all_bookings.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Log.d(TAG, "onItemClick: "+position);
                            Bundle b = new Bundle();
                            b.putSerializable("obj",arraybookingsdetail.get(position));
                            Fragment fragment = BookingDetail.newInstance("","");
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragment.setArguments(b);
                            fragmentTransaction.replace(R.id.frame, fragment);
                            fragmentTransaction.addToBackStack(TAG);
                            fragmentTransaction.commit();
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse == null) {
                    if (volleyError instanceof NoConnectionError) {

                        Toast.makeText(getActivity(), "No internet Access, Check your internet connection", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }

                    if (volleyError.getClass().equals(TimeoutError.class)) {
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
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("company_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id());

                return params;

            }


        };
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
