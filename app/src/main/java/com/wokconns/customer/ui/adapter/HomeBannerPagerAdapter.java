package com.wokconns.customer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.request.RequestOptions;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ViewpagerHomeBannerBinding;
import com.wokconns.customer.dto.HomeBannerDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.preferences.SharedPrefs;
import com.wokconns.customer.ui.fragment.Home;
import com.wokconns.customer.utils.GlideApp;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class HomeBannerPagerAdapter extends PagerAdapter {

    private final Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<HomeBannerDTO> bannerDTOArrayList;
    private SharedPrefs preference;
    private UserDTO userDTO;
    Home homeFragment;
    ViewpagerHomeBannerBinding binding;

    public HomeBannerPagerAdapter(Home homeFragment, Context mContext, ArrayList<HomeBannerDTO> bannerDTOArrayList) {
        this.mContext = mContext;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        preference = SharedPrefs.getInstance(mContext);
        this.userDTO = preference.getParentUser(Const.USER_DTO);
        this.bannerDTOArrayList = bannerDTOArrayList;
        this.homeFragment = homeFragment;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, final int position) {

        binding = DataBindingUtil.inflate(mLayoutInflater, R.layout.viewpager_home_banner, container, false);
        View itemView = binding.getRoot();

        binding.tvTitle.setText(String.format("%s %s!",
                mContext.getResources().getString(R.string.welcome_text), userDTO.getName()));
        binding.tvDescription.setText(bannerDTOArrayList.get(position).getDescription());

        GlideApp.with(mContext)
                .load(R.drawable.home_banner)
//                .load(bannerDTOArrayList.get(position).getImage())
                .apply(new RequestOptions())
                .placeholder(R.drawable.dummyuser_image)
                .into(binding.ivImage);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NotNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return bannerDTOArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view == object;
    }

}