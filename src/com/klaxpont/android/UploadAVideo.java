package com.klaxpont.android;

import java.io.File;

import com.dailymotion.android.FilePickerActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

public class UploadAVideo extends Activity {

	private static final int REQUEST_PICK_FILE = 1;
	private Button bSelectUploadVideo;
	private File fSelectedFile;
	private VideoView vPreviewUploadVideo;
	private TextView tTextUploadVideoSelected;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uploadavideo);
		bSelectUploadVideo = (Button) findViewById(R.id.selectUploadVideo);
		bSelectUploadVideo.setOnClickListener(new View.OnClickListener() {
       		public void onClick(View v){
       			Intent intent = new Intent(UploadAVideo.this, FilePickerActivity.class);
       			startActivityForResult(intent, REQUEST_PICK_FILE);
       		}
       	});
		//startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.youtube.com/watch?v=tsDYIgX_gDs")));
		/*
		vPreviewUploadVideo = (VideoView)findViewById(R.id.previewUploadVideo);
        vPreviewUploadVideo.setVideoURI(Uri.parse("http://www.videos-quad.fr/videos/68/vid-00018-20100131-1651.3gp"));
		vPreviewUploadVideo.setMediaController(new MediaController(this));
		vPreviewUploadVideo.requestFocus();
        vPreviewUploadVideo.start();
        */
		tTextUploadVideoSelected = (TextView)findViewById(R.id.textUploadVideoSelected);
		
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
	    switch(requestCode) {
        case REQUEST_PICK_FILE:
        	if(resultCode == RESULT_OK) {
        		if(data.hasExtra(FilePickerActivity.EXTRA_FILE_PATH)) {
                // Get the file path
            	fSelectedFile = new File(data.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH));
                // Set the file path text view
                tTextUploadVideoSelected.setText(fSelectedFile.getPath());
                //Now you have your selected file, You can do your additional requirement with file.
                //vPreviewUploadVideo.setVideoPath(Uri.parse(fSelectedFile.getPath()));
                vPreviewUploadVideo.setVideoURI(Uri.parse("http://www.videos-quad.fr/videos/68/vid-00018-20100131-1651.3gp"));
                vPreviewUploadVideo.start();
        		}
            }
        	return;
        default :
            
            break;
        }
    }
}