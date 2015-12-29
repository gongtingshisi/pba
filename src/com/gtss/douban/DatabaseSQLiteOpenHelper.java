package com.gtss.douban;

import kg.gtss.utils.Log;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

//import android.util.Log;

/**
 * 
 * database.At first i wanna creat serveral dbs for
 * DouBan,Google,Amazon.But...uh..Db is shared within all parts in a apk.So...i
 * create a dozen of tables. A good way to organize a contract class is to put
 * definitions that are global to your whole database in the root level of the
 * class. Then create an inner class for each table that enumerates its
 * columns.<b>http
 * ://developer.android.com/training/basics/data-storage/databases.html</b>
 * */
public class DatabaseSQLiteOpenHelper extends SQLiteOpenHelper {

	// private final static String this = "DatabaseSQLiteOpenHelper";
	// database name
	public static final String DB_NAME = "pba.db";
	// database version
	public static final int DATABASE_VERSION = 9;
	// table name
	public final static String TableName = "douban";

	// create table.INTEGER PRIMARY KEY,
	private static String TABLE_CREATE = "create table " + TableName + "("
			+ DatabaseBookInterface._ID
			+ " integer default '1' not null primary key autoincrement,"
			+ DatabaseBookInterface.COLUMN_NAME_FAVORITE + " integer not null,"
			+ DatabaseBookInterface.COLUMN_NAME_ISBN + " integer not null,"
			+ DatabaseBookInterface.COLUMN_NAME_TITLE + " text not null,"
			+ DatabaseBookInterface.COLUMN_NAME_AUTHOR + " text not null,"
			+ DatabaseBookInterface.COLUMN_NAME_DATE + " text not null,"
			+ DatabaseBookInterface.COLUMN_NAME_IMGURI + " text not null,"
			+ DatabaseBookInterface.COLUMN_NAME_SUMMARY + " text not null)";

	// + DatabaseBookInterface.COLUMN_NAME_DATE +
	// " timestamp not null default(datetime('now','localtime')))";
	// + DatabaseBookInterface.COLUMN_NAME_DATE +
	// " timestamp not null default  current_timestamp)";
	public static String[] PROJECTION = {

	DatabaseBookInterface._ID, DatabaseBookInterface.COLUMN_NAME_FAVORITE,
			DatabaseBookInterface.COLUMN_NAME_ISBN,
			DatabaseBookInterface.COLUMN_NAME_TITLE,
			DatabaseBookInterface.COLUMN_NAME_AUTHOR,
			DatabaseBookInterface.COLUMN_NAME_DATE,
			DatabaseBookInterface.COLUMN_NAME_SUMMARY,
			DatabaseBookInterface.COLUMN_NAME_IMGURI };
	// delete table
	private static String TABLE_DELETE = "DROP TABLE IF EXISTS " + TableName;

	public DatabaseSQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);

		// mTableName = tableName;
	}

	// public DatabaseSQLiteOpenHelper(Context context, String name,
	// CursorFactory factory, int version) {
	// super(context, name, factory, version);
	// // TODO Auto-generated constructor stub
	// }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.v(this, "onCreate--->>> " + TABLE_CREATE);
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.v(this, "onUpgrade ");
		// This database is only a cache for online data, so its upgrade policy
		// is
		// to simply to discard the data and start over
		db.execSQL(TABLE_DELETE);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		onUpgrade(db, oldVersion, newVersion);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);

	}

}
