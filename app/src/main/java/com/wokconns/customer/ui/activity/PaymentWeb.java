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

import com.wokconns.customer.BuildConfig;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivityPaymetWebBinding;
import com.wokconns.customer.dto.HistoryDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.utils.CardValidatorInterface;
import com.wokconns.customer.utils.NumberMaskInputFormatter;

import org.json.JSONException;

import java.util.Objects;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class PaymentWeb extends AppCompatActivity implements CardValidatorInterface {
    private UserDTO userDTO;
    private HistoryDTO historyDTO;
    private Context mContext;
    private static String coupon;
    private static final String kTAG = "MainActivity@Tag";
    ActivityPaymetWebBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_paymet_web);
        mContext = PaymentWeb.this;
        SharedPrefrence preference = SharedPrefrence.getInstance(mContext);
        userDTO = preference.getParentUser(Consts.USER_DTO);
        if (getIntent().hasExtra(Consts.HISTORY_DTO)) {
            historyDTO = (HistoryDTO) getIntent().getSerializableExtra(Consts.HISTORY_DTO);
            coupon = getIntent().getStringExtra(Consts.COUPON_CODE);
        }

        initializePaystack();

        initializeListeners();

        binding.IVback.setOnClickListener(view -> {
            view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.click_event));
            finish();
        });
    }

    private void initializePaystack() {
        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublicKey(BuildConfig.PSTK_PUBLIC_KEY);
    }

    private void initializeListeners() {
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

        binding.payNow.setOnClickListener(v -> validateAndChargeCard());
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
            performCharge(card);
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
        charge.putCustomField("Artisan Name", historyDTO.getArtistName());
        charge.putCustomField("Artisan Location", historyDTO.getAddress());
        charge.putCustomField("Coupon Code", historyDTO.getCoupon_code());
        charge.putCustomField("Category Name", historyDTO.getCategoryName());
        charge.putCustomField("Total Time in minutes", historyDTO.getWorking_min());
        charge.putCustomField("Booking Date", historyDTO.getBooking_date());
        charge.putCustomField("Booking Time", historyDTO.getBooking_time());
        charge.putCustomField("Discount Amount", historyDTO.getDiscount_amount());
        charge.putCustomField("Final Amount", historyDTO.getFinal_amount());
        charge.putCustomField("Total Amount", historyDTO.getTotal_amount());

        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                toggleLoading(false);
                parseResponse(transaction.getReference());
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
                Toast.makeText(PaymentWeb.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleLoading(boolean isLoading) {
        binding.progressIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.payNow.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void parseResponse(String reference) {
        String message = "Payment was successful!\n Transaction Reference => " + reference;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
