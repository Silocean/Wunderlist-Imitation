package com.trinfo.wunderlist.activity;

import android.os.Bundle;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.trinfo.wunderlist.R;

public class InboxActivity extends ActionbarBaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_inbox);
		super.setTitle("收件箱");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, 0, 0, "新邮件");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
