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
import com.wokconns.customer.databinding.AdapterInvoiceBinding;
import com.wokconns.customer.dto.HistoryDTO;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.utils.ProjectUtils;

import java.util.ArrayList;
import java.util.Locale;

public class AdapterInvoice extends RecyclerView.Adapter<AdapterInvoice.MyViewHolder> {

    Context mContext;
    ArrayList<HistoryDTO> objects = null;
    ArrayList<HistoryDTO> historyDTOList;
    private SharedPrefrence preference;
    private LayoutInflater inflater;
    AdapterInvoiceBinding binding;

    public AdapterInvoice(Context mContext, ArrayList<HistoryDTO> objects, LayoutInflater inflater) {
        this.mContext = mContext;
        this.objects = objects;
        this.historyDTOList = new ArrayList<HistoryDTO>();
        this.historyDTOList.addAll(objects);
        this.inflater = inflater;
        preference = SharedPrefrence.getInstance(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(inflater, R.layout.adapter_invoice, parent, false);
        View itemView = binding.getRoot();
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.binding.CTVBservice.setText(mContext.getResources().getString(R.string.service) + " " + objects.get(position).getInvoice_id());
        try {
            holder.binding.CTVdate.setText(ProjectUtils.convertTimestampDateToTime(ProjectUtils.correctTimestamp(Long.parseLong(objects.get(position).getCreated_at()))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.binding.CTVprice.setText(objects.get(position).getCurrency_type() + objects.get(position).getFinal_amount());
        holder.binding.CTVwork.setText(objects.get(position).getCategoryName());
        holder.binding.CTVname.setText(ProjectUtils.getFirstLetterCapital(objects.get(position).getUserName()));

        Glide.with(mContext).
                load(objects.get(position).getUserImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.IVprofile);
        if (objects.get(position).getFlag().equalsIgnoreCase("0")) {
            holder.binding.tvStatus.setText(mContext.getResources().getString(R.string.unpaid));
            holder.binding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_orange));
        } else if (objects.get(position).getFlag().equalsIgnoreCase("1")) {
            holder.binding.tvStatus.setText(mContext.getResources().getString(R.string.paid));
            holder.binding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
        }

    }

    @Override
    public int getItemCount() {

        return objects.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        AdapterInvoiceBinding binding;

        public MyViewHolder(AdapterInvoiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        objects.clear();
        if (charText.length() == 0) {
            objects.addAll(historyDTOList);
        } else {
            for (HistoryDTO historyDTO : historyDTOList) {
                if (historyDTO.getInvoice_id().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    objects.add(historyDTO);
                }
            }
        }
        notifyDataSetChanged();
    }

}