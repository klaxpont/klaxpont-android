package com.klaxpont.android;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

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
       		@Override
       	    public void onClick(View v){
       			//TODO
       		}
       	});
		bButtonHorn.setOnClickListener(new View.OnClickListener() {			
       		@Override
       	    public void onClick(View v){
       			//TODO
       			MediaPlayer mp = MediaPlayer.create(RecordAVideo.this, R.raw.boathorn);   
                mp.start();
                mp.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
		                mp.release();						
					}
                });
       		}
       	});
	}
}