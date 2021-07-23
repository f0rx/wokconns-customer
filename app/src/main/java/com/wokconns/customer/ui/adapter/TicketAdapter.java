package com.wokconns.customer.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.wokconns.customer.R;
import com.wokconns.customer.dto.TicketDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.interfaces.Consts;
import com.wokconns.customer.ui.activity.CommentOneByOne;
import com.wokconns.customer.ui.fragment.Tickets;
import com.wokconns.customer.utils.CustomTextView;
import com.wokconns.customer.utils.CustomTextViewBold;
import com.wokconns.customer.utils.ProjectUtils;

import java.util.ArrayList;


public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.MyViewHolder> {

    private Context mContext;
    private Tickets tickets;
    private ArrayList<TicketDTO> ticketDTOSList;
    private UserDTO userDTO;


    public TicketAdapter(Tickets tickets, ArrayList<TicketDTO> ticketDTOSList, UserDTO userDTO) {
        this.tickets = tickets;
        this.mContext = tickets.getActivity();
        this.ticketDTOSList = ticketDTOSList;
        this.userDTO = userDTO;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_ticket, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        holder.tvTicket.setText(ticketDTOSList.get(position).getReason());


        try {
            holder.tvDate.setText(ProjectUtils.convertTimestampDateToTime(ProjectUtils.correctTimestamp(Long.parseLong(ticketDTOSList.get(position).getCraeted_at()))));

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (ticketDTOSList.get(position).getStatus().equalsIgnoreCase("0")) {
            holder.tvStatus.setText(mContext.getResources().getString(R.string.pending));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_orange));
        } else if (ticketDTOSList.get(position).getStatus().equalsIgnoreCase("1")) {
            holder.tvStatus.setText(mContext.getResources().getString(R.string.solve));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
        } else if (ticketDTOSList.get(position).getStatus().equalsIgnoreCase("2")) {
            holder.tvStatus.setText(mContext.getResources().getString(R.string.close));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
        }

        holder.rlClick.setOnClickListener(v -> {
            Intent in = new Intent(mContext, CommentOneByOne.class);
            in.putExtra(Consts.TICKET_ID, ticketDTOSList.get(position).getId());
            mContext.startActivity(in);
        });


    }

    @Override
    public int getItemCount() {

        return ticketDTOSList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomTextViewBold tvTicket;
        public CustomTextView tvDate, tvStatus;
        public LinearLayout llStatus;
        public RelativeLayout rlClick;

        public MyViewHolder(View view) {
            super(view);
            llStatus = view.findViewById(R.id.llStatus);
            tvStatus = view.findViewById(R.id.tvStatus);
            tvTicket = view.findViewById(R.id.tvTicket);
            tvDate = view.findViewById(R.id.tvDate);
            rlClick = view.findViewById(R.id.rlClick);


        }
    }


}