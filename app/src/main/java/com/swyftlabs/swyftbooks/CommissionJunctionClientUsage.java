package com.swyftlabs.swyftbooks;

/**
 * Created by Gerard on 7/5/2016.
 */

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import com.loopj.android.http.*;
import java.io.IOException;


import cz.msebera.android.httpclient.Header;

public class CommissionJunctionClientUsage extends AsyncTask<String, Void, String>{
    static String xmlfile = "";



    @Override
    protected String doInBackground(String... params) {
        try {
            xmlfile = getBookInfo(params[0]);
            return xmlfile;
        }catch(IOException e){

            e.printStackTrace();
            return "";

        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    public String getBookInfo(String link) throws IOException{

        CommissionJunctionClient.get(link, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                xmlfile = responseString;

            }
        });

        return xmlfile;

    }
}
