package com.wokconns.customer.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivitySignInBinding;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.utils.ProjectUtils;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private String TAG = SignInActivity.class.getSimpleName();
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private SharedPreferences firebase;
    private boolean isHide = false;
    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProjectUtils.Fullscreen(SignInActivity.this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        mContext = SignInActivity.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        Log.e("tokensss", firebase.getString(Const.DEVICE_TOKEN, ""));
        setUiAction();
    }

    public void setUiAction() {
        binding.CBsignIn.setOnClickListener(this);
        binding.CTVBforgot.setOnClickListener(this);
        binding.CTVsignup.setOnClickListener(this);
        binding.ivEnterShow.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CTVBforgot:
                startActivity(new Intent(mContext, ForgotPass.class));
                break;
            case R.id.CBsignIn:
                clickForSubmit();
                break;
            case R.id.CTVsignup:
                startActivity(new Intent(mContext, SignUpActivity.class));
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
        }
    }

    public void login() {
        if (prefrence.getValue(Const.LANGUAGE_SELECTION).equalsIgnoreCase("")) {
            prefrence.setValue(Const.LANGUAGE_SELECTION, "en");
        }

        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));

        new HttpsRequest(Const.LOGIN_API, getparm(), mContext).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            if (flag) {
                try {
                    ProjectUtils.showToast(mContext, msg);

                    userDTO = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);
                    prefrence.setParentUser(userDTO, Const.USER_DTO);

                    prefrence.setBooleanValue(Const.IS_REGISTERED, true);

                    ProjectUtils.showToast(mContext, msg);

                    finish();

                    Intent in = new Intent(mContext, BaseActivity.class);
                    startActivity(in);
                    overridePendingTransition(R.anim.anim_slide_in_left,
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
        if (!ProjectUtils.isPhoneNumberValid(binding.CETMobileNumber.getText().toString().trim())) {
            showSnackbar(getResources().getString(R.string.valid_mobile_number));
        } else if (!ProjectUtils.isPasswordValid(binding.CETenterpassword.getText().toString().trim())) {
            showSnackbar(getResources().getString(R.string.val_pass));
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                login();
            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_connection));
            }
        }


    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Const.MOBILE, ProjectUtils.getEditTextValue(binding.CETMobileNumber));
        parms.put(Const.PASSWORD, ProjectUtils.getEditTextValue(binding.CETenterpassword));
        parms.put(Const.DEVICE_TYPE, "ANDROID");
        parms.put(Const.DEVICE_TOKEN, firebase.getString(Const.DEVICE_TOKEN, ""));
        parms.put(Const.DEVICE_ID, "12345");
        parms.put(Const.ROLE, "2");
        return parms;
    }

    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar.make(binding.RRsncbar, msg, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        clickDone();
    }

    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.closeMsg))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    dialog.dismiss();
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_HOME);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void _mockSignIn() {
        userDTO = new UserDTO("123456", "WOKCONNS Customer",
                "customer@wokconns.com", "passworD12", "https://firebasestorage.googleapis.com/v0/b/smartlets-x.appspot.com/o/assets%2Fdefault-user.png?alt=media&token=82e08454-1786-4f0f-989a-03605e489a64",
                "72 Congress Road", "Lekki Phase 1", "",
                "", "1", "", "", "",
                "", "User_Ref341", "Male", "Lagos",
                "Lagos", "Nigeria", "", "ANDROID", "",
                "");
        prefrence.setParentUser(userDTO, Const.USER_DTO);

        prefrence.setBooleanValue(Const.IS_REGISTERED, true);

        ProjectUtils.showToast(mContext, "Login Success!!");

        Intent in = new Intent(mContext, BaseActivity.class);
        startActivity(in);
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
    }
}
