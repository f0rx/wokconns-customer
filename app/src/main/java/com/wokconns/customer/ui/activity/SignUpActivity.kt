package com.wokconns.customer.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.wokconns.customer.R
import com.wokconns.customer.databinding.ActivitySignUpBinding
import com.wokconns.customer.https.HttpsRequest
import com.wokconns.customer.interfaces.Const
import com.wokconns.customer.interfaces.Helper
import com.wokconns.customer.network.NetworkManager
import com.wokconns.customer.preferences.SharedPrefs
import com.wokconns.customer.utils.ProjectUtils.Fullscreen
import com.wokconns.customer.utils.ProjectUtils.getEditTextValue
import com.wokconns.customer.utils.ProjectUtils.isEditTextFilled
import com.wokconns.customer.utils.ProjectUtils.isEmailValid
import com.wokconns.customer.utils.ProjectUtils.isPasswordValid
import com.wokconns.customer.utils.ProjectUtils.pauseProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showLong
import com.wokconns.customer.utils.ProjectUtils.showProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showToast
import org.json.JSONObject
import java.util.*

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mContext: Context
    private var prefs: SharedPrefs? = null
    private lateinit var firebase: SharedPreferences
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var crashlytics: FirebaseCrashlytics
    private var baseURL = ""
    private var isHide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fullscreen(this@SignUpActivity)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        mContext = this@SignUpActivity
        prefs = SharedPrefs.getInstance(mContext)
        firebase = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        Log.e("tokensss", firebase.getString(Const.DEVICE_TOKEN, "").toString())
        setUiAction()
    }

    fun setUiAction() {
        binding.CBsignup.setOnClickListener(this)
        binding.CTVsignin.setOnClickListener(this)
        binding.tvTerms.setOnClickListener(this)
        binding.tvPrivacy.setOnClickListener(this)
        binding.ivReEnterShow.setOnClickListener(this)
        binding.ivEnterShow.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.CBsignup -> clickForSubmit()
            R.id.CTVsignin -> {
                startActivity(Intent(mContext, SignInActivity::class.java))
                finish()
            }
            R.id.tvTerms -> {
                baseURL = Const.TERMS_URL
                uRLForWebView
            }
            R.id.tvPrivacy -> {
                baseURL = Const.PRIVACY_URL
                uRLForWebView
            }
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
            R.id.ivReEnterShow -> if (isHide) {
                binding.ivReEnterShow.setImageResource(R.drawable.ic_pass_visible)
                binding.CETenterpassagain.transformationMethod = null
                binding.CETenterpassagain.setSelection(binding.CETenterpassagain.text.length)
                isHide = false
            } else {
                binding.ivReEnterShow.setImageResource(R.drawable.ic_pass_invisible)
                binding.CETenterpassagain.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.CETenterpassagain.setSelection(binding.CETenterpassagain.text.length)
                isHide = true
            }
        }
    }

    private fun register() {
        showProgressDialog(mContext, false, resources.getString(R.string.please_wait))

        HttpsRequest(Const.REGISTER_API, param, mContext).stringPost(TAG,
            object : Helper {
                override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                    pauseProgressDialog()
                    if (flag) {
                        try {
                            showToast(mContext, String.format("%s %s!",
                                resources.getString(R.string.registration_success_msg),
                                getEditTextValue(binding.CETfirstname)))

                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, "email")
                            analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                            val `in` = Intent(mContext, OTPVerificationActivity::class.java)
                            `in`.putExtra(Const.MOBILE, binding.phoneNumber.text.toString())
                            startActivity(`in`)
                            finish()

                            overridePendingTransition(R.anim.anim_slide_in_left,
                                R.anim.anim_slide_out_left)
                        } catch (e: Exception) {
                            crashlytics.log(e.message.toString())
                            crashlytics.log(e.stackTrace.toString())
                            e.printStackTrace()
                        }
                    } else {
                        showToast(mContext, msg)
                    }
                }
            })
    }

    private fun clickForSubmit() {
        when {
            !validation(binding.CETfirstname, resources.getString(R.string.val_name)) -> return
            !validation(binding.phoneNumber, resources.getString(R.string.val_phone)) -> return
            !isEmailValid(binding.CETemailadd.text.toString().trim { it <= ' ' }) -> showSnackbar(
                resources.getString(R.string.val_email))
            !isPasswordValid(binding.CETenterpassword.text.toString()
                .trim { it <= ' ' }) -> showSnackbar(resources.getString(R.string.val_pass))
            !checkPassword() -> return
            !validateTerms() -> return
            else -> when {
                NetworkManager.isConnectToInternet(mContext) -> register()
                else -> showToast(mContext, resources.getString(R.string.internet_connection))
            }
        }
    }

    private fun checkPassword(): Boolean {
        when {
            binding.CETenterpassword.text.toString().trim { it <= ' ' } == "" -> {
                showSnackbar(resources.getString(R.string.val_pass))
                return false
            }
            binding.CETenterpassagain.text.toString().trim { it <= ' ' } == "" -> {
                showSnackbar(resources.getString(R.string.val_pass1))
                return false
            }
            binding.CETenterpassword.text.toString()
                .trim { it <= ' ' } != binding.CETenterpassagain.text.toString()
                .trim { it <= ' ' }
            -> {
                showSnackbar(resources.getString(R.string.pass_not_match))
                return false
            }
            else -> return true
        }
    }

    private fun validateTerms(): Boolean = when {
        binding.termsCB.isChecked -> true
        else -> {
            showSnackbar(resources.getString(R.string.terms_acc))
            showLong(mContext, resources.getString(R.string.terms_acc))
            false
        }
    }

    private val param: HashMap<String, String?>
        get() {
            val parms = HashMap<String, String?>()
            parms[Const.NAME] = getEditTextValue(binding.CETfirstname)
            parms[Const.EMAIL_ID] = getEditTextValue(
                binding.CETemailadd)
            parms[Const.PASSWORD] = getEditTextValue(
                binding.CETenterpassword)
            parms[Const.MOBILE] = getEditTextValue(
                binding.phoneNumber)
            parms[Const.ROLE] = "2"
            parms[Const.DEVICE_TYPE] = "ANDROID"
            parms[Const.DEVICE_TOKEN] =
                firebase.getString(Const.DEVICE_TOKEN, "")
            parms[Const.DEVICE_ID] = "12345"
            parms[Const.REFERRAL_CODE] = getEditTextValue(
                binding.etReferal)
            return parms
        }

    private fun showSnackbar(msg: String?) {
        val snackbar = msg?.let { Snackbar.make(binding.RRsncbar, it, Snackbar.LENGTH_LONG) }
        val snackbarView = snackbar?.view
        snackbarView?.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        snackbar?.show()
    }

    private fun validation(editText: EditText, msg: String?): Boolean {
        return if (!isEditTextFilled(editText)) {
            showSnackbar(msg)
            false
        } else true
    }

    private val uRLForWebView: Unit
        get() {
            if (prefs != null && prefs?.getValue(Const.LANGUAGE_SELECTION).equals("", ignoreCase = true))
                prefs?.setValue(Const.LANGUAGE_SELECTION, "en")

            HttpsRequest(baseURL, mContext).stringGet(TAG,
                object : Helper {
                    override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                        if (flag) {
                            try {
                                when {
                                    baseURL.equals(Const.PRIVACY_URL, ignoreCase = true) -> {
                                        val intent1 = Intent(mContext, WebViewCommon::class.java)
                                        intent1.putExtra(Const.URL, msg)
                                        intent1.putExtra(Const.HEADER,
                                            resources.getString(R.string.privacy_policy))
                                        startActivity(intent1)
                                    }
                                    baseURL.equals(Const.TERMS_URL, ignoreCase = true) -> {
                                        val intent3 = Intent(mContext, WebViewCommon::class.java)
                                        intent3.putExtra(Const.URL, msg)
                                        intent3.putExtra(Const.HEADER,
                                            resources.getString(R.string.terms_of_use))
                                        startActivity(intent3)
                                    }
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

    override fun onBackPressed() {
        startActivity(Intent(mContext, SignInActivity::class.java))
        finish()
    }

    companion object {
        private val TAG = SignUpActivity::class.java.simpleName
    }
}