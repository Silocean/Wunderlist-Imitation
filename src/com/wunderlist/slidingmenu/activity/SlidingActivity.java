package com.wunderlist.slidingmenu.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.wunderlist.R;
import com.wunderlist.slidingmenu.fragment.LeftFragment;
import com.wunderlist.slidingmenu.fragment.RightFragment;
import com.wunderlist.slidingmenu.fragment.MainFragment;
import com.wunderlist.slidingmenu.view.SlidingMenu;

public class SlidingActivity extends SherlockFragmentActivity {
	
	private SlidingMenu mSlidingMenu;
	private LeftFragment leftFragment;
	private RightFragment rightFragment;
	public static MainFragment mainFragment;
	private static ActionBar actionBar = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(R.drawable.wl_actionbar_appicon);
		setContentView(R.layout.main);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
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
	private void init() {
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		mSlidingMenu.setLeftView(getLayoutInflater().inflate(R.layout.left_frame, null));
		mSlidingMenu.setRightView(getLayoutInflater().inflate(R.layout.right_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(R.layout.center_frame, null));

		FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
		leftFragment = new LeftFragment();
		t.replace(R.id.left_frame, leftFragment);

		rightFragment = new RightFragment();
		t.replace(R.id.right_frame, rightFragment);

		mainFragment = new MainFragment();
		t.replace(R.id.center_frame, mainFragment);
		t.commit();
	}

	public void showLeft() {
		mSlidingMenu.showLeftView();
	}

	public void showRight() {
		mSlidingMenu.showRightView();
	}

}
