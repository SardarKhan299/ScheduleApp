package com.schedulix.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.schedulix.ItemClickWithArray;
import com.schedulix.ModelClasses.Customer;
import com.schedulix.ModelClasses.Region;
import com.schedulix.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Nsol on 5/9/2017.
 */

public class ListOfCustomerAdapter extends RecyclerView.Adapter<ListOfCustomerAdapter.ContactViewHolder>implements Filterable {

    private static final String TAG = CountryAdapter.class.getSimpleName();
    private ArrayList<Customer> countryList,filterList,list;
    private Context mContext;
    private ItemClickWithArray.ItemClickWithArrayCu clickListener;
    private CustomFilter mFilter;


    public ListOfCustomerAdapter(Context context) {
        this.countryList = new ArrayList<>();
        filterList = new ArrayList<>();
        list = new ArrayList<>();
        this.mContext = context;
        mFilter = new CustomFilter(ListOfCustomerAdapter.this);
    }

    public void setClickListener(ItemClickWithArray.ItemClickWithArrayCu itemClickListener) {
        this.clickListener = itemClickListener;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_item_list_staff, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Customer customer = countryList.get(position);
        holder.countryTitle.setText(customer.getFirst_name());
        holder.countryTitle.setText(customer.getFirst_name());
        Picasso.with(mContext).load(customer.getPic()).error(R.drawable.face).fit().into(holder.profilePic);
//        holder.serviceDuration.setText(" Duration : "+service.getDuration()+" "+service.getDurationType());
//        holder.servicePrice.setText("$ "+ service.getPrice());
//        Picasso.with(mContext).load(Uri.parse(service.getPicture())).placeholder(R.drawable.face)
//                .error(R.drawable.face).into(holder.ivServiceImage);
    }

    @Override
    public Filter getFilter() {
        if(mFilter==null)
        {
            mFilter = new CustomFilter(ListOfCustomerAdapter.this);
        }
        return mFilter;
    }


    @Override
    public int getItemCount() {
        return countryList.size();
    }



    public  class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView countryTitle;
        ImageView edit;
        CircleImageView profilePic;


        public ContactViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            countryTitle = (TextView) itemView.findViewById(R.id.tvStaffName);
            profilePic = (CircleImageView) itemView.findViewById(R.id.ivStaffImage);
            edit = (ImageView) itemView.findViewById(R.id.edit_list_staff);
            edit.setVisibility(View.INVISIBLE);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClickWithArray(view, getAdapterPosition(),filterList);
        }




    }
    public void setCustomers(List<Customer> listofServices) {
        Log.d(TAG, "setContacts: "+listofServices);
        countryList.clear();
        countryList.addAll(listofServices);
        filterList.clear();
        filterList.addAll(listofServices);
        list.clear();
        list.addAll(listofServices);
        notifyItemRangeInserted(0, countryList.size() - 1);
    }



    public class CustomFilter extends Filter {
        private ListOfCustomerAdapter mAdapter;

        private CustomFilter(ListOfCustomerAdapter mAdapter) {
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
                ArrayList<Customer> filteredCountries =new ArrayList<>();
                for (int i=0;i<list.size();i++)
                {
                    //CHECK
                    if(list.get(i).getFirst_name().toUpperCase().startsWith(constraint.toString()))
                    {
                        //ADD PLAYER TO FILTERED PLAYERS
                        filteredCountries.add(list.get(i));
                    }
                }

                if(filteredCountries.size() >0) {
                    results.count = filteredCountries.size();
                    results.values = filteredCountries;
                }
                else
                {
                    results.count=list.size();
                    results.values=list;
                    //   Toast.makeText(mContext, "No Record Found", Toast.LENGTH_SHORT).show();
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
            System.out.println("Count Countries " + ((List<Customer>) results.values).size());
            this.mAdapter.filterList = (ArrayList<Customer>) results.values;
            this.mAdapter.countryList = (ArrayList<Customer>) results.values;
            this.mAdapter.notifyDataSetChanged();
        }
    }





}


