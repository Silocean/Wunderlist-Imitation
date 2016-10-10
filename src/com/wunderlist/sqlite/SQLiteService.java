package com.wunderlist.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wunderlist.entity.User;

public class SQLiteService {
	
	DBOpenHelper openHelper;
	
	public SQLiteService(Context context){
		openHelper = new DBOpenHelper(context);
	}
	
	/**
	 * 检查该用户是否是第一次登录
	 * @param email 用户邮箱
	 * @return
	 */
	public boolean checkUserInfo(String email) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		String sql = "select * from T_SYSUSERS where USERCODE = ?";
		Cursor cursor = db.rawQuery(sql, new String[]{email});
		if(cursor.moveToFirst()) {
			cursor.close();
			db.close();
			return false; //表示用户不是第一次登录
		}
		cursor.close();
		db.close();
		return true; // 表示用户是第一次登录
	}
	
	/**
	 * 获取用户基本信息
	 * @return
	 */
	public User getUserInfo() {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		User user = null;
		Cursor cursor = db.rawQuery("select * from T_SYSUSERS", null);
		if(cursor.moveToFirst()) {
			user = new User();
			user.setUserSID(cursor.getString(cursor.getColumnIndex("SID")));
			user.setUserEmail(cursor.getString(cursor.getColumnIndex("USERCODE")));
			user.setUserName(cursor.getString(cursor.getColumnIndex("USERNAME")));
			user.setUserNameEn(cursor.getString(cursor.getColumnIndex("USERNAMEEN")));
			user.setUserImageUrl(cursor.getString(cursor.getColumnIndex("IMAGURL")));
			user.setUserPassword(cursor.getString(cursor.getColumnIndex("PASSWORD")));
			user.setUserSex(cursor.getString(cursor.getColumnIndex("SEX")));
			user.setUserAge(cursor.getString(cursor.getColumnIndex("AGE")));
			user.setUserHobby(cursor.getString(cursor.getColumnIndex("HOBBY")));
			user.setUserMemo(cursor.getString(cursor.getColumnIndex("MEMO")));
			user.setUserMobile(cursor.getString(cursor.getColumnIndex("MOBILE")));
		}
		cursor.close();
		db.close();
		return user;
	}
	
	/**
	 * 保存用户信息到本地
	 * @param user
	 */
	public void saveUserInfo(User user) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("SID", user.getUserSID());
		values.put("USERCODE", user.getUserEmail());
		values.put("USERNAME", user.getUserName());
		values.put("USERNAMEEN", user.getUserNameEn());
		values.put("IMAGURL", user.getUserImageUrl());
		values.put("PASSWORD", user.getUserPassword());
		values.put("SEX", user.getUserSex());
		values.put("AGE", user.getUserAge());
		values.put("HOBBY", user.getUserHobby());
		values.put("MEMO", user.getUserMemo());
		values.put("MOBILE", user.getUserMobile());
		db.insert("T_SYSUSERS", null, values);
		db.close();
	}
	
	/**
	 * 删除用户信息
	 */
	public void deleteUserInfo() {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("delete from T_SYSUSERS");
		db.close();
	}

}
