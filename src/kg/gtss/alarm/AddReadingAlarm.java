package kg.gtss.alarm;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.personalbooksassitant.ReadingNotesActivity;
import kg.gtss.personalbooksassitant.TabSubView;
import kg.gtss.utils.Common;
import kg.gtss.utils.Log;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

/**
 * add an alarm for reading
 * */
public class AddReadingAlarm extends Activity implements OnClickListener,
		LoaderCallbacks<Cursor> {

	ReadingAlarmAdapter mReadingAlarmAdapter;
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
	int LOADER_ID = Common.LOADER_ID_AddReadingAlarm;

	public ListView mListView;
	public TabSubView mAddBtn;

	// TabSubView mSettingsBtn;

	ContentObserver mContentObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			// Log.v(AddReadingAlarm.this, "onChange ,restartLoader ");
			AddReadingAlarm.this.getLoaderManager().restartLoader(LOADER_ID,
					null, AddReadingAlarm.this);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setTitle("添加阅读提醒");
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.setContentView(R.layout.add_reading_alarm);
		mListView = (ListView) this.findViewById(R.id.add_alarm_list);

		mAddBtn = (TabSubView) this.findViewById(R.id.add_alarm_add_btn);
		// mSettingsBtn = (TabSubView) this
		// .findViewById(R.id.add_alarm_settings_btn);
		mAddBtn.setOnClickListener(this);
		// mSettingsBtn.setOnClickListener(this);
		this.getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		this.getContentResolver().registerContentObserver(URI, true,
				mContentObserver);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.getContentResolver().unregisterContentObserver(mContentObserver);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_alarm_add_btn:
			if (v.getTag() instanceof ReadingNotesActivity) {
				Log.v(this, "create a note ~");
				return;
			}
			Intent i = new Intent();
			i.setClass(this, AddAlarm.class);
			this.startActivity(i);
			break;

		default:
			break;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		// Log.v(this, "onCreateLoader");
		return new CursorLoader(this, URI, PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		// TODO Auto-generated method stub

		Log.v(this, "onLoadFinished "
				+ ((null == c) ? "null"
						: (c.getCount() == 0 ? 0 : c.getCount())));

		mListView.setAdapter(mReadingAlarmAdapter = new ReadingAlarmAdapter(
				this, c));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
