package net.leifandersen.mobile.android.netcatch.providers;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

/**
 * A structure to hold a show object designed only for packing data to pass
 * in between methods
 * 
 * @author Leif Andersen
 *
 */
public class Show implements Serializable {

	private static final long serialVersionUID = 1L;
	private String title;
	private String author;
	private String feed;
	private Drawable image;
	private String imagePath;
	private String description;
	
	public Show() {
		
	}
	
	public Show(String title, String author, String feed, String description, String imagePath) {
		this.title = title;
		this.author = author;
		this.feed = feed;
		this.imagePath = imagePath;
		this.description = description;
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

	/**
	 * @param imagePath the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
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
}
