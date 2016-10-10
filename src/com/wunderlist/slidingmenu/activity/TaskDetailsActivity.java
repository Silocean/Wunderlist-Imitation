package com.wunderlist.slidingmenu.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.entity.CommonUser;
import com.wunderlist.entity.Reply;
import com.wunderlist.entity.Task;
import com.wunderlist.tools.ClockDialogUtil;
import com.wunderlist.tools.DateTimePickDialogUtil;
import com.wunderlist.tools.MyActivityManager;
import com.wunderlist.tools.StreamTool;
import com.wunderlist.tools.TimeConvertTool;
import com.wunderlist.tools.WebServiceRequest;

public class TaskDetailsActivity extends ActionbarBaseActivity implements
		OnClickListener {
	
	private static final String TASKNORMAL = "1";
	private static final String TASKCOMPLETE = "2";
	
	private static final int NORMALTOCOMPLETEORCANCEL = 1;
	private static final int COMPLETEORCANCELTONORMAL = 2;

	private ImageView checkboxImageView;
	private EditText titleEditText;
	private RelativeLayout receiversRelativeLayout;
	private RelativeLayout deadlineRelativeLayout;
	private RelativeLayout clockRelativeLayout;
	private EditText noteEditText;
	private ImageView enddateiImageView;
	private TextView enddateTextView;
	private ImageView clockiImageView;
	private TextView clockTextView;
	private RelativeLayout taskReplyRelativeLayout;
	private TextView replyEmailTextView;
	private TextView replyContentTextView;
	private TextView replyTimeTextView;
	private TextView receiversTextView;

	private Button replyButton;
	
	private boolean isComplete = false;
	
	private int position = 0;

	private Task task = null;
	private String barTitle = null;
	private String subject = null;
	private String enddate = null;
	private String clock = null;
	private String note = null;
	private String remindnum = null;
	private String remindtype = null;
	private boolean isReceiversChange = false;
	private ArrayList<String> receivers = new ArrayList<String>();
	private boolean isTaskChange = false;
	private boolean isTaskStatusChange = false;
	
	private Bundle bundle = new Bundle();
	
	private StringBuilder sb = new StringBuilder();

	private String replyJSON = "";
	private String receiverJSON = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_task_details);
		MyActivityManager.addActivity("TaskDetailsActivity", this);
		checkboxImageView = (ImageView) findViewById(R.id.taskdetails_checkbox);
		checkboxImageView.setOnClickListener(this);
		titleEditText = (EditText) findViewById(R.id.taskdetails_title);
		titleEditText.setOnClickListener(this);
		receiversRelativeLayout = (RelativeLayout) findViewById(R.id.taskdetails_receivers);
		receiversRelativeLayout.setOnClickListener(this);
		deadlineRelativeLayout = (RelativeLayout) findViewById(R.id.taskdetais_deadline);
		deadlineRelativeLayout.setOnClickListener(this);
		clockRelativeLayout = (RelativeLayout) findViewById(R.id.taskdetais_clock);
		clockRelativeLayout.setOnClickListener(this);
		noteEditText = (EditText) findViewById(R.id.taskdetails_note);
		noteEditText.setOnClickListener(this);
		enddateiImageView = (ImageView) findViewById(R.id.taskdetails_deadline_icon);
		enddateTextView = (TextView) findViewById(R.id.taskdetails_deadline_text);
		clockiImageView = (ImageView) findViewById(R.id.taskdetails_clock_icon);
		clockTextView = (TextView) findViewById(R.id.taskdetails_clock_text);
		replyButton = (Button) findViewById(R.id.taskdetails_reply);
		replyButton.setOnClickListener(this);
		taskReplyRelativeLayout = (RelativeLayout) findViewById(R.id.task_reply_layout);
		replyEmailTextView = (TextView) findViewById(R.id.task_reply_email);
		replyContentTextView = (TextView) findViewById(R.id.task_reply_content);
		replyTimeTextView = (TextView) findViewById(R.id.task_reply_time);
		receiversTextView = (TextView) findViewById(R.id.taskdetails_receivers_text);
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
		if(isComplete) {
			checkboxImageView.setImageResource(R.drawable.wl_task_checkbox_checked_pressed);
		}
		task = (Task) getIntent().getSerializableExtra("task");
		barTitle = getIntent().getStringExtra("title");
		enddate = task.getEnddate();
		remindnum = task.getRemindnum();
		remindtype = task.getRemindtype();
		super.setTitle(barTitle);
		titleEditText.setText(task.getSubject());
		subject = task.getSubject();
		note = task.getDisc();
		noteEditText.setText(note);
		this.updateDateView(enddate);
		this.updateClockTextView(task.getRemindnum(), task.getRemindtype());
		if (!task.getUserId().equals(CommonUser.USERID) || isComplete) { // 如果该任务不是用户自己发起的或者该任务已完成
			this.disableView();
		}
	}
	
	/**
	 * 把某些控件设置为不可用
	 */
	private void disableView() {
		this.titleEditText.setEnabled(false);
		this.receiversRelativeLayout.setEnabled(false);
		this.deadlineRelativeLayout.setEnabled(false);
		this.clockRelativeLayout.setEnabled(false);
		this.noteEditText.setEnabled(false);
		this.enddateTextView.setTextColor(getResources().getColor(R.color.listitem_text_complete_color));
		this.clockTextView.setTextColor(getResources().getColor(R.color.groupname_color_normal));
		this.enddateiImageView.setImageResource(R.drawable.wl_taskdetails_icon_deadline);
		this.clockiImageView.setImageResource(R.drawable.wl_taskdetails_icon_clock);
	}
	
	/**
	 * 根据截止日期更新dateView
	 */
	private void changeDateView() {
		if(TimeConvertTool.compareDate(enddate)) {
			enddateiImageView.setImageResource(R.drawable.wl_taskdetails_icon_deadline_active);
			enddateTextView.setTextColor(getResources().getColor(R.color.task_date_active_text_color));
		} else {
			enddateiImageView.setImageResource(R.drawable.wl_taskdetails_icon_deadline_overdue);
			enddateTextView.setTextColor(getResources().getColor(R.color.task_date_overdue_text_color));
		}
		if(!remindnum.equals("") && !remindtype.equals("")) {
			this.changeClockView();
		}
	}

	/**
	 * 根据提醒时间更新clockView
	 */
	private void changeClockView() {
		if(TimeConvertTool.judgeClock(enddate, remindnum, remindtype)) {
			clockiImageView.setImageResource(R.drawable.wl_taskdetails_icon_clock_active);
			clockTextView.setTextColor(getResources().getColor(R.color.task_date_active_text_color));
		} else { // 提醒已过期
			clockiImageView.setImageResource(R.drawable.wl_taskdetails_icon_clock_overdue);
			clockTextView.setTextColor(getResources().getColor( R.color.task_date_overdue_text_color));
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
				taskReplyRelativeLayout.setVisibility(View.VISIBLE);
				replyEmailTextView.setText(reply.getUserEmail());
				replyContentTextView.setText(reply.getReplyContent());
				replyTimeTextView.setText(TimeConvertTool.calDateTime(reply
						.getCreateDate()));
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
					reply.setCreateDate(obj.getString("CREATEDATE"));
					replys.add(reply);
				}
			} else {
				System.out.println("没有数据");
			}
		} else {
			System.out.println("网络连接出现问题");
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
		if(enddate.equals("1900/1/1 0:00:00")) {
			enddateTextView.setText("");
		} else {
			if (!dateTime.equals("")) {
				String enddateStr = "截止至 ";
				enddateStr += TimeConvertTool.convertToSpecialEnddateStr(enddate);
				enddateTextView.setText(enddateStr);
				this.changeDateView();
			} else {
				enddateTextView.setText(dateTime);
				enddateiImageView
						.setImageResource(R.drawable.wl_taskdetails_icon_deadline);
			}
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
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (receivers.size() > 0) {
				receiversTextView.setText(receivers.size() + "个接收人");
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
	 * @return 返回数据条目数量
	 */
	private ArrayList<String> parseReceiverJSON(String json) throws Exception {
		ArrayList<String> receivers = new ArrayList<String>();
		if (json != null) {
			JSONObject object = new JSONObject(json);
			int rows = Integer.parseInt(object.getString("rows"));
			if(rows > 0) {
				JSONArray array = new JSONArray(object.getString("Items"));
				for(int i=0; i<array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					receivers.add(obj.getString("TOMAILADDR"));
				}
			}
		} else {
			System.out.println("网络连接出现问题");
		}
		return receivers;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.taskdetails_checkbox: {
			//Toast.makeText(getApplicationContext(), "标记", Toast.LENGTH_SHORT).show();
			if(isComplete) { // 如果任务状态为已完成
				this.updataTaskStatus(task.getTaskId(), task.getTaskFrom(), TASKNORMAL, COMPLETEORCANCELTONORMAL);
				checkboxImageView.setImageResource(R.drawable.wl_task_checkbox);
				this.titleEditText.setEnabled(true);
				this.receiversRelativeLayout.setEnabled(true);
				this.deadlineRelativeLayout.setEnabled(true);
				this.clockRelativeLayout.setEnabled(true);
				this.noteEditText.setEnabled(true);
				updateDateView(enddate);
				updateClockTextView(remindnum, remindtype);
				isComplete = false;
			} else { // 如果任务状态为未完成
				this.updataTaskStatus(task.getTaskId(), task.getTaskFrom(), TASKCOMPLETE, NORMALTOCOMPLETEORCANCEL);
				checkboxImageView.setImageResource(R.drawable.wl_task_checkbox_checked_pressed);
				this.disableView();
				isComplete = true;
			}
			break;
		}
		case R.id.taskdetails_receivers: {
			Intent intent = new Intent(getApplicationContext(),
					ReceiversActivity.class);
			intent.putExtra("title", task.getSubject());
			intent.putStringArrayListExtra("receivers", receivers);
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
			ClockDialogUtil clockDialog = new ClockDialogUtil(
					TaskDetailsActivity.this, task.getRemindnum(),
					task.getRemindtype());
			clockDialog.showClockDialog();
			break;
		}
		case R.id.taskdetails_note: {
			Intent intent = new Intent(getApplicationContext(),
					NoteActivity.class);
			intent.putExtra("taskDisc", note);
			intent.putExtra("title", task.getSubject());
			startActivityForResult(intent, 1);
			break;
		}
		case R.id.taskdetails_reply: {
			Intent intent = new Intent(getApplicationContext(),
					ReplyActivity.class);
			intent.putExtra("title", task.getSubject());
			intent.putExtra("taskId", task.getTaskId());
			startActivityForResult(intent, 2);
			this.overridePendingTransition(R.anim.translate_in,
					R.anim.translate_out);
			break;
		}
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 1: // 更新回复
			this.updateReplyView((Reply) data.getSerializableExtra("reply"));
			break;
		case 2: // 更新笔记
			this.updateNoteEditText(data.getStringExtra("note"));
			break;
		case 3: // 更新接收人
			isReceiversChange = data.getBooleanExtra("isReceiversChange", false);
			receivers = data.getStringArrayListExtra("receivers");
			this.updateReceiverView(data.getIntExtra("receiversNum", 0));
			break;
		default:
			break;
		}
	}
	
	/**
	 * 构建接收人字符串
	 * @param receivers
	 * @return
	 */
	private StringBuilder constructReceiversString(ArrayList<String> receivers) {
		if(receivers.size() != 0) {
			for(int i=0; i<receivers.size(); i++) {
				sb.append("<string>");
				sb.append(receivers.get(i));
				sb.append("</string>");
				if(i != receivers.size()-1) {
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
	 * 更新接收人视图
	 * 
	 * @param
	 * @param receiversStr
	 */
	private void updateReceiverView(int receiverNum) {
		receiversTextView.setText(receiverNum + "个接收人");
	}

	/**
	 * 更新笔记视图
	 * 
	 * @param note
	 */
	private void updateNoteEditText(String note) {
		this.noteEditText.setText(note);
	}

	/**
	 * 更新下部回复界面视图
	 * 
	 * @param reply
	 */
	private void updateReplyView(Reply reply) {
		taskReplyRelativeLayout.setVisibility(View.VISIBLE);
		replyEmailTextView.setText(reply.getUserEmail());
		replyContentTextView.setText(reply.getReplyContent());
		replyTimeTextView.setText(TimeConvertTool.calDateTime(reply.getCreateDate()));
	}

	@Override
	public void finish() {
		subject = this.titleEditText.getText().toString().trim();
		if(!subject.equals("")) {
			note = this.noteEditText.getText().toString().trim();
			if (!this.subject.equals(task.getSubject())
					|| !this.enddate.equals(task.getEnddate())
					|| !(this.remindnum + "").equals(task.getRemindnum())
					|| !(this.remindtype + "").equals(task.getRemindtype())
					|| !this.note.equals(task.getDisc()) || isReceiversChange) {
				isTaskChange = true;
				updateTask();
			}
			Intent intent = new Intent();
			intent.putExtra("bundle", bundle);
			setResult(1, intent);
			super.finish();
		} else {
			Toast.makeText(getApplicationContext(), "标题不能为空", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 更新任务
	 */
	private void updateTask() {
		String json = null;
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("UpdateTask.xml");
			byte[] data = StreamTool.read(inputStream);
			String string = new String(data).replaceAll("\\&strTaskID", task.getTaskId())
					.replaceAll("\\&USERID", task.getUserId())
					.replaceAll("\\&MFROM", task.getTaskFrom())
					.replaceAll("\\&SUBJECT", subject).replaceAll("\\&DISC", note)
					.replaceAll("\\&PRIORITY", "").replaceAll("\\&ENDDATE", enddate)
					.replaceAll("\\&REMINDTYPE", remindtype).replaceAll("\\&REMINDNUM", remindnum)
					.replaceAll("\\&ATTFILES", "").replaceAll("<string>\\&string</string>", constructReceiversString(receivers).toString());
			//System.out.println("===" + string);
			data = string.getBytes();
			json = WebServiceRequest.SendPost(inputStream, data, "UpdateTaskResult");
			//System.out.println("++++"+json);
			if(parseUpdateJSON(json)) {
				//Toast.makeText(getApplicationContext(), "更改任务信息成功", Toast.LENGTH_SHORT).show();
				bundle.putBoolean("isTaskChange", isTaskChange);
				bundle.putInt("position", position);
				bundle.putString("taskJSON", constructTaskJSON());
			} else {
				Toast.makeText(getApplicationContext(), "更改任务信息失败",
						Toast.LENGTH_SHORT).show();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 构造任务json字符串
	 * @return
	 */
	private String constructTaskJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"SID\":");
		sb.append("\""+task.getTaskId()+"\",");
		sb.append("\"USERID\":");
		sb.append("\""+task.getUserId()+"\",");
		sb.append("\"MFROM\":");
		sb.append("\""+task.getTaskFrom()+"\",");
		sb.append("\"SUBJECT\":");
		sb.append("\""+this.subject+"\",");
		sb.append("\"DISC\":");
		sb.append("\""+this.note+"\",");
		sb.append("\"PRIORITY\":");
		sb.append("\"0\",");
		sb.append("\"ENDDATE\":");
		sb.append("\""+this.enddate+"\",");
		sb.append("\"REMINDTYPE\":");
		sb.append("\""+this.remindtype+"\",");
		sb.append("\"REMINDNUM\":");
		sb.append("\""+this.remindnum+"\",");
		sb.append("\"ATTFILES\":");
		sb.append("\"\",");
		sb.append("\"CREATEDATE\":");
		sb.append("\""+task.getCreateDate()+"\"}");
		return sb.toString();
	}
	
	/**
	 * 更改任务状态
	 * @param taskId
	 * @param useremail
	 * @param status
	 * @param tag 
	 */
	private void updataTaskStatus(String taskId, String useremail, String status, int tag) {
		String json = "";
		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("UpdateTaskStatus.xml");
			byte[] data = StreamTool.read(inputStream);
			String string = new String(data)
					.replaceAll("\\&strTaskID", taskId)
					.replaceAll("\\&strEmail", useremail)
					.replaceAll("\\&strStatus", status);
			data = string.getBytes();
			json = WebServiceRequest.SendPost(inputStream, data, "UpdateTaskStatusResult");
			if (parseUpdateJSON(json)) {
				//Toast.makeText(getApplicationContext(), "更改任务状态成功", Toast.LENGTH_SHORT).show();
				isTaskStatusChange = true;
				bundle.putBoolean("isTaskStatusChange", isTaskStatusChange);
				bundle.putInt("position", position);
				bundle.putInt("tag", tag);
			} else {
				Toast.makeText(getApplicationContext(), "更改任务状态失败", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
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

}
