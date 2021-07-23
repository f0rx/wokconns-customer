package com.wokconns.customer.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.wokconns.customer.R;
import com.wokconns.customer.dto.HistoryDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfaces.Consts;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.utils.CustomButton;
import com.wokconns.customer.utils.CustomEditText;
import com.wokconns.customer.utils.CustomTextView;
import com.wokconns.customer.utils.ProjectUtils;

import java.util.HashMap;

public class WriteReview extends AppCompatActivity implements View.OnClickListener {
    private String TAG = WriteReview.class.getSimpleName();
    private RatingBar rbReview;
    private CustomTextView tvCharReview;
    private CustomEditText yourReviewET;
    private CustomButton btnSubmit;
    private Context mContext;
    private float myrating;
    private String id = "";
    private HashMap<String, String> parms = new HashMap<>();
    private ImageView ivBack;
    private SharedPrefrence prefrence;
    private UserDTO userDTO;
    private HistoryDTO historyDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        mContext = WriteReview.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        if (getIntent().hasExtra(Consts.HISTORY_DTO)) {
            historyDTO = (HistoryDTO) getIntent().getSerializableExtra(Consts.HISTORY_DTO);
        }
        parms.put(Consts.USER_ID, userDTO.getUser_id());
        parms.put(Consts.ARTIST_ID, historyDTO.getArtist_id());
        parms.put(Consts.BOOKING_ID, historyDTO.getBooking_id());
        init();
    }

    public void init() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        rbReview = (RatingBar) findViewById(R.id.rbReview);
        tvCharReview = (CustomTextView) findViewById(R.id.tvCharReview);
        yourReviewET = (CustomEditText) findViewById(R.id.yourReviewET);
        btnSubmit = (CustomButton) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        /*
         *
         * handling rating bar
         *
         */

        rbReview.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> myrating = ratingBar.getRating());

        yourReviewET.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                tvCharReview.setText(String.valueOf(s.length()) + "/200");

            }
        });

    }

    public void submit() {
        if (!validateReview()) {
            return;
        } else {
            sendReview();
        }
    }

    /////checking validation
    public boolean validateReview() {
        if (yourReviewET.getText().toString().trim().length() <= 0) {
            yourReviewET.setError(getResources().getString(R.string.val_comment));
            yourReviewET.requestFocus();
            return false;
        } else {
            yourReviewET.setError(null);
            yourReviewET.clearFocus();
            return true;
        }
    }


    /*
     *
     * method  onclick()  is handling  the button event
     *
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                if (NetworkManager.isConnectToInternet(mContext)) {
                    submit();
                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_connection));
                }

                break;
            case R.id.ivBack:
                finish();
                break;
        }
    }
    /*
     *
     * method  sendReview()  send reviews of users  to server
     *
     */

    public void sendReview() {
        parms.put(Consts.RATING, String.valueOf(myrating));
        parms.put(Consts.COMMENT, ProjectUtils.getEditTextValue(yourReviewET));
        new HttpsRequest(Consts.ADD_RATING_API, parms, mContext).stringPost(TAG, (flag, msg, response) -> {
            if (flag) {
                ProjectUtils.showLong(mContext, msg);
                finish();
            } else {
                ProjectUtils.showLong(mContext, msg);
            }
        });
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }
}
