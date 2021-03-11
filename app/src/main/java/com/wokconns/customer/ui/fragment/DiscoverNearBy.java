package com.wokconns.customer.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wokconns.customer.R;
import com.wokconns.customer.databinding.FragmentDiscoverNearByBinding;
import com.wokconns.customer.ui.activity.BaseActivity;


public class DiscoverNearBy extends Fragment implements View.OnClickListener {
    private View view;
    private BaseActivity baseActivity;
    private FragmentManager fragmentManager;
    private DiscoverFragment discoverFragment = new DiscoverFragment();
    private NearByFragment nearByFragment = new NearByFragment();
    FragmentDiscoverNearByBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_discover_near_by, container, false);
        baseActivity.headerNameTV.setText(getResources().getString(R.string.company));
        fragmentManager = getChildFragmentManager();

        binding.CTVdiscover.setOnClickListener(this);
        binding.CTVnearby.setOnClickListener(this);
        binding.fabFilter.setOnClickListener(this);

        fragmentManager.beginTransaction().add(R.id.frame, discoverFragment).commit();

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CTVdiscover:
                setSelected(true, false);
                fragmentManager.beginTransaction().replace(R.id.frame, discoverFragment).commit();
                break;
            case R.id.CTVnearby:
                setSelected(false, true);
                fragmentManager.beginTransaction().replace(R.id.frame, nearByFragment).commit();
                break;
            case R.id.fab_filter:
                discoverFragment.dialogAbout();
                break;
        }

    }

    public void setSelected(boolean firstBTN, boolean secondBTN) {
        if (firstBTN) {
            binding.CTVdiscover.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            binding.CTVdiscover.setTextColor(getResources().getColor(R.color.white));
            binding.CTVnearby.setBackgroundColor(getResources().getColor(R.color.white));
            binding.CTVnearby.setTextColor(getResources().getColor(R.color.gray));

            binding.CTVdiscover.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bullet_list, 0, 0, 0);
            binding.CTVnearby.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_placeholder, 0, 0, 0);
        }
        if (secondBTN) {
            binding.CTVdiscover.setBackgroundColor(getResources().getColor(R.color.white));
            binding.CTVdiscover.setTextColor(getResources().getColor(R.color.gray));
            binding.CTVnearby.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            binding.CTVnearby.setTextColor(getResources().getColor(R.color.white));

            binding.CTVdiscover.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bullet_list_primary, 0, 0, 0);
            binding.CTVnearby.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_placeholder_white, 0, 0, 0);
        }
        binding.CTVdiscover.setSelected(firstBTN);
        binding.CTVnearby.setSelected(secondBTN);
    }

}
