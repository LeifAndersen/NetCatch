/*Copyright 2010 NetCatch Team
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package net.leifandersen.mobile.android.netcatch.other;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.services.RSSService;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Tools {
	
	public static final String SUBSCRIPTION_FINISHED = "SubscriptionDialog Finished";	

	public static boolean checkNetworkState(Context context, boolean backgroundUpdate) {
		// Get the connectivity manager
		ConnectivityManager manager = (ConnectivityManager)
		context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		// If user has set not to do background updates, 
		// And it's a background update, don't get it.
		if (!manager.getBackgroundDataSetting() && backgroundUpdate)
			return false;
		
		// If network is not available, bail
		NetworkInfo netInfo = manager.getActiveNetworkInfo();
		if(netInfo == null || netInfo.getState() != NetworkInfo.State.CONNECTED)
			return false;
		
		return true;
	}
	
	public static Dialog createSubscriptionDialog(final Context ctx) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		// Set up the layout
		LayoutInflater inflater = 
			(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.subscription_feed_dialog, null);
		final EditText editFeed = (EditText)layout.findViewById(R.id.sfd_editText);
		builder.setView(layout);
		builder.setCancelable(false);
		builder.setPositiveButton(ctx.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
			BroadcastReceiver finishedReceiver;
			BroadcastReceiver failedReciever;
			ProgressDialog progressDialog;
			@Override
			public void onClick(DialogInterface dialog, int which) {
				final String newFeed = editFeed.getText().toString();

				// Get the feed's data
				// Set the broadcast reciever
				finishedReceiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						// Clean up
						progressDialog.cancel();
						ctx.unregisterReceiver(finishedReceiver);
						ctx.unregisterReceiver(failedReciever);
						
						// Tell the list to refresh
						Intent broadcast = new Intent(SUBSCRIPTION_FINISHED);
						ctx.sendBroadcast(broadcast);
					}
				};
				ctx.registerReceiver(finishedReceiver, new IntentFilter(RSSService.RSSFINISH + newFeed));

				// Set up the failed dialog
				failedReciever = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						progressDialog.cancel();
						Toast.makeText(ctx, "Failed to fetch feed", Toast.LENGTH_LONG);
						ctx.unregisterReceiver(finishedReceiver);
						ctx.unregisterReceiver(failedReciever);
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
				service.putExtra(RSSService.BACKGROUND_UPDATE, false);
				service.setClass(ctx, RSSService.class);
				ctx.startService(service);
			}
		});
		builder.setNegativeButton(ctx.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return builder.create();
	}
	
	public static Dialog createUnsubscribeDialog(Context context,
			DialogInterface.OnClickListener positiveButton, String showName, long showID) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getResources().getString(R.string.unsubscribe_question) 
				+ showName + context.getResources().getString(R.string.question_punctuation))
				.setCancelable(false)
				.setPositiveButton(context.getResources().getString(R.string.ok), 
						positiveButton)
				.setNegativeButton(context.getResources().getString(R.string.cancel), 
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Cancel the dialog 
						dialog.cancel();
					}
				});
		return builder.create();
	}
}
