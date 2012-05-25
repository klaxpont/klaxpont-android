package com.klaxpont.android;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class RecordAVideo extends Activity {

	ImageButton bButtonRecord;
	ImageButton bButtonHorn;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	}
}