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
package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Episode;
import net.leifandersen.mobile.android.netcatch.providers.ShowsProvider;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

public class EpisodeActivity extends Activity {

	public static final String ID = "id";

	private long mId;
	private Episode mEpisode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.episode);

		// Get the Episode ID
		Bundle b = getIntent().getExtras();
		if (b == null)
			throw new IllegalArgumentException("No Bundle Given");
		mId = b.getLong(ID, -1);
		if(mId < 0)
			throw new IllegalArgumentException("No ID given");

		// Get the episode itself
		Cursor c = managedQuery(Uri.parse(ShowsProvider.EPISODES_CONTENT_URI + "/" + mId),
				null, null, null, null);
		c.moveToFirst();
		mEpisode = new Episode(mId, 
				c.getLong(c.getColumnIndex(ShowsProvider.SHOW_ID)),
				c.getString(c.getColumnIndex(ShowsProvider.TITLE)),
				c.getString(c.getColumnIndex(ShowsProvider.AUTHOR)),
				c.getString(c.getColumnIndex(ShowsProvider.DESCRIPTION)),
				c.getString(c.getColumnIndex(ShowsProvider.MEDIA)),
				c.getString(c.getColumnIndex(ShowsProvider.MEDIA_URL)),
				c.getLong(c.getColumnIndex(ShowsProvider.DATE)),
				c.getLong(c.getColumnIndex(ShowsProvider.BOOKMARK)),
				c.getLong(c.getColumnIndex(ShowsProvider.PLAYED)) == 
					ShowsProvider.IS_PLAYED);
		c.close();

		if(TextUtils.isEmpty(mEpisode.getMedia())) {
			Toast.makeText(this, "Please download episode first",
					Toast.LENGTH_LONG).show();
			finish();
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType((Uri.parse("file://" + mEpisode.getMedia())),
		"audio/mpeg");
		startActivity(intent);
		finish();
	}
}
