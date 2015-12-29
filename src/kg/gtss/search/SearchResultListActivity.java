package kg.gtss.search;

import com.gtss.douban.DatabaseBookInterface;
import com.gtss.douban.DatabaseChangedCallback;
import com.gtss.douban.DouBanContentProvider;
import com.gtss.douban.DouBanDatabase;
import com.gtss.douban.InnerCursorLoader;

import kg.gtss.personalbooksassitant.PbaMain;
import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Common;
import kg.gtss.utils.Log;

import android.app.ListActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

/**
 * 显示用户模糊搜索后结果列表的activity.这个是PbaMain的一个子集,可以考虑使用继承关系
 * */
public class SearchResultListActivity extends ListActivity implements
		LoaderCallbacks<Cursor> {
	DouBanDatabase mDouBanDatabase;
	SearchResultCursorAdapter mSearchResultCursorAdapter;
	String query;
	DatabaseChangedCallback cb;
	int LOADER_ID_SearchResultListActivity = Common.LOADER_ID_SearchResultListActivity;
	ListView mSearchResultListView;

	String SELECT = DatabaseBookInterface.COLUMN_NAME_TITLE + " like ? ";

	String ORDER = DatabaseBookInterface._ID + " DESC";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		// 获得搜索框里值
		query = intent.getStringExtra(SearchManager.QUERY);

		// 保存搜索记录.最后会显示到搜索框提示栏
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
				SearchSuggestProvider.AUTHORITY, SearchSuggestProvider.MODE);
		suggestions.saveRecentQuery(query, null);

		mDouBanDatabase = new DouBanDatabase(this,
				new DatabaseChangedCallback() {

					@Override
					public void DatabaseChangedCallbackUi() {
						// TODO Auto-generated method stub
						Log.v(this,
								"SearchResultListActivity DatabaseChangedCallbackUi");
						Log.v(this, "SearchResultListActivity reload db loader");
						// 此处因为更新了数据库整个重新加载数据库,更新ui.另一个办法就是使用contentprovider,cursor.setcontentchange.
						// 此处这个方法比较重量,臃肿
						SearchResultListActivity.this.getLoaderManager()
								.destroyLoader(
										LOADER_ID_SearchResultListActivity);
						SearchResultListActivity.this.getLoaderManager()
								.initLoader(LOADER_ID_SearchResultListActivity,
										null, SearchResultListActivity.this);
					}
				});

		this.setContentView(R.layout.search_result_listview_layout);
		this.setTitle("搜索结果");
		this.getActionBar().setDisplayHomeAsUpEnabled(true);

		// 开始创建加载器
		this.getLoaderManager().initLoader(LOADER_ID_SearchResultListActivity,
				null, this);
		this.getListView().setOnItemLongClickListener(
				new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int position, long id) {
						// TODO Auto-generated method stub
						Log.v(this, "SearchResultListActivity onItemLongClick "
								+ position + "	" + id);
						PbaMain.deleteOneBook(SearchResultListActivity.this,
								mDouBanDatabase, mSearchResultCursorAdapter,
								position);
						return true;
					}
				});
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		Log.v(this, "create loader " + id);
		String[] SELECT_ARGS = new String[] { "%" + query + "%" };
		return new CursorLoader(this, DouBanContentProvider.CONTENT_URI,
				DouBanContentProvider.PROJECTION, SELECT, SELECT_ARGS, ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// TODO Auto-generated method stub

		Log.v(this, "load finish ");
		if (null == data)
			return;
		if (0 == data.getCount())
			Log.v(this, "search for 0 result.");
		else
			Log.v(this, "search for " + data.getCount() + " results.");
		mSearchResultCursorAdapter = new SearchResultCursorAdapter(this, data,
				mDouBanDatabase);
		// 等搜索到了数据之后,将适配器和listview绑定
		getListView().setAdapter(mSearchResultCursorAdapter);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}

}
