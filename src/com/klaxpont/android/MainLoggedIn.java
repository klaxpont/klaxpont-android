package com.klaxpont.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainLoggedIn extends Activity {
	
	private TextView tMainLoggedInTopText;
	private String sFacebookId="";
	private String sFacebookName="";
	private Button bRecordAVideo;
	private Button bUploadAVideo;
	private Button bWatchVideos;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainloggedin);
		tMainLoggedInTopText = (TextView) this.findViewById(R.id.mainLoggedInTopText);
		Intent sender=getIntent();
		sFacebookId = sender.getExtras().getString("FacebookId");
		sFacebookName = sender.getExtras().getString("FacebookName");
		
		tMainLoggedInTopText.setText("Hello "+sFacebookName+" with id:"+sFacebookId);
		//TO DELETE	       	startActivity(new Intent(MainLoggedIn.this, WatchVideos.class));		
		
		bRecordAVideo = (Button) this.findViewById(R.id.recordAVideo);
		bRecordAVideo.setOnClickListener(new View.OnClickListener() {
       		public void onClick(View v){
       	       	startActivity(new Intent(MainLoggedIn.this, RecordAVideo.class));
       		}
       	});
		bUploadAVideo = (Button) this.findViewById(R.id.uploadAVideo);
		bUploadAVideo.setOnClickListener(new View.OnClickListener() {
       		public void onClick(View v){
       	       	startActivity(new Intent(MainLoggedIn.this, UploadAVideo.class));
       		}
       	});
		bWatchVideos = (Button) this.findViewById(R.id.watchVideos);
		bWatchVideos.setOnClickListener(new View.OnClickListener() {
       		public void onClick(View v){
       	       	startActivity(new Intent(MainLoggedIn.this, WatchVideos.class));
       		}
       	});
	}	
}