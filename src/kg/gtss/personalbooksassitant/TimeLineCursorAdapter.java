package kg.gtss.personalbooksassitant;

import java.net.URI;

import com.gtss.douban.DatabaseBookInterface;
import com.gtss.douban.DatabaseSQLiteOpenHelper;
import com.gtss.douban.DouBanDatabase;

import kg.gtss.utils.Log;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Unused ! use to handle each book
 * item.用于进入本地管理的时候异步加载数据库数据的适配器.这个和SearchResultCursorAdapter很相近
 * ,我在考虑继承,只是newView方法不同
 * */
public class TimeLineCursorAdapter extends CursorAdapter {
	final Context mContext;
	DouBanDatabase mDouBanDatabase;

	void requeryCursor() {

	}

	public TimeLineCursorAdapter(Context context, Cursor c, DouBanDatabase db) {
		super(context, c);

		// TODO Auto-generated constructor stub
		mContext = context;
		mDouBanDatabase = db;

	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		// TODO Auto-generated method stub
		if (null == c)
			return;
		final int pos = c.getPosition();
		if (pos >= c.getCount())
			return;

		final boolean isFavorite = (c.getInt(c
				.getColumnIndex(DatabaseBookInterface.COLUMN_NAME_FAVORITE)) == 1);
		final String isbn = c.getString(c
				.getColumnIndex(DatabaseBookInterface.COLUMN_NAME_ISBN));
		final String bookname = c.getString(c
				.getColumnIndex(DatabaseBookInterface.COLUMN_NAME_TITLE));
		Log.v(this, "pos:" + pos + "	bookname:" + bookname + "		isFavorite:"
				+ isFavorite);
		ImageView cover = (ImageView) view
				.findViewById(R.id.single_time_node_view_layout_cover);

		String uri = c.getString(c
				.getColumnIndex(DatabaseBookInterface.COLUMN_NAME_IMGURI));
		Log.v(this, "setting book <" + bookname + "> cover :" + uri);
		cover.setImageURI(Uri.parse(uri));

		TextView name = (TextView) view
				.findViewById(R.id.single_time_node_view_layout_name);
		name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra(DatabaseBookInterface.COLUMN_NAME_ISBN, isbn);

				intent.setClassName("kg.gtss.personalbooksassitant",
						"kg.gtss.personalbooksassitant.SingleBookDetailActivity");
				try {
					mContext.startActivity(intent);
				} catch (Exception e) {
					Log.v(this,
							"kg.gtss.personalbooksassitant.SingleBookDetailActivity NOT found.");
				}
			}
		});
		TextView author = (TextView) view
				.findViewById(R.id.single_time_node_view_layout_author);
		TextView date = (TextView) view
				.findViewById(R.id.single_time_node_view_layout_collect_time);
		final ImageView favorite = (ImageView) view.findViewById(R.id.favorite);
		favorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.v(this, "pos:" + pos + "  favorite:" + isFavorite + "->"
						+ !isFavorite + "........onclick");
				if (mDouBanDatabase.favoriteBookByISBN(isbn, !isFavorite)) {
					// notify to update ui and update state
					// TimeLineCursorAdapter.this.notifyDataSetChanged();

					favorite.setImageResource(!isFavorite ? R.drawable.favorite
							: R.drawable.favorite_not);

				}
			}
		});

		favorite.setImageResource(isFavorite ? R.drawable.favorite
				: R.drawable.favorite_not);
		name.setText(bookname);
		author.setText(c.getString(c
				.getColumnIndex(DatabaseBookInterface.COLUMN_NAME_AUTHOR)));
		date.setText(c.getString(c
				.getColumnIndex(DatabaseBookInterface.COLUMN_NAME_DATE)));
	}

	@Override
	public View newView(Context context, Cursor c, ViewGroup parent) {
		// TODO Auto-generated method stub

		View view = LayoutInflater.from(context).inflate(
				R.layout.single_time_node_view_layout, null);
		return view;
	}

}
