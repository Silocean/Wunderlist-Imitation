package com.trinfo.wunderlist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.trinfo.wunderlist.R;
import com.trinfo.wunderlist.tools.MyActivityManager;

/**
 * 笔记界面
 * @author Silocean
 *
 */
public class NoteActivity extends ActionbarBaseActivity {
	
	private EditText noteEditText;
	
	private String barTitle = null;
	private String note = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_note);
		MyActivityManager.addActivity("NoteActivity", this);
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
	
	@Override
	public void finish() {
		Intent intent = new Intent();
		intent.putExtra("note", noteEditText.getText().toString().trim());
		setResult(2, intent);
		super.finish();
	}

}
