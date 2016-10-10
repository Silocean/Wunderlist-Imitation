package com.wunderlist.slidingmenu.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.wunderlist.R;
import com.wunderlist.entity.CommonUser;
import com.wunderlist.entity.User;
import com.wunderlist.slidingmenu.fragment.LeftFragment;
import com.wunderlist.slidingmenu.fragment.MainFragment;
import com.wunderlist.slidingmenu.fragment.RightFragment;
import com.wunderlist.slidingmenu.view.SlidingMenu;
import com.wunderlist.sqlite.SQLiteService;
import com.wunderlist.tools.MyActivityManager;

/**
 * 主窗体，包含三个fragment
 * @author Silocean
 *
 */
public class SlidingActivity extends SherlockFragmentActivity {
	
	private SlidingMenu mSlidingMenu;
	private LeftFragment leftFragment;
	private RightFragment rightFragment;
	public static MainFragment mainFragment;
	private static ActionBar actionBar = null;
	
	public static Menu menu = null;
	private MenuItem item = null;
	
	public static boolean isRefreshing = true;
	
	private long waitTime = 2000;
	private long touchTime = 0 ;
	private long currentTime = 0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		MyActivityManager.addActivity("SlidingActivity", this);
		this.initCommonData();
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.wl_actionbar_appicon);
		setContentView(R.layout.main);
		init();
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
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		item = menu.add(0, 0, 0, "接收中...");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		SlidingActivity.menu = menu;
		if(isRefreshing) {
			item.setTitle("接收中...");
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			item.setTitle("");
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU) { // 屏蔽菜单键
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			showLeft();
			break;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 设置是否处于正在刷新数据状态
	 * @param isRefreshing
	 */
	public static void setIsRefreshing(boolean isRefreshing) {
		SlidingActivity.isRefreshing = isRefreshing;
	}
	
	/**
	 * 设置标题栏标题
	 * @param title
	 */
	public static void setBarTitle(String title) {
		actionBar.setTitle(title);
	}
	
	/**
	 * 获取标题栏标题
	 * @return
	 */
	public static String getBarTitle() {
		return actionBar.getTitle().toString();
	}
	
	/**
	 * 初始化三个fragment
	 */
	private void init() {
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		mSlidingMenu.setLeftView(getLayoutInflater().inflate(R.layout.left_frame, null));
		mSlidingMenu.setRightView(getLayoutInflater().inflate(R.layout.right_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(R.layout.center_frame, null));

		FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
		mainFragment = new MainFragment(this);
		t.replace(R.id.center_frame, mainFragment);
		
		leftFragment = new LeftFragment(mainFragment);
		t.replace(R.id.left_frame, leftFragment);

		rightFragment = new RightFragment();
		t.replace(R.id.right_frame, rightFragment);

		t.commit();
	}

	/**
	 * 显示左边的fragment
	 */
	public void showLeft() {
		mSlidingMenu.showLeftView();
	}

	/**
	 * 显示右边的fragment
	 */
	public void showRight() {
		mSlidingMenu.showRightView();
	}
	
	@Override
	public void onBackPressed() {
		currentTime = System.currentTimeMillis();
		if(currentTime-touchTime >= waitTime) {
			Toast.makeText(getApplicationContext(), "再按一次返回键退出", Toast.LENGTH_SHORT).show();
			touchTime = currentTime;
		} else {
			// 返回桌面，并不是真正的退出
			/*Intent i = new Intent(Intent.ACTION_MAIN);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.addCategory(Intent.CATEGORY_HOME);
			startActivity(i);*/
			super.onBackPressed();
		}
	}
	
}
