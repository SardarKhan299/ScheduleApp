package com.schedulix.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.schedulix.ModelClasses.Allbookingsdetails;
import com.schedulix.R;

import java.util.ArrayList;


/**
 * Created by Bir Al Sabia on 11/30/2016.
 */

public class Adapter_all_bookings extends RecyclerView.Adapter<Adapter_all_bookings.MyViewHolder> {
    private final Context context;
    Paint paint;
    private ArrayList<Allbookingsdetails> arrayallbooking;
    private ArrayList<Allbookingsdetails> arraylist;

    private Allbookingsdetails allbookingsdetails;
    static OnItemClickListener mItemClickListener;
    String baseimg_url = "https://bkmalls.com/assets/images/";

    public Adapter_all_bookings(Context context, ArrayList<Allbookingsdetails> arraybooking) {

        this.context = context;
        this.arrayallbooking = arraybooking;
        this.arraylist = new ArrayList<Allbookingsdetails>();
        this.arraylist.addAll(arrayallbooking);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // TextView tvcatogory;

        TextView txtName, txtdatetoandfrom, txtstaffname, txtprice, txtdate, txttime;


        public MyViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.customer_name);
            txtdatetoandfrom = (TextView) view.findViewById(R.id.date_to_from);
            txtstaffname = (TextView) view.findViewById(R.id.staff);
            txtprice = (TextView) view.findViewById(R.id.price);
            txtdate = (TextView) view.findViewById(R.id.date);
            txttime = (TextView) view.findViewById(R.id.time);
            //showprgogress = (ProgressBar) view.findViewById(R.id.progressBar1);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getPosition());
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.allbookings, parent, false);

        return new MyViewHolder(itemView);
        //    return null;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.txtName.setText(arrayallbooking.get(position).getServiceName());
        holder.txtdate.setText(arrayallbooking.get(position).getDate());
        holder.txtstaffname.setText(arrayallbooking.get(position).getStaffname());
        holder.txtprice.setText(arrayallbooking.get(position).getPrice());
        holder.txttime.setText(arrayallbooking.get(position).gettime());
        holder.txtdatetoandfrom.setText(arrayallbooking.get(position).getDatefrom());


    }

    @Override
    public int getItemCount() {
        return arrayallbooking.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListeners) {
        this.mItemClickListener = mItemClickListeners;
    }

   /* public String filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        arraycatimages.clear();
        if (charText.length() == 0) {
            arraycatimages.addAll(arraylist);
        } else {
            for (Categoriesimages search : arraylist) {
                if (search.getMovietitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arraycatimages.add(search);
                }

            }
        }
        notifyDataSetChanged();
        return charText;
    }*/
}
