package com.wokconns.customer.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.wokconns.customer.R;
import com.wokconns.customer.databinding.FragmentHistoryBinding;
import com.wokconns.customer.ui.activity.BaseActivity;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    FragmentHistoryBinding binding;
    private View view;
    private BaseActivity baseActivity;
    private PaidFrag paidFrag = new PaidFrag();
    private UnPaidFrag unPaidFrag = new UnPaidFrag();
    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        view = binding.getRoot();
        baseActivity.headerNameTV.setText(getResources().getString(R.string.invoice));
        fragmentManager = getChildFragmentManager();

        binding.tvPaid.setOnClickListener(this);
        binding.tvUnPaid.setOnClickListener(this);

        fragmentManager.beginTransaction().add(R.id.frame, unPaidFrag).commit();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvPaid:
                setSelected(true, false);
                fragmentManager.beginTransaction().replace(R.id.frame, paidFrag).commit();
                break;
            case R.id.tvUnPaid:
                setSelected(false, true);
                fragmentManager.beginTransaction().replace(R.id.frame, unPaidFrag).commit();
                break;
        }

    }

    public void setSelected(boolean firstBTN, boolean secondBTN) {
        if (firstBTN) {
            binding.tvPaid.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            binding.tvPaid.setTextColor(getResources().getColor(R.color.white));
            binding.tvUnPaid.setBackgroundColor(getResources().getColor(R.color.white));
            binding.tvUnPaid.setTextColor(getResources().getColor(R.color.gray));
        }
        if (secondBTN) {
            binding.tvPaid.setBackgroundColor(getResources().getColor(R.color.white));
            binding.tvPaid.setTextColor(getResources().getColor(R.color.gray));
            binding.tvUnPaid.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            binding.tvUnPaid.setTextColor(getResources().getColor(R.color.white));


        }
        binding.tvPaid.setSelected(firstBTN);
        binding.tvUnPaid.setSelected(secondBTN);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }
}
