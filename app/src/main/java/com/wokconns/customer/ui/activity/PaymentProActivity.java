package com.wokconns.customer.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wokconns.customer.R;
import com.wokconns.customer.dto.HistoryDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.interfaces.IPostPayment;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.utils.CustomEditText;
import com.wokconns.customer.utils.CustomTextView;
import com.wokconns.customer.utils.CustomTextViewBold;
import com.wokconns.customer.utils.GlideApp;
import com.wokconns.customer.utils.ProjectUtils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PaymentProActivity extends AppCompatActivity implements View.OnClickListener, IPostPayment {
    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;
    private final String TAG = PaymentProActivity.class.getSimpleName();
    private final HashMap<String, String> params = new HashMap<>();
    private final HashMap<String, String> parmsGetWallet = new HashMap<>();
    private Context mContext;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private HistoryDTO historyDTO;
    private CircleImageView ivArtist;
    private CustomTextView tvCategory, tvLocation;
    private CustomTextViewBold tvName, tvApplyCode, tvAmount, tvCancelCode;
    private LinearLayout llPayment;
//    private LinearLayout llWallet;
    private CustomEditText etCode;
    private String merchantKey, salt, userCredentials, invoice_id, user_id, coupon_code = "", final_amount, email;
    private ImageView IVback;
    private Dialog dialog;
    private LinearLayout paystackPay, flutterwavepay, llCancel;
    private String amt1 = "";
    private String discount_amount = "0";
    private String currency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mContext = PaymentProActivity.this;

        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Const.USER_DTO);

        parmsGetWallet.put(Const.USER_ID, userDTO.getUser_id());
        params.put(Const.USER_ID, userDTO.getUser_id());

        if (getIntent().hasExtra(Const.HISTORY_DTO)) {
            historyDTO = (HistoryDTO) getIntent().getSerializableExtra(Const.HISTORY_DTO);
        }
        setUiAction();
    }

    public void setUiAction() {
        IVback = findViewById(R.id.IVback);
        IVback.setOnClickListener(this);
        ivArtist = findViewById(R.id.ivArtist);
        tvCategory = findViewById(R.id.tvCategory);
        tvLocation = findViewById(R.id.tvLocation);
        tvName = findViewById(R.id.tvName);
        tvApplyCode = findViewById(R.id.tvApplyCode);
        tvCancelCode = findViewById(R.id.tvCancelCode);
        tvAmount = findViewById(R.id.tvAmount);
        llPayment = findViewById(R.id.llPayment);
//        llWallet = findViewById(R.id.llWallet);
        etCode = findViewById(R.id.etCode);

        llPayment.setOnClickListener(this);
        tvApplyCode.setOnClickListener(this);
        tvCancelCode.setOnClickListener(this);
//        llWallet.setOnClickListener(this);

        GlideApp.with(mContext).
                load(ProjectUtils.formatImageUri(historyDTO.getArtistImage()))
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivArtist);

        tvCategory.setText(historyDTO.getCategoryName());
        tvLocation.setText(historyDTO.getAddress());
        tvName.setText(ProjectUtils.getFirstLetterCapital(historyDTO.getArtistName()));
        tvAmount.setText(String.format("%s%s", historyDTO.getCurrency_type(), historyDTO.getTotal_amount()));

        final_amount = historyDTO.getTotal_amount();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llPayment:
                dialogPayment();
                break;
