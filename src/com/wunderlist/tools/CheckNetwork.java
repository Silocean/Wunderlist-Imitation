package com.wunderlist.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 检查网络情况
 * @author Silocean
 *
 */
public class CheckNetwork {
	
	/**
	 * 检查设备是否联网
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if(info != null && info.isAvailable()) {
			return true;
		}
		return false;
	}

}
