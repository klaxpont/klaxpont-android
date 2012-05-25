package com.klaxpont.android;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.VideoView;
import android.content.Intent;

public class WatchAVideo extends Activity {
	private TextView tVideoTitle;
	private TextView tNumberOfViewed;
	private String sVideoPath;
	private int iNbOfView=0;
	private int iCurrentRate=0;
	private VideoView vWatchAVideo;
	private TableRow tbVideoRatedLine;
	private RatingBar rbVideoRate;
	private RatingBar rbVideoRateGiven;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watchavideo);
		tVideoTitle = (TextView)findViewById(R.id.videoTitle);
		Intent sender=getIntent();
		tVideoTitle.setText(sender.getExtras().getString("Title"));
		sVideoPath = sender.getExtras().getString("Path");
		iNbOfView = sender.getExtras().getInt("NbOfView");
		iCurrentRate = sender.getExtras().getInt("Rate");

		Log.d("WatchAVideo","Path to the video to read:"+sVideoPath);
		Log.d("WatchAVideo","rate:"+iCurrentRate+",Viewed:"+iNbOfView);
		
       	vWatchAVideo = (VideoView)findViewById(R.id.videoWindow);

       	if(iNbOfView>0) {
       		tNumberOfViewed = (TextView)findViewById(R.id.videoNbOfView);
       		tNumberOfViewed.setText("Viewed:"+iNbOfView);
   			rbVideoRate = (RatingBar)findViewById(R.id.videoRate);
       		if(iCurrentRate>0) {
       			rbVideoRate.setRating(iCurrentRate);
       		} else {
       			rbVideoRate.setVisibility(View.INVISIBLE);
       		}
       	} else {
       		tbVideoRatedLine = (TableRow)findViewById(R.id.videoRatedLine);
       		tbVideoRatedLine.setVisibility(View.INVISIBLE);
       	}
       	/*
       	vWatchAVideo.setVideoPath(sVideoPath);
       	vWatchAVideo.setMediaController(new MediaController(this));
       	vWatchAVideo.requestFocus();
       	vWatchAVideo.start();
       	*/
       	rbVideoRateGiven = (RatingBar)findViewById(R.id.videoRateGiven);
       	
	}
}