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

import java.util.ArrayList;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Show;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 
 * An activity that allows the user to navigate through
 * all of his subscriptions.
 * 
 * @author Leif Andersen
 *
 */
public class ShowsListActivity extends Activity {

	private static final int NEW_FEED = 1;
	private LinearLayout background;
	private FrameLayout header;
	protected SharedPreferences sharedPrefs;
	//private View mPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feeds_list);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		background = (LinearLayout)findViewById(R.id.background);
		header = (FrameLayout)findViewById(R.id.header);
		NCMain.setColorOverlay(background, header);
		
			/*Just using this to test the ListAdapter*/
			ArrayList<Show> testShows = new ArrayList<Show>();
			Show bol = new Show("Buzz Out Loud", "CNet Podcasts", "http://buzzoutloudpodcast.cnet.com", "Podcast of Indeterminate Length", "", -1, -1);
			bol.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.bol));
			testShows.add(bol);
			Show engadget = new Show("Engadget", "Engadget", "http://engadgetpodcast.com", "The Engadget.com Podcast", "", -1, -1);
			engadget.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.engadget));
			testShows.add(engadget);		
		
		TexturedListAdapter tla = new TexturedListAdapter(this, testShows);
		ListView lv = (ListView)findViewById(R.id.feeds_list);
		lv.setAdapter(tla);
		
		// Start the widget
		//mPlayer = ((ViewStub)findViewById(R.id.sl_small_player_stub)).inflate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.subscriptions_menu, menu);
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
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;
		switch(id) {
		
		case NEW_FEED:
			dialog = new SubscriptionDialog(this);
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		int x = sharedPrefs.getInt("theme_color", -1);
		if(x != -1) {
			NCMain.overlay = null;
			NCMain.overlay = new PorterDuffColorFilter(x, PorterDuff.Mode.MULTIPLY);
			NCMain.setColorOverlay(background, header);
		}
	}

	/*
	private void refreshList() {
		// Reset the view, 
		mAdapter = new ShowAdapter(this);
		setListAdapter(mAdapter);

		// Get all of the shows
		Cursor shows = managedQuery(ShowsProvider.SUBSCRIPCTIONS_CONTENT_URI, null, null, null, null);

		// Populate the view
		if(shows.moveToFirst())
			do {
				Show s = new Show();
				s.setTitle(shows.getString(shows.getColumnIndex(ShowsProvider.TITLE)));
				s.setAuthor(shows.getString(shows.getColumnIndex(ShowsProvider.AUTHOR)));
				String imagePath = shows.getString(shows.getColumnIndex(ShowsProvider.IMAGE));
				if (imagePath != "")
					s.setImage(Drawable.createFromPath(imagePath));
				mAdapter.add(s);
			} while (shows.moveToNext());
	}*/

}
