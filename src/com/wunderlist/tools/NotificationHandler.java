package com.wunderlist.tools;

import com.wunderlist.slidingmenu.fragment.MainFragment;

import android.os.Handler;
import android.os.Message;

public class NotificationHandler extends Handler {
	
	MainFragment fragment = null;
	
	public NotificationHandler(MainFragment fragment) {
		this.fragment = fragment;
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			fragment.updateTaskBoxList();
			break;
		default:
			break;
		}
	}
}
