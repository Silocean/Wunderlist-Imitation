package com.wunderlist.slidingmenu.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.wunderlist.R;
import com.wunderlist.entity.Common;
import com.wunderlist.slidingmenu.fragment.MainFragment;
import com.wunderlist.tools.MyActivityManager;
import com.wunderlist.tools.MySharedPreferences;

/**
 * 选择主界面背景界面
 * @author Silocean
 *
 */
@SuppressWarnings("deprecation")
public class ChooseBgActivity extends ActionbarBaseActivity {

	private RelativeLayout bgLayout = null;

	private int bgId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_changebg);
		MyActivityManager.addActivity("ChooseBgActivity", this);
		bgLayout = (RelativeLayout) findViewById(R.id.bgLayout);
		Gallery g = (Gallery) findViewById(R.id.bgGallery);
		g.setAdapter(new ImageAdapter(getApplicationContext()));
		g.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				bgLayout.setBackgroundResource(Common.BGS[arg2]);
				bgId = arg2;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});
		this.getPreferences();
		g.setSelection(bgId);
		super.onCreate(savedInstanceState);
	}

	/**
	 * 获取sharedPreference
	 */
	private void getPreferences() {
		SharedPreferences preferences = MySharedPreferences
				.getPreferences(getApplicationContext());
		bgId = preferences.getInt(MySharedPreferences.BGID, 0);
	}

	/**
	 * 背景图片数据填充器
	 * @author Silocean
	 *
	 */
	private class ImageAdapter extends BaseAdapter {

		int mGalleryItemBackground;
		private Context mContext;

		public ImageAdapter(Context c) {
			mContext = c;
			TypedArray a = obtainStyledAttributes(R.styleable.HelloGallery);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.HelloGallery_android_galleryItemBackground, 0);
			a.recycle();
		}

		public int getCount() {
			return Common.BGS.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);
			i.setImageResource(Common.BGS[position]);
			i.setLayoutParams(new Gallery.LayoutParams(150, 100));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setBackgroundResource(mGalleryItemBackground);
			return i;
		}
	}

	/**
	 * 保存背景图片
	 * 
	 * @param v
	 */
	public void saveBg(View v) {
		SharedPreferences preferences = MySharedPreferences
				.getPreferences(getApplicationContext());
		Editor editor = MySharedPreferences.getEditor(preferences);
		editor.putInt(MySharedPreferences.BGID, bgId);
		editor.commit();
		MainFragment.changeBg(bgId);
		finish();
	}
	
	@Override
	public void finish() {
		Intent intent = new Intent();
		intent.putExtra("bgId", bgId);
		setResult(1, intent);
		super.finish();
	}
	
}
