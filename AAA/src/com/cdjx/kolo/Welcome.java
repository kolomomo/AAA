package com.cdjx.kolo;

import android.widget.CompoundButton;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Welcome extends Activity implements OnClickListener,CompoundButton.OnCheckedChangeListener {
	
	private View welcome = null;
	private SeekBar seekBar = null;
	
	private View main = null;
	private View passwordError = null;
	
	private TextView exit = null;		
	private EditText username = null;
	private EditText password = null;
	private TextView restChances = null;	
	
	private Button login = null;
	private Button btnTryAgainYes = null;
	private Button btnTryAgainNo = null;
	
	private String name = "";
	private String pd = "";
	
	private int cnt = 0;
	
	private static final int totalms = 4000;
	private int current = 0;
	public static boolean authFlag = false;

    private CheckBox login_check;
    private String isMemory = "";//isMemory变量用来判断SharedPreferences有没有数据
    static String YES = "yes";
    static String NO = "no";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0)
		{
			finish();
			return;
		}
		//	setContentView(R.layout.main);

	setContentView(R.layout.welcome);
		
		initViews();
		seekThread.start();
		current = 800;
	}
	
	private void initViews() {
		Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/SIMLI.TTF");
		((TextView) this.findViewById(R.id.icon)).setTypeface(typeface);
		Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/ziti2.ttf");
		((TextView) this.findViewById(R.id.subTitle)).setTypeface(typeface2);
		
		welcome = (View) this.findViewById(R.id.welcome);
		seekBar = (SeekBar) this.findViewById(R.id.my_seekbar);
		
		main = (View) this.findViewById(R.id.main);

		exit = (TextView) this.findViewById(R.id.exit);
		exit.setOnClickListener(this);
		
		restChances = (TextView) this.findViewById(R.id.restChances);
		
		username = (EditText) this.findViewById(R.id.username);	
		username.setFocusableInTouchMode(true);
		password = (EditText) this.findViewById(R.id.password);	
		password.setFocusableInTouchMode(true);	
						
		login = (Button) this.findViewById(R.id.login);
		login.setOnClickListener(this);

        login_check = (CheckBox) this.findViewById(R.id.login_check);
        login_check.setOnCheckedChangeListener(this);
		
		passwordError = (View) this.findViewById(R.id.pdwrong);
		passwordError.getBackground().setAlpha(89);
		
		btnTryAgainYes = (Button) this.findViewById(R.id.btnTryAgainYes);
		btnTryAgainYes.setOnClickListener(this);
		btnTryAgainNo = (Button) this.findViewById(R.id.btnTryAgainNo);
		btnTryAgainNo.setOnClickListener(this);

		checkout();
									
	}

	private void checkout(){
		//SharedPreferences将name和 pd记录起来每次进去软件时开始从中读取数据放入login_name，login_password中
            SharedPreferences remdname = getPreferences(Activity.MODE_PRIVATE);
            isMemory = remdname.getString("isMemory","");
        if (isMemory.equals(YES)){
            String name_str = remdname.getString("name", "");
            String pass_str = remdname.getString("pass", "");
            username.setText(name_str);
            password.setText(pass_str);
        }
	}

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
            {
                SharedPreferences remdname=getPreferences(Activity.MODE_PRIVATE);
                SharedPreferences.Editor edit=remdname.edit();
                edit.putString("name", username.getText().toString());
                edit.putString("pass", password.getText().toString());
                edit.putString("isMemory",YES);
                edit.commit();
            }
            if(!isChecked)
            {

                SharedPreferences remdname=getPreferences(Activity.MODE_PRIVATE);
                SharedPreferences.Editor edit=remdname.edit();
                edit.putString("isMemory",NO);
                edit.commit();
            }
        }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.exit:
			this.finish();
			break;
		case R.id.login:							
			name = username.getText().toString();
			pd = password.getText().toString();
			
			if(!name.equals("") && !pd.equals("")) {
				if( (name.equals(FinalConstant.USERNAME0)||name.equals(FinalConstant.USERNAME)) && pd.equals(FinalConstant.PASSWORD))
					{
                        if(login_check.isChecked())//检测用户名密码
                        {
                            SharedPreferences remdname=getPreferences(Activity.MODE_PRIVATE);
                            SharedPreferences.Editor edit=remdname.edit();
                            edit.putString("name", name);
                            edit.putString("pass", pd);
                            edit.putString("isMemory",YES);
                            edit.apply();
                        }
                        else
                        {
                            SharedPreferences remdname=getPreferences(Activity.MODE_PRIVATE);
                            SharedPreferences.Editor edit=remdname.edit();
                            edit.putString("name", name);
                            edit.putString("pass", pd);
                            edit.putString("isMemory",NO);
                            edit.apply();
                        }
					authFlag = AUTH();
					startActivity(new Intent(this, Main.class));
					this.finish();
				} else {
					cnt += 1;
					if (cnt != 4) {
						passwordError.setVisibility(View.VISIBLE);
					} else {
						this.finish();
					}
				}
			} else {
				Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
			}					
			break;
		case R.id.btnTryAgainNo:
			this.finish();
			break;
		case R.id.btnTryAgainYes:
			passwordError.setVisibility(View.GONE);
			if (cnt == 1) {
				restChances.setText("您还有3次尝试的机会，请谨慎！");
			} 
			if (cnt == 2) {
				restChances.setText("您还有2次尝试的机会，请谨慎！");
			}
			if (cnt == 3) {
				restChances.setText("您还有1次尝试的机会，请谨慎！");
			}
			break;
		}
		
	}		
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				seekBar.setProgress(current*100/totalms);
				break;
			case 2:
				welcome.setVisibility(View.GONE);
				main.setVisibility(View.VISIBLE);
				break;
			}
		}
		
	};
	
	public boolean AUTH (){
		if(name.equals(FinalConstant.USERNAME))			
			{
			authFlag = true;
			}
		else
		{
			authFlag = false;	
		}
			
		return authFlag;
	}
	
	private Thread seekThread = new Thread(new Runnable(){

		@Override
		public void run() {
			while (current < totalms) {
				current += 50;
				mHandler.sendEmptyMessage(1);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
			
			if (current == totalms) {
				mHandler.sendEmptyMessage(2);
			}
		}
		
	});

    private class OnCheckedChangeListener {
    }
}
