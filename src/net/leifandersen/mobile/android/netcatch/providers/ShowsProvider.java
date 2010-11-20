package net.leifandersen.mobile.android.netcatch.providers;

import java.util.List;

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
	public static final Uri SUBSCRIPCTIONS_CONTENT_URI = 
		Uri.parse("content://" + PROVIDER_NAME + "/subscriptions");

	/**
	 * The queue URI
	 */
	public static final Uri QUEUE_CONTENT_URI = 
		Uri.parse("content://" + PROVIDER_NAME + "/queue");
	
	/**
	 * New episodes URI
	 */
	public static final Uri NEW_EPISODES_CONTENT_URI =
		Uri.parse("content://" + PROVIDER_NAME + "/new");
	
	private static final String PROVIDER_TYPE =
		"vnd.leifandersen.provider.shows ";

	private static final String TAG = "ShowsProvider";

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
	
	// For the episodes tables
	/**
	 * The location for the actual media of the show
	 */
	public static final String MEDIA = "media";

	/**
	 * The date the show was released
	 */
	public static final String DATE = "date";
	
	/**
	 * Whether the episode has been played or not.
	 */
	public static final String PLAYED = "played";
	
	// For the queue
	/**
	 * Show the episode is from
	 */
	public static final String SHOW_TITLE = "show_title";

	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Shows";
	private static final String SUBSCRIPTIONS_TABLE_NAME = "subscriptions";
	private static final String QUEUE_TABLE_NAME = "queue";
	private static final String NEW_EPISODES_TABLE_NAME = "newepisodes";
	private static final String SUBSCRIPTIONS_TABLE_CREATE = 
		"CREATE TABLE " + SUBSCRIPTIONS_TABLE_NAME + " ("
		+ _ID + " INTEGER PRIMARY KEY,"
		+ TITLE + " TEXT,"
		+ AUTHOR + " TEXT,"
		+ FEED + " TEXT,"
		+ DESCRIPTION + " TEXT,"
		+ IMAGE + " TEXT" + ");";
	private static final String QUEUE_TABLE_CREATE =
		"CREATE TABLE " + QUEUE_TABLE_NAME + " ("
		+ _ID + " INTEGER PRIMARY KEY,"
		+ TITLE + " TEXT,"
		+ SHOW_TITLE + " TEXT,"
		+ IMAGE + " TEXT,"
		+ AUTHOR + " TEXT,"
		+ DESCRIPTION + " TEXT," 
		+ MEDIA + " TEXT, "
		+ DATE + " TEXT, "
		+ PLAYED + " BOOLEAN" + ");";
	private static final String NEW_EPISODES_TABLE_CREATE = 
		"CREATE TABLE " + NEW_EPISODES_TABLE_NAME + " ("
		+ _ID + " INTEGER PRIMARY KEY,"
		+ TITLE + " TEXT,"
		+ SHOW_TITLE + " TEXT,"
		+ IMAGE + " TEXT,"
		+ AUTHOR + " TEXT,"
		+ DESCRIPTION + " TEXT," 
		+ MEDIA + " TEXT, "
		+ DATE + " TEXT, "
		+ PLAYED + " BOOLEAN" + ");";
	
	private static final String createTableString(final String tableName) {
		return 	"CREATE TABLE IF NOT EXISTS " + tableName + " ("
		+ _ID + " INTEGER PRIMARY KEY,"
		+ TITLE + " TEXT,"
		+ AUTHOR + " TEXT,"
		+ DESCRIPTION + " TEXT," 
		+ MEDIA + " TEXT, "
		+ DATE + " TEXT, "
		+ PLAYED + " BOOLEAN" + ");";
	}
	
	private static final String SHOW = "show";
	private static final String EPISODE = "episode";
	private static final String SPECIAL_EPISODE = "specialepisode";
	private static final String EMPTY_DATE = "";
	
	private static final int SUBSCRIPTIONS = 1;
	private static final int SUBSCRIPTION_ID = 2;
	private static final int QUEUE = 3;
	private static final int QUEUE_ID = 4;
	private static final int NEW_EPISODES = 5;
	private static final int NEW_EPISODE_ID = 6;
	
	// To replace ] in a name, as SQLite doesn't like it.
	private static final String CATCHBRAK = "~CatchBrak";

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "subscriptions", SUBSCRIPTIONS);
		uriMatcher.addURI(PROVIDER_NAME, "subscriptions/#", SUBSCRIPTION_ID);
		uriMatcher.addURI(PROVIDER_NAME, "queue", QUEUE);
		uriMatcher.addURI(PROVIDER_NAME, "queue/#", QUEUE_ID);
		uriMatcher.addURI(PROVIDER_NAME, "new", NEW_EPISODES);
		uriMatcher.addURI(PROVIDER_NAME, "new/#", NEW_EPISODE_ID);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// Create the tables
			// Must be seperate commands
			db.execSQL(SUBSCRIPTIONS_TABLE_CREATE);
			db.execSQL(QUEUE_TABLE_CREATE);
			db.execSQL(NEW_EPISODES_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS subscriptions");
			db.execSQL("DROP TABLE IF EXISTS queue");
			db.execSQL("DROP TABLE IF EXISTS newepisodes");
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
		case QUEUE:
			qb.setTables(QUEUE_TABLE_NAME);
			break;
		case QUEUE_ID:
			qb.setTables(QUEUE_TABLE_NAME);
			qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
			break;
		case NEW_EPISODES:
			qb.setTables(NEW_EPISODES_TABLE_NAME);
			break;
		case NEW_EPISODE_ID:
			qb.setTables(NEW_EPISODES_TABLE_NAME);
			qb.appendWhere(_ID + "=" + uri.getPathSegments().get(1));
			break;
		default:  // It's either an episode, or a bad request.
			List<String> episode_request = uri.getPathSegments();
			if (episode_request.size() < 1 || episode_request.size() > 2
					|| !isInSubcriptions(episode_request.get(0))
					|| (episode_request.size() == 2 && !TextUtils.isDigitsOnly(episode_request.get(1))))
				// It's a bad request
				throw new IllegalArgumentException("Unkown URI " + uri);
			qb.setTables('[' + episode_request.get(0).replace("]", CATCHBRAK) + ']');
			if(episode_request.size() == 2)
				qb.appendWhere(_ID + "=" + episode_request.get(1));
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
		case SUBSCRIPTIONS:
		case QUEUE:
		case NEW_EPISODES:
			return "vnd.android.cursor.dir/" + PROVIDER_TYPE;
		case SUBSCRIPTION_ID:
		case QUEUE_ID:
		case NEW_EPISODE_ID:
			return "vnd.android.cursor.item/" + PROVIDER_TYPE;
		default:
			List<String> episode_request = uri.getPathSegments();
			if (episode_request.size() < 1 || episode_request.size() > 2
					|| !isInSubcriptions(episode_request.get(0))
					|| (episode_request.size() == 2 && !TextUtils.isDigitsOnly(episode_request.get(1))))
				// It's a bad request
				throw new IllegalArgumentException("Unkown URI " + uri);
			if (episode_request.size() == 1)
				return "vnd.android.cursor.dir/" + PROVIDER_TYPE;
			else
				return 	"vnd.android.cursor.item/" + PROVIDER_TYPE;
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
		case SUBSCRIPTIONS:
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

			// Insert the item
			rowId = db.insert(SUBSCRIPTIONS_TABLE_NAME, SHOW, values);
			if (rowId > 0) { //Added succesfully
				Uri _uri = ContentUris.withAppendedId(SUBSCRIPCTIONS_CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(_uri, null);
				db.execSQL(createTableString("[" + values.getAsString(TITLE).replace("]", CATCHBRAK) 
						+ "]")); // Create the table.
				return _uri;
			}
		case QUEUE:
			// Fill in empty values
			if (values.containsKey(TITLE) == false)
				values.put(TITLE, Resources.getSystem()
						.getString(android.R.string.untitled));
			if (values.containsKey(AUTHOR) == false)
				values.put(AUTHOR, Resources.getSystem()
						.getString(android.R.string.unknownName));
			if (values.containsKey(DESCRIPTION) == false)
				values.put(DESCRIPTION, "");
			if (values.containsKey(MEDIA) == false)
				values.put(MEDIA, "");
			if (values.containsKey(DATE) == false)
				values.put(DATE, EMPTY_DATE);
			if (values.containsKey(PLAYED) == false)
				values.put(PLAYED, false);
			if (values.containsKey(IMAGE) == false)
				values.put(IMAGE, "");
			if (values.containsKey(SHOW_TITLE) == false)
				values.put(SHOW_TITLE, "");
			
			// Insert the item
			rowId = db.insert(QUEUE_TABLE_NAME, SPECIAL_EPISODE, values);
			if (rowId > 0) { //Added succesfully
				Uri _uri = ContentUris.withAppendedId(uri, rowId);
				getContext().getContentResolver().notifyChange(_uri, null);
				return _uri;
			}
			
		case NEW_EPISODES:
			// Fill in empty values
			if (values.containsKey(TITLE) == false)
				values.put(TITLE, Resources.getSystem()
						.getString(android.R.string.untitled));
			if (values.containsKey(AUTHOR) == false)
				values.put(AUTHOR, Resources.getSystem()
						.getString(android.R.string.unknownName));
			if (values.containsKey(DESCRIPTION) == false)
				values.put(DESCRIPTION, "");
			if (values.containsKey(MEDIA) == false)
				values.put(MEDIA, "");
			if (values.containsKey(DATE) == false)
				values.put(DATE, EMPTY_DATE);
			if (values.containsKey(PLAYED) == false)
				values.put(PLAYED, false);
			if (values.containsKey(IMAGE) == false)
				values.put(IMAGE, "");
			if (values.containsKey(SHOW_TITLE) == false)
				values.put(SHOW_TITLE, "");
			
			// Insert the item
			rowId = db.insert(NEW_EPISODES_TABLE_NAME, SPECIAL_EPISODE, values);
			if (rowId > 0) { //Added succesfully
				Uri _uri = ContentUris.withAppendedId(uri, rowId);
				getContext().getContentResolver().notifyChange(_uri, null);
				return _uri;
			}
			
		default:
			List<String> episode_request = uri.getPathSegments();
			if (episode_request.size() != 1 || !isInSubcriptions(episode_request.get(0)))
				throw new IllegalArgumentException("Unkown URI " + uri);

			// Remove ] if it's in the request, to make it a valid table
			String tableName = "[" + episode_request.get(0).replace("]", "~CatchBrak") + "]";

			// Fill in empty values
			if (values.containsKey(TITLE) == false)
				values.put(TITLE, Resources.getSystem()
						.getString(android.R.string.untitled));
			if (values.containsKey(AUTHOR) == false)
				values.put(AUTHOR, Resources.getSystem()
						.getString(android.R.string.unknownName));
			if (values.containsKey(DESCRIPTION) == false)
				values.put(DESCRIPTION, "");
			if (values.containsKey(MEDIA) == false)
				values.put(MEDIA, "");
			if (values.containsKey(DATE) == false)
				values.put(DATE, EMPTY_DATE);
			if (values.containsKey(PLAYED) == false)
				values.put(PLAYED, false);

			// Insert the item
			rowId = db.insert(tableName, EPISODE, values);
			if (rowId > 0) { //Added succesfully
				Uri _uri = ContentUris.withAppendedId(uri, rowId);
				getContext().getContentResolver().notifyChange(_uri, null);
				return _uri;
			}
		}

		// Not a valid URI
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		Cursor c;
		String newTitle;
		String oldTitle;
		switch (uriMatcher.match(uri)) {
		case SUBSCRIPTIONS:
			
			// Update the shows' tables in the database
			c = query(uri, null, selection, selectionArgs, null);
			newTitle = values.getAsString(TITLE);
			if (c.moveToFirst())
				do {
					oldTitle = c.getString(c.getColumnIndex(TITLE));
					if (!oldTitle.equals(newTitle))
						db.execSQL("ALTER TABLE " + "[" + oldTitle.replace("]", CATCHBRAK) + "]"
								+ " rename to " + "[" + newTitle.replace("]", CATCHBRAK) + "]");
				} while (c.moveToNext());
			
			// Update the subscriptions
			count = db.update(SUBSCRIPTIONS_TABLE_NAME, values, selection, selectionArgs);
			break;
		case SUBSCRIPTION_ID:
			
			// Update the show's table in the database
			c = query(uri, null, selection, selectionArgs, null);
			newTitle = values.getAsString(TITLE);
			oldTitle = c.getString(c.getColumnIndex(TITLE));
			if (!oldTitle.equals(newTitle))
				db.execSQL("ALTER TABLE " + "[" + oldTitle.replace("]", CATCHBRAK) + "]"
						+ " rename to " + "[" + newTitle.replace("]", CATCHBRAK) + "]");
			
			// Update the subscriptions
			count = db.update(SUBSCRIPTIONS_TABLE_NAME, values, _ID + "=" +
					uri.getPathSegments().get(1) +
					(!TextUtils.isDigitsOnly(selection) 
							? " And (" + selection + ')' : ""), selectionArgs);
			break;
		case QUEUE:
			count = db.update(QUEUE_TABLE_NAME, values, selection, selectionArgs);
			break;
		case QUEUE_ID:
			count = db.update(QUEUE_TABLE_NAME, values, _ID + "=" +
					uri.getPathSegments().get(1) +
					(!TextUtils.isDigitsOnly(selection) 
							? " And (" + selection + ')' : ""), selectionArgs);
			break;
		case NEW_EPISODES:
			count = db.update(NEW_EPISODES_TABLE_NAME, values, selection, selectionArgs);
			break;
		case NEW_EPISODE_ID:
			count = db.update(NEW_EPISODES_TABLE_NAME, values, _ID + "=" +
					uri.getPathSegments().get(1) +
					(!TextUtils.isDigitsOnly(selection) 
							? " And (" + selection + ')' : ""), selectionArgs);
			break;
		default:
			List<String> episode_request = uri.getPathSegments();
			if (episode_request.size() != 2 || !isInSubcriptions(episode_request.get(0))
					|| !TextUtils.isDigitsOnly(episode_request.get(1)))
				// It's a bad request
				throw new IllegalArgumentException("Unkown URI " + uri);
			
			// Only allow episodes to be updated, not the entire table.
			count = db.update("[" + episode_request.get(0).replace("]", CATCHBRAK) + "]", values, 
					_ID + "=" + episode_request.get(1) + (!TextUtils.isDigitsOnly(selection) 
							? " And (" + selection + ')' : ""), selectionArgs);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count;
		Cursor c;
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		switch(uriMatcher.match(uri)) {
		case SUBSCRIPTIONS:
			
			// Delete all other tables from the database. 
			c = query(uri, null, selection, selectionArgs, null);
			if(c.moveToFirst())
				do {
					db.execSQL("DROP TABLE IF EXISTS [" + c.getString(c.getColumnIndex(TITLE)).replace("]", CATCHBRAK) + "]");
				} while (c.moveToNext());

			// Clear out the subscriptions table.
			count = db.delete(SUBSCRIPTIONS_TABLE_NAME, selection, selectionArgs);
			break;
		case SUBSCRIPTION_ID:
			
			// Delete the table for that show
			c = query(uri, null, selection, selectionArgs, null);
			db.execSQL("DROP TABLE IF EXISTS [" + c.getString(c.getColumnIndex(TITLE)).replace("]", CATCHBRAK) + "]");
			
			// Remove it from the subscriptions table
			count = db.delete(SUBSCRIPTIONS_TABLE_NAME, _ID + "=" 
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty((selection))
							? " AND (" + selection + ')' : ""), selectionArgs);
			break;
		case QUEUE:
			count = db.delete(QUEUE_TABLE_NAME, selection, selectionArgs);
			break;
		case QUEUE_ID:
			count = db.delete(QUEUE_TABLE_NAME, _ID + "=" 
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty((selection))
							? " AND (" + selection + ')' : ""), selectionArgs);
			break;
		case NEW_EPISODES:
			count = db.delete(NEW_EPISODES_TABLE_NAME, selection, selectionArgs);
			break;
		case NEW_EPISODE_ID:
			count = db.delete(NEW_EPISODES_TABLE_NAME, _ID + "=" 
					+ uri.getPathSegments().get(1)
					+ (!TextUtils.isEmpty((selection))
							? " AND (" + selection + ')' : ""), selectionArgs);
			break;
		default:
			List<String> episode_request = uri.getPathSegments();
			if (episode_request.size() != 2 || !isInSubcriptions(episode_request.get(0))
					|| !TextUtils.isDigitsOnly(episode_request.get(1)))
				// It's a bad request
				throw new IllegalArgumentException("Unkown URI " + uri);
			
			// Only allow them to delete an episode, not the whole show
			// Subscriptions interface must be used.
			count = db.delete(episode_request.get(0).replace("]", CATCHBRAK), _ID
					+ "=" + episode_request.get(1) + (!TextUtils.isEmpty((selection))
							? " AND (" + selection + ')' : ""), selectionArgs);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	private boolean isInSubcriptions(String showTitle) {
		return true;
		// TODO, get the actual return statement to work
		// Get all of the shows subscribed to
		/*Cursor c = query(ShowsProvider.SUBSCRIPCTIONS_CONTENT_URI, null, null, null, null);

		// Run through everything in the subscriptions, and return true if it's there.
		if (c.moveToFirst())
			do {
				if (c.getString(c.getColumnIndex(TITLE)).equals(showTitle))
					return true;
			} while (c.moveToNext());
		return false;*/
	}
}
