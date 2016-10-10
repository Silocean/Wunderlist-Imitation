package com.wunderlist.tools;

import com.example.wunderlist.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ShowNotification {
	
	private static Notification notification;
	private static NotificationManager manager;
	private static PendingIntent pendingIntent;
	
	@SuppressWarnings("deprecation")
	public static void showNotification(Context context, String subject) {
		notification = new Notification(R.drawable.wl_icon_statusbar, subject, System.currentTimeMillis());
		Intent intent = new Intent();
		// 这里注意第四个参数一定要是FLAG_UPDATE_CURRENT，否则传过去的intent仍旧是旧值
		pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, "提醒", subject, pendingIntent);
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(100, notification);
	}

}
