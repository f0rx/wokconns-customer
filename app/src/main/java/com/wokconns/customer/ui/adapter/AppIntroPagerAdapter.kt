package com.wokconns.customer.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.wokconns.customer.R
import com.wokconns.customer.databinding.AppintropagerAdapterBinding
import com.wokconns.customer.ui.activity.AppIntro

class AppIntroPagerAdapter(
    appIntroActivity: AppIntro,
    private val mContext: Context,
    private val mResources: IntArray
) : PagerAdapter() {
    var mLayoutInflater: LayoutInflater =
        mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val activity: AppIntro = appIntroActivity
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding: AppintropagerAdapterBinding = DataBindingUtil.inflate(
            mLayoutInflater,
            R.layout.appintropager_adapter,
            container,
            false
        )
        binding.ivImage.setImageResource(mResources[position])
        setDescText(position, binding.ctvTextdecrib, binding.ctvText)

        container.addView(binding.root)
        binding.ctvText.setOnClickListener {
            val pos = position + 1
            activity.scrollPage(pos)
        }
        return binding.root
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return mResources.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    fun setDescText(pos: Int, ctvDescription: TextView, tvBottom: TextView) {
        when (pos) {
            0 -> {
                ctvDescription.text = mContext.getString(R.string.intro_1)
                tvBottom.text = mContext.getString(R.string.intro_1_bottom)
            }
            1 -> {
                ctvDescription.text = mContext.getString(R.string.intro_2)
                tvBottom.text = mContext.getString(R.string.intro_2_bottom)
            }
            2 -> {
                ctvDescription.text = mContext.getString(R.string.intro_3)
                tvBottom.text = mContext.getString(R.string.intro_3_bottom)
            }
        }
    }

}