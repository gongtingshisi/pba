package kg.gtss.alarm;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import kg.gtss.utils.TimeUtils;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class AddAlarmFragment extends PreferenceFragment implements
		OnPreferenceChangeListener {

	static Uri URI = ReadingAlarmContentProvider.CONTENT_URI;
	String COLUMN_ID = ReadingAlarmSQLiteOpenHelper.Columns._ID;
	String COLUMN_ON = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_ON;
	String COLUMN_VIBRATE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_VIBRATE;
	static String COLUMN_COMMENT = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_COMMENT;
	String COLUMN_TIME = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_TIME;
	String COLUMN_MUTE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_MUTE;
	static String TIME_STRING = "date_time";
	static String HASH_CODE = "hashcode";
	String[] PROJECTION = { COLUMN_ID, COLUMN_ON, COLUMN_VIBRATE,
			COLUMN_COMMENT, COLUMN_TIME, COLUMN_MUTE };

	SwitchPreference mSwitchPreference;
	SwitchPreference mVibrateSwitchPreference;
	SwitchPreference mMuteSwitchPreference;
	EditTextPreference mEditTextPreference;
	// PreferenceScreen mDateSetting, mTimeSetting;

	// final String DATE = "setting_date";
	// final String TIME = "setting_time";
	final String ON = "switch";
	final String VIBRATE = "vibrate";
	final String COMMENT = "comment";
	final String MUTE = "mute";

	static int NotificationId = 0;
	private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker dp, int y, int m, int d) {
			// TODO Auto-generated method stub
			Log.v(this, "onDateSet:" + y + "/" + m + "/" + d);
		}
	};
	private OnTimeSetListener mOnTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker tp, int h, int m) {
			// TODO Auto-generated method stub
			Log.v(this, "onTimeSet:" + h + ":" + m);
		}
	};

	/**
	 * initialize our preferences
	 * */
	public void init(int on, int vibrate, int mute, String comment) {
		mSwitchPreference.setChecked(1 == on);
		mVibrateSwitchPreference.setChecked(1 == vibrate);
		mMuteSwitchPreference.setChecked(1 == mute);
		mEditTextPreference.setSummary(comment);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.addPreferencesFromResource(R.xml.add_alarm);

		final Calendar c = Calendar.getInstance();
		mSwitchPreference = (SwitchPreference) this.findPreference(ON);
		mMuteSwitchPreference = (SwitchPreference) this.findPreference(MUTE);
		/*
		 * mDateSetting = (PreferenceScreen) this.findPreference(DATE);
		 * mDateSetting .setOnPreferenceClickListener(new
		 * OnPreferenceClickListener() {
		 * 
		 * @Override public boolean onPreferenceClick(Preference arg0) { // TODO
		 * Auto-generated method stub DatePickerDialog dpd = new
		 * DatePickerDialog( AddAlarmFragment.this.getActivity(), Datelistener,
		 * c.get(Calendar.YEAR), c .get(Calendar.MONTH), c
		 * .get(Calendar.DAY_OF_MONTH)); dpd.show(); return false; } });
		 * 
		 * mTimeSetting = (PreferenceScreen) this.findPreference(TIME);
		 * mTimeSetting .setOnPreferenceClickListener(new
		 * OnPreferenceClickListener() {
		 * 
		 * @Override public boolean onPreferenceClick(Preference arg0) { // TODO
		 * Auto-generated method stub TimePickerDialog tpd = new
		 * TimePickerDialog( AddAlarmFragment.this.getActivity(),
		 * mOnTimeSetListener, c.get(Calendar.HOUR_OF_DAY), c
		 * .get(Calendar.MINUTE), true); tpd.show(); return false; } });
		 */
		mVibrateSwitchPreference = (SwitchPreference) this
				.findPreference(VIBRATE);
		mEditTextPreference = (EditTextPreference) this.findPreference(COMMENT);
		mEditTextPreference.setOnPreferenceChangeListener(this);
	}

	void save(int year, int month, int day, int hour, int minute) {
		boolean on = mSwitchPreference.isChecked();
		boolean vibrate = mVibrateSwitchPreference.isChecked();
		String comment = mEditTextPreference.getEditText().getText().toString();
		boolean mute = mMuteSwitchPreference.isChecked();
		Log.v(this, "save Year:" + year + " Month:" + month + " Day:" + day
				+ " Hour:" + hour + " Minute:" + minute + " on:" + on
				+ " vibrate:" + vibrate + " comment:" + comment + "	mute:"
				+ mute);
		// /////////////////////
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);

		ContentValues value = new ContentValues();
		value.put(COLUMN_ON, on);
		value.put(COLUMN_VIBRATE, vibrate);
		value.put(COLUMN_COMMENT, comment);
		value.put(COLUMN_MUTE, mute);
		value.put(COLUMN_TIME, cal.getTimeInMillis());
		this.getActivity().getContentResolver().insert(URI, value);

		// /////////////////////////////
		// make a alarm manager
		// ////////////////////////////
		// We want the alarm to go off 10 seconds from now.
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(System.currentTimeMillis());

		// some seconds after
		int time = 0;
		if (cal.getTimeInMillis() > now.getTimeInMillis())
			time = (int) (cal.getTimeInMillis() - now.getTimeInMillis()) / 1000;

		now.add(Calendar.SECOND, time);

		// jump to alarm receiver
		Intent i = new Intent(getActivity(), AlarmReceiver.class);
		// unique.以此次ContentValue用来标识此次PendingIntent唯一的,要不然会出现这次的PendingIntent还是很久之前的第一个
		int hashcode = i.hashCode();
		i.putExtra(HASH_CODE, hashcode);
		i.putExtra(COLUMN_COMMENT, comment);
		i.putExtra(TIME_STRING, TimeUtils.getDateFromCalendar(cal) + " "
				+ TimeUtils.getTimeFromCalendar(cal));
		//
		PendingIntent alarm_pi = PendingIntent.getBroadcast(getActivity(),
				hashcode, i, 0);

		// Schedule the alarm!
		AlarmManager am = (AlarmManager) getActivity().getSystemService(
				Service.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, now.getTimeInMillis(), alarm_pi);
	}

	@Override
	public boolean onPreferenceChange(Preference pf, Object arg1) {
		// TODO Auto-generated method stub
		Log.v(this, "onPreferenceChange " + pf.getKey() + "---"
				+ mEditTextPreference.getEditText().getText().toString());
		if (COMMENT.equals(pf.getKey())) {
			mEditTextPreference.setSummary(mEditTextPreference.getEditText()
					.getText().toString());
		}
		return false;
	}

	/*
	 * MUST be public static here,or
	 * "Caused by: java.lang.InstantiationException: can't instantiate class kg.gtss.alarm.AddAlarmFragment$AlarmReceiver; no empty constructor"
	 */
	public static class AlarmReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String comment = intent.getStringExtra(COLUMN_COMMENT);
			String time = intent.getStringExtra(TIME_STRING);
			Log.v(this, "intent:" + intent.getAction() + " " + comment + "	"
					+ time);

			// String where = "id=?";
			// String[] selectionArgs = new String[] { };
			// context.getContentResolver().delete(URI, where, selectionArgs);

			// ///////////////////////////////
			// make a notification
			// ///////////////////////////////
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Service.NOTIFICATION_SERVICE);

			Notification n = new Notification();
			n.icon = R.drawable.alarm;
			n.when = System.currentTimeMillis();
			n.tickerText = context.getString(
					R.string.reading_alarm_notice_ticker, 1);
			n.flags |= Notification.FLAG_AUTO_CANCEL;
			n.defaults = Notification.DEFAULT_ALL;

			// jump to alarm list screen
			PendingIntent pi = PendingIntent.getActivity(context, 0,
					new Intent(context, AddReadingAlarm.class), 0);

			nm.cancelAll();
			n.setLatestEventInfo(
					context,
					context.getApplicationContext().getString(
							R.string.reading_alarm_notice_title),
					context.getApplicationContext().getString(
							R.string.reading_alarm_notice_content, comment,
							time), pi);

			nm.notify(NotificationId, n);
		}
	}
}
