package com.wokconns.customer.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wokconns.customer.R;
import com.wokconns.customer.dto.HistoryDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfaces.Consts;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.adapter.UnPaidAdapter;
import com.wokconns.customer.utils.CustomTextViewBold;
import com.wokconns.customer.utils.ProjectUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnPaidFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = UnPaidFrag.class.getSimpleName();
    private View view;
    private RecyclerView RVhistorylist;
    private UnPaidAdapter unPaidAdapter;
    private ArrayList<HistoryDTO> historyDTOList;
    private ArrayList<HistoryDTO> historyDTOListUnPaid;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private CustomTextViewBold tvNo;
    private LayoutInflater myInflater;
    private SearchView svSearch;
    private RelativeLayout rlSearch;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_unpaid, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        myInflater = LayoutInflater.from(getActivity());
        setUiAction(view);
        return view;
    }

    public void setUiAction(View v) {
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        rlSearch = v.findViewById(R.id.rlSearch);
        svSearch = v.findViewById(R.id.svSearch);
        tvNo = v.findViewById(R.id.tvNo);
        RVhistorylist = v.findViewById(R.id.RVhistorylist);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        RVhistorylist.setLayoutManager(mLayoutManager);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 0) {
                    unPaidAdapter.filter(newText);

                } else {


                }
                return false;
            }
        });


    }


    public void getHistroy() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_INVOICE_API, getparm(), getActivity()).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            swipeRefreshLayout.setRefreshing(false);
            if (flag) {
                tvNo.setVisibility(View.GONE);
                RVhistorylist.setVisibility(View.VISIBLE);
                rlSearch.setVisibility(View.VISIBLE);
                try {
                    historyDTOList = new ArrayList<>();
                    Type getpetDTO = new TypeToken<List<HistoryDTO>>() {
                    }.getType();
                    historyDTOList = new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);
                    showData();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                tvNo.setVisibility(View.VISIBLE);
                RVhistorylist.setVisibility(View.GONE);
                rlSearch.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(() -> {

            Log.e("Runnable", "FIRST");
            if (NetworkManager.isConnectToInternet(UnPaidFrag.this.getActivity())) {
                swipeRefreshLayout.setRefreshing(true);

                UnPaidFrag.this.getHistroy();

            } else {
                ProjectUtils.showToast(UnPaidFrag.this.getActivity(), UnPaidFrag.this.getResources().getString(R.string.internet_connection));
            }
        }
        );
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        parms.put(Consts.ROLE, "2");
        return parms;
    }

    public void showData() {
        historyDTOListUnPaid = new ArrayList<>();
        for (int i = 0; i < historyDTOList.size(); i++) {
            if (historyDTOList.get(i).getFlag().trim().equals("0")) {
                historyDTOListUnPaid.add(historyDTOList.get(i));
            } else {
            }
        }

        if (historyDTOListUnPaid.size() > 0) {
            tvNo.setVisibility(View.GONE);
            RVhistorylist.setVisibility(View.VISIBLE);
            rlSearch.setVisibility(View.VISIBLE);

            unPaidAdapter = new UnPaidAdapter(UnPaidFrag.this, historyDTOListUnPaid, userDTO, myInflater);
            RVhistorylist.setAdapter(unPaidAdapter);

        } else {
            tvNo.setVisibility(View.VISIBLE);
            RVhistorylist.setVisibility(View.GONE);
            rlSearch.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getHistroy();
    }


}
