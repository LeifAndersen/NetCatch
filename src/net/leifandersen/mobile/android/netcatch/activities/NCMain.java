package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 
 * The main activity for netcatch, provides a menu that the user
 * can do everything else from.
 * 
 * @author Leif Andersen
 *
 */
public class NCMain extends ListActivity {
	/** Called when the activity is first created. */

	private static final int QUEUE = 0;
	private static final int NEW_EPISODES = 1;
	private static final int SHOWS = 2;

	ArrayAdapter<String> mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.main_menu));
		setListAdapter(mAdapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent();
		switch (position) {
		case QUEUE:
			i.setClass(this, QueueListActivity.class);
			break;
		case NEW_EPISODES:
			i.setClass(this, QueueListActivity.class);
			break;
		case SHOWS:
		default:
			i.setClass(this, SubscriptionsListActivity.class);
			break;
		}
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflator = getMenuInflater();
		inflator.inflate(R.layout.subscriptions_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent activity;
		switch (item.getItemId()) {
		case R.id.new_show_item:
			Toast.makeText(this, "Building interface still", Toast.LENGTH_LONG);
			return true;
		case R.id.preferences:
			activity = new Intent();
			activity.setClass(this, Preferences.class);
			startActivity(activity);
			return true;
		}
		return false;
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