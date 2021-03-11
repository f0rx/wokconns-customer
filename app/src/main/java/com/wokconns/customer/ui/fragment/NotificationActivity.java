package com.wokconns.customer.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wokconns.customer.dto.NotificationDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.R;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.interfacess.Helper;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.activity.BaseActivity;
import com.wokconns.customer.ui.adapter.NotificationAdapter;
import com.wokconns.customer.utils.CustomTextViewBold;
import com.wokconns.customer.utils.ProjectUtils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationActivity extends Fragment {
    private String TAG = NotificationActivity.class.getSimpleName();
    private RecyclerView RVnotification;
    private NotificationAdapter notificationAdapter;
    private ArrayList<NotificationDTO> notificationDTOlist;
    private LinearLayoutManager mLayoutManager;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private CustomTextViewBold tvNo;
    private View view;
    private BaseActivity baseActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.activity_notification, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        baseActivity.headerNameTV.setText(getResources().getString(R.string.notification));
        setUiAction(view);
        return view;
    }

    public void setUiAction(View v) {
        tvNo = v.findViewById(R.id.tvNo);
        RVnotification = v.findViewById(R.id.RVnotification);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        RVnotification.setLayoutManager(mLayoutManager);
    }



    @Override
    public void onResume() {
        super.onResume();
        if (NetworkManager.isConnectToInternet(getActivity())) {
            getNotification();

        } else {
            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
        }
    }

    public void getNotification() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.GET_NOTIFICATION_API, getparm(), getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    tvNo.setVisibility(View.GONE);
                    RVnotification.setVisibility(View.VISIBLE);
                    try {
                        notificationDTOlist = new ArrayList<>();
                        Type getpetDTO = new TypeToken<List<NotificationDTO>>() {
                        }.getType();
                        notificationDTOlist = (ArrayList<NotificationDTO>) new Gson().fromJson(response.getJSONArray("my_notifications").toString(), getpetDTO);
                        showData();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    tvNo.setVisibility(View.VISIBLE);
                    RVnotification.setVisibility(View.GONE);
                }
            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        return parms;
    }

    public void showData() {
        notificationAdapter = new NotificationAdapter(getActivity(), notificationDTOlist);
        RVnotification.setAdapter(notificationAdapter);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }
}
