package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 
 * The main activity for netcatch, provides a menu that the user
 * can do everything else from.
 * 
 * @author Leif Andersen
 *
 */
public class NCMain extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private static final int QUEUE = 0;
	private static final int NEW_EPISODES = 1;
	private static final int SHOWS = 2;
	private static Typeface vera, veraBold;
	private static final int NEW_FEED = 1;

	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.icon_feeds:
			i = new Intent(this, SubscriptionsListActivity.class);
			startActivity(i);
			break;
		case R.id.icon_queue:
			i = new Intent(this, QueueListActivity.class);
			startActivity(i);
			break;
		case R.id.icon_new:
			//implement
			break;
		}
	};
	
	TexturedListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_new_theme);
		
		vera = Typeface.createFromAsset(getAssets(), "Vera.ttf");
		veraBold = Typeface.createFromAsset(getAssets(), "VeraBd.ttf");
		
		TextView titleText = (TextView)findViewById(R.id.title_text);
		TextView playerEpisodeTitle = (TextView)findViewById(R.id.player_episode_title);
		TextView playerEpisodeTime = (TextView)findViewById(R.id.player_episode_time);
		
		titleText.setTypeface(veraBold);
		playerEpisodeTitle.setTypeface(veraBold);
		playerEpisodeTime.setTypeface(vera);
		
		ImageButton icon_queue = (ImageButton)findViewById(R.id.icon_queue);
		icon_queue.setOnClickListener(this);
		
		ImageButton icon_feeds = (ImageButton)findViewById(R.id.icon_feeds);
		icon_feeds.setOnClickListener(this);
		
		ImageButton icon_new = (ImageButton)findViewById(R.id.icon_new);
		icon_new.setOnClickListener(this);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.subscriptions_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent activity;
		switch (item.getItemId()) {
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
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;
		switch(id) {
		
		case NEW_FEED:
			dialog = new SubscriptionDialog(this);
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onRestart() {
		super.onRestart();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
}