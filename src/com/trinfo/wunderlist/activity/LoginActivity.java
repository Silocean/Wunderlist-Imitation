package com.trinfo.wunderlist.activity;

import java.io.InputStream;

import org.json.JSONArray;
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
import cn.jpush.android.api.JPushInterface;

import com.trinfo.wunderlist.R;
import com.trinfo.wunderlist.entity.Common;
import com.trinfo.wunderlist.entity.CommonUser;
import com.trinfo.wunderlist.entity.User;
import com.trinfo.wunderlist.sqlite.SQLiteService;
import com.trinfo.wunderlist.tools.CheckNetwork;
import com.trinfo.wunderlist.tools.MyActivityManager;
import com.trinfo.wunderlist.tools.StreamTool;
import com.trinfo.wunderlist.tools.WebServiceRequest;

/**
 * 登录界面
 * @author Silocean
 *
 */
public class LoginActivity extends Activity implements OnClickListener {
	
	private static final int MSG_CONNECT_TIMEOUT = 0;
	private static final int MSG_WRONG_ACCOUNT = 1;
	private static final int MSG_WRONG_PASSWORD = 2;
	private static final int MSG_SUCCESS_LOGIN = 3;
	private static final int MSG_WRONG_PATH = 4;
	
	private EditText emailEditText;
	private EditText passwordeEditText;
	private Button loginButton;
	
	private ProgressDialog dialog;
	private Message message;
	
	private String email;
	private String password;
	
	UIHandler handler = new UIHandler();
	
	@SuppressLint("HandlerLeak")
	private class UIHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			if(msg.arg1 == MSG_CONNECT_TIMEOUT){
				Toast.makeText(getApplicationContext(), "连接超时!", Toast.LENGTH_SHORT).show();
			} else if(msg.arg1 == MSG_SUCCESS_LOGIN){
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
			} else if(msg.arg1 == MSG_WRONG_PASSWORD){
				Toast.makeText(getApplicationContext(), "密码不对!", Toast.LENGTH_SHORT).show();
			} else if(msg.arg1 == MSG_WRONG_ACCOUNT){
				Toast.makeText(getApplicationContext(), "没有此用户，请检查用户名是否正确!", Toast.LENGTH_SHORT).show();
			} else if(msg.arg1 == MSG_WRONG_PATH){
				Toast.makeText(getApplicationContext(), "网络路径有问题", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyActivityManager.addActivity("LoginActivity", this);
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
			if(CheckNetwork.isNetworkAvailable(getApplicationContext())) {
				if(check()){
					dialog = ProgressDialog.show(LoginActivity.this, "", "正在登录，请稍等...", true);
					new LoginThread().start();
				}
			} else {
				Common.ToastIfNetworkIsNotAvailable(getApplicationContext());
			}
			break;
		}
		default:
			break;
		}
	}
	
	/**
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
				message.arg1 = MSG_WRONG_PATH;
				handler.sendMessage(message);
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
		if(json==null){// 如果json数据为null，表示连接超时
			message.arg1 = MSG_CONNECT_TIMEOUT;
		} else {// 如果json不为null，解析它，并做相应判断
			JSONObject object = new JSONObject(json);
			String str = (String) object.get("msg");
			if(str.equals("成功!")){
				User user = getUserInfoFromNet(email);
				SQLiteService service = new SQLiteService(getApplicationContext());
				service.deleteUserInfo();
				service.saveUserInfo(user);
				CommonUser.USERID = user.getUserSID();
				CommonUser.USEREMAIL = user.getUserEmail();
				CommonUser.UERPASSWORD = user.getUserPassword();
				message.arg1 = MSG_SUCCESS_LOGIN; //表示登陆成功
			} else if(str.equals("失败!密码不正确!")){
				message.arg1 = MSG_WRONG_PASSWORD; //表示登陆失败，密码不对
			} else if(str.equals("失败!系统不存在此用户，请确认用户名是否正确!")){
				message.arg1 = MSG_WRONG_ACCOUNT; //表示登陆失败，用户名不对
			}
		}
	}
	
	/**
	 * 从网络获取用户基本信息
	 * @param email
	 * @return
	 */
	private User getUserInfoFromNet(String email) {
		User user = null;
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("GetUserInfo.xml");
			byte[] data = StreamTool.read(inputStream);
			String string = new String(data).replaceAll("\\&strEmail", email);
			data = string.getBytes();
			String json = WebServiceRequest.SendPost(inputStream, data, "GETUSERINFOResult");
			user = parseJSON(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	
	/**
	 * 解析json字符串
	 * @param json
	 * @return
	 * @throws Exception
	 */
	private User parseJSON(String json) throws Exception {
		User user = null;
		if(json != null) {
			JSONObject object = new JSONObject(json);
			int row = Integer.parseInt(object.getString("rows"));
			if(row == 1) {
				user = new User();
				JSONArray array = new JSONArray(object.getString("Items"));
				JSONObject obj = array.getJSONObject(0);
				user.setUserSID(obj.getString("SID"));
				user.setUserEmail(obj.getString("USERCODE"));
				user.setUserName(obj.getString("USERNAME"));
				user.setUserImageUrl(obj.getString("IAMGURL"));
				user.setUserSex(obj.getString("SEX"));
				user.setUserAge(obj.getString("AGE"));
				user.setUserHobby(obj.getString("HOBBY"));
				user.setUserMemo(obj.getString("MEMO"));
				user.setUserMobile(obj.getString("MOBILE"));
			}
		}
		return user;
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
