package com.swyftlabs.swyftbooks;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Gerard on 4/7/2016.
 */
class DownloadWebpageTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {

        // params comes from the execute() call: params[0] is the url.
        try {
            return downloadUrl(urls[0]);
        } catch (IOException e) {
            return null;
        }
    }

    // onPostExecute displays the results of the AsyncTask.

    protected void onPostExecute(String result) {

        //Log.i("AppInfo", result);

    }


    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setInstanceFollowRedirects(true);
            conn.connect();
            int response = conn.getResponseCode();
            if(response != 200){

                return "";

            }
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsDocument = readIt(is);
            return contentAsDocument;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        try{

            BufferedReader reader = null;
            reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String document = "";
            String line = reader.readLine();
            while(line != null){

                document+=line;
                line = reader.readLine();


            }

            return document;

        }catch(Exception ex){

            System.out.println(ex.getMessage());

        }
        return null;
    }


}