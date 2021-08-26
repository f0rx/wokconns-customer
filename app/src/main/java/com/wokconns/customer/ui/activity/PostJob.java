package com.wokconns.customer.ui.activity;

import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cocosw.bottomsheet.BottomSheet;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.schibstedspain.leku.LocationPickerActivity;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivityPostJobBinding;
import com.wokconns.customer.dto.CategoryDTO;
import com.wokconns.customer.dto.CurrencyDTO;
import com.wokconns.customer.dto.UserDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfaces.Const;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefs;
import com.wokconns.customer.utils.GlideApp;
import com.wokconns.customer.utils.ImageCompression;
import com.wokconns.customer.utils.MainFragment;
import com.wokconns.customer.utils.ProjectUtils;
import com.wokconns.customer.utils.SpinnerDialog;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PostJob extends AppCompatActivity implements View.OnClickListener {
    public static final int MEDIA_TYPE_VIDEO = 6;
    Uri picUri;
    int PICK_FROM_CAMERA = 1, PICK_FROM_GALLERY = 2;
    int CROP_CAMERA_IMAGE = 3, CROP_GALLERY_IMAGE = 4;
    BottomSheet.Builder builder;
    String pathOfImage = "";
    Bitmap bm;
    ImageCompression imageCompression;
    HashMap<String, File> parmsFile = new HashMap<>();
    SimpleDateFormat sdf1, timeZone;
    ActivityPostJobBinding binding;
    String currencyId = "";
    private final String TAG = PostJob.class.getSimpleName();
    private Context mContext;
    private SharedPrefs prefrence;
    private UserDTO userDTO;
    private ArrayList<CategoryDTO> categoryDTOS = new ArrayList<>();
    private final HashMap<String, String> parmsadd = new HashMap<>();
    private final HashMap<String, String> parmsCategory = new HashMap<>();
    private File image;
    private Place place;
    private SpinnerDialog spinnerDialogCate;
    private ArrayList<CurrencyDTO> currencyDTOArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_job);
        mContext = PostJob.this;
        sdf1 = new SimpleDateFormat(Const.DATE_FORMATE_SERVER, Locale.ENGLISH);
        timeZone = new SimpleDateFormat(Const.DATE_FORMATE_TIMEZONE, Locale.ENGLISH);

        prefrence = SharedPrefs.getInstance(mContext);
        userDTO = prefrence.getParentUser(Const.USER_DTO);
        parmsadd.put(Const.USER_ID, userDTO.getUser_id());
        parmsCategory.put(Const.USER_ID, userDTO.getUser_id());
        setUiAction();
    }


    public void setUiAction() {
        binding.ivBack.setOnClickListener(this);
        binding.etDate.setOnClickListener(this);
        binding.tvCategory.setOnClickListener(this);
        binding.etAddress.setOnClickListener(this);
        binding.llPicture.setOnClickListener(this);
        binding.llPost.setOnClickListener(this);
        builder = new BottomSheet.Builder(PostJob.this).sheet(R.menu.menu_cards);
        builder.title(getResources().getString(R.string.take_image));
        builder.listener((dialog, which) -> {
            switch (which) {

                case R.id.camera_cards:
                    if (ProjectUtils.hasPermissionInManifest(PostJob.this, PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                        if (ProjectUtils.hasPermissionInManifest(PostJob.this, PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            try {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                File file = getOutputMediaFile(1);
                                if (!file.exists()) {
                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    //Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.asd", newFile);
                                    picUri = FileProvider.getUriForFile(PostJob.this.getApplicationContext(), PostJob.this.getApplicationContext().getPackageName() + ".fileprovider", file);
                                } else {
                                    picUri = Uri.fromFile(file); // create
                                }


                                prefrence.setValue(Const.IMAGE_URI_CAMERA, picUri.toString());
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the image file
                                startActivityForResult(intent, PICK_FROM_CAMERA);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    break;
                case R.id.gallery_cards:
                    if (ProjectUtils.hasPermissionInManifest(PostJob.this, PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                        if (ProjectUtils.hasPermissionInManifest(PostJob.this, PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            File file = getOutputMediaFile(1);
                            if (!file.exists()) {
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            picUri = Uri.fromFile(file);

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), PICK_FROM_GALLERY);

                        }
                    }
                    break;
                case R.id.cancel_cards:
                    builder.setOnDismissListener(dialog1 -> dialog1.dismiss());
                    break;
            }
        });

        binding.etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1 && s.toString().startsWith("0")) {
                    s.clear();
                }
            }
        });

        binding.etCurrency.setOnClickListener(v -> binding.etCurrency.showDropDown());

        binding.etCurrency.setOnItemClickListener((parent, view, position, id) -> {
            binding.etCurrency.showDropDown();
            CurrencyDTO currencyDTO = (CurrencyDTO) parent.getItemAtPosition(position);
            Log.e(TAG, "onItemClick: " + currencyDTO.getCurrency_symbol());

            currencyId = currencyDTO.getId();
            parmsadd.put(Const.CURRENCY_ID, currencyId);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llPicture:
                binding.cardview1.setVisibility(View.VISIBLE);
                builder.show();
                break;
            case R.id.llPost:
                submitForm();
                break;
            case R.id.etAddress:
                findPlace();
                break;
            case R.id.ivBack:
                finish();
                break;
            case R.id.etDate:
                clickScheduleDateTime();
                break;
            case R.id.tvCategory:
                if (categoryDTOS.size() > 0 || categoryDTOS != null) {
                    spinnerDialogCate.showSpinerDialog();
                } else {
                    ProjectUtils.showLong(mContext, getResources().getString(R.string.no_cate_found));
                }
                break;
        }
    }

    public void submitForm() {
        if (!validateCategory()) {
            return;
        } else if (!validateTitle()) {
            return;
        } else if (!validatePrice()) {
            return;
        } else if (!validateCurrency()) {
            return;
        } else if (!validateAddress()) {
            return;
        } else if (!validateDate()) {
            return;
        } else if (!validateComment()) {
            return;
        } else {
            if (NetworkManager.isConnectToInternet(mContext)) {
                addPost();

            } else {
                ProjectUtils.showLong(mContext, getResources().getString(R.string.internet_connection));
            }
        }
    }

    public void clickScheduleDateTime() {
        new SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()
                .defaultDate(new Date())
                .mustBeOnFuture()
                .listener(date -> {
                    parmsadd.put(Const.JOB_DATE, sdf1.format(date).toUpperCase());


                    binding.etDate.setText(sdf1.format(date).toUpperCase());
                })
                .display();
    }


    private File getOutputMediaFile(int type) {
        String root = Environment.getExternalStorageDirectory().toString();

        File mediaStorageDir = new File(root, Const.APP_NAME);

        /**Create the storage directory if it does not exist*/
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    Const.APP_NAME + timeStamp + ".png");

        } else {
            return null;
        }

        return mediaFile;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CROP_CAMERA_IMAGE) {

            if (data != null) {
                picUri = Uri.parse(data.getExtras().getString("resultUri"));

                try {
                    //bitmap = MediaStore.Images.Media.getBitmap(PostJob.this.getContentResolver(), resultUri);
                    pathOfImage = picUri.getPath();
                    imageCompression = new ImageCompression(PostJob.this);
                    imageCompression.execute(pathOfImage);
                    imageCompression.setOnTaskFinishedEvent(imagePath -> {
                        GlideApp.with(mContext).load("file://" + imagePath)
                                .thumbnail(0.5f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(binding.ivImg);

                        pathOfImage = imagePath;
                        binding.ivImg.setVisibility(View.VISIBLE);
                        image = new File(imagePath);
                        parmsFile.put(Const.AVTAR, image);
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        if (requestCode == CROP_GALLERY_IMAGE) {

            if (data != null) {
                picUri = Uri.parse(data.getExtras().getString("resultUri"));
                Log.e("image 1", picUri + "");
                try {
                    bm = MediaStore.Images.Media.getBitmap(PostJob.this.getContentResolver(), picUri);
                    pathOfImage = picUri.getPath();
                    imageCompression = new ImageCompression(PostJob.this);
                    imageCompression.execute(pathOfImage);
                    imageCompression.setOnTaskFinishedEvent(imagePath -> {

                        GlideApp.with(mContext).load("file://" + imagePath)
                                .thumbnail(0.5f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(binding.ivImg);
                        image = new File(imagePath);

                        pathOfImage = imagePath;
                        binding.ivImg.setVisibility(View.VISIBLE);
                        parmsFile.put(Const.AVTAR, image);

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
            if (picUri != null) {

                picUri = Uri.parse(prefrence.getValue(Const.IMAGE_URI_CAMERA));
                // image = new File(ConvertUriToFilePath.getPathFromURI(PostJob.this, picUri));
                startCropping(picUri, CROP_CAMERA_IMAGE);
            } else {
                picUri = Uri.parse(prefrence.getValue(Const.IMAGE_URI_CAMERA));
                // image = new File(ConvertUriToFilePath.getPathFromURI(PostJob.this, picUri));

                startCropping(picUri, CROP_CAMERA_IMAGE);
            }
        }


        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                Uri tempUri = data.getData();

                Log.e("front tempUri", "" + tempUri);
                if (tempUri != null) {
                    //    image = new File(ConvertUriToFilePath.getPathFromURI(PostJob.this, tempUri));
                    Log.e("image 2", image + "");
                    startCropping(tempUri, CROP_GALLERY_IMAGE);
                } else {

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                try {
                    getAddress(data.getDoubleExtra(LATITUDE, 0.0), data.getDoubleExtra(LONGITUDE, 0.0));


                } catch (Exception e) {

                }
            }
        }

    }


    public void startCropping(Uri uri, int requestCode) {

        Intent intent = new Intent(PostJob.this, MainFragment.class);
        intent.putExtra("imageUri", uri.toString());
        intent.putExtra("requestCode", requestCode);
        startActivityForResult(intent, requestCode);
    }


    public boolean validateComment() {
        if (!ProjectUtils.isEditTextFilled(binding.etCommet)) {
            binding.etCommet.setError(getResources().getString(R.string.val_des));
            binding.etCommet.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validateAddress() {
        if (!ProjectUtils.isEditTextFilled(binding.etAddress)) {
            binding.etAddress.setError(getResources().getString(R.string.val_address));
            binding.etAddress.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validateDate() {
        if (!ProjectUtils.isEditTextFilled(binding.etDate)) {
            binding.etDate.setError(getResources().getString(R.string.val_date));
            binding.etDate.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validateCategory() {

        if (binding.tvCategory.getText().toString().trim().equalsIgnoreCase("ALL CATEGORIES")) {
            binding.tvCategory.setError(getResources().getString(R.string.val_category));
            binding.tvCategory.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validateTitle() {
        if (!ProjectUtils.isEditTextFilled(binding.etTitle)) {
            binding.etTitle.setError(getResources().getString(R.string.val_title));
            binding.etTitle.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validatePrice() {
        if (!ProjectUtils.isEditTextFilled(binding.etPrice)) {
            binding.etPrice.setError(getResources().getString(R.string.val_price));
            binding.etPrice.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public boolean validateCurrency() {
        if (!ProjectUtils.isEditTextFilled(binding.etCurrency)) {
            binding.etCurrency.setError(getResources().getString(R.string.val_currency));
            binding.etCurrency.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public void addPost() {
        parmsadd.put(Const.TITLE, ProjectUtils.getEditTextValue(binding.etTitle));
        parmsadd.put(Const.PRICE, ProjectUtils.getEditTextValue(binding.etPrice));
        parmsadd.put(Const.DESCRIPTION, ProjectUtils.getEditTextValue(binding.etCommet));
        parmsadd.put(Const.ADDRESS, ProjectUtils.getEditTextValue(binding.etAddress));
        ProjectUtils.showProgressDialog(mContext, false, getResources().getString(R.string.please_wait));

        new HttpsRequest(Const.POST_JOB_API, parmsadd, parmsFile, mContext).imagePost(TAG, (flag, msg, response) -> {
            ProjectUtils.pauseProgressDialog();
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

    private void findPlace() {
        Intent locationPickerIntent = new LocationPickerActivity.Builder()
                .withGooglePlacesEnabled()
                //.withLocation(41.4036299, 2.1743558)
                .build(mContext);

        startActivityForResult(locationPickerIntent, 101);
    }


    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();
            Log.e("IGA", "Address" + add);
            binding.etAddress.setText(obj.getAddressLine(0));

            parmsadd.put(Const.LATI, String.valueOf(lat));
            parmsadd.put(Const.LONGI, String.valueOf(lng));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkManager.isConnectToInternet(mContext)) {
            getCategory();
            getCurrencyValue();
        } else {
            ProjectUtils.showLong(mContext, getResources().getString(R.string.internet_connection));
        }
    }

    public void getCategory() {
        new HttpsRequest(Const.GET_ALL_CATEGORY_API, parmsCategory, mContext).stringPost(TAG, (flag, msg, response) -> {
            if (flag) {
                try {
                    categoryDTOS = new ArrayList<>();
                    Type getpetDTO = new TypeToken<List<CategoryDTO>>() {
                    }.getType();
                    categoryDTOS = new Gson().fromJson(response.getJSONArray("data").toString(), getpetDTO);

                    spinnerDialogCate = new SpinnerDialog((Activity) mContext, categoryDTOS, getResources().getString(R.string.select_category));// With 	Animation
                    spinnerDialogCate.bindOnSpinerListener((item, id, position) -> {
                        binding.tvCategory.setText(item);
                        parmsadd.put(Const.CATEGORY_ID, id);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                ProjectUtils.showToast(mContext, msg);
            }
        });
    }


    public void getCurrencyValue() {

        new HttpsRequest(Const.GET_CURRENCY_API, mContext).stringGet(TAG, (flag, msg, response) -> {
            if (flag) {
                try {
                    currencyDTOArrayList = new ArrayList<>();
                    final Type getCurrencyDTO = new TypeToken<List<CurrencyDTO>>() {
                    }.getType();
                    currencyDTOArrayList = new Gson().fromJson(response.getJSONArray("data").toString(), getCurrencyDTO);

                    try {
                        ArrayAdapter<CurrencyDTO> currencyAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, currencyDTOArrayList);
                        binding.etCurrency.setAdapter(currencyAdapter);
                        binding.etCurrency.setCursorVisible(false);

                        // Initialize value with a default
                        CurrencyDTO naira = null;
                        // Loop thru then find Naira (NGN)
                        for (CurrencyDTO el : currencyDTOArrayList)
                            if (Objects.equals(el.getCode(), "NGN")) naira = el;

                        CurrencyDTO finalNaira = naira;
                        binding.etCurrency.postDelayed(() -> {
                            binding.etCurrency.showDropDown();
                            binding.etCurrency.setText(String.format("%s", finalNaira), false);
                            binding.etCurrency.setSelection(binding.etCurrency.getText().length());

                            currencyId = finalNaira.getId();
                            parmsadd.put(Const.CURRENCY_ID, currencyId);
                        }, 500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                ProjectUtils.showToast(mContext, msg);
            }
        });
    }
}
