package net.leifandersen.mobile.android.netcatch.services;

import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

public class UnsubscribeService extends Service {

	// Public parameters
	/**
	 * The Show ID to be unsubscribed from
	 */
	public static final String SHOW_ID = "show_id";
	
	// Public return values
	public static final String FINISHED = "finished ";
	
	// Private member variable
	private int mId;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		onStartCommand(intent, 0, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Setup the paremeters values
		mId = intent.getIntExtra(SHOW_ID, -1);
		if(mId < 0)
			throw new IllegalArgumentException("Bad or no ID");
		
		// Run the thread
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				unsubscribe();
			}
		});
		t.start();
		return START_STICKY;
	}
	
	private void unsubscribe() {
		// Delete the shows episodes
		getContentResolver().delete(Uri.parse(ShowsProvider.SHOWS_CONTENT_URI 
				+ "/" + mId + "/episodes"), null, null);
		
		// Delete the show itself
		getContentResolver().delete(Uri.parse(ShowsProvider.SHOWS_CONTENT_URI 
				+ "/" + mId), null, null);
		
		// Send out the finished broadcast, and finish
		Intent broadcast = new Intent(FINISHED + mId);
		sendBroadcast(broadcast);
		stopSelf();
	}
}
