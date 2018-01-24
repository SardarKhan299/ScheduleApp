package com.schedulix.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.schedulix.Adapter.Adpater_blockdates;
import com.schedulix.ModelClasses.Allblockdates;
import com.schedulix.R;

import static com.facebook.GraphRequest.TAG;
import static com.schedulix.Fragments.BlockTimeForm.blockTimeArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelectMultipleDateTime.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectMultipleDateTime#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectMultipleDateTime extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    public MaterialCalendarView mvc ;
    Context context;
    boolean dateSelected = true;
    String appointmentDate ;



    RecyclerView rvSelectedDates;
    LinearLayoutManager linearLayoutManager;
    Adpater_blockdates appointmentAdapter;

    ProgressBar progressBar;


    public SelectMultipleDateTime() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectMultipleDateTime.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectMultipleDateTime newInstance(String param1, String param2) {
        SelectMultipleDateTime fragment = new SelectMultipleDateTime();
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
        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Select Date");
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_select_multiple_date_time, container, false);
        mvc = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        rvSelectedDates = (RecyclerView) view.findViewById(R.id.rvSelectedDates);
        if(blockTimeArrayList.size()>0)
        {
            appointmentAdapter = new Adpater_blockdates(context,blockTimeArrayList,"0");
            // Attach the adapter to the recyclerview to populate items
            rvSelectedDates.setAdapter(appointmentAdapter);
            appointmentAdapter.notifyDataSetChanged();

        }


        linearLayoutManager = new GridLayoutManager(context,2);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSelectedDates.setLayoutManager(linearLayoutManager);
        rvSelectedDates.getRecycledViewPool().setMaxRecycledViews(0, 0);


        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);





        mvc.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                dateSelected = true;
                int year = date.getYear();
                int month = (date.getMonth()+1);
                int day = date.getDay();
                Log.d(TAG, "onDateSelected: "+year+"-"+month+"-"+day);
                String selectedDate = String.valueOf(year+"-"+month+"-"+day);
                appointmentDate = selectedDate;
                Log.d(TAG, "onDateSelected: "+selectedDate);

                Allblockdates allblockdates = new Allblockdates();
                allblockdates.setDate(selectedDate);


                blockTimeArrayList.add(allblockdates);
                appointmentAdapter = new Adpater_blockdates(context,blockTimeArrayList,"0");
                // Attach the adapter to the recyclerview to populate items
                rvSelectedDates.setAdapter(appointmentAdapter);
                // appointmentAdapter.notifyDataSetChanged();

                appointmentAdapter.SetOnItemClickListener(new Adpater_blockdates.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {

                            blockTimeArrayList.remove(position);
                            appointmentAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onItemClick: "+blockTimeArrayList.size());
                        }

                        catch (Exception e)
                        {
                            Log.d(TAG, "onItemClick: "+e.getMessage());
                        }

                    }
                });

                // add Date to recylerview //

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
            Log.d("tag", "onOptionsItemSelected : Save ");

            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove( new SelectMultipleDate());
            trans.commit();
            manager.popBackStack();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
