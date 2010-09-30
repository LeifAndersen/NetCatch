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
public class EpisodesDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shows.db";
    private static final int DATABASE_VERSION = 2;
	
	public EpisodesDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
