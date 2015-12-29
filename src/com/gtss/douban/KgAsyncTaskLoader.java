package com.gtss.douban;

import kg.gtss.utils.Log;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

/**
 * Unused!
 * */
public class KgAsyncTaskLoader extends AsyncTaskLoader<Cursor> {
	Context mContext;

	public KgAsyncTaskLoader(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public Cursor loadInBackground() {
		// TODO Auto-generated method stub
		Log.v(this, "loadInBackground ");
		return null;
	}

}
