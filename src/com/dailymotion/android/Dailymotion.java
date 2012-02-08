package com.dailymotion.android;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.klaxpont.android.Constants;

public final class Dailymotion {
	
	public static String get_access_token() throws Exception {
		String access_token="";
		String answer="";
		String body="grant_type=password&client_id="+Constants.DAILYMOTION_API_KEY+"&client_secret="+Constants.DAILYMOTION_SECRET_API_KEY+"&username="+Constants.DAILYMOTION_USER+"&password="+Constants.DAILYMOTION_PASSWORD;
		answer=http_post_access(Constants.DAILYMOTION_ACCESS_TOKEN_URL, body);
            JSONObject json = new JSONObject(answer);
            access_token = json.getString("access_token");
        return access_token;
	}
	
	public static String get_an_upload_url(String access_token) throws Exception {
		String upload_url="";
		String answer = http_get_access("https://api.dailymotion.com/file/upload?access_token="+access_token);
		JSONObject json = new JSONObject(answer);
        upload_url = json.getString("upload_url");
		return upload_url;
	}
	
	public static String http_get_access(String url) throws Exception {
        String response 	= "";
		URL login_url = new URL(url);
        HttpURLConnection login_request = (HttpURLConnection) login_url.openConnection();
        try {
	        login_request.setRequestMethod("GET");
	        login_request.setAllowUserInteraction(false); // you may not ask the user
	        login_request.setDoInput(true);
	        login_request.setDoOutput(false);
	        login_request.setUseCaches(false);

	        BufferedReader in = new BufferedReader(new InputStreamReader(login_request.getInputStream()));
	        String currentLine 	= "";
	        while((currentLine = in.readLine()) != null)
	        	response += currentLine + "\n";
	        in.close();
        }finally {
        	login_request.disconnect();
        }
        return response;
	}
	
	public static String http_post_access(String url,String body) throws Exception {
        String response 	= "";
		URL login_url = new URL(url);
        HttpURLConnection login_request = (HttpURLConnection) login_url.openConnection();
        try {
	        login_request.setRequestMethod("POST");
	        login_request.setAllowUserInteraction(false); // you may not ask the user
	        login_request.setDoInput(true);
	        login_request.setDoOutput(true);
	        login_request.setUseCaches(false);
	     	// the Content-type should be default, but we set it anyway
	        login_request.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

	        OutputStream out = new BufferedOutputStream(login_request.getOutputStream());
	        out.write(body.getBytes());
	        out.close();

	        BufferedReader in = new BufferedReader(new InputStreamReader(login_request.getInputStream()));
	        String currentLine 	= "";
	        while((currentLine = in.readLine()) != null)
	        	response += currentLine + "\n";
	        in.close();
        }finally {
        	login_request.disconnect();
        }
        return response;
	}
}