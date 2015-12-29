package kg.gtss.personalbooksassitant;

import com.gtss.douban.DatabaseBookInterface;
import com.gtss.douban.DatabaseChangedCallback;
import com.gtss.douban.DouBanContentProvider;
import com.gtss.douban.DouBanDatabase;

import kg.gtss.search.SearchResultCursorAdapter;
import kg.gtss.utils.Common;
import kg.gtss.utils.Log;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemLongClickListener;

public class LocalManagerFragment extends Fragment implements
		LoaderCallbacks<Cursor> {
	ListView mLocalManagerListView;
	ProgressBar mLocalManagerProgressView;
	Activity mActivity;
	DouBanDatabase mDouBanDatabase;
	private int LOADER_ID = Common.LOADER_ID_LocalManagerFragment;
	SearchResultCursorAdapter mTimeLineCursorAdapter;

	String SELECT = null;
	String[] SELECT_ARGS = new String[] {};
	String ORDER = null;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mActivity = activity;
		Log.v(this, "attach LocalManagerFragment");
	}

	/**
	 * 
	 * bind data to view
	 * **/
	void bindAdapter(Context context, Cursor c) {
		mTimeLineCursorAdapter = new SearchResultCursorAdapter(context, c,
				mDouBanDatabase);

		mLocalManagerListView.setAdapter(mTimeLineCursorAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v3 = inflater.inflate(R.layout.local_manager, null);

		mLocalManagerListView = (ListView) v3
				.findViewById(R.id.local_manager_list);
		mLocalManagerProgressView = (ProgressBar) v3
				.findViewById(R.id.local_manager_load_progress);

		mLocalManagerListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						Log.v(this, "onItemLongClick " + position + "	" + id);

						// 查询到这本书的名字传入
						deleteOneBook(position);
						return true;
					}
				});
		return v3;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		reloadData();
		// create db
		mDouBanDatabase = new DouBanDatabase(mActivity,
				new DatabaseChangedCallback() {

					@Override
					public void DatabaseChangedCallbackUi() {
						// TODO Auto-generated method stub
						Log.v(this, "DatabaseChangedCallbackUi");
						reloadData();
					}
				});
	}

	void reloadData() {
		// log("PbaMain reload db loader");
		// 此处因为更新了数据库整个重新加载数据库,更新ui.另一个办法就是使用contentprovider,cursor.setcontentchange.
		// 此处这个方法比较重量,臃肿
		mActivity.getLoaderManager().destroyLoader(LOADER_ID);
		mActivity.getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	void deleteOneBook(int position) {

		if (mTimeLineCursorAdapter != null
				&& mTimeLineCursorAdapter.getCount() > 0) {

			Cursor c = mTimeLineCursorAdapter.getCursor();
			c.moveToPosition(position);
			final String title = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_TITLE));
			final String author = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_AUTHOR));
			final String isbn = c
					.getString(c
							.getColumnIndexOrThrow(DatabaseBookInterface.COLUMN_NAME_ISBN));

			AlertDialog.Builder builder = new AlertDialog.Builder(
					this.getActivity());
			builder.setTitle("Delete it?");
			// 此处使用书名
			builder.setMessage("Are you sure to delete the book "
					+ (title + " " + author) + " ?");
			builder.setPositiveButton(R.string.ok_button,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// 从数据库删除，并且刷新时间轴节目，以及其他节目信息，比如这本书可能刚刚是在其他界面查询过的
							mDouBanDatabase.deleteBookByISBN(isbn);
							Log.v(this, "delete ");

						}
					}).setNegativeButton(R.string.no_button, null);
			builder.show();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return new CursorLoader(mActivity, DouBanContentProvider.CONTENT_URI,
				DouBanContentProvider.PROJECTION, SELECT, SELECT_ARGS, ORDER);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> l, Cursor data) {
		// TODO Auto-generated method stub
		bindAdapter(this.getActivity(), data);
		if (null != data) {
			mLocalManagerProgressView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}
}
