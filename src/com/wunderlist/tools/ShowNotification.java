package com.wunderlist.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.wunderlist.R;
import com.wunderlist.entity.Common;
import com.wunderlist.entity.Task;
import com.wunderlist.slidingmenu.activity.SlidingActivity;

public class ShowNotification {
	
	private static Notification notification;
	private static NotificationManager manager;
	private static PendingIntent pendingIntent;
	
	@SuppressWarnings("deprecation")
	public static void showNotification(Context context, Task task) {
		notification = new Notification(R.drawable.wl_icon_statusbar, "你有新的任务提醒", System.currentTimeMillis());
		Intent intent = new Intent();
		if(Common.isBack) {
			intent.setClass(context, SlidingActivity.class);
		}
		// 这里注意第四个参数一定要是FLAG_UPDATE_CURRENT，否则传过去的intent仍旧是旧值
		pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, "提醒", task.getSubject(), pendingIntent);
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(0, notification);
	}

}
