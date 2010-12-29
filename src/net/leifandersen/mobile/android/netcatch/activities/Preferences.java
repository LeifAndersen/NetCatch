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
package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * The Main Preferences Class
 * Also containts constants for the keys used in the XML file.
 * 
 * @author Leif Andersen
 *
 */
public class Preferences extends PreferenceActivity {

	/**
	 * Constant to represent the default waiting time in the db.
	 */
	public static final long DEFAULT_TIME = -1;
	
	/**
	 * String, castable to an integer, the default waiting time
	 */
	public static final String REFRESH_TIME = "refresh_time";
	
	/**
	 * String, the location to download to (SDCard must be added)
	 */
	public static final String DOWNLOAD_LOCATION = "download_location";
	
	/**
	 * Boolean, Weather or not to download audio implicetly
	 */
	public static final String DOWNLOAD = "download";
	
	/**
	 * Boolean, only download audio under wifi
	 */
	public static final String WIFI_ONLY = "wifi_only";
	
	/**
	 * Boolean, only download audio when it's in the queue
	 */
	public static final String QUEUE_ONNLY = "queue_only";
	
	/**
	 * Get the Theme color
	 */
	public static final String THEME_COLOR = "theme_color";
	
	/**
	 * Boolean, Play episodes in an external player
	 */
	public static final String INTERNAL_PLAYER = "internal_player";
	
	/**
	 * Boolean, use the widget (requires use of internal_player)
	 */
	public static final String WIDGET_ENABLE = "widget_enable";
	
	/**
	 * Boolean, whether or not to synchronize with another source
	 */
	public static final String SYNCHRONIZE = "synchronize";
	
	/**
	 * Boolean, synchronize with google reader
	 */
	public static final String GOOGLE_READER_SYNC = "google_reader_sync";
	
	/**
	 * String, the tag to sync with google reader
	 */
	public static final String GOOGLE_READERE_SYNC_TAG = "google_reader_sync_tag";
	
	/**
	 * Boolean, sync with gpodder.net
	 */
	public static final String GPODDER_SYNC = "gpodder_sync";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main);
	}
}
