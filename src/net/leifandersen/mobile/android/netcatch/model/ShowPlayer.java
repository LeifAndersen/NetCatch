package net.leifandersen.mobile.android.netcatch.model;

import java.util.List;

import android.content.Context;
import android.media.MediaPlayer;

public class ShowPlayer {

	MediaPlayer mPlayer;
	String episodeTitle;
	String showTitle;
	String author;
	Episode currentEpisode;
	List<Episode> showQue;
	
	public ShowPlayer(Episode episode) {
		mPlayer = new MediaPlayer();
		currentEpisode = episode;
	}
	
	public String getEpisodeTitle() {
		return episodeTitle;
	}
	
	public String getShowTitle() {
		return showTitle;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void play() {
		mPlayer.start();
	}
	
	public void pause() {
		mPlayer.pause();
	}
	
	public void nextEpisode() {
		// TODO
	}
	
	public void prevEpisode() {
		// TODO
	}
	
	public void jumpNext() {
		// TODO
	}
	
	public void jumpPrev() {
		
	}
}
