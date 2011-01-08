package net.leifandersen.mobile.android.netcatch.other;

import android.app.Application;
import android.graphics.Typeface;

public class GlobalVars extends Application {
	private Typeface vera;
	private Typeface veraBold;
	private Typeface veraItalic;
	private Typeface veraBoldItalic;
	
	public void initializeGlobalVars() {
		vera = Typeface.createFromAsset(getAssets(), "Vera.ttf");
		veraBold = Typeface.createFromAsset(getAssets(), "VeraBd.ttf");
		veraItalic = Typeface.createFromAsset(getAssets(), "VeraIt.ttf");
		veraBoldItalic = Typeface.createFromAsset(getAssets(), "VeraBI.ttf");
	}
	
	public Typeface getVera() {
		return vera;
	}
	
	public Typeface getVeraBold() {
		return veraBold;
	}
	
	public Typeface getVeraItalic() {
		return veraItalic;
	}
	
	public Typeface getVeraBoldItalic() {
		return veraBoldItalic;
	}
}
