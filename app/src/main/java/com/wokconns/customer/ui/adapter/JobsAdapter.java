package com.wokconns.customer.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wokconns.customer.databinding.AdapterJobsBinding;
import com.wokconns.customer.databinding.ItemSectionBinding;
import com.wokconns.customer.dto.PostedJobDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.R;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.interfacess.Helper;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.ui.activity.AppliedJob;
import com.wokconns.customer.ui.activity.EditJob;
import com.wokconns.customer.ui.fragment.Jobs;
import com.wokconns.customer.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class JobsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = AppliedJobAdapter.class.getSimpleName();
    private HashMap<String, String> params;
    private HashMap<String, String> paramsComplete;
    private DialogInterface dialog_book;
    private Context mContext;
    private Jobs jobs;
    private ArrayList<PostedJobDTO> objects = null;
    private ArrayList<PostedJobDTO> postedJobDTOSList;
    private UserDTO userDTO;
    private SharedPrefrence preferences;
    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    ItemSectionBinding itemSectionBinding;
    AdapterJobsBinding adapterJobsBinding;

    public JobsAdapter(Jobs jobs, ArrayList<PostedJobDTO> objects, UserDTO userDTO) {
        this.jobs = jobs;
        this.mContext = jobs.getActivity();
        this.objects = objects;
        this.postedJobDTOSList = new ArrayList<PostedJobDTO>();
        this.postedJobDTOSList.addAll(objects);
        this.userDTO = userDTO;
        preferences = SharedPrefrence.getInstance(mContext);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            adapterJobsBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.adapter_jobs, parent, false);
            vh = new MyViewHolder(adapterJobsBinding);
        } else {
            itemSectionBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_section, parent, false);
            vh = new MyViewHolderSection(itemSectionBinding);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderMain, final int position) {

        if (holderMain instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) holderMain;

            //0 = pending, 1 = confirm , 2 = complete, 3 =reject

            holder.adapterJobsBinding.tvJobIdValue.setText(objects.get(position).getJob_id());
            holder.adapterJobsBinding.tvTitle.setText(objects.get(position).getTitle());
            holder.adapterJobsBinding.tvName.setText(userDTO.getName());
            holder.adapterJobsBinding.tvDescription.setText(objects.get(position).getDescription());
            holder.adapterJobsBinding.tvCategory.setText(objects.get(position).getCategory_name());
            holder.adapterJobsBinding.tvAddress.setText(objects.get(position).getAddress());
            holder.adapterJobsBinding.tvDate.setText(mContext.getResources().getString(R.string.date) + " " + ProjectUtils.changeDateFormate1(objects.get(position).getJob_date())+" "+objects.get(position).getTime());
            holder.adapterJobsBinding.tvApplied.setText(mContext.getResources().getString(R.string.applied1) + " " + objects.get(position).getApplied_job());
            holder.adapterJobsBinding.tvPrice.setText(objects.get(position).getCurrency_symbol() + objects.get(position).getPrice());

            if (objects.get(position).getStatus().equalsIgnoreCase("0")) {
                holder.adapterJobsBinding.rlComplete.setVisibility(View.VISIBLE);
                holder.adapterJobsBinding.tvStatus.setText(mContext.getResources().getString(R.string.open));
                holder.adapterJobsBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
            } else if (objects.get(position).getStatus().equalsIgnoreCase("1")) {
                holder.adapterJobsBinding.rlComplete.setVisibility(View.VISIBLE);
                holder.adapterJobsBinding.tvStatus.setText(mContext.getResources().getString(R.string.confirm));
                holder.adapterJobsBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_yellow));
            } else if (objects.get(position).getStatus().equalsIgnoreCase("2")) {
                holder.adapterJobsBinding.rlComplete.setVisibility(View.GONE);
                holder.adapterJobsBinding.tvStatus.setText(mContext.getResources().getString(R.string.completed));
                holder.adapterJobsBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_green));
            } else if (objects.get(position).getStatus().equalsIgnoreCase("3")) {
                holder.adapterJobsBinding.rlComplete.setVisibility(View.VISIBLE);
                holder.adapterJobsBinding.tvStatus.setText(mContext.getResources().getString(R.string.rejected));
                holder.adapterJobsBinding.llStatus.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_dark_red));
            }

            Glide.with(mContext).
                    load(objects.get(position).getAvtar())
                    .placeholder(R.drawable.dummyuser_image)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.adapterJobsBinding.ivImage);

            holder.adapterJobsBinding.rlClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(mContext, AppliedJob.class);
                    preferences.setValue(Consts.JOB_ID, objects.get(position).getJob_id());
                    mContext.startActivity(in);
                }
            });

            holder.adapterJobsBinding.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (objects.get(position).getIs_edit().equalsIgnoreCase("1")) {
                        Intent in = new Intent(mContext, EditJob.class);
                        in.putExtra(Consts.POST_JOB_DTO, objects.get(position));
                        mContext.startActivity(in);
                    } else {
                        ProjectUtils.showLong(mContext, mContext.getResources().getString(R.string.not_edit_job));
                    }

                }
            });
            holder.adapterJobsBinding.tvDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    params = new HashMap<>();
                    params.put(Consts.JOB_ID, objects.get(position).getJob_id());
                    params.put(Consts.STATUS, "4");

                    rejectDialog(mContext.getResources().getString(R.string.delete) + " " + objects.get(position).getTitle(), mContext.getResources().getString(R.string.delete_job));
                }
            });
            holder.adapterJobsBinding.tvComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paramsComplete = new HashMap<>();
                    paramsComplete.put(Consts.JOB_ID, objects.get(position).getJob_id());
                    paramsComplete.put(Consts.USER_ID, objects.get(position).getUser_id());

                    completeDialog(mContext.getResources().getString(R.string.complete), mContext.getResources().getString(R.string.complete_job));
                }
            });
        } else {
            MyViewHolderSection view = (MyViewHolderSection) holderMain;
            view.itemSectionBinding.tvSection.setText(objects.get(position).getSection_name());
        }

    }

    @Override
    public int getItemViewType(int position) {
        return this.objects.get(position).isSection() ? VIEW_SECTION : VIEW_ITEM;
    }

    @Override
    public int getItemCount() {

        return objects.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        AdapterJobsBinding adapterJobsBinding;

        public MyViewHolder(AdapterJobsBinding adapterJobsBinding) {
            super(adapterJobsBinding.getRoot());
           this.adapterJobsBinding = adapterJobsBinding;

        }
    }


    public void reject() {

        new HttpsRequest(Consts.DELETE_JOB_API, params, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    dialog_book.dismiss();
                    jobs.getjobs();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void complete() {

        new HttpsRequest(Consts.JOB_COMPLETE_API, paramsComplete, mContext).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    dialog_book.dismiss();
                    jobs.getjobs();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

    public void rejectDialog(String title, String msg) {
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(title)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog_book = dialog;
                            reject();

                        }
                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void completeDialog(String title, String msg) {
        try {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(title)
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog_book = dialog;
                            complete();

                        }
                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        objects.clear();
        if (charText.length() == 0) {
            objects.addAll(postedJobDTOSList);
        } else {
            for (PostedJobDTO postedJobDTO : postedJobDTOSList) {
                if (postedJobDTO.getTitle().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    objects.add(postedJobDTO);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class MyViewHolderSection extends RecyclerView.ViewHolder {
        ItemSectionBinding itemSectionBinding;

        public MyViewHolderSection(ItemSectionBinding itemSectionBinding) {
            super(itemSectionBinding.getRoot());
            this.itemSectionBinding = itemSectionBinding;
        }
    }

}