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

import com.schedulix.ModelClasses.Appointment;
import com.schedulix.ModelClasses.Staff;
import com.schedulix.OnItemClickListener;
import com.schedulix.R;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nsol on 4/24/2017.
 */

public class AppointmentAdapter  extends RecyclerView.Adapter<AppointmentAdapter.ContactViewHolder> {

    private static final String TAG = StaffAdapter.class.getSimpleName();
    private List<Appointment> appointmentList;
    private Context mContext;
    private OnItemClickListener clickListener;

    public AppointmentAdapter(Context context) {
        this.appointmentList = new ArrayList<>();
        this.mContext = context;
    }

    public void setClickListener(OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }




    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_item_appointment, null);
       ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        DateTimeFormatter fmt = DateTimeFormat.forPattern(" MMM , d ");
        LocalDate date = new LocalDate(appointment.getAppointment_date().toString());
        String month = date.toString(fmt);

        DateTimeFormatter fmt1 = DateTimeFormat.forPattern("EEEE");
        String day = date.toString(fmt1);

        holder.tvMonth.setText(month);
        holder.tvDay.setText(day);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }



    public  class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvMonth,tvDay;

        public ContactViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvMonth = (TextView) itemView.findViewById(R.id.tvMonth);
            tvDay = (TextView) itemView.findViewById(R.id.tvDay);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }




    }
    public void setApp(List<Appointment> listofAppointments) {
        Log.d(TAG, "setContacts: "+listofAppointments);
        appointmentList.clear();
        appointmentList.addAll(listofAppointments);
        notifyItemRangeInserted(0, listofAppointments.size() - 1);
    }




}
