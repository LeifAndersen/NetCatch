package net.leifandersen.mobile.android.netcatch;

import net.leifandersen.mobile.android.netcatch.model.NetCatcher;
import android.app.Activity;
import android.os.Bundle;

public class NCMain extends Activity {
    /** Called when the activity is first created. */
	
	NetCatcher mNC;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mNC = new NetCatcher();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
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
}