package com.terang.paintboard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.terang.board.MainPicCommentListItem;
import com.terang.board.MainPicCommentdapter;
import com.terang.board.MainPicListItem;
import com.terang.board.MainPicdapter;
import com.terang.board.MainPictureActivity;
import com.terang.common.BasicInfo;
import com.terang.mypic.MyPictureActivity;

import android.R.color;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity {

	public static final String TAG = "Start Activity";
	final private static int DIALOG_LOGIN = 1;
	public static ImageLoaderConfiguration config;
	public static DisplayImageOptions options;

	/**
	 * 페이지가 열려 있는지 알기 위한 플래그
	 */
	boolean isPageOpen = false;

	/**
	 * 애니메이션 객체
	 */
	Animation translateLeftAnim;
	Animation translateRightAnim;

	/**
	 * 슬라이딩으로 보여지는 페이지 레이아웃
	 */
	LinearLayout slidingPage;
	LinearLayout mainPage;
	
	/**
	 * 버튼
	 */
	Button button1;
	
	/**
	 * Picture
	 */
	ImageView bestPic;
	ImageView currentPic01;
	ImageView currentPic02;
	ImageView currentPic03;
	ImageView currentPic011;
	ImageView currentPic021;
	ImageView currentPic031;
	
	/**
	 * Device ID
	 */
	String DevID;
	String PhoneNumber;
	TelephonyManager tManager;
	String dev_id="";
	String tel_no="";
	String user_id="";
	EditText userName;
	sharedPref pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
        button1 = (Button) findViewById(R.id.button1);
        mainPage = (LinearLayout) findViewById(R.id.mainPage);
        pref = new sharedPref(this);
		
		tManager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		DevID = tManager.getDeviceId();
        PhoneNumber = tManager.getLine1Number();
        
        if (PhoneNumber == null){
        	PhoneNumber = "00000000000";
        } else{
            PhoneNumber = PhoneNumber.substring(PhoneNumber.length()-10,PhoneNumber.length());
            PhoneNumber="0"+PhoneNumber;
        }
		
		//Toast.makeText(getApplicationContext(), DevID + " : " + PhoneNumber, Toast.LENGTH_LONG).show();
        
        new checkUser().execute();
	}
	

    public void launchGoBoard(View v) {
		slidingPage.startAnimation(translateRightAnim);
        Intent intent = new Intent(getApplicationContext(), MainPictureActivity.class);
        startActivity(intent);
    }

    public void launchDrawing(View v) {
		slidingPage.startAnimation(translateRightAnim);
        Intent intent = new Intent(getApplicationContext(), HandwritingMakingActivity.class);
        startActivity(intent);
    }

    public void launchMypicture(View v) {
		slidingPage.startAnimation(translateRightAnim);
        Intent intent = new Intent(getApplicationContext(), MyPictureActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_VIEW_ACTIVITY);
    }

	public void onButton1Clicked(View v) {
		// 애니메이션 적용
		if (isPageOpen) {
			slidingPage.startAnimation(translateRightAnim);
			//mainPage.setBackgroundColor(Color.WHITE);
		} else {
			slidingPage.setVisibility(View.VISIBLE);
			slidingPage.startAnimation(translateLeftAnim);
			//mainPage.setBackgroundColor(Color.GRAY);
		}
	}
	
    /**
     * 애니메이션 리스너 정의
     */
    private class SlidingPageAnimationListener implements AnimationListener {
    	/**
    	 * 애니메이션이 끝날 때 호출되는 메소드
    	 */
		public void onAnimationEnd(Animation animation) {
			if (isPageOpen) {
				slidingPage.setVisibility(View.INVISIBLE);

				button1.setText("Open");
				isPageOpen = false;
			} else {
				button1.setText("Close");
				isPageOpen = true;
			}
		}

		public void onAnimationRepeat(Animation animation) {

		}

		public void onAnimationStart(Animation animation) {

		}

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void onload(){
		
		pref.put(sharedPref.PREF_USER_ID, user_id);
		new mkUserdir().execute();
		
        // 슬라이딩으로 보여질 레이아웃 객체 참조
        slidingPage = (LinearLayout) findViewById(R.id.slidingPage01);

        // 애니메이션 객체 로딩
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        // 애니메이션 객체에 리스너 설정
        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

		
		// Universal Image Loader Start
		
		config = new ImageLoaderConfiguration.Builder(this)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs()
		.build();
		
		ImageLoader.getInstance().init(config);
		
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.build();

		// Universal Image Loader End

        bestPic = (ImageView)findViewById(R.id.bestPic);
    	currentPic01 = (ImageView)findViewById(R.id.currentPic01);
    	currentPic02 = (ImageView)findViewById(R.id.currentPic02);
    	currentPic03 = (ImageView)findViewById(R.id.currentPic03);
    	currentPic011 = (ImageView)findViewById(R.id.currentPic011);
    	currentPic021 = (ImageView)findViewById(R.id.currentPic021);
    	currentPic031 = (ImageView)findViewById(R.id.currentPic031);

        // SD Card checking
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		Toast.makeText(this, "You don't have SD Card!!", Toast.LENGTH_LONG).show();
    		return;
    	} else {
    		String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    		if (!BasicInfo.ExternalChecked && externalPath != null) {
    			BasicInfo.ExternalPath = externalPath + File.separator;
    			Log.d(TAG, "ExternalPath : " + BasicInfo.ExternalPath);
    			
    			BasicInfo.FOLDER_HANDWRITING = BasicInfo.ExternalPath + BasicInfo.FOLDER_HANDWRITING;
    			BasicInfo.DATABASE_NAME = BasicInfo.ExternalPath + BasicInfo.DATABASE_NAME;

    			BasicInfo.ExternalChecked = true;
    		}
    	}
        
        new LoadMainPic().execute();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {

		AlertDialog dialogDetails = null;

		switch (id) {
		case DIALOG_LOGIN:
			LayoutInflater inflater = LayoutInflater.from(this);
			View dialogview = inflater.inflate(R.layout.dialog_login, null);

			AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
			dialogbuilder.setTitle("가입하기");
			dialogbuilder.setView(dialogview);
			dialogDetails = dialogbuilder.create();

			break;
		}

		return dialogDetails;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {

		switch (id) {
		case DIALOG_LOGIN:
			final AlertDialog alertDialog = (AlertDialog) dialog;
			Button loginbutton = (Button) alertDialog
					.findViewById(R.id.btn_login);
			Button cancelbutton = (Button) alertDialog
					.findViewById(R.id.btn_cancel);
			userName = (EditText) alertDialog
					.findViewById(R.id.txt_name);

			loginbutton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
					new insertUserID().execute();

					user_id = userName.getText().toString();
					
					Toast.makeText(getApplicationContext(), user_id + " 님 환영합니다", Toast.LENGTH_LONG).show();

					button1.setVisibility(View.VISIBLE);
					onload();
				}
			});

			cancelbutton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
					finish();
				}
			});
			break;
		}
	}
	
	
	// Get Json and Parsing Json Data RHG
	private class insertUserID extends AsyncTask<Void, Void, String> {
		protected String getASCIIContentFromEntity(HttpEntity entity)
				throws IllegalStateException, IOException {
			InputStream in = entity.getContent();

			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n > 0) {
				byte[] b = new byte[4096];
				n = in.read(b);

				if (n > 0)
					out.append(new String(b, 0, n));
			}

			return out.toString();
		}

		@Override
		protected String doInBackground(Void... params) {

			String text = null;
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://180.68.48.157:9999/insertUser");
				String Json = null;
				
				JSONObject jsonObject = new JSONObject();	
				jsonObject.accumulate("user_id", userName.getText().toString());
				jsonObject.accumulate("dev_id", DevID);
				jsonObject.accumulate("tel_no", PhoneNumber);
				
				Json = jsonObject.toString();				
				StringEntity se = new StringEntity(Json, HTTP.UTF_8);
				
				httpPost.setEntity(se);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");
				
				HttpResponse response = httpClient.execute(httpPost);

				HttpEntity entity = response.getEntity();

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}

			return text;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Do anything with response..
		}

	}
    
	
	// Get Json and Parsing Json Data RHG
	private class checkUser extends AsyncTask<Void, Void, String> {
		protected String getASCIIContentFromEntity(HttpEntity entity)
				throws IllegalStateException, IOException {
			InputStream in = entity.getContent();

			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n > 0) {
				byte[] b = new byte[4096];
				n = in.read(b);

				if (n > 0)
					out.append(new String(b, 0, n));
			}

			return out.toString();
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			//HttpGet httpGet = new HttpGet("http://192.168.0.12:3303/listscomments/"+listID);
			HttpGet httpGet = new HttpGet("http://180.68.48.157:9999/checkUser/"+DevID);
			String text = null;
			try {
				HttpResponse response = httpClient.execute(httpGet, localContext);

				HttpEntity entity = response.getEntity();

				text = getASCIIContentFromEntity(entity);

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}

			return text;
		}

		protected void onPostExecute(String results) {

			if (results != null) {

				//JSONObject jsonResult;
				JSONArray jArr;
				try{
					jArr = new JSONArray(results);
					
					if (jArr.length() == 0){
						button1.setVisibility(View.GONE);
						showDialog(DIALOG_LOGIN);
					} else {
						for(int i=0; i<jArr.length(); i++){	
							JSONObject items = jArr.getJSONObject(i);
							String itemList = items.getString("dev_id")+ " : " + items.getString("tel_no") +
									 " : " + items.getString("user_id") + " : " + results.length();
							

				    		Log.d(TAG, "This is : " + itemList);

							dev_id = items.getString("dev_id");
							tel_no = items.getString("tel_no");
							user_id = items.getString("user_id");
						}
						
						Toast.makeText(getApplicationContext(), user_id + " 님 환영합니다", Toast.LENGTH_LONG).show();
						onload();
					}
					
				}catch(JSONException e){
					Toast.makeText(getApplicationContext(), e.getStackTrace().toString(), Toast.LENGTH_LONG).show();
				}

			}
		}
	} 

    
	
	// Get Json and Parsing Json Data RHG
	private class mkUserdir extends AsyncTask<Void, Void, String> {
		protected String getASCIIContentFromEntity(HttpEntity entity)
				throws IllegalStateException, IOException {
			InputStream in = entity.getContent();

			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n > 0) {
				byte[] b = new byte[4096];
				n = in.read(b);

				if (n > 0)
					out.append(new String(b, 0, n));
			}

			return out.toString();
		}

		@Override
		protected String doInBackground(Void... params) {

			String text = null;
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://180.68.48.157:9999/mkUserdir");
				String Json = null;
				
				JSONObject jsonObject = new JSONObject();	
				jsonObject.accumulate("c_user", user_id);
				
				Json = jsonObject.toString();				
				StringEntity se = new StringEntity(Json, HTTP.UTF_8);
				
				httpPost.setEntity(se);
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json");
				
				HttpResponse response = httpClient.execute(httpPost);

				HttpEntity entity = response.getEntity();

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}

			return text;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Do anything with response..
		}

	}
    
	
	// Get Json and Parsing Json Data RHG
	private class LoadMainPic extends AsyncTask<Void, Void, String> {
		protected String getASCIIContentFromEntity(HttpEntity entity)
				throws IllegalStateException, IOException {
			InputStream in = entity.getContent();

			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n > 0) {
				byte[] b = new byte[4096];
				n = in.read(b);

				if (n > 0)
					out.append(new String(b, 0, n));
			}

			return out.toString();
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			//HttpGet httpGet = new HttpGet("http://192.168.0.12:3303/lists");
			HttpGet httpGet = new HttpGet("http://180.68.48.157:9999/listMain");
			String text = null;
			try {
				HttpResponse response = httpClient.execute(httpGet, localContext);

				HttpEntity entity = response.getEntity();

				text = getASCIIContentFromEntity(entity);

			} catch (Exception e) {
				return e.getLocalizedMessage();
			}

			return text;
		}

		protected void onPostExecute(String results) {
			if (results != null) {
				
				//JSONObject jsonResult;
				JSONArray jArr;
				try{
					//jsonResult = new JSONObject(results);
					jArr = new JSONArray(results);
					
					for(int i=0; i<jArr.length(); i++){	
						JSONObject items = jArr.getJSONObject(i);
						String itemList = items.getString("c_id")+ " : " + items.getString("c_user")+ " : " + items.getString("c_title") +
								 " : " + items.getString("c_date") +  " : " + items.getString("c_img");
						
						String memoId="";
						String titleStr="";
						String userStr="";
						String dateStr="";
						String photoUriStr="";
						String cntLike="";
						String cntView="";
						
						
						int rep = Integer.parseInt(items.getString("c_reply"));
						titleStr = items.getString("c_title");
						memoId = items.getString("c_id");
						userStr = items.getString("c_user");
						dateStr = items.getString("c_date");
						cntLike = "Like "+items.getString("c_like");
						cntView = "View "+items.getString("c_cnt");
						photoUriStr = "http://180.68.48.157:9999/img/" + userStr + "/" +items.getString("c_img");

						addMain(i, photoUriStr, userStr);
					}
					
				}catch(JSONException e){
					Toast.makeText(getApplicationContext(), e.getStackTrace().toString(), Toast.LENGTH_LONG).show();
				}

			}
		}
	}
	
	private void addMain(int index, String uri, String title){
		if (index == 0){
			ImageLoader.getInstance().displayImage(uri, bestPic, this.options);
		} else if (index == 1){
			ImageLoader.getInstance().displayImage(uri, currentPic01, this.options);
		} else if (index == 2){
			ImageLoader.getInstance().displayImage(uri, currentPic02, this.options);
		} else if (index == 3){
			ImageLoader.getInstance().displayImage(uri, currentPic03, this.options);
		} else if (index == 4){
			ImageLoader.getInstance().displayImage(uri, currentPic011, this.options);
		} else if (index == 5){
			ImageLoader.getInstance().displayImage(uri, currentPic021, this.options);
		} else if (index == 6){
			ImageLoader.getInstance().displayImage(uri, currentPic031, this.options);
		}
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}