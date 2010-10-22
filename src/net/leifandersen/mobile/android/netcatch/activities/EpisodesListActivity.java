package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import net.leifandersen.mobile.android.netcatch.providers.Show;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
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

	public static String SHOW_NAME;
	
	String mShowName;
	
	private class EpisodeAdapter extends ArrayAdapter<Episode> {
		public EpisodeAdapter(Context context) {
			super(context, R.layout.episodes_list);
		}

		@Override
		public View getView(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row;
			if(convertView == null)
				row = inflater.inflate(R.layout.episode_list_item, null);
			else
				row = convertView;
			
			TextView title = (TextView)row.findViewById(R.id.eli_title);
			TextView description = (TextView)row.findViewById(R.id.eli_description);
			TextView date = (TextView)row.findViewById(R.id.eli_release_date);
			Episode episode = getItem(position);
			title.setText(episode.getTitle());
			description.setText(episode.getDescription());
			date.setText(episode.getDate());
			
			registerForContextMenu(row);
			return row;
		}
	}

	EpisodeAdapter mAdapter;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.episodes_list);

		// Get the show name
		// If no show was passed in, the activity was called poorly, abort.
		Bundle b = getIntent().getExtras();
		if (b != null) {
			mShowName = b.getString(SHOW_NAME);
		} else
			finish();
			
		// Set the List Adapter
		refreshList();
	}

	private void refreshList() {
		mAdapter = new EpisodeAdapter(this);
		setListAdapter(mAdapter);
		
		// Get a list of all of the elements.
		// TODO, make sure to get it in the write order!
		// Add the list to the adapter
		Cursor c = managedQuery(Uri.parse("content://" + ShowsProvider.PROVIDER_NAME + "/" + mShowName),
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
