package com.klaxpont.android;

import java.util.ArrayList;

import com.appliweb.android.AppliWeb;
import com.dailymotion.android.Dailymotion;
import com.dailymotion.android.VideoDailymotion;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

public class WatchVideos extends Activity implements AdapterView.OnItemSelectedListener {
	private Spinner spVideoType;
	private ListView lvVideoList;
	private String videoType[] = {"Most Viewed","Best Rated"}; //static for the moment
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watchvideos);
		if(!Dailymotion.isSessionValid()) {
       		Dailymotion.setJson_token(AppliWeb.getJson_dailymotion_token());
       		if(Dailymotion.isSessionValid())
       		{
       			Log.i("WatchVideos","Dailymotion Login");
       		}else{
       			Log.w("WatchVideos","Dailymotion unable to login, verify your credential");
       			return;
       		}
       	}
		spVideoType = (Spinner)findViewById(R.id.spinnerTypeOfVideo);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
	            android.R.layout.simple_spinner_item, videoType);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spVideoType.setAdapter(spinnerAdapter);
		spVideoType.setOnItemSelectedListener((OnItemSelectedListener) this);
	}
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
		Log.d("WatchVideos","Spinner selected:"+position);
		switch(position)
		{
		case 0 :
			showVideosList(Dailymotion.getListofMostViewVideoFromUser(Dailymotion.getMyUserId()));
			break;
		case 1 :
			showVideosList(Dailymotion.getListofBestRatedVideoFromUser(Dailymotion.getMyUserId()));
			break;
		default :
			break;			
		}
	}

	public void onNothingSelected(AdapterView<?> parent) {
		Log.d("WatchVideos","Spinner do not select anything");
	}
	private void showVideosList(final ArrayList<VideoDailymotion> videoList) {
		
		String[] lsVideos = new String[videoList.size()];
		for (int i=0; i < videoList.size();i++)
			lsVideos[i]=videoList.get(i).getTitle();
		
		lvVideoList = (ListView)findViewById(R.id.listViewWatchVideos);
		lvVideoList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,lsVideos));
		lvVideoList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view, int position, long arg)   {
				Log.d("WatchVideos","Asked to watch :"+videoList.get(position).stringize());
				Dailymotion.getEmbedUrl(videoList.get(position).getId());
				//Intent intent = new Intent(WatchVideos.this, WatchAVideo.class);
				//Intent intent = new Intent(WatchVideos.this, MediaPlayerTest.class);
				startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(Dailymotion.getEmbedUrl(videoList.get(position).getId()))));
				/*
				intent.putExtra("Title",videoList.get(position).getTitle());
       			intent.putExtra("Path",Dailymotion.getEmbedUrl(videoList.get(position).getId()));
       			intent.putExtra("NbOfView",videoList.get(position).getNbOfView());
       			intent.putExtra("Rate",videoList.get(position).getRating());
       			startActivity(intent);
       			*/
			}
		});
	}
	
}