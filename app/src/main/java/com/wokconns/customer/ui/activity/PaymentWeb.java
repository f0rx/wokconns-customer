package com.wokconns.customer.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.wokconns.customer.BuildConfig;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivityPaymetWebBinding;
import com.wokconns.customer.dto.ArtistDetailsDTO;
import com.wokconns.customer.dto.HistoryDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.interfacess.Helper;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.utils.CardValidatorInterface;
import com.wokconns.customer.utils.NumberMaskInputFormatter;
import com.wokconns.customer.utils.ProjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class PaymentWeb extends AppCompatActivity implements CardValidatorInterface {
    private static final String successPaypalUrl = Consts.PAYMENT_SUCCESS_paypal;
    private static final String failurePaypalUrl = Consts.PAYMENT_FAIL_Paypal;
    //    private static String surl_stripe_book = Consts.INVOICE_PAYMENT_SUCCESS_Stripe;
//    private static String furl_stripe_book = Consts.INVOICE_PAYMENT_FAIL_Stripe;
    private static final String kTAG = PaymentWeb.class.getName();
    private static String coupon;
    ActivityPaymetWebBinding binding;
    private String url;
    private SharedPrefrence preference;
    private UserDTO userDTO;
    private HistoryDTO historyDTO;
    private ArtistDetailsDTO artistDetailsDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_paymet_web);
        preference = SharedPrefrence.getInstance(this);
        userDTO = preference.getParentUser(Consts.USER_DTO);
        if (getIntent().hasExtra(Consts.HISTORY_DTO)) {
            historyDTO = (HistoryDTO) getIntent().getSerializableExtra(Consts.HISTORY_DTO);
            coupon = getIntent().getStringExtra(Consts.COUPON_CODE);
            url = getIntent().getStringExtra(Consts.PAYMENT_URL);
        }

        initializePaystack();

        setupListeners();

        binding.IVback.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(PaymentWeb.this, R.anim.click_event));
            PaymentWeb.this.finish();
        });

        if (NetworkManager.isConnectToInternet(this)) {
            // Get information about Artist to be paid
            getArtist();
        } else {
            ProjectUtils.showToast(this, getResources().getString(R.string.internet_connection));
        }
    }

    private void initializePaystack() {
        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublicKey(BuildConfig.PSTK_PUBLIC_KEY);
    }

    private void setupListeners() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.cardNumber.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_NUMBER);
            binding.cardExpiry.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_EXPIRATION_DATE);
            binding.cardCVV.setAutofillHints(View.AUTOFILL_HINT_CREDIT_CARD_SECURITY_CODE);
        }

        Objects.requireNonNull(binding.cardNumber.getEditText()).addTextChangedListener(
                new NumberMaskInputFormatter(23, 19, 5, ' ')
        );

        Objects.requireNonNull(binding.cardExpiry.getEditText()).addTextChangedListener(
                new NumberMaskInputFormatter(5, 4, 3, '/')
        );

        Objects.requireNonNull(binding.cardCVV.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == getResources().getInteger(R.integer.card_cvv_length) - 1 ||
                        s.toString().length() == getResources().getInteger(R.integer.card_cvv_length)) {
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(binding.cardCVV.getWindowToken(), 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.payNow.setOnClickListener(v -> PaymentWeb.this.validateAndChargeCard());
    }

    private void validateAndChargeCard() {
        String cardNumber = Objects.requireNonNull(binding.cardNumber.getEditText()).getText().toString();
        String cardExpiry = Objects.requireNonNull(binding.cardExpiry.getEditText()).getText().toString();
        String cvv = Objects.requireNonNull(binding.cardCVV.getEditText()).getText().toString();

        int expiryMonth = cleanExpiryDate(cardExpiry)[0];
        int expiryYear = cleanExpiryDate(cardExpiry)[1];

        Card card = new Card(cleanCardNumber(cardNumber), expiryMonth, expiryYear, cvv);

        if (cardNumber.isEmpty() || !isValidCardNumber(cardNumber) || !card.validNumber()) {
            Toast.makeText(this, "Invalid card number!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cardExpiry.isEmpty() || !isValidCardExpiryYear(cardExpiry) || !card.validExpiryDate()) {
            Toast.makeText(this, "Invalid expiry date!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cvv.isEmpty() || !card.validCVC()) {
            Toast.makeText(this, "Provide a valid CVV!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (NetworkManager.isConnectToInternet(this)) {
                if (artistDetailsDTO != null) // Perform card charge
                    performCharge(card);
                else // Get information about Artist to be paid
                    getArtist();
            } else {
                ProjectUtils.showToast(this, getResources().getString(R.string.internet_connection));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void performCharge(Card card) throws JSONException {
        int amount = Integer.parseInt(historyDTO.getFinal_amount());
        amount *= 100; // Convert amount to kobo

        toggleLoading(true);

        Charge charge = new Charge();
        charge.setAmount(amount);
        charge.setEmail(userDTO.getEmail_id());
        charge.setCard(card);
        charge.putCustomField("Customer Name", userDTO.getName());
        charge.putCustomField("Artisan Name", artistDetailsDTO.getName());
        charge.putCustomField("Artisan Email", artistDetailsDTO.getEmail_id());
        charge.putCustomField("Artisan Account Name", artistDetailsDTO.getAccount_holder_name());
        charge.putCustomField("Artisan Account Number", artistDetailsDTO.getAccount_no());
        charge.putCustomField("Artisan Bank Name", artistDetailsDTO.getBank_name());
        charge.putCustomField("Currency Name", artistDetailsDTO.getCurrency_name());
        charge.putCustomField("Currency Code", artistDetailsDTO.getCurrency_code());
        charge.putCustomField("Category Name", artistDetailsDTO.getCategory_name());
        charge.putCustomField("Artisan Location", historyDTO.getAddress());
        charge.putCustomField("Coupon Code", coupon);
        charge.putCustomField("Total Time in minutes", historyDTO.getWorking_min());
        charge.putCustomField("Final Amount", historyDTO.getFinal_amount());
        charge.putCustomField("Total Amount", historyDTO.getTotal_amount());
        charge.putCustomField("Discount Amount", historyDTO.getDiscount_amount());
        charge.putCustomField("Booking Date", historyDTO.getBooking_date());
        charge.putCustomField("Booking Time", historyDTO.getBooking_time());

        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                toggleLoading(false);
                parseResponse(transaction.getReference());
                preference.setValue(Consts.SURL, successPaypalUrl);
                finish();
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                Log.d(kTAG, "beforeValidate: " + transaction.getReference());
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                Log.d(kTAG, "onError: " + error.getMessage());
                Log.d(kTAG, "onError: " + error);
                Log.d(kTAG, "onErrorTransaction: " + transaction.getReference());

                toggleLoading(false);
                ProjectUtils.showToast(PaymentWeb.this, error.getLocalizedMessage());
                preference.setValue(Consts.FURL, failurePaypalUrl);
            }
        });
    }

    private void toggleLoading(boolean isLoading) {
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.payNow.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void parseResponse(String reference) {
        String message = "Payment was successful!\n Transaction Reference => " + reference;
        ProjectUtils.showToast(this, message);
    }

    public void getArtist() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Consts.ARTIST_ID, historyDTO.getArtist_id());
        params.put(Consts.USER_ID, userDTO.getUser_id());

        ProjectUtils.showProgressDialog(this, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_ARTIST_BY_ID_API, params, this).stringPost(kTAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            if (flag) {
                try {
                    artistDetailsDTO = new Gson().fromJson(response.getJSONObject("data").toString(), ArtistDetailsDTO.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
