package com.wunderlist.slidingmenu.fragment;

import android.annotation.SuppressLint;
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

import com.example.wunderlist.R;
import com.wunderlist.entity.CommonUser;
import com.wunderlist.slidingmenu.activity.InboxActivity;
import com.wunderlist.slidingmenu.activity.SettingsActivity;
import com.wunderlist.slidingmenu.activity.SlidingActivity;

@SuppressLint("ValidFragment")
public class LeftFragment extends Fragment implements OnClickListener {
	
	private TextView userNameTextView = null;
	private TextView userEmailTextView = null;
	
	private ImageView notifyImageView = null;
	private ImageView syncImageView = null;
	private ImageView settingsImageView = null;
	
	private RelativeLayout groupReceive = null;
	
	private MainFragment mainFragment = null;
	
	public LeftFragment() {
	}
	
	public LeftFragment(MainFragment mainFragment) {
		this.mainFragment = mainFragment;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.left, null);
		userNameTextView = (TextView)view.findViewById(R.id.user_name);
		userEmailTextView = (TextView)view.findViewById(R.id.user_email);
		notifyImageView = (ImageView)view.findViewById(R.id.user_notification);
		syncImageView = (ImageView)view.findViewById(R.id.sidebar_sync);
		settingsImageView = (ImageView)view.findViewById(R.id.siderbar_settings);
		groupReceive = (RelativeLayout)view.findViewById(R.id.group_receive);
		notifyImageView.setOnClickListener(this);
		syncImageView.setOnClickListener(this);
		settingsImageView.setOnClickListener(this);
		groupReceive.setOnClickListener(this);
		groupReceive.setSelected(true);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		this.initData();
		super.onActivityCreated(savedInstanceState);
	}

	/**
	 * 初始化界面数据
	 */
	private void initData() {
		userNameTextView.setText(CommonUser.USERNAME);
		userEmailTextView.setText(CommonUser.USEREMAIL);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.user_notification: {
			Intent intent = new Intent(getActivity(), InboxActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.sidebar_sync: {
			mainFragment.updateTaskBoxList();
			/*long duration = MainFragment.getRefreshingDuration();
			Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_refresh);
			animation.setDuration(duration);
			syncImageView.setAnimation(animation);
			syncImageView.startAnimation(animation);*/
			break;
		}
		case R.id.siderbar_settings: {
			Intent intent = new Intent(getActivity(), SettingsActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.group_receive: {
			((SlidingActivity)getActivity()).showLeft();
			break;
		}
		default:
			break;
		}
	}

}
