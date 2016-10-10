package com.trinfo.wunderlist.application;

import android.app.Application;
import cn.jpush.android.api.JPushInterface;

public class StartApplication extends Application {
	
	@Override
	public void onCreate() {
		JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
		super.onCreate();
	}

}
