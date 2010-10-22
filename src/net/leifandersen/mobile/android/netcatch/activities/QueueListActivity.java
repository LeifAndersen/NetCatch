package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class QueueListActivity extends ListActivity {

	private class QueueAdapter extends ArrayAdapter<Episode> {
		
		public QueueAdapter(Context context) {
			super(context, R.layout.episode_queue_item);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return super.getView(position, convertView, parent);
		}
	}
	
	private QueueAdapter mQueueAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.queue);
		
		// Set the adapter
		mQueueAdapter = new QueueAdapter(this);
		setListAdapter(mQueueAdapter);
		
		// Refresh the list
		refreshList();
	}
	
	private void refreshList() {
		
	}
}
