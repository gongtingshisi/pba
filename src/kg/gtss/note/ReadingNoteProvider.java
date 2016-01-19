package kg.gtss.note;

import com.progress.bookreading.ReadingRecord;

import kg.gtss.alarm.ReadingAlarmSQLiteOpenHelper;
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

public class ReadingNoteProvider extends ContentProvider {
	boolean DEBUG = true;
	public final static String AUTHORITY = "kg.gtss.personalbooksassitant.ReadingNoteProvider";
	ReadingNoteSQLiteOpenHelper mReadingNoteSQLiteOpenHelper;
	SQLiteDatabase mSQLiteDatabase;
	Context mContext;

	static String TABLE = ReadingNoteSQLiteOpenHelper.TABLE_NAME;
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE);
	private static final int NOTE = 1;
	private static final int NOTE_ID = 2;
	private static final UriMatcher sURLMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURLMatcher.addURI(AUTHORITY, TABLE, NOTE);
		sURLMatcher.addURI(AUTHORITY, TABLE + "/#", NOTE_ID);
	}

	@Override
	public int delete(Uri url, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.v(this, "delete " + url);
		int match = sURLMatcher.match(url);
		int count = -1;
		switch (match) {
		case NOTE:
			// delete all
			count = mSQLiteDatabase.delete(TABLE, where, whereArgs);
			break;
		case NOTE_ID:
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
		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	@Override
	public String getType(Uri url) {
		// TODO Auto-generated method stub
		int match = sURLMatcher.match(url);
		switch (match) {
		case NOTE:
			return "vnd.android.cursor.dir/" + TABLE;
		case NOTE_ID:
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
		if (DEBUG)
			Log.v(this, "query " + url);
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		// Generate the body of the query
		int match = sURLMatcher.match(url);
		switch (match) {
		case NOTE:
			// Sets the list of tables to query. Multiple tables can be
			// specified to perform a join. For example: setTables("foo, bar")
			// setTables("foo LEFT OUTER JOIN bar ON (foo.id = bar.foo_id)")
			qb.setTables(TABLE);
			break;
		case NOTE_ID:
			qb.setTables(TABLE);
			qb.appendWhere("_id=");
			if (DEBUG)
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
			if (DEBUG)
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
		mReadingNoteSQLiteOpenHelper = new ReadingNoteSQLiteOpenHelper(
				this.getContext());
		mSQLiteDatabase = mReadingNoteSQLiteOpenHelper.getWritableDatabase();
		return true;// !!
	}

	@Override
	public int update(Uri url, ContentValues value, String where,
			String[] whereArgs) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.v(this, "update " + url);
		int count = -1;
		int match = sURLMatcher.match(url);
		if (DEBUG)
			Log.v(this,
					"update " + url + "  match:" + match + "   "
							+ value.get(ReadingRecord.TYPE_title) + " "
							+ value.get(ReadingRecord.TYPE_read));
		switch (match) {
		case NOTE:

			break;
		case NOTE_ID:

			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		count = mSQLiteDatabase.update(TABLE, value, where, whereArgs);
		if (DEBUG)
			Log.v(this, "update result " + count);
		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	@Override
	public Uri insert(Uri url, ContentValues values) {
		// TODO Auto-generated method stub
		if (DEBUG)
			Log.v(this, "insert " + url);
		// cannot insert a special id,id assigned by database
		if (sURLMatcher.match(url) != NOTE) {
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
