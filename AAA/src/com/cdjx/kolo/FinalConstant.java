package com.cdjx.kolo;

public class FinalConstant {
	// 用户账户
	public static final String USERNAME = "ghgj";
	public static final String USERNAME0 = "ghgk";
	public static final String PASSWORD = "123456";
	
	// 系统超时时间
	public static final long WAIT_TIME = 10000;
	
	// HTTP通信相关
	public static final String ENCODING = "GB2312";
	public static final String QUERY_DATA = "queryData";
	public static final String QUERY_STATE = "queryState";
	public static final String QUERY_PARAMS = "queryParams";
	public static final String CONTROL = "control";
	public static final String SET_PARAMS = "setParams";
	
	public static final String SENSOR_DATA_HEAD = "SENSOR";
	public static final String CONTROL_STATE_HEAD = "EQUIPMENT";
	public static final String PARAMS_DATA_HEAD = "PARAMS";
	public static final String CONTROL_SUCCESS = "success";
	
	public static final int[] EQUIPMENT = {0, 1, 2, 3, 4};
	public static final int ON = 1;
	public static final int OFF = 0;
	
	public static final int QUERY_BACK_DATA = 1;
	public static final String BACK_INFO = "backMessageFromServer";
	public static final int CONTROL_BACK_DATA = 2;
	public static final String CONTROL_INFO = "controlbackMessageFromServer";
	
	public static final int TIME_OUT = 3;
	
	// 视频播放相关
	public static final int VIDEO_OK = 4;
	public static final int VIDEO_TIMEOUT =5;
	public static final int VIDEO_PLAY = 6;
	
	// 视频控制相关
	public static final int down_start = 11;
	public static final int down_end = 12;
	public static final int up_start = 21;
	public static final int up_end = 22;
	public static final int left_start = 31;
	public static final int left_end = 32;
	public static final int right_start = 41;
	public static final int right_end = 42;
	public static final int zoomin_start = 51;
	public static final int zoomin_end = 52;
	public static final int zoomout_start = 61;
	public static final int zoomout_end = 62;
	
	public static final int PARAMS_SETUP = 21;
	public static final int RESULT_PARAMS_SETUP = 22;
}
