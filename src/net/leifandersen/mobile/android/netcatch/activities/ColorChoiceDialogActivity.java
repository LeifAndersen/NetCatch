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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class ColorChoiceDialogActivity extends Activity {
	
	private int hue, sat, val;
	private int color;
	private static ColorChoiceViewHolder dialogViews;
	private SharedPreferences sharedPrefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.color_choice_dialog);
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		dialogViews = new ColorChoiceViewHolder();
		dialogViews.hueBar = (SeekBar)findViewById(R.id.seekbar_hue);
		dialogViews.satBar = (SeekBar)findViewById(R.id.seekbar_sat);
		dialogViews.valBar = (SeekBar)findViewById(R.id.seekbar_val);
		dialogViews.icon = (View)findViewById(R.id.icon_color_example);
		dialogViews.background = (LinearLayout)findViewById(R.id.background);
		
		//See if the user has set a theme preference
		int defaultColor = sharedPrefs.getInt("theme_color", -1);		
		
		/* If there is no set theme color yet (i.e. the default is returned)
		 * set the color to that of the default slider bar positions. This 
		 * should only happen on the first run. The progress is zero-based,
		 * so 1 is added to get the actual value.
		 */
		if(defaultColor == -1) {
			hue = dialogViews.hueBar.getProgress() + 1;
			sat = dialogViews.satBar.getProgress() + 1;
			val = dialogViews.valBar.getProgress() + 1;
		} else {
			/* Set the global packed int representation of the color to that
			 * of the user's preference. Then convert to HSV. Since the sliders
			 * only carry a progress value in integers, we can't directly capture
			 * floats from them. Since the saturation and value values should be
			 * floats between 0 and 1, we simply use a 0-99 progress range for 
			 * these sliders, and either divide or multiply through by 100
			 * depending on the situation.
			 */
			color = defaultColor;
			float hsv[] = new float[3]; 
			Color.RGBToHSV(
				Color.red(color), 
				Color.green(color), 
				Color.blue(color), 
				hsv);
			hue = (int)hsv[0];
			sat = (int)(hsv[1] * 100);
			val = (int)(hsv[2] * 100);
			
			//Zero-based slider progress, take away one to compensate
			dialogViews.hueBar.setProgress(hue - 1);
			dialogViews.satBar.setProgress(sat - 1);
			dialogViews.valBar.setProgress(val - 1);
		}
		
		dialogViews.icon.setBackgroundColor(color);
		
		dialogViews.hueBar.setOnSeekBarChangeListener(seekBarListener);
		dialogViews.satBar.setOnSeekBarChangeListener(seekBarListener);
		dialogViews.valBar.setOnSeekBarChangeListener(seekBarListener);
	}
	
	static class ColorChoiceViewHolder {
		SeekBar hueBar, satBar, valBar;
		View icon;
		LinearLayout background;
	}
	
	@Override
	public void onBackPressed() {
		//Commit the changes to the preferences upon back button press.
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putInt("theme_color", color);
		editor.commit();
		finish();
	}
	
	SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			
			switch(seekBar.getId())
			{
			case R.id.seekbar_hue:		
				hue = progress;
				break;
			case R.id.seekbar_sat:
				sat = progress;
				break;
			case R.id.seekbar_val:
				val = progress;
				break;
			}	
			
			/* divide saturation and value values by 100f to get a float representation
			 * between zero and one.
			 */
			color = Color.HSVToColor(new float[] { hue, sat / 100f, val / 100f });
			
			dialogViews.icon.setBackgroundColor(color);
		}
	};
}
