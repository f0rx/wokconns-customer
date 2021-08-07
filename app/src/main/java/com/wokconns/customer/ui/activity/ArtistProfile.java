package com.wokconns.customer.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivityArtistProfileBinding;
import com.wokconns.customer.dto.ArtistDetailsDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.fragment.ImageGallery;
import com.wokconns.customer.ui.fragment.PersnoalInfo;
import com.wokconns.customer.ui.fragment.PreviousWork;
import com.wokconns.customer.ui.fragment.Reviews;
import com.wokconns.customer.utils.ProjectUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ArtistProfile extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {
    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    public static String name = "", email = "";
    SimpleDateFormat sdf1, timeZone;
    int flag = 0;
    private String TAG = ArtistProfile.class.getSimpleName();
    private Context mContext;
    private String artist_id = "";
    private ArtistDetailsDTO artistDetailsDTO;
    private HashMap<String, String> parms = new HashMap<>();
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private HashMap<String, String> paramsFav = new HashMap<>();
    private ArrayList<String> list;
    private PersnoalInfo persnoalInfo = new PersnoalInfo();
    private ImageGallery imageGallery = new ImageGallery();
    private PreviousWork previousWork = new PreviousWork();
    private Reviews reviews = new Reviews();
    private Bundle bundle;
    private boolean mIsAvatarShown = true;
    private int mMaxScrollSize;
    private DialogInterface dialog_book;
    ;
    private HashMap<String, String> paramsBookingOp = new HashMap<>();
    private Date date;
    private HashMap<String, String> paramBookAppointment = new HashMap<>();
    private ActivityArtistProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProjectUtils.Fullscreen(ArtistProfile.this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_artist_profile);
        mContext = ArtistProfile.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        sdf1 = new SimpleDateFormat(Const.DATE_FORMATE_SERVER, Locale.ENGLISH);
        timeZone = new SimpleDateFormat(Const.DATE_FORMATE_TIMEZONE, Locale.ENGLISH);
        date = new Date();
        userDTO = prefrence.getParentUser(Const.USER_DTO);
        if (getIntent().hasExtra(Const.ARTIST_ID)) {
            if (getIntent().hasExtra(Const.FLAG)) {
                flag = getIntent().getIntExtra(Const.FLAG, 0);
            }
            artist_id = getIntent().getStringExtra(Const.ARTIST_ID);

        }
        parms.put(Const.ARTIST_ID, artist_id);
        parms.put(Const.USER_ID, userDTO.getUser_id());
        paramsFav.put(Const.ARTIST_ID, artist_id);
        paramsFav.put(Const.USER_ID, userDTO.getUser_id());
        setUiAction();
    }

    public void setUiAction() {
        binding.llBack.setOnClickListener(this);
//        binding.tvChat.setOnClickListener(this);

        binding.tvAppointment.setOnClickListener(this);
        binding.tvBookNow.setOnClickListener(this);
        binding.ivFav.setOnClickListener(this);
        binding.tvViewServices.setOnClickListener(this);


        if (NetworkManager.isConnectToInternet(mContext)) {
            getArtist();

        } else {
            ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_connection));
        }

        binding.appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = binding.appbarLayout.getTotalScrollRange();

        if (flag == 1) {
            binding.llBottom.setVisibility(View.GONE);
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBookNow:
                if (NetworkManager.isConnectToInternet(mContext)) {
                    if (artistDetailsDTO != null) {
                        Intent viewService = new Intent(mContext, Booking.class);
                        viewService.putExtra(Const.ARTIST_DTO, artistDetailsDTO);
                        viewService.putExtra(Const.ARTIST_ID, artist_id);
                        viewService.putExtra(Const.SCREEN_TAG, 1);
                        mContext.startActivity(viewService);
                    } else {
                        ProjectUtils.showLong(mContext, getResources().getString(R.string.no_data_found));
                    }
                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_connection));
                }

                break;
            case R.id.tvAppointment:
                if (NetworkManager.isConnectToInternet(mContext)) {
                    paramBookAppointment.put(Const.USER_ID, userDTO.getUser_id());
                    paramBookAppointment.put(Const.ARTIST_ID, artistDetailsDTO.getUser_id());
                    clickScheduleDateTime();
                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_connection));
                }


                break;
            case R.id.llBack:
                finish();
                break;
            case R.id.ivFav:
                if (NetworkManager.isConnectToInternet(mContext)) {
                    if (artistDetailsDTO.getFav_status().equalsIgnoreCase("1")) {
                        removeFav();
                    } else {
                        addFav();
                    }

                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_connection));
                }


                break;
            case R.id.tvChat:
                if (NetworkManager.isConnectToInternet(mContext)) {
                    Intent in = new Intent(mContext, OneTwoOneChat.class);
                    in.putExtra(Const.ARTIST_ID, artistDetailsDTO.getUser_id());
                    in.putExtra(Const.ARTIST_NAME, artistDetailsDTO.getName());
                    mContext.startActivity(in);
                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_connection));
                }

                break;
            case R.id.tvViewServices:
                if (NetworkManager.isConnectToInternet(mContext)) {
                    if (artistDetailsDTO != null) {
                        Intent viewService = new Intent(mContext, ViewServices.class);
                        viewService.putExtra(Const.ARTIST_DTO, artistDetailsDTO);
                        viewService.putExtra(Const.ARTIST_ID, artist_id);
                        mContext.startActivity(viewService);
                    } else {
                        ProjectUtils.showLong(mContext, getResources().getString(R.string.no_services_found));
                    }
                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_connection));
                }

                break;

        }
    }

    public void getArtist() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Const.GET_ARTIST_BY_ID_API, parms, mContext).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            if (flag) {
                try {
                    artistDetailsDTO = new Gson().fromJson(response.getJSONObject("data").toString(), ArtistDetailsDTO.class);
                    ArtistProfile.this.showData();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void showData() {
        bundle = new Bundle();
        bundle.putSerializable(Const.ARTIST_DTO, artistDetailsDTO);

        binding.tvName.setText(artistDetailsDTO.getName());
        binding.tvWork.setText(artistDetailsDTO.getCategory_name());

        binding.tvTotalJobsDone.setText(artistDetailsDTO.getJobDone());
        if (artistDetailsDTO.getFav_status().equalsIgnoreCase("1")) {
            binding.ivFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_fav_full));
        } else {
            binding.ivFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_fav_blank));
        }

        Glide.with(mContext).
                load(artistDetailsDTO.getImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivArtist);

        Glide.with(mContext).
                load(artistDetailsDTO.getBanner_image())
                .placeholder(R.drawable.banner_img)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivBanner);

        setupViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

    }

    public void addFav() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Const.ADD_FAVORITES_API, paramsFav, mContext).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            if (flag) {
                ProjectUtils.showToast(mContext, msg);
                getArtist();
            } else {
                ProjectUtils.showToast(mContext, msg);
            }
        });
    }

    public void removeFav() {
        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Const.REMOVE_FAVORITES_API, paramsFav, mContext).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            if (flag) {
                ProjectUtils.showToast(mContext, msg);
                getArtist();
            } else {
                ProjectUtils.showToast(mContext, msg);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        persnoalInfo.setArguments(bundle);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(persnoalInfo, "Info");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(i)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            binding.ivArtist.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(200)
                    .start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            binding.ivArtist.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    public void clickScheduleDateTime() {
        new SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()
                .mustBeOnFuture()
                .defaultDate(new Date())
                .listener(date -> {
                    paramBookAppointment.put(Const.DATE_STRING, String.valueOf(sdf1.format(date).toString().toUpperCase()));
                    paramBookAppointment.put(Const.TIMEZONE, String.valueOf(timeZone.format(date)));
                    bookAppointment();
                })
                .display();
    }

    public void bookAppointment() {

        ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Const.BOOK_APPOINTMENT_API, paramBookAppointment, mContext).stringPost(TAG, (flag, msg, response) -> {
            if (flag) {
                ProjectUtils.pauseProgressDialog();
                ProjectUtils.showToast(mContext, msg);

            } else {
                ProjectUtils.showToast(mContext, msg);
            }


        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
