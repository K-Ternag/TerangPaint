package com.terang.board;

import com.nostra13.universalimageloader.core.ImageLoader;
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

public class MainPictureView extends LinearLayout {

	ImageView image;
	TextView text01;
	TextView text02;
	TextView text03;
	TextView text04;
	TextView text05;
	TextView text06;
	
	public MainPictureView(Context context){
		super(context);
		
		init(context);
	}
	
	public MainPictureView(Context context, AttributeSet attrs){
		super(context, attrs);
		
		init(context);
	}
	
	private void init(Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.mainpic_item, this, true);

		image = (ImageView)findViewById(R.id.sImage);
		text01 = (TextView)findViewById(R.id.sText);
		text02 = (TextView)findViewById(R.id.sUser);
		text03 = (TextView)findViewById(R.id.sDate);
		text04 = (TextView)findViewById(R.id.sLike);
		text05 = (TextView)findViewById(R.id.sView);
		text06 = (TextView)findViewById(R.id.sReply);
	}

	public void setContents(int index, String data) {
		if (index == 0) {
			text01.setText(data);
		} else if (index == 1) {
			text02.setText(data + " 님의 글");
		} else if (index == 2) {
			text03.setText(data + " 에 등록");
		} else if (index == 3) {
			if (data == null || data.equals("-1") || data.equals("")) {
				image.setImageBitmap(null);
			} else {
				//image.setImageURI(Uri.parse(BasicInfo.FOLDER_HANDWRITING + data));
				ImageLoader.getInstance().displayImage(data, image, MainPictureActivity.options);
			}
		} else if (index == 4) {
			text04.setText("좋아한 이 " + data + "명");
		} else if (index == 5) {
			text05.setText("기웃거린 이 " + data + "명");
		} else if (index == 6) {
			text06.setText("댓글단 이 " + data + "명");
		} else {
			throw new IllegalArgumentException();
		}
	}
}
