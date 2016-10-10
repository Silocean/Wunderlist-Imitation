package com.wunderlist.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteService {
	
	DBOpenHelper openHelper;
	
	public SQLiteService(Context context){
		openHelper = new DBOpenHelper(context);
	}
	
	/*
	 * 检查该用户是否是第一次登录
	 * @param email 用户邮箱
	 * @return
	 */
	public boolean checkStuInfo(String email) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		String sql = "select * from T_SYSUSERS where USERCODE = ?";
		Cursor cursor = db.rawQuery(sql, new String[]{email});
		if(cursor.moveToFirst()) {
			cursor.close();
			db.close();
			return true; //表示已有用户登录过
		}
		cursor.close();
		db.close();
		return false; // 表示还没有用户登录过
	}

}
