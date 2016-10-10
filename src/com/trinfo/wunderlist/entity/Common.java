package com.trinfo.wunderlist.entity;

import android.content.Context;
import android.widget.Toast;

import com.trinfo.wunderlist.R;

/**
 * 全局变量类，用于保存程序中要用的变量
 * @author SIYUNFEI
 * 
 */
public class Common {
	
	/**webservice地址**/
	public static final String WEBSERVICEPATH = "http://115.28.223.152/xmailwebservice/taskservice.asmx";
	
	/**email正则表达式**/
	public static final String EMAILREGEX = "[\\w[.-]]+@[\\w[.-]]+\\.[\\w]+";
	
	/**主界面背景图片ID**/
	public static final Integer[] BGS = { R.drawable.wl_background0,
		R.drawable.wl_background1, R.drawable.wl_background2,
		R.drawable.wl_background3, R.drawable.wl_background4,
		R.drawable.wl_background5, R.drawable.wl_background6,
		R.drawable.wl_background7, R.drawable.wl_background8,
		R.drawable.wl_background9, R.drawable.wl_background10,
		R.drawable.wl_background11, R.drawable.wl_background12,
		R.drawable.wl_background13, R.drawable.wl_background14,
		R.drawable.wl_background15, R.drawable.wl_background16,
		R.drawable.wl_background17, R.drawable.wl_background18,
		R.drawable.wl_background19, R.drawable.wl_background20,
		R.drawable.wl_background21, R.drawable.wl_background22 };
	
	/**
	 * 提示网络不可用
	 * @param context
	 */
	public static void ToastIfNetworkIsNotAvailable(Context context) {
		Toast.makeText(context, "当前网络不可用，请检查你的网络设置", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 提示网络连接问题
	 * @param context
	 */
	public static void ToastIfNetworkProblem(Context context) {
		Toast.makeText(context, "网络连接出现问题", Toast.LENGTH_SHORT).show();
	}
	
}
