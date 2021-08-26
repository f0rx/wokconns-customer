package com.wokconns.customer.ui.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.wokconns.customer.R
import com.wokconns.customer.databinding.ActivitySignInBinding
import com.wokconns.customer.dto.UserDTO
import com.wokconns.customer.https.HttpsRequest
import com.wokconns.customer.interfaces.Const
import com.wokconns.customer.interfaces.Helper
import com.wokconns.customer.network.NetworkManager.isConnectToInternet
import com.wokconns.customer.preferences.SharedPrefs
import com.wokconns.customer.utils.ProjectUtils.Fullscreen
import com.wokconns.customer.utils.ProjectUtils.getEditTextValue
import com.wokconns.customer.utils.ProjectUtils.isPasswordValid
import com.wokconns.customer.utils.ProjectUtils.isPhoneNumberValid
import com.wokconns.customer.utils.ProjectUtils.pauseProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showToast
import org.json.JSONObject
import java.util.*
import android.R.id
import com.google.firebase.crashlytics.FirebaseCrashlytics


class SignInActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mContext: Context
    private var prefs: SharedPrefs? = null
    private lateinit var userDTO: UserDTO
    private lateinit var firebase: SharedPreferences
    private lateinit var binding: ActivitySignInBinding
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var crashlytics: FirebaseCrashlytics
    private var isHide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fullscreen(this@SignInActivity)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        mContext = this@SignInActivity
        analytics = FirebaseAnalytics.getInstance(mContext)
        prefs = SharedPrefs.getInstance(mContext)
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        Log.e("tokensss", firebase.getString(Const.DEVICE_TOKEN, "").toString())
        setUiAction()
    }

    fun setUiAction() {
        binding.CBsignIn.setOnClickListener(this)
        binding.CTVBforgot.setOnClickListener(this)
        binding.CTVsignup.setOnClickListener(this)
        binding.ivEnterShow.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.CTVBforgot -> startActivity(Intent(mContext, ForgotPass::class.java))
            R.id.CBsignIn -> clickForSubmit()
            R.id.CTVsignup -> startActivity(Intent(mContext, SignUpActivity::class.java))
            R.id.ivEnterShow -> if (isHide) {
                binding.ivEnterShow.setImageResource(R.drawable.ic_pass_visible)
                binding.CETenterpassword.transformationMethod = null
                binding.CETenterpassword.setSelection(binding.CETenterpassword.text.length)
                isHide = false
            } else {
                binding.ivEnterShow.setImageResource(R.drawable.ic_pass_invisible)
                binding.CETenterpassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.CETenterpassword.setSelection(binding.CETenterpassword.text.length)
                isHide = true
            }
        }
    }

    private fun login() {
        if (prefs != null && prefs?.getValue(Const.LANGUAGE_SELECTION).equals("", ignoreCase = true)) {
            prefs?.setValue(Const.LANGUAGE_SELECTION, "en")
        }

        showProgressDialog(mContext, false, resources.getString(R.string.please_wait))

        HttpsRequest(Const.LOGIN_API, getParams(), mContext).stringPost(TAG,
            object : Helper {
                override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                    pauseProgressDialog()
                    if (msg != null && msg.contains(notVerifiedErrorMatcher)) {
                        val `in` = Intent(mContext, OTPVerificationActivity::class.java)
                        `in`.putExtra(Const.MOBILE, binding.CETMobileNumber.text.toString())
                        startActivity(`in`)
                        overridePendingTransition(R.anim.anim_slide_in_left,
                            R.anim.anim_slide_out_left)
                    }

                    if (flag) {
                        try {
                            showToast(mContext, msg)

                            userDTO = Gson().fromJson(response?.getJSONObject("data").toString(),
                                UserDTO::class.java)
                            prefs?.setParentUser(userDTO, Const.USER_DTO)
                            prefs?.setBooleanValue(Const.IS_REGISTERED, true)

                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Param.METHOD, "email")
                            analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                            showToast(mContext, msg)
                            finish()

                            val `in` = Intent(mContext, BaseActivity::class.java)
                            startActivity(`in`)
                            overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left)
                        } catch (e: Exception) {
                            crashlytics.log(e.message.toString())
                            crashlytics.log(e.stackTrace.toString())
                            e.printStackTrace()
                        }
                    } else {
                        showToast(mContext, msg)
                        showSnackbar(msg)
                    }
                }
            })
    }

    private fun clickForSubmit() {
        if (!isPhoneNumberValid(binding.CETMobileNumber.text.toString().trim { it <= ' ' })) {
            showSnackbar(resources.getString(R.string.valid_mobile_number))
        } else if (!isPasswordValid(binding.CETenterpassword.text.toString()
                .trim { it <= ' ' })
        ) {
            showSnackbar(resources.getString(R.string.val_pass))
        } else {
            if (isConnectToInternet(mContext)) {
                login()
            } else {
                showToast(mContext, resources.getString(R.string.internet_connection))
            }
        }
    }

    fun getParams(): HashMap<String, String?> {
        val parms = HashMap<String, String?>()
        parms[Const.MOBILE] = getEditTextValue(
            binding.CETMobileNumber)
        parms[Const.PASSWORD] = getEditTextValue(
            binding.CETenterpassword)
        parms[Const.DEVICE_TYPE] = "ANDROID"
        parms[Const.DEVICE_TOKEN] =
            firebase.getString(Const.DEVICE_TOKEN, "")
        parms[Const.DEVICE_ID] = "12345"
        parms[Const.ROLE] = "2"
        return parms
    }

    private fun showSnackbar(msg: String?) {
        val snackbar = msg?.let { Snackbar.make(binding.RRsncbar, it, Snackbar.LENGTH_LONG) }
        val snackbarView = snackbar?.view
        snackbarView?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        snackbar?.show()
    }

    override fun onBackPressed() = clickDone()

    private fun clickDone() {
        AlertDialog.Builder(this)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle(getString(R.string.app_name))
            .setMessage(resources.getString(R.string.closeMsg))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                val i = Intent()
                i.action = Intent.ACTION_MAIN
                i.addCategory(Intent.CATEGORY_HOME)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }
            .setNegativeButton(resources.getString(R.string.no)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .show()
    }

    companion object {
        private val TAG = SignInActivity::class.java.simpleName
        private const val notVerifiedErrorMatcher = "does not have a verified phone number"
    }
}