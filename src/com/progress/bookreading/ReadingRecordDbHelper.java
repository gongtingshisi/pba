package com.progress.bookreading;

import com.gtss.douban.DatabaseBookInterface;
import com.gtss.douban.DatabaseSQLiteOpenHelper;

import kg.gtss.utils.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 用于储存阅读记录的数据库
 * */
public class ReadingRecordDbHelper extends SQLiteOpenHelper {
	public static String DB_NAME = "reading.db";
	final public static String TableName_reading_record = "reading";
	private static int DATABASE_VERSION = 1;
	// create table.INTEGER PRIMARY KEY,
	private static String TABLE_CREATE = "create table "
			+ TableName_reading_record + "(" + DatabaseBookInterface._ID

			+ " integer default '1' not null primary key autoincrement,"
			/*
			 * + DatabaseBookInterface.COLUMN_NAME_ISBN + " integer not null," +
			 * DatabaseBookInterface.COLUMN_NAME_IMGURI + " text not null,"
			 */
			+ ReadingRecord.TYPE_author + " text not null,"
			+ ReadingRecord.TYPE_title + " text not null,"
			+ ReadingRecord.TYPE_page + " integer not null,"
			+ ReadingRecord.TYPE_base + " integer not null,"
			+ ReadingRecord.TYPE_read + " integer not null,"
			+ ReadingRecord.TYPE_date + " long not null,"
			+ ReadingRecord.TYPE_color + " integer not null)";

	public ReadingRecordDbHelper(Context context) {

		// TODO Auto-generated constructor stub
		super(context, DB_NAME, null, DATABASE_VERSION);
		/* Log.v(this, "new ReadingRecordDbHelper"); */
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.v(this, "create table " + TableName_reading_record + "==>"
				+ TABLE_CREATE + "...");
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL(TableName_reading_record);
		onCreate(db);
	}

}
