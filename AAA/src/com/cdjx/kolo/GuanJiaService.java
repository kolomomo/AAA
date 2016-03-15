package com.cdjx.kolo;


import java.util.ArrayList;
import java.util.List;

import com.cdjx.kolo.GuanJia;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GuanJiaService {
	
	private DBOpenHelper dbOpenHelper;

	public GuanJiaService(Context context) {
		this.dbOpenHelper = new DBOpenHelper(context);
	}		

	/*
	 * 娣诲姞澶ф
	 */
	public void save (GuanJia guanJia){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();				
		db.execSQL("insert into dplocalparams(Name, serverPath, videoIP, videoPort,videoChanel) values(?,?,?,?,?)",
				new Object[]{guanJia.getName(), guanJia.getServerPath(),  guanJia.getVideoIP(), guanJia.getVideoPort(), guanJia.getVideoChanel()});
		db.close();	
	}
	
	/*
	 * 鍒犻櫎澶ф
	 */
	public void delete(Integer id){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("delete from dplocalparams where id=?",
				new Object[]{id});
		db.close();
	}

	/*
	 * 鏇存柊澶ф
	 */
	public void update(GuanJia guanJia){
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update dplocalparams set Name=?,serverPath=?,videoIP=?,videoPort=?,videoChanel=? where id=?",
				new Object[]{guanJia.getName(), guanJia.getServerPath(), guanJia.getVideoIP(),guanJia.getVideoPort(),guanJia.getVideoChanel(),guanJia.getId()});
		db.close();		
	}

	/*
	 * 鏌ヨ璁板綍
	 */
	public GuanJia find(Integer id){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		Cursor cursor =db.rawQuery("select * from dplocalparams where id=?", 
				new String[]{id.toString()});
		//Cursor瀵规煡璇㈣繑鍥炵殑缁撴灉杩涜闅忔満璁块棶
		
		if(cursor.moveToFirst()){
			int id1 = cursor.getInt(cursor.getColumnIndex("id"));
			String Name = cursor.getString(cursor.getColumnIndex("Name"));
			String serverPath = cursor.getString(cursor.getColumnIndex("serverPath"));
			String videoIP = cursor.getString(cursor.getColumnIndex("videoIP"));
			String videoPort = cursor.getString(cursor.getColumnIndex("videoPort"));
			String videoChanel = cursor.getString(cursor.getColumnIndex("videoChanel"));
			
			return new GuanJia(id1,Name,serverPath,videoIP,videoPort,videoChanel);
		}	
		cursor.close();
		return null;
	}
	public GuanJia find(String name){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		Cursor cursor =db.rawQuery("select * from dplocalparams where Name=?", 
				new String[]{name});
		//Cursor瀵规煡璇㈣繑鍥炵殑缁撴灉杩涜闅忔満璁块棶
		
		if(cursor.moveToFirst()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String Name = cursor.getString(cursor.getColumnIndex("Name"));
			String serverPath = cursor.getString(cursor.getColumnIndex("serverPath"));
//			String serverPort = cursor.getString(cursor.getColumnIndex("serverPort"));
//			String controlCenterIP = cursor.getString(cursor.getColumnIndex("controlCenterIP"));
//			String controlCenterPort = cursor.getString(cursor.getColumnIndex("controlCenterPort"));
			String videoIP = cursor.getString(cursor.getColumnIndex("videoIP"));
			String videoPort = cursor.getString(cursor.getColumnIndex("videoPort"));
			String videoChanel = cursor.getString(cursor.getColumnIndex("videoChanel"));
			
			return new GuanJia(id,Name,serverPath,videoIP,videoPort,videoChanel);
		}	
		cursor.close();
		return null;
	}

	//鍒嗛〉鏌ヨ
	public List<GuanJia> getScrollData(int offset, int maxResult){
		List<GuanJia> guanJias = new ArrayList<GuanJia>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();//鑻ュ彧闇�璇诲彇鏁版嵁锛屽缓璁娇鐢ㄦ鏂规硶锛岃鏂规硶浠呭湪鏁版嵁搴撳凡婊＄殑鎯呭喌涓嬫墠鏄彧璇荤殑
		Cursor cursor =db.rawQuery("select * from dplocalparams order by id asc limit ?,?", 
				new String[]{String.valueOf(offset),String.valueOf(maxResult)});
		
		while(cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String Name = cursor.getString(cursor.getColumnIndex("Name"));
			String serverPath = cursor.getString(cursor.getColumnIndex("serverPath"));
//			String serverPort = cursor.getString(cursor.getColumnIndex("serverPort"));
//			String controlCenterIP = cursor.getString(cursor.getColumnIndex("controlCenterIP"));
//			String controlCenterPort = cursor.getString(cursor.getColumnIndex("controlCenterPort"));
			String videoIP = cursor.getString(cursor.getColumnIndex("videoIP"));
			String videoPort = cursor.getString(cursor.getColumnIndex("videoPort"));
			String videoChanel = cursor.getString(cursor.getColumnIndex("videoChanel"));
			
			guanJias.add(new GuanJia(id,Name,serverPath,videoIP,videoPort,videoChanel));
		}
		cursor.close();
		return guanJias;
	}

	/*
	 * 缁熻澶ф鎬绘暟
	 */
	public long getCount(){
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor =db.rawQuery("select count(*) from dplocalparams", null);
		cursor.moveToFirst();
		long result = cursor.getLong(0);
		cursor.close();
		return result;
	}

	//杩斿洖鏁版嵁搴撴墍鏈変俊鎭�
	public List<GuanJia> getAllData(){
		return this.getScrollData(0, (int)this.getCount()); 
	}
}
