package kg.gtss.personalbooksassitant;

import com.gtss.douban.DatabaseChangedCallback;
import com.gtss.douban.DouBanBooks;

import com.gtss.douban.DouBanDatabase;
import com.gtss.douban.DouBanQuery;

import kg.gtss.utils.Common;
import kg.gtss.utils.Log;
import kg.gtss.utils.TimeUtils;
import android.app.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用于采集图书的Fragment
 * */
public class BooksGatherFragment extends Fragment {

	TextView mTitleText;
	ImageView mBookCover;
	TextView mAuthorText;
	TextView mSummaryText;
	TextView mISBNText;
	TextView mDateText;
	Uri mBookCoverImgUri;
	Activity mActivity;
	public static final int MESSAGE_REFRESH_BOOKINFO = 0001;

	public static final int MESSAGE_INVALID_BOOKINFO = 0002;
	private DouBanDatabase mDouBanDatabase;

	private DouBanBooks mSearchBookInfoFromDouBan = new DouBanBooks();

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			// super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_REFRESH_BOOKINFO:

				mTitleText.setText(getString(R.string.book_title)
						+ mSearchBookInfoFromDouBan.title);
				mAuthorText.setText(getString(R.string.book_author)
						+ mSearchBookInfoFromDouBan.author.get(0));
				mSummaryText.setText(getString(R.string.book_summary)
						+ mSearchBookInfoFromDouBan.summary);
				mISBNText.setText(getString(R.string.isbn)
						+ mSearchBookInfoFromDouBan.isbn13);
				mDateText.setText(getString(R.string.date)
						+ mSearchBookInfoFromDouBan.date);
				// set book cover,should placed in another thread
				if (mBookCoverImgUri != null) {
					mBookCover.setImageURI(mBookCoverImgUri);
				} else {
					// text display
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
			// case MESSAGE_FETCH_BOOK_ISBN:
			//
			// mSearchBooksitem.setEnabled(true);
			// break;
			default:
				return;
			}
		}

	};

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

		mActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		// create db
		mDouBanDatabase = new DouBanDatabase(mActivity,
				new DatabaseChangedCallback() {
					@Override
					public void DatabaseChangedCallbackUi() {
						// TODO Auto-generated method stub
						Log.v(this, "DatabaseChangedCallbackUi");
					}
				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater.inflate(R.layout.books_scanning, null);
		mTitleText = (TextView) v.findViewById(R.id.title);
		mBookCover = (ImageView) v.findViewById(R.id.book_cover);
		mAuthorText = (TextView) v.findViewById(R.id.author);
		mSummaryText = (TextView) v.findViewById(R.id.summary);
		mISBNText = (TextView) v.findViewById(R.id.isbn);
		mDateText = (TextView) v.findViewById(R.id.date);
		return v;
	}

	/**
	 * search book from search engine
	 * */
	void searchBook(String ScannedISBN) {
		Log.v(this, "searchBook " + ScannedISBN);
		new DouBanQuery(mActivity, new FeedbackResult() {

			@Override
			public void feedbackResult(int result, Uri uri) {
				// TODO Auto-generated method stub
				Log.v(this, "BooksGatherFragment feedbackResult " + result
						+ " " + uri);
				switch (result) {
				case Common.SEARCH_BOOK_SUCESS:
					mBookCoverImgUri = uri;
					mHandler.sendEmptyMessage(MESSAGE_REFRESH_BOOKINFO);
					mSearchBookInfoFromDouBan.date = TimeUtils.getCurrentTime();
					mDouBanDatabase.insert(mSearchBookInfoFromDouBan);

					Log.v(this, "insert");
					break;
				case Common.SEARCH_BOOK_FAIL_NO_NETWORK:
					Toast.makeText(
							mActivity,
							mActivity.getResources().getString(
									R.string.unconnected_network),
							Toast.LENGTH_SHORT).show();
					mHandler.sendEmptyMessage(MESSAGE_INVALID_BOOKINFO);
					break;
				case Common.SEARCH_BOOK_FAIL_UNKNOWN:
					Toast.makeText(
							mActivity,
							mActivity.getResources().getString(
									R.string.search_fail), Toast.LENGTH_SHORT)
							.show();
					mHandler.sendEmptyMessage(MESSAGE_INVALID_BOOKINFO);
					break;
				default:
					break;
				}
			}
		}, mSearchBookInfoFromDouBan).execute(ScannedISBN);

	}

}
