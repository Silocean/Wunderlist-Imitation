package com.wunderlist.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wunderlist.entity.Task;
import com.wunderlist.slidingmenu.fragment.MainFragment;
import com.wunderlist.tools.ClockAlarmUtil;
import com.wunderlist.tools.RawFilesUtil;
import com.wunderlist.tools.ShowNotification;

public class ClockBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Task task = (Task)intent.getSerializableExtra("task");
		ShowNotification.showNotification(context, task);
		RawFilesUtil.ring(context);
		MainFragment.clockTimes.remove(0);
		if(MainFragment.clockTimes.size() > 0) {
			ClockAlarmUtil.setClockAlarm(context, MainFragment.clockTimes.get(0));
		}
	}

}
