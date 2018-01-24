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
import com.schedulix.OnItemClickListener;
import com.schedulix.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nsol on 4/7/2017.
 */
public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ContactViewHolder> implements Filterable {

    private static final String TAG = StaffAdapter.class.getSimpleName();
    private List<Staff> staffList,filterList;
    private Context mContext;
    private OnItemClickListener clickListener;
    private CustomFilter mFilter;

    public StaffAdapter(Context context) {
        this.staffList = new ArrayList<>();
        filterList = new ArrayList<>();
        this.mContext = context;
        mFilter = new CustomFilter(StaffAdapter.this);
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public Filter getFilter() {
        if(mFilter==null)
        {
            mFilter = new CustomFilter(StaffAdapter.this);
        }
        return mFilter;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_item_contact, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Staff staff = staffList.get(position);
        holder.tvStaffName.setText(staff.getFirst_name());
        Picasso.with(mContext).load(Uri.parse(staff.getPicture())).placeholder(R.drawable.face)
                .error(R.drawable.face).into(holder.ivStaffImage);
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }



    public  class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvStaffName;
        ImageView ivStaffImage;

        public ContactViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivStaffImage = (ImageView) itemView.findViewById(R.id.ivStaffImage);
            tvStaffName = (TextView) itemView.findViewById(R.id.tvStaffName);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }




    }
    public void setStaffs(List<Staff> listOfStaffs) {
        Log.d(TAG, "setContacts: "+listOfStaffs);
        staffList.clear();
        staffList.addAll(listOfStaffs);
        filterList.clear();
        filterList.addAll(listOfStaffs);
        notifyItemRangeInserted(0, staffList.size() - 1);
    }

    public class CustomFilter extends Filter {
        private StaffAdapter mAdapter;

        private CustomFilter(StaffAdapter mAdapter) {
            super();
            this.mAdapter = mAdapter;
        }
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results=new FilterResults();

            //CHECK CONSTRAINT VALIDITY
            if(constraint != null && constraint.length() > 0)
            {
                //CHANGE TO UPPER
                constraint=constraint.toString().toUpperCase();
                //STORE OUR FILTERED PLAYERS
                ArrayList<Staff> filteredContacts=new ArrayList<>();
                for (int i=0;i<filterList.size();i++)
                {
                    //CHECK
                    if(filterList.get(i).getFirst_name().toUpperCase().startsWith(constraint.toString()))
                    {
                        //ADD PLAYER TO FILTERED PLAYERS
                        filteredContacts.add(filterList.get(i));
                    }
                }

                if(filteredContacts.size() >0) {
                    results.count = filteredContacts.size();
                    results.values = filteredContacts;
                }
                else
                {
                    results.count=filterList.size();
                    results.values=filterList;
                    // Toast.makeText(mContext, "No Record Found", Toast.LENGTH_SHORT).show();
                }

            }else
            {
                results.count=filterList.size();
                results.values=filterList;
            }

            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println(" Staffs are " + ((List<Staff>) results.values).size());
            this.mAdapter.staffList = (ArrayList<Staff>) results.values;
            this.mAdapter.notifyDataSetChanged();
        }
    }



}
