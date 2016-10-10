package com.wunderlist.slidingmenu.activity;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
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

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.wunderlist.R;
import com.wunderlist.entity.User;
import com.wunderlist.tools.AddReceiverDialogUtil;

public class ReceiversActivity extends ActionbarBaseActivity implements OnClickListener {
	
	//private String barTitle = null;
	private String receiverJSON = null;
	//private String taskId = null;
	
	private ListView listView;
	private ReceiverListItemAdapter adapter;
	
	private Button addButton;
	
	private LinkedList<String> orignalReceivers = new LinkedList<String>();
	private LinkedList<String> receivers;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_receiver);
		listView = (ListView)findViewById(R.id.receiverlist);
		adapter = new ReceiverListItemAdapter(getApplicationContext(), R.layout.listitem_receiver);
		listView.setAdapter(adapter);
		addButton = (Button)findViewById(R.id.receiverlist_add);
		addButton.setOnClickListener(this);
		initData();
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add(0, 0, 0, "添加");
		item.setIcon(R.drawable.wl_actionbar_addreceiver);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			//Toast.makeText(getApplicationContext(), "添加接收人", Toast.LENGTH_SHORT).show();
			AddReceiverDialogUtil addReceiverDialogUtil = new AddReceiverDialogUtil(ReceiversActivity.this);
			addReceiverDialogUtil.showAddReceiverDialog();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 更新接收人列表
	 * @param receiverEmail
	 */
	public void updateReceiverList(String receiverEmail) {
		if(!receivers.contains(receiverEmail)) {
			this.receivers.addLast(receiverEmail);
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
	
	private void initData() {
		//barTitle = getIntent().getStringExtra("title");
		setTitle("成员列表");
		//taskId = getIntent().getStringExtra("taskId");
		receiverJSON = getIntent().getStringExtra("json");
		try {
			receivers = this.parseGetReceiversJSON(receiverJSON);
		} catch (Exception e) {
			e.printStackTrace();
		}
		orignalReceivers.addAll(receivers);
		adapter.setData(receivers);
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 解析获取接收人返回的json字符串
	 * @param json 要解析的json字符串
	 * @return 所有接收人
	 * @throws Exception
	 */
	private LinkedList<String> parseGetReceiversJSON(String json) throws Exception {
		LinkedList<String> receivers = new LinkedList<String>();
		if(json != null) {
			JSONObject object = new JSONObject(json);
			int rows = Integer.parseInt(object.getString("rows"));
			if(rows > 0) {
				JSONArray array = new JSONArray(object.getString("Items"));
				for(int i=0; i<array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					receivers.add(obj.getString("TOMAILADDR"));
				}
			} else {
				System.out.println("没有数据");
			}
		} else {
			System.out.println("网络连接出现问题");
		}
		return receivers;
	}
	
	private class ReceiverListItemAdapter extends BaseAdapter {
		
		private LinkedList<String> list;
		private int resId;
		private LayoutInflater inflater;
		
		private ViewHolder holder;
		
		public ReceiverListItemAdapter(Context context, int resId) {
			this.resId = resId;
			this.list = new LinkedList<String>();
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setData(LinkedList<String> receivers) {
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
			if(userEmail.equals(User.USEREMAIL)) {
				holder.deleteImageView.setVisibility(View.GONE);
				holder.owneTextView.setVisibility(View.VISIBLE);
			} else {
				holder.deleteImageView.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						receivers.remove(position);
						updateReceiverList();
					}
				});
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
		if(receivers.containsAll(orignalReceivers) && orignalReceivers.containsAll(receivers)) {
			isReceiversChange = false; // 表示接收人没有改变
		}
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<receivers.size(); i++) {
			sb.append(receivers.get(i)+":");
		}
		sb.deleteCharAt(sb.length()-1);
		intent.putExtra("receiversStr", sb.toString());
		intent.putExtra("isReceiversChange", isReceiversChange);
		intent.putExtra("receiversNum", receivers.size());
		setResult(3, intent);
		super.finish();
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getApplicationContext(), AddReceiverActivity.class);
		startActivityForResult(intent, 0);
	}

}
