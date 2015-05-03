package com.terang.common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DrawDatabase {

	public static final String TAG = "MemoDatabase";

	/**
	 * 싱글톤 인스턴스
	 */
	private static DrawDatabase database;

	/**
	 * table name for HANDWRITING
	 */
	public static String TABLE_HANDWRITING = "HANDWRITING";

    /**
     * version
     */
	public static int DATABASE_VERSION = 1;


    /**
     * Helper class defined
     */
    private DatabaseHelper dbHelper;

    /**
     * SQLiteDatabase 인스턴스
     */
    private SQLiteDatabase db;

    /**
     * 컨텍스트 객체
     */
    private Context context;

    /**
     * 생성자
     */
	private DrawDatabase(Context context) {
		this.context = context;
	}

	/**
	 * 인스턴스 가져오기
	 */
	public static DrawDatabase getInstance(Context context) {
		if (database == null) {
			database = new DrawDatabase(context);
		}

		return database;
	}

	/**
	 * 데이터베이스 열기
	 */
    public boolean open() {
    	println("opening database [" + BasicInfo.DATABASE_NAME + "].");

    	dbHelper = new DatabaseHelper(context);
    	db = dbHelper.getWritableDatabase();

    	return true;
    }

    /**
     * 데이터베이스 닫기
     */
    public void close() {
    	println("closing database [" + BasicInfo.DATABASE_NAME + "].");
    	db.close();

    	database = null;
    }

    /**
     * execute raw query using the input SQL
     * close the cursor after fetching any result
     *
     * @param SQL
     * @return
     */
    public Cursor rawQuery(String SQL) {
		println("\nexecuteQuery called.\n");

		Cursor c1 = null;
		try {
			c1 = db.rawQuery(SQL, null);
			println("cursor count : " + c1.getCount());
		} catch(Exception ex) {
    		Log.e(TAG, "Exception in executeQuery", ex);
    	}

		return c1;
	}

    public boolean execSQL(String SQL) {
		println("\nexecute called.\n");

		try {
			Log.d(TAG, "SQL : " + SQL);
			db.execSQL(SQL);
	    } catch(Exception ex) {
			Log.e(TAG, "Exception in executeQuery", ex);
			return false;
		}

		return true;
	}



	/**
	 * Database Helper inner class
	 */
    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, BasicInfo.DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
        	println("creating database [" + BasicInfo.DATABASE_NAME + "].");

            // TABLE_HANDWRITING
        	println("creating table [" + TABLE_HANDWRITING + "].");

        	// drop existing table
        	String DROP_SQL = "drop table if exists " + TABLE_HANDWRITING;
        	try {
        		db.execSQL(DROP_SQL);
        	} catch(Exception ex) {
        		Log.e(TAG, "Exception in DROP_SQL", ex);
        	}

        	// create table
        	String CREATE_SQL = "create table " + TABLE_HANDWRITING + "("
		        			+ "  _id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT, "
		        			+ "  URI TEXT, "
		        			+ "  TITLE TEXT, "
		        			+ "  CREATE_DATE DATETIME DEFAULT CURRENT_TIMESTAMP"
		        			+ ")";
            try {
            	db.execSQL(CREATE_SQL);
            } catch(Exception ex) {
        		Log.e(TAG, "Exception in CREATE_SQL", ex);
        	}

            // create index
        	String CREATE_INDEX_SQL = "create index " + TABLE_HANDWRITING + "_IDX ON " + TABLE_HANDWRITING + "("
		        			+ "URI"
		        			+ ")";
            try {
            	db.execSQL(CREATE_INDEX_SQL);
            } catch(Exception ex) {
        		Log.e(TAG, "Exception in CREATE_INDEX_SQL", ex);
        	}
        }

        public void onOpen(SQLiteDatabase db)
        {
        	println("opened database [" + BasicInfo.DATABASE_NAME + "].");

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
        	println("Upgrading database from version " + oldVersion + " to " + newVersion + ".");



        }
    }

    private void println(String msg) {
    	Log.d(TAG, msg);
    }


}