package com.wokconns.customer.interfaces

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.CompoundButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.wokconns.customer.R
import com.wokconns.customer.databinding.DialogDisclaimerBindingImpl
import com.wokconns.customer.https.HttpsRequest
import com.wokconns.customer.ui.activity.WebViewCommon
import com.wokconns.customer.utils.ProjectUtils.showToast
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean

interface DisclaimerWarning {
    fun showDisclaimerDialog(mContext: Context?, intent: Intent?) {
        if (mContext == null) throw AssertionError("Context cannot be null")
        if (mContext !is Activity) throw AssertionError(
            "Interface methods can only be called within an Activity"
        )
        try {
            val dialog = Dialog(mContext)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val binding1: DialogDisclaimerBindingImpl = DataBindingUtil.inflate(
                LayoutInflater.from(mContext), R.layout.dialog_disclaimer, null, false
            )
            dialog.setContentView(binding1.root)
            dialog.show()
            dialog.setCancelable(true)
            val hasAcceptedTerms = AtomicBoolean(binding1.termsCheck.isChecked())
            binding1.tvTerms.setOnClickListener(View.OnClickListener { v: View? ->
                getURLForWebView(
                    Const.TERMS_URL, mContext
                )
            })
            binding1.tvPrivacy.setOnClickListener(View.OnClickListener { v: View? ->
                getURLForWebView(
                    Const.PRIVACY_URL, mContext
                )
            })
            binding1.cancelButton.setOnClickListener(View.OnClickListener { v: View? -> dialog.dismiss() })
            binding1.termsCheck.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { v: CompoundButton?, isChecked: Boolean ->
                hasAcceptedTerms.set(
                    isChecked
                )
            })
            binding1.consentButton.setOnClickListener(View.OnClickListener { v: View? ->
                if (hasAcceptedTerms.get()) {
                    if (intent != null) mContext.startActivity(intent)
                    dialog.dismiss()
                } else Toast.makeText(
                    mContext,
                    "Please accept Terms and Condition!", Toast.LENGTH_SHORT
                ).show()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getURLForWebView(baseURL: String, mContext: Context?) {
        if (mContext == null) throw AssertionError("Context cannot be null")
        if (mContext !is Activity) throw AssertionError(
            "Interface methods can only be called within an Activity"
        )
        HttpsRequest(baseURL, mContext)
            .stringGet(
                kTAG,
                object : Helper {
                    override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                        if (flag) {
                            try {
                                if (baseURL.equals(Const.PRIVACY_URL, ignoreCase = true)) {
                                    val intent1 = Intent(mContext, WebViewCommon::class.java)
                                    intent1.putExtra(Const.URL, msg)
                                    intent1.putExtra(
                                        Const.HEADER,
                                        mContext.getResources().getString(R.string.privacy_policy)
                                    )
                                    mContext.startActivity(intent1)
                                } else if (baseURL.equals(Const.TERMS_URL, ignoreCase = true)) {
                                    val intent3 = Intent(mContext, WebViewCommon::class.java)
                                    intent3.putExtra(Const.URL, msg)
                                    intent3.putExtra(
                                        Const.HEADER,
                                        mContext.getResources().getString(R.string.terms_of_use)
                                    )
                                    mContext.startActivity(intent3)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            showToast(mContext, msg)
                        }
                    }
                })
    }

    companion object {
        val kTAG: String = DisclaimerWarning::class.java.name
    }
}