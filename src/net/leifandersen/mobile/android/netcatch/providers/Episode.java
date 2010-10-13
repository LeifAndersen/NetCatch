package net.leifandersen.mobile.android.netcatch.providers;


/**
 * A structure to hold an episode object designed only for packing data to pass
 * in between methods
 * 
 * @author Leif Andersen
 *
 */
public class Episode {
	
	private String title;
	private String author;
	private String description;
	private String media;
	private boolean played;
	
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
}
