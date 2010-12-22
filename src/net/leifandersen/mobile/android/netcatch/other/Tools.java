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
package net.leifandersen.mobile.android.netcatch.other;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Tools {
	public static boolean checkNetworkState(Context context, boolean backgroundUpdate) {
		// Get the connectivity manager
		ConnectivityManager manager = (ConnectivityManager)
		context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		// If user has set not to do background updates, 
		// And it's a background update, don't get it.
		if (!manager.getBackgroundDataSetting() && backgroundUpdate)
			return false;
		
		// If network is not available, bail
		NetworkInfo netInfo = manager.getActiveNetworkInfo();
		if(netInfo == null || netInfo.getState() != NetworkInfo.State.CONNECTED)
			return false;
		
		return true;
	}
}
