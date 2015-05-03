package com.terang.mypic;

import java.io.File;
import java.util.Date;

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
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MyPictureActivity extends Activity {

	public static final String TAG = "My Picture Activity";
	public static ImageLoaderConfiguration config;
	public static DisplayImageOptions options;

	/**
	 * 데이터베이스 인스턴스
	 */
	public static DrawDatabase mDatabase = null;

	private ListView mListView = null;
	private MyPicAdapter mAdapter = null;
	TextView itemCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mypic_activity);
		
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
        
        mListView = (ListView)findViewById(R.id.viewMyPic);
        mAdapter = new MyPicAdapter(this);
        
        mListView.setAdapter(mAdapter);
        
        mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	    		Log.d(TAG, "Click List Items.");
				viewMemo(position);
			}
		});
	}


    /**
     * 메모 리스트 데이터 로딩
     */
    public int loadMemoListData() {
     	String SQL = "select _id, CREATE_DATE, TITLE, URI from HANDWRITING order by CREATE_DATE desc";

     	int recordCount = -1;
     	if (mDatabase != null) {
	   		Cursor outCursor = mDatabase.rawQuery(SQL);

	   		recordCount = outCursor.getCount();
			Log.d(TAG, "cursor count : " + recordCount + "\n");

			mAdapter.clear();

			for (int i = 0; i < recordCount; i++) {
				outCursor.moveToNext();

				String memoId = outCursor.getString(0);

				String dateStr = outCursor.getString(1);
				if (dateStr != null && dateStr.length() > 10) {
					//dateStr = dateStr.substring(0, 10);
					try {
						Date inDate = BasicInfo.dateFormat.parse(dateStr);

						if (BasicInfo.language.equals("ko")) {
							dateStr = BasicInfo.dateNameFormat2.format(inDate);
						} else {
							dateStr = BasicInfo.dateNameFormat3.format(inDate);
						}
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				} else {
					dateStr = "";
				}

				String memoStr = outCursor.getString(2);
				String photoUriStr = outCursor.getString(3);


				mAdapter.addItem(new MyPicListItem(memoId, dateStr, memoStr, photoUriStr));
			}

			outCursor.close();

			mAdapter.notifyDataSetChanged();

			//itemCount.setText(recordCount + " " + getResources().getString(R.string.item_count));
			//itemCount.invalidate();
     	}

	   return recordCount;
    }


    private void viewMemo(int position) {
    	MyPicListItem item = (MyPicListItem)mAdapter.getItem(position);

    	// 메모 보기 액티비티 띄우기
		Intent intent = new Intent(getApplicationContext(), MyPicItemView.class);
		intent.putExtra(BasicInfo.KEY_MEMO_ID, item.getId());
		intent.putExtra(BasicInfo.KEY_MEMO_DATE, item.getData(0));
		intent.putExtra(BasicInfo.KEY_MEMO_TEXT, item.getData(1));
		intent.putExtra(BasicInfo.KEY_URI_HANDWRITING, item.getData(2));

		startActivityForResult(intent, BasicInfo.REQ_INSERT_ACTIVITY);
    }

    /**
     * 다른 액티비티의 응답 처리
     */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode) {
		case BasicInfo.REQ_INSERT_ACTIVITY:
			if(resultCode == BasicInfo.REP_DELETE_ACTIVITY) {
				Toast.makeText(this, "Delete Paint Succesfully", Toast.LENGTH_LONG).show();
				loadMemoListData();
			} else if(resultCode == BasicInfo.REP_INSERT_ACTIVITY) {
				Toast.makeText(this, "Save Paint to Board Succesfully", Toast.LENGTH_LONG).show();
				loadMemoListData();
			}

			break;

			case BasicInfo.REQ_VIEW_ACTIVITY:
				loadMemoListData();

				break;

		}
	}

	protected void onStart() {
        // 데이터베이스 열기
        openDatabase();
        
        loadMemoListData();

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
    
   @Override
   public void onPause(){
       super.onPause();
       Log.i(TAG, "onPause()");
   }
    
   @Override
   public void onStop(){
       super.onStop();
       Log.i(TAG, "onStop()");
   }
    
   @Override
   public void onResume(){
       super.onResume();
       Log.i(TAG, "onResume()");
   }
    
   @Override
   public void onRestart(){
       super.onRestart();
       Log.i(TAG, "onRestart()");
   }
    
   @Override
   public void onDestroy(){
       super.onDestroy();
       Log.i(TAG, "onDestroy()");
   }
	
	
}
