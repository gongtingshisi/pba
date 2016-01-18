package kg.gtss.alarm;

import java.util.Calendar;
import kg.gtss.alarm.AddAlarmFragment.AlarmReceiver;
import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import kg.gtss.utils.TimeUtils;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ReadingAlarmAdapter extends CursorAdapter {

	Uri URI = ReadingAlarmContentProvider.CONTENT_URI;
	String COLUMN_ID = ReadingAlarmSQLiteOpenHelper.Columns._ID;
	String COLUMN_ON = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_ON;
	String COLUMN_VIBRATE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_VIBRATE;
	String COLUMN_COMMENT = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_COMMENT;
	String COLUMN_TIME = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_TIME;
	String COLUMN_MUTE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_MUTE;
	String COLUMN_PENDING_ID = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_PENDING_INTENT;
	String[] PROJECTION = { COLUMN_ID, COLUMN_ON, COLUMN_VIBRATE,
			COLUMN_COMMENT, COLUMN_TIME, COLUMN_MUTE, COLUMN_PENDING_ID };
	static String TIME_STRING = "date_time";
	final Context mContext;

	@SuppressWarnings("deprecation")
	public ReadingAlarmAdapter(Activity context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
		mContext = context;

	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		// TODO Auto-generated method stub

		if (c == null)
			return;
		final int pos = c.getPosition();
		if (pos >= c.getCount())
			return;

		final ContentResolver cr = mContext.getContentResolver();
		final AlarmManager am = (AlarmManager) mContext
				.getSystemService(Service.ALARM_SERVICE);
		// 如果将Cursor复制给另一个Cursor,会存在游标移动到最后的情况,所以不要这么做
		final int id = c.getInt(c.getColumnIndex(COLUMN_ID));
		final int on = c.getInt(c.getColumnIndex(COLUMN_ON));
		final int mute = c.getInt(c.getColumnIndex(COLUMN_MUTE));
		final int vibrate = c.getInt(c.getColumnIndex(COLUMN_VIBRATE));
		final String comment = c.getString(c.getColumnIndex(COLUMN_COMMENT));
		final long time = c.getLong(c.getColumnIndex(COLUMN_TIME));
		final int pending_id = c.getInt(c.getColumnIndex(COLUMN_PENDING_ID));

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent i = new Intent();
				i.putExtra(COLUMN_ID, id);
				i.putExtra(COLUMN_ON, on);
				i.putExtra(COLUMN_MUTE, mute);
				i.putExtra(COLUMN_VIBRATE, vibrate);
				i.putExtra(COLUMN_COMMENT, comment);
				i.putExtra(COLUMN_TIME, time);
				i.putExtra(COLUMN_PENDING_ID, pending_id);
				i.setClass(mContext, AddAlarm.class);
				mContext.startActivity(i);
			}
		});
		v.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v(this, "onLongClick");
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setMessage(R.string.sure_delete)
						.setTitle(R.string.sure_delete)
						.setNegativeButton(R.string.no_button,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										// TODO Auto-generated method stub

									}
								})
						.setPositiveButton(R.string.ok_button,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										removeAlarm(cr, am, id, on, pending_id);
									}
								}).show();
				return true;
			}
		});

		Calendar cal = TimeUtils.getCalendarFromLongTime(c.getLong(c
				.getColumnIndex(COLUMN_TIME)));

		ViewHolder holder = new ViewHolder();
		holder.time = (TextView) v.findViewById(R.id.alarm_time);
		holder.time.setText(TimeUtils.getTimeFromCalendar(cal));
		holder.days = (TextView) v.findViewById(R.id.alarm_days);
		holder.days.setText(TimeUtils.getDateFromCalendar(cal));
		holder.content = (TextView) v.findViewById(R.id.alarm_content);
		holder.content.setText(c.getString(c.getColumnIndex(COLUMN_COMMENT)));
		holder.checkbox = (CheckBox) v
				.findViewById(R.id.reading_alarm_checkbox);

		holder.onoff = (ImageView) v.findViewById(R.id.alarm_switch);
		if (1 == c.getInt(c.getColumnIndex(COLUMN_ON))) {
			holder.onoff.setImageResource(R.drawable.alarm_on);
		} else {
			holder.onoff.setImageResource(R.drawable.alarm_off);
		}
		holder.onoff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 对于已经过时的闹钟,我们不允许再打开,但是允许在外部列表关闭或者去闹钟详情界面修改.
				if (0 == on && time < System.currentTimeMillis()) {
					Toast.makeText(mContext, R.string.not_allowed_open,
							Toast.LENGTH_SHORT).show();
					return;
				}

				removeAlarm(cr, am, id, on, pending_id);

				// ////////////////////
				Log.v(this,
						"#############################saving starts##############################");
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(time);

				Intent i = new Intent(mContext, AlarmReceiver.class);
				// unique.以此次ContentValue用来标识此次PendingIntent唯一的,要不然会出现这次的PendingIntent还是很久之前的第一个
				int broadcast_pending_hashcode = i.hashCode();

				if (0 == on) {
					// /////////////////////////////
					// make a alarm manager
					// ////////////////////////////

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
					i.putExtra(TIME_STRING, TimeUtils.getDateFromCalendar(cal)
							+ " " + TimeUtils.getTimeFromCalendar(cal));
					//
					PendingIntent alarm_pi = PendingIntent.getBroadcast(
							mContext, broadcast_pending_hashcode, i, 0);

					// Schedule the alarm!
					Log.v(this,
							"register alarm on alarm manager...broadcast_pending_hashcode:"
									+ broadcast_pending_hashcode);
					am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
							alarm_pi);
				}

				ContentValues value = new ContentValues();
				value.put(COLUMN_ON, on == 1 ? 0 : 1);
				value.put(COLUMN_VIBRATE, vibrate);
				value.put(COLUMN_COMMENT, comment);
				value.put(COLUMN_MUTE, mute);
				value.put(COLUMN_TIME, cal.getTimeInMillis());
				value.put(COLUMN_PENDING_ID, broadcast_pending_hashcode);
				Uri insertResult = cr.insert(URI, value);
				Log.v(this, "inserted into database..." + insertResult);
				Log.v(this,
						"#############################saving ends##############################");
			}
		});
		holder.mute = (ImageView) v.findViewById(R.id.alarm_mute);
		if (1 == mute)
			holder.mute.setImageResource(R.drawable.alarm_mute);
		holder.vibrate = (ImageView) v.findViewById(R.id.alarm_vibrate);
		if (1 == vibrate)
			holder.vibrate.setImageResource(R.drawable.alarm_vibrate);
		v.setTag(holder);

	}

	/**
	 * delete alarm based on id if existed one
	 * */
	void removeAlarm(ContentResolver cr, AlarmManager am, int id, int on,
			int pending_id) {
		Log.v(ReadingAlarmAdapter.this,
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
			Log.v(ReadingAlarmAdapter.this, "delete from db, result "
					+ deleteCount + "...");

			// cancel from alarm manager
			if (1 == on) {
				Log.v(ReadingAlarmAdapter.this, "registered pending intent id:"
						+ pending_id + "...");
				PendingIntent operation = PendingIntent.getBroadcast(mContext,
						pending_id, new Intent(mContext, AlarmReceiver.class),
						0);
				Log.v(ReadingAlarmAdapter.this,
						"cancel alarm from alarm manager...");
				am.cancel(operation);
			}
		}

		Log.v(ReadingAlarmAdapter.this,
				"#############################deleting ends##############################");

	}

	@Override
	public View newView(Context arg0, Cursor c, ViewGroup arg2) {
		// TODO Auto-generated method stub

		return LayoutInflater.from(mContext).inflate(
				R.layout.add_reading_alarm_item, null);
	}

	class ViewHolder {
		TextView time;
		TextView days;
		TextView content;
		ImageView onoff;
		ImageView mute;
		ImageView vibrate;
		CheckBox checkbox;
	}

}
