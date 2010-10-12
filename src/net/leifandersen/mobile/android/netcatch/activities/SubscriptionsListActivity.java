package net.leifandersen.mobile.android.netcatch.activities;

import java.nio.Buffer;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class SubscriptionsListActivity extends ListActivity {

	Cursor mShows;

	private static class ShowAdapter extends ArrayAdapter<String> {
		public ShowAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			// TODO Auto-generated constructor stub
		}
	}
	
	ShowAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscriptions_list);
		adapter = new ShowAdapter(this, R.layout.subscriptions_list);
		setListAdapter(adapter);
		mShows = managedQuery(ShowsProvider.SUBSCRIPCTIONS_CONTENT_URI, null, null, null, null);
	}
}
