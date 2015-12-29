package com.progress.bookreading;

import com.gtss.douban.DatabaseBookInterface;

import kg.gtss.utils.Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 用于数据操作的类,作用于阅读记录数据库的
 * */
public class ReadingRecordDatabase {
	ReadingRecordDbHelper mReadingRecordDbHelper;
	SQLiteDatabase mSQLiteDatabase;
	Context mContext;
	String TABLE = ReadingRecordDbHelper.TableName_reading_record;
	/*
	 * + DatabaseBookInterface.COLUMN_NAME_ISBN + " integer not null," +
	 * DatabaseBookInterface.COLUMN_NAME_IMGURI + " text not null,"
	 */
	public static final String[] PROJ = new String[] {
			ReadingRecord.TYPE_title, ReadingRecord.TYPE_author,
			ReadingRecord.TYPE_page, ReadingRecord.TYPE_base,
			ReadingRecord.TYPE_read, ReadingRecord.TYPE_date,
			ReadingRecord.TYPE_color };
	String SortOrder = ReadingRecord.TYPE_date + " DESC";

	public ReadingRecordDatabase(Context c) {
		mContext = c;
		mReadingRecordDbHelper = new ReadingRecordDbHelper(c);
		mSQLiteDatabase = mReadingRecordDbHelper.getWritableDatabase();
	}

	boolean insert(ReadingRecord record) {

		ContentValues content = new ContentValues();
		content.put(ReadingRecord.TYPE_title, record.title);
		content.put(ReadingRecord.TYPE_author, record.author);
		content.put(ReadingRecord.TYPE_page, record.page);// need fetch from
															// DouBanDb?
		content.put(ReadingRecord.TYPE_base, record.base);// query this database
		content.put(ReadingRecord.TYPE_read, record.read);
		content.put(ReadingRecord.TYPE_date, record.date);
		content.put(ReadingRecord.TYPE_color, record.color);
		long rowId = mSQLiteDatabase.insert(TABLE, null, content);
		Log.v(this, "insert " + record + " to table " + TABLE + ",return "
				+ rowId);

		return rowId > 0;
	}

	Cursor query(ReadingRecord record) {
		return mSQLiteDatabase.query(TABLE, PROJ, null, null, null, null,
				SortOrder);
	}

	boolean delete(ReadingRecord record) {
		return false;
	}

	boolean update(ReadingRecord record) {
		return false;
	}

	void dump() {
		Log.v(this, "########################dump " + TABLE
				+ " begin##########################");
		Cursor c = mSQLiteDatabase.query(TABLE, PROJ, null, null, null, null,
				SortOrder);
		while (c.moveToNext()) {
			Log.v(this,
					c.getPosition()
							+ "/"
							+ c.getCount()
							+ "	"
							+ c.getString(c
									.getColumnIndex(ReadingRecord.TYPE_read)));
		}

		c.moveToFirst();// reset cursor
		Log.v(this, "########################dump " + TABLE
				+ " end##########################");

	}
}
