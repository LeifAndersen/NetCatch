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
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * The main activity for netcatch, provides a menu that the user
 * can do everything else from.
 * 
 * @author Leif Andersen
 *
 */
public class NCMain extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private static final int NEW_FEED = 1;
	private static final int COLOR_CHANGE_ACTIVITY_RESULT = 10;
	private static Typeface vera, veraBold;
	private static HomeScreenViewHolder homeViews;
	public static ColorFilter overlay = null;
	protected SharedPreferences sharedPrefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_new_theme);
		
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		overlay = null;
		overlay = new PorterDuffColorFilter(sharedPrefs.getInt("theme_color", Color.parseColor("#FFAAAAAA")), PorterDuff.Mode.MULTIPLY);
		
		vera = Typeface.createFromAsset(getAssets(), "Vera.ttf");
		veraBold = Typeface.createFromAsset(getAssets(), "VeraBd.ttf");
		
		/* Any new additions to the layout XML which require modification should be
		 * added to the HomeScreenViewHolder, set in the following block, and added
		 * to their respective layout modification method (setTypeface, etc.)
		 * 
		 * @author Kevin Coppock
		 */
		homeViews = new HomeScreenViewHolder();
		homeViews.titleText = (TextView)findViewById(R.id.title_text);
		homeViews.playerEpisodeTitle = (TextView)findViewById(R.id.player_episode_title);
		homeViews.playerEpisodeTime = (TextView)findViewById(R.id.player_episode_time);
		homeViews.iconFeedsText = (TextView)findViewById(R.id.icon_feeds_text);
		homeViews.iconNewText = (TextView)findViewById(R.id.icon_new_text);
		homeViews.iconQueueText = (TextView)findViewById(R.id.icon_queue_text);
		homeViews.miniPlayer = (RelativeLayout)findViewById(R.id.player);
		homeViews.header = (FrameLayout)findViewById(R.id.header);
		homeViews.iconQueue = (ImageButton)findViewById(R.id.icon_queue);
		homeViews.iconFeeds = (ImageButton)findViewById(R.id.icon_feeds);
		homeViews.iconNew = (ImageButton)findViewById(R.id.icon_new);

		setTypeface(vera, 
				homeViews.playerEpisodeTime);
		setTypeface(veraBold, 
				homeViews.titleText, 
				homeViews.playerEpisodeTitle,
				homeViews.iconFeedsText,
				homeViews.iconNewText,
				homeViews.iconQueueText);
		
		//need to change to pull color from SharedPreferences ~Kevin
		setColorOverlay( 
				homeViews.iconQueue,
				homeViews.iconFeeds,
				homeViews.iconNew,
				homeViews.miniPlayer,
				homeViews.header);		
		
		homeViews.iconQueue.setOnClickListener(this);
		homeViews.iconFeeds.setOnClickListener(this);
		homeViews.iconNew.setOnClickListener(this);
	}	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.subscriptions_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent activity;
		switch (item.getItemId()) {
		case R.id.new_show_item:
			showDialog(NEW_FEED);
			return true;
		case R.id.preferences_item:
			activity = new Intent();
			activity.setClass(this, Preferences.class);
			startActivity(activity);
			return true;
		}
		return false;
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		Dialog dialog = null;
		switch(id) {
		
		case NEW_FEED:
			dialog = new SubscriptionDialog(this);
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		int x = sharedPrefs.getInt("theme_color", -1);
		if(x != -1) {
			overlay = null;
			overlay = new PorterDuffColorFilter(x, PorterDuff.Mode.MULTIPLY);
			setColorOverlay(overlay,
					homeViews.iconQueue,
					homeViews.iconFeeds,
					homeViews.iconNew,
					homeViews.miniPlayer,
					homeViews.header);	
		}
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

	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.icon_feeds:
			i = new Intent(this, SubscriptionsListActivity.class);
			startActivity(i);
			break;
		case R.id.icon_queue:
			i = new Intent(this, QueueListActivity.class);
			startActivity(i);
			break;
		case R.id.icon_new:
			//implement
		}
	};
	
	static class HomeScreenViewHolder {
		TextView
			titleText,
			playerEpisodeTitle,
			playerEpisodeTime,
			iconFeedsText,
			iconNewText,
			iconQueueText;
		ImageButton
			iconNew,
			iconQueue,
			iconFeeds;
		RelativeLayout
			miniPlayer;
		FrameLayout
			header;
	}
	
	/* This is intended for changing the theme according to user preferences. Not the most
	 * versatile method right now, will have to modify if other types of views need overriding.
	 * 
	 * @author Kevin Coppock 12/14/2010
	 */
	public static void setColorOverlay(ColorFilter cf, Object...params) {
		for (Object v : params) {
			if(v.getClass() == RelativeLayout.class 
					|| v.getClass() == FrameLayout.class
					|| v.getClass() == LinearLayout.class) {
				((View)v).getBackground().setColorFilter(cf);
			} else if (v.getClass() == ImageButton.class) {
				((ImageButton)v).getDrawable().setColorFilter(cf);
			} else if (v.getClass() == ImageView.class) {
				((ImageView)v).getDrawable().setColorFilter(overlay);
			}
		}
	}
	
	//constructor assuming the default ColorFilter
	public static void setColorOverlay(Object...params) {
		if(overlay != null) {
			for (Object v : params) {
				if(v.getClass() == RelativeLayout.class 
						|| v.getClass() == FrameLayout.class
						|| v.getClass() == LinearLayout.class) {
					((View)v).getBackground().setColorFilter(overlay);
				} else if (v.getClass() == ImageButton.class) {
					((ImageButton)v).getDrawable().setColorFilter(overlay);
				} else if (v.getClass() == ImageView.class) {
					((ImageView)v).getDrawable().setColorFilter(overlay);
				}
			}
		}
	}
	
	/* This is just for purposes of simplifying the typeface change for the home screen,
	 * as many TextViews can be passed as necessary, to ease additional TextView additions
	 * to the layout.
	 * 
	 * @author Kevin Coppock 12/14/2010
	 */
	private void setTypeface(Typeface tf, TextView...params) {
		for (TextView tv : params) {
			tv.setTypeface(tf);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent i) {
		switch(requestCode) {
		case COLOR_CHANGE_ACTIVITY_RESULT:
			if(resultCode == RESULT_CANCELED) return;
			overlay = new PorterDuffColorFilter(i.getIntExtra("color", 0), PorterDuff.Mode.MULTIPLY);
			setColorOverlay(
					homeViews.iconQueue,
					homeViews.iconFeeds,
					homeViews.iconNew,
					homeViews.miniPlayer,
					homeViews.header);
		}
		
	}
}