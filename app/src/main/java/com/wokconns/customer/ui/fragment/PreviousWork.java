package com.wokconns.customer.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;

import com.wokconns.customer.databinding.FragmentPreviousWorkBinding;
import com.wokconns.customer.dto.ArtistBookingDTO;
import com.wokconns.customer.dto.ArtistDetailsDTO;
import com.wokconns.customer.R;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.ui.adapter.PreviousworkPagerAdapter;

import java.util.ArrayList;

public class PreviousWork extends AppCompatActivity {
    private View view;
    private ArtistDetailsDTO artistDetailsDTO;
    private PreviousworkPagerAdapter previousworkPagerAdapter;
    private ArrayList<ArtistBookingDTO> artistBookingDTOList;
    private Bundle bundle;
    private LinearLayoutManager mLayoutManagerReview;
    FragmentPreviousWorkBinding binding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_previous_work);
        context = PreviousWork.this;
        bundle = this.getIntent().getExtras();
        if (bundle != null) {
            artistDetailsDTO = (ArtistDetailsDTO) bundle.getSerializable(Consts.ARTIST_DTO);
        }
        showUiAction();
    }

    public void showUiAction() {

        mLayoutManagerReview = new LinearLayoutManager(context.getApplicationContext());
        binding.rvPreviousWork.setLayoutManager(mLayoutManagerReview);
        showData();
    }

    public void showData() {
        artistBookingDTOList = new ArrayList<>();
        artistBookingDTOList = artistDetailsDTO.getArtist_booking();
        if (artistBookingDTOList.size() > 0) {
            binding.tvNotFound.setVisibility(View.GONE);
            binding.rvPreviousWork.setVisibility(View.VISIBLE);
            previousworkPagerAdapter = new PreviousworkPagerAdapter(context, artistBookingDTOList);
            binding.rvPreviousWork.setAdapter(previousworkPagerAdapter);
        } else {
            binding.tvNotFound.setVisibility(View.VISIBLE);
            binding.rvPreviousWork.setVisibility(View.GONE);
        }

    }


}
