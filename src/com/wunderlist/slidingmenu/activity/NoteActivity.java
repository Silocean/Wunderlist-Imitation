package com.wunderlist.slidingmenu.activity;

import com.example.wunderlist.R;

import android.os.Bundle;
import android.widget.EditText;

public class NoteActivity extends ActionbarBaseActivity {
	
	private EditText noteEditText;
	
	private String barTitle = null;
	private String note = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_note);
		noteEditText = (EditText)this.findViewById(R.id.note_editText);
		this.initData();
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * 填充界面数据
	 */
	private void initData() {
		note = getIntent().getStringExtra("taskDisc");
		barTitle = getIntent().getStringExtra("title");
		super.setTitle(barTitle);
		noteEditText.setText(note);
	}

}
