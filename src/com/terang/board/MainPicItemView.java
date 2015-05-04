package com.terang.board;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.terang.paintboard.R.drawable;
import com.terang.paintboard.R.id;
import com.terang.paintboard.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainPicItemView extends Activity {

	public static final String TAG = "View Main Picture Item";

	/**
	 * 데이터베이스 인스턴스
	 */
	public static DrawDatabase mDatabase = null;
	String url="";
	
	String listID;
	String listDate;
	String listTitle;
	String listUser;
	String listURI;
	EditText commentEdit;
	
	public static ImageLoaderConfiguration config;
	public static DisplayImageOptions options;

	private ListView mListView = null;
	private MainPicCommentdapter mAdapter = null;
	private InputMethodManager imm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpic_item_view);
		
		imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		
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
		//listTitle = BasicInfo.dateNameFormat2.format(intent.getStringExtra(BasicInfo.KEY_MEMO_TEXT));
		listTitle = intent.getStringExtra(BasicInfo.KEY_MEMO_TEXT);
		listUser = "User : " + intent.getStringExtra(BasicInfo.KEY_MEMO_USER);
		listURI = intent.getStringExtra(BasicInfo.KEY_URI_HANDWRITING);
		
		TextView vTitle = (TextView)findViewById(R.id.bTitle);
		ImageView mImage = (ImageView)findViewById(R.id.bImage);
		TextView vDate = (TextView)findViewById(R.id.bDate);
		TextView vUser = (TextView)findViewById(R.id.bUser);
		Ion.getDefault(this).configure().setLogging("ion-teset", Log.DEBUG);

		vTitle.setText(listTitle);
		//mImage.setImageURI(Uri.parse(BasicInfo.FOLDER_HANDWRITING + listURI));
		ImageLoader.getInstance().displayImage(listURI, mImage, options);
		vUser.setText(listUser);
		vDate.setText(listDate);

        mListView = (ListView)findViewById(R.id.commentList);
        
        loadComment();
        
		commentEdit = (EditText)findViewById(R.id.editComment);
		
		Button saveComment = (Button)findViewById(R.id.saveComment);
		saveComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(commentEdit.getText().toString().compareTo("")==0){
					Toast.makeText(getApplicationContext(), "Comment 를 입력하세요!!", Toast.LENGTH_LONG).show();
					commentEdit.setHint("Comment 를 입력하세요!");
				} else {
					//url = "http://192.168.0.12:3303/instcomment";
					url = "http://180.68.48.157:9999/instcomment";
					new insertComment().execute();

					mAdapter.addItem(new MainPicCommentListItem("0", commentEdit.getText().toString(), null));
					mAdapter.notifyDataSetChanged();
					commentEdit.setText(null);
					commentEdit.setHint("Comment 를 입력하세요!");
					hideKeyboard();
				}
			}
		});
		
		Button checkLike = (Button)findViewById(R.id.checkLike);
		checkLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//url = "http://192.168.0.12:3303/contentsLike";
				url = "http://180.68.48.157:9999/contentsLike";
				new updateState().execute();
				Toast.makeText(getApplicationContext(), "이 글이 좋아요!!", Toast.LENGTH_LONG).show();
			}
		});
		
		Button delete = (Button)findViewById(R.id.deleteBItem);
		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//url = "http://192.168.0.12:3303/contentDelete";
				url = "http://180.68.48.157:9999/contentDelete";
				new updateState().execute();
				Toast.makeText(getApplicationContext(), listTitle+"를 삭제하였습니다!!", Toast.LENGTH_LONG).show();
				finish();
			}
		});
		
	}
	
	private void loadComment(){
		//mAdapter.clear();

		new GetComments().execute();
	}
    
	
	// Get Json and Parsing Json Data RHG
	private class GetComments extends AsyncTask<Void, Void, String> {
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
			HttpGet httpGet = new HttpGet("http://180.68.48.157:9999/listscomments/"+listID);
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
		        mAdapter = new MainPicCommentdapter(getApplicationContext());
		        
		        mListView.setAdapter(mAdapter);

				//JSONObject jsonResult;
				JSONArray jArr;
				try{
					//jsonResult = new JSONObject(results);
					jArr = new JSONArray(results);
					
					for(int i=0; i<jArr.length(); i++){	
						JSONObject items = jArr.getJSONObject(i);
						String itemList = items.getString("c_id")+ " : " + items.getString("c_re_user") +
								 " : " + items.getString("c_re") +  " : " + items.getString("c_re_dt");
						

			    		Log.d(TAG, "This is : " + itemList);
						
						String memoId="";
						String commentStr="";
						String commentDt="";

						memoId = items.getString("c_id");
						commentStr = items.getString("c_re");
						commentDt = items.getString("c_re_dt");

			        	mAdapter.addItem(new MainPicCommentListItem(memoId, commentStr, commentDt));
					}
					
				}catch(JSONException e){
					Toast.makeText(getApplicationContext(), e.getStackTrace().toString(), Toast.LENGTH_LONG).show();
				}

			}
		}
	}
	
	
	// Get Json and Parsing Json Data RHG
	private class insertComment extends AsyncTask<Void, Void, String> {
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
				jsonObject.accumulate("c_re_user", "ADMIN");
				jsonObject.accumulate("c_re", commentEdit.getText().toString());
				
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
	private class updateState extends AsyncTask<Void, Void, String> {
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
				StringEntity se = new StringEntity(Json);
				
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
	
	
	/**
	 * 키보드를 가림
	 */
	
	private void hideKeyboard(){
		imm.hideSoftInputFromWindow(commentEdit.getWindowToken(), 0);
	}
	
}
