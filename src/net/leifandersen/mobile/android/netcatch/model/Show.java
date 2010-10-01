package net.leifandersen.mobile.android.netcatch.model;

import java.util.List;

/**
 * 
 * @author leif
 *
 */
public class Show {

	String title;
	String author;
	String feed;
	List<Episode> episodes;
	
	public Show() {
		
	}
	
	/**
	 * 
	 * Gets the latest version of the RSS feed for the show.
	 * 
	 * @return A list of the new episodes
	 */
	public List<Episode> updateShow() {
		// TODO
		return null;
	}
	
	/**
	 * 
	 * Gets a list of all of the episodes that were in the show.
	 * 
	 * @return A list of all of the episodes in the show.
	 */
	public List<Episode> getEpisodes() {
		return episodes;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean saveShow() {
		// TODO
		return false;
	}
	
	/**
	 * 
	 * @param feed
	 * @return
	 */
	public static Show getShow(String feed) {
		// TODO
		return new Show();
	}
	
	/**
	 * 
	 * @param feed
	 * @return
	 */
	public boolean loadShow(String feed) {
		// TODO
		return false;
	}
}
