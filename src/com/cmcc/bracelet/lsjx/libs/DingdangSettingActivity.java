package com.cmcc.bracelet.lsjx.libs;

import java.util.Calendar;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.R;

import com.ai.android.picker.TimePicker;

public class DingdangSettingActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener {

	private TimePicker tpSport;
	private TimePicker tpSleep;
	private LinearLayout llSleep;
	private LinearLayout spSleep;
	private ToggleButton tbSport;
	private ToggleButton tbSleep;
	private RadioButton rbLeft;
	private RadioButton rbRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dingdang_setting);
		initViews();
		loadConfig();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	// 加载设置
	private void loadConfig() {
		SharedPreferences sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		boolean enableSport = sp.getBoolean(SharedPreferredKey.ENABLE_SPORT, false);
		boolean enableSleep = sp.getBoolean(SharedPreferredKey.ENABLE_SLEEP, false);
		int sleepRepeat = sp.getInt(SharedPreferredKey.DAYS, -1);
		int sleepHour = sp.getInt(SharedPreferredKey.SLEEP_HOUR, 17);
		int sleepMin = sp.getInt(SharedPreferredKey.SLEEP_MINUTE, 30);
		int sportRepeat = sp.getInt(SharedPreferredKey.DAYS2, -1);
		int sportHour = sp.getInt(SharedPreferredKey.SPORT_HOUR, 17);
		int sportMin = sp.getInt(SharedPreferredKey.SPORT_MINUTE, 30);
		sleepHour = sleepHour > 24 ? 17 : sleepHour;
		sportHour = sportHour > 24 ? 17 : sportHour;
		String pos = sp.getString(SharedPreferredKey.POSITION, "左手");

		tbSport.setChecked(enableSport);
		tbSleep.setChecked(enableSleep);
		tpSport.setEnabled(enableSport);
		enableSleep(enableSleep);

		loadRepeat(llSleep, sleepRepeat);
		loadRepeat(spSleep, sportRepeat);

		if (pos.equals("左手")) {
			rbLeft.setChecked(true);
		} else {
			rbRight.setChecked(true);
		}

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, sleepHour);
		cal.set(Calendar.MINUTE, sleepMin);
		tpSleep.setCalendar(cal);

		cal.set(Calendar.HOUR_OF_DAY, sportHour);
		cal.set(Calendar.MINUTE, sportMin);
		tpSport.setCalendar(cal);
	}

	// 保存设置
	private void saveConfig() {
		String pos = rbLeft.isChecked() ? "左手" : "右手";
		boolean enableSport = tbSport.isChecked();
		boolean enableSleep = tbSleep.isChecked();
		int sportRepeat = getRepeatConfig(spSleep);
		int sleepRepeat = getRepeatConfig(llSleep);
		int sleepHour = tpSleep.getHour();
		int sleepMin = tpSleep.getMinute();
		int sportHour = tpSport.getHour();
		int sportMin = tpSport.getMinute();

		SharedPreferences sp = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
		Editor e = sp.edit();
		e.putString(SharedPreferredKey.POSITION, pos);
		e.putBoolean(SharedPreferredKey.ENABLE_SPORT, enableSport);
		e.putBoolean(SharedPreferredKey.ENABLE_SLEEP, enableSleep);
		e.putInt(SharedPreferredKey.DAYS, sleepRepeat);
		e.putInt(SharedPreferredKey.DAYS2, sportRepeat);
		e.putInt(SharedPreferredKey.SPORT_HOUR, sportHour);
		e.putInt(SharedPreferredKey.SPORT_MINUTE, sportMin);
		e.putInt(SharedPreferredKey.SLEEP_HOUR, sleepHour);
		e.putInt(SharedPreferredKey.SLEEP_MINUTE, sleepMin);
		e.commit();
	}

	private int getRepeatConfig(LinearLayout container) {
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < container.getChildCount(); i++) {
			CheckBox cb = (CheckBox) container.getChildAt(i);
			buffer.append(cb.isChecked() ? "1" : "0");
		}
		buffer.append("1");
		return Integer.parseInt(buffer.toString(), 2);
	}

	private void loadRepeat(LinearLayout container, int config) {
		if (config != -1) {
			String code = Integer.toBinaryString(config);
			if (code.length() < 8) {
				int differ = 8 - code.length();
				for (int x = 0; x < differ; x++) {
					code = "0" + code;
				}
			}
			code = code.substring(0, code.length() - 1);
			char[] chars = code.toCharArray();
			for (int i = 0; i < 7; i++) {
				CheckBox cb = (CheckBox) container.getChildAt(i);
				if (chars[i] == '1') {
					cb.setChecked(true);
				} else {
					cb.setChecked(false);
				}
			}
		}
	}

	private void initViews() {
		// 返回按钮
		ImageButton backButton = (ImageButton) findViewById(R.id.button_input_bg_back);
		backButton.setBackgroundResource(R.drawable.my_button_back);
		backButton.setVisibility(View.VISIBLE);
		backButton.setOnClickListener(this);
		// 标题
		TextView textTitle = (TextView) findViewById(R.id.textView_title);
		textTitle.setText("手环设置");

		tpSport = (TimePicker) findViewById(R.id.dingdang_tp_sport);
		tpSleep = (TimePicker) findViewById(R.id.dingdang_tp_sleep);
		llSleep = (LinearLayout) findViewById(R.id.dingdang_ll_sleep);
		spSleep = (LinearLayout) findViewById(R.id.dingdang_ll_sport);
		tbSport = (ToggleButton) findViewById(R.id.dingdang_tb_sport);
		tbSleep = (ToggleButton) findViewById(R.id.dingdang_tb_sleep);

		tbSport.setOnCheckedChangeListener(this);
		tbSleep.setOnCheckedChangeListener(this);

		rbLeft = (RadioButton) findViewById(R.id.dingdang_radio_left);
		rbRight = (RadioButton) findViewById(R.id.dingdang_radio_right);

		findViewById(R.id.dingdang_btn_save).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.button_input_bg_back) {
			this.finish();
		} else if (id == R.id.dingdang_btn_save) {
			saveConfig();
			Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		if (id == R.id.dingdang_tb_sport) {
//			tpSport.setEnabled(isChecked);
			enableSport(isChecked);
		} else if (id == R.id.dingdang_tb_sleep) {
			enableSleep(isChecked);
		}
	}

	private void enableSport(boolean enable) {
		tpSport.setEnabled(enable);
		for (int i = 0; i < spSleep.getChildCount(); i++) {
			View v = spSleep.getChildAt(i);
			v.setEnabled(enable);
		}
	}

	private void enableSleep(boolean enable) {
		tpSleep.setEnabled(enable);
		for (int i = 0; i < llSleep.getChildCount(); i++) {
			View v = llSleep.getChildAt(i);
			v.setEnabled(enable);
		}
	}
}
