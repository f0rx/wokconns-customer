package com.wokconns.customer.ui.activity;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivityForgotPassBinding;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.interfacess.Helper;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPass extends AppCompatActivity {
    private Context mContext;
    private HashMap<String, String> parms = new HashMap<>();
    private String TAG = ForgotPass.class.getSimpleName();
    private ActivityForgotPassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_forgot_pass);
        mContext = ForgotPass.this;
        setUiAction();
    }

    public void setUiAction() {
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void submitForm() {
        if (!ValidateEmail()) {
            return;
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                updatepass();

            } else {
                ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
            }
        }
    }


    public boolean ValidateEmail() {
        if (!ProjectUtils.isEmailValid( binding.etEmail.getText().toString().trim())) {
            binding.etEmail.setError(getResources().getString(R.string.val_email));
            binding.etEmail.requestFocus();
            return false;
        }
        return true;
    }

    public void updatepass() {
        parms.put(Consts.EMAIL_ID, ProjectUtils.getEditTextValue( binding.etEmail));
        ProjectUtils.showProgressDialog(mContext, false, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.FORGET_PASSWORD_API, parms, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left);
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }
            }
        });
    }

}
