package com.trinfo.wunderlist.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.jpush.android.api.JPushInterface;

import com.trinfo.wunderlist.R;
import com.trinfo.wunderlist.entity.User;
import com.trinfo.wunderlist.sqlite.SQLiteService;
import com.trinfo.wunderlist.tools.MyActivityManager;

/**
 * 程序开始界面
 * @author Silocean
 *
 */
public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_start);
		MyActivityManager.addActivity("StartActivity", this);
		Message msg = new Message();
		msg.what = 1;
		handler.sendMessageDelayed(msg, 1500);
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * 检查该用户是否登陆过
	 * @param email
	 * @return
	 */
	private void checkIsHaveLogin() {
		SQLiteService service = new SQLiteService(getApplicationContext());
		User user = service.getUserInfo();
		if(user == null) {
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(intent);
		}
		finish();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				checkIsHaveLogin();
				break;
			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}
	
}
