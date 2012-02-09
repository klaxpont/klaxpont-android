package com.dailymotion.android;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.util.Log;

import com.klaxpont.android.Constants;

public final class Dailymotion {
	
	public static String get_access_token() throws Exception {
		String access_token="";
		String answer="";
		String body="grant_type=password&client_id="+Constants.DAILYMOTION_API_KEY+"&client_secret="+Constants.DAILYMOTION_SECRET_API_KEY+"&username="+Constants.DAILYMOTION_USER+"&password="+Constants.DAILYMOTION_PASSWORD+"&scope=write+read+delete+manage_videos";
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
	
	public static String upload_file(File f,String upload_uri) throws Exception {
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";
		String real_upload_url="";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;
		try {
			FileInputStream fileInputStream = new FileInputStream(f);
			// open a URL connection to the Servlet
			URL url = new URL(upload_uri);
			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			// Allow Inputs
			conn.setDoInput(true);
			// Allow Outputs
			conn.setDoOutput(true);
			// Don't use a cached copy.
			conn.setUseCaches(false);
			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			dos = new DataOutputStream( conn.getOutputStream() );
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + f.getPath() + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			// create a buffer of maximum size
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0)
			{
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			// close streams
			Log.e("Dailymotion-Upload","File is written");
			fileInputStream.close();
			dos.flush();
			dos.close();
		}
		catch (MalformedURLException ex) {
			Log.e("Dailymotion-Upload", "error: " + ex.getMessage(), ex);
		}
		catch (IOException ioe)
		{
			Log.e("Dailymotion-Upload", "error: " + ioe.getMessage(), ioe);
		}
		//------------------ read the SERVER RESPONSE
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        String currentLine 	= "";
	        String answer = "";
	        while((currentLine = in.readLine()) != null)
	        	answer += currentLine + "\n";
	        in.close();
			Log.e("Dailymotion-Upload","answer after upload:"+answer);
			JSONObject json = new JSONObject(answer);
	        real_upload_url = json.getString("url");
		}
		catch (IOException ioex){
			Log.e("Debug", "error: " + ioex.getMessage(), ioex);
		}
        return real_upload_url;
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
		Log.w("Dailymotion","http_post url:"+url+",body:"+body);
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