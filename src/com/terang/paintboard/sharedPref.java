package com.terang.paintboard;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class sharedPref {
	private final String PREF_NAME = "com.terang.pref";
	
	public final static String PREF_USER_ID = "USER_ID";
	
	static Context mContext;
	
	public sharedPref(Context c){
		mContext = c;
	}
	
	public void put(String key, String value){
		SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		editor.putString(key, value);
		editor.commit();
	}
	
	public String getValue(String key, String dftValue){
		SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		
		try{
			return pref.getString(key, dftValue);
		}catch(Exception e){
			return dftValue;
		}
	}
}
