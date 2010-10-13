package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Show;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SubscriptionsListActivity extends ListActivity {

	Cursor mShows;
	
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
		adapter = new ShowAdapter(this);
		setListAdapter(adapter);

		// Get all of the shows
		mShows = managedQuery(ShowsProvider.SUBSCRIPCTIONS_CONTENT_URI, null, null, null, null);

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
		else {
			ContentValues values = new ContentValues();
			values.put(ShowsProvider.TITLE, "My podcasat");
			values.put(ShowsProvider.AUTHOR, "Leif Andersen");
			values.put(ShowsProvider.FEED, "http://leifandersen.net/feed");
			getContentResolver().insert(ShowsProvider.SUBSCRIPCTIONS_CONTENT_URI, values);
		}
	}
}
