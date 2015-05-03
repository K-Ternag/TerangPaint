package com.terang.board;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.terang.paintboard.R;

public class MainPicCommentView extends LinearLayout {

	TextView commentList;
	TextView commentDt;
	Button delButton;
	LinearLayout cButtonLay;
	
	public MainPicCommentView(Context context){
		super(context);
		
		init(context);
	}
	
	public MainPicCommentView(Context context, AttributeSet attrs){
		super(context, attrs);
		
		init(context);
	}
	
	private void init(Context context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.commentview_item, this, true);

		commentList = (TextView)findViewById(R.id.commentList);
		commentDt = (TextView)findViewById(R.id.commentDate);
		delButton = (Button)findViewById(R.id.deleteComment);
		//cButtonLay = (LinearLayout)findViewById(R.id.cButtonLayout);
		//cButtonLay.setVisibility(View.GONE);
	}

	public void setContents(int index, String data) {
		if (index == 0) {
			commentList.setText(data);
		} else if (index == 1) {
			commentDt.setText(data);
		} else {
			throw new IllegalArgumentException();
		}
	}
}
