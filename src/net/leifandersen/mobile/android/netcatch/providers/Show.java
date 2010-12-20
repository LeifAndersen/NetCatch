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

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * A structure to hold a show object designed only for packing data to pass
 * in between methods
 * 
 * @author Leif Andersen
 *
 */
public class Show implements Serializable {

	public static final int NO_ID = -1;
	public static final int DEFAULT = -1;
	
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private String author;
	private String feed;
	private Drawable image;
	private String imagePath;
	private String description;
	private int episodesToKeep;
	private int updateFrequency;
	
	public Show() {
		
	}
	
	public Show(String title, String author, String feed, String description, 
			String imagePath, int episodesToKeep, int updateFrequency) {
		this.id = NO_ID;
		this.title = title;
		this.author = author;
		this.feed = feed;
		this.imagePath = imagePath;
		this.description = description;
		this.episodesToKeep = episodesToKeep;
		this.updateFrequency = updateFrequency;
	}
	
	public Show(int id, String title, String author, String feed, String description, 
			String imagePath, int episodesToKeep, int updateFrequency) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.feed = feed;
		this.imagePath = imagePath;
		this.description = description;
		this.episodesToKeep = episodesToKeep;
		this.updateFrequency = updateFrequency;
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
	 * @param drawable the image to set
	 */
	public void setImage(Drawable drawable) {
		this.image = drawable;
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

	/**
	 * @param episodesToKeep the episodesToKeep to set
	 */
	public void setEpisodesToKeep(int episodesToKeep) {
		this.episodesToKeep = episodesToKeep;
	}

	/**
	 * @return the episodesToKeep
	 */
	public int getEpisodesToKeep() {
		return episodesToKeep;
	}

	/**
	 * @param updateFrequency the updateFrequency to set
	 */
	public void setUpdateFrequency(int updateFrequency) {
		this.updateFrequency = updateFrequency;
	}

	/**
	 * @return the updateFrequency
	 */
	public int getUpdateFrequency() {
		return updateFrequency;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
}
