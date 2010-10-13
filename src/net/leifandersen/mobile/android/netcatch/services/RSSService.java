package net.leifandersen.mobile.android.netcatch.services;

import org.w3c.dom.Document;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
}
