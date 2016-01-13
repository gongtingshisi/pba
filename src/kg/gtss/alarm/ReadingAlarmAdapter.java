package kg.gtss.alarm;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.gtss.douban.DouBanContentProvider;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.personalbooksassitant.R.id;
import kg.gtss.personalbooksassitant.R.layout;
import kg.gtss.utils.Log;
import kg.gtss.utils.TimeUtils;
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

	@Override
	public void bindView(View v, Context context, Cursor c) {
		// TODO Auto-generated method stub

		if (c == null)
			return;
		final int pos = c.getPosition();
		if (pos >= c.getCount())
			return;
		// 如果将Cursor复制给另一个Cursor,会存在游标移动到最后的情况,所以不要这么做
		final int id = c.getInt(c.getColumnIndex(COLUMN_ID));
		final int on = c.getInt(c.getColumnIndex(COLUMN_ON));
		final int mute = c.getInt(c.getColumnIndex(COLUMN_MUTE));
		final int vibrate = c.getInt(c.getColumnIndex(COLUMN_VIBRATE));
		final String comment = c.getString(c.getColumnIndex(COLUMN_COMMENT));
		final long time = c.getLong(c.getColumnIndex(COLUMN_TIME));

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

				i.setClass(mContext, AddAlarm.class);
				mContext.startActivity(i);
			}
		});
		v.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub

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
		ImageView mute;
		ImageView vibrate;
	}

}