//            case R.id.llWallet:
//                Log.i(TAG, "Wallet BALANCE ===> " + amt1);
//
//                if (Float.parseFloat(amt1) >= Float.parseFloat(final_amount)) {
//                    cashDialog(getResources().getString(R.string.wallet_payment), getResources().getString(R.string.wallet_msg), "2");
//                } else {
//                    ProjectUtils.showToast(mContext, "Insufficient balance, please add money to your wallet!");
//                }
//                break;
            case R.id.tvApplyCode:
                params.put(Const.INVOICE_ID, historyDTO.getInvoice_id());
                params.put(Const.COUPON_CODE, ProjectUtils.getEditTextValue(etCode));

                checkCoupon();
                break;
            case R.id.tvCancelCode:
                etCode.setText("");
                tvAmount.setText(String.format("%s%s", historyDTO.getCurrency_type(), historyDTO.getTotal_amount()));
                final_amount = historyDTO.getTotal_amount();
                tvApplyCode.setVisibility(View.VISIBLE);
                tvCancelCode.setVisibility(View.GONE);
                coupon_code = "";
                etCode.setEnabled(true);
                break;
            case R.id.IVback:
                finish();
                break;
        }
    }


    public void checkCoupon() {
        ProjectUtils.showProgressDialog(mContext, true, mContext.getResources().getString(R.string.please_wait));
        new HttpsRequest(Const.CHECK_COUPON_API, params, mContext).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            if (flag) {
                try {
                    ProjectUtils.showToast(mContext, msg);
                    String amt = response.getString("final_amount");
                    discount_amount = response.getString("discount_amount");
                    final_amount = amt;
                    tvAmount.setText(String.format("%s%s", historyDTO.getCurrency_type(), amt));
                    tvApplyCode.setVisibility(View.GONE);
                    tvCancelCode.setVisibility(View.VISIBLE);
                    coupon_code = etCode.getText().toString().trim();
                    etCode.setEnabled(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                ProjectUtils.showToast(mContext, msg);
                etCode.setEnabled(true);
                coupon_code = "";

            }


        });
    }

    public HashMap<String, String> getParms(String type) {
        HashMap<String, String> params = new HashMap<>();
        params.put(Const.INVOICE_ID, historyDTO.getInvoice_id());
        params.put(Const.USER_ID, userDTO.getUser_id());
        params.put(Const.COUPON_CODE, coupon_code);
        params.put(Const.FINAL_AMOUNT, final_amount);
        params.put(Const.PAYMENT_STATUS, "1");
        params.put(Const.PAYMENT_TYPE, type);
        params.put(Const.DISCOUNT_AMOUNT, discount_amount);

        Log.e("sendPaymentConfirm", params.toString());
        return params;
    }

    public void cashDialog(String title, String msg, final String type) {
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(title)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                        sendPayment(PaymentProActivity.this, getParms(type), historyDTO);
                        dialog.dismiss();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            getWallet();
        } catch (Exception e) {
            e.printStackTrace();
        }

        updatePaymentStatus(this, prefrence, getParms("0"), historyDTO);
    }

    public void dialogPayment() {
        dialog = new Dialog(mContext/*, android.R.style.Theme_Dialog*/);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dailog_payment_option);


        ///dialog.getWindow().setBackgroundDrawableResource(R.color.black);
        paystackPay = dialog.findViewById(R.id.paystackButton);
        flutterwavepay = dialog.findViewById(R.id.flutterwaveButton);
        llCancel = dialog.findViewById(R.id.llCancel);

        dialog.show();
        dialog.setCancelable(false);
        llCancel.setOnClickListener(v -> dialog.dismiss());

        coupon_code = ProjectUtils.getEditTextValue(etCode);

        paystackPay.setOnClickListener(v -> {
            Intent in2 = new Intent(mContext, PaymentWeb.class);
            in2.putExtra(Const.HISTORY_DTO, historyDTO);
            in2.putExtra(Const.COUPON_CODE, coupon_code);
            PaymentProActivity.this.startActivity(in2);
            dialog.dismiss();
        });
        flutterwavepay.setOnClickListener(v -> {
            Intent in3 = new Intent(mContext, PaymentWeb.class);
            in3.putExtra(Const.HISTORY_DTO, historyDTO);
            in3.putExtra(Const.COUPON_CODE, coupon_code);
            PaymentProActivity.this.startActivity(in3);
            dialog.dismiss();
        });

    }

    public void getWallet() {
        new HttpsRequest(Const.GET_WALLET_API, parmsGetWallet, mContext).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            if (flag) {
                try {
                    amt1 = response.getJSONObject("data").getString("amount");
                    currency = response.getJSONObject("data").getString("currency_type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }
        });
    }

}
