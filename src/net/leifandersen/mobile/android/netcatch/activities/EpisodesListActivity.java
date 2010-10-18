package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;

public class EpisodesListActivity extends Activity {

	Adapter mAdapter;
	
	private class EpisodeAdapter extends ArrayAdapter<Episode> {

		public EpisodeAdapter(Context context) {
			super(context, R.layout.episodes_list);
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.episodes_list);
		
		// Set the List Adapter
		mAdapter = new EpisodeAdapter(this);
	}
	
}
