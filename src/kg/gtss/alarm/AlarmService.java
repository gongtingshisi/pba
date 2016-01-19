package kg.gtss.alarm;

import java.util.Calendar;

import kg.gtss.alarm.AddAlarmFragment.AlarmReceiver;
import kg.gtss.utils.Log;
import kg.gtss.utils.TimeUtils;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;

/**
 * to handle events:register alarms and listen pending alarms
 * */
public class AlarmService extends IntentService {
	static Uri URI = ReadingAlarmContentProvider.CONTENT_URI;
	static String COLUMN_ID = ReadingAlarmSQLiteOpenHelper.Columns._ID;
	static String COLUMN_ON = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_ON;
	static String COLUMN_VIBRATE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_VIBRATE;
	static String COLUMN_COMMENT = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_COMMENT;
	static String COLUMN_TIME = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_TIME;
	static String COLUMN_PENDING_ID = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_PENDING_INTENT;
	static String COLUMN_MUTE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_MUTE;
	static String TIME_STRING = "date_time";
	String SELECTION = COLUMN_ON + "=?";
	String[] SELECTIONARGS = new String[] { "1" };
	String ORDERBY = COLUMN_TIME + " asc";

	static String[] PROJECTION = { COLUMN_ID, COLUMN_ON, COLUMN_VIBRATE,
			COLUMN_COMMENT, COLUMN_TIME, COLUMN_MUTE, COLUMN_PENDING_ID };

	public AlarmService() {
		super("AlarmService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Log.v(this, "onHandleIntent ");
		Cursor c = this.getContentResolver().query(URI, PROJECTION, SELECTION,
				SELECTIONARGS, ORDERBY);
		if (null != c && c.getCount() > 0) {
			while (c.moveToNext()) {
				long time = c.getLong(c.getColumnIndex(COLUMN_TIME));
				Calendar cal = TimeUtils.getCalendarFromLongTime(time);

				int pending_id = c.getInt(c.getColumnIndex(COLUMN_PENDING_ID));
				String comment = c.getString(c.getColumnIndex(COLUMN_COMMENT));
				int vibrate = c.getInt(c.getColumnIndex(COLUMN_VIBRATE));
				int mute = c.getInt(c.getColumnIndex(COLUMN_MUTE));

				Intent i = new Intent(this, AlarmReceiver.class);
				i.putExtra(COLUMN_PENDING_ID, pending_id);
				i.putExtra(COLUMN_COMMENT, comment);
				i.putExtra(COLUMN_VIBRATE, vibrate == 1);
				i.putExtra(COLUMN_MUTE, mute == 1);
				i.putExtra(TIME_STRING, TimeUtils.getDateFromCalendar(cal)
						+ " " + TimeUtils.getTimeFromCalendar(cal));
				//
				PendingIntent alarm_pi = PendingIntent.getBroadcast(this,
						pending_id, i, 0);

				// Schedule the alarm!
				Log.v(this,
						c.getPosition()
								+ "	register alarm on alarm manager... "
								+ TimeUtils.getDateFromCalendar(cal) + " "
								+ TimeUtils.getTimeFromCalendar(cal));

				AlarmManager am = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarm_pi);
			}
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.v(this, "onBind");
		return super.onBind(intent);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.v(this, "onCreate");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.v(this, "onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.v(this, "onStart " + startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.v(this, "onStartCommand " + startId);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void setIntentRedelivery(boolean enabled) {
		// TODO Auto-generated method stub
		super.setIntentRedelivery(enabled);
		Log.v(this, "setIntentRedelivery " + enabled);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.v(this, "onUnbind");
		return super.onUnbind(intent);
	}

}
