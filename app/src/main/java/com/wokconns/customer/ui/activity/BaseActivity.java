package com.wokconns.customer.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.navigation.NavigationView;
import com.wokconns.customer.R;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.fragment.AppointmentFrag;
import com.wokconns.customer.ui.fragment.ChatList;
import com.wokconns.customer.ui.fragment.DiscoverNearBy;
import com.wokconns.customer.ui.fragment.GetDiscountActivity;
import com.wokconns.customer.ui.fragment.HistoryFragment;
import com.wokconns.customer.ui.fragment.Home;
import com.wokconns.customer.ui.fragment.Jobs;
import com.wokconns.customer.ui.fragment.MyBooking;
import com.wokconns.customer.ui.fragment.NotificationActivity;
import com.wokconns.customer.ui.fragment.ProfileSettingActivity;
import com.wokconns.customer.ui.fragment.Setting;
import com.wokconns.customer.ui.fragment.Tickets;
import com.wokconns.customer.ui.fragment.Wallet;
import com.wokconns.customer.utils.CustomTextView;
import com.wokconns.customer.utils.CustomTextViewBold;
import com.wokconns.customer.utils.CustomTypeFaceSpan;
import com.wokconns.customer.utils.FontCache;
import com.wokconns.customer.utils.ProjectUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final String TAG_MAIN = "main";
    public static final String TAG_CHAT = "chat";
    public static final String TAG_HOME = "home";
    public static final String TAG_BOOKING = "booking";
    public static final String TAG_NOTIFICATION = "notification";
    public static final String TAG_SETTING = "setting";
    public static final String TAG_DISCOUNT = "discount";
    public static final String TAG_HISTORY = "history";
    public static final String TAG_PROFILE_SETINGS = "profile_settings";
    public static final String TAG_TICKETS = "tickets";
    public static final String TAG_LOG_OUT = "signOut";
    public static final String TAG_APPOINTMENT = "appointment";
    public static final String TAG_JOBS = "jobs";
    public static final String TAG_WALLET = "wallet";
    private static final float END_SCALE = 0.8f;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    public static String CURRENT_TAG = TAG_MAIN;
    public static int navItemIndex = 0;
    private final String TAG = BaseActivity.class.getSimpleName();
    private final boolean shouldLoadHomeFragOnBackPress = true;
    public NavigationView navigationView;
    public RelativeLayout header;
    public DrawerLayout drawer;
    public View navHeader;
    public ImageView menuLeftIV, ivFilter;
    public SharedPrefrence prefrence;
    public CustomTextViewBold headerNameTV;
    HashMap<String, String> parms = new HashMap<>();
    Context mContext;
    InputMethodManager inputManager;
    DiscoverNearBy discoverNearBy = null;
    String type = "";
    private FrameLayout frame;
    private View contentView;
    private UserDTO userDTO;
    private Handler mHandler;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private CircleImageView img_profile;
    private CustomTextViewBold tvName;
    private CustomTextView tvEmail, tvOther, tvEnglish;
    private LinearLayout llProfileClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mContext = BaseActivity.this;
        mHandler = new Handler();
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Const.USER_DTO);

        if (getIntent().hasExtra(Const.SCREEN_TAG)) {
            type = getIntent().getStringExtra(Const.SCREEN_TAG);
        }


        setUpGClient();

        header = findViewById(R.id.header);
        frame = findViewById(R.id.frame);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        contentView = findViewById(R.id.content);
        headerNameTV = findViewById(R.id.headerNameTV);
        menuLeftIV = findViewById(R.id.menuLeftIV);
        ivFilter = findViewById(R.id.ivFilter);


        navHeader = navigationView.getHeaderView(0);
        img_profile = navHeader.findViewById(R.id.img_profile);
        tvName = navHeader.findViewById(R.id.tvName);
        tvEmail = navHeader.findViewById(R.id.tvEmail);

        tvEnglish = navHeader.findViewById(R.id.tvEnglish);
        tvOther = navHeader.findViewById(R.id.tvOther);
        tvOther = navHeader.findViewById(R.id.tvOther);
        llProfileClick = navHeader.findViewById(R.id.llProfileClick);


        llProfileClick.setOnClickListener(v -> {
            ivFilter.setVisibility(View.GONE);
            header.setVisibility(View.GONE);
            navItemIndex = 10;
            CURRENT_TAG = TAG_PROFILE_SETINGS;
            FragmentTransaction fragmentTransaction = BaseActivity.this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, new ProfileSettingActivity());
            fragmentTransaction.commitAllowingStateLoss();
            drawer.closeDrawers();
        });

        tvEnglish.setOnClickListener(v -> BaseActivity.this.language("en"));
        tvOther.setOnClickListener(v -> BaseActivity.this.language("ar"));
        Glide.with(mContext).
                load(userDTO.getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_profile);
        tvEmail.setText(userDTO.getEmail_id());
        tvName.setText(userDTO.getName());

        if (savedInstanceState == null) {
            if (type != null) {
                if (type.equalsIgnoreCase(Const.CHAT_NOTIFICATION)) {
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_CHAT;
                    loadHomeFragment(new ChatList(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Const.TICKET_COMMENT_NOTIFICATION)) {
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 11;
                    CURRENT_TAG = TAG_TICKETS;
                    loadHomeFragment(new Tickets(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Const.TICKET_STATUS_NOTIFICATION)) {
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 11;
                    CURRENT_TAG = TAG_TICKETS;
                    loadHomeFragment(new Tickets(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Const.WALLET_NOTIFICATION)) {
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 9;
                    CURRENT_TAG = TAG_WALLET;
                    loadHomeFragment(new Wallet(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Const.DECLINE_BOOKING_ARTIST_NOTIFICATION)) {
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_BOOKING;
                    loadHomeFragment(new MyBooking(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Const.START_BOOKING_ARTIST_NOTIFICATION)) {
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_BOOKING;
                    loadHomeFragment(new MyBooking(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Const.END_BOOKING_ARTIST_NOTIFICATION)) {
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_BOOKING;
                    loadHomeFragment(new MyBooking(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Const.ACCEPT_BOOKING_ARTIST_NOTIFICATION)) {
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_BOOKING;
                    loadHomeFragment(new MyBooking(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Const.JOB_APPLY_NOTIFICATION)) {
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 4;
                    CURRENT_TAG = TAG_JOBS;
                    loadHomeFragment(new Jobs(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Const.BRODCAST_NOTIFICATION)) {
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 6;
                    CURRENT_TAG = TAG_NOTIFICATION;
                    loadHomeFragment(new NotificationActivity(), CURRENT_TAG);
                } else if (type.equalsIgnoreCase(Const.ADMIN_NOTIFICATION)) {
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 6;
                    CURRENT_TAG = TAG_NOTIFICATION;
                    loadHomeFragment(new NotificationActivity(), CURRENT_TAG);
                } else {
                    header.setVisibility(View.GONE);
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    loadHomeFragment(new Home(), CURRENT_TAG);
                }
            } else {
                header.setVisibility(View.GONE);
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment(new Home(), CURRENT_TAG);
            }


        }
        menuLeftIV.setOnClickListener(v -> BaseActivity.this.drawerOpen());

        setUpNavigationView();
        Menu menu = navigationView.getMenu();

        changeColorItem(menu, R.id.nav_home_features);
        changeColorItem(menu, R.id.nav_bookings_and_job);
        changeColorItem(menu, R.id.nav_personal);
        changeColorItem(menu, R.id.other);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyCustomFont(subMenuItem);
                }
            }
            applyCustomFont(mi);
        }


        drawer.setScrimColor(Color.TRANSPARENT);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                                     @Override
                                     public void onDrawerSlide(View drawerView, float slideOffset) {

                                         // Scale the View based on current slide offset
                                         final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                                         final float offsetScale = 1 - diffScaledOffset;
                                         contentView.setScaleX(offsetScale);
                                         contentView.setScaleY(offsetScale);

                                         // Translate the View, accounting for the scaled width
                                         final float xOffset = drawerView.getWidth() * slideOffset;
                                         final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                                         final float xTranslation = xOffset - xOffsetDiff;
                                         contentView.setTranslationX(xTranslation);
                                     }

                                     @Override
                                     public void onDrawerClosed(View drawerView) {
                                     }
                                 }
        );


    }

    public void changeColorItem(Menu menu, int id) {
        MenuItem tools = menu.findItem(id);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        tools.setTitle(s);

    }

    public void applyCustomFont(MenuItem mi) {
        Typeface customFont = FontCache.getTypeface("Poppins-Regular.otf", BaseActivity.this);
        SpannableString spannableString = new SpannableString(mi.getTitle());
        spannableString.setSpan(new CustomTypeFaceSpan("", customFont), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(spannableString);
    }

    public void showImage() {
        userDTO = prefrence.getParentUser(Const.USER_DTO);

        Glide.with(mContext).
                load(userDTO.getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(img_profile);

        tvName.setText(userDTO.getName());
    }

    public void loadHomeFragment(final Fragment fragment, final String TAG) {

        Runnable mPendingRunnable = () -> {
            FragmentTransaction fragmentTransaction = BaseActivity.this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, fragment, TAG);
            fragmentTransaction.commitAllowingStateLoss();
            ivFilter.setVisibility(View.VISIBLE);

        };

        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        drawer.closeDrawers();

        invalidateOptionsMenu();
    }


    public void drawerOpen() {

        try {
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            FragmentTransaction fragmentTransaction = BaseActivity.this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out);

            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.GONE);
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    fragmentTransaction.replace(R.id.frame, new Home());
                    break;
                case R.id.nav_discover_jobs:
                    ivFilter.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 1;
                    CURRENT_TAG = TAG_MAIN;
                    fragmentTransaction.replace(R.id.frame, new DiscoverNearBy());
                    break;
                case R.id.nav_chat:
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_CHAT;
                    fragmentTransaction.replace(R.id.frame, new ChatList());
                    break;
                case R.id.nav_booking:
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_BOOKING;
                    fragmentTransaction.replace(R.id.frame, new MyBooking());
                    break;
                case R.id.nav_jobs:
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 4;
                    CURRENT_TAG = TAG_JOBS;
                    fragmentTransaction.replace(R.id.frame, new Jobs());
                    break;
                case R.id.nav_appointment:
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 5;
                    CURRENT_TAG = TAG_APPOINTMENT;
                    fragmentTransaction.replace(R.id.frame, new AppointmentFrag());
                    break;
                case R.id.nav_setting:
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 6;
                    CURRENT_TAG = TAG_SETTING;
                    fragmentTransaction.replace(R.id.frame, new Setting());
                    break;
                case R.id.nav_notification:
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 7;
                    CURRENT_TAG = TAG_NOTIFICATION;
                    fragmentTransaction.replace(R.id.frame, new NotificationActivity());
                    break;
                case R.id.nav_history:
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 8;
                    CURRENT_TAG = TAG_DISCOUNT;
                    fragmentTransaction.replace(R.id.frame, new HistoryFragment());
                    break;
                case R.id.nav_discount:
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 9;
                    CURRENT_TAG = TAG_HISTORY;
                    fragmentTransaction.replace(R.id.frame, new GetDiscountActivity());
                    break;
                case R.id.nav_wallet:
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    navItemIndex = 10;
                    CURRENT_TAG = TAG_WALLET;
                    fragmentTransaction.replace(R.id.frame, new Wallet());
                    break;
                case R.id.nav_profilesetting:
                    navItemIndex = 11;
                    CURRENT_TAG = TAG_PROFILE_SETINGS;
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.GONE);
                    fragmentTransaction.replace(R.id.frame, new ProfileSettingActivity());
                    break;
                case R.id.nav_tickets:
                    navItemIndex = 12;
                    CURRENT_TAG = TAG_TICKETS;
                    ivFilter.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    fragmentTransaction.replace(R.id.frame, new Tickets());
                    break;
                case R.id.nav_logout:
                    BaseActivity.this.confirmLogout();
                    break;
                default:
                    ivFilter.setVisibility(View.VISIBLE);
                    header.setVisibility(View.GONE);
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    fragmentTransaction.replace(R.id.frame, new Home());
                    break;

            }
            fragmentTransaction.commitAllowingStateLoss();
            drawer.closeDrawers();

            menuItem.setChecked(!menuItem.isChecked());
            menuItem.setChecked(true);

            //   loadHomeFragment();

            return true;
        });

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        if (shouldLoadHomeFragOnBackPress) {

            if (navItemIndex != 0) {
                header.setVisibility(View.GONE);
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment(new Home(), CURRENT_TAG);
                return;
            }
        }

        //super.onBackPressed();
        clickDone();
    }

    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.closeMsg))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    dialog.dismiss();
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_HOME);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    BaseActivity.this.startActivity(i);

                    BaseActivity.this.finish();
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(BaseActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(result1 -> {
                        final Status status = result1.getStatus();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied.
                                // You can initialize location requests here.
                                int permissionLocation1 = ContextCompat
                                        .checkSelfPermission(BaseActivity.this,
                                                Manifest.permission.ACCESS_FINE_LOCATION);
                                if (permissionLocation1 == PackageManager.PERMISSION_GRANTED) {
                                    mylocation = LocationServices.FusedLocationApi
                                            .getLastLocation(googleApiClient);

                                }
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied.
                                // But could be fixed by showing the fabcustomer a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    // Ask to turn on GPS automatically
                                    status.startResolutionForResult(BaseActivity.this,
                                            REQUEST_CHECK_SETTINGS_GPS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the dialog.
                                //finish();
                                break;
                        }
                    });
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS_GPS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getMyLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    break;
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            Double latitude = mylocation.getLatitude();
            Double longitude = mylocation.getLongitude();
            prefrence.setValue(Const.LATITUDE, latitude + "");
            prefrence.setValue(Const.LONGITUDE, longitude + "");

            parms.put(Const.USER_ID, userDTO.getUser_id());
            parms.put(Const.ROLE, "2");
            parms.put(Const.LATITUDE, latitude + "");
            parms.put(Const.LONGITUDE, longitude + "");
            updateLocation();
        }
    }


    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(BaseActivity.this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionLocation = ContextCompat.checkSelfPermission(BaseActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(BaseActivity.this)
                .enableAutoManage(BaseActivity.this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    public void updateLocation() {
        // ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Const.UPDATE_LOCATION_API, parms, mContext).stringPost(TAG, (flag, msg, response) -> {
            if (flag) {

            } else {
                ProjectUtils.showToast(mContext, msg);

            }
        });
    }

    public void language(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.locale = locale;

        BaseActivity.this.getResources().updateConfiguration(config,
                BaseActivity.this.getResources().getDisplayMetrics());

        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(i);

    }

    public void confirmLogout() {
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage(getResources().getString(R.string.logout_msg))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                        dialog.dismiss();
                        prefrence.clearAllPreferences();
                        Intent intent = new Intent(mContext, SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        BaseActivity.this.startActivity(intent);
                        ((BaseActivity) mContext).finish();
                    })
                    .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
