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
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.entity.Group;
import com.wunderlist.slidingmenu.activity.SettingsActivity;
import com.wunderlist.slidingmenu.activity.SlidingActivity;

public class LeftFragment extends Fragment implements OnClickListener, OnItemClickListener {
	
	private ListView listView;
	
	private ImageView notifImageView = null;
	private ImageView syncImageView = null;
	private TextView synctimeTextView = null;
	private ImageView settingsImageView = null;
	
	static String[] str = new String[]{"收件箱", "我执行的", "我关注的"};
	
	private void initTestData() {
		List<Group> list = new LinkedList<Group>();
		for (int i = 0; i < str.length; i++) {
			Group group = new Group(str[i], i);
			list.add(group);
		}
		GroupListItemAdapter adapter = new GroupListItemAdapter(getActivity(), list, R.layout.listitem_group);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				listView.getChildAt(0).setSelected(true);
			}
		};
		//这里做1秒的延时，以防getChildAt()出现空指针问题
		new Handler().postDelayed(runnable, 1000);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left, null);
		listView = (ListView)view.findViewById(R.id.grouplist);
		this.initTestData();
		notifImageView = (ImageView)view.findViewById(R.id.user_notification);
		syncImageView = (ImageView)view.findViewById(R.id.sidebar_sync);
		synctimeTextView = (TextView)view.findViewById(R.id.sidebar_synctime);
		settingsImageView = (ImageView)view.findViewById(R.id.siderbar_settings);
		notifImageView.setOnClickListener(this);
		syncImageView.setOnClickListener(this);
		settingsImageView.setOnClickListener(this);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_notification: {
			Toast.makeText(getActivity(), "notification", 0).show();
			break;
		}
		case R.id.sidebar_sync: {
			Toast.makeText(getActivity(), "sync", 0).show();
			break;
		}
		case R.id.siderbar_settings: {
			Intent intent = new Intent(getActivity(), SettingsActivity.class);
			startActivity(intent);
			break;
		}
		default:
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		view.setSelected(true);
		((SlidingActivity)getActivity()).showLeft();
	}
	
	private class GroupListItemAdapter extends BaseAdapter {
		
		private Context context;
		private List<Group> list;
		private int resId;
		private LayoutInflater inflater;
		
		private ViewHolder holder;
		
		public GroupListItemAdapter(Context context, List<Group> list, int resId) {
			this.context = context;
			this.list = list;
			this.resId = resId;
			this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			holder = new ViewHolder();
			if(convertView == null) {
				convertView = inflater.inflate(resId, null);
				holder.groupItemName = (TextView)convertView.findViewById(R.id.groupitem_name);
				holder.groupItemCount = (TextView)convertView.findViewById(R.id.groupitem_count);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			String name = list.get(position).getGroupName();
			holder.groupItemName.setText(name);
			int count = list.get(position).getGroupTaskCount();
			holder.groupItemCount.setText(count+"");
			return convertView;
		}
		
		private class ViewHolder {
			private TextView groupItemName;
			private TextView groupItemCount;
		}
		
	}

}
