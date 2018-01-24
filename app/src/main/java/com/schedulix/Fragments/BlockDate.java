package com.schedulix.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import com.github.clans.fab.FloatingActionButton;
import com.schedulix.Adapter.Adpater_blockdates;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlockDate.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlockDate#newInstance} factory method to
 * create an instance of this fragment.
 */


public class BlockDate extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Allblockdates obj;
    ArrayList<Allblockdates> arrayblockdates = new ArrayList<>();

    Context context;

    FloatingActionButton addBlockDate;
    RequestQueue queue;
    TextView emptyView;
    ProgressBar progressBar;

    private OnFragmentInteractionListener mListener;

    RecyclerView rvSelectedDates;
    LinearLayoutManager linearLayoutManager;
    Adpater_blockdates appointmentAdapter;
    static String staff_id;

    public BlockDate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlockDate.
     */
    // TODO: Rename and change types and number of parameters
    public static BlockDate newInstance(String param1, String param2) {
        BlockDate fragment = new BlockDate();
        staff_id = param1;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(context);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_block_date, container, false);
        addBlockDate = (FloatingActionButton) view.findViewById(R.id.addBlockDate);

        rvSelectedDates = (RecyclerView) view.findViewById(R.id.rvBlockDates);

        linearLayoutManager = new GridLayoutManager(context,2);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSelectedDates.setLayoutManager(linearLayoutManager);
        rvSelectedDates.getRecycledViewPool().setMaxRecycledViews(0, 0);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        emptyView = (TextView) view.findViewById(R.id.empty_view);

        GetAllblockdates();

        addBlockDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = BlockDateForm.newInstance("","");
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment);
                fragmentTransaction.addToBackStack("BlockDate");
                fragmentTransaction.commit();
            }
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


    public void GetAllblockdates() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_BLOCK_DATES, new Response.Listener<String>() {
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

//                        Toast.makeText(context,
//                                "" + jsonObjectdata.getString("message"),
//                                Toast.LENGTH_LONG).show();

                        JSONArray jsonobj = jsonObjectdata.getJSONArray("staffblockdates");

                        for (int i = 0; i < jsonobj.length(); i++) {
                            JSONObject jobj = jsonobj.getJSONObject(i);
                            obj = new Allblockdates();
                            obj.setDate(jobj.getString("date"));
                            arrayblockdates.add(obj);
                        }




                    }





                    appointmentAdapter = new Adpater_blockdates(context,arrayblockdates,"0");
                    // Attach the adapter to the recyclerview to populate items
                    rvSelectedDates.setAdapter(appointmentAdapter);
                    appointmentAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);

                    appointmentAdapter.SetOnItemClickListener(new Adpater_blockdates.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            try {

//                                arrayblockdates.remove(position);
//                                appointmentAdapter.notifyDataSetChanged();
//                                Log.d("tag", "onItemClick: "+arrayblockdates.size());
                            }

                            catch (Exception e)
                            {
                                Log.d("tag", "onItemClick: "+e.getMessage());
                            }

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

                        Toast.makeText(getActivity(),
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
                params.put("staff_id",staff_id);

                return params;

            }


        };
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);

    }



}
