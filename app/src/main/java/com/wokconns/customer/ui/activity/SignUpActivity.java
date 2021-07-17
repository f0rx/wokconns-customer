package com.wokconns.customer.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivitySignUpBinding;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.interfacess.Helper;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = SignUpActivity.class.getSimpleName();
    String baseURL = "";
    private Context mContext;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private SharedPreferences firebase;
    private boolean isHide = false;
    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProjectUtils.Fullscreen(SignUpActivity.this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        mContext = SignUpActivity.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Consts.DEVICE_TOKEN, ""));
        setUiAction();
    }

    public void setUiAction() {
        binding.CBsignup.setOnClickListener(this);
        binding.CTVsignin.setOnClickListener(this);
        binding.tvTerms.setOnClickListener(this);
        binding.tvPrivacy.setOnClickListener(this);
        binding.ivReEnterShow.setOnClickListener(this);
        binding.ivEnterShow.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CBsignup:
                clickForSubmit();
                break;
            case R.id.CTVsignin:
                startActivity(new Intent(mContext, SignInActivity.class));
                finish();
                break;
            case R.id.tvTerms:
                baseURL = Consts.TERMS_URL;
                getURLForWebView();
                break;
            case R.id.tvPrivacy:
                baseURL = Consts.PRIVACY_URL;
                getURLForWebView();
                break;
            case R.id.ivEnterShow:
                if (isHide) {
                    binding.ivEnterShow.setImageResource(R.drawable.ic_pass_visible);
                    binding.CETenterpassword.setTransformationMethod(null);
                    binding.CETenterpassword.setSelection(binding.CETenterpassword.getText().length());
                    isHide = false;
                } else {
                    binding.ivEnterShow.setImageResource(R.drawable.ic_pass_invisible);
                    binding.CETenterpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.CETenterpassword.setSelection(binding.CETenterpassword.getText().length());
                    isHide = true;
                }
                break;
            case R.id.ivReEnterShow:
                if (isHide) {
                    binding.ivReEnterShow.setImageResource(R.drawable.ic_pass_visible);
                    binding.CETenterpassagain.setTransformationMethod(null);
                    binding.CETenterpassagain.setSelection(binding.CETenterpassagain.getText().length());
                    isHide = false;
                } else {
                    binding.ivReEnterShow.setImageResource(R.drawable.ic_pass_invisible);
                    binding.CETenterpassagain.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    binding.CETenterpassagain.setSelection(binding.CETenterpassagain.getText().length());
                    isHide = true;
                }
                break;
        }

    }

    public void register() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));

        new HttpsRequest(Consts.REGISTER_API, getParam(), mContext).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();

            if (flag) {
                try {
                    ProjectUtils.showToast(mContext, msg);

                    SignUpActivity.this.finish();

                    SignUpActivity.this.startActivity(new Intent(mContext, SignInActivity.class));
                    SignUpActivity.this.overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ProjectUtils.showToast(mContext, msg);
            }
        });
    }

    public void clickForSubmit() {
        if (!validation(binding.CETfirstname, getResources().getString(R.string.val_name))) {
            return;
        } else if (!validation(binding.phoneNumber, getResources().getString(R.string.val_phone))) {
            return;
        } else if (!ProjectUtils.isEmailValid(binding.CETemailadd.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_email));
        } else if (!ProjectUtils.isPasswordValid(binding.CETenterpassword.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.val_pass));
        } else if (!checkPassword()) {
            return;
        } else if (!validateTerms()) {
            return;
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                register();
            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_connection));
            }
        }
    }

    private boolean checkPassword() {
        if (binding.CETenterpassword.getText().toString().trim().equals("")) {
            showSickbar(getResources().getString(R.string.val_pass));
            return false;
        } else if (binding.CETenterpassagain.getText().toString().trim().equals("")) {
            showSickbar(getResources().getString(R.string.val_pass1));
            return false;
        } else if (!binding.CETenterpassword.getText().toString().trim().equals(binding.CETenterpassagain.getText().toString().trim())) {
            showSickbar(getResources().getString(R.string.pass_not_match));
            return false;
        }
        return true;
    }

    private boolean validateTerms() {
        if (binding.termsCB.isChecked()) {
            return true;
        } else {
            showSickbar(getResources().getString(R.string.terms_acc));
            return false;
        }
    }

    public HashMap<String, String> getParam() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.NAME, ProjectUtils.getEditTextValue(binding.CETfirstname));
        parms.put(Consts.EMAIL_ID, ProjectUtils.getEditTextValue(binding.CETemailadd));
        parms.put(Consts.PASSWORD, ProjectUtils.getEditTextValue(binding.CETenterpassword));
        parms.put(Consts.MOBILE, ProjectUtils.getEditTextValue(binding.phoneNumber));
        parms.put(Consts.MOBILE_NUMBER, ProjectUtils.getEditTextValue(binding.phoneNumber));
        parms.put(Consts.ROLE, "2");
        parms.put(Consts.DEVICE_TYPE, "ANDROID");
        parms.put(Consts.DEVICE_TOKEN, firebase.getString(Consts.DEVICE_TOKEN, ""));
        parms.put(Consts.DEVICE_ID, "12345");
        parms.put(Consts.REFERRAL_CODE, ProjectUtils.getEditTextValue(binding.etReferal));
        Log.e(TAG, parms.toString());
        return parms;
    }

    public void showSickbar(String msg) {
        Snackbar snackbar = Snackbar.make(binding.RRsncbar, msg, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    public boolean validation(EditText editText, String msg) {
        if (!ProjectUtils.isEditTextFilled(editText)) {
            Snackbar snackbar = Snackbar.make(binding.RRsncbar, msg, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            snackbar.show();
            return false;
        } else {
            return true;
        }
    }

    private void getURLForWebView() {
        if (prefrence.getValue(Consts.LANGUAGE_SELECTION).equalsIgnoreCase("")) {
            prefrence.setValue(Consts.LANGUAGE_SELECTION, "en");
        }
//        new HttpsRequest(baseURL, mContext).stringGet(TAG, new Helper() {
//            @Override
//            public void backResponse(boolean flag, String msg, JSONObject response) {
//                if (flag) {
//                    try {
//                        if (baseURL.equalsIgnoreCase(Consts.PRIVACY_URL)) {
//                            Intent intent1 = new Intent(mContext, WebViewCommon.class);
//                            intent1.putExtra(Consts.URL, msg);
//                            intent1.putExtra(Consts.HEADER, getResources().getString(R.string.privacy_policy));
//                            startActivity(intent1);
//                        } else if (baseURL.equalsIgnoreCase(Consts.TERMS_URL)) {
//                            Intent intent3 = new Intent(mContext, WebViewCommon.class);
//                            intent3.putExtra(Consts.URL, msg);
//                            intent3.putExtra(Consts.HEADER, getResources().getString(R.string.terms_of_use));
//                            startActivity(intent3);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    ProjectUtils.showToast(mContext, msg);
//                }
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        startActivity(new Intent(mContext, SignInActivity.class));
        finish();
    }
}
