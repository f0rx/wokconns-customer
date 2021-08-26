package com.wokconns.customer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import com.wokconns.customer.interfaces.Const
import com.wokconns.customer.preferences.SharedPrefs
import com.wokconns.customer.preferences.SharedPrefs.Companion.getInstance
import com.wokconns.customer.ui.activity.AppIntro
import com.wokconns.customer.ui.activity.BaseActivity
import com.wokconns.customer.utils.ProjectUtils.Fullscreen

class SplashActivity : AppCompatActivity() {
    private lateinit var mContext: Context
    private var prefs: SharedPrefs? = null
    private lateinit var crashlytics: FirebaseCrashlytics

    var mTask = Runnable {
        if (prefs != null && prefs!!.getBooleanValue(Const.IS_REGISTERED)) {
            val `in` = Intent(mContext, BaseActivity::class.java)
            startActivity(`in`)
            finish()
            overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left)
        } else {
            startActivity(Intent(this@SplashActivity, AppIntro::class.java))
            finish()
            overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left)
        }
    }
    private val permissions = arrayOf(Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    private var cameraAccepted = false
    private var storageAccepted = false
    private var accessNetState = false
    private var fineLoc = false
    private var corasLoc = false
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Fullscreen(this@SplashActivity)
        setContentView(R.layout.activity_splash)

        mContext = this@SplashActivity
        prefs = getInstance(this@SplashActivity)

        crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setCrashlyticsCollectionEnabled(true)

        FirebaseMessaging.getInstance().subscribeToTopic(Const.TOPIC_CUSTOMER)
            .addOnCompleteListener { }
    }

    override fun onResume() {
        super.onResume()
        if (!hasPermissions(this@SplashActivity, *permissions)) {
            ActivityCompat.requestPermissions(this,
                permissions,
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
        } else {
            handler.postDelayed(mTask, SPLASH_TIME_OUT.toLong())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> try {
                cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                prefs?.setBooleanValue(Const.CAMERA_ACCEPTED, cameraAccepted)
                storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                prefs?.setBooleanValue(Const.STORAGE_ACCEPTED, storageAccepted)
                accessNetState = grantResults[2] == PackageManager.PERMISSION_GRANTED
                prefs?.setBooleanValue(Const.MODIFY_AUDIO_ACCEPTED, accessNetState)
                fineLoc = grantResults[3] == PackageManager.PERMISSION_GRANTED
                prefs?.setBooleanValue(Const.FINE_LOC, fineLoc)
                corasLoc = grantResults[4] == PackageManager.PERMISSION_GRANTED
                prefs?.setBooleanValue(Const.CORAS_LOC, corasLoc)
                handler.postDelayed(mTask, 3000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1003
        private const val SPLASH_TIME_OUT = 3000

        fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                for (permission in permissions) {
                    if (permission?.let {
                            ActivityCompat.checkSelfPermission(context,
                                it)
                        } != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }
    }
}