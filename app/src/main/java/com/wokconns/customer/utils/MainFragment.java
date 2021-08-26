package com.wokconns.customer.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Logger;
import com.isseiaoki.simplecropview.util.Utils;
import com.wokconns.customer.BuildConfig;
import com.wokconns.customer.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by VARUN on 01/01/19.
 */

@RuntimePermissions
public class MainFragment extends FragmentActivity {
    private static final String TAG = MainFragment.class.getName();
    private Uri myUri;
    private Uri resolvedUri;
    private int requestCode;
    private final Context mContext = MainFragment.this;
    private static final int REQUEST_PICK_IMAGE = 10011;
    private static final int REQUEST_SAF_PICK_IMAGE = 10012;
    private static final String PROGRESS_DIALOG = "ProgressDialog";
    private final Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {
            dismissProgress();
            Log.e("", "success");
        }

        @Override
        public void onError(Throwable e) {
            dismissProgress();
            Log.e("", "error");
        }
    };

    private final CropCallback mCropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {
//            Uri saveUri = createSaveUri();
//
//            mCropView.save(cropped)
//                    .compressFormat(mCompressFormat)
//                    .execute(saveUri, mSaveCallback);
        }

        @Override
        public void onError(Throwable e) {
            dismissProgress();
        }
    };

    // Note: only the system can call this constructor by reflection. 
    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override
        public void onError(Throwable e) {
            dismissProgress();
        }

        @Override
        public void onSuccess(@NotNull Uri outputUri) {
            dismissProgress();
            //addPicture.startResultActivity(outputUri);

            Intent i = getIntent();
            i.putExtra("resultUri", outputUri.toString());
            setResult(requestCode, i);
            finish();
        }
    };

//    public static MainFragment getInstance() {
//        MainFragment fragment = new MainFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }

    //    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setRetainInstance(true);
//    }
    // Views ///////////////////////////////////////////////////////////////////////////////////////
    private CropImageView mCropView;

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_base, null, false);
//    }

    //    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        // bind Views
//        bindViews(view);
//        // apply custom font
//        FontUtils.setFont(mRootLayout);
////        mCropView.setDebug(true);
//        // set bitmap to CropImageView
//        if (mCropView.getImageBitmap() == null) {
//            mCropView.setImageResource(R.drawable.dog_five);
//        }
//    }
    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonDone:
                    MainFragmentPermissionsDispatcher.cropImageWithPermissionCheck(MainFragment.this);
                    break;
                case R.id.buttonFitImage:
                    mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
                    break;
                case R.id.button1_1:
                    mCropView.setCropMode(CropImageView.CropMode.SQUARE);
                    break;
                case R.id.button3_4:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                    break;
                case R.id.button4_3:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                    break;
                case R.id.button9_16:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                    break;
                case R.id.button16_9:
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                    break;
                case R.id.buttonCustom:
                    mCropView.setCustomRatio(7, 5);
                    break;
                case R.id.buttonFree:
                    mCropView.setCropMode(CropImageView.CropMode.FREE);
                    break;
                case R.id.buttonCircle:
                    mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
                    break;
                case R.id.buttonShowCircleButCropAsSquare:
                    mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
                    break;
                case R.id.buttonRotateLeft:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                    break;
                case R.id.buttonRotateRight:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                    break;
                case R.id.buttonPickImage:
                    MainFragmentPermissionsDispatcher.pickImageWithPermissionCheck(MainFragment.this);
                    break;
            }
        }
    };
    private LinearLayout mRootLayout;

    // Bind views //////////////////////////////////////////////////////////////////////////////////

    public MainFragment() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        myUri = Uri.parse(getIntent().getExtras().getString("imageUri"));
        requestCode = getIntent().getExtras().getInt("requestCode");
        //ProjectUtils.statusbarBackgroundTrans(MainFragment.this, R.drawable.headergradient);
        // bind Views
        bindViews();
        // apply custom font
        FontUtils.setFont(mRootLayout);
//        mCropView.setDebug(true);
        // set bitmap to CropImageView
//        if (mCropView.getImageBitmap() == null) {
//            mCropView.setImageResource(R.drawable.dog_five);
//        }

