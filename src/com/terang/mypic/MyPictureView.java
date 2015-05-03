package com.terang.mypic;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.terang.common.BasicInfo;
import com.terang.paintboard.R;
import com.terang.paintboard.R.id;
import com.terang.paintboard.R.layout;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyPictureView extends LinearLayout {

	ImageView image;
	TextView text02;
	TextView text04;
	
	public MyPictureView(Context context){
		super(context);
		
		init(context);
	}
	
	public MyPictureView(Context context, AttributeSet attrs){
		super(context, attrs);
		
		init(context);
	}
	
	private void init(Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.mypic_item, this, true);

		image = (ImageView)findViewById(R.id.mImage);
		text02 = (TextView)findViewById(R.id.mText);
		text04 = (TextView)findViewById(R.id.mDate);
	}

	public void setContents(int index, String data) {
		if (index == 0) {
			text04.setText(data);
		} else if (index == 1) {
			text02.setText(data);
		} else if (index == 2) {
			if (data == null || data.equals("-1") || data.equals("")) {
				image.setImageBitmap(null);
			} else {
				//image.setImageURI(Uri.parse(BasicInfo.FOLDER_HANDWRITING + data));
				ImageLoader.getInstance().displayImage("file://"+BasicInfo.FOLDER_HANDWRITING + data
						, image, MyPictureActivity.options);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
}
