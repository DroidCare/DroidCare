package com.droidcare;

import android.content.Entity;
import android.os.AsyncTask;
import android.util.Pair;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public abstract class SimpleHttpPost extends AsyncTask<Void, Void, String> {
    public static final int connectionTimeout = 10000;

    private String url;
    private Pair<String, String> pairs[];

    public SimpleHttpPost(Pair<String, String>... pairs) {
        this.pairs = pairs;
    }

    public void send(String url) {
        this.url = url;

        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected String doInBackground(Void... params) {
        String responseText = "";

        HttpPost httpPost = new HttpPost(url);

        List<NameValuePair> data = new ArrayList<NameValuePair>();
        for(Pair<String, String> pair: pairs) {
            data.add(new BasicNameValuePair(pair.first, pair.second));
        }

        // httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        // httpPost.setHeader("Accept-Charset", "utf-8");

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(data, "UTF-8"));
        // Do nothing on exception
        } catch (UnsupportedEncodingException e) {
        }

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);

        try {
            HttpResponse response = new DefaultHttpClient(httpParams).execute(httpPost);
            if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                responseText = EntityUtils.toString(response.getEntity());
            } else {
                responseText = response.getStatusLine().getReasonPhrase();
            }
        // Do nothing on exception
        } catch (IOException e) {
        }

        return responseText;
    }

    @Override
    protected void onPostExecute(String result) {
        onFinish(result);
    }

    public abstract void onFinish(String responseText);
}
