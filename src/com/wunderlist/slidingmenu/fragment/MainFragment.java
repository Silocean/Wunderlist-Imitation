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
import android.widget.ListAdapter;
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
	
	private ListView listView;
	
	private EditText taskEditText = null;
	
	private TaskListItemAdapter adapter = null;
	
	private LinkedList<Task> tasks = new LinkedList<Task>();
	private LinkedList<Task> tasksComplete = new LinkedList<Task>();
	
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
		
		getTaskBoxList();
		
	}
	
	/**
	 * 获取用户所有任务列表
	 */
	public void getTaskBoxList() {
		SlidingActivity.setBarTitle("我的任务");
		GetTaskBoxListData task = new GetTaskBoxListData();
		task.execute("");
		listView.setAdapter(adapter);
		
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
				String string = new String(data).replaceAll("\\&strUserID", User.USERID).replaceAll("\\&IsActive", "").replaceAll("\\&IsRead", "").replaceAll("\\&strSubject", "");
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
			adapter.removeAllData();
			adapter.setData(tasks);
			adapter.notifyDataSetChanged();
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
				task.setRemindtype(obj.getString("REMINDTYPE"));
				task.setRemindnum(obj.getString("REMINDNUM"));
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
	 * 任务列表项数据填充器
	 */
	private class TaskListItemAdapter extends BaseAdapter {
		
		private static final int TYPE_CANCEL = 0;
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
			if(position == (getCount()-3)) {
				return TYPE_COMPLETE_LAYOUT;
			} else if (position > getCount()-3){
				return TYPE_COMPLETE;
			} else {
				return TYPE_NORMAL;
			}
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			holder = new ViewHolder();
			if(convertView == null) {
				switch (getItemViewType(position)) {
				case TYPE_COMPLETE_LAYOUT: {
					convertView = inflater.inflate(R.layout.listitem_task_complete_layout, null);
					break;
				}
				case TYPE_COMPLETE: {
					convertView = inflater.inflate(resId, null);
					holder.checkBox = (ImageView)convertView.findViewById(R.id.task_left_checkbox);
					holder.taskMiddle = (RelativeLayout)convertView.findViewById(R.id.task_middle_relativelayout);
					holder.taskTitle = (TextView)convertView.findViewById(R.id.task_middle_TextView);
					holder.taskIcon = (ImageView)convertView.findViewById(R.id.task_right_icon);
					holder.taskTitle.setTextColor(getResources().getColor(R.color.task_complete_text_color));
					holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					holder.checkBox.setImageResource(R.drawable.wl_task_checkbox_checked_pressed);
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
					break;
				}
				case TYPE_NORMAL: {
					convertView = inflater.inflate(resId, null);
					holder.checkBox = (ImageView)convertView.findViewById(R.id.task_left_checkbox);
					holder.taskMiddle = (RelativeLayout)convertView.findViewById(R.id.task_middle_relativelayout);
					holder.taskTitle = (TextView)convertView.findViewById(R.id.task_middle_TextView);
					holder.taskIcon = (ImageView)convertView.findViewById(R.id.task_right_icon);
					holder.checkBox.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Toast.makeText(context, "checkbox" + position, Toast.LENGTH_SHORT).show();
						}
					});
					String userId = this.list.get(position).getUserId();
					if(userId.equals(User.USERID)) {
						holder.taskTitle.setTextColor(getResources().getColor(R.color.task_normal_initiate_text_color));
					}
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
					break;
				}
				default: {
					break;
				}
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
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
