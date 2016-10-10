package com.trinfo.wunderlist.tools;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.trinfo.wunderlist.entity.Task;

/**
 * 闹铃提醒
 * @author Silocean
 *
 */
public class ClockAlarmUtil {
	
	public static AlarmManager alarmManager = null;
	private static Date date = null;
	
	public static void setClockAlarm(Context context, Task task) {
		date = TimeConvertTool.getClockTime(task.getEnddate(), task.getRemindnum(), task.getRemindtype());
		alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent("com.wunderlist.clockremind");
		intent.putExtra("task", task);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		//alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 60*1000, pendingIntent);
		alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
	}

}
