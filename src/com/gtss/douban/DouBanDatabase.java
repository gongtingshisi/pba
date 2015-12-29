package com.gtss.douban;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
//import android.util.Log;
import kg.gtss.utils.Log;

/**
 * database structure for douban,for database,different from DouBanBooks(for
 * query from server)
 * */
public class DouBanDatabase {
	// private String this = "DouBanDatabase";
	final public static String DB_TABLE_DouBan = DatabaseSQLiteOpenHelper.TableName;

	DatabaseSQLiteOpenHelper mDatabaseSQLiteOpenHelper;
	SQLiteDatabase mSQLiteDatabase;

	DatabaseChangedCallback mDatabaseChangedCallback;

	public DouBanDatabase(Context context, DatabaseChangedCallback cb) {
		mDatabaseSQLiteOpenHelper = new DatabaseSQLiteOpenHelper(context);
		mSQLiteDatabase = mDatabaseSQLiteOpenHelper.getWritableDatabase();
		mDatabaseChangedCallback = cb;
	}

	/**
	 * insert book info to table tableName.should better new a thread to exec
	 * */
	public void insert(DouBanBooks book) {
		// Create a new map of values, where column
		// names are the keys
		ContentValues values = new ContentValues();
		values.put(DatabaseBookInterface.COLUMN_NAME_FAVORITE, book.favorite);
		values.put(DatabaseBookInterface.COLUMN_NAME_ISBN, book.isbn13);
		values.put(DatabaseBookInterface.COLUMN_NAME_TITLE, book.title);
		// take care,primary author
		values.put(DatabaseBookInterface.COLUMN_NAME_AUTHOR, book.author.get(0));
		values.put(DatabaseBookInterface.COLUMN_NAME_DATE, book.date);
		values.put(DatabaseBookInterface.COLUMN_NAME_SUMMARY, book.summary);
		values.put(DatabaseBookInterface.COLUMN_NAME_IMGURI, book.imguri);
		// Insert the new row, returning the primary
		// key value of the new row
		long newRowId;
		if (!queryExistISBN(book)) {
			newRowId = mSQLiteDatabase.insert(DB_TABLE_DouBan, null, values);
			Log.v(this, "add row " + newRowId + " for " + DB_TABLE_DouBan);
		} else {
			Log.v(this, "the book has collected ever!!!" + book.isbn13);
		}
		mDatabaseChangedCallback.DatabaseChangedCallbackUi();
	}

	/**
	 * update a book by isbn
	 * */
	public void updateDatabase(String table, DouBanBooks book) {

		// New value for one column
		ContentValues values = new ContentValues();
		values.put(DatabaseBookInterface.COLUMN_NAME_FAVORITE, book.favorite);
		values.put(DatabaseBookInterface.COLUMN_NAME_ISBN, book.isbn13);
		values.put(DatabaseBookInterface.COLUMN_NAME_TITLE, book.title);
		values.put(DatabaseBookInterface.COLUMN_NAME_AUTHOR, book.author.get(0));
		values.put(DatabaseBookInterface.COLUMN_NAME_SUMMARY, book.summary);
		values.put(DatabaseBookInterface.COLUMN_NAME_IMGURI, book.imguri);
		// Which row to update, based on the title of book
		String selection = DatabaseBookInterface.COLUMN_NAME_ISBN + " = ?";
		String[] selectionArgs = { book.isbn13 };

		int count = mSQLiteDatabase.update(table, values, selection,
				selectionArgs);
		Log.v(this, "update database " + table + " " + count);
		mDatabaseChangedCallback.DatabaseChangedCallbackUi();
	}

	/**
	 * query database.DouBanDatabase.DB_TABLE_DouBan
	 * */
	public Cursor queryDatabase() {

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.

		// How you want the results sorted in the resulting Cursor
		// String sortOrder = DatabaseBookInterface._ID + " DESC";

		// sortef by collected date
		String sortOrder = DatabaseBookInterface.COLUMN_NAME_DATE + " DESC";

		Cursor c = mSQLiteDatabase.query(DB_TABLE_DouBan, // The
															// table
															// to
				// query
				DatabaseSQLiteOpenHelper.PROJECTION, // The columns to return
				null, // The columns for the WHERE clause
				null, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);
		Log.v(this, "queryDatabase----count>>>>" + c.getCount());

		// cursor position is import
		// while (c.moveToNext()) {
		// String isbn = c
		// .getString(c
		// .getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_ISBN));
		// String title = c
		// .getString(c
		// .getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_TITLE));
		// String author = c
		// .getString(c
		// .getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_AUTHOR));
		// String date = c
		// .getString(c
		// .getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_DATE));
		// Log.v(this, "query database " + DB_TABLE_DouBan + " get====" + isbn
		// + "===" + title + "====" + author + "========" + date);
		// }

		return c;
	}

