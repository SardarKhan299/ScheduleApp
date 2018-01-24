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

import com.schedulix.ItemClickWithArray;
import com.schedulix.ModelClasses.Country;
import com.schedulix.ModelClasses.Service;
import com.schedulix.OnItemClickListener;
import com.schedulix.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nsol on 5/8/2017.
 */

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ContactViewHolder>implements Filterable {

    private static final String TAG = CountryAdapter.class.getSimpleName();
    private ArrayList<Country> countryList,filterList,list;
    private Context mContext;
    private ItemClickWithArray clickListener;
    private CustomFilter mFilter;


    public CountryAdapter(Context context) {
        this.countryList = new ArrayList<>();
        filterList = new ArrayList<>();
        list = new ArrayList<>();
        this.mContext = context;
        mFilter = new CustomFilter(CountryAdapter.this);
    }

    public void setClickListener(ItemClickWithArray itemClickListener) {
        this.clickListener = itemClickListener;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_item_country, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Country country = countryList.get(position);
        holder.countryTitle.setText(country.getName());
//        holder.serviceDuration.setText(" Duration : "+service.getDuration()+" "+service.getDurationType());
//        holder.servicePrice.setText("$ "+ service.getPrice());
//        Picasso.with(mContext).load(Uri.parse(service.getPicture())).placeholder(R.drawable.face)
//                .error(R.drawable.face).into(holder.ivServiceImage);
    }

    @Override
    public Filter getFilter() {
        if(mFilter==null)
        {
            mFilter = new CustomFilter(CountryAdapter.this);
        }
        return mFilter;
    }


    @Override
    public int getItemCount() {
        return countryList.size();
    }



    public  class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView countryTitle;


        public ContactViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            countryTitle = (TextView) itemView.findViewById(R.id.tvCountryName);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClickWithArray(view, getAdapterPosition(),filterList);
        }




    }
    public void setCountries(List<Country> listofServices) {
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
        private CountryAdapter mAdapter;

        private CustomFilter(CountryAdapter mAdapter) {
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
                ArrayList<Country> filteredCountries =new ArrayList<>();
                for (int i=0;i<list.size();i++)
                {
                    //CHECK
                    if(list.get(i).getName().toUpperCase().startsWith(constraint.toString()))
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
            System.out.println("Count Countries " + ((List<Country>) results.values).size());
            this.mAdapter.filterList = (ArrayList<Country>) results.values;
            this.mAdapter.countryList = (ArrayList<Country>) results.values;
            this.mAdapter.notifyDataSetChanged();
        }
    }





}

