package com.wokconns.customer.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.text.format.DateUtils
import android.text.format.Time
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wokconns.customer.R
import java.net.URL
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object ProjectUtils {
    const val TAG = "ProjectUtility"
    private const val VERSION_UNAVAILABLE = "N/A"
    private var bmp: Bitmap? = null
    private var dialog: AlertDialog? = null
    private var toast: Toast? = null
    private var mProgressDialog: ProgressDialog? = null
    private val dialog_gif: Dialog? = null

    val screenWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels
    val screenHeight: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    //For Changing Status Bar Color if Device is above 5.0(Lollipop)
    @JvmStatic
    fun changeStatusBarColor(activity: Activity) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = activity.resources.getColor(R.color.status_bar_color)
        }
    }

    @JvmStatic
    fun changeStatusBarColorNew(activity: Activity, color: Int) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = activity.resources.getColor(color)
        }
    }

    @JvmStatic
    fun Fullscreen(activity: Activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
        activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    @JvmStatic
    fun statusbarBackgroundTrans(activity: Activity, drawable: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            @SuppressLint("UseCompatLoadingForDrawables") val background =
                activity.resources.getDrawable(drawable)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            // window.setNavigationBarColor(activity.getResources().getColor(R.color.transparent));
            window.setBackgroundDrawable(background)
        }
    }

    @JvmStatic
    fun statusbarBackgroundTransformURL(activity: Activity, imageurl: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val url: URL?
            try {
                url = URL(imageurl)
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val window = activity.window
            val background: Drawable = BitmapDrawable(activity.resources, bmp)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = activity.resources.getColor(android.R.color.transparent)
            // window.setNavigationBarColor(activity.getResources().getColor(R.color.transparent));
            window.setBackgroundDrawable(background)
        }
    }

    @JvmStatic
    fun hasPermissionInManifest(
        activity: Activity?,
        requestCode: Int,
        permissionName: String
    ): Boolean {
        if (activity == null) return false

        if (ContextCompat.checkSelfPermission(
                activity,
                permissionName
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(
                activity, arrayOf(permissionName),
                requestCode
            )
        } else {
            return true
        }
        return false
    }

    //For Progress Dialog
    @JvmStatic
    fun getProgressDialog(context: Context): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.setMessage(context.resources.getString(R.string.please_wait))
        return progressDialog
    }

    //For Long Period Toast Message
    @JvmStatic
    fun showLong(context: Context?, message: String?) {
        if (message == null) {
            return
        }
        if (message.isNotEmpty()) {
            if (toast == null && context != null) {
                toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            }
            if (toast != null) {
                toast?.setText(message)
                toast?.show()
            }
        }
    }

    @JvmStatic
    fun containsOnlyNumbers(str: String): Boolean {
        for (element in str) {
            if (!Character.isDigit(element)) return false
        }
        return true
    }

    // For Alert Dialog in App
    @JvmStatic
    fun createDialog(
        context: Context?, titleId: Int, messageId: Int,
        positiveButtonListener: DialogInterface.OnClickListener?,
        negativeButtonListener: DialogInterface.OnClickListener?
    ): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(titleId)
        builder.setMessage(messageId)
        builder.setPositiveButton(R.string.ok, positiveButtonListener)
        builder.setNegativeButton(R.string.cancel, negativeButtonListener)
        return builder.create()
    }

    // For Alert Dialog on Custom View in App
    @JvmStatic
    fun createDialog(
        context: Context?, titleId: Int, messageId: Int, view: View?,
        positiveClickListener: DialogInterface.OnClickListener?,
        negativeClickListener: DialogInterface.OnClickListener?
    ): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(titleId)
        builder.setMessage(messageId)
        builder.setView(view)
        builder.setPositiveButton(R.string.ok, positiveClickListener)
        builder.setNegativeButton(R.string.cancel, negativeClickListener)
        return builder.create()
    }

    @JvmStatic
    fun showDialog(
        context: Context, title: String?, msg: String?,
        OK: DialogInterface.OnClickListener?, isCancelable: Boolean
    ) {
        var title1 = title
        var OK1 = OK
        if (title1 == null) title1 = context.resources.getString(R.string.app_name)
        if (OK1 == null) OK1 =
            DialogInterface.OnClickListener { _: DialogInterface?, _: Int -> hideDialog() }
        if (dialog == null) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title1)
            builder.setMessage(msg)
            builder.setPositiveButton("OK", OK1)
            dialog = builder.create()
            dialog?.setCancelable(isCancelable)
        }
        try {
            dialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Static method to show the dialog with custom message on it
     *
     * @param context      Context of the activity where to show the dialog
     * @param title        Title to be shown either custom or application name
     * @param msg          Custom message to be shown on dialog
     * @param OK           Overridden click listener for OK button in dialog
     * @param cancel       Overridden click listener for cancel button in dialog
     * @param isCancelable : Sets whether this dialog is cancelable with the BACK key.
     */
    @JvmStatic
    fun showDialog(
        context: Context, title: String?, msg: String?,
        OK: DialogInterface.OnClickListener?,
        cancel: DialogInterface.OnClickListener?, isCancelable: Boolean
    ) {
        var title1 = title
        var OK1 = OK
        var cancel1 = cancel
        if (title1 == null) title1 = context.resources.getString(R.string.app_name)
        if (OK1 == null) OK1 =
            DialogInterface.OnClickListener { _: DialogInterface?, _: Int -> hideDialog() }
        if (cancel1 == null) cancel1 =
            DialogInterface.OnClickListener { _: DialogInterface?, _: Int -> hideDialog() }
        if (dialog == null) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(title1)
            builder.setMessage(msg)
            builder.setPositiveButton(R.string.ok, OK1)
            builder.setNegativeButton(R.string.cancel, cancel1)
            dialog = builder.create()
            dialog?.setCancelable(isCancelable)
        }
        try {
            dialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Static method to show the progress dialog.
     *
     * @param context      : Context of the activity where to show the dialog
     * @param isCancelable : Sets whether this dialog is cancelable with the BACK key.
     * @param message      : Message to be shwon on the progress dialog.
     * @return Object of progress dialog.
     */
    @JvmStatic
    fun showProgressDialog(
        context: Context?,
        isCancelable: Boolean, message: String?
    ): Dialog? {
        mProgressDialog = ProgressDialog(context)
        mProgressDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog?.setMessage(message)
        mProgressDialog?.show()
        mProgressDialog?.setCancelable(isCancelable)
        //mProgressDialog.setIndeterminate(true);
        //mProgressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.my_animation));
        return mProgressDialog
    }

    /**
     * Static method to pause the progress dialog.
     */
    @JvmStatic
    fun pauseProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog?.cancel()
                mProgressDialog?.dismiss()
                mProgressDialog = null
            }
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }

    /**
     * show Toast
     */
    @JvmStatic
    fun showToast(context: Context?, message: String?) {
        if (message == null) {
            return
        }
        if (toast == null && context != null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        }
        if (toast != null) {
            toast?.setText(message)
            toast?.show()
        }
    }

    @JvmOverloads
    @JvmStatic
    fun log(msg: String?, tr: Throwable? = null) {
        log(TAG, msg, tr)
    }

    @JvmStatic
    fun log(tag: String?, msg: String?, tr: Throwable? = null) {
        Log.wtf(tag, msg, tr)
    }

    /**
     * Static method to cancel the Dialog.
     */
    @JvmStatic
    fun cancelDialog() {
        try {
            if (dialog != null) {
                dialog?.cancel()
                dialog?.dismiss()
                dialog = null
            }
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }

    /**
     * Static method to hide the dialog if visible
     */
    @JvmStatic
    private fun hideDialog() {
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
            dialog?.cancel()
            dialog = null
        }
    }

    /**
     * Checks the validation of email address.
     * Takes pattern and checks the text entered is valid email address or not.
     *
     * @param email : email in string format
     * @return True if email address is correct.
     */
    @JvmStatic
    fun isEmailValid(email: String): Boolean {
        val expression =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)
        if (matcher.matches()) {
            return true
        } else if (email == "") {
            return false
        }
        return false
    }

    /**
     * Method checks if the given phone number is valid or not.
     *
     * @param number : Phone number is to be checked.
     * @return True if the number is valid.
     * False if number is not valid.
     */
    @JvmStatic
    fun isPhoneNumberValid(number: String): Boolean {
        return number.length == 11
    }

    @JvmStatic
    fun isPasswordValid(number: String): Boolean {

        //String regexStr = "^([0-9\\(\\)\\/\\+ \\-]*)$";
        val regexStr = " (?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{8,20})$"
        return !(number.length < 6 || number.length > 13)
    }

    @JvmStatic
    fun currentTimeInMillis(): Long {
        val time = Time()
        time.setToNow()
        return time.toMillis(false)
    }

    /**
     * Checks if any text box is null or not.
     *
     * @param text : Text view for which validation is to be checked.
     * @return True if not null.
     */
    @JvmStatic
    fun getEditTextValue(text: EditText): String {
        return text.text.toString().trim { it <= ' ' }
    }

    /**
     * Checks if any text box is null or not.
     *
     * @param text : Text view for which validation is to be checked.
     * @return True if not null.
     */
    @JvmStatic
    fun isEditTextFilled(text: EditText): Boolean {
        return text.text != null && text.text.toString().trim { it <= ' ' }.isNotEmpty()
    }

    @JvmStatic
    fun isTextFilled(text: TextView): Boolean {
        return text.text != null && text.text.toString().trim { it <= ' ' }.isNotEmpty()
    }

    @JvmStatic
    fun isPasswordLengthCorrect(text: EditText): Boolean {
        return text.text != null && text.text.toString().trim { it <= ' ' }.length >= 8
    }

    @JvmStatic
    fun isNetworkConnected(mContext: Context): Boolean {
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null
    }

    @JvmStatic
    fun internetAlertDialog(mContext: Context?) {
        val alertDialog = AlertDialog.Builder(mContext)

        //Setting Dialog Title
        alertDialog.setTitle("Error Connecting")

        //Setting Dialog Message
        alertDialog.setMessage("No Internet Connection")

        //On Pressing Setting button
        alertDialog.setPositiveButton(
            "Ok"
        ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        alertDialog.show()
    }

    @JvmStatic
    fun getAppVersion(ctx: Context): Int {
        return try {
            val packageInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
            packageInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            // should never happen
            throw RuntimeException("Could not get package name: $e")
        }
    }

    @JvmStatic
    fun capWordFirstLetter(fullname: String?): String {
        var fname = ""
        var s2: String
        val tokenizer = StringTokenizer(fullname)
        while (tokenizer.hasMoreTokens()) {
            s2 = tokenizer.nextToken().lowercase(Locale.getDefault())
            fname += if (fname.isEmpty()) s2.substring(0, 1)
                .uppercase(Locale.getDefault()) + s2.substring(1) else " " + s2.substring(0, 1)
                .uppercase(Locale.getDefault()) + s2.substring(
                1
            )
        }
        return fname
    }

    @JvmStatic
    fun getDisplayableTime(delta: Long): String? {
        val difference: Long
        val mDate = System.currentTimeMillis()
        if (mDate > delta) {
            difference = mDate - delta
            val seconds = difference / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val months = days / 31
            val years = days / 365
            return when {
                seconds < 0 -> {
                    "not yet"
                }
                seconds < 60 -> {
                    if (seconds == 1L) "one second ago" else "$seconds seconds ago"
                }
                seconds < 120 -> {
                    "a minute ago"
                }
                seconds < 2700 // 45 * 60
                -> {
                    "$minutes minutes ago"
                }
                seconds < 5400 // 90 * 60
                -> {
                    "an hour ago"
                }
                seconds < 86400 // 24 * 60 * 60
                -> {
                    "$hours hours ago"
                }
                seconds < 172800 // 48 * 60 * 60
                -> {
                    "yesterday"
                }
                seconds < 2592000 // 30 * 24 * 60 * 60
                -> {
                    "$days days ago"
                }
                seconds < 31104000 // 12 * 30 * 24 * 60 * 60
                -> {
                    if (months <= 1) "one month ago" else "$days months ago"
                }
                else -> {
                    if (years <= 1) "one year ago" else "$years years ago"
                }
            }
        }
        return null
    }

    @JvmStatic
    fun getDisplayableDay(delta: Long): String? {
        val difference: Long
        val mDate = System.currentTimeMillis()
        if (mDate > delta) {
            difference = mDate - delta
            val seconds = difference / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val months = days / 31
            val years = days / 365
            return when {
                seconds < 0 -> {
                    "not yet"
                }
                DateUtils.isToday(delta) -> {
                    "today"
                } /* else if (seconds < 120) {
                        return "TODAY";
                    } else if (seconds < 2700) // 45 * 60
                    {
                        return "TODAY";
                    } else if (seconds < 5400) // 90 * 60
                    {
                        return "TODAY";
                    } else if (seconds < 86400) // 24 * 60 * 60
                    {
                        return "TODAY";
                    }*/
                seconds < 172800 // 48 * 60 * 60
                -> {
                    "yesterday"
                }
                seconds < 2592000 // 30 * 24 * 60 * 60
                -> {
                    "$days days ago"
                }
                seconds < 31104000 // 12 * 30 * 24 * 60 * 60
                -> {
                    if (months <= 1) "one month ago" else "$days months ago"
                }
                else -> {
                    if (years <= 1) "one year ago" else "$years years ago"
                }
            }
        }
        return null
    }

    @JvmStatic
    fun getDisplayableDayTime(delta: Long): String? {
        val difference: Long
        val mDate = System.currentTimeMillis()
        if (mDate > delta) {
            difference = mDate - delta
            val seconds = difference / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val months = days / 31
            val years = days / 365
            return when {
                seconds < 0 -> {
                    "not yet"
                }
                DateUtils.isToday(delta) -> {
                    "today"
                } /* else if (seconds < 120) {
                        return "TODAY";
                    } else if (seconds < 2700) // 45 * 60
                    {
                        return "TODAY";
                    } else if (seconds < 5400) // 90 * 60
                    {
                        return "TODAY";
                    } else if (seconds < 86400) // 24 * 60 * 60
                    {
                        return "TODAY";
                    }*/
                seconds < 172800 // 48 * 60 * 60
                -> {
                    "yesterday"
                }
                seconds < 2592000 // 30 * 24 * 60 * 60
                -> {
                    "$days days ago"
                }
                seconds < 31104000 // 12 * 30 * 24 * 60 * 60
                -> {
                    if (months <= 1) "one month ago" else "$days months ago"
                }
                else -> {
                    if (years <= 1) "one year ago" else "$years years ago"
                }
            }
        }
        return null
    }

    @JvmStatic
    fun correctTimestamp(timestampInMessage: Long): Long {
        var correctedTimestamp = timestampInMessage
        if (timestampInMessage.toString().length < 13) {
            val difference = 13 - timestampInMessage.toString().length
            var differenceValue = "1"
            var i = 0
            while (i < difference) {
                differenceValue += "0"
                i++
            }
            correctedTimestamp = (timestampInMessage * differenceValue.toInt()
                    + System.currentTimeMillis() % differenceValue.toInt())
        }
        return correctedTimestamp
    }

    /*public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }*/
    @JvmStatic
    fun convertStringToTimestamp(timestamp: String?): String {
        val tStamp = Timestamp.valueOf(timestamp)
        val simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return simpleDateFormat.format(tStamp)
    }

    @JvmStatic
    fun convertTimestampToTime(timestamp: Long): String {
        val tStamp = Timestamp(timestamp)
        val simpleDateFormat: SimpleDateFormat
        return if (DateUtils.isToday(timestamp)) {
            simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            simpleDateFormat.format(tStamp)
        } else {
            simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            simpleDateFormat.format(tStamp)
        }
    }

    @JvmStatic
    fun convertTimestampDateToTime(timestamp: Long): String {
        val tStamp = Timestamp(timestamp)
        val simpleDateFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())
        return simpleDateFormat.format(tStamp)
    }

    @JvmStatic
    fun convertTimestampDate(timestamp: Long): String {
        val tStamp = Timestamp(timestamp)
        val simpleDateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        return simpleDateFormat.format(tStamp)
    }

    @JvmStatic
    fun getFirstLetterCapital(input: String): String {
        var `val` = ""
        try {
            `val` = Character.toUpperCase(input[0]).toString() + input.substring(1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return `val`
    }

    @JvmStatic
    fun getAppointmentDate(date: Date?): String {
        val cal = Calendar.getInstance()
        date?.let { cal.time = it }
        val sdf = SimpleDateFormat("d MMM yyyy hh:mm a", Locale.getDefault())
        return sdf.format(cal.time)
    }

//    @JvmStatic
//    fun changeDateFormat(time: String?): String {
//        //2019-05-15 19:36:22
//        val inputPattern = "yyyy-MM-dd HH:mm:ss"
//        val outputPattern = "dd MMM, yyyy"
//        val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
//        val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
//        val date: Date?
//        var str = ""
//        try {
//            date = inputFormat.parse(time)
//            str = outputFormat.format(date)
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//        return str
//    }

    @JvmStatic
    fun changeDateFormat(time: String?): String { //2019-05-15 19:36:22
        val inputPattern = "yyyy-MM-dd"
        val outputPattern = "dd MMM, yyyy"
        val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
        val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())
        val date: Date?
        var str = ""
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    @JvmStatic
    fun extractYoutubeVideoId(ytUrl: String): String {
        var vId = ""
        val pattern = Pattern.compile(
            "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = pattern.matcher(ytUrl)
        if (matcher.matches()) {
            vId = matcher.group(1)
        }
        return vId
    }
}