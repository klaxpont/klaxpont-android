package com.klaxpont.android;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.LoginButton;
import com.facebook.android.SessionEvents;
import com.facebook.android.SessionEvents.LogoutListener;
import com.facebook.android.SessionStore;
import com.facebook.android.Util;
import com.facebook.android.SessionEvents.AuthListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// Sets the content view to R.layout.main
public class Main extends Activity {

	private static final int RETURN_FROM_MAIN_LOGGED_IN = 1;
	private static final int RETURN_FROM_VISITOR = 2;
	
	private TextView tLoginStatus;
	private LoginButton bLoginButton;
	private Button bVisitButton;

    private Facebook mFacebook;
    private AsyncFacebookRunner mAsyncRunner;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		   setContentView(R.layout.startup);
		   tLoginStatus = (TextView) findViewById(R.id.loginStartupStatus);
		   bLoginButton = (LoginButton) findViewById(R.id.loginStartup);
		   bVisitButton = (Button) findViewById(R.id.visitButton);
		   
		   
		   mFacebook = new Facebook(Constants.FACEBOOK_APP_ID);
		   mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		   
		   Log.i("Main","Appli started");
		   
		   SessionStore.restore(mFacebook, this);
	       SessionEvents.addAuthListener(new FacebookAuthListener());
	       SessionEvents.addLogoutListener(new FacebookLogoutListener());
		   bLoginButton.init(this, mFacebook);
	        if (mFacebook.isSessionValid())
	        	mAsyncRunner.request("me", new FacebookRequestListener());
		   
		   bVisitButton.setOnClickListener(new View.OnClickListener() {
	       		public void onClick(View v){
	       			Log.i("Main","Launching VisitButton");
	       			Intent intent = new Intent(Main.this, OldMainLoggedIn.class);
	       			startActivityForResult(intent, RETURN_FROM_VISITOR);
	       			Log.i("Main","Launched VisitButton");
	       		}
	       	});
	
	}
	public class FacebookRequestListener extends BaseRequestListener {
	    public void onComplete(final String response, final Object state) {
	        try {
	            // process the response here: executed in background thread
	            Log.d("Facebook-Main", "Response: " + response.toString());
	            JSONObject json = Util.parseJson(response);
	            final String name = json.getString("name");
	            final String id = json.getString("id");
	            Log.i("Main","Facebook get name:"+name+" and id:"+id);
	            // then post the processed result back to the UI thread
	            // if we do not do this, an runtime exception will be generated
	            // e.g. "CalledFromWrongThreadException: Only the original
	            // thread that created a view hierarchy can touch its views."
	            Main.this.runOnUiThread(new Runnable() {
	                public void run() {
		       			Log.i("Main","Launching MainLoggedIn");
		       			//Intent intent = new Intent(Main.this, MainLoggedIn.class);
		       			Intent intent = new Intent(Main.this, MainLoggedInTab.class);
		       			intent.putExtra("FacebookName",name);
		       			intent.putExtra("FacebookId",id);
		       			startActivityForResult(intent, RETURN_FROM_MAIN_LOGGED_IN);
		       			Log.i("Main","Launched MainLoggedIn");
	                }
	            });
	        } catch (JSONException e) {
	            Log.w("Facebook-Main", "JSON Error in response");
	        } catch (FacebookError e) {
	            Log.w("Facebook-Main", "Facebook Error: " + e.getMessage());
	        }
	    }
	}
	public class FacebookAuthListener implements AuthListener {
        public void onAuthSucceed() {
            //tFacebookName.setText("You have logged in! ");
            //asking the facebook login...
            mAsyncRunner.request("me", new FacebookRequestListener());
            tLoginStatus.setText("Logged in !");
        }

        public void onAuthFail(String error) {
            tLoginStatus.setText("Authentication Failed !");
        }
    }
	public class FacebookLogoutListener implements LogoutListener {
        public void onLogoutBegin() {
            //tFacebookName.setText("Logging out...");
            //tFacebookId.setVisibility(View.INVISIBLE);
        }

        public void onLogoutFinish() {
            //tFacebookName.setText("You have logged out! ");
        }
    }
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
	    switch(requestCode) {
        case RETURN_FROM_MAIN_LOGGED_IN:
        	Log.i("Main","Returned from main logged in, going to stop");
        	finish();
        	return;
        case RETURN_FROM_VISITOR:
		default :
            
            break;
        }
    	mFacebook.authorizeCallback(requestCode, resultCode, data);
    }
}