package com.wunderlist.tools;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.example.wunderlist.R;

public class RawFilesUtil {
	
	private static MediaPlayer mediaPlayer = null;
	private static AssetFileDescriptor assetFileDescriptor = null;
	
	public static void ring(Context context) {
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					mp.seekTo(0);
				}
			});
			assetFileDescriptor = context.getResources().openRawResourceFd(R.raw.wunderlist_notification_bell);
			mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
			assetFileDescriptor.close();
			mediaPlayer.prepare();
			if(mediaPlayer != null) {
				mediaPlayer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
