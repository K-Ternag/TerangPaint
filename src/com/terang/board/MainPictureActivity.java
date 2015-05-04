package com.terang.board;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.terang.common.BasicInfo;
import com.terang.paintboard.R;
import com.terang.paintboard.R.drawable;
import com.terang.paintboard.R.id;
import com.terang.paintboard.R.layout;

public class MainPictureActivity extends Activity {

	public static final String TAG = "Board View Activity";
	public static ImageLoaderConfiguration config;
	public static DisplayImageOptions options;

	/**
	 * 페이지가 열려 있는지 알기 위한 플래그
	 */
	boolean isPageOpen = false;

	private ListView mListView = null;
	private MainPicdapter mAdapter = null;
	String listID;
	String url="";
	String spinnerTP = "전체보기";
	
	/**
	 * Object 들 선언
	 */
	Spinner boardSpinner;
	EditText boardEdit;
	Button boardSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpic_activity);
		
		setSpinner();
		boardEdit = (EditText)findViewById(R.id.boardEdit);
		boardSearch = (Button)findViewById(R.id.boardButton);
		
		boardSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(spinnerTP.compareTo("전체보기") == 0){
					new LongRunningGetIO().execute();
				} else{
					if(boardEdit.getText().toString().compareTo("")==0){
						Toast.makeText(getApplicationContext(), "한 글자 이상 입력하세요!!", Toast.LENGTH_LONG).show();
					} else {
						if(spinnerTP.compareTo("글쓴이") == 0){
							new ListByID().execute();						
						} else if(spinnerTP.compareTo("글 제목") == 0){
							new ListByTitle().execute();						
						} else {
							Toast.makeText(getApplicationContext(), "TYPE 설정이 잘못 되었습니다. [" + spinnerTP + "]", Toast.LENGTH_LONG).show();
						}
					}
				}
			}
		});
		
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

        mListView = (ListView)findViewById(R.id.viewList);
        
        mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	    		Log.d(TAG, "Click List Items.");
	    		//url = "http://192.168.0.12:3303/viewlist";
	    		url = "http://180.68.48.157:9999/viewlist";
				viewMemo(position);
			}
		});
        
	}
	

	protected void onStart() {
	   new LongRunningGetIO().execute();
       super.onStart();
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
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			//HttpGet httpGet = new HttpGet("http://192.168.0.12:3303/lists");
			HttpGet httpGet = new HttpGet("http://180.68.48.157:9999/lists");
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
		        mAdapter = new MainPicdapter(getApplicationContext());
		        
		        mListView.setAdapter(mAdapter);

				//mAdapter.clear();
				
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
						String cntReply="";
						
						titleStr = items.getString("c_title");
						memoId = items.getString("c_id");
						userStr = items.getString("c_user");
						dateStr = items.getString("c_date");
						cntLike = items.getString("c_like");
						cntView = items.getString("c_cnt");
						cntReply = items.getString("c_reply");
						photoUriStr = "http://180.68.48.157:9999/img/" + userStr + "/" +items.getString("c_img");

						mAdapter.addItem(new MainPicListItem(memoId, titleStr, userStr, dateStr, photoUriStr, cntLike, cntView, cntReply));
					}
					
				}catch(JSONException e){
					Toast.makeText(getApplicationContext(), e.getStackTrace().toString(), Toast.LENGTH_LONG).show();
				}

			}
		}
	}

    
	
	// Get Json and Parsing Json Data RHG
	private class ListByID extends AsyncTask<Void, Void, String> {
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
			HttpGet httpGet = new HttpGet("http://180.68.48.157:9999/listUser/"+boardEdit.getText().toString());
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
		        mAdapter = new MainPicdapter(getApplicationContext());
		        
		        mListView.setAdapter(mAdapter);

				//mAdapter.clear();
				
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
						String cntReply="";
						
						titleStr = items.getString("c_title");
						memoId = items.getString("c_id");
						userStr = items.getString("c_user");
						dateStr = items.getString("c_date");
						cntLike = items.getString("c_like");
						cntView = items.getString("c_cnt");
						cntReply = items.getString("c_reply");
						photoUriStr = "http://180.68.48.157:9999/img/" + userStr + "/" +items.getString("c_img");

						mAdapter.addItem(new MainPicListItem(memoId, titleStr, userStr, dateStr, photoUriStr, cntLike, cntView, cntReply));
					}
					
				}catch(JSONException e){
					Toast.makeText(getApplicationContext(), e.getStackTrace().toString(), Toast.LENGTH_LONG).show();
				}

			}
		}
	}

    
	
	// Get Json and Parsing Json Data RHG
	private class ListByTitle extends AsyncTask<Void, Void, String> {
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
			HttpGet httpGet = new HttpGet("http://180.68.48.157:9999/listTitle/"+boardEdit.getText().toString());
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
		        mAdapter = new MainPicdapter(getApplicationContext());
		        
		        mListView.setAdapter(mAdapter);

				//mAdapter.clear();
				
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
						String cntReply="";
						
						titleStr = items.getString("c_title");
						memoId = items.getString("c_id");
						userStr = items.getString("c_user");
						dateStr = items.getString("c_date");
						cntLike = items.getString("c_like");
						cntView = items.getString("c_cnt");
						cntReply = items.getString("c_reply");
						photoUriStr = "http://180.68.48.157:9999/img/" + userStr + "/" +items.getString("c_img");

						mAdapter.addItem(new MainPicListItem(memoId, titleStr, userStr, dateStr, photoUriStr, cntLike, cntView, cntReply));
					}
					
				}catch(JSONException e){
					Toast.makeText(getApplicationContext(), e.getStackTrace().toString(), Toast.LENGTH_LONG).show();
				}

			}
		}
	}
    


	
	
	// Get Json and Parsing Json Data RHG
	private class updateCount extends AsyncTask<Void, Void, String> {
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
				HttpPost httpPost = new HttpPost(url);
				String Json = null;
				
				JSONObject jsonObject = new JSONObject();	
				jsonObject.accumulate("c_id", listID);
				
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


    private void viewMemo(int position) {
    	MainPicListItem item = (MainPicListItem)mAdapter.getItem(position);

    	// 메모 보기 액티비티 띄우기
		Intent intent = new Intent(getApplicationContext(), MainPicItemView.class);
		intent.putExtra(BasicInfo.KEY_MEMO_ID, item.getId());
		intent.putExtra(BasicInfo.KEY_MEMO_TEXT, item.getData(0));
		intent.putExtra(BasicInfo.KEY_MEMO_USER, item.getData(1));
		intent.putExtra(BasicInfo.KEY_MEMO_DATE, item.getData(2));
		intent.putExtra(BasicInfo.KEY_URI_HANDWRITING, item.getData(3));
		listID = item.getId();
		new updateCount().execute();

		startActivityForResult(intent, BasicInfo.REQ_INSERT_ACTIVITY);
    }
    
    private void setSpinner() {
		boardSpinner = (Spinner) findViewById(R.id.boardSpinner);
		ArrayAdapter<CharSequence> spinnerdapter = ArrayAdapter.createFromResource(MainPictureActivity.this, R.array.boardTPentry,R.layout.mainpic_spinner);
		spinnerdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		boardSpinner.setAdapter(spinnerdapter);
		boardSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent,View view, int pos, long id) {
				if(parent.getItemAtPosition(pos).toString().compareTo("전체보기") ==0){
					spinnerTP = parent.getItemAtPosition(pos).toString();
					boardEdit.setText(null);
				} else {
					spinnerTP = parent.getItemAtPosition(pos).toString();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

}