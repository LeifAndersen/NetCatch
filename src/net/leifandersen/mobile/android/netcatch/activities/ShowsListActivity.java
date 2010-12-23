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
import java.util.List;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Show;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import net.leifandersen.mobile.android.netcatch.services.RSSService;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * An activity that allows the user to navigate through
 * all of his subscriptions.
 * 
 * @author Leif Andersen
 *
 */
public class ShowsListActivity extends ListActivity {

	private static final class ViewHolder {
		TextView title;
		TextView counts;
		TextView updateDate;
		ImageView art;
	}

	private class TexturedListAdapter extends ArrayAdapter<Show> {

		final Typeface vera, veraBold;
		LayoutInflater mInflator;

		public TexturedListAdapter(Context context) {
			super(context, R.layout.show);
			vera = Typeface.createFromAsset(context.getAssets(), "Vera.ttf");
			veraBold = Typeface.createFromAsset(context.getAssets(), "VeraBd.ttf");
			mInflator = getLayoutInflater();
		}

		@Override
		public View getView(int position, View v, ViewGroup parent) {
			ViewHolder holder;
			if (v == null) {
				// Setup the view and holder
				v = mInflator.inflate(R.layout.show, null);
				holder = new ViewHolder();

				// Setup the viewholder elements
				holder.title = (TextView)v.findViewById(R.id.list_feed_title);
				holder.counts = (TextView)v.findViewById(R.id.list_feed_counts);
				holder.updateDate = (TextView)v.findViewById(R.id.list_feed_update_time);
				holder.art = (ImageView)v.findViewById(R.id.list_album_art);

				// Save the viewholder
				v.setTag(holder);
			}
			else
				holder = (ViewHolder)v.getTag();

			Show s = getItem(position);

			//TODO placeholders until Show class description is finalized
			holder.title.setText(s.getTitle());
			holder.title.setTypeface(veraBold);

			holder.counts.setText(s.getDescription());
			holder.counts.setTypeface(vera);

			holder.updateDate.setText(s.getFeed());
			holder.updateDate.setTypeface(vera);

			if(s.getImage() == null)
				holder.art.setImageResource(R.drawable.image_album_background);
			else
				holder.art.setImageDrawable(s.getImage());
			return v;
		}
	}

	private static final int NEW_FEED = 1;
	
	private BroadcastReceiver refreshReceiver;
	private LinearLayout background;
	private FrameLayout header;
	private TexturedListAdapter adapter;
	private SharedPreferences sharedPrefs;
	private List<Show> shows;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shows_list);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Set up the view
		background = (LinearLayout)findViewById(R.id.background);
		header = (FrameLayout)findViewById(R.id.header);
		NCMain.setColorOverlay(background, header);

		// Set up the refresh receiver
		refreshReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				refreshList();
			}
		};

		// Set up the refresh button
		findViewById(R.id.btn_refresh).setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// Refresh each show in the list
				for(Show show : shows) {
					Intent service = new Intent();
					service.putExtra(RSSService.FEED, show.getFeed());
					service.putExtra(RSSService.ID, show.getId());
					service.putExtra(RSSService.UPDATE_METADATA, true);
					service.putExtra(RSSService.BACKGROUND_UPDATE, false);
					service.setClass(ShowsListActivity.this, RSSService.class);
					startService(service);
					Log.w("ShowsListAcitivity", "Refreshing: " + show.getFeed());
				}
			}
		});
		
		// Refresh the list
		refreshList();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		refreshList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		int x = sharedPrefs.getInt("theme_color", -1);
		if(x != -1) {
			NCMain.overlay = new PorterDuffColorFilter(x, PorterDuff.Mode.MULTIPLY);
			NCMain.setColorOverlay(background, header);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			unregisterReceiver(refreshReceiver);
		} catch (Exception e) {

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.shows_menu, menu);
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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		// Get the show
		Show s = shows.get(position);
		
		// Start up the episode list for that show
		Intent i = new Intent();
		i.setClass(this, EpisodesListActivity.class);
		i.putExtra(EpisodesListActivity.SHOW_ID, s.getId());
		startActivity(i);
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;


		switch(id) {
		case NEW_FEED:
			dialog = new SubscriptionDialog(this);
			registerReceiver(refreshReceiver, 
					new IntentFilter(SubscriptionDialog.FINISHED));
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	private void refreshList() {
		// Update the adapter
		adapter = new TexturedListAdapter(this);
		setListAdapter(adapter);
		this.shows = new ArrayList<Show>();

		// Get all of the shows
		Cursor shows = managedQuery(ShowsProvider.SHOWS_CONTENT_URI, null, null, null, null);

		// Populate the view
		if(shows.moveToFirst())
			do {
				String imagePath = shows.getString(shows.getColumnIndex(ShowsProvider.IMAGE));
				Show s = new Show(
						shows.getInt(shows.getColumnIndex(ShowsProvider._ID)),
						shows.getString(shows.getColumnIndex(ShowsProvider.TITLE)),
						shows.getString(shows.getColumnIndex(ShowsProvider.AUTHOR)),
						shows.getString(shows.getColumnIndex(ShowsProvider.FEED)),
						shows.getString(shows.getColumnIndex(ShowsProvider.DESCRIPTION)),
						imagePath, Show.DEFAULT, Show.DEFAULT);
				s.setImage(Drawable.createFromPath(imagePath));
				adapter.add(s);
				this.shows.add(s);
			} while (shows.moveToNext());

	}
}
