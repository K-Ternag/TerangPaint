package com.terang.paintboard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.terang.common.BasicInfo;
import com.terang.common.DrawDatabase;
import com.terang.paintboard.R;
import com.terang.paintboard.R.id;
import com.terang.paintboard.R.layout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 손글씨 입력 액티비티
 *
 * @author Mike
 * @date 2011-07-01
 */
public class HandwritingMakingActivity extends Activity {

	public static final String TAG = "HandwritingMakingActivity";
	Calendar mCalendar = Calendar.getInstance();

	HandwritingView mWritingBoard;
	TitleBitmapButton mColorBtn;
	TitleBitmapButton mPenBtn;
	TitleBitmapButton mEraserBtn;
	TitleBitmapButton mUndoBtn;

	LinearLayout mAddedLayout;
	TitleBitmapButton mColorLegendBtn;
	TextView mSizeLegendTxt;

	int mColor = 0xff000000;
	int mSize = 8;
	int mOldColor;
	int mOldSize;
	boolean mEraserSelected = false;
	String mDateStr;

	TitleBitmapButton mHandwritingMakingSaveBtn;
	
	Bitmap resultHandwritingBitmap;

	private Dialog mDialog = null;

	/**
	 * 데이터베이스 인스턴스
	 */
	public static DrawDatabase mDatabase = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.handwriting_making_activity);
		

		setTopLayout();

		setBottomLayout();

		setWritingBorad();
	    Log.i(TAG, "onCreate()");

    }

	public void setWritingBorad() {
		LinearLayout boardLayout = (LinearLayout) findViewById(R.id.boardLayout);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.FILL_PARENT);

        mWritingBoard = new HandwritingView(this);
        mWritingBoard.setLayoutParams(params);
        mWritingBoard.setPadding(2, 2, 2, 2);

        boardLayout.addView(mWritingBoard);
	}

	public void setTopLayout() {
		LinearLayout toolsLayout = (LinearLayout) findViewById(R.id.toolsLayout);

		mColorBtn = (TitleBitmapButton) findViewById(R.id.colorBtn);
		mPenBtn = (TitleBitmapButton) findViewById(R.id.penBtn);
		mEraserBtn = (TitleBitmapButton) findViewById(R.id.eraserBtn);
		mUndoBtn = (TitleBitmapButton) findViewById(R.id.undoBtn);

        mColorBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {

        		ColorPaletteDialog.mSelectedListener = new OnColorSelectedListener() {
        			public void onColorSelected(int color) {
        				mColor = color;
        				mWritingBoard.updatePaintProperty(mColor, mSize);
        				displayPaintProperty();
        			}
        		};


        		// show color palette dialog
        		Intent intent = new Intent(getApplicationContext(), ColorPaletteDialog.class);
        		startActivity(intent);

        	}
        });

        mPenBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {

        		PenPaletteDialog.mSelectedListener = new OnPenSelectedListener() {
        			public void onPenSelected(int size) {
        				mSize = size;
        				mWritingBoard.updatePaintProperty(mColor, mSize);
        				displayPaintProperty();
        			}
        		};


        		// show pen palette dialog
        		Intent intent = new Intent(getApplicationContext(), PenPaletteDialog.class);
        		intent.putExtra("size", ""+mSize);
        		startActivityForResult(intent, 0);

        	}
        });

        mEraserBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		
        		ErasePaletteDialog.mSelectedListener = new OnPenSelectedListener() {
        			public void onPenSelected(int size) {
        				mSize = size;
        				mWritingBoard.updatePaintProperty(Color.WHITE, mSize);
        				displayPaintProperty();
        			}
        		};


        		// show pen palette dialog
        		Intent intent = new Intent(getApplicationContext(), ErasePaletteDialog.class);
        		startActivity(intent);

        		/*mEraserSelected = !mEraserSelected;

        		if (mEraserSelected) {
        			mColorBtn.setEnabled(false);
        			mPenBtn.setEnabled(false);
        			mUndoBtn.setEnabled(false);

                    mColorBtn.invalidate();
                    mPenBtn.invalidate();
                    mUndoBtn.invalidate();

                    mOldColor = mColor;
                    mOldSize = mSize;

                    mColor = Color.WHITE;
                    mSize = 30;

                    mWritingBoard.updatePaintProperty(mColor, mSize);
                    displayPaintProperty();

                } else {
                	mColorBtn.setEnabled(true);
                	mPenBtn.setEnabled(true);
                	mUndoBtn.setEnabled(true);

                    mColorBtn.invalidate();
                    mPenBtn.invalidate();
                    mUndoBtn.invalidate();

                    mColor = mOldColor;
                    mSize = mOldSize;

                    mWritingBoard.updatePaintProperty(mColor, mSize);
                    displayPaintProperty();

                }*/

        	}
        });

        mUndoBtn.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		Log.d(TAG, "undo button clicked.");

        		mWritingBoard.undo();
        	}
        });

        // add legend buttons
        LinearLayout.LayoutParams addedParams = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.FILL_PARENT);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT);
        mAddedLayout = new LinearLayout(this);
        mAddedLayout.setLayoutParams(addedParams);
        mAddedLayout.setOrientation(LinearLayout.VERTICAL);
        mAddedLayout.setPadding(8,8,8,8);

        LinearLayout outlineLayout = new LinearLayout(this);
        outlineLayout.setLayoutParams(buttonParams);
        outlineLayout.setOrientation(LinearLayout.VERTICAL);
        outlineLayout.setBackgroundColor(Color.LTGRAY);
        outlineLayout.setPadding(1,1,1,1);

        mColorLegendBtn = new TitleBitmapButton(this);
        mColorLegendBtn.setClickable(false);
        mColorLegendBtn.setLayoutParams(buttonParams);
        mColorLegendBtn.setText(" ");
        mColorLegendBtn.setBackgroundColor(mColor);
        mColorLegendBtn.setHeight(20);
        outlineLayout.addView(mColorLegendBtn);
        mAddedLayout.addView(outlineLayout);

        mSizeLegendTxt = new TextView(this);
        mSizeLegendTxt.setLayoutParams(buttonParams);
        mSizeLegendTxt.setText("Size : " + mSize);
        mSizeLegendTxt.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        mSizeLegendTxt.setTextSize(16);
        mSizeLegendTxt.setTextColor(Color.BLACK);
        mAddedLayout.addView(mSizeLegendTxt);

        toolsLayout.addView(mAddedLayout);
	}

	public void setBottomLayout()
	{
		mHandwritingMakingSaveBtn = (TitleBitmapButton)findViewById(R.id.saveBtn);

		mHandwritingMakingSaveBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				createDialog();
				
				Button btnyes = (Button) mDialog.findViewById(R.id.btn_yes);
				btnyes.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d(TAG, "Click Yes Button!!");						
						EditText editText = (EditText) mDialog.findViewById(R.id.txt_title);
						String Title = editText.getText().toString();
						saveHandwritingMaking(Title);
						dismissDialog();
					}
				});
				
				Button btnno = (Button) mDialog.findViewById(R.id.btn_no);
				btnno.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d(TAG, "Click No Button!!");
						dismissDialog();
					}
				});
			}
		});
	}

    public int getChosenColor() {
    	return mColor;
    }

    public int getPenThickness() {
    	return mSize;
    }

    private void displayPaintProperty() {
    	mColorLegendBtn.setBackgroundColor(mColor);
    	mSizeLegendTxt.setText("Size : " + mSize);

    	mAddedLayout.invalidate();
    }
    
    public void saveHandwritingMaking(String Title) {

    	try {
    		checkHandwritingFolder();

        	String handwritingName = "made";

        	File file = new File(BasicInfo.FOLDER_HANDWRITING + handwritingName);
        	if(file.exists()) {
        		file.delete();
        	}

			FileOutputStream outstream = new FileOutputStream(BasicInfo.FOLDER_HANDWRITING + handwritingName);

			Bitmap mBitmap = mWritingBoard.getImage();
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream);
			outstream.close();
			resultHandwritingBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_HANDWRITING + "made");
			
			insertHandwriting(Title);
			mWritingBoard.clearUndo();

		} catch (Exception e) {
			e.printStackTrace();
		}

		setResult(RESULT_OK);
		finish();
	}
	
	private void createDialog() {
		final View innerView = getLayoutInflater().inflate(R.layout.dialog, null);
		
		mDialog = new Dialog(this);
		mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDialog.setContentView(innerView);
		
		// Back키 눌렀을 경우 Dialog Cancle 여부 설정
		mDialog.setCancelable(true);
		
		// Dialog 밖을 터치 했을 경우 Dialog 사라지게 하기
		mDialog.setCanceledOnTouchOutside(true);
		
		mDialog.show();
	}
	
	private void dismissDialog() {
		if(mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

    public void checkHandwritingFolder() {
    	File handwritingFolder = new File(BasicInfo.FOLDER_HANDWRITING);
		if(!handwritingFolder.isDirectory()){
			Log.d(TAG, "creating handwriting folder : " + handwritingFolder);

			handwritingFolder.mkdirs();
		}
    }

    private void insertHandwriting(String Title) {
       	String handwritingName = null;
       	
	    	try {
	    		
	    		File handwritingFolder = new File(BasicInfo.FOLDER_HANDWRITING);

				//폴더가 없다면 폴더를 생성한다.
				if(!handwritingFolder.isDirectory()){
					Log.d(TAG, "creating handwriting folder : " + handwritingFolder);
					handwritingFolder.mkdirs();
				}

				// Temporal Hash for handwriting file name

				handwritingName = createFilename();

				FileOutputStream outstream = new FileOutputStream(BasicInfo.FOLDER_HANDWRITING + handwritingName);
				resultHandwritingBitmap.compress(CompressFormat.PNG, 100, outstream);
				outstream.close();


				if (handwritingName != null) {

			    	// INSERT HANDWRITING INFO
			    	String SQL = "insert into " + DrawDatabase.TABLE_HANDWRITING + "(URI, TITLE, CREATE_DATE) values(" 
			    			+ "'" + handwritingName+ "', '" + Title + "', DATETIME('now','localtime','+13 hours'))";
			    	if (mDatabase != null) {
			    		mDatabase.execSQL(SQL);
			    	}
			    	
			    	mDatabase.close();
				}

	    	} catch (IOException ex) {
	    		Log.d(TAG, "Exception in copying handwriting : " + ex.toString());
	    	}
    }
    
    private String createFilename() {
    	Date curDate = new Date();
    	String curDateStr = String.valueOf(curDate.getTime());

    	return curDateStr;
	}

	protected void onStart() {

        // 데이터베이스 열기
        openDatabase();

		super.onStart();
	    Log.i(TAG, "onStart()");
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
