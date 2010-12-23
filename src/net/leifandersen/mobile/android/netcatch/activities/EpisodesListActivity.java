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

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
			super(context, R.layout.episodes_list);
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

			registerForContextMenu(convertView);
			return convertView;
		}
	}

	public static final String SHOW_ID = "id";
	public static final String SHOW_NAME = "name";
	
	private String mShowName;
	private int mShowID;
	private EpisodeAdapter mAdapter;
	private static final int NEW_FEED = 1;
	private static final int UNSUBSCRIBE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.episodes_list);

		// Get the show name
		// If no show was passed in, the activity was called poorly, abort.
		Bundle b = getIntent().getExtras();
		if (b == null)
			throw new IllegalArgumentException("No Bundle Given");
		mShowID = b.getInt(SHOW_ID, -1);
		mShowName = b.getString(SHOW_NAME);
		if(mShowID == -1 || mShowName == null)
			throw new IllegalArgumentException("No show ID and name given");

		// Set the List Adapter
		refreshList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.episodes_menu, menu);
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
	protected Dialog onCreateDialog(int id) {
		return onCreateDialog(id, new Bundle());
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;

		switch(id) {
		case NEW_FEED:
			dialog = new SubscriptionDialog(this);
			break;
		case UNSUBSCRIBE:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getResources().getString(R.string.unsubscribe_question) 
					+ mShowName + getResources().getString(R.string.question_punctuation))
					.setCancelable(false)
					.setPositiveButton(getResources().getString(R.string.ok), 
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Unsubscribe from the show
							getContentResolver().delete(Uri.parse(ShowsProvider.SHOWS_CONTENT_URI 
									+ "/" + mShowID + "/episodes"), null, null);						}
					})
					.setNegativeButton(getResources().getString(R.string.cancel), 
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Cancel the dialog 
							dialog.cancel();
						}
					});
			dialog = builder.create();
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
		// TODO, make sure to get it in the write order!
		// Add the list to the adapter
		Cursor c = managedQuery(Uri.parse(ShowsProvider.SHOWS_CONTENT_URI
				+ "/" + mShowID + "/episodes"), null, null, null, null);
		if (c.moveToFirst()) {
			do {
				Episode ep = new Episode(c.getString(c.getColumnIndex(ShowsProvider.TITLE)),
						c.getString(c.getColumnIndex(ShowsProvider.AUTHOR)),
						c.getString(c.getColumnIndex(ShowsProvider.DESCRIPTION)),
						c.getString(c.getColumnIndex(ShowsProvider.MEDIA)),
						c.getInt(c.getColumnIndex(ShowsProvider.DATE)),
						c.getInt(c.getColumnIndex(ShowsProvider.BOOKMARK)),
						/*c.getString(c.getColumnIndex(ShowsProvider.PLAYED))*/ false); // TODO, actually get the bool
				mAdapter.add(ep);
			} while (c.moveToNext());
		}		
	}
}
