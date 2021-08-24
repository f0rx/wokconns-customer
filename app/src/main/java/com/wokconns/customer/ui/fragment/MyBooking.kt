package com.wokconns.customer.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wokconns.customer.R
import com.wokconns.customer.dto.UserBooking
import com.wokconns.customer.dto.UserDTO
import com.wokconns.customer.https.HttpsRequest
import com.wokconns.customer.interfaces.Const
import com.wokconns.customer.interfaces.Helper
import com.wokconns.customer.network.NetworkManager
import com.wokconns.customer.preferences.SharedPrefrence
import com.wokconns.customer.ui.activity.BaseActivity
import com.wokconns.customer.ui.adapter.AdapterCustomerBooking
import com.wokconns.customer.utils.CustomTextViewBold
import com.wokconns.customer.utils.ProjectUtils
import org.json.JSONObject
import java.util.*

class MyBooking : Fragment(), OnRefreshListener {
    var intentFilter = IntentFilter()
    private val TAG = NotificationActivity::class.java.simpleName
    private lateinit var rvBooking: RecyclerView
    private var adapterCustomerBooking: AdapterCustomerBooking? = null
    private lateinit var userBookingList: ArrayList<UserBooking>
    private lateinit var userBookingListSection: ArrayList<UserBooking>
    private var userBookingListSection1: ArrayList<UserBooking>? = null
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var userDTO: UserDTO
    private lateinit var tvNo: CustomTextViewBold
    private lateinit var mLayout: View
    private lateinit var baseActivity: BaseActivity
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var svSearch: SearchView
    private lateinit var rlSearch: RelativeLayout
    private var mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Const.DECLINE_BOOKING_ARTIST_NOTIFICATION, ignoreCase = true)
                || intent.action.equals(Const.START_BOOKING_ARTIST_NOTIFICATION, ignoreCase = true)
                || intent.action.equals(Const.END_BOOKING_ARTIST_NOTIFICATION, ignoreCase = true)
                || intent.action.equals(
                    Const.ACCEPT_BOOKING_ARTIST_NOTIFICATION,
                    ignoreCase = true
                )
            ) {
                getBooking()
                Log.e("BROADCAST", "BROADCAST")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.activity_my_booking, container, false)
        val preference = SharedPrefrence.getInstance(activity)
        baseActivity.headerNameTV.text = resources.getString(R.string.my_bookings)
        userDTO = preference.getParentUser(Const.USER_DTO)
        setUiAction(mLayout)
        return mLayout
    }

    fun setUiAction(v: View?) {
        rlSearch = v!!.findViewById(R.id.rlSearch)
        svSearch = v.findViewById(R.id.svSearch)
        tvNo = v.findViewById(R.id.tvNo)
        rvBooking = v.findViewById(R.id.rvBooking)
        swipeRefreshLayout = v.findViewById<View>(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        mLayoutManager = LinearLayoutManager(requireActivity().applicationContext)
        rvBooking.layoutManager = mLayoutManager
        svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length > 0) {
                    adapterCustomerBooking?.filter(newText)
                } else {
                }
                return false
            }
        })
        intentFilter.addAction(Const.DECLINE_BOOKING_ARTIST_NOTIFICATION)
        intentFilter.addAction(Const.START_BOOKING_ARTIST_NOTIFICATION)
        intentFilter.addAction(Const.END_BOOKING_ARTIST_NOTIFICATION)
        intentFilter.addAction(Const.ACCEPT_BOOKING_ARTIST_NOTIFICATION)
        LocalBroadcastManager.getInstance(requireActivity())
            .registerReceiver(mBroadcastReceiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.post {
            Log.e("Runnable", "FIRST")
            if (NetworkManager.isConnectToInternet(this@MyBooking.activity)) {
                swipeRefreshLayout.isRefreshing = true
                getBooking()
            } else {
                ProjectUtils.showToast(
                    this@MyBooking.activity,
                    this@MyBooking.resources.getString(R.string.internet_connection)
                )
            }
        }
    }
    // setSection();
    //   ProjectUtils.pauseProgressDialog();

    // ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));

    fun getBooking() {
        // ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        HttpsRequest(
            Const.CURRENT_BOOKING_API,
            getParams(),
            requireContext()
        ).stringPost(TAG, object : Helper {
            override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                swipeRefreshLayout.isRefreshing = false
                if (flag) {
                    tvNo.visibility = View.GONE
                    swipeRefreshLayout.visibility = View.VISIBLE
                    rlSearch.visibility = View.VISIBLE
                    try {
                        userBookingList = ArrayList()
                        val getpetDTO = object : TypeToken<List<UserBooking?>?>() {}.type
                        userBookingList = Gson().fromJson<Any>(
                            response?.getJSONArray("data").toString(),
                            getpetDTO
                        ) as ArrayList<UserBooking>
                        // setSection();
                        showData()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    tvNo.visibility = View.VISIBLE
                    swipeRefreshLayout.visibility = View.GONE
                    rlSearch.visibility = View.GONE
                }
            }
        })
    }

    fun getParams(): HashMap<String, String?> {
        val parms = HashMap<String, String?>()
        parms[Const.USER_ID] = userDTO.user_id
        return parms
    }

    fun showData() {
        adapterCustomerBooking = AdapterCustomerBooking(
            this@MyBooking,
            baseActivity,
            userBookingList,
            userDTO,
            "booking"
        )
        rvBooking.adapter = adapterCustomerBooking
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        baseActivity = activity as BaseActivity
    }

    override fun onRefresh() = getBooking()

    fun setSection() {
        val has = HashMap<String, ArrayList<UserBooking>>()
        userBookingListSection = ArrayList()
        for (i in userBookingList.indices) {
            if (has.containsKey(ProjectUtils.changeDateFormat(userBookingList[i].booking_date))) {
                userBookingListSection1 = ArrayList()
                userBookingListSection1 =
                    has[ProjectUtils.changeDateFormat(userBookingList[i].booking_date)]
                userBookingListSection1?.add(userBookingList[i])
                ProjectUtils.changeDateFormat(userBookingList[i].booking_date)?.let {
                    has[it] = userBookingListSection1!!
                }
            } else {
                userBookingListSection1 = ArrayList()
                userBookingListSection1?.add(userBookingList[i])
                ProjectUtils.changeDateFormat(userBookingList[i].booking_date)?.let {
                    has[it] = userBookingListSection1!!
                }
            }
        }
        has.keys.forEach { key ->
            val userBooking = UserBooking()
            userBooking.isSection = true
            userBooking.section_name = key
            userBookingListSection.add(userBooking)
            has[key]?.let { userBookingListSection.addAll(it) }
        }
        showData()
    }
}