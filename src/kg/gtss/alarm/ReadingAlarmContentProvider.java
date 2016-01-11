package kg.gtss.alarm;

import com.progress.bookreading.ReadingRecord;

import kg.gtss.utils.Log;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * to manage reading alarm
 * */
public class ReadingAlarmContentProvider extends ContentProvider {
	public static final String AUTHORITY = "kg.gtss.personalbooksassitant.ReadingAlarmContentProvider";

	ReadingAlarmSQLiteOpenHelper mReadingAlarmSQLiteOpenHelper;
	SQLiteDatabase mSQLiteDatabase;
	Context mContext;

	static String TABLE = ReadingAlarmSQLiteOpenHelper.READING_ALARM_TABLE;
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE);
	private static final int ALARM = 1;
	private static final int ALARM_ID = 2;
	private static final UriMatcher sURLMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURLMatcher.addURI(AUTHORITY, TABLE, ALARM);
		sURLMatcher.addURI(AUTHORITY, TABLE + "/#", ALARM_ID);
	}

	@Override
	public int delete(Uri url, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		int match = sURLMatcher.match(url);
		int count = -1;
		switch (match) {
		case ALARM:
			// delete all
			count = mSQLiteDatabase.delete(TABLE, where, whereArgs);
			break;
		case ALARM_ID:
			// delete one
			// get row id
			String segment = url.getPathSegments().get(1);
			long rowId = Long.parseLong(segment);
			//
			if (TextUtils.isEmpty(where)) {
				where = "_id=" + segment;// need?// need ?of curse yes
			} else {
				where = "_id=" + segment + " AND (" + where + ")";
			}
			count = mSQLiteDatabase.delete(TABLE, where, whereArgs);
			break;
		}

		return count;
	}

	@Override
	public String getType(Uri url) {
		// TODO Auto-generated method stub
		int match = sURLMatcher.match(url);
		switch (match) {
		case ALARM:
			return "vnd.android.cursor.dir/" + TABLE;
		case ALARM_ID:
			return "vnd.android.cursor.item/" + TABLE;
		default:
			throw new IllegalArgumentException("Unknown " + TABLE + " URL:"
					+ url);
		}
	}

	@Override
	public Cursor query(Uri url, String[] proj, String select, String[] args,
			String order) {
		// TODO Auto-generated method stub

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		// Generate the body of the query
		int match = sURLMatcher.match(url);
		switch (match) {
		case ALARM:
			// Sets the list of tables to query. Multiple tables can be
			// specified to perform a join. For example: setTables("foo, bar")
			// setTables("foo LEFT OUTER JOIN bar ON (foo.id = bar.foo_id)")
			qb.setTables(TABLE);
			break;
		case ALARM_ID:
			qb.setTables(TABLE);
			qb.appendWhere("_id=");
			Log.v(this, "query " + TABLE + " of  id "
					+ url.getPathSegments().get(1));
			qb.appendWhere(url.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		Cursor c = qb.query(mSQLiteDatabase, proj, select, args, null, null,
				order);
		if (null == c) {
			Log.v(this, "query " + TABLE + " fails ~");
		} else {
			// !!???
			c.setNotificationUri(this.getContext().getContentResolver(), url);
		}
		return c;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mReadingAlarmSQLiteOpenHelper = new ReadingAlarmSQLiteOpenHelper(
				this.getContext());
		mSQLiteDatabase = mReadingAlarmSQLiteOpenHelper.getWritableDatabase();
		return true;// !!
	}

	@Override
	public int update(Uri url, ContentValues value, String where,
			String[] whereArgs) {
		// TODO Auto-generated method stub

		int count = -1;
		int match = sURLMatcher.match(url);
		Log.v(this,
				"update " + url + "  match:" + match + "   "
						+ value.get(ReadingRecord.TYPE_title) + " "
						+ value.get(ReadingRecord.TYPE_read));
		switch (match) {
		case ALARM:

			break;
		case ALARM_ID:

			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		count = mSQLiteDatabase.update(TABLE, value, where, whereArgs);
		Log.v(this, "update result " + count);
		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	@Override
	public Uri insert(Uri url, ContentValues values) {
		// TODO Auto-generated method stub

		// cannot insert a special id,id assigned by database
		if (sURLMatcher.match(url) != ALARM) {
			throw new IllegalArgumentException("Unknown " + TABLE + " URL:"
					+ url
					+ ":cannot insert a special id,id assigned by database~");
		}
		Uri rowUri;
		long rowId = mSQLiteDatabase.insert(TABLE, null, values);
		if (rowId > 0) {
			rowUri = ContentUris.appendId(CONTENT_URI.buildUpon(), rowId)
					.build();
			// notify content provider changed
			this.getContext().getContentResolver().notifyChange(rowUri, null);
			return rowUri;
		}
		throw new SQLException("Failed to insert row into " + TABLE + ",id="
				+ rowId + " ," + url);
	}

}
