package com.wokconns.customer.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wokconns.customer.R;
import com.wokconns.customer.databinding.FragmentReviewsBinding;
import com.wokconns.customer.dto.ArtistDetailsDTO;
import com.wokconns.customer.dto.ReviewsDTO;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.ui.adapter.ReviewAdapter;

import java.util.ArrayList;

public class Reviews extends AppCompatActivity {
    FragmentReviewsBinding binding;
    Context context;
    private View view;
    private ArtistDetailsDTO artistDetailsDTO;
    private ReviewAdapter reviewAdapter;
    private LinearLayoutManager mLayoutManagerReview;
    private ArrayList<ReviewsDTO> reviewsDTOList;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_reviews);
        context = Reviews.this;

        bundle = getIntent().getExtras();
        artistDetailsDTO = (ArtistDetailsDTO) bundle.getSerializable(Consts.ARTIST_DTO);
        showUiAction();
    }

    public void showUiAction() {
        mLayoutManagerReview = new LinearLayoutManager(context);
        binding.rvReviews.setLayoutManager(mLayoutManagerReview);
        showData();
    }


    public void showData() {
        reviewsDTOList = new ArrayList<>();
        reviewsDTOList = artistDetailsDTO.getReviews();
        if (reviewsDTOList.size() > 0) {
            binding.llList.setVisibility(View.VISIBLE);
            binding.tvNotFound.setVisibility(View.GONE);
            reviewAdapter = new ReviewAdapter(context, reviewsDTOList);
            binding.rvReviews.setAdapter(reviewAdapter);
            binding.tvReviewsText.setText(getString(R.string.reviews) + reviewsDTOList.size() + ")");
        } else {
            binding.llList.setVisibility(View.GONE);
            binding.tvNotFound.setVisibility(View.VISIBLE);
        }

    }
}
