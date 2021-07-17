package com.wokconns.customer.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.DailogFilterJobBinding;
import com.wokconns.customer.dto.AllAtristListDTO;
import com.wokconns.customer.dto.CategoryDTO;
import com.wokconns.customer.dto.CurrencyDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.interfacess.Helper;
import com.wokconns.customer.interfacess.OnSpinerItemClick;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.activity.BaseActivity;
import com.wokconns.customer.ui.adapter.DiscoverAdapter;
import com.wokconns.customer.utils.CustomTextViewBold;
import com.wokconns.customer.utils.ProjectUtils;
import com.wokconns.customer.utils.SpinnerDialog;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class DiscoverFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    public String categoryValue = "";
    HashMap<String, String> parms = new HashMap<>();
    AlertDialog alertDialog1;
    CharSequence[] values;
    DailogFilterJobBinding dailogFilterJobBinding;
    private String TAG = DiscoverFragment.class.getSimpleName();
    private View view;
    private RecyclerView rvDiscover;
    private DiscoverAdapter discoverAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<AllAtristListDTO> allAtristListDTOList = new ArrayList<>();
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private LayoutInflater myInflater;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomTextViewBold tvFilter, tvNotFound;
    private ArrayList<CategoryDTO> categoryDTOS = new ArrayList<>();
    private HashMap<String, String> parmsCategory = new HashMap<>();
    private SpinnerDialog spinnerDialogCate;
    private ArrayList<AllAtristListDTO> tempList;
    private BaseActivity baseActivity;
    private HashMap<String, String> params = new HashMap<>();
    private Dialog dialogFilterJob;
    private ArrayList<CurrencyDTO> currencyDTOArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_discover, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        parmsCategory.put(Consts.USER_ID, userDTO.getUser_id());
        myInflater = LayoutInflater.from(getActivity());
        getCategory();
        setUiAction();
        return view;
    }

    public void setUiAction() {
        values = new CharSequence[]{getString(R.string.low_to_high), getString(R.string.dis_jobs_done), getString(R.string.featured), getString(R.string.favourite)};

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        tvNotFound = view.findViewById(R.id.tvNotFound);
        tvFilter = view.findViewById(R.id.tvFilter);
        baseActivity.ivFilter.setOnClickListener(this);
        tvFilter.setOnClickListener(this);

        rvDiscover = view.findViewById(R.id.rvDiscover);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rvDiscover.setLayoutManager(mLayoutManager);

        discoverAdapter = new DiscoverAdapter(getActivity(), allAtristListDTOList, myInflater);
        rvDiscover.setAdapter(discoverAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        categoryValue = baseActivity.prefrence.getValue(Consts.VALUE);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(() -> {

            Log.e("Runnable", "FIRST");
            if (NetworkManager.isConnectToInternet(getActivity())) {
                swipeRefreshLayout.setRefreshing(true);
                parms.put(Consts.CATEGORY_ID, "" + categoryValue);
                getArtist();

            } else {
                ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_connection));
            }
        }
        );

        getCurrencyValue();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvFilter:
                if (categoryDTOS.size() > 0) {
                    spinnerDialogCate.showSpinerDialog();
                } else {
                    ProjectUtils.showLong(getActivity(), getResources().getString(R.string.no_cate_found));
                }
                break;
            case R.id.ivFilter:
                CreateAlertDialogWithRadioButtonGroup();
                break;

        }
    }


    public void getArtist() {
        parms.put(Consts.LATITUDE, prefrence.getValue(Consts.LATITUDE));
        parms.put(Consts.LONGITUDE, prefrence.getValue(Consts.LONGITUDE));
        new HttpsRequest(Consts.GET_ALL_ARTISTS_API, parms, getActivity()).stringPost(TAG, (flag, msg, response) -> {
            swipeRefreshLayout.setRefreshing(false);
            if (flag) {
                try {
                    allAtristListDTOList = new ArrayList<>();
                    Type getpetDTO = new TypeToken<List<AllAtristListDTO>>() {
                    }.getType();
                    allAtristListDTOList = (ArrayList<AllAtristListDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                    showData();

                } catch (Exception e) {
                    e.printStackTrace();

                }


            } else {
                tvNotFound.setVisibility(View.VISIBLE);
                rvDiscover.setVisibility(View.GONE);
                baseActivity.ivFilter.setVisibility(View.GONE);
            }

            prefrence.setValue(Consts.VALUE, "");
        });
    }


    public void showData() {
        tvNotFound.setVisibility(View.GONE);
        rvDiscover.setVisibility(View.VISIBLE);
        baseActivity.ivFilter.setVisibility(View.VISIBLE);
        discoverAdapter = new DiscoverAdapter(getActivity(), allAtristListDTOList, myInflater);
        rvDiscover.setAdapter(discoverAdapter);
    }

    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        parms.put(Consts.CATEGORY_ID, "" + categoryValue);
        tvFilter.setText(getResources().getString(R.string.all_category));
        getArtist();
    }

    public void getCategory() {
        new HttpsRequest(Consts.GET_ALL_CATEGORY_API, parmsCategory, getActivity()).stringPost(TAG, (flag, msg, response) -> {
            if (flag) {
                try {
                    categoryDTOS = new ArrayList<>();
                    Type getpetDTO = new TypeToken<List<CategoryDTO>>() {
                    }.getType();
                    categoryDTOS = (ArrayList<CategoryDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);

                    spinnerDialogCate = new SpinnerDialog((Activity) getActivity(), categoryDTOS, getResources().getString(R.string.select_category));// With 	Animation
                    spinnerDialogCate.bindOnSpinerListener((item, id, position) -> {
                        try {
                            tvFilter.setText(item);
                            parms.put(Consts.CATEGORY_ID, id);
//                                    getArtist();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        dailogFilterJobBinding.etCategoryD.setText(item);
                        params.put(Consts.CATEGORY_ID, id);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                ProjectUtils.showToast(getActivity(), msg);
            }
        });
    }

    public void shortlistlowtohigh() {
        Collections.sort(allAtristListDTOList, (obj1, obj2) -> {
            int az = Integer.parseInt(obj1.getPrice());
            int za = Integer.parseInt(obj2.getPrice());
            return (az < za) ? -1 : (az > za) ? 1 : 0;

        });
    }

    public void shortJobs() {
        Collections.sort(allAtristListDTOList, (obj1, obj2) -> {
            int az = Integer.parseInt(obj1.getJobDone());
            int za = Integer.parseInt(obj2.getJobDone());
            return (az > za) ? -1 : (az > za) ? 1 : 0;

        });
    }

    public void shortFeatured() {
        Collections.sort(allAtristListDTOList, (obj1, obj2) -> {
            int az = Integer.parseInt(obj1.getFeatured());
            int za = Integer.parseInt(obj2.getFeatured());
            return (az > za) ? -1 : (az > za) ? 1 : 0;

        });
    }

    public void shortFavourite() {
        Collections.sort(allAtristListDTOList, (obj1, obj2) -> {
            int az = Integer.parseInt(obj1.getFav_status());
            int za = Integer.parseInt(obj2.getFav_status());

            return (az > za) ? -1 : (az > za) ? 1 : 0;

        });
    }


    public void CreateAlertDialogWithRadioButtonGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Sort by");

        builder.setSingleChoiceItems(values, -1, (dialog, item) -> {

            switch (item) {
                case 0:
                    shortlistlowtohigh();
                    discoverAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    shortJobs();
                    discoverAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    shortFeatured();
                    discoverAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    shortFavourite();
                    discoverAdapter.notifyDataSetChanged();
                    break;
            }
            alertDialog1.dismiss();
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }

    public void dialogAbout() {
        dialogFilterJob = new Dialog(baseActivity/*, android.R.style.Theme_Dialog*/);
        dialogFilterJob.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogFilterJob.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dailogFilterJobBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dailog_filter_job, null, false);
        dialogFilterJob.setContentView(dailogFilterJobBinding.getRoot());

        dailogFilterJobBinding.etCategoryD.setOnClickListener(v -> {
            if (NetworkManager.isConnectToInternet(baseActivity)) {
                if (categoryDTOS.size() > 0)
                    spinnerDialogCate.showSpinerDialog();
            } else {
                ProjectUtils.showToast(baseActivity, getResources().getString(R.string.internet_connection));
            }
        });

        try {
            if (currencyDTOArrayList.size() > 0) {
                ArrayAdapter<CurrencyDTO> currencyAdapter = new ArrayAdapter<CurrencyDTO>(baseActivity, android.R.layout.simple_list_item_1, currencyDTOArrayList);
                dailogFilterJobBinding.etCurrencyD.setAdapter(currencyAdapter);
                dailogFilterJobBinding.etCurrencyD.setCursorVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        dailogFilterJobBinding.etCurrencyD.setOnClickListener(v -> dailogFilterJobBinding.etCurrencyD.showDropDown());

        dailogFilterJobBinding.etCurrencyD.setOnItemClickListener((parent, view, position, id) -> {
            dailogFilterJobBinding.etCurrencyD.showDropDown();
            CurrencyDTO currencyDTO = (CurrencyDTO) parent.getItemAtPosition(position);
            Log.e(TAG, "onItemClick: " + currencyDTO.getCurrency_symbol());
            params.put(Consts.CURRENCY, currencyDTO.getCurrency_symbol());
        });

        dialogFilterJob.show();
        dialogFilterJob.setCancelable(false);

        dailogFilterJobBinding.tvCancel.setOnClickListener(v -> dialogFilterJob.dismiss());
        dailogFilterJobBinding.tvSubmit.setOnClickListener(
                v -> {
                    Log.e(TAG, "onClick: " + dailogFilterJobBinding.seekBar.getProgress());
                    filteredList();
                });
    }


    public void filteredList() {
        params.put(Consts.USER_ID, userDTO.getUser_id());
        params.put(Consts.PRICE, "" + dailogFilterJobBinding.seekBar.getProgress());
        params.put(Consts.DISTANCE, "50");
        params.put(Consts.LATITUDE, prefrence.getValue(Consts.LATITUDE));
        params.put(Consts.LONGITUDE, prefrence.getValue(Consts.LONGITUDE));

        new HttpsRequest(Consts.GET_ALL_ARTIST_FILTER, params, baseActivity).imagePost(TAG, (flag, msg, response) -> {
            dialogFilterJob.dismiss();
            if (flag) {

                try {
                    allAtristListDTOList = new ArrayList<>();
                    Type getpetDTO = new TypeToken<List<AllAtristListDTO>>() {
                    }.getType();
                    allAtristListDTOList = (ArrayList<AllAtristListDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                    showData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ProjectUtils.showToast(baseActivity, msg);
            }
        });
    }

    public void getCurrencyValue() {

        new HttpsRequest(Consts.GET_CURRENCY_API, baseActivity).stringGet(TAG, (flag, msg, response) -> {
            if (flag) {
                try {
                    currencyDTOArrayList = new ArrayList<>();
                    Type getCurrencyDTO = new TypeToken<List<CurrencyDTO>>() {
                    }.getType();
                    currencyDTOArrayList = (ArrayList<CurrencyDTO>) new Gson().fromJson(response.getJSONArray("data").toString(), getCurrencyDTO);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                ProjectUtils.showToast(baseActivity, msg);
            }
        });
    }

}
