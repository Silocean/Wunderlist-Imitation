package com.wunderlist.slidingmenu.activity;

import com.example.wunderlist.R;

import android.os.Bundle;

public class NoteActivity extends ActionbarBaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getSupportActionBar().setTitle("项目标题");
		setContentView(R.layout.activity_note);
		super.onCreate(savedInstanceState);
	}

}
