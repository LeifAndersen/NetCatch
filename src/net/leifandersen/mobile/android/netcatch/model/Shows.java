package net.leifandersen.mobile.android.netcatch.model;

import android.provider.BaseColumns;

/**
 * 
 * @author leif
 *
 */
public class Shows {
	
	public static final String AUTHORITY = "net.leifandersen.mobile.android.netcatch.providers.Shows";
	
	/**
	 * 
	 * @author leif
	 *
	 */
	public class ShowsBaseColumns implements BaseColumns {
	
		/**
		 * The Title of the show.
		 * <P>Type: TEXT</P>
		 */
		public static final String TITLE = "title";
		
		/**
		 * The Author of the show.
		 * <P>Type: TEXT</P>
		 */
		public static final String AUTHOR = "author";
		
		/**
		 * The URL of the RSS feed for the show
		 * <P>Type: TEXT</P>
		 */
		public static final String FEED = "feed";
		
		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "modified DESC";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single show.
		 */
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.leifandersen.show";
		
	}
}
