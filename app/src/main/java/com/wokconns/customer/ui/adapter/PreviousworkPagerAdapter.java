package com.wokconns.customer.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wokconns.customer.R;
import com.wokconns.customer.dto.ArtistBookingDTO;
import com.wokconns.customer.preferences.SharedPrefs;
import com.wokconns.customer.utils.CustomTextView;
import com.wokconns.customer.utils.CustomTextViewBold;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class PreviousworkPagerAdapter extends RecyclerView.Adapter<PreviousworkPagerAdapter.MyViewHolder> {
    Context context;
    LayoutInflater layoutInflater;
    private SharedPrefs prefrence;
    private ArrayList<ArtistBookingDTO> artistBookingDTOList;

    public PreviousworkPagerAdapter(Context context, ArrayList<ArtistBookingDTO> artistBookingDTOList) {
        this.context = context;
        this.artistBookingDTOList = artistBookingDTOList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        prefrence = SharedPrefs.getInstance(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.previousworkpageradapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).
                load(artistBookingDTOList.get(position).getUserImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.IVartist);
        holder.ratingbar.setRating(Float.parseFloat(artistBookingDTOList.get(position).getRating()));
        holder.CTVBprevioususer.setText(artistBookingDTOList.get(position).getUsername());
        holder.CTVprice.setText(artistBookingDTOList.get(position).getCurrency_type() + artistBookingDTOList.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return artistBookingDTOList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView IVartist;
        CustomTextViewBold CTVBprevioususer;
        RatingBar ratingbar;
        CustomTextView CTVprice;

        public MyViewHolder(View view) {
            super(view);

            IVartist = (CircleImageView) itemView.findViewById(R.id.IVartist);
            CTVBprevioususer = itemView.findViewById(R.id.CTVBprevioususer);
            ratingbar = itemView.findViewById(R.id.ratingbar);
            CTVprice = itemView.findViewById(R.id.CTVprice);

        }
    }
}


