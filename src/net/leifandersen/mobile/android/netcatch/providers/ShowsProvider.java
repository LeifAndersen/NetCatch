package net.leifandersen.mobile.android.netcatch.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ShowsProvider extends ContentProvider {

	public static final String PROVIDER_NAME = 
		"net.leifandersen.provider.Shows";

	public static final Uri SUBSCRIPCTIONS_CONTENT_URI = 
		Uri.parse("content://" + PROVIDER_NAME + "/subscriptions");

	private static final String PROVIDER_TYPE =
		"vnd.leifandersen.provider.shows ";
	
	// For the shows table
	public static final String _ID = "_id";
	public static final String TITLE = "title";
	public static final String AUTHOR = "author";
	public static final String FEED = "feed";

	private static final String TAG = "ShowsProvider";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Shows";
	private static final String SUBSCRIPTIONS_TABLE_NAME = "subscriptions";
	private static final String DATABASE_CREATE = 
		"CREATE TABLE " + SUBSCRIPTIONS_TABLE_NAME + " ("
		+ _ID + " INTEGER PRIMARY KEY,"
		+ TITLE + " TEXT,"
		+ AUTHOR + " TEXT,"
		+ FEED + " TEXT" + ");";

	private static final String SHOW = "show";
	
	private static final int SUBSCRIPTIONS = 1;
	private static final int SUBSCRIPTION_ID = 2;

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "subscriptions", SUBSCRIPTIONS);
		uriMatcher.addURI(PROVIDER_NAME, "subscriptions/#", SUBSCRIPTION_ID);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS subscriptions");
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

		// Set up the table and possibly row selection
		switch (uriMatcher.match(uri)) {
		case SUBSCRIPTIONS:
			qb.setTables(SUBSCRIPTIONS_TABLE_NAME);
			break;
		case SUBSCRIPTION_ID:
			qb.setTables(SUBSCRIPTIONS_TABLE_NAME);
			qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}

		// Set up the order;
		if(TextUtils.isEmpty(sortOrder))
			sortOrder = TITLE;

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, 
				selectionArgs, null, null, null);

		// Tell the cursor what uri to watch, so it knows when it's source has changed
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case SUBSCRIPTIONS: // Get all subscriptions
			return "vnd.android.cursor.dir/" + PROVIDER_TYPE;
		case SUBSCRIPTION_ID:
			return "vnd.android.cursor.item/" + PROVIDER_TYPE;
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Validate the URI passed in
		if (uriMatcher.match(uri) != SUBSCRIPTIONS)
			throw new IllegalArgumentException("Unkown URI " + uri);
		
		if (values == null)
			values = new ContentValues();
		
		// Fill in empty values
		if (values.containsKey(TITLE) == false)
			values.put(TITLE, Resources.getSystem()
					.getString(android.R.string.untitled));
		if (values.containsKey(AUTHOR) == false)
			values.put(AUTHOR, Resources.getSystem()
					.getString(android.R.string.unknownName));
		if (values.containsKey(FEED) == false)
			values.put(FEED, Resources.getSystem()
					.getString(android.R.string.httpErrorBadUrl));
		
		// Insert the item
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(SUBSCRIPTIONS_TABLE_NAME, SHOW, values);
		if (rowId > 0) { //Added succesfully
			Uri _uri = ContentUris.withAppendedId(SUBSCRIPCTIONS_CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (uriMatcher.match(uri)) {
		case SUBSCRIPTIONS:
			count = db.update(SUBSCRIPTIONS_TABLE_NAME, values, selection, selectionArgs);
			break;
		case SUBSCRIPTION_ID:
			count = db.update(SUBSCRIPTIONS_TABLE_NAME, values, _ID + "=" +
					uri.getPathSegments().get(1) +
					(!TextUtils.isDigitsOnly(selection) 
							? " And (" + selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count;
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		switch(uriMatcher.match(uri)) {
		case SUBSCRIPTIONS:
			count = db.delete(SUBSCRIPTIONS_TABLE_NAME, selection, selectionArgs);
			break;
		case SUBSCRIPTION_ID:
			count = db.delete(SUBSCRIPTIONS_TABLE_NAME, _ID + "=" 
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty((selection))
							? " AND (" + selection + ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
