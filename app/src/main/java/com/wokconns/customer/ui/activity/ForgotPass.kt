package com.wokconns.customer.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.wokconns.customer.R
import com.wokconns.customer.databinding.ActivityForgotPassBinding
import com.wokconns.customer.https.HttpsRequest
import com.wokconns.customer.interfaces.Const
import com.wokconns.customer.interfaces.Helper
import com.wokconns.customer.network.NetworkManager
import com.wokconns.customer.utils.ProjectUtils.getEditTextValue
import com.wokconns.customer.utils.ProjectUtils.isPhoneNumberValid
import com.wokconns.customer.utils.ProjectUtils.pauseProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showToast
import org.json.JSONObject
import java.util.*

class ForgotPass : AppCompatActivity() {
    private lateinit var mContext: Context
    private val parms = HashMap<String, String?>()
    private val TAG = ForgotPass::class.java.simpleName
    private lateinit var binding: ActivityForgotPassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pass)
        mContext = this@ForgotPass
        setUiAction()
    }

    fun setUiAction() {
        binding.btnSubmit.setOnClickListener { v: View? -> submitForm() }
        binding.llBack.setOnClickListener { v: View? -> finish() }
    }

    fun submitForm() {
        if (!isPhoneNumberValid(getEditTextValue(binding.etMobile))) {
            binding.etMobile.error = resources.getString(R.string.valid_mobile_number)
            binding.etMobile.requestFocus()
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                updatePassword()
            } else {
                showToast(mContext, resources.getString(R.string.internet_connection))
            }
        }
    }

    fun updatePassword() {
        parms[Const.MOBILE] = getEditTextValue(binding.etMobile)

        showProgressDialog(mContext, false, resources.getString(R.string.please_wait))

        HttpsRequest(Const.FORGET_PASSWORD_API, parms, mContext).stringPost(
            TAG,
            object : Helper {
                override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                    pauseProgressDialog()
                    if (flag) {
                        showToast(
                            mContext,
                            msg?.ifEmpty {
                                String.format(
                                    "%s %s",
                                    resources.getString(R.string.forgot_pass_success_msg),
                                    getEditTextValue(binding.etMobile)
                                )
                            }
                        )
                        finish()
                        overridePendingTransition(
                            R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left
                        )
                    } else {
                        showToast(mContext, msg)
                    }
                }
            })
    }
}