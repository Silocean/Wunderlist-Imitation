package com.trinfo.wunderlist.activity;

import java.util.ArrayList;

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

import com.trinfo.wunderlist.R;
import com.trinfo.wunderlist.entity.CommonUser;
import com.trinfo.wunderlist.tools.MyActivityManager;

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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_receiver);
		MyActivityManager.addActivity("ReceiversActivity", this);
		listView = (ListView)findViewById(R.id.receiverlist);
		adapter = new ReceiverListItemAdapter(getApplicationContext(), R.layout.listitem_receiver);
		listView.setAdapter(adapter);
		addButton = (Button)findViewById(R.id.receiverlist_add);
		addButton.setOnClickListener(this);
		initData();
		super.onCreate(savedInstanceState);
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
		setTitle("成员列表");
		receivers = getIntent().getStringArrayListExtra("receivers");
		receiversId = getIntent().getStringArrayListExtra("receiversId");
		orignialReceivers.addAll(receivers);
		adapter.setData(receivers);
		adapter.notifyDataSetChanged();
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
		intent.putStringArrayListExtra("receivers", receivers);
		intent.putStringArrayListExtra("receiversId", receiversId);
		intent.putExtra("isReceiversChange", isReceiversChange);
		intent.putExtra("receiversNum", receivers.size());
		setResult(3, intent);
		super.finish();
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
