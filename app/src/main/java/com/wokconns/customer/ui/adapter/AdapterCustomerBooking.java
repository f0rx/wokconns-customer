package com.wokconns.customer.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.AdapterCustomerBookingBinding;
import com.wokconns.customer.databinding.ItemSectionBinding;
import com.wokconns.customer.dto.UserBooking;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.ui.activity.MapActivity;
import com.wokconns.customer.ui.activity.PaymentProActivity;
import com.wokconns.customer.ui.activity.ViewInvoice;
import com.wokconns.customer.ui.fragment.MyBooking;
import com.wokconns.customer.utils.ProjectUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class AdapterCustomerBooking extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = AdapterCustomerBooking.class.getSimpleName();
    Fragment myBooking;
    private Context mContext;
    private ArrayList<UserBooking> objects = null;
    private ArrayList<UserBooking> userBookingsList;
    private HashMap<String, String> paramsDecline;
    private UserDTO userDTO;
    private HashMap<String, String> paramsBookingOp;
    private DialogInterface dialog_book;
    int min;
    int sec;
    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    ItemSectionBinding itemSectionBinding;
    AdapterCustomerBookingBinding adapterCustomerBookingBinding;
    String type = "booking";

    public AdapterCustomerBooking(Fragment myBooking, Context mContext, ArrayList<UserBooking> objects, UserDTO userDTO, String type) {
        this.myBooking = myBooking;
        this.mContext = mContext;
        this.objects = objects;
        this.userDTO = userDTO;
        this.type = type;
        userBookingsList = new ArrayList<>();
        userBookingsList.addAll(objects);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            adapterCustomerBookingBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.adapter_customer_booking, parent, false);
            vh = new MyViewHolder(adapterCustomerBookingBinding);
        } else {
            itemSectionBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_section, parent, false);
            vh = new JobsAdapter.MyViewHolderSection(itemSectionBinding);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderMain, final int position) {
        if (holderMain instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) holderMain;
            Glide.with(mContext).
                    load(objects.get(position).getArtistImage())
                    .placeholder(R.drawable.dummyuser_image)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.adapterCustomerBookingBinding.ivArtist);

            if (objects.get(position).getBooking_type().equalsIgnoreCase("0") || objects.get(position).getBooking_type().equalsIgnoreCase("3")) {
                if (objects.get(position).getBooking_flag().equalsIgnoreCase("0")) {
                    holder.adapterCustomerBookingBinding.ivMap.setVisibility(View.GONE);
                    holder.adapterCustomerBookingBinding.tvStatus.setText(mContext.getResources().getString(R.string.waiting_artist));
                    holder.adapterCustomerBookingBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_orange));
                    holder.adapterCustomerBookingBinding.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_waiting));
                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("1")) {
                    holder.adapterCustomerBookingBinding.ivMap.setVisibility(View.GONE);
                    holder.adapterCustomerBookingBinding.tvStatus.setText(mContext.getResources().getString(R.string.request_acc));
                    holder.adapterCustomerBookingBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
                    holder.adapterCustomerBookingBinding.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("3")) {
                    holder.adapterCustomerBookingBinding.ivMap.setVisibility(View.VISIBLE);
                    holder.adapterCustomerBookingBinding.tvStatus.setText(mContext.getResources().getString(R.string.work_inpro));
                    holder.adapterCustomerBookingBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                    holder.adapterCustomerBookingBinding.llTime.setVisibility(View.VISIBLE);
                    holder.adapterCustomerBookingBinding.llCancel.setVisibility(View.GONE);
                    holder.adapterCustomerBookingBinding.llFinish.setVisibility(View.GONE);
                    holder.adapterCustomerBookingBinding.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_inprogress));

                    SimpleDateFormat sdf = new SimpleDateFormat("mm.ss");

                    try {
                        Date dt = sdf.parse(objects.get(position).getWorking_min());
                        sdf = new SimpleDateFormat("HH:mm:ss");
                        Log.e("time", sdf.format(dt) + "");
                        min = dt.getHours() * 60 + dt.getMinutes();
                        sec = dt.getSeconds();
                        holder.adapterCustomerBookingBinding.chronometer.setBase(SystemClock.elapsedRealtime() - (min * 60000 + sec * 1000));
                        holder.adapterCustomerBookingBinding.chronometer.start();
                        Log.e("min", min + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("4")) {
                    try {
                        if (objects.get(position).getInvoice() != null && objects.get(position).getInvoice().getFlag().equalsIgnoreCase("1")) {
                            holder.adapterCustomerBookingBinding.ivMap.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llTime.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llCancel.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llFinish.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llPay.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.tvStatus.setText(mContext.getResources().getString(R.string.invoice_paid));
                            holder.adapterCustomerBookingBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                            holder.adapterCustomerBookingBinding.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                        } else {
                            holder.adapterCustomerBookingBinding.ivMap.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llTime.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llCancel.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llFinish.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llPay.setVisibility(View.VISIBLE);
                            holder.adapterCustomerBookingBinding.tvStatus.setText(mContext.getResources().getString(R.string.invoice_genrate));
                            holder.adapterCustomerBookingBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                            holder.adapterCustomerBookingBinding.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (objects.get(position).getBooking_type().equalsIgnoreCase("2")) {
                if (objects.get(position).getBooking_flag().equalsIgnoreCase("0")) {
                    holder.adapterCustomerBookingBinding.ivMap.setVisibility(View.GONE);
                    holder.adapterCustomerBookingBinding.tvStatus.setText(mContext.getResources().getString(R.string.waiting_artist));
                    holder.adapterCustomerBookingBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_orange));
                    holder.adapterCustomerBookingBinding.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_waiting));
                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("1")) {
                    holder.adapterCustomerBookingBinding.ivMap.setVisibility(View.GONE);
                    holder.adapterCustomerBookingBinding.tvStatus.setText(mContext.getResources().getString(R.string.request_acc));
                    holder.adapterCustomerBookingBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
                    holder.adapterCustomerBookingBinding.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("3")) {
                    holder.adapterCustomerBookingBinding.ivMap.setVisibility(View.VISIBLE);
                    holder.adapterCustomerBookingBinding.tvStatus.setText(mContext.getResources().getString(R.string.work_inpro));
                    holder.adapterCustomerBookingBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                    holder.adapterCustomerBookingBinding.llTime.setVisibility(View.VISIBLE);
                    holder.adapterCustomerBookingBinding.llCancel.setVisibility(View.GONE);
                    holder.adapterCustomerBookingBinding.llFinish.setVisibility(View.VISIBLE);
                    holder.adapterCustomerBookingBinding.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_inprogress));
                    SimpleDateFormat sdf = new SimpleDateFormat("mm.ss");

                    try {
                        Date dt = sdf.parse(objects.get(position).getWorking_min());
                        sdf = new SimpleDateFormat("HH:mm:ss");
                        System.out.println(sdf.format(dt));
                        min = dt.getHours() * 60 + dt.getMinutes();
                        sec = dt.getSeconds();
                        holder.adapterCustomerBookingBinding.chronometer.setBase(SystemClock.elapsedRealtime() - (min * 60000 + sec * 1000));
                        holder.adapterCustomerBookingBinding.chronometer.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (objects.get(position).getBooking_flag().equalsIgnoreCase("4")) {
                    try {
                        if (objects.get(position).getInvoice().getFlag().equalsIgnoreCase("1")) {
                            holder.adapterCustomerBookingBinding.ivMap.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llTime.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llCancel.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llFinish.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llPay.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llViewInvoice.setVisibility(View.VISIBLE);
                            holder.adapterCustomerBookingBinding.tvStatus.setText(mContext.getResources().getString(R.string.invoice_paid));
                            holder.adapterCustomerBookingBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                            holder.adapterCustomerBookingBinding.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                        } else {
                            holder.adapterCustomerBookingBinding.ivMap.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llTime.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llCancel.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llFinish.setVisibility(View.GONE);
                            holder.adapterCustomerBookingBinding.llPay.setVisibility(View.VISIBLE);
                            holder.adapterCustomerBookingBinding.llViewInvoice.setVisibility(View.VISIBLE);
                            holder.adapterCustomerBookingBinding.tvStatus.setText(mContext.getResources().getString(R.string.invoice_genrate));
                            holder.adapterCustomerBookingBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
                            holder.adapterCustomerBookingBinding.ivStatus.setBackground(mContext.getResources().getDrawable(R.drawable.ic_accept));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (objects.get(position).getBooking_type().equalsIgnoreCase("0")) {
                if (objects.get(position).getArtist_commission_type().equalsIgnoreCase("0")) {
                    if (objects.get(position).getBooking_flag().equalsIgnoreCase("3")) {
                        float price_hr = Float.parseFloat(objects.get(position).getPrice()) / 60;
                        float total_price = price_hr * min;
                        holder.adapterCustomerBookingBinding.tvPrice.setText(objects.get(position).getCurrency_type() + String.format("%.2f", total_price));
                    } else {
                        holder.adapterCustomerBookingBinding.tvPrice.setText(objects.get(position).getCurrency_type() + objects.get(position).getPrice() + mContext.getResources().getString(R.string.hr_add_on));

                    }
                } else {
                    holder.adapterCustomerBookingBinding.tvPrice.setText(objects.get(position).getCurrency_type() + objects.get(position).getPrice());
                }
            } else if (objects.get(position).getBooking_type().equalsIgnoreCase("2")) {
                holder.adapterCustomerBookingBinding.tvPrice.setText(objects.get(position).getCurrency_type() + objects.get(position).getPrice());

            } else if (objects.get(position).getBooking_type().equalsIgnoreCase("3")) {
                holder.adapterCustomerBookingBinding.tvPrice.setText(objects.get(position).getCurrency_type() + objects.get(position).getPrice());

            }


            holder.adapterCustomerBookingBinding.tvDate.setText(mContext.getResources().getString(R.string.date) + " " + ProjectUtils.changeDateFormate1(objects.get(position).getBooking_date()) + " " + objects.get(position).getBooking_time());
            holder.adapterCustomerBookingBinding.tvDescription.setText(objects.get(position).getDescription());
            holder.adapterCustomerBookingBinding.tvWork.setText(objects.get(position).getCategory_name());
            holder.adapterCustomerBookingBinding.tvLocation.setText(objects.get(position).getArtistLocation());
            holder.adapterCustomerBookingBinding.tvJobComplete.setText(objects.get(position).getJobDone() + " " + mContext.getResources().getString(R.string.jobs_completed));
            holder.adapterCustomerBookingBinding.tvProfileComplete.setText(objects.get(position).getCompletePercentages() + mContext.getResources().getString(R.string.completion));

            holder.adapterCustomerBookingBinding.tvName.setText(mContext.getResources().getString(R.string.booking_with) + " " + objects.get(position).getArtistName());


            holder.adapterCustomerBookingBinding.llCancel.setOnClickListener(v ->
                    completeDialog(mContext.getResources().getString(R.string.cancel),
                            mContext.getResources().getString(R.string.booking_cancel_msg) + " " +
                                    objects.get(position).getArtistName() + "?",
                            position
                    )
            );

            holder.adapterCustomerBookingBinding.ivMap.setOnClickListener(v -> {
                Intent in = new Intent(mContext, MapActivity.class);
                in.putExtra(Consts.ARTIST_ID, objects.get(position).getArtist_id());
                mContext.startActivity(in);
            });

            holder.adapterCustomerBookingBinding.llFinish.setOnClickListener(v -> {
                if (NetworkManager.isConnectToInternet(mContext)) {
                    booking("3", position);
                } else {
                    ProjectUtils.showToast(mContext, mContext.getResources().getString(R.string.internet_concation));
                }
            });
            holder.adapterCustomerBookingBinding.llPay.setOnClickListener(v -> {
                Intent in = new Intent(mContext, PaymentProActivity.class);
                in.putExtra(Consts.HISTORY_DTO, objects.get(position).getInvoice());
                mContext.startActivity(in);
//                myBooking.requireActivity().getSupportFragmentManager()
//                        .beginTransaction().remove(myBooking).commit();
            });
            holder.adapterCustomerBookingBinding.llViewInvoice.setOnClickListener(v -> {
                Intent in = new Intent(mContext, ViewInvoice.class);
                in.putExtra(Consts.HISTORY_DTO, objects.get(position).getInvoice());
                mContext.startActivity(in);

            });

            if (type.equalsIgnoreCase("home")) {
                holder.adapterCustomerBookingBinding.llCancel.setVisibility(View.GONE);
                holder.adapterCustomerBookingBinding.llTime.setVisibility(View.GONE);
                holder.adapterCustomerBookingBinding.llFinish.setVisibility(View.GONE);
                holder.adapterCustomerBookingBinding.llViewInvoice.setVisibility(View.GONE);
                holder.adapterCustomerBookingBinding.llPay.setVisibility(View.GONE);

            }
        } else {
            JobsAdapter.MyViewHolderSection view = (JobsAdapter.MyViewHolderSection) holderMain;
            view.itemSectionBinding.tvSection.setText(objects.get(position).getSection_name());
        }
    }

    @Override
    public int getItemCount() {

        return objects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.objects.get(position).isSection() ? VIEW_SECTION : VIEW_ITEM;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        AdapterCustomerBookingBinding adapterCustomerBookingBinding;

        public MyViewHolder(AdapterCustomerBookingBinding adapterCustomerBookingBinding) {
            super(adapterCustomerBookingBinding.getRoot());
            this.adapterCustomerBookingBinding = adapterCustomerBookingBinding;

        }
    }

    public static class MyViewHolderSection extends RecyclerView.ViewHolder {
        ItemSectionBinding itemSectionBinding;

        public MyViewHolderSection(ItemSectionBinding itemSectionBinding) {
            super(itemSectionBinding.getRoot());
            this.itemSectionBinding = itemSectionBinding;
        }
    }


    public void decline(int pos) {
        paramsDecline = new HashMap<>();
        paramsDecline.put(Consts.USER_ID, userDTO.getUser_id());
        paramsDecline.put(Consts.BOOKING_ID, objects.get(pos).getId());
        paramsDecline.put(Consts.DECLINE_BY, "2");
        paramsDecline.put(Consts.DECLINE_REASON, "Busy");
        ProjectUtils.showProgressDialog(mContext, true, mContext.getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.DECLINE_BOOKING_API, paramsDecline, mContext).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            dialog_book.dismiss();
            if (flag) {
                ProjectUtils.showToast(mContext, msg);

                try {
                    ((MyBooking) myBooking).getBooking();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                ProjectUtils.showToast(mContext, msg);
            }


        });
    }

    public void booking(String req, int pos) {
        paramsBookingOp = new HashMap<>();
        paramsBookingOp.put(Consts.BOOKING_ID, objects.get(pos).getId());
        paramsBookingOp.put(Consts.REQUEST, req);
        paramsBookingOp.put(Consts.USER_ID, objects.get(pos).getUser_id());
        ProjectUtils.showProgressDialog(mContext, true, mContext.getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.BOOKING_OPERATION_API, paramsBookingOp, mContext).stringPost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
            if (flag) {
                ProjectUtils.showToast(mContext, msg);

                try {
                    ((MyBooking) myBooking).getBooking();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                ProjectUtils.showToast(mContext, msg);
            }


        });
    }

    public void completeDialog(String title, String msg, final int pos) {
        try {
            new AlertDialog.Builder(mContext)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.yes), (dialog, which) -> {
                        dialog_book = dialog;
                        decline(pos);

                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss())
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        objects.clear();
        if (charText.length() == 0) {
            objects.addAll(userBookingsList);
        } else {
            for (UserBooking userBooking : userBookingsList) {
                if (userBooking.getArtistName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    objects.add(userBooking);
                }
            }
        }
        notifyDataSetChanged();
    }


}