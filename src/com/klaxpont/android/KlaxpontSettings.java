package com.klaxpont.android;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class KlaxpontSettings extends Activity {

	private TextView tTitle;
	private ImageView iImageFacebook;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.klaxpontsettings);
		tTitle=(TextView)findViewById(R.id.textViewForSettings);
		tTitle.setText("To be done");
		iImageFacebook=(ImageView)findViewById(R.id.imageFacebook);
		if(MainLoggedInTab.getFacebookId()!=0){
			URL img_value = null;
			try {
				img_value = new URL("http://graph.facebook.com/"+MainLoggedInTab.getFacebookId()+"/picture?type=large");
				Bitmap mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
				iImageFacebook.setImageBitmap(mIcon1);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		}
		
	}

}