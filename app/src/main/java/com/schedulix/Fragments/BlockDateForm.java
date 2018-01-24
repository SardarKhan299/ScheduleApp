package com.schedulix.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.schedulix.Adapter.Adpater_blockdates;
import com.schedulix.AppController;
import com.schedulix.ModelClasses.Allblockdates;
import com.schedulix.ModelClasses.City;
import com.schedulix.ModelClasses.Country;
import com.schedulix.ModelClasses.Customer;
import com.schedulix.ModelClasses.Region;
import com.schedulix.ModelClasses.Staff;
import com.schedulix.R;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import static com.facebook.GraphRequest.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlockDateForm.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlockDateForm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlockDateForm extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = BlockDate.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context context;
    EditText edtStaff,edtDate,edtReason;
    java.util.Calendar myCalendar;
    ProgressBar progressBar;
    RequestQueue queue;
    public static ArrayList<Allblockdates> appointmentArrayList = new ArrayList<>();


    RecyclerView rvSelectedDates;
    LinearLayoutManager linearLayoutManager;
    Adpater_blockdates appointmentAdapter;

    private OnFragmentInteractionListener mListener;

    public BlockDateForm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlockDateForm.
     */
    // TODO: Rename and change types and number of parameters
    public static BlockDateForm newInstance(String param1, String param2) {
        BlockDateForm fragment = new BlockDateForm();
        Bundle args = new Bundle();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        queue = Volley.newRequestQueue(context);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_block_date_form, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Block Date");

        rvSelectedDates = (RecyclerView) view.findViewById(R.id.rvSelectedDates);
        if(appointmentArrayList.size()>0)
        {
            appointmentAdapter = new Adpater_blockdates(context,appointmentArrayList,"0");
            // Attach the adapter to the recyclerview to populate items
            rvSelectedDates.setAdapter(appointmentAdapter);
            appointmentAdapter.notifyDataSetChanged();

            appointmentAdapter.SetOnItemClickListener(new Adpater_blockdates.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    try {

                        appointmentArrayList.remove(position);
                        appointmentAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onItemClick: "+appointmentArrayList.size());
                    }

                    catch (Exception e)
                    {
                        Log.d(TAG, "onItemClick: "+e.getMessage());
                    }

                }
            });



        }

        linearLayoutManager = new GridLayoutManager(context,2);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSelectedDates.setLayoutManager(linearLayoutManager);
        rvSelectedDates.getRecycledViewPool().setMaxRecycledViews(0, 0);

        edtStaff = (EditText) view.findViewById(R.id.edt_staff);
        edtDate = (EditText) view.findViewById(R.id.edt_date);
        edtReason = (EditText) view.findViewById(R.id.edt_reason);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        edtDate.setText("Select Date");





        edtStaff.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    // Display Dialog with Service List....

                    // 2 is for block date to use staff class //
                    Fragment fragment = Staffs.newInstance("2","");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.addToBackStack("BlockDate");
                    fragmentTransaction.commit();


                }

                return true; // return is important...
            }
        });
        myCalendar = java.util.Calendar.getInstance();



        DateTimeFormatter fmt = DateTimeFormat.forPattern(" EEEE , MMM d  ");
        LocalDate date = new LocalDate(com.schedulix.ModelClasses.AddAppointment.getInstance().getBooking_date());
        String selectedDate = date.toString(fmt);
        edtDate.setText(selectedDate);

        edtDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    // Display Dialog with Service List...
                    Fragment fragment = SelectMultipleDate.newInstance("","");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.addToBackStack(TAG);
                    fragmentTransaction.commit();
                }

                return true; // return is important...
            }
        });











        ///// call api////
        //     new RescheduleAppointment.LoadAppointmentDetailAsync(RescheduleAppointment.this).execute();



        return  view;

    }

    @Override
    public void onResume() {
        super.onResume();

        if(Staff.getInstance().getFirst_name()!= null) {
            Log.d(TAG, "onResume: " + Staff.getInstance().getFirst_name());
            edtStaff.setText(Staff.getInstance().getFirst_name().toString());
            // clear first name ///
            Staff.getInstance().setFirst_name("");
            edtStaff.setError(null);
        }

        if(appointmentArrayList.size()>0)
        {
           edtDate.setText("Selected Dates "+appointmentArrayList.size());
        }

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



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.block_save, menu);
            super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            Log.d(TAG, "onOptionsItemSelected : Save ");

            /// Call Api to save block Dates/////
            addBlockDate();

            ///// move back //////
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.popBackStack();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addBlockDate() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_ADD_BLOCK_DATE,
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

                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();


                            }

//                            progressBar.setVisibility(View.GONE);
                            //// call the query here again////


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
                Log.d(TAG, "getParams: Company Id is "+ com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id());



                params.put("company_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id());
                params.put("id", Staff.getInstance().getId());
                params.put("reason", edtReason.getText().toString());
                for(int i =0; i<= appointmentArrayList.size()-1;i++)
                {
                 params.put("date[" + i + "]", appointmentArrayList.get(i).getDate());
                }

                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);


    }


}
