package com.wokconns.customer.https;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wokconns.customer.interfaces.Consts;
import com.wokconns.customer.interfaces.Helper;
import com.wokconns.customer.jsonparser.JSONParser;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.utils.ProjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

/**
 * Created by VARUN on 01/01/19.
 */
public class HttpsRequest {
    public static final int ERROR_INT = 0;
    private String match;
    private Map<String, String> params;
    private Map<String, File> fileparams;
    private Context ctx;
    private JSONObject jObject;
    private SharedPrefrence sharedPreference;

    public HttpsRequest(String match, Map<String, String> params, Context ctx) {
        this.match = match;
        this.params = params;
        this.ctx = ctx;
        this.sharedPreference = SharedPrefrence.getInstance(ctx);
    }

    public HttpsRequest(String match, Map<String, String> params, Map<String, File> fileparams, Context ctx) {
        this.match = match;
        this.params = params;
        this.fileparams = fileparams;
        this.ctx = ctx;
        this.sharedPreference = SharedPrefrence.getInstance(ctx);
    }

    public HttpsRequest(String match, Context ctx) {
        this.match = match;
        this.ctx = ctx;
        this.sharedPreference = SharedPrefrence.getInstance(ctx);
    }

    public HttpsRequest(String match, JSONObject jObject, Context ctx) {
        this.match = match;
        this.jObject = jObject;
        this.ctx = ctx;
        this.sharedPreference = SharedPrefrence.getInstance(ctx);

    }

    public void stringPostJson(final String TAG, final Helper h) {
        AndroidNetworking.post(Consts.BASE_URL + match)
                .addJSONObjectBody(jObject)
                .setTag("test")
                .addHeaders(Consts.LANGUAGE, sharedPreference.getValue(Consts.LANGUAGE_SELECTION))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, " response body --->" + response.toString());
                        Log.e(TAG, " response body --->" + jObject.toString());

                        try {
                            JSONParser jsonParser = new JSONParser(ctx, response);

                            Object status = jsonParser.jObj.get("status");
                            Object message = jsonParser.jObj.get("message");

                            if ((status instanceof String && ((String) status).contains("err"))
                                    || status.equals("error")) {
                                ProjectUtils.showLong(ctx, message.toString());

                                h.backResponse(false, jsonParser.MESSAGE, null);
                            } else if (status.equals(ERROR_INT) || status.equals("0")) {
                                ProjectUtils.showLong(ctx, message.toString());

                                h.backResponse(false, jsonParser.MESSAGE, null);
                            } else {
                                h.backResponse(jsonParser.RESULT, jsonParser.MESSAGE, response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtils.pauseProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(anError.getErrorBody());

                            Object status = jsonObject.get("status");
                            Object message = jsonObject.get("message");

                            if ((status instanceof String && ((String) status).contains("err"))
                                    || status.equals("error"))
                                h.backResponse(false, message.toString(), jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.e(TAG, " error body --->" + anError.getErrorBody() + " error msg --->" + anError.getMessage());
                    }
                });
    }

    public void stringPost(final String TAG, final Helper h) {
        AndroidNetworking.post(Consts.BASE_URL + match)
                .addBodyParameter(params)
                .setTag("test")
                .addHeaders(Consts.LANGUAGE, sharedPreference.getValue(Consts.LANGUAGE_SELECTION))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, " response body --->" + response.toString());
                        Log.e(TAG, " param --->" + params.toString());

                        try {
                            JSONParser jsonParser = new JSONParser(ctx, response);

                            Object status = jsonParser.jObj.get("status");
                            Object message = jsonParser.jObj.get("message");

                            if ((status instanceof String && ((String) status).contains("err"))
                                    || status.equals("error")) {
                                ProjectUtils.showLong(ctx, message.toString());

                                h.backResponse(false, jsonParser.MESSAGE, null);
                            } else if (status.equals(ERROR_INT) || status.equals("0")) {
                                ProjectUtils.showLong(ctx, message.toString());

                                h.backResponse(false, jsonParser.MESSAGE, null);
                            } else {
                                h.backResponse(jsonParser.RESULT, jsonParser.MESSAGE, response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtils.pauseProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(anError.getErrorBody());

                            Object status = jsonObject.get("status");
                            Object message = jsonObject.get("message");

                            if ((status instanceof String && ((String) status).contains("err"))
                                    || status.equals("error"))
                                h.backResponse(false, message.toString(), jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.e(TAG, " error body --->" + anError.getErrorBody() + " error msg --->" + anError.getMessage());
                    }
                });
    }

    public void stringGet(final String TAG, final Helper h) {
        AndroidNetworking.get(Consts.BASE_URL + match)
                .setTag("test")
                .addHeaders(Consts.LANGUAGE, sharedPreference.getValue(Consts.LANGUAGE_SELECTION))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, " response body --->" + response.toString());
                        JSONParser jsonParser = new JSONParser(ctx, response);
                        if (jsonParser.RESULT) {
                            h.backResponse(true, jsonParser.MESSAGE, response);
                        } else {
                            h.backResponse(false, jsonParser.MESSAGE, null);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtils.pauseProgressDialog();
                        Log.e(TAG, " error body --->" + anError.getErrorBody() + " error msg --->" + anError.getMessage());
                    }
                });
    }

    public void imagePost(final String TAG, final Helper h) {
        AndroidNetworking.upload(Consts.BASE_URL + match)
                .addMultipartFile(fileparams)
                .addMultipartParameter(params)
                .setTag("uploadTest")
                .addHeaders(Consts.LANGUAGE, sharedPreference.getValue(Consts.LANGUAGE_SELECTION))
                .setPriority(Priority.IMMEDIATE)
                .build()
                .setUploadProgressListener((bytesUploaded, totalBytes) -> Log.e("Byte", bytesUploaded + "  !!! " + totalBytes))
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, " response body --->" + response.toString());
                        Log.e(TAG, " param --->" + params.toString());

                        try {
                            JSONParser jsonParser = new JSONParser(ctx, response);

                            Object status = jsonParser.jObj.get("status");
                            Object message = jsonParser.jObj.get("message");

                            if ((status instanceof String && ((String) status).contains("err"))
                                    || status.equals("error")) {
                                ProjectUtils.showLong(ctx, message.toString());

                                h.backResponse(false, jsonParser.MESSAGE, null);
                            } else if (status.equals(ERROR_INT) || status.equals("0")) {
                                ProjectUtils.showLong(ctx, message.toString());

                                h.backResponse(false, jsonParser.MESSAGE, null);
                            } else {
                                h.backResponse(jsonParser.RESULT, jsonParser.MESSAGE, response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ProjectUtils.pauseProgressDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(anError.getErrorBody());

                            Object status = jsonObject.get("status");
                            Object message = jsonObject.get("message");

                            if ((status instanceof String && ((String) status).contains("err"))
                                    || status.equals("error"))
                                h.backResponse(false, message.toString(), jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.e(TAG, " error body --->" + anError.getErrorBody() + " error msg --->" + anError.getMessage());
                    }
                });
    }

}