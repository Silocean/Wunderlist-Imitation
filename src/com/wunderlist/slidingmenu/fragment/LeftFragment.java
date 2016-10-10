package com.wunderlist.slidingmenu.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wunderlist.R;
import com.wunderlist.slidingmenu.activity.SettingsActivity;
import com.wunderlist.slidingmenu.activity.SlidingActivity;

public class LeftFragment extends Fragment implements OnClickListener {
	
	private ImageView notifImageView = null;
	private ImageView syncImageView = null;
	private TextView synctimeTextView = null;
	private ImageView settingsImageView = null;
	
	private RelativeLayout groupReceive = null;
	//private RelativeLayout groupExecute = null;
	//private RelativeLayout groupFollow = null;
	//private RelativeLayout groupInitiate = null;
	
	private MainFragment mainFragment = null;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left, null);
		notifImageView = (ImageView)view.findViewById(R.id.user_notification);
		syncImageView = (ImageView)view.findViewById(R.id.sidebar_sync);
		synctimeTextView = (TextView)view.findViewById(R.id.sidebar_synctime);
		settingsImageView = (ImageView)view.findViewById(R.id.siderbar_settings);
		groupReceive = (RelativeLayout)view.findViewById(R.id.group_receive);
		//groupExecute = (RelativeLayout)view.findViewById(R.id.group_execute);
		//groupFollow = (RelativeLayout)view.findViewById(R.id.group_follow);
		//groupInitiate = (RelativeLayout)view.findViewById(R.id.group_initiate);
		notifImageView.setOnClickListener(this);
		syncImageView.setOnClickListener(this);
		settingsImageView.setOnClickListener(this);
		groupReceive.setOnClickListener(this);
		//groupExecute.setOnClickListener(this);
		//groupFollow.setOnClickListener(this);
		//groupInitiate.setOnClickListener(this);
		groupReceive.setSelected(true);
		mainFragment = new MainFragment();
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_notification: {
			Toast.makeText(getActivity(), "notification", 0).show();
			break;
		}
		case R.id.sidebar_sync: {
			Toast.makeText(getActivity(), "sync", 0).show();
			break;
		}
		case R.id.siderbar_settings: {
			Intent intent = new Intent(getActivity(), SettingsActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.group_receive: {
			((SlidingActivity)getActivity()).showLeft();
			groupReceive.setSelected(true);
			//groupExecute.setSelected(false);
			//groupFollow.setSelected(false);
			//groupInitiate.setSelected(false);
			//SlidingActivity.mainFragment.getTaskBoxList();
			break;
		}
		/*case R.id.group_execute: {
			((SlidingActivity)getActivity()).showLeft();
			groupReceive.setSelected(false);
			groupExecute.setSelected(true);
			groupFollow.setSelected(false);
			groupInitiate.setSelected(false);
			break;
		}
		case R.id.group_follow: {
			((SlidingActivity)getActivity()).showLeft();
			groupReceive.setSelected(false);
			groupExecute.setSelected(false);
			groupFollow.setSelected(true);
			groupInitiate.setSelected(false);
			break;
		}*/
		/*case R.id.group_initiate: {
			((SlidingActivity)getActivity()).showLeft();
			groupReceive.setSelected(false);
			//groupExecute.setSelected(false);
			//groupFollow.setSelected(false);
			groupInitiate.setSelected(true);
			SlidingActivity.mainFragment.getTaskList();
			break;
		}*/
		default:
			break;
		}
	}

}
