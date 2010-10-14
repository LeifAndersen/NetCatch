package net.leifandersen.mobile.android.netcatch.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.leifandersen.mobile.android.netcatch.providers.Episode;

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
import android.os.IBinder;
import android.util.Log;

public class RSSService extends Service {

	public static final String FEED = "feed";

	private String feed;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
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

	public void fetchData() {
		List<Episode> episodes = parseRSS(getRSS(feed));
		if(episodes == null)
			return;
		// TODO
	}

	private Document getRSS(String url) {
		// Get the connectivity manager
		ConnectivityManager manager = 
			(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
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
			HttpGet request = new HttpGet(feed);
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

	private static List<Episode> parseRSS(Document feed) {
		if (feed == null)
			return null;
		try {
			ArrayList<Episode> episodes = new ArrayList<Episode>();
			NodeList items = feed.getElementsByTagName("item");
			for(int i = 0; i < items.getLength(); i++) {
				Element el = (Element)items.item(i);  // Safe if it's an actual feed.
				String title = el.getElementsByTagName("title")
					.item(0).getFirstChild().getNodeValue();
				String author = el.getElementsByTagName("author")
					.item(0).getFirstChild().getNodeValue();
				String date = el.getElementsByTagName("pubDate")
					.item(0).getFirstChild().getNodeValue();
				String desc = el.getElementsByTagName("comments")
					.item(0).getFirstChild().getNodeValue();
				String url = el.getElementsByTagName("link")
					.item(0).getFirstChild().getNodeValue();
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
