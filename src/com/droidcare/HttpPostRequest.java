package com.droidcare;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class HttpPostRequest{
	private HashMap<String, String> data;
	public static int CONNECTION_TIMEOUT = 10000;
	
	private class SimpleAsyncHttpPost extends AsyncTask<String, Integer, String> {
		private HashMap<String, String> data;
		private int timeoutConnection;
		
		public SimpleAsyncHttpPost(HashMap<String, String> data, int timeoutConnection) {
			this.data = data;
			this.timeoutConnection = timeoutConnection;
		}
		
		@Override
		protected String doInBackground(String... params) { // ASSUMING params[0] IS THE URL
			String responseText = "";
			
			HttpPost httpPost = new HttpPost(params[0]);
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.setHeader("charset", "UTF-8");
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			for(Iterator<String> it = data.keySet().iterator(); it.hasNext(); ) {
				String key = it.next();
				pairs.add(new BasicNameValuePair(key, data.get(key)));
			}
			
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
			
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
			
			try {
				HttpResponse response = new DefaultHttpClient(httpParams).execute(httpPost);
				if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
					responseText = EntityUtils.toString(response.getEntity());
				} else {
					responseText = response.getStatusLine().getReasonPhrase();
				}
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			} catch (ParseException e) {
			}
			
			return responseText;
		}
	}
	
	public HttpPostRequest(HashMap<String, String> data){
		this.data = data;
	}
	
	public String send(String url){
		try {
			return new SimpleAsyncHttpPost(data, CONNECTION_TIMEOUT)
					.execute(url)
					.get();
		// Do nothing on exception
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}
		
		return "";
	}
}