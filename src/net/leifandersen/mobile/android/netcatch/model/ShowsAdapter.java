package net.leifandersen.mobile.android.netcatch.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * @author leif
 *
 */
public class ShowsAdapter {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_AUTHOR = "author";
	private static final String TAG = "ShowsAdapter";
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "shows";
	
	private static final String DATABASE_CREATE = "";
	
	private SQLiteDatabase db;
	
	/**
	 * A helper for creating the shows database.
	 * 
	 * @author Leif
	 *
	 */
	private class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
		}
		
	}
}
