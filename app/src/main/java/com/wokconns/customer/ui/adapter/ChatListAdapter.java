package com.wokconns.customer.ui.adapter;
/**
 * Created by VARUN on 01/01/19.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.AdapterChatListBinding;
import com.wokconns.customer.dto.ChatListDTO;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.interfaces.DisclaimerWarning;
import com.wokconns.customer.ui.activity.OneTwoOneChat;
import com.wokconns.customer.utils.ProjectUtils;

import java.util.ArrayList;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> implements DisclaimerWarning {

    Context mContext;
    ArrayList<ChatListDTO> chatList;
    AdapterChatListBinding binding;
    LayoutInflater layoutInflater;

    public ChatListAdapter(Context mContext, ArrayList<ChatListDTO> chatList) {
        this.mContext = mContext;
        this.chatList = chatList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.adapter_chat_list, parent, false);

        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.binding.tvTitle.setText(chatList.get(position).getArtistName());
        holder.binding.tvMsg.setText(chatList.get(position).getMessage());

        try {
            holder.binding.tvDate.setText(ProjectUtils.convertTimestampDate(ProjectUtils.correctTimestamp(Long.parseLong(chatList.get(position).getSend_at()))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Glide.with(mContext)
                .load(chatList.get(position).getArtistImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.binding.IVprofile);

        holder.itemView.setOnClickListener(v -> {
            Intent in = new Intent(mContext, OneTwoOneChat.class);
            in.putExtra(Const.ARTIST_ID, chatList.get(position).getArtist_id());
            in.putExtra(Const.ARTIST_NAME, chatList.get(position).getArtistName());

            showDisclaimerDialog(mContext, in);
        });

    }

    @Override
    public int getItemCount() {

        return chatList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        AdapterChatListBinding binding;

        public MyViewHolder(AdapterChatListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}