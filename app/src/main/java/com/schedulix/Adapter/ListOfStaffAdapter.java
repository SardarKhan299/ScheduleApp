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

import com.schedulix.ModelClasses.Region;
import com.schedulix.ModelClasses.Staff;
import com.schedulix.OnItemClickListener;
import com.schedulix.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nsol on 5/2/2017.
 */

public class ListOfStaffAdapter  extends RecyclerView.Adapter<ListOfStaffAdapter.ContactViewHolder> implements Filterable {

    private static final String TAG = StaffAdapter.class.getSimpleName();
    private List<Staff> staffList,filterList,list;
    private Context mContext;
    private OnItemClickListener clickListener;
    private CustomFilter mFilter;

    public ListOfStaffAdapter(Context context) {
        this.staffList = new ArrayList<>();
        filterList = new ArrayList<>();
        list = new ArrayList<>();
        this.mContext = context;
        mFilter = new CustomFilter(ListOfStaffAdapter.this);
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public Filter getFilter() {
        if(mFilter==null)
        {
            mFilter = new CustomFilter(ListOfStaffAdapter.this);
        }
        return mFilter;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_item_list_staff, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Staff staff = staffList.get(position);
        holder.tvStaffName.setText(staff.getFirst_name());
        holder.edit.setVisibility(View.INVISIBLE);
        Picasso.with(mContext).load(Uri.parse(staff.getPicture()))
                .resize(500,500)
                .fit()
                .error(R.drawable.face).into(holder.ivStaffImage);
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }



    public  class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvStaffName;
        ImageView ivStaffImage,edit;

        public ContactViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivStaffImage = (ImageView) itemView.findViewById(R.id.ivStaffImage);
            edit = (ImageView) itemView.findViewById(R.id.edit_list_staff);
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
        list.clear();
        list.addAll(listOfStaffs);
        notifyItemRangeInserted(0, staffList.size() - 1);
    }

    public class CustomFilter extends Filter {
        private ListOfStaffAdapter mAdapter;

        private CustomFilter(ListOfStaffAdapter mAdapter) {
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
                for (int i=0;i<list.size();i++)
                {
                    //CHECK
                    if(list.get(i).getFirst_name().toUpperCase().startsWith(constraint.toString()))
                    {
                        //ADD PLAYER TO FILTERED PLAYERS
                        filteredContacts.add(list.get(i));
                    }
                }

                if(filteredContacts.size() >0) {
                    results.count = filteredContacts.size();
                    results.values = filteredContacts;
                }
                else
                {
                    results.count=list.size();
                    results.values=list;
                    // Toast.makeText(mContext, "No Record Found", Toast.LENGTH_SHORT).show();
                }

            }else
            {
                results.count=list.size();
                results.values=list;
            }

            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            System.out.println(" Staffs are " + ((List<Staff>) results.values).size());
            this.mAdapter.staffList = (ArrayList<Staff>) results.values;
            this.mAdapter.filterList = (ArrayList<Staff>) results.values;
            this.mAdapter.notifyDataSetChanged();
        }
    }



}
