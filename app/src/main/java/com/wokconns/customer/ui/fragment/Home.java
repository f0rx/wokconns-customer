package com.wokconns.customer.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.wokconns.customer.R;
import com.wokconns.customer.application.GlobalState;
import com.wokconns.customer.databinding.FragmentHomeBinding;
import com.wokconns.customer.dto.HistoryDTO;
import com.wokconns.customer.dto.HomeBannerDTO;
import com.wokconns.customer.dto.HomeCategoryDTO;
import com.wokconns.customer.dto.HomeDataDTO;
import com.wokconns.customer.dto.HomeNearByArtistsDTO;
import com.wokconns.customer.dto.HomeRecomendedDTO;
import com.wokconns.customer.dto.UserBooking;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.interfacess.Helper;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.activity.BaseActivity;
import com.wokconns.customer.ui.adapter.AdapterCategory;
import com.wokconns.customer.ui.adapter.AdapterCustomerBooking;
import com.wokconns.customer.ui.adapter.AdapterInvoice;
import com.wokconns.customer.ui.adapter.AdapterNearByArtist;
import com.wokconns.customer.ui.adapter.AdapterRecommended;
import com.wokconns.customer.ui.adapter.HomeBannerPagerAdapter;
import com.wokconns.customer.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Home extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    HashMap<String, String> params = new HashMap<>();
    FragmentHomeBinding binding;
    HomeDataDTO homeDataDTO;
    GlobalState globalState;
    ArrayList<HomeBannerDTO> bannerDTOArrayList = new ArrayList<>();
    HomeBannerPagerAdapter homeBannerPagerAdapter;
    AdapterNearByArtist nearByAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<HomeNearByArtistsDTO> nearByArtistsDTOArrayList = new ArrayList<>();
    AdapterCustomerBooking activeBookingAdapter;
    LinearLayoutManager linearLayoutManager1;
    ArrayList<UserBooking> activeBookingDTOArrayList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager4;
    AdapterRecommended recommendedAdapter;
    ArrayList<HomeRecomendedDTO> recomendedDTOArrayList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager3;
    AdapterInvoice invoiceAdapter;
    ArrayList<HistoryDTO> invoiceDTOArrayList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager2;
    AdapterCategory categoryAdapter;
    ArrayList<HomeCategoryDTO> categoryDTOArrayList = new ArrayList<>();
    private View view;
    private String TAG = Home.class.getSimpleName();
    private SharedPrefrence preference;
    private UserDTO userDTO;
    private BaseActivity baseActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        view = binding.getRoot();
        baseActivity.headerNameTV.setText(getResources().getString(R.string.app_name));
        preference = SharedPrefrence.getInstance(getActivity());
        userDTO = preference.getParentUser(Consts.USER_DTO);

        setUiAction();
        return view;
    }

    public void setUiAction() {
        globalState = GlobalState.getInstance();
        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvNearBy.setLayoutManager(linearLayoutManager);
        linearLayoutManager4 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvRecommended.setLayoutManager(linearLayoutManager4);
        linearLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvMyBookings.setLayoutManager(linearLayoutManager1);
        linearLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvCategories.setLayoutManager(linearLayoutManager2);
        linearLayoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        binding.rvRecentInvoice.setLayoutManager(linearLayoutManager3);

        if (globalState.getHomeData() != null) {
            homeDataDTO = globalState.getHomeData();
            setData();
        }

        binding.swipeRefreshLayout.setOnRefreshListener(this);
        binding.ivMenu.setOnClickListener(this);
        binding.tvSeeAll.setOnClickListener(this);
        binding.tvSeeAll1.setOnClickListener(this);
        binding.tvSeeAll2.setOnClickListener(this);
        binding.tvSeeAll3.setOnClickListener(this);
        binding.tvSeeAll6.setOnClickListener(this);

        params.put(Consts.USER_ID, userDTO.getUser_id());
        params.put(Consts.LATITUDE, "" + preference.getValue(Consts.LATITUDE));
        params.put(Consts.LONGITUDE, "" + preference.getValue(Consts.LONGITUDE));
        params.put(Consts.DISTANCE, "50");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llAddMoney:
                break;
            case R.id.iv_menu:
                if (baseActivity.drawer.isDrawerVisible(GravityCompat.START)) {
                    baseActivity.drawer.closeDrawer(GravityCompat.START);
                } else {
                    baseActivity.drawer.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.tv_see_all:
                try {
                    baseActivity.ivFilter.setVisibility(View.VISIBLE);
                    baseActivity.header.setVisibility(View.VISIBLE);
                    BaseActivity.navItemIndex = 1;
                    BaseActivity.CURRENT_TAG = BaseActivity.TAG_MAIN;
                    baseActivity.loadHomeFragment(new DiscoverNearBy(), BaseActivity.CURRENT_TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_see_all1:
                try {
                    baseActivity.ivFilter.setVisibility(View.VISIBLE);
                    baseActivity.header.setVisibility(View.VISIBLE);
                    BaseActivity.navItemIndex = 3;
                    BaseActivity.CURRENT_TAG = BaseActivity.TAG_BOOKING;
                    baseActivity.loadHomeFragment(new MyBooking(), BaseActivity.CURRENT_TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_see_all2:
                try {
                    baseActivity.ivFilter.setVisibility(View.GONE);
                    baseActivity.header.setVisibility(View.VISIBLE);
                    BaseActivity.navItemIndex = 9;
                    BaseActivity.CURRENT_TAG = BaseActivity.TAG_HISTORY;
                    baseActivity.loadHomeFragment(new HistoryFragment(), BaseActivity.CURRENT_TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_see_all3:
            case R.id.tv_see_all6:
                try {
                    baseActivity.ivFilter.setVisibility(View.VISIBLE);
                    baseActivity.header.setVisibility(View.VISIBLE);
                    BaseActivity.navItemIndex = 1;
                    BaseActivity.CURRENT_TAG = BaseActivity.TAG_MAIN;
                    baseActivity.loadHomeFragment(new DiscoverNearBy(), BaseActivity.CURRENT_TAG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    public void getHomeData() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.CUSTOMER_HOME_DATA, params, getActivity()).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            binding.swipeRefreshLayout.setRefreshing(false);
            if (flag) {
                binding.tvNo.setVisibility(View.GONE);
                try {
                    homeDataDTO = new Gson().fromJson(response.getJSONObject("data").toString(), HomeDataDTO.class);
                    globalState.setHomeData(homeDataDTO);
                    setData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                binding.tvNo.setVisibility(View.VISIBLE);
                binding.rvNearBy.setVisibility(View.GONE);
                binding.rvRecommended.setVisibility(View.GONE);
                binding.rvMyBookings.setVisibility(View.GONE);
                binding.rvCategories.setVisibility(View.GONE);
                binding.rvRecentInvoice.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.swipeRefreshLayout.post(() -> {

            Log.e("Runnable", "FIRST");
            if (NetworkManager.isConnectToInternet(Home.this.getActivity())) {
                binding.swipeRefreshLayout.setRefreshing(true);
                Home.this.getHomeData();

            } else {
                ProjectUtils.showToast(Home.this.getActivity(), Home.this.getResources().getString(R.string.internet_connection));
            }
        }
        );

    }


    public void setData() {
        bannerDTOArrayList = homeDataDTO.getBanner();
        nearByArtistsDTOArrayList = homeDataDTO.getNear_by_artist();
        recomendedDTOArrayList = homeDataDTO.getRecommended();
        invoiceDTOArrayList = homeDataDTO.getInvoice();
        categoryDTOArrayList = homeDataDTO.getCategory();
        activeBookingDTOArrayList = homeDataDTO.getActive_booking();

        if (bannerDTOArrayList.size() > 0) {
            homeBannerPagerAdapter = new HomeBannerPagerAdapter(Home.this, baseActivity, bannerDTOArrayList);
            binding.mViewPager.setAdapter(homeBannerPagerAdapter);
            binding.mViewPager.setCurrentItem(0);
            binding.tabDots.setViewPager(binding.mViewPager);
            binding.mViewPager.setNestedScrollingEnabled(false);
        }

        if (nearByArtistsDTOArrayList.size() > 0) {
            binding.rlNearBy.setVisibility(View.VISIBLE);
            nearByAdapter = new AdapterNearByArtist(baseActivity, nearByArtistsDTOArrayList);
            binding.rvNearBy.setAdapter(nearByAdapter);
            binding.rvNearBy.setNestedScrollingEnabled(false);
        } else {
            binding.rlNearBy.setVisibility(View.GONE);
        }

        if (recomendedDTOArrayList.size() > 0) {
            binding.rlRecommended.setVisibility(View.VISIBLE);
            recommendedAdapter = new AdapterRecommended(baseActivity, recomendedDTOArrayList);
            binding.rvRecommended.setAdapter(recommendedAdapter);
            binding.rvRecommended.setNestedScrollingEnabled(false);
        } else {
            binding.rlRecommended.setVisibility(View.GONE);
        }

        if (invoiceDTOArrayList.size() > 0) {
            binding.rlRecentInvoice.setVisibility(View.VISIBLE);
            invoiceAdapter = new AdapterInvoice(baseActivity, invoiceDTOArrayList, LayoutInflater.from(getActivity()));
            binding.rvRecentInvoice.setAdapter(invoiceAdapter);
            binding.rvRecentInvoice.setNestedScrollingEnabled(false);
        } else {
            binding.rvRecentInvoice.setVisibility(View.GONE);
        }

        if (categoryDTOArrayList.size() > 0) {
            binding.rlCategories.setVisibility(View.VISIBLE);
            categoryAdapter = new AdapterCategory(baseActivity, categoryDTOArrayList);
            binding.rvCategories.setAdapter(categoryAdapter);
            binding.rvCategories.setNestedScrollingEnabled(false);
        } else {
            binding.rvCategories.setVisibility(View.GONE);
        }

        if (activeBookingDTOArrayList.size() > 0) {
            binding.rlMyBookings.setVisibility(View.VISIBLE);
            activeBookingAdapter = new AdapterCustomerBooking(null, baseActivity, activeBookingDTOArrayList, userDTO, "home");
            binding.rvMyBookings.setAdapter(activeBookingAdapter);
            binding.rvMyBookings.setNestedScrollingEnabled(false);
        } else {
            binding.rvMyBookings.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getHomeData();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        baseActivity = (BaseActivity) context;
    }
}
