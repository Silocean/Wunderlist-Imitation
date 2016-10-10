package com.wunderlist.slidingmenu.activity;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.entity.Task;

public class TaskDetailsActivity extends ActionbarBaseActivity implements
		OnClickListener {

	private ImageView checkboxImageView;
	private EditText titleEditText;
	private RelativeLayout deadlineRelativeLayout;
	private RelativeLayout clockRelativeLayout;
	private EditText noteEditText;

	private RelativeLayout bottomRelativeLayout;

	private Task task = null;
	private String barTitle = null;
	private String enddate = null;

	private Calendar calendar = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_task_details);
		checkboxImageView = (ImageView) findViewById(R.id.taskdetails_checkbox);
		checkboxImageView.setOnClickListener(this);
		titleEditText = (EditText) findViewById(R.id.taskdetails_title);
		titleEditText.setOnClickListener(this);
		deadlineRelativeLayout = (RelativeLayout) findViewById(R.id.taskdetais_deadline);
		deadlineRelativeLayout.setOnClickListener(this);
		clockRelativeLayout = (RelativeLayout) findViewById(R.id.taskdetais_clock);
		clockRelativeLayout.setOnClickListener(this);
		noteEditText = (EditText) findViewById(R.id.taskdetails_note);
		noteEditText.setOnClickListener(this);
		bottomRelativeLayout = (RelativeLayout) findViewById(R.id.taskdetails_bottomLayout);
		this.initData();
		super.onCreate(savedInstanceState);
	}

	/**
	 * 填充界面数据
	 */
	private void initData() {
		task = (Task) getIntent().getSerializableExtra("task");
		barTitle = getIntent().getStringExtra("title");
		super.setTitle(barTitle);
		titleEditText.setText(task.getSubject());
		noteEditText.setText(task.getDisc());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.taskdetails_checkbox: {
			break;
		}
		case R.id.taskdetails_title: {
			break;
		}
		case R.id.taskdetais_deadline: {
			DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int month,
						int day) {
					Toast.makeText(getApplicationContext(),
							year + "年" + (month + 1) + "月" + day + "日", 0)
							.show();
				}
			};
			new DatePickerDialog(TaskDetailsActivity.this, dateListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH)).show(); // 这里不要用getApplicationContext()
			break;
		}
		case R.id.taskdetais_clock: {
			TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker view, int hour, int minute) {
					Toast.makeText(getApplicationContext(), hour+"小时"+minute+"分钟", 0).show();
				}
			};
			new TimePickerDialog(TaskDetailsActivity.this, timeListener,
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), true).show();
			break;
		}
		case R.id.taskdetails_note: {
			Intent intent = new Intent(getApplicationContext(),
					NoteActivity.class);
			intent.putExtra("taskDisc", task.getDisc());
			intent.putExtra("title", barTitle);
			startActivity(intent);
			break;
		}
		default:
			break;
		}
	}

}
