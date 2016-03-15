package com.cdjx.kolo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	public DBOpenHelper(Context context) {
		super(context, "ghgktest15.db", null, 1);	//数据被默认保存在<package>/database///88super(context, "baixinguanjia.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE dplocalparams(" +
				"id integer primary key autoincrement, " +
				"Name varchar(20)," +
				"serverPath VARCHAR(40), " +
//				"serverPort VARCHAR(5)," +
//				"controlCenterIP VARCHAR(15)," +
//				"controlCenterPort VARCHAR(5)," +
				"videoIP VARCHAR(15)," +
				"videoPort VARCHAR(5)," +
				"videoChanel VARCHAR(2))");		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE dplocalparams ADD other integer null");		
	}
	
	

}
