package com.trinfo.wunderlist.activity;

import java.io.InputStream;
import java.util.ArrayList;

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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.api.JPushClient;
import cn.jpush.api.MessageResult;

import com.trinfo.wunderlist.R;
import com.trinfo.wunderlist.entity.Common;
import com.trinfo.wunderlist.entity.CommonUser;
import com.trinfo.wunderlist.entity.Task;
import com.trinfo.wunderlist.tools.JPushUtil;
import com.trinfo.wunderlist.tools.MyActivityManager;
import com.trinfo.wunderlist.tools.StreamTool;
import com.trinfo.wunderlist.tools.WebServiceRequest;

/**
 * 接收人界面
 * @author Silocean
 *
 */
public class ReceiversActivity extends ActionbarBaseActivity implements OnClickListener {
	
	private ListView listView;
	private ReceiverListItemAdapter adapter;
	
	private Button addButton;
	
	private ArrayList<String> orignialReceivers = new ArrayList<String>();
	private ArrayList<String> receivers;
	private ArrayList<String> receiversId;
	private ArrayList<String> newAddReceivers;
	private ArrayList<String> newAddReceiversId;
	
	private String receiverJSON = "";
	private Task task = null;
	private int tag = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_receiver);
		MyActivityManager.addActivity("ReceiversActivity", this);
		listView = (ListView)findViewById(R.id.receiverlist);
		adapter = new ReceiverListItemAdapter(getApplicationContext(), R.layout.listitem_receiver);
		listView.setAdapter(adapter);
		addButton = (Button)findViewById(R.id.receiverlist_add);
		addButton.setOnClickListener(this);
		setTitle("成员列表");
		initData();
		super.onCreate(savedInstanceState);
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
				orignialReceivers.addAll(receivers);
				adapter.setData(receivers);
				adapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}

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
	 * 更新接收人列表
	 * @param receiverEmail
	 */
	public void updateReceiverList(String receiverEmail) {
		if(!receivers.contains(receiverEmail)) {
			this.receivers.add(receiverEmail);
			this.updateReceiverList();
		} else {
			Toast.makeText(getApplicationContext(), "该接收人已存在", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 更新接收人列表
	 */
	public void updateReceiverList() {
		adapter.setData(receivers);
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 初始化界面数据
	 */
	private void initData() {
		tag = getIntent().getIntExtra("tag", 0);
		if(tag == 1) {
			task = (Task)getIntent().getSerializableExtra("task");
			new GetTaskAllReceivers(task.getTaskId()).execute("");
		} else {
			receivers = getIntent().getStringArrayListExtra("receivers");
			receiversId = getIntent().getStringArrayListExtra("receiversId");
			orignialReceivers.addAll(receivers);
			adapter.setData(receivers);
			adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 接收人数据列表填充器
	 * @author Silocean
	 *
	 */
	private class ReceiverListItemAdapter extends BaseAdapter {
		
		private ArrayList<String> list;
		private int resId;
		private LayoutInflater inflater;
		
		private ViewHolder holder;
		
		public ReceiverListItemAdapter(Context context, int resId) {
			this.resId = resId;
			this.list = new ArrayList<String>();
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setData(ArrayList<String> receivers) {
			this.list = receivers;
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			holder = new ViewHolder();
			if(convertView == null) {
				convertView = inflater.inflate(resId, null);
				holder.emailTextView = (TextView)convertView.findViewById(R.id.receiver_email_text);
				holder.deleteImageView = (ImageView)convertView.findViewById(R.id.receiver_delete_icon);
				holder.owneTextView = (TextView)convertView.findViewById(R.id.receiver_ownertextview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			final String userEmail = (String)getItem(position);
			holder.emailTextView.setText(userEmail);
			if(userEmail.equals(CommonUser.USEREMAIL)) {
				holder.deleteImageView.setVisibility(View.GONE);
				holder.owneTextView.setVisibility(View.VISIBLE);
			} else {
				holder.deleteImageView.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						receivers.remove(position);
						receiversId.remove(position);
						updateReceiverList();
					}
				});
				holder.deleteImageView.setVisibility(View.VISIBLE);
				holder.owneTextView.setVisibility(View.GONE);
			}
			return convertView;
		}
		
		private class ViewHolder {
			private TextView emailTextView;
			private ImageView deleteImageView;
			private TextView owneTextView;
		}
		
	}
	
	@Override
	public void finish() {
		Intent intent = new Intent();
		boolean isReceiversChange = true;
		if(receivers.containsAll(orignialReceivers) && orignialReceivers.containsAll(receivers)) {
			isReceiversChange = false; // 表示接收人没有改变
		}
		if(isReceiversChange && tag == 1) {
			new UpdateTask().execute("");
		} else {
			intent.putStringArrayListExtra("receivers", receivers);
			intent.putStringArrayListExtra("receiversId", receiversId);
			intent.putExtra("isReceiversChange", isReceiversChange);
			intent.putExtra("receiversNum", receivers.size());
			setResult(3, intent);
		}
		super.finish();
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
						.replaceAll("\\&SUBJECT", task.getSubject())
						.replaceAll("\\&DISC", task.getDisc())
						.replaceAll("\\&PRIORITY", task.getPriority())
						.replaceAll("\\&ENDDATE", task.getEnddate())
						.replaceAll("\\&REMINDTYPE", task.getRemindtype())
						.replaceAll("\\&REMINDNUM", task.getRemindnum())
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

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getApplicationContext(), AddReceiverActivity.class);
		intent.putStringArrayListExtra("receivers", receivers);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 1:
			newAddReceivers = data.getStringArrayListExtra("selectedContracts");
			newAddReceiversId = data.getStringArrayListExtra("selectedContractsId");
			receivers.addAll(newAddReceivers);
			receiversId.addAll(newAddReceiversId);
			adapter.setData(receivers);
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

}
