package com.trinfo.wunderlist.tools;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.trinfo.wunderlist.R;

/**
 * 提示音管理
 * @author Silocean
 *
 */
public class RawFilesUtil {
	
	private static MediaPlayer mediaPlayer = null;
	private static AssetFileDescriptor assetFileDescriptor = null;
	
	public static void ring(Context context, int tag) {
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					mp.seekTo(0);
				}
			});
			if(tag == 1) {
				assetFileDescriptor = context.getResources().openRawResourceFd(R.raw.wunderlist_notification_bell);
			} else if (tag == 2) {
				assetFileDescriptor = context.getResources().openRawResourceFd(R.raw.pencil_complete_task);
			}
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
