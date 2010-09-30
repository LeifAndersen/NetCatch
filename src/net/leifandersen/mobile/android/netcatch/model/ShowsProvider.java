package net.leifandersen.mobile.android.netcatch.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * 
 * @author leif
 *
 */
public class ShowsProvider extends ContentProvider {
	
	private static final String TAG = "ShowsProvider";
	
    private static final String DATABASE_NAME = "shows.db";
    private static final int DATABASE_VERSION = 2;
    private static final String SHOWS_TABLE_NAME = "shows";
	
    /**
     * 
     * @author leif
     *
     */
	private class DatabaseHelper extends SQLiteOpenHelper {
	    
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + SHOWS_TABLE_NAME + " ("
						+ Shows._ID + " INTEGER PRIMARY KEY,"
						+ Shows.TITLE + " TEXT,"
						+ Shows.AUTHOR + " TEXT,"
						+ Shows.FEED + " Text,"
						+ ");");
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + SHOWS_TABLE_NAME);
			onCreate(db);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
