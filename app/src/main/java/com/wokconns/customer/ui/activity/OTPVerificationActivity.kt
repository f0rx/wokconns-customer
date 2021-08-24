package com.wokconns.customer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.wokconns.customer.R
import com.wokconns.customer.https.HttpsRequest
import com.wokconns.customer.interfaces.Const
import com.wokconns.customer.interfaces.Helper
import com.wokconns.customer.utils.CustomEditText
import com.wokconns.customer.utils.CustomTextView
import com.wokconns.customer.utils.ProjectUtils.Fullscreen
import com.wokconns.customer.utils.ProjectUtils.pauseProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showLong
import com.wokconns.customer.utils.ProjectUtils.showProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showToast
import org.json.JSONObject
import java.util.*

class OTPVerificationActivity : AppCompatActivity(), TextWatcher {
    private val cetArrayList = ArrayList<CustomEditText>(NUMBER_OF_DIGITS)
    private lateinit var tempNum: String
    private lateinit var otpCode: String
    private var mobile: String? = null
    private val mContext: Context = this@OTPVerificationActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fullscreen(this@OTPVerificationActivity)
        setContentView(R.layout.activity_otp_verification)
        val ctvPhoneNumber = findViewById<CustomTextView>(R.id.CTVMobileNumber)
        val codeLayout = findViewById<LinearLayout>(R.id.codeLayout)
        val verifyBtn = findViewById<Button>(R.id.CBVerifyMobile)
        val resendVerificationBtn = findViewById<CustomTextView>(R.id.resendVerificationBtn)
        val root = findViewById<View>(R.id.rootOTPScreen)
        if (intent.hasExtra(Const.MOBILE)) {
            mobile = intent.getStringExtra(Const.MOBILE)
            ctvPhoneNumber.text = mobile
        }
        for (i in 0 until codeLayout.childCount) {
            val v = codeLayout.getChildAt(i)
            if (v is CustomEditText) {
                cetArrayList.add(i, v)
                cetArrayList[i].addTextChangedListener(this)
                cetArrayList[i].setOnKeyListener { _: View?, keyCode: Int, event: KeyEvent ->
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                        // user hit backspace
                        if (i != 0) { // Don't implement for first digit
                            cetArrayList[i - 1].requestFocus()
                            cetArrayList[i - 1]
                                .setSelection(cetArrayList[i - 1].length())
                        }
                    }
                    false
                }
            }
        }
        cetArrayList[0].requestFocus()
        resendVerificationBtn.setOnClickListener { v: View? -> resendOTP() }
        verifyBtn.setOnClickListener { v: View? ->
            if (!isValidPinFields) {
                showToast(mContext, resources.getString(R.string.invalid_otp_str))
                val snackbar = Snackbar.make(
                    root,
                    resources.getString(R.string.invalid_otp_str),
                    Snackbar.LENGTH_LONG
                )
                val snackbarView = snackbar.view
                snackbarView.setBackgroundColor(resources.getColor(R.color.colorPrimary))
                snackbar.show()
                return@setOnClickListener
            }
            verifyOTP()
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        tempNum = s.toString()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        //
    }

    override fun afterTextChanged(s: Editable) {
        for (i in cetArrayList.indices) {
            val i1 = cetArrayList[i]
            if (s == i1.editableText) {
                if (s.isEmpty()) return
                if (s.length >= 2) { // if more than 1 char
                    val newTemp = s.toString().substring(s.length - 1, s.length) //get 2nd digit
                    if (newTemp != tempNum) {
                        cetArrayList[i].setText(newTemp)
                    } else {
                        cetArrayList[i].setText(s.toString().substring(0, s.length - 1))
                    }
                } else if (i != cetArrayList.size - 1) { // not last char
                    cetArrayList[i + 1].requestFocus()
                    cetArrayList[i + 1].setSelection(cetArrayList[i + 1].length())
                    return
                } else {
                    // Hide keyboard
                    val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    manager.hideSoftInputFromWindow(i1.windowToken, 0)
                }
            }
        }
    }

    private val isValidPinFields: Boolean
        get() {
            showProgressDialog(mContext, false, resources.getString(R.string.please_wait))
            val sb = StringBuilder()
            for (i in cetArrayList.indices) {
                val editText = cetArrayList[i]
                sb.append(editText.text.toString())
            }
            otpCode = sb.toString()
            pauseProgressDialog()
            return sb.length == NUMBER_OF_DIGITS
        }

    private fun verifyOTP() {
        showProgressDialog(mContext, false, resources.getString(R.string.please_wait))
        val params = HashMap<String, String?>()
        params[Const.MOBILE] = mobile
        params[Const.OTP_CODE] = otpCode
        HttpsRequest(Const.VERIFY_PHONE, params, mContext).stringPost(
            TAG,
            object : Helper {
                override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                    pauseProgressDialog()
                    if (flag) {
                        showToast(mContext, resources.getString(R.string.verification_done))
                        startActivity(Intent(mContext, SignInActivity::class.java))
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

    private fun resendOTP() {
        showProgressDialog(mContext, false, resources.getString(R.string.please_wait))
        val params = HashMap<String, String?>()
        params[Const.MOBILE] = mobile
        HttpsRequest(Const.RESEND_VERIFY_OTP_CODE, params, mContext).stringPost(
            TAG,
            object : Helper {
                override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                    pauseProgressDialog()
                    if (flag) {
                        showLong(mContext, msg ?: resources.getString(R.string.verification_resent))
                    } else {
                        showLong(mContext, msg)
                    }
                }
            })
    }

    companion object {
        private val TAG = OTPVerificationActivity::class.java.simpleName
        private const val NUMBER_OF_DIGITS = 4
    }
}