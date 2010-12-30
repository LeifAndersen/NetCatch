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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.other.Tools;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import net.leifandersen.mobile.android.netcatch.services.MediaDownloadService;
import net.leifandersen.mobile.android.netcatch.services.UnsubscribeService;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * 
 * @author Leif Andersen
 *
 */
public class EpisodesListActivity extends ListActivity {

	private static final class ViewHolder {
		TextView title;
		TextView description;
		TextView date;
	}

	private class EpisodeAdapter extends ArrayAdapter<Episode> {

		LayoutInflater mInflater;

		public EpisodeAdapter(Context context) {
			super(context, R.layout.episode_list_item);
			mInflater = getLayoutInflater();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.episode_list_item, null);
				holder = new ViewHolder();
				holder.title = (TextView)convertView.findViewById(R.id.eli_title);
				holder.description = (TextView)convertView.findViewById(R.id.eli_description);
				holder.date = (TextView)convertView.findViewById(R.id.eli_release_date);
				convertView.setTag(holder);
			}
			else
				holder = (ViewHolder)convertView.getTag();

			Episode episode = getItem(position);
			holder.title.setText(episode.getTitle());
			holder.description.setText(episode.getDescription());
			// holder.date.setText(episode.getDate());
			holder.date.setTag(new Date(episode.getDate()).toString());
			
			return convertView;
		}
	}

	public static final String SHOW_ID = "show_id";
	public static final String SHOW_NAME = "show_name";
	
	private String mShowName;
	private long mShowID;
	private EpisodeAdapter mAdapter;
	private static final int NEW_FEED = 1;
	private static final int UNSUBSCRIBE = 2;
	private List<Episode> mEpisodes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.episodes_list);

		// Get the show name
		// If no show was passed in, the activity was called poorly, abort.
		Bundle b = getIntent().getExtras();
		if (b == null)
			throw new IllegalArgumentException("No Bundle Given");
		
		
		mShowID = b.getLong(SHOW_ID, -1);
		mShowName = b.getString(SHOW_NAME);
		
		if(mShowID < 0 || mShowName == null)
			throw new IllegalArgumentException("No show ID and name given");

		// Set the List Adapter
		refreshList();
		
		// Registor the list for context menus
		registerForContextMenu(getListView());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		int foo = position;
		Episode e = mEpisodes.get(position);
		Intent i = new Intent();
		i.putExtra("Foo", e.getId()); // TODO
		i.setClass(this, EpisodeActivity.class);
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.episodes_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent activity;
		switch (item.getItemId()) {
		case R.id.home_item:
			activity = new Intent();
			activity.setClass(this, NCMain.class);
			startActivity(activity);
			return true;
		case R.id.unsubscribe_item:
			showDialog(UNSUBSCRIBE);
			break;
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		// TODO actually set up the proper menu
		inflater.inflate(R.menu.episodes_context_not_downloaded, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = 
			(AdapterContextMenuInfo)item.getMenuInfo();
		if(info == null)
			return false;
		switch(item.getItemId()) {
		case R.id.download:
			long id = info.id;
			Episode episode = mEpisodes.get((int)info.id);
			Intent service = new Intent();
			service.putExtra(MediaDownloadService.MEDIA_ID, episode.getId());
			service.putExtra(MediaDownloadService.MEDIA_URL, episode.getMediaUrl());
			service.putExtra(MediaDownloadService.MEDIA_LOCATION, "");
			service.putExtra(MediaDownloadService.BACKGROUND_UPDATE, false);
			startService(service);
			return true;
		}
		return false;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		return onCreateDialog(id, new Bundle());
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;
		switch(id) {
		case NEW_FEED:
			dialog = Tools.createSubscriptionDialog(this);
			break;
		case UNSUBSCRIBE:
			dialog = Tools.createUnsubscribeDialog(this, new DialogInterface.OnClickListener() {
				BroadcastReceiver finishedReceiver;
				ProgressDialog progressDialog;		
				public void onClick(DialogInterface dialog, int id) {
					// Register the broadcast receiver
					finishedReceiver = new BroadcastReceiver() {
						@Override
						public void onReceive(Context context, Intent intent) {
							// Clean up the mess that was made.
							unregisterReceiver(finishedReceiver);
							progressDialog.cancel();
							refreshList();
							finish();
						}
					};
					registerReceiver(finishedReceiver, new IntentFilter(UnsubscribeService.FINISHED + mShowID));
					
					// Pop up a dialog while waiting
					progressDialog =
						ProgressDialog.show(EpisodesListActivity.this, "",
								EpisodesListActivity.this.getString(R.string.unsubscribing_from_show)
								+ mShowName
								+ EpisodesListActivity.this.getString(R.string.end_quotation));
					progressDialog.setCancelable(false);
					progressDialog.show();

					// Unsubscribe from the show
					Intent service = new Intent();
					service.putExtra(UnsubscribeService.SHOW_ID, mShowID);
					service.setClass(EpisodesListActivity.this, UnsubscribeService.class);
					startService(service);
				}
			}, mShowName, mShowID);
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	private void refreshList() {
		mAdapter = new EpisodeAdapter(this);
		setListAdapter(mAdapter);

		// Get a list of all of the elements.
		// Add the list to the adapter
		mEpisodes = new ArrayList<Episode>();
		Cursor c = managedQuery(Uri.parse(ShowsProvider.SHOWS_CONTENT_URI
				+ "/" + mShowID + "/episodes"), null, null, null, null);
		if (c.moveToFirst()) {
			do {
				Episode ep = new Episode(c.getString(c.getColumnIndex(ShowsProvider.TITLE)),
						c.getString(c.getColumnIndex(ShowsProvider.AUTHOR)),
						c.getString(c.getColumnIndex(ShowsProvider.DESCRIPTION)),
						c.getString(c.getColumnIndex(ShowsProvider.MEDIA)),
						c.getString(c.getColumnIndex(ShowsProvider.MEDIA_URL)),
						c.getInt(c.getColumnIndex(ShowsProvider.DATE)),
						c.getInt(c.getColumnIndex(ShowsProvider.BOOKMARK)),
						/*c.getString(c.getColumnIndex(ShowsProvider.PLAYED))*/ false); // TODO, actually get the bool
				mAdapter.add(ep);
				mEpisodes.add(ep);
			} while (c.moveToNext());
		}		
	}
}
