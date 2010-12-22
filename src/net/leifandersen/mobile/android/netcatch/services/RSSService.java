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
package net.leifandersen.mobile.android.netcatch.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import net.leifandersen.mobile.android.netcatch.providers.Show;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 * A simple service that takes an RSS feed and saves the data from the RSS feed.
 * 
 * Requires either the show object to be pased in, or just a feed, which will create
 * a new show object
 * 
 * @author Leif Andersen
 *
 */
public class RSSService extends Service {

	public static final String FEED = "feed";
	public static final String EPISODES = "episodes";
	public static final String SHOW = "show";
	public static final String RSSFINISH = "RSSFinish ";
	public static final String RSSFAILED = "RSSFAILED ";
	public static final String EPISODE_TITLES = "episode_titles";
	public static final String ID = "id";
	public static final String UPDATE_METADATA = "update_metadata";
	
	private static final int NEW_SHOW = -1;
	private int id;
	private String feed;
	private boolean updateMetadata;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		onStartCommand(intent, 0, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Set up the feed for this service
		id = intent.getIntExtra(ID, NEW_SHOW);
		feed = intent.getStringExtra(FEED);
		updateMetadata = intent.getBooleanExtra(UPDATE_METADATA, false);
		
		if (feed == null)
			throw new IllegalArgumentException("No feed placed in intent");

		// Start and run a thread to get and parse the XML
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				fetchData();	
			}
		});
		t.start();

		return START_NOT_STICKY;
	}


	/**
	 * Get the data from the service's feed.
	 */
	public void fetchData() {
		
		String feed = this.feed;
		
		// Download the RSS feed
		Document feedDoc = getRSS(this, feed);
		if (feedDoc == null) {
			serviceFailed(feed);
			return;
		}

		// Get the show
		Show show = getShowFromRSS(this, feedDoc, feed);

		// Get the episodes
		List<Episode> episodes = getEpisodesFromRSS(this, feedDoc);
		if(episodes == null || show == null) {
			serviceFailed(feed);
			return;
		}

		// Get the show's image:
		if(show.getImage() != null && (updateMetadata || id == NEW_SHOW)) {
			// Setup files,  save data
			File path = new File(Environment.
					getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS)
					.getPath() + "/" + show.getTitle() + "/");
			File file = new File(path, "image.png");
			saveImage(this, show.getImagePath(), path, file);
			
			// Add to to class to be writen to database
			show.setImagePath(file.getPath());
		}
		
		// Write show information
		ContentValues values = new ContentValues();
		values.put(ShowsProvider.TITLE, show.getTitle());
		values.put(ShowsProvider.AUTHOR, show.getAuthor());
		values.put(ShowsProvider.IMAGE, show.getImagePath());
		values.put(ShowsProvider.DESCRIPTION, show.getDescription());
		values.put(ShowsProvider.UPDATE_FREQUENCY, show.getUpdateFrequency());
		values.put(ShowsProvider.EPISODES_TO_KEEP, show.getEpisodesToKeep());
		int id;
		if(this.id == NEW_SHOW) {
			// It's a new show
			// Insert the show into the database
			getContentResolver().insert(ShowsProvider.SHOWS_CONTENT_URI, values);
			
			// Get the id of the new show
			Cursor c = getContentResolver().query(ShowsProvider.LATEST_ID_URI, null, null, null, null);
			c.moveToFirst();
			id = c.getInt(c.getColumnIndex(ShowsProvider.LATEST_ID));
		} else if(updateMetadata) {
			// The show is already in the database.
			id = this.id;
			
			// Update the metadata
			getContentResolver().update(Uri.parse(ShowsProvider.SHOWS_CONTENT_URI 
					+ "/" + id), values, null, null);
			
			// TODO clear out old episode information from db\
		}
		else {
			// Set the id
			id = this.id;
			
			// TODO Clear out old episode information from db
		}
		
		// Write the episode information
		for(Episode episode : episodes) {
			values = new ContentValues();
			values.put(ShowsProvider.SHOW_ID, id);
			values.put(ShowsProvider.TITLE, episode.getTitle());
			values.put(ShowsProvider.AUTHOR, episode.getAuthor());
			values.put(ShowsProvider.DATE, episode.getDate());
			values.put(ShowsProvider.PLAYED, episode.isPlayed());
			values.put(ShowsProvider.DESCRIPTION, episode.getDescription());
			getContentResolver().insert(ShowsProvider.EPISODES_CONTENT_URI, values);
		}
		
		// Send out the finish broadcast
		Intent broadcast = new Intent(RSSFINISH + feed);
		sendBroadcast(broadcast);
		stopSelf();
	}

	private void serviceFailed(String feed) {
		Intent broadcast = new Intent(RSSFAILED + feed);
		sendBroadcast(broadcast);
		stopSelf();
	}

	private static void saveImage(Context context, String url, File path, File file) {
		// Get the connectivity manager
		ConnectivityManager manager = (ConnectivityManager)
		context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		// If user has set not to do background updates, don't get it.
		if (!manager.getBackgroundDataSetting())
			return;
		
		// If network is not available, bail
		NetworkInfo netInfo = manager.getActiveNetworkInfo();
		if(netInfo == null || netInfo.getState() != NetworkInfo.State.CONNECTED)
			return;
		
		// Get the image
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			path.mkdirs();
			OutputStream os = new FileOutputStream(file);
			InputStream is = response.getEntity().getContent();
			byte[] data = new byte[is.available()];
	        is.read(data);
	        os.write(data);
	        is.close();
	        os.close();
		} catch (Exception e) {
			// Any exception is probably a newtork faiilure, bail
			return;
		}
	}
	
	private static Document getRSS(Context context, String url) {
		// Get the connectivity manager
		ConnectivityManager manager = (ConnectivityManager)
		context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// If the user has set to not to do background updates, don't get it.
		if (!manager.getBackgroundDataSetting())
			return null;

		// If networks is not available, bail
		NetworkInfo netInfo = manager.getActiveNetworkInfo();
		if(netInfo == null || netInfo.getState() != NetworkInfo.State.CONNECTED)
			return null;

		// Network is available get the document.
		try {
			Document doc;
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder();
			DefaultHttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			doc = builder.parse(response.getEntity().getContent());
			return doc;
		} catch (IOException e) {
			return null;  // The network probably died, just return null
		} catch (SAXException e) {
			// Problem parsing the XML, log and return nothing
			Log.e("NCRSS", "Error parsing XML", e);
			return null;
		} catch (Exception e) {
			// Anything else was probably another network problem, fail silently
			return null;
		}
	}

	private static Show getShowFromRSS(Context context, Document feed, String feedUrl) {
		try {
			// There should be one channel in the feed, get it.
			// Also, the cast should be okay if the XML is formatted correctly
			NodeList item = feed.getElementsByTagName("channel");
			Element el = (Element)item.item(0);
			
			String title;
			NodeList titleNode = el.getElementsByTagName("title");
			if (titleNode == null || titleNode.getLength() < 1)
				title = context.getString(R.string.default_title);
			else
				title = titleNode.item(0).getFirstChild().getNodeValue();
			
			String author;
			NodeList authorNode = el.getElementsByTagName("author");
			if (authorNode == null || authorNode.getLength() < 1)
				author = context.getString(R.string.default_author);
			else
				author = authorNode.item(0).getFirstChild().getNodeValue();
			
			String desc;
			NodeList descNode = el.getElementsByTagName("comments");
			if (descNode == null || descNode.getLength() < 1)
				desc = context.getString(R.string.default_comments);
			else
				desc = descNode.item(0).getFirstChild().getNodeValue();
			
			String imageUrl = null;
			NodeList imagNode = el.getElementsByTagName("image");
			if(imagNode != null) {
				Element ima = (Element)imagNode.item(0);
				NodeList urlNode = ima.getElementsByTagName("url");
				if(urlNode == null || descNode.getLength() < 1)
					imageUrl = null;
				else
					imageUrl = urlNode.item(0).getFirstChild().getNodeValue();
			}
			
			return new Show(title, author, feedUrl, desc, imageUrl, -1, -1);
		} catch (Exception e) {
			// Any parse errors and we'll log and fail
			Log.e("NCRSS", "Error parsing RSS", e);
			return null;
		}
	}

	private static List<Episode> getEpisodesFromRSS(Context context, Document feed) {
		try {
			ArrayList<Episode> episodes = new ArrayList<Episode>();
			NodeList items = feed.getElementsByTagName("item");
			for(int i = 0; i < items.getLength(); i++) {
				Element el = (Element)items.item(i);  // Safe if it's an actual feed.

				String title;
				NodeList titleNode = el.getElementsByTagName("title");
				if (titleNode == null || titleNode.getLength() < 1)
					title = context.getString(R.string.default_title);
				else
					title = titleNode.item(0).getFirstChild().getNodeValue();

				String author;
				NodeList authorNode = el.getElementsByTagName("author");
				if (authorNode == null || authorNode.getLength() < 1)
					author = context.getString(R.string.default_author);
				else
					author = authorNode.item(0).getFirstChild().getNodeValue();

				String date;
				NodeList dateNode = el.getElementsByTagName("pubDate");
				if (dateNode == null || dateNode.getLength() < 1)
					date = context.getString(R.string.default_date);
				else
					date = dateNode.item(0).getFirstChild().getNodeValue();

				String desc;
				NodeList descNode = el.getElementsByTagName("comments");
				if (descNode == null || descNode.getLength() < 1)
					desc = context.getString(R.string.default_comments);
				else
					desc = descNode.item(0).getFirstChild().getNodeValue();

				String url;
				NodeList urlNode = el.getElementsByTagName("link");
				if (urlNode == null || urlNode.getLength() < 1)
					url = "";
				else 
					url = urlNode.item(0).getFirstChild().getNodeValue();
				episodes.add(new Episode(title, author, desc, url, /* TODO date*/ 0, 0, false));
			}
			return episodes;

		} catch (Exception e) {
			// Any parse errors and we'll log and fail
			Log.e("NCRSS", "Error parsing RSS", e);
			return null;
		}
	}
}
