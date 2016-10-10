package com.wunderlist.slidingmenu.activity;

import java.io.InputStream;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.tools.StreamTool;
import com.wunderlist.tools.WebServiceRequest;

public class LoginActivity extends Activity implements OnClickListener {
	
	private EditText emailEditText;
	private EditText passwordeEditText;
	private Button loginButton;
	
	private ProgressDialog dialog;
	private Message message;
	
	private String email;
	private String password;
	
	UIHandler handler = new UIHandler();
	
	@SuppressLint("HandlerLeak")
	private class UIHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			if(msg.arg1 == 0){
				Toast.makeText(getApplicationContext(), "连接超时!", Toast.LENGTH_SHORT).show();
			} else if(msg.arg1 == 1){
				Intent intent = new Intent(getApplicationContext(), SlidingActivity.class);
				startActivity(intent);
			} else if(msg.arg1 == 2){
				Toast.makeText(getApplicationContext(), "密码不对!", Toast.LENGTH_SHORT).show();
			} else if(msg.arg1 == 3){
				Toast.makeText(getApplicationContext(), "没有此用户，请检查用户名是否正确!", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		emailEditText = (EditText)findViewById(R.id.login_email_editText);
		passwordeEditText = (EditText)findViewById(R.id.login_password_editText);
		loginButton = (Button)findViewById(R.id.login_loginbutton);
		loginButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_loginbutton: {
			if(check()){
				dialog = ProgressDialog.show(LoginActivity.this, "", "正在登录，请稍等...", true);
				new LoginThread().start();
			}
			break;
		}
		default:
			break;
		}
	}
	/*
	 * 登录线程
	 */
	private class LoginThread extends Thread {
		@Override
		public void run() {
			message = new Message();
			try {
				login(email, password);
				handler.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 登录
	 * @param email 用户邮箱
	 * @param password 用户密码
	 */
	private void login(String email, String password) throws Exception {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("CheckUser.xml");
		byte[] data = StreamTool.read(inputStream);
		String string = new String(data).replaceAll("\\$strUserCode", email).replaceAll("\\$strPwd", password);
		data = string.getBytes();
		String json = WebServiceRequest.SendPost(inputStream, data, "CheckUserResult");
		if(json==null){// 如果json数据为null
			message.arg1 = 0; //表示连接超时
		} else {// 如果json不为null，解析它，并做相应判断
			JSONObject object = new JSONObject(json);
			String str = (String) object.get("msg");
			if(str.equals("成功!")){
				message.arg1 = 1; //表示登陆成功
			} else if(str.equals("失败!密码不正确!")){
				message.arg1 = 2; //表示登陆失败，密码不对
			} else if(str.equals("失败!系统不存在此用户，请确认用户名是否正确!")){
				message.arg1 = 3; //表示登陆失败，用户名不对
			}
		}
	}
	
	/**
	 * 检查输入是否合法
	 */
	private boolean check(){
		this.email = emailEditText.getText().toString().trim();
		this.password = passwordeEditText.getText().toString().trim();
		if(email.equals("")){
			Toast.makeText(getApplicationContext(), "邮箱不能为空!", Toast.LENGTH_SHORT).show();
			return false;
		} else if(password.equals("")){
			Toast.makeText(getApplicationContext(), "密码不能为空!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

}
