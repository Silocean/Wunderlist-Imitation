package com.wunderlist.slidingmenu.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.wunderlist.R;

public class SettingsActivity extends ActionbarBaseActivity implements OnClickListener{
	
	private RelativeLayout settingsNotif = null;
	private RelativeLayout settingsAccount = null;
	private RelativeLayout settingsLogoff = null;
	private RelativeLayout settingsBg = null;
	private RelativeLayout settingsSound = null;
	private RelativeLayout settingsAbout = null;
	private RelativeLayout settingsFeedback = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("设置");
		setContentView(R.layout.activity_settings);
		settingsNotif = (RelativeLayout)findViewById(R.id.settings_notification);
		settingsNotif.setOnClickListener(this);
		settingsAccount = (RelativeLayout)findViewById(R.id.settings_accountDetails);
		settingsAccount.setOnClickListener(this);
		settingsLogoff = (RelativeLayout)findViewById(R.id.settings_logoff);
		settingsLogoff.setOnClickListener(this);
		settingsBg = (RelativeLayout)findViewById(R.id.settings_bg);
		settingsBg.setOnClickListener(this);
		settingsSound = (RelativeLayout)findViewById(R.id.settings_sound);
		settingsSound.setOnClickListener(this);
		settingsAbout = (RelativeLayout)findViewById(R.id.settings_about);
		settingsAbout.setOnClickListener(this);
		settingsFeedback = (RelativeLayout)findViewById(R.id.settings_feedback);
		settingsFeedback.setOnClickListener(this);
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
			break;
		}
		case R.id.settings_bg: {
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

	

}
