package com.schedulix.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.schedulix.ModelClasses.Staff;
import com.schedulix.ModelClasses.StaffTimings;
import com.schedulix.OnItemClickListener;
import com.schedulix.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nsol on 4/26/2017.
 */

public class StaffTimingsAdapter extends RecyclerView.Adapter<StaffTimingsAdapter.ContactViewHolder>  {

    private static final String TAG = StaffAdapter.class.getSimpleName();
    private List<StaffTimings> staffList;
    private Context mContext;
    private OnItemClickListener clickListener;

    public StaffTimingsAdapter(Context context) {
        this.staffList = new ArrayList<>();
        this.mContext = context;
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }



    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_item_timings, null);
       ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        StaffTimings staff = staffList.get(position);
        holder.tvStaffTimings.setText("Day :"+staff.getService_day() +
                " Timings :"+staff.getStart_time()+" - "+staff.getEnd_time());
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }



    public  class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvStaffTimings;

        public ContactViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvStaffTimings = (TextView) itemView.findViewById(R.id.tvStaffTimings);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }




    }
    public void setStaffs(List<StaffTimings> listOfStaffs) {
        Log.d(TAG, "setContacts: "+listOfStaffs);
        staffList.clear();
        staffList.addAll(listOfStaffs);
        notifyItemRangeInserted(0, staffList.size() - 1);
    }





}
