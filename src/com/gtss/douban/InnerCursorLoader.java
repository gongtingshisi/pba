package com.gtss.douban;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

/**
 * Unused! 用于用户模糊搜索的加载器,,这个和KgCursorLoader和相近,我再考虑继承,,只是loadInBackground中方法不同而已
 * */
public class InnerCursorLoader extends CursorLoader {
	Context mContext;
	DouBanDatabase mDouBanDatabase;
	String mSearchKeyWord;

	public static int TYPE_SEARCH_ALL = 0;
	public static int TYPE_SEARCH_BOOKNAME = 1;
	public static int TYPE_SEARCH_FAVORITEBOOK = 2;

	public InnerCursorLoader(Context c, DouBanDatabase db, String s) {
		super(c);
		// TODO Auto-generated constructor stub
		mDouBanDatabase = db;
		mContext = c;
		mSearchKeyWord = s;
	}

	@Override
	public Cursor loadInBackground() {
		// TODO Auto-generated method stub
		return mDouBanDatabase.queryBookFuzzy(mSearchKeyWord);

	}
}
