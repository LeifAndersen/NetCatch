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

import java.io.Serializable;


/**
 * A structure to hold an episode object designed only for packing data to pass
 * in between methods
 * 
 * @author Leif Andersen
 *
 */
public class Episode implements Serializable {
	
	public static final int NO_ID = -1;
	public static final int NO_SHOW = -1;
	
	private static final long serialVersionUID = 1L;
	private String title;
	private String author;
	private String description;
	private String media;
	private boolean played;
	private int date;
	private int showId;
	private int bookmark;
	private int id;
	
	public Episode(int id, int showId, String title, String author, String description, 
			String media, int date, int bookmark, boolean played) {
		this.title = title;
		this.author = author;
		this.description = description;
		this.media = media;
		this.date = date;
		this.played = played;
		this.showId = showId;
		this.bookmark = bookmark;
		this.id = id;
	}
	
	public Episode(int showId, String title, String author, String description, 
			String media, int date, int bookmark, boolean played) {
		this.title = title;
		this.author = author;
		this.description = description;
		this.media = media;
		this.date = date;
		this.played = played;
		this.showId = showId;
		this.bookmark = bookmark;
		this.id = NO_ID;
	}
	
	public Episode(String title, String author, String description, 
			String media, int date, int bookmark, boolean played) {
		this.title = title;
		this.author = author;
		this.description = description;
		this.media = media;
		this.date = date;
		this.played = played;
		this.showId = showId;
		this.bookmark = bookmark;
		this.id = NO_ID;
		this.showId = NO_SHOW;
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	
	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param media the media to set
	 */
	public void setMedia(String media) {
		this.media = media;
	}
	
	/**
	 * @return the media
	 */
	public String getMedia() {
		return media;
	}

	/**
	 * @param played the played to set
	 */
	public void setPlayed(boolean played) {
		this.played = played;
	}

	/**
	 * @return the played
	 */
	public boolean isPlayed() {
		return played;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(int date) {
		this.date = date;
	}

	/**
	 * @return the date
	 */
	public int getDate() {
		return date;
	}

	/**
	 * @param showId the showId to set
	 */
	public void setShowId(int showId) {
		this.showId = showId;
	}

	/**
	 * @return the showId
	 */
	public int getShowId() {
		return showId;
	}

	/**
	 * @param bookmark the bookmark to set
	 */
	public void setBookmark(int bookmark) {
		this.bookmark = bookmark;
	}

	/**
	 * @return the bookmark
	 */
	public int getBookmark() {
		return bookmark;
	}
}
