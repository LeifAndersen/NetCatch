package net.leifandersen.mobile.android.netcatch.activities;

import java.util.ArrayList;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Show;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * An activity that allows the user to navigate through
 * all of his subscriptions.
 * 
 * @author Leif Andersen
 *
 */
public class SubscriptionsListActivity extends Activity {

	private static final int NEW_FEED = 1;
	
	private View mPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feeds_list);
		
			/*Just using this to test the ListAdapter*/
			ArrayList<Show> testShows = new ArrayList<Show>();
			Show bol = new Show("Buzz Out Loud", "CNet Podcasts", "http://buzzoutloudpodcast.cnet.com", "Podcast of Indeterminate Length", null);
			bol.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.bol));
			testShows.add(bol);
			Show engadget = new Show("Engadget", "Engadget", "http://engadgetpodcast.com", "The Engadget.com Podcast", null);
			engadget.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.engadget));
			testShows.add(engadget);		
		
		TexturedListAdapter tla = new TexturedListAdapter(this, testShows);
		ListView lv = (ListView)findViewById(R.id.feeds_list);
		lv.setAdapter(tla);
		
		// Start the widget
		//mPlayer = ((ViewStub)findViewById(R.id.sl_small_player_stub)).inflate();
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

	/*
	private void refreshList() {
		// Reset the view, 
		mAdapter = new ShowAdapter(this);
		setListAdapter(mAdapter);

		// Get all of the shows
		Cursor shows = managedQuery(ShowsProvider.SUBSCRIPCTIONS_CONTENT_URI, null, null, null, null);

		// Populate the view
		if(shows.moveToFirst())
			do {
				Show s = new Show();
				s.setTitle(shows.getString(shows.getColumnIndex(ShowsProvider.TITLE)));
				s.setAuthor(shows.getString(shows.getColumnIndex(ShowsProvider.AUTHOR)));
				String imagePath = shows.getString(shows.getColumnIndex(ShowsProvider.IMAGE));
				if (imagePath != "")
					s.setImage(Drawable.createFromPath(imagePath));
				mAdapter.add(s);
			} while (shows.moveToNext());
	}*/

}
