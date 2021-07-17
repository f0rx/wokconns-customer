package com.wokconns.customer.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.wokconns.customer.R;
import com.wokconns.customer.dto.AppointmentDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.interfacess.Helper;
import com.wokconns.customer.ui.fragment.AppointmentFrag;
import com.wokconns.customer.utils.CustomTextView;
import com.wokconns.customer.utils.CustomTextViewBold;
import com.wokconns.customer.utils.ProjectUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterAppointmnet extends RecyclerView.Adapter<AdapterAppointmnet.MyViewHolder> {
    SimpleDateFormat sdf1, timeZone;
    private String TAG = AdapterAppointmnet.class.getSimpleName();
    private AppointmentFrag appointmentFrag;
    private Context mContext;
    private ArrayList<AppointmentDTO> appointmentDTOSList;
    private UserDTO userDTO;
    private HashMap<String, String> paramBookAppointment = new HashMap<>();
    private HashMap<String, String> paramDeclineAppointment = new HashMap<>();
    private DialogInterface dialog_book;

    public AdapterAppointmnet(AppointmentFrag appointmentFrag, ArrayList<AppointmentDTO> appointmentDTOSList, UserDTO userDTO) {
        this.appointmentFrag = appointmentFrag;
        mContext = appointmentFrag.getActivity();
        this.appointmentDTOSList = appointmentDTOSList;
        this.userDTO = userDTO;

        sdf1 = new SimpleDateFormat(Consts.DATE_FORMATE_SERVER, Locale.ENGLISH);
        timeZone = new SimpleDateFormat(Consts.DATE_FORMATE_TIMEZONE, Locale.ENGLISH);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_appointment, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
//0. Pending 1. Accepted 2. Rejected 3. Completed 4. Decline
        Glide.with(mContext).
                load(appointmentDTOSList.get(position).getArtistImage())
                .placeholder(R.drawable.dummyuser_image)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivArtist);

        holder.tvWork.setText(appointmentDTOSList.get(position).getCategory_name());
        holder.tvLocation.setText(appointmentDTOSList.get(position).getArtistAddress());
        holder.tvName.setText(appointmentDTOSList.get(position).getArtistName());
        holder.tvDate.setText(appointmentDTOSList.get(position).getDate_string());
        holder.tvEmail.setText(appointmentDTOSList.get(position).getArtistEmail());
        holder.tvMobile.setText(appointmentDTOSList.get(position).getArtistMobile());

        if (appointmentDTOSList.get(position).getStatus().equalsIgnoreCase("0")) {
            holder.rlComplete.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.pending));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_orange));
        } else if (appointmentDTOSList.get(position).getStatus().equalsIgnoreCase("1")) {
            holder.rlComplete.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.accepted));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
        } else if (appointmentDTOSList.get(position).getStatus().equalsIgnoreCase("2")) {
            holder.rlComplete.setVisibility(View.GONE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.rejected));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_dark_red));
        } else if (appointmentDTOSList.get(position).getStatus().equalsIgnoreCase("3")) {
            holder.rlComplete.setVisibility(View.GONE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.completed));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
        } else if (appointmentDTOSList.get(position).getStatus().equalsIgnoreCase("4")) {
            holder.rlComplete.setVisibility(View.GONE);
            holder.tvStatus.setText(mContext.getResources().getString(R.string.decline));
            holder.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_dark_blue));
        }


        holder.tvEdit.setOnClickListener(v -> {
            paramBookAppointment.put(Consts.USER_ID, userDTO.getUser_id());
            paramBookAppointment.put(Consts.ARTIST_ID, appointmentDTOSList.get(position).getArtist_id());
            paramBookAppointment.put(Consts.APPOINTMENT_ID, appointmentDTOSList.get(position).getId());
            clickScheduleDateTime();
        });
        holder.tvDecline.setOnClickListener(v -> {
            paramDeclineAppointment.put(Consts.USER_ID, userDTO.getUser_id());
            paramDeclineAppointment.put(Consts.APPOINTMENT_ID, appointmentDTOSList.get(position).getId());
            paramDeclineAppointment.put(Consts.REQUEST, "4");
            bookDailog();
        });
    }


    @Override
    public int getItemCount() {

        return appointmentDTOSList.size();
    }

    public void clickScheduleDateTime() {
        new SingleDateAndTimePickerDialog.Builder(mContext)
                .bottomSheet()
                .curved()
                .mustBeOnFuture()
                .listener(date -> {
                    paramBookAppointment.put(Consts.DATE_STRING, String.valueOf(sdf1.format(date).toString().toUpperCase()));
                    paramBookAppointment.put(Consts.TIMEZONE, String.valueOf(timeZone.format(date)));
                    bookAppointment();
                })
                .display();
    }

    public void bookAppointment() {
        new HttpsRequest(Consts.EDIT_APPOINTMENT_API, paramBookAppointment, mContext).stringPost(TAG, (flag, msg, response) -> {
            if (flag) {
                ProjectUtils.showToast(mContext, msg);
                appointmentFrag.getHistroy();
            } else {
                ProjectUtils.showToast(mContext, msg);
            }


        });
    }

    public void declineAppointment() {

        new HttpsRequest(Consts.APPOINTMENT_OPERATION_API, paramDeclineAppointment, mContext).stringPost(TAG, (flag, msg, response) -> {
            if (flag) {
                ProjectUtils.showToast(mContext, msg);
                dialog_book.dismiss();
                appointmentFrag.getHistroy();
            } else {
                ProjectUtils.showToast(mContext, msg);
            }


        });
    }

    public void bookDailog() {
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(mContext.getResources().getString(R.string.decline))
                    .setMessage(mContext.getResources().getString(R.string.decline_msg))
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.yes), (dialog, which) -> {
                        dialog_book = dialog;
                        declineAppointment();

                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView ivArtist;
        public CustomTextView tvWork, tvLocation, tvDate, tvStatus, tvMobile, tvEmail;
        public LinearLayout llStatus;
        public RelativeLayout rlComplete;
        public CustomTextViewBold tvName;
        public CustomTextView tvDecline, tvEdit;

        public MyViewHolder(View view) {
            super(view);
            ivArtist = view.findViewById(R.id.ivArtist);
            tvWork = view.findViewById(R.id.tvWork);
            tvName = view.findViewById(R.id.tvName);
            tvLocation = view.findViewById(R.id.tvLocation);
            tvDate = view.findViewById(R.id.tvDate);
            tvDecline = view.findViewById(R.id.tvDecline);
            tvEdit = view.findViewById(R.id.tvEdit);
            llStatus = view.findViewById(R.id.llStatus);
            tvStatus = view.findViewById(R.id.tvStatus);
            rlComplete = view.findViewById(R.id.rlComplete);
            tvEmail = view.findViewById(R.id.tvEmail);
            tvMobile = view.findViewById(R.id.tvMobile);
        }
    }

}