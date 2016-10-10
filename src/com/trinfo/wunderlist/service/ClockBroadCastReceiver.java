package com.trinfo.wunderlist.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.trinfo.wunderlist.entity.Task;
import com.trinfo.wunderlist.fragment.MainFragment;
import com.trinfo.wunderlist.tools.ClockAlarmUtil;
import com.trinfo.wunderlist.tools.RawFilesUtil;
import com.trinfo.wunderlist.tools.ShowNotification;

/**
 * 广播接收者，用于接收提醒广播
 * @author Silocean
 *
 */
public class ClockBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Task task = (Task)intent.getSerializableExtra("task");
		ShowNotification.showNotification(context, task);
		RawFilesUtil.ring(context, 1);
		MainFragment.clockTimes.remove(0);
		if(MainFragment.clockTimes.size() > 0) {
			ClockAlarmUtil.setClockAlarm(context, MainFragment.clockTimes.get(0));
		}
	}

}
