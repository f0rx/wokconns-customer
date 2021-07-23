package com.wokconns.customer.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivityAddMoneyBinding;
import com.wokconns.customer.databinding.DailogPaymentOptionBinding;
import com.wokconns.customer.dto.CurrencyDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfaces.Consts;
import com.wokconns.customer.interfaces.IPostPayment;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.utils.DecimalDigitsInputFilter;
import com.wokconns.customer.utils.ProjectUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddMoney extends AppCompatActivity implements View.OnClickListener, IPostPayment {
    float rs = 0;
    float rs1 = 0;
    float final_rs = 0;
    String currencyCode = "";
    private final String TAG = AddMoney.class.getSimpleName();
    private Context mContext;
    private final HashMap<String, String> parmas = new HashMap<>();
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private String amt = "";
    private String currency = "";
    private Dialog dialog;
    private ActivityAddMoneyBinding binding;
    private ArrayList<CurrencyDTO> currencyDTOArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_money);
        mContext = AddMoney.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        parmas.put(Consts.USER_ID, userDTO.getUser_id());
        setUiAction();
    }

    @SuppressLint("SetTextI18n")
    public void setUiAction() {

        binding.ivBack.setOnClickListener(v -> finish());

        if (getIntent().hasExtra(Consts.AMOUNT)) {
            amt = getIntent().getStringExtra(Consts.AMOUNT);
            currency = getIntent().getStringExtra(Consts.CURRENCY);

            binding.tvWallet.setText(currency + " " + amt);
        }

        binding.cbAdd.setOnClickListener(this);

        binding.etAddMoney.setSelection(binding.etAddMoney.getText().length());

        binding.tv1000.setOnClickListener(this);

        binding.tv1500.setOnClickListener(this);

        binding.tv2000.setOnClickListener(this);

        binding.tv1000.setText("+ 100");
        binding.tv1500.setText("+ 150");
        binding.tv2000.setText("+ 200");


        binding.etAddMoney.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(12, 2)});
        binding.etAddMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1 && s.toString().startsWith("0")) {
                    s.clear();
                }
            }
        });


        binding.etCurrency.setOnClickListener(v -> binding.etCurrency.showDropDown());

        binding.etCurrency.setOnItemClickListener((parent, view, position, id) -> {
            binding.etCurrency.showDropDown();
            CurrencyDTO currencyDTO = (CurrencyDTO) parent.getItemAtPosition(position);
            Log.e(TAG, "onItemClick: " + currencyDTO.getCurrency_symbol());

            currencyCode = currencyDTO.getCode();
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (binding.etAddMoney.getText().toString().trim().equalsIgnoreCase("")) {
            rs1 = 0;

        } else {
            rs1 = Float.parseFloat(binding.etAddMoney.getText().toString().trim());
        }

        switch (v.getId()) {
            case R.id.tv1000:
                rs = 100;
                final_rs = rs1 + rs;
                binding.etAddMoney.setText(String.format("%s", final_rs));
                binding.etAddMoney.setSelection(binding.etAddMoney.getText().length());
                break;
            case R.id.tv1500:
                rs = 150;
                final_rs = rs1 + rs;
                binding.etAddMoney.setText(String.format("%s", final_rs));
                binding.etAddMoney.setSelection(binding.etAddMoney.getText().length());
                break;
            case R.id.tv2000:
                rs = 200;
                final_rs = rs1 + rs;
                binding.etAddMoney.setText(String.format("%s", final_rs));
                binding.etAddMoney.setSelection(binding.etAddMoney.getText().length());
                break;
            case R.id.cbAdd:
                if (binding.etAddMoney.getText().toString().length() > 0 && Float.parseFloat(binding.etAddMoney.getText().toString().trim()) > 0) {
                    if (NetworkManager.isConnectToInternet(mContext)) {
                        parmas.put(Consts.AMOUNT, ProjectUtils.getEditTextValue(binding.etAddMoney));
                        dialogPayment();
                    } else {
                        ProjectUtils.showLong(mContext, getResources().getString(R.string.internet_connection));
                    }
                } else {
                    ProjectUtils.showLong(mContext, getResources().getString(R.string.val_money));
                }
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (prefrence.getValue(Consts.SURL).equalsIgnoreCase(Consts.PAYMENT_SUCCESS)) {
            prefrence.clearPreferences(Consts.SURL);
            addMoney();
        } else if (prefrence.getValue(Consts.FURL).equalsIgnoreCase(Consts.PAYMENT_FAIL)) {
            prefrence.clearPreferences(Consts.FURL);
            finish();
        }

        try {
            getCurrencyValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addMoney() {
        new HttpsRequest(Consts.ADD_MONEY_API, parmas, mContext).stringPost(TAG, (flag, msg, response) -> {
            if (flag) {
                ProjectUtils.showLong(mContext, msg);
                AddMoney.this.finish();
            } else {
                ProjectUtils.showLong(mContext, msg);
            }
        });
    }


    public void dialogPayment() {
        dialog = new Dialog(mContext/*, android.R.style.Theme_Dialog*/);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        final DailogPaymentOptionBinding binding1 = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dailog_payment_option, null, false);
        dialog.setContentView(binding1.getRoot());
        ///dialog.getWindow().setBackgroundDrawableResource(R.color.black);

        dialog.show();
        dialog.setCancelable(false);
        binding1.llCancel.setOnClickListener(v -> dialog.dismiss());

        binding1.paystackButton.setOnClickListener(v -> {
            Intent in2 = new Intent(mContext, PaymentWeb.class);
            in2.putExtra(Consts.USER_DTO, userDTO);
            in2.putExtra(Consts.AMOUNT, ProjectUtils.getEditTextValue(binding.etAddMoney));
            in2.putExtra(Consts.CURRENCY, currencyCode);
            AddMoney.this.startActivity(in2);
            dialog.dismiss();
        });
        binding1.flutterwaveButton.setOnClickListener(v -> {
            Intent in2 = new Intent(mContext, PaymentWeb.class);
            in2.putExtra(Consts.USER_DTO, userDTO);
            in2.putExtra(Consts.AMOUNT, ProjectUtils.getEditTextValue(binding.etAddMoney));
            in2.putExtra(Consts.CURRENCY, currencyCode);
            AddMoney.this.startActivity(in2);
            dialog.dismiss();
        });

    }

    public void getCurrencyValue() {

        new HttpsRequest(Consts.GET_CURRENCY_API, mContext).stringGet(TAG, (flag, msg, response) -> {
            if (flag) {
                try {
                    currencyDTOArrayList = new ArrayList<>();
                    Type getCurrencyDTO = new TypeToken<List<CurrencyDTO>>() {}.getType();
                    currencyDTOArrayList = new Gson().fromJson(response.getJSONArray("data").toString(), getCurrencyDTO);

                    try {
                        ArrayAdapter<CurrencyDTO> currencyAdapter = new ArrayAdapter<CurrencyDTO>(mContext, android.R.layout.simple_list_item_1, currencyDTOArrayList);
                        binding.etCurrency.setAdapter(currencyAdapter);
                        binding.etCurrency.setCursorVisible(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    setInitialData(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                ProjectUtils.showToast(mContext, msg);
            }
        });
    }


    public void setInitialData(int index) {
        String currency = currencyDTOArrayList.get(index).toString();
        binding.etCurrency.setText(currency);

        currencyCode = currencyDTOArrayList.get(index).getCode();
    }

}
