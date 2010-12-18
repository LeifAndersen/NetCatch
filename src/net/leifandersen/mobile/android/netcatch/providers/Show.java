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

	private static final long serialVersionUID = 1L;
	private String title;
	private String author;
	private String feed;
	private Bitmap image;
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
	public void setImage(Bitmap image) {
		this.image = image;
	}
	
	/**
	 * @return the image
	 */
	public Bitmap getImage() {
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
