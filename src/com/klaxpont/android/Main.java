/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.klaxpont.android;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.LoginButton;
import com.facebook.android.SessionEvents;
import com.facebook.android.SessionStore;
import com.facebook.android.Util;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;

import com.klaxpont.android.Constants;

public class Main extends Activity implements SurfaceHolder.Callback{

	private Button mDailymotion;
	private LoginButton mLoginButton;
    private TextView mFacebookName;
    private TextView mFacebookId;
    
    private Preview mPreview;
    private boolean iCameraEnable;
    private Camera mCamera;

    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;
    
    private String mAccessTokenDailymotion="";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setiCameraEnable(false);
        
        setContentView(R.layout.main);
        mDailymotion = (Button) findViewById(R.id.dailymotion_button);
        mLoginButton = (LoginButton) findViewById(R.id.login);
        mFacebookName = (TextView) Main.this.findViewById(R.id.txtname);
        mFacebookId = (TextView) Main.this.findViewById(R.id.txtid);
        
        mPreview = (Preview) Main.this.findViewById(R.id.preview_camera);
        mPreview.init(this);
        
       	mFacebook = new Facebook(Constants.FACEBOOK_APP_ID);
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);

       	mDailymotion.setOnClickListener(new View.OnClickListener() {
       		@Override
       	    public void onClick(View v){
				try {
					mAccessTokenDailymotion = get_access_token();
				} catch (Exception e) {
					e.printStackTrace();
				} catch (FacebookError e) {
					e.printStackTrace();
				}
				Log.w("Dailymotion","access_token:"+ mAccessTokenDailymotion);
				try {
					String answer = http_get_access("https://api.dailymotion.com/me/videos?access_token="+mAccessTokenDailymotion);
					Log.w("Dailymotion","answer to /me:"+ answer);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FacebookError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
       	    }
       	});
       	
        SessionStore.restore(mFacebook, this);
        SessionEvents.addAuthListener(new SampleAuthListener());
        SessionEvents.addLogoutListener(new SampleLogoutListener());
        mLoginButton.init(this, mFacebook);
        if (mFacebook.isSessionValid())
        	mAsyncRunner.request("me", new SampleRequestListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }

    public class SampleAuthListener implements AuthListener {

        @Override
		public void onAuthSucceed() {
            mFacebookName.setText("You have logged in! ");
            //asking the facebook login...
            mAsyncRunner.request("me", new SampleRequestListener());
        }

        @Override
		public void onAuthFail(String error) {
            mFacebookName.setText("Login Failed: " + error);
        }
    }

    public class SampleLogoutListener implements LogoutListener {
        @Override
		public void onLogoutBegin() {
            mFacebookName.setText("Logging out...");
            mFacebookId.setVisibility(View.INVISIBLE);
        }

        @Override
		public void onLogoutFinish() {
            mFacebookName.setText("You have logged out! ");
        }
    }

    public class SampleRequestListener extends BaseRequestListener {

        @Override
		public void onComplete(final String response, final Object state) {
            try {
                // process the response here: executed in background thread
                Log.d("Facebook-Main", "Response: " + response.toString());
                JSONObject json = Util.parseJson(response);
                final String name = json.getString("name");
                final String id = json.getString("id");

                // then post the processed result back to the UI thread
                // if we do not do this, an runtime exception will be generated
                // e.g. "CalledFromWrongThreadException: Only the original
                // thread that created a view hierarchy can touch its views."
                Main.this.runOnUiThread(new Runnable() {
                    @Override
					public void run() {
                        mFacebookName.setText("name:" + name);
                        mFacebookId.setVisibility(View.VISIBLE);
                        mFacebookId.setText("id:" + id);
                    }
                });
            } catch (JSONException e) {
                Log.w("Facebook-Main", "JSON Error in response");
            } catch (FacebookError e) {
                Log.w("Facebook-Main", "Facebook Error: " + e.getMessage());
            }
        }
    }
    
    public boolean openCamera() {
    	if(getiCameraEnable())
    	{
	    	try{
		   		mCamera = Camera.open(0);
		   		if (mCamera != null){
		   			Camera.Parameters params = mCamera.getParameters();
		   			mCamera.setParameters(params);
		   		}
		   		else {
		   			Toast.makeText(getApplicationContext(), "Camera not available!", Toast.LENGTH_LONG).show();
		   			return false;
		   		}
	    	} catch (RuntimeException e) {
	            Log.w("Camera", "Unable to open : "+ e.getMessage());
	            mCamera = null;
	            return false;
	        }
			return true;
    	}else
    		return false;
    }
    
    public void closeCamera() {
    	if(getiCameraEnable())
    	{
    		if (mCamera != null) {
                mPreview.setCamera(null);
                mCamera.release();
                mCamera = null;
            }
    	}
    }
    
    @Override
   	public void surfaceCreated(SurfaceHolder holder) {
    	openCamera();
   	}

    @Override
    protected void onResume() {
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        if(openCamera())
        	mPreview.setCamera(mCamera);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        closeCamera();
    }
    
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	public boolean getiCameraEnable() {
		return iCameraEnable;
	}

	public void setiCameraEnable(boolean iCameraEnable) {
		this.iCameraEnable = iCameraEnable;
	}
	
	public static String get_access_token() throws Exception, FacebookError {
		String access_token="";
		String body="grant_type=password&client_id="+Constants.DAILYMOTION_API_KEY+"&client_secret="+Constants.DAILYMOTION_SECRET_API_KEY+"&username="+Constants.DAILYMOTION_USER+"&password="+Constants.DAILYMOTION_PASSWORD;
		URL login_url = new URL(Constants.DAILYMOTION_ACCESS_TOKEN_URL);
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
	        String response 	= "";
	        String currentLine 	= "";
	        while((currentLine = in.readLine()) != null)
	        	response += currentLine + "\n";
            JSONObject json = Util.parseJson(response);
            access_token = json.getString("access_token");
            //final String refresh_token = json.getString("refresh_token");
	        in.close();
        }finally {
        	login_request.disconnect();
        }
        return access_token;
	}
	
	public static String http_get_access(String url) throws Exception, FacebookError {
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
            /*
	        JSONObject json = Util.parseJson(response);
            final String access_token = json.getString("access_token");
            final String refresh_token = json.getString("refresh_token");
            */
	        in.close();
        }finally {
        	login_request.disconnect();
        }
        return response;
	}
	
	public static String http_post_access(String url,String body) throws Exception, FacebookError {
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
            /*
	        JSONObject json = Util.parseJson(response);
            final String access_token = json.getString("access_token");
            final String refresh_token = json.getString("refresh_token");
            */
	        in.close();
        }finally {
        	login_request.disconnect();
        }
        return response;
	}
}
