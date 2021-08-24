package com.wokconns.customer.https

import android.content.Context
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.wokconns.customer.interfaces.Const
import com.wokconns.customer.interfaces.Helper
import com.wokconns.customer.jsonparser.JSONParser
import com.wokconns.customer.preferences.SharedPrefrence
import com.wokconns.customer.utils.ProjectUtils.log
import com.wokconns.customer.utils.ProjectUtils.pauseProgressDialog
import com.wokconns.customer.utils.ProjectUtils.showLong
import org.json.JSONException
import org.json.JSONObject
import java.io.File

/**
 * Created by VARUN on 01/01/19.
 */
class HttpsRequest {
    private var match: String
    private var params: HashMap<String, String>? = null
    private var fileparams: HashMap<String, File>? = null
    private var ctx: Context
    private var jObject: JSONObject? = null
    private var sharedPreference: SharedPrefrence

    constructor(match: String, params: HashMap<String, String>?, ctx: Context) {
        this.match = match
        this.params = params
        this.ctx = ctx
        sharedPreference = SharedPrefrence.getInstance(ctx)
    }

    constructor(
        match: String,
        params: HashMap<String, String>?,
        fileparams: HashMap<String, File>?,
        ctx: Context
    ) {
        this.match = match
        this.params = params
        this.fileparams = fileparams
        this.ctx = ctx
        sharedPreference = SharedPrefrence.getInstance(ctx)
    }

    constructor(match: String, ctx: Context) {
        this.match = match
        this.ctx = ctx
        sharedPreference = SharedPrefrence.getInstance(ctx)
    }

    constructor(match: String, jObject: JSONObject?, ctx: Context) {
        this.match = match
        this.jObject = jObject
        this.ctx = ctx
        sharedPreference = SharedPrefrence.getInstance(ctx)
    }

    fun stringPostJson(TAG: String?, h: Helper) {
        if (params != null)
            if (!params?.containsKey(Const.ROLE)!!)
                params!![Const.ROLE] = "2"

        AndroidNetworking.post(Const.BASE_URL + match)
            .addJSONObjectBody(jObject)
            .setTag("test")
            .addHeaders(Const.LANGUAGE, sharedPreference.getValue(Const.LANGUAGE_SELECTION))
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(res: JSONObject) {
                    Log.e(TAG, " response body --->$res")
                    Log.e(TAG, " response body --->" + jObject.toString())
                    try {
                        val jsonParser = JSONParser(ctx, res)
                        val status = jsonParser.jObj.opt("status")
                        val message = jsonParser.jObj.optString("message")
                        if (status is String && status.contains("err")
                            || status == "error"
                        ) {
                            showLong(ctx, message.toString())
                            h.backResponse(false, jsonParser.MESSAGE, null)
                        } else if (status == ERROR_INT || status == "0") {
                            showLong(ctx, message.toString())
                            h.backResponse(false, jsonParser.MESSAGE, null)
                        } else {
                            h.backResponse(jsonParser.RESULT, jsonParser.MESSAGE, res)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError?) {
                    pauseProgressDialog()
                    try {
                        if (anError != null && anError.errorBody != null) {
                            val jsonObject = JSONObject(anError.errorBody)
                            val status = jsonObject.opt("status")
                            val message = jsonObject.optString("message")
                            if ((status is String && status.contains("err"))
                                || status == "error"
                            ) h.backResponse(false, message.toString(), jsonObject)
                            return
                        }

                        h.backResponse(false, anError?.errorDetail
                            ?: anError?.cause?.localizedMessage ?: anError.toString(), null)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    log(
                        TAG,
                        " error body --->" + anError?.errorBody + " error msg --->" + anError?.message
                    )
                }
            })
    }

    fun stringPost(TAG: String?, h: Helper) {
        if (params != null)
            if (!params?.containsKey(Const.ROLE)!!)
                params!![Const.ROLE] = "2"
        AndroidNetworking.post(Const.BASE_URL + match)
            .addBodyParameter(params)
            .setTag("test")
            .addHeaders(Const.LANGUAGE, sharedPreference.getValue(Const.LANGUAGE_SELECTION))
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(res: JSONObject) {
                    Log.e(TAG, " response body --->$res")
                    Log.e(TAG, " param --->" + params.toString())
                    try {
                        val jsonParser = JSONParser(ctx, res)
                        val status = jsonParser.jObj.opt("status")
                        val message = jsonParser.jObj.optString("message")
                        if (status is String && status.contains("err")
                            || status == "error"
                        ) {
                            showLong(ctx, message.toString())
                            h.backResponse(false, jsonParser.MESSAGE, null)
                        } else if (status == ERROR_INT || status == "0") {
                            showLong(ctx, message.toString())
                            h.backResponse(false, jsonParser.MESSAGE, null)
                        } else {
                            h.backResponse(jsonParser.RESULT, jsonParser.MESSAGE, res)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError?) {
                    pauseProgressDialog()
                    try {
                        if (anError != null && anError.errorBody != null) {
                            val jsonObject = JSONObject(anError.errorBody)
                            val status = jsonObject.opt("status")
                            val message = jsonObject.optString("message")
                            if ((status is String && status.contains("err"))
                                || status == "error"
                            ) h.backResponse(false, message.toString(), jsonObject)
                            return
                        }

                        h.backResponse(false, anError?.errorDetail
                            ?: anError?.cause?.localizedMessage ?: anError.toString(), null)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    log(
                        TAG,
                        " error body --->" + anError?.errorBody + " error msg --->" + anError?.message
                    )
                }
            })
    }

    fun stringGet(TAG: String?, h: Helper) {
        if (params != null)
            if (!params?.containsKey(Const.ROLE)!!)
                params!![Const.ROLE] = "2"
        AndroidNetworking.get(Const.BASE_URL + match)
            .setTag("test")
            .addHeaders(Const.LANGUAGE, sharedPreference.getValue(Const.LANGUAGE_SELECTION))
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(res: JSONObject) {
                    Log.e(TAG, " response body --->$res")
                    val jsonParser = JSONParser(ctx, res)
                    if (jsonParser.RESULT) {
                        h.backResponse(true, jsonParser.MESSAGE, res)
                    } else {
                        h.backResponse(false, jsonParser.MESSAGE, null)
                    }
                }

                override fun onError(anError: ANError) {
                    pauseProgressDialog()
                    log(
                        TAG,
                        " error body --->" + anError.errorBody + " error msg --->" + anError.message
                    )
                }
            })
    }

