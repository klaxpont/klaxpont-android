package com.klaxpont.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class KlaxpontSettings extends Activity {

	private TextView tTitle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.klaxpontsettings);
		tTitle=(TextView)findViewById(R.id.textViewForSettings);
		tTitle.setText("To be done");
		
	}

}