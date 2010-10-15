package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Show;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * An activity that allows the user to navigate through
 * all of his subscriptions.
 * 
 * @author Leif Andersen
 *
 */
public class SubscriptionsListActivity extends ListActivity {

	private Cursor mShows;

	private static final int NEW_FEED = 1;
	private static final int NEW_FEED_DETAILS = 2;

	private class ShowAdapter extends ArrayAdapter<Show> {
		public ShowAdapter(Context context) {
			super(context, R.layout.subscriptions_list);
		}

		@Override
		public View getView(int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View row;
			if(convertView == null)
				row = inflater.inflate(R.layout.subscription, null);
			else
				row = convertView;

			TextView title = (TextView)row.findViewById(R.id.sc_title);
			TextView author = (TextView)row.findViewById(R.id.sc_author);
			ImageView image = (ImageView)row.findViewById(R.id.sc_picture);
			Show subscription = getItem(position);
			title.setText(subscription.getTitle());
			author.setText(subscription.getAuthor());
			Drawable d = subscription.getImage();
			if (d != null)
				image.setImageDrawable(d);
			registerForContextMenu(row);

			return row;
		}
	}

	ShowAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscriptions_list);

		// Set up the list
		refreshList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflator = getMenuInflater();
		inflator.inflate(R.layout.subscriptions_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent activity = new Intent();
		switch (item.getItemId()) {
		case R.id.sm_new_show:
			showDialog(NEW_FEED);
			return true;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;
		switch(id) {
		case NEW_FEED:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = 
				(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.subscription_feed_dialog, null);
			builder.setView(layout);
			builder.setCancelable(false);
			builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO, actually get the feed properties!!!
					String newFeed = "";
					Show show = new Show("Title", "Author", newFeed, "");

					// Add the show
					ContentValues values = new ContentValues();
					values.put(ShowsProvider.TITLE, show.getTitle());
					values.put(ShowsProvider.AUTHOR, show.getAuthor());
					values.put(ShowsProvider.FEED, show.getFeed());
					values.put(ShowsProvider.IMAGE, show.getImagePath());
					getContentResolver().insert(ShowsProvider.SUBSCRIPCTIONS_CONTENT_URI, values);

					// Refresh and return
					refreshList();
				}
			});
			builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	private void refreshList() {
		// Reset the view, 
		adapter = new ShowAdapter(this);
		setListAdapter(adapter);

		// Get all of the shows
		mShows = managedQuery(ShowsProvider.SUBSCRIPCTIONS_CONTENT_URI, null, null, null, null);

		// Populate the view
		if(mShows.moveToFirst())
			do {
				Show s = new Show();
				s.setTitle(mShows.getString(mShows.getColumnIndex(ShowsProvider.TITLE)));
				s.setAuthor(mShows.getString(mShows.getColumnIndex(ShowsProvider.AUTHOR)));
				String imagePath = mShows.getString(mShows.getColumnIndex(ShowsProvider.IMAGE));
				if (imagePath != "")
					s.setImage(Drawable.createFromPath(imagePath));
				adapter.add(s);
			} while (mShows.moveToNext());
	}

}
