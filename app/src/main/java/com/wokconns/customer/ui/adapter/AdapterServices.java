package com.wokconns.customer.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivityViewServicesBinding;
import com.wokconns.customer.databinding.AdapterServicesBinding;
import com.wokconns.customer.dto.ArtistDetailsDTO;
import com.wokconns.customer.dto.ProductDTO;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.ui.activity.ServiceSlider;
import com.wokconns.customer.ui.activity.ViewServices;

import java.util.ArrayList;


public class AdapterServices extends RecyclerView.Adapter<AdapterServices.MyViewHolder> {
    public ActivityViewServicesBinding binding;
    boolean isHide = false;
    boolean select = true;
    AdapterServicesBinding servicesBinding;
    private LayoutInflater mLayoutInflater;
    private ViewServices context;
    private ArrayList<ProductDTO> productDTOList;
    private ArtistDetailsDTO artistDetailsDTO;

    public AdapterServices(ViewServices context, ActivityViewServicesBinding binding, ArrayList<ProductDTO> productDTOList, ArtistDetailsDTO artistDetailsDTO) {
        this.context = context;
        this.binding = binding;
        this.productDTOList = productDTOList;
        this.artistDetailsDTO = artistDetailsDTO;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        servicesBinding = DataBindingUtil.inflate(mLayoutInflater, R.layout.adapter_services, parent, false);
        return new MyViewHolder(servicesBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Glide
                .with(context)
                .load(productDTOList.get(position).getProduct_image())
                .placeholder(R.drawable.dummyuser_image)
                .into(holder.servicesBinding.ivBottomFoster);
        holder.servicesBinding.tvPrice.setText(productDTOList.get(position).getCurrency_type() + productDTOList.get(position).getPrice());
        holder.servicesBinding.tvProductName.setText(productDTOList.get(position).getProduct_name());


        holder.servicesBinding.ivBottomFoster.setOnClickListener(v -> {
            Intent in = new Intent(context, ServiceSlider.class);
            in.putExtra(Const.DTO, productDTOList);
            in.putExtra(Const.POSTION, position);
            in.putExtra(Const.ARTIST_DTO, artistDetailsDTO);
            in.putExtra(Const.ARTIST_ID, artistDetailsDTO.getUser_id());
            context.startActivity(in);
        });

        if (productDTOList.get(position).isSelected()) {
            holder.servicesBinding.cbSelect.setChecked(true);

        } else {
            holder.servicesBinding.cbSelect.setChecked(false);
        }


        holder.servicesBinding.cbSelect.setOnClickListener(v -> {
            if (productDTOList.get(position).isSelected()) {
                productDTOList.get(position).setSelected(false);

                int quantity = Integer.parseInt(binding.tvPrice.getText().toString());
                quantity = quantity - Integer.parseInt(productDTOList.get(position).getPrice());
                binding.tvPrice.setText("" + quantity);

            } else {
                productDTOList.get(position).setSelected(true);
                if (select) {
                    binding.tvPrice.setText(productDTOList.get(position).getPrice());
                    binding.tvPriceType.setText(productDTOList.get(position).getCurrency_type());
                    select = false;
                } else {
                    int quantity = Integer.parseInt(binding.tvPrice.getText().toString());
                    quantity = quantity + Integer.parseInt(productDTOList.get(position).getPrice());
                    binding.tvPrice.setText("" + quantity);
                }
            }
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return productDTOList.size();
    }

    private void fadeInAndShowView(final RelativeLayout img) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(500);

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(View.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        img.startAnimation(fadeIn);
    }

    private void fadeOutAndHideView(final RelativeLayout img) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        img.startAnimation(fadeOut);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        AdapterServicesBinding servicesBinding;

        public MyViewHolder(AdapterServicesBinding servicesBinding) {
            super(servicesBinding.getRoot());
            this.servicesBinding = servicesBinding;
        }
    }

}
