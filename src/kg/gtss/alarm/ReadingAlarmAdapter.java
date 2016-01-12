package kg.gtss.alarm;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.gtss.douban.DouBanContentProvider;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.personalbooksassitant.R.id;
import kg.gtss.personalbooksassitant.R.layout;
import kg.gtss.utils.Log;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReadingAlarmAdapter extends CursorAdapter {

	Uri URI = ReadingAlarmContentProvider.CONTENT_URI;
	String COLUMN_ID = ReadingAlarmSQLiteOpenHelper.Columns._ID;
	String COLUMN_ON = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_ON;
	String COLUMN_VIBRATE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_VIBRATE;
	String COLUMN_COMMENT = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_COMMENT;
	String COLUMN_TIME = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_TIME;
	String COLUMN_MUTE = ReadingAlarmSQLiteOpenHelper.Columns.READING_ALARM_MUTE;
	String[] PROJECTION = { COLUMN_ID, COLUMN_ON, COLUMN_VIBRATE,
			COLUMN_COMMENT, COLUMN_TIME, COLUMN_MUTE };

	Context mContext;

	public ReadingAlarmAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
		mContext = context;

	}

	Calendar getCalendarFromLongTime(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		return c;
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		// TODO Auto-generated method stub
		final Cursor cur = c;
		if (c == null)
			return;
		if (c.getPosition() >= c.getCount())
			return;

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v(ReadingAlarmAdapter.this, "onclick " + cur.getPosition());
				Intent i = new Intent();

				i.putExtra(COLUMN_ID, cur.getInt(cur.getColumnIndex(COLUMN_ID))
						+ "");
				i.putExtra(COLUMN_ON, cur.getInt(cur.getColumnIndex(COLUMN_ON))
						+ "");
				i.putExtra(COLUMN_MUTE,
						cur.getInt(cur.getColumnIndex(COLUMN_MUTE)) + "");
				i.putExtra(COLUMN_VIBRATE,
						cur.getInt(cur.getColumnIndex(COLUMN_VIBRATE)) + "");
				i.putExtra(COLUMN_COMMENT,
						cur.getInt(cur.getColumnIndex(COLUMN_COMMENT)) + "");
				i.putExtra(COLUMN_TIME,
						cur.getInt(cur.getColumnIndex(COLUMN_TIME)) + "");

				i.setClass(mContext, AddAlarm.class);
				mContext.startActivity(i);
			}
		});
		v.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v(ReadingAlarmAdapter.this,
						"onLongClick " + cur.getPosition());
				return true;
			}
		});

		Calendar cal = getCalendarFromLongTime(c.getLong(c
				.getColumnIndex(COLUMN_TIME)));

		ViewHolder holder = new ViewHolder();
		holder.time = (TextView) v.findViewById(R.id.alarm_time);
		holder.time.setText(cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE));
		holder.days = (TextView) v.findViewById(R.id.alarm_days);
		holder.days.setText(cal.get(Calendar.YEAR) + "/"
				+ (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.DAY_OF_MONTH));
		holder.content = (TextView) v.findViewById(R.id.alarm_content);
		holder.content.setText(c.getString(c.getColumnIndex(COLUMN_COMMENT)));
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

			}
		});
		v.setTag(holder);

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
	}

}
