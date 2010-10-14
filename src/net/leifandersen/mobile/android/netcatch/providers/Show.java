package net.leifandersen.mobile.android.netcatch.providers;

import android.graphics.drawable.Drawable;

/**
 * A structure to hold a show object designed only for packing data to pass
 * in between methods
 * 
 * @author Leif Andersen
 *
 */
public class Show {
	private String title;
	private String author;
	private String feed;
	private Drawable image;
	
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
	 * @param feed the feed to set
	 */
	public void setFeed(String feed) {
		this.feed = feed;
	}
	
	/**
	 * @return the feed
	 */
	public String getFeed() {
		return feed;
	}
	
	/**
	 * @param image the image to set
	 */
	public void setImage(Drawable image) {
		this.image = image;
	}
	
	/**
	 * @return the image
	 */
	public Drawable getImage() {
		return image;
	}
}
