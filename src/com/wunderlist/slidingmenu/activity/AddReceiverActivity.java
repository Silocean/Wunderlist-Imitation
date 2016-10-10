package com.wunderlist.slidingmenu.activity;

import java.io.InputStream;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.entity.User;
import com.wunderlist.tools.StreamTool;
import com.wunderlist.tools.WebServiceRequest;

public class AddReceiverActivity extends ActionbarBaseActivity {
	
	private ListView listView;
	private AddReceiverListItemAdapter adapter;
	
	private LinkedList<String> contracts = new LinkedList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_addreceiver);
		listView = (ListView)this.findViewById(R.id.addreceiverlist);
		adapter = new AddReceiverListItemAdapter(getApplicationContext(), R.layout.listitem_addreceiver);
		listView.setAdapter(adapter);
		setTitle("添加接收人");
		this.getContracts();
		super.onCreate(savedInstanceState);
	}
	
	private void getContracts() {
		new GetContract().execute("");
	}
	
	private class AddReceiverListItemAdapter extends BaseAdapter {
		
		private LinkedList<String> list;
		private int resId;
		private LayoutInflater inflater;
		
		private ViewHolder holder;
		
		public AddReceiverListItemAdapter(Context context, int resId) {
			this.resId = resId;
			this.list = new LinkedList<String>();
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setData(LinkedList<String> contracts) {
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
				holder.emailTextView = (TextView)convertView.findViewById(R.id.addreceiver_email_text);
				holder.addImageView = (ImageView)convertView.findViewById(R.id.addreceiver_add_icon);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			holder.emailTextView.setText((String)getItem(position));
			holder.addImageView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Toast.makeText(getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
				}
			});
			return convertView;
		}
		
		private class ViewHolder {
			private TextView emailTextView;
			private ImageView addImageView;
		}
		
	}
	
	private class GetContract extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			String json = "";
			try {
				InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("GetContract.xml");
				byte[] data = StreamTool.read(inputStream);
				String string = new String(data).replaceAll("\\&strUserID", User.USERID).replaceAll("\\&email", "");
				data = string.getBytes();
				json = WebServiceRequest.SendPost(inputStream, data, "GetContractResult");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				contracts = parseJSON(result);
				adapter.setData(contracts);
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
	 * 解析json字符串
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */
	private LinkedList<String> parseJSON(String json) throws Exception {
		LinkedList<String> contracts = new LinkedList<String>();
		if (json != null) {
			JSONObject object = new JSONObject(json);
			int rows = Integer.parseInt(object.getString("rows"));
			if (rows > 0) {
				JSONArray array = new JSONArray(object.getString("Items"));
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					contracts.add(obj.getString("EMAIL"));
				}
			} else {
				System.out.println("没有数据");
			}
		} else {
			System.out.println("网络连接出现问题");
		}
		return contracts;
	}

}
