/*
 * Copyright (C) 2012 yueyueniao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wunderlist.slidingmenu.fragment;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.entity.Task;
import com.wunderlist.slidingmenu.activity.TaskDetailsActivity;

public class MainFragment extends Fragment {
	
	private ListView listView;
	static final String[] COUNTRIES = new String[] {
		  "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra",
		  "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina" };
	
	private EditText taskeEditText = null;
	
	/**
	 * 初始化测试数据
	 */
	public void initTestData() {
		List<Task> list = new LinkedList<Task>();
		for(int i=0; i<COUNTRIES.length; i++) {
			Task task = new Task(COUNTRIES[i], "haha就是没有任务",  "2013-5-20 15:30:40");
			list.add(task);
		}
		TaskListItemAdapter adapter = new TaskListItemAdapter(getActivity(), list, R.layout.listitem_task);
		listView.setAdapter(adapter);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_frame, null);
		listView = (ListView)view.findViewById(R.id.tasklist);
		this.initTestData();
		taskeEditText = (EditText)view.findViewById(R.id.taskEdit);
		taskeEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEND) {
					Toast.makeText(getActivity(), taskeEditText.getText().toString().trim(), Toast.LENGTH_SHORT).show();
					taskeEditText.setText("");
				}
				return false;
			}
		});
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	/**
	 * 任务列表项数据填充器
	 */
	private class TaskListItemAdapter extends BaseAdapter {
		
		private Context context;
		private List<Task> list;
		private int resId;
		private LayoutInflater inflater;
		
		private ViewHolder holder;
		
		public TaskListItemAdapter(Context context, List<Task> list, int resId) {
			this.context = context;
			this.list = list;
			this.resId = resId;
			this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return this.list.size();
		}

		@Override
		public Object getItem(int position) {
			return this.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			holder = new ViewHolder();
			if(convertView == null) {
				convertView = inflater.inflate(resId, null);
				holder.checkBox = (ImageView)convertView.findViewById(R.id.task_left_checkbox);
				holder.taskTitle = (TextView)convertView.findViewById(R.id.task_middle_TextView);
				holder.taskIcon = (ImageView)convertView.findViewById(R.id.task_right_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "checkbox", Toast.LENGTH_SHORT).show();
				}
			});
			final String title = this.list.get(position).getTaskTitle();
			holder.taskTitle.setText(title);
			holder.taskTitle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TaskDetailsActivity.class);
					startActivity(intent);
				}
			});
			holder.taskIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "icon", Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}
		
		private class ViewHolder {
			private ImageView checkBox;
			private TextView taskTitle;
			private ImageView taskIcon;
		}
	}

	
}
