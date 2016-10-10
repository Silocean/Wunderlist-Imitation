package com.wunderlist.slidingmenu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.example.wunderlist.R;

public class TaskDetailsActivity extends ActionbarBaseActivity implements OnClickListener {
	
	private EditText noteEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getSupportActionBar().setTitle("分组标题");
		setContentView(R.layout.activity_task_details);
		noteEditText = (EditText)findViewById(R.id.taskdetails_note);
		noteEditText.setOnClickListener(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.taskdetails_note: {
			Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
			startActivity(intent);
			break;
		}
			

		default:
			break;
		}
	}

}
