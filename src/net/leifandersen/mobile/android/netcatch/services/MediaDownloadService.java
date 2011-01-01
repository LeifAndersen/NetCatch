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
import java.net.MalformedURLException;
import java.net.URL;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.other.Tools;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * A service to download media.  Can run in the background, or in the
 * forground.  The directory for the file doesn't need to exist (the service
 * will create it).
 * 
 * @author Leif Andersen
 *
 */
public class MediaDownloadService extends Service {

	// Input parameters
	public static final String MEDIA_URL = "media_url";
	public static final String MEDIA_LOCATION = "media_location";
	public static final String MEDIA_ID = "media_id";
	public static final String BACKGROUND_UPDATE = "background_update";

	// Output results
	// To be used like: MEDIA_FAILED/FINISHED + media url 
	// + ' ' + media location
	public static final String MEDIA_FAILED = "media_failed ";
	public static final String MEDIA_FINISHED = "media_finished ";

	private String media_url;
	private String media_location;
	private boolean backgroundUpdate;
	private long id;
	private URL url;
	private File file;

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
		// Get the needed peramiters
		media_url = intent.getStringExtra(MEDIA_URL);
		media_location = intent.getStringExtra(MEDIA_LOCATION);
		backgroundUpdate = intent.getBooleanExtra(BACKGROUND_UPDATE, true);
		id = intent.getLongExtra(MEDIA_ID, -1);

		// If peramiters not provided, bail
		if(media_url == null || media_location == null || id == -1)
			throw new IllegalArgumentException(
					"Invalid parameters for MediaDownloadService");

		// Make the file and url objects, make sure path exists
		file = new File(media_location);
		try {
			url = new URL(media_url);
		} catch (MalformedURLException e) {
			// Bad URL given, abort
			// Don't throw exception because it may just be that there
			//    is no media.
			Log.e("Downloading", "Bad URL given");
			Toast.makeText(getApplicationContext(),
					getString(R.string.no_media_to_download),
					Toast.LENGTH_LONG).show();
			serviceFailed();
			return START_NOT_STICKY;
		}

		// Start the download in another thread
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				downloadMedia();
			}
		});
		t.start();

		if(backgroundUpdate)
			return START_NOT_STICKY;
		else
			return START_STICKY;
	}

	private void downloadMedia() {

		// Check network state
		if(!Tools.checkNetworkState(this, backgroundUpdate)) {
			serviceFailed();
			return;
		}

		try {
			// Actually download the file
			Tools.downloadFile(this, backgroundUpdate, url, file);
			
			// Finish up, send finished broadcast, stop the service
			Intent broadcast = new Intent(MEDIA_FINISHED 
					+ media_url + " " + media_location);
			sendBroadcast(broadcast);
			stopSelf();
			return;
		} catch (Exception e) {
			// Any exceptions at this point are likely 
			// network problem, abort
			Toast.makeText(getApplicationContext(),
					getString(R.string.no_media_to_download),
					Toast.LENGTH_LONG).show();
			serviceFailed();
			Log.v("Download", "Download failed url: " + media_url + " file: "
					+ media_location);
			return;
		}
	}

	private void serviceFailed() {
		Intent broadcast = new Intent(MEDIA_FAILED 
				+ media_url + " " + media_location);
		sendBroadcast(broadcast);
		stopSelf();
		return;
	}
}
