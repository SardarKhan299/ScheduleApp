package com.schedulix.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.schedulix.ModelClasses.Staff;
import com.schedulix.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FutureUnavailabilty.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FutureUnavailabilty#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FutureUnavailabilty extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    Context context;
    TextView tvStaffName,tvStaffEmail,tvStaffNumber;
    CircleImageView pic;

    String staff_id ;
    String staff_name ;
    String staff_email ;
    String staff_num ;
    String staff_image ;



    private OnFragmentInteractionListener mListener;

    public FutureUnavailabilty() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FutureUnavailabilty.
     */
    // TODO: Rename and change types and number of parameters
    public static FutureUnavailabilty newInstance(String param1, String param2) {
        FutureUnavailabilty fragment = new FutureUnavailabilty();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_future_unavailabilty, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Future Unavailability");

        Bundle b = getArguments();
        if(b!= null) {
           staff_id =   b.getString("staff_id");
           staff_name =  b.getString("staff_name");
           staff_num =  b.getString("staff_num");
           staff_email =  b.getString("staff_email");
           staff_image =     b.getString("staff_image");
        }


        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tvStaffName = (TextView) view.findViewById(R.id.tv_staff_name);
        tvStaffEmail = (TextView) view.findViewById(R.id.tv_staff_email);
        tvStaffNumber = (TextView) view.findViewById(R.id.tv_staff_nunber);
        pic = (CircleImageView) view.findViewById(R.id.staff_image);



        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        if(staff_id!=null)
        {
            tvStaffName.setText(staff_name);
            tvStaffEmail.setText(staff_email);
            tvStaffNumber.setText(staff_num);
            Picasso.with(context).load(Uri.parse(staff_image))
                    .fit()
                    .error(R.drawable.face).into(pic);


//            /// clear All Fields for Future Use///////
//          //  Staff.getInstance().setId("");
//            Staff.getInstance().setFirst_name("");
//            Staff.getInstance().setPhone_num("");
//            Staff.getInstance().setEmail("");
//            Staff.getInstance().setPicture("");


        }
    }

    private void setupViewPager(ViewPager viewPager) {
        //////////////////// Never use Get Support Fragment Manager inside another fragment /////////////
        /////////////////// because it cause blank fragment issue when navigate back//////////////////
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(BlockDate.newInstance(staff_id,""), "Blocked Date");
        adapter.addFragment(BlockTime.newInstance(staff_id,""), "Block Time");
        adapter.addFragment(GoogleCalendar.newInstance("",""), "Google Calendar");
        viewPager.setAdapter(adapter);
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
