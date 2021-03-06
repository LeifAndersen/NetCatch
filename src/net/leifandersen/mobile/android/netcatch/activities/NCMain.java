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
import net.leifandersen.mobile.android.netcatch.other.GlobalVars;
import net.leifandersen.mobile.android.netcatch.other.ThemeTools;
import net.leifandersen.mobile.android.netcatch.other.Tools;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import net.leifandersen.mobile.android.netcatch.services.RSSService;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * The main activity for netcatch, provides a menu that the user
 * can do everything else from.
 * 
 * @author Leif Andersen
 *
 */
public class NCMain extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private static final int NEW_FEED = 1;
	private static final int COLOR_CHANGE_ACTIVITY_RESULT = 10;
	private static HomeScreenViewHolder homeViews;
	protected SharedPreferences sharedPrefs;
	private GlobalVars globalVars;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		globalVars = (GlobalVars)getApplicationContext();
		globalVars.initializeGlobalVars();
		
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		/* Any new additions to the layout XML which require modification should
		 * be added to the HomeScreenViewHolder, set in the following block, and
		 * added to their respective layout modification method (setTypeface,
		 * etc.)
		 * 
		 * @author Kevin Coppock
		 */
		homeViews = new HomeScreenViewHolder();
		homeViews.titleText = (TextView)findViewById(R.id.title_text);
		homeViews.playerEpisodeTitle = 
			(TextView)findViewById(R.id.player_episode_title);
		homeViews.playerEpisodeTime = 
			(TextView)findViewById(R.id.player_episode_time);
		homeViews.miniPlayer = (RelativeLayout)findViewById(R.id.player);
		homeViews.header = (FrameLayout)findViewById(R.id.header);
		homeViews.iconQueue = (ImageButton)findViewById(R.id.icon_queue);
		homeViews.iconFeeds = (ImageButton)findViewById(R.id.icon_feeds);
		homeViews.iconNew = (ImageButton)findViewById(R.id.icon_new);

		setTypeface(globalVars.getVera(), 
				homeViews.playerEpisodeTime);
		setTypeface(globalVars.getVeraBold(), 
				homeViews.titleText, 
				homeViews.playerEpisodeTitle);

		//need to change to pull color from SharedPreferences ~Kevin
		ThemeTools.setColorOverlay(new PorterDuffColorFilter(sharedPrefs
				.getInt("theme_color", Color.parseColor("#FFAAAAAA")),
				PorterDuff.Mode.MULTIPLY),
				homeViews.iconQueue,
				homeViews.iconFeeds,
				homeViews.iconNew,
				homeViews.miniPlayer,
				homeViews.header);

		homeViews.iconQueue.setOnClickListener(this);
		homeViews.iconFeeds.setOnClickListener(this);
		homeViews.iconNew.setOnClickListener(this);

		// Set up the refresh button
		findViewById(R.id.btn_refresh).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// Make sure only one is going at a time, so that
						// the system isn't getting overloaded
						synchronized (NCMain.this) {
							// Get each element from the database
							Cursor shows = managedQuery(
									ShowsProvider.SHOWS_CONTENT_URI, 
									null, null, null, null);
							if(shows.moveToFirst())
								do {
									String feed =
										shows.getString(shows.getColumnIndex(
											ShowsProvider.FEED));
									Intent service = new Intent();
									service.putExtra(RSSService.FEED, feed);
									service.putExtra(RSSService.ID,
											shows.getInt(shows.getColumnIndex(
													ShowsProvider._ID)));
									service.putExtra(
											RSSService.UPDATE_METADATA, true);
									service.putExtra(RSSService
											.BACKGROUND_UPDATE, false);
									service.setClass(
											NCMain.this, RSSService.class);
									startService(service);
									Log.w("NCMain", "Refreshing: " + feed);
								} while(shows.moveToNext());
						}
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent activity;
		switch (item.getItemId()) {
		case R.id.new_show_item:
			showDialog(NEW_FEED);
			return true;
		case R.id.preferences_item:
			activity = new Intent();
			activity.setClass(this, Preferences.class);
			startActivity(activity);
			return true;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		return onCreateDialog(id, null);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;
		switch(id) {

		case NEW_FEED:
			dialog = Tools.createSubscriptionDialog(this);
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

		int x = sharedPrefs.getInt("theme_color", -1);
		if(x != -1) {
			ColorFilter overlay = new PorterDuffColorFilter(x,
					PorterDuff.Mode.MULTIPLY);
			ThemeTools.setColorOverlay(overlay, overlay,
					homeViews.iconQueue,
					homeViews.iconFeeds,
					homeViews.iconNew,
					homeViews.miniPlayer,
					homeViews.header);	
		}
	}

	@Override
	public void onRestart() {
		super.onRestart();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.icon_feeds:
			i = new Intent(this, ShowsListActivity.class);
			startActivity(i);
			break;
		case R.id.icon_queue:
			i = new Intent(this, QueueListActivity.class);
			startActivity(i);
			break;
		case R.id.icon_new:
			i = new Intent(this, NewEpisodeListActivity.class);
			startActivity(i);
			break;
		}
	};

	static class HomeScreenViewHolder {
		TextView
			titleText,
			playerEpisodeTitle,
			playerEpisodeTime;
		ImageButton
			iconNew,
			iconQueue,
			iconFeeds;
			RelativeLayout
			miniPlayer;
		FrameLayout
			header;
	}

	/* This is just for purposes of simplifying the typeface change for the home
	 * screen, as many TextViews can be passed as necessary, to ease additional
	 * TextView additions to the layout.
	 * 
	 * @author Kevin Coppock 12/14/2010
	 */
	private void setTypeface(Typeface tf, TextView...params) {
		for (TextView tv : params) {
			tv.setTypeface(tf);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent i) {
		switch(requestCode) {
		case COLOR_CHANGE_ACTIVITY_RESULT:
			if(resultCode == RESULT_CANCELED) return;
			ThemeTools.setColorOverlay(
					new PorterDuffColorFilter(i.getIntExtra("color", 0),
							PorterDuff.Mode.MULTIPLY),
							homeViews.iconQueue,
							homeViews.iconFeeds,
							homeViews.iconNew,
							homeViews.miniPlayer,
							homeViews.header);
		}

	}
}