package com.appliweb.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.provider.SyncStateContract.Constants;
import android.util.Log;

public final class AppliWeb {
	private static JSONObject json_dailymotion_token=null;
	
	public static JSONObject getJson_dailymotion_token() {
		return json_dailymotion_token;
	}

	public static void setJson_dailymotion_token(JSONObject json_token) {
		AppliWeb.json_dailymotion_token = json_token;
	}
	
	public static String getDailymotionAccessToken() {
		String returned = null;
		String answer = http_get_access(com.klaxpont.android.Constants.APPLIWEB_DAILYMOTIONACESSTOKEN_URL);
		Log.i("AppliWeb","access_token:"+answer);	
		try {
			if(answer!=null)
			{
				setJson_dailymotion_token(new JSONObject(answer));
				returned = getJson_dailymotion_token().getString("access_token");
			}
		} catch (JSONException e) {
			Log.w("AppliWeb","Problem getting the dailymotion access_token");
		}
		return returned;
	}
	
	private static String http_get_access(String url){
        String response=null;
        HttpURLConnection login_request=null;
        try {
        	URL login_url = new URL(url);
        	login_request = (HttpURLConnection) login_url.openConnection();
	        login_request.setRequestProperty("Accept","*/*");
	        login_request.setRequestMethod("GET");
	        login_request.setAllowUserInteraction(false); // you may not ask the user
	        login_request.setDoInput(true);
	        login_request.setDoOutput(false);
	        login_request.setUseCaches(false);
	        BufferedReader in = new BufferedReader(new InputStreamReader(login_request.getInputStream()));
	        response = "";
	        String currentLine 	= "";
	        while((currentLine = in.readLine()) != null)
	        	response += currentLine + "\n";
	        in.close();
        } catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			Log.w("AppliWeb","Probably bad information");
			Log.w("AppliWeb","http_get:"+e.toString());
		}finally {
        	if(login_request!=null)
        		login_request.disconnect();
        }
        return response;
	}
}