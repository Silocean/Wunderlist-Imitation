package com.wunderlist.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wunderlist.slidingmenu.fragment.MainFragment;
import com.wunderlist.tools.ClockAlarmUtil;
import com.wunderlist.tools.ShowNotification;

public class ClockBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ShowNotification.showNotification(context, "你有新的任务提醒哟");
		MainFragment.clockTimes.remove(0);
		if(MainFragment.clockTimes.size() > 0) {
			ClockAlarmUtil.setClockAlarm(context, MainFragment.clockTimes.get(0).getTime());
		}
	}

}
