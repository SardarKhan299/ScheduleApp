package com.schedulix.Fragments;

import android.content.Context;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.schedulix.ModelClasses.Allbookingsdetails;
import com.schedulix.ModelClasses.Appointment;
import com.schedulix.R;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.facebook.GraphRequest.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookingDetail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookingDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingDetail extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BookingDetail() {
        // Required empty public constructor
    }

    FloatingActionButton add;
    RequestQueue queue;
    Allbookingsdetails obj;
    ArrayList<Allbookingsdetails> arraybookingsdetail = new ArrayList<>();
    Context context;

    ///// Butter Knife Varaible Initialization..////
    @BindView(R.id.username) TextView customerName;
    @BindView(R.id.email) TextView customerEmail;
    @BindView(R.id.phnumber) TextView customerNumber;
    @BindView(R.id.service_name) TextView serviceName;
    @BindView(R.id.date_to_from) TextView timeToFrom;
    @BindView(R.id.staff) TextView staffName;
    @BindView(R.id.price) TextView price;
    @BindView(R.id.booking_date) TextView bookingDate;
    @BindView(R.id.txt_note) TextView noteText;
    @BindView(R.id.fab_reschedule) ImageView reschdule;






    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingDetail newInstance(String param1, String param2) {
        BookingDetail fragment = new BookingDetail();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getArguments() != null) {
            obj = (Allbookingsdetails) getArguments().getSerializable("obj");
            Log.d(TAG, "onCreateView: "+obj.toString());
        }

        View view = inflater.inflate(R.layout.fragment_booking_detail, container, false);
        queue = Volley.newRequestQueue(getActivity());
        ButterKnife.bind(this,view);
        showAppointmentDetail();

        return view;

    }

    @OnClick(R.id.fab_reschedule)
    public void rescheduleAppointment()
    {
        Log.d(TAG, "rescheduleAppointment: ");


        ///////////////// Add Appointment data/////////////////////////////
        Appointment.getInstance().setId(arraybookingsdetail.get(0).getId());
        Appointment.getInstance().setStaff_id(arraybookingsdetail.get(0).getStaff_id());
        Appointment.getInstance().setService_id(arraybookingsdetail.get(0).getService_id());
        Appointment.getInstance().setCompany_id(arraybookingsdetail.get(0).getCompany_id());



        Log.d(TAG, "onClick: "+Appointment.getInstance().toString());


        Fragment fragment = RescheduleAppointment.newInstance("","");
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();

    }

    private void showAppointmentDetail() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_APP_DETAIL, new Response.Listener<String>() {
            // StringRequest stringRequest = new StringRequest
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectdata = jsonObject.getJSONObject("success");

                    int status = jsonObjectdata.getInt("status");
                    if (status == 0) {
                        // user not login..
                        Toast.makeText(context,
                                "" + jsonObjectdata.getString("message"),
                                Toast.LENGTH_LONG).show();
                    } else {


                        Toast.makeText(context,
                                "" + jsonObjectdata.getString("message"),
                                Toast.LENGTH_LONG).show();

                    JSONArray jsonobj = jsonObjectdata.getJSONArray("appointmentdetail");
                    for (int i = 0; i < jsonobj.length(); i++) {
                        JSONObject jobj = jsonobj.getJSONObject(i);
                       Allbookingsdetails obj = new Allbookingsdetails();
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
                        obj.setCustomerName(jobj.getString("first_name"));
                        obj.setCustomerEmail(jobj.getString("email"));
                        obj.setCustomerPhoneNumber(jobj.getString("mobile"));
                        arraybookingsdetail.add(obj);

                    }


                        Log.d(TAG, "onResponse: Booking Detail size is"+arraybookingsdetail.get(0).getId());
                        customerName.setText(arraybookingsdetail.get(0).getCustomerName());
                        customerEmail.setText(arraybookingsdetail.get(0).getCustomerEmail());
                        customerNumber.setText(arraybookingsdetail.get(0).getCustomerPhoneNumber());
                        serviceName.setText(arraybookingsdetail.get(0).getServiceName());
                        timeToFrom.setText(arraybookingsdetail.get(0).getDatefrom());
                        staffName.setText(arraybookingsdetail.get(0).getStaffname());
                        price.setText(arraybookingsdetail.get(0).getPrice());
                        noteText.setText(arraybookingsdetail.get(0).getBooking_note());

                        DateTimeFormatter fmt = DateTimeFormat.forPattern(" EEEE , d MMM , yyyy");
                        LocalDate date = new LocalDate(arraybookingsdetail.get(0).getDate().toString());
                        String month = date.toString(fmt);
                        bookingDate.setText(month);


                }




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
                    }

                    if (volleyError.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message

                        Toast.makeText(getActivity(),
                                "No internet connection.  Please connect to the internet and try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("company_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id());
                params.put("service_id", obj.getService_id());
                params.put("staff_id", obj.getStaff_id());
                params.put("id", obj.getId());

                return params;

            }


        };
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);

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
