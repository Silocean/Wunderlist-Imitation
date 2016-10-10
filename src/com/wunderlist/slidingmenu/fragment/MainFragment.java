package com.wunderlist.slidingmenu.fragment;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.entity.Common;
import com.wunderlist.entity.Task;
import com.wunderlist.entity.CommonUser;
import com.wunderlist.slidingmenu.activity.SlidingActivity;
import com.wunderlist.slidingmenu.activity.TaskDetailsActivity;
import com.wunderlist.tools.CheckNetwork;
import com.wunderlist.tools.EnddateComparator;
import com.wunderlist.tools.MySharedPreferences;
import com.wunderlist.tools.StreamTool;
import com.wunderlist.tools.SubjectComparator;
import com.wunderlist.tools.TimeConvertTool;
import com.wunderlist.tools.WebServiceRequest;

@SuppressLint("ValidFragment")
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

	private StringBuilder sb = new StringBuilder();
	private String[] receivers = new String[] { CommonUser.USEREMAIL };

	private static RelativeLayout mainfragmentLayout;

	private static SlidingActivity slidingActivity = null;

	private View popupViewBottomBar;
	private PopupWindow popupWindowBottomBar;
	private RelativeLayout bottombarLeftlayout;
	private RelativeLayout bottombarRightlayout;

	private View popupViewSortChoose;
	private PopupWindow popupwindowSortChoose;
	private RelativeLayout sortBySubjectLayout;
	private RelativeLayout sortByEnddateLayout;

	private ListView listView;

	private EditText taskEditText = null;

	private TaskListItemAdapter adapter = null;

	private static LinkedList<Task> tasksTotal = new LinkedList<Task>();
	private static LinkedList<Task> tasksNormal = new LinkedList<Task>();
	private static LinkedList<Task> tasksComplete = new LinkedList<Task>();
	// 临时变量用于保存已完成任务列表
	private static LinkedList<Task> tempList = new LinkedList<Task>();

	private Task[] arrayTasksNormal = null;
	private Task[] arrayTasksComplete = null;

	private Task task = null;

	private boolean isComplete = false;

	public static long duration = 0L;
	public static long timeStart = 0L;
	public static long timeEnd = 0L;

	public MainFragment(SlidingActivity slidingActivity) {
		MainFragment.slidingActivity = slidingActivity;
	}

	public MainFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_frame, null);
		mainfragmentLayout = (RelativeLayout) view
				.findViewById(R.id.head_layout);
		taskEditText = (EditText) view.findViewById(R.id.taskEdit);
		taskEditText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					String subject = taskEditText.getText().toString().trim();
					if (!subject.equals("")) {
						Toast.makeText(getActivity(), subject,
								Toast.LENGTH_SHORT).show();
						taskEditText.setText("");
						try {
							addTask(subject);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(getActivity(), "主题不能为空",
								Toast.LENGTH_SHORT).show();
					}
				}
				return false;
			}
		});
		SlidingActivity.setBarTitle("我的任务");
		this.getPreferences();
		listView = (ListView) view.findViewById(R.id.tasklist);
		listView.setOnScrollListener(this);
		adapter = new TaskListItemAdapter(getActivity(), R.layout.listitem_task);
		listView.setAdapter(adapter);
		if (CheckNetwork.isNetworkAvailable(getActivity())) {
			this.getTaskBoxList();
		} else {
			Common.ToastIfNetworkIsNotAvailable(getActivity());
		}
		return view;
	}

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
	 * 发起任务
	 */
	public void addTask(String subject) throws Exception {
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
						this.constructReceiversString(receivers).toString());
		data = string.getBytes();
		String json = WebServiceRequest.SendPost(inputStream, data,
				"AddTaskResult");
		if (parseUpdateJSON(json)) {
			this.updateTaskBoxList();
		} else {
			Toast.makeText(getActivity(), "添加任务失败", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 构建接收人字符串
	 * 
	 * @param receivers
	 * @return
	 */
	private StringBuilder constructReceiversString(String[] receivers) {
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
	 * 获取用户所有任务列表
	 */
	@SuppressLint("NewApi")
	private void getTaskBoxList() {
		SlidingActivity.setIsRefreshing(true);
		slidingActivity.invalidateOptionsMenu();
		tasksTotal.removeAll(tasksTotal);
		GetTaskBoxListData task = new GetTaskBoxListData(TAGNORMAL, tasksNormal);
		task.execute("");
	}

	/**
	 * 更新任务列表
	 */
	public void updateTaskBoxList() {
		this.getTaskBoxList();
	}

	/**
	 * 更新任务列表（不从网络获取数据）
	 * 
	 * @param position
	 * @param tag
	 */
	private void updateTaskBoxList(int position, int tag) {
		Task task = tasksTotal.get(position);
		switch (tag) {
		case NORMALTOCOMPLETEORCANCEL:
			if (showCompleteTasks) {
				tasksNormal.remove(position);
				tasksComplete.addFirst(task);
				tasksTotal.removeAll(tasksTotal);
				tasksTotal.addAll(tasksNormal);
				tasksTotal.add(new Task());
				tasksTotal.addAll(tasksComplete);
				tasksTotal.add(new Task());
			} else {
				tasksNormal.remove(position);
				tempList.add(task);
				tasksComplete.removeAll(tasksComplete);
				tasksTotal.removeAll(tasksTotal);
				tasksTotal.addAll(tasksNormal);
				tasksTotal.add(new Task());
				tasksTotal.addAll(tasksComplete);
				tasksTotal.add(new Task());
			}
			break;
		case COMPLETEORCANCELTONORMAL:
			tasksComplete.remove(position - tasksNormal.size() - 1);
			tasksNormal.add(task);
			tasksTotal.removeAll(tasksTotal);
			tasksTotal.addAll(tasksNormal);
			tasksTotal.add(new Task());
			tasksTotal.addAll(tasksComplete);
			tasksTotal.add(new Task());
			break;
		default:
			break;
		}
		adapter.setData(tasksTotal);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 更新任务列表（不从网络获取数据）
	 * 
	 * @param taskJSON
	 * @param position
	 */
	private void updateTaskBoxList(String taskJSON, int position) {
		try {
			Task task = this.parseSingleTaskJSON(taskJSON);
			tasksTotal.remove(position);
			tasksTotal.add(position, task);
			adapter.setData(tasksTotal);
			adapter.notifyDataSetChanged();
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
		task.setDisc(obj.getString("DISC"));
		task.setEnddate(obj.getString("ENDDATE"));
		task.setRemindtype(obj.getString("REMINDTYPE"));
		task.setRemindnum(obj.getString("REMINDNUM"));
		task.setCreateDate(obj.getString("CREATEDATE"));
		return task;
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
				// System.out.println(tasks);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("网络连接出现问题");
			}
			return tasks;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(LinkedList<Task> tasks) {
			if (tag.equals(TAGNORMAL)) {
				tasksTotal.addAll(tasks);
				tasksTotal.add(new Task());
				GetTaskBoxListData task = new GetTaskBoxListData(TAGCOMPLETE,
						tasksComplete);
				task.execute("");
				tasksNormal = tasks;
			} else if (tag.equals(TAGCOMPLETE)) {
				tasksTotal.addAll(tasks);
				tasksTotal.add(new Task());
				if (tasksTotal.size() != 0) {
					adapter.setData(tasksTotal);
					adapter.notifyDataSetChanged();
				}
				tasksComplete = tasks;
				timeEnd = System.currentTimeMillis();
				SlidingActivity.setIsRefreshing(false);
				slidingActivity.invalidateOptionsMenu();
				showOrDismissCompleteTasks();
			}
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
	private static LinkedList<Task> parseJSON(String json) throws Exception {
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
					task.setDisc(obj.getString("DISC"));
					task.setEnddate(obj.getString("ENDDATE"));
					task.setRemindtype(obj.getString("REMINDTYPE"));
					task.setRemindnum(obj.getString("REMINDNUM"));
					task.setCreateDate(obj.getString("CREATEDATE"));
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

	/**
	 * 显示或隐藏已完成任务列表
	 */
	private void showOrDismissCompleteTasks() {
		if (showCompleteTasks) {
			tasksComplete.addAll(tempList);
			tempList.removeAll(tempList);

			tasksTotal.removeAll(tasksTotal);
			tasksTotal.addAll(tasksNormal);
			tasksTotal.add(new Task());
			tasksTotal.addAll(tasksComplete);
			tasksTotal.add(new Task());
			adapter.setData(tasksTotal);
			adapter.notifyDataSetChanged();
		} else {
			tempList.removeAll(tempList);
			tempList.addAll(tasksComplete);
			tasksComplete.removeAll(tasksComplete);

			tasksTotal.removeAll(tasksTotal);
			tasksTotal.addAll(tasksNormal);
			tasksTotal.add(new Task());
			tasksTotal.addAll(tasksComplete);
			tasksTotal.add(new Task());
			adapter.setData(tasksTotal);
			adapter.notifyDataSetChanged();
		}
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.initBottomPopupWindow();
		this.initSortChoosePopupWindow();
	}

	/**
	 * 任务列表项数据填充器
	 */
	private class TaskListItemAdapter extends BaseAdapter {

		private static final int TYPE_NORMAL = 1;
		private static final int TYPE_COMPLETE = 2;
		private static final int TYPE_COMPLETE_LAYOUT = 3;
		private static final int TYPE_BOTTOMLAYOUT = 4;

		private Context context;
		private LinkedList<Task> list;
		private int resId;
		private LayoutInflater inflater;

		private ViewHolder holder;

		public TaskListItemAdapter(Context context, int resId) {
			this.context = context;
			this.list = new LinkedList<Task>();
			this.resId = resId;
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			// System.out.println("完成的个数:"+tasksComplete.size());
			if (position == getCount() - 1) {
				return TYPE_BOTTOMLAYOUT;
			} else {
				if (position == (getCount() - tasksComplete.size() - 2)) {
					return TYPE_COMPLETE_LAYOUT;
				} else if (position > (getCount() - tasksComplete.size() - 2)) {
					return TYPE_COMPLETE;
				} else {
					return TYPE_NORMAL;
				}
			}
		}

		/**
		 * 判断该任务是否包含截止日期等其他信息
		 * 
		 * @param position
		 * @return
		 */
		private boolean isHaveOthers(int position) {
			if (!((Task) getItem(position)).getEnddate().equals("")
					&& !((Task) getItem(position)).getEnddate().equals(
							"1900/1/1 0:00:00")) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			holder = new ViewHolder();
			task = this.list.get(position);
			switch (getItemViewType(position)) {
			case TYPE_BOTTOMLAYOUT: {
				convertView = inflater.inflate(
						R.layout.listitem_task_bottom_layout, null);
				break;
			}
			case TYPE_COMPLETE_LAYOUT: {
				convertView = inflater.inflate(
						R.layout.listitem_task_complete_layout, null);
				holder.taskCompleteLayout = (RelativeLayout) convertView
						.findViewById(R.id.task_complete_layout);
				holder.taskCompleteCountTextView = (TextView) convertView
						.findViewById(R.id.task_complete_count_text);
				holder.taskCompleteVisibleIcon = (ImageView) convertView
						.findViewById(R.id.task_complete_visible_icon);
				if (showCompleteTasks) {
					holder.taskCompleteCountTextView.setText(tasksComplete
							.size() + "个已完成的任务");
					holder.taskCompleteVisibleIcon
							.setImageResource(R.drawable.wl_taskview_icon_hide_selected);
				} else {
					holder.taskCompleteCountTextView.setText(tempList.size()
							+ "个已完成的任务");
					holder.taskCompleteVisibleIcon
							.setImageResource(R.drawable.wl_taskview_icon_hide);
				}
				holder.taskCompleteLayout
						.setOnClickListener(new OnClickListener() {
							@SuppressLint("CommitPrefEdits")
							public void onClick(View v) {
								if (showCompleteTasks) {
									showCompleteTasks = false;
								} else {
									showCompleteTasks = true;
								}
								SharedPreferences preferences = MySharedPreferences
										.getPreferences(getActivity());
								Editor editor = preferences.edit();
								editor.putBoolean("showCompleteTasks",
										showCompleteTasks);
								editor.commit();
								showOrDismissCompleteTasks();
							}
						});
				break;
			}
			case TYPE_COMPLETE: {
				if (isHaveOthers(position)) {
					convertView = inflater.inflate(
							R.layout.listitem_task_ohter, null);
					this.initListItemOthers(position, convertView);
				} else {
					convertView = inflater.inflate(resId, null);
					this.initListItemView(position, convertView, task);
				}
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
								new AlertDialog.Builder(context)
										.setTitle("恢复任务")
										.setMessage("确认恢复此任务，将此任务标记为未完成状态？")
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														// Toast.makeText(context,
														// "确定", 0).show();
														updateTaskStatus(list
																.get(position)
																.getTaskId(),
																CommonUser.USEREMAIL,
																TASKNORMAL,
																position,
																COMPLETEORCANCELTONORMAL);
													}
												})
										.setNegativeButton("取消", null).show();
								return false;
							}
						});
				break;
			}
			case TYPE_NORMAL: {
				if (isHaveOthers(position)) {
					convertView = inflater.inflate(
							R.layout.listitem_task_ohter, null);
					this.initListItemOthers(position, convertView);
				} else {
					convertView = inflater.inflate(resId, null);
					this.initListItemView(position, convertView, task);
				}
				holder.checkBox.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						updateTaskStatus(list.get(position).getTaskId(),
								CommonUser.USEREMAIL, TASKCOMPLETE, position,
								NORMALTOCOMPLETEORCANCEL);
					}
				});
				holder.middleLayout
						.setOnLongClickListener(new OnLongClickListener() {
							public boolean onLongClick(View v) {
								new AlertDialog.Builder(context)
										.setTitle("取消任务")
										.setMessage("确认取消此任务，将此任务标记为取消状态？")
										.setPositiveButton(
												"确定",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														// Toast.makeText(context,
														// "确定", 0).show();
														updateTaskStatus(list
																.get(position)
																.getTaskId(),
																CommonUser.USEREMAIL,
																TASKCANCEL,
																position,
																NORMALTOCOMPLETEORCANCEL);
													}
												})
										.setNegativeButton("取消", null).show();
								return false;
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

		private void initListItemView(final int position, View convertView,
				Task task) {
			holder.leftLayout = (RelativeLayout) convertView
					.findViewById(R.id.task_left_relativelayout);
			holder.middleLayout = (RelativeLayout) convertView
					.findViewById(R.id.task_middle_relativelayout);
			holder.rightLayout = (RelativeLayout) convertView
					.findViewById(R.id.task_right_relativelayout);
			holder.checkBox = (ImageView) convertView
					.findViewById(R.id.task_left_checkbox);
			holder.taskEnddate = (TextView) convertView
					.findViewById(R.id.task_middle_enddate);
			holder.taskTitle = (TextView) convertView
					.findViewById(R.id.task_middle_subject);
			holder.taskIcon = (ImageView) convertView
					.findViewById(R.id.task_right_icon);
			String userId = task.getUserId();
			if (userId.equals(CommonUser.USERID)) { // 该任务是用户自己发起的
				// holder.taskTitle.setTextColor(getResources().getColor(R.color.task_normal_initiate_text_color));
				holder.taskIcon
						.setImageResource(R.drawable.wl_task_ismeinitiate);
			}
			final String title = task.getSubject();
			holder.taskTitle.setText(title);
			holder.middleLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,
							TaskDetailsActivity.class);
					intent.putExtra("task", (Task) getItem(position));
					intent.putExtra("title", SlidingActivity.getBarTitle());
					intent.putExtra("position", position);
					if (position > (getCount() - tasksComplete.size() - 2)) {
						isComplete = true;
					} else {
						isComplete = false;
					}
					intent.putExtra("isComplete", isComplete);
					startActivityForResult(intent, 1);
				}
			});
			holder.taskIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Toast.makeText(context, "icon" + position,
					// Toast.LENGTH_SHORT).show();
				}
			});
		}

		private void initListItemOthers(int position, View convertView) {
			this.initListItemView(position, convertView, task);
			holder.taskEnddate = (TextView) convertView
					.findViewById(R.id.task_middle_enddate);
			String enddate = task.getEnddate();
			holder.taskEnddate.setText(TimeConvertTool
					.convertToSpecialEnddateStr(enddate));
			switch (TimeConvertTool.compareDate(enddate)) {
			case 0: {
				holder.taskEnddate.setTextColor(getResources().getColor(
						R.color.task_date_overdue_text_color));
				break;
			}
			case 1: {
				holder.taskEnddate.setTextColor(getResources().getColor(
						R.color.task_date_active_text_color));
				break;
			}
			default:
				break;
			}
		}

		private class ViewHolder {
			private ImageView checkBox;
			private TextView taskTitle;
			private TextView taskEnddate;
			private ImageView taskIcon;
			private RelativeLayout taskCompleteLayout;
			private TextView taskCompleteCountTextView;
			private ImageView taskCompleteVisibleIcon;
			private RelativeLayout leftLayout;
			private RelativeLayout middleLayout;
			private RelativeLayout rightLayout;
		}

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 1: {
			Bundle bundle = data.getBundleExtra("bundle");
			boolean isTaskChange = bundle.getBoolean("isTaskChange");
			boolean isTaskStatusChange = bundle
					.getBoolean("isTaskStatusChange");
			if (isTaskChange) {
				int position = bundle.getInt("position");
				String taskJSON = bundle.getString("taskJSON");
				this.updateTaskBoxList(taskJSON, position);
			}
			if (isTaskStatusChange) {
				int position = bundle.getInt("position");
				int tag = bundle.getInt("tag");
				this.updateTaskBoxList(position, tag);
			}
			break;
		}
		default:
			break;
		}
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
		popupWindowBottomBar.setAnimationStyle(R.style.AnimationPreview);
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
		popupwindowSortChoose = new PopupWindow(popupViewSortChoose, 140,
				LayoutParams.WRAP_CONTENT, false);
		// 设置此参数获得焦点，否则无法点击
		popupwindowSortChoose.setFocusable(true);
		// 需要设置一下此参数，点击外边可消失
		popupwindowSortChoose.setBackgroundDrawable(new BitmapDrawable());
		// 设置点击窗口外边窗口消失
		popupwindowSortChoose.setOutsideTouchable(true);
		// 设置窗口动画效果
		popupwindowSortChoose.setAnimationStyle(R.style.AnimationPreview);
		sortBySubjectLayout = (RelativeLayout) popupViewSortChoose
				.findViewById(R.id.sort_subject);
		sortByEnddateLayout = (RelativeLayout) popupViewSortChoose
				.findViewById(R.id.sort_enddate);
		sortBySubjectLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sortBySubject(tasksNormal, tasksComplete, SORTBYSUBJECT);
				showOrDismissSortChoosePopupWindow();
			}
		});
		sortByEnddateLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sortBySubject(tasksNormal, tasksComplete, SORTBYENDDATE);
				showOrDismissSortChoosePopupWindow();
			}
		});
	}
	
	/**
	 * 根据指定的排序规则对指定的任务集合进行排序
	 * @param tasksNormal 未完成任务列表
	 * @param tasksComplete 已完成任务列表
	 * @param sortByWhat 排序规则
	 */
	private void sortBySubject(LinkedList<Task> tasksNormal, LinkedList<Task> tasksComplete, int sortByWhat) {
		arrayTasksNormal = new Task[tasksNormal.size()];
		for (int i = 0; i < tasksNormal.size(); i++) {
			arrayTasksNormal[i] = tasksNormal.get(i);
		}
		if(sortByWhat == SORTBYSUBJECT) {
			Arrays.sort(arrayTasksNormal, subjectComparator);
		} else if(sortByWhat == SORTBYENDDATE) {
			Arrays.sort(arrayTasksNormal, enddateComparator);
		}
		tasksNormal.removeAll(tasksNormal);
		for (int i = 0; i < arrayTasksNormal.length; i++) {
			tasksNormal.add(arrayTasksNormal[i]);
		}
		arrayTasksComplete = new Task[tasksComplete.size()];
		for (int i = 0; i < tasksComplete.size(); i++) {
			arrayTasksComplete[i] = tasksComplete.get(i);
		}
		if(sortByWhat == SORTBYSUBJECT) {
			Arrays.sort(arrayTasksComplete, subjectComparator);
		} else if(sortByWhat == SORTBYENDDATE){
			Arrays.sort(arrayTasksComplete, enddateComparator);
		}
		tasksComplete.removeAll(tasksComplete);
		for (int i = 0; i < arrayTasksComplete.length; i++) {
			tasksComplete.add(arrayTasksComplete[i]);
		}
		tasksTotal.removeAll(tasksTotal);
		tasksTotal.addAll(tasksNormal);
		tasksTotal.add(new Task());
		tasksTotal.addAll(tasksComplete);
		tasksTotal.add(new Task());
		adapter.setData(tasksTotal);
		adapter.notifyDataSetChanged();
	}

}
