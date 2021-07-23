package com.wokconns.customer.interfaces;

import android.app.Activity;
import android.content.Intent;

import com.wokconns.customer.R;
import com.wokconns.customer.dto.HistoryDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.activity.WriteReview;
import com.wokconns.customer.utils.ProjectUtils;

import java.util.Map;

public interface IPostPayment {
    String _kTAG = IPostPayment.class.getName();

    default void updatePaymentStatus(Activity activity,
                                     SharedPrefrence prefrence, Map<String, String> params,
                                     HistoryDTO history) {
        if (prefrence.getValue(Consts.SURL).equalsIgnoreCase(Consts.PAYMENT_SUCCESS)) {
            prefrence.clearPreferences(Consts.SURL);
            sendPayment(activity, params, history);
        } else if (prefrence.getValue(Consts.FURL).equalsIgnoreCase(Consts.PAYMENT_FAIL)) {
            prefrence.clearPreferences(Consts.FURL);
            activity.finish();
        }
    }

    default void sendPayment(Activity activity, Map<String, String> params, HistoryDTO history) {
        ProjectUtils.showProgressDialog(activity, true, activity.getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.MAKE_PAYMENT_API, params, activity).stringPost(_kTAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            if (flag) {
                ProjectUtils.showToast(activity, msg);
                Intent in = new Intent(activity, WriteReview.class);
                in.putExtra(Consts.HISTORY_DTO, history);
                activity.startActivity(in);
                activity.finish();
            } else {
                ProjectUtils.showToast(activity, msg);
            }
        });
    }
}
