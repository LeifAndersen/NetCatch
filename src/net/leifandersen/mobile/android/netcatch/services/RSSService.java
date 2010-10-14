package net.leifandersen.mobile.android.netcatch.services;

import java.util.ArrayList;
import java.util.List;

import net.leifandersen.mobile.android.netcatch.providers.Episode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RSSService extends Service {

	public static final String FEED = "feed";

	private Document doc;
	private String feed;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		feed = intent.getStringExtra(FEED);
		if (feed == null)
			throw new IllegalArgumentException("Know feed placed in intent");
		return START_STICKY;
	}

	private static List<Episode> parseRSS(Document feed) {
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
			Log.e("NetCatch", "Error parsing RSS", e);
			return null;
		}
	}
}
