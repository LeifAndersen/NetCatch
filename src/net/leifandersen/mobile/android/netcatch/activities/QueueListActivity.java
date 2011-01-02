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
import net.leifandersen.mobile.android.netcatch.other.HueColorFilter;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QueueListActivity extends ListActivity {

	private static class ViewHolder {
		TextView title;
		TextView description;
		TextView date;
	}
	
	private class QueueAdapter extends ArrayAdapter<Episode> {
		
		LayoutInflater mInflater;
		
		public QueueAdapter(Context context) {
			super(context, R.layout.episode_queue_item);
			mInflater = getLayoutInflater();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO, fill out the image too.
			ViewHolder holder;
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.episode_queue_item, null);
				holder = new ViewHolder();
				holder.title = (TextView)convertView.findViewById(R.id.eqi_title);
				holder.description = (TextView)convertView.findViewById(R.id.eqi_description);
				holder.date = (TextView)convertView.findViewById(R.id.eqi_release_date);
				convertView.setTag(holder);
			}
			else
				holder = (ViewHolder)convertView.getTag();
			
			Episode episode = getItem(position);
			holder.title.setText(episode.getTitle());
			holder.description.setText(episode.getDescription());
			holder.date.setText(((Long)episode.getDate()).toString());
			
			registerForContextMenu(convertView);
			return convertView;
		}
	}
	
	private QueueAdapter mAdapter;
	private LinearLayout background;
	private FrameLayout header;
	private SharedPreferences mSharedPrefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.queue);
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		// Set up the view
		background = (LinearLayout)findViewById(R.id.background);
		header = (FrameLayout)findViewById(R.id.header);
		int x = mSharedPrefs.getInt("theme_color", -1);
		if(x != -1)
			HueColorFilter.setColorOverlay(new PorterDuffColorFilter(x, 
					PorterDuff.Mode.MULTIPLY), background, header);
		
		// Refresh the list
		refreshList();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		
		// Set up the view
		int x = mSharedPrefs.getInt("theme_color", -1);
		if(x != -1)
			HueColorFilter.setColorOverlay(new PorterDuffColorFilter(x, 
					PorterDuff.Mode.MULTIPLY), background, header);
	}
	
	private void refreshList() {
		// Reset the view, 
		mAdapter = new QueueAdapter(this);
		setListAdapter(mAdapter);

		// Get a list of all of the elements.
		// TODO, make sure to get it in the write order!
		// Add the list to the adapter
		// TODO Also get image
		Cursor c = managedQuery(ShowsProvider.QUEUE_CONTENT_URI,
				null, null, null, null);
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
			} while (c.moveToNext());
		}	
	}
}
