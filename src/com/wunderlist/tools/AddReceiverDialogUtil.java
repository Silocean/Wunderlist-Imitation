package com.wunderlist.tools;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.slidingmenu.activity.ReceiversActivity;

public class AddReceiverDialogUtil {

	private ReceiversActivity activity;
	
	private RelativeLayout addreceiverLayout;
	private EditText addreceiverEditText;
	
	private String receiverEmail;

	public AddReceiverDialogUtil(ReceiversActivity activity) {
		this.activity = activity;
		addreceiverLayout = (RelativeLayout)activity.getLayoutInflater().inflate(R.layout.dialog_addreceiver, null);
		addreceiverEditText = (EditText)addreceiverLayout.findViewById(R.id.dialog_addreceiver_edittext);
	}
	
	/**
	 * 显示添加接收人对话框
	 * @return
	 */
	public AlertDialog showAddReceiverDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(activity)
			.setTitle("添加接收人")
			.setView(addreceiverLayout)
			.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					receiverEmail = addreceiverEditText.getText().toString().trim();
					addReceiver(receiverEmail);
				}
			})
			.setNegativeButton("取消", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
		return alertDialog;
	}
	
	/**
	 * 添加接收人
	 * @param receiverEmail
	 */
	private void addReceiver(String receiverEmail) {
		if(!receiverEmail.equals("")) {
			updateReceiverList(receiverEmail);
		} else {
			Toast.makeText(activity, "收件人邮箱不能为空", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 更新接收人列表
	 * @param receiverEmail
	 */
	private void updateReceiverList(String receiverEmail) {
		activity.updateReceiverList(receiverEmail);
	}
	

}
