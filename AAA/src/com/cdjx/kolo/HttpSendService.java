package com.cdjx.kolo;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.cdjx.kolo.FinalConstant;
import com.cdjx.kolo.StreamTool;

public class HttpSendService {

	/*
	 * 保存数据，向WEB端提交数据，并等待服务器反馈，作为返回值反馈给上层函数
	 */
	public static String query(String path, String cmd) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", cmd);
	
		try {
			return postRequest(path, params, FinalConstant.ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String controling(String path, String cmd, String PARAMS0, String PARAMS1) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", cmd);	
		params.put("PARAMS0", PARAMS0);
		params.put("PARAMS1", PARAMS1);
	
		try {
			return postRequest(path, params, FinalConstant.ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
	/*
	 * 发送POST请求
	 * @param path 请求路径
	 * @param params 请求参数
	 * @param ecoding 编码
	 * @return 请求是否成功
	 */
	private static String postRequest(String path,
			Map<String, String> params, String ecoding) throws Exception {

		StringBuilder data = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				data.append(entry.getKey()).append("=");
				data.append(URLEncoder.encode(entry.getValue(), ecoding)); // URL对中文参数进行编码
				data.append("&");
			}
			data.deleteCharAt(data.length() - 1);						
		}
		
		byte[] entity = data.toString().getBytes();// 生成实体数据
		
		// 发送POST请求				
		HttpURLConnection conn = (HttpURLConnection) (new URL(path)).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);// 允许对外输出数据
		
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", String.valueOf(entity.length));
		
		OutputStream outStream = conn.getOutputStream();
		outStream.write(entity);	// 数据写到内部缓存，还未往WEB发送
		   
		if (conn.getResponseCode() == 200) {	// 触发数据发送
			Log.e("发出", params.toString());
			InputStream inStream = conn.getInputStream(); // 等待响应，并获取反馈的数据流
			String out = StreamTool.parseJson(inStream);
			conn.disconnect();	// 如果不关闭连接，会有什么影响？
			return out;
		}	
		conn.disconnect();
		return null;
	}	
}
