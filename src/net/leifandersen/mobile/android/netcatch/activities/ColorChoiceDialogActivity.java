package net.leifandersen.mobile.android.netcatch.activities;

import net.leifandersen.mobile.android.netcatch.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class ColorChoiceDialogActivity extends Activity {
	
	private int redValue, blueValue, greenValue;
	private int color;
	private static ColorChoiceViewHolder dialogViews;
	private static PorterDuffColorFilter overlay;
	private static final int ALPHA_OPAQUE = 255;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.color_choice_dialog);
		
		dialogViews = new ColorChoiceViewHolder();
		dialogViews.red = (SeekBar)findViewById(R.id.seekbar_red);
		dialogViews.blue = (SeekBar)findViewById(R.id.seekbar_blue);
		dialogViews.green = (SeekBar)findViewById(R.id.seekbar_green);
		dialogViews.icon = (View)findViewById(R.id.icon_color_example);
		dialogViews.background = (LinearLayout)findViewById(R.id.background);
		
		redValue = dialogViews.red.getProgress();
		blueValue = dialogViews.blue.getProgress();
		greenValue = dialogViews.green.getProgress();
		
		overlay = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
		
		dialogViews.red.setOnSeekBarChangeListener(seekBarListener);
		dialogViews.blue.setOnSeekBarChangeListener(seekBarListener);
		dialogViews.green.setOnSeekBarChangeListener(seekBarListener);
	}
	
	static class ColorChoiceViewHolder {
		SeekBar red, blue, green;
		View icon;
		LinearLayout background;
	}
	
	@Override
	public void onBackPressed() {
		Intent data = new Intent();
		data.putExtra("color", color);
		setResult(RESULT_OK, data);
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
			case R.id.seekbar_red:		
				redValue = progress;
				break;
			case R.id.seekbar_green:
				greenValue = progress;
				break;
			case R.id.seekbar_blue:
				blueValue = progress;
				break;
			}	
			color = Color.argb(ALPHA_OPAQUE, redValue, greenValue, blueValue);
			dialogViews.icon.setBackgroundColor(color);
			Log.d("COLORPARSE", "R: " + redValue + " G: " + greenValue + " B: " + blueValue);
		}
	};
}
