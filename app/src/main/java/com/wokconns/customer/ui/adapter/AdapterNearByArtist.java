package com.wokconns.customer.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.AdapterNearByArtistBinding;
import com.wokconns.customer.dto.HomeNearByArtistsDTO;
import com.wokconns.customer.utils.GlideApp;
import com.wokconns.customer.utils.ProjectUtils;

import java.util.ArrayList;

public class AdapterNearByArtist extends RecyclerView.Adapter<AdapterNearByArtist.MyViewHolder> {

    Context mContext;
    ArrayList<HomeNearByArtistsDTO> recomendedDTOArrayList;
    AdapterNearByArtistBinding binding;
    LayoutInflater layoutInflater;

    public AdapterNearByArtist(Context mContext, ArrayList<HomeNearByArtistsDTO> recomendedDTOArrayList) {
        this.mContext = mContext;
        this.recomendedDTOArrayList = recomendedDTOArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.adapter_near_by_artist, parent, false);
        View itemView = binding.getRoot();
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.binding.setHomeNearByArtistsDTO(recomendedDTOArrayList.get(position));

        GlideApp.with(mContext).
                load(ProjectUtils.formatImageUri(recomendedDTOArrayList.get(position).getImage()))
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.cIvImage);
    }

    @Override
    public int getItemCount() {
        return recomendedDTOArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        AdapterNearByArtistBinding binding;

        public MyViewHolder(AdapterNearByArtistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}