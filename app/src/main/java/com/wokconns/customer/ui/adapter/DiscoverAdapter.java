package com.wokconns.customer.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.AdapterdiscoverBinding;
import com.wokconns.customer.dto.AllAtristListDTO;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.activity.ArtistProfileNew;
import com.wokconns.customer.utils.GlideApp;
import com.wokconns.customer.utils.ProjectUtils;

import java.util.ArrayList;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.MyViewHolder> {

    Context mContext;
    AdapterdiscoverBinding binding;
    private final ArrayList<AllAtristListDTO> allAtristListDTOList;
    private final LayoutInflater inflater;

    public DiscoverAdapter(Context mContext, ArrayList<AllAtristListDTO> allAtristListDTOList, LayoutInflater inflater) {
        this.mContext = mContext;
        this.allAtristListDTOList = allAtristListDTOList;
        this.inflater = inflater;
        SharedPrefrence preference = SharedPrefrence.getInstance(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                holder.binding.CTVartistchargeprh.setText(String.format("%s%s%s", allAtristListDTOList.get(position).getCurrency_type(), allAtristListDTOList.get(position).getPrice(), mContext.getResources().getString(R.string.hr_add_on)));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("2")) {
                holder.binding.CTVartistchargeprh.setText(String.format("%s%s%s", allAtristListDTOList.get(position).getCurrency_type(), allAtristListDTOList.get(position).getPrice(), mContext.getResources().getString(R.string.hr_add_on)));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("1")) {
                holder.binding.CTVartistchargeprh.setText(String.format("%s%s%s", allAtristListDTOList.get(position).getCurrency_type(), allAtristListDTOList.get(position).getPrice(), mContext.getResources().getString(R.string.hr_add_on)));
            } else {
                holder.binding.CTVartistchargeprh.setText(String.format("%s%s%s", allAtristListDTOList.get(position).getCurrency_type(), allAtristListDTOList.get(position).getPrice(), mContext.getResources().getString(R.string.hr_add_on)));
            }
        } else {
            if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("0")) {
                holder.binding.CTVartistchargeprh.setText(String.format("%s%s %s", allAtristListDTOList.get(position).getCurrency_type(), allAtristListDTOList.get(position).getPrice(), mContext.getResources().getString(R.string.fixed_rate_add_on)));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("2")) {
                holder.binding.CTVartistchargeprh.setText(String.format("%s%s %s", allAtristListDTOList.get(position).getCurrency_type(), allAtristListDTOList.get(position).getPrice(), mContext.getResources().getString(R.string.fixed_rate_add_on)));
            } else if (allAtristListDTOList.get(position).getCommission_type().equalsIgnoreCase("1") && allAtristListDTOList.get(position).getFlat_type().equalsIgnoreCase("1")) {
                holder.binding.CTVartistchargeprh.setText(String.format("%s%s %s", allAtristListDTOList.get(position).getCurrency_type(), allAtristListDTOList.get(position).getPrice(), mContext.getResources().getString(R.string.fixed_rate_add_on)));
            } else {
                holder.binding.CTVartistchargeprh.setText(String.format("%s%s %s", allAtristListDTOList.get(position).getCurrency_type(), allAtristListDTOList.get(position).getPrice(), mContext.getResources().getString(R.string.fixed_rate_add_on)));
            }
        }

        holder.binding.CTVlocation.setText(allAtristListDTOList.get(position).getLocation());

        holder.binding.CTVjobdone.setText(allAtristListDTOList.get(position).getJobDone());
        holder.binding.tvRating.setText(String.format("(%s/5)", allAtristListDTOList.get(position).getAva_rating()));
        holder.binding.CTVpersuccess.setText(String.format("%s%%", allAtristListDTOList.get(position).getPercentages()));

        GlideApp.with(mContext).
                load(ProjectUtils.formatImageUri(allAtristListDTOList.get(position).getImage()))
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
            in.putExtra(Const.ARTIST_ID, allAtristListDTOList.get(position).getUser_id());
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