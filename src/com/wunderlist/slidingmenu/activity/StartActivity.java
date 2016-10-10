package com.wunderlist.slidingmenu.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.wunderlist.R;
import com.wunderlist.entity.CommonUser;
import com.wunderlist.entity.User;
import com.wunderlist.sqlite.SQLiteService;
import com.wunderlist.tools.MyActivityManager;

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
			CommonUser.USERID = user.getUserSID();
			CommonUser.USEREMAIL = user.getUserEmail();
			CommonUser.UERPASSWORD = user.getUserPassword();
			Intent intent = new Intent(getApplicationContext(), SlidingActivity.class);
			startActivity(intent);
		}
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
	
}
