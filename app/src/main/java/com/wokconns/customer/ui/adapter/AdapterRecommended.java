package com.wokconns.customer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.AdapterRecommendedBinding;
import com.wokconns.customer.dto.HomeRecomendedDTO;
import com.wokconns.customer.utils.GlideApp;
import com.wokconns.customer.utils.ProjectUtils;

import java.util.ArrayList;

public class AdapterRecommended extends RecyclerView.Adapter<AdapterRecommended.MyViewHolder> {

    Context mContext;
    ArrayList<HomeRecomendedDTO> recomendedDTOArrayList;
    AdapterRecommendedBinding binding;
    LayoutInflater layoutInflater;

    public AdapterRecommended(Context mContext, ArrayList<HomeRecomendedDTO> recomendedDTOArrayList) {
        this.mContext = mContext;
        this.recomendedDTOArrayList = recomendedDTOArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.adapter_recommended, parent, false);
        View itemView = binding.getRoot();
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.binding.setHomeRecommendedDTO(recomendedDTOArrayList.get(position));

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
        AdapterRecommendedBinding binding;

        public MyViewHolder(AdapterRecommendedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}