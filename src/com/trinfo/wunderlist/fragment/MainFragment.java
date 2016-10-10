package com.trinfo.wunderlist.fragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trinfo.wunderlist.R;
import com.trinfo.wunderlist.activity.MainActivity;
import com.trinfo.wunderlist.activity.ReceiversActivity;
import com.trinfo.wunderlist.activity.TaskDetailsActivity;
import com.trinfo.wunderlist.entity.Common;
import com.trinfo.wunderlist.entity.CommonUser;
import com.trinfo.wunderlist.entity.Task;
import com.trinfo.wunderlist.tools.CheckNetwork;
import com.trinfo.wunderlist.tools.ClockAlarmUtil;
import com.trinfo.wunderlist.tools.EnddateComparator;
import com.trinfo.wunderlist.tools.MySharedPreferences;
import com.trinfo.wunderlist.tools.RawFilesUtil;
import com.trinfo.wunderlist.tools.StreamTool;
import com.trinfo.wunderlist.tools.SubjectComparator;
import com.trinfo.wunderlist.tools.TimeConvertTool;
import com.trinfo.wunderlist.tools.WebServiceRequest;

public class MainFragment extends Fragment implements OnScrollListener {

	private static final String TAGNORMAL = "1";
	private static final String TAGCOMPLETE = "0,2";

	private static final String TASKCANCEL = "0";
	private static final String TASKNORMAL = "1";
	private static final String TASKCOMPLETE = "2";

	private static final int NORMALTOCOMPLETEORCANCEL = 1;
	private static final int COMPLETEORCANCELTONORMAL = 2;

	private static final int SORTBYSUBJECT = 1;
	private static final int SORTBYENDDATE = 2;

	private SubjectComparator subjectComparator = new SubjectComparator();
	private EnddateComparator enddateComparator = new EnddateComparator();

	private static boolean showCompleteTasks = true;
	private RelativeLayout completeLayout;
	private TextView completeCountTxt;
	private ImageView completeVisibleIcon;

	private String[] receivers = new String[] { CommonUser.USEREMAIL };

	private static RelativeLayout mainfragmentLayout;

	private static MainActivity mainActivity = null;

	private View popupViewBottomBar;
	private PopupWindow popupWindowBottomBar;
	private RelativeLayout bottombarLeftlayout;
	private RelativeLayout bottombarRightlayout;

	private View popupViewSortChoose;
	private PopupWindow popupwindowSortChoose;
	private RelativeLayout sortBySubjectLayout;
	private RelativeLayout sortByEnddateLayout;

	private ListView listView_taskNormal;
	private ListView listView_taskComplete;

	private TaskNormalListAdapter normalAdapter = null;
	private TaskCompleteListAdapter completeAdapter = null;

	private String priority = null;

	private EditText taskEditText = null;

	private static LinkedList<Task> tasksNormal = new LinkedList<Task>();
	private static LinkedList<Task> tasksComplete = new LinkedList<Task>();

	private static LinkedList<Task> tempCompleteList = new LinkedList<Task>();

	public static ArrayList<Task> clockTimes = new ArrayList<Task>();

	private Task[] arrayTasks = null;

	private Task task = null;

	public static long duration = 0L;
	public static long timeStart = 0L;
	public static long timeEnd = 0L;

	private UIHandler handler = new UIHandler();

