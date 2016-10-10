package com.wunderlist.slidingmenu.activity;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.entity.Reply;
import com.wunderlist.entity.Task;
import com.wunderlist.entity.User;
import com.wunderlist.tools.ClockDialogUtil;
import com.wunderlist.tools.DateTimePickDialogUtil;
import com.wunderlist.tools.StreamTool;
import com.wunderlist.tools.TimeConvertTool;
import com.wunderlist.tools.WebServiceRequest;

public class TaskDetailsActivity extends ActionbarBaseActivity implements
		OnClickListener {

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
	private ImageView replyHeadImageView;
	private TextView replyEmailTextView;
	private TextView replyContentTextView;
	private TextView replyTimeTextView;
	private TextView receiversTextView;

	private Button replyButton;

	private Task task = null;
	private String barTitle = null;
	private String enddate = null;
	private String clock = null;
	private int remindnum = 0;
	private int remindtype = 0;
	private boolean isReceiversChange = false;
	
	private String replyJSON = "";
	private String receiverJSON = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_task_details);
		checkboxImageView = (ImageView) findViewById(R.id.taskdetails_checkbox);
		checkboxImageView.setOnClickListener(this);
		titleEditText = (EditText) findViewById(R.id.taskdetails_title);
		titleEditText.setOnClickListener(this);
		receiversRelativeLayout = (RelativeLayout)findViewById(R.id.taskdetails_receivers);
		receiversRelativeLayout.setOnClickListener(this);
		deadlineRelativeLayout = (RelativeLayout) findViewById(R.id.taskdetais_deadline);
		deadlineRelativeLayout.setOnClickListener(this);
		clockRelativeLayout = (RelativeLayout) findViewById(R.id.taskdetais_clock);
		clockRelativeLayout.setOnClickListener(this);
		noteEditText = (EditText) findViewById(R.id.taskdetails_note);
		noteEditText.setOnClickListener(this);
		enddateiImageView = (ImageView)findViewById(R.id.taskdetails_deadline_icon);
		enddateTextView = (TextView)findViewById(R.id.taskdetails_deadline_text);
		clockiImageView = (ImageView)findViewById(R.id.taskdetails_clock_icon);
		clockTextView = (TextView)findViewById(R.id.taskdetails_clock_text);
		replyButton = (Button)findViewById(R.id.taskdetails_reply);
		replyButton.setOnClickListener(this);
		taskReplyRelativeLayout = (RelativeLayout)findViewById(R.id.task_reply_layout);
		replyHeadImageView = (ImageView)findViewById(R.id.task_reply_headimage);
		replyEmailTextView = (TextView)findViewById(R.id.task_reply_email);
		replyContentTextView = (TextView)findViewById(R.id.task_reply_content);
		replyTimeTextView = (TextView)findViewById(R.id.task_reply_time);
		receiversTextView = (TextView)findViewById(R.id.taskdetails_receivers_text);
		this.initData();
		this.getReply();
		this.getTaskReceivers();
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * 填充界面数据
	 */
	private void initData() {
		task = (Task) getIntent().getSerializableExtra("task");
		barTitle = getIntent().getStringExtra("title");
		enddate = task.getEnddate();
		super.setTitle(barTitle);
		titleEditText.setText(task.getSubject());
		noteEditText.setText(task.getDisc());
		if(!enddate.equals("")) {
			enddateTextView.setText(enddate);
			this.changeDateView();
		}
		if(!task.getRemindtype().equals("") && !task.getRemindnum().equals("")) {
			remindtype = Integer.parseInt(task.getRemindtype());
			remindnum = Integer.parseInt(task.getRemindnum());
			this.changeClockView();
			switch (Integer.parseInt(task.getRemindtype())) {
			case 0: {
				clock = "提前"+task.getRemindnum()+"天提醒";
				break;
			}
			case 1: {
				clock = "提前"+task.getRemindnum()+"小时提醒";
				break;
			}
			case 2: {
				clock = "提前"+task.getRemindnum()+"分钟提醒";
				break;
			}
			default:
				break;
			}
			clockTextView.setText(clock);
		}
		if(!task.getUserId().equals(User.USERID)) { // 如果该任务不是用户自己发起的
			this.titleEditText.setEnabled(false);
			this.receiversRelativeLayout.setEnabled(false);
			this.deadlineRelativeLayout.setEnabled(false);
			this.clockRelativeLayout.setEnabled(false);
			this.noteEditText.setEnabled(false);
		}
	}
	
	/**
	 * 根据截止日期更新dateView
	 */
	private void changeDateView() {
		switch (this.judgeDate(enddate)) {
		case 0: {
			enddateiImageView.setImageResource(R.drawable.wl_taskdetails_icon_deadline_overdue);
			enddateTextView.setTextColor(getResources().getColor(R.color.task_date_overdue_text_color));
			break;
		}
		case 1: {
			enddateiImageView.setImageResource(R.drawable.wl_taskdetails_icon_deadline_active);
			enddateTextView.setTextColor(getResources().getColor(R.color.task_date_active_text_color));
			break;
		}
		default:
			break;
		}
	}
	
	/**
	 * 根据提醒时间更新clockView
	 */
	private void changeClockView() {
		switch (this.judgeClock(enddate, remindnum, remindtype)) {
		case 0: {
			clockiImageView.setImageResource(R.drawable.wl_taskdetails_icon_clock_overdue);
			clockTextView.setTextColor(getResources().getColor(R.color.task_date_overdue_text_color));
			break;
		}
		case 1: {
			clockiImageView.setImageResource(R.drawable.wl_taskdetails_icon_clock_active);
			clockTextView.setTextColor(getResources().getColor(R.color.task_date_active_text_color));
			break;
		}
		default:
			break;
		}
	}
	
	/**
	 * 判断截止日期
	 * @param enddate
	 * @return 0表示截止日期在当前日期之前，1表示之后
	 */
	private int judgeDate(String enddate) {
		Date date = TimeConvertTool.convertToDate(enddate);
		if(date.before(new Date())) {
			return 0;
		} else {
			return 1;
		}
	}
	/**
	 * 判断提醒提前量
	 * @param enddate
	 * @param remindnum
	 * @param remindtype
	 * @return 0表示截止日期减去提醒提前量在当前日期之前，1表示之后
	 */
	private int judgeClock(String enddate, int remindnum, int remindtype) {
		Date date = TimeConvertTool.convertToDate(enddate);
		switch (remindtype) {
		case 0: {
			date.setTime(date.getTime()-(remindnum*24*60*60*1000));
			break;
		}
		case 1: {
			date.setTime(date.getTime()-(remindnum*60*60*1000));
			break;
		}
		case 2: {
			date.setTime(date.getTime()-(remindnum*60*1000));
			break;
		}
		default:
			break;
		}
		if(date.before(new Date())) {
			return 0;
		} else {
			return 1;
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
	private class GetReply extends AsyncTask<String, Integer, LinkedList<Reply>> {
		LinkedList<Reply> replys = new LinkedList<Reply>();
		@Override
		protected LinkedList<Reply> doInBackground(String... arg0) {
			try {
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("GetReply.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strTaskID", task.getTaskId());
				data = string.getBytes();
				replyJSON = WebServiceRequest.SendPost(inputStream, data, "GetReplyResult");
				replys = parseJSON(replyJSON);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return replys;
		}

		@Override
		protected void onPostExecute(LinkedList<Reply> replys) {
			if(replys.size() != 0) {
				Reply reply = replys.getLast();
				taskReplyRelativeLayout.setVisibility(View.VISIBLE);
				replyEmailTextView.setText(reply.getUserEmail());
				replyContentTextView.setText(reply.getReplyContent());
				replyTimeTextView.setText(TimeConvertTool.calDateTime(reply.getCreateDate()));
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
		
	}
	
	/**
	 * 解析json字符串
	 * @param json
	 * @return
	 * @throws Exception
	 */
	private LinkedList<Reply> parseJSON(String json) throws Exception {
		LinkedList<Reply> replys = new LinkedList<Reply>();
		if(json != null) {
			JSONObject object = new JSONObject(json);
			int rows = Integer.parseInt(object.getString("rows"));
			if(rows > 0) {
				JSONArray array = new JSONArray(object.getString("Items"));
				for(int i=0; i<array.length(); i++) {
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
	 * @param dateTime
	 */
	public void updateDateView(String dateTime) {
		this.enddate = dateTime;
		this.changeDateView();
	}
	
	/**
	 * 更新提醒提前量显示视图
	 * @param remindnum
	 * @param remindtype
	 */
	public void updateClockTextView(String remindnum, String remindtype) {
		if(!remindtype.equals("") && !remindnum.equals("")) {
			this.remindnum = Integer.parseInt(remindnum);
			this.remindtype = Integer.parseInt(remindtype);
			switch (Integer.parseInt(remindtype)) {
			case 0: {
				clock = "提前"+remindnum+"天提醒";
				break;
			}
			case 1: {
				clock = "提前"+remindnum+"小时提醒";
				break;
			}
			case 2: {
				clock = "提前"+remindnum+"分钟提醒";
				break;
			}
			default:
				break;
			}
			clockTextView.setText(clock);
			this.changeClockView();
		} else {
			clockTextView.setText("");
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
	 * @author Silocean
	 *
	 */
	private class GetTaskAllReceivers extends AsyncTask<String, Integer, String> {
		
		private String taskId;
		
		public GetTaskAllReceivers(String taskId) {
			this.taskId = taskId;
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("GetTaskDDetail.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strTaskID", task.getTaskId());
				data = string.getBytes();
				receiverJSON = WebServiceRequest.SendPost(inputStream, data, "GetTaskDDetailResult");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return receiverJSON;
		}

		@Override
		protected void onPostExecute(String json) {
			int num = 0;
			try {
				num = parseReceiverJSON(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(num > 0) {
				receiversTextView.setText(num + "个接收人");
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
		
	}
	
	/**
	 * 解析获取接收人返回的json字符串
	 * @param json
	 * @return 返回数据条目数量
	 */
	private int parseReceiverJSON(String json) throws Exception {
		int num = 0;
		if(json != null) {
			JSONObject object = new JSONObject(json);
			num = Integer.parseInt(object.getString("rows"));
		} else {
			System.out.println("网络连接出现问题");
		}
		return num;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.taskdetails_checkbox: {
			break;
		}
		case R.id.taskdetails_title: {
			break;
		}
		case R.id.taskdetails_receivers: {
			Intent intent = new Intent(getApplicationContext(), ReceiversActivity.class);
			intent.putExtra("title", task.getSubject());
			intent.putExtra("json", receiverJSON);
			startActivityForResult(intent, 3);
			break;
		}
		case R.id.taskdetais_deadline: {
			DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
					TaskDetailsActivity.this, enddateTextView.getText().toString().trim());
			dateTimePicKDialog.dateTimePicKDialog(enddateTextView);
			break;
		}
		case R.id.taskdetais_clock: {
			ClockDialogUtil clockDialog = new ClockDialogUtil(TaskDetailsActivity.this);
			clockDialog.showClockDialog();
			break;
		}
		case R.id.taskdetails_note: {
			Intent intent = new Intent(getApplicationContext(),
					NoteActivity.class);
			intent.putExtra("taskDisc", task.getDisc());
			intent.putExtra("title", task.getSubject());
			startActivityForResult(intent, 1);
			break;
		}
		case R.id.taskdetails_reply: {
			Intent intent = new Intent(getApplicationContext(), ReplyActivity.class);
			intent.putExtra("title", task.getSubject());
			intent.putExtra("taskId", task.getTaskId());
			startActivityForResult(intent, 2);
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
			this.updateReplyView((Reply)data.getSerializableExtra("reply"));
			break;
		case 2: // 更新笔记
			this.updateNoteEditText(data.getStringExtra("note"));
			break;
		case 3: // 更新接收人
			isReceiversChange = data.getBooleanExtra("isReceiversChange", false);
			this.updateReceiverView(data.getIntExtra("receiversNum", 0));
			break;
		default:
			break;
		}
	}
	
	/**
	 * 更新接收人视图
	 * @param  
	 * @param receiversStr
	 */
	private void updateReceiverView(int receiverNum ) {
		receiversTextView.setText(receiverNum + "个接收人");
	}
	
	/**
	 * 更新笔记视图
	 * @param note
	 */
	private void updateNoteEditText(String note) {
		this.noteEditText.setText(note);
	}
	
	/**
	 * 更新下部回复界面视图
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
		String title = this.titleEditText.getText().toString().trim();
		String enddate = this.enddateTextView.getText().toString().trim();
		String clock = this.clockTextView.getText().toString().trim();
		String note = this.noteEditText.getText().toString().trim();
		if(!title.equals(task.getSubject()) || !enddate.equals(task.getEnddate()) || !clock.equals(clock) || !note.equals(task.getDisc()) || isReceiversChange) {
			//updateTask();
		}
		super.finish();
	}
	
}
