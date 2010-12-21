/*Copyright 2010 NetCatch Team
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package net.leifandersen.mobile.android.netcatch.providers;

import java.util.ArrayList;

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

/**
 * Manages the content provider for all show data.
 * 
 * @author Leif Andersen
 *
 */
public class ShowsProvider extends ContentProvider {

	/**
	 * The provider name for shows subscriptions and episodes.
	 */
	public static final String PROVIDER_NAME = 
		"net.leifandersen.provider.Shows";

	/**
	 * The Subscriptions URI.
	 */
	public static final Uri SHOWS_CONTENT_URI = 
		Uri.parse("content://" + PROVIDER_NAME + "/shows");

	/**
	 * The Episodes URI
	 */
	public static final Uri EPISODES_CONTENT_URI = 
		Uri.parse("content://" + PROVIDER_NAME + "/episodes");
	
	/**
	 * The queue URI
	 */
	public static final Uri QUEUE_CONTENT_URI = 
		Uri.parse("content://" + PROVIDER_NAME + "/queue");

	/**
	 * The latest index URI
	 */
	public static final Uri LATEST_ID_URI = 
		Uri.parse("content://" + PROVIDER_NAME + "/id");
	
	/**
	 * New episodes URI
	 */
	public static final Uri NEW_EPISODES_CONTENT_URI =
		Uri.parse("content://" + PROVIDER_NAME + "/new");

	private static final String PROVIDER_TYPE =
		"vnd.leifandersen.provider.shows ";

	private static final String TAG = "ShowsProvider";

	/**
	 * The last ID case
	 */
	public static final String LATEST_ID = "last_insert_rowid()";
	
	// For the shows table
	/**
	 * The Row ID
	 */
	public static final String _ID = "_id";

	/**
	 * The show's or episode's title
	 */
	public static final String TITLE = "title";

	/**
	 * The show's or episode's author.
	 */
	public static final String AUTHOR = "author";

	/**
	 * The URI of the show's RSS Feed.
	 */
	public static final String FEED = "feed";

	/**
	 * The location of the show's or episode's image on the filesystem.
	 */
	public static final String IMAGE = "image";

	/**
	 * The episode description.
	 */
	public static final String DESCRIPTION = "description";

	/**
	 * The frequency the show should be updated.
	 */
	public static final String UPDATE_FREQUENCY = "update_frequency";

	/**
	 * The amount of episodes to keep.
	 */
	public static final String EPISODES_TO_KEEP = "episodes_to_keep";

	/**
	 * How long an episode stays on the device.
	 */
	public static final String EPISODE_EXPIRATION_TIME = "episode_expiration_time";

	/**
	 * The last time the show was updated.	
	 */
	public static final String PREVIOUS_UPDATE_TIME = "previous_update_time";

	// For the episodes tables
	/**
	 * The ID for and episode's show
	 */
	public static final String SHOW_ID = "show_id";

	/**
	 * The location for the actual media of the show, empty
	 * string if not downloaded
	 */
	public static final String MEDIA = "media";

	/**
	 * Where the media can be found and downloaded
	 */
	public static final String MEDIA_URL = "media_url";
	
	/**
	 * The date the show was released
	 */
	public static final String DATE = "date";

	/**
	 * Whether the episode has been played or not.
	 */
	public static final String PLAYED = "played";

	/**
	 * The last position the user was listening at.
	 */
	public static final String BOOKMARK = "position";
	
	
	// For the queue
	/**
	 * The ID for the queue's episode
	 */
	public static final String EPISODE_ID = "episode_id";
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Shows";
	private static final String SHOWS_TABLE_NAME = "shows";
	private static final String QUEUE_TABLE_NAME = "queue";
	private static final String EPISODES_TABLE_NAME = "episdoes";
	private static final String SHOW_TABLE_CREATE = 
		"CREATE TABLE " + SHOWS_TABLE_NAME + " ("
		+ _ID + " INTEGER PRIMARY KEY,"
		+ TITLE + " TEXT,"
		+ AUTHOR + " TEXT,"
		+ FEED + " TEXT,"
		+ DESCRIPTION + " TEXT,"
		+ IMAGE + " TEXT," 
		+ UPDATE_FREQUENCY + " INTEGER," 
		+ EPISODES_TO_KEEP + " INTEGER,"
		+ PREVIOUS_UPDATE_TIME + " INTEGER" + ");";
	private static final String EPISODE_TABLE_CREATE = 
		"CREATE TABLE  " + EPISODES_TABLE_NAME + " ("
		+ _ID + " INTEGER PRIMARY KEY,"
		+ SHOW_ID + " INTEGER NOT NULL,"
		+ TITLE + " TEXT,"
		+ AUTHOR + " TEXT,"
		+ DESCRIPTION + " TEXT," 
		+ MEDIA + " TEXT, "
		+ MEDIA_URL + " TEXT,"
		+ DATE + " INTEGER, "
		+ BOOKMARK + " INTEGER,"
		+ PLAYED + " BOOLEAN,"
		+ "FOREIGN KEY (" + SHOW_ID + ") REFERENCES " +
		SHOWS_TABLE_NAME + " (" + _ID + ")" + ");";
	private static final String QUEUE_TABLE_CREATE =
		"CREATE TABLE " + QUEUE_TABLE_NAME + " ("
		+ _ID + " INTEGER PRIMARY KEY,"
		+ EPISODE_ID + " INTEGER NOT NULL,"
		+ "FOREIGN KEY (" + EPISODE_ID + ") REFERENCES " + 
		EPISODES_TABLE_NAME + " (" + _ID + ")" + ");";

