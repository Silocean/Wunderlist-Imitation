package com.wunderlist.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * sharedPreferee管理
 * @author Silocean
 *
 */
public class MySharedPreferences {
	
	private static final String PREFERRENCES_NAME = "sharedPreferences";
	
	public static final String BGID = "bgId";
	
	private static SharedPreferences preferences;
	
	private static Editor editor;
	
	public static SharedPreferences getPreferences(Context context) {
		if(preferences == null) {
			preferences = context.getSharedPreferences(PREFERRENCES_NAME, Context.MODE_PRIVATE);
		}
		return preferences;
	}
	
	public static Editor getEditor(SharedPreferences preferences) {
		if(preferences != null) {
			if(editor == null) {
				editor = preferences.edit();
			}
		}
		return editor;
	}

}
