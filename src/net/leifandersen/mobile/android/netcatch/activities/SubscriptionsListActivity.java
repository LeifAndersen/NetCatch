package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Show;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import net.leifandersen.mobile.android.netcatch.services.RSSService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

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

	private ProgressDialog progressDialog;
	private String newFeed;
	private EditText mEditFeed;
	
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

			// Set up the layout
			LayoutInflater inflater = 
				(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.subscription_feed_dialog, null);
			mEditFeed = (EditText)layout.findViewById(R.id.sfd_editText);
			builder.setView(layout);
			builder.setCancelable(false);
			builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Get the RSS feed the user entered
					newFeed = mEditFeed.getText().toString();

					// Get the feed's data
					// Set the broadcast reciever
					BroadcastReceiver finishedReceiver = new BroadcastReceiver() {
						@Override
						public void onReceive(Context context, Intent intent) {							
							// Get the data
							Bundle showBundle = intent.getBundleExtra(RSSService.SHOW);
							Show show = (Show)showBundle.get(RSSService.SHOW);

							// Add the show
							ContentValues values = new ContentValues();
							values.put(ShowsProvider.TITLE, show.getTitle());
							values.put(ShowsProvider.AUTHOR, show.getAuthor());
							values.put(ShowsProvider.FEED, show.getFeed());
							values.put(ShowsProvider.IMAGE, show.getImagePath());
							getContentResolver().insert(ShowsProvider.SUBSCRIPCTIONS_CONTENT_URI, values);

							progressDialog.cancel();

							// Refresh and return
							refreshList();
						}
					};
					registerReceiver(finishedReceiver, new IntentFilter(RSSService.RSSFINISH + newFeed));

					// Set up the failed dialog
					BroadcastReceiver failedReciever = new BroadcastReceiver() {
						@Override
						public void onReceive(Context context, Intent intent) {
							Toast.makeText(SubscriptionsListActivity.this, "Failed to fetch feed", Toast.LENGTH_LONG);
							progressDialog.cancel();
						}
					};
					registerReceiver(failedReciever, new IntentFilter(RSSService.RSSFAILED + newFeed));

					// Show a waiting dialog (that can be canceled)
					progressDialog =
						ProgressDialog.show(SubscriptionsListActivity.this,
								"", getString(R.string.getting_show_details));
					progressDialog.setCancelable(true);
					progressDialog.show();

					// Start the service
					Intent service = new Intent();
					service.putExtra(RSSService.FEED, newFeed);
					service.setClass(SubscriptionsListActivity.this, RSSService.class);
					startService(service);
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
