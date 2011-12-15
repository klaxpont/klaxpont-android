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

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class Main extends Activity {

    // Your Facebook Application ID must be set before running this example
    // See http://www.facebook.com/developers/createapp.php

    private LoginButton mLoginButton;
    private TextView mFacebookName;
    private TextView mFacebookId;

    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        mLoginButton = (LoginButton) findViewById(R.id.login);
        mFacebookName = (TextView) Main.this.findViewById(R.id.txtname);
        mFacebookId = (TextView) Main.this.findViewById(R.id.txtid);

       	mFacebook = new Facebook(Constants.FACEBOOK_APP_ID);
       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);

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
}
