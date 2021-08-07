package com.wokconns.customer.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.wokconns.customer.R;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.utils.CustomEditText;
import com.wokconns.customer.utils.CustomTextView;
import com.wokconns.customer.utils.ProjectUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class OTPVerificationActivity extends AppCompatActivity implements TextWatcher {
    private static final String TAG = OTPVerificationActivity.class.getSimpleName();
    private static final int NUMBER_OF_DIGITS = 4;
    private final ArrayList<CustomEditText> CETArrayList = new ArrayList<>(NUMBER_OF_DIGITS);
    private String tempNum, emailAddress, otpCode;
    private final Context mContext = OTPVerificationActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProjectUtils.Fullscreen(OTPVerificationActivity.this);
        setContentView(R.layout.activity_otp_verification);

        String mobile = "";
        final CustomTextView CTVPhoneNumber = findViewById(R.id.CTVMobileNumber);
        final LinearLayout codeLayout = findViewById(R.id.codeLayout);
        final Button verifyBtn = findViewById(R.id.CBVerifyMobile);
        final CustomTextView resendVerificationBtn = findViewById(R.id.resendVerificationBtn);
        final View root = findViewById(R.id.rootOTPScreen);

        if (getIntent().hasExtra(Const.MOBILE)) {
            mobile = getIntent().getStringExtra(Const.MOBILE);
            emailAddress = getIntent().getStringExtra(Const.EMAIL);
            CTVPhoneNumber.setText(mobile);
        }

        for (int i = 0; i < codeLayout.getChildCount(); i++) {
            final View v = codeLayout.getChildAt(i);
            if (v instanceof CustomEditText) {
                CETArrayList.add(i, (CustomEditText) v);
                CETArrayList.get(i).addTextChangedListener(this);

                int finalI = i;

                CETArrayList.get(i).setOnKeyListener((_1, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        // user hit backspace
                        if (finalI != 0) { // Don't implement for first digit
                            CETArrayList.get(finalI - 1).requestFocus();
                            CETArrayList.get(finalI - 1)
                                    .setSelection(CETArrayList.get(finalI - 1).length());
                        }
                    }
                    return false;
                });
            }
        }

        CETArrayList.get(0).requestFocus();

        resendVerificationBtn.setOnClickListener(v -> resendOTP());

        verifyBtn.setOnClickListener(v -> {
            if (!isValidPinFields()) {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.invalid_otp_str));

                Snackbar snackbar = Snackbar.make(root, getResources().getString(R.string.invalid_otp_str), Snackbar.LENGTH_LONG);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                snackbar.show();
                return;
            }

            verifyOTP();
        });
    }

    @Override
    public void beforeTextChanged(@NotNull CharSequence s, int start, int count, int after) {
        tempNum = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //
    }

    @Override
    public void afterTextChanged(Editable s) {
        for (int i = 0; i < CETArrayList.size(); i++) {
            CustomEditText i1 = CETArrayList.get(i);

            if (s.equals(i1.getEditableText())) {
                if (s.length() == 0) return;

                if (s.length() >= 2) { // if more than 1 char
                    String newTemp = s.toString().substring(s.length() - 1, s.length()); //get 2nd digit
                    if (!newTemp.equals(tempNum)) {
                        CETArrayList.get(i).setText(newTemp);
                    } else {
                        CETArrayList.get(i).setText(s.toString().substring(0, s.length() - 1));
                    }
                } else if (i != CETArrayList.size() - 1) { // not last char
                    CETArrayList.get(i + 1).requestFocus();
                    CETArrayList.get(i + 1).setSelection(CETArrayList.get(i + 1).length());
                    return;
                } else {
                    // Hide keyboard
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(i1.getWindowToken(), 0);
                }
            }
        }
    }

    private Boolean isValidPinFields() {
        ProjectUtils.showProgressDialog(mContext, false, getResources().getString(R.string.please_wait));

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < CETArrayList.size(); i++) {
            CustomEditText editText = CETArrayList.get(i);

            sb.append(editText.getText().toString());
        }

        otpCode = sb.toString();

        ProjectUtils.pauseProgressDialog();

        return sb.length() == NUMBER_OF_DIGITS;
    }

    private void verifyOTP() {
        ProjectUtils.showProgressDialog(mContext, false, getResources().getString(R.string.please_wait));

        HashMap<String, String> params = new HashMap<>();
        params.put(Const.EMAIL, emailAddress);
        params.put(Const.OTP_CODE, otpCode);

        new HttpsRequest(Const.VERIFY_PHONE, params, mContext).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();

            if (flag) {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.verification_done));

                startActivity(new Intent(mContext, SignInActivity.class));
                finish();
                overridePendingTransition(R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_left);
            } else {
                ProjectUtils.showToast(mContext, msg);
            }
        });
    }

    private void resendOTP() {
        ProjectUtils.showProgressDialog(mContext, false, getResources().getString(R.string.please_wait));

        HashMap<String, String> params = new HashMap<>();
        params.put(Const.EMAIL, emailAddress);

        new HttpsRequest(Const.RESEND_VERIFY_OTP_CODE, params, mContext).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();

            if (flag) {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.verification_resent));
            } else {
                ProjectUtils.showToast(mContext, msg);
            }
        });
    }
}