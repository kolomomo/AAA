package com.cdjx.kolo;

import java.util.Calendar;

import org.MediaPlayer.PlayM4.Player;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import android.view.View.OnClickListener;

import com.hikvision.netsdk.*;
import com.cdjx.kolo.R;
import com.cdjx.kolo.PlaySurfaceView;
import com.cdjx.kolo.GuanJia;
import com.cdjx.kolo.GuanJiaService;
import com.cdjx.kolo.FinalConstant;
import com.cdjx.kolo.HttpSendService;
import com.cdjx.kolo.SetupParams;
import com.cdjx.kolo.Welcome;


/**
 * Created by Administrator on 2015/11/23.
 */
public class Main extends Activity implements SurfaceHolder.Callback {
	public static GuanJia localParamsForGJ = null;
	public static GuanJiaService gjService = null;
	public static Welcome authflag =null;
    private final String TAG = "Main";
    private SurfaceView videoView = null;
    public static int currentDP = 1;
    private int[] autoControlFlag = new int[2];
    
	private Button onlinedetect = null;
	private Button control = null;
    private View controlPage = null;
    private View onlinePage = null;
    
 	private TextView dtitle1 = null;
 	private TextView dtitle2 = null;
 	private TextView dtitle3 = null;
 	private TextView dtitle4 = null;
 	private TextView dtitle5 = null;
 	private TextView dtitle6 = null;
 	private TextView dtitle7 = null;	
	// 实时数据表
 	private TextView[] tv_data = null;
	// 设备管控button	
	private View[] keyControl = null;
	private int[] flagControl = null;
	private int keyBeClicked = 0;
	private Button controlYes = null;
	private Button controlCancle = null;
	private TextView controlRemaind = null;
 	//video
 	private View playControlPage = null;
 	private ImageView playorpause;
 	private TextView videoTOut = null;
 	private boolean startVideo = false;
 	private boolean videoCheck = false;
 	private View videoDrectionControl = null;
 //	private static int videoControlCmd = 0;
 	private long videoTimeOut = 0;	
 	private PlaySurfaceView mSurface;
 	private View dControlPage = null; 
 	private ImageView vControl = null;
 	private View startVideoAnimPage = null;
 	private ImageView loadingVideo = null;
	// 网络通信
	private static boolean isQuery = false;
	private static boolean isControlOrSet = false;
	private static String CMD = "";
	private static String PATH = "";
	private static String PARAMS0 = "";
	private static String PARAMS1 = "";
	private static long linkStartTime;
	// 网络异常
	private View netErrorPage = null;
	private Button setNet = null;
	private Button linkTryAgain = null;
	private Button linkCancle = null;
	private TextView errorText = null;
	//
	private TextView loadTV = null;
	private TextView onlineTableTitle = null;
	private View linkAnimPage = null;
	private ImageView loading = null;
	private Animation loadingAnim = null;
 	
 	private TextView mainTitle = null;

	private LinearLayout SCDP1 = null;
	private LinearLayout SCDP2 = null;

	
	private View clickMorePage = null;
	private View setupButton = null;
	private View quitButton = null;
	private View adminview = null;
	private View quitDialog = null;
	private View controlDialog = null;
	
	// 退出提醒按钮
	private Button quitYes = null;
	private Button quitCancle = null;
	
	private Button          m_oLoginBtn         	= null;
	private Button          m_oPreviewBtn           = null;
	private ImageView		moreButton 				= null;
	
    private int				m_iPort	= 				-1;					// play port
	private int				m_iChanNum				= 0;				//channel number
	private int				m_iPlaybackID			= -1;				// return by NET_DVR_PlayBackByTime	
	private	int 			m_iStartChan 			= 0;				// start channel no
	private static PlaySurfaceView [] playView = new PlaySurfaceView[4];
	private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

	private int				m_iLogID				= -1;				// return by NET_DVR_Login_v30
	private int 			m_iPlayID				= -1;				// return by NET_DVR_RealPlay_V30
	private boolean			m_bNeedDecode			= true;
	private boolean			m_bMultiPlay			= false;
	private long exitTime = 0;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        gjService = new GuanJiaService(this);
        authflag = new Welcome();
		currentDP = 1;
		if (gjService.getCount() == 0) 
		{			
			gjService.save(new GuanJia(1,"开心阳台1","http://192.168.1.88:80/dphoneapi.php","118.113.95.71","8000","3"));
			gjService.save(new GuanJia(2,"开心阳台2","http://www.cdghny.com/d2phoneapi.php","118.113.95.71","8000","2"));
		}
		localParamsForGJ = gjService.find(currentDP);
        if (!initeSdk()) {
            this.finish();
            return;
        }

        if (!initeActivity()) {
            this.finish();
            return;
        }
        setButtonFocus(onlinedetect);
    }
    @Override
	protected void onResume() {
		Log.e("--- Main ---", "onResume");
		super.onResume();
		if(netErrorPage.getVisibility() == View.VISIBLE) {
			netErrorPage.setVisibility(View.GONE);
		}
		if (linkAnimPage.getVisibility() == View.VISIBLE) {
			linkAnimPage.setVisibility(View.GONE);
			loading.clearAnimation();
		}						
		localParamsForGJ = gjService.find(currentDP);
		mainTitle.setText(localParamsForGJ.getName());
		videoDrectionControl.setVisibility(View.GONE);
		onlineTableTitle.setText(localParamsForGJ.getName()+"当前环境数据");		
		tryagagin();		
	}
