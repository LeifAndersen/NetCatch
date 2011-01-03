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
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.activities.EpisodeActivity;
import net.leifandersen.mobile.android.netcatch.activities.Preferences;
import net.leifandersen.mobile.android.netcatch.other.Tools;
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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 
 * A simple service that takes an RSS feed and saves the data from the RSS feed.
 * 
 * Requires either the show object to be pased in, or just a feed,
 * which will create a new show object
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
	public static final String BACKGROUND_UPDATE = "background_update";

	private static final int NEW_SHOW = -1;
	private long id;
	private String feed;
	private boolean updateMetadata;
	private boolean backgroundUpdate;
	private NotificationManager mNotificationManager;

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
		id = intent.getLongExtra(ID, NEW_SHOW);
		feed = intent.getStringExtra(FEED);
		updateMetadata = intent.getBooleanExtra(UPDATE_METADATA, false);
		backgroundUpdate = intent.getBooleanExtra(BACKGROUND_UPDATE, true);

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

		if (backgroundUpdate)
			return START_NOT_STICKY;
		else
			return START_STICKY;
	}


	/**
	 * Get the data from the service's feed.
	 */
	public void fetchData() {

		String feed = this.feed;

		// Notify the user
		mNotificationManager =
			(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE); 
		Notification notification = 
			new Notification(R.drawable.notification_downloading,
					getString(R.string.refreshing), System.currentTimeMillis());	
		Intent notificationIntent = new Intent(this, EpisodeActivity.class);
		notificationIntent.putExtra(EpisodeActivity.ID, id);
		PendingIntent contentIntent = 
			PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getString(R.string.refreshing),
				feed, contentIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		mNotificationManager.notify(1, notification);

		// Download the RSS feed
		Document feedDoc = getRSS(this, backgroundUpdate, feed);
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
		if(Environment.MEDIA_MOUNTED.equals(
				Environment.getExternalStorageState()) && 
				show.getImagePath() != null &&
				(updateMetadata || id == NEW_SHOW)) {
			try {
				// Setup files,  save data
				SharedPreferences pref =
					PreferenceManager.getDefaultSharedPreferences(this);
				File file = new File(Environment.getExternalStorageDirectory(),
						pref.getString(Preferences.DOWNLOAD_LOCATION,
						"PODCASTS")
						+ "/" + show.getTitle() + "/image.png");
				Tools.downloadFile(this, backgroundUpdate,
						new URL(show.getImagePath()), file);

				// Add to to class to be writen to database
				show.setImagePath(file.getPath());
			} catch (Exception e) {
				// Problems are likely from malformed paths, skip this step
				Log.e("RSSService", "Could not download image");
			}
		}

		// Write show information
		synchronized(this) {
			ContentValues values = new ContentValues();
			values.put(ShowsProvider.TITLE, show.getTitle());
			values.put(ShowsProvider.AUTHOR, show.getAuthor());
			values.put(ShowsProvider.IMAGE, show.getImagePath());
			values.put(ShowsProvider.FEED, feed);
			values.put(ShowsProvider.DESCRIPTION, show.getDescription());
			values.put(ShowsProvider.UPDATE_FREQUENCY,
					show.getUpdateFrequency());
			values.put(ShowsProvider.EPISODES_TO_KEEP,
					show.getEpisodesToKeep());
			long id;
			if(this.id == NEW_SHOW) {
				// It's a new show
				// Insert the show into the database
				getContentResolver().insert(ShowsProvider.SHOWS_CONTENT_URI,
						values);

				// Get the id of the new show
				Cursor c = getContentResolver().query(
						ShowsProvider.LATEST_ID_URI, null, null, null, null);
				c.moveToFirst();
				id = c.getInt(c.getColumnIndex(ShowsProvider.LATEST_ID));
				c.close();
			} else if(updateMetadata) {
				// The show is already in the database.
				id = this.id;

				// Update the metadata
				getContentResolver().update(
						Uri.parse(ShowsProvider.SHOWS_CONTENT_URI 
								+ "/" + id), values, null, null);

				// Clear out old episode information from db
				getContentResolver().delete(
						Uri.parse(ShowsProvider.SHOWS_CONTENT_URI 
								+ "/" + id + "/episodes"), null, null);
			}
			else {
				// Set the id
				id = this.id;

				// Clear out old episode information from db
				getContentResolver().delete(
						Uri.parse(ShowsProvider.SHOWS_CONTENT_URI 
								+ "/" + id + "/episodes"), null, null);
			}

			// Write the episode information
			for(Episode episode : episodes) {
				values = new ContentValues();
				values.put(ShowsProvider.SHOW_ID, id);
				values.put(ShowsProvider.TITLE, episode.getTitle());
				values.put(ShowsProvider.AUTHOR, episode.getAuthor());
				values.put(ShowsProvider.DATE, episode.getDate());
				values.put(ShowsProvider.MEDIA_URL, episode.getMediaUrl());
				if(episode.isPlayed())
					values.put(ShowsProvider.PLAYED, ShowsProvider.IS_PLAYED);
				else
					values.put(ShowsProvider.PLAYED, ShowsProvider.NOT_PLAYED);
				values.put(ShowsProvider.DESCRIPTION, episode.getDescription());
				getContentResolver().insert(ShowsProvider.EPISODES_CONTENT_URI,
						values);
			}
		}

		// Send out the finish broadcast, clear notifications, stop self
		Intent broadcast = new Intent(RSSFINISH + feed);
		sendBroadcast(broadcast);
		mNotificationManager.cancel(1);
		stopSelf();
	}

	private void serviceFailed(String feed) {
		Intent broadcast = new Intent(RSSFAILED + feed);
		sendBroadcast(broadcast);
		mNotificationManager.cancel(1);
		stopSelf();
	}

	private static Document getRSS(Context context, boolean backgroundUpdate,
			String url) {

		if (!Tools.checkNetworkState(context, backgroundUpdate))
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

	private static Show getShowFromRSS(Context context, Document feed,
			String feedUrl) {
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

			String imageUrl;
			NodeList imagNode = el.getElementsByTagName("image");
			if(imagNode != null) {
				Element ima = (Element)imagNode.item(0);
				if (ima != null) {
					NodeList urlNode = ima.getElementsByTagName("url");
					if(urlNode == null || urlNode.getLength() < 1)
						imageUrl = null;
					else
						imageUrl =
							urlNode.item(0).getFirstChild().getNodeValue();
				} else
					imageUrl = null;
			} else
				imageUrl = null;

			return new Show(title, author, feedUrl, desc, imageUrl, -1, -1);
		} catch (Exception e) {
			// Any parse errors and we'll log and fail
			Log.e("NCRSS", "Error parsing RSS", e);
			return null;
		}
	}

	private static List<Episode> getEpisodesFromRSS(Context context,
			Document feed) {
		try {
			ArrayList<Episode> episodes = new ArrayList<Episode>();
			NodeList items = feed.getElementsByTagName("item");
			for(int i = 0; i < items.getLength(); i++) {
				// Fetch the elements
				// Safe if it's an actual feed.
				Element el = (Element)items.item(i);

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
				NodeList urlNode = el.getElementsByTagName("enclosure");
				if (urlNode == null || urlNode.getLength() < 1)
					url = "";
				else {
					Element urlEl = (Element)urlNode.item(0);
					if(urlEl == null)
						url = "";
					else
						url = urlEl.getAttribute("url");
				}


				// Convert the date string into the needed integer
				// TODO, use a non-depricated method
				long dateMills;
				try {
					dateMills = Date.parse(date);
				} catch (Exception e) {
					dateMills = 0;
				}

				// Add the new episode
				// ShowId and played doesn't really matter at this point
				episodes.add(new Episode(title, author, desc, "", url,
						dateMills, 0, false));
			}
			return episodes;

		} catch (Exception e) {
			// Any parse errors and we'll log and fail
			Log.e("NCRSS", "Error parsing RSS", e);
			return null;
		}
	}
}
