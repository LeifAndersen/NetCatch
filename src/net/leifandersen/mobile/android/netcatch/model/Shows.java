package net.leifandersen.mobile.android.netcatch.model;

import android.provider.BaseColumns;

/**
 * 
 * @author leif
 *
 */
public class Shows implements BaseColumns {

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
	
}
