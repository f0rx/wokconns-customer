package com.wokconns.customer.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.FragmentWalletBinding;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.dto.WalletCurrencyDTO;
import com.wokconns.customer.dto.WalletHistory;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfaces.Consts;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.activity.AddMoney;
import com.wokconns.customer.ui.activity.BaseActivity;
import com.wokconns.customer.ui.adapter.AdapterWalletHistory;
import com.wokconns.customer.utils.ProjectUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Wallet extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    FragmentWalletBinding binding;
    WalletCurrencyDTO walletCurrencyDTO;
    private View view;
    private AdapterWalletHistory adapterWalletHistory;
    private ArrayList<WalletHistory> walletHistoryList;
    private ArrayList<WalletCurrencyDTO> walletCurrencyList;
    private final String TAG = Wallet.class.getSimpleName();
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private String status = "";
    private HashMap<String, String> parms;
    private final HashMap<String, String> parmsGetWallet = new HashMap<>();
    private String amt = "";
    private String currency = "";
    private BaseActivity baseActivity;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet, container, false);
        view = binding.getRoot();
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        baseActivity.headerNameTV.setText(getResources().getString(R.string.ic_wallet));
        parmsGetWallet.put(Consts.USER_ID, userDTO.getUser_id());

        parms = new HashMap<>();
        parms.put(Consts.USER_ID, userDTO.getUser_id());

        setUiAction(view);
        return view;
    }

    public void setUiAction(View v) {
        binding.tvAll.setOnClickListener(this);
        binding.tvDebit.setOnClickListener(this);
        binding.tvCredit.setOnClickListener(this);

        binding.llAddMoney.setOnClickListener(this);

        mLayoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());
        binding.RVhistorylist.setLayoutManager(mLayoutManager);

        binding.swipeRefreshLayout.setOnRefreshListener(this);

        binding.etCurrency.setOnClickListener(v1 -> binding.etCurrency.showDropDown());

        binding.etCurrency.setOnItemClickListener((parent, view, position, id) -> {
            binding.etCurrency.showDropDown();
            walletCurrencyDTO = (WalletCurrencyDTO) parent.getItemAtPosition(position);
            Log.e(TAG, "onItemClick: " + walletCurrencyDTO.getCurrency_code());

            setWalletData(position);
            filter(walletCurrencyDTO);
        });
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llAddMoney:
                if (NetworkManager.isConnectToInternet(getActivity())) {
                    Intent in = new Intent(getActivity(), AddMoney.class);
                    in.putExtra(Consts.AMOUNT, amt);
                    in.putExtra(Consts.CURRENCY, currency);
                    startActivity(in);
                } else {
                    ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_connection));
                }

                break;
            case R.id.tvAll:
                setSelected(true, false, false);
                try {
                    showData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tvCredit:
                setSelected(false, false, true);
                status = "0";
                try {
                    if (walletCurrencyList != null) {
                        walletCurrencyDTO = walletCurrencyList.get(0);
                        if (walletCurrencyDTO != null) {
                            updateAccordingStatus(walletCurrencyDTO, "0");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.tvDebit:
                setSelected(false, true, false);
                status = "1";
                try {
                    if (walletCurrencyList != null) {
                        walletCurrencyDTO = walletCurrencyList.get(0);
                        if (walletCurrencyDTO != null) {
                            updateAccordingStatus(walletCurrencyDTO, "1");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void getHistory() {
        ProjectUtils.showProgressDialog(requireActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_USER_WALLET_API, parms, requireActivity()).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            binding.swipeRefreshLayout.setRefreshing(false);

            if (flag) {
                Log.i(TAG, Objects.requireNonNull(response).toString());

                binding.tvNo.setVisibility(View.GONE);
                binding.RVhistorylist.setVisibility(View.VISIBLE);
                try {
                    walletCurrencyList = new ArrayList<>();
                    Type getpetDTO = new TypeToken<List<WalletCurrencyDTO>>() {}.getType();
                    walletCurrencyList = new Gson().fromJson(response.getJSONObject("data").getJSONArray("currency").toString(), getpetDTO);
                    if (walletCurrencyList.size() > 0) {

                        ArrayAdapter<WalletCurrencyDTO> currencyAdapter = new ArrayAdapter<>(baseActivity, android.R.layout.simple_list_item_1, walletCurrencyList);
                        binding.etCurrency.setAdapter(currencyAdapter);
                        binding.etCurrency.setCursorVisible(false);
                        binding.etCurrency.setText(binding.etCurrency.getAdapter().getItem(0).toString(), false);

                        setWalletData(0);
                    }
                    showData();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                binding.tvNo.setVisibility(View.VISIBLE);
                binding.RVhistorylist.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        getWallet();

        binding.swipeRefreshLayout.post(() -> {

            Log.e("Runnable", "FIRST");
            if (NetworkManager.isConnectToInternet(getActivity())) {
                binding.swipeRefreshLayout.setRefreshing(true);
                getHistory();

            } else {
                ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_connection));
            }
        }
        );

    }

    public void getWallet() {
        new HttpsRequest(Consts.GET_WALLET_API, parmsGetWallet, getActivity()).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            if (flag) {
                try {
                    amt = response.getJSONObject("data").getString("amount");
                    currency = response.getJSONObject("data").getString("currency_type");
                    binding.tvWallet.setText(String.format("%s %s", currency, amt));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }
        });
    }

    public void showData() {
        if (walletHistoryList.size() > 0) {
            binding.tvNo.setVisibility(View.GONE);
            binding.RVhistorylist.setVisibility(View.VISIBLE);

            adapterWalletHistory = new AdapterWalletHistory(Wallet.this, walletHistoryList);
            binding.RVhistorylist.setAdapter(adapterWalletHistory);
        } else {
            binding.tvNo.setVisibility(View.VISIBLE);
            binding.RVhistorylist.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        Log.e("ONREFREST_Firls", "FIRS");
        getHistory();
    }

    public void setSelected(boolean firstBTN, boolean secondBTN, boolean thirdBTN) {
        if (firstBTN) {
            binding.tvAllSelect.setVisibility(View.VISIBLE);
            binding.tvDebitSelect.setVisibility(View.GONE);
            binding.tvCreditSelect.setVisibility(View.GONE);

        }
        if (secondBTN) {
            binding.tvDebitSelect.setVisibility(View.VISIBLE);
            binding.tvAllSelect.setVisibility(View.GONE);
            binding.tvCreditSelect.setVisibility(View.GONE);

        }
        if (thirdBTN) {
            binding.tvCreditSelect.setVisibility(View.VISIBLE);
            binding.tvAllSelect.setVisibility(View.GONE);
            binding.tvDebitSelect.setVisibility(View.GONE);

        }
        binding.tvAllSelect.setSelected(firstBTN);
        binding.tvDebitSelect.setSelected(secondBTN);
        binding.tvCreditSelect.setSelected(secondBTN);

    }

    @Override
    public void onAttach(@NotNull Context activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }

    public void setWalletData(int index) {
        amt = walletCurrencyList.get(index).getAmount();
        currency = walletCurrencyList.get(index).getCurrency_type();
        binding.tvWallet.setText(String.format("%s %s", currency, amt));
        walletHistoryList = walletCurrencyList.get(index).getWallet_history();
    }

    private void filter(@NotNull WalletCurrencyDTO dto) {
        ArrayList<WalletHistory> filterdNames = new ArrayList<>(dto.getWallet_history());
        adapterWalletHistory.updateList(filterdNames);
    }

    private void updateAccordingStatus(@NotNull WalletCurrencyDTO dto, String status) {
        ArrayList<WalletHistory> walletHistoryArrayList = new ArrayList<>();
        for (WalletHistory dto1 : dto.getWallet_history()) {
            if (dto1.getStatus().equalsIgnoreCase(status)) {
                walletHistoryArrayList.add(dto1);
            }
        }
        adapterWalletHistory.updateList(walletHistoryArrayList);
    }
}
