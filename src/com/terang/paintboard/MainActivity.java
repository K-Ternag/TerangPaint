package com.terang.paintboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends Activity {

	public static final String TAG = "Main Activity";

	/**
	 * 애니메이션 객체
	 */
	Animation alphaIconAnim;
	
	/**
	 * Picture
	 */
	ImageView icon;
	
	Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		icon = (ImageView) findViewById(R.id.iconContents);
        alphaIconAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_contents);
        icon.setVisibility(View.VISIBLE);
        icon.startAnimation(alphaIconAnim);
        
        intent = new Intent(this, StartActivity.class);
        
        new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				startActivity(intent);
			}
            
        }, 3500);
	}

}