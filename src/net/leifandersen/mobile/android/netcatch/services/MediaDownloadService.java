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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MediaDownloadService extends Service {

	public String MEDIA_URL = "media_url";
	public String MEDIA_LOCATION = "media_location";
	public String MEDIA_ID = "media_id";
	public String BACKGROUND_UPDATE = "background_update";
	
	private String media_url;
	private String media_location;
	private boolean backgroundUpdate;
	private int id;
	
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
		id = intent.getIntExtra(MEDIA_ID, -1);
		
		// If peramiters not provided, bail
		if(media_url == null || media_location == null || id == -1)
			throw new IllegalArgumentException("Invalid parameters for MediaDownloadService");
		
		// Start the download in another thread
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				downloadMedia();
			}
		});
		
		if(backgroundUpdate)
			return START_NOT_STICKY;
		else
			return START_STICKY;
	}
	
	private void downloadMedia() {
		// TODO
	}
}
