package com.wokconns.customer.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.AdapterdiscoverBinding;
import com.wokconns.customer.dto.AllAtristListDTO;
import com.wokconns.customer.interfaces.Consts;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.activity.ArtistProfileNew;

import java.util.ArrayList;


public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.MyViewHolder> {

    Context mContext;
    AdapterdiscoverBinding binding;
    private ArrayList<AllAtristListDTO> allAtristListDTOList;
    private LayoutInflater inflater;
    private SharedPrefrence prefrence;

    public DiscoverAdapter(Context mContext, ArrayList<AllAtristListDTO> allAtristListDTOList, LayoutInflater inflater) {
        this.mContext = mContext;
        this.allAtristListDTOList = allAtristListDTOList;
        this.inflater = inflater;
        prefrence = SharedPrefrence.getInstance(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(inflater, R.layout.adapterdiscover, parent, false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.binding.CTVartistwork.setText(allAtristListDTOList.get(position).getCategory_name());
        holder.binding.CTVartistname.setText(allAtristListDTOList.get(position).getName());
        if (allAtristListDTOList.get(position).getArtist_commission_type().equalsIgnoreCase("0")) {
            if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("0")) {
                holder.binding.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + mContext.getResources().getString(R.string.hr_add_on));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("2")) {
                holder.binding.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + mContext.getResources().getString(R.string.hr_add_on));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("1")) {
                holder.binding.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + mContext.getResources().getString(R.string.hr_add_on));
            } else {
                holder.binding.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + mContext.getResources().getString(R.string.hr_add_on));
            }
        } else {
            if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("0")) {
                holder.binding.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + " " + mContext.getResources().getString(R.string.fixed_rate_add_on));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("2")) {
                holder.binding.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + " " + mContext.getResources().getString(R.string.fixed_rate_add_on));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("1")) {
                holder.binding.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + " " + mContext.getResources().getString(R.string.fixed_rate_add_on));
            } else {
                holder.binding.CTVartistchargeprh.setText(allAtristListDTOList.get(position).getCurrency_type() + allAtristListDTOList.get(position).getPrice() + " " + mContext.getResources().getString(R.string.fixed_rate_add_on));
            }
        }

        holder.binding.CTVlocation.setText(allAtristListDTOList.get(position).getLocation());

        holder.binding.CTVjobdone.setText(allAtristListDTOList.get(position).getJobDone());
        holder.binding.tvRating.setText("(" + allAtristListDTOList.get(position).getAva_rating() + "/5)");
        holder.binding.CTVpersuccess.setText(allAtristListDTOList.get(position).getPercentages() + "%");
        Glide.with(mContext).
                load(allAtristListDTOList.get(position).getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.IVartist);
        if (allAtristListDTOList.get(position).getFav_status().equalsIgnoreCase("1")) {
            holder.binding.ivFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_fav_full));
        } else {
            holder.binding.ivFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_fav_blank));
        }
        if (allAtristListDTOList.get(position).getFeatured().equalsIgnoreCase("1")) {
            holder.binding.ivfeatured.setVisibility(View.VISIBLE);
        } else {
            holder.binding.ivfeatured.setVisibility(View.GONE);
        }
        holder.binding.ratingbar.setRating(Float.parseFloat(allAtristListDTOList.get(position).getAva_rating()));
        holder.binding.rlClick.setOnClickListener(v -> {
            Intent in = new Intent(mContext, ArtistProfileNew.class);
            in.putExtra(Consts.ARTIST_ID, allAtristListDTOList.get(position).getUser_id());
            mContext.startActivity(in);
        });
    }

    @Override
    public int getItemCount() {
        return allAtristListDTOList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        AdapterdiscoverBinding binding;

        public MyViewHolder(AdapterdiscoverBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}