package com.wunderlist.tools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ClockAlarmUtil {
	
	public static AlarmManager alarmManager = null;
	
	public static void setClockAlarm(Context context, long time) {
		alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent("com.wunderlist.clockremind");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 60*1000, pendingIntent);
		alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
	}

}
