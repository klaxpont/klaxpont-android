package com.klaxpont.android;

import android.app.TabActivity;
import android.content.Intent;
import android.media.audiofx.BassBoost.Settings;
import android.os.Bundle;
import android.widget.TabHost;

public class MainLoggedInTab extends TabActivity {

	private TabHost tabHost;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainloggedintab);
        tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Watch",getResources().getDrawable(R.drawable.videofolder)
        		).setContent(new Intent(this,WatchVideos.class)));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("Record",getResources().getDrawable(R.drawable.camera)
        		).setContent(new Intent(this,RecordAVideo.class)));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("Upload",getResources().getDrawable(R.drawable.upload)
        		).setContent(new Intent(this,UploadAVideo.class)));
        tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator("Maps",getResources().getDrawable(R.drawable.networkfolder)
        		).setContent(new Intent(this,Maps.class)));
        tabHost.addTab(tabHost.newTabSpec("tab5").setIndicator("Settings",getResources().getDrawable(R.drawable.settings)
        		).setContent(new Intent(this,KlaxpontSettings.class)));
        /*
        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++) 
        {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#00FF00"));
        }
        */
    }
}