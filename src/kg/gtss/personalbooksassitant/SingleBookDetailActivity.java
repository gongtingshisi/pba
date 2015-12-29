package kg.gtss.personalbooksassitant;

import com.gtss.douban.DatabaseBookInterface;
import com.gtss.douban.DatabaseChangedCallback;
import com.gtss.douban.DouBanBooks;
import com.gtss.douban.DouBanDatabase;
import com.gtss.douban.DouBanQuery;

import kg.gtss.utils.Common;
import kg.gtss.utils.Log;
import kg.gtss.utils.TimeUtils;
import android.app.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SingleBookDetailActivity extends FragmentActivity {
	private DouBanDatabase mDouBanDatabase;
	String mIsbn;
	DouBanBooks mInfo = new DouBanBooks();

	TextView mTitleText, mAuthorText, mSummaryText, mISBNText, mDateText;
	ImageView mBookCover;
	/**
	 * get info from internet about this book,even though it's invalid
	 * */
	public static final int MESSAGE_REFRESH_BOOKINFO = 0001;

	public static final int MESSAGE_INVALID_BOOKINFO = 0002;
	/**
	 * get isbn through zxing or mine scanning(which will be ported into)
	 * */
	public static final int MESSAGE_FETCH_BOOK_ISBN = 0003;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_REFRESH_BOOKINFO:

				mTitleText
						.setText(getString(R.string.book_title) + mInfo.title);
				mAuthorText.setText(getString(R.string.book_author)
						+ mInfo.author.get(0));
				mSummaryText.setText(getString(R.string.book_summary)
						+ mInfo.summary);
				mISBNText.setText(getString(R.string.isbn) + mInfo.isbn13);
				mDateText.setText(getString(R.string.date) + mInfo.date);
				// set book cover,should placed in another thread
				if (mInfo.imguri != null) {
					mBookCover.setImageURI(Uri.parse(mInfo.imguri));
				}
				break;
			case MESSAGE_INVALID_BOOKINFO:

				mTitleText.setText(getString(R.string.book_title)
						+ getString(R.string.unknown));
				mAuthorText.setText(getString(R.string.book_author)
						+ getString(R.string.unknown));
				mSummaryText.setText(getString(R.string.book_summary)
						+ getString(R.string.unknown));
				mISBNText.setText(getString(R.string.isbn)
						+ getString(R.string.unknown));
				mDateText.setText(getString(R.string.date)
						+ getString(R.string.unknown));
				break;
			case MESSAGE_FETCH_BOOK_ISBN:

				break;
			default:
				return;
			}
		}

	};

	@Override
	public boolean onSearchRequested() {
		// TODO Auto-generated method stub
		Log.v(this, "SEARCH_______");
		Bundle data = new Bundle();
		data.putString("author", "��Ľ��");
		this.startSearch(null, true, data, false);
		return super.onSearchRequested();
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

	/*
	 * 
	 * DouBanBooks
	 * 
	 * String[] projection = { DatabaseBookInterface._ID,
	 * DatabaseBookInterface.COLUMN_NAME_ISBN,
	 * DatabaseBookInterface.COLUMN_NAME_TITLE,
	 * DatabaseBookInterface.COLUMN_NAME_AUTHOR, };
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("详细信息");
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		/*
		 * requestWindowFeature(Window.FEATURE_NO_TITLE);
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
		this.setContentView(R.layout.books_scanning);

		mTitleText = (TextView) findViewById(R.id.title);
		mBookCover = (ImageView) findViewById(R.id.book_cover);
		mBookCover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v(this, "-----------------------------");
				Intent i = new Intent();
				i.setData(Uri.parse(mInfo.imguri));

				try {
					i.setClassName("kg.gtss.personalbooksassitant",
							"kg.gtss.personalbooksassitant.FullDisplayPictureActivity");
					SingleBookDetailActivity.this.startActivity(i);
				} catch (Exception e) {
					e.printStackTrace();
					Log.v(this,
							"kg.gtss.personalbooksassitant.FullDisplayPictureActivity NOT found ");
				}
			}
		});
		mAuthorText = (TextView) findViewById(R.id.author);
		mSummaryText = (TextView) findViewById(R.id.summary);
		mISBNText = (TextView) findViewById(R.id.isbn);
		mDateText = (TextView) findViewById(R.id.date);

		Bundle bundle = getIntent().getExtras();
		mIsbn = bundle.getString(DatabaseBookInterface.COLUMN_NAME_ISBN);
		Log.v(this, "isbn:" + mIsbn);
		if (null == mDouBanDatabase)
			mDouBanDatabase = new DouBanDatabase(this,
					new DatabaseChangedCallback() {

						@Override
						public void DatabaseChangedCallbackUi() {
							// TODO Auto-generated method stub

						}
					});

		new DouBanQuery(this, new FeedbackResult() {

			@Override
			public void feedbackResult(int result, Uri uri) {
				// TODO Auto-generated method stub
				switch (result) {
				case Common.SEARCH_BOOK_SUCESS:
					mHandler.sendEmptyMessage(MESSAGE_REFRESH_BOOKINFO);
					break;
				case Common.SEARCH_BOOK_FAIL_NO_NETWORK:
					Toast.makeText(
							SingleBookDetailActivity.this,
							getResources().getString(
									R.string.unconnected_network),
							Toast.LENGTH_SHORT).show();
					mHandler.sendEmptyMessage(MESSAGE_INVALID_BOOKINFO);
					break;
				case Common.SEARCH_BOOK_FAIL_UNKNOWN:
					Toast.makeText(
							SingleBookDetailActivity.this,
							SingleBookDetailActivity.this.getResources()
									.getString(R.string.search_fail),
							Toast.LENGTH_SHORT).show();
					mHandler.sendEmptyMessage(MESSAGE_INVALID_BOOKINFO);
					break;
				default:
					break;
				}

			}
		}, mInfo).execute(mIsbn);
		// should new a thread
		// Cursor c = mDouBanDatabase.queryDatabaseByIsbn(mIsbn);
		// while (c.moveToNext()) {
		//
		// Log.v(this,
		// c.getString(c
		// .getColumnIndex(DatabaseBookInterface.COLUMN_NAME_TITLE))
		// + "..."
		// + c.getString(c
		// .getColumnIndex(DatabaseBookInterface.COLUMN_NAME_AUTHOR)));
		// break;// 1st
		// }
		// c.close();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
