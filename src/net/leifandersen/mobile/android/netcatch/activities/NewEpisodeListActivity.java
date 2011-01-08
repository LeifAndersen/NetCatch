package net.leifandersen.mobile.android.netcatch.activities;

import java.sql.Date;
import java.util.ArrayList;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.other.GlobalVars;
import net.leifandersen.mobile.android.netcatch.other.ThemeTools;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
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

public class NewEpisodeListActivity extends ListActivity {

	private GlobalVars globalVars;
	
	private static final class ViewHolder {
		TextView title;
		TextView date;
	}
	
	private class NewEpisodesAdapter extends ArrayAdapter<Episode> {
		
		LayoutInflater mInflater;
		
		public NewEpisodesAdapter(Context context) {
			super(context, R.layout.new_episode);
			mInflater = getLayoutInflater();
		}
		
		@Override
		public View getView(int position, View v, ViewGroup parent) {
			ViewHolder holder;
			if (v == null) {
				// Setup the view and holder
				v = mInflater.inflate(R.layout.new_episode, null);
				holder = new ViewHolder();

				// Setup the viewholder elements
				holder.title = (TextView)v.findViewById(R.id.episode_title);
				holder.date = (TextView)v.findViewById(R.id.episode_date);

				// Save the viewholder
				v.setTag(holder);
			}
			else
				holder = (ViewHolder)v.getTag();

			Episode episode = getItem(position);

			//TODO placeholders until Show class description is finalized
			holder.title.setText(episode.getTitle());
			holder.title.setTypeface(globalVars.getVeraBold());

			String dateText = "Published " + (new Date(episode.getDate())).toLocaleString();
			holder.date.setText(dateText);
			holder.date.setTypeface(globalVars.getVera());

			return v;
		}
	}
	
	private LinearLayout background;
	private FrameLayout header;
	private TextView headerText;
	private NewEpisodesAdapter mAdapter;
	private ArrayList<Episode> mEpisodes;
	SharedPreferences mSharedPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_episodes_list);
		globalVars = (GlobalVars)getApplicationContext();
		
		mEpisodes = new ArrayList<Episode>();
		mAdapter = new NewEpisodesAdapter(this);
		setListAdapter(mAdapter);
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		
		// Set up the view
		background = (LinearLayout)findViewById(R.id.background);
		header = (FrameLayout)findViewById(R.id.header);
		headerText = (TextView)header.findViewById(R.id.title_text);
		headerText.setTypeface(globalVars.getVeraBold());
		int x = mSharedPrefs.getInt("theme_color", -1);
		if(x != -1)
			ThemeTools.setColorOverlay(new PorterDuffColorFilter(x, 
					PorterDuff.Mode.MULTIPLY), background, header);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		
		// Set up the view
		int x = mSharedPrefs.getInt("theme_color", -1);
		if(x != -1)
			ThemeTools.setColorOverlay(new PorterDuffColorFilter(x, 
					PorterDuff.Mode.MULTIPLY), background, header);
	}
}
