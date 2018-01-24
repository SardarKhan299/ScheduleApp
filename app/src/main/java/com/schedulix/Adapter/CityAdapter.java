package com.schedulix.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.schedulix.ItemClickWithArray;
import com.schedulix.ModelClasses.City;
import com.schedulix.ModelClasses.Region;
import com.schedulix.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nsol on 5/8/2017.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ContactViewHolder>implements Filterable {

    private static final String TAG = CountryAdapter.class.getSimpleName();
    private ArrayList<City> countryList,filterList,list;
    private Context mContext;
    private ItemClickWithArray.ItemClickWithArrayC clickListener;
    private CustomFilter mFilter;


    public CityAdapter(Context context) {
        this.countryList = new ArrayList<>();
        filterList = new ArrayList<>();
        list = new ArrayList<>();
        this.mContext = context;
        mFilter = new CustomFilter(CityAdapter.this);
    }

    public void setClickListener(ItemClickWithArray.ItemClickWithArrayC itemClickListener) {
        this.clickListener = itemClickListener;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_item_country, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(CityAdapter.ContactViewHolder holder, int position) {
        City city = countryList.get(position);
        holder.countryTitle.setText(city.getName());
//        holder.serviceDuration.setText(" Duration : "+service.getDuration()+" "+service.getDurationType());
//        holder.servicePrice.setText("$ "+ service.getPrice());
//        Picasso.with(mContext).load(Uri.parse(service.getPicture())).placeholder(R.drawable.face)
//                .error(R.drawable.face).into(holder.ivServiceImage);
    }

    @Override
    public Filter getFilter() {
        if(mFilter==null)
        {
            mFilter = new CustomFilter(CityAdapter.this);
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
    public void setRegions(List<City> listofServices) {
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
        private CityAdapter mAdapter;

        private CustomFilter(CityAdapter mAdapter) {
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
                ArrayList<City> filteredCountries =new ArrayList<>();
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
            System.out.println("Count Countries " + ((List<City>) results.values).size());
            this.mAdapter.filterList = (ArrayList<City>) results.values;
            this.mAdapter.countryList = (ArrayList<City>) results.values;
            this.mAdapter.notifyDataSetChanged();
        }
    }





}