	/**
	 * query database.DouBanDatabase.DB_TABLE_DouBan based on isbn
	 * */
	public Cursor queryDatabaseByIsbn(String isbn) {
		if (null == isbn)
			return null;
		// Define a projection that specifies which columns from the database
		// you will actually use after this query.

		// How you want the results sorted in the resulting Cursor
		String sortOrder = DatabaseBookInterface._ID + " DESC";

		Cursor c = mSQLiteDatabase.query(DB_TABLE_DouBan, // The
															// table
															// to
				// query
				DatabaseSQLiteOpenHelper.PROJECTION, // The columns to return
				DatabaseBookInterface.COLUMN_NAME_ISBN + "=?", // The columns
																// for the WHERE
																// clause
				new String[] { isbn }, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);
		Log.v(this, "queryDatabase----count>>>>" + c.getCount());

		// while (c.moveToNext()) {
		// String Isbn13 = c
		// .getString(c
		// .getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_ISBN));
		// String title = c
		// .getString(c
		// .getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_TITLE));
		// String author = c
		// .getString(c
		// .getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_AUTHOR));
		// Log.v(this, "query database " + DB_TABLE_DouBan + " get====" + Isbn13
		// + "===" + title + "====" + author);
		// }
		return c;
	}

	/**
	 * wether we has collected the isbn
	 * */
	public boolean queryExistISBN(DouBanBooks book) {

		Cursor c = mSQLiteDatabase.query(DB_TABLE_DouBan, // The
				// table
				// to
				// query
				new String[] { DatabaseBookInterface.COLUMN_NAME_ISBN }, // The
																			// columns
																			// to
																			// return
				DatabaseBookInterface.COLUMN_NAME_ISBN + " = ?", // The
																	// columns
																	// for the
																	// WHERE
																	// clause
				new String[] { book.isbn13 },// The values for the WHERE clause
				null, null, DatabaseBookInterface._ID + " asc");
		while (c.moveToNext()) {
			if (book.isbn13
					.equals(c.getString(c
							.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_ISBN)))) {
				// c.close();
				return true;
			}
		}

		return false;
	}

	/**
	 * delete a book by isbn
	 * */
	public boolean deleteBookByISBN(String isbn) {

		boolean r = mSQLiteDatabase.delete(DB_TABLE_DouBan,
				DatabaseBookInterface.COLUMN_NAME_ISBN + " = ?",
				new String[] { isbn }) > 0;
		if (r) {
			mDatabaseChangedCallback.DatabaseChangedCallbackUi();
		}
		return r;
	}

	/**
	 * wether favorite a book
	 * */
	public boolean favoriteBookByISBN(String isbn, boolean favorite) {
		Cursor c = queryDatabaseByIsbn(isbn);

		if (null != c && c.getCount() > 0) {
			c.moveToFirst();

			// New value for one column
			ContentValues values = new ContentValues();
			values.put(DatabaseBookInterface.COLUMN_NAME_FAVORITE, favorite);

			// Which row to update, based on the isbn of book
			String selection = DatabaseBookInterface.COLUMN_NAME_ISBN + " = ?";
			String[] selectionArgs = { c.getString(c
					.getColumnIndex(DatabaseBookInterface.COLUMN_NAME_ISBN)) };

			boolean r = mSQLiteDatabase.update(DB_TABLE_DouBan, values,
					selection, selectionArgs) > 0;
			Log.v(this, "favoriteBookByISBN " + isbn + " -> " + favorite
					+ "	,GET:" + r);
			if (r)
				mDatabaseChangedCallback.DatabaseChangedCallbackUi();
			return r;
		}
		return false;
	}

	/**
	 * fuzzy search,including title,author,tag,etc.
	 * DatabaseBookInterface.COLUMN_NAME_AUTHOR + " like ? or " +
	 * */
	public Cursor queryBookFuzzy(String info) {
		return mSQLiteDatabase.query(DB_TABLE_DouBan,
				DatabaseSQLiteOpenHelper.PROJECTION,
				DatabaseBookInterface.COLUMN_NAME_TITLE + " like ? ",
				new String[] { "%" + info + "%" }, null, null,
				DatabaseBookInterface._ID + " DESC");
	}

}
