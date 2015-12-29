package com.gtss.douban;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Unused! content provider for douban
 * */
public class DouBanContentProvider extends ContentProvider {
	DatabaseSQLiteOpenHelper mDatabaseSQLiteOpenHelper;
	public static final String[] PROJECTION = DatabaseSQLiteOpenHelper.PROJECTION;
	public static final String AOTHORITY = "kg.gtss.personalbooksassitant.douban";
	public static final String TABLE_NAME_DOUBAN = DatabaseSQLiteOpenHelper.TableName;
	public static final Uri CONTENT_URI = Uri.parse("content://" + AOTHORITY
			+ "/" + TABLE_NAME_DOUBAN);

	@Override
	public int delete(Uri uri, String whereClause, String[] whereArgs) {
		// TODO Auto-generated method stub
		int id = -1;
		mDatabaseSQLiteOpenHelper.getWritableDatabase().delete(
				TABLE_NAME_DOUBAN, whereClause, whereArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return id;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub

		long rowId = -1;
		rowId = mDatabaseSQLiteOpenHelper.getWritableDatabase().insert(
				TABLE_NAME_DOUBAN, null, values);
		// 构造插入的新Uri,并通知更新数据库
		Uri cururi = Uri.withAppendedPath(CONTENT_URI, rowId + "");
		getContext().getContentResolver().notifyChange(CONTENT_URI, null);
		return cururi;

	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mDatabaseSQLiteOpenHelper = new DatabaseSQLiteOpenHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String selection,
			String[] selectionArgs, String orderBy) {
		// TODO Auto-generated method stub
		return mDatabaseSQLiteOpenHelper.getWritableDatabase().query(
				TABLE_NAME_DOUBAN, columns, selection, selectionArgs, null,
				null, orderBy);

	}

	@Override
	public int update(Uri uri, ContentValues values, String whereClause,
			String[] whereArgs) {
		// TODO Auto-generated method stub
		int rowId = -1;
		rowId = mDatabaseSQLiteOpenHelper.getWritableDatabase().update(
				TABLE_NAME_DOUBAN, values, whereClause, whereArgs);
		getContext().getContentResolver().notifyChange(CONTENT_URI, null);
		return rowId;
	}
}