	private static final String SHOW = "show";
	private static final String EPISODE = "episode";
	private static final String QUEUE_STRING = "queue";

	private static final int SHOWS = 1;
	private static final int SHOW_ID_CASE = 2;
	private static final int EPISODES = 3;
	private static final int SHOW_ID_EPISODES = 4;
	private static final int EPISODE_ID_CASE = 5;
	private static final int QUEUE = 6;
	private static final int QUEUE_ID_CASE = 7;
	private static final int NEW_EPISODES = 8;
	private static final int NEW_EPISODE_ID = 9;
	private static final int LATEST_ID_CASE = 10;
	
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "shows", SHOWS);
		uriMatcher.addURI(PROVIDER_NAME, "shows/#", SHOW_ID_CASE);
		uriMatcher.addURI(PROVIDER_NAME, "episodes", EPISODES);
		uriMatcher.addURI(PROVIDER_NAME, "shows/#/episodes", SHOW_ID_EPISODES);
		uriMatcher.addURI(PROVIDER_NAME, "episodes/#", EPISODE_ID_CASE);
		uriMatcher.addURI(PROVIDER_NAME, "queue", QUEUE);
		uriMatcher.addURI(PROVIDER_NAME, "queue/#", QUEUE_ID_CASE);
		uriMatcher.addURI(PROVIDER_NAME, "new", NEW_EPISODES);
		uriMatcher.addURI(PROVIDER_NAME, "new/#", NEW_EPISODE_ID);
		uriMatcher.addURI(PROVIDER_NAME, "id", LATEST_ID_CASE);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Create the tables
			// Must be seperate commands
			db.execSQL(SHOW_TABLE_CREATE);
			db.execSQL(EPISODE_TABLE_CREATE);
			db.execSQL(QUEUE_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			
			// TODO Backup the feeds, also stored episode locations
			ArrayList<String> feeds = new ArrayList<String>();
			
			// Drop the table
			db.execSQL("DROP TABLE IF EXISTS subscriptions");
			db.execSQL("DROP TABLE IF EXISTS episodes");
			db.execSQL("DROP TABLE IF EXISTS queue");
			
			// Create the new table
			onCreate(db);
			
			// TODO restore the feeds
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
		case SHOW_ID_CASE:
			qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
		case SHOWS:
			qb.setTables(SHOWS_TABLE_NAME);
			break;
		case SHOW_ID_EPISODES:
			qb.setTables(EPISODES_TABLE_NAME);
			qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
			break;
		case EPISODE_ID_CASE:
			qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
		case EPISODES:
			qb.setTables(EPISODES_TABLE_NAME);
			break;
		case QUEUE_ID_CASE:
			qb.appendWhere(QUEUE_TABLE_NAME + "." + _ID + "=" + uri.getPathSegments().get(1) + " AND ");
		case QUEUE:
			qb.appendWhere(EPISODE_ID + "=" + EPISODES_TABLE_NAME + "." + _ID);
			qb.setTables(QUEUE_TABLE_NAME + ", " + EPISODES_TABLE_NAME);
			break;
		case NEW_EPISODE_ID:
			qb.appendWhere(DATE + "<" + uri.getPathSegments().get(1));
		case NEW_EPISODES:
			qb.setTables(EPISODES_TABLE_NAME);
			break;
		case LATEST_ID_CASE:
			SQLiteDatabase db = mOpenHelper.getReadableDatabase();
			Cursor c = db.rawQuery("SELECT last_insert_rowid();", null);
			c.setNotificationUri(getContext().getContentResolver(), uri);
			return c;
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}

		// Set up the order;
		if(TextUtils.isEmpty(sortOrder))
			sortOrder = TITLE;

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, 
				selectionArgs, null, null, sortOrder);

		// Tell the cursor what uri to watch, so it knows when it's source has changed
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case SHOWS:
		case SHOW_ID_EPISODES:
		case EPISODES:
		case QUEUE:
		case NEW_EPISODES:
			return "vnd.android.cursor.dir/" + PROVIDER_TYPE;
		case SHOW_ID_CASE:
		case EPISODE_ID_CASE:
		case QUEUE_ID_CASE:
		case NEW_EPISODE_ID:
		case LATEST_ID_CASE:
			return "vnd.android.cursor.item/" + PROVIDER_TYPE;
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		// Get the database
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		if (values == null)
			values = new ContentValues();
		long rowId;

		// Validate the URI passed in
		switch (uriMatcher.match(uri)) {
		case SHOWS:
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
			if (values.containsKey(DESCRIPTION) == false)
				values.put(DESCRIPTION, "");
			if (values.containsKey(IMAGE) == false)
				values.put(IMAGE, "");
			if (values.containsKey(UPDATE_FREQUENCY) == false)
				values.put(UPDATE_FREQUENCY, -1);
			if (values.containsKey(EPISODES_TO_KEEP) == false)
				values.put(EPISODES_TO_KEEP, -1);
			if (values.containsKey(PREVIOUS_UPDATE_TIME) == false)
				values.put(PREVIOUS_UPDATE_TIME, -1);
			
			// Insert the item
			rowId = db.insert(SHOWS_TABLE_NAME, SHOW, values);
			if (rowId > 0) { //Added successfully
				Uri _uri = ContentUris.withAppendedId(SHOWS_CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(_uri, null);
				return _uri;
			}
		case EPISODES:
			// Fill in empty values
			if(values.containsKey(SHOW_ID) == false)
				throw new SQLException("Show not in the database");
			if(values.containsKey(TITLE) == false)
				values.put(TITLE, Resources.getSystem()
						.getString(android.R.string.untitled));
			if(values.containsKey(AUTHOR) == false)
				values.put(AUTHOR, Resources.getSystem()
						.getString(android.R.string.unknownName));
			if(values.containsKey(BOOKMARK) == false)
				values.put(BOOKMARK, 0);
			if(values.containsKey(DATE) == false)
				values.put(DATE, java.lang.System.currentTimeMillis());
			
			
			// Insert the item
			rowId = db.insert(EPISODES_TABLE_NAME, EPISODE, values);
			if (rowId > 0) { //Added successfully
				Uri _uri = ContentUris.withAppendedId(EPISODES_CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(_uri, null);
				return _uri;
			}
		case QUEUE:
			// Fill in empty values
			if(values.containsKey(EPISODE_ID) == false)
				throw new SQLException("Episode not in database");
			
			// Insert the item
			rowId = db.insert(QUEUE_TABLE_CREATE, QUEUE_STRING, values);
			if(rowId > 0) { // Added successfully
				Uri _uri = ContentUris.withAppendedId(QUEUE_CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(_uri, null);
			}
		case SHOW_ID_EPISODES:
		case NEW_EPISODES:
		case NEW_EPISODE_ID:
		case LATEST_ID_CASE:
			throw new IllegalArgumentException("Read Only: " + uri);

		default:
			throw new SQLException("Failed to insert row into " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = 0;
		switch (uriMatcher.match(uri)) {
		case SHOWS:
			count = db.update(SHOWS_TABLE_NAME, values, selection, selectionArgs);
			break;
		case SHOW_ID_CASE:
			count = db.update(SHOWS_TABLE_NAME, values, _ID + "=" +
					uri.getPathSegments().get(1) +
					(!TextUtils.isDigitsOnly(selection) 
							? " And (" + selection + ')' : ""), selectionArgs);
			break;
		case EPISODES:
			count = db.update(EPISODES_TABLE_NAME, values, selection, selectionArgs);
			break;
		case EPISODE_ID_CASE:
			count = db.update(EPISODES_TABLE_NAME, values, _ID + "=" +
					uri.getPathSegments().get(1) +
					(!TextUtils.isDigitsOnly(selection) 
							? " And (" + selection + ')' : ""), selectionArgs);			break;
		case QUEUE:
			count = db.update(QUEUE_TABLE_NAME, values, selection, selectionArgs);
			break;
		case QUEUE_ID_CASE:
			count = db.update(QUEUE_TABLE_NAME, values, _ID + "=" +
					uri.getPathSegments().get(1) +
					(!TextUtils.isDigitsOnly(selection) 
							? " And (" + selection + ')' : ""), selectionArgs);
			break;
		case SHOW_ID_EPISODES:
		case NEW_EPISODES:
		case NEW_EPISODE_ID:
		case LATEST_ID_CASE:
			throw new IllegalArgumentException("Read Only: " + uri);
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		switch(uriMatcher.match(uri)) {
		case SHOWS:
			count = db.delete(SHOWS_TABLE_NAME, selection, selectionArgs);
			break;
		case SHOW_ID_CASE:
			count = db.delete(SHOWS_TABLE_NAME, _ID + "=" 
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty((selection))
							? " AND (" + selection + ')' : ""), selectionArgs);
			break;
		case EPISODES:
			count = db.delete(EPISODES_TABLE_NAME, selection, selectionArgs);
			break;
		case EPISODE_ID_CASE:
			count = db.delete(EPISODES_TABLE_NAME, _ID + "=" 
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty((selection))
							? " AND (" + selection + ')' : ""), selectionArgs);			break;
		case QUEUE:
			count = db.delete(QUEUE_TABLE_NAME, selection, selectionArgs);
			break;
		case QUEUE_ID_CASE:
			count = db.delete(QUEUE_TABLE_NAME, _ID + "=" 
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty((selection))
							? " AND (" + selection + ')' : ""), selectionArgs);
			break;
		case SHOW_ID_EPISODES:
		case NEW_EPISODES:
		case NEW_EPISODE_ID:
		case LATEST_ID_CASE:
			throw new IllegalArgumentException("Read Only: " + uri);
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
