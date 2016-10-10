package com.wunderlist.slidingmenu.fragment;

import java.io.InputStream;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.entity.Task;
import com.wunderlist.entity.User;
import com.wunderlist.slidingmenu.activity.SlidingActivity;
import com.wunderlist.slidingmenu.activity.TaskDetailsActivity;
import com.wunderlist.tools.StreamTool;
import com.wunderlist.tools.WebServiceRequest;

public class MainFragment extends Fragment {
	
	private static final String TAGNORMAL = "1";
	private static final String TAGCOMPLETE = "0,2";
	
	private ListView listView;
	
	private EditText taskEditText = null;
	
	private TaskListItemAdapter adapter = null;
	
	private LinkedList<Task> tasksTotal = new LinkedList<Task>();
	private LinkedList<Task> tasksNormal = new LinkedList<Task>();
	private LinkedList<Task> tasksComplete = new LinkedList<Task>();
	
	private Task task = null;
	
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
		SlidingActivity.setBarTitle("我的任务");
		listView = (ListView)view.findViewById(R.id.tasklist);
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
		String string = new String(data).replaceAll("\\&USERID", User.USERID).replaceAll("\\&MFROM", User.USEREMAIL)
				.replaceAll("\\&SUBJECT", subject).replaceAll("\\&DISC", "").replaceAll("\\&PRIORITY", "")
				.replaceAll("\\&ENDDATE", "").replaceAll("\\&REMINDTYPE", "").replaceAll("\\&REMINDNUM", "")
				.replaceAll("\\&ATTFILES", "");
		data = string.getBytes();
		String json = WebServiceRequest.SendPost(inputStream, data, "AddTaskResult");
		if(parseUpdateJSON(json)) {
			this.updateTaskBoxList();
		} else {
			Toast.makeText(getActivity(), "添加任务失败", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 获取用户所有任务列表
	 */
	public void getTaskBoxList() {
		tasksTotal.removeAll(tasksTotal);
		GetTaskBoxListData task = new GetTaskBoxListData(TAGNORMAL, tasksNormal);
		task.execute("");
		listView.setAdapter(adapter);
	}
	
	/**
	 * 更新任务列表
	 */
	private void updateTaskBoxList() {
		this.getTaskBoxList();
	}
	
	/**
	 * 异步任务，用于获取用户所有任务列表
	 * @author SIYUNFEI
	 *
	 */
	private class GetTaskBoxListData extends AsyncTask<String, Integer, LinkedList<Task>> {
		
		private String tag = "";
		private LinkedList<Task> tasks;
		
		public GetTaskBoxListData(String tag, LinkedList<Task> tasks) {
			this.tag = tag;
			this.tasks = tasks;
		}
		
		@Override
		protected LinkedList<Task> doInBackground(String... strings) {
			tasks.removeAll(tasks); // 先清空任务列表
			try {
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("GetTaskBoxList.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strUserID", User.USERID).replaceAll("\\&IsActive", tag).replaceAll("\\&IsRead", "").replaceAll("\\&strSubject", "");
				data = string.getBytes();
				String json = WebServiceRequest.SendPost(inputStream, data, "GetTaskBoxListResult");
				tasks = parseJSON(json);
				System.out.println(tasks);
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
			if(tag.equals(TAGNORMAL)) {
				tasks.addLast(new Task());
				tasksTotal.addAll(tasks);
				GetTaskBoxListData task = new GetTaskBoxListData(TAGCOMPLETE, tasksComplete);
				task.execute("");
				tasksNormal = tasks;
			} else if(tag.equals(TAGCOMPLETE)) {
				tasksTotal.addAll(tasks);
				if(tasksTotal.size() != 0) {
					adapter.setData(tasksTotal);
					adapter.notifyDataSetChanged();
				}
				tasksComplete = tasks;
			}
		}
		
	}
	
	/**
	 * 解析json字符串
	 * @param json
	 * @return 任务列表数据
	 * @throws Exception
	 */
	private static LinkedList<Task> parseJSON(String json) throws Exception {
		LinkedList<Task> tasks = new LinkedList<Task>();
		if(json != null) {
			JSONObject object = new JSONObject(json);
			int rows = Integer.parseInt(object.getString("rows"));
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
					task.setRemindtype(obj.getString("REMINDTYPE"));
					task.setRemindnum(obj.getString("REMINDNUM"));
					tasks.add(task);
				}
			} else {
				System.out.println("没有数据");
			}
		} else { // 网络连接出现问题
			System.out.println("网络连接出现问题");
		}
		return tasks;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	/**
	 * 任务列表项数据填充器
	 */
	private class TaskListItemAdapter extends BaseAdapter {
		
		private static final int TYPE_NORMAL = 1;
		private static final int TYPE_COMPLETE = 2;
		private static final int TYPE_COMPLETE_LAYOUT = 3;
		
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
			//System.out.println("完成的个数:"+tasksComplete.size());
			if(position == (getCount()-tasksComplete.size()-1)) {
				return TYPE_COMPLETE_LAYOUT;
			} else if (position > (getCount()-tasksComplete.size()-1)){
				return TYPE_COMPLETE;
			} else {
				return TYPE_NORMAL;
			}
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			holder = new ViewHolder();
			task = this.list.get(position);
			switch (getItemViewType(position)) {
			case TYPE_COMPLETE_LAYOUT: {
				convertView = inflater.inflate(R.layout.listitem_task_complete_layout, null);
				holder.taskCompleteCountTextView = (TextView)convertView.findViewById(R.id.task_complete_count_text);
				holder.taskCompleteVisibleIcon = (ImageView)convertView.findViewById(R.id.task_complete_visible_icon);
				holder.taskCompleteCountTextView.setText(tasksComplete.size() + "个已完成的任务");
				holder.taskCompleteVisibleIcon.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(context, "visibleIcon完成", Toast.LENGTH_SHORT).show();
					}
				});
				break;
			}
			case TYPE_COMPLETE: {
				convertView = inflater.inflate(resId, null);
				this.initListItemView(position, convertView, task);
				holder.taskTitle.setTextColor(getResources().getColor(R.color.task_complete_text_color));
				holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				holder.checkBox.setImageResource(R.drawable.wl_task_checkbox_checked_pressed);
				holder.checkBox.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(context, "checkbox" + position+"解除", Toast.LENGTH_SHORT).show();
						// 解除任务完成状态
					}
				});
				break;
			}
			case TYPE_NORMAL: {
				convertView = inflater.inflate(resId, null);
				this.initListItemView(position, convertView, task);
				holder.checkBox.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Toast.makeText(context, "checkbox" + position, Toast.LENGTH_SHORT).show();
						//updateTaskStatus(task.getTaskId(), User.USEREMAIL, TASKNORMAL);
					}
				});
				break;
			}
			default: {
				break;
			}
			}
			convertView.setTag(holder);
			return convertView;
		}
		
		private void initListItemView(final int position, View convertView, Task task) {
			holder.checkBox = (ImageView)convertView.findViewById(R.id.task_left_checkbox);
			holder.taskMiddle = (RelativeLayout)convertView.findViewById(R.id.task_middle_relativelayout);
			holder.taskTitle = (TextView)convertView.findViewById(R.id.task_middle_TextView);
			holder.taskIcon = (ImageView)convertView.findViewById(R.id.task_right_icon);
			String userId = task.getUserId();
			//System.out.println(position+"===="+task.getSubject());
			if(userId.equals(User.USERID)) { // 该任务是用户自己发起的
				holder.taskTitle.setTextColor(getResources().getColor(R.color.task_normal_initiate_text_color));
			}
			final String title = task.getSubject();
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
		}
		
		private class ViewHolder {
			private ImageView checkBox;
			private RelativeLayout taskMiddle;
			private TextView taskTitle;
			private ImageView taskIcon;
			private TextView taskCompleteCountTextView;
			private ImageView taskCompleteVisibleIcon;
		}

	}
	
	/**
	 * 更改任务状态
	 * @param taskId
	 * @param useremail
	 * @param string
	 */
	private void updateTaskStatus(String taskId, String useremail, String status) {
		new UpdateTaskStatus(taskId, useremail, status).execute("");
	}
	
	/**
	 * 异步任务，用户更新任务状态
	 * @author Silocean
	 * 
	 */
	private class UpdateTaskStatus extends AsyncTask<String, Integer, String> {
		
		private String taskId;
		private String useremail;
		private String status;
		
		public UpdateTaskStatus(String taskId, String useremail, String status) {
			this.taskId = taskId;
			this.useremail = useremail;
			this.status = status;
		}

		@Override
		protected String doInBackground(String... params) {
			String json = "";
			try {
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("UpdateTaskStatus.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strTaskID", taskId).replaceAll("\\&strEmail", useremail).replaceAll("\\&strStatus", status);
				data = string.getBytes();
				json = WebServiceRequest.SendPost(inputStream, data, "UpdateTaskStatusResult");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if(parseUpdateJSON(result)) {
					updateTaskBoxList();
				} else {
					Toast.makeText(getActivity(), "更改任务状态失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
		
	}
	
	/**
	 * 解析更新任务返回的json字符串
	 * @param json 要解析的json字符串
	 * @return 0表示出现异常，1表示保存成功
	 * @throws Exception
	 */
	private boolean parseUpdateJSON(String json) throws Exception {
		if(json != null) {
			JSONObject object = new JSONObject(json);
			if(object.getString("status").equals("1")) {
				return true;
			}
		}
		return false;
	}


}
