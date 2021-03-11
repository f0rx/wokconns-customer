package com.wokconns.customer.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wokconns.customer.dto.GalleryDTO;
import com.wokconns.customer.R;
import com.wokconns.customer.ui.fragment.ImageGallery;

import java.util.ArrayList;

/**
 * Created by VARUN on 01/01/19.
 */

public class AdapterGallery extends RecyclerView.Adapter<AdapterGallery.MyViewHolder> {
    LayoutInflater mLayoutInflater;
    private ArrayList<GalleryDTO> gallery;
    private Context context;

    public AdapterGallery(ImageGallery imageGallery, ArrayList<GalleryDTO> gallery) {
        context = imageGallery;
        this.gallery = gallery;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_gallery, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Glide
                .with(context)
                .load(gallery.get(position).getImage())
                .placeholder(R.drawable.dummyuser_image)
                .into(holder.iv_bottom_foster);
    }

    @Override
    public int getItemCount() {
        return gallery.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_bottom_foster;

        public MyViewHolder(View view) {
            super(view);

            iv_bottom_foster = (ImageView) itemView.findViewById(R.id.iv_bottom_foster);


        }
    }
}