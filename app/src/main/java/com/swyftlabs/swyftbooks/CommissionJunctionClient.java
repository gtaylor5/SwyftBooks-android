package com.swyftlabs.swyftbooks;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.loopj.android.http.*;

import org.w3c.dom.Text;

import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.client.ResponseHandler;

/**
 * Created by Gerard on 7/5/2016.
 */
public class CommissionJunctionClient  {

    public static AsyncHttpClient syncHttpClient= new SyncHttpClient();
    public static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private static final String BASE_URL = "https://product-search.api.cj.com";

    public static void get(String url, RequestParams params, TextHttpResponseHandler responseHandler) {
        getClient().setAuthenticationPreemptive(true);
        getClient().addHeader("Authorization","0090ea50e79aad5591e67493c4fa6bcace035b8a2158f09b14a" +
                "63256d2ec36ccd2a70519cf7e26fee2bb6c627a6c766656a7b59137678028209b492625e8e3a987/3b7" +
                "223661ea96424c9a8a0c57904a6d903bf955640a9d2b411c92c9cb191794b814a97cf20eb900a1084f2" +
                "5a248a8c06a3b1bed094447508b7a849ffebe34361" );
        getClient().get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        getClient().post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static AsyncHttpClient getClient()
    {
        // Return the synchronous HTTP client when the thread is not prepared
        if (Looper.myLooper() == null)
            return syncHttpClient;
        return asyncHttpClient;
    }



}
