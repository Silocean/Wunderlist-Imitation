package com.wunderlist.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context) {
		super(context, "wuderlist.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//user table
		db.execSQL("create table T_SYSUSERS (SID varchar(40) primary key, USERCODE varchar(100), USERNAME varchar(50), USERNAMEEN varchar(50), " +
				"IMAGURL varchar(100), PASSWORD varchar(50), SEX integer, AGE integer, HOBBY varchar(200), MEMO varchar(400), MOBILE varchar(20), ISACTIVE integer)");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
