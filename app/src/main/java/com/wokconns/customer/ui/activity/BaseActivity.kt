package com.wokconns.customer.ui.activity

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.SimpleDrawerListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Result
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.wokconns.customer.R
import com.wokconns.customer.dto.UserDTO
import com.wokconns.customer.https.HttpsRequest
import com.wokconns.customer.interfaces.Const
import com.wokconns.customer.interfaces.Helper
import com.wokconns.customer.preferences.SharedPrefs
import com.wokconns.customer.preferences.SharedPrefs.Companion.getInstance
import com.wokconns.customer.ui.fragment.*
import com.wokconns.customer.utils.*
import com.wokconns.customer.utils.ProjectUtils.formatImageUri
import com.wokconns.customer.utils.ProjectUtils.log
import com.wokconns.customer.utils.ProjectUtils.showToast
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.*

class BaseActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private val shouldLoadHomeFragOnBackPress = true
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var navigationView: NavigationView
    lateinit var header: RelativeLayout
    lateinit var drawer: DrawerLayout
    private lateinit var navHeader: View
    private lateinit var menuLeftIV: ImageView
    lateinit var ivFilter: ImageView
    lateinit var prefrence: SharedPrefs
    lateinit var headerNameTV: CustomTextViewBold
    private var parms = HashMap<String, String?>()
    private lateinit var mContext: Context
    private var inputManager: InputMethodManager? = null
    private lateinit var discoverNearBy: DiscoverNearBy
    private var type: String = ""
    private lateinit var frame: FrameLayout
    private lateinit var mContentView: View
    private var userDTO: UserDTO? = null
    private lateinit var mHandler: Handler
    private var mylocation: Location? = null
    private var googleApiClient: GoogleApiClient? = null
    private lateinit var img_profile: CircleImageView
    private lateinit var tvName: CustomTextViewBold
    private lateinit var tvEmail: CustomTextView
    private lateinit var tvOther: CustomTextView
    private lateinit var tvEnglish: CustomTextView
    private lateinit var llProfileClick: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        mContext = this@BaseActivity
        mHandler = Handler()
        inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        prefrence = getInstance(mContext)!!
        userDTO = prefrence.getParentUser(Const.USER_DTO)
        firebaseAnalytics = FirebaseAnalytics.getInstance(mContext)
        firebaseAnalytics.setUserId("${userDTO?.mobile}")
        if (intent.hasExtra(Const.SCREEN_TAG)) {
            type = intent?.getStringExtra(Const.SCREEN_TAG).toString()
        }
        setUpGClient()
        header = findViewById(R.id.header)
        frame = findViewById(R.id.frame)
        drawer = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        mContentView = findViewById(R.id.content)
        headerNameTV = findViewById(R.id.headerNameTV)
        menuLeftIV = findViewById(R.id.menuLeftIV)
        ivFilter = findViewById(R.id.ivFilter)
        navHeader = navigationView.getHeaderView(0)
        img_profile = navHeader.findViewById(R.id.img_profile)
        tvName = navHeader.findViewById(R.id.tvName)
        tvEmail = navHeader.findViewById(R.id.tvEmail)
        tvEnglish = navHeader.findViewById(R.id.tvEnglish)
        tvOther = navHeader.findViewById(R.id.tvOther)
        tvOther = navHeader.findViewById(R.id.tvOther)
        llProfileClick = navHeader.findViewById(R.id.llProfileClick)

        llProfileClick.setOnClickListener {
            ivFilter.visibility = View.GONE
            header.visibility = View.GONE
            navItemIndex = 10
            CURRENT_TAG = TAG_PROFILE_SETTINGS
            val fragmentTransaction = this@BaseActivity.supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
            fragmentTransaction.replace(R.id.frame, ProfileSettingActivity())
            fragmentTransaction.commitAllowingStateLoss()
            drawer.closeDrawers()
        }

        tvEnglish.setOnClickListener { language("en") }
        tvOther.setOnClickListener { language("ar") }

        val uri = formatImageUri(userDTO?.image)

        GlideApp.with(mContext)
            .load(uri)
            .placeholder(R.drawable.dummyuser_image)
            //.useAnimationPool(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(img_profile)

        tvEmail.text = userDTO?.email_id
        tvName.text = userDTO?.name

        if (savedInstanceState == null) {
            when {
                type.equals(Const.CHAT_NOTIFICATION, ignoreCase = true) -> {
                    header.visibility = View.VISIBLE
                    navItemIndex = 2
                    CURRENT_TAG = TAG_CHAT
                    loadHomeFragment(ChatList(), CURRENT_TAG)
                }
                type.equals(Const.TICKET_COMMENT_NOTIFICATION, ignoreCase = true) -> {
                    header.visibility = View.VISIBLE
                    navItemIndex = 11
                    CURRENT_TAG = TAG_TICKETS
                    loadHomeFragment(Tickets(), CURRENT_TAG)
                }
                type.equals(Const.TICKET_STATUS_NOTIFICATION, ignoreCase = true) -> {
                    header.visibility = View.VISIBLE
                    navItemIndex = 11
                    CURRENT_TAG = TAG_TICKETS
                    loadHomeFragment(Tickets(), CURRENT_TAG)
                }
                type.equals(Const.WALLET_NOTIFICATION, ignoreCase = true) -> {
                    header.visibility = View.VISIBLE
                    navItemIndex = 9
                    CURRENT_TAG = TAG_WALLET
                    loadHomeFragment(Wallet(), CURRENT_TAG)
                }
                type.equals(Const.DECLINE_BOOKING_ARTIST_NOTIFICATION,
                    ignoreCase = true) -> {
                    header.visibility = View.VISIBLE
                    navItemIndex = 3
                    CURRENT_TAG = TAG_BOOKING
                    loadHomeFragment(MyBooking(), CURRENT_TAG)
                }
                type.equals(Const.START_BOOKING_ARTIST_NOTIFICATION,
                    ignoreCase = true) -> {
                    header.visibility = View.VISIBLE
                    navItemIndex = 3
                    CURRENT_TAG = TAG_BOOKING
                    loadHomeFragment(MyBooking(), CURRENT_TAG)
                }
                type.equals(Const.END_BOOKING_ARTIST_NOTIFICATION, ignoreCase = true) -> {
                    header.visibility = View.VISIBLE
                    navItemIndex = 3
                    CURRENT_TAG = TAG_BOOKING
                    loadHomeFragment(MyBooking(), CURRENT_TAG)
                }
                type.equals(Const.ACCEPT_BOOKING_ARTIST_NOTIFICATION,
                    ignoreCase = true) -> {
                    header.visibility = View.VISIBLE
                    navItemIndex = 3
                    CURRENT_TAG = TAG_BOOKING
                    loadHomeFragment(MyBooking(), CURRENT_TAG)
                }
                type.equals(Const.JOB_APPLY_NOTIFICATION, ignoreCase = true) -> {
                    header.visibility = View.VISIBLE
                    navItemIndex = 4
                    CURRENT_TAG = TAG_JOBS
                    loadHomeFragment(Jobs(), CURRENT_TAG)
                }
                type.equals(Const.BRODCAST_NOTIFICATION, ignoreCase = true) -> {
                    header.visibility = View.VISIBLE
                    navItemIndex = 6
                    CURRENT_TAG = TAG_NOTIFICATION
                    loadHomeFragment(NotificationActivity(), CURRENT_TAG)
                }
                type.equals(Const.ADMIN_NOTIFICATION, ignoreCase = true) -> {
                    header.visibility = View.VISIBLE
                    navItemIndex = 6
                    CURRENT_TAG = TAG_NOTIFICATION
                    loadHomeFragment(NotificationActivity(), CURRENT_TAG)
                }
                else -> {
                    header.visibility = View.GONE
                    navItemIndex = 0
                    CURRENT_TAG = TAG_HOME
                    loadHomeFragment(Home(), CURRENT_TAG)
                }
            }
        }

        menuLeftIV.setOnClickListener { drawerOpen() }
        setUpNavigationView()

        val menu = navigationView.menu
        changeColorItem(menu, R.id.nav_home_features)
        changeColorItem(menu, R.id.nav_bookings_and_job)
        changeColorItem(menu, R.id.nav_personal)
        changeColorItem(menu, R.id.other)
        for (i in 0 until menu.size()) {
            val mi = menu.getItem(i)
            val subMenu = mi.subMenu
            if (subMenu != null && subMenu.size() > 0) {
                for (j in 0 until subMenu.size()) {
                    val subMenuItem = subMenu.getItem(j)
                    applyCustomFont(subMenuItem)
                }
            }
            applyCustomFont(mi)
        }
        drawer.setScrimColor(Color.TRANSPARENT)
        drawer.addDrawerListener(object : SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                // Scale the View based on current slide offset
                val diffScaledOffset = slideOffset * (1 - END_SCALE)
                val offsetScale = 1 - diffScaledOffset
                mContentView.scaleX = offsetScale
                mContentView.scaleY = offsetScale

                // Translate the View, accounting for the scaled width
                val xOffset = drawerView.width * slideOffset
                val xOffsetDiff = mContentView.width * diffScaledOffset / 2
                val xTranslation = xOffset - xOffsetDiff
                mContentView.translationX = xTranslation
            }

            override fun onDrawerClosed(drawerView: View) {}
        }
        )
    }

    private fun changeColorItem(menu: Menu, id: Int) {
        val tools = menu.findItem(id)
        val s = SpannableString(tools.title)
        s.setSpan(TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length, 0)
        tools.title = s
    }

    private fun applyCustomFont(mi: MenuItem) {
        val customFont = FontCache.getTypeface("Poppins-Regular.otf", this@BaseActivity)
        val spannableString = SpannableString(mi.title)
        spannableString.setSpan(CustomTypeFaceSpan("", customFont),
            0,
            spannableString.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = spannableString
    }

    fun showImage() {
        userDTO = prefrence.getParentUser(Const.USER_DTO)

        val uri = formatImageUri(userDTO?.image)

        GlideApp.with(mContext)
            .load(uri)
            .placeholder(R.drawable.dummyuser_image)
            //.useAnimationPool(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(img_profile)

        tvName.text = userDTO?.name
    }

    fun loadHomeFragment(fragment: Fragment?, TAG: String?) {
        val mPendingRunnable = Runnable {
            val fragmentTransaction = this@BaseActivity.supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
            fragmentTransaction.replace(R.id.frame, fragment!!, TAG)
            fragmentTransaction.commitAllowingStateLoss()
            ivFilter.visibility = View.VISIBLE
        }
        mHandler.post(mPendingRunnable)
        drawer.closeDrawers()
        invalidateOptionsMenu()
    }

    private fun drawerOpen() {
        try {
            inputManager?.hideSoftInputFromWindow(currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    private fun setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            val fragmentTransaction = this@BaseActivity.supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    ivFilter.visibility = View.GONE
                    header.visibility = View.GONE
                    navItemIndex = 0
                    CURRENT_TAG = TAG_HOME
                    fragmentTransaction.replace(R.id.frame, Home())
                }
                R.id.nav_discover_jobs -> {
                    ivFilter.visibility = View.VISIBLE
                    header.visibility = View.VISIBLE
                    navItemIndex = 1
                    CURRENT_TAG = TAG_MAIN
                    fragmentTransaction.replace(R.id.frame, DiscoverNearBy())
                }
                R.id.nav_chat -> {
                    ivFilter.visibility = View.GONE
                    header.visibility = View.VISIBLE
                    navItemIndex = 2
                    CURRENT_TAG = TAG_CHAT
                    fragmentTransaction.replace(R.id.frame, ChatList())
                }
                R.id.nav_booking -> {
                    ivFilter.visibility = View.GONE
                    header.visibility = View.VISIBLE
                    navItemIndex = 3
                    CURRENT_TAG = TAG_BOOKING
                    fragmentTransaction.replace(R.id.frame, MyBooking())
                }
                R.id.nav_jobs -> {
                    ivFilter.visibility = View.GONE
                    header.visibility = View.VISIBLE
                    navItemIndex = 4
                    CURRENT_TAG = TAG_JOBS
                    fragmentTransaction.replace(R.id.frame, Jobs())
                }
                R.id.nav_appointment -> {
                    ivFilter.visibility = View.GONE
                    header.visibility = View.VISIBLE
                    navItemIndex = 5
                    CURRENT_TAG = TAG_APPOINTMENT
                    fragmentTransaction.replace(R.id.frame, AppointmentFrag())
                }
                R.id.nav_setting -> {
                    ivFilter.visibility = View.GONE
                    header.visibility = View.VISIBLE
                    navItemIndex = 6
                    CURRENT_TAG = TAG_SETTING
                    fragmentTransaction.replace(R.id.frame, Setting())
                }
                R.id.nav_notification -> {
                    ivFilter.visibility = View.GONE
                    header.visibility = View.VISIBLE
                    navItemIndex = 7
                    CURRENT_TAG = TAG_NOTIFICATION
                    fragmentTransaction.replace(R.id.frame, NotificationActivity())
                }
                R.id.nav_history -> {
                    ivFilter.visibility = View.GONE
                    header.visibility = View.VISIBLE
                    navItemIndex = 8
                    CURRENT_TAG = TAG_DISCOUNT
                    fragmentTransaction.replace(R.id.frame, HistoryFragment())
                }
                R.id.nav_discount -> {
                    ivFilter.visibility = View.GONE
                    header.visibility = View.VISIBLE
                    navItemIndex = 9
                    CURRENT_TAG = TAG_HISTORY
                    fragmentTransaction.replace(R.id.frame, GetDiscountActivity())
                }
                R.id.nav_wallet -> {
                    ivFilter.visibility = View.GONE
                    header.visibility = View.VISIBLE
                    navItemIndex = 10
                    CURRENT_TAG = TAG_WALLET
                    fragmentTransaction.replace(R.id.frame, Wallet())
                }
                R.id.nav_profilesetting -> {
                    navItemIndex = 11
                    CURRENT_TAG = TAG_PROFILE_SETTINGS
                    ivFilter.visibility = View.GONE
                    header.visibility = View.GONE
                    fragmentTransaction.replace(R.id.frame, ProfileSettingActivity())
                }
                R.id.nav_tickets -> {
                    navItemIndex = 12
                    CURRENT_TAG = TAG_TICKETS
                    ivFilter.visibility = View.GONE
                    header.visibility = View.VISIBLE
                    fragmentTransaction.replace(R.id.frame, Tickets())
                }
                R.id.nav_logout -> confirmLogout()
                else -> {
                    ivFilter.visibility = View.VISIBLE
                    header.visibility = View.GONE
                    navItemIndex = 0
                    CURRENT_TAG = TAG_HOME
                    fragmentTransaction.replace(R.id.frame, Home())
                }
            }
            fragmentTransaction.commitAllowingStateLoss()
            drawer.closeDrawers()
            menuItem.isChecked = !menuItem.isChecked
            menuItem.isChecked = true
            true
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers()
            return
        }
        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                header.visibility = View.GONE
                navItemIndex = 0
                CURRENT_TAG = TAG_HOME
                loadHomeFragment(Home(), CURRENT_TAG)
                return
            }
        }

        clickDone()
    }

    private fun clickDone() {
        AlertDialog.Builder(this)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle(resources.getString(R.string.app_name))
            .setMessage(resources.getString(R.string.closeMsg))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
                val i = Intent()
                i.action = Intent.ACTION_MAIN
                i.addCategory(Intent.CATEGORY_HOME)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                this@BaseActivity.startActivity(i)
                finish()
            }
            .setNegativeButton(resources.getString(R.string.no)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .show()
    }// Ignore the error.// Show the dialog by calling startResolutionForResult(),

    // and check the result in onActivityResult().
    // Ask to turn on GPS automatically
