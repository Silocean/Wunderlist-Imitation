package com.wunderlist.slidingmenu.activity;

import com.example.wunderlist.R;
import com.wunderlist.tools.MyActivityManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 登录和注册界面
 * @author Silocean
 *
 */
public class RegisterAndLoginActivity extends Activity implements OnClickListener{
	
	private Button registerButton;
	private Button loginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.addActivity("RegisterAndLoginActivity", this);
		setContentView(R.layout.activity_registerandlogin);
		registerButton = (Button)findViewById(R.id.walkthrough_registerbutton);
		registerButton.setOnClickListener(this);
		loginButton = (Button)findViewById(R.id.walkthrough_loginbutton);
		loginButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.walkthrough_registerbutton: {
			break;
		}
		case R.id.walkthrough_loginbutton: {
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(intent);
			break;
		}
		default:
			break;
		}
	}
	
}
