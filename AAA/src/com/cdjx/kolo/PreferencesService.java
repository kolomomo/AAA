package com.cdjx.kolo;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesService {
	private Context context;
	
	public PreferencesService(Context context) {
		this.context = context;
	}

	/*
	 * 保存基本参数
	 */
	public void save(String name, String serverPath, String videoIP, String videoPort, String videoChannel) {
		SharedPreferences preferences = context.getSharedPreferences("cdjx_mm_greenhouseDP", Context.MODE_APPEND);
		Editor editor = preferences.edit();
		editor.putString("name", name);
		editor.putString("serverPath", serverPath);
		editor.putString("videoIP", videoIP);
		editor.putString("videoPort", videoPort);
		editor.putString("videoChannel", videoChannel);
		editor.commit();		
	}
	
	public Map<String, String> get() {
		Map<String, String> back = new HashMap<String, String>();
		SharedPreferences preferences = context.getSharedPreferences("cdjx_mm_greenhouseDP", Context.MODE_APPEND);
		back.put("name", preferences.getString("name", "4号棚"));
		back.put("serverPath", preferences.getString("serverPath", "http://10.5.25.70/phoneapi.php"));
		back.put("videoIP", preferences.getString("videoIP", "10.5.25.171"));
		back.put("videoPort", preferences.getString("videoPort", "8000"));
		back.put("videoChannel", preferences.getString("videoChannel", "1"));
		return back;		
	}
}
