package com.trinfo.wunderlist.tools;

import android.os.Handler;
import android.os.Message;

import com.trinfo.wunderlist.fragment.MainFragment;

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
