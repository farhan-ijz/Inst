package com.wurfel.yaqeen.inst.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wurfel.yaqeen.inst.Interface.VolleyResponseListener;
import com.wurfel.yaqeen.inst.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Farhan Ijaz on 9/13/17.
 */

public class VolleyUtils {

    static String REQUEST_TAG = "volleyStringRequest";
    static Dialog loadingDialog;

    public static void GET_METHOD(final Context context,final String url, final Map<String,String> getParams,
                                  final Map<String,String> getHeader, final VolleyResponseListener listener)
    {
        showLoadingDialoag(context);
        // Initialize a new StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResponse(response);
                        dismissLoadingDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            listener.onError(getErrorData(response));
                        }
                        dismissLoadingDialog();
                        noInternetDialogShow(context,url,getParams,getHeader,"",listener,"get");
                    }
                })

        {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeader;
            }

            @Override
            public String getUrl() {
                StringBuilder stringBuilder = new StringBuilder(url);
                int i = 1;
                for (Map.Entry<String,String> entry: getParams.entrySet()) {
                    String key;
                    String value;
                    try {
                        key = URLEncoder.encode(entry.getKey(), "UTF-8");
                        value = URLEncoder.encode(entry.getValue(), "UTF-8");
                        if(i == 1) {
                            stringBuilder.append("?" + key + "=" + value);
                        } else {
                            stringBuilder.append("&" + key + "=" + value);
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    i++;

                }
                String url = stringBuilder.toString();

                return url;
            }

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return getParams;
            };

        };

        // Access the RequestQueue through singleton class.
        AppSingleton.getInstance(context).addToRequestQueue(stringRequest, REQUEST_TAG);
    }

    public static void POST_METHOD(final Context context, final String url, final Map<String,String> getParams,
                                   final Map<String,String> getHeader,final String bodyData,final VolleyResponseListener listener)
    {

        showLoadingDialoag(context);

        // Initialize a new StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dismissLoadingDialog();
                        listener.onResponse(response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            listener.onError(getErrorData(response));
                        }
                        dismissLoadingDialog();
                        noInternetDialogShow(context,url,getParams,getHeader,bodyData,listener,"post");
                    }
                })

        {

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return bodyData.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeader;
            }

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return getParams;
            };

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 5, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Access the RequestQueue through singleton class.
        AppSingleton.getInstance(context).addToRequestQueue(stringRequest, REQUEST_TAG);
    }

    static void showLoadingDialoag(Context context){
        loadingDialog = new Dialog(context);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setContentView(R.layout.dialogbox_loading);
        final ImageView imgViewLoader = (ImageView) loadingDialog.findViewById(R.id.imageview_loader);
        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.clockwise_rotation);
        rotation.setRepeatCount(Animation.INFINITE);
        imgViewLoader.startAnimation(rotation);

        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    static void dismissLoadingDialog(){
//        handler.removeCallbacks(runnable);
        loadingDialog.dismiss();
    }

    static void noInternetDialogShow(final Context context, final String url, final Map<String, String> getParams,
                                     final Map<String, String> getHeader, final String bodyData,
                                     final VolleyResponseListener listener, final String method){
        MaterialDialog noInternetDialog = new MaterialDialog.Builder(context)
//                .positiveText(R.string.agree)
                .title("No Internet Connection")
                .content("Sorry, no Internet connectivity detected. Please reconnect and try again.")

                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
//                        volleyStringRequstForCat(url);
                        if(method.equals("post")) {
                            POST_METHOD(context, url, getParams, getHeader, bodyData, listener);
                        }
                        else {
                            GET_METHOD(context, url, getParams, getHeader, listener);
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        System.exit(0);
                    }
                })
                .positiveText("Retry")
                .negativeText("Exit")
                .cancelable(false)
                .show();
    }

    static String getErrorData(NetworkResponse response){
        String errorDescription = "";
        try {
            String json = new String(response.data, StandardCharsets.UTF_8);
            JSONObject jsonObjectResponse = new JSONObject(json);
            errorDescription = jsonObjectResponse.getString("error_description");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return errorDescription;
    }

}