//        mCropView.setImageURI(myUri);
        mCropView.startLoad(myUri, mLoadCallback);

        mCropView.setCropMode(CropImageView.CropMode.SQUARE);

//        mCropView.startLoad(Utils.ensureUriPermission(this, getIntent()), mLoadCallback);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            showProgress();
            String urij = result.getData().toString();
            mCropView.startLoad(result.getData(), mLoadCallback);
        } else if (requestCode == REQUEST_SAF_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            showProgress();
            String uri = Utils.ensureUriPermission(this, result).toString();
            mCropView.startLoad(Utils.ensureUriPermission(this, result), mLoadCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void bindViews() {
        mCropView = findViewById(R.id.cropImageView);
        findViewById(R.id.buttonDone).setOnClickListener(btnListener);
        findViewById(R.id.buttonFitImage).setOnClickListener(btnListener);
        findViewById(R.id.button1_1).setOnClickListener(btnListener);
        findViewById(R.id.button3_4).setOnClickListener(btnListener);
        findViewById(R.id.button4_3).setOnClickListener(btnListener);
        findViewById(R.id.button9_16).setOnClickListener(btnListener);
        findViewById(R.id.button16_9).setOnClickListener(btnListener);
        findViewById(R.id.buttonFree).setOnClickListener(btnListener);
        findViewById(R.id.buttonPickImage).setOnClickListener(btnListener);
        findViewById(R.id.buttonRotateLeft).setOnClickListener(btnListener);
        findViewById(R.id.buttonRotateRight).setOnClickListener(btnListener);
        findViewById(R.id.buttonCustom).setOnClickListener(btnListener);
        findViewById(R.id.buttonCircle).setOnClickListener(btnListener);
        findViewById(R.id.buttonShowCircleButCropAsSquare).setOnClickListener(btnListener);
        mRootLayout = findViewById(R.id.layout_root);


    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void cropImage() {
        showProgress();
//        mCropView.crop(myUri).execute(mCropCallback);
        Uri saveUri = createSaveUri();

        mCropView.startCrop(saveUri, mCropCallback, mSaveCallback);
    }

    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void showRationaleForPick(PermissionRequest request) {
        showRationaleDialog(R.string.permission_pick_rationale, request);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void showRationaleForCrop(PermissionRequest request) {
        showRationaleDialog(R.string.permission_crop_rationale, request);
    }

    // Handle button event /////////////////////////////////////////////////////////////////////////

    public void showProgress() {
        ProgressDialogFragment f = ProgressDialogFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(f, PROGRESS_DIALOG)
                .commitAllowingStateLoss();
    }

    // Callbacks ///////////////////////////////////////////////////////////////////////////////////

    public void dismissProgress() {
//        if (!isAdded()) return;
        FragmentManager manager = getSupportFragmentManager();
        if (manager == null) return;
        ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
        }
    }

    public Uri createSaveUri() {
        return createNewUri(mContext, mCompressFormat);
    }

    public static String getDirPath(@Nullable File dir) {
        String dirPath = "";
        File imageDir = null;
        File extStorageDir = dir != null ? dir : Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            imageDir = new File(extStorageDir.getPath() + "/cropped");
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }

    public Uri createNewUri(Context context, Bitmap.CompressFormat format) {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String title = dateFormat.format(today);
        String dirPath = getDirPath(getCacheDir());
//        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        String fileName = "scv_-_" + title + "." + getMimeType(format);
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
        values.put(MediaStore.Images.Media.DATA, path);
        long time = currentTimeMillis / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length());
        }
//
//        Uri uri = FileProvider.getUriForFile(mContext,
//                BuildConfig.APPLICATION_ID + ".provider", file);

        Uri from = Uri.fromFile(file);
        ProjectUtils.log("From URI = " + from);
        return from;
    }

    @NotNull
    public static String getMimeType(@NotNull Bitmap.CompressFormat format) {
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
        }
        return "png";
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton(R.string.button_allow, (dialog, which) -> request.proceed())
                .setNegativeButton(R.string.button_deny, (dialog, which) -> request.cancel())
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }
}