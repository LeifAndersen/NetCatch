package net.leifandersen.mobile.android.netcatch.activities;

import java.util.ArrayList;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import net.leifandersen.mobile.android.netcatch.providers.Show;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import net.leifandersen.mobile.android.netcatch.services.RSSService;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SubscriptionDialog extends Dialog {

	private Context ctx;
	private EditText mEditFeed;
	private String newFeed;
	private Dialog progressDialog;
	
	public SubscriptionDialog(Context context) {
		super(context);
		ctx = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set the view
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.subscription_feed_dialog, null);
		setContentView(layout);
		
		// Get the textbox, for retreiving text later.
		mEditFeed = (EditText)layout.findViewById(R.id.edit_text);
		
		// Set up other properties
		setTitle(ctx.getString(R.string.enter_feed));
		setCancelable(false);
		
		// Set up the OK button click
		((Button)findViewById(R.id.ok_button)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Get the RSS feed the user entered
				newFeed = mEditFeed.getText().toString();

				// Get the feed's data
				// Set the broadcast reciever
				BroadcastReceiver finishedReceiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {							
						// Get the data
						// Get the show
						Bundle showBundle = intent.getBundleExtra(RSSService.SHOW);
						Show show = (Show)showBundle.get(RSSService.SHOW);
						
						// Add the show
						ContentValues values = new ContentValues();
						values.put(ShowsProvider.TITLE, show.getTitle());
						values.put(ShowsProvider.AUTHOR, show.getAuthor());
						values.put(ShowsProvider.FEED, show.getFeed());
						values.put(ShowsProvider.IMAGE, show.getImagePath());
						ctx.getContentResolver().insert(ShowsProvider.SUBSCRIPCTIONS_CONTENT_URI, values);
						
						// Get the episodes, add to database.
						// Database doesn't need to be cleared, as it shouldn't have existed.
						Bundle episodeBundle = intent.getBundleExtra(RSSService.EPISODES);
						ArrayList<String> titles = episodeBundle.getStringArrayList(RSSService.EPISODE_TITLES);
						for (String title : titles) {								
							Episode episode = (Episode)episodeBundle.get(title);
							values = new ContentValues();
							values.put(ShowsProvider.TITLE, episode.getTitle());
							values.put(ShowsProvider.AUTHOR, episode.getAuthor());
							values.put(ShowsProvider.DATE, episode.getDate());
							values.put(ShowsProvider.PLAYED, episode.isPlayed());
							values.put(ShowsProvider.DESCRIPTION, episode.getDescription());
							ctx.getContentResolver().insert(Uri.parse("content://" + ShowsProvider.PROVIDER_NAME
									+ "/"+ show.getTitle()), values);
						}

						progressDialog.cancel();
					}
				};
				ctx.registerReceiver(finishedReceiver, new IntentFilter(RSSService.RSSFINISH + newFeed));

				// Set up the failed dialog
				BroadcastReceiver failedReciever = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						progressDialog.cancel();
						Toast.makeText(ctx, "Failed to fetch feed", Toast.LENGTH_LONG);
					}
				};
				ctx.registerReceiver(failedReciever, new IntentFilter(RSSService.RSSFAILED + newFeed));
				
				// Show a waiting dialog (that can be canceled)
				progressDialog =
					ProgressDialog.show(ctx,
							"", ctx.getString(R.string.getting_show_details));
				progressDialog.setCancelable(true);
				progressDialog.show();
				
				// Start the service
				Intent service = new Intent();
				service.putExtra(RSSService.FEED, newFeed);
				service.setClass(ctx, RSSService.class);
				ctx.startService(service);
				dismiss();
			}
		});
		
		// Set up the cancle button
		((Button)findViewById(R.id.cancel_button)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
