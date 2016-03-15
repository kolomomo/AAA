package com.cdjx.kolo;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cdjx.kolo.FinalConstant;
import com.cdjx.kolo.GuanJia;
import com.cdjx.kolo.HttpSendService;
import com.cdjx.kolo.PreferencesService;

public class SetupParams extends Activity implements OnClickListener,OnFocusChangeListener{
	// 网络通信
	private static boolean isQuery = false;
	private static boolean isControlOrSet = false;
	private static String CMD = "";
	private static String PATH = "";
	private static String PARAMS0 = "";
	private static String PARAMS1 = "";
	private static long linkStartTime;
	
	private InputMethodManager imm = null;
	
	private boolean firstAskForOnlineData = false;
	
	// 加载动画
	private View linkAnimPage = null;
	private ImageView loading = null;
	private Animation loadingAnim = null;	
	
	private TextView open1 = null;
	private TextView close1 = null;
	private Button save1 = null;
	private TextView open2 = null;
	private TextView close2 = null;
	private Button save2 = null;
	
	private View localParams = null;
	private View onlineParams = null;
	
	private TextView title = null;
	
	private Button back = null;
	
	private ImageView autoControlBtn = null;
	private ImageView autoIrrigateBtn = null;
  	private int autoControlFlag; // 自动控制标记：0，不支持；1，支持
 	private int newFlag;
 	private int autoControlFlag2; // 自动控制标记：0，不支持；1，支持
 	private int newFlag2;	
	
	private EditText ed_serverIP = null;
	private EditText ed_videoIP = null;
	private EditText ed_videoPort = null;
	private EditText ed_videoChanel = null;	
	
