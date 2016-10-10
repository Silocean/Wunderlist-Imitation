package com.trinfo.wunderlist.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.api.JPushClient;
import cn.jpush.api.MessageResult;

import com.trinfo.wunderlist.R;
import com.trinfo.wunderlist.entity.Common;
import com.trinfo.wunderlist.entity.CommonUser;
import com.trinfo.wunderlist.entity.Reply;
import com.trinfo.wunderlist.entity.Task;
import com.trinfo.wunderlist.tools.ClockDialogUtil;
import com.trinfo.wunderlist.tools.DateTimePickDialogUtil;
import com.trinfo.wunderlist.tools.JPushUtil;
import com.trinfo.wunderlist.tools.MyActivityManager;
import com.trinfo.wunderlist.tools.StreamTool;
import com.trinfo.wunderlist.tools.TimeConvertTool;
import com.trinfo.wunderlist.tools.WebServiceRequest;

public class TaskDetailsActivity extends ActionbarBaseActivity implements
		OnClickListener {

	private static final String TASKNORMAL = "1";
	private static final String TASKCOMPLETE = "2";

	private static final int NORMALTOCOMPLETEORCANCEL = 1;
	private static final int COMPLETEORCANCELTONORMAL = 2;

	private ImageView checkboxImageView;
	private EditText titleEditText;
	private ImageView taskReceiversIcon;
	private ImageView starIcon;
	private RelativeLayout deadlineRelativeLayout;
	private RelativeLayout clockRelativeLayout;
	private ImageView enddateiImageView;
	private TextView enddateTextView;
	private ImageView clockiImageView;
	private TextView clockTextView;
	private RelativeLayout taskReplyRelativeLayout;
	private TextView replyEmailTextView;
	private TextView replyContentTextView;
	private TextView replyTimeTextView;

	private EditText replyContentEditText;
	private Button replyButton;

	private boolean isComplete = false;

	private int position = 0;

	private Task task = null;
	private String barTitle = null;
	private String subject = null;
	private String enddate = null;
	private String clock = null;
	private String remindnum = null;
	private String remindtype = null;
	private String priority = null;
	private String replyContent = null;
	private boolean isReceiversChange = false;
	private ArrayList<String> receivers = new ArrayList<String>();
	private ArrayList<String> receiversId = new ArrayList<String>();
	private boolean isTaskChange = false;
	private boolean isTaskStatusChange = false;
	private boolean isTaskStarChange = false;

	private Bundle bundle = new Bundle();

	private String replyJSON = "";
	private String receiverJSON = "";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_task_details);
		MyActivityManager.addActivity("TaskDetailsActivity", this);
		checkboxImageView = (ImageView) findViewById(R.id.taskdetails_checkbox);
		checkboxImageView.setOnClickListener(this);
		titleEditText = (EditText) findViewById(R.id.taskdetails_title);
		titleEditText.setOnClickListener(this);
		taskReceiversIcon = (ImageView) findViewById(R.id.task_receivers_icon);
		taskReceiversIcon.setOnClickListener(this);
		starIcon = (ImageView) findViewById(R.id.taskdetails_icon);
		starIcon.setOnClickListener(this);
		deadlineRelativeLayout = (RelativeLayout) findViewById(R.id.taskdetais_deadline);
		deadlineRelativeLayout.setOnClickListener(this);
		clockRelativeLayout = (RelativeLayout) findViewById(R.id.taskdetais_clock);
		clockRelativeLayout.setOnClickListener(this);
		enddateiImageView = (ImageView) findViewById(R.id.taskdetails_deadline_icon);
		enddateTextView = (TextView) findViewById(R.id.taskdetails_deadline_text);
		clockiImageView = (ImageView) findViewById(R.id.taskdetails_clock_icon);
		clockTextView = (TextView) findViewById(R.id.taskdetails_clock_text);
		replyContentEditText = (EditText) this
				.findViewById(R.id.taskreply_comment);
		replyButton = (Button) this.findViewById(R.id.taskreply_reply);
		replyButton.setOnClickListener(this);
		int width = getWindowManager().getDefaultDisplay().getWidth()
				- replyButton.getLayoutParams().width - 15;
		LayoutParams layoutParams = replyContentEditText.getLayoutParams();
		layoutParams.width = width;
		replyContentEditText.setLayoutParams(layoutParams);
		taskReplyRelativeLayout = (RelativeLayout) findViewById(R.id.task_reply_layout);
		taskReplyRelativeLayout.setOnClickListener(this);
		replyEmailTextView = (TextView) findViewById(R.id.task_reply_email);
		replyContentTextView = (TextView) findViewById(R.id.task_reply_content);
		replyTimeTextView = (TextView) findViewById(R.id.task_reply_time);
		this.initData();
		this.getReply();
		this.getTaskReceivers();
		super.onCreate(savedInstanceState);
	}

	/**
	 * 填充界面数据
	 */
	private void initData() {
		position = getIntent().getIntExtra("position", 0);
		isComplete = getIntent().getBooleanExtra("isComplete", false);
		task = (Task) getIntent().getSerializableExtra("task");
		barTitle = getIntent().getStringExtra("title");
		enddate = task.getEnddate();
		remindnum = task.getRemindnum();
		remindtype = task.getRemindtype();
		priority = task.getPriority();
		super.setTitle(barTitle);
		titleEditText.setText(task.getSubject());
		subject = task.getSubject();
		this.updateDateView(enddate);
		this.updateClockTextView(task.getRemindnum(), task.getRemindtype());
		if (!task.getUserId().equals(CommonUser.USERID) || isComplete) { // 如果该任务不是用户自己发起的或者该任务已完成
			this.disableView();
		}
		if (isComplete) {
			checkboxImageView
					.setImageResource(R.drawable.wl_task_checkbox_checked_pressed);
			if (priority != null) {
				if (priority.equals("") || priority.equals("0")) {
					starIcon.setImageResource(R.drawable.wl_task_ribbon);
				} else {
					starIcon.setImageResource(R.drawable.wl_detail_ribbon_selected_disabled);
				}
			}
		} else {
			if (priority != null) {
				if (priority.equals("") || priority.equals("0")) {
					starIcon.setImageResource(R.drawable.wl_task_ribbon);
				} else {
					starIcon.setImageResource(R.drawable.wl_detail_ribbon_selected);
				}
			}
		}
	}

	/**
	 * 把某些控件设置为不可用
	 */
	private void disableView() {
		this.titleEditText.setEnabled(false);
		this.taskReceiversIcon.setEnabled(false);
		this.deadlineRelativeLayout.setEnabled(false);
		this.clockRelativeLayout.setEnabled(false);
		this.starIcon.setEnabled(false);
		this.enddateTextView.setTextColor(getResources().getColor(
				R.color.listitem_text_complete_color));
		this.clockTextView.setTextColor(getResources().getColor(
				R.color.groupname_color_normal));
		this.enddateiImageView
				.setImageResource(R.drawable.wl_taskdetails_icon_deadline);
		this.clockiImageView
				.setImageResource(R.drawable.wl_taskdetails_icon_clock);
	}

	/**
	 * 根据截止日期更新dateView
	 */
	private void changeDateView() {
		if (TimeConvertTool.compareDate(enddate)) {
			enddateiImageView
					.setImageResource(R.drawable.wl_taskdetails_icon_deadline_active);
			enddateTextView.setTextColor(getResources().getColor(
					R.color.task_date_active_text_color));
		} else {
			enddateiImageView
					.setImageResource(R.drawable.wl_taskdetails_icon_deadline_overdue);
			enddateTextView.setTextColor(getResources().getColor(
					R.color.task_date_overdue_text_color));
		}
		if (!remindnum.equals("") && !remindtype.equals("")) {
			this.changeClockView();
		}
	}

	/**
	 * 根据提醒时间更新clockView
	 */
	private void changeClockView() {
		if (TimeConvertTool.judgeClock(enddate, remindnum, remindtype)) {
			clockiImageView
					.setImageResource(R.drawable.wl_taskdetails_icon_clock_active);
			clockTextView.setTextColor(getResources().getColor(
					R.color.task_date_active_text_color));
		} else { // 提醒已过期
			clockiImageView
					.setImageResource(R.drawable.wl_taskdetails_icon_clock_overdue);
			clockTextView.setTextColor(getResources().getColor(
					R.color.task_date_overdue_text_color));
		}
	}

	/**
	 * 获取回复
	 */
	private void getReply() {
		GetReply task = new GetReply();
		task.execute("");
	}

	/**
	 * 异步任务，用户该任务下的全部回复
	 */
	private class GetReply extends
			AsyncTask<String, Integer, LinkedList<Reply>> {
		LinkedList<Reply> replys = new LinkedList<Reply>();
		
		@Override
		protected LinkedList<Reply> doInBackground(String... arg0) {
			try {
				InputStream inputStream = this.getClass().getClassLoader()
						.getResourceAsStream("GetReply.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strTaskID",
						task.getTaskId());
				data = string.getBytes();
				replyJSON = WebServiceRequest.SendPost(inputStream, data,
						"GetReplyResult");
				replys = parseJSON(replyJSON);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return replys;
		}

		@Override
		protected void onPostExecute(LinkedList<Reply> replys) {
			if (replys.size() != 0) {
				Reply reply = replys.getLast();
				updateReplyView(reply);
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}

	}

	/**
	 * 解析json字符串
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */
	private LinkedList<Reply> parseJSON(String json) throws Exception {
		LinkedList<Reply> replys = new LinkedList<Reply>();
		if (json != null) {
			JSONObject object = new JSONObject(json);
			int rows = Integer.parseInt(object.getString("rows"));
			if (rows > 0) {
				JSONArray array = new JSONArray(object.getString("Items"));
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Reply reply = new Reply();
					reply.setReplyId(obj.getString("SID"));
					reply.setTaskId(obj.getString("TASKID"));
					reply.setUserId(obj.getString("USERID"));
					reply.setUserEmail(obj.getString("MAILADDR"));
					reply.setReplyContent(obj.getString("REPLY"));
					reply.setCreateDate(obj.getString("CREATEDATE").replaceAll(
							"/", "-"));
					replys.add(reply);
				}
			} else {
				System.out.println("没有数据");
			}
		} else {
			Common.ToastIfNetworkProblem(getApplicationContext());
		}
		return replys;
	}

	/**
	 * 更新截止日期显示视图
	 * 
	 * @param dateTime
	 */
	public void updateDateView(String dateTime) {
		this.enddate = dateTime;
		if (!dateTime.equals("")) {
			String enddateStr = "截止至 ";
			enddateStr += TimeConvertTool.convertToSpecialEnddateStr(enddate);
			enddateTextView.setText(enddateStr);
			this.changeDateView();
		} else {
			enddateTextView.setText(dateTime);
			enddateiImageView
					.setImageResource(R.drawable.wl_taskdetails_icon_deadline);
			this.remindnum = "";
			this.remindtype = "";
			this.updateClockTextView(remindnum, remindtype);
		}
	}

	/**
	 * 更新提醒提前量显示视图
	 * 
	 * @param remindnum
	 * @param remindtype
	 */
	public void updateClockTextView(String remindnum, String remindtype) {
		this.remindnum = remindnum;
		this.remindtype = remindtype;
		if (!remindtype.equals("") && !remindnum.equals("")) {
			switch (Integer.parseInt(remindtype)) {
			case 0: {
				clock = "提前" + remindnum + "天提醒";
				break;
			}
			case 1: {
				clock = "提前" + remindnum + "小时提醒";
				break;
			}
			case 2: {
				clock = "提前" + remindnum + "分钟提醒";
				break;
			}
			default:
				break;
			}
			clockTextView.setText(clock);
			this.changeClockView();
		} else {
			clockTextView.setText("");
			clockiImageView
					.setImageResource(R.drawable.wl_taskdetails_icon_clock);
		}
	}

	/**
	 * 获取该任务所有接收人
	 */
	private void getTaskReceivers() {
		new GetTaskAllReceivers(task.getTaskId()).execute("");
	}

	/**
	 * 异步任务，用于获取某项任务下的所有接收人
	 * 
	 * @author Silocean
	 * 
	 */
	private class GetTaskAllReceivers extends
			AsyncTask<String, Integer, String> {

		private String taskId;

		public GetTaskAllReceivers(String taskId) {
			this.taskId = taskId;
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				InputStream inputStream = this.getClass().getClassLoader()
						.getResourceAsStream("GetTaskDDetail.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strTaskID",
						taskId);
				data = string.getBytes();
				receiverJSON = WebServiceRequest.SendPost(inputStream, data,
						"GetTaskDDetailResult");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return receiverJSON;
		}

		@Override
		protected void onPostExecute(String json) {
			try {
				receivers = parseReceiverJSON(json);
				receiversId = parseReceiverIdJSON(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}

	}

	/**
	 * 解析获取接收人返回的json字符串
	 * 
	 * @param json
	 * @return
	 */
	private ArrayList<String> parseReceiverJSON(String json) throws Exception {
		ArrayList<String> receivers = new ArrayList<String>();
		if (json != null) {
			JSONObject object = new JSONObject(json);
			int rows = Integer.parseInt(object.getString("rows"));
			if (rows > 0) {
				JSONArray array = new JSONArray(object.getString("Items"));
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					receivers.add(obj.getString("TOMAILADDR"));
				}
			}
		} else {
			Common.ToastIfNetworkProblem(getApplicationContext());
		}
		return receivers;
	}

	/**
	 * 解析获取接收人ID返回的json字符串
	 * 
	 * @param json
	 * @return
	 */
	private ArrayList<String> parseReceiverIdJSON(String json) throws Exception {
		ArrayList<String> receiversId = new ArrayList<String>();
		if (json != null) {
			JSONObject object = new JSONObject(json);
			int rows = Integer.parseInt(object.getString("rows"));
			if (rows > 0) {
				JSONArray array = new JSONArray(object.getString("Items"));
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					receiversId.add(obj.getString("TOUSERID"));
				}
			}
		} else {
			Common.ToastIfNetworkProblem(getApplicationContext());
		}
		return receiversId;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.taskdetails_checkbox: {
			if (isComplete) { // 如果任务状态为已完成
				this.updateTaskStatus(task.getTaskId(), task.getTaskFrom(),
						TASKNORMAL, COMPLETEORCANCELTONORMAL);
				checkboxImageView.setImageResource(R.drawable.wl_task_checkbox);
				this.titleEditText.setEnabled(true);
				this.taskReceiversIcon.setEnabled(true);
				this.deadlineRelativeLayout.setEnabled(true);
				this.clockRelativeLayout.setEnabled(true);
				this.starIcon.setEnabled(true);
				updateDateView(enddate);
				updateClockTextView(remindnum, remindtype);
				isComplete = false;
			} else { // 如果任务状态为未完成
				this.updateTaskStatus(task.getTaskId(), task.getTaskFrom(),
						TASKCOMPLETE, NORMALTOCOMPLETEORCANCEL);
				checkboxImageView
						.setImageResource(R.drawable.wl_task_checkbox_checked_pressed);
				this.disableView();
				isComplete = true;
			}
			break;
		}
		case R.id.task_receivers_icon: {
			Intent intent = new Intent(getApplicationContext(),
					ReceiversActivity.class);
			intent.putExtra("title", task.getSubject());
			intent.putStringArrayListExtra("receivers", receivers);
			intent.putStringArrayListExtra("receiversId", receiversId);
			startActivityForResult(intent, 3);
			break;
		}
		case R.id.taskdetais_deadline: {
			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
					TaskDetailsActivity.this, enddate);
			dateTimePicKDialog.dateTimePicKDialog();
			break;
		}
		case R.id.taskdetais_clock: {
			if (this.enddate.equals("")) {
				Toast.makeText(getApplicationContext(), "请先设定截止日期",
						Toast.LENGTH_SHORT).show();
			} else {
				ClockDialogUtil clockDialog = new ClockDialogUtil(
						TaskDetailsActivity.this, this.remindnum,
						this.remindtype);
				clockDialog.showClockDialog();
			}
			break;
		}
		case R.id.task_reply_layout: {
			Intent intent = new Intent(getApplicationContext(),
					ReplyActivity.class);
			intent.putExtra("title", task.getSubject());
			intent.putExtra("taskId", task.getTaskId());
			startActivityForResult(intent, 2);
			this.overridePendingTransition(R.anim.translate_in,
					R.anim.translate_out);
			break;
		}
		case R.id.taskreply_reply: {
			String content = replyContentEditText.getText().toString().trim();
			if (content.equals("")) {
				Toast.makeText(getApplicationContext(), "回复不能为空",
						Toast.LENGTH_SHORT).show();
			} else {
				addReply(task.getTaskId(), CommonUser.USERID,
						CommonUser.USEREMAIL, content);
			}
			replyContentEditText.setText("");
			break;
		}
		case R.id.taskdetails_icon: {
			if (priority != null) {
				if (priority.equals("") || priority.equals("0")) {
					this.updateTaskStar(task.getTaskId(), "1");
					starIcon.setImageResource(R.drawable.wl_detail_ribbon_selected);
					priority = "1";
				} else {
					this.updateTaskStar(task.getTaskId(), "0");
					starIcon.setImageResource(R.drawable.wl_task_ribbon);
					priority = "0";
				}
			}
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 添加回复
	 * 
	 * @param taskId
	 * @param userid
	 * @param useremail
	 * @param content
	 */
	private void addReply(String taskId, String userid, String useremail,
			String content) {
		new AddReply(taskId, userid, useremail, content).execute("");
	}

	/**
	 * 异步任务，用户添加回复
	 * 
	 * @author Silocean
	 * 
	 */
	private class AddReply extends AsyncTask<String, Integer, String> {

		private String taskId;
		private String userId;
		private String userEmail;
		private String content;

		public AddReply(String taskId, String userId, String userEmail,
				String content) {
			this.taskId = taskId;
			this.userId = userId;
			this.userEmail = userEmail;
			this.content = content;
		}

		@Override
		protected String doInBackground(String... params) {
			String json = "";
			try {
				InputStream inputStream = this.getClass().getClassLoader()
						.getResourceAsStream("AddReply.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data)
						.replaceAll("\\&strTaskID", taskId)
						.replaceAll("\\&strUserID", userId)
						.replaceAll("\\&strMAILADDR", userEmail)
						.replaceAll("\\&strATTFILES", "")
						.replaceAll("\\&strREPLY", content);
				data = string.getBytes();
				json = WebServiceRequest.SendPost(inputStream, data,
						"AddReplyResult");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		@Override
		protected void onPostExecute(String json) {
			try {
				if (parseUpdateJSON(json)) {
					Reply reply = new Reply();
					reply.setTaskId(taskId);
					reply.setUserId(userId);
					reply.setUserEmail(userEmail);
					reply.setReplyContent(content);
					reply.setCreateDate(TimeConvertTool
							.convertToString(new Date()));
					updateReplyView(reply);
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
	 * 更新任务星标状态
	 * 
	 * @param taskId
	 * @param status
	 */
	private void updateTaskStar(String taskId, String status) {
		new SetStarTask(taskId, status).execute("");
		isTaskStarChange = true;
		bundle.putBoolean("isTaskStarChange", isTaskStarChange);
		bundle.putString("taskStar", status);
		bundle.putInt("position", position);
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

		public SetStarTask(String taskId, String status) {
			this.taskId = taskId;
			this.status = status;
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
				} else {
					Toast.makeText(getApplicationContext(), "设置星标任务失败",
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 1: // 更新回复
			this.updateReplyView((Reply) data.getSerializableExtra("reply"));
			break;
		case 3: // 更新接收人
			isReceiversChange = data
					.getBooleanExtra("isReceiversChange", false);
			receivers = data.getStringArrayListExtra("receivers");
			receiversId = data.getStringArrayListExtra("receiversId");
			break;
		default:
			break;
		}
	}

	/**
	 * 构建接收人字符串
	 * 
	 * @param receivers
	 * @return
	 */
	private StringBuilder constructReceiversString(ArrayList<String> receivers) {
		StringBuilder sb = new StringBuilder();
		if (receivers.size() != 0) {
			for (int i = 0; i < receivers.size(); i++) {
				sb.append("<string>");
				sb.append(receivers.get(i));
				sb.append("</string>");
				if (i != receivers.size() - 1) {
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
	 * 更新下部回复界面视图
	 * 
	 * @param reply
	 */
	private void updateReplyView(Reply reply) {
		taskReplyRelativeLayout.setVisibility(View.VISIBLE);
		replyEmailTextView.setText(reply.getUserEmail());
		replyContentTextView.setText(replyContent);
		replyTimeTextView.setText(TimeConvertTool.calDateTime(reply
				.getCreateDate()));
	}

	@Override
	public void finish() {
		subject = this.titleEditText.getText().toString().trim();
		if (!subject.equals("")) {
			if (!this.subject.equals(task.getSubject())
					|| !this.enddate.equals(task.getEnddate())
					|| !(this.remindnum + "").equals(task.getRemindnum())
					|| !(this.remindtype + "").equals(task.getRemindtype())
					|| isReceiversChange) {
				isTaskChange = true;
				this.updateTask();
			}
			Intent intent = new Intent();
			intent.putExtra("bundle", bundle);
			setResult(1, intent);
			super.finish();
		} else {
			Toast.makeText(getApplicationContext(), "标题不能为空",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 更新任务详细信息
	 */
	private void updateTask() {
		new UpdateTask().execute("");
		bundle.putBoolean("isTaskChange", isTaskChange);
		bundle.putInt("position", position);
		bundle.putString("taskJSON", constructTaskJSON());
	}

	/**
	 * 异步任务，用户更新任务详细信息
	 * 
	 * @author Silocean
	 * 
	 */
	private class UpdateTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			String json = null;
			try {
				InputStream inputStream = this.getClass().getClassLoader()
						.getResourceAsStream("UpdateTask.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data)
						.replaceAll("\\&strTaskID", task.getTaskId())
						.replaceAll("\\&USERID", task.getUserId())
						.replaceAll("\\&MFROM", task.getTaskFrom())
						.replaceAll("\\&SUBJECT", subject)
						.replaceAll("\\&PRIORITY", task.getPriority())
						.replaceAll("\\&ENDDATE", enddate)
						.replaceAll("\\&REMINDTYPE", remindtype)
						.replaceAll("\\&REMINDNUM", remindnum)
						.replaceAll("\\&ATTFILES", "")
						.replaceAll("<string>\\&string</string>",
								constructReceiversString(receivers).toString());
				data = string.getBytes();
				json = WebServiceRequest.SendPost(inputStream, data,
						"UpdateTaskResult");
				for (int i = 0; i < receivers.size(); i++) {
					if (!receivers.get(i).equals(CommonUser.USEREMAIL)) {
						System.out.println(receiversId.get(i));
						sendCustomMessageWithAlias(task.getTaskFrom(),
								receiversId.get(i));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (parseUpdateJSON(result)) {
					// Toast.makeText(getApplicationContext(), "更改任务信息成功",
					// Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "更改任务信息失败",
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
	 * 构造任务json字符串
	 * 
	 * @return
	 */
	private String constructTaskJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"SID\":");
		sb.append("\"" + task.getTaskId() + "\",");
		sb.append("\"USERID\":");
		sb.append("\"" + task.getUserId() + "\",");
		sb.append("\"MFROM\":");
		sb.append("\"" + task.getTaskFrom() + "\",");
		sb.append("\"SUBJECT\":");
		sb.append("\"" + this.subject + "\",");
		sb.append("\"PRIORITY\":");
		sb.append("\"" + this.priority + "\",");
		sb.append("\"ENDDATE\":");
		sb.append("\"" + this.enddate + "\",");
		sb.append("\"REMINDTYPE\":");
		sb.append("\"" + this.remindtype + "\",");
		sb.append("\"REMINDNUM\":");
		sb.append("\"" + this.remindnum + "\",");
		sb.append("\"ATTFILES\":");
		sb.append("\"\",");
		sb.append("\"CREATEDATE\":");
		sb.append("\"" + task.getCreateDate() + "\"}");
		return sb.toString();
	}

	/**
	 * 更新任务执行状态
	 * 
	 * @param taskId
	 * @param useremail
	 * @param status
	 * @param tag
	 */
	private void updateTaskStatus(String taskId, String useremail,
			String status, int tag) {
		new UpdateTaskStatus(taskId, useremail, status).execute("");
		isTaskStatusChange = true;
		bundle.putBoolean("isTaskStatusChange", isTaskStatusChange);
		bundle.putInt("position", position);
		bundle.putInt("tag", tag);
	}

	/**
	 * 异步任务，用于更新任务执行状态
	 * 
	 * @author Silocean
	 * 
	 */
	private class UpdateTaskStatus extends AsyncTask<String, Integer, String> {

		private String taskId = null;
		private String useremail = null;
		private String status = null;

		public UpdateTaskStatus(String taskId, String useremail, String status) {
			this.taskId = taskId;
			this.useremail = useremail;
			this.status = status;
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
					// Toast.makeText(getApplicationContext(), "更改任务状态成功",
					// Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "更改任务状态失败",
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
	 * 解析更新任务返回的json字符串
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

	private void sendCustomMessageWithAlias(String mfrom, String receiverId) {
		JPushClient client = new JPushClient(
				JPushUtil.getMasterSecret(getApplicationContext()),
				JPushUtil.getAppKey(getApplicationContext()));
		MessageResult result = client.sendCustomMessageWithAlias(
				JPushUtil.getRandomSendNo(), receiverId.replaceAll("-", "_"),
				mfrom, "任务信息发生变更");
		System.out.println(result.getErrcode() + "===" + result.getErrmsg());
	}
	
}
