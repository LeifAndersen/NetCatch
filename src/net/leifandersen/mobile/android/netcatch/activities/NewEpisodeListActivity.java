package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.other.HueColorFilter;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import net.leifandersen.mobile.android.netcatch.R;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
	
	private LinearLayout background;
	private FrameLayout header;
	private NewEpisodesAdapter mAdapter;
	SharedPreferences mSharedPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_episodes_list);
		mAdapter = new NewEpisodesAdapter(this);
		setListAdapter(mAdapter);
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Set up the view
		background = (LinearLayout)findViewById(R.id.background);
		header = (FrameLayout)findViewById(R.id.header);
		int x = mSharedPrefs.getInt("theme_color", -1);
		if(x != -1)
			HueColorFilter.setColorOverlay(new PorterDuffColorFilter(x, 
					PorterDuff.Mode.MULTIPLY), background, header);
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
}
