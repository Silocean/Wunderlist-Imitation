package com.wunderlist.tools;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.wunderlist.R;
import com.wunderlist.slidingmenu.activity.TaskDetailsActivity;

/**
 * 提醒设置对话框
 * @author Silocean
 *
 */
public class ClockDialogUtil implements android.view.View.OnClickListener {

	private TaskDetailsActivity activity;
	private LayoutInflater inflater;
	private RelativeLayout clockLayout;

	private Button spinnerRemindnum;
	private Button spinnerRemindtype;

	private String[] remindnumArrayDay = new String[30];
	private String[] remindnumArrayHour = new String[24];
	private String[] remindnumArrayMinute = new String[60];
	private String[] remindtypeArray = new String[3];
	
	private int remindtype = 0;
	private int remindnum = 0;

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
		clockLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_clock,
				null);
		spinnerRemindnum = (Button) clockLayout
				.findViewById(R.id.remindnumSpinner);
		spinnerRemindnum.setOnClickListener(this);
		spinnerRemindnum.setText(this.remindnum+"");
		spinnerRemindtype = (Button) clockLayout
				.findViewById(R.id.remindtypeSpinner);
		spinnerRemindtype.setOnClickListener(this);
		switch (this.remindtype) {
		case 0:
			spinnerRemindtype.setText("天");
			break;
		case 1:
			spinnerRemindtype.setText("小时");
			break;
		case 2:
			spinnerRemindtype.setText("分钟");
			break;
		default:
			break;
		}
		for(int i = 0; i < 30; i++) {
			remindnumArrayDay[i] = "" + (i+1);
		}
		for(int i = 0; i < 24; i++) {
			remindnumArrayHour[i] = "" + (i+1);
		}
		for(int i = 0; i < 60; i++) {
			remindnumArrayMinute[i] = "" + (i+1);
		}
		remindtypeArray[0] = "天";
		remindtypeArray[1] = "小时";
		remindtypeArray[2] = "分钟";
	}
	
	/**
	 * 显示设置提醒提前量对话框
	 * 
	 * @return
	 */
	public AlertDialog showClockDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(activity)
				.setView(clockLayout).setTitle("设置提醒")
				.setPositiveButton("保存", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String remindnum = spinnerRemindnum.getText().toString();
						String remindtype = spinnerRemindtype.getText().toString();
						if (remindtype.equals("天")) {
							remindtype = "0";
						} else if (remindtype.equals("小时")) {
							remindtype = "1";
						} else if (remindtype.equals("分钟")) {
							remindtype = "2";
						}
						activity.updateClockTextView(remindnum, remindtype);
					}
				}).setNegativeButton("移除", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.updateClockTextView("", "");
					}
				}).show();
		return alertDialog;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.remindnumSpinner: {
			this.showRemindNumDialog(remindtype, remindnum);
			break;
		}
		case R.id.remindtypeSpinner: {
			this.showRemindTypeDialog(remindtype);
			break;
		}
		default:
			break;
		}
	}

	private void showRemindTypeDialog(final int type) {
		new AlertDialog.Builder(activity)
				.setSingleChoiceItems(remindtypeArray, type, new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								remindtype = which;
								switch (which) {
								case 0:
									remindtype = 0;
									if(remindnum > 30) {
										remindnum = 1;
										spinnerRemindnum.setText("1");
									}
									spinnerRemindtype.setText("天");
									break;
								case 1:
									remindtype = 1;
									if(remindnum > 24) {
										spinnerRemindnum.setText("1");
									}
									spinnerRemindtype.setText("小时");
									break;
								case 2:
									remindtype = 2;
									if(remindnum > 60) {
										spinnerRemindnum.setText("1");
									}
									spinnerRemindtype.setText("分钟");
									break;
								default:
									break;
								}
							}
						}).show();
	}
	
	private void showRemindNumDialog(int type, final int num) {
		switch (type) {
		case 0: {
			new AlertDialog.Builder(activity)
				.setSingleChoiceItems(remindnumArrayDay, num-1, new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						remindnum = which;
						spinnerRemindnum.setText(""+(which+1));
					}
				}).show();
			break;
		}
		case 1: {
			new AlertDialog.Builder(activity)
			.setSingleChoiceItems(remindnumArrayHour, num-1, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					remindnum = which;
					spinnerRemindnum.setText(""+(which+1));
				}
			}).show();
			break;
		}
		case 2: {
			new AlertDialog.Builder(activity)
			.setSingleChoiceItems(remindnumArrayMinute, num-1, new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					remindnum = which;
					spinnerRemindnum.setText(""+(which+1));
				}
			}).show();
			break;
		}
		default:
			break;
		}
	}
	

}
