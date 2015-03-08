package com.droidcare.control;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class HttpGetRequest{
	private HashMap<String, String> data;
	public static int CONNECTION_TIMEOUT = 10000;
	
	private class SimpleAsyncHttpGet extends AsyncTask<String, Void, String>{
		private int timeoutConnection;
		private HashMap<String, String> data;
		
		public SimpleAsyncHttpGet(HashMap<String, String> data, int timeout){
			this.data = data;
			this.timeoutConnection = timeout;
		}
	
		@Override
		protected String doInBackground(String... params){ // params[0] contains URL
			String responseText = "";
			
			if(params.length != 1){
				return responseText;
			}
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			for(Iterator<String> it = data.keySet().iterator(); it.hasNext(); ){
				String key = it.next();
				pairs.add(new BasicNameValuePair(key, data.get(key)));
			}
	
			HttpGet httpGet = new HttpGet(params[0]
					+ URLEncodedUtils.format(pairs, "utf-8")); // Parameters
			httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httpGet.setHeader("Accept-Charset", "utf-8");
			
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
			
			try {
				HttpResponse response = new DefaultHttpClient(httpParams).execute(httpGet);
				if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK){
					responseText = EntityUtils.toString(response.getEntity());
				} else {
					responseText = response.getStatusLine().getReasonPhrase();
				}
			
			// Do nothing on exception
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}
			
			return responseText;
		}
	}
	
	public HttpGetRequest(HashMap<String, String> data){
		this.data = data;
	}
	
	public String send(String url){
		try {
			return new SimpleAsyncHttpGet(data, CONNECTION_TIMEOUT)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url)
					.get();
		// Do nothing on exception
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		}
		
		return "";
	}
}