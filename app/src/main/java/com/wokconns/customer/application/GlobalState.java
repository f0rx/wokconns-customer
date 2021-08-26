package com.wokconns.customer.application;

import androidx.multidex.MultiDexApplication;

import com.wokconns.customer.dto.HomeDataDTO;
import com.wokconns.customer.preferences.SharedPrefs;

public class GlobalState extends MultiDexApplication {

    private static GlobalState mInstance;
    HomeDataDTO homeData;
    SharedPrefs sharedPrefs;

    public static synchronized GlobalState getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPrefs = SharedPrefs.getInstance(this);
    }

    public HomeDataDTO getHomeData() {
        return homeData;
    }

    public void setHomeData(HomeDataDTO homeData) {
        this.homeData = homeData;
    }
}
