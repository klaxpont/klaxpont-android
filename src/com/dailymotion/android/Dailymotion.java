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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public final class Dailymotion {
	
	private static JSONObject json_token;
	
	public static String getEmbedUrl(String videoId) {
		String embed_url=null;
		String answer = http_get_access("https://api.dailymotion.com/video/"+videoId+"?fields=embed_url");
		
		try {
    		JSONObject json = new JSONObject(answer);
			embed_url = json.getString("embed_url");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		Log.d("Dailymotion","The embed_url of "+videoId+" is :"+answer);
		return embed_url;
		
	}	
	public static boolean set_access_token(String access_token) {
		Log.i("Dailymotion","set_access_token:"+access_token);
		return set_data_in_json_token("access_token",access_token);
	}
	
	/* Not really useful...
	public static boolean set_refresh_token(String refresh_token) {
		return set_data_in_json_token("refresh_token",refresh_token);
	}
	*/
	
	private static boolean set_data_in_json_token(String key,String value) {
		JSONObject json = getJson_token();
		if(json==null)
			json = new JSONObject();
		try {
			json.put(key,value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setJson_token(json);
		return true;
	}
	
	public static boolean relogin() {
		JSONObject json = getJson_token();
		if(json==null)
			return false;
		try {
			return login(json.getString("api_key"),json.getString("api_secret_key"),json.getString("user"),json.getString("password"));
		} catch (JSONException e) {
			Log.w("Dailymotion","Missing data to relogin...");
			return false;
		}
	}
	
	public static boolean login(String api_key,String api_secret_key,String user,String password) {
		String answer="";
		String body="grant_type=password&client_id="+api_key+"&client_secret="+api_secret_key+"&username="+user+"&password="+password+"&scope=write+read+delete";
		answer=http_post_access("https://api.dailymotion.com/oauth/token", body);
		if(answer==null){
			Log.e("Dailymotion","Unable to login");
			return false;
		}
		Log.d("Dailymotion","login:"+answer);
		try {
			setJson_token(new JSONObject(answer));
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d("Dailymotion","Probably invalid credentials");
			return false;
		}
		set_data_in_json_token("api_key",api_key);
		set_data_in_json_token("api_secret_key",api_secret_key);
		set_data_in_json_token("user",user);
		set_data_in_json_token("password",password);
        return true;
	}
	
	public static void logout() {
		JSONObject json = getJson_token();
		if(json==null){
			Log.w("Dailymotion","You were not authenticate...");
			return;
		}
		try {
			String answer = http_get_access("https://api.dailymotion.com/logout?access_token="+json.getString("access_token"));
			Log.d("Dailymotion","logout:"+answer);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		setJson_token(null);
	}
	
	public static boolean isSessionValid() {
		JSONObject json = getJson_token();
		if(json==null){
			Log.w("Dailymotion","You were not logged in...");
			return false;
		}
		try {
			String answer = http_get_access("https://api.dailymotion.com/videos?access_token="+json.getString("access_token"));
			if(answer!=null)
				Log.d("Dailymotion","check authentication:"+answer);
			else
				return false;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String getMyUserId() {
		String returned=null;
		JSONObject json = getJson_token();
		Log.d("Dailymotion","json_token:"+json);
		if(json==null){
			Log.w("Dailymotion","You were not logged in to get the UserId...");
			return returned;
		}
		try {
			returned = json.getString("uid");
			if(returned !=null){
				Log.d("Dailymotion","MyUserId:"+returned);
			}
			else{
				return returned;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return returned;
		}
		return returned;
	}
	
	public static String publish_a_video(File f,String title,String channel,String tag,String published) {
		String videoId="";
		String temp="";
		JSONObject json = getJson_token();
		if(f==null) {
			Log.w("Dailymotion","No file to upload specified");
			return null;
		}			
		if(!isSessionValid())
			return null;
		try {
			String sUploadLink = get_an_upload_url(json.getString("access_token"));
			Log.w("Dailymotion","upload URL:"+ sUploadLink);
			String url = upload_file(f, sUploadLink);
			Log.w("Dailymotion","get the real url:"+url);
			temp = http_post_access(
					"https://api.dailymotion.com/me/videos?access_token="+json.getString("access_token"),
					"url="+url+"&title="+title+"&channel="+channel+"&tags="+tag+"&published="+published);
			Log.w("Dailymotion","video id:"+temp);
			JSONObject json_answer = new JSONObject(temp);
			videoId = json_answer.getString("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return videoId;
	}
	
	private static String get_an_upload_url(String access_token) {
		String upload_url=null;
		String answer = http_get_access("https://api.dailymotion.com/file/upload?access_token="+access_token);
        try {
    		JSONObject json = new JSONObject(answer);
			upload_url = json.getString("upload_url");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return upload_url;
	}
	
	private static String upload_file(File f,String upload_uri) {
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return real_upload_url;
	}
	
	private static String http_get_access(String url){
        String response=null;
        HttpURLConnection login_request=null;
        try {
        	Log.d("Dailymotion","retrieving http get access:"+url);
        	URL login_url = new URL(url);
        	login_request = (HttpURLConnection) login_url.openConnection();
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
			Log.w("Dailymotion","Probably not logged in");
			Log.d("Dailymotion","http_get:"+e.toString());
		}finally {
        	if(login_request!=null)
        		login_request.disconnect();
        }
        return response;
	}
	
	private static String http_post_access(String url,String body){
        String response=null;
        HttpURLConnection login_request=null;
		Log.d("Dailymotion","http_post used url:"+url+",body:"+body);
        try {
        	URL login_url = new URL(url);
        	login_request = (HttpURLConnection) login_url.openConnection();
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
	        response="";
	        while((currentLine = in.readLine()) != null)
	        	response += currentLine + "\n";
	        in.close();
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(login_request!=null)
				login_request.disconnect();
        }
        return response;
	}

	public static JSONObject getJson_token() {
		return json_token;
	}

	public static void setJson_token(JSONObject json_token) {
		Dailymotion.json_token = json_token;
	}
	public static ArrayList<VideoDailymotion> getListofBestRatedVideoFromUser(String user)
	{
		return getListOfVideosWithParameters("user="+user+"&sort=rated");
	}
	public static ArrayList<VideoDailymotion> getListofMostViewVideoFromUser(String user)
	{
		return getListOfVideosWithParameters("user="+user+"&sort=visited");
	}
	private static ArrayList<VideoDailymotion> getListOfVideosWithParameters(String parameters) {

		ArrayList<VideoDailymotion> videoList = new ArrayList<VideoDailymotion>();
		boolean has_more=true;
		int page = 1;
		while(has_more){
			String answer = http_get_access("https://api.dailymotion.com/videos&page="+page+"&limit=10&fields=id,title,status,rating,views_total&"+parameters);
			has_more = parseListOfVideosWithParametersAndAddToArray(answer,parameters,videoList);
			page++;
		}
		return videoList;
	}	
	private static boolean parseListOfVideosWithParametersAndAddToArray(String answer,String parameters,ArrayList<VideoDailymotion> videoList) {
		boolean returned=false;
		int index=0;
		if(answer==null)
			return returned;
		try {
    		JSONObject json = new JSONObject(answer);
    		returned = (json.getString("has_more").compareTo("false")!=0);
			JSONArray jsonvideolist = json.getJSONArray("list");
			while(!jsonvideolist.isNull(index)){
				videoList.add(new VideoDailymotion(
						jsonvideolist.getJSONObject(index).getString("id"),
						jsonvideolist.getJSONObject(index).getString("title"),
						jsonvideolist.getJSONObject(index).getInt("rating"),
						jsonvideolist.getJSONObject(index).getInt("views_total"),
						(jsonvideolist.getJSONObject(index).getString("status").compareTo("published")==0)
						));
				index++;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returned;
	}
}