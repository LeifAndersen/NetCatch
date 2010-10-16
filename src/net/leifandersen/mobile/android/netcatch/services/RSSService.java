package net.leifandersen.mobile.android.netcatch.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import net.leifandersen.mobile.android.netcatch.providers.Show;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 * A simple service that takes an RSS feed and returns a list of all
 * of the shows in that feed, or nothing if there isn't anything in that
 * feed to begin with.
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
	private String feed;
	
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
		feed = intent.getStringExtra(FEED);
		if (feed == null)
			throw new IllegalArgumentException("No feed placed in intent");

		// Start and run a thread to get and parse the XML
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				fetchData();	
			}
		});
		t.run();

		return START_NOT_STICKY;
	}

	
	/**
	 * Get the data from the service's feed.
	 */
	public void fetchData() {
		
		// Download the RSS feed
		Document feedDoc = getRSS(this, feed);
		if (feedDoc == null)
			serviceFailed();
		
		// Get the show
		Show show = getShowFromRSS(feedDoc, feed);
		
		// Get the episodes
		List<Episode> episodes = getEpisodesFromRSS(this, feedDoc);
		if(episodes == null || show == null)
			serviceFailed();
		
		// Put the Show into a bundle
		Bundle showBundle = new Bundle();
		showBundle.putSerializable(SHOW, show);
		
		// Put the Episodes into a bundle
		Bundle episodeBundle = new Bundle();
		for (Episode episode : episodes) {
			episodeBundle.putSerializable(episode.getTitle(), episode);
		}
		
		// Broadcast the bundles back to the main app
		Intent broadcast = new Intent(RSSFINISH + feed);
		broadcast.putExtra(EPISODES, episodeBundle);
		broadcast.putExtra(SHOW, showBundle);
		sendBroadcast(broadcast);
		stopSelf();
	}

	private void serviceFailed() {
		Intent broadcast = new Intent(RSSFAILED + feed);
		sendBroadcast(broadcast);
		stopSelf();
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
		if(netInfo == null || manager.getActiveNetworkInfo().getState() != NetworkInfo.State.CONNECTED)
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

	private static Show getShowFromRSS(Document feed, String feedUrl) {
		return new Show("Title", "Leif", feedUrl, null);
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
				episodes.add(new Episode(title, author, desc, url, date, false));
			}
			return episodes;
			
		} catch (Exception e) {
			// Any parse errors and we'll log and fail
			Log.e("NCRSS", "Error parsing RSS", e);
			return null;
		}
	}
}
