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

import com.schedulix.ModelClasses.Service;
import com.schedulix.ModelClasses.Staff;
import com.schedulix.OnItemClickListener;
import com.schedulix.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nsol on 4/25/2017.
 */
public class ServiceAdapter  extends RecyclerView.Adapter<ServiceAdapter.ContactViewHolder>{

    private static final String TAG = StaffAdapter.class.getSimpleName();
    private List<Service> serviceList,filterList;
    private Context mContext;
    private OnItemClickListener clickListener;


    public ServiceAdapter(Context context) {
        this.serviceList = new ArrayList<>();
        filterList = new ArrayList<>();
        this.mContext = context;
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_item_service, null);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.serviceTitle.setText(service.getTitle());
        holder.serviceDuration.setText(" Duration : "+service.getDuration()+" "+service.getDurationType());
        holder.servicePrice.setText("$ "+ service.getPrice());
        Picasso.with(mContext).load(Uri.parse(service.getPicture())).placeholder(R.drawable.face)
                .error(R.drawable.face).into(holder.ivServiceImage);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }



    public  class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView serviceTitle;
        TextView serviceDuration;
        TextView servicePrice;
        ImageView ivServiceImage;

        public ContactViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ivServiceImage = (ImageView) itemView.findViewById(R.id.ivServiceImage);
            serviceTitle = (TextView) itemView.findViewById(R.id.service_title);
            serviceDuration = (TextView) itemView.findViewById(R.id.service_duration);
            servicePrice = (TextView) itemView.findViewById(R.id.service_price);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }




    }
    public void setServices(List<Service> listofServices) {
        Log.d(TAG, "setContacts: "+listofServices);
        serviceList.clear();
        serviceList.addAll(listofServices);
        filterList.clear();
        filterList.addAll(listofServices);
        notifyItemRangeInserted(0, serviceList.size() - 1);
    }





}

