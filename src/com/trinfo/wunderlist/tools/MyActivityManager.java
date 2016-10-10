package com.trinfo.wunderlist.tools;

import java.util.HashMap;

import android.app.Activity;

/**
 * activity管理器
 * @author Silocean
 *
 */
public class MyActivityManager {
	
	public static HashMap<String, Activity> activities = new HashMap<String, Activity>();
	
	public static void addActivity(String name, Activity activity) {
		activities.put(name, activity);
	}
	
	public static Activity getActivity(String name) {
		return activities.get(name);
	}
	
	public static int getActivitiesSize() {
		return activities.size();
	}
	
}