	private TextView tv_serverIP = null;
	private TextView tv_videoIP = null;
	private TextView tv_videoPort = null;
	private TextView tv_videoChanel = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);	
		initLoacal();
	}		

	@Override
	protected void onResume() {
		Log.e("=== Setup ===", "onResume");
		super.onResume();
	}

	@Override
	protected void onPause() {		
		Log.e("=== Setup ===", "onPause");		
		if (linkAnimPage.getVisibility() == View.VISIBLE) {
			linkAnimPage.setVisibility(View.GONE);
			loading.clearAnimation();
		}
		isQuery = false;
		isControlOrSet = false;
		
		super.onPause();
	}
	
	private void initLoacal() {		
		initViews();
		title.setText(Main.localParamsForGJ.getName()+"参数设置");
					
		if (Main.localParamsForGJ.getServerPath().equals("")){						
			tv_serverIP.setText(R.string.not_set);
			tv_serverIP.setTextColor(Color.rgb(216,216,216));			
			tv_videoIP.setText(R.string.not_set);
			tv_videoIP.setTextColor(Color.rgb(216,216,216));
			tv_videoPort.setText(R.string.not_set);
			tv_videoPort.setTextColor(Color.rgb(216,216,216));
			tv_videoChanel.setText(R.string.not_set);
			tv_videoChanel.setTextColor(Color.rgb(216,216,216));						
		} else {			
			tv_serverIP.setText(Main.localParamsForGJ.getServerPath());
			ed_serverIP.setText(Main.localParamsForGJ.getServerPath());
			tv_serverIP.setTextColor(Color.rgb(165, 165, 165));
			tv_videoIP.setText(Main.localParamsForGJ.getVideoIP());
			ed_videoIP.setText(Main.localParamsForGJ.getVideoIP());
			tv_videoIP.setTextColor(Color.rgb(165, 165, 165));
			tv_videoPort.setText(Main.localParamsForGJ.getVideoPort());
			ed_videoPort.setText(Main.localParamsForGJ.getVideoPort());
			tv_videoPort.setTextColor(Color.rgb(165, 165, 165));
			tv_videoChanel.setText(Main.localParamsForGJ.getVideoChanel());
			ed_videoChanel.setText(Main.localParamsForGJ.getVideoChanel());
			tv_videoChanel.setTextColor(Color.rgb(165, 165, 165));
		}
	}
	
	private void initOnline() {
				
		if (autoControlFlag == 0) {
			autoControlBtn.setBackgroundResource(R.drawable.notbechoosed);
		} else {
			autoControlBtn.setBackgroundResource(R.drawable.bechoosed);
		}
		if (autoControlFlag2 == 0) {
			autoIrrigateBtn.setBackgroundResource(R.drawable.notbechoosed);
		} else {
			autoIrrigateBtn.setBackgroundResource(R.drawable.bechoosed);	
		}
	}
	
	private void initViews() {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		linkAnimPage = (View) this.findViewById(R.id.linkAnimPage2);
		linkAnimPage.setOnClickListener(this);
		linkAnimPage.getBackground().setAlpha(0);
		loading = (ImageView) this.findViewById(R.id.linkAnim2);
		loadingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		
		title = (TextView) this.findViewById(R.id.titleForSetupParams);
		title.setOnClickListener(this);
		title.setFocusableInTouchMode(true);
		
		localParams = (View) this.findViewById(R.id.communicationAddress);	
		onlineParams = (View) this.findViewById(R.id.environmentParams);
		
		autoControlBtn = (ImageView) this.findViewById(R.id.chooseOrNot);
		autoControlBtn.setOnClickListener(this);
		autoIrrigateBtn = (ImageView) this.findViewById(R.id.chooseOrNot2);
		autoIrrigateBtn.setOnClickListener(this);
		
		open1 = (TextView) this.findViewById(R.id.open1);
		open1.setOnClickListener(this);
		close1 = (TextView) this.findViewById(R.id.close1);
		close1.setOnClickListener(this);
		save1 = (Button) this.findViewById(R.id.save1);
		save1.setOnClickListener(this);
		
		open2 = (TextView) this.findViewById(R.id.open2);
		open2.setOnClickListener(this);
		close2 = (TextView) this.findViewById(R.id.close2);
		close2.setOnClickListener(this);
		save2 = (Button) this.findViewById(R.id.save2);
		save2.setOnClickListener(this);
	
		back = (Button) this.findViewById(R.id.backSetupToMain);
		back.setOnClickListener(this);
		
		ed_serverIP = (EditText) this.findViewById(R.id.ed_serverIP);
		ed_serverIP.setOnFocusChangeListener(this);
		ed_videoIP = (EditText) this.findViewById(R.id.ed_videoIP);
		ed_videoIP.setOnFocusChangeListener(this);
		ed_videoPort = (EditText) this.findViewById(R.id.ed_videoPort);
		ed_videoPort.setOnFocusChangeListener(this);
		ed_videoChanel = (EditText) this.findViewById(R.id.ed_videoChanel);
		ed_videoChanel.setOnFocusChangeListener(this);
		
		tv_serverIP = (TextView) this.findViewById(R.id.tv_serverIP);
		tv_serverIP.setOnClickListener(this);
		tv_videoIP = (TextView) this.findViewById(R.id.tv_videoIP);
		tv_videoIP.setOnClickListener(this);
		tv_videoPort = (TextView) this.findViewById(R.id.tv_videoPort);
		tv_videoPort.setOnClickListener(this);
		tv_videoChanel = (TextView) this.findViewById(R.id.tv_videoChanel);
		tv_videoChanel.setOnClickListener(this);
		
		((TextView) this.findViewById(R.id.autoControlTitle)).setText("智能灌溉参数");		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chooseOrNot:
			imm.hideSoftInputFromWindow(autoControlBtn.getWindowToken(), 0);
			switch (newFlag) {
			case 0:
				newFlag = 1;
				newFlag2 = 0;
				autoControlBtn.setBackgroundResource(R.drawable.bechoosed);
				autoIrrigateBtn.setBackgroundResource(R.drawable.notbechoosed);
				break;
			case 1:
				newFlag = 0;
				newFlag2 = 1;
				autoControlBtn.setBackgroundResource(R.drawable.notbechoosed);
				autoIrrigateBtn.setBackgroundResource(R.drawable.bechoosed);
				break;
			}
			break;
		case R.id.chooseOrNot2:
			imm.hideSoftInputFromWindow(autoIrrigateBtn.getWindowToken(), 0);
			switch (newFlag2) {
			case 0:
				newFlag = 0;
				newFlag2 = 1;				
				autoIrrigateBtn.setBackgroundResource(R.drawable.bechoosed);
				autoControlBtn.setBackgroundResource(R.drawable.notbechoosed);
				break;
			case 1:
				newFlag = 1;
				newFlag2 = 0;				
				autoIrrigateBtn.setBackgroundResource(R.drawable.notbechoosed);
				autoControlBtn.setBackgroundResource(R.drawable.bechoosed);
				break;
			}
			break;

		case R.id.linkAnimPage2:
			break;
		case R.id.open1:
			close1.setVisibility(View.VISIBLE);
			open1.setVisibility(View.GONE);			
			localParams.setVisibility(View.VISIBLE);
			imm.hideSoftInputFromWindow(open1.getWindowToken(), 0);
			initLoacal();
			break;
		case R.id.close1:
			open1.setVisibility(View.VISIBLE);
			close1.setVisibility(View.GONE);
			localParams.setVisibility(View.GONE);
			imm.hideSoftInputFromWindow(close1.getWindowToken(), 0);
			break;
		case R.id.save1:
			imm.hideSoftInputFromWindow(save1.getWindowToken(), 0);
			lostAllFocus2();
			saveLocalParams();
			break;
		
		// 向主控中心请求环境阈值数据
		case R.id.open2:
			
			imm.hideSoftInputFromWindow(open2.getWindowToken(), 0);

			if (!firstAskForOnlineData) {					
				if(!Main.localParamsForGJ.getServerPath().equals("")) {
					PATH = Main.localParamsForGJ.getServerPath();
					CMD = FinalConstant.QUERY_PARAMS;
					new Thread(query).start();	
					loginAnim();
				}
			} else {
				onlineParams.setVisibility(View.VISIBLE);
			}
			firstAskForOnlineData = true;
			
			close2.setVisibility(View.VISIBLE);
			open2.setVisibility(View.GONE);
			break;
		case R.id.close2:
			imm.hideSoftInputFromWindow(close2.getWindowToken(), 0);
			open2.setVisibility(View.VISIBLE);
			close2.setVisibility(View.GONE);
			onlineParams.setVisibility(View.GONE);
			break;						
		case R.id.save2:
			imm.hideSoftInputFromWindow(save2.getWindowToken(), 0);
			lostAllFocus2();
			saveOnlineParamsDT();
			break;
					
		case R.id.titleForSetupParams:
			title.requestFocus();
			
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(title.getWindowToken(), 0);
			break;
		case R.id.backSetupToMain:
			isQuery = false;
			isControlOrSet = false;
			imm.hideSoftInputFromWindow(back.getWindowToken(), 0);
			finish();
			break;
		case R.id.tv_serverIP:
			tvBeClicked(ed_serverIP, tv_serverIP);
			break;
		case R.id.tv_videoIP:
			tvBeClicked(ed_videoIP, tv_videoIP);
			break;
		case R.id.tv_videoPort:
			tvBeClicked(ed_videoPort, tv_videoPort);
			break;
		case R.id.tv_videoChanel:
			tvBeClicked(ed_videoChanel, tv_videoChanel);
			break;					
		}				
	}
		

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.ed_serverIP:
			lostFocus(ed_serverIP, tv_serverIP, hasFocus);
			break;
		case R.id.ed_videoIP:
			lostFocus(ed_videoIP, tv_videoIP, hasFocus);
			break;
		case R.id.ed_videoPort:
			lostFocus(ed_videoPort, tv_videoPort, hasFocus);
			break;
		case R.id.ed_videoChanel:
			lostFocus(ed_videoChanel, tv_videoChanel, hasFocus);
			break;
		}		
	}

	private void tvBeClicked(EditText ed, TextView tv) {
		ed.setVisibility(View.VISIBLE);
		ed.requestFocus();
		tv.setVisibility(View.GONE);
	}
		
	private void lostAllFocus2() {
		if (localParams.getVisibility() == View.VISIBLE) {
			lostFocus(ed_serverIP, tv_serverIP, false);
			lostFocus(ed_videoIP, tv_videoIP, false);
			lostFocus(ed_videoPort, tv_videoPort, false);
			lostFocus(ed_videoChanel, tv_videoChanel, false);
		}
	}
	
	private void lostFocus(EditText ed, TextView tv, boolean hasFocus) {
		if (!hasFocus) {
			imm.hideSoftInputFromWindow(ed.getWindowToken(), 0);
			String s = ed.getText().toString();
			if (s.equals("")) {
				s = "(空)";
			}
			if (s.equals("")) {
				tv.setTextColor(Color.rgb(216, 216, 216));
				
			} else {
				tv.setTextColor(Color.rgb(165, 165, 165));
			}
			tv.setText(s);
			tv.setVisibility(View.VISIBLE);
			ed.setVisibility(View.GONE);
		}
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {			
			switch(msg.what) {
			case FinalConstant.QUERY_BACK_DATA:
				linkStartTime = Calendar.getInstance().getTimeInMillis();
				if (linkAnimPage.getVisibility() == View.VISIBLE) {
					linkAnimPage.setVisibility(View.GONE);
					loading.clearAnimation();
				}
				try {
					JSONObject jsonOBJECT = new JSONObject(msg.getData().getString(FinalConstant.BACK_INFO));
					
					String cmd = jsonOBJECT.getString("cmd");
					if (cmd.equals(FinalConstant.QUERY_PARAMS)) {
						isQuery = false;							
						autoControlFlag = jsonOBJECT.getInt("PARAMS0");
						autoControlFlag2 = jsonOBJECT.getInt("PARAMS1");
						initOnline();
						onlineParams.setVisibility(View.VISIBLE);
					}
				} catch (JSONException e) {
					Toast.makeText(SetupParams.this, "JSON解析异常", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				break;
			case FinalConstant.CONTROL_BACK_DATA:
				linkStartTime = Calendar.getInstance().getTimeInMillis();
				if (linkAnimPage.getVisibility() == View.VISIBLE) {
					linkAnimPage.setVisibility(View.GONE);
					loading.clearAnimation();
				}
				try {
					JSONObject jsonOBJECT = new JSONObject(msg.getData().getString(FinalConstant.CONTROL_INFO));
					
					String cmd = jsonOBJECT.getString("cmd");
					if (cmd.equals(FinalConstant.SET_PARAMS)) {					
						if (jsonOBJECT.getString("result").equals(FinalConstant.CONTROL_SUCCESS)) {
							isControlOrSet = false;
							autoControlFlag = newFlag;							
							autoControlFlag2 = newFlag2;								
							Toast.makeText(SetupParams.this, "设置成功", Toast.LENGTH_SHORT).show();
						}
					}
				} catch (JSONException e) {
					Toast.makeText(SetupParams.this, "JSON解析异常", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}								
				break;
			case FinalConstant.TIME_OUT:
				firstAskForOnlineData = false;
				isQuery = false;
				isControlOrSet = false;
				if (linkAnimPage.getVisibility() == View.VISIBLE) {
					linkAnimPage.setVisibility(View.GONE);
					loading.clearAnimation();
				}				
				
				if (CMD.equals(FinalConstant.QUERY_PARAMS)) {
					Toast.makeText(SetupParams.this, "等待超时，获取当前参数失败", Toast.LENGTH_LONG).show();
					open2.setVisibility(View.VISIBLE);
					close2.setVisibility(View.GONE);
					onlineParams.setVisibility(View.GONE);	
				} else if (CMD.equals(FinalConstant.SET_PARAMS)) {
					initOnline();
					Toast.makeText(SetupParams.this, "等待超时，修改参数失败", Toast.LENGTH_LONG).show();
				}					
				break;
			}
		}		   	
    };	
	
	// 保存参数
	private boolean saveLocalParams() {		
		String serverIP = ed_serverIP.getText().toString();
		String videoIP = ed_videoIP.getText().toString();
		String videoPort = ed_videoPort.getText().toString();
		String videoChanel = ed_videoChanel.getText().toString();

		if (!(!serverIP.equals("") && !videoIP.equals("") && !videoPort.equals("") && !videoChanel.equals(""))) {
			Toast.makeText(this, "尚有参数未设置，不能保存，修改失败", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if (serverIP.equals(Main.localParamsForGJ.getServerPath()) && videoIP.equals(Main.localParamsForGJ.getVideoIP()) 
				&& videoPort.equals(Main.localParamsForGJ.getVideoPort()) && videoChanel.equals(Main.localParamsForGJ.getVideoChanel())) {
			Toast.makeText(this, "无变化", Toast.LENGTH_LONG).show();
			return false;	
		}

		if (videoPort.length() > 1) {
			if (videoPort.startsWith("0") || videoPort.length() > 5) {
				Toast.makeText(this, "请检查端口号输入（0~65535）", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		if (videoChanel.length() > 1) {
			if (videoChanel.startsWith("0") || videoChanel.length() > 5) {
				Toast.makeText(this, "请检查视频通道号输入（1~32）", Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		if(!(Integer.parseInt(videoChanel)>0 && Integer.parseInt(videoChanel)<32)) {
			Toast.makeText(this, "请注意视频通道号范围（1~32）", Toast.LENGTH_SHORT).show();
			return false;
		}					

		firstAskForOnlineData = false;	
		isQuery = false;
		isControlOrSet = false;	
								
		// 本地通信地址更新
//		PreferencesService pService = new PreferencesService(this);
//		pService.save(Main.greenhousedp.get("name"), serverIP, videoIP, videoPort, videoChanel);		
		
		Main.gjService.update(new GuanJia(Main.currentDP,Main.localParamsForGJ.getName(),serverIP, videoIP, videoPort, videoChanel));		
		Main.localParamsForGJ = Main.gjService.find(Main.currentDP);								
		Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();												
		return true;				
	}

	private boolean saveOnlineParamsDT() {		
		if (newFlag==autoControlFlag && newFlag2 == autoControlFlag2) {
			Toast.makeText(this, "参数无变化", Toast.LENGTH_SHORT).show();
			return false;
		} else {			
			CMD = FinalConstant.SET_PARAMS;
			if (newFlag == 0) {
				PARAMS0 = "" + FinalConstant.OFF;
			} else {
				PARAMS0 = "" + FinalConstant.ON;				
			}	
			if (newFlag2 == 0) {
				PARAMS1 = "" + FinalConstant.OFF;
			} else {
				PARAMS1 = "" + FinalConstant.ON;				
			}
			loginAnim();
			new Thread(controlOrSet).start();	
			return true;
		}		
	}
	
	// 连接动画
	private void loginAnim(){		
		linkAnimPage.setVisibility(View.VISIBLE);
		loading.startAnimation(loadingAnim);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {    	
    	if(keyCode == KeyEvent.KEYCODE_BACK){    		   		
			return false;					
		}
		return false;
	}
	
	private Runnable query = new Runnable(){
		
		@Override
		public void run() {
			isQuery = true;
			linkStartTime = Calendar.getInstance().getTimeInMillis();
			while (isQuery) {
				long now = Calendar.getInstance().getTimeInMillis();
				if (now - linkStartTime < FinalConstant.WAIT_TIME) {
					String back = HttpSendService.query(PATH, CMD);
					if (back != null) {
						Message msg = mHandler.obtainMessage(FinalConstant.QUERY_BACK_DATA);
			            Bundle bundle = new Bundle();
			            bundle.putString(FinalConstant.BACK_INFO, back);
			            msg.setData(bundle);
			            mHandler.sendMessage(msg);
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					mHandler.sendEmptyMessage(FinalConstant.TIME_OUT);
				}				
			}			
		}		
	};
	
	private Runnable controlOrSet = new Runnable(){
		
		@Override
		public void run() {
			isControlOrSet = true;
			linkStartTime = Calendar.getInstance().getTimeInMillis();
			while (isControlOrSet) {
				long now = Calendar.getInstance().getTimeInMillis();
				if (now - linkStartTime < FinalConstant.WAIT_TIME) {
					String back = HttpSendService.controling(PATH, CMD, PARAMS0, PARAMS1);
					if (back != null) {
						Message msg = mHandler.obtainMessage(FinalConstant.CONTROL_BACK_DATA);
			            Bundle bundle = new Bundle();
			            bundle.putString(FinalConstant.CONTROL_INFO, back);
			            msg.setData(bundle);
			            mHandler.sendMessage(msg);
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					mHandler.sendEmptyMessage(FinalConstant.TIME_OUT);
				}				
			}			
		}		
	};
}

