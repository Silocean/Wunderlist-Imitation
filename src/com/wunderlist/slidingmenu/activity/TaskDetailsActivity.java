package com.wunderlist.slidingmenu.activity;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.wunderlist.tools.StreamTool;
import com.wunderlist.tools.TimeConvertTool;
import com.wunderlist.tools.WebServiceRequest;

public class TaskDetailsActivity extends ActionbarBaseActivity implements
		OnClickListener {

	private ImageView checkboxImageView;
	private EditText titleEditText;
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

	private Button replyButton;

	private Task task = null;
	private String barTitle = null;
	private String enddate = null;
	private int remindnum = 0;
	private int remindtype = 0;
	
	private String replyJSON = "";

	private Calendar calendar = Calendar.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_task_details);
		checkboxImageView = (ImageView) findViewById(R.id.taskdetails_checkbox);
		checkboxImageView.setOnClickListener(this);
		titleEditText = (EditText) findViewById(R.id.taskdetails_title);
		titleEditText.setOnClickListener(this);
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
		this.initData();
		this.getReply();
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
		if(!task.getRemindtype().equals("") && !task.getRemindnum().equals("")) {
			remindtype = Integer.parseInt(task.getRemindtype());
			remindnum = Integer.parseInt(task.getRemindnum());
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
			switch (Integer.parseInt(task.getRemindtype())) {
			case 0: {
				clockTextView.setText("提前"+task.getRemindnum()+"天提醒");
				break;
			}
			case 1: {
				clockTextView.setText("提前"+task.getRemindnum()+"小时提醒");
				break;
			}
			case 2: {
				clockTextView.setText("提前"+task.getRemindnum()+"分提醒");
				break;
			}
			default:
				break;
			}
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
	 * 更新下部回复界面数据
	 * @param reply
	 */
	private void updateReplyView(Reply reply) {
		taskReplyRelativeLayout.setVisibility(View.VISIBLE);
		replyEmailTextView.setText(reply.getUserEmail());
		replyContentTextView.setText(reply.getReplyContent());
		replyTimeTextView.setText(TimeConvertTool.calDateTime(reply.getCreateDate()));
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
		JSONObject object = new JSONObject(json);
		int rows = Integer.parseInt(object.getString("rows"));
		LinkedList<Reply> replys = new LinkedList<Reply>();
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
		return replys;
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
		case R.id.taskdetais_deadline: {
			DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int month,
						int day) {
					Toast.makeText(getApplicationContext(),
							year + "年" + (month + 1) + "月" + day + "日", 0)
							.show();
				}
			};
			new DatePickerDialog(TaskDetailsActivity.this, dateListener,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH)).show(); // 这里不要用getApplicationContext()
			break;
		}
		case R.id.taskdetais_clock: {
			TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
				@Override
				public void onTimeSet(TimePicker view, int hour, int minute) {
					Toast.makeText(getApplicationContext(), hour+"小时"+minute+"分钟", 0).show();
				}
			};
			new TimePickerDialog(TaskDetailsActivity.this, timeListener,
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), true).show();
			break;
		}
		case R.id.taskdetails_note: {
			Intent intent = new Intent(getApplicationContext(),
					NoteActivity.class);
			intent.putExtra("taskDisc", task.getDisc());
			intent.putExtra("title", task.getSubject());
			startActivity(intent);
			break;
		}
		case R.id.taskdetails_reply: {
			Intent intent = new Intent(getApplicationContext(), ReplyActivity.class);
			intent.putExtra("title", task.getSubject());
			intent.putExtra("taskId", task.getTaskId());
			startActivityForResult(intent, 1);
			break;
		}
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 1:
			this.updateReplyView((Reply)data.getSerializableExtra("reply"));
			break;
		default:
			break;
		}
	}
	
}
