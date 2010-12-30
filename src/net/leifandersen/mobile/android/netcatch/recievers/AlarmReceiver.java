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

import net.leifandersen.mobile.android.netcatch.activities.Preferences;
import net.leifandersen.mobile.android.netcatch.services.RSSService;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * This receiver is triggered whenever the system sends out an alarm
 * message, it runs the RSS service for all of the feeds.
 * 
 * @author Leif Andersen
 *
 */
public class AlarmReceiver extends BroadcastReceiver {

	private static final int ALARM_ID = 1; // Not really used by android, but needed.
	public static final String ID = "id";
	public static final String UPDATE_FREQ = "update_frequency";
	public static final String FEED = "feed";
	@Override
	public void onReceive(Context context, Intent intent) {

		// Get the show's properties from the intent
		Bundle b = intent.getExtras();
		if(b == null)
			throw new IllegalArgumentException("No data given in the bundle");
		long id = b.getLong(ID, -1);
		String feed = b.getString(FEED);
		long updateFreq = b.getLong(UPDATE_FREQ, 0);
		if(feed == null || id < 0 || updateFreq < -2)
			throw new IllegalArgumentException("Bad parameter's given");

		// Start alarm again, call the rss service
		Intent service = new Intent();
		service.setClass(context, RSSService.class);
		service.putExtra(RSSService.ID, id);
		service.putExtra(RSSService.FEED, feed);
		service.putExtra(RSSService.BACKGROUND_UPDATE, true);
		Log.i("AlarmReceiver", "Updating id:" + id + " feed:" + feed);
		context.startService(service);
	}

	/**
	 * 
	 * @param context The application's context
	 * @param show The show used for the alarm
	 */
	public static void startAlarm(Context context, long id, String feed, long delay) {

		// If the show has a specific update time, use it
		// otherwise, stick to the default one.  If no default one,
		// don't update
		if(delay == Preferences.DEFAULT_TIME) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);		
			delay = new Long(pref.getString(Preferences.REFRESH_TIME, "0"));
		}

		// 0 means no auto-update, return
		if(delay == 0)
			return;

		// Start up the alarm.
		AlarmManager alarmManager = 
			(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC,
				System.currentTimeMillis(), delay, 
				getPendingIntent(context, id, feed, delay));
	}

	/**
	 * Returns the pending intent of the RSSSerivce.
	 * 
	 * @param context The context for the application
	 * @param show The show that the RSSService will update
	 * @return The pending intent
	 */
	public static PendingIntent getPendingIntent(Context context, long id, String feed, long updateFreq) {
		Intent service = new Intent();
		service.setClass(context, AlarmReceiver.class);
		service.putExtra(AlarmReceiver.ID, id);
		service.putExtra(AlarmReceiver.FEED, feed);
		service.putExtra(AlarmReceiver.UPDATE_FREQ, updateFreq);
		PendingIntent pendingIntent = 
			PendingIntent.getBroadcast(context, ALARM_ID, 
					service, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}
}
