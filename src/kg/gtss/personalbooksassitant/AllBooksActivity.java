package kg.gtss.personalbooksassitant;

import com.gtss.douban.DouBanContentProvider;

import kg.gtss.utils.Common;
import kg.gtss.utils.Log;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * extends FavoriteBooksActivity.Perfect
 * ~这个地方其实还是在父类中使用父类的id创建的数据库查询initLoader,只不过是在子类中接受响应去onCreateLoader
 * ,然后在子类中onLoadFinished.
 * */
public class AllBooksActivity extends FavoriteBooksActivity {
	String SELECT = null;
	String[] SELECT_ARGS = null;
	int LOADER_ID = Common.LOADER_ID_AllBooksActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setTitle("所有书籍浏览");
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		// TODO Auto-generated method stub
		Log.v(this, "onCreateLoader " + id);
		return new CursorLoader(this, DouBanContentProvider.CONTENT_URI,
				DouBanContentProvider.PROJECTION, SELECT, SELECT_ARGS, ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> l, Cursor c) {
		// TODO Auto-generated method stub
		Log.v(this, "onLoadFinished " + l.getId());
		super.onLoadFinished(l, c);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		super.onLoaderReset(arg0);
	}

}
