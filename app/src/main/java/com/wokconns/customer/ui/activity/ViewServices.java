package com.wokconns.customer.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.JsonArray;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivityViewServicesBinding;
import com.wokconns.customer.dto.ArtistDetailsDTO;
import com.wokconns.customer.dto.ProductDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.preferences.SharedPrefs;
import com.wokconns.customer.ui.adapter.AdapterServices;
import com.wokconns.customer.utils.ProjectUtils;

import java.util.ArrayList;

public class ViewServices extends AppCompatActivity implements View.OnClickListener {
    public ActivityViewServicesBinding binding;
    private Context mContext;
    private ArtistDetailsDTO artistDetailsDTO;
    private AdapterServices adapterServices;
    private ArrayList<ProductDTO> productDTOList;
    private ArrayList<ProductDTO> serviceList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private JsonArray array;
    private DialogInterface dialog_book;
    private SharedPrefs prefrence;
    private UserDTO userDTO;
    private String artist_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_services);
        mContext = ViewServices.this;
        prefrence = SharedPrefs.getInstance(mContext);

        userDTO = prefrence.getParentUser(Const.USER_DTO);
        if (getIntent().hasExtra(Const.ARTIST_DTO)) {
            artistDetailsDTO = (ArtistDetailsDTO) getIntent().getSerializableExtra(Const.ARTIST_DTO);
            artist_id = getIntent().getStringExtra(Const.ARTIST_ID);
        }
        showUiAction();
    }


    public void showUiAction() {
        binding.llBack.setOnClickListener(this);
        binding.cardBook.setOnClickListener(this);
        showData();

    }

    public void showData() {
        gridLayoutManager = new GridLayoutManager(mContext, 2);
        productDTOList = new ArrayList<>();
        productDTOList = artistDetailsDTO.getProducts();
        if (productDTOList.size() > 0) {
            binding.tvNotFound.setVisibility(View.GONE);
            binding.rvServices.setVisibility(View.VISIBLE);
            binding.cardBook.setVisibility(View.VISIBLE);
            adapterServices = new AdapterServices(ViewServices.this, binding, productDTOList, artistDetailsDTO);
            binding.rvServices.setLayoutManager(gridLayoutManager);
            binding.rvServices.setAdapter(adapterServices);
        } else {
            binding.tvNotFound.setVisibility(View.VISIBLE);
            binding.rvServices.setVisibility(View.GONE);
            binding.cardBook.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBack:
                onBackPressed();
                break;
            case R.id.cardBook:
                updateList();
                if ((array.size() > 0)) {
                    Intent in = new Intent(mContext, Booking.class);
                    in.putExtra(Const.ARTIST_DTO, artistDetailsDTO);
                    in.putExtra(Const.ARTIST_ID, artist_id);
                    in.putExtra(Const.SERVICE_ARRAY, array.toString());
                    in.putExtra(Const.SERVICE_NAME_ARRAY, serviceList);
                    in.putExtra(Const.SCREEN_TAG, 2);
                    in.putExtra(Const.PRICE, binding.tvPrice.getText().toString().trim());
                    startActivity(in);
                    //finish();
                } else {
                    ProjectUtils.showLong(mContext, mContext.getResources().getString(R.string.select_any_service));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    public void updateList() {
        array = new JsonArray();
        for (int i = 0; i < productDTOList.size(); i++) {
            if (productDTOList.get(i).isSelected()) {
                array.add(productDTOList.get(i).getId());
                serviceList.add(productDTOList.get(i));
            }
        }
    }
}
