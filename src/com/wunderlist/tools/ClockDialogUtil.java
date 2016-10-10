package com.wunderlist.tools;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.wunderlist.R;
import com.wunderlist.slidingmenu.activity.TaskDetailsActivity;

/**
 * 提醒设置对话框
 * @author Silocean
 *
 */
public class ClockDialogUtil {

	private TaskDetailsActivity activity;
	private LayoutInflater inflater;
	private RelativeLayout clockLayout;

	private Spinner spinnerRemindnum;
	private Spinner spinnerRemindtype;

	private int remindtype = 0;
	private int remindnum = 0;
	
	private ArrayAdapter<CharSequence> remindnumAdapter;
	private ArrayAdapter<CharSequence> remindtypeAdapter;

	public ClockDialogUtil(TaskDetailsActivity activity, String remindnum, String remindtype) {
		this.activity = activity;
		this.inflater = (LayoutInflater) activity.getLayoutInflater();
		if(remindnum.equals("")) {
			this.remindnum = 1;
		} else if(remindtype.equals("")) {
			this.remindtype = 0;
		} else {
			this.remindnum = Integer.parseInt(remindnum);
			this.remindtype = Integer.parseInt(remindtype);
		}
		this.initView();
	}

	private void initView() {
		clockLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_clock, null);
		spinnerRemindnum = (Spinner) clockLayout.findViewById(R.id.remindnumSpinner);
		spinnerRemindnum.setOnItemSelectedListener(new RemindnumListener());
		spinnerRemindtype = (Spinner) clockLayout.findViewById(R.id.remindtypeSpinner);
		spinnerRemindtype.setOnItemSelectedListener(new RemindtypeListener());
		remindnumAdapter = ArrayAdapter.createFromResource(activity, R.array.day, android.R.layout.simple_spinner_item);
		remindnumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRemindnum.setAdapter(remindnumAdapter);
		remindtypeAdapter = ArrayAdapter.createFromResource(activity, R.array.type, android.R.layout.simple_spinner_item);
		remindtypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerRemindtype.setAdapter(remindtypeAdapter);
		switch (remindtype) {
		case 0:
			spinnerRemindtype.setSelection(0, true);
			break;
		case 1:
			spinnerRemindtype.setSelection(1, true);
			break;
		case 2:
			spinnerRemindtype.setSelection(2, true);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 显示设置提醒提前量对话框
	 * @return
	 */
	public AlertDialog showClockDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(activity)
				.setView(clockLayout)
				.setTitle("设置提醒")
				.setPositiveButton("保存", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//Toast.makeText(activity, spinnerRemindnum.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
						String tempremindnum = spinnerRemindnum.getSelectedItem().toString();
						String tempremindtype = spinnerRemindtype.getSelectedItem().toString();
						if(tempremindtype.equals("天")) {
							tempremindtype = "0";
						} else if(tempremindtype.equals("小时")) {
							tempremindtype = "1";
						} else if(tempremindtype.equals("分钟")) {
							tempremindtype = "2";
						}
						remindnum = Integer.parseInt(tempremindnum);
						remindtype = Integer.parseInt(tempremindtype);
						System.out.println("设置："+remindnum);
						activity.updateClockTextView(tempremindnum, tempremindtype);
					}
				}).setNegativeButton("移除", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.updateClockTextView("", "");
					}
				}).show();
		return alertDialog;
	}
	
	private class RemindnumListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected (AdapterView<?> parent, View view, int pos, long id) {
			remindnum = pos+1;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
		
	}
	
	private class RemindtypeListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			switch (pos) {
			case 0:
				remindnumAdapter = ArrayAdapter.createFromResource(activity, R.array.day, android.R.layout.simple_spinner_item);
				remindnumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerRemindnum.setAdapter(remindnumAdapter);
				spinnerRemindnum.setSelection(remindnum-1);
				break;
			case 1:
				remindnumAdapter = ArrayAdapter.createFromResource(activity, R.array.hour, android.R.layout.simple_spinner_item);
				remindnumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerRemindnum.setAdapter(remindnumAdapter);
				spinnerRemindnum.setSelection(remindnum-1);
				break;
			case 2:
				remindnumAdapter = ArrayAdapter.createFromResource(activity, R.array.minute, android.R.layout.simple_spinner_item);
				remindnumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerRemindnum.setAdapter(remindnumAdapter);
				spinnerRemindnum.setSelection(remindnum-1);
				break;
			default:
				break;
			}
		}
		
		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
		
	}
	
}
