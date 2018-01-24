package com.schedulix.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.schedulix.ModelClasses.Allblockdates;
import com.schedulix.ModelClasses.Allbookingsdetails;
import com.schedulix.R;

import java.util.ArrayList;

/**
 * Created by Bir Al Sabia on 5/10/2017.
 */

public class Adpater_blockdates extends RecyclerView.Adapter<Adpater_blockdates.MyViewHolder> {
   private final Context context;
    private ArrayList<Allblockdates> arrayblockdates;
    private ArrayList<Allblockdates> arraylist;

    private Allbookingsdetails allbookingsdetails;
    static OnItemClickListener mItemClickListener;
    String fromTimeFragment;

    public Adpater_blockdates(Context context, ArrayList<Allblockdates> arraybooking,String fromTimeF) {

        this.context = context;
        this.arrayblockdates = arraybooking;
        this.arraylist = new ArrayList<Allblockdates>();
        this.arraylist.addAll(arrayblockdates);
        this.fromTimeFragment = fromTimeF;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtblckdates;
        TextView txtblcktime;


        public MyViewHolder(View view) {
            super(view);

            txtblckdates = (TextView) view.findViewById(R.id.txtblockdates);
            txtblcktime = (TextView) view.findViewById(R.id.txtblocktime);

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
                .inflate(R.layout.adapter_blockdate, parent, false);

        return new MyViewHolder(itemView);
        //    return null;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if(fromTimeFragment.equals("1"))
        {
            holder.txtblcktime.setVisibility(View.VISIBLE);
            holder.txtblckdates.setText(arrayblockdates.get(position).getFromTime()+"/"+arrayblockdates.get(position).getToTime());
            holder.txtblcktime.setText(arrayblockdates.get(position).getDate());
        }
        else {
            holder.txtblcktime.setVisibility(View.GONE);
            holder.txtblckdates.setText(arrayblockdates.get(position).getDate());
        }

    }

    @Override
    public int getItemCount() {
        return arrayblockdates.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListeners) {
        this.mItemClickListener = mItemClickListeners;
    }


}


