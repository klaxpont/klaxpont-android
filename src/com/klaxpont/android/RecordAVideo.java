package com.klaxpont.android;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class RecordAVideo extends Activity {

	ImageButton bButtonRecord;
	ImageButton bButtonHorn;
	
	
	Uri uriVideo = null;
	final static int REQUEST_VIDEO_CAPTURED = 1;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
		startActivityForResult(intent, REQUEST_VIDEO_CAPTURED);
		/*
		setContentView(R.layout.recordavideo);
		bButtonRecord = (ImageButton) findViewById(R.id.buttonRecord);
		bButtonHorn = (ImageButton) findViewById(R.id.buttonHorn);
		bButtonRecord.setOnClickListener(new View.OnClickListener() {
       		public void onClick(View v){
       			//TODO
       		}
       	});
		bButtonHorn.setOnClickListener(new View.OnClickListener() {			
       		public void onClick(View v){
       			//TODO
       			MediaPlayer mp = MediaPlayer.create(RecordAVideo.this, R.raw.boathorn);   
                mp.start();
                mp.setOnCompletionListener(new OnCompletionListener() {

					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
		                mp.release();						
					}
                });
       		}
       	});
       	*/
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode == RESULT_OK){
		 if(requestCode == REQUEST_VIDEO_CAPTURED){
		  uriVideo = data.getData();
		  Toast.makeText(this,uriVideo.getPath(),
		    Toast.LENGTH_LONG)
		    .show();
		 }
		}else if(resultCode == RESULT_CANCELED){
		 uriVideo = null;
		 Toast.makeText(this,
		   "Cancelled!",
		   Toast.LENGTH_LONG)
		   .show();
		}
		}
}