package com.terang.mypic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.terang.common.BasicInfo;
import com.terang.common.DrawDatabase;
import com.terang.paintboard.R;
import com.terang.paintboard.sharedPref;
import com.terang.paintboard.R.drawable;
import com.terang.paintboard.R.id;
import com.terang.paintboard.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyPicItemView extends Activity {

	public static final String TAG = "View Picture Item";

	/**
	 * 데이터베이스 인스턴스
	 */
	public static DrawDatabase mDatabase = null;
	
	String listID;
	String listDate;
	String listTitle;
	String listURI;
	String user_id;
	sharedPref pref;
	
	public static ImageLoaderConfiguration config;
	public static DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mypic_item_view);
        pref = new sharedPref(this);
        user_id = pref.getValue(sharedPref.PREF_USER_ID, "");
		
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
		
		Intent intent = getIntent();
		listID = intent.getStringExtra(BasicInfo.KEY_MEMO_ID);
		listDate = "등록일 : " + intent.getStringExtra(BasicInfo.KEY_MEMO_DATE);
		listTitle = intent.getStringExtra(BasicInfo.KEY_MEMO_TEXT);
		listURI = intent.getStringExtra(BasicInfo.KEY_URI_HANDWRITING);
		
		TextView vTitle = (TextView)findViewById(R.id.vTitle);
		ImageView mImage = (ImageView)findViewById(R.id.mImage);
		TextView vDate = (TextView)findViewById(R.id.vDate);
		Ion.getDefault(this).configure().setLogging("ion-teset", Log.DEBUG);

		vTitle.setText(listTitle);
		//mImage.setImageURI(Uri.parse(BasicInfo.FOLDER_HANDWRITING + listURI));
		ImageLoader.getInstance().displayImage("file://"+BasicInfo.FOLDER_HANDWRITING + listURI, mImage, options);
		vDate.setText(listDate);
		
		Button saveBoard = (Button)findViewById(R.id.saveBoard);
		saveBoard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveBoardPic();
				new LongRunningGetIO().execute();
				setResult(RESULT_OK);
				finish();
			}
		});
		
		Button saveSns = (Button)findViewById(R.id.saveSns);
		saveSns.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Button delete = (Button)findViewById(R.id.deletePic);
		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	        	File file = new File(BasicInfo.FOLDER_HANDWRITING + listURI);
	        	if(file.exists()) {
	        		file.delete();
	        	}
	        	
	        	deletePic();
				setResult(RESULT_OK);
				finish();
			}
		});
	}


    private void deletePic() {
    	
		try {
			// INSERT HANDWRITING INFO
			String SQL = "delete from " + DrawDatabase.TABLE_HANDWRITING
					+ " where _id = '" + listID + "'"
					+ " and URI = '" + listURI + "'";
			if (mDatabase != null) {
				mDatabase.execSQL(SQL);
			}

		} catch (Exception ex) {
			Log.d(TAG, "Exception in delete db data : " + ex.toString());
		}
    }


    private void saveBoardPic() {
    	
		try {
			File file = new File(BasicInfo.FOLDER_HANDWRITING + listURI);
			Future uploading = Ion.with(this)
					//.load("http://192.168.0.12:3303/upload")
					.load("http://180.68.37.108:9999/upload")
					.setMultipartParameter("c_user", user_id)
					.setMultipartFile("image", file)
					.asString()
					.withResponse()
					.setCallback(new FutureCallback<Response<String>>() {
						
						@Override
						public void onCompleted(Exception e, Response<String> result) {
							try{
								JSONObject obj = new JSONObject(result.getResult());
								Toast.makeText(getApplicationContext(), "(" + listTitle + ")이미지를 게시판에 저장 하였습니다", 
										Toast.LENGTH_SHORT).show();
								//Log.d(TAG, "Insert Image Result : " + obj.getString("response"));
							}catch(JSONException ex){
								ex.printStackTrace();
							}
						}
					});
		} catch (Exception ex) {
			Log.d(TAG, "Exception in delete db data : " + ex.toString());
		}
    }
    


	
	
	// Get Json and Parsing Json Data RHG
	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
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
				HttpPost httpPost = new HttpPost("http://180.68.37.108:9999/uploaddb");
				String Json = null;
				
				JSONObject jsonObject = new JSONObject();	
				jsonObject.accumulate("title", listTitle);
				jsonObject.accumulate("uri", listURI);
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

	protected void onStart() {

        // 데이터베이스 열기
        openDatabase();

		super.onStart();
	}

	/**
     * 데이터베이스 열기 (데이터베이스가 없을 때는 만들기)
     */
    public void openDatabase() {
		// open database
    	if (mDatabase != null) {
    		mDatabase.close();
    		mDatabase = null;
    	}

    	mDatabase = DrawDatabase.getInstance(this);
    	boolean isOpen = mDatabase.open();
    	if (isOpen) {
    		Log.d(TAG, "Draw database is open.");
    	} else {
    		Log.d(TAG, "Draw database is not open.");
    	}
    }
}
