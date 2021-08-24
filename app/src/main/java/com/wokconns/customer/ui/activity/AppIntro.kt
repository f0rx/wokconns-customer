package com.wokconns.customer.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ToxicBakery.viewpager.transforms.StackTransformer
import com.wokconns.customer.R
import com.wokconns.customer.databinding.ActivityAppIntro2Binding
import com.wokconns.customer.preferences.SharedPrefrence
import com.wokconns.customer.ui.adapter.AppIntroPagerAdapter
import com.wokconns.customer.utils.ProjectUtils.Fullscreen

class AppIntro : AppCompatActivity(), OnPageChangeListener, View.OnClickListener {
    lateinit var preference: SharedPrefrence
    private var mResources = intArrayOf(R.drawable.intro_1, R.drawable.intro_2, R.drawable.intro_3)
    private lateinit var mAdapter: AppIntroPagerAdapter
    private var dotsCount = 0
    private lateinit var dots: Array<ImageView?>
    private var mContext: Context = this@AppIntro
    private lateinit var binding: ActivityAppIntro2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fullscreen(this@AppIntro)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_intro2)
        preference = SharedPrefrence.getInstance(mContext)
        binding.llSignin.setOnClickListener(this)
        binding.llSignup.setOnClickListener(this)
        //        binding.llLanguage.setOnClickListener(this);
        mAdapter = AppIntroPagerAdapter(this@AppIntro, mContext, mResources)
        binding.viewpager.adapter = mAdapter
        binding.viewpager.setPageTransformer(true, StackTransformer())
        binding.viewpager.currentItem = 0
        binding.viewpager.addOnPageChangeListener(this)
        setPageViewIndicator()
    }

    @SuppressLint("UseCompatLoadingForDrawables", "ClickableViewAccessibility")
    private fun setPageViewIndicator() {
        Log.d("###setPageViewIndicator", " : called")
        dotsCount = mAdapter.count
        dots = arrayOfNulls(dotsCount)
        for (i in 0 until dotsCount) {
            dots[i] = ImageView(mContext)
            dots[i]!!.setImageDrawable(resources.getDrawable(R.drawable.nonselecteditem_dot))
            val params = LinearLayout.LayoutParams(
                18,
                18
            )
            params.setMargins(4, 0, 4, 0)
            dots[i]!!.setOnTouchListener { _: View?, _: MotionEvent? ->
                binding.viewpager.currentItem = i
                true
            }
            binding.viewPagerCountDots.addView(dots[i], params)
        }
        dots[0]!!.setImageDrawable(resources.getDrawable(R.drawable.selecteditem_dot))
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onPageSelected(position: Int) {

        for (i in 0 until dotsCount) {
            dots[i]!!.setImageDrawable(resources.getDrawable(R.drawable.nonselecteditem_dot))
        }
        dots[position]!!.setImageDrawable(resources.getDrawable(R.drawable.selecteditem_dot))
//        if (position + 1 == dotsCount) {
//        } else {
//        }
    }

    override fun onPageScrollStateChanged(state: Int) {}
    fun scrollPage(position: Int) {
        binding.viewpager.currentItem = position
    }

    override fun onBackPressed() {
        //super.onBackPressed();
        clickDone()
    }

    fun clickDone() {
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.llSignin -> {
                startActivity(Intent(mContext, SignInActivity::class.java))
                finish()
            }
            R.id.llSignup -> {
                startActivity(Intent(mContext, SignUpActivity::class.java))
                finish()
            }
        }
    }
}