// Location settings are not satisfied.
    // But could be fixed by showing the fabcustomer a dialog.
    // All location settings are satisfied.
    // You can initialize location requests here.
    private val myLocation: Unit
        get() {
            if (googleApiClient != null && googleApiClient!!.isConnected) {
                val permissionLocation = ContextCompat.checkSelfPermission(this@BaseActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation =
                        LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
                    val locationRequest = LocationRequest()
                    locationRequest.interval = 3000
                    locationRequest.fastestInterval = 3000
                    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    val builder = LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)
                    builder.setAlwaysShow(true)
                    LocationServices.FusedLocationApi
                        .requestLocationUpdates(googleApiClient, locationRequest, this)
                    val result: PendingResult<*> = LocationServices.SettingsApi
                        .checkLocationSettings(googleApiClient, builder.build())
                    result.setResultCallback { result1: Result ->
                        val status = result1.status
                        when (status.statusCode) {
                            LocationSettingsStatusCodes.SUCCESS -> {
                                // All location settings are satisfied.
                                // You can initialize location requests here.
                                val permissionLocation1 = ContextCompat
                                    .checkSelfPermission(this@BaseActivity,
                                        Manifest.permission.ACCESS_FINE_LOCATION)
                                if (permissionLocation1 == PackageManager.PERMISSION_GRANTED) {
                                    mylocation = LocationServices.FusedLocationApi
                                        .getLastLocation(googleApiClient)
                                }
                            }
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                                 // Location settings are not satisfied.
                                // But could be fixed by showing the fabcustomer a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    // Ask to turn on GPS automatically
                                    status.startResolutionForResult(this@BaseActivity,
                                        REQUEST_CHECK_SETTINGS_GPS)
                                } catch (e: SendIntentException) {
                                    // Ignore the error.
                                }
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            }
                        }
                    }
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS_GPS) {
            when (resultCode) {
                RESULT_OK -> myLocation
                RESULT_CANCELED -> {
                }
            }
        }
    }

    override fun onConnected(bundle: Bundle?) {
        checkPermissions()
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onLocationChanged(location: Location) {
        mylocation = location
        if (mylocation != null) {
            val latitude = mylocation?.latitude
            val longitude = mylocation?.longitude
            prefrence.setValue(Const.LATITUDE, latitude.toString() + "")
            prefrence.setValue(Const.LONGITUDE, longitude.toString() + "")
            parms[Const.USER_ID] = userDTO?.user_id
            parms[Const.ROLE] = "2"
            parms[Const.LATITUDE] = latitude.toString() + ""
            parms[Const.LONGITUDE] = longitude.toString() + ""
            updateLocation()
        }
    }

    private fun checkPermissions() {
        val permissionLocation = ContextCompat.checkSelfPermission(this@BaseActivity,
            Manifest.permission.ACCESS_FINE_LOCATION)
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this@BaseActivity,
                    listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            }
        } else {
            myLocation
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionLocation = ContextCompat.checkSelfPermission(this@BaseActivity,
            Manifest.permission.ACCESS_FINE_LOCATION)
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            myLocation
        }
    }

    @Synchronized
    private fun setUpGClient() {
        googleApiClient = GoogleApiClient.Builder(this@BaseActivity)
            .enableAutoManage(this@BaseActivity, 0, this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        googleApiClient?.connect()
    }

    private fun updateLocation() {
        // ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        log("About to update location data on server")
        log("User id ==> ${userDTO?.toString()}")

        HttpsRequest(Const.UPDATE_LOCATION_API, parms, mContext).stringPost(Companion.TAG,
            object : Helper {
                override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                    when {
                        !flag -> showToast(mContext, msg)
                    }
                }
            })
    }

    fun language(language: String?) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        this@BaseActivity.resources.updateConfiguration(config,
            this@BaseActivity.resources.displayMetrics)
        val i = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
    }

    fun confirmLogout() {
        try {
            AlertDialog.Builder(mContext)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(resources.getString(R.string.app_name))
                .setMessage(resources.getString(R.string.logout_msg))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, which: Int ->
                    dialog.dismiss()
                    prefrence.clearAllPreferences()
                    val intent = Intent(mContext, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    this@BaseActivity.startActivity(intent)
                    (mContext as BaseActivity).finish()
                }
                .setNegativeButton(resources.getString(R.string.no)) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val TAG_MAIN = "main"
        const val TAG_CHAT = "chat"
        const val TAG_HOME = "home"
        const val TAG_BOOKING = "booking"
        const val TAG_NOTIFICATION = "notification"
        const val TAG_SETTING = "setting"
        const val TAG_DISCOUNT = "discount"
        const val TAG_HISTORY = "history"
        const val TAG_PROFILE_SETTINGS = "profile_settings"
        const val TAG_TICKETS = "tickets"
        const val TAG_LOG_OUT = "signOut"
        const val TAG_APPOINTMENT = "appointment"
        const val TAG_JOBS = "jobs"
        const val TAG_WALLET = "wallet"
        private const val END_SCALE = 0.8f
        private const val REQUEST_CHECK_SETTINGS_GPS = 0x1
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2

        @JvmField
        var CURRENT_TAG = TAG_MAIN

        @JvmField
        var navItemIndex = 0
        private val TAG = BaseActivity::class.java.simpleName
    }
}