package com.schedulix.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.schedulix.ModelClasses.Review;
import com.schedulix.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Bir Al Sabia on 5/9/2017.
 */

public class Adapter_reviews  extends RecyclerView.Adapter<Adapter_reviews.MyViewHolder> {
    private final Context context;
    Paint paint;
    private ArrayList<Review> arrayallbooking;
    private ArrayList<Review> arraylist;

    static Adapter_all_bookings.OnItemClickListener mItemClickListener;
    String baseimg_url = "https://bkmalls.com/assets/images/";

    public Adapter_reviews(Context context, ArrayList<Review> arraybooking) {

        this.context = context;
        this.arrayallbooking = arraybooking;
        this.arraylist = new ArrayList<Review>();
        this.arraylist.addAll(arrayallbooking);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       CircleImageView circleImageView;
        TextView txtName, txtdesc;
        RatingBar ratingBar;


        public MyViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txt_customer_name);
            txtdesc = (TextView) view.findViewById(R.id.txt_desc);
            ratingBar=(RatingBar)view.findViewById(R.id.rating);
            circleImageView=(CircleImageView) view.findViewById(R.id.profile_image);
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
                .inflate(R.layout.allreviews, parent, false);

        return new MyViewHolder(itemView);
        //    return null;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.txtName.setText(arrayallbooking.get(position).getCustomer_name());
        holder.txtdesc.setText(arrayallbooking.get(position).getDescription());
        holder.ratingBar.setRating(Float.parseFloat(arrayallbooking.get(position).getRating()));
        /// set rating ////


    }

    @Override
    public int getItemCount() {
        return arrayallbooking.size();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final Adapter_all_bookings.OnItemClickListener mItemClickListeners) {
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

