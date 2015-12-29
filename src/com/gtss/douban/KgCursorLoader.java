package com.gtss.douban;

import kg.gtss.utils.Log;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;

/**
 * 
 * Unused ! A loader that queries the ContentResolver and returns a
 * Cursor.用于进入本地管理加载数据库中数据的加载器
 * .这个和InnerCursorLoader很接近,我在考虑继承,只是loadInBackground中方法不同而已
 * */
public class KgCursorLoader extends CursorLoader {
	Context mContext;
	DouBanDatabase db;

	public KgCursorLoader(Context context, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		super(context, uri, projection, selection, selectionArgs, sortOrder);
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	public KgCursorLoader(Context context, DouBanDatabase db) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		this.db = db;
	}

	@Override
	public Cursor loadInBackground() {
		// TODO Auto-generated method stub
		Log.v(this, "loadInBackground ");
		Cursor c = db.queryDatabase();

		return c;
	}

	@Override
	protected void onStartLoading() {
		// TODO Auto-generated method stub
		super.onStartLoading();
		Log.v(this, "onStartLoading ");
	}

	@Override
	protected void onStopLoading() {
		// TODO Auto-generated method stub
		super.onStopLoading();
		Log.v(this, "onStopLoading ");
	}

}
