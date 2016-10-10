package com.wunderlist.slidingmenu.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wunderlist.R;
import com.wunderlist.entity.Common;
import com.wunderlist.entity.CommonUser;
import com.wunderlist.sqlite.SQLiteService;
import com.wunderlist.tools.MyActivityManager;
import com.wunderlist.tools.MySharedPreferences;

/**
 * 设置界面
 * @author Silocean
 *
 */
public class SettingsActivity extends ActionbarBaseActivity implements
		OnClickListener {

	private RelativeLayout settingsNotif = null;
	private RelativeLayout settingsAccount = null;
	private RelativeLayout settingsLogoff = null;
	private RelativeLayout settingsBg = null;
	private RelativeLayout settingsSound = null;
	private RelativeLayout settingsAbout = null;
	private RelativeLayout settingsFeedback = null;

	private ImageView settingsImageicon = null;
	
	private TextView usernameTextView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.addActivity("SettingsActivity", this);
		getSupportActionBar().setTitle("设置");
		setContentView(R.layout.activity_settings);
		settingsNotif = (RelativeLayout) findViewById(R.id.settings_notification);
		settingsNotif.setOnClickListener(this);
		settingsAccount = (RelativeLayout) findViewById(R.id.settings_accountDetails);
		settingsAccount.setOnClickListener(this);
		settingsLogoff = (RelativeLayout) findViewById(R.id.settings_logoff);
		settingsLogoff.setOnClickListener(this);
		settingsBg = (RelativeLayout) findViewById(R.id.settings_bg);
		settingsBg.setOnClickListener(this);
		settingsSound = (RelativeLayout) findViewById(R.id.settings_sound);
		settingsSound.setOnClickListener(this);
		settingsAbout = (RelativeLayout) findViewById(R.id.settings_about);
		settingsAbout.setOnClickListener(this);
		settingsFeedback = (RelativeLayout) findViewById(R.id.settings_feedback);
		settingsFeedback.setOnClickListener(this);
		settingsImageicon = (ImageView) findViewById(R.id.settings_imageicon);
		this.getPreferences();
		this.initData();
	}

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		usernameTextView = (TextView)findViewById(R.id.settings_name);
		usernameTextView.setText(CommonUser.USERNAME);
	}

	/**
	 * 获取sharedPreference
	 */
	private void getPreferences() {
		SharedPreferences preferences = MySharedPreferences
				.getPreferences(getApplicationContext());
		int bgId = preferences.getInt(MySharedPreferences.BGID, 0);
		settingsImageicon.setBackgroundResource(Common.BGS[bgId]);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.settings_notification: {
			break;
		}
		case R.id.settings_accountDetails: {
			break;
		}
		case R.id.settings_logoff: {
			logoff();
			break;
		}
		case R.id.settings_bg: {
			Intent intent = new Intent(getApplicationContext(),
					ChooseBgActivity.class);
			startActivityForResult(intent, 1);
			break;
		}
		case R.id.settings_sound: {
			break;
		}
		case R.id.settings_about: {
			break;
		}
		case R.id.settings_feedback: {
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 注销
	 */
	private void logoff() {
		new AlertDialog.Builder(SettingsActivity.this).setTitle("注销")
				.setMessage("你确定要注销吗?")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						SQLiteService service = new SQLiteService(getApplicationContext());
						service.deleteUserInfo();
						for (String name : MyActivityManager.activities.keySet()) {
							if(MyActivityManager.getActivity(name) != null) {
								MyActivityManager.getActivity(name).finish();
							}
						}
						Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
						startActivity(intent);
					}
				}).setNegativeButton("取消", null).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 1:
			int bgId = data.getIntExtra("bgId", 0);
			settingsImageicon.setBackgroundResource(Common.BGS[bgId]);
			break;

		default:
			break;
		}
	}

}
