package com.gtss.douban;

import android.provider.BaseColumns;

/**
 * some common db Column of book info in various shop,for database.
 * */
public abstract interface DatabaseBookInterface extends BaseColumns {
	public static final String COLUMN_NAME_FAVORITE = "favorite";
	public static final String COLUMN_NAME_TITLE = "title";
	public static final String COLUMN_NAME_ORIGINAL_TITLE = "origin_title";
	public static final String COLUMN_NAME_AUTHOR = "author";
	public static final String COLUMN_NAME_PUBLISH_DATE = "pubdate";
	public static final String COLUMN_NAME_PUBLISHER = "publisher";
	public static final String COLUMN_NAME_PAGE = "pages";
	public static final String COLUMN_NAME_PRICE = "price";
	public static final String COLUMN_NAME_SUMMARY = "summary";
	public static final String COLUMN_NAME_ISBN = "isbn";
	public static final String COLUMN_NAME_DATE = "date";
	public static final String COLUMN_NAME_IMGURI = "imguri";
}
