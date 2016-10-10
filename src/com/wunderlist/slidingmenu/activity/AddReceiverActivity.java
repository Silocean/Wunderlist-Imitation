package com.wunderlist.slidingmenu.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.entity.Common;
import com.wunderlist.entity.CommonUser;
import com.wunderlist.entity.Receiver;
import com.wunderlist.tools.MyActivityManager;
import com.wunderlist.tools.StreamTool;
import com.wunderlist.tools.WebServiceRequest;

/**
 * 添加接收人界面
 * @author Silocean
 *
 */
public class AddReceiverActivity extends ActionbarBaseActivity implements OnClickListener {
	
	private ListView listView;
	private AddReceiverListItemAdapter adapter;
	
	private EditText emailEditText;
	private ImageView addEmailIcon;
	
	private ArrayList<String> receivers;
	
	private LinkedList<Receiver> contracts = new LinkedList<Receiver>();
	
	private LinkedList<Receiver> tempList = new LinkedList<Receiver>();
	
	private ArrayList<String> selectedContracts = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_addreceiver);
		MyActivityManager.addActivity("AddReceiverActivity", this);
		listView = (ListView)this.findViewById(R.id.addreceiverlist);
		adapter = new AddReceiverListItemAdapter(getApplicationContext(), R.layout.listitem_addreceiver);
		listView.setAdapter(adapter);
		emailEditText = (EditText)this.findViewById(R.id.addreceiver_email_edittext);
		addEmailIcon = (ImageView)this.findViewById(R.id.addreceiver_rightaddicon);
		emailEditText.addTextChangedListener(new UserEmailEditTextWatcher());
		addEmailIcon.setOnClickListener(this);
		setTitle("添加接收人");
		receivers = getIntent().getStringArrayListExtra("receivers");
		this.getContracts();
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * useremail编辑框监视器
	 * @author Silocean
	 *
	 */
	private class UserEmailEditTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(s.toString().matches(Common.EMAILREGEX)) {
				addEmailIcon.setVisibility(View.VISIBLE);
			} else {
				addEmailIcon.setVisibility(View.GONE);
			}
			dynamicMatchEmail(s.toString());
		}
		
	}
	
	/**
	 * 根据输入的邮件地址动态的查询符合的接收人
	 * @param str
	 */
	private void dynamicMatchEmail(String str) {
		tempList.removeAll(tempList);
		for(int i=0; i<contracts.size(); i++) {
			Receiver receiver = contracts.get(i);
			if(receiver.getReceiverEmail().startsWith(str)) {
				tempList.add(receiver);
			}
		}
		adapter.setData(tempList);
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 获取联系人
	 */
	private void getContracts() {
		new GetContract().execute("");
	}
	
	/**
	 * 接收人数据列表填充器
	 * @author Silocean
	 *
	 */
	private class AddReceiverListItemAdapter extends BaseAdapter {
		
		private LinkedList<Receiver> list;
		private int resId;
		private LayoutInflater inflater;
		
		private ViewHolder holder;
		
		public AddReceiverListItemAdapter(Context context, int resId) {
			this.resId = resId;
			this.list = new LinkedList<Receiver>();
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setData(LinkedList<Receiver> contracts) {
			this.list = contracts;
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
				holder.usernameTextView = (TextView)convertView.findViewById(R.id.addreceiver_username);
				holder.emailTextView = (TextView)convertView.findViewById(R.id.addreceiver_email_text);
				holder.addImageView = (ImageView)convertView.findViewById(R.id.addreceiver_add_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			final Receiver receiver = (Receiver)getItem(position);
			if(receiver.getReceiverName().equals("")) {
				holder.usernameTextView.setText("佚名");
			} else {
				holder.usernameTextView.setText(receiver.getReceiverName());
			}
			holder.emailTextView.setText(receiver.getReceiverEmail());
			holder.addImageView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(!selectedContracts.contains(((Receiver)getItem(position)).getReceiverEmail())) {
						selectedContracts.add(((Receiver)getItem(position)).getReceiverEmail());
						contracts.remove(position);
						adapter.setData(contracts);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getApplicationContext(), "你已添加过此接收人", Toast.LENGTH_SHORT).show();
					}
				}
			});
			return convertView;
		}
		
		private class ViewHolder {
			private TextView usernameTextView;
			private TextView emailTextView;
			private ImageView addImageView;
		}
		
	}
	
	/**
	 * 异步任务，用户获取联系人
	 * @author Silocean
	 *
	 */
	private class GetContract extends AsyncTask<String, Integer, LinkedList<Receiver>> {

		@Override
		protected LinkedList<Receiver> doInBackground(String... params) {
			try {
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("GetContractList.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strUserID", CommonUser.USERID);
				data = string.getBytes();
				String json = WebServiceRequest.SendPost(inputStream, data, "GetContractListResult");
				contracts = parseJSON(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return contracts;
		}

		@Override
		protected void onPostExecute(LinkedList<Receiver> contracts) {
			tempList.removeAll(tempList);
			for(int i=0; i<contracts.size(); i++) {
				String email = contracts.get(i).getReceiverEmail();
				for(int j=0; j<receivers.size(); j++) {
					if(email.equals(receivers.get(j))) {
						tempList.add(contracts.get(i));
					}
				}
			}
			contracts.removeAll(tempList);
			adapter.setData(contracts);
			adapter.notifyDataSetChanged();
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
	private LinkedList<Receiver> parseJSON(String json) throws Exception {
		LinkedList<Receiver> contracts = new LinkedList<Receiver>();
		if (json != null) {
			JSONObject object = new JSONObject(json);
			int rows = Integer.parseInt(object.getString("rows"));
			if (rows > 0) {
				JSONArray array = new JSONArray(object.getString("Items"));
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					Receiver receiver = new Receiver();
					receiver.setReceiverEmail(obj.getString("EMAIL"));
					receiver.setReceiverName(obj.getString("USERNAME"));
					receiver.setReceiverSex(obj.getString("SEX"));
					receiver.setReceiverAge(obj.getString("AGE"));
					receiver.setReceiverHobby(obj.getString("HOBBY"));
					receiver.setReceiverMobile(obj.getString("MOBILE"));
					contracts.add(receiver);
				}
			} else {
				System.out.println("没有数据");
			}
		} else {
			Common.ToastIfNetworkProblem(getApplicationContext());
		}
		return contracts;
	}
	
	@Override
	public void finish() {
		Intent intent = new Intent();
		intent.putStringArrayListExtra("selectedContracts", selectedContracts);
		setResult(1, intent);
		super.finish();
	}

	@Override
	public void onClick(View v) {
		String email = emailEditText.getText().toString().trim();
		if(!email.equals(CommonUser.USEREMAIL)) {
			if(contracts.size() > 0) {
				if(!contracts.get(0).getReceiverEmail().equals(email)) {
					Receiver receiver = new Receiver("", "", "", "", "", "");
					receiver.setReceiverEmail(emailEditText.getText().toString().trim());
					contracts.addFirst(receiver);
				}
			} else {
				Receiver receiver = new Receiver("", "", "", "", "", "");
				receiver.setReceiverEmail(email);
				contracts.addFirst(receiver);
			}
			adapter.setData(contracts);
			adapter.notifyDataSetChanged();
		} else {
			Toast.makeText(getApplicationContext(), "你不能添加自己为接收人", Toast.LENGTH_SHORT).show();
		}
	}

}
