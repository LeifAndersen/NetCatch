package net.leifandersen.mobile.android.netcatch.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * 
 * @author leif
 *
 */
public class ShowsProvider extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "shows.db";
    private static final int DATABASE_VERSION = 2;
    private static final String SHOWS_TABLE_NAME = "shows";
    
	public ShowsProvider(Context context) {
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
		// TODO Auto-generated method stub
		
	}

}
