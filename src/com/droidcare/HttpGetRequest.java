package com.droidcare;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public abstract class HttpGetRequest extends AsyncTask<Void, Void, Void>{
	public static int timeoutConnection = 10000;
	
	public abstract HttpGetRequest init(Object... objects);
	
	private String send(String url, HashMap<String, String> data){
		String responseText = "";
		
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		for(Iterator<String> it = data.keySet().iterator(); it.hasNext(); ){
			String key = it.next();
			pairs.add(new BasicNameValuePair(key, data.get(key)));
		}
		
		String params = URLEncodedUtils.format(pairs, "utf-8");

		HttpGet httpGet = new HttpGet(url + params);
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