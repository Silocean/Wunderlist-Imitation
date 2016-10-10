package com.wunderlist.tools;

import java.util.ArrayList;
import java.util.List;

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

public class ClockDialogUtil {

	private TaskDetailsActivity activity;
	private LayoutInflater inflater;
	private RelativeLayout clockLayout;
	
	private Spinner spinnerRemindnum;
	private Spinner spinnerRemindtype;
	
	private ArrayAdapter<String> adapter;
	private List<String> listNum = new ArrayList<String>();
	private List<String> listType = new ArrayList<String>();

	public ClockDialogUtil(TaskDetailsActivity activity) {
		this.activity = activity;
		this.inflater = (LayoutInflater) activity.getLayoutInflater();
		this.initView();
	}
	
	private void initView() {
		clockLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_clock, null);
		spinnerRemindnum = (Spinner)clockLayout.findViewById(R.id.remindnumSpinner);
		spinnerRemindnum.setOnItemSelectedListener(new RemindnumListener());
		spinnerRemindtype = (Spinner)clockLayout.findViewById(R.id.remindtypeSpinner);
		spinnerRemindtype.setOnItemSelectedListener(new RemindtypeListener());
		for(int i=1; i<=30; i++) {
			listNum.add(""+i);
		}
		adapter = new ArrayAdapter<String>(activity, R.layout.dialog_listitem_spinner, R.id.clock_listitem_textview, listNum);
		spinnerRemindnum.setAdapter(adapter);
		this.listType.add("天");
		this.listType.add("小时");
		this.listType.add("分钟");
		adapter = new ArrayAdapter<String>(activity, R.layout.dialog_listitem_spinner, R.id.clock_listitem_textview, listType);
		spinnerRemindtype.setAdapter(adapter);
	}
	
	/**
	 * 显示设置提醒提前量对话框
	 * @return
	 */
	public AlertDialog showClockDialog() {
		String[] str = new String[]{};
		AlertDialog alertDialog = new AlertDialog.Builder(activity)
				.setView(clockLayout)
				.setTitle("设置提醒")
				.setPositiveButton("保存", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//Toast.makeText(activity, spinnerRemindnum.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
						String remindnum = spinnerRemindnum.getSelectedItem().toString();
						String remindtype = spinnerRemindtype.getSelectedItem().toString();
						if(remindtype.equals("天")) {
							remindtype = "0";
						} else if(remindtype.equals("小时")) {
							remindtype = "1";
						} else if(remindtype.equals("分钟")) {
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
	
	private class RemindtypeListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			listNum.removeAll(listNum);
			switch (pos) {
			case 0:
				for(int i=1; i<=30; i++) {
					listNum.add(""+i);
				}
				break;
			case 1:
				for(int i=1; i<=24; i++) {
					listNum.add(""+i);
				}
				break;
			case 2:
				for(int i=1; i<=60; i++) {
					listNum.add(""+i);
				}
				break;
			default:
				break;
			}
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
		
	}
	
	private class RemindnumListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected (AdapterView<?> parent, View view, int pos, long id) {
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
		}
		
	}
	
	
}
