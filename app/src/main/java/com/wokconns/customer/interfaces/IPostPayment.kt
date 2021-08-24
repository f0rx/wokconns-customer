package com.wokconns.customer.interfaces

import android.app.Activity
import android.content.Intent
import com.wokconns.customer.R
import com.wokconns.customer.dto.HistoryDTO
import com.wokconns.customer.https.HttpsRequest
import com.wokconns.customer.preferences.SharedPrefrence
import com.wokconns.customer.ui.activity.WriteReview
import com.wokconns.customer.utils.ProjectUtils.pauseProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showToast
import org.json.JSONObject
import java.util.*

interface IPostPayment {
    fun updatePaymentStatus(
        activity: Activity,
        preference: SharedPrefrence,
        params: HashMap<String, String>?,
        history: HistoryDTO?
    ) {
        if (preference.getValue(Const.SURL).equals(Const.PAYMENT_SUCCESS, ignoreCase = true)) {
            preference.clearPreferences(Const.SURL)
            sendPayment(activity, params, history)
        } else if (preference.getValue(Const.FURL).equals(Const.PAYMENT_FAIL, ignoreCase = true)) {
            preference.clearPreferences(Const.FURL)
            activity.finish()
        }
    }

    fun sendPayment(activity: Activity, params: HashMap<String, String>?, history: HistoryDTO?) {
        showProgressDialog(activity, false, activity.resources.getString(R.string.please_wait))
        HttpsRequest(Const.MAKE_PAYMENT_API, params, activity).stringPost(
            kTAG,
            object : Helper {
                override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                    pauseProgressDialog()
                    if (flag) {
                        showToast(activity, msg)
                        val `in` = Intent(activity, WriteReview::class.java)
                        `in`.putExtra(Const.HISTORY_DTO, history)
                        activity.startActivity(`in`)
                        activity.finish()
                    } else {
                        showToast(activity, msg)
                    }
                }
            })
    }

    companion object {
        val kTAG: String = IPostPayment::class.java.name
    }
}