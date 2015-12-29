package kg.gtss.search;

import com.gtss.douban.DatabaseBookInterface;
import com.gtss.douban.DouBanDatabase;

import kg.gtss.personalbooksassitant.R;
import kg.gtss.utils.Log;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用户手动字串模糊查询异步加载的适配器.这个和TimeLineCursorAdapter很相近,我再考虑让二者继承,只是newView方法不同.
 * 我在考虑使用和TimeLineCursorAdapter相同的布局文件
 * */
public class SearchResultCursorAdapter extends CursorAdapter {
	Context mContext;
	DouBanDatabase mDouBanDatabase;
	int mCurrentForm;

	public static final int TIMELINE_SUB_ITEM_FORM = 1;// default.single_time_node_view_layout
	public static final int GRID_SUB_ITEM_FORM = 2;// gridview_entry_item

	public SearchResultCursorAdapter(Context context, Cursor c,
			DouBanDatabase db) {
		super(context, c);
		// TODO Auto-generated constructor stub
		mContext = context;
		mDouBanDatabase = db;
		mCurrentForm = TIMELINE_SUB_ITEM_FORM;
	}

	public SearchResultCursorAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
		mContext = context;
		mCurrentForm = TIMELINE_SUB_ITEM_FORM;
	}

	public SearchResultCursorAdapter(Context context, Cursor c, int form) {
		super(context, c);
		// TODO Auto-generated constructor stub
		mContext = context;
		mCurrentForm = form;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = null;
		switch (mCurrentForm) {
		case TIMELINE_SUB_ITEM_FORM:
			v = LayoutInflater.from(context).inflate(
					R.layout.single_time_node_view_layout, null);
			break;
		case GRID_SUB_ITEM_FORM:
			v = LayoutInflater.from(context).inflate(
					R.layout.gridview_entry_item, null);
			break;
		}
		return v;
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
		String uri = c.getString(c
				.getColumnIndex(DatabaseBookInterface.COLUMN_NAME_IMGURI));
		Log.v(this, "pos:" + pos + "	bookname:" + bookname + "		isFavorite:"
				+ isFavorite);

		ImageView cover;
		TextView name;
		TextView author;
		TextView date;
		final ImageView favorite;

		switch (mCurrentForm) {
		case TIMELINE_SUB_ITEM_FORM:
			cover = (ImageView) view
					.findViewById(R.id.single_time_node_view_layout_cover);
			cover.setImageURI(Uri.parse(uri));
			name = (TextView) view
					.findViewById(R.id.single_time_node_view_layout_name);
			name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.putExtra(DatabaseBookInterface.COLUMN_NAME_ISBN,
							isbn);

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
			author = (TextView) view
					.findViewById(R.id.single_time_node_view_layout_author);
			date = (TextView) view
					.findViewById(R.id.single_time_node_view_layout_collect_time);
			favorite = (ImageView) view.findViewById(R.id.favorite);
			favorite.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Log.v(this, "pos:" + pos + "  favorite:" + isFavorite
							+ "->" + !isFavorite + "........onclick");
					if (mDouBanDatabase != null
							&& mDouBanDatabase.favoriteBookByISBN(isbn,
									!isFavorite)) {
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
			break;
		case GRID_SUB_ITEM_FORM:
			cover = (ImageView) view
					.findViewById(R.id.gridview_entry_item_image);
			cover.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.putExtra(DatabaseBookInterface.COLUMN_NAME_ISBN,
							isbn);

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
			cover.setImageURI(Uri.parse(uri));
			name = (TextView) view.findViewById(R.id.gridview_entry_item_text);
			name.setText(bookname);
			break;
		}

	}

}
