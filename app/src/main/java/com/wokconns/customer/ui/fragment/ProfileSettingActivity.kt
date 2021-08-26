package com.wokconns.customer.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cocosw.bottomsheet.BottomSheet
import com.google.gson.Gson
import com.schibstedspain.leku.*
import com.wokconns.customer.R
import com.wokconns.customer.databinding.ActivityProfileSettingBinding
import com.wokconns.customer.databinding.DailogAddressBinding
import com.wokconns.customer.databinding.DailogPersonalInfoBinding
import com.wokconns.customer.dto.UserDTO
import com.wokconns.customer.https.HttpsRequest
import com.wokconns.customer.interfaces.Const
import com.wokconns.customer.interfaces.Helper
import com.wokconns.customer.network.NetworkManager
import com.wokconns.customer.preferences.SharedPrefrence
import com.wokconns.customer.ui.activity.BaseActivity
import com.wokconns.customer.utils.*
import com.wokconns.customer.utils.ProjectUtils.formatImageUri
import com.wokconns.customer.utils.ProjectUtils.getEditTextValue
import com.wokconns.customer.utils.ProjectUtils.hasPermissionInManifest
import com.wokconns.customer.utils.ProjectUtils.isPasswordValid
import com.wokconns.customer.utils.ProjectUtils.log
import com.wokconns.customer.utils.ProjectUtils.pauseProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showToast
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ProfileSettingActivity : Fragment(), View.OnClickListener {
    companion object {
        const val PICK_FROM_CAMERA = 189
        const val PICK_FROM_GALLERY = 225
        const val CROP_CAMERA_IMAGE = 305
        const val CROP_GALLERY_IMAGE = 411
    }

    private val TAG: String = ProfileSettingActivity::class.java.name
    private lateinit var rg_gender_options: RadioGroup
    private lateinit var rb_gender_female: RadioButton
    private lateinit var rb_gender_male: RadioButton
    private lateinit var ivCloseAddress: ImageView
    private lateinit var ivCloseInfo: ImageView
    private lateinit var builder: BottomSheet.Builder
    private var picUri: Uri? = null
    private lateinit var imageName: String
    private var pathOfImage: String? = null
    private lateinit var bm: Bitmap
    private lateinit var imageCompression: ImageCompression
    private lateinit var resultByteArray: ByteArray
    private var file: File? = null
    private lateinit var bitmap: Bitmap
    private lateinit var binding: ActivityProfileSettingBinding
    private lateinit var dailogPersonalInfoBinding: DailogPersonalInfoBinding
    private lateinit var dailogAddressBinding: DailogAddressBinding
    private lateinit var dialog_profile: Dialog
    private var dialog_pass: Dialog? = null
    private var dialog_address: Dialog? = null
    private lateinit var tvYes: CustomTextViewBold
    private lateinit var tvNo: CustomTextViewBold
    private lateinit var tvYesPass: CustomTextViewBold
    private lateinit var tvYesAddress: CustomTextViewBold
    private lateinit var etNameD: CustomEditText
    private lateinit var etEmailD: CustomEditText
    private lateinit var etMobileD: CustomEditText
    private lateinit var etOldPassD: CustomEditText
    private lateinit var etNewPassD: CustomEditText
    private lateinit var etConfrimPassD: CustomEditText
    private lateinit var etAddressD: CustomEditText
    private lateinit var etCityD: CustomEditText
    private lateinit var etCountryD: CustomEditText
    private var params: HashMap<String, String?>? = null
    private lateinit var RRsncbar: RelativeLayout
    private var preference: SharedPrefrence? = null
    private var userDTO: UserDTO? = null
    private val paramsFile = HashMap<String, File>()
    private lateinit var mView: View
    private lateinit var baseActivity: BaseActivity
    private val paramsDeleteImg = HashMap<String, String?>()
    private var lats = 0.0
    private var longs = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.activity_profile_setting, container, false)
        preference = SharedPrefrence.getInstance(activity)
        baseActivity.headerNameTV.text = resources.getString(R.string.profile_settings)

        setUiAction()

        return binding.root
    }

    fun setUiAction() {
        binding.ivPersonalInfoChange.setOnClickListener(this)
        binding.ivAddressChange.setOnClickListener(this)
        binding.llProfilePhoto.setOnClickListener(this)
        binding.llImage.setOnClickListener(this)

        showData()

        builder = BottomSheet.Builder(requireActivity()).sheet(R.menu.menu_cards)
        builder.title(resources.getString(R.string.take_image))
        builder.listener { _: DialogInterface?, which: Int ->
            when (which) {
                R.id.camera_cards -> if (hasPermissionInManifest(
                        activity, PICK_FROM_CAMERA, Manifest.permission.CAMERA
                    )
                ) {
                    if (hasPermissionInManifest(
                            activity,
                            PICK_FROM_GALLERY,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        try {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                            val file = getOutputMediaFile(1) ?: return@listener

                            if (!file.exists()) {
                                try {
                                    pauseProgressDialog()
                                    file.createNewFile()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                            picUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                //Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.asd", newFile);
                                FileProvider.getUriForFile(
                                    requireActivity().applicationContext,
                                    requireActivity().applicationContext.packageName + ".fileprovider",
                                    file
                                )
                            } else {
                                Uri.fromFile(file) // create
                            }
                            preference?.setValue(Const.IMAGE_URI_CAMERA, picUri.toString())
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri) // set the image file
                            startActivityForResult(intent, PICK_FROM_CAMERA)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                R.id.gallery_cards -> if (hasPermissionInManifest(
                        activity,
                        PICK_FROM_CAMERA,
                        Manifest.permission.CAMERA
                    )
                ) {
                    if (hasPermissionInManifest(
                            activity,
                            PICK_FROM_GALLERY,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    ) {
                        val file = getOutputMediaFile(1) ?: return@listener

                        if (!file.exists()) {
                            try {
                                file.createNewFile()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                        picUri = Uri.fromFile(file)
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(
                            Intent.createChooser(
                                intent,
                                resources.getString(R.string.select_picture)
                            ), PICK_FROM_GALLERY
                        )
                    }
                }
                R.id.cancel_cards -> builder.setOnDismissListener { obj: DialogInterface -> obj.dismiss() }
            }
        }
    }

    private fun getOutputMediaFile(type: Int): File? {
        val root = requireActivity().externalCacheDir?.path
        val mediaStorageDir = File(root, Const.APP_NAME)
        /*Create the storage directory if it does not exist*/
        if (!mediaStorageDir.exists())
            if (!mediaStorageDir.mkdirs()) return null

        /*Create a media file name*/
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
            Date()
        )
        val mediaFile: File
        if (type == 1) {
            mediaFile = File(
                mediaStorageDir.path + File.separator +
                        Const.APP_NAME + "_-_" + timeStamp + ".jpg"
            )
            imageName = Const.APP_NAME + "_-_" + timeStamp + ".jpg"
        } else {
            return null
        }
        return mediaFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CROP_CAMERA_IMAGE) {
            if (data != null) {

                picUri = Uri.parse(data.extras?.getString("resultUri"))

                try {
                    //bitmap = MediaStore.Images.Media.getBitmap(SaveDetailsActivityNew.this.getContentResolver(), resultUri);
                    pathOfImage = picUri?.path
                    imageCompression = ImageCompression(activity)
                    imageCompression.execute(pathOfImage)
                    imageCompression.setOnTaskFinishedEvent { imagePath: String ->

                        GlideApp.with(requireActivity()).load("file://$imagePath")
                            .thumbnail(0.5f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.ivProfile)

                        try {
                            // bitmap = MediaStore.Images.Media.getBitmap(SaveDetailsActivityNew.this.getContentResolver(), resultUri);
                            file = File(imagePath)
                            paramsFile[Const.IMAGE] = file!!

                            log("image", imagePath)

                            params = HashMap()
                            userDTO?.user_id?.let { params?.put(Const.USER_ID, it) }

                            if (NetworkManager.isConnectToInternet(activity)) {
                                updateProfile(params)
                            } else {
                                showToast(
                                    activity,
                                    resources.getString(R.string.internet_connection)
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (requestCode == CROP_GALLERY_IMAGE) {
            if (data != null) {
                picUri = Uri.parse(data.extras?.getString("resultUri"))
                try {
                    bm =
                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, picUri)

                    pathOfImage = picUri?.path
                    imageCompression = ImageCompression(activity)
                    imageCompression.execute(pathOfImage)
                    imageCompression.setOnTaskFinishedEvent { imagePath: String ->
                        GlideApp.with(requireActivity()).load(imagePath)
                            .thumbnail(0.5f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.ivProfile)

                        log("image", imagePath)

                        try {
                            file = File(imagePath)
                            paramsFile[Const.IMAGE] = file!!
                            params = HashMap()
                            userDTO?.user_id?.let { params?.put(Const.USER_ID, it) }

                            if (NetworkManager.isConnectToInternet(activity)) {
                                updateProfile(params)
                            } else {
                                showToast(
                                    activity,
                                    resources.getString(R.string.internet_connection)
                                )
                            }
                            log("image", imagePath)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (requestCode == PICK_FROM_CAMERA && resultCode == Activity.RESULT_OK) {
            if (picUri != null) {
                picUri = Uri.parse(preference?.getValue(Const.IMAGE_URI_CAMERA))
                startCropping(picUri, CROP_CAMERA_IMAGE)
            } else {
                picUri = Uri.parse(
                    preference?.getValue(Const.IMAGE_URI_CAMERA)
                )
                startCropping(picUri, CROP_CAMERA_IMAGE)
            }
        }
        if (requestCode == PICK_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                val tempUri = data?.data

                log("front tempUri", "" + tempUri)

                if (tempUri != null) {
                    startCropping(tempUri, CROP_GALLERY_IMAGE)
                } else {
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    assert(data != null)
                    getAddress(
                        data?.getDoubleExtra(LATITUDE, 0.0),
                        data?.getDoubleExtra(LONGITUDE, 0.0)
                    )
                } catch (ignored: Exception) {
                }
            }
        }
    }

    fun startCropping(uri: Uri?, requestCode: Int) {
        val intent = Intent(activity, MainFragment::class.java)
        intent.putExtra("imageUri", uri.toString())
        intent.putExtra("requestCode", requestCode)
        startActivityForResult(intent, requestCode)
    }

    fun showData() {
        userDTO = preference?.getParentUser(Const.USER_DTO)

        GlideApp.with(requireActivity())
            .load(formatImageUri(userDTO?.image))
            .placeholder(R.drawable.dummyuser_image)
//            .useAnimationPool(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.ivProfile)

        binding.tvName.text = userDTO?.name
        binding.tvFullName.text = userDTO?.name
        binding.tvEmail.text = userDTO?.email_id
        binding.tvMobile.text = userDTO?.mobile

        if (userDTO != null) {
            when {
                userDTO?.gender.equals("0", ignoreCase = true) -> {
                    binding.tvGender.text = resources.getString(R.string.female)
                }
                userDTO?.gender.equals("1", ignoreCase = true) -> {
                    binding.tvGender.text = resources.getString(R.string.male)
                }
                else -> {
                    binding.tvGender.text = ""
                }
            }
        }

        binding.tvAddressValue.text = userDTO?.address
        binding.tvCityValue.text = userDTO?.city
        binding.tvCountryValue.text = userDTO?.country
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ll_profile_photo -> builder.show()
            R.id.ivPersonalInfoChange -> dialogPersonalProfile()
            R.id.ivAddressChange -> if (NetworkManager.isConnectToInternet(activity)) {
                dialogAddress()
            } else {
                showToast(activity, resources.getString(R.string.internet_connection))
            }
            R.id.ll_image -> if (baseActivity.drawer.isDrawerVisible(GravityCompat.START)) {
                baseActivity.drawer.closeDrawer(GravityCompat.START)
            } else {
                baseActivity.drawer.openDrawer(GravityCompat.START)
            }
        }
    }

    private fun dialogPersonalProfile() {
        dialog_profile = Dialog(requireActivity() /*, android.R.style.Theme_Dialog*/)
        dialog_profile.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog_profile.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog_profile.setContentView(R.layout.dailog_personal_info)
        rg_gender_options = dialog_profile.findViewById(R.id.rg_gender_options)
        rb_gender_male = dialog_profile.findViewById(R.id.rb_gender_male)
        rb_gender_female = dialog_profile.findViewById(R.id.rb_gender_female)
        etNameD = dialog_profile.findViewById(R.id.etNameD)
        etEmailD = dialog_profile.findViewById(R.id.etEmailD)
        etMobileD = dialog_profile.findViewById(R.id.etMobileD)
        ivCloseInfo = dialog_profile.findViewById(R.id.iv_close)
        etNameD.setText(userDTO?.name)
        etEmailD.setText(userDTO?.email_id)
        etMobileD.setText(userDTO?.mobile)

        when {
            userDTO?.gender.equals("0", ignoreCase = true) -> {
                rb_gender_female.isChecked = true
                rb_gender_male.isChecked = false
            }
            userDTO?.gender.equals("1", ignoreCase = true) -> {
                rb_gender_female.isChecked = false
                rb_gender_male.isChecked = true
            }
            else -> {
                rb_gender_female.isChecked = false
                rb_gender_male.isChecked = false
            }
        }

        tvYes = dialog_profile.findViewById(R.id.tvYes)
        dialog_profile.show()
        dialog_profile.setCancelable(false)
        ivCloseInfo.setOnClickListener { dialog_profile.dismiss() }
        tvYes.setOnClickListener {
            params = HashMap()
            userDTO?.user_id?.let { params?.put(Const.USER_ID, it) }
            params?.put(Const.NAME, getEditTextValue(etNameD))
            params?.put(Const.MOBILE, getEditTextValue(etMobileD))
            if (rb_gender_female.isChecked) {
                params?.put(Const.GENDER, "0")
            } else {
                params?.put(Const.GENDER, "1")
            }
            if (NetworkManager.isConnectToInternet(activity)) {
                updateProfile(params)
                dialog_profile.dismiss()
            } else {
                showToast(activity, resources.getString(R.string.internet_connection))
            }
        }
    }

    private fun dialogAddress() {
        dialog_address = Dialog(requireActivity() /*, android.R.style.Theme_Dialog*/)
        dialog_address?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog_address?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog_address?.setContentView(R.layout.dailog_address)
        ivCloseAddress = dialog_address!!.findViewById(R.id.iv_close)
        etAddressD = dialog_address!!.findViewById(R.id.etAddressD)
        etCityD = dialog_address!!.findViewById(R.id.etCityD)
        etCountryD = dialog_address!!.findViewById(R.id.etCountryD)
        etAddressD.setText(userDTO?.address)
        etCityD.setText(userDTO?.city)
        etCountryD.setText(userDTO?.country)
        etCountryD.setText(userDTO?.country)
        tvYesAddress = dialog_address!!.findViewById(R.id.tvYesAddress)
        dialog_address?.show()
        dialog_address?.setCancelable(false)
        etAddressD.setOnClickListener { findPlace() }
        ivCloseAddress.setOnClickListener { dialog_address?.dismiss() }
        tvYesAddress.setOnClickListener {
            params = HashMap()
            userDTO?.user_id?.let { params?.put(Const.USER_ID, it) }
            params?.put(Const.ADDRESS, getEditTextValue(etAddressD))
            //            params.put(Consts.OFFICE_ADDRESS, ProjectUtils.getEditTextValue(etAddressDs));
            params?.put(Const.CITY, getEditTextValue(etCityD))
            params?.put(Const.COUNTRY, getEditTextValue(etCountryD))
            params?.put(Const.LATITUDE, lats.toString())
            params?.put(Const.LONGITUDE, longs.toString())

            if (NetworkManager.isConnectToInternet(activity)) {
                updateProfile(params)
                dialog_address?.dismiss()
            } else {
                showToast(activity, resources.getString(R.string.internet_connection))
            }
        }
    }

    private fun updateProfile(params: HashMap<String, String?>?) {
        showProgressDialog(requireActivity(), true, resources.getString(R.string.please_wait))

        HttpsRequest(Const.UPDATE_PROFILE_IMAGE, params, paramsFile, requireActivity()).imagePost(
            TAG,
            object : Helper {
                override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                    pauseProgressDialog()
                    if (flag) {
                        try {
                            showToast(
                                requireActivity(),
                                msg?.ifEmpty { "Profile image updated successfully" }
                            )

                            userDTO = Gson().fromJson(
                                response?.getJSONObject("user").toString(),
                                UserDTO::class.java
                            )
                            preference?.setParentUser(userDTO, Const.USER_DTO)

                            baseActivity.showImage()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        showToast(activity, msg)
                    }
                }
            }
        )
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        baseActivity = activity as BaseActivity
    }

    fun deleteImage() {
        showProgressDialog(activity, true, resources.getString(R.string.please_wait))

        userDTO?.user_id?.let { paramsDeleteImg.put(Const.USER_ID, it) }

        HttpsRequest(
            Const.DELETE_PROFILE_IMAGE_API,
            paramsDeleteImg,
            requireActivity()
        ).stringPost(TAG, object : Helper {
            override fun backResponse(flag: Boolean, msg: String?, response: JSONObject?) {
                pauseProgressDialog()

                if (flag) {
                    userDTO?.image = ""
                    preference?.setParentUser(userDTO, Const.USER_DTO)
                    showData()
                } else {
                    showToast(activity, msg)
                }
            }
        })
    }

    private fun findPlace() {
        val locationPickerIntent: Intent = LocationPickerActivity.Builder()
            .withGooglePlacesEnabled() //.withLocation(41.4036299, 2.1743558)
            .build(requireContext())
        startActivityForResult(locationPickerIntent, 101)
    }

    private fun getAddress(lat: Double?, lng: Double?) {
        val geocoder = Geocoder(activity, Locale.getDefault())

        if (lat == null || lng == null) return

        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val obj = addresses[0]
            var add = obj.getAddressLine(0)
            add = """
                $add
                ${obj.countryName}
                """.trimIndent()
            add = """
                $add
                ${obj.countryCode}
                """.trimIndent()
            add = """
                $add
                ${obj.adminArea}
                """.trimIndent()
            add = """
                $add
                ${obj.postalCode}
                """.trimIndent()
            add = """
                $add
                ${obj.subAdminArea}
                """.trimIndent()
            add = """
                $add
                ${obj.locality}
                """.trimIndent()
            add = """
                $add
                ${obj.subThoroughfare}
                """.trimIndent()

            log("IGA", "Address$add")

            etAddressD.setText(obj.getAddressLine(0))
            lats = lat
            longs = lng
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    private fun Submit() {
        if (!passwordValidation()) {
            return
        } else if (!confirmPassword()) {
            return
        } else {
            if (NetworkManager.isConnectToInternet(activity)) {
                updateProfile(params)
                dialog_pass?.dismiss()
            } else {
                showToast(activity, resources.getString(R.string.internet_connection))
            }
        }
    }

    private fun passwordValidation(): Boolean {
        return if (!isPasswordValid(etOldPassD.text.toString().trim { it <= ' ' })) {
            etOldPassD.error = resources.getString(R.string.val_pass_c)
            etOldPassD.requestFocus()
            false
        } else if (!isPasswordValid(etNewPassD.text.toString().trim { it <= ' ' })) {
            etNewPassD.error = resources.getString(R.string.val_pass_c)
            etNewPassD.requestFocus()
            false
        } else true
    }

    private fun confirmPassword(): Boolean {
        if (etNewPassD.text.toString().trim { it <= ' ' } == "") {
            etNewPassD.error = resources.getString(R.string.val_new_pas)
            return false
        } else if (etConfrimPassD.text.toString().trim { it <= ' ' } == "") {
            etConfrimPassD.error = resources.getString(R.string.val_c_pas)
            return false
        } else if (etNewPassD.text.toString().trim { it <= ' ' } != etConfrimPassD.text.toString()
                .trim { it <= ' ' }) {
            etConfrimPassD.error = resources.getString(R.string.val_n_c_pas)
            return false
        }
        return true
    }
}