//拉拉********************************************************************************************************	
	private void tryagagin() {
		switch (currentDP) {
		case 1:
			dtitle7.setText("土壤湿度1:");
			dtitle1.setText("土壤湿度2");
			dtitle2.setText("土壤湿度3");
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 2:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 3:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 4:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 5:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 6:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 7:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 8:
			dtitle7.setText("浅层水分/%RH:");	
			dtitle1.setText("中层水分/%RH:");
			dtitle2.setText("深层水分/%RH:");	
			dtitle3.setText("土壤PH值:");	
			dtitle4.setText("土壤温度/℃:");
			dtitle5.setText("");	
			dtitle6.setText("");
			break;	
		case 9:
			dtitle7.setText("浅层水分/%RH:");	
			dtitle1.setText("中层水分/%RH:");
			dtitle2.setText("深层水分/%RH:");	
			dtitle3.setText("土壤PH值:");	
			dtitle4.setText("土壤温度/℃:");
			dtitle5.setText("");	
			dtitle6.setText("");
			break;	
		case 10:
			dtitle1.setText("降雨量/mm:");
			dtitle2.setText("风向/度:");	
			dtitle3.setText("风速/m/s:");
			dtitle4.setText("土壤温度/℃:");
			dtitle5.setText("");	
			dtitle6.setText("");
			break;	
		}						
		
        PATH = localParamsForGJ.getServerPath();                                                                      
			CMD = FinalConstant.QUERY_DATA;			           
			loadTV.setText("正在载入当前监测值……");                                                                   
		new Thread(query).start();                             
		loginAnim();	                                          		
	}

    //@Override
    public void surfaceCreated(SurfaceHolder holder) {
        videoView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created");
        if (-1 == m_iPort)
        {
            return;
        }
        Surface surface = holder.getSurface();
        if (true == surface.isValid()) {
            if (false == Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }
    //@Override  
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {   
    }  
    //@Override  
    public void surfaceDestroyed(SurfaceHolder holder) {  
    	Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
		if (-1 == m_iPort)
		{
			return;
		}
        if (true == holder.getSurface().isValid()) {
        	if (false == Player.getInstance().setVideoWindow(m_iPort, 0, null)) {	
        		Log.e(TAG, "Player setVideoWindow failed!");
        	}
        }
    }
	@Override  
	protected void onSaveInstanceState(Bundle outState) {    
		outState.putInt("m_iPort", m_iPort);  
		super.onSaveInstanceState(outState);  
		Log.i(TAG, "onSaveInstanceState"); 
	}  
	@Override  
	protected void onRestoreInstanceState(Bundle savedInstanceState) {  
		m_iPort = savedInstanceState.getInt("m_iPort");  
		super.onRestoreInstanceState(savedInstanceState);  
		Log.i(TAG, "onRestoreInstanceState" ); 
	}  
    /**
     * @param
     * @param
     * @return true - success;false - fail
     * @fn initeSdk
     * @author zhuzhenlei
     * @brief SDK init
     */
    private boolean initeSdk() {
        //init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
        return true;
    }

    // GUI init
    private boolean initeActivity() {
        findViews();
        videoView.getHolder().addCallback(this);
        setListeners();
        return true;
    }

    private void findViews() {
    	controlPage = (View) this.findViewById(R.id.controlPage);
    	videoView = (SurfaceView) findViewById(R.id.Sur_Player);
        m_oLoginBtn = (Button) findViewById(R.id.btn_Login);
    	m_oPreviewBtn = (Button) findViewById(R.id.btn_Preview);
    	moreButton = (ImageView) this.findViewById(R.id.more);
		clickMorePage = (View) this.findViewById(R.id.MorePage);
		
		adminview = (View) this.findViewById(R.id.administrator);
		
		setupButton = (View) this.findViewById(R.id.setup);
		
		SCDP1 = (LinearLayout) this.findViewById(R.id.SCDP1);
	
		SCDP2 = (LinearLayout) this.findViewById(R.id.SCDP2);
		
		quitButton = (View) this.findViewById(R.id.quit);
		
		quitDialog = (View) this.findViewById(R.id.quitDialog);
		quitYes = (Button) this.findViewById(R.id.btnQuitYes);
		quitCancle = (Button) this.findViewById(R.id.btnQuitCancle);
		onlinedetect = (Button) this.findViewById(R.id.onlinedetect);		
		control = (Button) this.findViewById(R.id.control);
		onlinePage = (View) this.findViewById(R.id.onlinePage);
		controlPage = (View) this.findViewById(R.id.controlPage);
		playControlPage = (View) this.findViewById(R.id.pause);
		playorpause = (ImageView) this.findViewById(R.id.playorpause);
		videoTOut = (TextView) this.findViewById(R.id.videoTimeOut);
		startVideoAnimPage = (View) this.findViewById(R.id.startVideoAnim);
		loadingVideo = (ImageView) this.findViewById(R.id.loadingVideo);

		
		onlineTableTitle = (TextView) this.findViewById(R.id.onlineDataTableTitle);
		dtitle1 = (TextView) this.findViewById(R.id.datatitle1);
		dtitle2 = (TextView) this.findViewById(R.id.datatitle2);
		dtitle3 = (TextView) this.findViewById(R.id.datatitle3);
		dtitle4 = (TextView) this.findViewById(R.id.datatitle4);
		dtitle5 = (TextView) this.findViewById(R.id.datatitle5);
		dtitle6 = (TextView) this.findViewById(R.id.datatitle6);
		dtitle7 = (TextView) this.findViewById(R.id.datatitle7);
		
		tv_data = new TextView[8];

		tv_data[0] = (TextView) this.findViewById(R.id.DATA0);
		tv_data[1] = (TextView) this.findViewById(R.id.DATA1);
		tv_data[2] = (TextView) this.findViewById(R.id.DATA2);
		tv_data[3] = (TextView) this.findViewById(R.id.DATA3);
		tv_data[4] = (TextView) this.findViewById(R.id.DATA4);
		tv_data[5] = (TextView) this.findViewById(R.id.DATA5);
		tv_data[6] = (TextView) this.findViewById(R.id.DATA6);
		tv_data[7] = (TextView) this.findViewById(R.id.DATA7);
		
		mainTitle = (TextView) this.findViewById(R.id.dp_name);
		
		loadTV = (TextView) this.findViewById(R.id.loadingTV);
		
		linkAnimPage = (View) this.findViewById(R.id.linkAnimPage);
		loading = (ImageView) this.findViewById(R.id.linkAnim);
		loadingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
		
		netErrorPage = (View) this.findViewById(R.id.netErrorPage);
		setNet = (Button) this.findViewById(R.id.setNetWork);		
		linkTryAgain = (Button) this.findViewById(R.id.linkTryAgain);		
		linkCancle = (Button) this.findViewById(R.id.linkCancle);	
		errorText = (TextView) this.findViewById(R.id.netErrorTV);
		videoDrectionControl = (View)this.findViewById(R.id.videoControl);
		((ImageView) this.findViewById(R.id.videoStop)).setOnClickListener(more_Listener);
		
		((ImageView) this.findViewById(R.id.zoomIn)).setOnClickListener(more_Listener);
		((ImageView) this.findViewById(R.id.zoomIn)).setOnTouchListener(PTZ_Listener);
		((ImageView) this.findViewById(R.id.zoomOut)).setOnClickListener(more_Listener);
		((ImageView) this.findViewById(R.id.zoomOut)).setOnTouchListener(PTZ_Listener);
		((ImageView) this.findViewById(R.id.btn_to_left)).setOnClickListener(more_Listener);
		((ImageView) this.findViewById(R.id.btn_to_right)).setOnClickListener(more_Listener);
		((ImageView) this.findViewById(R.id.btn_to_up)).setOnClickListener(more_Listener);
		((ImageView) this.findViewById(R.id.btn_to_down)).setOnClickListener(more_Listener);		
		((ImageView) this.findViewById(R.id.btn_to_left)).setOnTouchListener(PTZ_Listener);
		((ImageView) this.findViewById(R.id.btn_to_right)).setOnTouchListener(PTZ_Listener);
		((ImageView) this.findViewById(R.id.btn_to_up)).setOnTouchListener(PTZ_Listener);
		((ImageView) this.findViewById(R.id.btn_to_down)).setOnTouchListener(PTZ_Listener);	
		
		controlDialog = (View) this.findViewById(R.id.controlDialog);
		controlDialog.getBackground().setAlpha(89);
		controlYes = (Button) this.findViewById(R.id.controlYes);
		controlCancle = (Button) this.findViewById(R.id.controlCancle);
		controlRemaind = (TextView) this.findViewById(R.id.tv_control_remaind);
		
		mSurface = new PlaySurfaceView(Main.this);
		vControl = ((ImageView) this.findViewById(R.id.vControl));
		dControlPage = (View) this.findViewById(R.id.dControl);
		keyControl = new View[5];			
		flagControl = new int[5];	
		
		int id0 = R.id.key0;
		int i=0;
		for (i=0; i<5; i++) {
			keyControl[i] = (ImageView) this.findViewById(id0 + i*2);
			keyControl[i].setOnClickListener(more_Listener);
			keyControl[i].setBackgroundResource(R.drawable.btn_gk_off);
		}

    }

    // listen
    private void setListeners()
    {
    	m_oLoginBtn.setOnClickListener(Login_Listener);
    	m_oPreviewBtn.setOnClickListener(Preview_Listener);
    	moreButton.setOnClickListener(more_Listener);
    	clickMorePage.setOnClickListener(more_Listener);
    	setupButton.setOnClickListener(more_Listener);
    	quitButton.setOnClickListener(more_Listener);
    	SCDP1.setOnClickListener(more_Listener);	
    	SCDP2.setOnClickListener(more_Listener);
		quitDialog.getBackground().setAlpha(89);
		quitDialog.setOnClickListener(more_Listener);
		quitYes.setOnClickListener(more_Listener);
		quitCancle.setOnClickListener(more_Listener);
    	linkAnimPage.setOnClickListener(more_Listener);
    	linkAnimPage.getBackground().setAlpha(100);
    	
		netErrorPage.getBackground().setAlpha(89);
		netErrorPage.setOnClickListener(more_Listener);
		setNet.setOnClickListener(more_Listener);
		linkTryAgain.setOnClickListener(more_Listener);
		linkCancle.setOnClickListener(more_Listener);
		control.setOnClickListener(more_Listener);
		onlinedetect.setOnClickListener(more_Listener);
		playorpause.setOnClickListener(more_Listener);
		videoDrectionControl.getBackground().setAlpha(0);
		videoDrectionControl.setOnClickListener(more_Listener);
		dControlPage.setOnClickListener(more_Listener);
		vControl.setOnClickListener(more_Listener);
		controlDialog.setOnClickListener(more_Listener);
		controlCancle.setOnClickListener(more_Listener);
		controlYes.setOnClickListener(more_Listener);
		startVideoAnimPage.setOnClickListener(more_Listener);
		startVideoAnimPage.getBackground().setAlpha(0);
    }
    
 // 切换大棚，
 	private void changeToAnotherDP () {
 		
		if (linkAnimPage.getVisibility() == View.VISIBLE) {
			linkAnimPage.setVisibility(View.GONE);
			loading.clearAnimation();
		}		

 		localParamsForGJ = gjService.find(currentDP);
 		mainTitle.setText(localParamsForGJ.getName());
 		playControlPage.setVisibility(View.VISIBLE);
 		
 		onlineTableTitle.setText(localParamsForGJ.getName()+"当前环境数据");	
 		
		int h = 0;
		for (h=0; h<8; h++) {	
			tv_data[h].setText("--");									
		}

 		switch (currentDP) {
		case 1:
			dtitle7.setText("土壤湿度：");
			dtitle1.setText("土壤湿度：");
			dtitle2.setText("土壤湿度：");
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 2:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 3:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 4:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 5:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 6:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 7:
			dtitle7.setText("表层水分/%RH:");	
			dtitle1.setText("浅层水分/%RH:");
			dtitle2.setText("中层水分/%RH:");	
			dtitle3.setText("土壤温度:");	
			dtitle4.setText("CO");
			dtitle5.setText("2");	
			dtitle6.setText("/ppm:");	
			break;
		case 8:
			dtitle7.setText("浅层水分/%RH:");	
			dtitle1.setText("中层水分/%RH:");
			dtitle2.setText("深层水分/%RH:");	
			dtitle3.setText("土壤PH值:");	
			dtitle4.setText("土壤温度/℃:");
			dtitle5.setText("");	
			dtitle6.setText("");
			break;	
		case 9:
			dtitle7.setText("浅层水分/%RH:");	
			dtitle1.setText("中层水分/%RH:");
			dtitle2.setText("深层水分/%RH:");	
			dtitle3.setText("土壤PH值:");	
			dtitle4.setText("土壤温度/℃:");
			dtitle5.setText("");	
			dtitle6.setText("");
			break;	
		case 10:
			dtitle1.setText("降雨量/mm:");
			dtitle2.setText("风向/度:");	
			dtitle3.setText("风速/m/s:");
			dtitle4.setText("土壤温度/℃:");
			dtitle5.setText("");	
			dtitle6.setText("");
			break;		
 		}

        PATH = localParamsForGJ.getServerPath();                    
		if (controlPage.getVisibility() == View.VISIBLE) {       
			CMD = FinalConstant.QUERY_PARAMS;			         			
			loadTV.setText("正在切换至" + localParamsForGJ.getName() + "……");            
		} else {                                          
			CMD = FinalConstant.QUERY_DATA;			        
			loadTV.setText("正在切换至" + localParamsForGJ.getName() + "……");        
		} 
		new Thread(query).start();                            
		loginAnim();
		if (!localParamsForGJ.getVideoIP().equals("")) {
		if(m_iPlayID >= 0)
		{
			stopSinglePreview();
	 //s   	m_bMultiPlay = false;
	    	m_oPreviewBtn.setText("预览");
	    	if(!authflag.authFlag)
	    	{
			videoDrectionControl.setVisibility(View.INVISIBLE);
			((ImageView) this.findViewById(R.id.vControl)).setVisibility(View.VISIBLE);
			dControlPage.setVisibility(View.INVISIBLE);
	    	}
			startVideo = false;
		}
	}
 }
 	
	// 连接动画
	private void loginAnim(){
		linkAnimPage.setVisibility(View.VISIBLE);
		loading.startAnimation(loadingAnim);
	}
	
	 //ptz listener
    private View.OnTouchListener PTZ_Listener = new View.OnTouchListener()
    {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(v.getId()) {
			case R.id.zoomIn:
				vControl.setVisibility(View.VISIBLE);
				dControlPage.setVisibility(View.INVISIBLE);
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
  //   				videoControlCmd = FinalConstant.zoomin_start;
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_IN, 0);
//					videoHandler.obtainMessage(FinalConstant.zoomin_start, 2, -1).sendToTarget();
					//Log.e("DEBUG============>","按下");
					break;
				case MotionEvent.ACTION_UP:
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_IN, 1);
//					videoControlCmd = FinalConstant.zoomin_end;
//					videoHandler.obtainMessage(FinalConstant.zoomin_end, 2, -1).sendToTarget();
					//Log.e("DEBUG============>","离开===");
					break;
				}						
				break;
			case R.id.zoomOut:
				vControl.setVisibility(View.VISIBLE);
				dControlPage.setVisibility(View.INVISIBLE);
				
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_OUT, 0);
					//Log.e("DEBUG============>","按下");
					break;
				case MotionEvent.ACTION_UP:
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_OUT, 1);
					//Log.e("DEBUG============>","离开===");
					break;
				}
				break;
			case R.id.btn_to_left:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_LEFT, 0);
					//Log.e("DEBUG============>","按下");
					break;
				case MotionEvent.ACTION_UP:
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_LEFT, 1);
					//Log.e("DEBUG============>","离开===");
					break;
				}
				break;
			case R.id.btn_to_right:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_RIGHT, 0);
					//Log.e("DEBUG============>","按下");
					break;
				case MotionEvent.ACTION_UP:
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.PAN_RIGHT, 1);
					//Log.e("DEBUG============>","离开===");
					break;
				}
				break;
			case R.id.btn_to_up:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.TILT_UP, 0);
					//Log.e("DEBUG============>","按下===");
					break;
				case MotionEvent.ACTION_UP:
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.TILT_UP, 1);
					//Log.e("DEBUG============>","离开");
					break;
				}
				break;
			case R.id.btn_to_down:
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.TILT_DOWN, 0);
					//Log.e("DEBUG============>","按下");
					break;
				case MotionEvent.ACTION_UP:
					HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.TILT_DOWN, 1);
					//Log.e("DEBUG============>","离开===");
					break;
				}
				break;
			}
		return false;
		}
    };
 	private View.OnClickListener more_Listener = new View.OnClickListener()
	{
 	public void onClick(View v) {
	switch(v.getId()) {
	case R.id.dControl:
			vControl.setVisibility(View.VISIBLE);
			dControlPage.setVisibility(View.GONE);
	case R.id.vControl:
		dControlPage.setVisibility(View.VISIBLE);	
		vControl.setVisibility(View.GONE);
		break;
		
	case R.id.zoomIn:
	case R.id.zoomOut:
	case R.id.btn_to_left:
	case R.id.btn_to_right:
	case R.id.btn_to_up:
	case R.id.btn_to_down:   
 	case R.id.MorePage:
		clickMorePage.setVisibility(View.GONE);
		break;
		// 设备管控
	case R.id.controlCancle:
			controlDialog.setVisibility(View.GONE);
			break;
	case R.id.controlDialog:
		break;
	case R.id.controlYes:
		dpControl(keyBeClicked);
		break;
 	case R.id.key0:
		keyBeClicked = 0;
		if (flagControl[keyBeClicked] == 0) {
			controlRemaind.setText("确定打开热风机？");
		} else {
			controlRemaind.setText("确定关闭热风机？");
		}
		controlDialog.setVisibility(View.VISIBLE);
		break;
	case R.id.key1:
		keyBeClicked = 1;
		if (flagControl[keyBeClicked] == 0) {
			controlRemaind.setText("确定打开湿帘？");
		} else {
			controlRemaind.setText("确定关闭湿帘？");
		}
		controlDialog.setVisibility(View.VISIBLE);
		break;
	case R.id.key2:
		keyBeClicked = 2;
		if (flagControl[keyBeClicked] == 0) {
			controlRemaind.setText("确定打开补光灯？");
		} else {
			controlRemaind.setText("确定关闭补光灯？");
		}
		controlDialog.setVisibility(View.VISIBLE);
		break;
	case R.id.key3:
		keyBeClicked = 3;
		if (flagControl[keyBeClicked] == 0) {
			controlRemaind.setText("确定打开喷灌A？");
		} else {
			controlRemaind.setText("确定关闭喷灌A？");
		}
		controlDialog.setVisibility(View.VISIBLE);
		break;
	case R.id.key4:
		if (autoControlFlag[0] == 1) {
			Toast.makeText(Main.this, R.string.cannotControl1, Toast.LENGTH_SHORT).show();
		} else {
			keyBeClicked = 4;
			if (flagControl[keyBeClicked] == 0) {
				controlRemaind.setText("确定打开喷灌B？");
			} else {
				controlRemaind.setText("确定关闭喷灌B？");
			}
			controlDialog.setVisibility(View.VISIBLE);					
		}			
		break;
		// 在线监测页	
	case R.id.onlinedetect:
					setButtonFocus(onlinedetect);
					if (onlinePage.getVisibility() != View.VISIBLE) {
	//					playControlPage.setVisibility(View.VISIBLE);///////////////////////0621
						onlinePage.setVisibility(View.VISIBLE);
						controlPage.setVisibility(View.GONE);	    
						
						isQuery = false;
						CMD = FinalConstant.QUERY_DATA;
						new Thread(query).start();
						loadTV.setText("正在载入当前监测值……");
						loginAnim();								  
					}						
					break;						
					
				// 设备管控页
	case R.id.control:			
					setButtonFocus(control);
					if (controlPage.getVisibility() != View.VISIBLE) {
						controlPage.setVisibility(View.VISIBLE);
						onlinePage.setVisibility(View.GONE);	
						
						isQuery = false;
						CMD = FinalConstant.QUERY_PARAMS;
						new Thread(query).start();				
						loadTV.setText("正在载入当前设备状态……");
						loginAnim();

	/*					if (videoView.playFlag) {	
							videoView.stopPlay(); 	
							videoDrectionControl.setVisibility(View.GONE);
							((ImageView) this.findViewById(R.id.vControl)).setVisibility(View.VISIBLE);
							dControlPage.setVisibility(View.GONE);
						}
						startVideo = false;
	*/				}			
					break;
	case R.id.playorpause:
		playControlPage.setVisibility(View.GONE);
		if(!authflag.authFlag){
		videoDrectionControl.setVisibility(View.VISIBLE);
		}
		videoTOut.setVisibility(View.GONE);
		startVideoAnimPage.setVisibility(View.VISIBLE);
		loadingVideo.startAnimation(loadingAnim);
		if(m_iLogID < 0)
		{
			// login on the device
			m_iLogID = loginDevice();
			if (m_iLogID < 0)
			{
				Log.e(TAG, "This device logins failed!");
				return;
			}
			// get instance of exception callback and set
			ExceptionCallBack oexceptionCbf = getExceptiongCbf();
			if (oexceptionCbf == null)
			{
			    Log.e(TAG, "ExceptionCallBack object is failed!");
			    return ;
			}

			if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf))
		    {
		        Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
		        return;
		    }

			m_oLoginBtn.setText("断开连接");
			Log.i(TAG, "Login sucess ****************************1***************************");
		}
		if (!startVideo) {
			startVideo = true;
			startSinglePreview();
			}
		else
		{
			stopSinglePreview();
			m_oPreviewBtn.setText("Preview");
		}
		videoCheck = true;
		videoTimeOut = Calendar.getInstance().getTimeInMillis();
		new Thread(checkVideo).start();
		break;
	case R.id.videoStop:
		playControlPage.setVisibility(View.VISIBLE);
		if(!authflag.authFlag){
		videoDrectionControl.setVisibility(View.GONE);
		vControl.setVisibility(View.VISIBLE);
		dControlPage.setVisibility(View.GONE);
		}
		videoTOut.setVisibility(View.GONE);
		playorpause.setBackgroundResource(R.drawable.pause_xh);
// whether we have logout
	if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID))
			{
				Log.e(TAG, " NET_DVR_Logout is failed!");
				return;
			}
			m_oLoginBtn.setText("连接");
			m_iLogID = -1;
	
		if (startVideo) {
			stopSinglePreview();
			startVideo = false;
		}			
		break;
	case R.id.setup:
		if(!authflag.authFlag){
		startActivityForResult(new Intent(Main.this,SetupParams.class), FinalConstant.PARAMS_SETUP);
        clickMorePage.setVisibility(View.GONE);
		}
		else
		{
			Toast.makeText(Main.this, "用户暂不支持", Toast.LENGTH_SHORT).show();
		}
		break;	
	case R.id.linkAnimPage:
		break;
		// 退出客户端
	case R.id.quitDialog:
			break;
	case R.id.quit:
			// 退出提醒按钮
		clickMorePage.setVisibility(View.GONE);
		quitDialog.setVisibility(View.VISIBLE);
		break;
	case R.id.btnQuitYes:
			finish();
			break;
	case R.id.btnQuitCancle:
			quitDialog.setVisibility(View.GONE);
			break;
	case R.id.netErrorPage:
			break;
			// 网络异常操作
	case R.id.setNetWork:
			netErrorPage.setVisibility(View.GONE);	
				new Thread(query).start();
				if (CMD.equals(FinalConstant.QUERY_DATA)) {
				loadTV.setText("正在载入当前监测值……");
				} else {
					loadTV.setText("正在载入当前设备状态……");
					}
			loginAnim();						
			break;
	case R.id.linkTryAgain:	
		startActivity(new Intent(Main.this,SetupParams.class));																							
		break;
	case R.id.linkCancle:
		netErrorPage.setVisibility(View.GONE);
		break;
	case R.id.more:		
		adminview.setBackgroundColor(Color.rgb(0,0,0));		
		setupButton.setBackgroundColor(Color.rgb(0,0,0));
		SCDP1.setBackgroundColor(Color.rgb(0,0,0));	
		SCDP2.setBackgroundColor(Color.rgb(0,0,0));
		quitButton.setBackgroundColor(Color.rgb(0,0,0));
		clickMorePage.setVisibility(View.VISIBLE);	
		switch (currentDP) {
		case 1:
			((TextView)SCDP1.getChildAt(1)).setTextColor(Color.rgb(153,153,153));
			((TextView)SCDP2.getChildAt(1)).setTextColor(Color.rgb(242,242,242));
			break;
		case 2:
			((TextView)SCDP1.getChildAt(1)).setTextColor(Color.rgb(242,242,242));
			((TextView)SCDP2.getChildAt(1)).setTextColor(Color.rgb(153,153,153));
			break;
		}
		break;
		
		//*********************************************
	case R.id.SCDP1:
		clickMorePage.setVisibility(View.GONE);
		if (currentDP != 1) {
			currentDP = 1;
			changeToAnotherDP();			
		}			
		break;
	case R.id.SCDP2:
		clickMorePage.setVisibility(View.GONE);
		if (currentDP != 2) {
			currentDP = 2;
			changeToAnotherDP();			
		}			
		break;
		}		
 		}
	};
	
    /*Listen*/
    private Button.OnClickListener Login_Listener = new Button.OnClickListener() 
	{
		public void onClick(View v) 
		{
			try
			{
				if(m_iLogID < 0)
				{
					// login on the device
					m_iLogID = loginDevice();
					if (m_iLogID < 0)
					{
						Log.e(TAG, "This device logins failed!");
						return;
					}
					// get instance of exception callback and set
					ExceptionCallBack oexceptionCbf = getExceptiongCbf();
					if (oexceptionCbf == null)
					{
					    Log.e(TAG, "ExceptionCallBack object is failed!");
					    return ;
					}
					
					if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf))
				    {
				        Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
				        return;
				    }
					
					m_oLoginBtn.setText("断开连接");
					Log.i(TAG, "Login sucess ****************************1***************************");
				}
				else
				{
					// whether we have logout
					if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID))
					{
						Log.e(TAG, " NET_DVR_Logout is failed!");
						return;
					}
					m_oLoginBtn.setText("连接");
					m_iLogID = -1;
				}		
			} 
			catch (Exception err)
			{
				Log.e(TAG, "error: " + err.toString());
			}
		}
	};
	// Preview listener 
    private Button.OnClickListener Preview_Listener = new Button.OnClickListener() 
	{
		public void onClick(View v) 
		{
			
			try
			{
				((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
			    hideSoftInputFromWindow(Main.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);				
				if(m_iLogID < 0)
				{
					Log.e(TAG,"please login on device first");
					return ;
				}
				if(m_bNeedDecode)
				{
					if(m_iChanNum < 1)//preview more than a channel
					{
						if(!m_bMultiPlay)
						{
							startMultiPreview();
					    	m_bMultiPlay = true;
					    	m_oPreviewBtn.setText("停止预览");
						}
						else
						{
							stopMultiPreview();
							m_bMultiPlay = false;
							m_oPreviewBtn.setText("预览");
						}
					}
					else	//preivew a channel
					{
						if(m_iPlayID < 0)
						{	
							startSinglePreview();
						}
						else
						{
							stopSinglePreview();
							m_oPreviewBtn.setText("Preview");
						}
					}
				}
				else
				{
					
				}								
			} 
			catch (Exception err)
			{
				Log.e(TAG, "error: " + err.toString());
			}
		}
	};
	
	private void startSinglePreview()
	{
		if(m_iPlaybackID >= 0)
		{
			Log.i(TAG, "Please stop palyback first");
			return ;
		}
		RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
		if (fRealDataCallBack == null)
		{
		    Log.e(TAG, "fRealDataCallBack object is failed!");
            return ;
		}
		Log.i(TAG, "m_iStartChan:" +m_iStartChan);
		        
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan+Integer.parseInt(localParamsForGJ.getVideoChanel())-1;
        previewInfo.dwStreamType = 1; //substream
        previewInfo.bBlocked = 1;       
		// HCNetSDK start preview
        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID, previewInfo, fRealDataCallBack);
		if (m_iPlayID < 0)
		{
		 	Log.e(TAG, "NET_DVR_RealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
		 	return ;
		}
		
		Log.i(TAG, "NetSdk Play sucess ***********************3***************************");										
		m_oPreviewBtn.setText("停止预览");
	}
	private void startMultiPreview()
	{
		DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int i = 0;
        int	ichannal = Integer.parseInt(localParamsForGJ.getVideoChanel());
   //     for(i = 0; i < 4; i++)
   //     {
        	if(playView[i] == null)
        	{
        		playView[i] = new PlaySurfaceView(this);       	       	  
                playView[i].setParam(metric.widthPixels);				    	
            	FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(  
            	FrameLayout.LayoutParams.WRAP_CONTENT,  
            	FrameLayout.LayoutParams.WRAP_CONTENT);   
            	//params.topMargin = playView[i].getCurHeight();//playView[i].getCurHeight() - (i/2) * playView[i].getCurHeight();
            	//params.leftMargin = (i%2) * playView[i].getCurWidth();
            	params.gravity = Gravity.CENTER |Gravity.TOP; 		//BOTTOM		    	
            	addContentView(playView[i], params);
        	}   	
 	   		playView[i].startPreview(m_iLogID, m_iStartChan + ichannal - 1);
  //      }
        m_iPlayID = playView[0].m_iPreviewHandle;
	}
	private void stopMultiPreview()
	{
		int i = 0;
		//for(i = 0; i < 4;i++)
	//	{
			playView[i].stopPreview();
		//}
	}
	/** 
     * @fn stopSinglePreview
     * @author zhuzhenlei
     * @brief stop preview
     * @param NULL [in]
     * @param NULL [out]
     * @return NULL
     */
	private void stopSinglePreview()
	{
		if ( m_iPlayID < 0)
		{
			Log.e(TAG, "m_iPlayID < 0");
			return;
		}
		
		//  net sdk stop preview
		if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID))
		{
			Log.e(TAG, "StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
			return;
		}
		
		m_iPlayID = -1;		
		stopSinglePlayer();
	}
	private void stopSinglePlayer()
	{
		Player.getInstance().stopSound();		
		// player stop play
		if (!Player.getInstance().stop(m_iPort)) 
        {
            Log.e(TAG, "stop is failed!");
            return;
        }	
		
		if(!Player.getInstance().closeStream(m_iPort))
		{
            Log.e(TAG, "closeStream is failed!");
            return;
        }
		if(!Player.getInstance().freePort(m_iPort))
		{
            Log.e(TAG, "freePort is failed!" + m_iPort);
            return;
        }
		m_iPort = -1;
	}
	/** 
     * @fn loginDevice
     * @author zhuzhenlei
     * @brief login on device
     * @param NULL [in]
     * @param NULL [out]
     * @return login ID
     */
	private int loginDevice()
	{
		// get instance
		m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
		if (null == m_oNetDvrDeviceInfoV30)
		{
			Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
			return -1;
		}
	//	String strIP = m_oIPAddr.getText().toString();
	//	int	nPort = Integer.parseInt(m_oPort.getText().toString());
	//	String strUser = m_oUser.getText().toString();
	//	String strPsd = m_oPsd.getText().toString();
		// call NET_DVR_Login_v30 to login on, port 8000 as default
		// kolo
		String strIP = localParamsForGJ.getVideoIP();
		int	nPort = Integer.parseInt(localParamsForGJ.getVideoPort());
		String strUser = "admin";
		String strPsd = "cdjx1234";
	//	cameraInfo.channel =Integer.parseInt(localParamsForGJ.getVideoChanel())+32;
		int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, nPort, strUser, strPsd, m_oNetDvrDeviceInfoV30);
		if (iLogID < 0)
		{
			Log.e(TAG, "NET_DVR_Login is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
			return -1;
		}
		if(m_oNetDvrDeviceInfoV30.byChanNum > 0)
		{
			m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
			m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
		}
		else if(m_oNetDvrDeviceInfoV30.byIPChanNum > 0)
		{
			m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
			m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
		}
		Log.i(TAG, "NET_DVR_Login is Successful!");
		
		return iLogID;
	}
	
	/**
     * @fn getExceptiongCbf
     * @author zhuzhenlei
     * @brief process exception
     * @param NULL [in]
     * @param NULL [out]
     * @return exception instance
     */
	private ExceptionCallBack getExceptiongCbf()
	{
	    ExceptionCallBack oExceptionCbf = new ExceptionCallBack()
        {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle)
            {
            	System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
	}
	/** 
     * @fn getRealPlayerCbf
     * @author zhuzhenlei
     * @brief get realplay callback instance
     * @param NULL [in]
     * @param NULL [out]
     * @return callback instance
     */
	private RealPlayCallBack getRealPlayerCbf()
	{
	    RealPlayCallBack cbf = new RealPlayCallBack()
        {
             public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize)
             {
            	// player channel 1
            	Main.this.processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME); 
             }
        };
        return cbf;
	}
	/** 
     * @fn processRealData
     * @author zhuzhenlei
     * @brief process real data
     * @param iPlayViewNo - player channel [in]
     * @param iDataType	  - data type [in]
     * @param pDataBuffer - data buffer [in]
     * @param iDataSize   - data size [in]
     * @param iStreamMode - stream mode [in]
     * @param NULL [out]
     * @return NULL
     */
	public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode)
	{
		if(!m_bNeedDecode)
		{
		//   Log.i(TAG, "iPlayViewNo:" + iPlayViewNo + ",iDataType:" + iDataType + ",iDataSize:" + iDataSize);
		}
		else
		{
			if(HCNetSDK.NET_DVR_SYSHEAD == iDataType)
		    {
		    	if(m_iPort >= 0)
	    		{
	    			return;
	    		}	    			
	    		m_iPort = Player.getInstance().getPort();	
	    		if(m_iPort == -1)
	    		{
	    			Log.e(TAG, "getPort is failed with: " + Player.getInstance().getLastError(m_iPort));
	    			return;
	    		}
	    		Log.i(TAG, "getPort succ with: " + m_iPort);
	    		if (iDataSize > 0)
	    		{
	    			if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
	    			{
	    				Log.e(TAG, "setStreamOpenMode failed");
	    				return;
	    			}
	    			if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2*1024*1024)) //open stream
	    			{
	    				Log.e(TAG, "openStream failed");
	    				return;
	    			}
	    			if (!Player.getInstance().play(m_iPort, videoView.getHolder())) 
	    			{
	    				Log.e(TAG, "play failed");
	    				return;
	    			}	
	    			if(!Player.getInstance().playSound(m_iPort))
					{
						Log.e(TAG, "playSound failed with error code:" + Player.getInstance().getLastError(m_iPort));
						return;
					}
	    		}
		    }
		    else
		    {
		    	if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize))
    			{
//		    		Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
  		    	    for(int i = 0; i < 4000 && m_iPlaybackID >=0 ; i++)
		    		{
		    			if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize))
		    				Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
		    			else
		    				break;
		    			try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						
						}	    				
		    		}
		    	}

		    }		
		}
	    
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
	private Runnable VideoPlay = new Runnable(){
		@Override
		public void run() {
			startSinglePreview();		
		}		
	};
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what) {				
			case FinalConstant.QUERY_BACK_DATA:
				linkStartTime = Calendar.getInstance().getTimeInMillis();
				if (!CMD.equals(FinalConstant.QUERY_PARAMS)&& linkAnimPage.getVisibility() == View.VISIBLE) {
					linkAnimPage.setVisibility(View.GONE);
					loading.clearAnimation();
				}
				analyzeData(msg.getData().getString(FinalConstant.BACK_INFO));
				break;
			case FinalConstant.CONTROL_BACK_DATA:
				linkStartTime = Calendar.getInstance().getTimeInMillis();
				if (CMD.equals(FinalConstant.QUERY_STATE) && linkAnimPage.getVisibility() == View.VISIBLE) {
					linkAnimPage.setVisibility(View.GONE);
					loading.clearAnimation();
				}
				analyzeControlData(msg.getData().getString(FinalConstant.CONTROL_INFO));
				break;
		
			case FinalConstant.VIDEO_OK:
				startVideoAnimPage.setVisibility(View.GONE);
				loadingVideo.clearAnimation();
				if(!authflag.authFlag){
				videoDrectionControl.setVisibility(View.VISIBLE);
				}
				break;
				// 加载视频超时
			case FinalConstant.VIDEO_TIMEOUT:
				startVideoAnimPage.setVisibility(View.GONE);
				loadingVideo.clearAnimation();
					
				playControlPage.setVisibility(View.VISIBLE);
				videoTOut.setVisibility(View.VISIBLE);
				playorpause.setBackgroundResource(R.drawable.pause_xh);				
					startVideo = false;
					break;			
				
			case FinalConstant.TIME_OUT:
				isQuery = false;
				isControlOrSet = false;
				if (linkAnimPage.getVisibility() == View.VISIBLE) {
					linkAnimPage.setVisibility(View.GONE);
					loading.clearAnimation();
				}	
				
				netErrorPage.setVisibility(View.VISIBLE);	
				
				if (CMD.equals(FinalConstant.QUERY_DATA)) {										
					errorText.setText("等待超时，获取数据失败");
				} else if (CMD.equals(FinalConstant.QUERY_STATE)) {
					errorText.setText("等待超时，获取设备状态失败");
				} else if (CMD.equals(FinalConstant.QUERY_PARAMS)) {
					errorText.setText("等待超时，查询系统工作模式失败");
				} else if (CMD.equals(FinalConstant.CONTROL)) {
					errorText.setText("等待超时，设备控制失败");
				}
				break;
			case FinalConstant.VIDEO_PLAY:								
				new Thread(VideoPlay).start();
				break;
			}
		}  	
    };	
	
    protected void analyzeData(String jsonData) {
		try {
			JSONObject jsonOBJECT = new JSONObject(jsonData);
			String cmd = jsonOBJECT.getString("cmd");
			int ii = 0;
			String str = "";
			switch (currentDP){
			case 1:			
			if (cmd.equals(FinalConstant.QUERY_DATA)) {					
				for (ii=0; ii<8; ii++) {
					str = FinalConstant.SENSOR_DATA_HEAD + ii;
					if(ii == 4)
						tv_data[ii].setText("--");
					else if(ii == 2 || ii == 3)
						tv_data[ii].setText("" + jsonOBJECT.getInt(str));
					else
						tv_data[ii].setText("" + jsonOBJECT.getDouble(str));	
					str = "";  
				}
			} else if (cmd.equals(FinalConstant.QUERY_STATE)) {
				isQuery = false;
				for (ii=0; ii<5; ii++) {
					str = FinalConstant.CONTROL_STATE_HEAD + ii;
					flagControl[ii] = jsonOBJECT.getInt(str);
					str = "";  
				}
				setControlState();
			} else if (cmd.equals(FinalConstant.QUERY_PARAMS)) {
				isQuery = false;
				CMD = FinalConstant.QUERY_STATE;
				new Thread(query).start();
				
				for (ii=0; ii<2; ii++) {
					str = FinalConstant.PARAMS_DATA_HEAD + ii;
					autoControlFlag[ii] = jsonOBJECT.getInt(str);
					str = ""; 
				}
				remaindAutoControl();					
			}
			break;
			
			case 2:			
			if (cmd.equals(FinalConstant.QUERY_DATA)) {					
				for (ii=0; ii<8; ii++) {
					str = FinalConstant.SENSOR_DATA_HEAD + ii;
					if(ii == 6)
						tv_data[ii].setText("--");
					else if(ii == 2 || ii == 3)
						tv_data[ii].setText("" + jsonOBJECT.getInt(str));
					else
						tv_data[ii].setText("" + jsonOBJECT.getDouble(str));	
					str = "";    
				}
			} else if (cmd.equals(FinalConstant.QUERY_STATE)) {
				isQuery = false;
				for (ii=0; ii<5; ii++) {
					str = FinalConstant.CONTROL_STATE_HEAD + ii;
					flagControl[ii] = jsonOBJECT.getInt(str);
					str = "";  
				}
				setControlState();
			} else if (cmd.equals(FinalConstant.QUERY_PARAMS)) {
				isQuery = false;
				CMD = FinalConstant.QUERY_STATE;
				new Thread(query).start();
				
				for (ii=0; ii<2; ii++) {
					str = FinalConstant.PARAMS_DATA_HEAD + ii;
					autoControlFlag[ii] = jsonOBJECT.getInt(str);
					str = ""; 
				}
	 		remaindAutoControl();					
			}
			break;
			}
		} catch (JSONException e) {
			Toast.makeText(this, "JSON解析异常", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} 		
	}
 // 按钮焦点效果
 	private void setButtonFocus (View v) {
 		switch (v.getId()) {
 		case R.id.onlinedetect:
 			onlinedetect.setBackgroundResource(R.drawable.selected);
 			onlinedetect.setTextColor(Color.rgb(255,255,255));
 			control.setBackgroundResource(R.drawable.notselected);
 			control.setTextColor(Color.rgb(71,190,162));
 			break;
 		case R.id.control:
 			onlinedetect.setBackgroundResource(R.drawable.notselected);
 			onlinedetect.setTextColor(Color.rgb(71,190,162));
 			control.setBackgroundResource(R.drawable.selected);
 			control.setTextColor(Color.rgb(255,255,255));
 			break;
 		}
 	}
 
 	private void setControlState () {
		for (int i=0; i<5; i++) {
			switch (flagControl[i]) {
			case 0:
				keyControl[i].setBackgroundResource(R.drawable.btn_gk_off);
				break;
			case 1:
				keyControl[i].setBackgroundResource(R.drawable.btn_gk_on);
				break;
			}
		}
	}

	//两次返回键退出程序
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			if((System.currentTimeMillis()-exitTime) > 2000){
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 大鹏控制，待修改
	private void dpControl(int i) {
			controlDialog.setVisibility(View.GONE);
				if ( i==1 || i==2 )
				{
					if(authflag.authFlag)
					{
					Toast.makeText(Main.this, "用户暂不支持", Toast.LENGTH_SHORT).show();
					}
					else
					{
					Toast.makeText(Main.this, "暂不支持", Toast.LENGTH_SHORT).show();				
					}
				}
				else{
					if(authflag.authFlag)
					{
						Toast.makeText(Main.this, "用户暂不支持", Toast.LENGTH_SHORT).show();
					}
					else
					{
					CMD = FinalConstant.CONTROL;
					PARAMS0 = "" + FinalConstant.EQUIPMENT[i];
					if (flagControl[i] == 0) {
						// 开
						flagControl[i] = 1;		
						PARAMS1 = "" + FinalConstant.ON;
					} else {
						// 关
						flagControl[i] = 0;
						PARAMS1 = "" + FinalConstant.OFF;				
					}				
					new Thread(controlOrSet).start();	
					loadTV.setText("等待主控中心响应……");
					loginAnim();
					}
				}			
			}

	
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
	
	private void remaindAutoControl () {
		if (autoControlFlag[0] == 1) {
			Toast.makeText(Main.this, "智能灌溉系统自动控制模式", Toast.LENGTH_SHORT).show();
		}
		
		if (autoControlFlag[1] == 1) {
			Toast.makeText(Main.this, "智能灌溉系统手动控制模式", Toast.LENGTH_SHORT).show();
		}
	}

	private Runnable checkVideo = new Runnable() {

		@Override
		public void run() {
			while (videoCheck) {
				long now = Calendar.getInstance().getTimeInMillis();
				if (now - videoTimeOut < FinalConstant.WAIT_TIME) {		
						videoCheck = false;
						mHandler.obtainMessage(FinalConstant.VIDEO_OK, 2, -1).sendToTarget();
					}
			else {
					videoCheck = false;
					mHandler.obtainMessage(FinalConstant.VIDEO_TIMEOUT, 2, -1).sendToTarget();
				
				}			
			}
		}
	};	
	
	protected void analyzeControlData(String jsonData) {
		try {
			JSONObject jsonOBJECT = new JSONObject(jsonData);			
			String cmd = jsonOBJECT.getString("cmd");
			if (cmd.equals(FinalConstant.CONTROL)) {
				isControlOrSet = false;
				if (jsonOBJECT.getString("result").equals(FinalConstant.CONTROL_SUCCESS)) {
					Toast.makeText(this, "操作成功,更新设备状态", Toast.LENGTH_SHORT).show();					
				} else {
					Toast.makeText(this, "操作失败,更新设备状态", Toast.LENGTH_SHORT).show();
				}
				CMD = FinalConstant.QUERY_STATE;
				new Thread(query).start();
			}
		} catch (JSONException e) {
			Toast.makeText(this, "JSON解析异常", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} 		
	}
}