	@SuppressLint("HandlerLeak")
	private class UIHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				Common.ToastIfNetworkProblem(getActivity());
				break;
			default:
				break;
			}
		}
	}

	public MainFragment(MainActivity mainActivity) {
		MainFragment.mainActivity = mainActivity;
	}

	public MainFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_frame, null);
		mainfragmentLayout = (RelativeLayout) view
				.findViewById(R.id.mainfragment_layout);
		taskEditText = (EditText) view.findViewById(R.id.taskEdit);
		taskEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					String subject = taskEditText.getText().toString().trim();
					if (!subject.equals("")) {
						taskEditText.setText("");
						addTask(subject);
					} else {
						Toast.makeText(getActivity(), "主题不能为空",
								Toast.LENGTH_SHORT).show();
					}
				}
				return false;
			}
		});
		MainActivity.setBarTitle("我的任务");
		clockTimes.removeAll(clockTimes);
		this.getPreferences();
		inflater = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		View footerView = inflater.inflate(R.layout.footerview, null);
		completeLayout = (RelativeLayout) footerView
				.findViewById(R.id.task_complete_layout);
		completeCountTxt = (TextView) footerView
				.findViewById(R.id.task_complete_count_text);
		completeVisibleIcon = (ImageView) footerView
				.findViewById(R.id.task_complete_visible_icon);
		completeLayout.setOnClickListener(new OnClickListener() {
			@SuppressLint("CommitPrefEdits")
			public void onClick(View v) {
				if (showCompleteTasks) {
					showCompleteTasks = false;
					completeVisibleIcon
							.setImageResource(R.drawable.wl_taskview_icon_hide);
				} else {
					showCompleteTasks = true;
					completeVisibleIcon
							.setImageResource(R.drawable.wl_taskview_icon_hide_selected);
				}
				SharedPreferences preferences = MySharedPreferences
						.getPreferences(getActivity());
				Editor editor = preferences.edit();
				editor.putBoolean("showCompleteTasks", showCompleteTasks);
				editor.commit();
				showOrDismissCompleteTasks();
			}
		});
		listView_taskNormal = (ListView) view
				.findViewById(R.id.tasknormal_listview);
		listView_taskNormal.setOnScrollListener(this);
		listView_taskNormal.addFooterView(footerView);
		listView_taskComplete = (ListView) footerView
				.findViewById(R.id.taskcomplete_listview);
		normalAdapter = new TaskNormalListAdapter(getActivity(), tasksNormal);
		completeAdapter = new TaskCompleteListAdapter(getActivity(),
				tasksComplete);
		listView_taskNormal.setAdapter(normalAdapter);
		listView_taskComplete.setAdapter(completeAdapter);
		setListViewHeightBasedOnChildren(listView_taskComplete);
		if (CheckNetwork.isNetworkAvailable(getActivity())) {
			this.getTaskBoxList();
		} else {
			Common.ToastIfNetworkIsNotAvailable(getActivity());
		}
		return view;
	}

	/**
	 * 显示或隐藏已完成任务列表
	 */
	private void showOrDismissCompleteTasks() {
		if (showCompleteTasks) {
			tasksComplete.addAll(tempCompleteList);
			tempCompleteList.removeAll(tempCompleteList);
			normalAdapter.setData(tasksNormal);
			completeAdapter.setData(tasksComplete);
			setListViewHeightBasedOnChildren(listView_taskComplete);
		} else {
			tempCompleteList.removeAll(tempCompleteList);
			tempCompleteList.addAll(tasksComplete);
			tasksComplete.removeAll(tasksComplete);
			normalAdapter.setData(tasksNormal);
			completeAdapter.setData(tasksComplete);
		}
	}

	/**
	 * 动态设置listview的高度
	 * 
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter adapter = listView.getAdapter();
		if (adapter != null) {
			int totalHeight = 0;
			for (int i = 0; i < adapter.getCount(); i++) {
				View listItem = adapter.getView(i, null, listView);
				listItem.setLayoutParams(new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (adapter.getCount() - 1));
			((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
			listView.setLayoutParams(params);

			completeCountTxt.setText(tasksComplete.size() + "个已完成的任务");
			if (showCompleteTasks) {
				completeVisibleIcon
						.setImageResource(R.drawable.wl_taskview_icon_hide_selected);
			} else {
				completeVisibleIcon
						.setImageResource(R.drawable.wl_taskview_icon_hide);
			}

		}
	}

	/**
	 * 取得sharedPreference
	 */
	private void getPreferences() {
		SharedPreferences preferences = MySharedPreferences
				.getPreferences(getActivity());
		int bgId = preferences.getInt(MySharedPreferences.BGID, 0);
		showCompleteTasks = preferences.getBoolean("showCompleteTasks", true);
		mainfragmentLayout.setBackgroundResource(Common.BGS[bgId]);
	}

	/**
	 * 更改全局背景
	 * 
	 * @param bgId
	 */
	public static void changeBg(int bgId) {
		mainfragmentLayout.setBackgroundResource(Common.BGS[bgId]);
	}

	/**
	 * 添加任务
	 * 
	 * @param subject
	 */
	private void addTask(String subject) {
		new AddTask(subject).execute("");
	}

	/**
	 * 异步任务，用于添加任务
	 * 
	 * @author Silocean
	 * 
	 */
	private class AddTask extends AsyncTask<String, Integer, String> {

		private String subject = null;

		public AddTask(String subject) {
			this.subject = subject;
		}

		@Override
		protected String doInBackground(String... params) {
			String json = "";
			try {
				InputStream inputStream = this.getClass().getClassLoader()
						.getResourceAsStream("AddTask.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data)
						.replaceAll("\\&USERID", CommonUser.USERID)
						.replaceAll("\\&MFROM", CommonUser.USEREMAIL)
						.replaceAll("\\&SUBJECT", subject)
						.replaceAll("\\&DISC", "")
						.replaceAll("\\&PRIORITY", "")
						.replaceAll("\\&ENDDATE", "")
						.replaceAll("\\&REMINDTYPE", "")
						.replaceAll("\\&REMINDNUM", "")
						.replaceAll("\\&ATTFILES", "")
						.replaceAll("<string>\\&string</string>",
								constructReceiversString(receivers).toString());
				data = string.getBytes();
				json = WebServiceRequest.SendPost(inputStream, data,
						"AddTaskResult");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (parseUpdateJSON(result)) {
					updateTaskBoxList();
				} else {
					Toast.makeText(getActivity(), "添加任务失败", Toast.LENGTH_SHORT)
							.show();
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
	 * 给任务设置星标
	 * 
	 * @param taskId
	 * @param status
	 * @param position
	 */
	private void setStar(String taskId, String status, int position) {
		new SetStarTask(taskId, status, position).execute("");
	}

	/**
	 * 异步任务，用于给任务设置星标
	 * 
	 * @author Silocean
	 * 
	 */
	private class SetStarTask extends AsyncTask<String, Integer, String> {

		private String taskId;
		private String status;
		private int position;

		public SetStarTask(String taskId, String status, int position) {
			this.taskId = taskId;
			this.status = status;
			this.position = position;
		}

		@Override
		protected String doInBackground(String... params) {
			String json = "";
			try {
				InputStream inputStream = this.getClass().getClassLoader()
						.getResourceAsStream("SetStar.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strTaskID",
						taskId).replaceAll("\\&strStatus", status);
				data = string.getBytes();
				json = WebServiceRequest.SendPost(inputStream, data,
						"SetStarResult");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (parseUpdateJSON(result)) {
					// updateTaskBoxList();
					updateTaskBoxListStar(position, status);
				} else {
					Toast.makeText(getActivity(), "设置星标任务失败",
							Toast.LENGTH_SHORT).show();
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
	 * 构建接收人字符串
	 * 
	 * @param receivers
	 * @return
	 */
	private StringBuilder constructReceiversString(String[] receivers) {
		StringBuilder sb = new StringBuilder();
		if (receivers.length != 0) {
			for (int i = 0; i < receivers.length; i++) {
				sb.append("<string>");
				sb.append(receivers[i]);
				sb.append("</string>");
				if (i != receivers.length - 1) {
					sb.append("\n");
				}
			}
		} else {
			sb.append("<string>");
			sb.append("");
			sb.append("</string>");
		}
		return sb;
	}

	/**
	 * 更新任务列表（星标状态更改）
	 * 
	 * @param position
	 * @param status
	 */
	public void updateTaskBoxListStar(int position, String status) {
		Task task = tasksNormal.get(position);
		task.setPriority(status);
		normalAdapter.setData(tasksNormal);
	}

	/**
	 * 获取用户所有任务列表
	 */
	@SuppressLint("NewApi")
	private void getTaskBoxList() {
		showRefreshMenu();
		GetTaskBoxListData task = new GetTaskBoxListData(TAGNORMAL, tasksNormal);
		task.execute("");
	}

	/**
	 * 显示刷新数据菜单
	 */
	@SuppressLint("NewApi")
	private void showRefreshMenu() {
		MainActivity.setIsRefreshing(true);
		mainActivity.invalidateOptionsMenu();
	}

	/**
	 * 隐藏刷新数据菜单
	 */
	@SuppressLint("NewApi")
	private void hideRefreshMenu() {
		timeEnd = System.currentTimeMillis();
		MainActivity.setIsRefreshing(false);
		mainActivity.invalidateOptionsMenu();
	}

	/**
	 * 更新任务列表
	 */
	public void updateTaskBoxList() {
		this.getTaskBoxList();
	}

	/**
	 * 异步任务，用于获取用户所有任务列表
	 * 
	 * @author SIYUNFEI
	 * 
	 */
	private class GetTaskBoxListData extends
			AsyncTask<String, Integer, LinkedList<Task>> {

		private String tag = "";
		private LinkedList<Task> tasks;

		public GetTaskBoxListData(String tag, LinkedList<Task> tasks) {
			this.tag = tag;
			this.tasks = tasks;
		}

		@Override
		protected LinkedList<Task> doInBackground(String... strings) {
			timeStart = System.currentTimeMillis();
			tasks.removeAll(tasks); // 先清空任务列表
			try {
				InputStream inputStream = this.getClass().getClassLoader()
						.getResourceAsStream("GetTaskBoxList.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data)
						.replaceAll("\\&strUserID", CommonUser.USERID)
						.replaceAll("\\&IsActive", tag)
						.replaceAll("\\&IsRead", "")
						.replaceAll("\\&strSubject", "");
				data = string.getBytes();
				String json = WebServiceRequest.SendPost(inputStream, data,
						"GetTaskBoxListResult");
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
			if (tag.equals(TAGNORMAL)) {
				tasksNormal.addAll(tasks);
				GetTaskBoxListData task = new GetTaskBoxListData(TAGCOMPLETE,
						tasksComplete);
				task.execute("");
			} else if (tag.equals(TAGCOMPLETE)) {
				tasksComplete.addAll(tasks);

				normalAdapter.setData(tasksNormal);
				completeAdapter.setData(tasksComplete);

				showOrDismissCompleteTasks();

				setListViewHeightBasedOnChildren(listView_taskComplete);
				hideRefreshMenu();
				setClockAlarm();

			}
		}

	}

	/**
	 * 设置提醒
	 */
	private void setClockAlarm() {
		clockTimes.removeAll(clockTimes);
		arrayTasks = new Task[tasksNormal.size()];
		for (int i = 0; i < tasksNormal.size(); i++) {
			arrayTasks[i] = tasksNormal.get(i);
		}
		Arrays.sort(arrayTasks, new EnddateComparator());
		for (int i = 0; i < arrayTasks.length; i++) {
			task = arrayTasks[i];
			if (!task.getRemindnum().equals("")
					&& !task.getRemindtype().equals("")
					&& !task.getEnddate().equals("")) {
				Date date = TimeConvertTool.getClockTime(task.getEnddate(),
						task.getRemindnum(), task.getRemindtype());
				if (TimeConvertTool.compareDate(date)) {
					clockTimes.add(task);
				}
			}
		}
		if (clockTimes.size() > 0) {
			ClockAlarmUtil.setClockAlarm(getActivity(), clockTimes.get(0));
		}
	}

	/**
	 * 获取程序刷新数据时间
	 * 
	 * @return
	 */
	public static long getRefreshingDuration() {
		return timeEnd - timeStart;
	}

	/**
	 * 解析json字符串
	 * 
	 * @param json
	 * @return 任务列表数据
	 * @throws Exception
	 */
	private LinkedList<Task> parseJSON(String json) throws Exception {
		LinkedList<Task> tasks = new LinkedList<Task>();
		if (json != null) {
			JSONObject object = new JSONObject(json);
			int rows = Integer.parseInt(object.getString("rows"));
			if (rows > 0) {
				JSONArray array = new JSONArray(object.getString("Items"));
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Task task = new Task();
					task.setTaskId(obj.getString("SID"));
					task.setUserId(obj.getString("USERID"));
					task.setTaskFrom(obj.getString("MFROM"));
					task.setSubject(obj.getString("SUBJECT"));
					task.setEnddate(obj.getString("ENDDATE").replaceAll("/",
							"-"));
					task.setRemindtype(obj.getString("REMINDTYPE"));
					task.setRemindnum(obj.getString("REMINDNUM"));
					task.setPriority(obj.getString("PRIORITY"));
					task.setCreateDate(obj.getString("CREATEDATE").replaceAll(
							"/", "-"));
					tasks.add(task);
				}
			} else {
				System.out.println("没有数据");
			}
		} else { // 网络连接出现问题
			handler.sendEmptyMessage(1);
		}
		return tasks;
	}

	/**
	 * 不带截止日期的listAdapter
	 * 
	 * @author Silocean
	 * 
	 */
	private class TaskNormalListAdapter extends BaseAdapter {

		private LinkedList<Task> list;
		private LayoutInflater inflater;

		private ViewHolder holder;

		public TaskNormalListAdapter(Context context, LinkedList<Task> list) {
			this.list = list;
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		/**
		 * 加载数据
		 * 
		 * @param tasks
		 */
		public void setData(LinkedList<Task> tasks) {
			this.list = tasks;
			this.notifyDataSetChanged();
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

		/**
		 * 判断该任务是否包含截止日期等其他信息
		 * 
		 * @param position
		 * @return
		 */
		private boolean isHaveOthers(int position) {
			if (!((Task) getItem(position)).getEnddate().equals("")) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			task = (Task) getItem(position);
			holder = new ViewHolder();
			if (isHaveOthers(position)) {
				convertView = inflater.inflate(R.layout.listitem_task_ohter,
						null);
				holder.taskEnddate = (TextView) convertView
						.findViewById(R.id.task_middle_enddate);
				String enddate = task.getEnddate();
				holder.taskEnddate.setText(TimeConvertTool
						.convertToSpecialEnddateStr(enddate));
				if (TimeConvertTool.compareDate(enddate)) {
					holder.taskEnddate.setTextColor(getResources().getColor(
							R.color.task_date_active_text_color));
				} else {
					holder.taskEnddate.setTextColor(getResources().getColor(
							R.color.task_date_overdue_text_color));
				}
			} else {
				convertView = inflater.inflate(R.layout.listitem_task, null);
			}
			holder.middleLayout = (RelativeLayout) convertView
					.findViewById(R.id.task_middle_relativelayout);
			holder.checkBox = (ImageView) convertView
					.findViewById(R.id.task_left_checkbox);
			holder.taskTitle = (TextView) convertView
					.findViewById(R.id.task_middle_subject);
			holder.taskStarIcon = (ImageView) convertView
					.findViewById(R.id.task_right_icon);
			holder.taskReveiversIcon = (ImageView) convertView
					.findViewById(R.id.task_receivers_icon);
			if (!task.getUserId().equals(CommonUser.USERID)) {
				holder.taskReveiversIcon.setVisibility(View.GONE);
			} else {
				holder.taskReveiversIcon.setVisibility(View.VISIBLE);
			}
			if (task.getPriority() != null && !task.getPriority().equals("")
					&& task.getPriority().equals("1")) { // 该任务是星标任务
				holder.taskStarIcon
						.setImageResource(R.drawable.wl_task_ribbon_selected);
			} else {
				holder.taskStarIcon.setImageResource(R.drawable.wl_task_ribbon);
			}
			String title = task.getSubject();
			holder.taskTitle.setText(title);
			priority = list.get(position).getPriority();
			holder.checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					RawFilesUtil.ring(getActivity(), 2);
					updateTaskStatus(list.get(position).getTaskId(),
							CommonUser.USEREMAIL, TASKCOMPLETE, position,
							NORMALTOCOMPLETEORCANCEL);
				}
			});
			holder.middleLayout
					.setOnLongClickListener(new OnLongClickListener() {
						public boolean onLongClick(View v) {
							cancleTask(position);
							return false;
						}
					});
			holder.taskStarIcon.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					priority = list.get(position).getPriority();
					if (priority != null) {
						if (priority.equals("") || priority.equals("0")) {
							// holder.taskIcon.setImageResource(R.drawable.wl_task_ribbon);
							setStar(list.get(position).getTaskId(), "1",
									position);
						} else {
							// holder.taskIcon.setImageResource(R.drawable.wl_task_ribbon_selected);
							setStar(list.get(position).getTaskId(), "0",
									position);
						}
					}
				}
			});
			holder.middleLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							TaskDetailsActivity.class);
					intent.putExtra("task", (Task) getItem(position));
					intent.putExtra("title", MainActivity.getBarTitle());
					intent.putExtra("position", position);
					intent.putExtra("isComplete", false);
					startActivityForResult(intent, 1);
				}
			});
			holder.taskReveiversIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							ReceiversActivity.class);
					intent.putExtra("tag", 1);
					intent.putExtra("task", (Task) getItem(position));
					startActivity(intent);
				}
			});
			return convertView;
		}

		private class ViewHolder {
			private ImageView checkBox;
			private TextView taskTitle;
			private TextView taskEnddate;
			private ImageView taskStarIcon;
			private ImageView taskReveiversIcon;
			private RelativeLayout middleLayout;
		}

	}

	/**
	 * 取消任务
	 * 
	 * @param position
	 */
	private void cancleTask(final int position) {
		new AlertDialog.Builder(getActivity()).setTitle("取消任务")
				.setMessage("确认取消此任务，将此任务标记为取消状态？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						updateTaskStatus(tasksNormal.get(position).getTaskId(),
								CommonUser.USEREMAIL, TASKCANCEL, position,
								NORMALTOCOMPLETEORCANCEL);
					}
				}).setNegativeButton("取消", null).show();
	}

	/**
	 * 带截止日期的listAdapter
	 * 
	 * @author Silocean
	 * 
	 */
	private class TaskCompleteListAdapter extends BaseAdapter {

		private LinkedList<Task> list;
		private LayoutInflater inflater;

		private ViewHolder holder;

		public TaskCompleteListAdapter(Context context, LinkedList<Task> list) {
			this.list = list;
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		/**
		 * 加载数据
		 * 
		 * @param tasks
		 */
		public void setData(LinkedList<Task> tasks) {
			this.list = tasks;
			this.notifyDataSetChanged();
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

		/**
		 * 判断该任务是否包含截止日期等其他信息
		 * 
		 * @param position
		 * @return
		 */
		private boolean isHaveOthers(int position) {
			if (!((Task) getItem(position)).getEnddate().equals("")) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			task = (Task) getItem(position);
			holder = new ViewHolder();
			if (isHaveOthers(position)) {
				convertView = inflater.inflate(R.layout.listitem_task_ohter,
						null);
				holder.taskEnddate = (TextView) convertView
						.findViewById(R.id.task_middle_enddate);
				String enddate = task.getEnddate();
				holder.taskEnddate.setText(TimeConvertTool
						.convertToSpecialEnddateStr(enddate));
				if (TimeConvertTool.compareDate(enddate)) {
					holder.taskEnddate.setTextColor(getResources().getColor(
							R.color.task_date_active_text_color));
				} else {
					holder.taskEnddate.setTextColor(getResources().getColor(
							R.color.task_date_overdue_text_color));
				}
			} else {
				convertView = inflater.inflate(R.layout.listitem_task, null);
			}
			holder.leftLayout = (RelativeLayout) convertView
					.findViewById(R.id.task_left_relativelayout);
			holder.middleLayout = (RelativeLayout) convertView
					.findViewById(R.id.task_middle_relativelayout);
			holder.rightLayout = (RelativeLayout) convertView
					.findViewById(R.id.task_right_relativelayout);
			holder.checkBox = (ImageView) convertView
					.findViewById(R.id.task_left_checkbox);
			holder.taskTitle = (TextView) convertView
					.findViewById(R.id.task_middle_subject);
			holder.taskStarIcon = (ImageView) convertView
					.findViewById(R.id.task_right_icon);
			holder.taskReveiversIcon = (ImageView) convertView
					.findViewById(R.id.task_receivers_icon);
			if (!task.getUserId().equals(CommonUser.USERID)) {
				holder.taskReveiversIcon.setVisibility(View.GONE);
			} else {
				holder.taskReveiversIcon.setVisibility(View.VISIBLE);
			}
			if (task.getPriority() != null && !task.getPriority().equals("")
					&& task.getPriority().equals("1")) { // 该任务是星标任务
				holder.taskStarIcon
						.setImageResource(R.drawable.wl_task_ribbon_selected);
			} else {
				holder.taskStarIcon.setImageResource(R.drawable.wl_task_ribbon);
			}
			String title = task.getSubject();
			holder.taskTitle.setText(title);
			holder.leftLayout
					.setBackgroundResource(R.drawable.wl_task_background_left_disabled);
			holder.middleLayout
					.setBackgroundResource(R.drawable.wl_task_background_middle_disabled);
			holder.rightLayout
					.setBackgroundResource(R.drawable.wl_task_background_right_disabled);
			holder.taskTitle.setTextColor(getResources().getColor(
					R.color.listitem_text_complete_color));
			holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			holder.taskReveiversIcon.setEnabled(false);
			if (task.getPriority() != null && !task.getPriority().equals("")
					&& task.getPriority().equals("1")) { // 该任务是星标任务
				holder.taskStarIcon
						.setImageResource(R.drawable.wl_task_ribbon_disabled_selected);
			}
			holder.checkBox
					.setImageResource(R.drawable.wl_task_checkbox_checked_pressed);
			holder.checkBox.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 解除任务完成状态
					updateTaskStatus(list.get(position).getTaskId(),
							CommonUser.USEREMAIL, TASKNORMAL, position,
							COMPLETEORCANCELTONORMAL);
				}
			});
			holder.middleLayout
					.setOnLongClickListener(new OnLongClickListener() {
						public boolean onLongClick(View v) {
							recoverCancelTask(position);
							return false;
						}
					});
			holder.middleLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							TaskDetailsActivity.class);
					intent.putExtra("task", list.get(position));
					intent.putExtra("title", MainActivity.getBarTitle());
					intent.putExtra("position", position);
					intent.putExtra("isComplete", true);
					startActivityForResult(intent, 1);
				}
			});
			holder.taskReveiversIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							ReceiversActivity.class);
					intent.putExtra("tag", 1);
					intent.putExtra("task", list.get(position));
					startActivity(intent);
				}
			});
			return convertView;
		}

		private class ViewHolder {
			private ImageView checkBox;
			private TextView taskTitle;
			private TextView taskEnddate;
			private ImageView taskStarIcon;
			private ImageView taskReveiversIcon;
			private RelativeLayout leftLayout;
			private RelativeLayout middleLayout;
			private RelativeLayout rightLayout;
		}

	}

	/**
	 * 恢复取消任务
	 * 
	 * @param position
	 */
	private void recoverCancelTask(final int position) {
		new AlertDialog.Builder(getActivity()).setTitle("恢复任务")
				.setMessage("确认恢复此任务，将此任务标记为未完成状态？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						updateTaskStatus(tasksComplete.get(position)
								.getTaskId(), CommonUser.USEREMAIL, TASKNORMAL,
								position, COMPLETEORCANCELTONORMAL);
					}
				}).setNegativeButton("取消", null).show();
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.initBottomPopupWindow();
		this.initSortChoosePopupWindow();
	}

	/**
	 * 更改任务状态
	 * 
	 * @param taskId
	 * @param useremail
	 * @param status
	 * @param position
	 * @param tag
	 *            1表示normal->complete|cancel,2表示complete|cancel->normal
	 */
	private void updateTaskStatus(String taskId, String useremail,
			String status, int position, int tag) {
		new UpdateTaskStatus(taskId, useremail, status, position, tag)
				.execute("");
	}

	/**
	 * 异步任务，用户更新任务状态
	 * 
	 * @author Silocean
	 * 
	 */
	private class UpdateTaskStatus extends AsyncTask<String, Integer, String> {

		private String taskId;
		private String useremail;
		private String status;
		private int position;
		private int tag;

		public UpdateTaskStatus(String taskId, String useremail, String status,
				int position, int tag) {
			this.taskId = taskId;
			this.useremail = useremail;
			this.status = status;
			this.position = position;
			this.tag = tag;
		}

		@Override
		protected String doInBackground(String... params) {
			String json = "";
			try {
				InputStream inputStream = this.getClass().getClassLoader()
						.getResourceAsStream("UpdateTaskStatus.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data)
						.replaceAll("\\&strTaskID", taskId)
						.replaceAll("\\&strEmail", useremail)
						.replaceAll("\\&strStatus", status);
				data = string.getBytes();
				json = WebServiceRequest.SendPost(inputStream, data,
						"UpdateTaskStatusResult");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (parseUpdateJSON(result)) {
					// updateTaskBoxList();
					updateTaskBoxList(position, tag);
				} else {
					Toast.makeText(getActivity(), "更改任务状态失败",
							Toast.LENGTH_SHORT).show();
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
	 * 解析更新任务状态返回的json字符串
	 * 
	 * @param json
	 *            要解析的json字符串
	 * @return 0表示出现异常，1表示保存成功
	 * @throws Exception
	 */
	private boolean parseUpdateJSON(String json) throws Exception {
		if (json != null) {
			JSONObject object = new JSONObject(json);
			if (object.getString("status").equals("1")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 更新任务列表（normal to complete and complete to normal）
	 * 
	 * @param position
	 * @param tag
	 */
	public void updateTaskBoxList(int position, int tag) {
		Task tempTask;
		switch (tag) {
		case NORMALTOCOMPLETEORCANCEL:
			tempTask = tasksNormal.get(position);
			if (showCompleteTasks) {
				tasksNormal.remove(position);
				tasksComplete.addFirst(tempTask);
			} else {
				tasksNormal.remove(position);
				tempCompleteList.add(tempTask);
				tasksComplete.removeAll(tasksComplete);
			}
			break;
		case COMPLETEORCANCELTONORMAL:
			tempTask = tasksComplete.get(position);
			tasksComplete.remove(position);
			tasksNormal.add(tempTask);
			break;
		default:
			break;
		}
		normalAdapter.setData(tasksNormal);
		completeAdapter.setData(tasksComplete);
		this.setListViewHeightBasedOnChildren(listView_taskComplete);
		this.setClockAlarm();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 1: {
			Bundle bundle = data.getBundleExtra("bundle");
			boolean isTaskChange = bundle.getBoolean("isTaskChange");
			boolean isTaskStatusChange = bundle
					.getBoolean("isTaskStatusChange");
			boolean isTaskStarChange = bundle.getBoolean("isTaskStarChange");
			if (isTaskChange) {
				// this.updateTaskBoxList();
				int position = bundle.getInt("position");
				String taskJSON = bundle.getString("taskJSON");
				this.updateTaskBoxListSingleTask(taskJSON, position);
			}
			if (isTaskStatusChange) {
				updateTaskBoxList();
			}
			if (isTaskStarChange) {
				// this.updateTaskBoxList();
				int position = bundle.getInt("position");
				String status = bundle.getString("taskStar");
				this.updateTaskBoxListStar(position, status);
			}
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 更新任务列表（单个任务信息更改）
	 * 
	 * @param taskJSON
	 * @param position
	 */
	private void updateTaskBoxListSingleTask(String taskJSON, int position) {
		try {
			Task task = this.parseSingleTaskJSON(taskJSON);
			tasksNormal.remove(position);
			tasksNormal.add(position, task);
			normalAdapter.setData(tasksNormal);
			this.setClockAlarm();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析单个任务json字符串
	 * 
	 * @param taskJSON
	 * @return
	 * @throws Exception
	 */
	private Task parseSingleTaskJSON(String taskJSON) throws Exception {
		Task task = new Task();
		JSONObject obj = new JSONObject(taskJSON);
		task.setTaskId(obj.getString("SID"));
		task.setUserId(obj.getString("USERID"));
		task.setTaskFrom(obj.getString("MFROM"));
		task.setSubject(obj.getString("SUBJECT"));
		task.setPriority(obj.getString("PRIORITY"));
		task.setEnddate(obj.getString("ENDDATE"));
		task.setRemindtype(obj.getString("REMINDTYPE"));
		task.setRemindnum(obj.getString("REMINDNUM"));
		task.setCreateDate(obj.getString("CREATEDATE"));
		return task;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			showOrDismissBottomBarPopupWindow();
			break;
		default:
			break;
		}
	}

	/**
	 * 显示或隐藏底部popupWindow
	 */
	private void showOrDismissBottomBarPopupWindow() {
		if (popupWindowBottomBar.isShowing()) {
			popupWindowBottomBar.dismiss();
		} else {
			popupWindowBottomBar.showAtLocation(mainfragmentLayout,
					Gravity.BOTTOM, 0, 0);
		}
	}

	/**
	 * 初始化底部popupWindow
	 */
	@SuppressWarnings("deprecation")
	private void initBottomPopupWindow() {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popupViewBottomBar = inflater.inflate(
				R.layout.popupwindow_taskview_bottombar, null);
		popupWindowBottomBar = new PopupWindow(popupViewBottomBar,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, false);
		// 设置此参数获得焦点，否则无法点击
		// popupWindow.setFocusable(true);
		// 需要设置一下此参数，点击外边可消失
		popupWindowBottomBar.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		popupWindowBottomBar.setOutsideTouchable(true);
		// 设置窗口动画效果
		popupWindowBottomBar.setAnimationStyle(R.style.BottomLayoutAnimation);
		bottombarLeftlayout = (RelativeLayout) popupViewBottomBar
				.findViewById(R.id.taskview_bottombar_leftlayout);
		int width = getActivity().getWindowManager().getDefaultDisplay()
				.getWidth() / 2;
		LayoutParams layoutParams = bottombarLeftlayout.getLayoutParams();
		layoutParams.width = width;
		bottombarLeftlayout.setLayoutParams(layoutParams);
		bottombarLeftlayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
				intent.putExtra(Intent.EXTRA_TEXT, "嗨！我发现一款好玩儿的社交应用，推荐给你！");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(Intent.createChooser(intent, "共享"));
			}
		});
		bottombarLeftlayout.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				Toast.makeText(getActivity(), "共享", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		bottombarRightlayout = (RelativeLayout) popupViewBottomBar
				.findViewById(R.id.taskview_bottombar_rightlayout);
		bottombarRightlayout.setLayoutParams(layoutParams);
		bottombarRightlayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showOrDismissSortChoosePopupWindow();
			}
		});
		bottombarRightlayout.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				Toast.makeText(getActivity(), "排序选项", Toast.LENGTH_SHORT)
						.show();
				return false;
			}
		});
	}

	/**
	 * 显示或隐藏排序选项popupWindow
	 */
	@SuppressWarnings("deprecation")
	private void showOrDismissSortChoosePopupWindow() {
		if (popupwindowSortChoose.isShowing()) {
			popupwindowSortChoose.dismiss();
		} else {
			int offsetX = getActivity().getWindowManager().getDefaultDisplay()
					.getWidth()
					- sortBySubjectLayout.getLayoutParams().width;
			int offsetY = getActivity().getWindowManager().getDefaultDisplay()
					.getHeight()
					- sortBySubjectLayout.getLayoutParams().height
					* 2
					- bottombarLeftlayout.getLayoutParams().height;
			popupwindowSortChoose.showAtLocation(mainfragmentLayout,
					Gravity.TOP, offsetX, offsetY);
		}
	}

	/**
	 * 初始化排序选项popupWindow
	 */
	@SuppressWarnings("deprecation")
	private void initSortChoosePopupWindow() {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		popupViewSortChoose = inflater.inflate(R.layout.popupwindow_sortchoose,
				null);
		popupwindowSortChoose = new PopupWindow(popupViewSortChoose,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);
		// 设置此参数获得焦点，否则无法点击
		popupwindowSortChoose.setFocusable(true);
		// 需要设置一下此参数，点击外边可消失
		popupwindowSortChoose.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		popupwindowSortChoose.setOutsideTouchable(true);
		// 设置窗口动画效果
		popupwindowSortChoose.setAnimationStyle(R.style.SortLayoutAnimation);
		sortBySubjectLayout = (RelativeLayout) popupViewSortChoose
				.findViewById(R.id.sort_subject);
		sortByEnddateLayout = (RelativeLayout) popupViewSortChoose
				.findViewById(R.id.sort_enddate);
		sortBySubjectLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sortByWhat(tasksNormal, tasksComplete, SORTBYSUBJECT);
				showOrDismissSortChoosePopupWindow();
			}
		});
		sortByEnddateLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sortByWhat(tasksNormal, tasksComplete, SORTBYENDDATE);
				showOrDismissSortChoosePopupWindow();
			}
		});
	}

	/**
	 * 根据指定的排序规则对指定的任务集合进行排序
	 * 
	 * @param tasksNormal
	 *            未完成任务列表
	 * @param tasksComplete
	 *            已完成任务列表
	 * @param sortByWhat
	 *            排序规则
	 */
	private void sortByWhat(LinkedList<Task> tasksNormal,
			LinkedList<Task> tasksComplete, int sortByWhat) {
		arrayTasks = new Task[tasksNormal.size()];
		for (int i = 0; i < tasksNormal.size(); i++) {
			arrayTasks[i] = tasksNormal.get(i);
		}
		if (sortByWhat == SORTBYSUBJECT) {
			Arrays.sort(arrayTasks, subjectComparator);
		} else if (sortByWhat == SORTBYENDDATE) {
			Arrays.sort(arrayTasks, enddateComparator);
		}
		tasksNormal.removeAll(tasksNormal);
		for (int i = 0; i < arrayTasks.length; i++) {
			tasksNormal.add(arrayTasks[i]);
		}
		arrayTasks = new Task[tasksComplete.size()];
		for (int i = 0; i < tasksComplete.size(); i++) {
			arrayTasks[i] = tasksComplete.get(i);
		}
		if (sortByWhat == SORTBYSUBJECT) {
			Arrays.sort(arrayTasks, subjectComparator);
		} else if (sortByWhat == SORTBYENDDATE) {
			Arrays.sort(arrayTasks, enddateComparator);
		}
		tasksComplete.removeAll(tasksComplete);
		for (int i = 0; i < arrayTasks.length; i++) {
			tasksComplete.add(arrayTasks[i]);
		}

		normalAdapter.setData(tasksNormal);
		completeAdapter.setData(tasksComplete);

	}

}
