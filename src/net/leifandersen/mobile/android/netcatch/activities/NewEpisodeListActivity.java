package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.providers.Episode;
import net.leifandersen.mobile.android.netcatch.R;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;

public class NewEpisodeListActivity extends ListActivity {


	private static final class ViewHolder {
		
	}
	
	private class NewEpisodesAdapter extends ArrayAdapter<Episode> {
		
		LayoutInflater mInflater;
		
		public NewEpisodesAdapter(Context context) {
			super(context, R.layout.new_episode_item);
			mInflater = getLayoutInflater();
		}
	}
	
	private NewEpisodesAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_episodes_list);
		mAdapter = new NewEpisodesAdapter(this);
		setListAdapter(mAdapter);
	}
}
