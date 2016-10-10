package com.wunderlist.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.wunderlist.R;
import com.wunderlist.slidingmenu.activity.SlidingActivity;
import com.wunderlist.slidingmenu.activity.StartActivity;

/**
 * 显示通知
 * @author Silocean
 *
 */
public class ShowNotificationJPush {
	
	private static Notification notification;
	private static NotificationManager manager;
	private static PendingIntent pendingIntent;
	
	@SuppressWarnings("deprecation")
	public static void showNotification(Context context, String title, String content) {
		notification = new Notification(R.drawable.my_icon_launcher, content, System.currentTimeMillis());
		Intent intent = new Intent(context, SlidingActivity.class);
		if(MyActivityManager.getActivity("SlidingActivity") == null) {
			intent = new Intent(context, StartActivity.class);
		}
		//intent.setAction(Intent.ACTION_MAIN);
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式
		// 这里注意第四个参数一定要是FLAG_UPDATE_CURRENT，否则传过去的intent仍旧是旧值
		pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, title, content, pendingIntent);
		notification.defaults = Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(0, notification);
	}

}
