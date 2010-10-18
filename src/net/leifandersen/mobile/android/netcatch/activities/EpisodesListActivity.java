package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EpisodesListActivity extends ListActivity {

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
				row = inflater.inflate(R.layout.episode, null);
			else
				row = convertView;
			
			TextView title = (TextView)row.findViewById(R.id.ep_title);
			TextView description = (TextView)row.findViewById(R.id.ep_description);
			TextView date = (TextView)row.findViewById(R.id.ep_release_date);
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
		mShowName = ""; // TODOo
		
		// Set the List Adapter
		refreshList();
	}

	private void refreshList() {
		mAdapter = new EpisodeAdapter(this);
		setListAdapter(mAdapter);
		
		// TODO Add the elements
		
	}
	
}
