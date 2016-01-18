package kg.gtss.alarm;

import java.util.Calendar;

import com.google.zxing.common.StringUtils;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import kg.gtss.utils.StringUtil;
import kg.gtss.utils.TimeUtils;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.widget.Toast;

public class AddAlarmFragment extends PreferenceFragment implements
		OnPreferenceChangeListener {

	static Uri URI = ReadingAlarmContentProvider.CONTENT_URI;
	static String COLUMN_ID = ReadingAlarmSQLiteOpenHelper.Columns._ID;
	static String COLUMN_ON = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_ON;
	static String COLUMN_VIBRATE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_VIBRATE;
	static String COLUMN_COMMENT = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_COMMENT;
	static String COLUMN_TIME = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_TIME;
	static String COLUMN_PENDING_ID = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_PENDING_INTENT;
	static String COLUMN_MUTE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_MUTE;
	static String TIME_STRING = "date_time";

	static String[] PROJECTION = { COLUMN_ID, COLUMN_ON, COLUMN_VIBRATE,
			COLUMN_COMMENT, COLUMN_TIME, COLUMN_MUTE, COLUMN_PENDING_ID };

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
	final String ID = "_id";

	int AlarmId = -1;

	static int NotificationId = 0;

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

		mSwitchPreference = (SwitchPreference) this.findPreference(ON);
		mMuteSwitchPreference = (SwitchPreference) this.findPreference(MUTE);

		mVibrateSwitchPreference = (SwitchPreference) this
				.findPreference(VIBRATE);
		mEditTextPreference = (EditTextPreference) this.findPreference(COMMENT);
		mEditTextPreference.setOnPreferenceChangeListener(this);
	}

	/**
	 * delete alarm based on id from database and alarm manager if existed one
	 * */
	void deleteAlarm(ContentResolver cr, AlarmManager am, int id, int on,
			int pending_id) {
		Log.v(this,
				"#############################deleting starts##############################");

		// delete alarm from database and alarm
		// manager

		// firstly,delete alarm from
		// AlarmManager and Database if
		// existed.
		String selection = COLUMN_ID + "=?";
		String[] selectionArgs = new String[] { id + "" };
		Cursor cursor = cr.query(URI, PROJECTION, selection, selectionArgs,
				null);

		if (null != cursor && cursor.getCount() > 0) {
			// get pending intent id
			cursor.moveToFirst();

			// delete it from database
			int deleteCount = cr.delete(URI, selection, selectionArgs);
			Log.v(this, "delete from db, result " + deleteCount + "...");

			// cancel from alarm manager
			if (1 == on) {
				Log.v(this, "registered pending intent id:" + pending_id
						+ "...");
				PendingIntent operation = PendingIntent.getBroadcast(
						getActivity(), pending_id, new Intent(getActivity(),
								AlarmReceiver.class), 0);
				Log.v(this, "cancel alarm from alarm manager...");
				am.cancel(operation);
			}
		}

		Log.v(this,
				"#############################deleting ends##############################");

	}

	void save(int id, int year, int month, int day, int hour, int minute) {
		AlarmId = id;
		boolean on = mSwitchPreference.isChecked();
		boolean vibrate = mVibrateSwitchPreference.isChecked();
		String comment = mEditTextPreference.getEditText().getText().toString();
		if (StringUtil.isEmpty(comment)) {
			Toast.makeText(getActivity(), R.string.not_allowed_null,
					Toast.LENGTH_SHORT).show();
			return;
		}
		boolean mute = mMuteSwitchPreference.isChecked();
		Intent i = new Intent(getActivity(), AlarmReceiver.class);
		// unique.以此次ContentValue用来标识此次PendingIntent唯一的,要不然会出现这次的PendingIntent还是很久之前的第一个
		int broadcast_pending_hashcode = i.hashCode();

		ContentResolver cr = getActivity().getContentResolver();
		AlarmManager am = (AlarmManager) getActivity().getSystemService(
				Service.ALARM_SERVICE);

		if (-1 != id) {
			deleteAlarm(cr, am, id, on ? 1 : 0, broadcast_pending_hashcode);
		}

		// ////////////////////
		Log.v(this,
				"#############################saving starts##############################");
		// re-register

		Log.v(this, "save Year:" + year + " Month:" + month + " Day:" + day
				+ " Hour:" + hour + " Minute:" + minute + " on:" + on
				+ " vibrate:" + vibrate + " comment:" + comment + "	mute:"
				+ mute + "		alarmId: " + AlarmId);
		// /////////////////////
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);// on time

		if (on) {
			// /////////////////////////////
			// make a alarm manager
			// ////////////////////////////

			// some seconds after
			long after = 0, now = System.currentTimeMillis();

			if (cal.getTimeInMillis() > now) {
				after = (cal.getTimeInMillis() - now) / 1000;
				Log.v(this, "set alarm after " + after + " seconds.");
			} else {
				cal.setTimeInMillis(now);// ring at once
				Log.v(this, "alarm rings at once !");
			}

			// jump to alarm receiver

			i.putExtra(COLUMN_PENDING_ID, broadcast_pending_hashcode);
			i.putExtra(COLUMN_COMMENT, comment);
			i.putExtra(COLUMN_VIBRATE, vibrate);
			i.putExtra(COLUMN_MUTE, mute);
			i.putExtra(TIME_STRING, TimeUtils.getDateFromCalendar(cal) + " "
					+ TimeUtils.getTimeFromCalendar(cal));
			//
			PendingIntent alarm_pi = PendingIntent.getBroadcast(getActivity(),
					broadcast_pending_hashcode, i, 0);

			// Schedule the alarm!
			Log.v(this,
					"register alarm on alarm manager...broadcast_pending_hashcode:"
							+ broadcast_pending_hashcode);
			am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarm_pi);
		}

		ContentValues value = new ContentValues();
		value.put(COLUMN_ON, on ? 1 : 0);
		value.put(COLUMN_VIBRATE, vibrate ? 1 : 0);
		value.put(COLUMN_COMMENT, comment);
		value.put(COLUMN_MUTE, mute ? 1 : 0);
		value.put(COLUMN_TIME, cal.getTimeInMillis());
		value.put(COLUMN_PENDING_ID, broadcast_pending_hashcode);
		Uri insertResult = cr.insert(URI, value);
		Log.v(this, "inserted into database..." + insertResult);
		Log.v(this,
				"#############################saving ends##############################");
	}

	@Override
	public boolean onPreferenceChange(Preference pf, Object arg1) {
		// TODO Auto-generated method stub

		if (ON.equals(pf.getKey())) {
			// 不允许保存已经过时的闹钟

		} else if (COMMENT.equals(pf.getKey())) {
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
			boolean vibrate = intent.getExtras().getBoolean(COLUMN_VIBRATE);
			boolean mute = intent.getExtras().getBoolean(COLUMN_MUTE);
			String time = intent.getStringExtra(TIME_STRING);
			int pending_id = intent.getExtras().getInt(COLUMN_PENDING_ID);

			Log.v(this, "comment:" + comment + ",time:" + time + ",pending_id:"
					+ pending_id);

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
			if (!mute) {
				n.defaults |= Notification.DEFAULT_SOUND;
			}
			if (vibrate) {
				n.defaults |= Notification.DEFAULT_VIBRATE;
			}

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

			// disable alarm state from database

			String where = COLUMN_PENDING_ID + "=?";
			String[] selectionArgs = new String[] { pending_id + "" };
			ContentValues value = new ContentValues();
			value.put(COLUMN_ON, 0);
			int updateCount = context.getContentResolver().update(URI, value,
					where, selectionArgs);
			Log.v(this, "update database result:" + updateCount
					+ "  pending_id:" + pending_id);
		}
	}
}
