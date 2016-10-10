package com.wunderlist.slidingmenu.fragment;

import java.io.InputStream;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.entity.Task;
import com.wunderlist.slidingmenu.activity.SlidingActivity;
import com.wunderlist.slidingmenu.activity.TaskDetailsActivity;
import com.wunderlist.tools.StreamTool;
import com.wunderlist.tools.WebServiceRequest;

public class MainFragment extends Fragment {
	
	private ListView listView;
	//private ListView listViewCancel;
	//private ListView listViewcomplete;
	
	private EditText taskEditText = null;
	
	private String userId = "ec060fdf-af0e-40a2-a86d-4d11a4cad9ea";
	
	private String userEmail = "andy.sun@tr-info.net";
	
	private TaskListItemAdapter adapter = null;
	
	private LinkedList<Task> tasks = new LinkedList<Task>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_frame, null);
		taskEditText = (EditText)view.findViewById(R.id.taskEdit);
		taskEditText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEND) {
					String subject = taskEditText.getText().toString().trim();
					Toast.makeText(getActivity(), subject, Toast.LENGTH_SHORT).show();
					taskEditText.setText("");
					try {
						addTask(subject);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return false;
			}
		});
		listView = (ListView)view.findViewById(R.id.tasklist);
		//listViewCancel = (ListView)view.findViewById(R.id.taskcancellist);
		//listViewcomplete = (ListView)view.findViewById(R.id.taskcompletelist);
		adapter = new TaskListItemAdapter(getActivity(), R.layout.listitem_task);
		this.getTaskBoxList();
		return view;
	}
	
	/**
	 * 发起任务
	 */
	public void addTask(String subject) throws Exception {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("AddTask.xml");
		byte[] data = StreamTool.read(inputStream);
		String string = new String(data).replaceAll("\\&USERID", userId).replaceAll("\\&MFROM", userEmail)
				.replaceAll("\\&SUBJECT", subject).replaceAll("\\&DISC", "").replaceAll("\\&PRIORITY", "")
				.replaceAll("\\&ENDDATE", "").replaceAll("\\&REMINDTYPE", "").replaceAll("\\&REMINDNUM", "")
				.replaceAll("\\&ATTFILES", "");
		data = string.getBytes();
		String json = WebServiceRequest.SendPost(inputStream, data, "AddTaskResult");
		System.out.println("===="+json);
		
		getTaskList();
		
	}
	
	/**
	 * 获取用户所有任务列表
	 */
	public void getTaskBoxList() {
		SlidingActivity.setBarTitle("我的任务");
		GetTaskBoxListData task = new GetTaskBoxListData();
		task.execute("");
		//System.out.println("===================="); 
		listView.setAdapter(adapter);
		//listViewCancel.setAdapter(adapter);
		//listViewcomplete.setAdapter(adapter);
		
	}
	
	/**
	 * 获取用户发起的任务列表
	 */
	public void getTaskList() {
		SlidingActivity.setBarTitle("我发起的");
		GetTaskListData task = new GetTaskListData();
		task.execute("");
		listView.setAdapter(adapter);
		//this.setListViewHeightBasedOnChildren(listView);
		
	}
	
	/**
	 * 异步任务，用于获取用户所有任务列表
	 * @author SIYUNFEI
	 *
	 */
	private class GetTaskBoxListData extends AsyncTask<String, Integer, LinkedList<Task>> {
		
		@Override
		protected LinkedList<Task> doInBackground(String... strings) {
			tasks.removeAll(tasks); // 先清空任务列表
			try {
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("GetTaskBoxList.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strUserID", userId).replaceAll("\\&IsActive", "").replaceAll("\\&IsRead", "").replaceAll("\\&strSubject", "");
				data = string.getBytes();
				String json = WebServiceRequest.SendPost(inputStream, data, "GetTaskBoxListResult");
				tasks = parseJSON(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tasks;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
		}
		
		@Override
		protected void onPostExecute(LinkedList<Task> tasks) {
			//System.out.println(tasks);
			adapter.removeAllData();
			adapter.setData(tasks);
			adapter.notifyDataSetChanged();
			System.out.println("a:"+adapter.getCount());
			/*setListViewHeightBasedOnChildren(listView);
			setListViewHeightBasedOnChildren(listViewCancel);
			setListViewHeightBasedOnChildren(listViewcomplete);*/
		}
		
	}
	/**
	 * 异步任务，用于获取用户发起的任务列表
	 * @author SIYUNFEI
	 *
	 */
	private class GetTaskListData extends AsyncTask<String, Integer, LinkedList<Task>> {
		
		@Override
		protected LinkedList<Task> doInBackground(String... strings) {
			LinkedList<Task> tasks = new LinkedList<Task>();
			tasks.removeAll(tasks); // 先清空任务列表
			try {
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("GetTaskList.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strUserID", userId).replaceAll("\\&strTaskID", "");
				data = string.getBytes();
				String json = WebServiceRequest.SendPost(inputStream, data, "GetTaskListResult");
				tasks = parseJSON(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tasks;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
		}
		
		@Override
		protected void onPostExecute(LinkedList<Task> tasks) {
			//System.out.println(tasks);
			adapter.removeAllData();
			adapter.setData(tasks);
			adapter.notifyDataSetChanged();
			System.out.println("b:"+adapter.getCount());
		}
		
	}
	
	/**
	 * 解析json字符串
	 * @param json
	 * @return 任务列表数据
	 * @throws Exception
	 */
	private static LinkedList<Task> parseJSON(String json) throws Exception {
		JSONObject object = new JSONObject(json);
		int rows = Integer.parseInt(object.getString("rows"));
		LinkedList<Task> tasks = new LinkedList<Task>();
		if(rows > 0) {
			JSONArray array = new JSONArray(object.getString("Items"));
			for(int i=0; i<array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Task task = new Task();
				task.setTaskId(obj.getString("SID"));
				task.setUserId(obj.getString("USERID"));
				task.setTaskFrom(obj.getString("MFROM"));
				task.setSubject(obj.getString("SUBJECT"));
				task.setDisc(obj.getString("DISC"));
				task.setEnddate(obj.getString("ENDDATE"));
				tasks.add(task);
			}
		} else {
			System.out.println("没有数据");
		}
		return tasks;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	/**
	 * 动态设置listview的高度
	 * @param listView
	 *//*
	private void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if(listAdapter == null) {
			return;
		} else {
			int totalHeight = 0;
			for(int i=0; i<listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				System.out.println(listItem);
				//listItem.measure(0, 0); // 在还没有构建view之前无法取得view的度宽，在此之前我们必须要measure一下
				totalHeight += listItem.getMeasuredHeight();
				System.out.println(i+"==="+listItem.getMeasuredHeight() + "=" + listItem.getHeight());
			}
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight+(listView.getDividerHeight()*(listAdapter.getCount()+1));
			listView.setLayoutParams(params);
		}
	}*/
	
	/**
	 * 任务列表项数据填充器
	 */
	private class TaskListItemAdapter extends BaseAdapter {
		
		private static final int TYPE_CANCEL = 0;
		private static final int TYPE_NORMAL = 1;
		private static final int TYPE_COMPLETE = 2;
		
		private Context context;
		private LinkedList<Task> list;
		private int resId;
		private LayoutInflater inflater;
		
		private ViewHolder holder;
		
		public TaskListItemAdapter(Context context, int resId) {
			this.context = context;
			this.list = new LinkedList<Task>();
			this.resId = resId;
			this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setData(LinkedList<Task> tasks) {
			this.list = tasks;
		}
		
		public void removeAllData() {
			this.list.addAll(list);
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
		public int getItemViewType(int position) {
			System.out.println(getCount());
			if(position >= getCount()-2) {
				return TYPE_COMPLETE;
			} else {
				return TYPE_NORMAL;
			}
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			holder = new ViewHolder();
			if(convertView == null) {
				convertView = inflater.inflate(resId, null);
				holder.checkBox = (ImageView)convertView.findViewById(R.id.task_left_checkbox);
				holder.taskMiddle = (RelativeLayout)convertView.findViewById(R.id.task_middle_relativelayout);
				holder.taskTitle = (TextView)convertView.findViewById(R.id.task_middle_TextView);
				holder.taskIcon = (ImageView)convertView.findViewById(R.id.task_right_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			switch (getItemViewType(position)) {
			case TYPE_COMPLETE:
				holder.taskTitle.setTextColor(getResources().getColor(R.color.complete_color));
				break;
			case TYPE_NORMAL:
				break;
			default:
				break;
			}
			holder.checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "checkbox" + position, Toast.LENGTH_SHORT).show();
				}
			});
			final String title = this.list.get(position).getSubject();
			holder.taskTitle.setText(title);
			holder.taskMiddle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, TaskDetailsActivity.class);
					intent.putExtra("task", (Task)getItem(position));
					intent.putExtra("title", SlidingActivity.getBarTitle());
					startActivity(intent);
				}
			});
			holder.taskIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "icon" + position, Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}
		
		private class ViewHolder {
			private ImageView checkBox;
			private RelativeLayout taskMiddle;
			private TextView taskTitle;
			private ImageView taskIcon;
		}

	}

}
