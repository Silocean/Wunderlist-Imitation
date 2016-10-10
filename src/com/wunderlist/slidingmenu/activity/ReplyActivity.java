package com.wunderlist.slidingmenu.activity;

import java.io.InputStream;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.entity.Common;
import com.wunderlist.entity.CommonUser;
import com.wunderlist.entity.Reply;
import com.wunderlist.tools.MyActivityManager;
import com.wunderlist.tools.StreamTool;
import com.wunderlist.tools.TimeConvertTool;
import com.wunderlist.tools.WebServiceRequest;

/**
 * 回复界面
 * @author Silocean
 *
 */
public class ReplyActivity extends ActionbarBaseActivity implements OnClickListener {
	
	private String barTitle = null;
	private String replyJSON = null;
	private String taskId = null;
	
	private RelativeLayout emptyLayout = null;
	private ListView listView = null;
	private ReplyListItemAdapter adapter = null;
	private LinkedList<Reply> mReplys = null;
	
	private EditText replyContentEditText;
	private Button replyButton;
	
	private String replyContent = null;
	
	@Override
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_reply);
		MyActivityManager.addActivity("ReplyActivity", this);
		emptyLayout =(RelativeLayout)this.findViewById(R.id.taskreply_emptylayout);
		replyContentEditText = (EditText)this.findViewById(R.id.taskreply_comment);
		replyButton = (Button)this.findViewById(R.id.taskreply_reply);
		replyButton.setOnClickListener(this);
		listView = (ListView)this.findViewById(R.id.replylist);
		adapter = new ReplyListItemAdapter(getApplicationContext(), R.layout.listitem_reply);
		listView.setAdapter(adapter);
		int width = getWindowManager().getDefaultDisplay().getWidth() - replyButton.getLayoutParams().width - 15;
		LayoutParams layoutParams = replyContentEditText.getLayoutParams();
		layoutParams.width = width;
		replyContentEditText.setLayoutParams(layoutParams);
		initData();
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * 初始化界面数据
	 */
	private void initData() {
		barTitle = getIntent().getStringExtra("title");
		setTitle(barTitle);
		taskId = getIntent().getStringExtra("taskId");
		this.getReply();
	}
	
	/**
	 * 更新回复列表
	 */
	private void updateReplyList() {
		this.getReply();
		adapter.setData(mReplys);
		adapter.notifyDataSetChanged();
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
				String string = new String(data).replaceAll("\\&strTaskID", taskId);
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
			mReplys = replys;
			if(replys.size() != 0) {
				emptyLayout.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
				adapter.setData(replys);
				adapter.notifyDataSetChanged();
				
				Reply reply = mReplys.getLast();
				Intent intent = new Intent();
				intent.putExtra("reply", reply);
				setResult(1, intent);
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
					reply.setCreateDate(obj.getString("CREATEDATE").replaceAll("/", "-"));
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
	 * 格式化回复字符串
	 * @param note
	 * @return
	 */
	private String formatReplyContent(String replyContent) {
		return replyContent.replaceAll("<[a-zA-Z]+>", "").replaceAll("</[a-zA-Z]+>", "")
				.replaceAll("&nbsp;", " ");
	}
	
	/**
	 * 回复列表数据填充器
	 * @author Silocean
	 *
	 */
	private class ReplyListItemAdapter extends BaseAdapter {
		
		private LinkedList<Reply> list;
		private int resId;
		private LayoutInflater inflater;
		
		private ViewHolder holder;
		
		public ReplyListItemAdapter(Context context, int resId) {
			this.resId = resId;
			this.list = new LinkedList<Reply>();
			this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setData(LinkedList<Reply> replys) {
			this.list = replys;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			holder = new ViewHolder();
			if(convertView == null) {
				convertView = inflater.inflate(resId, null);
				holder.replyEmailTextView = (TextView)convertView.findViewById(R.id.reply_email);
				holder.replyContentTextView = (TextView)convertView.findViewById(R.id.reply_content);
				holder.replyTimeTextView = (TextView)convertView.findViewById(R.id.reply_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			Reply reply = this.list.get(position);
			replyContent = formatReplyContent(reply.getReplyContent());
			holder.replyEmailTextView.setText(reply.getUserEmail());
			holder.replyContentTextView.setText(replyContent);
			holder.replyTimeTextView.setText(TimeConvertTool.calDateTime(reply.getCreateDate()));
			return convertView;
		}
		
		private class ViewHolder {
			private TextView replyEmailTextView;
			private TextView replyContentTextView;
			private TextView replyTimeTextView;
		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.taskreply_reply:
			String content = replyContentEditText.getText().toString().trim();
			if(content.equals("")) {
				Toast.makeText(getApplicationContext(), "回复不能为空", Toast.LENGTH_SHORT).show();
			} else {
				addReply(taskId, CommonUser.USERID, CommonUser.USEREMAIL, content);
			}
			replyContentEditText.setText("");
			break;
		default:
			break;
		}
	}
	
	/**
	 * 添加回复
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
	 * @author Silocean
	 *
	 */
	private class AddReply extends AsyncTask<String, Integer, String> {
		
		private String taskId;
		private String userid;
		private String useremail;
		private String content;

		public AddReply(String taskId, String userid, String useremail, String content) {
			this.taskId = taskId;
			this.userid = userid;
			this.useremail = useremail;
			this.content = content;
		}

		@Override
		protected String doInBackground(String... params) {
			String json = "";
			try {
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("AddReply.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strTaskID", taskId).replaceAll("\\&strUserID", userid).replaceAll("\\&strMAILADDR", useremail)
						.replaceAll("\\&strATTFILES", "").replaceAll("\\&strREPLY", content);
				data = string.getBytes();
				json = WebServiceRequest.SendPost(inputStream, data, "AddReplyResult");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		@Override
		protected void onPostExecute(String json) {
			try {
				if(parseSaveJSON(json)) {
					updateReplyList();
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
	 * 解析json字符串（用户解析添加回复后返回的json数据）
	 * @param json
	 * @return
	 */
	private boolean parseSaveJSON(String json) throws Exception {
		if(json != null) {
			JSONObject object = new JSONObject(json);
			if(object.getString("msg").equals("保存成功!")) {
				return true;
			}
		}
		return false;
	}

}
