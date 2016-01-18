package kg.gtss.alarm;

import java.util.Calendar;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import kg.gtss.utils.TimeUtils;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

//
public class AddAlarm extends Activity implements OnClickListener {
	String COLUMN_ID = ReadingAlarmSQLiteOpenHelper.Columns._ID;
	String COLUMN_ON = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_ON;
	String COLUMN_VIBRATE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_VIBRATE;
	String COLUMN_COMMENT = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_COMMENT;
	String COLUMN_TIME = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_TIME;
	String COLUMN_MUTE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_MUTE;
	static String COLUMN_PENDING_ID = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_PENDING_INTENT;
	int DefaultValue = -1;
	int AlarmId = -1;
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
		setFields(Calendar.getInstance());
	}

	/**
	 * set all fields based on Calendar
	 * */
	void setFields(Calendar c) {
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
		mDisplayDate.setText(TimeUtils.getDateFromCalendar(c));
		mDisplayTime = (TextView) this.findViewById(R.id.display_time);
		mDisplayTime.setText(TimeUtils.getTimeFromCalendar(c));

		mSettingDate = (TextView) this.findViewById(R.id.setting_date);
		LinearLayout SettingDateLayout = (LinearLayout) this
				.findViewById(R.id.setting_date_layout);
		LinearLayout SettingTimeLayout = (LinearLayout) this
				.findViewById(R.id.setting_time_layout);
		SettingDateLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v(AddAlarm.this, "init DatePickerDialog " + year + " "
						+ month + " " + day);
				DatePickerDialog dpd = new DatePickerDialog(AddAlarm.this,
						Datelistener, year, month, day);
				dpd.setTitle(R.string.setting_date);
				dpd.show();
			}
		});
		mSettingTime = (TextView) this.findViewById(R.id.setting_time);
		SettingTimeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v(AddAlarm.this, "init TimePickerDialog " + hour + " "
						+ minute);
				TimePickerDialog tpd = new TimePickerDialog(AddAlarm.this,
						mOnTimeSetListener, hour, minute, true);
				tpd.setTitle(R.string.setting_time);
				tpd.show();
			}
		});

		// initialize fragment if needed
		if (null != getIntent() && null != getIntent().getExtras()) {

			AlarmId = (int) getIntent().getExtras().getInt(COLUMN_ID);
			String comment = this.getIntent().getExtras()
					.getString(COLUMN_COMMENT);
			long time = getIntent().getExtras().getLong(COLUMN_TIME);
			int on = getIntent().getExtras().getInt(COLUMN_ON);
			int vibrate = getIntent().getExtras().getInt(COLUMN_VIBRATE);
			int mute = getIntent().getExtras().getInt(COLUMN_MUTE);
			int pending_id = getIntent().getExtras().getInt(COLUMN_PENDING_ID);
			// reset new date and time
			Calendar cal = TimeUtils.getCalendarFromLongTime(time);
			String yearmonthday = TimeUtils.getDateFromCalendar(cal);
			String hourminte = TimeUtils.getTimeFromCalendar(cal);

			setFields(cal);
			setDisplayDate(yearmonthday);
			setDisplayTime(hourminte);
			mFragment.init(on, vibrate, mute, comment);
		}
	}

	void setDisplayDate(int y, int m, int d) {
		mDisplayDate.setText(y + "/" + (m + 1) + "/" + d);
	}

	void setDisplayDate(String date) {
		mDisplayDate.setText(date);
	}

	void setDisplayTime(int h, int m) {
		mDisplayTime.setText(h + ":" + m);
	}

	void setDisplayTime(String time) {
		mDisplayTime.setText(time);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (-1 == AlarmId) {
			initDateTime();
		}
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
		// 不允许保存已经过时的闹钟
		Calendar c = Calendar.getInstance();
		// 为了使刚刚设定的闹钟生效,此处加一分钟.因为我不知道现在多少秒了
		c.set(year, month, day, hour, minute /* + 1 */, 0);
		// 对于已经过时的闹钟,我们不允许再打开,但是允许在外部列表关闭或者去闹钟详情界面修改.
		if (c.getTimeInMillis() < System.currentTimeMillis()) {
			Toast.makeText(this, R.string.not_allowed_open, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		mFragment.save(AlarmId, year, month, day, hour, minute);
	}
}
