package com.wokconns.customer.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivityAppIntro2Binding;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.adapter.AppIntroPagerAdapter;
import com.wokconns.customer.utils.ProjectUtils;

public class AppIntro extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    public SharedPrefrence preference;
    int[] mResources = {R.drawable.intro_1, R.drawable.intro_2, R.drawable.intro_3};
    private AppIntroPagerAdapter mAdapter;
    private int dotsCount;
    private ImageView[] dots;
    private Context mContext;
    private ActivityAppIntro2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProjectUtils.Fullscreen(AppIntro.this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_intro2);
        mContext = AppIntro.this;
        preference = SharedPrefrence.getInstance(mContext);

        binding.llSignin.setOnClickListener(this);
        binding.llSignup.setOnClickListener(this);
        binding.llLanguage.setOnClickListener(this);

        mAdapter = new AppIntroPagerAdapter(AppIntro.this, mContext, mResources);
        binding.viewpager.setAdapter(mAdapter);
        binding.viewpager.setPageTransformer(true, new StackTransformer());
        binding.viewpager.setCurrentItem(0);
        binding.viewpager.addOnPageChangeListener(this);
        setPageViewIndicator();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setPageViewIndicator() {

        Log.d("###setPageViewIndicator", " : called");
        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(mContext);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    18,
                    18
            );

            params.setMargins(4, 0, 4, 0);

            final int presentPosition = i;
            dots[presentPosition].setOnTouchListener((v, event) -> {
                binding.viewpager.setCurrentItem(presentPosition);
                return true;
            });


            binding.viewPagerCountDots.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onPageSelected(int position) {
        Log.e("###onPageSelected, pos ", String.valueOf(position));
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }


        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

        if (position + 1 == dotsCount) {

        } else {

        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void scrollPage(int position) {
        binding.viewpager.setCurrentItem(position);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        clickDone();
    }

    public void clickDone() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.closeMsg))
                .setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
                    dialog.dismiss();
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_HOME);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                    finish();
                })
                .setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llSignin:
                startActivity(new Intent(mContext, SignInActivity.class));
                finish();
                break;
            case R.id.llSignup:
                startActivity(new Intent(mContext, SignUpActivity.class));
                finish();
                break;
            case R.id.ll_language:
                Intent intent = new Intent(mContext, LanguageSelection.class);
                intent.putExtra(Const.TYPE, "0");
                startActivity(intent);
                finish();
                break;
        }
    }
}
