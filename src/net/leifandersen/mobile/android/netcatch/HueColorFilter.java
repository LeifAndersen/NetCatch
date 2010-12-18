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
package net.leifandersen.mobile.android.netcatch;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class HueColorFilter extends ColorFilter {
	
	private static float hsv[];
	private static float hue;
	public HueColorFilter() {
		
	}
	
	public static Drawable shiftHueAngle(BitmapDrawable drawable, float angle) {
		Bitmap bmp = drawable.getBitmap();
		int pixels = bmp.getWidth() * bmp.getHeight();
		int allPixels[] = new int[pixels];
		int newPixels[] = new int[pixels];
		bmp.getPixels(allPixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
		for (int pos = 0; pos < pixels; pos++) {
			Color.RGBToHSV(allPixels[pos] & 255, (allPixels[pos] >> 8) & 255, (allPixels[pos] >> 16) & 255, hsv);
			hue = hsv[0];
			if ((hue + angle) > 360) {
				hue = (hue + angle) % 360;
			} else hue = hue + angle;
			newPixels[pos] = Color.HSVToColor(new float[] { hue, hsv[1], hsv[2] } );
		}
		bmp.setPixels(newPixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
		return new BitmapDrawable(bmp);		
	}
}
