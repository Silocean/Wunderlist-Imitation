package com.trinfo.wunderlist.activity;

import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;
import com.trinfo.wunderlist.R;
import com.trinfo.wunderlist.entity.CommonUser;
import com.trinfo.wunderlist.entity.User;
import com.trinfo.wunderlist.fragment.LeftFragment;
import com.trinfo.wunderlist.fragment.MainFragment;
import com.trinfo.wunderlist.sqlite.SQLiteService;
import com.trinfo.wunderlist.tools.MyActivityManager;

/**
 * 主activity
 * @author Silocean
 *
 */
public class MainActivity extends SlidingActivity {
	
	SlidingMenu sm = null;
	
	private static ActionBar actionBar = null;

	public static Menu menu = null;
	private MenuItem item = null;

	public static boolean isRefreshing = true;

	private long waitTime = 2000;
	private long touchTime = 0;
	private long currentTime = 0;
	
	public static boolean isForeground = false;
	
	MainFragment mainFragment = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MyActivityManager.addActivity("SlidingActivity", this);
		this.initCommonData();
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.wl_actionbar_appicon);
		
		setContentView(R.layout.fragment_main);
		
		setBehindContentView(R.layout.fragment_left);
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		
		mainFragment = new MainFragment(this);
		fragmentTransaction.replace(R.id.main, mainFragment);
		
		LeftFragment leftFragment = new LeftFragment();
		fragmentTransaction.replace(R.id.left, leftFragment);
		
		fragmentTransaction.commit();
		
		sm = getSlidingMenu();
		
		sm.setShadowWidth(50);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffset(160);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		mainFragment.updateTaskBoxList();
		super.onNewIntent(intent);
	}
	
	/**
	 * 初始化Common类中的静态变量
	 */
	private void initCommonData() {
		SQLiteService service = new SQLiteService(getApplicationContext());
		User user = service.getUserInfo();
		CommonUser.USERID = user.getUserSID();
		CommonUser.USEREMAIL = user.getUserEmail();
		CommonUser.USERNAME = user.getUserName();
		CommonUser.UERPASSWORD = user.getUserPassword();
		JPushInterface.setAlias(getApplicationContext(), CommonUser.USERID.replaceAll("-", "_"),
				new TagAliasCallback() {
					public void gotResult(int responseCode, String alias,
							Set<String> tags) {
						System.out.println(responseCode+"=="+alias+"="+tags);
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		item = menu.add(0, 0, 0, "接收中...");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MainActivity.menu = menu;
		if (isRefreshing) {
			item.setTitle("接收中...");
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			item.setTitle("");
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) { // 屏蔽菜单键
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			sm.showMenu();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 设置是否处于正在刷新数据状态
	 * 
	 * @param isRefreshing
	 */
	public static void setIsRefreshing(boolean isRefreshing) {
		MainActivity.isRefreshing = isRefreshing;
	}

	/**
	 * 设置标题栏标题
	 * 
	 * @param title
	 */
	public static void setBarTitle(String title) {
		actionBar.setTitle(title);
	}

	/**
	 * 获取标题栏标题
	 * 
	 * @return
	 */
	public static String getBarTitle() {
		return actionBar.getTitle().toString();
	}
	
	@Override
	public void onBackPressed() {
		currentTime = System.currentTimeMillis();
		if (currentTime - touchTime >= waitTime) {
			Toast.makeText(getApplicationContext(), "再按一次返回键退出",
					Toast.LENGTH_SHORT).show();
			touchTime = currentTime;
		} else {
			// 返回桌面，并不是真正的退出
			/*
			 * Intent i = new Intent(Intent.ACTION_MAIN);
			 * i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * i.addCategory(Intent.CATEGORY_HOME); startActivity(i);
			 */
			for (Map.Entry<String, Activity> element : MyActivityManager.activities.entrySet()) {
				if(element.getValue() != null) {
					element.getValue().finish();
				}
			}
			MyActivityManager.activities.clear();
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		isForeground = true;
		JPushInterface.onResume(this);
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		isForeground = false;
		JPushInterface.onPause(this);
	}

}
