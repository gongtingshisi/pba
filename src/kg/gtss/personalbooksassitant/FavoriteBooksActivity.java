package kg.gtss.personalbooksassitant;

import kg.gtss.search.SearchResultCursorAdapter;
import kg.gtss.utils.Common;

import com.gtss.douban.DouBanContentProvider;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

/**
 * favorite books
 * */
public class FavoriteBooksActivity extends Activity implements
		LoaderCallbacks<Cursor> {
	int LOADER_ID = Common.LOADER_ID_FavoriteBooksActivity;
	String SELECT = "favorite = ?";
	String[] SELECT_ARGS = new String[] { "" + 1 };
	String ORDER = null;
	SearchResultCursorAdapter mSearchResultCursorAdapter;
	GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		this.setTitle("喜欢的图书");
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.setContentView(R.layout.gridview_entry);
		mGridView = (GridView) this.findViewById(R.id.grid);
		this.getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new CursorLoader(this, DouBanContentProvider.CONTENT_URI,
				DouBanContentProvider.PROJECTION, SELECT, SELECT_ARGS, ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> l, Cursor c) {
		// TODO Auto-generated method stub
		if (null == c || c.getCount() == 0) {
			((TextView) findViewById(R.id.null_notice))
					.setText(R.string.null_notice);
			mGridView.setAdapter(null);
			return;
		}
		mSearchResultCursorAdapter = new SearchResultCursorAdapter(this, c,
				SearchResultCursorAdapter.GRID_SUB_ITEM_FORM);
		mGridView.setAdapter(mSearchResultCursorAdapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
