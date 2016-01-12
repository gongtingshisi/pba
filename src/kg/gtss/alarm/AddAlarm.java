package kg.gtss.alarm;

import java.util.Calendar;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.app.FragmentManager;
import android.app.TimePickerDialog.OnTimeSetListener;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

//
public class AddAlarm extends Activity implements OnClickListener {
	AddAlarmFragment mFragment;
	TextView mSettingDate, mSettingTime, mDisplayDate, mDisplayTime;
	int year, month, day, hour, minute;
	private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker dp, int y, int m, int d) {
			// TODO Auto-generated method stub
			Log.v(this, "onDateSet:" + y + "/" + m + "/" + d);
			year = y;
			month = m;
			day = d;
			setDisplayDate(y, m, d);
		}
	};
	private OnTimeSetListener mOnTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker tp, int h, int m) {
			// TODO Auto-generated method stub
			Log.v(this, "onTimeSet:" + h + ":" + m);
			hour = h;
			minute = m;
			setDisplayTime(h, m);
		}
	};

	void initDateTime() {
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		/*
		 * this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
		FragmentManager fm = this.getFragmentManager();
		this.setContentView(R.layout.add_reading_alarm_activity);
		final Calendar c = Calendar.getInstance();
		mFragment = (AddAlarmFragment) fm
				.findFragmentById(R.id.add_reading_alarm_fragment);
		fm.beginTransaction()
				.replace(R.id.add_reading_alarm_fragment, mFragment).commit();
		Button ok = (Button) this.findViewById(R.id.add_reading_alarm_ok_btn);

		ok.setOnClickListener(this);
		Button cancel = (Button) this
				.findViewById(R.id.add_reading_alarm_cancel_btn);
		cancel.setOnClickListener(this);

		mDisplayDate = (TextView) this.findViewById(R.id.display_date);
		mDisplayDate.setText(getDate(c));
		mDisplayTime = (TextView) this.findViewById(R.id.display_time);
		mDisplayTime.setText(getTime(c));

		mSettingDate = (TextView) this.findViewById(R.id.setting_date);
		mSettingDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DatePickerDialog dpd = new DatePickerDialog(AddAlarm.this,
						Datelistener, c.get(Calendar.YEAR), c
								.get(Calendar.MONTH), c
								.get(Calendar.DAY_OF_MONTH));
				dpd.setTitle(R.string.setting_date);
				dpd.show();
			}
		});
		mSettingTime = (TextView) this.findViewById(R.id.setting_time);
		mSettingTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TimePickerDialog tpd = new TimePickerDialog(AddAlarm.this,
						mOnTimeSetListener, c.get(Calendar.HOUR_OF_DAY), c
								.get(Calendar.MINUTE), true);
				tpd.setTitle(R.string.setting_time);
				tpd.show();
			}
		});
	}

	String getDate(Calendar c) {
		return c.get(Calendar.YEAR) + "/" + (c.get(Calendar.MONTH) + 1) + "/"
				+ c.get(Calendar.DAY_OF_MONTH);
	}

	void setDisplayDate(int y, int m, int d) {
		mDisplayDate.setText(y + "/" + (m + 1) + "/" + d);
	}

	void setDisplayTime(int h, int m) {
		mDisplayTime.setText(h + ":" + m);
	}

	String getTime(Calendar c) {
		return c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initDateTime();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_reading_alarm_ok_btn:
			save();
			break;
		case R.id.add_reading_alarm_cancel_btn:
			break;
		}
		finish();
	}

	void save() {
		mFragment.save(year, month, day, hour, minute);
	}
}