    fun imagePost(TAG: String?, h: Helper) {
        if (params != null)
            if (!params?.containsKey(Const.ROLE)!!)
                params!![Const.ROLE] = "2"

        AndroidNetworking.upload(Const.BASE_URL + match)
            .addMultipartFile(fileparams)
            .addMultipartParameter(params)
            .setTag("uploadTest")
            .addHeaders(Const.LANGUAGE, sharedPreference.getValue(Const.LANGUAGE_SELECTION))
            .setPriority(Priority.IMMEDIATE)
            .build()
            .setUploadProgressListener { bytesUploaded: Long, totalBytes: Long ->
                log(
                    "Byte",
                    "$bytesUploaded  !!! $totalBytes"
                )
            }
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(res: JSONObject) {
                    try {
                        val jsonParser = JSONParser(ctx, res)
                        val status = jsonParser.jObj.opt("status")
                        val message = jsonParser.jObj.optString("message")
                        if (status is String && status.contains("err")
                            || status == "error"
                        ) {
                            showLong(ctx, message.toString())
                            h.backResponse(false, jsonParser.MESSAGE, null)
                        } else if (status == ERROR_INT || status == "0") {
                            showLong(ctx, message.toString())
                            h.backResponse(false, jsonParser.MESSAGE, null)
                        } else {
                            h.backResponse(jsonParser.RESULT, jsonParser.MESSAGE, res)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(anError: ANError?) {
                    pauseProgressDialog()
                    try {
                        if (anError != null && anError.errorBody != null) {
                            val jsonObject = JSONObject(anError.errorBody)
                            val status = jsonObject.opt("status")
                            val message = jsonObject.optString("message")
                            if ((status is String && status.contains("err"))
                                || status == "error"
                            ) h.backResponse(false, message.toString(), jsonObject)
                            return
                        }

                        h.backResponse(false, anError?.errorDetail
                            ?: anError?.cause?.localizedMessage ?: anError.toString(), null)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    log(
                        TAG,
                        " error body --->" + anError?.errorBody + " error msg --->" + anError?.message
                    )
                }
            })
    }

    companion object {
        const val ERROR_INT = 0
    }
}