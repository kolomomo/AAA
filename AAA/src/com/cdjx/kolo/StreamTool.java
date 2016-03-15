package com.cdjx.kolo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.json.JSONObject;

import android.util.Log;

public class StreamTool {
	/*
	 * JSON数据格式，两种均可
	 * 		1) {SENSOR0:10.56,cmd:"queryData",SENSOR1:34.65}
	 * 		2) {"SENSOR0":10.56,"cmd":"queryData","SENSOR1":34.65}
	 */
	public static String parseJson(InputStream inStream) throws Exception {
		byte[] data = read(inStream);
		String json = new String(data, "GB2312");
		Log.e("收到JSON",json);
		return json;
	}

	/*
	 * 读取流中的数据
	 */
	private static byte[] read(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
}
