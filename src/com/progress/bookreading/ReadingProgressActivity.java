package com.progress.bookreading;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.gtss.douban.DatabaseBookInterface;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Common;
import kg.gtss.utils.Log;
import kg.gtss.utils.TimeUtils;
import android.app.Activity;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * main entrypoint activity of reading. 1.sorted by a book; 2.sorted by date
 * */
public class ReadingProgressActivity extends Activity implements
		OnClickListener, LoaderCallbacks<Cursor>, OnTouchListener {

	GestureDetector mGestureDetector;

	Uri URI = ReadingRecordProvider.CONTENT_URI;
	String[] PROJECT = ReadingRecordDatabase.PROJ;
	ReadingProgressGridView mReadingProgressGridView;
	int LOADER_ID = Common.LOADER_ID_ReadingProgressActivity;
	int MONTH_PER_YEAR = 12;
	/**
	 * 当前选择的年月
	 * */
	int mYear, mMonth, mMaxDayOfCurrentMonth;

	String[] test = { "test" };

	/**
	 * 按照书名整理之后的vector
	 * */
	Vector<ReadHistory> m2DimensionVector = new Vector<ReadHistory>();

	/**
	 * 用来封装一本书的完整阅读历史
	 * */
	class ReadHistory {
		Vector<ReadingRecord> history;
		int color;// random color

		public ReadHistory(Vector<ReadingRecord> h, int c) {
			this.history = h;
			this.color = c;
		}

		public ReadHistory(ReadingRecord r, int c) {
			this.history.add(r);
			this.color = c;
		}

		boolean contains(ReadingRecord r) {
			return history.contains(r);
		}

		void add(ReadingRecord r) {
			this.history.add(r);
		}
	}

	// HorizontalListView mHorizontalListView;
	// DateDisplayAdapter mDateDisplayAdapter;
	ImageView mAddBtn, mSettingsBtn;
	ContentObserver mContentObserver = new ContentObserver(new Handler()) {

		@Override
		public void onChange(boolean selfChange, Uri uri) {
			// TODO Auto-generated method stub
			Log.v(this, uri + " changes.. " + selfChange + ", restartLoader "
					+ LOADER_ID);
			// reloading data,then refresh ui
			ReadingProgressActivity.this.getLoaderManager().restartLoader(
					LOADER_ID, null, ReadingProgressActivity.this);
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			Log.v(this, URI + " changes. " + selfChange);
		}
	};

	void initTime() {
		// initiate month and year
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mMaxDayOfCurrentMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		Log.v(this, "Current Date:" + mYear + "/" + (mMonth + 1) + ",max days:"
				+ mMaxDayOfCurrentMonth);
	}

	/**
	 * 根据当前年月递增得到月份
	 * */
	void increaseMonth() {
		if (++mMonth > MONTH_PER_YEAR - 1) {
			mMonth %= MONTH_PER_YEAR;// =0
			mYear++;
		}
		Log.v(this, "increase to " + mYear + "/" + (mMonth + 1) + ",max days:"
				+ getDaysOfCurrentMonth());

		mReadingProgressGridView.setViewDate(mYear, mMonth);
		// reloading data,then refresh ui
		getLoaderManager().restartLoader(LOADER_ID, null,
				ReadingProgressActivity.this);
	}

	/**
	 * 根据当前年月递减得到月份
	 * */
	void decreaseMonth() {
		if (--mMonth < 0) {
			mMonth = MONTH_PER_YEAR - 1;
			mYear--;
		}
		Log.v(this, "decrease to " + mYear + "/" + (mMonth + 1) + ",max days:"
				+ getDaysOfCurrentMonth());

		mReadingProgressGridView.setViewDate(mYear, mMonth);
		// reloading data,then refresh ui
		getLoaderManager().restartLoader(LOADER_ID, null,
				ReadingProgressActivity.this);
	}

	/**
	 * 根据当前所处位置(也就是手指滑动到的年月)确定当前月,得到这个月天数
	 * 
	 * @throws ParseException
	 * */
	int getDaysOfCurrentMonth() {

		Calendar c = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

		try {
			c.setTime(sdf.parse(mYear + "-" + (mMonth + 1)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);

	}

	void initGestureEvent() {
		mReadingProgressGridView.setOnTouchListener(this);
		mGestureDetector = new GestureDetector(this,
				new android.view.GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onDoubleTap(MotionEvent e) {
						// TODO Auto-generated method stub

						mReadingProgressGridView.setScale();
						return super.onDoubleTap(e);

					}

					@Override
					public boolean onDoubleTapEvent(MotionEvent e) {
						// TODO Auto-generated method stub

						return super.onDoubleTapEvent(e);
					}

					@Override
					public boolean onDown(MotionEvent e) {
						// TODO Auto-generated method stub
						return true;// !!
					}

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float vx, float vy) {
						// TODO Auto-generated method stub

						Log.v(this, "onFling (" + e1.getX() + "," + e1.getY()
								+ "),(" + e2.getX() + "," + e2.getY()
								+ ")		vx:" + vx + ",vy:" + vy);

						if (moveLeft(vx)) {
							// increase month and year
							increaseMonth();

						} else if (moveRight(vx)) {
							// decrease month and year
							decreaseMonth();

						}
						return true;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						// TODO Auto-generated method stub
						super.onLongPress(e);
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						// TODO Auto-generated method stub
						return super.onScroll(e1, e2, distanceX, distanceY);
					}

					@Override
					public void onShowPress(MotionEvent e) {
						// TODO Auto-generated method stub
						super.onShowPress(e);
					}

					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						// TODO Auto-generated method stub
						return super.onSingleTapConfirmed(e);
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						// TODO Auto-generated method stub
						return super.onSingleTapUp(e);
					}

				});

	}

	boolean moveLeft(float vx) {

		return vx < 0;
	}

	boolean moveRight(float vx) {

		return vx > 0;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// //////////////////////////////////////////////////////////
		this.setContentView(R.layout.reading_progress_single);
		this.setTitle("阅读记录");
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		mAddBtn = (ImageView) this.findViewById(R.id.add);
		mAddBtn.setOnClickListener(this);
		mReadingProgressGridView = (ReadingProgressGridView) this
				.findViewById(R.id.readingProgressGridView);
		mSettingsBtn = (ImageView) this.findViewById(R.id.settings);
		mSettingsBtn.setOnClickListener(this);
		this.getContentResolver().registerContentObserver(URI, true,
				mContentObserver);
		// init cursor loader
		this.getLoaderManager().initLoader(LOADER_ID, null, this);
		initGestureEvent();
		initTime();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.getContentResolver().unregisterContentObserver(mContentObserver);
	}

	/**
	 * 最核心的一句
	 * */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub

		if (mGestureDetector.onTouchEvent(ev)) {
			// ev.setAction(MotionEvent.ACTION_CANCEL);
		}

		// 拦截传给activity内的布局和控件
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void showAddDialog(View view) {
		AddReadingRecordDialogFragment addDialog = new AddReadingRecordDialogFragment();
		addDialog.show(getFragmentManager(), "AddReading");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.add:
			showAddDialog(v);
			break;
		case R.id.settings:

			break;
		default:
			return;
		}
	}

	/**
	 * 根据当前年月递增得到月份
	 * */
	int getYearAfterIncreaseMonth(int year, int month) {
		if (++month > MONTH_PER_YEAR - 1) {
			month %= MONTH_PER_YEAR;// =0
			return year + 1;
		}
		return year;
	}

	int getMonthAfterIncreaseMonth(int month) {
		month++;
		return month %= MONTH_PER_YEAR;
	}

	@Override
	public Loader onCreateLoader(int id, Bundle arg1) {
		// TODO Auto-generated method stub
		Log.v(this, "onCreateLoader " + id + "...");

		int year = mReadingProgressGridView.getYear();
		int mon = mReadingProgressGridView.getMonth();
		Calendar c1 = Calendar.getInstance();
		c1.set(Calendar.YEAR, year);
		c1.set(Calendar.MONTH, mon);
		c1.set(Calendar.DAY_OF_MONTH, 1);

		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.YEAR, getYearAfterIncreaseMonth(year, mon));
		c2.set(Calendar.MONTH, getMonthAfterIncreaseMonth(mon));
		c2.set(Calendar.DAY_OF_MONTH, 1);

		// Date need jianqu 1900 year

		/**
		 * 过滤 当前月数据库
		 * */
		String SELECT = ReadingRecord.TYPE_date + " >= ? and "
				+ ReadingRecord.TYPE_date + " <= ? ";
		String[] SELECT_ARGS = new String[] { "" + c1.getTimeInMillis(),
				"" + c2.getTimeInMillis() };
		String ORDER = ReadingRecord.TYPE_title + " asc";

		return new CursorLoader(this, URI, PROJECT, SELECT, SELECT_ARGS, ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		Log.v(this, "onLoadFinished " + loader.getId() + "...");
		reloadData(cursor);
		refreshUi();
	}

	/**
	 * pass data to refresh reading progress view
	 * */
	void refreshUi() {
		Log.v(this, "refreshUi reading progress view, using cursor...");
		mReadingProgressGridView.refresh(m2DimensionVector);
	}

	/**
	 * reloading data.后面的数据默认为空
	 * */
	void reloadData(Cursor c) {
		Log.v(this, "reloading data...");
		dumpCursor(c);
		// first ,clear original data
		m2DimensionVector.clear();

		if (0 == c.getCount()) {
			c.close();
			return;
		}
		// cursor reset to 0.

		c.moveToFirst();
		do {
			ReadingRecord r = new ReadingRecord(c.getString(c
					.getColumnIndex(ReadingRecord.TYPE_title)), c.getString(c
					.getColumnIndex(ReadingRecord.TYPE_author)), c.getInt(c
					.getColumnIndex(ReadingRecord.TYPE_read)), c.getLong(c
					.getColumnIndex(ReadingRecord.TYPE_date)));

			ContentValues values = new ContentValues();
			String where = ReadingRecord.TYPE_title + " = ? ";
			String[] selectionArgs = new String[] { "" + r.title };

			// 获取这本书读书历史的颜色值
			int defaultColor = 0;
			Cursor colorCursor = getContentResolver().query(URI,
					new String[] { ReadingRecord.TYPE_color }, where,
					selectionArgs, null);
			if (colorCursor != null && colorCursor.getCount() > 0) {
				colorCursor.moveToFirst();
				defaultColor = colorCursor.getInt(colorCursor
						.getColumnIndex(ReadingRecord.TYPE_color));
			}
			colorCursor.close();

			// 为了不使他为空,预先加入一个.
			if (m2DimensionVector.size() == 0) {
				int col = productRandomColor();
				Vector<ReadingRecord> v = new Vector<ReadingRecord>();
				v.add(r);

				// values.put(ReadingRecord.TYPE_color, col);
				// getContentResolver().update(URI, values, where,
				// selectionArgs);
				m2DimensionVector.add(new ReadHistory(v, col));
			} else {
				int i = 0;
				for (; i < m2DimensionVector.size(); i++) {
					// 书名相同,日期不同

					if (m2DimensionVector.get(i).contains(r)) {
						m2DimensionVector.get(i).add(r);
						break;// important
					}
				}
				if (i == m2DimensionVector.size()) {
					int col = productRandomColor();// use previous color if
													// existed
													// values.put(ReadingRecord.TYPE_color,
													// col);
					Vector<ReadingRecord> v = new Vector<ReadingRecord>();
					v.add(r);
					// getContentResolver().update(URI, values, where,
					// selectionArgs);
					m2DimensionVector.add(new ReadHistory(v, col));
				}
			}
		} while (c.moveToNext());
		c.close();
		dump2DimensionVector();
	}

	void dump2DimensionVector() {
		Log.v(this,
				"########################dump2DimensionVector#################################");
		for (int i = 0; i < m2DimensionVector.size(); i++) {
			for (int j = 0; j < m2DimensionVector.get(i).history.size(); j++) {
				Log.v(this, "book:" + i + "		,day:" + j + "		"
						+ m2DimensionVector.get(i).history.get(j) + "		color:"
						+ m2DimensionVector.get(i).color);
			}
		}
		Log.v(this,
				"########################dump2DimensionVector#################################");
	}

	/**
	 * product a random color
	 * */
	int productRandomColor() {
		int RED = (int) (Math.random() * 255) + 1;
		int GREEN = (int) (Math.random() * 255) + 1;
		int BLUE = (int) (Math.random() * 255) + 1;

		return Color.rgb(RED, GREEN, BLUE);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

	void dumpCursor(Cursor c) {
		if (null == c)
			return;
		Log.v(this,
				"########################dumpCursor   begin##########################");
		while (c.moveToNext()) {
			Log.v(this,
					c.getPosition()
							+ "/"
							+ c.getCount()
							+ "	"
							+ c.getString(c
									.getColumnIndex(ReadingRecord.TYPE_title))
							+ "	"
							+ c.getString(c
									.getColumnIndex(ReadingRecord.TYPE_read))
							+ "	"
							+ c.getLong(c
									.getColumnIndex(ReadingRecord.TYPE_date)));
		}
		c.moveToFirst();// reset cursor
		Log.v(this,
				"########################dumpCursor   end##########################");

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub

		return false;// super.onTouchEvent(event);
	}

}
