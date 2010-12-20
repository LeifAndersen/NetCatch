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

import java.util.ArrayList;
import java.util.List;

import net.leifandersen.mobile.android.netcatch.R;
import net.leifandersen.mobile.android.netcatch.providers.Show;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TexturedListAdapter extends BaseAdapter {
	private List<Show> items = new ArrayList<Show>();
	private Context context;
	final Typeface vera, veraBold;
	
	public int getCount() {
		return items.size();
	}
	
	public long getItemId(int position) {
		return position;
	}
	
	public Show getItem(int position) {
		return items.get(position);
	}
	
	public TexturedListAdapter(Context context, ArrayList<Show> shows) {
		vera = Typeface.createFromAsset(context.getAssets(), "Vera.ttf");
		veraBold = Typeface.createFromAsset(context.getAssets(), "VeraBd.ttf");
		this.items = shows;
		this.context = context;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.main_menu_list_item_textured, null);
		}
		Show s = items.get(position);
		if (s != null) {
			
			TextView title = (TextView)v.findViewById(R.id.list_feed_title);
			TextView counts = (TextView)v.findViewById(R.id.list_feed_counts);
			TextView updateDate = (TextView)v.findViewById(R.id.list_feed_update_time);
			ImageView art = (ImageView)v.findViewById(R.id.list_album_art);
			
				//placeholders until Show class description is finalized
				title.setText(s.getTitle());
				title.setTypeface(veraBold);
				
				counts.setText(s.getDescription());
				counts.setTypeface(vera);
				
				updateDate.setText(s.getFeed());
				updateDate.setTypeface(vera);
			
			if(s.getImage() == null) art.setImageResource(R.drawable.image_album_background);
			else art.setImageDrawable(s.getImage());
		}
		return v;
	}
}
