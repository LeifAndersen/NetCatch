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
package net.leifandersen.mobile.android.netcatch.recievers;

import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

/**
 * To be run at the start of the boot sequence.  Waits for 30 seconds,
 * and starts up the alarms.
 * 
 * @author Leif Andersen
 *
 */
public class BootCompletedReceiver extends BroadcastReceiver{

	Context mCtx;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// Save the context
		mCtx = context;
		
		// Run in another thread
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				startAlarms();
			}
		});
		t.start();
	}

	private void startAlarms() {
		// Sleep for 30 seconds (to let system get network started)
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			Log.w("BootCompletedReceiver",
					"Interupted, continuting on with work");
		}
		
		Log.i("BootCompletedReceiver",
				"Starting up subscriptionAlarms");
		
		// Query for all of the shows
		Cursor c = mCtx.getContentResolver().query(ShowsProvider.SHOWS_CONTENT_URI,
				new String[] {ShowsProvider._ID,
				ShowsProvider.FEED, ShowsProvider.UPDATE_FREQUENCY},
				null, null, null);
		
		// For each show, start it's alarm
		c.moveToFirst();
		do {
			AlarmReceiver.startAlarm(mCtx,
					c.getLong(c.getColumnIndex(ShowsProvider._ID)),
					c.getString(c.getColumnIndex(ShowsProvider.FEED)),
					c.getLong(c.getColumnIndex(
							ShowsProvider.UPDATE_FREQUENCY)));
		} while(c.moveToNext());
	}
	
}
