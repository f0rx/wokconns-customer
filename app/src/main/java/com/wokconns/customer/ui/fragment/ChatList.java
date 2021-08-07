package com.wokconns.customer.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wokconns.customer.R;
import com.wokconns.customer.dto.ChatListDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.activity.BaseActivity;
import com.wokconns.customer.ui.adapter.ChatListAdapter;
import com.wokconns.customer.utils.CustomTextViewBold;
import com.wokconns.customer.utils.ProjectUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatList extends Fragment {
    private String TAG = ChatList.class.getSimpleName();
    private RecyclerView rvChatList;
    private ChatListAdapter chatListAdapter;
    private ArrayList<ChatListDTO> chatList = new ArrayList<>();
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
        view = inflater.inflate(R.layout.activity_chat_list, container, false);

        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Const.USER_DTO);
        baseActivity.headerNameTV.setText(getResources().getString(R.string.chats));
        setUiAction(view);
        return view;
    }


    public void setUiAction(View v) {
        tvNo = v.findViewById(R.id.tvNo);
        rvChatList = v.findViewById(R.id.rvChatList);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rvChatList.setLayoutManager(mLayoutManager);

        chatListAdapter = new ChatListAdapter(getActivity(), chatList);
        rvChatList.setAdapter(chatListAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (NetworkManager.isConnectToInternet(getActivity())) {
            getChat();

        } else {
            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_connection));
        }
    }

    public void getChat() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Const.GET_CHAT_HISTORY_API, getparm(), getActivity()).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            if (flag) {
                tvNo.setVisibility(View.GONE);
                rvChatList.setVisibility(View.VISIBLE);
                try {
                    chatList = new ArrayList<>();
                    Type getpetDTO = new TypeToken<List<ChatListDTO>>() {
                    }.getType();
                    chatList = (ArrayList<ChatListDTO>) new Gson().fromJson(response.getJSONArray("my_chat").toString(), getpetDTO);
                    showData();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                tvNo.setVisibility(View.VISIBLE);
                rvChatList.setVisibility(View.GONE);
            }
        });
    }

    public HashMap<String, String> getparm() {
        HashMap<String, String> parms = new HashMap<>();
        parms.put(Const.USER_ID, userDTO.getUser_id());
        return parms;
    }

    public void showData() {
        chatListAdapter = new ChatListAdapter(getActivity(), chatList);
        rvChatList.setAdapter(chatListAdapter);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity) activity;
    }
}
