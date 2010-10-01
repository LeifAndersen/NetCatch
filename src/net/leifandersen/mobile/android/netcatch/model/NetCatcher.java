package net.leifandersen.mobile.android.netcatch.model;

import java.util.ArrayList;
import java.util.List;

public class NetCatcher {

	private ShowPlayer player;
	private List<Episode> showQue;
	private List<Show> subscribedShows;
	private List<Episode> newEpisodes;
	
	public NetCatcher() {
		player = new ShowPlayer();
		showQue = null; // TODO, retrieve this from a database.
		subscribedShows = null; // TODO, retrieve subscriptions from database.
		newEpisodes = new ArrayList<Episode>();
		
		// Update the feeds, add the new episodes to the list.
		for (Show s : subscribedShows) {
			newEpisodes.addAll(s.updateShow());
		}
	}
}
