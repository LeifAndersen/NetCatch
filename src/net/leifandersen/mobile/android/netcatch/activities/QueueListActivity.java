package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
			holder.date.setText(episode.getDate());
			
			registerForContextMenu(convertView);
			return convertView;
		}
	}
	
	private QueueAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.queue);
		
		// Refresh the list
		refreshList();
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
						c.getString(c.getColumnIndex(ShowsProvider.DATE)),
						/*c.getString(c.getColumnIndex(ShowsProvider.PLAYED))*/ false); // TODO, actually get the bool
				mAdapter.add(ep);
			} while (c.moveToNext());
		}	
	}
}
