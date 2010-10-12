package net.leifandersen.mobile.android.netcatch.activities;

import java.nio.Buffer;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;

public class SubscriptionsListActivity extends ListActivity {

	Cursor mShows;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscriptions_list);
		mShows = managedQuery(ShowsProvider.SUBSCRIPCTIONS_CONTENT_URI, null, null, null, null);
	}
}
