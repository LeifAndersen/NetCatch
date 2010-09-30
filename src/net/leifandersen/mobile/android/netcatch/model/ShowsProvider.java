package net.leifandersen.mobile.android.netcatch.model;

import java.util.HashMap;

import net.leifandersen.mobile.android.netcatch.model.Shows.ShowsBaseColumns;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
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
	
    private static HashMap<String, String> sShowsProjectionMap;
    
    private static final int SHOWS = 1;
    private static final int SHOWS_ID = 2;
    
    private static final UriMatcher sUriMatcher;
    
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
						+ ShowsBaseColumns._ID + " INTEGER PRIMARY KEY,"
						+ ShowsBaseColumns.TITLE + " TEXT,"
						+ ShowsBaseColumns.AUTHOR + " TEXT,"
						+ ShowsBaseColumns.FEED + " Text,"
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

	private DatabaseHelper mOpenHelper;
	
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(SHOWS_TABLE_NAME);
		switch (sUriMatcher.match(uri)) {
		case SHOWS:
			qb.setProjectionMap(sShowsProjectionMap);
			break;
		case SHOWS_ID:
			qb.setProjectionMap(sShowsProjectionMap);
			qb.appendWhere(ShowsBaseColumns._ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unkown uri" + uri);
		}
		return null;
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
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Shows.AUTHORITY, "shows", SHOWS);
        sUriMatcher.addURI(Shows.AUTHORITY, "shows_id", SHOWS_ID);
        
        sShowsProjectionMap = new HashMap<String, String>();
        sShowsProjectionMap.put(ShowsBaseColumns._ID, ShowsBaseColumns._ID);
        sShowsProjectionMap.put(ShowsBaseColumns.TITLE, ShowsBaseColumns.TITLE);
        sShowsProjectionMap.put(ShowsBaseColumns.AUTHOR, ShowsBaseColumns.AUTHOR);
        sShowsProjectionMap.put(ShowsBaseColumns.FEED, ShowsBaseColumns.FEED);
	}
}
